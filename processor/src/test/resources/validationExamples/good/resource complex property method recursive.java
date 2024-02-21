// This file is compiled only in test.
package x;

import static com.inrupt.rdf.wrapping.annotation.Property.Mapping.AS;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstInstanceOfEither;
import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.annotation.Resource;

@Graph
interface X {
    @OptionalFirstInstanceOfEither("x")
    Y x();

    @Resource
    interface Y {
        @Property(predicate = "x", mapping = AS)
        Y x();
    }
}
