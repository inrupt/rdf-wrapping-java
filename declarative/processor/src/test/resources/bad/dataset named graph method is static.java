package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Dataset;
import com.inrupt.rdf.wrapping.declarative.annotation.NamedGraph;

@Dataset
interface X {
    @NamedGraph("x")
    static Object x() {
        return null;
    }
}
