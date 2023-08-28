package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Property;
import com.inrupt.rdf.wrapping.declarative.annotation.Resource;

@Resource
interface X {
    @Property
    default Object x() {
        return null;
    }
}
