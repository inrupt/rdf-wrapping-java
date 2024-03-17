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

import static com.inrupt.rdf.wrapping.processor.PredicateShim.not;
import static javax.lang.model.type.TypeKind.VOID;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

class ResourceDefinition extends Definition<TypeElement, Resource> {
    private static final Predicate<ResourcePropertyDefinition> isComplex = p -> p.valueMapping().isComplex();
    private static final Predicate<ResourcePropertyDefinition> isPlural = p -> p.cardinality().isPlural();
    private final Predicate<ResourcePropertyDefinition> isSetter = p -> p.cardinality().isSetter();
    private final Predicate<ResourcePropertyDefinition> isVoid = p -> p.getReturnType().getKind() == VOID;

    ResourceDefinition(final TypeElement element, final Environment env) {
        super(element, env, Resource.class);
    }

    Stream<ResourcePropertyDefinition> primitiveProperties() {
        return properties()
                .filter(not(isSetter))
                .filter(not(isComplex));
    }

    Stream<ResourcePropertyDefinition> resourceProperties() {
        return properties()
                .filter(not(isPlural))
                .filter(not(isSetter))
                .filter(isComplex);
    }

    Stream<ResourcePropertyDefinition> complexMappingProperties() {
        return properties()
                .filter(isComplex)
                .filter(not(isPlural))
                .filter(not(isVoid));
    }

    Stream<ResourcePropertyDefinition> setterProperties() {
        return properties()
                .filter(isSetter);
    }

    Stream<ResourcePropertyDefinition> primitiveMappingProperties() {
        return properties()
                .filter(not(isComplex))
                .filter(not(isPlural))
                .filter(not(isVoid));
    }

    Stream<ResourcePropertyDefinition> primitivePluralProperties() {
        return properties()
                .filter(not(isComplex))
                .filter(isPlural);
    }

    Stream<ResourcePropertyDefinition> complexPlurals() {
        return properties()
                .filter(isComplex)
                .filter(isPlural);
    }

    Stream<ResourcePropertyDefinition> voidGetters() {
        return properties()
                .filter(not(isSetter))
                .filter(isVoid);
    }

    Stream<TypeMirror> transitiveResourceTypes() {
        final Queue<TypeMirror> outstanding = new LinkedList<>();
        final Set<TypeMirror> results = new HashSet<>();

        ResourceDefinition current = this;
        while (true) {
            current.resourceProperties()
                    .map(ResourcePropertyDefinition::getReturnType)
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

    Stream<ResourcePropertyDefinition> properties() {
        return membersAnnotatedWith(ResourceProperty.class).map(e -> new ResourcePropertyDefinition(e, getEnv()));
    }
}
