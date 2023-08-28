package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Graph;
import com.inrupt.rdf.wrapping.declarative.annotation.OptionalFirstInstanceOfEither;
import com.inrupt.rdf.wrapping.declarative.annotation.Resource;

@Graph
interface X {
    @OptionalFirstInstanceOfEither("x")
    Y x();

    @Resource
    interface Y {
    }
}
