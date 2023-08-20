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
package com.inrupt.rdf.wrapping.declarative.processor;

import static org.jboss.jdeparser.JMod.PUBLIC;
import static org.jboss.jdeparser.JTypes.$t;

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
import javax.lang.model.util.ElementFilter;

import org.jboss.jdeparser.*;

abstract class Implementor {
    protected static final String ORIGINAL = "original";
    protected static final String WRAP = "wrap";

    protected final ProcessingEnvironment environment;
    protected final TypeElement element;
    protected final JSources sources;
    protected final String originalInterface;
    protected JSourceFile sourceFile;

    private final String implementationClass;

    protected Implementor(final ProcessingEnvironment environment, final TypeElement element) {
        this.environment = environment;
        this.element = element;

        originalInterface = element.getQualifiedName().toString();
        final String originalBinaryName = environment.getElementUtils().getBinaryName(element).toString();
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

    static Implementor get(final TypeElement annotation, final ProcessingEnvironment env, final Element element) {

        final TypeElement annotatedType = (TypeElement) element;

        switch (annotation.getQualifiedName().toString()) {
            case "com.inrupt.rdf.wrapping.declarative.annotation.Dataset":
                return new DatasetImplementor(env, annotatedType);
            case "com.inrupt.rdf.wrapping.declarative.annotation.Graph":
                return new GraphImplementor(env, annotatedType);
            case "com.inrupt.rdf.wrapping.declarative.annotation.Resource":
                return new ResourceImplementor(env, annotatedType);
            default:
                throw new RuntimeException("unknown annotation type");
        }
    }

    static String asImplementation(final String original) {
        return original + "_$impl";
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

        return ElementFilter.methodsIn(element.getEnclosedElements()).stream()
                .filter(method -> !method.isDefault()
                                  && !method.getModifiers().contains(Modifier.STATIC)
                                  && !method.getReturnType().equals($t(Void.class)))
                .filter(method -> Arrays.stream(annotations)
                        .anyMatch(annotation -> method.getAnnotation(annotation) != null));
    }

    protected JType returnTypeAsImplementation(final ExecutableElement method) {
        final Element returnType = environment.getTypeUtils().asElement(method.getReturnType());
        final String originalBinaryName = environment
                .getElementUtils()
                .getBinaryName((TypeElement) returnType)
                .toString();
        return $t(asImplementation(originalBinaryName));
    }

    private void annotateAndDocumentAsGenerated(final JClassDef implementation) {
        implementation.docComment().text("Warning, this class consists of generated code.");

        implementation
                .annotate(Generated.class)
                .value(this.getClass().getName()).value("date", Instant.now().toString());
    }
}
