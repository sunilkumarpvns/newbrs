package com.elitecore.exprlib.parser.expression.impl;

import java.io.File;
import java.security.PublicKey;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.encryptutil.PasswordUtil;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionEncrypt extends AbstractStringFunctionExpression {
	
	private static final long serialVersionUID = 1L;
	public static final String MD5 = "MD5";
	public static final String RSA = "RSA";
	public static final String SHA = "SHA";
	
	@Override
	public int getFunctionType() {
		return 0;
	}

	@Override
	public String getName() {
		return "encrypt";
	}

	@Override
	public String getStringValue(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size() < 2 || argumentList.size() > 3){
			throw new IllegalArgumentException("Number of parameters mismatch, ENCRYPT function has 2 or 3 arguments 1)Encryption Algorithm Name   2)Encryption Key   3)String to be encrypted");
		}
		
		String algorithmName = argumentList.get(0).getStringValue(valueProvider);
		if(MD5.equalsIgnoreCase(algorithmName)){
			try{
				String encryptParam  = argumentList.get(1).getStringValue(valueProvider);
				return PasswordUtil.cryptMD5(encryptParam);
			}catch(Exception e){
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}else if(SHA.equalsIgnoreCase(algorithmName)){
			try{
				String encryptParam = argumentList.get(1).getStringValue(valueProvider);
				return PasswordUtil.cryptSHA(encryptParam);
			}catch(Exception e){
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}else if(RSA.equalsIgnoreCase(algorithmName)){
			try {
				String encryptionKey = argumentList.get(1).getStringValue(valueProvider);
				String encryptParam  = argumentList.get(2).getStringValue(valueProvider);
				
				File encryptFile = Compiler.getDefaultCompiler().getEncryptionKeyFile(encryptionKey);
				if(encryptFile == null){
					throw new IllegalArgumentException("Encryption Key File not found for key: " + encryptionKey);
				}
				PublicKey pubKey = PasswordUtil.readPublicKeyFromFile(encryptFile);
				return PasswordUtil.cryptRSA(encryptParam, pubKey);
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}else{
			throw new IllegalArgumentException("Invalid encryption algorithm specified");
		}
	}

	@Override
	public long getLongValue(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		throw new InvalidTypeCastException("Cannot cast a String to Integer");
	}

}
