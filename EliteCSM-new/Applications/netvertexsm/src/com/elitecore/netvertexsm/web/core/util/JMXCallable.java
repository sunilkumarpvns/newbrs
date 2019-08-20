package com.elitecore.netvertexsm.web.core.util;

import java.util.concurrent.Callable;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertexsm.util.remotecommunications.RemoteMethodInvocator;

public class JMXCallable<T> implements Callable<T> {

		private final Object[] objParameter;
		private final String[] parameterTypes ;
		private final String methodName;
		private final String mBeanObject;
		
		public JMXCallable(String mBeanObject,String methodName, Object[] objParameter, String[] parameterTypes){
			this.objParameter = objParameter;
			this.parameterTypes = parameterTypes;
			this.methodName = methodName;
			this.mBeanObject = mBeanObject;
			
		}
		@Override
		public T call() throws Exception {
			return (T) RemoteMethodInvocator.getInstance().invokeRemoteMethod(mBeanObject, methodName, objParameter,parameterTypes);
		}

}
