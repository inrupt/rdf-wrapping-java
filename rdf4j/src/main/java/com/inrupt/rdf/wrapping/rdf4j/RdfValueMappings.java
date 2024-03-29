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
package com.inrupt.rdf.wrapping.rdf4j;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XSD;


/**
 * Common mappings from various values to RDF terms. For use in wrapping classes.
 *
 * @author Samu Lang
 */
public final class RdfValueMappings {
    private static final String VALUE_REQUIRED = "Value must not be null";
    private static final String GRAPH_REQUIRED = "Graph must not be null";
    private static final ValueFactory FACTORY = SimpleValueFactory.getInstance();


    /**
     * Maps a string to a literal term.
     *
     * @param value the value to map
     * @param model the graph that serves as the context for creating the term
     *
     * @return an xsd:string typed literal with the string as its lexical form
     *
     * @throws NullPointerException if the value is null
     * @throws NullPointerException if the model is null
     */
    public static Literal asStringLiteral(final String value, final Model model) {
        Objects.requireNonNull(value, VALUE_REQUIRED);
        Objects.requireNonNull(model, GRAPH_REQUIRED);

        return FACTORY.createLiteral(value);
    }

    /**
     * Maps an IRI string to an IRI term.
     *
     * @param value the value to map
     * @param model ignored
     *
     * @return an IRI term with the string as its identifier
     *
     * @throws NullPointerException if the value is null
     * @throws NullPointerException if the model is null
     */
    public static IRI asIri(final String value, final Model model) {
        Objects.requireNonNull(value, VALUE_REQUIRED);
        Objects.requireNonNull(model, GRAPH_REQUIRED);

        return FACTORY.createIRI(value);
    }

    /**
     * Maps a URI to an IRI term.
     *
     * @param value the value to map
     * @param model ignored
     *
     * @return an IRI term with the URI as its identifier
     *
     * @throws NullPointerException if the value is null
     * @throws NullPointerException if the model is null
     */
    public static IRI asIri(final URI value, final Model model) {
        Objects.requireNonNull(value, VALUE_REQUIRED);
        Objects.requireNonNull(model, GRAPH_REQUIRED);

        return FACTORY.createIRI(value.toString());
    }

    /**
     * Maps a point in time to a literal term.
     *
     * @param value the value to map
     * @param model ignored
     *
     * @return an xsd:dateTime typed literal term with the point in time as its lexical value
     *
     * @throws NullPointerException if the value is null
     * @throws NullPointerException if the model is null
     */
    public static Literal asTypedLiteral(final Instant value, final Model model) {
        Objects.requireNonNull(value, VALUE_REQUIRED);
        Objects.requireNonNull(model, GRAPH_REQUIRED);

        return FACTORY.createLiteral(value.toString(), XSD.DATETIME);
    }

    /**
     * Maps a boolean value to a literal term.
     *
     * @param value the value to map
     * @param model ignored
     *
     * @return an xsd:boolean typed literal node with the boolean value as its lexical value
     *
     * @throws NullPointerException if the value is null
     * @throws NullPointerException if the model is null
     */
    public static Literal asTypedLiteral(final Boolean value, final Model model) {
        Objects.requireNonNull(value, VALUE_REQUIRED);
        Objects.requireNonNull(model, GRAPH_REQUIRED);

        return FACTORY.createLiteral(value.toString(), XSD.BOOLEAN);
    }

    /**
     * Maps a integer value to a literal term.
     *
     * @param value the value to map
     * @param model ignored
     *
     * @return an xsd:int typed literal node with the integer value as its lexical value
     *
     * @throws NullPointerException if the value is null
     * @throws NullPointerException if the model is null
     */
    public static Literal asTypedLiteral(final Integer value, final Model model) {
        Objects.requireNonNull(value, VALUE_REQUIRED);
        Objects.requireNonNull(model, GRAPH_REQUIRED);

        return FACTORY.createLiteral(value.toString(), XSD.INT);
    }

    /**
     * Maps a term to itself.
     *
     * @param value the value to map
     * @param model ignored
     *
     * @return the same term
     *
     * @throws NullPointerException if the value is null
     * @throws NullPointerException if the model is null
     */
    public static Value identity(final Value value, final Model model) {
        Objects.requireNonNull(value, VALUE_REQUIRED);
        Objects.requireNonNull(model, GRAPH_REQUIRED);

        return value;
    }

    private RdfValueMappings() {
    }
}
