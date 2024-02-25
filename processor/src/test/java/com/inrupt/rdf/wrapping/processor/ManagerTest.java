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

import static com.inrupt.rdf.wrapping.processor.Manager.wrap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Manager")
class ManagerTest {
    @DisplayName("fails to wrap with error")
    @ParameterizedTest(name = "[{1}] when definition is [{0}]")
    @MethodSource
    void wrapFails(final Class<?> definition, final String error) {
        final Model model = ModelFactory.createDefaultModel();

        final Throwable t = assertThrows(RuntimeException.class, () -> wrap(model, definition));
        assertThat(t, hasProperty("message", is(error)));
    }

    static Stream<Arguments> wrapFails() {
        return Stream.of(
                arguments(NoImplementation.class, "implementation not found"),
                arguments(ImplementationTypeMismatch.class, "implementation type mismatch"),
                arguments(WrapMissing.class, "wrap method not found"),
                arguments(WrapVoid.class, "wrap method is void"),
                arguments(WrapInaccessible.class, "wrap method inaccessible"),
                arguments(WrapThrows.class, "wrap method threw exception"),
                arguments(WrapMismatch.class, "wrap method return type mismatch")
        );
    }

    @Test
    @DisplayName("wraps graph")
    void wrapModel() {
        final Model model = ModelFactory.createDefaultModel();

        assertDoesNotThrow(() -> wrap(model, OkModel.class));
    }

    @Test
    @DisplayName("wraps dataset")
    void wrapDataset() {
        final Dataset dataset = DatasetFactory.create();

        assertDoesNotThrow(() -> wrap(dataset, OkDataset.class));
    }

    interface NoImplementation {
    }

    interface ImplementationTypeMismatch {
    }

    static class ImplementationTypeMismatch_$impl {
    }

    interface WrapMissing {
    }

    static class WrapMissing_$impl implements WrapMissing {
    }

    interface WrapVoid {
    }

    static class WrapVoid_$impl implements WrapVoid {
        static void wrap(final Model ignored) {
        }
    }

    interface WrapInaccessible {
    }

    static class WrapInaccessible_$impl implements WrapInaccessible {
        private static WrapInaccessible wrap(final Model ignored) {
            return null;
        }
    }

    interface WrapThrows {
    }

    static class WrapThrows_$impl implements WrapThrows {
        static Object wrap(final Model ignored) {
            throw new RuntimeException();
        }
    }

    interface WrapMismatch {
    }

    static class WrapMismatch_$impl implements WrapMismatch {
        static Object wrap(final Model ignored) {
            return new Object();
        }
    }

    interface OkModel {
    }

    static class OkModel_$impl implements OkModel {
        static OkModel wrap(final Model ignored) {
            return new OkModel() {
            };
        }
    }

    interface OkDataset {
    }

    static class OkDataset_$impl implements OkDataset {
        static OkDataset wrap(final Dataset ignored) {
            return new OkDataset() {
            };
        }
    }
}
