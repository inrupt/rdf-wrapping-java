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

import static java.util.stream.Stream.concat;

import com.inrupt.rdf.wrapping.annotation.OptionalFirstInstanceOfEither;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstObjectOfEither;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstSubjectOfEither;

import java.util.stream.Stream;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

class GraphInterface extends Interface {
    GraphInterface(final EnvironmentHelper environment, final TypeElement type) {
        super(environment, type);
    }

    Stream<TypeMirror> transitiveResourceTypes() {
        final Stream<TypeMirror> children = resourceMethodTypes();
        final Stream<TypeMirror> descendants = resourceMethodTypes()
                .map(environment::type)
                .map(type -> new ResourceInterface(environment, type))
                .flatMap(ResourceInterface::transitiveResourceTypes);

        return concat(children, descendants)
                .distinct();
    }

    Stream<ExecutableElement> instanceMethods() {
        return membersAnnotatedWith(OptionalFirstInstanceOfEither.class);
    }

    Stream<ExecutableElement> subjectMethods() {
        return membersAnnotatedWith(OptionalFirstSubjectOfEither.class);
    }

    Stream<ExecutableElement> objectMethods() {
        return membersAnnotatedWith(OptionalFirstObjectOfEither.class);
    }

    private Stream<TypeMirror> resourceMethodTypes() {
        return concat(concat(instanceMethods(), subjectMethods()), objectMethods())
                .map(ExecutableElement::getReturnType)
                .distinct();
    }
}
