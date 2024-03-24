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
import static org.jboss.jdeparser.JType.THIS;
import static org.jboss.jdeparser.JTypes.typeOf;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.jena.NodeMappings;
import com.inrupt.rdf.wrapping.jena.UriOrBlankFactory;
import com.inrupt.rdf.wrapping.jena.ValueMappings;
import com.inrupt.rdf.wrapping.jena.WrapperResource;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.RDFNode;
import org.jboss.jdeparser.*;

class ResourceImplementor extends Implementor<ResourceDefinition> {
    static final String FACTORY = "factory";

    ResourceImplementor(final ResourceDefinition definition) {
        super(definition);
    }

    @Override
    protected void implementInternal() {
        addClass();
        addFactoryField();
        addConstructor();
        addPrimitivePropertyMethods();
        addResourcePropertyMethods();
        addMutators();
        addComplexPlural();
    }

    private void addClass() {
        addClass(WrapperResource.class);
    }

    private void addFactoryField() {
        target.field(
                PUBLIC | STATIC | FINAL,
                org.apache.jena.enhanced.Implementation.class,
                FACTORY,
                typeOf(UriOrBlankFactory.class)._new().arg(typeOf(target).methodRef("new")));
    }

    private void addConstructor() {
        final String node = "node";
        final String graph = "graph";

        final JMethodDef myConstructor = target.constructor(PROTECTED);
        myConstructor.param(FINAL, Node.class, node);
        myConstructor.param(FINAL, EnhGraph.class, graph);
        myConstructor.body().callSuper().arg($v(node)).arg($v(graph));
    }

    private void addPrimitivePropertyMethods() {
        definition.primitiveProperties().forEach(p -> {
            final JExpr mapping = typeOf(ValueMappings.class).methodRef(p.valueMappingMethod());

            addPropertyMethod(p, mapping);
        });
    }

    private void addResourcePropertyMethods() {
        definition.resourceProperties().forEach(p ->
                addComplex(p, p.getReturnType()));
    }

    // TODO: Cover
    private void addComplexPlural() {
        definition.complexPlurals().forEach(p -> {
            final DeclaredType thisReturn = (DeclaredType) p.getReturnType();

            final TypeMirror typeArg = thisReturn.getTypeArguments().stream()
                    .map(WildcardType.class::cast) // safe due to validator
                    .map(WildcardType::getExtendsBound)
                    .findFirst()
                    .get(); // safe due to validator

            addComplex(p, typeArg);
        });
    }

    // TODO: Validate
    // TODO: Cover
    private void addMutators() {
        definition.setterProperties().forEach(p -> {
            final JCall predicate = call("getModel").call("createProperty").arg(str(p.predicate()));

            final JExpr mapping = typeOf(NodeMappings.class).methodRef(p.nodeMappingMethod());

            final JMethodDef m = addMethod(p);
            final DeclaredType valueArgType = (DeclaredType) p.getValueParamType();
            final JParamDeclaration value = m.param(FINAL, typeOf(valueArgType), "value");

            final JExpr valueExpr;
            if (definition.typeOf(valueArgType).getAnnotation(Resource.class) != null) {
                valueExpr = name(value).cast(RDFNode.class);
            } else {
                valueExpr = name(value);
            }

            final JCall call = m.body().call(THIS._super(), p.cardinalityMethod());

            valueArgType.getTypeArguments().stream()
                    .findFirst()
                    .ifPresent(typeMirror ->
                            call.typeArg(typeOf(typeMirror)));

            call.arg(predicate).arg(valueExpr).arg(mapping);
        });
    }

    private void addComplex(final ResourcePropertyDefinition definition, final TypeMirror type) {
        final JType implementation = asImplementation(type);
        final JCall mapping = typeOf(ValueMappings.class).call("as").arg(implementation._class());

        addPropertyMethod(definition, mapping);
    }

    private void addPropertyMethod(final ResourcePropertyDefinition p, final JExpr mapping) {
        final JCall predicate = THIS._super().call("getModel").call("createProperty").arg(str(p.predicate()));

        addMethod(p).body()._return(call(p.cardinalityMethod()).arg(predicate).arg(mapping));
    }
}
