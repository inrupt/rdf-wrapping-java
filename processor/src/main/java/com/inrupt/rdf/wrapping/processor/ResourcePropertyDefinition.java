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

import com.inrupt.rdf.wrapping.annotation.ResourceProperty;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty.NodeMapping;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty.ValueMapping;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.DeclaredType;

class ResourcePropertyDefinition extends PropertyDefinition<ResourceProperty> {
    ResourcePropertyDefinition(final ExecutableElement element, final Environment env) {
        super(element, env, ResourceProperty.class);
    }

    String predicate() {
        return annotation().value();
    }

    // TODO: Rename
    Cardinality cardinality() {
        return annotation().cardinality();
    }

    // TODO: Rename
    String cardinalityMethod() {
        return cardinality().getMethodName();
    }

    ValueMapping valueMapping() {
        return annotation().valueMapping();
    }

    String valueMappingMethod() {
        return valueMapping().getMethodName();
    }

    NodeMapping nodeMapping() {
        return annotation().nodeMapping();
    }

    String nodeMappingMethod() {
        return nodeMapping().getMethodName();
    }

    Name getName() {
        return getElement().getSimpleName();
    }

    DeclaredType getValueParamType() {
        return (DeclaredType) getElement().getParameters().get(0).asType();
    }
}
