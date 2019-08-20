package com.elitecore.coreeap.cipher.providers;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.elitecore.coreeap.cipher.providers.DefaultCipherProvider;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;

public class DefaultCipherProviderTest extends TestCase {

	private DefaultCipherProvider.BlockCipher provider_TLS_1;
	private DefaultCipherProvider.BlockCipher provider_TLS_2;
	private DefaultCipherProvider.BlockCipher provider_TLS_3;

	@Before
	public void setUp(){
		provider_TLS_1 = new DefaultCipherProvider(anyCipher(), ProtocolVersion.TLS1_0, true).new BlockCipher();
		provider_TLS_2 = new DefaultCipherProvider(anyCipher(), ProtocolVersion.TLS1_1, true).new BlockCipher();
		provider_TLS_3 = new DefaultCipherProvider(anyCipher(), ProtocolVersion.TLS1_2, true).new BlockCipher();
	}

	private CipherSuites anyCipher(){
		CipherSuites[] cipherSuites = CipherSuites.values();
		Collections.shuffle(Arrays.asList(cipherSuites));
		return cipherSuites[0];
	}

	/**
	 * 	From here testing of validatePadding() procedure starts.
	 * 	This test cases are tls version independant
	 */

	/*-----------------------------------------------------------------------------------------------------------------------------*/

	@Test
	public void testValidPaddingBytes(){
		final byte[] VALID_PADDDING_BYTES = {
				47, 58, 4, 5, 54, 65, 12, 45,
				65, 78, 54, 95, 65, 25, 14, 12,
				24, 32, 55, 12, 14, 15, 16, 32,
				54, 15, 14, 21, 23, 2, 11, 45, 82, 	// rand bytes
				7, 7, 7, 7, 7, 7, 7, 7 				// 7 byte padding 
		};

		assertTrue(provider_TLS_1.validatePadding(VALID_PADDDING_BYTES, VALID_PADDDING_BYTES[VALID_PADDDING_BYTES.length - 1]));
		assertTrue(provider_TLS_2.validatePadding(VALID_PADDDING_BYTES, VALID_PADDDING_BYTES[VALID_PADDDING_BYTES.length - 1]));
		assertTrue(provider_TLS_3.validatePadding(VALID_PADDDING_BYTES, VALID_PADDDING_BYTES[VALID_PADDDING_BYTES.length - 1]));
	}

	@Test
	public void testInvalidPadiingBytes(){
		final byte[] INVALID_PADDING_BYTES = {
				47, 58, 4, 5, 54, 65, 12, 45,
				65, 78, 54, 95, 65, 25, 14, 12,
				24, 32, 55, 12, 14, 15, 16, 32,
				54, 15, 14, 21, 23, 2, 11, 45, 82, 	// rand bytes
				1, 2, 3, 4, 5, 6, 7, 7 				// 7 byte padding
		};

		assertFalse(provider_TLS_1.validatePadding(INVALID_PADDING_BYTES, INVALID_PADDING_BYTES[INVALID_PADDING_BYTES.length - 1]));
		assertFalse(provider_TLS_2.validatePadding(INVALID_PADDING_BYTES, INVALID_PADDING_BYTES[INVALID_PADDING_BYTES.length - 1]));
		assertFalse(provider_TLS_3.validatePadding(INVALID_PADDING_BYTES, INVALID_PADDING_BYTES[INVALID_PADDING_BYTES.length - 1]));
	}

	@Test
	public void testPaddingLengthLargerThanArraySize(){
		final byte[] PADDING_LENGTH_LARGER_THAN_ARRAY_SIZE = {
				47, 58, 4, 5, 54, 65, 12, 45,
				65, 78, 54, 95, 65, 25, 14, 12,
				24, 32, 55, 12, 14, 15, 16, 32,
				54, 15, 14, 21, 23, 2, 11, 45, 82, 	// rand bytes
				1, 2, 3, 4, 5, 6, 7, 80 			// 7 byte padding
		};

		assertFalse(provider_TLS_1.validatePadding(PADDING_LENGTH_LARGER_THAN_ARRAY_SIZE, PADDING_LENGTH_LARGER_THAN_ARRAY_SIZE[PADDING_LENGTH_LARGER_THAN_ARRAY_SIZE.length - 1]));
		assertFalse(provider_TLS_2.validatePadding(PADDING_LENGTH_LARGER_THAN_ARRAY_SIZE, PADDING_LENGTH_LARGER_THAN_ARRAY_SIZE[PADDING_LENGTH_LARGER_THAN_ARRAY_SIZE.length - 1]));
		assertFalse(provider_TLS_3.validatePadding(PADDING_LENGTH_LARGER_THAN_ARRAY_SIZE, PADDING_LENGTH_LARGER_THAN_ARRAY_SIZE[PADDING_LENGTH_LARGER_THAN_ARRAY_SIZE.length - 1]));
	}

	@Test
	public void testInvalidPaddingLength(){
		final byte[] INVALID_PADDING_LENGTH = {
				47, 58, 4, 5, 54, 65, 12, 45,
				65, 78, 54, 95, 65, 25, 14, 12,
				24, 32, 55, 12, 14, 15, 16, 32,
				54, 15, 14, 21, 23, 2, 11, 45, 82, 	// rand bytes
				8, 8, 8, 8, 8, 8, 8, 8 				// 7 byte padding
		};

		assertFalse(provider_TLS_1.validatePadding(INVALID_PADDING_LENGTH, INVALID_PADDING_LENGTH[INVALID_PADDING_LENGTH.length - 1]));
		assertFalse(provider_TLS_2.validatePadding(INVALID_PADDING_LENGTH, INVALID_PADDING_LENGTH[INVALID_PADDING_LENGTH.length - 1]));
		assertFalse(provider_TLS_3.validatePadding(INVALID_PADDING_LENGTH, INVALID_PADDING_LENGTH[INVALID_PADDING_LENGTH.length - 1]));
	}

	@Test
	public void testInvalidFirstByteInPadding(){
		final byte[] INVALID_FIRST_BYTE_IN_PADDING = {
				47, 58, 4, 5, 54, 65, 12, 45,
				65, 78, 54, 95, 65, 25, 14, 12,
				24, 32, 55, 12, 14, 15, 16, 32,
				54, 15, 14, 21, 23, 2, 11, 45, 82, 	// rand bytes
				6, 7, 7, 7, 7, 7, 7, 7 				// 7 byte padding 
		};

		assertFalse(provider_TLS_1.validatePadding(INVALID_FIRST_BYTE_IN_PADDING, INVALID_FIRST_BYTE_IN_PADDING[INVALID_FIRST_BYTE_IN_PADDING.length - 1]));
		assertFalse(provider_TLS_2.validatePadding(INVALID_FIRST_BYTE_IN_PADDING, INVALID_FIRST_BYTE_IN_PADDING[INVALID_FIRST_BYTE_IN_PADDING.length - 1]));
		assertFalse(provider_TLS_3.validatePadding(INVALID_FIRST_BYTE_IN_PADDING, INVALID_FIRST_BYTE_IN_PADDING[INVALID_FIRST_BYTE_IN_PADDING.length - 1]));
	}

	@Test
	public void testInvalidMiddleByteInPadding(){
		final byte[] INVALID_MIDDLE_BYTE_IN_PADDING = {
				47, 58, 4, 5, 54, 65, 12, 45,
				65, 78, 54, 95, 65, 25, 14, 12,
				24, 32, 55, 12, 14, 15, 16, 32,
				54, 15, 14, 21, 23, 2, 11, 45, 82, 	// rand bytes
				7, 7, 7, 7, 8, 7, 7, 7 				// 7 byte padding 
		};

		assertFalse(provider_TLS_1.validatePadding(INVALID_MIDDLE_BYTE_IN_PADDING, INVALID_MIDDLE_BYTE_IN_PADDING[INVALID_MIDDLE_BYTE_IN_PADDING.length - 1]));
		assertFalse(provider_TLS_2.validatePadding(INVALID_MIDDLE_BYTE_IN_PADDING, INVALID_MIDDLE_BYTE_IN_PADDING[INVALID_MIDDLE_BYTE_IN_PADDING.length - 1]));
		assertFalse(provider_TLS_3.validatePadding(INVALID_MIDDLE_BYTE_IN_PADDING, INVALID_MIDDLE_BYTE_IN_PADDING[INVALID_MIDDLE_BYTE_IN_PADDING.length - 1]));
	}

	/*-----------------------------------------------------------------------------------------------------------------------------*/

}