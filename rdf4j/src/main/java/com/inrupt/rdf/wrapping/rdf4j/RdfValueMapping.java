package com.inrupt.rdf.wrapping.rdf4j;


import com.inrupt.commons.rdf4j.RDF4J;
import com.inrupt.commons.rdf4j.RDF4JGraph;
import com.inrupt.rdf.wrapping.commons.TermMapping;

import java.util.Optional;
import java.util.function.BiFunction;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;

@FunctionalInterface
public interface RdfValueMapping<T> extends BiFunction<T, Model, Value> {
    @Override
    Value apply(T value, Model graph);

    default TermMapping<T> asCommons() {
        return (value, graph) -> {
            if (!(graph instanceof RDF4JGraph)) {
                throw new IllegalStateException("Graph is not RDF4J graph");
            }

            final Optional<Model> model = ((RDF4JGraph) graph).asModel();
            if (!model.isPresent()) {
                throw new IllegalStateException("Graph lacks RDF4J model");
            }

            final RDF4J rdf4J = new RDF4J();
            return rdf4J.asRDFTerm(this.apply(value, model.get()));
        };
    }
}
