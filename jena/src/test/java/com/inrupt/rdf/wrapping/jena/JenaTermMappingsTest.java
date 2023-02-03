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
package com.inrupt.rdf.wrapping.jena;

import static com.inrupt.rdf.wrapping.jena.NodeMappings.*;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.inrupt.rdf.wrapping.commons.TermMappings;
import com.inrupt.rdf.wrapping.test.base.HasSameMethods;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Jena Term Mappings")
class JenaTermMappingsTest extends HasSameMethods {
    @BeforeAll
    static void init() {
        HasSameMethods.initializeClassesForComparison(
                TermMappings.class,
                NodeMappings.class);
    }

    @Override
    protected Stream<Class<?>> translate(final Class<?> clazz) {
        if (clazz == RDFTerm.class) {
            return Stream.of(RDFNode.class);
        }

        if (clazz == RDFNode.class) {
            return Stream.of(RDFTerm.class);
        }

        if (clazz == Graph.class) {
            return Stream.of(Model.class);
        }

        if (clazz == Model.class) {
            return Stream.of(Graph.class);
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

        return Stream.of(clazz);
    }

    private static final Model MODEL = ModelFactory.createDefaultModel();

    @Test
    void asStringLiteralTest() {
        final String string = randomUUID().toString();

        assertThrows(NullPointerException.class, () -> asStringLiteral(null, null));
        assertThrows(NullPointerException.class, () -> asStringLiteral(string, null));

        assertThat(asStringLiteral(string, MODEL), both(
                instanceOf(Literal.class)).and(
                hasProperty("lexicalForm", is(string))).and(
                hasProperty("datatype", is(XSDDatatype.XSDstring))));
    }

    @Test
    void asIriResourceStringTest() {
        final String uri = "urn:" + randomUUID();

        assertThrows(NullPointerException.class, () -> asIri((String) null, null));
        assertThrows(NullPointerException.class, () -> asIri(uri, null));

        assertThat(asIri(uri, MODEL), both(
                instanceOf(Resource.class)).and(
                hasProperty("URI", is(uri))));
    }

    @Test
    void asIriResourceUriTest() {
        final URI uri = URI.create("urn:" + randomUUID());

        assertThrows(NullPointerException.class, () -> asIri((URI) null, null));
        assertThrows(NullPointerException.class, () -> asIri(uri, null));

        assertThat(asIri(uri, MODEL), both(
                instanceOf(Resource.class)).and(
                hasProperty("URI", is(uri.toString()))));
    }

    @Test
    void asTypedLiteralTest() {
        final Instant instant = Instant.now();

        assertThrows(NullPointerException.class, () -> asTypedLiteral(null, null));
        assertThrows(NullPointerException.class, () -> asTypedLiteral(instant, null));

        assertThat(asTypedLiteral(instant, MODEL), both(
                instanceOf(Literal.class)).and(
                hasProperty("lexicalForm", is(instant.toString()))).and(
                hasProperty("datatype", is(XSDDatatype.XSDdateTime))));
    }

    @Test
    void identityTest() {
        final Resource blank = MODEL.createResource();

        assertThrows(NullPointerException.class, () -> identity(null, null));
        assertThrows(NullPointerException.class, () -> identity(blank, null));

        assertThat(identity(blank, MODEL), sameInstance(blank));
    }
}
