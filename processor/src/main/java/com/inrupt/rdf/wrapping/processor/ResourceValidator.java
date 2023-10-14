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

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.jena.ValueMappings;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.apache.jena.rdf.model.Resource;

class ResourceValidator extends Validator {
    ResourceValidator(final TypeElement annotation, final ProcessingEnvironment env, final Element element) {
        super(annotation, env, element);
    }

    @Override
    protected void validateInternal() {
        requireInterface();

        limitBaseInterfaces(Resource.class);

        requireMemberMethods(Property.class);

        requireNonMemberMethods(Property.class);

        requireCompatibleReturnType();
    }

    private void requireCompatibleReturnType() {
        for (final ExecutableElement method : env.methodsOf(element)) {
            final Property propertyAnnotation = method.getAnnotation(Property.class);
            if (propertyAnnotation != null) {
                // TODO: Validate otherwise
                if (propertyAnnotation.mapping() == Property.Mapping.AS){
                    continue;
                }

                final String mappingMethod = propertyAnnotation.mapping().getMethodName();
                final TypeMirror mappingMethodReturnType = returnTypeOfMapper(mappingMethod);

                if (!env.getTypeUtils().isAssignable(mappingMethodReturnType, method.getReturnType())) {
                    errors.add(new ValidationError(
                            method,
                            "Return type [%s] of [%s] must be assignable from return type [%s] of mapping [%s]",
                            method.getReturnType(),
                            method.getSimpleName(),
                            mappingMethodReturnType,
                            mappingMethod));
                }
            }
        }
    }

    private TypeMirror returnTypeOfMapper(final String mappingMethod) {
        for (final ExecutableElement m : env.methodsOf(ValueMappings.class)) {
            if (m.getSimpleName().contentEquals(mappingMethod)) {
                return m.getReturnType();
            }
        }

        return null;
    }
}
