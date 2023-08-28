package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Graph;

@Graph
interface X {
    default Object x() {
        return null;
    }
}
