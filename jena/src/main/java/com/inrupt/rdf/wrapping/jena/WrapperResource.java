/*
 * Copyright 2023 Inrupt Inc.
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
package com.inrupt.rdf.wrapping.jena;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.shared.PropertyNotFoundException;

/**
 * A wrapper for resources  which contains methods that aid authoring wrapping classes.
 *
 * <p>All methods require a predicate and a value mapping function.
 *
 * <p>This table details the behavior of singular getter helper methods depending on the number of matching statements
 * in the underlying graph:
 * <pre>
 * ┌───────────────┬───────┬────────┬────────┐
 * │               │   0   │   1    │   &gt;1   │
 * ├───────────────┼───────┼────────┼────────┤
 * │ anyOrNull     │ null  │ single │ random │
 * │ anyOrThrow    │ throw │ single │ random │
 * │ singleOrNull  │ null  │ single │ throw  │
 * │ singleOrThrow │ throw │ single │ throw  │
 * └───────────────┴───────┴────────┴────────┘
 * </pre>
 *
 * <p>This table details the behavior of plural getter helper methods in terms of reflecting changes to the underlying
 * graph after calling them:
 * <pre>
 * ┌──────────┬─────────┐
 * │ iterator │ static  │
 * │ snapshot │ static  │
 * │ live     │ dynamic │
 * └──────────┴─────────┘
 * </pre>
 *
 * <p>This table details the behavior of setter helper methods in terms of effect on existing statements in the
 * underlying graph and given values:
 * <pre>
 * ┌───────────────────┬──────────┬────────────┐
 * │                   │ existing │ null value │
 * ├───────────────────┼──────────┼────────────┤
 * │ overwrite         │ remove   │ throw      │
 * │ overwriteNullable │ remove   │ ignore     │
 * │ add               │ leave    │ throw      │
 * └───────────────────┴──────────┴────────────┘
 * </pre>
 *
 * @author Samu Lang
 */
public abstract class WrapperResource extends ResourceImpl {
    /**
     * Create a new subject resource with a backing {@link EnhGraph} structure.
     *
     * @param node the subject node
     * @param model the rdf model
     */
    protected WrapperResource(final Node node, final EnhGraph model) {
        super(node, model);
    }

    /**
     * A converting singular getter helper for expected cardinality {@code 0..1} that ignores overflow.
     *
     * @param p the predicate to query
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned the type of values returned
     *
     * @return the converted object of an arbitrary statement with this subject and the given predicate or null if there
     * are no such statements
     */
    protected <T> T anyOrNull(final Property p, final ValueMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(m);

        final Statement statement = getProperty(p);

        if (statement == null) {
            return null;
        }

        return m.apply(statement.getObject());
    }

    /**
     * A converting singular getter helper for expected cardinality {@code 1..1} that ignores overflow.
     *
     * @param p the predicate to query
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     *
     * @return the converted object of an arbitrary statement with this subject and the given predicate
     *
     * @throws PropertyNotFoundException if there are no such statements
     */
    protected <T> T anyOrThrow(final Property p, final ValueMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(m);

        return m.apply(getRequiredProperty(p).getObject());
    }

    /**
     * A converting singular getter helper for expected cardinality {@code 0..1} that forbids overflow.
     *
     * @param p the predicate to query
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     *
     * @return the converted object of the only statement with this subject and the given predicate, or null if there is
     * no such statement
     *
     * @throws IllegalStateException if there are multiple such statements
     */
    protected <T> T singleOrNull(final Property p, final ValueMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(m);

        final Iterator<T> statements = objectIterator(p, m);

        if (!statements.hasNext()) {
            return null;
        }

        final T any = statements.next();
        atMostOne(statements, p);

        return any;
    }

    /**
     * A converting singular getter helper for expected cardinality {@code 1..1} that forbids overflow.
     *
     * @param p the predicate to query
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     *
     * @return the converted object of the only statement with this subject and the given predicate
     *
     * @throws PropertyNotFoundException if there are no such statements
     * @throws IllegalStateException if there are multiple such statements
     */
    protected <T> T singleOrThrow(final Property p, final ValueMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(m);

        final Iterator<T> statements = objectIterator(p, m);

        if (!statements.hasNext()) {
            throw new PropertyNotFoundException(p);
        }

        final T any = statements.next();
        atMostOne(statements, p);

        return any;
    }

    /**
     * A static converting plural getter helper for expected cardinality {@code 0..*}.
     *
     * @param p the predicate to query
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     *
     * @return the converted objects of statements with this subject and the given predicate
     */
    protected <T> Iterator<T> objectIterator(final Property p, final ValueMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(m);

        return listProperties(p).mapWith(Statement::getObject).mapWith(m);
    }

    /**
     * A static converting plural getter helper for expected cardinality {@code 0..*}.
     *
     * @param p the predicate to query
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     *
     * @return a static set view over converted objects of statements with this subject and the given predicate
     */
    protected <T> Set<T> objectsReadOnly(final Property p, final ValueMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(m);

        return objectStream(p, m).collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
    }

    /**
     * A dynamic converting plural getter helper for expected cardinality {@code 0..*}.
     *
     * @param p the predicate to query
     * @param nm the input mapping to apply to values
     * @param vm the output mapping to apply to nodes
     * @param <T> the type of values returned
     *
     * @return a dynamic set view over converted objects of statements with this subject and the given predicate
     */
    protected <T> Set<T> objects(final Property p, final NodeMapping<T> nm, final ValueMapping<T> vm) {
        return new ObjectSet<>(this, p, nm, vm);
    }

    /**
     * A static converting plural getter helper for expected cardinality {@code 0..*}.
     *
     * @param p the predicate to query
     * @param m the mapping applied to result terms
     * @param <T> the type of values returned
     *
     * @return a static stream of converted objects of statements with this subject and the given predicate
     */
    protected <T> Stream<T> objectStream(final Property p, final ValueMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(m);

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(objectIterator(p, m), Spliterator.NONNULL),
                false);
    }

    /**
     * A destructive converting singular setter helper for expected cardinality {@code 1..1}.
     *
     * @param p the predicate to query
     * @param v the value to assert as object in the graph
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     *
     * @throws NullPointerException if the given value is {@code null}
     */
    protected <T> void overwrite(final Property p, final T v, final NodeMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(v);
        Objects.requireNonNull(m);

        removeAll(p);
        add(p, v, m);
    }

    /**
     * A destructive converting plural setter helper for expected cardinality {@code 1..*}.
     *
     * @param p the predicate to query
     * @param v the value to assert as object in the graph
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     *
     * @throws NullPointerException if the given value is {@code null}
     * @throws NullPointerException if the given value contains {@code null} elements
     */
    protected <T> void overwrite(final Property p, final Iterable<T> v, final NodeMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(v);
        Objects.requireNonNull(m);
        v.forEach(Objects::requireNonNull);

        removeAll(p);
        v.forEach(value -> add(p, v, m));
    }

    /**
     * A destructive converting plural setter helper for expected cardinality {@code 0..*}.
     *
     * @param p the predicate to query
     * @param v the value to assert as object in the graph
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     *
     * @throws NullPointerException if the given value is not {@code null} and contains {@code null} elements
     */
    protected <T> void overwriteNullable(final Property p, final Iterable<T> v, final NodeMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(m);

        if (v != null) {
            v.forEach(Objects::requireNonNull);
        }

        removeAll(p);

        if (v == null) {
            return;
        }

        v.forEach(value -> add(p, value, m));
    }

    /**
     * A destructive converting singular setter helper for expected cardinality {@code 0..1}.
     *
     * @param p the predicate to query
     * @param v the value to assert as object in the graph
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     */
    protected <T> void overwriteNullable(final Property p, final T v, final NodeMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(m);

        removeAll(p);

        if (v == null) {
            return;
        }

        add(p, v, m);
    }

    /**
     * An additive converting singular setter helper for expected cardinality {@code 0..*}.
     *
     * @param p the predicate to query
     * @param v the value to assert as object in the graph
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     *
     * @throws NullPointerException if the given value is {@code null}
     */
    protected <T> void add(final Property p, final T v, final NodeMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(v);
        Objects.requireNonNull(m);

        addProperty(p, m.apply(v, getModel()));
    }

    /**
     * An additive converting singular setter helper for expected cardinality {@code 0..*}.
     *
     * @param p the predicate to query
     * @param v the value to assert as object in the graph
     * @param m the mapping applied to result nodes
     * @param <T> the type of values returned
     *
     * @throws NullPointerException if the given value is {@code null}
     * @throws NullPointerException if the given value contains {@code null} elements
     */
    protected <T> void add(final Property p, final Iterable<T> v, final NodeMapping<T> m) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(v);
        Objects.requireNonNull(m);
        v.forEach(Objects::requireNonNull);

        v.forEach(value -> add(p, value, m));
    }

    private <T> void atMostOne(final Iterator<T> objects, final Property p) {
        if (objects.hasNext()) {
            final String message = String.format("Multiple statements with subject [%s] and predicate [%s]", this, p);
            // TODO: Throw specific exception
            throw new IllegalStateException(message);
        }
    }
}
