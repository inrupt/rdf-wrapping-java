package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Dataset;
import com.inrupt.rdf.wrapping.declarative.annotation.Graph;
import com.inrupt.rdf.wrapping.declarative.annotation.NamedGraph;

@Dataset
interface X {
    @NamedGraph("x")
    Y x();

    @Graph
    interface Y {
    }
}
