// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstInstanceOfEither;
import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

@Graph
interface X {
    @OptionalFirstInstanceOfEither("x")
    Y x();

    @Resource
    interface Y {
        @ResourceProperty(predicate = "x")
        Y x();
    }
}
