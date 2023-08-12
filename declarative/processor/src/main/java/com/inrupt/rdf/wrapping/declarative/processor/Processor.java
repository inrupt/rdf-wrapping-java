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
import static org.jboss.jdeparser.JTypes.$t;

import com.inrupt.rdf.wrapping.jena.UriOrBlankFactory;
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
import javax.lang.model.element.TypeElement;
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

        for (TypeElement annotation : annotations) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    String.format("annotation [%s]", annotation), annotation);

            final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element annotatedElement : annotatedElements) {
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
                String packageName = null;
                if (lastDot > 0) {
                    packageName = originalBinaryName.substring(0, lastDot);
                }

                final JFiler jFiler = JFiler.newInstance(processingEnv.getFiler());
                final JSources sources = JDeparser.createSources(jFiler, new FormatPreferences());
                final JSourceFile sourceFile = sources.createSourceFile(packageName, implementationClassName);

                switch (annotation.getQualifiedName().toString()) {
                    case "com.inrupt.rdf.wrapping.declarative.annotations.Dataset":
                        printDataset(originalInterfaceName, implementationClassName, sourceFile);
                        break;
                    case "com.inrupt.rdf.wrapping.declarative.annotations.Graph":
                        printGraph(originalInterfaceName, implementationClassName, sourceFile);
                        break;
                    case "com.inrupt.rdf.wrapping.declarative.annotations.Resource":
                        printResource(originalInterfaceName, implementationClassName, sourceFile);
                        break;
                }

                try {
                    sources.writeSources();
                } catch (IOException e) {
                    throw new RuntimeException("could not open writer", e);
                }
            }
        }

        return true;
    }

    private void printDataset(
            final String originalInterfaceName,
            final String implementationClassName,
            final JSourceFile sourceFile) {
        sourceFile._import(Generated.class);
        sourceFile._import(Dataset.class);
        sourceFile._import(DatasetGraph.class);
        sourceFile._import(DatasetImpl.class);

        final JClassDef jClassDef = sourceFile._class(JMod.PUBLIC, implementationClassName);
        jClassDef.annotate(Generated.class).value(this.getClass().getName()).value("date", Instant.now().toString());
        jClassDef.docComment().text("Warning, this class consists of generated code.");
        jClassDef._extends(DatasetImpl.class)._implements(originalInterfaceName);

        final JMethodDef constructor = jClassDef.constructor(JMod.PROTECTED);
        constructor.param(JMod.FINAL, DatasetGraph.class, "original");
        constructor.body().callSuper().arg($v("original"));

        final JMethodDef wrap = jClassDef.method(JMod.PUBLIC | JMod.STATIC, originalInterfaceName, "wrap");
        wrap.param(JMod.FINAL, Dataset.class, "original");
        wrap.body()._return($t(implementationClassName)._new().arg($v("original").call("asDatasetGraph")));
    }

    private void printGraph(
            final String originalInterfaceName,
            final String implementationClassName,
            final JSourceFile sourceFile) {
        sourceFile._import(Generated.class);
        sourceFile._import(Graph.class);
        sourceFile._import(Model.class);
        sourceFile._import(ModelCom.class);

        final JClassDef jClassDef = sourceFile._class(JMod.PUBLIC, implementationClassName);
        jClassDef.annotate(Generated.class).value(this.getClass().getName()).value("date", Instant.now().toString());
        jClassDef.docComment().text("Warning, this class consists of generated code.");
        jClassDef._extends(ModelCom.class)._implements(originalInterfaceName);

        final JMethodDef constructor = jClassDef.constructor(JMod.PROTECTED);
        constructor.param(JMod.FINAL, Graph.class, "original");
        constructor.body().callSuper().arg($v("original"));

        final JMethodDef wrap = jClassDef.method(JMod.PUBLIC | JMod.STATIC, originalInterfaceName, "wrap");
        wrap.param(JMod.FINAL, Model.class, "original");
        wrap.body()._return($t(implementationClassName)._new().arg($v("original").call("getGraph")));
    }

    private void printResource(
            final String originalInterfaceName,
            final String implementationClassName,
            final JSourceFile sourceFile) {
        sourceFile._import(UriOrBlankFactory.class);
        sourceFile._import(WrapperResource.class);
        sourceFile._import(Generated.class);
        sourceFile._import(EnhGraph.class);
        sourceFile._import(Implementation.class);
        sourceFile._import(Node.class);

        final JClassDef jClassDef = sourceFile._class(JMod.PUBLIC, implementationClassName);
        jClassDef.annotate(Generated.class).value(this.getClass().getName()).value("date", Instant.now().toString());
        jClassDef.docComment().text("Warning, this class consists of generated code.");
        jClassDef._extends(WrapperResource.class)._implements(originalInterfaceName);

        jClassDef.field(JMod.STATIC | JMod.FINAL, Implementation.class, "factory", $t(UriOrBlankFactory.class)._new().arg($t(implementationClassName).methodRef("new")));
        final JMethodDef constructor = jClassDef.constructor(JMod.PROTECTED);
        constructor.param(JMod.FINAL, Node.class, "node");
        constructor.param(JMod.FINAL, EnhGraph.class, "graph");
        final JCall jCall = constructor.body().callSuper();
        jCall.arg($v("node"));
        jCall.arg($v("graph"));
    }
}
