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
package com.inrupt.rdf.wrapping.jena;

import static java.util.UUID.randomUUID;
import static org.apache.jena.graph.NodeFactory.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.EnhNode;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceRequiredException;
import org.junit.jupiter.api.Test;

class UriOrBlankFactoryTest {
    @Test
    void constructorTest() {
        assertThrows(NullPointerException.class, () -> new UriOrBlankFactory(null));
    }

    @Test
    void wrapTest() {
        final UriOrBlankFactory implementation = new UriOrBlankFactory(EnhNode::new);
        final Node blank = createBlankNode();
        final Node literal = createLiteral(randomUUID().toString());
        final EnhGraph graph = new EnhGraph(null, null);

        assertThrows(NullPointerException.class, () -> implementation.wrap(null, null));
        assertThrows(NullPointerException.class, () -> implementation.wrap(blank, null));
        assertThrows(ResourceRequiredException.class, () -> implementation.wrap(literal, graph));

        assertThat(implementation.wrap(blank, graph).asNode(), is(blank));
    }

    @Test
    void canWrapTest() {
        final UriOrBlankFactory implementation = new UriOrBlankFactory(EnhNode::new);
        final Node blank = createBlankNode();
        final Node iri = createURI(randomUUID().toString());
        final Node literal = createLiteral(randomUUID().toString());
        final EnhGraph graph = new EnhGraph(null, null);

        assertThrows(NullPointerException.class, () -> implementation.canWrap(null, null));
        assertThrows(NullPointerException.class, () -> implementation.canWrap(blank, null));

        assertThat(implementation.canWrap(literal, graph), is(false));
        assertThat(implementation.canWrap(iri, graph), is(true));
        assertThat(implementation.canWrap(blank, graph), is(true));
    }
}
