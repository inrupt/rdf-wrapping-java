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
package com.inrupt.rdf.wrapping.processor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.jsonldjava.shaded.com.google.common.base.Charsets;

import java.net.URI;

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
    void graphCanGetFirstSubjectOf() {
        final Model model = ModelFactory.createDefaultModel();
        final X.Y y = X.Y.wrap(model);

        assertDoesNotThrow(y::getResource2);
    }

    @Test
    void graphCanGetFirstObjectOf() {
        final Model model = ModelFactory.createDefaultModel();
        final X.Y y = X.Y.wrap(model);

        assertDoesNotThrow(y::getResource3);
    }

    @Test
    void resourceCanGetProperty() {
        final Model model = modelFrom("PREFIX : <urn:example:>\n" +
                                      "\n" +
                                      "[\n" +
                                      "    a :C ;\n" +
                                      "] .\n");
        final X.Y y = X.Y.wrap(model);
        final X.Y.Z z = y.getResource();

        assertDoesNotThrow(z::getProperty);
    }

    @Test
    void e2e() {
        final Dataset dataset = datasetFrom("PREFIX : <urn:example:>\n" +
                                            "\n" +
                                            "GRAPH <urn:example:g1> {\n" +
                                            "    [\n" +
                                            "        a :C ;\n" +
                                            "        :p :VALUE ;\n" +
                                            "    ] .\n" +
                                            "\n" +
                                            "    [ :p [] ] .\n" +
                                            "}\n");

        final X x = X.wrap(dataset);
        final X.Y y = x.getNamedGraph();
        final X.Y.Z z = y.getResource();
        final X.Y.Z z2 = y.getResource2();
        final X.Y.Z z3 = y.getResource3();
        final String p = z.getProperty();
        final URI p2 = z.getProperty2();
        final X.Y.Z2 p3 = z.getProperty3();
        final X.Y.Z3 p4 = p3.getProperty();

        System.out.println(z);
        System.out.println(z2);
        System.out.println(z3);
        System.out.println(p);
        System.out.println(p2);
        System.out.println(p3);
        System.out.println(p4);
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
