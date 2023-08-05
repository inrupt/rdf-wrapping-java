package com.inrupt.rdf.wrapping.declarative.template;

// @Resource
public interface ExampleNode1 {
    // @Property(predicate = "urn:example:p1", type = PropertyMappingTypes.anyOrNull, mapping = ValueMappingTypes.as)
    ExampleNode2 getP1();
}
