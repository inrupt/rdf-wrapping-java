// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality.OBJECTS_READ_ONLY;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

import java.util.Collection;
import java.util.Set;

@Resource
interface X {
    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY)
    Set<? extends X> x1();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY)
    Collection<? extends X> x2();
}
