package x;

import com.inrupt.rdf.wrapping.declarative.annotation.Dataset;

@Dataset
interface X {
    default Object x() {
        return null;
    }
}
