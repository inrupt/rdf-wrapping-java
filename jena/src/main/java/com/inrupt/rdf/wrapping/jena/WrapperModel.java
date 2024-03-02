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

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.vocabulary.RDF;

public abstract class WrapperModel extends ModelCom {
    protected WrapperModel(final Graph base) {
        super(base);
    }

    protected <T extends RDFNode> T optionalFirstInstanceOfEither(final Class<T> view, final String... classes) {
        return listStatements()
                .filterKeep(new InstanceOfEither(classes))
                .mapWith(Statement::getSubject)
                .mapWith(subject -> subject.as(view))
                .nextOptional()
                .orElse(null);
    }

    protected <T extends RDFNode> T optionalFirstSubjectOfEither(final Class<T> view, final String... predicates) {
        return listStatements()
                .filterKeep(new EitherPredicate(predicates))
                .mapWith(Statement::getSubject)
                .mapWith(subject -> subject.as(view))
                .nextOptional()
                .orElse(null);
    }

    protected <T extends RDFNode> T optionalFirstObjectOfEither(final Class<T> view, final String... predicates) {
        return listStatements()
                .filterKeep(new EitherPredicate(predicates))
                .mapWith(Statement::getObject)
                .mapWith(object -> object.as(view))
                .nextOptional()
                .orElse(null);
    }

    private static final class InstanceOfEither implements Predicate<Statement> {
        private final List<Resource> classes;

        private InstanceOfEither(final String[] classes) {
            this.classes = Arrays
                    .stream(classes)
                    .map(ResourceFactory::createResource)
                    .collect(Collectors.toList());
        }

        @Override
        public boolean test(final Statement statement) {
            if (!statement.getObject().isResource()) {
                return false;
            }

            if (!RDF.type.equals(statement.getPredicate())) {
                return false;
            }

            return classes.contains(statement.getObject().asResource());
        }
    }

    private static final class EitherPredicate implements Predicate<Statement> {
        private final List<Property> predicates;

        private EitherPredicate(final String[] predicates) {
            this.predicates = Arrays
                    .stream(predicates)
                    .map(ResourceFactory::createProperty)
                    .collect(Collectors.toList());
        }

        @Override
        public boolean test(final Statement statement) {
            return predicates.contains(statement.getPredicate());
        }
    }
}
