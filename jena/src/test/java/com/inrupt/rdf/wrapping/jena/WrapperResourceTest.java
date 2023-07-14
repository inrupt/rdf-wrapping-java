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
package com.inrupt.rdf.wrapping.jena;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.inrupt.rdf.wrapping.commons.TermMapping;
import com.inrupt.rdf.wrapping.commons.WrapperBlankNodeOrIRI;
import com.inrupt.rdf.wrapping.test.base.HasSameMethods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.IRI;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.shared.PropertyNotFoundException;
import org.apache.jena.sparql.graph.GraphFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WrapperResourceTest extends HasSameMethods {
    @BeforeAll
    static void init() {
        HasSameMethods.initializeClassesForComparison(
                WrapperBlankNodeOrIRI.class,
                WrapperResource.class);
    }

    @Override
    protected Stream<Class<?>> translate(final Class<?> clazz) {
        if (clazz == Property.class) {
            return Stream.of(IRI.class);
        }

        if (clazz == IRI.class) {
            return Stream.of(Property.class);
        }

        if (clazz == ValueMapping.class) {
            return Stream.of(com.inrupt.rdf.wrapping.commons.ValueMapping.class);
        }

        if (clazz == com.inrupt.rdf.wrapping.commons.ValueMapping.class) {
            return Stream.of(ValueMapping.class);
        }

        if (clazz == NodeMapping.class) {
            return Stream.of(TermMapping.class);
        }

        if (clazz == TermMapping.class) {
            return Stream.of(NodeMapping.class);
        }

        return Stream.of(clazz);
    }

    private static final Property P = ResourceFactory.createProperty("http://example.com/p");
    private static final String O1 = randomUUID().toString();
    private static final String O2 = randomUUID().toString();

    private MockResource s;

    @BeforeEach
    void setUp() {
        s = new MockModel().getResource();
    }

    @DisplayName("anyOrNull is null if no statement matches subject & predicate")
    @Test
    void anyOrNullIsNullIfZero() {
        assertThat(s.anyOrNull(), is(nullValue()));
    }

    @DisplayName("anyOrNull is converted object of random statement matching subject & predicate")
    @Test
    void anyOrNullIsAnyIfMany() {
        s.addProperty(P, O1);
        s.addProperty(P, O2);

        assertThat(s.anyOrNull(), is(oneOf(O1, O2)));
    }

    @DisplayName("anyOrThrow throws if no statement matches subject & predicate")
    @Test
    void anyOrThrowThrowsIfZero() {
        assertThrows(PropertyNotFoundException.class, s::anyOrThrow);
    }

    @DisplayName("anyOrThrow is converted object of random statement matching subject & predicate")
    @Test
    void anyOrThrowIsAnyIfMany() {
        s.addProperty(P, O1);
        s.addProperty(P, O2);

        assertThat(s.anyOrThrow(), is(oneOf(O1, O2)));
    }

    @DisplayName("singleOrNull is null if no statement matches subject & predicate")
    @Test
    void singleOrNullIsNullIfZero() {
        assertThat(s.singleOrNull(), is(nullValue()));
    }

    @DisplayName("singleOrNull is converted object of only statement matching subject & predicate")
    @Test
    void singleOrNullIsOneIfOne() {
        s.addProperty(P, O1);

        assertThat(s.singleOrNull(), is(O1));
    }

    @DisplayName("anyOrThrow throws if multiple statements match subject & predicate")
    @Test
    void singleOrNullThrowsIfMany() {
        s.addProperty(P, O1);
        s.addProperty(P, O2);

        assertThrows(IllegalStateException.class, s::singleOrNull);
    }

    @DisplayName("singleOrThrow throws if no statements match subject & predicate")
    @Test
    void singleOrThrowThrowsIfZero() {
        assertThrows(PropertyNotFoundException.class, s::singleOrThrow);
    }

    @DisplayName("singleOrThrow is converted object of only statement matching subject & predicate")
    @Test
    void singleOrThrowIsOneIfOne() {
        s.addProperty(P, O1);

        assertThat(s.singleOrThrow(), is(O1));
    }

    @DisplayName("singleOrThrow throws if multiple statements match subject & predicate")
    @Test
    void singleOrThrowThrowsIfMany() {
        s.addProperty(P, O1);
        s.addProperty(P, O2);

        assertThrows(IllegalStateException.class, s::singleOrThrow);
    }

    @DisplayName("iterator is empty if no statements match subject & predicate")
    @Test
    void iteratorEmptyIfZero() {
        assertThat(s.objectIterator().hasNext(), is(false));
    }

    @DisplayName("iterator contains converted objects of statements matching subject & predicate")
    @Test
    void iteratorContainsIfNonZero() {
        s.addProperty(P, O1);
        s.addProperty(P, O2);

        assertThat(() -> s.objectIterator(), containsInAnyOrder(O1, O2));
    }

    @DisplayName("iterator does not implement remove")
    @Test
    void iteratorRemoveThrows() {
        s.addProperty(P, O1);

        assertThrows(IllegalStateException.class, s.objectIterator()::remove);
    }

    @DisplayName("snapshot is empty if no statements match subject & predicate")
    @Test
    void snapshotEmptyIfZero() {
        assertThat(s.objectsReadOnly(), is(empty()));
    }

    @DisplayName("snapshot contains converted objects of statements matching subject & predicate")
    @Test
    void snapshotContainsIfNonZero() {
        s.addProperty(P, O1);
        s.addProperty(P, O2);

        assertThat(s.objectsReadOnly(), containsInAnyOrder(O1, O2));
    }

    @DisplayName("snapshot does not reflect subsequent changes to underlying graph")
    @Test
    void snapshotIsStatic() {
        s.addProperty(P, O1);

        final Set<String> snapshot = s.objectsReadOnly();

        s.addProperty(P, O2);

        assertThat(snapshot, not(containsInAnyOrder(O1, O2)));
    }

    @DisplayName("live is empty if no statements match subject & predicate")
    @Test
    void liveEmptyIfZero() {
        assertThat(s.objects(), is(empty()));
    }

    @DisplayName("live contains converted objects of statements matching subject & predicate")
    @Test
    void liveContainsIfNonZero() {
        s.addProperty(P, O1);
        s.addProperty(P, O2);

        assertThat(s.objects(), containsInAnyOrder(O1, O2));
    }

    @DisplayName("live reflects subsequent changes to underlying graph")
    @Test
    void liveIsDynamic() {
        s.addProperty(P, O1);

        final Set<String> live = s.objects();

        s.addProperty(P, O2);

        assertThat(live, containsInAnyOrder(O1, O2));
    }

    @DisplayName("overwrite (1) throws if value is null")
    @Test
    void overwriteOneThrowsIfNull() {
        assertThrows(NullPointerException.class, () -> s.overwrite((String) null));
    }

    @DisplayName("overwrite (1) removes statements matching subject & predicate and adds new with converted object")
    @Test
    void overwriteOneRemovesAndAdds() {
        s.addProperty(P, O1);

        s.overwrite(O2);

        assertThat(
                (Iterable<Statement>) () -> s.listProperties(P),
                contains(hasProperty("literal", hasProperty("lexicalForm", is(O2)))));
    }

    @DisplayName("overwrite (*) throws if value is null")
    @Test
    void overwriteManyThrowsIfNull() {
        assertThrows(NullPointerException.class, () -> s.overwrite((Iterable<String>) null));
    }

    @DisplayName("overwrite (*) throws if value has null elements")
    @Test
    void overwriteManyThrowsIfNullElement() {
        final ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(null);

        assertThrows(NullPointerException.class, () -> s.overwrite(arrayList));
    }

    @DisplayName("overwrite (*) removes statements matching subject & predicate and adds new with converted objects")
    @Test
    void overwriteManyRemovesAndAdds() {
        s.addProperty(P, O1);

        final List<String> value = new ArrayList<>();
        value.add(O2);

        s.overwrite(value);

        assertThat(
                (Iterable<Statement>) () -> s.listProperties(P),
                contains(hasProperty("literal", hasProperty("lexicalForm", is(O2)))));
    }

    @DisplayName("overwriteNullable (1) removes when value is null")
    @Test
    void overwriteOneNullableRemovesIfNull() {
        s.addProperty(P, O1);

        s.overwriteNullable((String) null);

        assertThat((Iterable<Statement>) () -> s.listProperties(P), emptyIterable());
    }

    @DisplayName("overwrite (1) removes statements matching subject & predicate and adds new with converted object")
    @Test
    void overwriteOneNullableRemovesAndAdds() {
        s.addProperty(P, O1);

        s.overwriteNullable(O2);

        assertThat(
                (Iterable<Statement>) () -> s.listProperties(P),
                contains(hasProperty("literal", hasProperty("lexicalForm", is(O2)))));
    }

    @DisplayName("overwriteNullable (*) removes when value is null")
    @Test
    void overwriteManyNullableRemovesIfNull() {
        s.addProperty(P, O1);

        s.overwriteNullable((Iterable<String>) null);

        assertThat((Iterable<Statement>) () -> s.listProperties(P), emptyIterable());
    }

    @DisplayName("overwriteNullable (*) throws if value has null elements")
    @Test
    void overwriteManyNullableThrowsIfNullElement() {
        final ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(null);

        assertThrows(NullPointerException.class, () -> s.overwriteNullable(arrayList));
    }

    @DisplayName("overwrite (*) removes statements matching subject & predicate and adds new with converted objects")
    @Test
    void overwriteManyNullableRemovesAndAdds() {
        s.addProperty(P, O1);

        final List<String> value = new ArrayList<>();
        value.add(O2);
        s.overwriteNullable(value);

        assertThat(
                (Iterable<Statement>) () -> s.listProperties(P),
                contains(hasProperty("literal", hasProperty("lexicalForm", is(O2)))));
    }

    @DisplayName("add (1) throws if value is null")
    @Test
    void addOneThrowsIfNull() {
        assertThrows(NullPointerException.class, () -> s.add((String) null));
    }

    @DisplayName("add (1) adds statement with converted object, subject & predicate")
    @Test
    void addOneAdds() {
        s.addProperty(P, O1);

        s.add(O2);

        assertThat((Iterable<Statement>) () -> s.listProperties(P), containsInAnyOrder(
                hasProperty("literal", hasProperty("lexicalForm", is(O1))),
                hasProperty("literal", hasProperty("lexicalForm", is(O2)))));
    }

    @DisplayName("add (*) throws if value is null")
    @Test
    void addManyThrowsIfNull() {
        assertThrows(NullPointerException.class, () -> s.add((Iterable<String>) null));
    }

    @DisplayName("add (*) throws if value has null elements")
    @Test
    void addManyThrowsIfNullElement() {
        final ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(null);

        assertThrows(NullPointerException.class, () -> s.add(arrayList));
    }

    @DisplayName("add (1) adds statements with converted objects, subject & predicate")
    @Test
    void addManyAdds() {
        s.addProperty(P, O1);

        final List<String> value = new ArrayList<>();
        value.add(O2);
        s.add(value);

        assertThat((Iterable<Statement>) () -> s.listProperties(P), containsInAnyOrder(
                hasProperty("literal", hasProperty("lexicalForm", is(O1))),
                hasProperty("literal", hasProperty("lexicalForm", is(O2)))));
    }

    static class MockModel extends ModelCom {
        private final MockResource resource;

        MockModel() {
            super(GraphFactory.createDefaultGraph());

            getPersonality().add(MockResource.class, MockResource.factory);

            resource = createResource().as(MockResource.class);
        }

        MockResource getResource() {
            return resource;
        }
    }

    public static class MockResource extends WrapperResource {
        private static final ValueMapping<String> VM = ValueMappings::literalAsString;
        private static final NodeMapping<String> NM = NodeMappings::asStringLiteral;

        static final Implementation factory = new UriOrBlankFactory(MockResource::new);

        protected MockResource(final Node n, final EnhGraph m) {
            super(n, m);
        }

        public String anyOrNull() {
            return super.anyOrNull(P, VM);
        }

        public String anyOrThrow() {
            return super.anyOrThrow(P, VM);
        }

        public String singleOrNull() {
            return super.singleOrNull(P, VM);
        }

        public String singleOrThrow() {
            return super.singleOrThrow(P, VM);
        }

        public Iterator<String> objectIterator() {
            return super.objectIterator(P, VM);
        }

        public Set<String> objectsReadOnly() {
            return super.objectsReadOnly(P, VM);
        }

        public Set<String> objects() {
            return super.objects(P, NM, VM);
        }

        public void overwrite(final String value) {
            overwrite(P, value, NM);
        }

        public void overwrite(final Iterable<String> value) {
            overwrite(P, value, NM);
        }

        public void overwriteNullable(final String value) {
            overwriteNullable(P, value, NM);
        }

        public void overwriteNullable(final Iterable<String> value) {
            overwriteNullable(P, value, NM);
        }

        public void add(final String value) {
            add(P, value, NM);
        }

        public void add(final Iterable<String> value) {
            add(P, value, NM);
        }
    }

}
