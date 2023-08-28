package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Dataset;
import com.inrupt.rdf.wrapping.declarative.annotation.DefaultGraph;

@Dataset
interface X {
    @DefaultGraph
    default Object x() {
        return null;
    }
}
