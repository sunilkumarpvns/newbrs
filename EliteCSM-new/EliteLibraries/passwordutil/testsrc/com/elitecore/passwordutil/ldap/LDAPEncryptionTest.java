package com.elitecore.passwordutil.ldap;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;

@RunWith(JUnitParamsRunner.class)
public class LDAPEncryptionTest {

	private static final String ANY = "any";

	private LDAPEncryption ldapEncryption = new LDAPEncryption();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public Object[][] dataFor_LDAPEncryption(){
		return new Object[][]{
				{"{MD5}OFj2IjCsPJFfMAxmQxLGPw==", "foobar"},
				{"{SHA}iEPX+SQWIR3p67lj/0zigSWTKHg=", "foobar"},
				{"{SSHA}c5kRS2c0BN+6z8M9OvR7M5Ts2JQ=", "sshaInput#"},
				{"{SMD5}geiVGkQJ1NbH9eNrAqtd5pTs2JQ=", "smd5Input#@"}
				// TODO crypt method not checked yet
		};
	}
	
	@Test
	@Parameters(method = "dataFor_LDAPEncryption")
	public void matchesPlainTextWithEncryptedText(String encryptedText, String PlainText) {
		assertTrue(ldapEncryption.matches(encryptedText, PlainText));
	}
	
	public Object[][] dataFor_DoesNotMatchEncryptionTextIsDifferentThanGeneratedValue() {
		
		return new Object[][] {
				{"{MD5}OFj2IjCsPJFfMAxmQxLGPwd==", "foobar"},
				{"{SHA}iEPX+SQWIR3p67lj/0zigSWdTKHg=", "foobar"},
				{"{SSHA}c5kRS2c0BN+6z8M9OvR7M5Tds2JQ=", "sshaInput#"},
				{"{SMD5}geiVGkQJ1NbH9eNrAqtd5pTs2JQ==","smd5Input#@"}
				// TODO crypt method not checked yet
		};
	}
	
	@Test
	@Parameters(method = "dataFor_DoesNotMatchEncryptionTextIsDifferentThanGeneratedValue")
	public void doesNotMatchEncryptionTextIsDifferentThanGeneratedValue(String encryptedText, String PlainText) {
		assertFalse(ldapEncryption.matches(encryptedText, PlainText));
	}
	
	public Object[][] dataFor_DoesNotMatchEncryptionText_WhenLDAPMethodIsInvalid() {
		
		return new Object[][] {
				{"{notExist}geiVGkQJ1NbH9eNrAqtd5pTs2JQ==", "smd5Input#@"},
				{null, "foobar"},
				{"foobar", null},
				{"{}c5kRS2c0BN+6z8M9OvR7M5Ts2JQ=", "sshaInput#"},
				{"c5kRS2c0BN+6z8M9OvR7M5Ts2JQ=", "sshaInput#"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_DoesNotMatchEncryptionText_WhenLDAPMethodIsInvalid")
	public void doesNotMatchEncryptionText_WhenLDAPMethodIsInvalid(String encyrptedText, String plainText) {
		assertFalse(ldapEncryption.matches(encyrptedText, plainText));
	}

	@Test
	public void decryptionNotSupported() throws DecryptionNotSupportedException, DecryptionFailedException{
		exception.expect(DecryptionNotSupportedException.class);
		ldapEncryption.decrypt(ANY);
	}
}
