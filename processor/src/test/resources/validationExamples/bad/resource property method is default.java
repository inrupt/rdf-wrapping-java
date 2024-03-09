// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.ValueMapping.IRI_AS_STRING;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

@Resource
interface X {
    @ResourceProperty(predicate = "x", valueMapping = IRI_AS_STRING)
    default Object x() {
        return null;
    }
}
