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

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.annotation.Resource;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

class ResourceInterface extends Interface {
    ResourceInterface(final TypeElement type, final Environment env) {
        super(type, env);
    }

    Stream<ExecutableElement> primitivePropertyMethods() {
        return membersAnnotatedWith(Property.class).filter(returnTypeIsResource().negate());
    }

    Stream<ExecutableElement> resourcePropertyMethods() {
        return membersAnnotatedWith(Property.class).filter(returnTypeIsResource());
    }

    Stream<ExecutableElement> complexMappingPropertyMethods() {
        return membersAnnotatedWith(Property.class).filter(isComplexMapping());
    }

    Stream<ExecutableElement> primitiveMappingPropertyMethods() {
        return membersAnnotatedWith(Property.class).filter(isComplexMapping().negate());
    }

    Stream<TypeMirror> transitiveResourceTypes() {
        final Queue<TypeMirror> outstanding = new LinkedList<>();
        final Set<TypeMirror> results = new HashSet<>();

        ResourceInterface current = this;
        while (true) {
            current.resourcePropertyMethods()
                    .map(ExecutableElement::getReturnType)
                    .filter(results::add)
                    .forEach(outstanding::add);

            if (outstanding.isEmpty()) {
                break;
            }

            final TypeMirror next = outstanding.remove();
            final TypeElement nextType = getEnv().type(next);

            current = new ResourceInterface(nextType, getEnv());
        }

        return results.stream();
    }

    private Predicate<ExecutableElement> returnTypeIsResource() {
        return method -> getEnv().type(method.getReturnType()).getAnnotation(Resource.class) != null;
    }

    private Predicate<ExecutableElement> isComplexMapping() {
        return method -> method.getAnnotation(Property.class).mapping() == Property.Mapping.AS;
    }
}
