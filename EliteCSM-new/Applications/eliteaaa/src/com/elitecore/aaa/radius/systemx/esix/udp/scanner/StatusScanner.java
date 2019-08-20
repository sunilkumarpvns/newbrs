package com.elitecore.aaa.radius.systemx.esix.udp.scanner;

import com.elitecore.core.commons.InitializationFailedException;


public interface StatusScanner {

		public void init() throws InitializationFailedException;
		public void scan();
}
