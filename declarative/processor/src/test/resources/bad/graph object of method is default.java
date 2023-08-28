package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Graph;
import com.inrupt.rdf.wrapping.declarative.annotation.OptionalFirstObjectOfEither;

@Graph
interface X {
    @OptionalFirstObjectOfEither("x")
    default Object x() {
        return null;
    }
}
