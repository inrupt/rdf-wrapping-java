// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstObjectOfEither;

@Graph
interface X {
    @OptionalFirstObjectOfEither("x")
    Object x();
}
