package com.elitecore.config.core.factory;

import com.elitecore.config.core.Writer;
import com.elitecore.config.util.ReflectionUtil;

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
