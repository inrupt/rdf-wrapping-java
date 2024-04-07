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

import static java.util.stream.Collectors.toSet;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.tools.Diagnostic.Kind.NOTE;

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@SupportedSourceVersion(RELEASE_8)
public class Processor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(Dataset.class, Graph.class, Resource.class).map(Class::getName).collect(toSet());
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(NOTE, "processing over");
            return false;
        }

        final Collection<Implementor<?>> implementors = new ArrayList<>();
        final Collection<ValidationError> validationErrors = new ArrayList<>();
        final Environment env = new Environment(processingEnv);

        for (final TypeElement annotation : annotations) {
            for (final Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                // All our annotations are @Target(TYPE), so they're all TypeElements
                final TypeElement type = (TypeElement) element;

                final Definition<TypeElement, ?> definition = definition(type, env);
                implementors.add(implementor(definition));
                validationErrors.addAll(validator(definition).validate());
            }
        }

        if (validationErrors.isEmpty()) {
            implementors.forEach(Implementor::implement);
        } else {
            validationErrors.forEach(error -> error.printMessage(processingEnv.getMessager()));
        }

        return true;
    }

    private static Definition<TypeElement, ?> definition(final TypeElement type, final Environment env) {
        if (type.getAnnotation(Dataset.class) != null) {
            return new DatasetDefinition(type, env);

        } else if (type.getAnnotation(Graph.class) != null) {
            return new GraphDefinition(type, env);

        } else { // Resource
            // Supported annotations are finite
            return new ResourceDefinition(type, env);
        }
    }

    private static Implementor<?> implementor(final Definition<?, ?> definition) {
        if (definition instanceof DatasetDefinition) {
            return new DatasetImplementor((DatasetDefinition) definition);

        } else if (definition instanceof GraphDefinition) {
            return new GraphImplementor((GraphDefinition) definition);

        } else { // Resource
            // Supported annotations are finite
            return new ResourceImplementor((ResourceDefinition) definition);
        }
    }

    private static Validator<?> validator(final Definition<?, ?> definition) {
        if (definition instanceof DatasetDefinition) {
            return new DatasetValidator((DatasetDefinition) definition);

        } else if (definition instanceof GraphDefinition) {
            return new GraphValidator((GraphDefinition) definition);

        } else { // Resource
            // Supported annotations are finite
            return new ResourceValidator((ResourceDefinition) definition);
        }
    }
}
