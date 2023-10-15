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
import com.inrupt.rdf.wrapping.annotation.Resource;

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.jboss.jdeparser.*;

abstract class Implementor {
    protected final EnvironmentHelper environment;
    protected final TypeElement type;
    protected final JSources sources;
    protected final JType originalInterface;
    protected final JSourceFile sourceFile;

    protected final String implementationClass;

    protected Implementor(final ProcessingEnvironment environment, final Element element) {
        this.environment = new EnvironmentHelper(environment);
        type = (TypeElement) element;

        originalInterface = typeOf(type.asType());
        final String originalBinaryName = environment.getElementUtils().getBinaryName(type).toString();
        final String qualifiedName = asImplementation(originalBinaryName);
        final int lastDot = originalBinaryName.lastIndexOf('.');
        implementationClass = qualifiedName.substring(lastDot + 1);
        final String packageName = environment.getElementUtils().getPackageOf(element).getQualifiedName().toString();

        final JFiler filer = JFiler.newInstance(environment.getFiler());
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

    static Implementor get(final ProcessingEnvironment env, final Element element) {
        if (element.getAnnotation(Dataset.class) != null) {
            return new DatasetImplementor(env, element);
        } else if (element.getAnnotation(Graph.class) != null) {
            return new GraphImplementor(env, element);
        } else if (element.getAnnotation(Resource.class) != null) {
            return new ResourceImplementor(env, element);
        } else {
            throw new RuntimeException("unknown annotation type");
        }
    }
}
