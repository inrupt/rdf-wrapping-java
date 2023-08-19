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
package com.inrupt.rdf.wrapping.declarative.template;

import com.inrupt.rdf.wrapping.jena.WrapperModel;

import javax.annotation.Generated;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.graph.GraphFactory;

/**
 * Warning this class consists of generated code.
 */
@Generated(value = "TODO: FQCN of generator", date = "TODO: Generation date")
public class ExampleGraph_$impl extends WrapperModel implements ExampleGraph {
    protected ExampleGraph_$impl(final Graph base) {
        super(base);

        getPersonality()
                .add(ExampleNode1_$impl.class, ExampleNode1_$impl.factory)
                .add(ExampleNode2_$impl.class, ExampleNode2_$impl.factory);
    }

    public static ExampleGraph wrap(final Model original) {
        return new ExampleGraph_$impl(original.getGraph());
    }

    public static ExampleGraph create() {
        return new ExampleGraph_$impl(GraphFactory.createDefaultGraph());
    }

    @Override
    public ExampleNode1 getResource() {
        return firstInstanceOf("urn:example:C", ExampleNode1_$impl.class);
    }
}
