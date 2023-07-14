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

import com.inrupt.commons.rdf4j.RDF4JGraph;
import com.inrupt.commons.rdf4j.RDF4JTerm;
import com.inrupt.rdf.wrapping.commons.ValueMappings;

import java.util.Optional;
import java.util.function.BiFunction;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;

/**
 * Represents a function that converts a term in a graph to another value. Intended to be used in wrapping classes.
 *
 * @param <T> the type of value to convert to
 *
 * @see ValueMappings Common mappings
 */
@FunctionalInterface
public interface ValueMapping<T> extends BiFunction<Value, Model, T> {
    /**
     * Converts a node to a value.
     *
     * @param rdfValue the node to convert
     *
     * @return a value that represents the term
     */
    @Override
    T apply(Value rdfValue, Model model);

    default com.inrupt.rdf.wrapping.commons.ValueMapping<T> asCommons() {
        return (term, graph) -> {
            if (!(term instanceof RDF4JTerm)) {
                throw new IllegalStateException("Graph is not RDF4J graph");
            }

            if (!(graph instanceof RDF4JGraph)) {
                throw new IllegalStateException("Graph is not RDF4J graph");
            }

            final Optional<Model> model = ((RDF4JGraph) graph).asModel();
            if (!model.isPresent()) {
                throw new IllegalStateException("Graph lacks RDF4J model");
            }


            final RDF4JTerm rdfValue = (RDF4JTerm) term;
            return apply(rdfValue.asValue(), model.get());
        };
    }
}
