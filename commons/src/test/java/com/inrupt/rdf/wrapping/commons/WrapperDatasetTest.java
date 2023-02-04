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

class WrapperDatasetTest {

    private Dataset original;
    private WrapperDataset wrapped;
    private Quad quad;
    private BlankNodeOrIRI graphName;
    private BlankNodeOrIRI subject;
    private IRI predicate;
    private RDFTerm object;
    private Optional<BlankNodeOrIRI> optionalGraphName;

    @BeforeEach
    void setup() {
        graphName = mock(BlankNodeOrIRI.class);
        optionalGraphName = Optional.of(graphName);
        subject = mock(BlankNodeOrIRI.class);
        predicate = mock(IRI.class);
        object = mock(RDFTerm.class);
        quad = mock(Quad.class);
        original = mock(Dataset.class);
        wrapped = mock(WrapperDataset.class, withSettings().useConstructor(original).defaultAnswer(CALLS_REAL_METHODS));
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
        wrapped.add(quad);

        verify(original).add(quad);
    }

    @DisplayName("add verbose delegates to underlying dataset")
    @Test
    void addVerboseDelegatesToOriginal() {
        wrapped.add(graphName, subject, predicate, object);

        verify(original).add(graphName, subject, predicate, object);
    }

    @DisplayName("contains delegates to underlying dataset")
    @Test
    void containsDelegatesToOriginal() {
        final boolean result = true;

        when(original.contains(quad)).thenReturn(result);

        assertThat(wrapped.contains(quad), is(equalTo(result)));
        verify(original).contains(quad);
    }

    @DisplayName("contains verbose delegates to underlying dataset")
    @Test
    void containsVerboseDelegatesToOriginal() {
        final boolean result = true;

        when(original.contains(optionalGraphName, subject, predicate, object)).thenReturn(result);

        assertThat(wrapped.contains(optionalGraphName, subject, predicate, object), is(equalTo(result)));
        verify(original).contains(optionalGraphName, subject, predicate, object);
    }

    @DisplayName("get graph delegates to underlying dataset")
    @Test
    void getGraphDelegatesToOriginal() {
        final Graph result = mock(Graph.class);

        when(original.getGraph()).thenReturn(result);

        assertThat(wrapped.getGraph(), is(equalTo(result)));
        verify(original).getGraph();
    }

    @DisplayName("get graph verbose delegates to underlying dataset")
    @Test
    void getGraphVerboseDelegatesToOriginal() {
        final Optional<Graph> result = Optional.of(mock(Graph.class));

        when(original.getGraph(graphName)).thenReturn(result);

        assertThat(wrapped.getGraph(graphName), is(equalTo(result)));
        verify(original).getGraph(graphName);
    }

    @DisplayName("get graph names delegates to underlying dataset")
    @Test
    void getGraphNamesDelegatesToOriginal() {
        final Stream<BlankNodeOrIRI> result = Stream.of(graphName);

        when(original.getGraphNames()).thenReturn(result);

        assertThat(wrapped.getGraphNames(), is(equalTo(result)));
        verify(original).getGraphNames();
    }

    @DisplayName("add delegates to underlying dataset")
    @Test
    void removeDelegatesToOriginal() {
        wrapped.remove(quad);

        verify(original).remove(quad);
    }

    @DisplayName("add verbose delegates to underlying dataset")
    @Test
    void removeVerboseDelegatesToOriginal() {
        wrapped.remove(optionalGraphName, subject, predicate, object);

        verify(original).remove(optionalGraphName, subject, predicate, object);
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
        final Stream<Quad> result = Stream.of(quad);

        doReturn(result).when(original).stream();

        assertThat(wrapped.stream(), is(equalTo(result)));
        verify(original).stream();
    }

    @DisplayName("stream verbos delegates to underlying dataset")
    @Test
    void streamVerboseDelegatesToOriginal() {
        final Stream<Quad> result = Stream.of(quad);

        doReturn(result).when(original).stream(optionalGraphName, subject, predicate, object);

        assertThat(wrapped.stream(optionalGraphName, subject, predicate, object), is(equalTo(result)));
        verify(original).stream(optionalGraphName, subject, predicate, object);
    }
}
