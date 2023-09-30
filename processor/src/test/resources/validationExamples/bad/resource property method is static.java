// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Property;
import com.inrupt.rdf.wrapping.annotation.Resource;

@Resource
interface X {
    @Property("x")
    default Object x() {
        return null;
    }
}
