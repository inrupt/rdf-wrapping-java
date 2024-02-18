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
package com.inrupt.rdf.wrapping.annotation;

import static com.inrupt.rdf.wrapping.annotation.Property.Mapping;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;

import com.inrupt.rdf.wrapping.jena.ValueMappings;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Mapping")
class MappingTest {
    private static final Collection<String> MAPPING_METHODS = mappingMethods().collect(toList());
    private static final Collection<String> ENUM_CONSTANTS = stream(Mapping.class.getEnumConstants())
            .map(Mapping::getMethodName)
            .collect(toList());

    @DisplayName("has equivalent ValueMappings method for enum constant")
    @ParameterizedTest(name = "{0}")
    @EnumSource(Mapping.class)
    void constantHasEquivalentMethod(final Mapping mapping) {
        assertThat(mapping.getMethodName(), is(in(MAPPING_METHODS)));
    }

    @DisplayName("has enum constant equivalent to ValueMappings method")
    @ParameterizedTest(name = "{0}")
    @MethodSource("mappingMethods")
    void methodHasEquivalentConstant(final String method) {
        assertThat(method, is(in(ENUM_CONSTANTS)));
    }

    private static Stream<String> mappingMethods() {
        return stream(ValueMappings.class.getDeclaredMethods())
                .filter(method -> isPublic(method.getModifiers()))
                .map(Method::getName);
    }
}
