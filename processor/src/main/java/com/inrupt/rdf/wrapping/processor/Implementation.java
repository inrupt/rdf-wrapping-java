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

import static org.jboss.jdeparser.JMod.PUBLIC;
import static org.jboss.jdeparser.JTypes.$t;
import static org.jboss.jdeparser.JTypes.typeOf;

import java.time.Instant;

import javax.annotation.Generated;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.jboss.jdeparser.JClassDef;
import org.jboss.jdeparser.JMethodDef;
import org.jboss.jdeparser.JSourceFile;
import org.jboss.jdeparser.JType;

abstract class Implementation {
    static final String WRAP = "wrap";

    private final Environment env;
    protected JClassDef target;

    Implementation(final Environment env) {
        this.env = env;
    }

    protected void annotateAndDocument() {
        target.docComment().text("Warning, this class consists of generated code.");
        target.annotate(Generated.class).value(this.getClass().getName()).value("date", Instant.now().toString());
    }

    protected void addClass(final JSourceFile source, final String name, final JType original, final Class<?> clazz) {
        target = source._class(PUBLIC, name);
        target._extends(clazz);
        target._implements(original);

        annotateAndDocument();
    }

    protected JMethodDef addMethod(final ExecutableElement method) {
        final String myName = method.getSimpleName().toString();
        final JType myType = typeOf(method.getReturnType());

        final JMethodDef myMethod = target.method(PUBLIC, myType, myName);
        myMethod.annotate(Override.class);

        return myMethod;
    }

    protected JType asImplementation(final TypeMirror original) {
        final TypeElement returnType = env.type(original);
        final String originalBinaryName = env
                .getElementUtils()
                .getBinaryName(returnType)
                .toString();

        return $t(asImplementation(originalBinaryName));
    }

    static String asImplementation(final String original) {
        return original + "_$impl";
    }
}
