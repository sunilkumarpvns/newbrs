package com.elitecore.corenetvertex.pm;

import com.elitecore.config.core.Writer;
import com.elitecore.config.core.factory.WriterFactory;

public class HibernateWriterFactory implements WriterFactory {

	public HibernateWriterFactory(String hibernateConfigurationFilePath) {
	}

	@Override
	public Writer createInstance(Class<? extends Writer> writerClass) throws RuntimeException {
		return null;
	}

}
