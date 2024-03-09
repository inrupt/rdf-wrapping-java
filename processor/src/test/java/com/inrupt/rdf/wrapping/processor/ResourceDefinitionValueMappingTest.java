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

import static com.inrupt.rdf.wrapping.annotation.GraphProperty.Method.INSTANCE_OF;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.ValueMapping.*;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;
import static org.apache.jena.vocabulary.RDF.type;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.GraphProperty;
import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty.ValueMapping;
import com.inrupt.rdf.wrapping.jena.ValueMappings;

import java.lang.reflect.Method;
import java.net.URI;
import java.time.Instant;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Resource definition")
class ResourceDefinitionValueMappingTest {
    private static final String P = "urn:example:p";
    private static final String C = "urn:example:c";
    private static final Object[] RESOURCE_DEFINITION_METHODS = stream(ResourceDefinition.class.getDeclaredMethods())
            .map(m -> m.getAnnotation(ResourceProperty.class))
            .map(ResourceProperty::valueMapping)
            .map(ValueMapping::getMethodName)
            .toArray();

    private Model m;
    private GraphDefinition wrapped;

    @BeforeEach
    void setUp() {
        m = createDefaultModel();
        wrapped = GraphDefinition.wrap(m);
    }

    @DisplayName("properly converts node with mapping")
    @ParameterizedTest(name = "{0}")
    @MethodSource
    void e2e(final ValueMapping valueMapping, final RDFNode original, final Object expected) {
        final org.apache.jena.rdf.model.Property p = m.createProperty(P);
        final org.apache.jena.rdf.model.Resource c = m.createResource(C);

        final Matcher<Object> isExpected;
        final RDFNode o;

        if (valueMapping == AS) {
            o = m.createResource().addProperty(p, (String) expected); // Complex properties need a bit more setup
            isExpected = hasProperty(LITERAL_AS_STRING.getMethodName(), equalTo(expected)); // and matcher
        } else {
            o = original; // method source supplies value of object being wrapped
            isExpected = equalTo(expected); // as well as expected value
        }

        m.createResource()
                .addProperty(type, c) // anchor for graph definition
                .addProperty(p, o); // what's being wrapped

        assertThat(wrapped.getResource(), hasProperty(valueMapping.getMethodName(), isExpected));
    }

    private static Stream<Arguments> e2e() {
        final String string = randomUUID().toString();
        final URI uri = URI.create(string);
        final Instant instant = Instant.now();
        final boolean bool = true;
        final Integer integer = instant.getNano();

        final Literal stringLiteral = createTypedLiteral(string);
        final org.apache.jena.rdf.model.Resource resource = createResource(string);
        final Literal instantLiteral = createTypedLiteral(instant);
        final Literal booleanLiteral = createTypedLiteral(bool);
        final Literal integerLiteral = createTypedLiteral(integer);

        return Stream.of(
                arguments(LITERAL_AS_STRING, stringLiteral, string),
                arguments(IRI_AS_URI, resource, uri),
                arguments(IRI_AS_STRING, resource, string),
                arguments(LITERAL_AS_INSTANT, instantLiteral, instant),
                arguments(LITERAL_AS_BOOLEAN, booleanLiteral, bool),
                arguments(LITERAL_AS_INTEGER_OR_NULL, integerLiteral, integer),
                arguments(AS, null, string)
        );
    }

    @DisplayName("mock has equivalent of ValueMappings method")
    @ParameterizedTest(name = "{0}")
    @MethodSource
    void valueMappingsMethods(final String name) {
        assertThat(RESOURCE_DEFINITION_METHODS, hasItemInArray(name));
    }

    private static Stream<String> valueMappingsMethods() {
        return stream(ValueMappings.class.getDeclaredMethods())
                .filter(m -> isPublic(m.getModifiers()))
                .map(Method::getName);
    }

    @Resource
    interface ResourceDefinition {
        @ResourceProperty(predicate = P, valueMapping = LITERAL_AS_STRING)
        String getLiteralAsString();

        @ResourceProperty(predicate = P, valueMapping = IRI_AS_URI)
        URI getIriAsUri();

        @ResourceProperty(predicate = P, valueMapping = IRI_AS_STRING)
        String getIriAsString();

        @ResourceProperty(predicate = P, valueMapping = LITERAL_AS_INSTANT)
        Instant getLiteralAsInstant();

        @ResourceProperty(predicate = P, valueMapping = LITERAL_AS_BOOLEAN)
        Boolean getLiteralAsBoolean();

        @ResourceProperty(predicate = P, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
        Integer getLiteralAsIntegerOrNull();

        @ResourceProperty(predicate = P)
        ResourceDefinition getAs();
    }

    @Graph
    interface GraphDefinition {
        static GraphDefinition wrap(final Model original) {
            return Manager.wrap(original, GraphDefinition.class);
        }

        @GraphProperty(value = C, method = INSTANCE_OF)
        ResourceDefinition getResource();
    }
}
