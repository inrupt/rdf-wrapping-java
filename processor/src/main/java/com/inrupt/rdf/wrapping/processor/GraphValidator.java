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

import com.inrupt.rdf.wrapping.annotation.OptionalFirstInstanceOfEither;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstObjectOfEither;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstSubjectOfEither;
import com.inrupt.rdf.wrapping.annotation.Resource;

import org.apache.jena.rdf.model.Model;

class GraphValidator extends Validator<GraphDefinition> {
    GraphValidator(final GraphDefinition definition) {
        super(definition);
    }

    @Override
    protected void validateInternal() {
        requireInterface();

        limitBaseInterfaces(Model.class);

        requireMemberMethods(OptionalFirstInstanceOfEither.class);
        requireMemberMethods(OptionalFirstObjectOfEither.class);
        requireMemberMethods(OptionalFirstSubjectOfEither.class);

        requireNonMemberMethods(
                OptionalFirstInstanceOfEither.class,
                OptionalFirstObjectOfEither.class,
                OptionalFirstSubjectOfEither.class);

        requireNonVoidReturnType(OptionalFirstInstanceOfEither.class);
        requireNonVoidReturnType(OptionalFirstObjectOfEither.class);
        requireNonVoidReturnType(OptionalFirstSubjectOfEither.class);

        requireAnnotatedReturnType(OptionalFirstInstanceOfEither.class, Resource.class);
        requireAnnotatedReturnType(OptionalFirstObjectOfEither.class, Resource.class);
        requireAnnotatedReturnType(OptionalFirstSubjectOfEither.class, Resource.class);
    }
}
