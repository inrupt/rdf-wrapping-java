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

import java.io.IOException;
import java.time.Instant;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.jboss.jdeparser.*;

abstract class Implementor {
    protected final ProcessingEnvironment processingEnvironment;
    protected final TypeElement annotatedElement;
    protected final JSources sources;
    protected final String implementationClass;
    protected final String originalInterface;
    protected JSourceFile sourceFile;

    protected Implementor(final ProcessingEnvironment processingEnvironment, final TypeElement annotatedElement) {
        this.processingEnvironment = processingEnvironment;
        this.annotatedElement = annotatedElement;

        originalInterface = annotatedElement.getQualifiedName().toString();
        final String originalBinaryName = processingEnvironment
                .getElementUtils()
                .getBinaryName(annotatedElement)
                .toString();
        final String qualifiedName = originalBinaryName + "_$impl";
        final int lastDot = originalBinaryName.lastIndexOf('.');
        implementationClass = qualifiedName.substring(lastDot + 1);
        final String packageName = processingEnvironment.getElementUtils()
                .getPackageOf(annotatedElement)
                .getQualifiedName().toString();

        final JFiler jFiler = JFiler.newInstance(processingEnvironment.getFiler());
        sources = JDeparser.createSources(jFiler, new FormatPreferences());

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

    static Implementor get(
            final TypeElement annotation,
            final ProcessingEnvironment processingEnvironment,
            final Element annotatedElement) {

        final TypeElement annotatedType = (TypeElement) annotatedElement;

        switch (annotation.getQualifiedName().toString()) {
            case "com.inrupt.rdf.wrapping.declarative.annotations.Dataset":
                return new DatasetImplementor(processingEnvironment, annotatedType);
            case "com.inrupt.rdf.wrapping.declarative.annotations.Graph":
                return new GraphImplementor(processingEnvironment, annotatedType);
            case "com.inrupt.rdf.wrapping.declarative.annotations.Resource":
                return new ResourceImplementor(processingEnvironment, annotatedType);
            default:
                throw new RuntimeException("unknown annotation type");
        }
    }

    protected void annotateAndDocumentAsGenerated(final JClassDef implementation) {
        implementation.docComment().text("Warning, this class consists of generated code.");

        implementation
                .annotate(Generated.class)
                .value(this.getClass().getName()).value("date", Instant.now().toString());
    }
}
