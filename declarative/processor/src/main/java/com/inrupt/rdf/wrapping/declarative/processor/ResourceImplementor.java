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

import com.inrupt.rdf.wrapping.jena.UriOrBlankFactory;
import com.inrupt.rdf.wrapping.jena.WrapperResource;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;
import org.jboss.jdeparser.JClassDef;
import org.jboss.jdeparser.JMethodDef;

class ResourceImplementor extends Implementor {
    ResourceImplementor(final ProcessingEnvironment processingEnvironment, final TypeElement annotatedElement) {
        super(processingEnvironment, annotatedElement);
    }

    @Override
    protected void implementInternal() {
        // Imports
        sourceFile
                ._import(UriOrBlankFactory.class)
                ._import(WrapperResource.class)
                ._import(Generated.class)
                ._import(EnhGraph.class)
                ._import(Implementation.class)
                ._import(Node.class);

        // Class
        final JClassDef implementation = sourceFile._class(PUBLIC, implementationClass)
                ._extends(WrapperResource.class)
                ._implements(originalInterface);

        // @Generated & Javadocs
        annotateAndDocumentAsGenerated(implementation);

        // Factory
        implementation.field(
                STATIC | FINAL,
                Implementation.class,
                "factory",
                $t(UriOrBlankFactory.class)._new().arg($t(implementationClass).methodRef("new")));

        // Constructor
        final JMethodDef constructor = implementation.constructor(PROTECTED);
        constructor.param(FINAL, Node.class, "node");
        constructor.param(FINAL, EnhGraph.class, "graph");
        constructor.body().callSuper().arg($v("node")).arg($v("graph"));
    }
}
