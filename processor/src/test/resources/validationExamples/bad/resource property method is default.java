// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

@Resource
interface X {
    @ResourceProperty("x")
    default Object x() {
        return null;
    }
}
