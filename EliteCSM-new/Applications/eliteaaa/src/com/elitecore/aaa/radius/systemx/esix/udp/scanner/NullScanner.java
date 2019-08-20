package com.elitecore.aaa.radius.systemx.esix.udp.scanner;

import com.elitecore.core.commons.InitializationFailedException;

/**
 * Place holder scanner that does not perform any scanning
 * 
 * @author narendra.pathai
 * @author kuldeep.panchal
 *
 */
public class NullScanner implements StatusScanner {

	public static final NullScanner INSTANCE = new NullScanner();
	
	@Override
	public void init() throws InitializationFailedException {
		// no-op
	}

	@Override
	public void scan() {
		//no-op
	}
}
