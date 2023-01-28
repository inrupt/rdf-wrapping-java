/*
 * Copyright 2023 Inrupt Inc.
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
package com.inrupt.commons.wrapping.jena;

import static com.inrupt.commons.wrapping.jena.ValueMappings.*;
import static java.util.UUID.randomUUID;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStringLiteral;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.inrupt.commons.wrapping.test.base.HasSameMethods;

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.enhanced.UnsupportedPolymorphismException;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.sparql.graph.GraphFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Jena Value Mappings")
class JenaValueMappingsTest extends HasSameMethods {
    @BeforeAll
    static void init() {
        HasSameMethods.initializeClassesForComparison(
                com.inrupt.commons.wrapping.commonsrdf.ValueMappings.class,
                ValueMappings.class);
    }

    @Override
    protected Stream<Class<?>> translate(final Class<?> clazz) {
        if (clazz == RDFNode.class) {
            return Stream.of(RDFTerm.class, Graph.class);
        }

        if (clazz == RDFTerm.class) {
            return Stream.of(RDFNode.class);
        }

        if (clazz == Graph.class) {
            return Stream.of((Class<?>) null);
        }

        if (clazz == Literal.class) {
            return Stream.of(org.apache.commons.rdf.api.Literal.class);
        }

        if (clazz == org.apache.commons.rdf.api.Literal.class) {
            return Stream.of(Literal.class);
        }

        if (clazz == Resource.class) {
            return Stream.of(org.apache.commons.rdf.api.IRI.class);
        }

        if (clazz == org.apache.commons.rdf.api.IRI.class) {
            return Stream.of(Resource.class);
        }

        if (clazz == ValueMapping.class) {
            return Stream.of(com.inrupt.commons.wrapping.commonsrdf.ValueMapping.class);
        }

        if (clazz == com.inrupt.commons.wrapping.commonsrdf.ValueMapping.class) {
            return Stream.of(ValueMapping.class);
        }

        return Stream.of(clazz);
    }

    @Test
    void literalAsStringTest() {
        final Resource blank = createResource();
        final Literal literal = createStringLiteral(randomUUID().toString());

        assertThrows(NullPointerException.class, () -> literalAsString(null));
        assertThrows(LiteralRequiredException.class, () -> literalAsString(blank));

        assertThat(literalAsString(literal), is(literal.getLexicalForm()));
    }

    @Test
    void iriAsStringTest() {
        final Resource blank = createResource();
        final Resource iri = createResource(randomUUID().toString());

        assertThrows(NullPointerException.class, () -> iriAsString(null));
        assertThrows(ResourceRequiredException.class, () -> iriAsString(blank));

        assertThat(iriAsString(iri), is(iri.getURI()));
    }

    @Test
    void literalAsInstantTest() {
        final Resource blank = createResource();
        final Literal malformed = createStringLiteral(randomUUID().toString());
        final Literal literal = createStringLiteral(Instant.now().toString());

        assertThrows(NullPointerException.class, () -> literalAsInstant(null));
        assertThrows(LiteralRequiredException.class, () -> literalAsInstant(blank));
        assertThrows(DateTimeParseException.class, () -> literalAsInstant(malformed));

        assertThat(literalAsInstant(literal), is(Instant.parse(literal.getLexicalForm())));
    }

    @Test
    void literalAsBooleanTest() {
        final Resource blank = createResource();
        final Literal literal = ResourceFactory.createTypedLiteral(true);

        assertThrows(NullPointerException.class, () -> literalAsBoolean(null));
        assertThrows(LiteralRequiredException.class, () -> literalAsBoolean(blank));

        assertThat(literalAsBoolean(literal), is(true));
    }

    @Test
    void asTest() {
        final Model model = new MockModel();
        final Resource blank = model.createResource();
        final Literal literal = createStringLiteral(randomUUID().toString());
        final ValueMapping<MockModel.MockNode> asWrapperNode = as(MockModel.MockNode.class);

        assertThrows(NullPointerException.class, () -> as(null));

        assertThrows(NullPointerException.class, () -> asWrapperNode.apply(null));
        assertThrows(UnsupportedPolymorphismException.class, () -> asWrapperNode.apply(literal));
        assertDoesNotThrow(() -> asWrapperNode.apply(blank));
    }

    @Test
    void iriAsUriTest() {
        final Resource blank = createResource();
        final Resource iri = createResource(randomUUID().toString());

        assertThrows(NullPointerException.class, () -> iriAsUri(null));
        assertThrows(ResourceRequiredException.class, () -> iriAsUri(blank));

        assertThat(iriAsUri(iri), is(URI.create(iri.getURI())));
    }

    @Test
    void literalAsIntegerOrNullTest() {
        final Resource blank = createResource();
        final Literal malformed = ResourceFactory.createStringLiteral(randomUUID().toString());
        final Literal literal = createStringLiteral(String.valueOf(Integer.MAX_VALUE));

        assertThrows(NullPointerException.class, () -> literalAsIntegerOrNull(null));
        assertThrows(LiteralRequiredException.class, () -> literalAsIntegerOrNull(blank));

        assertThat(literalAsIntegerOrNull(malformed), is(nullValue()));
        assertThat(literalAsIntegerOrNull(literal), is(Integer.parseInt(literal.getLexicalForm())));
    }

    static class MockModel extends ModelCom {
        MockModel() {
            super(GraphFactory.createDefaultGraph());

            getPersonality().add(MockNode.class, MockNode.factory);
        }

        static final class MockNode extends ResourceImpl {
            static final Implementation factory = new UriOrBlankFactory(MockNode::new);

            private MockNode(final Node node, final EnhGraph enhGraph) {
                super(node, enhGraph);
            }
        }
    }
}
