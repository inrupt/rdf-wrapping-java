// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.DefaultGraph;
import com.inrupt.rdf.wrapping.annotation.Graph;

@Dataset
interface X {
    @DefaultGraph
    Y x();

    @Graph
    interface Y {
    }
}
