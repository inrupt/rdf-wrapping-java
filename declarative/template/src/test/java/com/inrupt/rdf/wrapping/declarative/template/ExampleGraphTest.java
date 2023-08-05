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

import com.github.jsonldjava.shaded.com.google.common.base.Charsets;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

class ExampleGraphTest {
    @Test
    void test() {
        final Dataset dataset = datasetFrom("""
                PREFIX : <urn:example:>

                [
                    a :C ;
                    :p1 [
                        :p2 "XXX" ;
                    ] ;
                ] .
                """);

        final var wrap = ExampleDataset.wrap(dataset);
        final var graph = wrap.getGraph();
        final var resource = graph.getResource();
        final var p1 = resource.getP1();
        final var p2 = p1.getP2();

        System.out.println(p2);
    }

    private static Dataset datasetFrom(final String rdf) {
        final var dataset = DatasetFactory.create();
        RDFDataMgr.read(dataset, IOUtils.toInputStream(rdf, Charsets.UTF_8), Lang.TRIG);
        return dataset;
    }
}
