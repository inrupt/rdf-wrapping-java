/*
 * Copyright 2023 Inrupt Inc.
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
package com.inrupt.rdf.wrapping.test.jena;

import com.inrupt.rdf.wrapping.test.commons.*;

import org.apache.commons.rdf.api.RDF;
import org.apache.jena.commonsrdf.JenaRDF;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Node Mapping (Jena)")
@SuppressWarnings("java:S2187") // Tests in base class
class TermMappingsTest extends TermMappingsBase {
}

@DisplayName("Commons Predicate-Object Set (Jena)")
@SuppressWarnings("java:S2187") // Tests in base class
class ObjectSetTest extends CommonsObjectSetBase {
}

@DisplayName("Value Mapping (Jena)")
@SuppressWarnings("java:S2187") // Tests in base class
class ValueMappingsTest extends ValueMappingsBase {
}

@DisplayName("Wrapper blank node or IRI wrapper (Jena)")
@SuppressWarnings("java:S2187") // Tests in base class
class WrapperBlankNodeOrIRITest extends WrapperBlankNodeOrIRIBase {
}

@DisplayName("RDF factory (Jena)")
@SuppressWarnings("java:S2187") // Tests in base class
class RdfFactoryTest extends RdfFactoryBase {
    @Override
    protected Class<? extends RDF> getRdfClass() {
        return JenaRDF.class;
    }
}
