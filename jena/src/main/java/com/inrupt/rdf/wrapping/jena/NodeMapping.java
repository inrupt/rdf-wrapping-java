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
