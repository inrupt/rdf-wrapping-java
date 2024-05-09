// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Graph;

@Graph
interface X {
    static Object x() {
        return null;
    }
}
