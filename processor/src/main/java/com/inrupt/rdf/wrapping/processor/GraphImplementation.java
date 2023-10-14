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

import static org.jboss.jdeparser.JExprs.$v;
import static org.jboss.jdeparser.JExprs.call;
import static org.jboss.jdeparser.JMod.*;
import static org.jboss.jdeparser.JTypes.$t;

import com.inrupt.rdf.wrapping.jena.WrapperModel;

import java.time.Instant;

import javax.annotation.Generated;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.jboss.jdeparser.*;

class GraphImplementation {
    private static final String ORIGINAL = "original";
    private static final String WRAP = "wrap";

    private JClassDef target;
    private JMethodDef constructor;

    void addImports(final JSourceFile source) {
        source
                ._import(WrapperModel.class)
                ._import(Generated.class)
                ._import(Graph.class)
                ._import(Model.class)
                ._import(ModelCom.class);
    }

    void addClass(final JSourceFile source, final String name, final JType originalInterface) {
        target = source._class(PUBLIC, name);
        target._extends(WrapperModel.class);
        target._implements(originalInterface);
    }

    void annotateAndDocument() {
        target.docComment().text("Warning, this class consists of generated code.");
        target.annotate(Generated.class).value(this.getClass().getName()).value("date", Instant.now().toString());
    }

    void addConstructor() {
        constructor = target.constructor(PROTECTED);
        constructor.param(FINAL, Graph.class, ORIGINAL);
        constructor.body().callSuper().arg($v(ORIGINAL));
    }

    void addWrap(final JType originalInterface) {
        final JMethodDef myWrap = target.method(PUBLIC | STATIC, originalInterface, WRAP);
        myWrap.param(FINAL, Model.class, ORIGINAL);
        myWrap.body()._return($t(target)._new().arg($v(ORIGINAL).call("getGraph")));
    }

    void addToPersonality(final JType implementationType) {
        constructor
                .body()
                .call(call("getPersonality"), "add")
                .arg(implementationType._class())
                .arg(implementationType.field(ResourceImplementation.FACTORY));
    }

    void addResourceMethod(
            final JType returnType,
            final String name,
            final String convenience,
            final JType implementationType,
            final String[] values) {

        final JMethodDef myMethod = target.method(PUBLIC, returnType, name);
        myMethod.annotate(Override.class);

        // Call model wrapper convenience method passing projection class argument
        final JCall wrapperConvenienceCall = call(convenience).arg(implementationType._class());

        // Pass each filter value from the annotation as additional argument
        for (final String value : values) {
            wrapperConvenienceCall.arg(JExprs.str(value));
        }

        myMethod.body()._return(wrapperConvenienceCall);
    }
}
