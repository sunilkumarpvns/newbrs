package com.elitecore.exprlib.compiler;

/**
 * Class only to be used in test cases, that allows breaking singleton-ness of {@link Compiler}.
 * 
 * <p>
 * This class is useful for creating fresh compiler instance because Compiler.getDefaultCompiler() returns 
 * a Compiler that contains pre-baked functions already registered. This does not allow testable function 
 * instances to be added in Compiler.
 * 
 * @author narendra.pathai
 *
 */
public class CompilerFactory {

	/**
	 * Creates a new instance of expression library compiler.
	 * <p>
	 * <b>NOTE: </b>Returned compiler does not have any registered functions, it is the responsibility of user
	 * to add any required functions.
	 */
	public Compiler newCompiler() {
		return new Compiler();
	}
}
