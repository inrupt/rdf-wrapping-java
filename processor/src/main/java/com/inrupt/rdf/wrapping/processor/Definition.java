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

import static javax.lang.model.type.TypeKind.VOID;

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.Graph;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.jboss.jdeparser.JType;
import org.jboss.jdeparser.JTypes;

class Definition<T extends Element, U extends Annotation> {
    static final Predicate<ExecutableElement> isVoid = method -> method.getReturnType().getKind() == VOID;

    private final T element;
    private final Environment env;
    private final Class<U> clazz;

    Definition(final T element, final Environment env, final Class<U> clazz) {
        this.element = element;
        this.env = env;
        this.clazz = clazz;
    }

    static Definition<TypeElement, ?> definition(final TypeElement type, final Environment env) {
        if (type.getAnnotation(Dataset.class) != null) {
            return new DatasetDefinition(type, env);

        } else if (type.getAnnotation(Graph.class) != null) {
            return new GraphDefinition(type, env);

        } else { // Resource
            // Processor's supported annotations are finite
            return new ResourceDefinition(type, env);
        }
    }

    TypeElement typeOf(final TypeMirror mirror) {
        return getEnv().type(mirror);
    }

    TypeElement returnTypeOf(final ExecutableElement method) {
        return getEnv().findDeclaration(method.getReturnType());
    }

    protected T getElement() {
        return element;
    }

    protected Environment getEnv() {
        return env;
    }

    protected Stream<ExecutableElement> membersAnnotatedWith(final Class<? extends Annotation> annotation) {
        return env.methodsOf(element)
                .filter(method -> method.getAnnotation(annotation) != null);
    }

    U annotation() {
        return element.getAnnotation(clazz);
    }
}
