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

import static java.lang.reflect.Modifier.isProtected;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.describedAs;
import static org.hamcrest.Matchers.in;

import com.inrupt.rdf.wrapping.annotation.Property.Cardinality;
import com.inrupt.rdf.wrapping.jena.WrapperResource;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Cardinality")
class CardinalityTest {
    private static final Collection<String> CARDINALITY_METHODS = cardinalityMethods().collect(toList());
    private static final Collection<String> ENUM_CONSTANTS = stream(Cardinality.class.getEnumConstants())
            .map(Cardinality::getMethodName)
            .collect(toList());

    @DisplayName("has equivalent WrapperResource method for enum constant")
    @ParameterizedTest(name = "{0}")
    @EnumSource(Cardinality.class)
    void constantHasEquivalentMethod(final Cardinality mapping) {
        final Matcher<String> hasCorrespondingMethod = describedAs("WrapperResource to have corresponding method",
                in(CARDINALITY_METHODS));

        assertThat("Cardinality enum constant without corresponding WrapperResource method",
                mapping.getMethodName(), hasCorrespondingMethod);
    }

    @Disabled("Not ready yet") // TODO: Enable
    @DisplayName("has enum constant equivalent to WrapperResource method")
    @ParameterizedTest(name = "{0}")
    @MethodSource("cardinalityMethods")
    void methodHasEquivalentConstant(final String method) {
        final Matcher<String> hasCorrespondingConstant = describedAs("Cardinality enum to have corresponding constant",
                in(ENUM_CONSTANTS));

        assertThat("WrapperResource method without corresponding Cardinality enum constant",
                method, hasCorrespondingConstant);
    }

    private static Stream<String> cardinalityMethods() {
        return stream(WrapperResource.class.getDeclaredMethods())
                .filter(method -> isProtected(method.getModifiers()))
                .map(Method::getName);
    }
}
