package com.inrupt.rdf.wrapping.processor;

import com.inrupt.rdf.wrapping.jena.ValueMappings;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class EnvironmentHelper implements ProcessingEnvironment {
    TypeMirror mirror(final Class<?> clazz) {
        return type(clazz).asType();
    }

    TypeElement type(final Class<?> clazz) {
        return getElementUtils().getTypeElement(clazz.getCanonicalName());
    }

    TypeElement type(final TypeMirror mirror) {
        return (TypeElement) getTypeUtils().asElement(mirror);
    }

    boolean isVoid(final TypeMirror type) {
        return isSameType(type, Void.class);
    }

    boolean isSameType(final TypeMirror t1, final Class<?> t2) {
        return getTypeUtils().isSameType(t1, mirror(t2));
    }

    List<ExecutableElement> methodsOf(final Element element) {
        return ElementFilter.methodsIn(element.getEnclosedElements());
    }

    public List<ExecutableElement> methodsOf(final Class<ValueMappings> clazz) {
        return methodsOf(type(clazz));
    }

    // region pass-through
    private final ProcessingEnvironment original;

    EnvironmentHelper(final ProcessingEnvironment original) {
        this.original = original;
    }

    @Override
    public Map<String, String> getOptions() {
        return original.getOptions();
    }

    @Override
    public Messager getMessager() {
        return original.getMessager();
    }

    @Override
    public Filer getFiler() {
        return original.getFiler();
    }

    @Override
    public Elements getElementUtils() {
        return original.getElementUtils();
    }

    @Override
    public Types getTypeUtils() {
        return original.getTypeUtils();
    }

    @Override
    public SourceVersion getSourceVersion() {
        return original.getSourceVersion();
    }

    @Override
    public Locale getLocale() {
        return original.getLocale();
    }
    // endregion
}
