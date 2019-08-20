package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.util.Arrays;

import org.junit.Test;

import junit.framework.TestCase;

import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ServerKeyExchange;
import com.elitecore.coreeap.util.tls.TLSUtility;


public class ServerKeyExchangeTest extends TestCase {

	@Test
	public void testSetBytesAndGetBytes() {
		final String SERVER_PARAMETER_HEX = "0x0040bf244d9296dbf689ad87108dfb44c87f87044c3ea4c12e787ae0d2aa8b882256c211e367619850a051bc5232b8e5d16dd4e2945e4536bb064fee2510cf7f02630040bf02cba959b6bbeae4caf2915a9c99b1a1668a6ba515800f0b967c77f67d2cc0dbecc465ae0e575f03b2cea65a4ed1e6cc0a0ca2381286483b414acb4df2b4c10040156bc8234c880e36831e1cd26d24943478a97f172ae64ef630ad68dd8ae3c8fea5d4c352596be36d6bdbd3a3f1fb85d36921c99f796be92b7710a0b71f3cae88";
		final String SIGNATURE_HEX = "0x99b50e22af06b2f2ce3ece1f328dc64526448de53ac9074ed3df21b47cdea2ebf40702ff4f437f926be8f76d52e9062267b092de63f4c9e1c5e3049dfec16186e6b020440bc670ef8702fa6ede29be1a0eeaee498d8f716058d8e775152b044e5d68c4b3ce5b22959ff93c563eaf1bf2248a3c62ed0489cc54f8db12379ba8de5cf6c941014da21a61119a139b841d16fab287bccef95844ae89026ab490b7834e80f2c736a7e8084a000e0194bcfd56f1327c0c922e32d04cb3e94e220670b566d733db3d8111496cc15289b00f84a38d747f548797f80df36f3c7235491926eda2dd4af20c9f7716549df67303887e9a7a9a0b3a084b64eb9d0d10284abbf0";
		byte[] serverParamBytes = TLSUtility.HexToBytes(SERVER_PARAMETER_HEX);
		byte[] signatureBytes = TLSUtility.HexToBytes(SIGNATURE_HEX);
		ServerKeyExchange serverKeyExchange = new ServerKeyExchange();
		serverKeyExchange.setServerParams(serverParamBytes);
		serverKeyExchange.setSignature(signatureBytes);
		
		assertEquals("Server Key Exchange length is not proper", serverKeyExchange.getBytes().length, serverParamBytes.length + signatureBytes.length);
		assertTrue("Server Key Exchange bytes are not proper", Arrays.equals(serverKeyExchange.getBytes(), TLSUtility.appendBytes(serverParamBytes, signatureBytes)));
	}

}

