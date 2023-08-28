package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Graph;
import com.inrupt.rdf.wrapping.declarative.annotation.OptionalFirstSubjectOfEither;

@Graph
interface X {
    @OptionalFirstSubjectOfEither("x")
    default Object x() {
        return null;
    }
}
