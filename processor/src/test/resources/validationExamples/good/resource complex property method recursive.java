// This file is compiled only in test.
package x;

import com.inrupt.rdf.wrapping.annotation.Graph;
import com.inrupt.rdf.wrapping.annotation.GraphProperty;
import com.inrupt.rdf.wrapping.annotation.Resource;
import com.inrupt.rdf.wrapping.annotation.ResourceProperty;

@Graph
interface X {
    @GraphProperty("x")
    Y x();

    @Resource
    interface Y {
        @ResourceProperty("x")
        Y x();
    }
}
