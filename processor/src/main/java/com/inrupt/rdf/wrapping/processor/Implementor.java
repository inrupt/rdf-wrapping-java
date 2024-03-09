/*
 * Copyright Inrupt Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.inrupt.rdf.wrapping.processor;

import static org.jboss.jdeparser.JMod.PUBLIC;
import static org.jboss.jdeparser.JTypes.$t;
import static org.jboss.jdeparser.JTypes.typeOf;

import java.io.IOException;
import java.time.Instant;

import javax.annotation.Generated;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.jboss.jdeparser.*;

abstract class Implementor<T extends Definition<?, ?>> {
    protected static final String ORIGINAL = "original";
    static final String WRAP = "wrap";

    protected final JSources sources;
    protected final JSourceFile sourceFile;
    protected final T definition;
    protected JClassDef target;

    protected Implementor(final T definition) {
        this.definition = definition;

        final Element type = definition.getElement();
        final Environment env = definition.getEnv();

        final String packageName = env.getElementUtils().getPackageOf(type).getQualifiedName().toString();

        final JFiler filer = JFiler.newInstance(env.getFiler());
        sources = JDeparser.createSources(filer, new FormatPreferences());
        sourceFile = sources.createSourceFile(packageName, getImplementationClass());
    }

    void implement() {
        implementInternal();

        try {
            sources.writeSources();
        } catch (IOException e) {
            throw new RuntimeException("could not open writer", e);
        }
    }

    static Implementor<?> implementor(final Definition<?, ?> definition) {
        if (definition instanceof DatasetDefinition) {
            return new DatasetImplementor((DatasetDefinition) definition);

        } else if (definition instanceof GraphDefinition) {
            return new GraphImplementor((GraphDefinition) definition);

        } else { // Resource
            // Processor's supported annotations are finite
            return new ResourceImplementor((ResourceDefinition) definition);
        }
    }

    static String asImplementation(final String original) {
        return original + "_$impl";
    }

    protected abstract void implementInternal();

    protected void addClass(final Class<?> clazz) {
        target = sourceFile._class(PUBLIC, getImplementationClass());
        target._extends(clazz);
        target._implements(definition.getOriginalInterface());

        target.docComment().text("Warning, this class consists of generated code.");
        target.annotate(Generated.class).value(this.getClass().getName()).value("date", Instant.now().toString());
    }

    protected JMethodDef addMethod(final ExecutableElement method) {
        final String myName = method.getSimpleName().toString();
        final JType myType = typeOf(method.getReturnType());

        final JMethodDef myMethod = target.method(PUBLIC, myType, myName);
        myMethod.annotate(Override.class);

        return myMethod;
    }

    protected JMethodDef addMethod(final Definition<? extends ExecutableElement, ?> d) {
        final String myName = d.element.getSimpleName().toString();
        final JType myType = typeOf(d.element.getReturnType());

        final JMethodDef myMethod = target.method(PUBLIC, myType, myName);
        myMethod.annotate(Override.class);

        return myMethod;
    }

    protected JType asImplementation(final TypeMirror original) {
        final TypeElement returnType = definition.typeOf(original);
        final String originalBinaryName = binaryName(returnType);

        return $t(asImplementation(originalBinaryName));
    }

    private String getImplementationClass() {
        final String originalBinaryName = binaryName((TypeElement) definition.getElement());
        final String qualifiedName = asImplementation(originalBinaryName);
        final int lastDot = originalBinaryName.lastIndexOf('.');
        return qualifiedName.substring(lastDot + 1);
    }

    private String binaryName(final TypeElement element) {
        return definition.getEnv().getElementUtils().getBinaryName(element).toString();
    }
}
