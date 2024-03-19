// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.GraphProperty;

@Graph
interface X {
    @GraphProperty("x")
    static Object x() {
        return null;
    }
}
