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

import static org.apache.jena.commonsrdf.JenaCommonsRDF.toJena;
import static org.apache.jena.rdf.model.ModelFactory.createModelForGraph;

import com.inrupt.rdf.wrapping.commons.ValueMappings;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.rdf.api.*;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Represents a function that converts a term in a graph to another value. Intended to be used in wrapping classes.
 *
 * @param <T> the type of value to convert to
 *
 * @see ValueMappings Common mappings
 */
@FunctionalInterface
public interface ValueMapping<T> extends Function<RDFNode, T> {
    /**
     * Converts a node to a value.
     *
     * @param node the node to convert
     *
     * @return a value that represents the term
     */
    @Override
    T apply(RDFNode node);

    default com.inrupt.rdf.wrapping.commons.ValueMapping<T> asCommons() {
        return (term, graph) -> apply(getRdfNode(term, graph));
    }

    static RDFNode getRdfNode(final RDFTerm term, final Graph graph) {
        final Model model = createModelForGraph(toJena(graph));

        // org.apache.jena.commonsrdf.impl.JCR_Factory#fromJena(Node) creates only these types of terms.

        if (term instanceof IRI) {
            return model.createResource(((IRI) term).getIRIString());
        } else if (term instanceof BlankNode) {
            return model.createResource(new AnonId(((BlankNode) term).uniqueReference()));
        } else { // Literal
            final Literal term1 = (Literal) term;
            final Optional<String> languageTag = term1.getLanguageTag();
            final String lexicalForm = term1.getLexicalForm();

            if (languageTag.isPresent()) {
                return model.createLiteral(lexicalForm, languageTag.get());
            }

            return model.createTypedLiteral(lexicalForm, term1.getDatatype().getIRIString());
        }
    }
}
