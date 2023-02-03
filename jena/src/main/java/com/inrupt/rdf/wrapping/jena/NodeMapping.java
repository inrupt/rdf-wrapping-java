package com.inrupt.rdf.wrapping.jena;

import static org.apache.jena.commonsrdf.JenaCommonsRDF.fromJena;
import static org.apache.jena.commonsrdf.JenaCommonsRDF.toJena;
import static org.apache.jena.rdf.model.ModelFactory.createModelForGraph;

import com.inrupt.rdf.wrapping.commons.TermMapping;

import java.util.function.BiFunction;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

@FunctionalInterface
public interface NodeMapping<T> extends BiFunction<T, Model, RDFNode> {
    @Override
    RDFNode apply(T value, Model graph);

    default TermMapping<T> asCommons() {
        return (value, graph) -> fromJena(this.apply(value, createModelForGraph(toJena(graph))).asNode());
    }
}
