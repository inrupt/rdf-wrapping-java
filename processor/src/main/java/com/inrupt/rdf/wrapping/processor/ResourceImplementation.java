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

import static org.jboss.jdeparser.JExprs.*;
import static org.jboss.jdeparser.JMod.*;
import static org.jboss.jdeparser.JTypes.$t;

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.jena.UriOrBlankFactory;
import com.inrupt.rdf.wrapping.jena.ValueMappings;
import com.inrupt.rdf.wrapping.jena.WrapperResource;

import javax.annotation.Generated;
import javax.lang.model.element.ExecutableElement;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;
import org.jboss.jdeparser.*;

class ResourceImplementation extends com.inrupt.rdf.wrapping.processor.Implementation {
    static final String FACTORY = "factory";

    ResourceImplementation(final Environment env) {
        super(env);
    }

    void addImports(final JSourceFile source) {
        source
                ._import(UriOrBlankFactory.class)
                ._import(WrapperResource.class)
                ._import(Generated.class)
                ._import(EnhGraph.class)
                ._import(Implementation.class)
                ._import(Node.class)
                ._import(ValueMappings.class);
    }

    void addClass(final JSourceFile source, final String name, final JType originalInterface) {
        addClass(source, name, originalInterface, WrapperResource.class);
    }

    void addFactoryField() {
        target.field(
                STATIC | FINAL,
                Implementation.class,
                FACTORY,
                $t(UriOrBlankFactory.class)._new().arg($t(target).methodRef("new")));
    }

    void addConstructor() {
        final String node = "node";
        final String graph = "graph";

        final JMethodDef myConstructor = target.constructor(PROTECTED);
        myConstructor.param(FINAL, Node.class, node);
        myConstructor.param(FINAL, EnhGraph.class, graph);
        myConstructor.body().callSuper().arg($v(node)).arg($v(graph));
    }

    void addPrimitivePropertyMethod(final ExecutableElement method) {
        final String mappingMethodName = method.getAnnotation(Property.class).mapping().getMethodName();
        final JExpr mapping = $t(ValueMappings.class).methodRef(mappingMethodName);

        addPropertyMethod(method, mapping);
    }

    void addResourcePropertyMethod(final ExecutableElement method) {
        final JType implementation = asImplementation(method.getReturnType());
        final JCall mapping = $t(ValueMappings.class).call("as").arg(implementation._class());

        addPropertyMethod(method, mapping);
    }

    private void addPropertyMethod(final ExecutableElement method, final JExpr mapping) {
        final String predicateFromAnnotation = method.getAnnotation(Property.class).predicate();
        final JCall predicate = call("getModel").call("createProperty").arg(str(predicateFromAnnotation));

        // TODO: Dynamic cardniality
        addMethod(method).body()._return(call("anyOrNull").arg(predicate).arg(mapping));
    }
}
