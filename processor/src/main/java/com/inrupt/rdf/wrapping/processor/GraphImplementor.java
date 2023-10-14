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

import static org.jboss.jdeparser.JTypes.typeOf;

import com.inrupt.rdf.wrapping.annotation.OptionalFirstInstanceOfEither;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstObjectOfEither;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstSubjectOfEither;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

class GraphImplementor extends Implementor {
    GraphImplementor(final ProcessingEnvironment environment, final Element element) {
        super(environment, element);
    }

    @Override
    protected void implementInternal() {
        final GraphInterface myInterface = new GraphInterface(environment, type);
        final GraphImplementation myClass = new GraphImplementation();

        myClass.addImports(sourceFile);
        myClass.addClass(sourceFile, implementationClass, originalInterface);
        myClass.annotateAndDocument();
        myClass.addConstructor();
        myClass.addWrap(originalInterface);

        // TODO: Also add return types of properties of resources
        for (final TypeMirror type : myInterface.resourceMethodTypes()) {
            myClass.addToPersonality(asImplementation(type));
        }

        for (final ExecutableElement method : myInterface.instanceMethods()) {
            myClass.addResourceMethod(
                    typeOf(method.getReturnType()),
                    method.getSimpleName().toString(),
                    "optionalFirstInstanceOfEither",
                    asImplementation(method.getReturnType()),
                    method.getAnnotation(OptionalFirstInstanceOfEither.class).value());
        }

        for (final ExecutableElement method : myInterface.subjectMethods()) {
            myClass.addResourceMethod(
                    typeOf(method.getReturnType()),
                    method.getSimpleName().toString(),
                    "optionalFirstSubjectOfEither",
                    asImplementation(method.getReturnType()),
                    method.getAnnotation(OptionalFirstSubjectOfEither.class).value());
        }

        for (final ExecutableElement method : myInterface.objectMethods()) {
            myClass.addResourceMethod(
                    typeOf(method.getReturnType()),
                    method.getSimpleName().toString(),
                    "optionalFirstObjectOfEither",
                    asImplementation(method.getReturnType()),
                    method.getAnnotation(OptionalFirstObjectOfEither.class).value());
        }
    }
}
