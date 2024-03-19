// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Dataset;
import com.inrupt.rdf.wrapping.annotation.DatasetProperty;

@Dataset
interface X {
    @DatasetProperty("x")
    void x();
}
