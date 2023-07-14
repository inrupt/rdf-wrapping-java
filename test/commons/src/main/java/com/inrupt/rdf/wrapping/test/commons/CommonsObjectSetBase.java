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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.inrupt.rdf.wrapping.commons.*;
import com.inrupt.rdf.wrapping.test.base.ObjectSetBase;

import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDF;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommonsObjectSetBase extends ObjectSetBase {
    private static final RDF FACTORY = RDFFactory.getInstance();
    private static final TermMapping<String> T2V = TermMappings::asStringLiteral;
    private static final ValueMapping<String> V2T = ValueMappings::literalAsString;

    private Graph graph;

    @Override
    protected void addTriple(final String subject, final String predicate, final String object) {
        final IRI s = FACTORY.createIRI(subject);
        final IRI p = FACTORY.createIRI(predicate);
        final Literal o = FACTORY.createLiteral(object);

        graph.add(s, p, o);
    }

    @Override
    protected boolean containsTriple(final String subject, final String predicate, final String object) {
        final IRI s = FACTORY.createIRI(subject);
        final IRI p = FACTORY.createIRI(predicate);
        final Literal o = FACTORY.createLiteral(object);

        return graph.contains(s, p, o);
    }

    @Override
    protected Set<String> createNewSetForTest(final String subject, final String predicate) {
        graph = FACTORY.createGraph();
        return createOtherSetOverSameGraph(subject, predicate);
    }

    @Override
    protected Set<String> createOtherSetOverSameGraph(final String subject, final String predicate) {
        final IRI s = FACTORY.createIRI(subject);
        final IRI p = FACTORY.createIRI(predicate);

        return new ObjectSet<>(s, p, graph, T2V, V2T);
    }

    // Here instead of com.inrupt.rdf.wrapping.test.base.ObjectSetBase due to circular dependencies
    @DisplayName("Set invariant: size capped at greatest integer")
    @Test
    void sizeCappedAtIntegerMaxValue() {
        final IRI any = mock(IRI.class);
        final Stream<?> manyStatements = mock(Stream.class);
        final Graph largeGraph = mock(Graph.class);
        final ObjectSet<?> set = new ObjectSet<>(any, any, largeGraph, T2V, V2T);

        when(manyStatements.count()).thenReturn(Integer.MAX_VALUE + 1L);
        doReturn(manyStatements).when(largeGraph).stream(any(), any(), any());

        assertThat(set, hasSize(lessThanOrEqualTo(Integer.MAX_VALUE)));
    }
}
