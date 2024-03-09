// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.Property.ValueMapping.IRI_AS_STRING;

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.annotation.Resource;

@Resource
interface X {
    @Property(predicate = "x", valueMapping = IRI_AS_STRING)
    void x();
}
