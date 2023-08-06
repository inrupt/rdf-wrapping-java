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

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

import com.inrupt.rdf.wrapping.jena.UriOrBlankFactory;
import com.inrupt.rdf.wrapping.jena.ValueMappings;
import com.inrupt.rdf.wrapping.jena.WrapperResource;

import javax.annotation.processing.Generated;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;

/**
 * Warning this class consists of generated code.
 */
@Generated(value = "TODO: FQCN of generator", date = "TODO: Generation date")
public class ExampleNode2_$impl extends WrapperResource implements ExampleNode2 {
    static final Implementation factory = new UriOrBlankFactory(ExampleNode2_$impl::new);

    protected ExampleNode2_$impl(Node node, EnhGraph model) {
        super(node, model);
    }

    @Override
    public String getP2() {
        return anyOrNull(
                createProperty("urn:example:p2"),
                ValueMappings::literalAsString);
    }
}
