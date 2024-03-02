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
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Implementor")
class ImplementorTest {
    @Test
    @DisplayName("fails to implement when filer can't write")
    void failsToWrite() throws IOException {
        final TypeElement type = mock(TypeElement.class);
        final Definition definition = mock(Definition.class);
        final Environment env = mock(Environment.class);
        final Elements elementUtils = mock(Elements.class);
        final Name name = mock(Name.class);
        final PackageElement packageElement = mock(PackageElement.class);
        final Filer filer = mock(Filer.class);
        when(definition.getType()).thenReturn(type);
        when(definition.getEnv()).thenReturn(env);

        when(elementUtils.getBinaryName(any())).thenReturn(name);
        when(packageElement.getQualifiedName()).thenReturn(name);
        when(elementUtils.getPackageOf(any())).thenReturn(packageElement);
        when(filer.createSourceFile(any())).thenThrow(IOException.class); // Substance
        when(env.getElementUtils()).thenReturn(elementUtils);
        when(env.getFiler()).thenReturn(filer);

        @SuppressWarnings({"rawtypes", "unchecked"}) // For test brevity
        final Implementor mock = new Implementor(definition) {
            @Override
            protected void implementInternal() {
            }
        };

        final Throwable t = assertThrows(RuntimeException.class, mock::implement);
        assertThat(t, hasProperty("message", is("could not open writer")));
    }
}
