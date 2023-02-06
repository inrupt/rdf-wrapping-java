package com.inrupt.rdf.wrapping.jena;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.inrupt.rdf.wrapping.test.base.ObjectSetBase;

import java.util.Set;

import org.apache.jena.rdf.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Jena Predicate-Object Set")
class JenaObjectSetTest extends ObjectSetBase {
    private static final NodeMapping<String> N2V = NodeMappings::asStringLiteral;
    private static final ValueMapping<String> V2N = ValueMappings::literalAsString;

    private Model model;

    @DisplayName("requires blank node or IRI subject")
    @Test
    void requiresBlankOrIriSubject() {
        final RDFNode literal = ResourceFactory.createStringLiteral(randomUUID().toString());

        assertThrows(IllegalStateException.class, () ->
                new ObjectSet<>(literal, null, null, null));
    }

    @DisplayName("requires IRI predicate")
    @Test
    void requiresIriPredicate() {
        final RDFNode blank = ResourceFactory.createResource();

        assertThrows(IllegalStateException.class, () ->
                new ObjectSet<>(blank, blank, null, null));
    }

    @DisplayName("requires subject with model")
    @Test
    void requiresSubjectWithModel() {
        final RDFNode blank = ResourceFactory.createResource();
        final RDFNode iri = ResourceFactory.createResource(randomUUID().toString());

        assertThrows(IllegalStateException.class, () ->
                new ObjectSet<>(blank, iri, null, null));
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
