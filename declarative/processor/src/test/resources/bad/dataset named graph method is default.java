package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Dataset;
import com.inrupt.rdf.wrapping.declarative.annotation.NamedGraph;

@Dataset
interface X {
    @NamedGraph("x")
    default Object x() {
        return null;
    }
}
