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

import com.inrupt.rdf.wrapping.annotation.Property;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

class ResourceImplementor extends Implementor {
    ResourceImplementor(final ProcessingEnvironment environment, final Element element) {
        super(environment, element);
    }

    @Override
    protected void implementInternal() {
        final ResourceInterface myInterface = new ResourceInterface(environment, type);
        final ResourceImplementation myClass = new ResourceImplementation();

        myClass.addImports(sourceFile);
        myClass.addClass(sourceFile, implementationClass, originalInterface);
        myClass.addFactoryField();
        myClass.annotateAndDocument();
        myClass.addConstructor();

        myInterface.primitivePropertyMethods().forEach(method -> myClass.addPrimitivePropertyMethod(
                typeOf(method.getReturnType()),
                method.getSimpleName().toString(),
                method.getAnnotation(Property.class).mapping().getMethodName(),
                method.getAnnotation(Property.class).predicate()));

        myInterface.resourcePropertyMethods().forEach(method -> myClass.addResourcePropertyMethod(
                typeOf(method.getReturnType()),
                method.getSimpleName().toString(),
                method.getAnnotation(Property.class).predicate(),
                asImplementation(method.getReturnType())));
    }
}
