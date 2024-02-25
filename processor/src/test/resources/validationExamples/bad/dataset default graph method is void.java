// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.DefaultGraph;
import com.inrupt.rdf.wrapping.annotation.NamedGraph;

@Dataset
interface X {
    @DefaultGraph
    void x();
}
