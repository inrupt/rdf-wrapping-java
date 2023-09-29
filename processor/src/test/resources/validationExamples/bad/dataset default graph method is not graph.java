// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.DefaultGraph;

@Dataset
interface X {
    @DefaultGraph
    Object x();
}
