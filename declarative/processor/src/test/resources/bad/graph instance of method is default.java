package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Graph;
import com.inrupt.rdf.wrapping.declarative.annotation.OptionalFirstInstanceOfEither;

@Graph
interface X {
    @OptionalFirstInstanceOfEither("x")
    default Object x() {
        return null;
    }
}
