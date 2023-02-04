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
package com.inrupt.rdf.wrapping.commons;

import static com.inrupt.rdf.wrapping.commons.ExceptionUtils.rootCause;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.mockito.exceptions.base.MockitoException;

class WrapperGraphTest {

    private Graph original;
    private WrapperGraph wrapped;
    private Triple triple;
    private BlankNodeOrIRI graphName;
    private BlankNodeOrIRI subject;
    private IRI predicate;
    private RDFTerm object;
    private Optional<BlankNodeOrIRI> optionalGraphName;

    @BeforeEach
    void setUp() {
        graphName = mock(BlankNodeOrIRI.class);
        optionalGraphName = Optional.of(graphName);
        subject = mock(BlankNodeOrIRI.class);
        predicate = mock(IRI.class);
        object = mock(RDFTerm.class);
        triple = mock(Triple.class);
        original = mock(Graph.class);
        wrapped = mock(WrapperGraph.class, withSettings().useConstructor(original).defaultAnswer(CALLS_REAL_METHODS));
    }

    @DisplayName("constructor requires original")
    @Test
    void constructorRequiresOriginal() {
        final MockSettings settings = withSettings().useConstructor((Dataset) null);

        final Throwable t = assertThrows(MockitoException.class, () -> mock(WrapperDataset.class, settings));
        assertThat(rootCause(t), is(instanceOf(NullPointerException.class)));
    }

    @DisplayName("add delegates to underlying dataset")
    @Test
    void addDelegatesToOriginal() {
        wrapped.add(triple);

        verify(original).add(triple);
    }

    @DisplayName("add verbose delegates to underlying dataset")
    @Test
    void addVerboseDelegatesToOriginal() {
        wrapped.add(subject, predicate, object);

        verify(original).add(subject, predicate, object);
    }

    @DisplayName("contains delegates to underlying dataset")
    @Test
    void containsDelegatesToOriginal() {
        final boolean result = true;

        when(original.contains(triple)).thenReturn(result);

        assertThat(wrapped.contains(triple), is(equalTo(result)));
        verify(original).contains(triple);
    }

    @DisplayName("contains verbose delegates to underlying dataset")
    @Test
    void containsVerboseDelegatesToOriginal() {
        final boolean result = true;

        when(original.contains(subject, predicate, object)).thenReturn(result);

        assertThat(wrapped.contains(subject, predicate, object), is(equalTo(result)));
        verify(original).contains(subject, predicate, object);
    }

    @DisplayName("add delegates to underlying dataset")
    @Test
    void removeDelegatesToOriginal() {
        wrapped.remove(triple);

        verify(original).remove(triple);
    }

    @DisplayName("add verbose delegates to underlying dataset")
    @Test
    void removeVerboseDelegatesToOriginal() {
        wrapped.remove(subject, predicate, object);

        verify(original).remove(subject, predicate, object);
    }

    @DisplayName("clear delegates to underlying dataset")
    @Test
    void clearDelegatesToOriginal() {
        wrapped.clear();

        verify(original).clear();
    }

    @DisplayName("size delegates to underlying dataset")
    @Test
    void sizeDelegatesToOriginal() {
        final long result = Long.MAX_VALUE;

        when(original.size()).thenReturn(result);

        assertThat(wrapped.size(), is(equalTo(result)));
        verify(original).size();
    }

    @DisplayName("stream delegates to underlying dataset")
    @Test
    void streamDelegatesToOriginal() {
        final Stream<Triple> result = Stream.of(triple);

        doReturn(result).when(original).stream();

        assertThat(wrapped.stream(), is(equalTo(result)));
        verify(original).stream();
    }

    @DisplayName("stream verbos delegates to underlying dataset")
    @Test
    void streamVerboseDelegatesToOriginal() {
        final Stream<Triple> result = Stream.of(triple);

        doReturn(result).when(original).stream(subject, predicate, object);

        assertThat(wrapped.stream(subject, predicate, object), is(equalTo(result)));
        verify(original).stream(subject, predicate, object);
    }
}
