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

import java.util.ArrayList;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({
    "com.inrupt.rdf.wrapping.declarative.annotation.Dataset",
    "com.inrupt.rdf.wrapping.declarative.annotation.Graph",
    "com.inrupt.rdf.wrapping.declarative.annotation.Resource"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "processing over");
            return false;
        }

        final ArrayList<Implementor> implementors = new ArrayList<>();
        final ArrayList<ValidationError> validationErrors = new ArrayList<>();
        for (final TypeElement annotation : annotations) {
            for (final Element annotatedElement : roundEnv.getElementsAnnotatedWith(annotation)) {
                implementors.add(Implementor.get(annotation, processingEnv, annotatedElement));
                validationErrors.addAll(Validator.get(annotation, processingEnv, annotatedElement).validate());
            }
        }

        if (validationErrors.isEmpty()) {
            implementors.forEach(Implementor::implement);
        } else {
            validationErrors.forEach(error -> error.printMessage(processingEnv.getMessager()));
        }

        return true;
    }
}
