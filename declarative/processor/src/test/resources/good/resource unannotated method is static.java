package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Resource;

@Resource
interface X {
    static Object x() {
        return null;
    }
}
