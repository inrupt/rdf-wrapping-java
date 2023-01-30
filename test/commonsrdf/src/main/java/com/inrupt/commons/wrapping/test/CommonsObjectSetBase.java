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
package com.inrupt.commons.wrapping.test;

import com.inrupt.commons.wrapping.commonsrdf.*;
import com.inrupt.commons.wrapping.test.base.ObjectSetBase;

import java.util.Set;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDF;

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
}
