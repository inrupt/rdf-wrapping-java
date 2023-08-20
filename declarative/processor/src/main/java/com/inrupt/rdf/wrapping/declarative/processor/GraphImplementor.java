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

import com.inrupt.rdf.wrapping.declarative.annotation.FirstInstanceOf;
import com.inrupt.rdf.wrapping.jena.WrapperModel;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.jboss.jdeparser.*;

class GraphImplementor extends Implementor {
    GraphImplementor(final ProcessingEnvironment processingEnvironment, final TypeElement annotatedElement) {
        super(processingEnvironment, annotatedElement);
    }

    @Override
    protected void implementInternal() {
        // Imports
        sourceFile
                ._import(WrapperModel.class)
                ._import(Generated.class)
                ._import(Graph.class)
                ._import(Model.class)
                ._import(ModelCom.class);

        // Class
        final JClassDef implementation = sourceFile
                ._class(PUBLIC, implementationClass)
                ._extends(WrapperModel.class)
                ._implements(originalInterface);

        // @Generated & Javadocs
        annotateAndDocumentAsGenerated(implementation);

        // Constructor
        final JMethodDef constructor = implementation.constructor(PROTECTED);
        constructor.param(FINAL, Graph.class, "original");
        constructor.body().callSuper().arg($v("original"));

        // Personality
        membersAnnotatedWith(FirstInstanceOf.class).forEach(method -> {
            final JType returnType = JTypes.typeOf(method.getReturnType());
            final Element returnTypeElement = processingEnvironment.getTypeUtils()
                    .asElement(method.getReturnType());
            final String originalBinaryName = processingEnvironment
                    .getElementUtils()
                    .getBinaryName((TypeElement) returnTypeElement)
                    .toString();
            final String qualifiedName = originalBinaryName + "_$impl";

            sourceFile._import(returnType);
            constructor
                    .body()
                    .call($t(implementationClass)._this().call("getPersonality"), "add")
                    .arg($t(qualifiedName)._class())
                    .arg($t(qualifiedName).field("factory"));
        });

        // Wrap method
        final JMethodDef wrapMethod = implementation.method(PUBLIC | STATIC, originalInterface, "wrap");
        wrapMethod.param(FINAL, Model.class, "original");
        wrapMethod.body()._return($t(implementationClass)._new().arg($v("original").call("getGraph")));

        // @FirstInstanceOf
        membersAnnotatedWith(FirstInstanceOf.class).forEach(method -> {
            final JType returnType = JTypes.typeOf(method.getReturnType());
            final Element returnTypeElement = processingEnvironment.getTypeUtils()
                    .asElement(method.getReturnType());
            final String originalBinaryName = processingEnvironment
                    .getElementUtils()
                    .getBinaryName((TypeElement) returnTypeElement)
                    .toString();
            final String qualifiedName = originalBinaryName + "_$impl";

            sourceFile._import(returnType);
            final JMethodDef namedGraphMethod = implementation
                    .method(PUBLIC, returnType, method.getSimpleName().toString());
            namedGraphMethod.annotate(Override.class);
            namedGraphMethod
                    .body()
                    ._return($t(implementationClass)
                            ._this()
                            .call("firstInstanceOf")
                            .arg(JExprs.str(method.getAnnotation(FirstInstanceOf.class).value()))
                            .arg($t(qualifiedName)._class()));
        });
    }
}
