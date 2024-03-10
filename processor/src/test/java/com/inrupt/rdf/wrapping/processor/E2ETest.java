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
import static com.inrupt.rdf.wrapping.annotation.GraphProperty.Method.INSTANCE_OF;
import static com.inrupt.rdf.wrapping.annotation.GraphProperty.Method.OBJECT_OF;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality.*;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.NodeMapping.AS_TYPED_LITERAL;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.ValueMapping.*;
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
            "         :many 5, 6;    \n" +
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
        assertThat(myDataset.anonymous().instance().many1(), contains(2, 3));
        assertThat(myDataset.anonymous().instance().many2(), contains(2, 3));
        assertThat(myDataset.anonymous().instance().many3(), contains(2, 3));
        assertThat(myDataset.anonymous().instance().many4(), contains(2, 3));
        assertThat(myDataset.anonymous().instance().many5(), contains(2, 3));
        assertThat(myDataset.anonymous().instance().many6(), contains(2, 3));
        assertThat(myDataset.anonymous().instance().many7(), contains(2, 3));
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

        myDataset.anonymous().instance().overwrite(999);
        assertThat(myDataset.anonymous().instance().label(), is(999));
    }
}

@Dataset
interface MyDataset {
    static MyDataset wrap(final org.apache.jena.query.Dataset original) {
        return Manager.wrap(original, MyDataset.class);
    }

    @DatasetProperty
    MyGraph anonymous();

    @DatasetProperty(G)
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
    @GraphProperty(CHILD)
    MyResource subject();

    @GraphProperty(value = CHILD, method = OBJECT_OF)
    MyResource object();

    @GraphProperty(value = C, method = INSTANCE_OF)
    MyResource instance();

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
    @ResourceProperty(value = LABEL, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
    Integer label();

    @ResourceProperty(value = IRI, valueMapping = IRI_AS_URI)
    URI uri();

    @ResourceProperty(CHILD)
    MyResource child();

    @ResourceProperty(value = THROWING, cardinality = ANY_OR_THROW, valueMapping = LITERAL_AS_STRING)
    String throwing();

    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
    Set<Integer> many1();

    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
    Set<?> many2();

    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
    Set<? extends Integer> many3();

    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
    Set<? extends Number> many4();

    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
    Set<? super Integer> many5();

    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
    Set<? super Number> many6();

    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
    Set<Number> many7();

    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
    Set many8();

//    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY)
//    Set<?> many7();
//
//    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY)
//    Set<? extends MyResource> many8();
//
//    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY)
//    Set many9();


//    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
//    Collection<Integer> many21();
//
//    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
//    Collection<?> many22();
//
//    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
//    Collection<? extends Integer> many23();
//
//    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
//    Collection<? super Integer> many24();
//
//    @ResourceProperty(value = MANY, cardinality = OBJECTS_READ_ONLY, valueMapping = LITERAL_AS_INTEGER_OR_NULL)
//    Collection many25();

    @ResourceProperty(value = LABEL, cardinality = OVERWRITE, nodeMapping = AS_TYPED_LITERAL)
    void overwrite(Integer value);

    // Resource definitions support static methods.
    static UUID constant() {
        return STATIC;
    }

    // Resource definitions support default methods.
    default Integer childLabel() {
        return child().label();
    }
}
