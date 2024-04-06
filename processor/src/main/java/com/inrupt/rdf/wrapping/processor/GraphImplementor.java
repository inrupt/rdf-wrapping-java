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

import static com.inrupt.rdf.wrapping.processor.ResourceImplementor.FACTORY;
import static org.jboss.jdeparser.JExprs.$v;
import static org.jboss.jdeparser.JMod.*;
import static org.jboss.jdeparser.JType.THIS;
import static org.jboss.jdeparser.JTypes.$t;

import com.inrupt.rdf.wrapping.jena.WrapperModel;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.jboss.jdeparser.JCall;
import org.jboss.jdeparser.JExprs;
import org.jboss.jdeparser.JMethodDef;
import org.jboss.jdeparser.JType;

class GraphImplementor extends Implementor<GraphDefinition> {
    GraphImplementor(final GraphDefinition definition) {
        super(definition);
    }

    @Override
    protected void implementInternal() {
        addClass();
        final JMethodDef constructor = addConstructor();
        addToPersonality(constructor);
        addWrap();

        addResourceMethods();
    }

    private void addClass() {
        addClass(WrapperModel.class);
    }

    private JMethodDef addConstructor() {
        final JMethodDef constructor = target.constructor(PROTECTED);
        constructor.param(FINAL, Graph.class, ORIGINAL);
        constructor.body().callSuper().arg($v(ORIGINAL));

        return constructor;
    }

    private void addWrap() {
        final JMethodDef myWrap = target.method(PUBLIC | STATIC, getOriginalInterface(), WRAP);
        myWrap.param(FINAL, Model.class, ORIGINAL);
        myWrap.body()._return($t(target)._new().arg($v(ORIGINAL).call("getGraph")));
    }

    private void addToPersonality(final JMethodDef constructor) {
        definition.transitiveResourceTypes().forEach(type -> {
            final JType implementation = asImplementation(type);

            constructor
                    .body()
                    .call(THIS._super().call("getPersonality"), "add")
                    .arg(implementation._class())
                    .arg(implementation.field(FACTORY));
        });
    }

    private void addResourceMethods() {
        definition.properties().forEach(p -> {
            final JType implementation = asImplementation(p.getReturnType());

            // Call model wrapper convenience method passing projection class argument
            final JCall convenienceCall = THIS._super().call(p.method()).arg(implementation._class());

            // Pass each filter value from the annotation as additional argument
            for (final String value : p.resources()) {
                convenienceCall.arg(JExprs.str(value));
            }

            addMethod(p).body()._return(convenienceCall);
        });
    }
}
