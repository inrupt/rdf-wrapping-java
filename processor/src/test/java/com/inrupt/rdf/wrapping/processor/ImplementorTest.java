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

import static javax.lang.model.type.TypeKind.VOID;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import org.junit.jupiter.api.Test;

class ImplementorTest {
    @Test
    void x() throws IOException {
        final TypeElement type = mock(TypeElement.class);
        final Interface myInterface = mock(Interface.class);
        final Implementation myClass = mock(Implementation.class);
        final TypeMirror mirror = mock(NoType.class);
        final Environment env = mock(Environment.class);
        final Elements elementUtils = mock(Elements.class);
        final Name name = mock(Name.class);
        final PackageElement packageElement = mock(PackageElement.class);
        final Filer filer = mock(Filer.class);
        when(mirror.getKind()).thenReturn(VOID);
        when(type.asType()).thenReturn(mirror);
        when(myInterface.getType()).thenReturn(type);
        when(myInterface.getEnv()).thenReturn(env);
        when(myClass.getMyInterface()).thenReturn(myInterface);

        when(elementUtils.getBinaryName(any())).thenReturn(name);
        when(packageElement.getQualifiedName()).thenReturn(name);
        when(elementUtils.getPackageOf(any())).thenReturn(packageElement);
        when(filer.createSourceFile(any())).thenThrow(IOException.class);
        when(env.getElementUtils()).thenReturn(elementUtils);
        when(env.getFiler()).thenReturn(filer);

        final Implementor mock = new Implementor(myClass) {
            @Override
            protected void implementInternal() {
            }
        };

        assertThrows(RuntimeException.class, mock::implement);
    }
}
