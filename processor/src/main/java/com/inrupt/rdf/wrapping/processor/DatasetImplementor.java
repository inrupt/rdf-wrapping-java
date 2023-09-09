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

import static org.jboss.jdeparser.JExpr.THIS;
import static org.jboss.jdeparser.JExprs.$v;
import static org.jboss.jdeparser.JMod.*;
import static org.jboss.jdeparser.JTypes.$t;

import com.inrupt.rdf.wrapping.annotation.DefaultGraph;
import com.inrupt.rdf.wrapping.annotation.NamedGraph;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import org.apache.jena.query.Dataset;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetImpl;
import org.jboss.jdeparser.*;

class DatasetImplementor extends Implementor {
    DatasetImplementor(final ProcessingEnvironment environment, final TypeElement element) {
        super(environment, element);
    }

    @Override
    protected void implementInternal() {
        addImports();

        final JClassDef myClass = createClass(DatasetImpl.class);
        final JType myType = $t(myClass);

        createConstructor(myClass);

        createWrapMethod(myClass, myType);

        createDefaultGraphMethods(myClass);

        createNamedGraphMethods(myClass);
    }

    private void addImports() {
        sourceFile
                ._import(Generated.class)
                ._import(Dataset.class)
                ._import(DatasetGraph.class)
                ._import(DatasetImpl.class);
    }

    private static void createConstructor(final JClassDef myClass) {
        final JMethodDef myConstructor = myClass.constructor(PROTECTED);
        myConstructor.param(FINAL, DatasetGraph.class, ORIGINAL);
        myConstructor.body().callSuper().arg($v(ORIGINAL));
    }

    private void createWrapMethod(final JClassDef myClass, final JType myType) {
        final JMethodDef myWrap = myClass.method(PUBLIC | STATIC, originalInterface, WRAP);
        myWrap.param(FINAL, Dataset.class, ORIGINAL);
        myWrap.body()._return(myType._new().arg($v(ORIGINAL).call("asDatasetGraph")));
    }

    private void createDefaultGraphMethods(final JClassDef myClass) {
        membersAnnotatedWithAny(DefaultGraph.class).forEach(method -> {
            final JType returnType = JTypes.typeOf(method.getReturnType());
            final JType implementationType = asImplementation(method.getReturnType());

            final JMethodDef myMethod = myClass
                    .method(PUBLIC, returnType, method.getSimpleName().toString());
            myMethod.annotate(Override.class);
            myMethod
                    .body()
                    ._return(implementationType.call(WRAP).arg(THIS.call("getDefaultModel")));
        });
    }

    private void createNamedGraphMethods(final JClassDef myClass) {
        membersAnnotatedWithAny(NamedGraph.class).forEach(method -> {
            final JType returnType = JTypes.typeOf(method.getReturnType());
            final JType implementationType = asImplementation(method.getReturnType());

            final JMethodDef myMethod = myClass
                    .method(PUBLIC, returnType, method.getSimpleName().toString());
            myMethod.annotate(Override.class);
            myMethod
                    .body()
                    ._return(implementationType
                            .call(WRAP)
                            .arg(THIS
                                    .call("getNamedModel")
                                    .arg(JExprs.str(method.getAnnotation(NamedGraph.class).value()))));
        });
    }
}
