package com.inrupt.commons.wrapping.jena;

import com.inrupt.commons.wrapping.test.base.PredicateObjectSetBase;

import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Jena Predicate-Object Set")
class JenaPredicateObjectSetTest extends PredicateObjectSetBase {
    private static final NodeMapping<String> N2V = (n, m) -> m.createLiteral(n);
    private static final ValueMapping<String> V2N = n -> n.asLiteral().getLexicalForm();

    private Model model;

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

        return new com.inrupt.commons.wrapping.jena.PredicateObjectSet<>(s, p, N2V, V2N);
    }
}
