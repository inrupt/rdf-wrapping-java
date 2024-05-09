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
package com.inrupt.rdf.wrapping.processor;

import java.util.Locale;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class WrapperProcessingEnvironment implements ProcessingEnvironment {
    protected final ProcessingEnvironment original;

    WrapperProcessingEnvironment(final ProcessingEnvironment original) {
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
}
