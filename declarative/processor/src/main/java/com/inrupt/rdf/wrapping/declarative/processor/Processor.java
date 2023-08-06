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
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("annotation [%s]", annotation), annotation);

            final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element annotatedElement : annotatedElements) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("annotatedElement [%s]", annotatedElement), annotatedElement);

                final TypeElement annotatedType = (TypeElement) annotatedElement;
                final String originalInterfaceName = annotatedType.getQualifiedName().toString();
                final String originalBinaryName = processingEnv.getElementUtils().getBinaryName(annotatedType).toString();
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

                    out.println("import javax.annotation.processing.Generated;");
                    out.println();

                    out.println("/**");
                    out.println(" * Warning this class consists of generated code.");
                    out.println(" */");

                    out.print("@Generated(value = \"");
                    out.print(this.getClass().getName());
                    out.print("\", date = \"");
                    out.print(Instant.now());
                    out.println("\")");

                    out.print("public class ");
                    out.print(implementationClassName);
                    out.print(" implements ");
                    out.print(originalInterfaceName);
                    out.println(" {}");
                } catch (IOException e) {
                    throw new RuntimeException("could not open writer", e);
                }
            }
        }

        return true;
    }
}
