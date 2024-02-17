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

import javax.lang.model.element.TypeElement;

class GraphImplementor extends Implementor<GraphImplementation> {
    GraphImplementor(final TypeElement type, final Environment env) {
        super(new GraphImplementation(new GraphInterface(type, env)));
    }

    @Override
    protected void implementInternal() {
        myClass.addImports(sourceFile);
        myClass.addClass(sourceFile);
        myClass.addConstructor();
        myClass.addWrap();

        myClass.getMyInterface().transitiveResourceTypes().forEach(myClass::addToPersonality);

        myClass.getMyInterface().instanceMethods().forEach(method -> myClass.addResourceMethod(
                method,
                "optionalFirstInstanceOfEither",
                method.getAnnotation(OptionalFirstInstanceOfEither.class).value()));

        myClass.getMyInterface().subjectMethods().forEach(method -> myClass.addResourceMethod(
                method,
                "optionalFirstSubjectOfEither",
                method.getAnnotation(OptionalFirstSubjectOfEither.class).value()));

        myClass.getMyInterface().objectMethods().forEach(method -> myClass.addResourceMethod(
                method,
                "optionalFirstObjectOfEither",
                method.getAnnotation(OptionalFirstObjectOfEither.class).value()));
    }
}
