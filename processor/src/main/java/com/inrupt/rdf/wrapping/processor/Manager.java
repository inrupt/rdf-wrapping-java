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

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

/**
 * A utility class that aids wrapper interfaces to find generated implementations.
 */
// TODO: Should this be SPI?
public final class Manager {
    private Manager() {
    }

    public static <T> T wrap(final Model original, final Class<T> interfaceType) {
        return wrap(original, interfaceType, Model.class);
    }

    public static <T> T wrap(final Dataset original, final Class<T> interfaceType) {
        return wrap(original, interfaceType, Dataset.class);
    }

    private static <T> T wrap(final Object original, final Class<T> interfaceType, final Class<?> parameterType) {
        final ClassLoader classLoader = interfaceType.getClassLoader();
        final String implTypeName = asImplementation(interfaceType.getName());

        final Class<? extends T> implClass;
        try {
            implClass = Class.forName(implTypeName, true, classLoader).asSubclass(interfaceType);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("implementation not found", e);
        } catch (ClassCastException e) {
            // TODO: probably not needed as it's generated, match on $impl
            throw new RuntimeException("implementation type mismatch", e);
        }


        final Method wrapMethod;
        try {
            wrapMethod = implClass.getMethod(WRAP, parameterType);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("wrap method not found", e);
        }

        try {
            return interfaceType.cast(wrapMethod.invoke(null, original));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("wrap method inaccessible", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("wrap method threw exception", e);
        } catch (ClassCastException e) {
            // TODO: probably not needed as it's generated, match on $impl
            throw new RuntimeException("wrap method return type mismatch", e);
        }
    }
}
