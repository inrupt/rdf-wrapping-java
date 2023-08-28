package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Graph;
import com.inrupt.rdf.wrapping.declarative.annotation.OptionalFirstObjectOfEither;
import com.inrupt.rdf.wrapping.declarative.annotation.Resource;

@Graph
interface X {
    @OptionalFirstObjectOfEither("x")
    Y x();

    @Resource
    interface Y {
    }
}
