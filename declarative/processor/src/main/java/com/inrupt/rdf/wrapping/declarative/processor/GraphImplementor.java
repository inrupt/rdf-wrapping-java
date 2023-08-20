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
package com.inrupt.rdf.wrapping.declarative.processor;

import static org.jboss.jdeparser.JExprs.$v;
import static org.jboss.jdeparser.JMod.*;
import static org.jboss.jdeparser.JTypes.$t;

import com.inrupt.rdf.wrapping.declarative.annotation.OptionalFirstInstanceOfEither;
import com.inrupt.rdf.wrapping.jena.WrapperModel;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.jboss.jdeparser.*;

class GraphImplementor extends Implementor {
    GraphImplementor(final ProcessingEnvironment environment, final TypeElement element) {
        super(environment, element);
    }

    @Override
    protected void implementInternal() {
        addImports();

        final JClassDef myClass = createClass(WrapperModel.class);
        final JType myType = $t(myClass);
        final JExpr myInstance = myType._this();

        final JMethodDef myConstructor = createConstructor(myClass);

        addImplementationsToPersonality(myConstructor, myInstance);

        createWrapMethod(myClass, myType);

        createOptionalFirstInstanceOfEitherMethods(myClass, myInstance);
    }

    private void addImports() {
        sourceFile
                ._import(WrapperModel.class)
                ._import(Generated.class)
                ._import(Graph.class)
                ._import(Model.class)
                ._import(ModelCom.class);
    }

    private static JMethodDef createConstructor(final JClassDef myClass) {
        final JMethodDef myConstructor = myClass.constructor(PROTECTED);
        myConstructor.param(FINAL, Graph.class, ORIGINAL);
        myConstructor.body().callSuper().arg($v(ORIGINAL));

        return myConstructor;
    }

    private void addImplementationsToPersonality(final JMethodDef myConstructor, final JExpr myInstance) {
        membersAnnotatedWith(OptionalFirstInstanceOfEither.class).forEach(method -> {
            final JType returnType = JTypes.typeOf(method.getReturnType());
            final JType implementationType = returnTypeAsImplementation(method);

            sourceFile._import(returnType);
            myConstructor
                    .body()
                    .call(myInstance.call("getPersonality"), "add")
                    .arg(implementationType._class())
                    .arg(implementationType.field(ResourceImplementor.FACTORY));
        });
    }

    private void createWrapMethod(final JClassDef myClass, final JType myType) {
        final JMethodDef myWrap = myClass.method(PUBLIC | STATIC, originalInterface, WRAP);
        myWrap.param(FINAL, Model.class, ORIGINAL);
        myWrap.body()._return(myType._new().arg($v(ORIGINAL).call("getGraph")));
    }

    private void createOptionalFirstInstanceOfEitherMethods(final JClassDef myClass, final JExpr myInstance) {
        membersAnnotatedWith(OptionalFirstInstanceOfEither.class).forEach(method -> {
            final JType returnType = JTypes.typeOf(method.getReturnType());
            final JType implementationType = returnTypeAsImplementation(method);

            sourceFile._import(returnType);
            final JMethodDef namedGraphMethod = myClass.method(PUBLIC, returnType, method.getSimpleName().toString());
            namedGraphMethod.annotate(Override.class);

            // Call model wrapper convenience method passing projection class argument
            final JCall wrapperConvenienceCall = myInstance
                    .call("optionalFirstInstanceOfEither")
                    .arg(implementationType._class());

            // Pass each filter class value from the annotation as additional argument
            for (final String s : method.getAnnotation(OptionalFirstInstanceOfEither.class).value()) {
                wrapperConvenienceCall.arg(JExprs.str(s));
            }

            namedGraphMethod.body()._return(wrapperConvenienceCall);
        });
    }
}
