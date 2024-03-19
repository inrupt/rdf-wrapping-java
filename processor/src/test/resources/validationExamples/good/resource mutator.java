// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.Cardinality.*;
import static com.inrupt.rdf.wrapping.annotation.ResourceProperty.NodeMapping.AS_TYPED_LITERAL;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

@Resource
interface X {
    @ResourceProperty(value = "x", cardinality = OVERWRITE)
    void overwrite(final X value);

    @ResourceProperty(value = "x", cardinality = OVERWRITE, nodeMapping = AS_TYPED_LITERAL)
    void overwrite(final Integer value);

    @ResourceProperty(value = "x", cardinality = OVERWRITE, nodeMapping = AS_TYPED_LITERAL)
    void overwrite(final Iterable<Integer> value);

    @ResourceProperty(value = "x", cardinality = OVERWRITE_NULLABLE)
    void overwriteNullable(final X value);

    @ResourceProperty(value = "x", cardinality = OVERWRITE_NULLABLE, nodeMapping = AS_TYPED_LITERAL)
    void overwriteNullable(final Integer value);

    @ResourceProperty(value = "x", cardinality = OVERWRITE_NULLABLE, nodeMapping = AS_TYPED_LITERAL)
    void overwriteNullable(final Iterable<Integer> value);

    @ResourceProperty(value = "x", cardinality = ADD)
    void add(final X value);

    @ResourceProperty(value = "x", cardinality = ADD, nodeMapping = AS_TYPED_LITERAL)
    void add(final Integer value);

    @ResourceProperty(value = "x", cardinality = ADD, nodeMapping = AS_TYPED_LITERAL)
    void add(final Iterable<Integer> value);
}
