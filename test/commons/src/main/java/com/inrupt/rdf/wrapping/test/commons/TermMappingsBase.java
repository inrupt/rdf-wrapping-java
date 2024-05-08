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
package com.inrupt.rdf.wrapping.test.commons;

import static com.inrupt.rdf.wrapping.commons.TermMappings.*;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.inrupt.rdf.wrapping.commons.RDFFactory;

import java.net.URI;
import java.time.Instant;

import org.apache.commons.rdf.api.*;
import org.junit.jupiter.api.Test;

public class TermMappingsBase {
    private static final RDF FACTORY = RDFFactory.getInstance();
    private static final Graph GRAPH = FACTORY.createGraph();
    private static final String LEXICAL_FORM = "lexicalForm";

    @Test
    void asStringLiteralTest() {
        final IRI xsdString = FACTORY.createIRI("http://www.w3.org/2001/XMLSchema#string");
        final String string = randomUUID().toString();

        assertThrows(NullPointerException.class, () -> asStringLiteral(null, null));
        assertThrows(NullPointerException.class, () -> asStringLiteral(string, null));

        assertThat(asStringLiteral(string, GRAPH), both(
                instanceOf(Literal.class)).and(
                hasProperty(LEXICAL_FORM, is(string))).and(
                hasProperty("datatype", is(xsdString))));
    }

    @Test
    void asIriResourceStringTest() {
        final String uri = "urn:" + randomUUID();

        assertThrows(NullPointerException.class, () -> asIri((String) null, null));
        assertThrows(NullPointerException.class, () -> asIri(uri, null));

        assertThat(asIri(uri, GRAPH), both(
                instanceOf(IRI.class)).and(
                hasProperty("IRIString", is(uri))));
    }

    @Test
    void asIriResourceUriTest() {
        final URI uri = URI.create("urn:" + randomUUID());

        assertThrows(NullPointerException.class, () -> asIri((URI) null, null));
        assertThrows(NullPointerException.class, () -> asIri(uri, null));

        assertThat(asIri(uri, GRAPH), both(
                instanceOf(IRI.class)).and(
                hasProperty("IRIString", is(uri.toString()))));
    }

    @Test
    void asTypedLiteralInstantTest() {
        final IRI xsdDateTime = FACTORY.createIRI("http://www.w3.org/2001/XMLSchema#dateTime");
        final Instant instant = Instant.now();

        assertThrows(NullPointerException.class, () -> asTypedLiteral((Instant)null, null));
        assertThrows(NullPointerException.class, () -> asTypedLiteral(instant, null));

        assertThat(asTypedLiteral(instant, GRAPH), both(
                instanceOf(Literal.class)).and(
                hasProperty(LEXICAL_FORM, is(instant.toString()))).and(
                hasProperty("datatype", is(xsdDateTime))));
    }

    @Test
    void asTypedLiteralBooleanTest() {
        final IRI xsdBoolean = FACTORY.createIRI("http://www.w3.org/2001/XMLSchema#boolean");

        assertThrows(NullPointerException.class, () -> asTypedLiteral((Boolean) null, null));
        assertThrows(NullPointerException.class, () -> asTypedLiteral(true, null));

        assertThat(asTypedLiteral(true, GRAPH), both(
                instanceOf(Literal.class)).and(
                hasProperty(LEXICAL_FORM, is("true"))).and(
                hasProperty("datatype", is(xsdBoolean))));
    }

    @Test
    void asTypedLiteralIntegerTest() {
        final IRI xsdInt = FACTORY.createIRI("http://www.w3.org/2001/XMLSchema#int");

        assertThrows(NullPointerException.class, () -> asTypedLiteral((Integer) null, null));
        assertThrows(NullPointerException.class, () -> asTypedLiteral(1, null));

        assertThat(asTypedLiteral(1, GRAPH), both(
                instanceOf(Literal.class)).and(
                hasProperty(LEXICAL_FORM, is("1"))).and(
                hasProperty("datatype", is(xsdInt))));
    }

    @Test
    void identityTest() {
        final BlankNode blank = FACTORY.createBlankNode();

        assertThrows(NullPointerException.class, () -> identity(null, null));
        assertThrows(NullPointerException.class, () -> identity(blank, null));

        assertThat(identity(blank, GRAPH), sameInstance(blank));
    }
}
