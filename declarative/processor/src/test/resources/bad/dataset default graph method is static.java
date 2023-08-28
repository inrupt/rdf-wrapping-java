package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Dataset;
import com.inrupt.rdf.wrapping.declarative.annotation.DefaultGraph;

@Dataset
interface X {
    @DefaultGraph
    static Object x() {
        return null;
    }
}
