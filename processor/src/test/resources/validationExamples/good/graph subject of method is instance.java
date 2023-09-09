// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstSubjectOfEither;
import com.inrupt.rdf.wrapping.annotation.Resource;

@Graph
interface X {
    @OptionalFirstSubjectOfEither("x")
    Y x();

    @Resource
    interface Y {
    }
}
