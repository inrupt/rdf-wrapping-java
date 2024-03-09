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

import static com.inrupt.rdf.wrapping.processor.Definition.isVoid;
import static com.inrupt.rdf.wrapping.processor.PredicateShim.not;
import static com.inrupt.rdf.wrapping.processor.ResourceDefinition.isSetter;

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.jena.ValueMappings;
import com.inrupt.rdf.wrapping.jena.WrapperResource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

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

        requireMemberMethods(Property.class);

        requireNonMemberMethods(Property.class);

        requireNonVoidReturnType();
        requireCompatiblePrimitiveReturnType();
        requireCompatibleComplexReturnType();

        requirePluralErasure();
        requirePluralTypeArgument();
    }

    private void requireNonVoidReturnType() {
        definition.membersAnnotatedWith(Property.class)
                .filter(not(isSetter))
                .filter(isVoid)
                .forEach(method ->
                        errors.add(new ValidationError(
                                method,
                                "Method [%s] must not be void",
                                method.getSimpleName())));
    }

    private void requireCompatiblePrimitiveReturnType() {
        definition.primitiveMappingPropertyMethods().forEach(method -> {
            final TypeMirror thisReturn = method.getReturnType();

            final String mappingMethod = method.getAnnotation(Property.class).valueMapping().getMethodName();
            final TypeMirror mappingReturn = returnTypeOf(mappingMethod, ValueMappings.class);

            if (definition.getEnv().getTypeUtils().isAssignable(mappingReturn, thisReturn)) {
                return;
            }

            errors.add(new ValidationError(
                    method,
                    "Return type [%s] of [%s] must be assignable from return type [%s] of mapping [%s]",
                    thisReturn,
                    method.getSimpleName(),
                    mappingReturn,
                    mappingMethod));
        });
    }

    private void requireCompatibleComplexReturnType() {
        definition.complexMappingPropertyMethods().forEach(method -> {
            final TypeElement returnType = definition.returnTypeOf(method);

            if (returnType != null && returnType.getAnnotation(Resource.class) != null) {
                return;
            }

            errors.add(new ValidationError(
                    method,
                    "Method %s on interface %s must return @Resource interface",
                    method.getSimpleName(),
                    definition.getType().getSimpleName()));
        });
    }

    private void requirePluralErasure() {
        definition.pluralPropertyMethods().forEach(method -> {
            final TypeMirror thisReturn = method.getReturnType();
            final TypeMirror thisErasure = definition.getEnv().getTypeUtils().erasure(thisReturn);

            final String cardinalityMethod = method.getAnnotation(Property.class).cardinality().getMethodName();
            final TypeMirror cardinalityReturn = returnTypeOf(cardinalityMethod, WrapperResource.class);
            final TypeMirror cardinalityErasure =
                    definition.getEnv().getTypeUtils().erasure(cardinalityReturn);

            // TODO: Check subclassing of erasures e.g. Collection<T> & Set<T>
            if (definition.getEnv().isSameType(cardinalityErasure, thisErasure)) {
                return;
            }

            errors.add(new ValidationError(
                    method,
                    "Return type [%s] of [%s] must have same erasure as return type [%s] of cardinality [%s]",
                    thisReturn,
                    method.getSimpleName(),
                    cardinalityReturn,
                    cardinalityMethod));
        });
    }

    // TODO: Check bounds of type parameters e.g. String & CharSequence
    private void requirePluralTypeArgument() {
        definition.pluralPropertyMethods().forEach(method -> {
            final DeclaredType thisReturn = (DeclaredType) method.getReturnType();
            final TypeMirror thisErasure = definition.getEnv().getTypeUtils().erasure(thisReturn);

            if (definition.getEnv().isSameType(thisErasure, thisReturn)) {
                // Ignore cases where return type of property method is not generic
                return;
            }

            final String mappingMethod = method.getAnnotation(Property.class).valueMapping().getMethodName();
            final TypeMirror mappingReturn = returnTypeOf(mappingMethod, ValueMappings.class);

            final List<TypeElement> mappingTypeArguments = new ArrayList<>();
            mappingTypeArguments.add(definition.typeOf(mappingReturn));

            final List<TypeElement> thisTypeArguments = thisReturn.getTypeArguments().stream()
                    .map(definition::typeOf)
                    .collect(Collectors.toList());

            if (mappingTypeArguments.equals(thisTypeArguments)) {
                return;
            }

            errors.add(new ValidationError(
                    method,
                    "Return type [%s] of [%s] must have same type argument as return type [%s] of mapping [%s]",
                    thisReturn,
                    method.getSimpleName(),
                    mappingReturn,
                    mappingMethod));
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
