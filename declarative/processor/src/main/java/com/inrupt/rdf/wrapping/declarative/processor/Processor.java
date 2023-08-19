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
import com.inrupt.rdf.wrapping.declarative.annotations.FirstInstanceOf;
import com.inrupt.rdf.wrapping.declarative.annotations.NamedGraph;
import com.inrupt.rdf.wrapping.jena.UriOrBlankFactory;
import com.inrupt.rdf.wrapping.jena.WrapperModel;
import com.inrupt.rdf.wrapping.jena.WrapperResource;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetImpl;
import org.jboss.jdeparser.*;

@SupportedAnnotationTypes({
    "com.inrupt.rdf.wrapping.declarative.annotations.Dataset",
    "com.inrupt.rdf.wrapping.declarative.annotations.Graph",
    "com.inrupt.rdf.wrapping.declarative.annotations.Resource"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "processing over");
            return false;
        }

        for (final TypeElement annotation : annotations) {
            process(roundEnv, annotation);
        }

        return true;
    }

    private void process(final RoundEnvironment roundEnv, final TypeElement annotation) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                String.format("annotation [%s]", annotation), annotation);

        for (final Element annotatedElement : roundEnv.getElementsAnnotatedWith(annotation)) {
            process(annotation, annotatedElement);
        }
    }

    private void process(final TypeElement annotation, final Element annotatedElement) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                String.format("annotatedElement [%s]", annotatedElement), annotatedElement);

        final TypeElement annotatedType = (TypeElement) annotatedElement;
        final String originalInterfaceName = annotatedType.getQualifiedName().toString();
        final String originalBinaryName = processingEnv
                .getElementUtils()
                .getBinaryName(annotatedType)
                .toString();
        final String qualifiedName = originalBinaryName + "_$impl";
        final int lastDot = originalBinaryName.lastIndexOf('.');
        final String implementationClassName = qualifiedName.substring(lastDot + 1);
        final String packageName = processingEnv.getElementUtils().getPackageOf(annotatedElement).getQualifiedName().toString();

        final JFiler jFiler = JFiler.newInstance(processingEnv.getFiler());
        final JSources sources = JDeparser.createSources(jFiler, new FormatPreferences());
        final JSourceFile sourceFile = sources.createSourceFile(packageName, implementationClassName);

        switch (annotation.getQualifiedName().toString()) {
            case "com.inrupt.rdf.wrapping.declarative.annotations.Dataset":
                implementDataset(originalInterfaceName, implementationClassName, sourceFile, annotatedType);
                break;
            case "com.inrupt.rdf.wrapping.declarative.annotations.Graph":
                implementGraph(originalInterfaceName, implementationClassName, sourceFile, annotatedType);
                break;
            case "com.inrupt.rdf.wrapping.declarative.annotations.Resource":
                implementResource(originalInterfaceName, implementationClassName, sourceFile);
                break;
            default:
                throw new RuntimeException("unknown annotation type");
        }

        try {
            sources.writeSources();
        } catch (IOException e) {
            throw new RuntimeException("could not open writer", e);
        }
    }

    private void implementDataset(
            final String originalInterface,
            final String implementationClass,
            final JSourceFile sourceFile,
            final TypeElement annotatedType) {

        sourceFile
                ._import(Generated.class)
                ._import(Dataset.class)
                ._import(DatasetGraph.class)
                ._import(DatasetImpl.class);

        final JClassDef implementation = sourceFile._class(PUBLIC, implementationClass)
                ._extends(DatasetImpl.class)
                ._implements(originalInterface);

        annotateAndDocumentAsGenerated(implementation);

        final JMethodDef constructor = implementation.constructor(PROTECTED);
        constructor.param(FINAL, DatasetGraph.class, "original");
        constructor.body().callSuper().arg($v("original"));

        final JMethodDef wrapMethod = implementation.method(PUBLIC | STATIC, originalInterface, "wrap");
        wrapMethod.param(FINAL, Dataset.class, "original");
        wrapMethod.body()._return($t(implementationClass)._new().arg($v("original").call("asDatasetGraph")));

        ElementFilter.methodsIn(annotatedType.getEnclosedElements()).stream()
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

        ElementFilter.methodsIn(annotatedType.getEnclosedElements()).stream()
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

    private void implementGraph(
            final String originalInterface,
            final String implementationClass,
            final JSourceFile sourceFile,
            final TypeElement annotatedType) {

        sourceFile
                ._import(WrapperModel.class)
                ._import(Generated.class)
                ._import(Graph.class)
                ._import(Model.class)
                ._import(ModelCom.class);

        final JClassDef implementation = sourceFile
                ._class(PUBLIC, implementationClass)
                ._extends(WrapperModel.class)
                ._implements(originalInterface);

        annotateAndDocumentAsGenerated(implementation);

        final JMethodDef constructor = implementation.constructor(PROTECTED);
        constructor.param(FINAL, Graph.class, "original");
        constructor.body().callSuper().arg($v("original"));

        ElementFilter.methodsIn(annotatedType.getEnclosedElements()).stream()
                .filter(method -> !method.isDefault() && !method.getModifiers().contains(Modifier.STATIC) && !method.getReturnType().equals($t(Void.class)))
                .filter(method -> method.getAnnotation(FirstInstanceOf.class) != null)
                .forEach(method -> {
                    final JType returnType = JTypes.typeOf(method.getReturnType());
                    final String originalBinaryName = processingEnv
                            .getElementUtils()
                            .getBinaryName((TypeElement) processingEnv.getTypeUtils().asElement(method.getReturnType()))
                            .toString();
                    final String qualifiedName = originalBinaryName + "_$impl";

                    sourceFile._import(returnType);
                    constructor.body().call($t(implementationClass)._this().call("getPersonality"), "add").arg($t(qualifiedName)._class()).arg($t(qualifiedName).field("factory"));
                });

        final JMethodDef wrapMethod = implementation.method(PUBLIC | STATIC, originalInterface, "wrap");
        wrapMethod.param(FINAL, Model.class, "original");
        wrapMethod.body()._return($t(implementationClass)._new().arg($v("original").call("getGraph")));

        ElementFilter.methodsIn(annotatedType.getEnclosedElements()).stream()
                .filter(method -> !method.isDefault() && !method.getModifiers().contains(Modifier.STATIC) && !method.getReturnType().equals($t(Void.class)))
                .filter(method -> method.getAnnotation(FirstInstanceOf.class) != null)
                .forEach(method -> {
                    final JType returnType = JTypes.typeOf(method.getReturnType());
                    final String originalBinaryName = processingEnv
                            .getElementUtils()
                            .getBinaryName((TypeElement) processingEnv.getTypeUtils().asElement(method.getReturnType()))
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

    private void implementResource(
            final String originalInterface,
            final String implementationClass,
            final JSourceFile sourceFile) {

        sourceFile
                ._import(UriOrBlankFactory.class)
                ._import(WrapperResource.class)
                ._import(Generated.class)
                ._import(EnhGraph.class)
                ._import(Implementation.class)
                ._import(Node.class);

        final JClassDef implementation = sourceFile._class(PUBLIC, implementationClass)
                ._extends(WrapperResource.class)
                ._implements(originalInterface);

        annotateAndDocumentAsGenerated(implementation);

        implementation.field(
                STATIC | FINAL,
                Implementation.class,
                "factory",
                $t(UriOrBlankFactory.class)._new().arg($t(implementationClass).methodRef("new")));

        final JMethodDef constructor = implementation.constructor(PROTECTED);
        constructor.param(FINAL, Node.class, "node");
        constructor.param(FINAL, EnhGraph.class, "graph");
        constructor.body().callSuper().arg($v("node")).arg($v("graph"));
    }

    private void annotateAndDocumentAsGenerated(final JClassDef implementation) {
        implementation.docComment().text("Warning, this class consists of generated code.");

        implementation
                .annotate(Generated.class)
                .value(this.getClass().getName()).value("date", Instant.now().toString());
    }
}
