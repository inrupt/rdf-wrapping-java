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
package com.inrupt.commons.wrapping.jena;

import static java.util.Objects.requireNonNull;
import static org.apache.jena.commonsrdf.JenaCommonsRDF.fromJena;

import com.inrupt.commons.wrapping.commonsrdf.ValueMappings;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;


/**
 * This class implements the {@link Set} interface as a dynamic, mutable view over an RDF predicate-object list
 * (statements that share a subject and a predicate). It is intended for use in classes that wrap
 * {@link RDFTerm RDF nodes} for strongly typed convenience mapping.
 *
 * <p>This set does not permit {@code null} elements.
 *
 * <p>The order of elements returned by this implementation is not guaranteed as it depends on the ordering of query
 * results in the underlying {@link Graph}. This reflects the unordered nature of RDF graphs.
 *
 * <p>The synchronization characteristics and time complexity of this implementation are those of the underlying
 * {@link Graph} implementation. It could well be that read and write operations on instances of this class result in
 * expensive IO operations. Even simple iteration is most likely to be much less performant than what callers expect
 * from other Java collections.
 *
 * <p>The iterators returned by this implementation do not support the {@link Iterator#remove()} operation.
 *
 * <p>This implementation uses the {@link AbstractSet#equals(Object)} and {@link AbstractSet#hashCode()} operations.
 * Equality and hashing are dynamic: They depend on the state of the underlying {@link Graph} at the time of calling and
 * are not fixed when creating the instance.
 *
 * <p>Example: Given a node wrapper {@code N}, instances of this class can be used to make read/write strongly typed
 * set properties.
 * <pre>{@code public class N {
 *     public Set<String> getType {
 *         return new PredicateObjectSet<>(
 *             this,
 *             RDF.type,
 *             g,
 *             NodeMappings::asIriResource,
 *             ValueMappings::iriAsString
 *         );
 *     }
 * }}</pre>
 *
 * @param <T> the type of elements handled by this set
 *
 * @author Samu Lang
 */
@SuppressWarnings("java:S2176") // Intentional. Callers can distinguish between base and derived based on package name.
public class PredicateObjectSet<T> extends com.inrupt.commons.wrapping.commonsrdf.PredicateObjectSet<T> {
    /**
     * Constructs a new dynamic set view over the objects of statements that share a predicate and a subject.
     *
     * @param subject the subject node shared by all statements
     * @param predicate the predicate node shared by all statements
     * @param nodeMapping a mapping from terms to values used for read operations (use {@link NodeMappings} for common
     * mappings)
     * @param valueMapping a mapping from values to nodes used for write operations (use {@link ValueMappings} for
     * common mappings)
     *
     * @throws NullPointerException if any of the arguments are null
     */
    public PredicateObjectSet(
            final RDFNode subject,
            final RDFNode predicate,
            final NodeMapping<T> nodeMapping,
            final ValueMapping<T> valueMapping) {

        super(
                convertSubject(requireNonNull(subject)),
                convertPredicate(requireNonNull(predicate)),
                convertGraph(subject),
                requireNonNull(nodeMapping).asCommons(),
                requireNonNull(valueMapping).asCommons());
    }

    private static BlankNodeOrIRI convertSubject(final RDFNode subject) {
        final RDFTerm term = fromJena(subject.asNode());

        if (!(term instanceof BlankNodeOrIRI)) {
            // TODO: Throw specific exception
            throw new IllegalStateException("Subject is not IRI or blank node");
        }

        return (BlankNodeOrIRI) term;
    }

    private static IRI convertPredicate(final RDFNode predicate) {
        final RDFTerm term = fromJena(predicate.asNode());

        if (!(term instanceof IRI)) {
            // TODO: Throw specific exception
            throw new IllegalStateException("Predicate is not IRI node");
        }

        return (IRI) term;
    }

    private static Graph convertGraph(final RDFNode subject) {
        final Model model = subject.getModel();

        if (model == null) {
            // TODO: Throw specific exception
            throw new IllegalStateException("Subject has no model");
        }

        return fromJena(model.getGraph());
    }
}
