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

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

class Interface {
    protected final EnvironmentHelper environment;
    private final TypeElement type;

    Interface(final EnvironmentHelper environment, final TypeElement type) {
        this.environment = environment;
        this.type = type;
    }

    protected final Stream<ExecutableElement> membersAnnotatedWith(final Class<? extends Annotation> annotation) {
        return environment.methodsOf(type).stream()
                .filter(method -> !method.isDefault())
                .filter(method -> !method.getModifiers().contains(Modifier.STATIC))
                .filter(method -> !environment.isVoid(method.getReturnType()))
                .filter(method -> method.getAnnotation(annotation) != null);
    }
}
