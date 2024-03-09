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

import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality.*;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.ValueMapping.LITERAL_AS_STRING;
import static java.beans.Introspector.getBeanInfo;
import static java.lang.reflect.Modifier.isProtected;
import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.vocabulary.RDF.type;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstInstanceOfEither;
import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality;
import com.inrupt.rdf.wrapping.jena.WrapperResource;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.PropertyNotFoundException;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Resource definition")
class ResourceDefinitionCardinalityTest {
    private static final String P = "urn:example:p";
    private static final String C = "urn:example:c";
    private static final Object[] RESOURCE_DEFINITION_METHODS = stream(ResourceDefinition.class.getDeclaredMethods())
            .map(m -> m.getAnnotation(ResourceProperty.class))
            .map(ResourceProperty::cardinality)
            .map(Cardinality::getMethodName)
            .distinct()
            .toArray();
    private static final int NONE = 0;
    private static final int SINGLE = 1;
    private static final int MANY = 2;
    private static final String DOES_NOT_THROW = "does not throw";
    private static final String THROWS = "throws";

    private Model m;
    private GraphDefinition wrapped;

    @BeforeEach
    void setUp() {
        m = createDefaultModel();
        wrapped = GraphDefinition.wrap(m);
    }

    @DisplayName("properly converts/fails node with cardinality")
    @ParameterizedTest(name = "{2} for cardinality {0} with {1} items")
    @MethodSource
    void singular(final Cardinality cardinality, final int number, final String throwing) throws Exception {
        final List<String> values = new ArrayList<>();
        final Class<? extends Exception> expectedException;
        final Matcher<?> asExpected;
        // anchor for graph definition
        final org.apache.jena.rdf.model.Resource s = m.createResource().addProperty(type, m.createResource(C));
        final ResourceDefinition resource = wrapped.getResource();

        switch (number) {
            case NONE:
                asExpected = nullValue();
                expectedException = PropertyNotFoundException.class;
                break;
            case SINGLE:
                asExpected = in(values);
                expectedException = null;
                break;
            default:
            case MANY:
                asExpected = in(values);
                expectedException = IllegalStateException.class;
                break;
        }

        // Add and remember as many objects as needed
        for (int i = 0; i < number; i++) {
            final String value = randomUUID().toString();
            values.add(value);
            s.addProperty(m.createProperty(P), m.createTypedLiteral(value));
        }

        switch (throwing) {
            case THROWS:
                final Method getter = findProperty(resource, cardinality);
                assertThat(
                        assertThrows(InvocationTargetException.class, () -> getter.invoke(resource)),
                        hasProperty("cause", is(instanceOf(expectedException))));
                break;
            default:
            case DOES_NOT_THROW:
                assertThat(resource, hasProperty(cardinality.getMethodName(), is(asExpected)));
        }
    }

    private static Stream<Arguments> singular() {
        return Stream.of(
                arguments(ANY_OR_NULL, NONE, DOES_NOT_THROW),
                arguments(ANY_OR_NULL, SINGLE, DOES_NOT_THROW),
                arguments(ANY_OR_NULL, MANY, DOES_NOT_THROW),
                arguments(ANY_OR_THROW, NONE, THROWS),
                arguments(ANY_OR_THROW, SINGLE, DOES_NOT_THROW),
                arguments(ANY_OR_THROW, MANY, DOES_NOT_THROW),
                arguments(SINGLE_OR_NULL, NONE, DOES_NOT_THROW),
                arguments(SINGLE_OR_NULL, SINGLE, DOES_NOT_THROW),
                arguments(SINGLE_OR_NULL, MANY, THROWS),
                arguments(SINGLE_OR_THROW, NONE, THROWS),
                arguments(SINGLE_OR_THROW, SINGLE, DOES_NOT_THROW),
                arguments(SINGLE_OR_THROW, MANY, THROWS)
        );
    }

    @DisplayName("returns correct items for plural cardinality")
    @ParameterizedTest(name = "{0}")
    @EnumSource(value = Cardinality.class, names = {"OBJECT_ITERATOR", "OBJECTS_READ_ONLY", "OBJECT_STREAM"})
    @SuppressWarnings("unchecked")
    void plural(final Cardinality cardinality) throws Exception {
        final List<String> values = new ArrayList<>();

        // anchor for graph definition
        final org.apache.jena.rdf.model.Resource s = m.createResource().addProperty(type, m.createResource(C));
        final ResourceDefinition resource = wrapped.getResource();

        for (int i = 0; i < 100; i++) {
            final String value = randomUUID().toString();
            values.add(value);
            s.addProperty(m.createProperty(P), m.createTypedLiteral(value));
        }

        final Object invoke = findProperty(resource, cardinality).invoke(resource);

        final Iterable<?> result;
        switch (cardinality) {
            case OBJECT_ITERATOR:
                result = () -> (Iterator) invoke;
                break;

            case OBJECTS_READ_ONLY:
                result = (Set) invoke;
                break;

            default:
            case OBJECT_STREAM:
                result = () -> ((Stream) invoke).iterator();
                break;
        }

        assertThat(result, containsInAnyOrder(values.toArray()));
    }

    @Disabled("Not ready yet") // TODO: Enable
    @DisplayName("mock has equivalent of ValueMappings method")
    @ParameterizedTest(name = "{0}")
    @MethodSource
    void wrapperResourceMethods(final String name) {
        assertThat(RESOURCE_DEFINITION_METHODS, hasItemInArray(name));
    }

    private static Stream<String> wrapperResourceMethods() {
        return stream(WrapperResource.class.getDeclaredMethods())
                .filter(m -> isProtected(m.getModifiers()))
                .map(Method::getName);
    }

    private static Method findProperty(final ResourceDefinition r, final Cardinality c) throws IntrospectionException {
        return stream(getBeanInfo(r.getClass(), Object.class).getPropertyDescriptors())
                .filter(p -> p.getName().equals(c.getMethodName()))
                .map(PropertyDescriptor::getReadMethod)
                .findAny()
                .orElseThrow(RuntimeException::new);
    }

    @Resource
    interface ResourceDefinition {
        @ResourceProperty(predicate = P, valueMapping = LITERAL_AS_STRING)
        String getAnyOrNull();

        @ResourceProperty(predicate = P, cardinality = ANY_OR_THROW, valueMapping = LITERAL_AS_STRING)
        String getAnyOrThrow();

        @ResourceProperty(predicate = P, cardinality = SINGLE_OR_NULL, valueMapping = LITERAL_AS_STRING)
        String getSingleOrNull();

        @ResourceProperty(predicate = P, cardinality = SINGLE_OR_THROW, valueMapping = LITERAL_AS_STRING)
        String getSingleOrThrow();

        @ResourceProperty(predicate = P, cardinality = OBJECT_ITERATOR, valueMapping = LITERAL_AS_STRING)
        Iterator<String> getObjectIterator();

        @ResourceProperty(predicate = P, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_STRING)
        Set<String> getObjectsReadOnly();

        @ResourceProperty(predicate = P, cardinality = OBJECT_STREAM, valueMapping = LITERAL_AS_STRING)
        Stream<String> getObjectStream();
    }

    @Graph
    interface GraphDefinition {
        static GraphDefinition wrap(final Model original) {
            return Manager.wrap(original, GraphDefinition.class);
        }

        @OptionalFirstInstanceOfEither(C)
        ResourceDefinition getResource();
    }
}
