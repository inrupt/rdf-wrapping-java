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

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.Resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.jboss.jdeparser.*;

abstract class Implementor {
    protected static final String ORIGINAL = "original";
    protected static final String WRAP = "wrap";

    protected final EnvironmentHelper environment;
    protected final TypeElement type;
    protected final JSources sources;
    protected final String originalInterface;
    protected final JSourceFile sourceFile;

    private final String implementationClass;

    protected Implementor(final ProcessingEnvironment environment, final Element element) {
        this.environment = new EnvironmentHelper(environment);
        type = (TypeElement) element;

        originalInterface = type.getQualifiedName().toString();
        final String originalBinaryName = environment.getElementUtils().getBinaryName(type).toString();
        final String qualifiedName = asImplementation(originalBinaryName);
        final int lastDot = originalBinaryName.lastIndexOf('.');
        implementationClass = qualifiedName.substring(lastDot + 1);
        final String packageName = environment.getElementUtils().getPackageOf(element).getQualifiedName().toString();

        final JFiler filer = JFiler.newInstance(environment.getFiler());
        sources = JDeparser.createSources(filer, new FormatPreferences());
        sourceFile = sources.createSourceFile(packageName, implementationClass);
    }

    void implement() {
        implementInternal();

        try {
            sources.writeSources();
        } catch (IOException e) {
            throw new RuntimeException("could not open writer", e);
        }
    }

    protected abstract void implementInternal();

    static Implementor get(final ProcessingEnvironment env, final Element element) {
        if (element.getAnnotation(Dataset.class) != null) {
            return new DatasetImplementor(env, element);
        } else if (element.getAnnotation(Graph.class) != null) {
            return new GraphImplementor(env, element);
        } else if (element.getAnnotation(Resource.class) != null) {
            return new ResourceImplementor(env, element);
        } else {
            throw new RuntimeException("unknown annotation type");
        }
    }

    static String asImplementation(final String original) {
        return original + "_$impl";
    }

    protected JType asImplementation(final TypeMirror original) {
        final Element returnType = environment.getTypeUtils().asElement(original);
        final String originalBinaryName = environment
                .getElementUtils()
                .getBinaryName((TypeElement) returnType)
                .toString();

        return $t(asImplementation(originalBinaryName));
    }

    protected JClassDef createClass(final Class<?> base) {
        final JClassDef myClass = sourceFile
                ._class(PUBLIC, implementationClass)
                ._extends(base)
                ._implements(originalInterface);

        annotateAndDocumentAsGenerated(myClass);

        return myClass;
    }

    @SafeVarargs
    protected final Stream<ExecutableElement> membersAnnotatedWithAny(
            final Class<? extends Annotation>... annotations) {

        return environment.methodsOf(type).stream()
                .filter(method -> !method.isDefault()
                                  && !method.getModifiers().contains(Modifier.STATIC)
                                  && !environment.isVoid(method.getReturnType()))
                .filter(method -> Arrays.stream(annotations)
                        .anyMatch(annotation -> method.getAnnotation(annotation) != null));
    }

    private void annotateAndDocumentAsGenerated(final JClassDef implementation) {
        implementation.docComment().text("Warning, this class consists of generated code.");

        implementation
                .annotate(Generated.class)
                .value(this.getClass().getName()).value("date", Instant.now().toString());
    }
}
