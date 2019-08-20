package com.elitecore.commons.security;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import com.elitecore.commons.io.ByteStreams;

/**
 * 
 * @author malav.desai
 * @author narendra.pathai
 *
 */
@SuppressWarnings("unchecked")
@RunWith(JUnitParamsRunner.class)
public class CertificateUtilityTest {
	private static final String RSA = "NONEwithRSA";

	private static final String DER_CERTIFICATE_NAME = "der-cert-for-test.der";
	private static final String PEM_CERTIFICATE_CHAIN_NAME = "pem-chain-for-test.pem";
	private static final String PEM_CERTIFICATE_WITHOUT_TEXT_NAME = "pem-notext-cert-for-test.pem";
	private static final String PEM_ENC_RSA_PRIVATE_KEY_NAME = "pem-enc-rsa-privatekey.pem";
	private static final String PEM_DEC_RSA_PRIVATE_KEY_NAME = "pem-dec-rsa-privatekey.pem";

	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test
	@Parameters(method="dataFor_testGenerateCertificates")
	public void testGenerateCertificatesFromPath_ShouldGetSuccess_WhenCertificatePathIsPassed(String certPath, List<CertificateDetails> expectedDetails) throws CertificateException, IOException{
		Collection<X509Certificate> actualCertificates = (Collection<X509Certificate>) CertificateUtility.generateCertificates(resourcePath(certPath));

		assertX509CertificateDetails(expectedDetails, actualCertificates);
	}

	@Test
	@Parameters(method="dataFor_testGenerateCertificates")
	public void testGenerateCertificatesFromFile_ShouldGetSuccess_WhenCertificateByteArrayIsPassed(String certPath, List<CertificateDetails> expectedDetails) throws CertificateException, IOException{
		
		byte[] fileBytes = ByteStreams.readFully(new FileInputStream(resourcePath(certPath)));

		Collection<X509Certificate> certificates = (Collection<X509Certificate>) CertificateUtility.generateCertificates(fileBytes);

		assertX509CertificateDetails(expectedDetails, certificates);
	}

	/*
	 * Returns the absolute resource path where the certificate file exists
	 */
	private String resourcePath(String certPath) {
		URL resource = CertificateUtilityTest.class.getClassLoader().getResource("certificates/" + certPath);
		return resource.getFile();
	}

	@Test
	public void testGenerateCertificatesFromBytes_ShouldThrowNullPointerException_WhenByteArrayPassedIsNull() throws CertificateException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("certificateBytes are null");

		CertificateUtility.generateCertificates((byte[])null);
	}

	@Test
	public void testGenerateCertificatesFromInputStream_ShouldThrowNullPointerException_WhenInpuStreamPassedIsNull() throws CertificateException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("certificateStream is null");

		CertificateUtility.generateCertificates((InputStream)null);
	}

	@Test
	public void testGenerateCertificatesFromFile_ShouldThrowNullPointerException_WhenFilePassedIsNull() throws CertificateException, FileNotFoundException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("certificateFile is null");

		CertificateUtility.generateCertificates((File)null);
	}

	@Test
	public void testGenerateCertificatesFromPath_ShouldThrowNullPointerException_WhenFilePathPassedIsNull() throws CertificateException, FileNotFoundException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("certificatePath is null");

		CertificateUtility.generateCertificates((String)null);
	}

	@Test
	public void testGenerateCertificatesFromFile_ShouldThrowIllegalArgumentException_WhenFilePassedIsDirectory() throws CertificateException, IOException {
		File file = folder.newFolder("temp");
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(file.getPath() + " is not a file");

		CertificateUtility.generateCertificates(file);
	}

	@Test
	public void testGenerateCertificatesFromPath_ShouldThrowIllegalArgumentException_WhenFilePathPassedIsDirectory() throws CertificateException, IOException {
		File file = folder.newFolder("temp");
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(file.getPath() + " is not a file");

		CertificateUtility.generateCertificates(file.getPath());
	}

	@Test
	public void testIsSelfSigned_ShouldThrowNullPointerException_IfCertificatePassedIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("certificate is null");
		
		CertificateUtility.isSelfSigned(null);
	}
	
	@Test
	public void testIsSelfSigned() throws CertificateException{
		byte[] selfSignedCertBytes = {45, 45, 45, 45, 45, 66, 69, 71, 73, 78, 32, 67, 69, 82, 84, 73, 70, 73, 67, 65, 84, 69, 45, 45, 45, 45, 45, 10, 77, 73, 73, 69, 73, 106, 67, 67, 65, 119, 113, 103, 65, 119, 73, 66, 65, 103, 73, 74, 65, 77, 102, 82, 74, 121, 52, 80, 122, 51, 80, 103, 77, 65, 48, 71, 67, 83, 113, 71, 83, 73, 98, 51, 68, 81, 69, 66, 66, 81, 85, 65, 77, 71, 81, 120, 67, 122, 65, 74, 66, 103, 78, 86, 10, 66, 65, 89, 84, 65, 107, 108, 79, 77, 82, 65, 119, 68, 103, 89, 68, 86, 81, 81, 73, 69, 119, 100, 72, 100, 87, 112, 104, 99, 109, 70, 48, 77, 82, 73, 119, 69, 65, 89, 68, 86, 81, 81, 72, 69, 119, 108, 66, 97, 71, 49, 108, 90, 71, 70, 105, 89, 87, 81, 120, 69, 106, 65, 81, 10, 66, 103, 78, 86, 66, 65, 111, 84, 67, 85, 86, 115, 97, 88, 82, 108, 89, 50, 57, 121, 90, 84, 69, 77, 77, 65, 111, 71, 65, 49, 85, 69, 67, 120, 77, 68, 81, 49, 78, 78, 77, 81, 48, 119, 67, 119, 89, 68, 86, 81, 81, 68, 69, 119, 82, 121, 98, 50, 57, 48, 77, 66, 52, 88, 10, 68, 84, 69, 122, 77, 68, 103, 121, 79, 84, 65, 52, 77, 84, 103, 120, 77, 70, 111, 88, 68, 84, 73, 122, 77, 68, 103, 121, 78, 122, 65, 52, 77, 84, 103, 120, 77, 70, 111, 119, 90, 68, 69, 76, 77, 65, 107, 71, 65, 49, 85, 69, 66, 104, 77, 67, 83, 85, 52, 120, 69, 68, 65, 79, 10, 66, 103, 78, 86, 66, 65, 103, 84, 66, 48, 100, 49, 97, 109, 70, 121, 89, 88, 81, 120, 69, 106, 65, 81, 66, 103, 78, 86, 66, 65, 99, 84, 67, 85, 70, 111, 98, 87, 86, 107, 89, 87, 74, 104, 90, 68, 69, 83, 77, 66, 65, 71, 65, 49, 85, 69, 67, 104, 77, 74, 82, 87, 120, 112, 10, 100, 71, 86, 106, 98, 51, 74, 108, 77, 81, 119, 119, 67, 103, 89, 68, 86, 81, 81, 76, 69, 119, 78, 68, 85, 48, 48, 120, 68, 84, 65, 76, 66, 103, 78, 86, 66, 65, 77, 84, 66, 72, 74, 118, 98, 51, 81, 119, 103, 103, 69, 105, 77, 65, 48, 71, 67, 83, 113, 71, 83, 73, 98, 51, 10, 68, 81, 69, 66, 65, 81, 85, 65, 65, 52, 73, 66, 68, 119, 65, 119, 103, 103, 69, 75, 65, 111, 73, 66, 65, 81, 68, 84, 70, 112, 105, 105, 72, 77, 71, 47, 113, 98, 97, 120, 80, 65, 71, 56, 53, 48, 69, 47, 118, 56, 50, 51, 87, 55, 106, 119, 106, 115, 89, 55, 117, 57, 70, 118, 10, 77, 102, 52, 110, 74, 85, 112, 71, 73, 83, 110, 121, 90, 55, 98, 68, 78, 89, 75, 100, 47, 74, 83, 56, 66, 85, 102, 108, 97, 106, 122, 84, 80, 120, 79, 51, 88, 52, 73, 71, 107, 50, 120, 54, 117, 76, 98, 122, 90, 115, 78, 52, 76, 56, 115, 68, 86, 87, 50, 55, 73, 69, 97, 109, 10, 88, 70, 56, 121, 121, 116, 73, 116, 73, 49, 83, 101, 56, 76, 108, 51, 72, 50, 68, 107, 47, 50, 119, 67, 73, 119, 110, 100, 107, 49, 97, 109, 102, 52, 74, 120, 108, 68, 120, 85, 56, 71, 102, 119, 100, 69, 114, 56, 48, 69, 81, 43, 67, 76, 78, 54, 90, 120, 78, 88, 52, 89, 57, 99, 10, 81, 72, 108, 107, 109, 77, 56, 54, 111, 108, 73, 106, 120, 85, 43, 114, 74, 97, 101, 52, 104, 82, 109, 50, 99, 43, 122, 103, 110, 102, 118, 111, 82, 81, 47, 103, 74, 88, 107, 55, 88, 77, 73, 120, 49, 108, 66, 47, 75, 112, 103, 115, 98, 89, 81, 85, 117, 116, 48, 56, 50, 52, 116, 77, 10, 54, 114, 106, 109, 68, 100, 113, 71, 89, 97, 121, 65, 68, 82, 114, 101, 90, 106, 80, 75, 67, 73, 85, 67, 52, 50, 51, 100, 85, 105, 119, 43, 80, 82, 54, 74, 88, 78, 111, 66, 122, 48, 72, 43, 49, 84, 55, 47, 122, 107, 108, 78, 116, 69, 54, 83, 86, 102, 119, 50, 80, 87, 84, 53, 10, 105, 51, 119, 55, 110, 121, 87, 43, 69, 72, 109, 54, 120, 116, 49, 76, 74, 111, 101, 100, 103, 118, 122, 65, 67, 103, 85, 50, 118, 98, 67, 98, 72, 107, 71, 52, 79, 101, 54, 53, 121, 72, 118, 66, 116, 115, 98, 49, 65, 103, 77, 66, 65, 65, 71, 106, 103, 100, 89, 119, 103, 100, 77, 119, 10, 72, 81, 89, 68, 86, 82, 48, 79, 66, 66, 89, 69, 70, 72, 89, 54, 104, 104, 116, 120, 56, 104, 118, 99, 106, 83, 118, 119, 77, 122, 82, 104, 108, 105, 43, 48, 69, 122, 85, 73, 77, 73, 71, 87, 66, 103, 78, 86, 72, 83, 77, 69, 103, 89, 52, 119, 103, 89, 117, 65, 70, 72, 89, 54, 10, 104, 104, 116, 120, 56, 104, 118, 99, 106, 83, 118, 119, 77, 122, 82, 104, 108, 105, 43, 48, 69, 122, 85, 73, 111, 87, 105, 107, 90, 106, 66, 107, 77, 81, 115, 119, 67, 81, 89, 68, 86, 81, 81, 71, 69, 119, 74, 74, 84, 106, 69, 81, 77, 65, 52, 71, 65, 49, 85, 69, 67, 66, 77, 72, 10, 82, 51, 86, 113, 89, 88, 74, 104, 100, 68, 69, 83, 77, 66, 65, 71, 65, 49, 85, 69, 66, 120, 77, 74, 81, 87, 104, 116, 90, 87, 82, 104, 89, 109, 70, 107, 77, 82, 73, 119, 69, 65, 89, 68, 86, 81, 81, 75, 69, 119, 108, 70, 98, 71, 108, 48, 90, 87, 78, 118, 99, 109, 85, 120, 10, 68, 68, 65, 75, 66, 103, 78, 86, 66, 65, 115, 84, 65, 48, 78, 84, 84, 84, 69, 78, 77, 65, 115, 71, 65, 49, 85, 69, 65, 120, 77, 69, 99, 109, 57, 118, 100, 73, 73, 74, 65, 77, 102, 82, 74, 121, 52, 80, 122, 51, 80, 103, 77, 65, 119, 71, 65, 49, 85, 100, 69, 119, 81, 70, 10, 77, 65, 77, 66, 65, 102, 56, 119, 67, 119, 89, 68, 86, 82, 48, 80, 66, 65, 81, 68, 65, 103, 69, 71, 77, 65, 48, 71, 67, 83, 113, 71, 83, 73, 98, 51, 68, 81, 69, 66, 66, 81, 85, 65, 65, 52, 73, 66, 65, 81, 67, 101, 76, 56, 78, 86, 113, 80, 79, 71, 72, 55, 79, 69, 10, 82, 77, 55, 88, 120, 115, 110, 97, 73, 81, 120, 105, 49, 114, 89, 49, 82, 83, 80, 122, 120, 101, 103, 49, 51, 43, 114, 113, 76, 50, 115, 75, 106, 87, 48, 113, 97, 99, 57, 121, 102, 90, 97, 102, 114, 51, 51, 108, 85, 115, 99, 87, 112, 107, 121, 87, 107, 108, 122, 53, 111, 71, 68, 106, 10, 112, 120, 122, 68, 110, 85, 81, 89, 56, 104, 69, 105, 82, 85, 81, 89, 115, 114, 116, 52, 70, 82, 107, 88, 107, 81, 75, 55, 105, 78, 66, 99, 57, 97, 112, 73, 109, 98, 105, 78, 77, 84, 71, 73, 68, 108, 118, 81, 106, 51, 47, 112, 65, 65, 103, 85, 50, 108, 113, 102, 56, 118, 69, 48, 10, 65, 108, 83, 111, 69, 80, 72, 87, 118, 105, 54, 102, 114, 84, 115, 89, 110, 114, 71, 78, 78, 97, 103, 107, 100, 68, 108, 80, 98, 89, 50, 77, 90, 81, 82, 84, 74, 105, 101, 51, 100, 106, 99, 107, 107, 51, 70, 115, 68, 50, 121, 107, 55, 87, 122, 98, 65, 99, 108, 71, 119, 69, 68, 97, 10, 108, 52, 114, 76, 72, 79, 90, 74, 66, 66, 79, 82, 66, 120, 76, 68, 85, 55, 82, 84, 86, 81, 71, 89, 100, 84, 86, 103, 108, 122, 114, 122, 102, 88, 87, 120, 68, 121, 81, 80, 111, 75, 122, 112, 55, 77, 100, 54, 72, 55, 105, 122, 83, 69, 72, 86, 112, 87, 51, 102, 76, 111, 110, 80, 10, 74, 56, 119, 76, 114, 87, 121, 108, 76, 66, 78, 53, 54, 108, 47, 68, 108, 54, 72, 47, 88, 86, 104, 71, 49, 112, 86, 69, 76, 85, 67, 83, 48, 56, 73, 52, 49, 87, 104, 112, 72, 116, 68, 113, 75, 88, 98, 68, 72, 103, 88, 84, 109, 51, 114, 103, 54, 57, 104, 79, 80, 71, 99, 121, 10, 116, 110, 53, 90, 113, 110, 90, 120, 10, 45, 45, 45, 45, 45, 69, 78, 68, 32, 67, 69, 82, 84, 73, 70, 73, 67, 65, 84, 69, 45, 45, 45, 45, 45, 10};
		byte[] caSignedCertBytes = {45, 45, 45, 45, 45, 66, 69, 71, 73, 78, 32, 67, 69, 82, 84, 73, 70, 73, 67, 65, 84, 69, 45, 45, 45, 45, 45, 10, 77, 73, 73, 69, 73, 106, 67, 67, 65, 119, 113, 103, 65, 119, 73, 66, 65, 103, 73, 66, 65, 84, 65, 78, 66, 103, 107, 113, 104, 107, 105, 71, 57, 119, 48, 66, 65, 81, 81, 70, 65, 68, 66, 107, 77, 81, 115, 119, 67, 81, 89, 68, 86, 81, 81, 71, 69, 119, 74, 74, 84, 106, 69, 81, 10, 77, 65, 52, 71, 65, 49, 85, 69, 67, 66, 77, 72, 82, 51, 86, 113, 89, 88, 74, 104, 100, 68, 69, 83, 77, 66, 65, 71, 65, 49, 85, 69, 66, 120, 77, 74, 81, 87, 104, 116, 90, 87, 82, 104, 89, 109, 70, 107, 77, 82, 73, 119, 69, 65, 89, 68, 86, 81, 81, 75, 69, 119, 108, 70, 10, 98, 71, 108, 48, 90, 87, 78, 118, 99, 109, 85, 120, 68, 68, 65, 75, 66, 103, 78, 86, 66, 65, 115, 84, 65, 48, 78, 84, 84, 84, 69, 78, 77, 65, 115, 71, 65, 49, 85, 69, 65, 120, 77, 69, 99, 109, 57, 118, 100, 68, 65, 101, 70, 119, 48, 120, 77, 122, 65, 52, 77, 106, 107, 120, 10, 77, 68, 69, 52, 78, 84, 90, 97, 70, 119, 48, 121, 77, 106, 65, 52, 77, 106, 99, 120, 77, 68, 69, 52, 78, 84, 90, 97, 77, 71, 119, 120, 67, 122, 65, 74, 66, 103, 78, 86, 66, 65, 89, 84, 65, 107, 108, 79, 77, 82, 65, 119, 68, 103, 89, 68, 86, 81, 81, 73, 69, 119, 100, 72, 10, 100, 87, 112, 104, 99, 109, 70, 48, 77, 82, 73, 119, 69, 65, 89, 68, 86, 81, 81, 72, 69, 119, 108, 66, 97, 71, 49, 108, 90, 71, 70, 105, 89, 87, 81, 120, 69, 106, 65, 81, 66, 103, 78, 86, 66, 65, 111, 84, 67, 85, 86, 115, 97, 88, 82, 108, 89, 50, 57, 121, 90, 84, 69, 77, 10, 77, 65, 111, 71, 65, 49, 85, 69, 67, 120, 77, 68, 81, 49, 78, 78, 77, 82, 85, 119, 69, 119, 89, 68, 86, 81, 81, 68, 70, 65, 120, 68, 81, 86, 57, 70, 98, 71, 108, 48, 90, 87, 78, 118, 99, 109, 85, 119, 103, 103, 69, 105, 77, 65, 48, 71, 67, 83, 113, 71, 83, 73, 98, 51, 10, 68, 81, 69, 66, 65, 81, 85, 65, 65, 52, 73, 66, 68, 119, 65, 119, 103, 103, 69, 75, 65, 111, 73, 66, 65, 81, 67, 121, 67, 66, 67, 87, 121, 102, 100, 68, 66, 120, 103, 112, 66, 107, 87, 56, 47, 74, 66, 76, 99, 120, 118, 71, 83, 68, 52, 66, 122, 119, 89, 89, 72, 89, 76, 72, 10, 88, 83, 111, 97, 116, 74, 106, 100, 118, 110, 113, 70, 55, 116, 122, 99, 110, 103, 83, 48, 50, 104, 90, 72, 74, 72, 54, 97, 71, 119, 101, 57, 75, 72, 69, 105, 68, 51, 78, 81, 55, 116, 87, 103, 72, 87, 107, 121, 74, 115, 104, 102, 116, 85, 122, 49, 120, 109, 120, 86, 84, 53, 110, 54, 10, 77, 77, 82, 69, 104, 99, 113, 106, 109, 47, 98, 68, 71, 90, 88, 106, 105, 111, 49, 74, 114, 99, 101, 84, 65, 53, 53, 99, 50, 73, 72, 79, 67, 99, 99, 83, 104, 97, 97, 108, 98, 67, 101, 50, 88, 98, 79, 83, 71, 98, 57, 56, 82, 79, 72, 81, 56, 89, 113, 112, 65, 122, 49, 103, 10, 78, 47, 71, 99, 77, 87, 87, 55, 89, 57, 121, 97, 102, 73, 112, 110, 99, 68, 118, 121, 71, 55, 113, 116, 116, 111, 102, 107, 116, 54, 67, 97, 50, 117, 78, 65, 81, 47, 85, 67, 68, 56, 97, 121, 66, 47, 51, 48, 98, 47, 74, 65, 120, 120, 115, 100, 105, 103, 83, 81, 115, 49, 85, 49, 10, 53, 75, 82, 47, 66, 97, 108, 72, 70, 67, 65, 119, 86, 105, 82, 78, 102, 111, 71, 119, 111, 107, 57, 54, 86, 111, 113, 88, 108, 48, 49, 52, 117, 71, 54, 89, 105, 82, 85, 103, 79, 79, 49, 67, 112, 70, 70, 107, 66, 68, 84, 118, 66, 78, 78, 50, 84, 110, 117, 55, 78, 84, 75, 72, 10, 116, 67, 68, 107, 101, 119, 79, 102, 102, 53, 99, 57, 115, 85, 97, 57, 84, 74, 107, 66, 122, 114, 117, 110, 67, 77, 74, 83, 102, 65, 83, 89, 67, 105, 43, 100, 49, 88, 84, 85, 113, 54, 101, 48, 48, 120, 107, 80, 65, 103, 77, 66, 65, 65, 71, 106, 103, 100, 89, 119, 103, 100, 77, 119, 10, 72, 81, 89, 68, 86, 82, 48, 79, 66, 66, 89, 69, 70, 80, 112, 49, 87, 51, 50, 115, 82, 54, 99, 81, 54, 79, 101, 114, 85, 90, 98, 80, 56, 120, 104, 67, 116, 117, 78, 49, 77, 73, 71, 87, 66, 103, 78, 86, 72, 83, 77, 69, 103, 89, 52, 119, 103, 89, 117, 65, 70, 72, 89, 54, 10, 104, 104, 116, 120, 56, 104, 118, 99, 106, 83, 118, 119, 77, 122, 82, 104, 108, 105, 43, 48, 69, 122, 85, 73, 111, 87, 105, 107, 90, 106, 66, 107, 77, 81, 115, 119, 67, 81, 89, 68, 86, 81, 81, 71, 69, 119, 74, 74, 84, 106, 69, 81, 77, 65, 52, 71, 65, 49, 85, 69, 67, 66, 77, 72, 10, 82, 51, 86, 113, 89, 88, 74, 104, 100, 68, 69, 83, 77, 66, 65, 71, 65, 49, 85, 69, 66, 120, 77, 74, 81, 87, 104, 116, 90, 87, 82, 104, 89, 109, 70, 107, 77, 82, 73, 119, 69, 65, 89, 68, 86, 81, 81, 75, 69, 119, 108, 70, 98, 71, 108, 48, 90, 87, 78, 118, 99, 109, 85, 120, 10, 68, 68, 65, 75, 66, 103, 78, 86, 66, 65, 115, 84, 65, 48, 78, 84, 84, 84, 69, 78, 77, 65, 115, 71, 65, 49, 85, 69, 65, 120, 77, 69, 99, 109, 57, 118, 100, 73, 73, 74, 65, 77, 102, 82, 74, 121, 52, 80, 122, 51, 80, 103, 77, 65, 119, 71, 65, 49, 85, 100, 69, 119, 81, 70, 10, 77, 65, 77, 66, 65, 102, 56, 119, 67, 119, 89, 68, 86, 82, 48, 80, 66, 65, 81, 68, 65, 103, 71, 109, 77, 65, 48, 71, 67, 83, 113, 71, 83, 73, 98, 51, 68, 81, 69, 66, 66, 65, 85, 65, 65, 52, 73, 66, 65, 81, 67, 71, 80, 106, 50, 54, 67, 116, 104, 99, 69, 80, 100, 97, 10, 48, 69, 53, 66, 101, 75, 50, 103, 75, 78, 43, 116, 78, 48, 119, 85, 80, 121, 55, 98, 76, 107, 121, 106, 122, 103, 98, 80, 120, 66, 82, 83, 112, 114, 50, 71, 57, 121, 50, 120, 107, 55, 74, 81, 84, 75, 90, 82, 79, 111, 88, 74, 84, 101, 54, 72, 55, 108, 52, 56, 73, 100, 114, 52, 10, 99, 103, 102, 107, 111, 70, 47, 97, 103, 121, 51, 85, 69, 57, 114, 76, 117, 53, 98, 74, 68, 77, 56, 120, 81, 51, 111, 109, 79, 54, 52, 87, 101, 88, 50, 122, 119, 74, 98, 101, 51, 107, 116, 51, 84, 66, 108, 105, 80, 81, 97, 117, 52, 43, 76, 122, 67, 55, 48, 112, 117, 54, 48, 105, 10, 54, 76, 55, 55, 116, 114, 86, 65, 56, 117, 86, 76, 113, 68, 107, 112, 48, 87, 81, 74, 97, 69, 69, 77, 67, 118, 98, 75, 56, 50, 47, 100, 119, 115, 103, 75, 66, 113, 80, 70, 84, 81, 120, 72, 103, 81, 56, 102, 112, 55, 118, 52, 103, 88, 51, 100, 89, 79, 105, 87, 52, 108, 74, 67, 10, 87, 68, 48, 104, 114, 78, 87, 118, 49, 66, 115, 88, 122, 121, 70, 72, 101, 80, 120, 121, 71, 115, 105, 79, 79, 108, 117, 83, 52, 90, 86, 97, 122, 89, 57, 50, 102, 53, 100, 113, 79, 67, 116, 77, 48, 97, 121, 70, 50, 68, 116, 79, 99, 99, 101, 115, 116, 54, 113, 118, 49, 52, 115, 48, 10, 87, 97, 50, 101, 81, 97, 51, 97, 86, 101, 73, 85, 53, 52, 71, 99, 101, 78, 107, 43, 122, 101, 97, 69, 51, 53, 86, 122, 67, 97, 87, 98, 77, 49, 104, 119, 107, 100, 97, 118, 49, 88, 85, 116, 71, 87, 97, 85, 48, 47, 69, 107, 106, 120, 88, 86, 69, 83, 114, 81, 66, 114, 55, 68, 10, 51, 47, 67, 106, 118, 80, 52, 51, 10, 45, 45, 45, 45, 45, 69, 78, 68, 32, 67, 69, 82, 84, 73, 70, 73, 67, 65, 84, 69, 45, 45, 45, 45, 45, 10};

		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate selfSignedCert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(selfSignedCertBytes));
		X509Certificate caSignedCert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(caSignedCertBytes));

		assertEquals(true, CertificateUtility.isSelfSigned(selfSignedCert));
		assertEquals(false, CertificateUtility.isSelfSigned(caSignedCert));
	}
	
	@Test
	public void testIsChain_ShouldThrowNullPointerException_WhenCertificateCollectionPassedIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("certificateList is null");
		
		Collection<? extends Certificate> certificateList = null;
		CertificateUtility.isChain(certificateList);
	}
	
	@Test
	public void testIsChain_ShouldReturnTrue_WhenCertificateCollectionSizeIsMoreThanOne() throws CertificateException, IOException{
		Collection<? extends Certificate> certificateList = CertificateUtility.generateCertificates(resourcePath(PEM_CERTIFICATE_CHAIN_NAME));
		assertTrue(CertificateUtility.isChain(certificateList));
	}
	
	@Test
	public void testIsChain_ShouldReturnBooleanFalse_WhenCertificateCollectionSizeIsExactlyOne() throws CertificateException, IOException{
		Collection<? extends Certificate> certificateList = CertificateUtility.generateCertificates(resourcePath(PEM_CERTIFICATE_WITHOUT_TEXT_NAME));
		assertFalse(CertificateUtility.isChain(certificateList));
	}
	
	@Test
	public void testIsChain_ShouldReturnBooleanFalse_WhenCertificateCollectionIsEmpty() throws CertificateException{
		Collection<? extends Certificate> certificateList = Collections.emptyList();
		assertFalse(CertificateUtility.isChain(certificateList));
	}
	
	@Test
	public void testGeneratePrivateKeyFromPath_ShouldThrowNullPointerException_WhenPrivateKeyFilePathPassedIsNull() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException {
		exception.expect(NullPointerException.class);
		exception.expectMessage("privateKeyPath is null");
		
		String privateKeyPath = null;
		CertificateUtility.generatePrivateKey(privateKeyPath, "elitecore", RSA);
	}
	
	@Test
	public void testGeneratePrivateKeyFromFile_ShouldThrowNullPointerException_WhenPrivateKeyFilePassedIsNull() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException {
		exception.expect(NullPointerException.class);
		exception.expectMessage("privateKeyFile is null");
		
		File privateKeyFile = null;
		CertificateUtility.generatePrivateKey(privateKeyFile, "elitecore", RSA);
	}
	
	@Test
	public void testGeneratePrivateKeyFromFile_ShouldThrowIllegalArgumentException_WhenPrivateKeyFilePassedIsDirectory() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException {
		File privateKeyFile = folder.newFolder("temp");
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(privateKeyFile.getPath() + " is not a file");
		
		CertificateUtility.generatePrivateKey(privateKeyFile, "elitecore", RSA);
	}

	@Test
	public void testGeneratePrivateKeyFromBytes_ShouldThrowNullPointerException_WhenPrivateKeyBytesPassedIsNull() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException {
		exception.expect(NullPointerException.class);
		exception.expectMessage("privateKeyBytes are null");
		
		byte[] privateKeyBytes = null;
		CertificateUtility.generatePrivateKey(privateKeyBytes, "elitecore", RSA);
	}
	
	@Test
	public void testGeneratePrivateKeyFromPath_ShouldGetSuccess_WhenPrivateKeyEncAndPasswdProperAndPrivateKeyAlgoIsProper() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException{
		PrivateKey generatedPrivateKey =
			CertificateUtility.generatePrivateKey(
					resourcePath(PEM_ENC_RSA_PRIVATE_KEY_NAME), 
					"elitecore", 
					"RSA"
			);

		PrivateKey expectedPrivateKey = createRSAPrivateKey();
		
		assertTrue(generatedPrivateKey.equals(expectedPrivateKey));
	}
	
	
	@Test
	public void testGeneratePrivateKeyFromPath_ShouldNotDecryptPrivateKey_WhenPasswordPassedIsNull() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException{
		String password = null;
		PrivateKey generatedPrivateKey = 
			CertificateUtility.generatePrivateKey(
					resourcePath(File.separator + PEM_DEC_RSA_PRIVATE_KEY_NAME), 
					password, 
					"RSA"
			);

		PrivateKey expectedPrivateKey = createRSAPrivateKey();
		
		assertTrue(generatedPrivateKey.equals(expectedPrivateKey));
	}

	@Test
	public void testGeneratePrivateKeyFromPath_ShouldNotDecryptPrivateKey_WhenPasswordPassedIsEmpty() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException{
		String password = "";
		PrivateKey generatedPrivateKey = 
			CertificateUtility.generatePrivateKey(
					resourcePath(File.separator + PEM_DEC_RSA_PRIVATE_KEY_NAME), 
					password, 
					"RSA"
			);

		PrivateKey expectedPrivateKey = createRSAPrivateKey();
		
		assertTrue(generatedPrivateKey.equals(expectedPrivateKey));
	}

	@Test
	public void testValidate_ShouldThrowNullPointerException_WhenPublicKeyPassedIsNull() throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
		exception.expect(NullPointerException.class);
		exception.expectMessage("publicKey is null");
		
		Certificate publicCertificate = null;
		CertificateUtility.validate(publicCertificate, createRSAPrivateKey(), "ANY");
	}

	@Test
	public void testValidate_ShouldThrowNullPointerException_WhenPrivateKeyPassedIsNull() throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException {
		Certificate publicKey = ((List<X509Certificate>)CertificateUtility.generateCertificates(resourcePath(PEM_CERTIFICATE_WITHOUT_TEXT_NAME))).get(0);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage("privateKey is null");
		
		PrivateKey privateKey = null;
		CertificateUtility.validate(publicKey, privateKey, RSA);
	}
	
	@Test
	public void testValidate_ShouldThrow_NullPointerException_WhenSignatureAlgorihmIsNull() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, CertificateException, IOException{
		Certificate publicKey = ((List<X509Certificate>)CertificateUtility.generateCertificates(resourcePath(PEM_CERTIFICATE_WITHOUT_TEXT_NAME))).get(0);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage("signatureAlgorithm is null");
		
		CertificateUtility.validate(publicKey, createRSAPrivateKey(), null);
	}

	@Test
	public void testValidate_ShouldReturnTrue_WhenPublicKeyAndPrivateKeyPairIsProper() throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, IOException {
		Certificate publicKey = ((List<X509Certificate>)CertificateUtility.generateCertificates(resourcePath(PEM_CERTIFICATE_WITHOUT_TEXT_NAME))).get(0);

		assertTrue(CertificateUtility.validate(publicKey, createRSAPrivateKey(), RSA));
	}

	@Test
	public void testValidate_ShouldGetFalse_WhenPublicKeyAndPrivateKeyPairIsOfSameEncryptionAlgorithmButNotFormAPair() throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
		byte[] certificate = {45, 45, 45, 45, 45, 66, 69, 71, 73, 78, 32, 67, 69, 82, 84, 73, 70, 73, 67, 65, 84, 69, 45, 45, 45, 45, 45, 10, 77, 73, 73, 69, 73, 106, 67, 67, 65, 119, 113, 103, 65, 119, 73, 66, 65, 103, 73, 74, 65, 77, 102, 82, 74, 121, 52, 80, 122, 51, 80, 103, 77, 65, 48, 71, 67, 83, 113, 71, 83, 73, 98, 51, 68, 81, 69, 66, 66, 81, 85, 65, 77, 71, 81, 120, 67, 122, 65, 74, 66, 103, 78, 86, 10, 66, 65, 89, 84, 65, 107, 108, 79, 77, 82, 65, 119, 68, 103, 89, 68, 86, 81, 81, 73, 69, 119, 100, 72, 100, 87, 112, 104, 99, 109, 70, 48, 77, 82, 73, 119, 69, 65, 89, 68, 86, 81, 81, 72, 69, 119, 108, 66, 97, 71, 49, 108, 90, 71, 70, 105, 89, 87, 81, 120, 69, 106, 65, 81, 10, 66, 103, 78, 86, 66, 65, 111, 84, 67, 85, 86, 115, 97, 88, 82, 108, 89, 50, 57, 121, 90, 84, 69, 77, 77, 65, 111, 71, 65, 49, 85, 69, 67, 120, 77, 68, 81, 49, 78, 78, 77, 81, 48, 119, 67, 119, 89, 68, 86, 81, 81, 68, 69, 119, 82, 121, 98, 50, 57, 48, 77, 66, 52, 88, 10, 68, 84, 69, 122, 77, 68, 103, 121, 79, 84, 65, 52, 77, 84, 103, 120, 77, 70, 111, 88, 68, 84, 73, 122, 77, 68, 103, 121, 78, 122, 65, 52, 77, 84, 103, 120, 77, 70, 111, 119, 90, 68, 69, 76, 77, 65, 107, 71, 65, 49, 85, 69, 66, 104, 77, 67, 83, 85, 52, 120, 69, 68, 65, 79, 10, 66, 103, 78, 86, 66, 65, 103, 84, 66, 48, 100, 49, 97, 109, 70, 121, 89, 88, 81, 120, 69, 106, 65, 81, 66, 103, 78, 86, 66, 65, 99, 84, 67, 85, 70, 111, 98, 87, 86, 107, 89, 87, 74, 104, 90, 68, 69, 83, 77, 66, 65, 71, 65, 49, 85, 69, 67, 104, 77, 74, 82, 87, 120, 112, 10, 100, 71, 86, 106, 98, 51, 74, 108, 77, 81, 119, 119, 67, 103, 89, 68, 86, 81, 81, 76, 69, 119, 78, 68, 85, 48, 48, 120, 68, 84, 65, 76, 66, 103, 78, 86, 66, 65, 77, 84, 66, 72, 74, 118, 98, 51, 81, 119, 103, 103, 69, 105, 77, 65, 48, 71, 67, 83, 113, 71, 83, 73, 98, 51, 10, 68, 81, 69, 66, 65, 81, 85, 65, 65, 52, 73, 66, 68, 119, 65, 119, 103, 103, 69, 75, 65, 111, 73, 66, 65, 81, 68, 84, 70, 112, 105, 105, 72, 77, 71, 47, 113, 98, 97, 120, 80, 65, 71, 56, 53, 48, 69, 47, 118, 56, 50, 51, 87, 55, 106, 119, 106, 115, 89, 55, 117, 57, 70, 118, 10, 77, 102, 52, 110, 74, 85, 112, 71, 73, 83, 110, 121, 90, 55, 98, 68, 78, 89, 75, 100, 47, 74, 83, 56, 66, 85, 102, 108, 97, 106, 122, 84, 80, 120, 79, 51, 88, 52, 73, 71, 107, 50, 120, 54, 117, 76, 98, 122, 90, 115, 78, 52, 76, 56, 115, 68, 86, 87, 50, 55, 73, 69, 97, 109, 10, 88, 70, 56, 121, 121, 116, 73, 116, 73, 49, 83, 101, 56, 76, 108, 51, 72, 50, 68, 107, 47, 50, 119, 67, 73, 119, 110, 100, 107, 49, 97, 109, 102, 52, 74, 120, 108, 68, 120, 85, 56, 71, 102, 119, 100, 69, 114, 56, 48, 69, 81, 43, 67, 76, 78, 54, 90, 120, 78, 88, 52, 89, 57, 99, 10, 81, 72, 108, 107, 109, 77, 56, 54, 111, 108, 73, 106, 120, 85, 43, 114, 74, 97, 101, 52, 104, 82, 109, 50, 99, 43, 122, 103, 110, 102, 118, 111, 82, 81, 47, 103, 74, 88, 107, 55, 88, 77, 73, 120, 49, 108, 66, 47, 75, 112, 103, 115, 98, 89, 81, 85, 117, 116, 48, 56, 50, 52, 116, 77, 10, 54, 114, 106, 109, 68, 100, 113, 71, 89, 97, 121, 65, 68, 82, 114, 101, 90, 106, 80, 75, 67, 73, 85, 67, 52, 50, 51, 100, 85, 105, 119, 43, 80, 82, 54, 74, 88, 78, 111, 66, 122, 48, 72, 43, 49, 84, 55, 47, 122, 107, 108, 78, 116, 69, 54, 83, 86, 102, 119, 50, 80, 87, 84, 53, 10, 105, 51, 119, 55, 110, 121, 87, 43, 69, 72, 109, 54, 120, 116, 49, 76, 74, 111, 101, 100, 103, 118, 122, 65, 67, 103, 85, 50, 118, 98, 67, 98, 72, 107, 71, 52, 79, 101, 54, 53, 121, 72, 118, 66, 116, 115, 98, 49, 65, 103, 77, 66, 65, 65, 71, 106, 103, 100, 89, 119, 103, 100, 77, 119, 10, 72, 81, 89, 68, 86, 82, 48, 79, 66, 66, 89, 69, 70, 72, 89, 54, 104, 104, 116, 120, 56, 104, 118, 99, 106, 83, 118, 119, 77, 122, 82, 104, 108, 105, 43, 48, 69, 122, 85, 73, 77, 73, 71, 87, 66, 103, 78, 86, 72, 83, 77, 69, 103, 89, 52, 119, 103, 89, 117, 65, 70, 72, 89, 54, 10, 104, 104, 116, 120, 56, 104, 118, 99, 106, 83, 118, 119, 77, 122, 82, 104, 108, 105, 43, 48, 69, 122, 85, 73, 111, 87, 105, 107, 90, 106, 66, 107, 77, 81, 115, 119, 67, 81, 89, 68, 86, 81, 81, 71, 69, 119, 74, 74, 84, 106, 69, 81, 77, 65, 52, 71, 65, 49, 85, 69, 67, 66, 77, 72, 10, 82, 51, 86, 113, 89, 88, 74, 104, 100, 68, 69, 83, 77, 66, 65, 71, 65, 49, 85, 69, 66, 120, 77, 74, 81, 87, 104, 116, 90, 87, 82, 104, 89, 109, 70, 107, 77, 82, 73, 119, 69, 65, 89, 68, 86, 81, 81, 75, 69, 119, 108, 70, 98, 71, 108, 48, 90, 87, 78, 118, 99, 109, 85, 120, 10, 68, 68, 65, 75, 66, 103, 78, 86, 66, 65, 115, 84, 65, 48, 78, 84, 84, 84, 69, 78, 77, 65, 115, 71, 65, 49, 85, 69, 65, 120, 77, 69, 99, 109, 57, 118, 100, 73, 73, 74, 65, 77, 102, 82, 74, 121, 52, 80, 122, 51, 80, 103, 77, 65, 119, 71, 65, 49, 85, 100, 69, 119, 81, 70, 10, 77, 65, 77, 66, 65, 102, 56, 119, 67, 119, 89, 68, 86, 82, 48, 80, 66, 65, 81, 68, 65, 103, 69, 71, 77, 65, 48, 71, 67, 83, 113, 71, 83, 73, 98, 51, 68, 81, 69, 66, 66, 81, 85, 65, 65, 52, 73, 66, 65, 81, 67, 101, 76, 56, 78, 86, 113, 80, 79, 71, 72, 55, 79, 69, 10, 82, 77, 55, 88, 120, 115, 110, 97, 73, 81, 120, 105, 49, 114, 89, 49, 82, 83, 80, 122, 120, 101, 103, 49, 51, 43, 114, 113, 76, 50, 115, 75, 106, 87, 48, 113, 97, 99, 57, 121, 102, 90, 97, 102, 114, 51, 51, 108, 85, 115, 99, 87, 112, 107, 121, 87, 107, 108, 122, 53, 111, 71, 68, 106, 10, 112, 120, 122, 68, 110, 85, 81, 89, 56, 104, 69, 105, 82, 85, 81, 89, 115, 114, 116, 52, 70, 82, 107, 88, 107, 81, 75, 55, 105, 78, 66, 99, 57, 97, 112, 73, 109, 98, 105, 78, 77, 84, 71, 73, 68, 108, 118, 81, 106, 51, 47, 112, 65, 65, 103, 85, 50, 108, 113, 102, 56, 118, 69, 48, 10, 65, 108, 83, 111, 69, 80, 72, 87, 118, 105, 54, 102, 114, 84, 115, 89, 110, 114, 71, 78, 78, 97, 103, 107, 100, 68, 108, 80, 98, 89, 50, 77, 90, 81, 82, 84, 74, 105, 101, 51, 100, 106, 99, 107, 107, 51, 70, 115, 68, 50, 121, 107, 55, 87, 122, 98, 65, 99, 108, 71, 119, 69, 68, 97, 10, 108, 52, 114, 76, 72, 79, 90, 74, 66, 66, 79, 82, 66, 120, 76, 68, 85, 55, 82, 84, 86, 81, 71, 89, 100, 84, 86, 103, 108, 122, 114, 122, 102, 88, 87, 120, 68, 121, 81, 80, 111, 75, 122, 112, 55, 77, 100, 54, 72, 55, 105, 122, 83, 69, 72, 86, 112, 87, 51, 102, 76, 111, 110, 80, 10, 74, 56, 119, 76, 114, 87, 121, 108, 76, 66, 78, 53, 54, 108, 47, 68, 108, 54, 72, 47, 88, 86, 104, 71, 49, 112, 86, 69, 76, 85, 67, 83, 48, 56, 73, 52, 49, 87, 104, 112, 72, 116, 68, 113, 75, 88, 98, 68, 72, 103, 88, 84, 109, 51, 114, 103, 54, 57, 104, 79, 80, 71, 99, 121, 10, 116, 110, 53, 90, 113, 110, 90, 120, 10, 45, 45, 45, 45, 45, 69, 78, 68, 32, 67, 69, 82, 84, 73, 70, 73, 67, 65, 84, 69, 45, 45, 45, 45, 45, 10};
		Certificate publicKey = ((List<X509Certificate>)CertificateUtility.generateCertificates(certificate)).get(0);

		assertFalse(CertificateUtility.validate(publicKey, createRSAPrivateKey(), RSA));
	}

	private PrivateKey createRSAPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] decPrivateKeyBytes = {48, -126, 4, -67, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126, 4, -89, 48, -126, 4, -93, 2, 1, 0, 2, -126, 1, 1, 0, -81, -62, 65, 64, 102, -34, -18, -14, -56, 19, 76, -61, 116, 122, 122, 6, -108, 34, -79, -47, 9, -7, 29, -41, -99, 49, 63, -65, -100, -111, 54, -123, -113, 87, -123, 69, 120, 92, -73, 10, -85, 5, 44, -28, 89, 64, -27, -80, 113, 50, -27, 56, -42, -117, 106, -4, -86, -103, 17, -15, 90, -25, 73, 45, -84, 125, -5, -60, -85, -30, -96, -56, -117, -126, -31, -46, 106, 51, 101, 127, -77, -86, 83, 107, 42, -100, -112, 54, 104, 57, -35, -85, -19, 46, 39, -120, 114, 121, -62, -7, 105, 117, 33, -42, 2, -49, 31, -124, 125, 19, -85, 7, 26, 9, -13, -37, 61, 52, -104, 68, 25, 86, 98, 60, -8, -30, 122, 125, 14, -128, 115, 34, 39, -96, -100, -62, -80, 101, -122, -46, -35, 57, -4, -32, -48, -111, 2, 13, 107, 66, -4, -109, 59, -57, 81, 110, 121, -74, -126, 50, -51, 80, 28, -74, -106, -74, -33, 114, 118, 55, -62, 41, 77, -43, 17, 32, -2, 60, 66, 73, 80, 80, -21, 48, 0, 81, -52, 3, 24, 14, 117, -107, -2, -59, 73, -20, -123, -123, 91, -128, 118, 116, 19, 8, 14, 23, 19, 86, -51, -7, -45, -12, -50, 109, -10, -27, 15, -87, -77, -20, -114, -36, -31, -110, 124, 48, 122, 9, -76, -71, -124, 46, -26, -81, -86, -122, 97, 47, -2, -36, 48, 2, -73, 99, 125, -12, 86, -70, -54, 101, 111, 0, 90, -119, -126, 65, 2, 3, 1, 0, 1, 2, -126, 1, 0, 77, 117, -116, 50, 104, -29, -23, 101, 64, 62, 25, 63, -82, -50, -25, -122, -116, -76, -71, -81, 73, -8, -21, 105, 2, 19, 25, 104, 70, 5, 77, -123, 17, 70, 94, 42, -34, -65, -27, -65, -94, -35, -106, 10, 81, 55, -93, -62, -34, 48, -77, 11, 24, -86, -69, 12, -3, -42, -106, 44, -53, -122, 81, 30, -6, -3, 78, -1, 63, 77, -110, -44, -81, 127, -112, 44, -98, -73, 102, 13, 32, -50, 121, 19, 42, -27, 115, 61, 55, 52, 1, 120, -22, -1, -61, 86, -124, 53, 27, 115, -74, 65, 116, 23, 16, -121, -35, -7, 18, -14, -97, 22, 115, 40, 93, -69, -118, -66, -22, 56, 96, -2, 76, 47, 88, -12, -33, 76, 16, -124, 93, -29, 118, -101, -11, 58, -103, 16, 7, -51, -81, 104, 59, -72, 107, 121, -98, -119, 122, 122, 1, 104, -52, 6, -35, -39, 81, 94, 58, 105, -27, 114, 86, 49, 57, 23, 24, 99, -123, -34, -23, -63, 43, 9, -21, 120, -13, 22, -120, 48, -57, -1, 21, 61, -24, 43, 66, -92, 1, 13, 75, -95, 90, -99, -122, -108, 74, 51, 14, -98, 64, -120, 91, -56, 24, 82, -72, -103, -123, -104, 113, 9, -59, 44, -107, 77, 28, -92, -96, -97, -89, -28, 39, -105, -83, -27, 17, 6, -17, -103, 9, 20, -4, -79, -23, -124, -27, -96, 13, 64, -65, 56, 36, -112, -9, -12, 118, -26, -50, -19, -91, 105, 109, 64, -65, 61, 2, -127, -127, 0, -26, 85, 68, -96, 85, 42, -124, 56, -59, 10, -75, 61, 62, -53, -105, -11, 48, 98, 71, 97, 48, -8, -18, 101, -88, 54, -80, -101, -78, -33, 30, 93, 111, 16, 78, -122, -5, 77, 91, -122, 37, 35, -128, -12, -33, -59, 34, -114, 97, -86, -63, -110, -26, -42, -57, -19, 42, 62, -56, 91, -60, -21, -65, -98, -60, 103, 79, -28, -44, -51, 0, -34, -104, 46, -106, -123, -79, 108, 91, 94, 66, -48, 29, 14, 71, -63, -124, 109, -92, -49, 14, 41, -24, -114, 107, -71, -126, 75, -89, 91, -100, 60, -18, 18, 4, -78, -127, -122, 20, 69, -108, 27, -84, 29, 14, 80, 122, -71, 18, -40, 1, -95, -15, 121, 95, 91, 127, 59, 2, -127, -127, 0, -61, 88, 36, 78, -111, -31, 22, 51, -24, 40, -116, 7, 6, 4, -110, -40, 47, -55, 62, 104, 101, 121, -33, -57, -71, 78, -35, 0, -6, -69, 61, -18, -59, -111, 51, 97, -9, 82, 126, 51, 102, 0, 105, -70, 48, 65, -31, -106, -52, -118, -104, -124, 71, 127, 9, 124, -113, 62, -20, -54, -18, 120, -49, -23, 111, 56, 100, -11, -123, -67, 27, 61, 41, 93, 122, 6, 2, 33, 34, 52, 108, 49, -38, -80, 85, 108, -3, 127, -26, 109, -35, -124, 99, -111, 50, 113, 123, -64, -62, 58, -53, -76, 41, -110, 116, -1, 75, 89, 44, 3, -15, -53, -76, -105, -30, -15, 42, -15, -66, -72, 118, -104, -83, 104, 10, -51, -28, -77, 2, -127, -127, 0, -100, 52, 69, 119, 42, 106, -52, 2, -89, -10, 108, 21, 29, -23, -6, -94, -24, 110, 46, 20, 70, -98, -5, 37, -21, -33, 51, 4, 84, 89, 38, 1, -99, 89, -124, -73, -37, -29, -119, 103, 62, -10, -123, -11, 11, 112, 104, -48, 0, -126, 74, 6, -77, 16, 3, -61, -34, 59, 119, -85, 108, -81, -88, 99, 38, -75, 29, -92, -123, -128, -123, -49, 111, -117, 96, -65, 72, 36, -63, 13, -6, -25, -94, 92, -43, -30, -14, 73, 93, 84, -94, 57, 95, -94, 126, -127, -17, 80, 25, 46, 114, -107, -117, -86, -94, -114, -32, 114, 42, 123, 73, 71, 63, 0, -15, 125, 56, -51, 28, 111, 107, 36, 5, 92, 23, -124, 56, -109, 2, -127, -128, 89, 122, -46, -122, -69, 3, -29, -21, -70, -8, 119, -53, -17, -47, -64, -126, 14, -63, -100, 56, -83, -30, -7, -62, -66, -5, -124, -8, -95, -31, -117, 41, 8, -114, -50, 126, -99, -113, -56, 7, 59, 62, 13, 48, 32, 82, -51, 71, 10, 66, -94, 66, 85, -115, -73, -122, -69, 7, 19, -24, -112, -5, -2, 32, -21, -75, 101, 105, -95, -19, 81, -64, -34, 101, -41, 51, 81, 94, 100, 16, -89, 78, -34, -31, -50, -1, -79, -49, 24, 122, 82, -14, 5, -71, -56, -27, 122, -50, 125, 41, -7, -115, 121, 77, -127, 18, 64, 45, -124, -97, -7, -18, 89, -73, 9, 89, 119, 86, 109, 10, -118, 40, -67, -91, -34, -19, 25, 73, 2, -127, -128, 42, 63, -95, -34, 48, -77, -32, 24, 81, 21, -53, -54, -48, 41, 93, 106, -39, -28, 47, 0, -59, -124, 92, 90, -58, 9, 17, 60, -90, 37, 68, 127, 20, -107, 59, -79, 123, 105, -90, 36, -24, -39, -65, -74, 14, -103, 38, -82, -9, 77, -15, -26, 107, 127, 107, -93, -109, 48, 13, -35, 112, -87, 84, 49, -128, -21, 94, -113, 107, 12, 47, 71, -76, 101, -103, -48, -23, -68, -27, -117, -61, -113, -64, -49, -99, 33, -36, -71, -18, -128, -119, 101, 39, -27, -82, 39, 29, -59, 45, -31, -123, 78, 4, 118, 119, -5, -53, -106, 73, 32, -77, -56, 121, -73, 61, -105, 126, -5, 80, 7, -112, 74, -106, -103, -59, -18, 111, -43};
		PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec(decPrivateKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(keysp);
	}

	public Object[][] dataFor_testGenerateCertificates() throws IOException{
		return new Object[][] {
				{DER_CERTIFICATE_NAME, 				detailsFor_DER_CERTIFICATEfile()},
				{PEM_CERTIFICATE_CHAIN_NAME, 		detailsFor_PEM_CERTIFICATE_CHAINfile()},
				{PEM_CERTIFICATE_WITHOUT_TEXT_NAME, detailsFor_PEM_CERTIFICATE_WITHOUT_TEXTfile()}
		};
	}

	private List<CertificateDetails> detailsFor_PEM_CERTIFICATE_WITHOUT_TEXTfile() throws IOException {
		List<CertificateDetails> certificateList = new ArrayList<CertificateDetails>();
		certificateList.add(
				new CertificateDetails(
						1661595695000L, 
						1377771695000L, 
						new byte[]{42, 101, 101, 29, -127, 59, 41, 23, 29, -107, 73, -29, 72, 90, -76, 126, -87, -82, 113, 51, 66, 61, -100, 10, 108, -2, -15, 39, 19, 7, -57, -88, 32, -72, 61, -25, -35, -6, -5, 123, 22, 19, -70, -117, -103, 36, 111, 55, 117, 60, 49, 91, -16, 25, 73, 45, -19, 30, -62, -72, -101, 110, -54, 127, 127, -76, -87, -10, 40, 55, -103, -96, -19, -42, 16, 39, 13, -121, -87, 90, -32, -55, -82, -74, -6, 96, -98, -83, 120, -43, -74, 74, 116, -57, 103, 41, 66, -63, 57, -105, -32, 114, 92, 117, -5, -57, 106, -83, -9, -92, 22, 110, 119, 10, -2, -128, 2, -33, -72, -68, -39, 11, -110, 56, 26, 100, 108, 0, 28, 87, -44, -81, -58, -106, -120, 27, 72, -76, -69, 4, -95, -79, 29, -63, -12, 21, -28, -15, -67, -47, 100, -65, -1, -27, 72, 92, 74, -91, -127, -118, 86, 111, -46, -60, 121, -20, -64, 124, -12, -85, 22, 78, -110, -66, 127, -22, -113, 33, 36, 43, 117, 41, -115, 37, 76, -24, -58, -123, 92, -51, 14, 99, 16, -125, 120, -75, -19, -57, -96, 0, 36, 59, -125, 48, 24, 125, 35, 96, -43, -71, 47, 14, -56, 40, -92, 124, -102, -82, -81, -29, -99, -90, -39, -65, -10, 104, -128, 53, -18, -117, -17, 27, 94, -32, 80, -35, -78, 91, -55, -37, 119, -19, 105, -59, -103, 64, 75, -73, -82, -95, 100, -120, -17, -78, 103, -107}, 
						"CN=server, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN", 
						"CN=CA_CSM, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN"
				)
		);
		return certificateList;
	}


	private List<CertificateDetails> detailsFor_DER_CERTIFICATEfile() throws IOException {
		List<CertificateDetails> certificateList = new ArrayList<CertificateDetails>();
		certificateList.add(
				new CertificateDetails(
						1661595695000L, 
						1377771695000L, 
						new byte[]{42, 101, 101, 29, -127, 59, 41, 23, 29, -107, 73, -29, 72, 90, -76, 126, -87, -82, 113, 51, 66, 61, -100, 10, 108, -2, -15, 39, 19, 7, -57, -88, 32, -72, 61, -25, -35, -6, -5, 123, 22, 19, -70, -117, -103, 36, 111, 55, 117, 60, 49, 91, -16, 25, 73, 45, -19, 30, -62, -72, -101, 110, -54, 127, 127, -76, -87, -10, 40, 55, -103, -96, -19, -42, 16, 39, 13, -121, -87, 90, -32, -55, -82, -74, -6, 96, -98, -83, 120, -43, -74, 74, 116, -57, 103, 41, 66, -63, 57, -105, -32, 114, 92, 117, -5, -57, 106, -83, -9, -92, 22, 110, 119, 10, -2, -128, 2, -33, -72, -68, -39, 11, -110, 56, 26, 100, 108, 0, 28, 87, -44, -81, -58, -106, -120, 27, 72, -76, -69, 4, -95, -79, 29, -63, -12, 21, -28, -15, -67, -47, 100, -65, -1, -27, 72, 92, 74, -91, -127, -118, 86, 111, -46, -60, 121, -20, -64, 124, -12, -85, 22, 78, -110, -66, 127, -22, -113, 33, 36, 43, 117, 41, -115, 37, 76, -24, -58, -123, 92, -51, 14, 99, 16, -125, 120, -75, -19, -57, -96, 0, 36, 59, -125, 48, 24, 125, 35, 96, -43, -71, 47, 14, -56, 40, -92, 124, -102, -82, -81, -29, -99, -90, -39, -65, -10, 104, -128, 53, -18, -117, -17, 27, 94, -32, 80, -35, -78, 91, -55, -37, 119, -19, 105, -59, -103, 64, 75, -73, -82, -95, 100, -120, -17, -78, 103, -107}, 
						"CN=server, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN", 
						"CN=CA_CSM, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN"
				)
		);
		return certificateList;
	}

	private List<CertificateDetails> detailsFor_PEM_CERTIFICATE_CHAINfile() throws IOException {
		List<CertificateDetails> certificateList = new ArrayList<CertificateDetails>();
		certificateList.add(
				new CertificateDetails(
						1661595695000L, 
						1377771695000L, 
						new byte[]{42, 101, 101, 29, -127, 59, 41, 23, 29, -107, 73, -29, 72, 90, -76, 126, -87, -82, 113, 51, 66, 61, -100, 10, 108, -2, -15, 39, 19, 7, -57, -88, 32, -72, 61, -25, -35, -6, -5, 123, 22, 19, -70, -117, -103, 36, 111, 55, 117, 60, 49, 91, -16, 25, 73, 45, -19, 30, -62, -72, -101, 110, -54, 127, 127, -76, -87, -10, 40, 55, -103, -96, -19, -42, 16, 39, 13, -121, -87, 90, -32, -55, -82, -74, -6, 96, -98, -83, 120, -43, -74, 74, 116, -57, 103, 41, 66, -63, 57, -105, -32, 114, 92, 117, -5, -57, 106, -83, -9, -92, 22, 110, 119, 10, -2, -128, 2, -33, -72, -68, -39, 11, -110, 56, 26, 100, 108, 0, 28, 87, -44, -81, -58, -106, -120, 27, 72, -76, -69, 4, -95, -79, 29, -63, -12, 21, -28, -15, -67, -47, 100, -65, -1, -27, 72, 92, 74, -91, -127, -118, 86, 111, -46, -60, 121, -20, -64, 124, -12, -85, 22, 78, -110, -66, 127, -22, -113, 33, 36, 43, 117, 41, -115, 37, 76, -24, -58, -123, 92, -51, 14, 99, 16, -125, 120, -75, -19, -57, -96, 0, 36, 59, -125, 48, 24, 125, 35, 96, -43, -71, 47, 14, -56, 40, -92, 124, -102, -82, -81, -29, -99, -90, -39, -65, -10, 104, -128, 53, -18, -117, -17, 27, 94, -32, 80, -35, -78, 91, -55, -37, 119, -19, 105, -59, -103, 64, 75, -73, -82, -95, 100, -120, -17, -78, 103, -107}, 
						"CN=server, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN", 
						"CN=CA_CSM, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN"
				)
		);
		certificateList.add(
				new CertificateDetails(
						1661595623000L, 
						1377771623000L, 
						new byte[]{-116, 41, -11, 43, -56, -73, -124, -70, -71, 95, 22, -31, 14, -8, 91, -32, -98, 35, 44, 8, 41, -78, -117, 37, 0, 58, 104, 55, 77, 50, 89, -67, -85, 127, -42, -36, -52, -56, -19, -113, -17, -20, -36, -88, -126, 106, -19, -40, 121, 119, -88, 122, 81, -45, 84, 107, -98, 38, 65, -33, -77, -11, 120, 48, 68, 9, -99, -68, -79, -54, 27, -95, -56, -49, -12, -6, 8, 72, -11, -1, -108, -46, 0, -15, 89, -118, -115, -24, 123, -30, 32, 100, 94, -86, -42, -42, -58, -53, 96, 46, -86, 18, 60, -79, -57, 65, -34, -1, -95, -124, -93, 123, 39, -115, -78, -52, 6, -10, -46, -72, -102, 67, -42, 108, 0, -94, -9, -7, 98, 40, 84, 38, 37, -78, -22, -90, -117, 18, -122, 115, 47, 66, 48, -101, -15, 67, 84, 48, 108, 66, -1, -117, -101, 40, 69, 55, 83, 1, -80, 121, 83, 3, -98, -4, 39, 62, 53, -11, 97, 100, 75, 19, 82, -58, -37, 123, 46, -125, 102, 118, -69, 29, -124, -122, 79, -95, -107, 85, 2, 98, 92, -22, -50, -74, 56, -68, 30, -43, -87, -60, -66, 4, -45, -23, 105, 48, -127, 93, 75, -2, -25, -52, -113, 101, 66, -20, -55, 63, -113, -41, 27, -79, 93, 79, -60, 105, -27, 90, -17, -89, 76, -96, -117, 119, 105, -81, -95, -49, -6, 29, 78, 84, -110, -10, -57, 118, 119, 49, 33, -44, -15, 108, 8, 84, 117, -48}, 
						"CN=CA_CSM, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN", 
						"CN=CA_Elitecore, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN"
				)
		);
		certificateList.add(
				new CertificateDetails(
						1661595536000L, 
						1377771536000L, 
						new byte[]{-122, 62, 61, -70, 10, -40, 92, 16, -9, 90, -48, 78, 65, 120, -83, -96, 40, -33, -83, 55, 76, 20, 63, 46, -37, 46, 76, -93, -50, 6, -49, -60, 20, 82, -90, -67, -122, -9, 45, -79, -109, -78, 80, 76, -90, 81, 58, -123, -55, 77, -18, -121, -18, 94, 60, 33, -38, -8, 114, 7, -28, -96, 95, -38, -125, 45, -44, 19, -38, -53, -69, -106, -55, 12, -49, 49, 67, 122, 38, 59, -82, 22, 121, 125, -77, -64, -106, -34, -34, 75, 119, 76, 25, 98, 61, 6, -82, -29, -30, -13, 11, -67, 41, -69, -83, 34, -24, -66, -5, -74, -75, 64, -14, -27, 75, -88, 57, 41, -47, 100, 9, 104, 65, 12, 10, -10, -54, -13, 111, -35, -62, -56, 10, 6, -93, -59, 77, 12, 71, -127, 15, 31, -89, -69, -8, -127, 125, -35, 96, -24, -106, -30, 82, 66, 88, 61, 33, -84, -43, -81, -44, 27, 23, -49, 33, 71, 120, -4, 114, 26, -56, -114, 58, 91, -110, -31, -107, 90, -51, -113, 118, 127, -105, 106, 56, 43, 76, -47, -84, -123, -40, 59, 78, 113, -57, -84, -73, -86, -81, -41, -117, 52, 89, -83, -98, 65, -83, -38, 85, -30, 20, -25, -127, -100, 120, -39, 62, -51, -26, -124, -33, -107, 115, 9, -91, -101, 51, 88, 112, -111, -42, -81, -43, 117, 45, 25, 102, -108, -45, -15, 36, -113, 21, -43, 17, 42, -48, 6, -66, -61, -33, -16, -93, -68, -2, 55}, 
						"CN=CA_Elitecore, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN", 
						"CN=root, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN"
				)
		);
		certificateList.add(
				new CertificateDetails(
						1693124290000L, 
						1377764290000L, 
						new byte[]{-98, 47, -61, 85, -88, -13, -122, 31, -77, -124, 68, -50, -41, -58, -55, -38, 33, 12, 98, -42, -74, 53, 69, 35, -13, -59, -24, 53, -33, -22, -22, 47, 107, 10, -115, 109, 42, 105, -49, 114, 125, -106, -97, -81, 125, -27, 82, -57, 22, -90, 76, -106, -110, 92, -7, -96, 96, -29, -89, 28, -61, -99, 68, 24, -14, 17, 34, 69, 68, 24, -78, -69, 120, 21, 25, 23, -111, 2, -69, -120, -48, 92, -11, -86, 72, -103, -72, -115, 49, 49, -120, 14, 91, -48, -113, 127, -23, 0, 8, 20, -38, 90, -97, -14, -15, 52, 2, 84, -88, 16, -15, -42, -66, 46, -97, -83, 59, 24, -98, -79, -115, 53, -88, 36, 116, 57, 79, 109, -115, -116, 101, 4, 83, 38, 39, -73, 118, 55, 36, -109, 113, 108, 15, 108, -92, -19, 108, -37, 1, -55, 70, -64, 64, -38, -105, -118, -53, 28, -26, 73, 4, 19, -111, 7, 18, -61, 83, -76, 83, 85, 1, -104, 117, 53, 96, -105, 58, -13, 125, 117, -79, 15, 36, 15, -96, -84, -23, -20, -57, 122, 31, -72, -77, 72, 65, -43, -91, 109, -33, 46, -119, -49, 39, -52, 11, -83, 108, -91, 44, 19, 121, -22, 95, -61, -105, -95, -1, 93, 88, 70, -42, -107, 68, 45, 64, -110, -45, -62, 56, -43, 104, 105, 30, -48, -22, 41, 118, -61, 30, 5, -45, -101, 122, -32, -21, -40, 78, 60, 103, 50, -74, 126, 89, -86, 118, 113}, 
						"CN=root, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN", 
						"CN=root, OU=CSM, O=Elitecore, L=Ahmedabad, ST=Gujarat, C=IN"
				)
		);

		return certificateList;
	}
	
	private static void assertX509CertificateDetails(List<CertificateDetails> details, Collection<X509Certificate> certificates) {
		for(int i=0; i<certificates.size(); i++) {
			CertificateDetails expectedCertificate = details.get(i);
			X509Certificate certificate = ((List<X509Certificate>)certificates).get(i);

			assertX509CertificateDetails(expectedCertificate, certificate);
		}
	}

	private static void assertX509CertificateDetails(CertificateDetails expectedCertificate,X509Certificate certificate) {
		assertEquals(expectedCertificate.getNotAfter(), certificate.getNotAfter().getTime());
		assertEquals(expectedCertificate.getNotBefore(), certificate.getNotBefore().getTime());
		assertArrayEquals(expectedCertificate.getSignature(), certificate.getSignature());
		assertEquals(expectedCertificate.getSubjectDN(), certificate.getSubjectDN().getName());
		assertEquals(expectedCertificate.getIssuerDN(), certificate.getIssuerDN().getName());
	}

	
	private static class CertificateDetails{
		private long notBefore;
		private long notAfter;
		private byte[] signature;
		private String subjectDN;
		private String issuerDN;

		public CertificateDetails(long notAfter, long notBefore, byte[] signature, String subjectDN, String issuerDN) {
			this.notAfter = notAfter;
			this.notBefore = notBefore;
			this.signature = signature;
			this.subjectDN = subjectDN;
			this.issuerDN = issuerDN;
		}


		public long getNotBefore() {
			return notBefore;
		}
		public long getNotAfter() {
			return notAfter;
		}
		public byte[] getSignature() {
			return signature;
		}
		public String getSubjectDN() {
			return subjectDN;
		}
		public String getIssuerDN() {
			return issuerDN;
		}
	}
}
