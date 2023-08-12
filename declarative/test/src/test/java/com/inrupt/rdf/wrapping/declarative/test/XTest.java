package com.inrupt.rdf.wrapping.declarative.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.jsonldjava.shaded.com.google.common.base.Charsets;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
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

    private static Dataset datasetFrom(final String rdf) {
        final Dataset dataset = DatasetFactory.create();
        RDFDataMgr.read(dataset, IOUtils.toInputStream(rdf, Charsets.UTF_8), Lang.TRIG);
        return dataset;
    }
}
