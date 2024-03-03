// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.Property.Cardinality.OBJECTS_READ_ONLY;
import static com.inrupt.rdf.wrapping.annotation.Property.Mapping.IRI_AS_STRING;

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.annotation.Resource;

@Resource
interface X {
    @Property(predicate = "x", cardinality = OBJECTS_READ_ONLY, mapping = IRI_AS_STRING)
    Iterable<String> x();
}
