/*
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 */

package com.elitecore.passwordutil;

public class CaesarCipher { 

	public final  String[] UNENCRYPT_LIST = {"nikhil","sanjay","hemal" ,"harish"};
	public final  String NO_USER = "12NO_USER";
	private static final int DEFALUT_SHIFT_VALUE = 5; //kept for backward compatibility 
	
	private int shiftValue;
	
	public CaesarCipher(){
		this.shiftValue =  DEFALUT_SHIFT_VALUE;
	}
	
	public CaesarCipher(int shiftValue){
		this.shiftValue = shiftValue;
	}


	/**
	 * To check if second string is the encrypted string of first string
	 * 
	 * @param psswd
	 * @param encrypted
	 * @return
	 */
    public  boolean checkEncrypted(String psswd ,String encrypted) {
    	return(encrypted.equals(encryptcrc(psswd)));
	}
	
   /**
    * To encrypt a string using CRC32
	* @param passwd- string to be encrypted
	* @return String- encrypted string
	*
	*/
    public  String encryptcrc(String passwd) {
    	/*CRC32 ch = new CRC32();
		ch.update(passwd.getBytes());
		return (Long.toString(ch.getValue()));*/
		String encst="";
		for(int i = 0; i< passwd.length() ; i++){
			char temp = (char)((int)passwd.charAt(i) + shiftValue);
			encst = encst + temp;
		}
		return encst;
    }

    /**
     * To decrpyt a string using CRC32
	 * @param passwd- string to be encrypted
	 * @return String- decrypted string
	 *
     */
    public  String decryptcrc(String username,String passwd) {
    	if (!isDecryptAllowed(username))
    		return "whypasswd";
    	
    	String encst="";
		for(int i = 0; i< passwd.length() ; i++){
			char temp = (char)((int)passwd.charAt(i) - shiftValue);
			encst = encst + temp;
		}
		return encst;
	 }
     
    public  boolean isDecryptAllowed(String username){
    	int size = UNENCRYPT_LIST.length;
        //Check if found in unecnrytpt list then return false
        if (username.equals(NO_USER))
        	return true;
        for ( int i = 0; i < size ; i++){
        	if (username.equalsIgnoreCase(UNENCRYPT_LIST[i]))
            	return false;
        }
            //If not found in list then
        return true;
    }

	public String decryptcrc(String passwd){
		return decryptcrc(NO_USER,passwd);
    }
}
