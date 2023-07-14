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
package com.inrupt.rdf.wrapping.commons;

import static com.inrupt.rdf.wrapping.commons.ExceptionUtils.rootCause;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.mockito.exceptions.base.MockitoException;

class WrapperIRITest {

    private Graph graph;
    private IRI original;
    private WrapperIRI wrapped;

    @BeforeEach
    void setup() {
        graph = mock(Graph.class);
        original = mock(IRI.class);
        wrapped = mock(
                WrapperIRI.class,
                withSettings().useConstructor(original, graph).defaultAnswer(CALLS_REAL_METHODS));
    }

    @DisplayName("constructor requires original")
    @Test
    void constructorRequiresOriginal() {
        final MockSettings settings = withSettings().useConstructor(null, graph);

        final Throwable t = assertThrows(MockitoException.class, () -> mock(WrapperIRI.class, settings));
        assertThat(rootCause(t), is(instanceOf(NullPointerException.class)));
    }

    @DisplayName("constructor requires IRI")
    @Test
    void constructorRequiresIRI() {
        final Literal literal = mock(Literal.class);

        final MockSettings settings = withSettings().useConstructor(literal, graph);

        final Throwable t = assertThrows(MockitoException.class, () -> mock(WrapperIRI.class, settings));
        assertThat(rootCause(t), is(instanceOf(IllegalStateException.class)));
    }

    @DisplayName("IRI string delegates to underlying dataset")
    @Test
    void iriStringDelegatesToOriginal() {
        final String result = randomUUID().toString();

        when(original.getIRIString()).thenReturn(result);

        assertThat(wrapped.getIRIString(), is(equalTo(result)));
        verify(original).getIRIString();
    }

    @DisplayName("N-Triples string delegates to underlying dataset")
    @Test
    void ntriplesStringDelegatesToOriginal() {
        final String result = randomUUID().toString();

        when(original.ntriplesString()).thenReturn(result);

        assertThat(wrapped.ntriplesString(), is(equalTo(result)));
        verify(original).ntriplesString();
    }

    @DisplayName("hash code delegates to underlying dataset")
    @Test
    void hashCodeDelegatesToOriginal() {
        final int result = randomUUID().hashCode();

        final MockIRI original = new MockIRI(result);
        final MockWrapperIRI wrapped = new MockWrapperIRI(original, graph);

        assertThat(wrapped.hashCode(), is(equalTo(result)));
        assertThat(original.hashCodeCalled, is(true));
    }

    @DisplayName("equals delegates to underlying dataset")
    @Test
    void equalsDelegatesToOriginal() {
        final boolean result = true;

        final MockIRI original = new MockIRI(result);
        final MockWrapperIRI wrapped = new MockWrapperIRI(original, graph);

        assertThat(wrapped.equals(null), is(equalTo(result)));
        assertThat(original.equalsCalled, is(true));
    }

    class MockWrapperIRI extends WrapperIRI {
        protected MockWrapperIRI(final RDFTerm original, final Graph graph) {
            super(original, graph);
        }
    }

    class MockIRI implements IRI {
        int hashCode = 0;
        boolean equals = false;
        boolean hashCodeCalled = false;
        boolean equalsCalled = false;

        public MockIRI(final int hashCode) {
            this.hashCode = hashCode;
        }

        public MockIRI(final boolean equals) {
            this.equals = equals;
        }

        public boolean equals(final Object o) {
            equalsCalled = true;
            return equals;
        }

        public int hashCode() {
            hashCodeCalled = true;
            return hashCode;
        }

        // region unsupported
        public String ntriplesString() {
            throw new UnsupportedOperationException();
        }

        public String getIRIString() {
            throw new UnsupportedOperationException();
        }
        // endregion
    }
}
