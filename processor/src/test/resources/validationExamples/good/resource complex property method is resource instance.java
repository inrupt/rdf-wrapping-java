// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.Property.Cardinality.ANY_OR_NULL;
import static com.inrupt.rdf.wrapping.annotation.Property.Mapping.AS;

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.annotation.Resource;

@Resource
interface X {
    @Property(predicate = "x", cardinality = ANY_OR_NULL, mapping = AS)
    X x();
}
