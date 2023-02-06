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

import static com.inrupt.rdf.wrapping.jena.ValueMapping.getRdfNode;
import static java.util.UUID.randomUUID;
import static org.apache.jena.commonsrdf.JenaCommonsRDF.fromJena;
import static org.apache.jena.graph.NodeFactory.*;
import static org.apache.jena.sparql.graph.GraphFactory.createDefaultGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Jena Value Mapping")
class JenaValueMappingTest {
    @DisplayName("converts URI resources")
    @Test
    void convertsUriResources() {
        final String iriString = randomUUID().toString();
        final org.apache.jena.graph.Graph jenaGraph = createDefaultGraph();
        final Node jenaNode = createURI(iriString);
        final Graph graph = fromJena(jenaGraph);
        final RDFTerm term = fromJena(jenaNode);

        assertThat(getRdfNode(term, graph), both(
                instanceOf(Resource.class)).and(
                hasProperty("URI", is(equalTo(iriString)))));
    }

    @DisplayName("converts blank node resources")
    @Test
    void convertsBlanks() {
        final String id = randomUUID().toString();
        final org.apache.jena.graph.Graph jenaGraph = createDefaultGraph();
        final Node jenaNode = createBlankNode(id);
        final Graph graph = fromJena(jenaGraph);
        final RDFTerm term = fromJena(jenaNode);

        assertThat(getRdfNode(term, graph), both(
                instanceOf(Resource.class)).and(
                hasProperty("id", hasProperty("labelString", is(equalTo(id))))));
    }

    @DisplayName("converts language-tagged literals")
    @Test
    void convertsLangStrings() {
        final String lexicalForm = randomUUID().toString();
        final String language = randomUUID().toString();
        final org.apache.jena.graph.Graph jenaGraph = createDefaultGraph();
        final Node jenaNode = createLiteral(lexicalForm, language);
        final Graph graph = fromJena(jenaGraph);
        final RDFTerm term = fromJena(jenaNode);

        assertThat(getRdfNode(term, graph), both(
                instanceOf(Literal.class)).and(
                hasProperty("lexicalForm", is(equalTo(lexicalForm)))).and(
                hasProperty("language", is(equalTo(language)))));
    }

    @DisplayName("converts typed literals")
    @Test
    void convertsTypedLiterals() {
        final String lexicalForm = randomUUID().toString();
        final String datatype = randomUUID().toString();
        final RDFDatatype jenaDatatype = getType(datatype);
        final org.apache.jena.graph.Graph jenaGraph = createDefaultGraph();
        final Node jenaNode = createLiteral(lexicalForm, jenaDatatype);
        final Graph graph = fromJena(jenaGraph);
        final RDFTerm term = fromJena(jenaNode);

        assertThat(getRdfNode(term, graph), both(
                instanceOf(Literal.class)).and(
                hasProperty("lexicalForm", is(equalTo(lexicalForm)))).and(
                hasProperty("datatype", hasProperty("URI", is(equalTo(datatype))))));
    }
}
