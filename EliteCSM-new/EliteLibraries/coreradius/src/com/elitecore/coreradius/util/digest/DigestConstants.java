/*
 * @(#)DigestConstants	04/08/2008
 * Elitecore Technologies Ltd.
 */
package com.elitecore.coreradius.util.digest;
/**
 * The <code>DigestConstants</code> class represents constant strings. All
 * string literals in Digest Authentication logic, such as <code>"DRAFT_BASED_DIGEST_REQUEST"</code>, 
 * are define in this class.
 *
 * @author  Devang Adeshara <br>Elitecore Technologies Ltd.</br> 
 * @version 04/08/2008
 * @since   JDK1.5
 */
public class DigestConstants {

	 /**
	  * DRAFT_BASED_DIGEST_REQUEST contains boolean value
	  */
	public static final String DRAFT_BASED_DIGEST_REQUEST = "DRAFT_BASED_DIGEST_REQUEST";
	public static final String DIGEST_HA1 = "DIGEST_HA1";
	public static final String DIGEST_NONCE = "DIGEST_NONCE";
	public static final String USER_NAME = "USER_NAME";
	public static final String USER_PASSWORD =  "USER_PASSWORD"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String DIGEST_ALGORITHM = "DIGEST_ALGORITHM";
	public static final String DIGEST_REALM = "DIGEST_REALM";
	
	/**
	 * Digest draft attribute constants 
	 */
	public static final int DIGEST_DRAFT_REALM         = 1;
	public static final int DIGEST_DRAFT_NONCE         = 2;
	public static final int DIGEST_DRAFT_METHOD        = 3;
	public static final int DIGEST_DRAFT_URI           = 4;
	public static final int DIGEST_DRAFT_QOP           = 5;
	public static final int DIGEST_DRAFT_ALGORITHM     = 6;
	public static final int DIGEST_DRAFT_BODY_DIGEST   = 7;
	public static final int DIGEST_DRAFT_CNONCE        = 8;
	public static final int DIGEST_DRAFT_NONCE_COUNT   = 9;
	public static final int DIGEST_DRAFT_USER_NAME    = 10;
}
