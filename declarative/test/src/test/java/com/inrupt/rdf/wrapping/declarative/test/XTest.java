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
package com.inrupt.rdf.wrapping.declarative.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.jsonldjava.shaded.com.google.common.base.Charsets;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

class XTest {
    @Test
    void datasetCanWrap() {
        final Dataset dataset = DatasetFactory.create();

        assertDoesNotThrow(() -> X.wrap(dataset));
    }

    @Test
    void datasetCanGetDefaultGraph() {
        final X x = X.wrap(DatasetFactory.create());

        assertDoesNotThrow(x::getDefaultGraph);
    }

    @Test
    void datasetCanGetNamedGraph() {
        final X x = X.wrap(DatasetFactory.create());

        assertDoesNotThrow(x::getNamedGraph);
    }

    @Test
    void graphCanWrap() {
        final Model model = ModelFactory.createDefaultModel();

        assertDoesNotThrow(() -> X.Y.wrap(model));
    }

    @Test
    void graphCanGetFirstInstanceOf() {
        final Model model = ModelFactory.createDefaultModel();
        final X.Y y = X.Y.wrap(model);

        assertDoesNotThrow(y::getResource);
    }

    @Test
    void e2e() {
        final Dataset dataset = datasetFrom("" +
                                            "PREFIX : <urn:example:>\n" +
                                            "\n" +
                                            "GRAPH <urn:example:g1> {\n" +
                                            "    [\n" +
                                            "        a :C ;\n" +
                                            "    ] .\n" +
                                            "}\n");

        final X x = X.wrap(dataset);
        final X.Y y = x.getNamedGraph();
        final X.Y.Z z = y.getResource();

        System.out.println(z);
    }

    private static Dataset datasetFrom(final String rdf) {
        final Dataset dataset = DatasetFactory.create();
        RDFDataMgr.read(dataset, IOUtils.toInputStream(rdf, Charsets.UTF_8), Lang.TRIG);
        return dataset;
    }

    private static Model modelFrom(final String rdf) {
        final Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, IOUtils.toInputStream(rdf, Charsets.UTF_8), Lang.TRIG);
        return model;
    }
}
