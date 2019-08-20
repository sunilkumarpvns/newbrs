package com.elitecore.passwordutil.aes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.InitializationFailedException;

import de.bechte.junit.runners.context.HierarchicalContextRunner;


@RunWith(HierarchicalContextRunner.class)
public class EliteAESEncryptionTest {

	private EliteAESEncryption eliteAESEncryption =  new EliteAESEncryption();;
	@Rule public ExpectedException exception = ExpectedException.none(); 

	@Test
	public void throwsAnEncryptionFailedExceptionWhileEncryptionIfInitIsNotCalled() throws EncryptionFailedException {
		exception.expect(EncryptionFailedException.class);
		exception.expectMessage("Error while encrypting the data");
		eliteAESEncryption.crypt("Sterlite Tech Elitecore");
	}
	
	@Test
	public void throwsDecryptionFailedExceptionWhileDecryptionIfInitIsNotCalled() throws DecryptionNotSupportedException, DecryptionFailedException {
		exception.expect(DecryptionFailedException.class);
		exception.expectMessage("Error while decrypting the data");
		eliteAESEncryption.decrypt("3a29271abc43aaac27268f44657fbc503129f8e4bf1707d6c8cdd8c30cc124be");
	}

	@Test
	public void throwsEncryptionFailedExceptionWhileMatchingEncryptedAndPlainTextIfInitIsNotCalled() throws EncryptionFailedException {
		exception.expect(EncryptionFailedException.class);
		exception.expectMessage("Error while matching the data");
		eliteAESEncryption.matches("3a29271abc43aaac27268f44657fbc503129f8e4bf1707d6c8cdd8c30cc124be", "Sterlite Tech Elitecore");
	}


	public class TestCasesWithInitializationContext {

		@Before
		public void setUp() throws InitializationFailedException {
			eliteAESEncryption.init(new String());
		}

		@Test
		public void returnsEncryptedStringOfGivenArgument() throws EncryptionFailedException {
			String encrptedString = eliteAESEncryption.crypt("Sterlite Tech Elitecore");
			assertEquals("3a29271abc43aaac27268f44657fbc503129f8e4bf1707d6c8cdd8c30cc124be", encrptedString);
		}

		@Test
		public void returnsDecryptedStringOfGivenArgument() throws DecryptionNotSupportedException, DecryptionFailedException {
			String decryptedString = eliteAESEncryption.decrypt("3a29271abc43aaac27268f44657fbc503129f8e4bf1707d6c8cdd8c30cc124be");
			Assert.assertEquals("Sterlite Tech Elitecore", decryptedString);
		}

		@Test
		public void retrunsTrueIfFirstArgumentIsEqualsToEncryptedValueOfSecondArgument() throws EncryptionFailedException {
			assertTrue(eliteAESEncryption.matches("3a29271abc43aaac27268f44657fbc503129f8e4bf1707d6c8cdd8c30cc124be", "Sterlite Tech Elitecore"));
		}
		
		@Test
		public void retrunsFalseIfFirstArgumentIsNotEqualsToEncryptedValueOfSecondArgument() throws EncryptionFailedException {
			assertFalse(eliteAESEncryption.matches("3a29271abc43aaac27268f44657fbc503129f8e4bf1707d6c8cdd8c30cc124be", " Sterlite Tech Elitecore "));
		}

		@Test
		public void throwsInitializationFailedException_WhenKeyLengthIsNot_16_24_Or_32() throws InitializationFailedException {
			exception.expect(InitializationFailedException.class);
			exception.expectMessage("Error while initialization");
			eliteAESEncryption.init("key");
		}

		@Test
		public void keyLengthMustBe16_24_32() throws InitializationFailedException {
			eliteAESEncryption.init("aaaaaaaaaaaaaaaa");
			eliteAESEncryption.init("aaaaaaaaaaaaaaaaaaaaaaaa");
			eliteAESEncryption.init("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		}
	}

}
