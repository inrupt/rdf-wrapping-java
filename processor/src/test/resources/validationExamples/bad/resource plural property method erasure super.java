// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality.OBJECTS_READ_ONLY;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.ValueMapping.IRI_AS_STRING;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

@Resource
interface X {
    @ResourceProperty(predicate = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Iterable<String> x();
}
