package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Dataset;
import com.inrupt.rdf.wrapping.declarative.annotation.DefaultGraph;
import com.inrupt.rdf.wrapping.declarative.annotation.Graph;

@Dataset
interface X {
    @DefaultGraph
    Y x();

    @Graph
    interface Y {
    }
}
