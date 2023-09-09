// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstInstanceOfEither;

@Graph
interface X {
    @OptionalFirstInstanceOfEither("x")
    default Object x() {
        return null;
    }
}
