package com.elitecore.config.core.factory;

import com.elitecore.config.core.Writer;

/**
 * Created by harsh on 5/6/15.
 */
public interface WriterFactory {

    public Writer createInstance(Class<? extends Writer> writerClass)  throws RuntimeException;
}
