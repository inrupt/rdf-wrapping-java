// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstObjectOfEither;
import com.inrupt.rdf.wrapping.annotation.Resource;

@Graph
interface X {
    @OptionalFirstObjectOfEither("x")
    Y x();

    @Resource
    interface Y {
    }
}
