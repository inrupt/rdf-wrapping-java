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
        return listStatements(new InstanceOfEitherSelector(classes))
                .mapWith(Statement::getSubject)
                .mapWith(subject -> subject.as(view))
                .nextOptional()
                .orElse(null);
    }

    private static final class InstanceOfEitherSelector implements Selector {
        private final List<Resource> classes;

        private InstanceOfEitherSelector(final String[] classes) {
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

            if (!getPredicate().equals(statement.getPredicate())) {
                return false;
            }

            return classes.contains(statement.getObject().asResource());
        }

        @Override
        public Property getPredicate() {
            return RDF.type;
        }

        @Override
        public boolean isSimple() {
            return false;
        }

        @Override
        public Resource getSubject() {
            return null;
        }

        @Override
        public RDFNode getObject() {
            return null;
        }
    }
}
