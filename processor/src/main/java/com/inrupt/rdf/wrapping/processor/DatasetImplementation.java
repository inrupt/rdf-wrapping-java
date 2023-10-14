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

import java.time.Instant;

import javax.annotation.Generated;

import org.apache.jena.query.Dataset;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetImpl;
import org.jboss.jdeparser.JClassDef;
import org.jboss.jdeparser.JMethodDef;
import org.jboss.jdeparser.JSourceFile;
import org.jboss.jdeparser.JType;

class DatasetImplementation {
    private static final String ORIGINAL = "original";

    static final String WRAP = "wrap"; // TODO: reuse in Manager

    private JClassDef target;

    void addImports(final JSourceFile source) {
        source
                ._import(Generated.class)
                ._import(Dataset.class)
                ._import(DatasetGraph.class)
                ._import(DatasetImpl.class);
    }

    void addClass(final JSourceFile source, final String name, final JType originalInterface) {
        target = source._class(PUBLIC, name);
        target._extends(DatasetImpl.class);
        target._implements(originalInterface);
    }

    void annotateAndDocument() {
        target.docComment().text("Warning, this class consists of generated code.");
        target.annotate(Generated.class).value(this.getClass().getName()).value("date", Instant.now().toString());
    }

    void addConstructor() {
        final JMethodDef myConstructor = target.constructor(PROTECTED);
        myConstructor.param(FINAL, DatasetGraph.class, ORIGINAL);
        myConstructor.body().callSuper().arg($v(ORIGINAL));
    }

    void addWrap(final JType originalInterface) {
        final JMethodDef myWrap = target.method(PUBLIC | STATIC, originalInterface, WRAP);
        myWrap.param(FINAL, Dataset.class, ORIGINAL);
        myWrap.body()._return($t(target)._new().arg($v(ORIGINAL).call("asDatasetGraph")));
    }

    void addDefaultGraph(final JType implementation, final String name, final JType returnType) {
        final JMethodDef myMethod = target.method(PUBLIC, returnType, name);
        myMethod.annotate(Override.class);
        myMethod.body()._return(implementation.call(WRAP).arg(call("getDefaultModel")));
    }

    void addNamedGraph(final JType implementation, final String name, final String graph, final JType returnType) {
        final JMethodDef myMethod = target.method(PUBLIC, returnType, name);
        myMethod.annotate(Override.class);
        myMethod.body()._return(implementation.call(WRAP).arg(call("getNamedModel").arg(str(graph))));
    }
}