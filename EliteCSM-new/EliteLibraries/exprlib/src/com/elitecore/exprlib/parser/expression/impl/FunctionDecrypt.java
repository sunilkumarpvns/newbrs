package com.elitecore.exprlib.parser.expression.impl;

import java.io.File;
import java.security.PrivateKey;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.encryptutil.PasswordUtil;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionDecrypt extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;
	public static final String RSA = "RSA";

	@Override
	public String getStringValue(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size() != 3){
			throw new IllegalArgumentException("Number of parameters mismatch, DECRYPT function has 3 arguments 1)Decryption Algorithm Name   2)Decryption Key   3)String to be decrypted");
		}
		
		String algorithmName = argumentList.get(0).getStringValue(valueProvider);
		if(RSA.equalsIgnoreCase(algorithmName)){
			try {
				String decryptionKey = argumentList.get(1).getStringValue(valueProvider);
				String decryptParam  = argumentList.get(2).getStringValue(valueProvider);
				
				File decryptFile = Compiler.getDefaultCompiler().getEncryptionKeyFile(decryptionKey);
				if(decryptFile == null){
					throw new IllegalArgumentException("Encryption Key File not found for key: " + decryptionKey);
				}
				PrivateKey privateKey = PasswordUtil.readPrivateKeyFromFile(decryptFile);
				return PasswordUtil.decryptRSA(decryptParam, privateKey);
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}else{
			throw new IllegalArgumentException("Invalid decryption algorithm specified");
		}
	}

	@Override
	public long getLongValue(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		throw new InvalidTypeCastException("Cannot cast a String to Integer");
	}
	
	@Override
	public int getFunctionType() {
		return 0;
	}

	@Override
	public String getName() {
		return "decrypt";
	}

}
