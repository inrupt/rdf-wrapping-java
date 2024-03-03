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

import static com.inrupt.rdf.wrapping.annotation.Property.Cardinality.ANY_OR_NULL;
import static com.inrupt.rdf.wrapping.annotation.Property.Mapping.*;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.jsonldjava.shaded.com.google.common.base.Charsets;
import com.inrupt.rdf.wrapping.annotation.*;
import com.inrupt.rdf.wrapping.processor.TemporaryE2ETest.DatasetDefinition.GraphDefinition;
import com.inrupt.rdf.wrapping.processor.TemporaryE2ETest.DatasetDefinition.GraphDefinition.ResourceDefinition1;
import com.inrupt.rdf.wrapping.processor.TemporaryE2ETest.DatasetDefinition.GraphDefinition.ResourceDefinition2;
import com.inrupt.rdf.wrapping.processor.TemporaryE2ETest.DatasetDefinition.GraphDefinition.ResourceDefinition3;

import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

class TemporaryE2ETest {
    @Test
    void datasetCanWrap() {
        final Dataset dataset = DatasetFactory.create();

        assertDoesNotThrow(() -> DatasetDefinition.wrap(dataset));
    }

    @Test
    void datasetCanGetDefaultGraph() {
        final DatasetDefinition x = DatasetDefinition.wrap(DatasetFactory.create());

        assertDoesNotThrow(x::getDefaultGraph);
    }

    @Test
    void datasetCanGetNamedGraph() {
        final DatasetDefinition x = DatasetDefinition.wrap(DatasetFactory.create());

        assertDoesNotThrow(x::getNamedGraph);
    }

    @Test
    void graphCanWrap() {
        final Model model = createDefaultModel();

        assertDoesNotThrow(() -> GraphDefinition.wrap(model));
    }

    @Test
    void graphCanGetFirstInstanceOf() {
        final Model model = createDefaultModel();
        final GraphDefinition y = GraphDefinition.wrap(model);

        assertDoesNotThrow(y::getResource1);
    }

    @Test
    void graphCanGetFirstSubjectOf() {
        final Model model = createDefaultModel();
        final GraphDefinition y = GraphDefinition.wrap(model);

        assertDoesNotThrow(y::getResource2);
    }

    @Test
    void graphCanGetFirstObjectOf() {
        final Model model = createDefaultModel();
        final GraphDefinition y = GraphDefinition.wrap(model);

        assertDoesNotThrow(y::getResource3);
    }

    @Test
    void resourceCanGetProperty() {
        final Model model = modelFrom("" +
                "PREFIX : <urn:example:> \n" +
                "                        \n" +
                "[                       \n" +
                "    a :C ;              \n" +
                "] .                     \n");
        final GraphDefinition y = GraphDefinition.wrap(model);
        final ResourceDefinition1 z = y.getResource1();

        assertDoesNotThrow(z::getProperty1);
    }

    @Test
    void e2e() {
        final Dataset dataset = datasetFrom("" +
                "PREFIX : <urn:example:>  \n" +
                "                         \n" +
                "GRAPH <urn:example:g1> { \n" +
                "    [                    \n" +
                "        a :C ;           \n" +
                "        :p :VALUE ;      \n" +
                "    ] .                  \n" +
                "                         \n" +
                "    [ :p [] ] .          \n" +
                "}                        \n");

        final DatasetDefinition x = DatasetDefinition.wrap(dataset);
        final GraphDefinition y = x.getNamedGraph();
        final ResourceDefinition1 z = y.getResource1();
        final ResourceDefinition1 z2 = y.getResource2();
        final ResourceDefinition1 z3 = y.getResource3();
        final String p = z.getProperty1();
        final URI p2 = z.getProperty2();
        final ResourceDefinition2 p3 = z.getProperty3();
        final ResourceDefinition3 p4 = p3.getProperty();

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
        final Model model = createDefaultModel();
        RDFDataMgr.read(model, IOUtils.toInputStream(rdf, Charsets.UTF_8), Lang.TRIG);
        return model;
    }

    @com.inrupt.rdf.wrapping.annotation.Dataset
    interface DatasetDefinition {
        static DatasetDefinition wrap(final Dataset original) {
            return Manager.wrap(original, DatasetDefinition.class);
        }

        @DefaultGraph
        GraphDefinition getDefaultGraph();

        @NamedGraph("urn:example:g1")
        GraphDefinition getNamedGraph();

        @Graph
        interface GraphDefinition {
            static GraphDefinition wrap(final Model original) {
                return Manager.wrap(original, GraphDefinition.class);
            }

            @OptionalFirstInstanceOfEither({"urn:example:C", "urn:example:Other"})
            ResourceDefinition1 getResource1();

            @OptionalFirstSubjectOfEither({"urn:example:p", "urn:example:Other"})
            ResourceDefinition1 getResource2();

            @OptionalFirstObjectOfEither({"urn:example:p", "urn:example:Other"})
            ResourceDefinition1 getResource3();

            @Resource
            interface ResourceDefinition1 {
                @Property(predicate = "urn:example:p", cardinality = ANY_OR_NULL, mapping = IRI_AS_STRING)
                String getProperty1();

                @Property(predicate = "urn:example:p", cardinality = ANY_OR_NULL, mapping = IRI_AS_URI)
                URI getProperty2();

                @Property(predicate = "urn:example:p", cardinality = ANY_OR_NULL, mapping = AS)
                ResourceDefinition2 getProperty3();
            }

            @Resource
            interface ResourceDefinition2 {
                @Property(predicate = "urn:example:p", cardinality = ANY_OR_NULL, mapping = AS)
                ResourceDefinition3 getProperty();
            }

            @Resource
            interface ResourceDefinition3 {
            }
        }
    }
}
