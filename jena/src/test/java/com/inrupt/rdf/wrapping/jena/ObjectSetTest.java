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
package com.inrupt.rdf.wrapping.jena;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.inrupt.rdf.wrapping.test.base.ObjectSetBase;

import java.util.Set;

import org.apache.jena.rdf.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Jena Predicate-Object Set")
class ObjectSetTest extends ObjectSetBase {
    private static final NodeMapping<String> N2V = NodeMappings::asStringLiteral;
    private static final ValueMapping<String> V2N = ValueMappings::literalAsString;

    private Model model;

    @DisplayName("requires subject with model")
    @Test
    void requiresSubjectWithModel() {
        final Resource s = ResourceFactory.createResource();
        final Property p = ResourceFactory.createProperty(randomUUID().toString());

        assertThrows(HasNoModelException.class, () ->
                new ObjectSet<>(s, p, N2V, V2N));
    }

    @Override
    protected void addTriple(final String subject, final String predicate, final String object) {
        final Resource s = model.createResource(subject);
        final Property p = model.createProperty(predicate);

        model.add(s, p, object);
    }

    @Override
    protected boolean containsTriple(final String subject, final String predicate, final String object) {
        final Resource s = model.createResource(subject);
        final Property p = model.createProperty(predicate);

        return model.contains(s, p, object);
    }

    @Override
    protected Set<String> createNewSetForTest(final String subject, final String predicate) {
        model = ModelFactory.createDefaultModel();
        return createOtherSetOverSameGraph(subject, predicate);
    }

    @Override
    protected Set<String> createOtherSetOverSameGraph(final String subject, final String predicate) {
        final Resource s = subject == null ? null : model.createResource(subject);
        final Property p = predicate == null ? null : model.createProperty(predicate);

        return new ObjectSet<>(s, p, N2V, V2N);
    }
}
