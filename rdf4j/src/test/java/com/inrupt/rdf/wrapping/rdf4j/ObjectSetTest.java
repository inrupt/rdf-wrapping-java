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
package com.inrupt.rdf.wrapping.rdf4j;

import com.inrupt.rdf.wrapping.test.base.ObjectSetBase;

import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.DynamicModelFactory;
import org.eclipse.rdf4j.model.util.Values;
import org.junit.jupiter.api.DisplayName;

@DisplayName("RDF4J Predicate-Object Set")
class ObjectSetTest extends ObjectSetBase {
    private static final RdfValueMapping<String> N2V = (v, m) -> Values.literal(v);
    private static final ValueMapping<String> V2N = (n, m) -> n.stringValue();
    private static final DynamicModelFactory FACTORY = new DynamicModelFactory();

    private Model model;

    @Override
    protected void addTriple(final String subject, final String predicate, final String object) {
        final IRI s = Values.iri(subject);
        final IRI p = Values.iri(predicate);
        final Literal o = Values.literal(object);

        model.add(s, p, o);
    }

    @Override
    protected boolean containsTriple(final String subject, final String predicate, final String object) {
        final IRI s = Values.iri(subject);
        final IRI p = Values.iri(predicate);
        final Literal o = Values.literal(object);

        return model.contains(s, p, o);
    }

    @Override
    protected Set<String> createNewSetForTest(final String subject, final String predicate) {
        model = FACTORY.createEmptyModel();
        return createOtherSetOverSameGraph(subject, predicate);
    }

    @Override
    protected Set<String> createOtherSetOverSameGraph(final String subject, final String predicate) {
        final IRI s = subject == null ? null : Values.iri(subject);
        final IRI p = predicate == null ? null : Values.iri(predicate);

        return new ObjectSet<>(s, p, model, N2V, V2N);
    }
}
