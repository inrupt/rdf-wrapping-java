package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Graph;
import com.inrupt.rdf.wrapping.declarative.annotation.OptionalFirstSubjectOfEither;
import com.inrupt.rdf.wrapping.declarative.annotation.Resource;

@Graph
interface X {
    @OptionalFirstSubjectOfEither("x")
    Y x();

    @Resource
    interface Y {
    }
}
