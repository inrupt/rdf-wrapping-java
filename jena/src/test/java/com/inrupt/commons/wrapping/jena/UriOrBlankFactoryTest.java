/*
 * Proprietary and Confidential
 *
 * Copyright Inrupt Inc. 2023 - all rights reserved.
 *
 * Do not use without explicit permission from Inrupt Inc.
 */
package com.inrupt.commons.wrapping.jena;

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
