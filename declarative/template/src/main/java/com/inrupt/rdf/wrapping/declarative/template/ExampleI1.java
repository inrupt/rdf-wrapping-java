package com.inrupt.rdf.wrapping.declarative.template;

import org.apache.jena.query.Dataset;

public interface ExampleI1 {
    interface ExampleI2 {
        static ExampleI2 wrap(final Dataset original) {
            return Manager.wrap(ExampleI2.class, original);
        }

    }
}
