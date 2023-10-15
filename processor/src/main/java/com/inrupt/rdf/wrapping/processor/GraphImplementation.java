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

import static com.inrupt.rdf.wrapping.processor.ResourceImplementation.FACTORY;
import static org.jboss.jdeparser.JExprs.$v;
import static org.jboss.jdeparser.JExprs.call;
import static org.jboss.jdeparser.JMod.*;
import static org.jboss.jdeparser.JTypes.$t;

import com.inrupt.rdf.wrapping.jena.WrapperModel;

import javax.annotation.Generated;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.jboss.jdeparser.*;

class GraphImplementation extends Implementation {
    private static final String ORIGINAL = "original";

    private JMethodDef constructor;

    GraphImplementation(final EnvironmentHelper environment) {
        super(environment);
    }

    void addImports(final JSourceFile source) {
        source
                ._import(WrapperModel.class)
                ._import(Generated.class)
                ._import(Graph.class)
                ._import(Model.class)
                ._import(ModelCom.class);
    }

    void addClass(final JSourceFile source, final String name, final JType originalInterface) {
        addClass(source, name, originalInterface, WrapperModel.class);
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

    void addToPersonality(final TypeMirror type) {
        final JType implementation = asImplementation(type);

        constructor
                .body()
                .call(call("getPersonality"), "add")
                .arg(implementation._class())
                .arg(implementation.field(FACTORY));
    }

    void addResourceMethod(final ExecutableElement method, final String convenience, final String[] values) {
        final JType implementation = asImplementation(method.getReturnType());

        // Call model wrapper convenience method passing projection class argument
        final JCall convenienceCall = call(convenience).arg(implementation._class());

        // Pass each filter value from the annotation as additional argument
        for (final String value : values) {
            convenienceCall.arg(JExprs.str(value));
        }

        addMethod(method).body()._return(convenienceCall);
    }
}
