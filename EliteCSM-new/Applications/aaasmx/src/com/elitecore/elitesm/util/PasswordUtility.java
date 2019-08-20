package com.elitecore.elitesm.util;

import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidPasswordException;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;

public class PasswordUtility {
	
  private static int countAlphabets = 0;
  private static int countDigit=0;
  private static int countSpecialCharacter=0;
  private static String errorMessage="";
  public static void validatePassword(String password,PasswordPolicyConfigData passwordPolicyConfigData) throws InvalidPasswordException{
	  
	  //count total characters,digits,Special characters
	  countCharacters(password);
	  
	  //check for password policy
	  if(passwordPolicyConfigData.getPasswordRange() !=null){
		  	if(!(isValidLength(password,passwordPolicyConfigData)))
		  		throw new InvalidPasswordException("systemparameter.pwdpolicy.passwordlength", passwordPolicyConfigData.getPasswordRange());
	  }else if(passwordPolicyConfigData.getAlphabetRange() != null){
		  	if(!(isValidAlphabets(password,passwordPolicyConfigData)))
		  		throw new InvalidPasswordException("systemparameter.pwdpolicy.alphabets",passwordPolicyConfigData.getAlphabetRange());
	  }else if(passwordPolicyConfigData.getDigitsRange() != null){
		  	if(!(isValidDigits(password,passwordPolicyConfigData)))
		  		throw new InvalidPasswordException("systemparameter.pwdpolicy.digits",passwordPolicyConfigData.getDigitsRange());
	  }else if(passwordPolicyConfigData.getSpecialCharRange() != null){
		  	if(!(isValidSpecialChars(password,passwordPolicyConfigData)))
		  		throw new InvalidPasswordException("systemparameter.pwdpolicy.specialchar",passwordPolicyConfigData.getSpecialCharRange());
	  }else if(passwordPolicyConfigData.getProhibitedChars() != null){
		    if(isProhibitedCharacter(password,passwordPolicyConfigData.getProhibitedChars()))
			  throw new InvalidPasswordException("systemparameter.pwdpolicy.prohibitedchar",passwordPolicyConfigData.getProhibitedChars());
	  }
  }
 
  //check for password contains min-max digits as per password policy
  private static boolean isValidDigits(String password,PasswordPolicyConfigData passwordPolicyConfigData){
		if(countDigit < passwordPolicyConfigData.getMinDigits() || countDigit > passwordPolicyConfigData.getMaxDigits()){
			return false;
		}
		return true;
  }
  
  //check for password min-max password length as per password policy
  private static boolean isValidLength(String password,PasswordPolicyConfigData passwordPolicyConfigData){
		if(password.length() < passwordPolicyConfigData.getMinPasswordLength() || password.length() > passwordPolicyConfigData.getMaxPasswordLength()){
			return false;
		}
		return true;
  }
  
  //check for password min-max Alphabets Range as per password policy
  private static boolean isValidAlphabets(String password,PasswordPolicyConfigData passwordPolicyConfigData){
		if(countAlphabets < passwordPolicyConfigData.getMinAlphabates() || countAlphabets >passwordPolicyConfigData.getMaxAlphabates()){
			return false;
		}
		return true;
  }
  
  //check for special characters
  private static boolean isValidSpecialChars(String password,PasswordPolicyConfigData passwordPolicyConfigData){
		if(countSpecialCharacter <passwordPolicyConfigData.getMinSpecialChars() || countSpecialCharacter>passwordPolicyConfigData.getMaxSpecialChars()){
			return false;	
		}
		return true;
  }
  
  //check for prohibited characters
  private static boolean isProhibitedCharacter(String str,String prohibitedChars){
	   char[] cArray = prohibitedChars.toCharArray();
	   for (int i=0;i<str.length();++i) {
		   for(int j=0;j<cArray.length;++j){
			   int ascii1 = (int) str.charAt(i);
			   int ascii2 = (int) cArray[j];
			   
			   if(ascii1==ascii2){
				   return true;
			   }
		   }
	   }
	   return false;
  }
  
  //count total numbers of digit,characters and special characters
  private static void countCharacters(String password){
	  countAlphabets=0;
	  countDigit=0;
	  countSpecialCharacter=0;
	  for (int i = 0; i < password.length(); i++) {
          if (Character.isLetter(password.charAt(i)))
        	  countAlphabets++;
         
          if (Character.isDigit(password.charAt(i)))
        	  countDigit++; 
          
          if (!Character.isLetterOrDigit(password.charAt(i)))
			   countSpecialCharacter++;
      }
   }
 }
