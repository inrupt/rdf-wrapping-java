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
package com.inrupt.rdf.wrapping.processor;

import com.inrupt.rdf.wrapping.annotation.DefaultGraph;
import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.NamedGraph;

import javax.lang.model.element.TypeElement;

import org.apache.jena.query.Dataset;

class DatasetValidator extends Validator<DatasetInterface> {
    DatasetValidator(final TypeElement type, final Environment env) {
        super(new DatasetInterface(type, env));
    }

    @Override
    protected void validateInternal() {
        requireInterface();

        limitBaseInterfaces(Dataset.class);

        requireMemberMethods(DefaultGraph.class);
        requireMemberMethods(NamedGraph.class);

        requireNonMemberMethods(DefaultGraph.class, NamedGraph.class);

        requireAnnotatedReturnType(DefaultGraph.class, Graph.class);
        requireAnnotatedReturnType(NamedGraph.class, Graph.class);
    }
}
