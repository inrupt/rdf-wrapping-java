/*
 * Copyright 2023 Inrupt Inc.
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
package com.inrupt.commons.wrapping.test.base;

import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.*;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * This class contains test logic only, while the task of creating instances of that class (and the circumstances
 * required for it) are delegated to derived classes.
 */
public abstract class PredicateObjectSetBase {
    // region constants

    private static final String URN_UUID = "urn:uuid:";
    private static final String S = URN_UUID + randomUUID();
    private static final String SX = URN_UUID + randomUUID();
    private static final String P = URN_UUID + randomUUID();
    private static final String PX = URN_UUID + randomUUID();
    private static final String O = randomUUID().toString();
    private static final String OX = randomUUID().toString();

    // endregion

    /**
     * This field holds the PredicateObjectSet under test. A fresh instance over a fresh context graph is created for
     * every test.
     */
    private Set<String> set;

    // region implementation

    /**
     * Implementation specific derived methods must assert a triple with the given parameters in the per-test context
     * graph contains.
     *
     * @param subject the IRI string for the subject IRI node to be added
     * @param predicate the IRI string for the predicate IRI node to be added
     * @param object the lexical form for the object literal node to be added
     */
    protected abstract void addTriple(String subject, String predicate, String object);

    /**
     * Implementation specific derived methods must check whether the per-test context graph contains a triple with the
     * given parameters.
     *
     * @param subject the IRI string for the subject IRI node to be checked
     * @param predicate the IRI string for the predicate IRI node to be checked
     * @param object the lexical form for the object literal node to be checked
     */
    protected abstract boolean containsTriple(String subject, String predicate, String object);

    /**
     * Implementation specific derived methods must create a new PredicateObjectSet over a fresh, per-test context
     * graph.
     *
     * @param subject the IRI string for the subject IRI node to be checked
     * @param predicate the IRI string for the predicate IRI node to be checked
     */
    protected abstract Set<String> createNewSetForTest(String subject, String predicate);

    /**
     * Implementation specific derived methods must create a new PredicateObjectSet over the existing, per-test context
     * graph.
     *
     * @param subject the IRI string for the subject IRI node to be checked
     * @param predicate the IRI string for the predicate IRI node to be checked
     */
    protected abstract Set<String> createOtherSetOverSameGraph(String subject, String predicate);

    // endregion

    @BeforeEach
    void setUp() {
        set = createNewSetForTest(S, P);
    }

    // region constructor

    @DisplayName("constructor throws")
    @ParameterizedTest(name = "NullPointerException when {0} is null")
    @MethodSource
    void constructorRequiresArguments(
            final String ignoredName,
            final String subject,
            final String predicate) {

        assertThrows(NullPointerException.class, () ->
                createNewSetForTest(subject, predicate));
    }

    static Stream<Arguments> constructorRequiresArguments() {
        return Stream.of(
                Arguments.of("subject", null, null),
                Arguments.of("predicate", SX, null)
        );
    }

    // endregion
    // region size

    /**
     * This untestable invariant of {@link Set} happens to be guaranteed by implementations.
     */
    @DisplayName("Set invariant: size capped at greatest integer")
    @Test
    void sizeCappedAtIntegerMaxValue() {
        // Not feasible to exhaust possibility space as of writing.
        // Not feasible to mock either, because iterator is enumerated.
        for (long i = 0L; i < 1 /* 0x7fffffffL + 2 */; i++) {
            // Subject and predicate both match
            addTriple(S, P, String.valueOf(i));
        }

        assertThat(set, hasSize(lessThanOrEqualTo(Integer.MAX_VALUE)));
    }

    @DisplayName("size ignores other subjects")
    @Test
    void sizeIgnoresOtherSubjects() {
        addTriple(SX, P, O);

        assertThat(set, hasSize(0));
    }

    @DisplayName("size ignores other predicates")
    @Test
    void sizeIgnoresOtherPredicates() {
        addTriple(S, PX, O);

        assertThat(set, hasSize(0));
    }

    @DisplayName("size counts statements by subject and predicate")
    @Test
    void sizeCountsBySubjectAndPredicate() {
        addTriple(S, P, O);

        assertThat(set, hasSize(1));
    }

    // endregion
    // region isEmpty

    @DisplayName("isEmpty ignores other subjects")
    @Test
    void isEmptyIgnoresOtherSubjects() {
        addTriple(SX, P, O);

        assertThat(set, is(empty()));
    }

    @DisplayName("isEmpty ignores other predicates")
    @Test
    void isEmptyIgnoresOtherPredicates() {
        addTriple(S, PX, O);

        assertThat(set, is(empty()));
    }

    @DisplayName("isEmpty is false if statements exist by subject and predicate")
    @Test
    void isEmptyFalseWithObject() {
        addTriple(S, P, O);

        assertThat(set, is(not(empty())));
    }

    @DisplayName("isEmpty is true if no statements exist by subject and predicate")
    @Test
    void isEmptyTrueWithoutObject() {
        assertThat(set, is(empty()));
    }

    // endregion
    // region contains

    @DisplayName("Set invariant: contains throws if object is null")
    @Test
    void containsRequiresObject() {
        assertThrows(NullPointerException.class, () ->
                set.contains(null));
    }

    @DisplayName("Set invariant: contains throws if object cannot be cast")
    @Test
    @SuppressWarnings("java:S2175")
        // Intentional so it throws
    void containsRequiresCastableObject() {
        assertThrows(ClassCastException.class, () ->
                set.contains(0));
    }

    @DisplayName("contains ignores other subject")
    @Test
    void containsIgnoresOtherSubject() {
        addTriple(SX, P, O);

        assertThat(set.contains(O), is(false));
    }

    @DisplayName("contains ignores other predicate")
    @Test
    void containsIgnoresOtherPredicate() {
        addTriple(S, PX, O);

        assertThat(set.contains(O), is(false));
    }

    @DisplayName("contains is false if no statement exists by subject, predicate and converted object")
    @Test
    void containsFalseWithOtherObject() {
        addTriple(S, P, OX);

        assertThat(set.contains(O), is(false));
    }

    @DisplayName("contains is true if statement exists by subject, predicate and converted object")
    @Test
    void containsConsidersObjectsOfPredicateForSubject() {
        addTriple(S, P, O);

        assertThat(set.contains(O), is(true));
    }

    // endregion
    // region iterator

    @DisplayName("iterator remove is not supported")
    @Test
    void iteratorRemoveIsUnsupported() {
        final Iterator<String> iterator = set.iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @DisplayName("iterator ignores other subjects")
    @Test
    void iteratorIgnoresOtherSubjects() {
        addTriple(SX, P, O);

        assertThat(set, is(emptyIterableOf(String.class)));
    }

    @DisplayName("iterator ignores other predicates")
    @Test
    void iteratorIgnoresOtherPredicates() {
        addTriple(S, PX, O);

        assertThat(set, is(emptyIterableOf(String.class)));
    }

    @DisplayName("iterator enumerates converted objects by subject and predicate")
    @Test
    void iteratorConsidersConvertedObjectsOfPredicateForObject() {
        addTriple(S, P, O);

        assertThat(set, containsInAnyOrder(O));
    }

    // endregion
    // region toArray

    @DisplayName("toArray ignores other subjects")
    @Test
    void toArrayIgnoresOtherSubjects() {
        addTriple(SX, P, O);

        assertThat(set.toArray(), is(emptyArray()));
    }

    @DisplayName("toArray ignores other predicates")
    @Test
    void toArrayIgnoresOtherPredicates() {
        addTriple(S, PX, O);

        assertThat(set.toArray(), is(emptyArray()));
    }

    @DisplayName("toArray enumerates converted objects by subject and predicate")
    @Test
    void toArrayConsidersConvertedObjectsOfPredicateForObject() {
        addTriple(S, P, O);

        assertThat(set.toArray(), is(arrayContainingInAnyOrder(O)));
    }

    // endregion
    // region toArrayOther

    @DisplayName("Set invariant: toArray (other) throws if array is null")
    @Test
    void toArrayOtherRequiresArray() {
        assertThrows(NullPointerException.class, () ->
                set.toArray((String[]) null));
    }

    @DisplayName("Set invariant: toArray (other) throws if array is other type")
    @Test
    void toArrayOtherRequiresMatchingArrayType() {
        addTriple(S, P, O);

        assertThrows(ArrayStoreException.class, () ->
                set.toArray(new UUID[0]));
    }

    @DisplayName("Set invariant: toArray (other) reuses array if it fits")
    @Test
    void toArrayOtherReusesArray() {
        final String[] spacious = new String[1];

        addTriple(S, P, O);

        assertThat(set.toArray(spacious), is(theInstance(spacious)));
    }

    @DisplayName("Set invariant: toArray (other) allocates new array if it does not fit")
    @Test
    void toArrayOtherAllocatesNewArray() {
        final String[] crowded = new String[0];

        addTriple(S, P, O);

        assertThat(set.toArray(crowded), is(not(theInstance(crowded))));
    }

    @DisplayName("toArray (other) ignores other subjects")
    @Test
    void toArrayOtherIgnoresOtherSubjects() {
        addTriple(SX, P, O);

        assertThat(set.toArray(new String[0]), is(emptyArray()));
    }

    @DisplayName("toArray (other) ignores other predicates")
    @Test
    void toArrayOtherIgnoresOtherPredicates() {
        addTriple(S, PX, O);

        assertThat(set.toArray(new String[0]), is(emptyArray()));
    }

    @DisplayName("toArray (other) populates converted objects by subject and predicate")
    @Test
    void toArrayOtherConsidersConvertedObjectsOfPredicateForObject() {
        addTriple(S, P, O);

        assertThat(set.toArray(new String[0]), is(arrayContainingInAnyOrder(O)));
    }

    // endregion
    // region add

    @DisplayName("Set invariant: add throws if element is null")
    @Test
    void addRequiresElement() {
        assertThrows(NullPointerException.class, () ->
                set.add(null));
    }

    @DisplayName("Set invariant: add is false if set was not modified")
    @Test
    void addFalseWhenPresent() {
        addTriple(S, P, O);

        assertThat(set.add(O), is(false));
    }

    @DisplayName("Set invariant: add is true if set was modified")
    @Test
    void addTrueWhenNew() {
        assertThat(set.add(O), is(true));
    }

    @DisplayName("add asserts statement by subject, predicate and converted element object")
    @Test
    void addAssertsConverted() {
        set.add(O);

        assertThat(containsTriple(S, P, O), is(true));
    }

    // endregion
    // region remove

    @DisplayName("Set invariant: remove throws if element is null")
    @Test
    void removeRequiresElement() {
        assertThrows(NullPointerException.class, () ->
                set.remove(null));
    }

    @DisplayName("Set invariant: remove is false if set was not modified")
    @Test
    void removeFalseWhenPresent() {
        assertThat(set.remove(O), is(false));
    }

    @DisplayName("Set invariant: remove is true if set was modified")
    @Test
    void removeTrueWhenNew() {
        addTriple(S, P, O);

        assertThat(set.remove(O), is(true));
    }

    @DisplayName("remove retracts statement by subject, predicate and converted object")
    @Test
    void removeRetractsConverted() {
        addTriple(S, P, O);

        set.remove(O);

        assertThat(containsTriple(S, P, O), is(false));
    }


    // endregion
    // region containsAll

    @DisplayName("Set invariant: contains all throws if collection is null")
    @Test
    void containsAllRequiresCollection() {
        assertThrows(NullPointerException.class, () ->
                set.remove(null));
    }

    @DisplayName("Set invariant: contains all throws if collection has null element")
    @Test
    void containsAllRequiresNonNullElements() {
        final Collection<?> c = new ArrayList<>();
        c.add(null);

        assertThrows(NullPointerException.class, () ->
                set.containsAll(c));
    }

    @DisplayName("contains all ignores other subject")
    @Test
    void containsAllIgnoresOtherSubject() {
        addTriple(SX, P, O);

        assertThat(set.containsAll(singletonList(O)), is(false));
    }

    @DisplayName("contains all ignores other predicate")
    @Test
    void containsAllIgnoresOtherPredicate() {
        addTriple(S, PX, O);

        assertThat(set.containsAll(singletonList(O)), is(false));
    }

    @DisplayName("contains all is false if no statements exist by subject, predicate and any converted object")
    @Test
    void containsAllFalseForMissing() {
        addTriple(S, PX, OX);

        assertThat(set.containsAll(singletonList(O)), is(false));
    }

    @DisplayName("contains all is true if statements exist by subject, predicate and any converted object")
    @Test
    void containsAllTrueForExisting() {
        addTriple(S, P, O);

        assertThat(set.containsAll(singletonList(O)), is(true));
    }

    // endregion
    // region addAll

    @DisplayName("Set invariant: add all throws if collection is null")
    @Test
    void addAllRequiresCollection() {
        assertThrows(NullPointerException.class, () ->
                set.addAll(null));
    }

    @DisplayName("Set invariant: add all is false if set was not modified")
    @Test
    void addAllFalseWhenPresent() {
        addTriple(S, P, O);

        assertThat(set.addAll(singletonList(O)), is(false));
    }

    @DisplayName("Set invariant: add all is true if set was modified")
    @Test
    void addAllTrueWhenNew() {
        assertThat(set.addAll(singletonList(O)), is(true));
    }

    @DisplayName("add all asserts statements by subject, predicate and converted element objects")
    @Test
    void addAllAssertsConverted() {
        set.addAll(singletonList(O));

        assertThat(containsTriple(S, P, O), is(true));
    }


    // endregion
    // region retainAll

    @DisplayName("Set invariant: retain all throws if collection is null")
    @Test
    void retainAllRequiresCollection() {
        assertThrows(NullPointerException.class, () ->
                set.retainAll(null));
    }

    @DisplayName("Set invariant: retain all does not throw if collection element is not castable")
    @Test
    void retainAllAllowsNonCastableCollection() {
        addTriple(S, P, O);

        assertDoesNotThrow(() ->
                set.retainAll(singletonList(randomUUID())));
    }

    @DisplayName("Set invariant: retain all is false if set was not modified")
    @Test
    void retainAllLeavesElementsOfCollection1() {
        assertThat(set.retainAll(singletonList(O)), is(false));
    }

    @DisplayName("Set invariant: retain all is true if set was modified")
    @Test
    void retainAllRetractsWhenObjectMissing1() {
        addTriple(S, P, OX);

        assertThat(set.retainAll(singletonList(O)), is(true));
    }

    @DisplayName("retain all does not retract if converted object is in collection")
    @Test
    void retainAllLeavesElementsOfCollection() {
        addTriple(S, P, O);

        set.retainAll(singletonList(O));

        assertThat(containsTriple(S, P, O), is(true));
    }

    @DisplayName("retain all retracts by subject and predicate if converted object is not in collection")
    @Test
    void retainAllRetractsWhenObjectMissing() {
        addTriple(S, P, OX);

        set.retainAll(singletonList(O));

        assertThat(containsTriple(S, P, O), is(false));
    }

    // endregion
    // region removeAll

    @DisplayName("Set invariant: remove all throws if collection is null")
    @Test
    void removeAllRequiresCollection() {
        assertThrows(NullPointerException.class, () ->
                set.removeAll(null));
    }

    @DisplayName("Set invariant: remove all throws if collection element is not castable")
    @Test
    void removeAllRequiresCastableCollection() {
        final Collection<?> c = singletonList(randomUUID());

        addTriple(S, P, O);

        assertThrows(ClassCastException.class, () ->
                set.removeAll(c));
    }

    @DisplayName("Set invariant: remove all is false if set was not modified")
    @Test
    void removeAllFalseIfUnchanged() {
        assertThat(set.removeAll(singletonList(O)), is(false));
    }

    @DisplayName("Set invariant: remove all is true if set was modified")
    @Test
    void removeAllTruIfChanged() {
        addTriple(S, P, O);

        assertThat(set.removeAll(singletonList(O)), is(true));
    }

    @DisplayName("remove all ignores other subject")
    @Test
    void removeAllIgnoresOtherSubject() {
        addTriple(SX, P, O);

        assertThat(set.removeAll(singletonList(O)), is(false));
    }

    @DisplayName("remove all ignores other predicate")
    @Test
    void removeAllIgnoresOtherPredicate() {
        addTriple(S, PX, O);

        assertThat(set.removeAll(singletonList(O)), is(false));
    }

    @DisplayName("remove all ignores other object")
    @Test
    void removeAllIgnoresOtherObject() {
        addTriple(S, P, OX);

        assertThat(set.removeAll(singletonList(O)), is(false));
    }

    @DisplayName("remove all retracts by subject, predicate and any converted object")
    @Test
    void removeAllRetracts() {
        addTriple(S, P, O);

        set.removeAll(singletonList(O));

        assertThat(containsTriple(S, P, O), is(false));
    }

    // endregion
    // region clear

    @DisplayName("clear retracts all statements by subject and predicate")
    @Test
    void clearRetractsConverted() {
        addTriple(S, P, O);

        set.clear();

        assertThat(containsTriple(S, P, O), is(false));
    }

    // endregion
    // region equals

    @DisplayName("equals is false for different subjects")
    @Test
    void equalsFalseForDifferentSubjects() {
        final Set<String> other = createOtherSetOverSameGraph(SX, P);

        addTriple(S, P, O);

        assertThat(set, is(not(equalTo(other))));
    }

    @DisplayName("equals is false for different predicates")
    @Test
    void equalsFalseForDifferentPredicates() {
        final Set<String> other = createOtherSetOverSameGraph(S, PX);

        addTriple(S, P, O);

        assertThat(set, is(not(equalTo(other))));
    }

    @DisplayName("equals is false for different objects")
    @Test
    void equalsFalseForDifferentObjects() {
        final Set<String> other = createOtherSetOverSameGraph(SX, PX);

        addTriple(S, P, O);
        addTriple(SX, PX, OX);

        assertThat(set, is(not(equalTo(other))));
    }

    @DisplayName("equals is true for empty sets")
    @Test
    void equalsTrueForEmpty() {
        final Set<String> other = createOtherSetOverSameGraph(SX, PX);

        assertThat(set, is(equalTo(other)));
    }

    @DisplayName("equals is true for sets with equal predicate and object")
    @Test
    void equalsTrueForEqualPredicateAndObject() {
        final Set<String> other = createOtherSetOverSameGraph(S, P);

        addTriple(S, P, O);

        assertThat(set, is(equalTo(other)));
    }

    // endregion
    // region hashCode

    @DisplayName("hashCode differs for different subjects")
    @Test
    void hashCodeDiffersForDifferentSubjects() {
        final Set<String> other = createOtherSetOverSameGraph(SX, P);

        addTriple(S, P, O);

        assertThat(set.hashCode(), is(not(equalTo(other.hashCode()))));
    }

    @DisplayName("hashCode differs for different predicates")
    @Test
    void hashCodeDiffersForDifferentPredicates() {
        final Set<String> other = createOtherSetOverSameGraph(S, PX);

        addTriple(S, P, O);

        assertThat(set.hashCode(), is(not(equalTo(other.hashCode()))));
    }

    @DisplayName("hashCode differs for different objects")
    @Test
    void hashCodeDiffersForDifferentObjects() {
        final Set<String> other = createOtherSetOverSameGraph(SX, PX);

        addTriple(S, P, O);
        addTriple(SX, PX, OX);

        assertThat(set.hashCode(), is(not(equalTo(other.hashCode()))));
    }

    @DisplayName("hashCode matches for empty sets")
    @Test
    void hashCodeSameForEmpty() {
        final Set<String> other = createOtherSetOverSameGraph(SX, PX);

        assertThat(set.hashCode(), is(equalTo(other.hashCode())));
    }

    @DisplayName("hashCode matches for sets with equal predicate and object")
    @Test
    void hashCodeSameForEqualPredicateAndObject() {
        final Set<String> other = createOtherSetOverSameGraph(S, P);

        addTriple(S, P, O);

        assertThat(set.hashCode(), is(equalTo(other.hashCode())));
    }

    // endregion
}
