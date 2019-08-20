package com.elitecore.passwordutil.threedes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
public class Elite3DESEncryptionTest {

	private Elite3DESEncryption elite3DesEncryption = new Elite3DESEncryption();
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	public class FunctionalTests{

		@Before
		public void setUp() throws InitializationFailedException{
			elite3DesEncryption.init("El!T3C0re+c1p#3R+1lG0r!t#m");
		}

		@Test
		public void returns3DESEncryptedStringOfGivenInputString() throws EncryptionFailedException{
			assertEquals(elite3DesEncryption.crypt("foo"),"9186b906134d69fd");
		}

		@Test
		public void returns3DESDecryptedStringOfGivenInputString() throws DecryptionNotSupportedException, DecryptionFailedException{
			assertEquals(elite3DesEncryption.decrypt("9186b906134d69fd"),"foo");
		}

		@Test
		public void matchesPlainTextWithEncryptedText() throws EncryptionFailedException{
			assertTrue(elite3DesEncryption.matches("9186b906134d69fd", "foo"));
		}

		@Test
		public void doesNotMatchEncryptionTextIsDifferentThanGeneratedValue() throws EncryptionFailedException{
			assertFalse(elite3DesEncryption.matches("9186b906134d69f", "foo"));
		}
	}

	public class DeveloperTests {
		
		private static final String INVALID_KEY = "foo";
		private static final String ANY_VALUE = "any";

		@Test
		public void throwsInitializationFailedExceptionIfKeyIsInvalid() throws InitializationFailedException{
			exception.expect(InitializationFailedException.class);
			
			elite3DesEncryption.init(INVALID_KEY);
		}
		
		@Test
		public void throwsEncryptionFailedExceptionIfNotInitialized() throws EncryptionFailedException{
			exception.expect(EncryptionFailedException.class);
			
			elite3DesEncryption.crypt(ANY_VALUE);
		}
		
		@Test
		public void throwsDecryptionFailedExceptionIfNotInitialized() throws  DecryptionFailedException, DecryptionNotSupportedException{
			exception.expect(DecryptionFailedException.class);
			
			elite3DesEncryption.decrypt(ANY_VALUE);
		}
		
		@Test
		public void throwsEncryptionFailedExceptionWhileMatchingIfNotInitialized() throws EncryptionFailedException{
			exception.expect(EncryptionFailedException.class);
			
			elite3DesEncryption.matches(ANY_VALUE, ANY_VALUE);
		}
	}
}
