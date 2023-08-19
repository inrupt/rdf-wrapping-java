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

import com.inrupt.rdf.wrapping.declarative.annotations.DefaultGraph;
import com.inrupt.rdf.wrapping.declarative.annotations.NamedGraph;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.apache.jena.query.Dataset;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetImpl;
import org.jboss.jdeparser.*;

class DatasetImplementor extends Implementor {
    DatasetImplementor(final ProcessingEnvironment processingEnvironment, final TypeElement annotatedElement) {
        super(processingEnvironment, annotatedElement);
    }

    @Override
    protected void implementInternal() {
        // Imports
        sourceFile
                ._import(Generated.class)
                ._import(Dataset.class)
                ._import(DatasetGraph.class)
                ._import(DatasetImpl.class);

        // Class
        final JClassDef implementation = sourceFile._class(PUBLIC, implementationClass)
                ._extends(DatasetImpl.class)
                ._implements(originalInterface);

        // @Generated & Javadocs
        annotateAndDocumentAsGenerated(implementation);

        // Constructor
        final JMethodDef constructor = implementation.constructor(PROTECTED);
        constructor.param(FINAL, DatasetGraph.class, "original");
        constructor.body().callSuper().arg($v("original"));

        // Wrap method
        final JMethodDef wrapMethod = implementation.method(PUBLIC | STATIC, originalInterface, "wrap");
        wrapMethod.param(FINAL, Dataset.class, "original");
        wrapMethod.body()._return($t(implementationClass)._new().arg($v("original").call("asDatasetGraph")));

        // @DefaultGraph
        ElementFilter.methodsIn(annotatedElement.getEnclosedElements()).stream()
                .filter(method -> !method.isDefault() && !method.getModifiers().contains(Modifier.STATIC))
                .filter(method -> method.getAnnotation(DefaultGraph.class) != null)
                .forEach(method -> {
                    final JType returnType = JTypes.typeOf(method.getReturnType());
                    sourceFile._import(returnType);
                    final JMethodDef defaultGraphMethod = implementation.method(
                            PUBLIC,
                            returnType,
                            method.getSimpleName().toString());
                    defaultGraphMethod.annotate(Override.class);
                    defaultGraphMethod
                            .body()
                            ._return(returnType
                                    .call("wrap")
                                    .arg($t(implementationClass)
                                            ._this()
                                            .call("getDefaultModel")));
                });

        // @NamedGraph
        ElementFilter.methodsIn(annotatedElement.getEnclosedElements()).stream()
                .filter(method -> !method.isDefault() && !method.getModifiers().contains(Modifier.STATIC))
                .filter(method -> method.getAnnotation(NamedGraph.class) != null)
                .forEach(method -> {
                    final JType returnType = JTypes.typeOf(method.getReturnType());
                    sourceFile._import(returnType);
                    final JMethodDef namedGraphMethod = implementation
                            .method(PUBLIC, returnType, method.getSimpleName().toString());
                    namedGraphMethod.annotate(Override.class);
                    namedGraphMethod
                            .body()
                            ._return(returnType
                                    .call("wrap")
                                    .arg($t(implementationClass)
                                            ._this()
                                            .call("getNamedModel")
                                            .arg(JExprs.str(method.getAnnotation(NamedGraph.class).value()))));
                });
    }
}
