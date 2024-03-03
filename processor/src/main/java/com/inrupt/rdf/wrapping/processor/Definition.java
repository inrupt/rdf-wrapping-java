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

import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.type.TypeKind.VOID;

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.Graph;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.jboss.jdeparser.JType;
import org.jboss.jdeparser.JTypes;

class Definition {
    static final Predicate<ExecutableElement> isVoid = method -> method.getReturnType().getKind() == VOID;

    private final TypeElement type;
    private final Environment env;

    Definition(final TypeElement type, final Environment env) {
        this.env = env;
        this.type = type;
    }

    static Definition definition(final TypeElement type, final Environment env) {
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
        return typeOf(method.getReturnType());
    }

    protected TypeElement getType() {
        return type;
    }

    protected Environment getEnv() {
        return env;
    }

    protected JType getOriginalInterface() {
        return JTypes.typeOf(getType().asType());
    }

    protected Stream<ExecutableElement> membersAnnotatedWith(final Class<? extends Annotation> annotation) {
        return getEnv().methodsOf(getType())
                .filter(method -> !method.isDefault())
                .filter(method -> !method.getModifiers().contains(STATIC))
                .filter(method -> method.getAnnotation(annotation) != null);
    }
}
