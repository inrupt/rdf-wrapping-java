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
import java.io.PrintWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes({
        "com.inrupt.rdf.wrapping.declarative.annotations.Dataset",
        "com.inrupt.rdf.wrapping.declarative.annotations.Graph",
        "com.inrupt.rdf.wrapping.declarative.annotations.Resource"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "processing over");
            return true;
        }

        if (annotations.isEmpty()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "annotations empty");
            return true;
        }

        for (TypeElement annotation : annotations) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("annotation [%s]", annotation), annotation);

            // TODO: Just to get started
            if (!"com.inrupt.rdf.wrapping.declarative.annotations.Resource".equals(annotation.getQualifiedName().toString())) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("skipping [%s]", annotation), annotation);
                continue;
            }

            final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element annotatedElement : annotatedElements) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("annotatedElement [%s]", annotatedElement), annotatedElement);

                final TypeElement annotatedType = (TypeElement) annotatedElement;
                final String qualifiedName = annotatedType.getQualifiedName().toString() + "_$impl";

                final JavaFileObject builderFile;
                try {
                    builderFile = processingEnv.getFiler().createSourceFile(qualifiedName, annotatedElement);
                } catch (IOException e) {
                    throw new RuntimeException("could not create class file", e);
                }

                try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                    // This should surely be a framework like JDeparser
                    out.println("generated");
                } catch (IOException e) {
                    throw new RuntimeException("could not open writer", e);
                }
            }
        }

        return true;
    }
}
