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

import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

class Environment extends WrapperProcessingEnvironment {
    Environment(final ProcessingEnvironment env) {
        super(env);
    }

    TypeElement findDeclaration(final Class<?> clazz) {
        return getElementUtils().getTypeElement(clazz.getCanonicalName());
    }

    TypeElement findDeclaration(final TypeMirror mirror) {
        return (TypeElement) getTypeUtils().asElement(mirror);
    }

    boolean isSameType(final TypeMirror t1, final Class<?> t2) {
        return isSameType(t1, mirror(t2));
    }

    boolean isSameType(final TypeMirror t1, final TypeMirror t2) {
        return getTypeUtils().isSameType(t1, t2);
    }

    Stream<ExecutableElement> methodsOf(final Element element) {
        return ElementFilter.methodsIn(element.getEnclosedElements()).stream();
    }

    Stream<ExecutableElement> methodsOf(final Class<?> clazz) {
        return methodsOf(findDeclaration(clazz));
    }

    private TypeMirror mirror(final Class<?> clazz) {
        return findDeclaration(clazz).asType();
    }
}
