// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality.OBJECTS_READ_ONLY;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.ValueMapping.IRI_AS_STRING;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

import java.util.Collection;
import java.util.Set;

@Resource
interface X {
    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Set x1();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Set<?> x2();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Set<CharSequence> x3();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Set<? extends CharSequence> x4();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Set<? super CharSequence> x5();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Set<String> x6();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Set<? extends String> x7();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Set<? super String> x8();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Collection x9();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Collection<?> x10();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Collection<CharSequence> x11();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Collection<? extends CharSequence> x12();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Collection<? super CharSequence> x13();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Collection<String> x14();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Collection<? extends String> x15();

    @ResourceProperty(value = "x", cardinality = OBJECTS_READ_ONLY, valueMapping = IRI_AS_STRING)
    Collection<? super String> x16();
}
