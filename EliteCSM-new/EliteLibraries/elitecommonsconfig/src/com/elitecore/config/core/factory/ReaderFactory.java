package com.elitecore.config.core.factory;

import com.elitecore.config.core.Reader;

/**
 * Created by harsh on 5/6/15.
 */
public interface ReaderFactory {

    public Reader createInstance(Class<? extends  Reader> readerClass) throws RuntimeException;
}