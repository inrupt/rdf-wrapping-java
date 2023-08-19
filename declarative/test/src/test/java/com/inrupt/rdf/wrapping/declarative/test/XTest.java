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
        final Dataset dataset = datasetFrom("" +
                                            "PREFIX : <urn:example:>\n" +
                                            "\n" +
                                            "[\n" +
                                            "    a :C ;\n" +
                                            "    :p1 [\n" +
                                            "        :p2 \"XXX\" ;\n" +
                                            "    ] ;\n" +
                                            "] .\n");

        assertDoesNotThrow(() -> X.wrap(dataset));
    }

    @Test
    void datasetCanGetDefaultGraph() {
        final X x = X.wrap(DatasetFactory.create());

        assertDoesNotThrow(x::getDefaultGraph);
    }

    @Test
    void graphCanWrap() {
        final Model model = modelFrom("" +
                                          "PREFIX : <urn:example:>\n" +
                                          "\n" +
                                          "[\n" +
                                          "    a :C ;\n" +
                                          "    :p1 [\n" +
                                          "        :p2 \"XXX\" ;\n" +
                                          "    ] ;\n" +
                                          "] .\n");

        assertDoesNotThrow(() -> X.Y.wrap(model));
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
