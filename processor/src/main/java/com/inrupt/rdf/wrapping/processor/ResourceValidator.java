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

import static com.inrupt.rdf.wrapping.processor.PredicateShim.not;
import static com.inrupt.rdf.wrapping.processor.ResourceDefinition.isSetter;
import static com.inrupt.rdf.wrapping.processor.ResourceDefinition.isVoid;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;
import com.inrupt.rdf.wrapping.jena.ValueMappings;
import com.inrupt.rdf.wrapping.jena.WrapperResource;

import java.util.Objects;
import java.util.stream.Stream;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

class ResourceValidator extends Validator<ResourceDefinition> {
    ResourceValidator(final ResourceDefinition definition) {
        super(definition);
    }

    @Override
    protected void validateInternal() {
        requireInterface();

        // TODO: Allow definitions to extend WrapperResource. That means interface must be extracted from
        //  WrapperResource so definition can extend it. Also allow definitions to extend other definitions
        //  (interfaces annotated with @Resource). That requires implementor to follow suit. Both above apply to
        //  dataset and graph definitions as well
        limitBaseInterfaces(org.apache.jena.rdf.model.Resource.class);

        requireMemberMethods(ResourceProperty.class);

        requireNonMemberMethods(ResourceProperty.class);

        requireNonVoidReturnType();
        requireCompatiblePrimitiveReturnType();
        requireCompatibleComplexReturnType();

        requirePluralErasure();
        requirePluralTypeArgument();
        // TODO: plural complex
    }

    private void requireNonVoidReturnType() {
        definition.properties()
                .filter(not(isSetter))
                .filter(isVoid)
                .forEach(method ->
                        errors.add(new ValidationError(
                                method.element,
                                "Method [%s] must not be void",
                                method.getName())));
    }

    private void requireCompatiblePrimitiveReturnType() {
        definition.primitiveMappingProperties().forEach(p -> {
            final TypeMirror mappingReturn = returnTypeOf(p.valueMappingMethod(), ValueMappings.class);

            if (definition.getEnv().getTypeUtils().isAssignable(mappingReturn, p.getReturnType())) {
                return;
            }

            errors.add(new ValidationError(
                    p.element,
                    "Return type [%s] of [%s] must be assignable from return type [%s] of mapping [%s]",
                    p.getReturnType(),
                    p.getName(),
                    mappingReturn,
                    p.valueMappingMethod()));
        });
    }

    private void requireCompatibleComplexReturnType() {
        definition.complexMappingProperties().forEach(p -> {
            final TypeElement returnType = definition.returnTypeOf(p.element);

            if (returnType != null && returnType.getAnnotation(Resource.class) != null) {
                return;
            }

            errors.add(new ValidationError(
                    p.element,
                    "Method %s on interface %s must return @Resource interface",
                    p.getName(),
                    definition.getElement().getSimpleName()));
        });
    }

    private void requirePluralErasure() {
        definition.primitivePluralProperties().forEach(p -> {
            final TypeMirror thisErasure = definition.getEnv().getTypeUtils().erasure(p.getReturnType());

            final TypeMirror cardinalityReturn = returnTypeOf(p.cardinalityMethod(), WrapperResource.class);
            final TypeMirror cardinalityErasure =
                    definition.getEnv().getTypeUtils().erasure(cardinalityReturn);

            if (definition.getEnv().getTypeUtils().isAssignable(cardinalityErasure, thisErasure)) {
                return;
            }

            errors.add(new ValidationError(
                    p.element,
                    "Return type [%s] of [%s] must have same erasure as return type [%s] of cardinality [%s]",
                    p.getReturnType(),
                    p.getName(),
                    cardinalityReturn,
                    p.cardinalityMethod()));
        });
    }

    private void requirePluralTypeArgument() {
        definition.primitivePluralProperties().forEach(p -> {
            final DeclaredType thisReturn = (DeclaredType) p.getReturnType();

            // raw generic or not generic
            if (thisReturn.getTypeArguments().isEmpty()) {
                return;
            }

            final boolean hasWildcard = thisReturn.getTypeArguments().stream().anyMatch(WildcardType.class::isInstance);

            // wildcard
            if (hasWildcard && thisReturn.getTypeArguments().stream()
                    .filter(WildcardType.class::isInstance)
                    .map(WildcardType.class::cast)
                    .flatMap(wildcard -> Stream.of(wildcard.getSuperBound(), wildcard.getExtendsBound()))
                    .allMatch(Objects::isNull)) {
                return;
            }

            final TypeMirror mappingReturn = returnTypeOf(p.valueMappingMethod(), ValueMappings.class);

            // extends or super
            if (thisReturn.getTypeArguments().stream()
                    .filter(WildcardType.class::isInstance)
                    .map(WildcardType.class::cast)
                    .flatMap(wildcard -> Stream.of(wildcard.getSuperBound(), wildcard.getExtendsBound()))
                    .filter(Objects::nonNull)
                    .anyMatch(x -> definition.getEnv().getTypeUtils().isAssignable(mappingReturn, x))) {
                return;
            }

            // concrete
            if (thisReturn.getTypeArguments().stream()
                    .filter(not(WildcardType.class::isInstance))
                    .anyMatch(x -> definition.getEnv().getTypeUtils().isAssignable(mappingReturn, x))) {
                return;
            }

            errors.add(new ValidationError(
                    p.element,
                    "Return type [%s] of [%s] must have type argument assignable from " +
                            "type argument of return type [%s] of mapping [%s]",
                    thisReturn,
                    p.getName(),
                    mappingReturn,
                    p.valueMappingMethod()));
        });
    }

    private TypeMirror returnTypeOf(final String mappingMethod, final Class<?> clazz) {
        return definition.getEnv().methodsOf(clazz)
                .filter(method -> method.getSimpleName().contentEquals(mappingMethod))
                .map(ExecutableElement::getReturnType)
                .findFirst()
                .orElse(null);
    }
}
