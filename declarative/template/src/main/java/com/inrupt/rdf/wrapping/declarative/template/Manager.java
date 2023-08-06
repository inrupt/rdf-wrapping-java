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
package com.inrupt.rdf.wrapping.declarative.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

/**
 * A utility class that aids wrapper interfaces to find generated implementations.
 */
// TODO: Move to processor module
// TODO: Should this be SPI?
public final class Manager {
    private Manager() {
    }

    public static <T> T wrap(final Class<T> type, final Model original) {
        return wrap(type, original, Model.class);
    }

    public static <T> T wrap(final Class<T> type, final Dataset original) {
        return wrap(type, original, Dataset.class);
    }

    private static <T> T wrap(final Class<T> type, final Object original, final Class<?> xtype) {
        final ClassLoader classLoader = type.getClassLoader();
        final String implTypeName = type.getName() + "_$impl";

        final Class<? extends T> implClass;
        try {
            implClass = Class.forName(implTypeName, true, classLoader).asSubclass(type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("implementation not found", e);
        } catch (ClassCastException e) {
            // TODO: probably not needed as it's generated, match on $impl
            throw new RuntimeException("implementation type mismatch", e);
        }


        final Method wrapMethod;
        try {
            wrapMethod = implClass.getMethod("wrap", xtype);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("wrap method not found", e);
        }

        try {
            return type.cast(wrapMethod.invoke(null, original));
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
