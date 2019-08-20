package com.elitecore.core.commons.config.core.factory;

import com.elitecore.core.commons.config.core.Writer;
import com.elitecore.core.commons.config.util.ReflectionUtil;

/**
 * Created by harsh on 5/6/15.
 */
public class DefaultWriterFactory implements WriterFactory {

    @Override
    public Writer createInstance(Class<? extends Writer> writerClass) {
        try {
            return ReflectionUtil.createInstance(writerClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
