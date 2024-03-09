// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.DatasetProperty;
import com.inrupt.rdf.wrapping.annotation.Graph;

@Dataset
interface X {
    @DatasetProperty("x")
    Y x();

    @Graph
    interface Y {
    }
}
