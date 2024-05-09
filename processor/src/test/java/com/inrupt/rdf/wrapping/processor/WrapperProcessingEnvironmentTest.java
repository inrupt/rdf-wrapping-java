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

import static java.beans.Introspector.getBeanInfo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("Wrapper processing environment")
@ExtendWith(MockitoExtension.class)
class WrapperProcessingEnvironmentTest {
    // Stubs suffice as we're testing only for reference equality
    @Mock(answer = RETURNS_DEEP_STUBS)
    ProcessingEnvironment original;

    @InjectMocks
    WrapperProcessingEnvironment wrapper;

    @DisplayName("returns original value for property")
    @ParameterizedTest(name = "{0}")
    @MethodSource("propertiesOfWrapper")
    void roundtrips(final String propertyName, final PropertyDescriptor descriptor) throws Exception {
        final Object originalValue = descriptor.getReadMethod().invoke(original);

        final Matcher<Object> sameAsOriginal = describedAs("the same instance as the original's",
                is(theInstance(originalValue)));
        final Matcher<ProcessingEnvironment> roundtripsProperty = describedAs("Wrapper property which is %0",
                hasProperty(propertyName, is(sameAsOriginal)),
                sameAsOriginal);

        assertThat("Property differs", wrapper, roundtripsProperty);
    }

    static Stream<Arguments> propertiesOfWrapper() throws IntrospectionException {
        return Stream.of(getBeanInfo(WrapperProcessingEnvironment.class, Object.class).getPropertyDescriptors())
                .map(d -> arguments(d.getName(), d));
    }
}
