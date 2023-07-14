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
package com.inrupt.rdf.wrapping.test.base;

import static java.lang.reflect.Modifier.*;
import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class HasSameMethods {
    private static Class<?> class1;
    private static Class<?> class2;

    public static void initializeClassesForComparison(final Class<?> class1, final Class<?> class2) {
        HasSameMethods.class1 = class1;
        HasSameMethods.class2 = class2;
    }

    protected abstract Stream<Class<?>> translate(Class<?> clazz);

    @DisplayName("have same public static methods")
    @ParameterizedTest(name = "{1}")
    @MethodSource
    void haveSameMethods(final Class<?> thisClass, final Method thatMethod) {
        final Class<?>[] thoseParams = stream(thatMethod.getParameterTypes())
                .flatMap(this::translate)
                .filter(Objects::nonNull)
                .toArray(Class<?>[]::new);
        final Class<?> thatReturnType = translate(thatMethod.getReturnType())
                .findFirst()
                .orElse(Void.class);

        final Method thisMethod = assertDoesNotThrow(() ->
                thisClass.getDeclaredMethod(thatMethod.getName(), thoseParams));
        assertThat(thisMethod.getReturnType(), is(thatReturnType));
    }

    @SuppressWarnings("java:S2234") // Intentionally permuting
    static Stream<Arguments> haveSameMethods() {
        return concat(
                asArguments(class1, class2),
                asArguments(class2, class1));
    }

    private static Stream<Arguments> asArguments(final Class<?> thisClass, final Class<?> thatClass) {
        return stream(thatClass.getDeclaredMethods())
                .filter(HasSameMethods::relevantMethod)
                .map(thatMethod -> Arguments.of(thisClass, thatMethod));
    }

    private static boolean relevantMethod(final Method method) {
        return isPublic(method.getModifiers()) && isStatic(method.getModifiers())
               || isProtected(method.getModifiers());
    }
}
