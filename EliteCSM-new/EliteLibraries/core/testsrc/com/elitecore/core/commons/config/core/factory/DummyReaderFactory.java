package com.elitecore.core.commons.config.core.factory;

import com.elitecore.core.commons.config.core.Reader;

public class DummyReaderFactory implements ReaderFactory {
	private Reader reader;

	public void setReader(Reader reader) {
		this.reader = reader;
	}
	
	@Override
	public Reader createInstance(Class<? extends Reader> readerClass) throws RuntimeException {
		return reader;
	}
}