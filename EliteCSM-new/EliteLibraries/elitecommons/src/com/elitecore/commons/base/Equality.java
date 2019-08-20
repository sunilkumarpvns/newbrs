package com.elitecore.commons.base;

/**
 * 
 * A static import helper utility for readable implementation of {@code equals()} method.
 * 
 * <p>
 * Example: For implementing equals() method for a class A with definition
 * <code><pre>
 * import static com.elitecore.commons.base.*;
 * 
 * class A{
 * 		private int id;
 * 		private Collection<String> strings;
 * 
 *      ...
 *      
 * 		public boolean equals(Object o){
 * 			if(o instanceof A == false)
 * 				return false;
 * 
 *          A other = (A)o;
 *          return areEqual(this.id,other.id)
 *          		&& areEqual(this.strings, other.strings);
 * 		}
 * }
 * </pre>
 * </code>
 * 
 * @author narendra.pathai
 *
 */
public class Equality {
	
	private Equality() {
		//the class is not meant to be instantiated
	}
	
	/**
	 * To test the equality of two objects. This method is <i>null-safe</i>.
	 * Returns true if both objects are null. It calls {@code equals()} to test
	 * equality if object is not null.
	 * 
	 * @param oThis first object
	 * @param oThat second object
	 * @return true if both objects are null. Returns the output of {@code equals()} when
	 * objects are not null. 
	 */
	public static boolean areEqual(Object oThis, Object oThat) {
		return oThis == null ? oThat == null : oThis.equals(oThat);
	}

	/**
	 * To test the equality of two ints. Does a {@code ==} check for equality.
	 * 
	 * @param iThis first int
	 * @param iThat second int
	 * @return true if both ints bear same value
	 */
	public static boolean areEqual(int iThis, int iThat) {
		return iThis == iThat;
	}
	
	/**
	 * To test the equality of two longs. Does a {@code ==} check for equality.
	 * 
	 * @param lThis first long
	 * @param lThat second long
	 * @return true if both longs bear same value
	 */
	public static boolean areEqual(long lThis, long lThat) {
		return lThis == lThat;
	}
	
	/**
	 * To test the equality of two chars. Does a {@code ==} check for equality.
	 * 
	 * @param cThis first char
	 * @param cThat second char
	 * @return true if both chars bear same value
	 */
	public static boolean areEqual(char cThis, char cThat) {
		return cThis == cThat;
	}
	
	/**
	 * To test the equality of two bytes. Does a {@code ==} check for equality.
	 * 
	 * @param bThis first byte
	 * @param bThat second byte
	 * @return true if both bytes bear same value
	 */
	public static boolean areEqual(byte bThis, byte bThat) {
		return bThis == bThat;
	}
	
	/**
	 * To test the equality of two doubles. Does a {@code ==} check for equality.
	 * 
	 * @param dThis first double
	 * @param dThat second double
	 * @return true if both doubles bear same value
	 */
	public static boolean areEqual(double dThis, double dThat) {
		return dThis == dThat; //NOSONAR - Reason: Floating point numbers should not be tested for equality
	}
	
	/**
	 * To test the equality of two floats. Does a {@code ==} check for equality.
	 * 
	 * @param fThis first float
	 * @param fThat second float
	 * @return true if both floats bear same value
	 */
	public static boolean areEqual(float fThis, float fThat) {
		return fThis == fThat; //NOSONAR - Reason: Floating point numbers should not be tested for equality
	}
}
