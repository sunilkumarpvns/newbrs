package com.elitecore.corenetvertex.spr;


public interface RecordProcessor<T> {
	public void process(T t) throws ProcessFailException;
	public void stop();

	class EmptyRecordProcessor<T> implements RecordProcessor<T> {

		@Override
		public void process(T t) throws ProcessFailException {
			//IGNORE
		}

		@Override
		public void stop() {
			//IGNORE
		}
	}
}
