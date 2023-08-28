package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Graph;
import com.inrupt.rdf.wrapping.declarative.annotation.OptionalFirstInstanceOfEither;

@Graph
interface X {
    @OptionalFirstInstanceOfEither("x")
    static Object x() {
        return null;
    }
}
