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

import com.inrupt.rdf.wrapping.annotation.OptionalFirstInstanceOfEither;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstObjectOfEither;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstSubjectOfEither;
import com.inrupt.rdf.wrapping.jena.WrapperModel;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.jboss.jdeparser.*;

class GraphImplementor extends Implementor {
    GraphImplementor(final ProcessingEnvironment environment, final Element element) {
        super(environment, element);
    }

    @Override
    protected void implementInternal() {
        addImports();

        final JClassDef myClass = createClass(WrapperModel.class);
        final JType myType = $t(myClass);

        final JMethodDef myConstructor = createConstructor(myClass);

        addImplementationsToPersonality(myConstructor); // TODO: Also add return types of properties of resources

        createWrapMethod(myClass, myType);

        createMethods(
                myClass,
                OptionalFirstInstanceOfEither.class,
                "optionalFirstInstanceOfEither",
                m -> m.getAnnotation(OptionalFirstInstanceOfEither.class).value()
        );

        createMethods(
                myClass,
                OptionalFirstSubjectOfEither.class,
                "optionalFirstSubjectOfEither",
                m -> m.getAnnotation(OptionalFirstSubjectOfEither.class).value()
        );

        createMethods(
                myClass,
                OptionalFirstObjectOfEither.class,
                "optionalFirstObjectOfEither",
                m -> m.getAnnotation(OptionalFirstObjectOfEither.class).value()
        );
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

    private void addImplementationsToPersonality(final JMethodDef myConstructor) {
        membersAnnotatedWithAny(
                OptionalFirstInstanceOfEither.class,
                OptionalFirstSubjectOfEither.class,
                OptionalFirstObjectOfEither.class)
                .map(ExecutableElement::getReturnType)
                .distinct()
                .forEach(returnType -> {
                    final JType implementationType = asImplementation(returnType);

                    myConstructor
                            .body()
                            .call(call("getPersonality"), "add")
                            .arg(implementationType._class())
                            .arg(implementationType.field(ResourceImplementor.FACTORY));
                });
    }

    private void createWrapMethod(final JClassDef myClass, final JType myType) {
        final JMethodDef myWrap = myClass.method(PUBLIC | STATIC, originalInterface, WRAP);
        myWrap.param(FINAL, Model.class, ORIGINAL);
        myWrap.body()._return(myType._new().arg($v(ORIGINAL).call("getGraph")));
    }

    private void createMethods(
            final JClassDef myClass,
            final Class<? extends Annotation> annotation,
            final String convenienceMethod,
            final Function<ExecutableElement, String[]> annotationValueExtractor) {

        membersAnnotatedWithAny(annotation).forEach(method -> {
            final JType returnType = JTypes.typeOf(method.getReturnType());
            final JType implementationType = asImplementation(method.getReturnType());

            final JMethodDef myMethod = myClass.method(PUBLIC, returnType, method.getSimpleName().toString());
            myMethod.annotate(Override.class);

            // Call model wrapper convenience method passing projection class argument
            final JCall wrapperConvenienceCall = call(convenienceMethod).arg(implementationType._class());

            // Pass each filter value from the annotation as additional argument
            for (final String s : annotationValueExtractor.apply(method)) {
                wrapperConvenienceCall.arg(JExprs.str(s));
            }

            myMethod.body()._return(wrapperConvenienceCall);
        });
    }
}
