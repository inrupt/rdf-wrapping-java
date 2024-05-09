// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality.OVERWRITE;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

@Resource
interface X {
    @ResourceProperty(value = "x", cardinality = OVERWRITE)
    void x(final Iterable<X> value);
}
