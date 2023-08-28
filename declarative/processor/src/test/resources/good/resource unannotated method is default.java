package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Resource;

@Resource
interface X {
    default Object x() {
        return null;
    }
}
