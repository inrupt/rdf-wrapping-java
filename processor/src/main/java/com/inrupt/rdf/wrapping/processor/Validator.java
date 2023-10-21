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

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.Resource;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

// TODO: Use interfaces in validators
abstract class Validator {
    protected final TypeElement annotation;
    protected final EnvironmentHelper env;
    protected final Element element;
    protected final Collection<ValidationError> errors = new ArrayList<>();

    protected Validator(final TypeElement annotation, final ProcessingEnvironment env, final Element element) {
        this.annotation = annotation;
        this.env = new EnvironmentHelper(env);
        this.element = element;
    }

    static Validator get(final TypeElement annotation, final ProcessingEnvironment env, final Element element) {
        if (element.getAnnotation(Dataset.class) != null) {
            return new DatasetValidator(annotation, env, element);
        } else if (element.getAnnotation(Graph.class) != null) {
            return new GraphValidator(annotation, env, element);
        } else if (element.getAnnotation(Resource.class) != null) {
            return new ResourceValidator(annotation, env, element);
        } else {
            throw new RuntimeException("unknown annotation type");
        }
    }

    Collection<ValidationError> validate() {
        validateInternal();

        return errors;
    }

    protected abstract void validateInternal();

    protected void requireMemberMethods(final Class<? extends Annotation> annotation) {
        final Predicate<ExecutableElement> isNotMember = method ->
                method.isDefault() || method.getModifiers().contains(Modifier.STATIC);

        final Predicate<ExecutableElement> isAnnotated = method -> method.getAnnotation(annotation) != null;

        env.methodsOf(element)
                .filter(isNotMember)
                .filter(isAnnotated)
                .forEach(method -> errors.add(new ValidationError(
                        method,
                        "Method %s on interface %s annotated with @%s cannot be static or default",
                        method.getSimpleName(),
                        element.getSimpleName(),
                        annotation.getSimpleName())));
    }

    @SafeVarargs
    protected final void requireNonMemberMethods(final Class<? extends Annotation>... annotations) {
        final Predicate<ExecutableElement> isMember = method ->
                !method.isDefault() && !method.getModifiers().contains(Modifier.STATIC);

        final Predicate<ExecutableElement> isUnannotated = method ->
                Arrays.stream(annotations).noneMatch(a -> method.getAnnotation(a) != null);

        env.methodsOf(element)
                .filter(isMember)
                .filter(isUnannotated)
                .forEach(method -> errors.add(new ValidationError(
                        method,
                        "Unannotated method %s on interface %s must be static or default",
                        method.getSimpleName(),
                        element.getSimpleName())));
    }

    protected void limitBaseInterfaces(final Class<?> allowed) {
        if (!element.getKind().isInterface()) {
            return;
        }

        final TypeElement type = (TypeElement) element;

        for (final TypeMirror implemented : type.getInterfaces()) {
            if (env.isSameType(implemented, allowed)) {
                continue;
            }

            errors.add(new ValidationError(
                    element,
                    "Interface %s annotated with @%s can only extend %s or nothing",
                    element.getSimpleName(),
                    annotation.getSimpleName(),
                    allowed.getName()));
        }
    }

    protected void requireInterface() {
        if (element.getKind().isInterface()) {
            return;
        }

        errors.add(new ValidationError(
                element,
                "Element %s annotated with @%s must be an interface but was a %s",
                element.getSimpleName(),
                annotation.getSimpleName(),
                element.getKind()));
    }

    protected void requireAnnotatedReturnType(
            final Class<? extends Annotation> annotation,
            final Class<? extends Annotation> required) {

        final Predicate<ExecutableElement> isAnnotated = method -> method.getAnnotation(annotation) != null;
        final Predicate<ExecutableElement> isNotResource = method ->
                env.type(method.getReturnType()).getAnnotation(required) == null;

        env.methodsOf(element)
                .filter(isAnnotated)
                .filter(isNotResource)
                .forEach(method -> errors.add(new ValidationError(
                        method,
                        "Method %s on interface %s annotated with @%s must return @%s interface",
                        method.getSimpleName(),
                        element.getSimpleName(),
                        annotation.getSimpleName(),
                        required.getSimpleName())));
    }
}
