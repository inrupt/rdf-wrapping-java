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

import javax.lang.model.element.TypeElement;
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
            final JExpr mapping = typeOf(NodeMappings.class).methodRef(p.nodeMappingMethod());

            final JMethodDef method = addMethod(p);
            final JParamDeclaration valueParam = method.param(FINAL, typeOf(p.getValueParamType()), "value");

            // Mutator method implementations for complex properties must cast value parameter to node so it works
            // with identity node mapping.
            // TODO: What happens with other node mapping methods?
            final TypeElement declaration = definition.getEnv().findDeclaration(p.getValueParamType());
            final JExpr value = declaration.getAnnotation(Resource.class) != null ?
                    name(valueParam).cast(RDFNode.class) :
                    name(valueParam);

            final JCall call = method.body().call(THIS._super(), p.cardinalityMethod());

            // Plural mutator method implementations must specify type argument of cardinality method to assist type
            // inference.
            p.getValueParamType().getTypeArguments().stream()
                    .findFirst()
                    .map(JTypes::typeOf)
                    .ifPresent(call::typeArg);

            call.arg(getProperty(p)).arg(value).arg(mapping);
        });
    }

    private void addComplex(final ResourcePropertyDefinition definition, final TypeMirror type) {
        final JType implementation = asImplementation(type);
        final JCall mapping = typeOf(ValueMappings.class).call("as").arg(implementation._class());

        addPropertyMethod(definition, mapping);
    }

    private void addPropertyMethod(final ResourcePropertyDefinition p, final JExpr mapping) {
        addMethod(p).body()._return(THIS._super().call(p.cardinalityMethod()).arg(getProperty(p)).arg(mapping));
    }

    private static JCall getProperty(final ResourcePropertyDefinition d) {
        return THIS._super().call("getModel").call("getProperty").arg(str(d.predicate()));
    }
}
