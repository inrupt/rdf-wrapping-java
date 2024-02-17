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

import static com.inrupt.rdf.wrapping.processor.Implementation.asImplementation;
import static org.jboss.jdeparser.JTypes.typeOf;

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.Graph;

import java.io.IOException;

import javax.lang.model.element.TypeElement;

import org.jboss.jdeparser.*;

abstract class Implementor<T extends Interface, U extends Implementation> {
    protected final JSources sources;
    protected final JType originalInterface;
    protected final JSourceFile sourceFile;
    protected final String implementationClass;
    protected final T myInterface;
    protected final U myClass;

    protected Implementor(final T myInterface, final U myClass) {
        this.myInterface = myInterface;
        this.myClass = myClass;

        final TypeElement type = myInterface.getType();
        final Environment env = myInterface.getEnv();

        originalInterface = typeOf(type.asType());
        final String originalBinaryName = env.getElementUtils().getBinaryName(type).toString();
        final String qualifiedName = asImplementation(originalBinaryName);
        final int lastDot = originalBinaryName.lastIndexOf('.');
        implementationClass = qualifiedName.substring(lastDot + 1);
        final String packageName = env.getElementUtils().getPackageOf(type).getQualifiedName().toString();

        final JFiler filer = JFiler.newInstance(env.getFiler());
        sources = JDeparser.createSources(filer, new FormatPreferences());
        sourceFile = sources.createSourceFile(packageName, implementationClass);
    }

    void implement() {
        implementInternal();

        try {
            sources.writeSources();
        } catch (IOException e) {
            throw new RuntimeException("could not open writer", e);
        }
    }

    protected abstract void implementInternal();

    static Implementor<? extends Interface, ? extends Implementation> implementor(final Environment env, final TypeElement e) {
        if (e.getAnnotation(Dataset.class) != null) {
            return new DatasetImplementor(e, env);

        } else if (e.getAnnotation(Graph.class) != null) {
            return new GraphImplementor(e, env);

        } else { // Resource
            // Processor's supported annotations are finite
            return new ResourceImplementor(e, env);
        }
    }
}
