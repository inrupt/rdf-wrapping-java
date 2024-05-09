// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality.OBJECT_ITERATOR;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.ValueMapping.IRI_AS_STRING;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

import java.util.Iterator;

@Resource
interface X {
    @ResourceProperty(value = "x", cardinality = OBJECT_ITERATOR, valueMapping = IRI_AS_STRING)
    Iterator<Integer> x();
}
