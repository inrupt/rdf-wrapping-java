// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.OptionalFirstSubjectOfEither;

@Graph
interface X {
    @OptionalFirstSubjectOfEither("x")
    void x();
}
