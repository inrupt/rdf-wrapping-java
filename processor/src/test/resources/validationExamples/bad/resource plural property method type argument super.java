// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.Property.Cardinality.OBJECTS_READ_ONLY;
import static com.inrupt.rdf.wrapping.annotation.Property.ValueMapping.IRI_AS_STRING;

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.annotation.Resource;

import java.util.Set;

@Resource
interface X {
    @Property(predicate = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Set<CharSequence> x();
}
