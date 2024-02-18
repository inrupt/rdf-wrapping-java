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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.PropertyUtil.propertyDescriptorsFor;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.beans.PropertyDescriptor;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Wrapper processing environment")
class WrapperProcessingEnvironmentTest {
    private static final ProcessingEnvironment original = mock(ProcessingEnvironment.class);
    private static final ProcessingEnvironment wrapper = new WrapperProcessingEnvironment(original);

    @BeforeAll
    static void beforeAll() {
        // Mocks suffice as we're testing only for reference equality
        when(original.getOptions()).thenReturn(mock(Map.class));
        when(original.getMessager()).thenReturn(mock(Messager.class));
        when(original.getFiler()).thenReturn(mock(Filer.class));
        when(original.getElementUtils()).thenReturn(mock(Elements.class));
        when(original.getTypeUtils()).thenReturn(mock(Types.class));
        when(original.getSourceVersion()).thenReturn(mock(SourceVersion.class));
        when(original.getLocale()).thenReturn(mock(Locale.class));
    }

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

    static Stream<Arguments> propertiesOfWrapper() {
        return Stream.of(propertyDescriptorsFor(wrapper, Object.class)).map(d -> arguments(d.getName(), d));
    }
}
