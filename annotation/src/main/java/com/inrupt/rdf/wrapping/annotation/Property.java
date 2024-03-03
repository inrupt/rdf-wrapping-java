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
package com.inrupt.rdf.wrapping.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Property {
    String predicate();

    Cardinality cardinality();

    Mapping mapping();

    enum Mapping {
        AS("as"),
        IRI_AS_STRING("iriAsString"),
        IRI_AS_URI("iriAsUri"),
        LITERAL_AS_BOOLEAN("literalAsBoolean"),
        LITERAL_AS_INSTANT("literalAsInstant"),
        LITERAL_AS_INTEGER_OR_NULL("literalAsIntegerOrNull"),
        LITERAL_AS_STRING("literalAsString");

        private final String methodName;

        Mapping(final String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return methodName;
        }
    }

    enum Cardinality {
        ANY_OR_NULL("anyOrNull", false),

        ANY_OR_THROW("anyOrThrow", false),

        SINGLE_OR_NULL("singleOrNull", false),

        SINGLE_OR_THROW("singleOrThrow", false),

        OBJECT_ITERATOR("objectIterator", true),

        OBJECTS_READ_ONLY("objectsReadOnly", true),

        OBJECT_STREAM("objectStream", true);

        private final String methodName;
        private final boolean plural;

        Cardinality(final String methodName, final boolean plural) {
            this.methodName = methodName;
            this.plural = plural;
        }

        public String getMethodName() {
            return methodName;
        }

        public boolean isPlural() {
            return plural;
        }
    }
}
