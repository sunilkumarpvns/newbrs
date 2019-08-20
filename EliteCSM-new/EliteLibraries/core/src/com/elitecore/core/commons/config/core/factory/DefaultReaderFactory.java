package com.elitecore.core.commons.config.core.factory;

import com.elitecore.core.commons.config.core.Reader;
import com.elitecore.core.commons.config.util.ReflectionUtil;

/**
 * Created by harsh on 5/6/15.
 */
public class DefaultReaderFactory implements ReaderFactory {

    @Override
    public Reader createInstance(Class<? extends  Reader> readerClass) {
        try {
            return ReflectionUtil.createInstance(readerClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
