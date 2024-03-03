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

import static com.inrupt.rdf.wrapping.annotation.Property.Mapping.AS;
import static com.inrupt.rdf.wrapping.processor.PredicateShim.not;

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

class ResourceDefinition extends Definition {
    private static final Predicate<ExecutableElement> isComplex = method ->
            method.getAnnotation(Property.class).mapping() == AS;
    private static final Predicate<ExecutableElement> isPlural = method ->
            method.getAnnotation(Property.class).cardinality().isPlural();
    private final Predicate<ExecutableElement> returnsResource = method ->
            getEnv().type(method.getReturnType()).getAnnotation(Resource.class) != null;

    ResourceDefinition(final TypeElement type, final Environment env) {
        super(type, env);
    }

    Stream<ExecutableElement> primitivePropertyMethods() {
        return membersAnnotatedWith(Property.class)
                .filter(not(returnsResource));
    }

    Stream<ExecutableElement> resourcePropertyMethods() {
        return membersAnnotatedWith(Property.class)
                .filter(returnsResource);
    }

    Stream<ExecutableElement> complexMappingPropertyMethods() {
        return membersAnnotatedWith(Property.class)
                .filter(isComplex)
                .filter(not(isPlural))
                .filter(not(isVoid));
    }

    Stream<ExecutableElement> primitiveMappingPropertyMethods() {
        return membersAnnotatedWith(Property.class)
                .filter(not(isComplex))
                .filter(not(isPlural))
                .filter(not(isVoid));
    }

    Stream<ExecutableElement> pluralPropertyMethods() {
        return membersAnnotatedWith(Property.class)
                .filter(isPlural);
    }

    Stream<TypeMirror> transitiveResourceTypes() {
        final Queue<TypeMirror> outstanding = new LinkedList<>();
        final Set<TypeMirror> results = new HashSet<>();

        ResourceDefinition current = this;
        while (true) {
            current.resourcePropertyMethods()
                    .map(ExecutableElement::getReturnType)
                    .filter(results::add)
                    .forEach(outstanding::add);

            if (outstanding.isEmpty()) {
                break;
            }

            current = dequeue(outstanding);
        }

        return results.stream();
    }

    private ResourceDefinition dequeue(final Queue<TypeMirror> outstanding) {
        final TypeMirror next = outstanding.remove();
        final TypeElement nextType = getEnv().type(next);

        return new ResourceDefinition(nextType, getEnv());
    }
}
