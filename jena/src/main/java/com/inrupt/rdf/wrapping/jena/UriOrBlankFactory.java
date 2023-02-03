/*
 * Proprietary and Confidential
 *
 * Copyright Inrupt Inc. 2023 - all rights reserved.
 *
 * Do not use without explicit permission from Inrupt Inc.
 */
package com.inrupt.rdf.wrapping.jena;

import java.util.Objects;
import java.util.function.BiFunction;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.EnhNode;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceRequiredException;

/**
 * A factory for generating facets from blank and IRI nodes in enhanced graphs. Note: should not be invoked directly by
 * user code: use {@link RDFNode#as(Class) as()} instead.
 *
 * <p>Example: Given a node wrapper {@code N} and a graph wrapper {@code G}, this class can be used to make the
 * relationship between the two easier to write.
 * <pre>{@code public class N extends ResourceImpl {
 *     static final Implementation factory = new UriOrBlankFactory(N::new);
 *
 *     public N(final Node n, final EnhGraph g) {
 *         super(n, g);
 *     }
 * }
 *
 * public class G extends ModelCom {
 *     public G(final Model model) {
 *         getPersonality().add(N.class, N.factory);
 *     }
 * }}</pre>
 */
public class UriOrBlankFactory extends Implementation {
    private final BiFunction<Node, EnhGraph, EnhNode> factory;

    public UriOrBlankFactory(final BiFunction<Node, EnhGraph, EnhNode> factory) {
        Objects.requireNonNull(factory);

        this.factory = factory;
    }

    @Override
    public EnhNode wrap(final Node node, final EnhGraph enhGraph) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(enhGraph);

        if (!canWrap(node, enhGraph)) {
            throw new ResourceRequiredException(node);
        }

        return factory.apply(node, enhGraph);
    }

    @Override
    public boolean canWrap(final Node node, final EnhGraph enhGraph) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(enhGraph);

        return node.isURI() || node.isBlank();
    }
}
