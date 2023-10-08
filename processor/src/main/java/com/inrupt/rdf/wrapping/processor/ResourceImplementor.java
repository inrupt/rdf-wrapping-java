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

import static org.jboss.jdeparser.JExpr.THIS;
import static org.jboss.jdeparser.JExprs.$v;
import static org.jboss.jdeparser.JExprs.str;
import static org.jboss.jdeparser.JMod.*;
import static org.jboss.jdeparser.JTypes.$t;

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.jena.UriOrBlankFactory;
import com.inrupt.rdf.wrapping.jena.ValueMappings;
import com.inrupt.rdf.wrapping.jena.WrapperResource;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;
import org.jboss.jdeparser.*;

class ResourceImplementor extends Implementor {
    static final String FACTORY = "factory";

    ResourceImplementor(final ProcessingEnvironment environment, final Element element) {
        super(environment, element);
    }

    @Override
    protected void implementInternal() {
        addImports();

        final JClassDef myClass = createClass(WrapperResource.class);
        final JType myType = $t(myClass);

        createFactoryField(myClass, myType);

        createConstructor(myClass);

        createPropertyMethods(myClass);
    }

    private void addImports() {
        sourceFile
                ._import(UriOrBlankFactory.class)
                ._import(WrapperResource.class)
                ._import(Generated.class)
                ._import(EnhGraph.class)
                ._import(Implementation.class)
                ._import(Node.class)
                ._import(ValueMappings.class);
    }

    private static void createFactoryField(final JClassDef myClass, final JType myType) {
        myClass.field(
                STATIC | FINAL,
                Implementation.class,
                FACTORY,
                $t(UriOrBlankFactory.class)._new().arg(myType.methodRef("new")));
    }

    private static void createConstructor(final JClassDef myClass) {
        final JMethodDef myConstructor = myClass.constructor(PROTECTED);
        myConstructor.param(FINAL, Node.class, "node");
        myConstructor.param(FINAL, EnhGraph.class, "graph");
        myConstructor.body().callSuper().arg($v("node")).arg($v("graph"));
    }

    private void createPropertyMethods(final JClassDef myClass) {
        membersAnnotatedWithAny(Property.class).forEach(method -> {
            final JType returnType = JTypes.typeOf(method.getReturnType());

            final JMethodDef myMethod = myClass.method(PUBLIC, returnType, method.getSimpleName().toString());
            myMethod.annotate(Override.class);

            final String mappingMethodName = method.getAnnotation(Property.class).mapping().getMethodName();
            final JExpr mapping = $t(ValueMappings.class).methodRef(mappingMethodName);
            final String predicateFromAnnotation = method.getAnnotation(Property.class).predicate();
            final JCall predicate = THIS
                    .call("getModel")
                    .call("createProperty")
                    .arg(str(predicateFromAnnotation));

            myMethod.body()._return(
                    THIS
                            .call("anyOrNull") // TODO: Dynamic cardniality
                            .arg(predicate)
                            .arg(mapping));
        });
    }
}
