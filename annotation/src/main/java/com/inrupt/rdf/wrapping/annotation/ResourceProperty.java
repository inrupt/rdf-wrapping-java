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

import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality.ANY_OR_NULL;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.NodeMapping.IDENTITY;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.ValueMapping.AS;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface ResourceProperty {
    String value();

    Cardinality cardinality() default ANY_OR_NULL;

    ValueMapping valueMapping() default AS;

    NodeMapping nodeMapping() default IDENTITY;

    enum ValueMapping {
        AS("as", true),
        IRI_AS_STRING("iriAsString", false),
        IRI_AS_URI("iriAsUri", false),
        LITERAL_AS_BOOLEAN("literalAsBoolean", false),
        LITERAL_AS_INSTANT("literalAsInstant", false),
        LITERAL_AS_INTEGER_OR_NULL("literalAsIntegerOrNull", false),
        LITERAL_AS_STRING("literalAsString", false);

        private final String methodName;
        private final boolean complex;

        ValueMapping(final String methodName, final boolean complex) {
            this.methodName = methodName;
            this.complex = complex;
        }

        public String getMethodName() {
            return methodName;
        }

        public boolean isComplex() {
            return complex;
        }
    }

    enum NodeMapping {
        AS_STRING_LITERAL("asStringLiteral"),
        AS_IRI("asIri"),
        AS_TYPED_LITERAL("asTypedLiteral"),
        IDENTITY("identity");

        private final String methodName;

        NodeMapping(final String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return methodName;
        }
    }

    enum Cardinality {
        ANY_OR_NULL("anyOrNull", false, false),

        ANY_OR_THROW("anyOrThrow", false, false),

        SINGLE_OR_NULL("singleOrNull", false, false),

        SINGLE_OR_THROW("singleOrThrow", false, false),

        OBJECT_ITERATOR("objectIterator", true, false),

        OBJECTS_READ_ONLY("objectsReadOnly", true, false),

        OBJECT_STREAM("objectStream", true, false),

        OVERWRITE("overwrite", false, true),

        OVERWRITE_NULLABLE("overwriteNullable", false, true),

        ADD("add", false, true);

        private final String methodName;
        private final boolean plural;
        private final boolean setter;

        Cardinality(final String methodName, final boolean plural, final boolean setter) {
            this.methodName = methodName;
            this.plural = plural;
            this.setter = setter;
        }

        public String getMethodName() {
            return methodName;
        }

        public boolean isPlural() {
            return plural;
        }

        public boolean isSetter() {
            return setter;
        }
    }
}
