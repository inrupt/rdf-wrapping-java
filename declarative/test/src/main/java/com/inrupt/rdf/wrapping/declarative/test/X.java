/*
 * Copyright Inrupt Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.inrupt.rdf.wrapping.declarative.test;

import com.inrupt.rdf.wrapping.declarative.annotations.Dataset;
import com.inrupt.rdf.wrapping.declarative.annotations.Graph;
import com.inrupt.rdf.wrapping.declarative.annotations.Resource;
import com.inrupt.rdf.wrapping.declarative.processor.Manager;

import org.apache.jena.rdf.model.Model;

@Dataset
public interface X {
    static X wrap(final org.apache.jena.query.Dataset original) {
        return Manager.wrap(original, X.class);
    }

    @Graph
    interface Y {
        static Y wrap(final Model original) {
            return Manager.wrap(original, Y.class);
        }

        @Resource
        interface Z {
        }
    }
}
