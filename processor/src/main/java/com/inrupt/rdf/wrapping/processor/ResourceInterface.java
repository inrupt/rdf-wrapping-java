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

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.annotation.Resource;

import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

class ResourceInterface extends Interface {
    ResourceInterface(final EnvironmentHelper environment, final TypeElement type) {
        super(environment, type);
    }

    Stream<ExecutableElement> primitivePropertyMethods() {
        return membersAnnotatedWith(Property.class).filter(returnTypeIsResource().negate());
    }

    Stream<ExecutableElement> resourcePropertyMethods() {
        return membersAnnotatedWith(Property.class).filter(returnTypeIsResource());
    }

    Stream<TypeMirror> transitiveResourceTypes() {
        final Stream<TypeMirror> children = resourcePropertyMethods().map(ExecutableElement::getReturnType);

        final Stream<TypeMirror> descendants = resourcePropertyMethods()
                .map(ExecutableElement::getReturnType)
                .map(environment::type)
                .map(type -> new ResourceInterface(environment, type))
                .flatMap(ResourceInterface::transitiveResourceTypes);

        return concat(children, descendants).distinct();
    }

    private Predicate<ExecutableElement> returnTypeIsResource() {
        return method -> environment.type(method.getReturnType()).getAnnotation(Resource.class) != null;
    }
}
