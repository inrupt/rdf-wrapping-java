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

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

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

                final JavaFileObject builderFile;
                try {
                    builderFile = processingEnv.getFiler().createSourceFile(qualifiedName, annotatedElement);
                } catch (IOException e) {
                    throw new RuntimeException("could not create class file", e);
                }

                try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                    // This should surely be a framework like JDeparser
                    if (packageName != null) {
                        out.print("package ");
                        out.print(packageName);
                        out.println(";");
                        out.println();
                    }

                    switch (annotation.getQualifiedName().toString()) {
                        case "com.inrupt.rdf.wrapping.declarative.annotations.Dataset":
                            printDataset(originalInterfaceName, implementationClassName, out);
                            break;
                        case "com.inrupt.rdf.wrapping.declarative.annotations.Graph":
                            printGraph(originalInterfaceName, implementationClassName, out);
                            break;
                        case "com.inrupt.rdf.wrapping.declarative.annotations.Resource":
                            printResource(originalInterfaceName, implementationClassName, out);
                            break;

                    }


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
            final PrintWriter out) {
        out.println("import javax.annotation.Generated;");
        out.println();
        out.println("import org.apache.jena.query.Dataset;");
        out.println("import org.apache.jena.sparql.core.DatasetGraph;");
        out.println("import org.apache.jena.sparql.core.DatasetImpl;");
        out.println();

        printJavadoc(out);
        printGenerated(out);

        out.print("public class ");
        out.print(implementationClassName);
        out.print(" extends DatasetImpl implements ");
        out.print(originalInterfaceName);
        out.println(" {");

        out.print("    protected ");
        out.print(implementationClassName);
        out.println("(final DatasetGraph original) {");
        out.println("        super(original);");
        out.println("    }");
        out.println();

        out.print("    public static ");
        out.print(originalInterfaceName);
        out.println(" wrap(final Dataset original) {");
        out.print("        return new ");
        out.print(implementationClassName);
        out.println("(original.asDatasetGraph());");
        out.println("    }");

        out.println("}");
    }

    private void printGraph(
            final String originalInterfaceName,
            final String implementationClassName,
            final PrintWriter out) {
        out.println("import javax.annotation.Generated;");
        out.println();
        out.println("import org.apache.jena.graph.Graph;");
        out.println("import org.apache.jena.rdf.model.Model;");
        out.println("import org.apache.jena.rdf.model.impl.ModelCom;");
        out.println();

        printJavadoc(out);
        printGenerated(out);

        out.print("public class ");
        out.print(implementationClassName);
        out.print(" extends ModelCom implements ");
        out.print(originalInterfaceName);
        out.println(" {");

        out.print("    protected ");
        out.print(implementationClassName);
        out.println("(final Graph original) {");
        out.println("        super(original);");
        out.println("    }");
        out.println();

        out.print("    public static ");
        out.print(originalInterfaceName);
        out.println(" wrap(final Model original) {");
        out.print("        return new ");
        out.print(implementationClassName);
        out.println("(original.getGraph());");
        out.println("    }");

        out.println("}");
    }

    private void printResource(
            final String originalInterfaceName,
            final String implementationClassName,
            final PrintWriter out) {
        out.println("import com.inrupt.rdf.wrapping.jena.UriOrBlankFactory;");
        out.println("import com.inrupt.rdf.wrapping.jena.WrapperResource;");
        out.println();
        out.println("import javax.annotation.Generated;");
        out.println();
        out.println("import org.apache.jena.enhanced.EnhGraph;");
        out.println("import org.apache.jena.enhanced.Implementation;");
        out.println("import org.apache.jena.graph.Node;");
        out.println();

        printJavadoc(out);
        printGenerated(out);

        out.print("public class ");
        out.print(implementationClassName);
        out.print(" extends WrapperResource implements ");
        out.print(originalInterfaceName);
        out.println(" {");

        out.print("    static final Implementation factory = new UriOrBlankFactory(");
        out.print(implementationClassName);
        out.println("::new);");
        out.println();

        out.print("    protected ");
        out.print(implementationClassName);
        out.println("(final Node node, final EnhGraph graph) {");
        out.println("        super(node, graph);");
        out.println("    }");

        out.println("}");
    }

    private void printGenerated(final PrintWriter out) {
        out.print("@Generated(value = \"");
        out.print(this.getClass().getName());
        out.print("\", date = \"");
        out.print(Instant.now());
        out.println("\")");
    }

    private static void printJavadoc(final PrintWriter out) {
        out.println("/**");
        out.println(" * Warning this class consists of generated code.");
        out.println(" */");
    }
}
