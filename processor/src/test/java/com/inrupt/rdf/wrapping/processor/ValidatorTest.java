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

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;

import java.util.stream.Stream;

import javax.tools.JavaFileObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Compile-time validation")
class ValidatorTest {
    private static final String MUST_BE_AN_INTERFACE = "must be an interface";
    private static final String CANNOT_BE_STATIC_OR_DEFAULT = "cannot be static or default";
    private static final String MUST_BE_STATIC_OR_DEFAULT = "must be static or default";
    private static final String MUST_RETURN_RESOURCE_INTERFACE = "must return @Resource interface";
    private static final String MUST_RETURN_GRAPH_INTERFACE = "must return @Graph interface";

    private Compiler compiler;

    @BeforeEach
    void init() {
        compiler = Compiler.javac().withProcessors(new Processor());
    }

    @DisplayName("produces error containing")
    @ParameterizedTest(name = "[{1}] when {0}")
    @MethodSource
    void compilationErrorOnInvalid(final String name, final String error) {
        final JavaFileObject file = forResource("validationExamples/bad/" + name + ".java");
        final Compilation compilation = compiler.compile(file);

        assertThat(compilation).hadErrorContaining(error).inFile(file);
    }

    private static Stream<Arguments> compilationErrorOnInvalid() {
        return Stream.of(
                arguments("dataset is not an interface", MUST_BE_AN_INTERFACE),
                arguments("dataset extends unacceptable", "can only extend org.apache.jena.query.Dataset"),
                arguments("dataset default graph method is static", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("dataset default graph method is default", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("dataset default graph method is not graph", MUST_RETURN_GRAPH_INTERFACE),
                arguments("dataset named graph method is static", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("dataset named graph method is default", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("dataset named graph method is not graph", MUST_RETURN_GRAPH_INTERFACE),
                arguments("dataset instance method is not annotated", MUST_BE_STATIC_OR_DEFAULT),

                arguments("graph is not an interface", MUST_BE_AN_INTERFACE),
                arguments("graph extends unacceptable", "can only extend org.apache.jena.rdf.model.Model"),
                arguments("graph instance of method is static", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("graph instance of method is default", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("graph instance of method is not resource", MUST_RETURN_RESOURCE_INTERFACE),
                arguments("graph object of method is static", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("graph object of method is default", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("graph object of method is not resource", MUST_RETURN_RESOURCE_INTERFACE),
                arguments("graph subject of method is static", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("graph subject of method is default", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("graph subject of method is not resource", MUST_RETURN_RESOURCE_INTERFACE),
                arguments("graph instance method is not annotated", MUST_BE_STATIC_OR_DEFAULT),

                arguments("resource is not an interface", MUST_BE_AN_INTERFACE),
                arguments("resource extends unacceptable", "can only extend org.apache.jena.rdf.model.Resource"),
                arguments("resource property method is static", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("resource property method is default", CANNOT_BE_STATIC_OR_DEFAULT),
                arguments("resource instance method is not annotated", MUST_BE_STATIC_OR_DEFAULT),
                arguments("resource property method return type not assignable", "must be assignable from return type"),
                arguments("resource property method is void", "must not be void"),
                arguments("resource complex property method is not resource", MUST_RETURN_RESOURCE_INTERFACE)
        );
    }

    @DisplayName("succeeds when")
    @ParameterizedTest(name = "{0}")
    @MethodSource
    void compilationSucceeds(final String name) {
        final JavaFileObject file = forResource("validationExamples/good/" + name + ".java");
        final Compilation compilation = compiler.compile(file);

        assertThat(compilation).succeeded();
    }

    private static Stream<Arguments> compilationSucceeds() {
        return Stream.of(
                arguments("dataset extends nothing"),
                arguments("dataset extends Dataset"),
                arguments("dataset default graph method is graph instance"),
                arguments("dataset named graph method is graph instance"),
                arguments("dataset unannotated method is static"),
                arguments("dataset unannotated method is default"),

                arguments("graph extends nothing"),
                arguments("graph extends Model"),
                arguments("graph instance of method is resource instance"),
                arguments("graph subject of method is resource instance"),
                arguments("graph object of method is resource instance"),
                arguments("graph unannotated method is static"),
                arguments("graph unannotated method is default"),

                arguments("resource extends nothing"),
                arguments("resource extends Resource"),
                arguments("resource property method is assignable instance"),
                arguments("resource unannotated method is static"),
                arguments("resource unannotated method is default"),
                arguments("resource complex property method is resource instance"),
                arguments("resource complex property method recursive")
        );
    }
}
