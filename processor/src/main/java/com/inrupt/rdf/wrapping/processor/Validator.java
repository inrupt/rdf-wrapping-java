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

import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.type.TypeKind.VOID;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

abstract class Validator<T extends Definition> {
    protected final T definition;
    protected final Collection<ValidationError> errors = new ArrayList<>();

    protected Validator(final T definition) {
        this.definition = definition;
    }

    static Validator<?> validator(final Definition definition) {
        if (definition instanceof DatasetDefinition) {
            return new DatasetValidator((DatasetDefinition) definition);

        } else if (definition instanceof GraphDefinition) {
            return new GraphValidator((GraphDefinition) definition);

        } else { // Resource
            // Processor's supported annotations are finite
            return new ResourceValidator((ResourceDefinition) definition);
        }
    }

    Collection<ValidationError> validate() {
        validateInternal();

        return errors;
    }

    protected abstract void validateInternal();

    protected void requireMemberMethods(final Class<? extends Annotation> annotation) {
        final Predicate<ExecutableElement> isNotMember = method ->
                method.isDefault() || method.getModifiers().contains(STATIC);

        final Predicate<ExecutableElement> isAnnotated = method -> method.getAnnotation(annotation) != null;

        definition.getEnv().methodsOf(definition.getType())
                .filter(isNotMember)
                .filter(isAnnotated)
                .forEach(method -> errors.add(new ValidationError(
                        method,
                        "Method %s on interface %s annotated with @%s cannot be static or default",
                        method.getSimpleName(),
                        definition.getType().getSimpleName(),
                        annotation.getSimpleName())));
    }

    @SafeVarargs
    protected final void requireNonMemberMethods(final Class<? extends Annotation>... annotations) {
        final Predicate<ExecutableElement> isMember = method ->
                !method.isDefault() && !method.getModifiers().contains(STATIC);

        final Predicate<ExecutableElement> isUnannotated = method ->
                Arrays.stream(annotations).noneMatch(a -> method.getAnnotation(a) != null);

        definition.getEnv().methodsOf(definition.getType())
                .filter(isMember)
                .filter(isUnannotated)
                .forEach(method -> errors.add(new ValidationError(
                        method,
                        "Unannotated method %s on interface %s must be static or default",
                        method.getSimpleName(),
                        definition.getType().getSimpleName())));
    }

    protected void limitBaseInterfaces(final Class<?> allowed) {
        if (!definition.getType().getKind().isInterface()) {
            return;
        }

        for (final TypeMirror implemented : definition.getType().getInterfaces()) {
            if (definition.getEnv().isSameType(implemented, allowed)) {
                continue;
            }

            errors.add(new ValidationError(
                    definition.getType(),
                    "Interface %s can only extend %s or nothing",
                    definition.getType().getSimpleName(),
                    allowed.getName()));
        }
    }

    protected void requireInterface() {
        if (definition.getType().getKind().isInterface()) {
            return;
        }

        errors.add(new ValidationError(
                definition.getType(),
                "Element %s must be an interface but was a %s",
                definition.getType().getSimpleName(),
                definition.getType().getKind()));
    }

    protected void requireAnnotatedReturnType(
            final Class<? extends Annotation> annotation,
            final Class<? extends Annotation> required) {

        final Predicate<ExecutableElement> isAnnotated = method -> method.getAnnotation(annotation) != null;
        final Predicate<ExecutableElement> isNotResource = method ->
                definition.getEnv().type(method.getReturnType()).getAnnotation(required) == null;

        definition.getEnv().methodsOf(definition.getType())
                .filter(method -> method.getReturnType().getKind() != VOID)
                .filter(isAnnotated)
                .filter(isNotResource)
                .forEach(method -> errors.add(new ValidationError(
                        method,
                        "Method %s on interface %s annotated with @%s must return @%s interface",
                        method.getSimpleName(),
                        definition.getType().getSimpleName(),
                        annotation.getSimpleName(),
                        required.getSimpleName())));
    }

    protected void requireNonVoidReturnType(final Class<? extends Annotation> annotation) {
        definition.membersAnnotatedWith(annotation)
                .filter(method -> method.getReturnType().getKind() == VOID)
                .forEach(method ->
                        errors.add(new ValidationError(
                                method,
                                "Method [%s] must not be void",
                                method.getSimpleName())));
    }
}
