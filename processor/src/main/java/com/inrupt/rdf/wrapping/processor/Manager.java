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

import static com.inrupt.rdf.wrapping.processor.Implementor.WRAP;
import static com.inrupt.rdf.wrapping.processor.Implementor.asImplementation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import org.apache.jena.enhanced.UnsupportedPolymorphismException;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

/**
 * A utility class that aids wrapper interfaces to find generated implementations.
 */
public final class Manager {
    private Manager() {
    }

    public static <T> T wrap(final Model original, final Class<T> definition) {
        return wrap(original, definition, Model.class);
    }

    public static <T> T wrap(final Dataset original, final Class<T> definition) {
        return wrap(original, definition, Dataset.class);
    }

    public static <T> T create(final String name, final Class<T> targetClass, final Object graph) {
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(graph);

        final Class<?> rawImplementation = findImplementation(targetClass);
        final Class<? extends RDFNode> implementation = subclass(rawImplementation, RDFNode.class);
        final Model model = asModel(graph);
        final RDFNode result = createAndProject(name, model, implementation);
        return ensureImplements(result, targetClass);
    }

    private static <T> T wrap(final Object original, final Class<T> definition, final Class<?> parameterType) {
        final Class<?> rawImplementation = findImplementation(definition);
        final Class<? extends T> implementation = subclass(rawImplementation, definition);
        final Method wrap = findWrapMethod(implementation, parameterType);
        ensureNotVoid(wrap);
        final Object result = invoke(wrap, original);
        return cast(result, definition);
    }

    private static <T> Class<?> findImplementation(final Class<T> definition) {
        final String implementation = asImplementation(definition.getName());

        try {
            return Class.forName(implementation);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("implementation not found", e);
        }
    }

    private static <T> Class<? extends T> subclass(final Class<?> implementation, final Class<T> definition) {
        try {
            return implementation.asSubclass(definition);

        } catch (ClassCastException e) {
            throw new RuntimeException("implementation type mismatch", e);
        }
    }

    private static <T> Method findWrapMethod(final Class<? extends T> implementation, final Class<?> parameterType) {
        try {
            return implementation.getDeclaredMethod(WRAP, parameterType);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("wrap method not found", e);
        }
    }

    private static void ensureNotVoid(final Method wrap) {
        if (wrap.getReturnType() == void.class) {
            throw new RuntimeException("wrap method is void");
        }
    }

    private static Object invoke(final Method wrap, final Object... original) {
        try {
            return wrap.invoke(null, original);

        } catch (IllegalAccessException e) {
            throw new RuntimeException("wrap method inaccessible", e);

        } catch (InvocationTargetException e) {
            throw new RuntimeException("wrap method threw exception", e);
        }
    }

    private static <T> T cast(final Object result, final Class<T> definition) {
        try {
            return definition.cast(result);

        } catch (ClassCastException e) {
            throw new RuntimeException("wrap method return type mismatch", e);
        }
    }

    private static <T> T ensureImplements(final Object result, final Class<T> definition) {
        try {
            return definition.cast(result);

        } catch (ClassCastException e) {
            throw new RuntimeException("implementation does not implement definition", e);
        }
    }

    private static RDFNode createAndProject(final String name, final Model m,
                                            final Class<? extends RDFNode> implementation) {
        try {
            return m.createResource(name).as(implementation);

        } catch (UnsupportedPolymorphismException e) {
            throw new RuntimeException("could not project to implementation", e);
        }
    }

    private static Model asModel(final Object graph) {
        if (!(graph instanceof Model)) {
            throw new RuntimeException("graph must be a Model");
        }

        return (Model) graph;
    }
}
