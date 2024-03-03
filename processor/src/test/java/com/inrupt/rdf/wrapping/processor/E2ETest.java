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

import static com.github.jsonldjava.shaded.com.google.common.base.Charsets.UTF_8;
import static com.inrupt.rdf.wrapping.annotation.Property.Cardinality.*;
import static com.inrupt.rdf.wrapping.annotation.Property.Mapping.*;
import static com.inrupt.rdf.wrapping.processor.E2ETest.*;
import static java.util.UUID.randomUUID;
import static org.apache.commons.io.IOUtils.toInputStream;
import static org.apache.jena.riot.Lang.TRIG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import com.inrupt.rdf.wrapping.annotation.*;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import org.apache.jena.query.DatasetFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class E2ETest {
    private static final String NS = "urn:example:";
    private static final URI U = URI.create(NS);
    static final String G = NS + "g";
    static final String LABEL = NS + "label";
    static final String CHILD = NS + "child";
    static final String IRI = NS + "iri";
    static final String THROWING = NS + "throwing";
    static final String MANY = NS + "many";
    static final String C = NS + "C";
    static final UUID STATIC = randomUUID();

    private static final String RDF = "" +
            " BASE <urn:example:>    \n" +
            " PREFIX : <>            \n" +
            "                        \n" +
            " [                      \n" +
            "     a        :C ;      \n" +
            "     :label    1 ;      \n" +
            "     :iri     <> ;      \n" +
            "     :many  2, 3 ;      \n" +
            "     :child [           \n" +
            "        :label  4 ;     \n" +
            "        :iri   <> ;     \n" +
            "     ] ;                \n" +
            " ] .                    \n" +
            "                        \n" +
            " GRAPH :g {             \n" +
            "     [                  \n" +
            "         a      :C ;    \n" +
            "         :label  5 ;    \n" +
            "     ] .                \n" +
            " }                      \n";
    private static final org.apache.jena.query.Dataset DATASET = DatasetFactory.create();

    @BeforeEach
    void setUp() {
        RDFDataMgr.read(DATASET, toInputStream(RDF, UTF_8), TRIG);
    }

    @Test
    void test() {
        final MyDataset myDataset = MyDataset.wrap(DATASET);

        assertThat(myDataset.anonymous().instance().label(), is(1));
        assertThat(myDataset.anonymous().instance().uri(), is(U));
        assertThat(myDataset.anonymous().instance().many(), contains(2, 3));
        assertThat(myDataset.anonymous().instance().child().label(), is(4));
        assertThrows(Throwable.class, () -> myDataset.anonymous().instance().throwing());
        assertThat(myDataset.anonymous().instance().childLabel(), is(4));
        assertThat(myDataset.anonymous().subject().label(), is(1));
        assertThat(myDataset.anonymous().object().label(), is(4));
        assertThat(myDataset.anonymous().label(), is(1));
        assertThat(myDataset.named().instance().label(), is(5));
        assertThat(myDataset.label(), is(5));

        assertThat(MyDataset.constant(), is(STATIC));
        assertThat(MyGraph.constant(), is(STATIC));
        assertThat(MyResource.constant(), is(STATIC));
    }
}

@Dataset
interface MyDataset {
    static MyDataset wrap(final org.apache.jena.query.Dataset original) {
        return Manager.wrap(original, MyDataset.class);
    }

    @DefaultGraph
    MyGraph anonymous();

    @NamedGraph(G)
    MyGraph named();

    // Dataset definitions support static methods.
    static UUID constant() {
        return STATIC;
    }

    // Dataset definitions support default methods.
    default Integer label() {
        return named().instance().label();
    }
}

@Graph
interface MyGraph {
    @OptionalFirstInstanceOfEither(C)
    MyResource instance();

    @OptionalFirstSubjectOfEither(CHILD)
    MyResource subject();

    @OptionalFirstObjectOfEither(CHILD)
    MyResource object();

    // Graph definitions support static methods.
    static UUID constant() {
        return STATIC;
    }

    // Graph definitions support default methods.
    default Integer label() {
        return instance().label();
    }
}

@Resource
interface MyResource {
    @Property(predicate = LABEL, cardinality = ANY_OR_NULL, mapping = LITERAL_AS_INTEGER_OR_NULL)
    Integer label();

    @Property(predicate = IRI, cardinality = ANY_OR_NULL, mapping = IRI_AS_URI)
    URI uri();

    @Property(predicate = CHILD, cardinality = ANY_OR_NULL, mapping = AS)
    MyResource child();

    @Property(predicate = THROWING, cardinality = ANY_OR_THROW, mapping = LITERAL_AS_STRING)
    String throwing();

    @Property(predicate = MANY, cardinality = OBJECTS_READ_ONLY, mapping = LITERAL_AS_INTEGER_OR_NULL)
    Set<Integer> many();

    // Resource definitions support static methods.
    static UUID constant() {
        return STATIC;
    }

    // Resource definitions support default methods.
    default Integer childLabel() {
        return child().label();
    }
}
