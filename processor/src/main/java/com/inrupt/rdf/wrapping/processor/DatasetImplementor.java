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

import static org.jboss.jdeparser.JExprs.*;
import static org.jboss.jdeparser.JMod.*;
import static org.jboss.jdeparser.JTypes.$t;

import javax.annotation.Generated;

import org.apache.jena.query.Dataset;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetImpl;
import org.jboss.jdeparser.JExpr;
import org.jboss.jdeparser.JMethodDef;
import org.jboss.jdeparser.JType;

class DatasetImplementor extends Implementor<DatasetDefinition> {
    DatasetImplementor(final DatasetDefinition definition) {
        super(definition);
    }

    @Override
    protected void implementInternal() {
        addImports();
        addClass();
        addConstructor();
        addWrap();
        addGraphs();
    }

    private void addImports() {
        sourceFile
                ._import(Generated.class)
                ._import(Dataset.class)
                ._import(DatasetGraph.class)
                ._import(DatasetImpl.class);
    }

    private void addClass() {
        addClass(DatasetImpl.class);
    }

    private void addConstructor() {
        final JMethodDef myConstructor = target.constructor(PROTECTED);
        myConstructor.param(FINAL, DatasetGraph.class, ORIGINAL);
        myConstructor.body().callSuper().arg($v(ORIGINAL));
    }

    private void addWrap() {
        final JMethodDef myWrap = target.method(PUBLIC | STATIC, definition.getOriginalInterface(), WRAP);
        myWrap.param(FINAL, Dataset.class, ORIGINAL);
        myWrap.body()._return($t(target)._new().arg($v(ORIGINAL).call("asDatasetGraph")));
    }

    private void addGraphs() {
        definition.properties().forEach(p -> {
            if (p.graphName().isEmpty()) {
                addGraph(p, call("getDefaultModel"));
            } else {
                addGraph(p, call("getNamedModel").arg(str(p.graphName())));
            }
        });
    }

    private void addGraph(final DatasetPropertyDefinition d, final JExpr expr) {
        final JType implementation = asImplementation(d.getReturnType());

        addMethod(d).body()._return(implementation.call(WRAP).arg(expr));
    }
}
