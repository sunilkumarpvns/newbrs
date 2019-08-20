package com.elitecore.aaa.core.radius.service.auth.handlers;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.elitecore.aaa.core.conf.EAPConfigurationData;
import com.elitecore.aaa.core.conf.EAPConfigurationData.VendorSpecificCertificate;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.certificate.CertificateTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class RadEapHandlerValues {
	private String scenario;
	private PrivateKey privateKey;
	private List<byte[]> serverCertificateChainBytes;
	private Collection<X509Certificate> x509TrustedCertificate;
	private AccountData accountData;
	private List<Integer> enabledCipherSuites;
	private int defaultNegotiationMethod;
	private List<Integer> enabledEapMethods;
	private boolean sendCertificateRequest;
	private boolean isFailureExpected;
	private ProtocolVersion minProtocolVersion;
	private ProtocolVersion maxProtocolVersion;
	private List<Integer> certificateTypes;
	private List<VendorSpecificCertificate> vendorSpecificCertificates;
	
	public RadEapHandlerValues(String scenario,ProtocolVersion minProtocolVersion, ProtocolVersion maxProtocolVersion,
			List<byte[]> serverCertificateBytes2,PrivateKey privateKey2,
			Collection<X509Certificate> x509TrustedCertificate2, AccountData accountData2,
			List<Integer> enabledCipherSuites, List<Integer> enabledEapMethods,
			boolean sendCertificateRequest, int defaultNegotiationMethod,
			boolean isFailureExpected,List<Integer> certificateTypes,
			List<VendorSpecificCertificate> vendorSpecificCertificates) {
	 
		this.scenario = scenario;
		this.minProtocolVersion = minProtocolVersion;
		this.maxProtocolVersion = maxProtocolVersion;
		this.serverCertificateChainBytes = serverCertificateBytes2;
		this.privateKey = privateKey2;
		this.x509TrustedCertificate = x509TrustedCertificate2;
		this.accountData = accountData2;
		this.enabledCipherSuites = enabledCipherSuites;
		this.defaultNegotiationMethod = defaultNegotiationMethod;
		this.enabledEapMethods = enabledEapMethods;
		this.sendCertificateRequest = sendCertificateRequest;
		this.isFailureExpected = isFailureExpected;
		this.certificateTypes = certificateTypes;
		this.vendorSpecificCertificates = vendorSpecificCertificates;
	}

	public List<VendorSpecificCertificate> getVendorSpecificCertificates() {
		return vendorSpecificCertificates;
	}

	public List<Integer> getCertificateTypes() {
		return certificateTypes;
	}

	public String getScenario() {
		return scenario;
	}
	
	public ProtocolVersion getMinProtocolVersion() {
		return minProtocolVersion;
	}

	public ProtocolVersion getMaxProtocolVersion() {
		return maxProtocolVersion;
	}

	public boolean isFailureExpected() {
		return isFailureExpected;
	}
	
	public List<Integer> getEnabledCipherSuites() {
		return enabledCipherSuites;
	}

	public int getDefaultNegotiationMethod() {
		return defaultNegotiationMethod;
	}

	public List<Integer> getEnabledEapMethods() {
		return enabledEapMethods;
	}

	public boolean isSendCertificateRequest() {
		return sendCertificateRequest;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public List<byte[]> getServerCertificateChainBytes() {
		return serverCertificateChainBytes;
	}

	public Collection<X509Certificate> getX509TrustedCertificate() {
		return x509TrustedCertificate;
	}

	public AccountData getAccountData() {
		return accountData;
	}


	public static class RadEapHandlerValuesBuilder{
		public class OUIBuilder{
			private String oui;
			private String certificateStringInHex;
			private String serverPrivateKeyHexString;
			private PrivateKeyAlgo privateKeyAlgo;
			private String serverPrivateKeyPassword;
			private boolean skipOnPrivateKeyError;
			private boolean skipOnCertificateChainError;
			
			public OUIBuilder(String oui) {
				this.oui = oui;
			}
			
			public OUIBuilder withCertificate(String certificateStringInHex){
				this.certificateStringInHex = certificateStringInHex;
				return this;
			}
			
			public OUIBuilder skipOnCertificateFormationError(){
				this.skipOnCertificateChainError = true;
				return this;
			}
			
			public OUIBuilder withPrivateKey(String privateKeyPassword,PrivateKeyAlgo privateKeyAlgo,  String serverPrivateKeyHexString){
				this.serverPrivateKeyHexString = serverPrivateKeyHexString;
				this.privateKeyAlgo = privateKeyAlgo;
				this.serverPrivateKeyPassword = privateKeyPassword;
				return this;
			}
			
			public OUIBuilder skipOnPrivateKeyFormationError(){
				this.skipOnPrivateKeyError = true;
				return this;
			}
			
			public RadEapHandlerValuesBuilder build() throws Exception{
				final List<byte[]> chain = buildChain(certificateStringInHex,skipOnCertificateChainError);
				final PrivateKey pk = getPrivateKey(serverPrivateKeyPassword, privateKeyAlgo, serverPrivateKeyHexString,skipOnPrivateKeyError);
				return RadEapHandlerValuesBuilder.this.withOUI(oui,chain,pk);
			}
		}
		
		
		private static final String PRIVATE_KEY_PASSWORD = "elitecore";
		public static final String PRIVATE_KEY_RSA_BYTES_HEXSTR = "0x308204e9301b06092a864886f70d010503300e04089cf8d4e154e0dad302020800048204c8e23ae37ed6aba8118923e483cd2afa15b8dd819a5e04166f0416cdff64c1d5e70d576463597f91d3d6c92d19020536cce7bbd6cf36ab310cfb8d19e4492f476244b9336e7b49000054dfeb76cf55c3be136110942fe23f6d61d381ff1a68ee147310087fea71d06e09e2ab9672f4850c8f61725bfa17a209399983e8ab85c7593eced765656b45e5fdbb959e8b4e7619b0f83de7b42348563f187662322cc04b4d7bb9a4b727bb6e9e3dde8fbf26e05eaef65d176033fd883f8dc69d2e732c81db7eb630295f0b15fa65ec8c1820e1e88ea8395ec8106df60e8196f1dd5504c74fbcb0dbd88c4831a661922dbf8256404aeafd5df618e43249e7e3b1e498bf868b7267a547f1127d5d1ea422bd2e0712f72dfc8ca57230608c63746a3314ca997973cd177488b13a9bac483c697362122bd49e5b0ed99a8fbfd7352c0503c316c6827fc39d48571cfa1b3084a33601a432740c65cb276ab3bdff77401298f5d4bdea4aff2eaea758df90652c2d251d4bc951dbfa9dc547dc47cb7ebcb28fdf69d1111692953e55287d9c757caba66b83cf65a52b4d36252dbf2fdd48621b819c0c32f04b1ccad20b3a10a1dcf4af1a43555e2d65c96f30d97994069e8e45a387a726e0053e92fd40d44b34f771dab9b027047bfb0a8f755fae550aef3ab24079d3d2fa6b7b1b643707e3c97d00517d1da31aa46ba305ecdd0493b63d05bd46e1728ae019bf34beb4ad8c21364b82cf1880d3895902065a38827e3d81eb3525b7d4904962132aea18b21a91a6b07eedbfeb6c675ac6f8ab2de9cc493c91185a2b73cb86b472575ba9b19d26e7acea160cc5f51c6a22cd07d5caff18ed9274ff7092867f60ae25e0b516bb712f588ae54918a2df74ec4bb1f05e89a4f3fe2c28e28abd6263dd5c92d7c8dec493ae66eed72a1cf21a8ad95f67126f96669161c34451c0e2bb86cbe20aab3e6c8bc41161a4277f898465c0edb36dec40c9a4070a5e081bb66eec8e12ca040e307814600989e076f405a8bafc35c3ae6554e8a14e7989a4a078cf095aa1140c07c8faefe45dff3e99457e250811ce5d2b10309fa281364168e8b57a5fd3116899d0494d445bea04d4d97c83f53088e9c239636b84f7421768fe8c667bd92596ef41e7bae07711a82134e7ace6df673001db739a24c127415ec6b084da5763c178432a3d209f98a8d163a680d2ad3161a00252f7d7c12a0c8ee6f77a820354121444d952c1fd4a8d7061be4f6f236354fae50207b8182e76fa677e962c11b92e3eeff38c20ed752a57cb45fd719ea6b2e7f9ae5e577149b8e8d8db7d70c40469ad024f212cf29261584e0c8c9868f4ca448b6361e08cffea69729b0241af966d74ccd1b321f3c20b61092d554a856a4d2e7480fede32a2d9aa4fb303ef3c447240f7e90eca0bfbef3bccdd2e0fa4acbd994bc853dac2e43c0739e2f2c4bbbf6384aa282a3fc363feef6d7a56ade2b29c143d1c0d591945912229c19dcc919bba1f54d84ea1aac7ecd78818e9063c9afca46159cd90abd1d944479db62ad728ab474e6fb496af16f55a88e26f7975ea0b10df9af6cfade8984b41886ab4aec81591f9b497d5b7794ae42a1dd07aa983207a880ac8501d9c326847f98f850d1c2a05cf6ed1f6d50e372eb89e7fc7925495022a6a2efe0448224922f4a4900822e718e30e386d13ca19c739eedc80c0b3a53aaa2fdaa11d601e1d4ea57ebc34";
		public static final String SERVER_CERTIFICATE_CHAIN_RSA_HEXSTR = "0x2d2d2d2d2d424547494e2043455254494649434154452d2d2d2d2d0d0a4d494945574443434130436741774942416749424244414e42676b71686b69473977304241515546414443427054454c4d416b474131554542684d43535534780d0a4544414f42674e564241675442306431616d467959585178456a415142674e56424163544355466f6257566b59574a685a44456c4d434d474131554543684d630d0a525778706447566a62334a6c4946526c59326874626d3973623264705a584d675448526b4c6a45524d4138474131554543784d495257787064475642515545780d0a4654415442674e5642414d54444556736158526c593239795a534244515445664d42304743537147534962334451454a41525951593246415a5778706447566a0d0a62334a6c4c6d4e7662544165467730774f5441784d4463784d6a55784d445a61467730784f4441784d4455784d6a55784d445a614d4947734d517377435159440d0a5651514745774a4a546a45514d4134474131554543424d485233567159584a68644445534d4241474131554542784d4a515768745a575268596d466b4d5351770d0a496759445651514b4578744662476c305a574e76636d55675647566a614735766247396e6157567a494578305a4334784554415042674e5642417354434556730d0a6158526c515546424d526b7746775944565151444578424662476c305a574e76636d556755325679646d56794d534d774951594a4b6f5a496876634e41516b420d0a4668527a5a584a325a584a415a5778706447566a62334a6c4c6d4e7662544343415349774451594a4b6f5a496876634e415145424251414467674550414443430d0a41516f4367674542414b71645a562b522f7669576333592b5369356543572f5444587156307674473234392f3449754d794f517477573968334172397137374b0d0a547a6e4d623163344153446f696554304e686d704f495453684c58733879575a65573350507167574232635555705573536c792f494350786e4a76615a7a65590d0a6f4a717a4a366d697036793150306f30374b62666a486d4130766a68686769737635726c44517754504d3557306770564b76666a4165764a4479497471724d520d0a386a5671446d5852325477584e7031786d41386950705241787369624173646545345346764d73394b52383375796a47752b6675677a7a61553270366546517a0d0a694b4152424e6e6f45677247736c6d68536239766a415779426d6265714445355a4f7363646d6f664e6341726e757a456e35554366647749666d616b4f6d4a750d0a765a4e2b63636e75536845337a444f2b426d4a74624c7135585a78332f694d434177454141614f4269544342686a414a42674e5648524d45416a41414d4173470d0a4131556444775145417749463444417342676c67686b67426876684341513045487859645433426c626c4e54544342485a57356c636d46305a575167513256790d0a64476c6d61574e6864475577485159445652304f42425945464e516f522b7163565031516d62483367664f6e71307a562f3036694d42384741315564497751590d0a4d426141464f543946767757744257536c717973666a4b2b30454566716270744d4130474353714753496233445145424251554141344942415141536155714f0d0a5573446e596c5771564c5a306d556b792b2b504d6d36354c5364324a4837344837346a31756c537571543131472f614b4476462b445a2b543670414b36622f4d0d0a753353616a4e3648624c3066752b594d5978664f41504d425846777a494c39487358736f4f7251486252756f4848563373494d7a4c3450454e30322b63586d760d0a2b76456a4c6b6c6b4f335067466532592b424b4246552b526a7274506c314736734653663176575a5437754c6c5178584743556e5943625431376e537765306a0d0a4a784572752f6769564b36476155344e315243684c414444627476305a6d6a62736e6c744e6e4947664650535a51324a705944623864423349725238425a4c490d0a42514c32354452426f6c4e65725478397579442b45397358496e5734372b774643456f504a414f34753776575757582b62665a70366c41594e347835566a37570d0a6f642f353844516a4f683731473379500d0a2d2d2d2d2d454e442043455254494649434154452d2d2d2d2d0d0a2d2d2d2d2d424547494e2043455254494649434154452d2d2d2d2d0d0a4d49494533444343413853674177494241674942657a414e42676b71686b69473977304241515546414443427054454c4d416b474131554542684d43535534780d0a4544414f42674e564241675442306431616d467959585178456a415142674e56424163544355466f6257566b59574a685a44456c4d434d474131554543684d630d0a525778706447566a62334a6c4946526c59326874626d3973623264705a584d675448526b4c6a45524d4138474131554543784d495257787064475642515545780d0a4654415442674e5642414d54444556736158526c593239795a534244515445664d42304743537147534962334451454a41525951593246415a5778706447566a0d0a62334a6c4c6d4e7662544165467730774f5441784d4463784d6a49344d6a4a61467730784f5441784d4455784d6a49344d6a4a614d49476c4d517377435159440d0a5651514745774a4a546a45514d4134474131554543424d485233567159584a68644445534d4241474131554542784d4a515768745a575268596d466b4d5355770d0a497759445651514b4578784662476c305a574e76636d55675647566a61473175623278765a326c6c6379424d644751754d524577447759445651514c457768460d0a62476c305a554642515445564d424d474131554541784d4d525778706447566a62334a6c49454e424d5238774851594a4b6f5a496876634e41516b424668426a0d0a5955426c62476c305a574e76636d5575593239744d494942496a414e42676b71686b6947397730424151454641414f43415138414d49494243674b43415145410d0a783154496a576952704455517134394453594e305366323975716f754c365a4257674147303879414c626d61523945314b654e4872546c644c714c726f64646b0d0a4f52504178317574734c4d66545164756b6539464c7a6d51504555374d4a634550552f4553344d7147752b686b794a797756564d797145706e2f306f645353570d0a786c57764b694d79424b644643636a75627265764557522f32465a4e636d5a69723272344f527a465263444d79696d6c46774d54733037776a6e4353694564450d0a6653496e6e323550644b5a795438394e42724e394c6578566974556a7259466c705946355565392b664866696e2f79374957556674417766337a73466b4a33440d0a423275356a646a643158682b41725569377938384f4d623651305045544a6f737a71413135344e394c4f4e6e2f4659494b3454726a5a2b41523849346d4164310d0a53705266707434646630316847594c6f6b38367343514944415141426f344942457a434341513877485159445652304f42425945464f543946767757744257530d0a6c717973666a4b2b30454566716270744d49485342674e5648534d4567636f7767636541464f543946767757744257536c717973666a4b2b30454566716270740d0a6f5947727049476f4d49476c4d517377435159445651514745774a4a546a45514d4134474131554543424d485233567159584a68644445534d424147413155450d0a42784d4a515768745a575268596d466b4d535577497759445651514b4578784662476c305a574e76636d55675647566a61473175623278765a326c6c6379424d0d0a644751754d524577447759445651514c4577684662476c305a554642515445564d424d474131554541784d4d525778706447566a62334a6c49454e424d5238770d0a4851594a4b6f5a496876634e41516b424668426a5955426c62476c305a574e76636d557559323974676746374d41774741315564457751464d414d42416638770d0a437759445652305042415144416745474d4130474353714753496233445145424251554141344942415141637a723159775765687070694b646d38335a6c50730d0a73574f534a74497553774d2b547a4b755664676842444f3258526344347774704936784e4750464a41692f79336a485865794a555165526e6a434e58355a4b700d0a374f426279484f4e494a4941667959424e59765772426446786468774434302b2f4b677170435a54645448727a44664b6e7a5041635a49746f6873724c48794a0d0a656350764c45414c573732467074515542673052796563664c4d633975596638514b385152566673704836652b304e6673744d37314a6955506e6364564657790d0a513336504646436d3366357870636d493834312f7976664f2f7754352b73714d512f71545a6d655a4535454177793151464c7559436553496c34677847504e420d0a66704c6c79677a49472b516e386454674a78463355635a35396f4856666f30454f50724e4a4c353065726236784b41464e6169754d577858435879452f6642740d0a2d2d2d2d2d454e442043455254494649434154452d2d2d2d2d0d0a";
		public static final String PRIVATE_KEY_DSA_BYTES_HEXSTR = "0x3081f0301b06092a864886f70d010503300e0408054a4e9166d077b1020208000481d0d6b716a95bb49989abd5d38cdc4ce4cc2d75960e0e40444dfa95315bb4135f2f641a0ee2a2f708ed38d4b10a3f80fd972a4b147b8c23014ba8bc234c5167b4b791d6c3a43821c228962d579b4e2e1060ea9156b8295c8517805aeda5c800eb055f185aded32bcad5ef0272053b722e5efffcc3c20a04615d13f85f2b75a4eedbfee2dec5ffcdf703cdaefc657658ef85240130cd6d958e12accf7811e97ef1e589b7e61b361c6022c328ffc1435c1cb14c0ce2613f4f3d7e303936366cdec6a977858e9f79edd0f3e8333de3a314dfaf";
		public static final String SERVER_CERTIFICATE_CHAIN_DSA_HEXSTR = "0x2d2d2d2d2d424547494e2043455254494649434154452d2d2d2d2d0d0a4d49494443444343417369674177494241674943414c4977435159484b6f5a497a6a6745417a43426a7a454c4d416b474131554542684d43535534784544414f0d0a42674e564241674d42306431616d467959585178456a415142674e564241634d4355466f6257566b59574a685a4445534d424147413155454367774a525778700d0a6447566a62334a6c4d517777436759445651514c44414e4455303078446a414d42674e5642414d4d4255526c633246704d5367774a67594a4b6f5a496876634e0d0a41516b4246686c74595778686469356b5a584e686155426c62476c305a574e76636d5575593239744d4234584454457a4d44637a4d4441324e4459304d566f580d0a445449794d4463794f4441324e4459304d566f7767593878437a414a42674e5642415954416b6c4f4d52417744675944565151494441644864577068636d46300d0a4d524977454159445651514844416c426147316c5a47466959575178456a415142674e5642416f4d435556736158526c593239795a54454d4d416f47413155450d0a4377774451314e4e4d51347744415944565151444441567459577868646a456f4d43594743537147534962334451454a4152595a62574673595859755a47567a0d0a59576c415a5778706447566a62334a6c4c6d4e766254434238444342714159484b6f5a497a6a6745415443426e414a42414b384d544b724536335567696a64640d0a7a624436346339596c52444b6234793747376a503671736c486a66454b73725932545a6c5341674a56682b4c4e4c4b623037387633327778782b6673707272700d0a79645942516163434651436c565564784b48354a4b47474d2f534636354a4847716b76786f774a4148316c496262722f4c7667317247704f456d507835462f7a0d0a7479676661714445396a526f4473556748414f56674354365233717750442b672f61774148506a6f67613565774649665574583032593770776e4f6c45774e440d0a41414a415076662b4b744937554e6679635645594c4269396b5a5032486b5a4777497971596e4b6a6232364a4d344f4e75514c502f644f394c594d594b5739730d0a5a4a6c7a4831305266623464457862414337317133442b455a364e374d486b774351594456523054424149774144417342676c67686b674268766843415130450d0a487859645433426c626c4e54544342485a57356c636d46305a5751675132567964476c6d61574e6864475577485159445652304f42425945464b314b377344360d0a56484e69754f54653869365155795546724a79554d42384741315564497751594d426141464755464b44684d43324c56326267527669697961474f415775732f0d0a4d416b4742797147534d343442414d444c7741774c41495544527737416636766c566a373438704e426a62697264485a50696b434644314d43533771486877580d0a39766c746e4449474647566d446d72650d0a2d2d2d2d2d454e442043455254494649434154452d2d2d2d2d0d0a2d2d2d2d2d424547494e2043455254494649434154452d2d2d2d2d0d0a4d4949433544434341715367417749424167494a4149495352565537596336584d416b4742797147534d343442414d7767593878437a414a42674e56424159540d0a416b6c4f4d52417744675944565151494441644864577068636d46304d524977454159445651514844416c426147316c5a47466959575178456a415142674e560d0a42416f4d435556736158526c593239795a54454d4d416f47413155454377774451314e4e4d5134774441594456515144444156455a584e686154456f4d4359470d0a43537147534962334451454a4152595a62574673595859755a47567a59576c415a5778706447566a62334a6c4c6d4e7662544165467730784d7a41334d7a41770d0a4e6a51314e545261467730794d7a41334d6a67774e6a51314e5452614d4947504d517377435159445651514745774a4a546a45514d41344741315545434177480d0a5233567159584a68644445534d424147413155454277774a515768745a575268596d466b4d524977454159445651514b44416c4662476c305a574e76636d55780d0a4444414b42674e564241734d41304e545454454f4d41774741315545417777465247567a59576b784b44416d42676b71686b69473977304243514557475731680d0a624746324c6d526c63324670514756736158526c593239795a53356a62323077676641776761674742797147534d343442414577675a77435151434c336134760d0a456451484c64492f77786b737133332b69554835422b34536b516830666c69576a4d7537713270533850376c726b6b6f6865745543356b42413158566859444f0d0a4e3553325a70674c6f66707a3575736c416855412b7048506d716e687a66395a36576567334f5676634d674652464d43514866426a617a4f507966554179422b0d0a4344787751564173304a454a316e5234472f336a532f326a45464c3762475a37772f38754b49654d542f396c7164794e41716379617a5466445874624b6f4e6a0d0a6d575842666b73445177414351424d346d78325130624e666841427635794b687a77547364326f6259427068726b7741436e38666236466c63557634594a79780d0a7171645370794d4b5a54416a562f50447039654963537755712b50704c376966306a656a5544424f4d42304741315564446751574242526c42536734544174690d0a31646d344562346f736d686a67467272507a416642674e5648534d45474441576742526c425367345441746931646d344562346f736d686a67467272507a414d0d0a42674e5648524d45425441444151482f4d416b4742797147534d343442414d444c7741774c41495562586b7662735032493437344f77386b4d505a3359316e700d0a684a38434643756e5a3052684e383857323165414d4b715a4e4c5666584148580d0a2d2d2d2d2d454e442043455254494649434154452d2d2d2d2d0d0a"; 
		private static final String USERNAME_PASSWORD = "eliteaaa";
		
		static final String CLIENT_TRUSTED_CERTIFICATE_RSA = "0x2d2d2d2d2d424547494e2043455254494649434154452d2d2d2d2d0a4d4949455144434341796967417749424167494a414c4c336c493455584573684d413047435371475349623344514542425155414d473478437a414a42674e560a42415954416b6c754d52417744675944565151494577644864577068636d46304d52457744775944565151484577684c61474674596d6868644445534d4241470a4131554543684d4a525778706447566a62334a6c4d517777436759445651514c45774e44553030784744415742674e5642414d54443074316247526c5a5841670a554746755932686862444165467730784d7a417a4d4467774e4455304d446861467730794d7a417a4d4459774e4455304d4468614d473478437a414a42674e560a42415954416b6c754d52417744675944565151494577644864577068636d46304d52457744775944565151484577684c61474674596d6868644445534d4241470a4131554543684d4a525778706447566a62334a6c4d517777436759445651514c45774e44553030784744415742674e5642414d54443074316247526c5a5841670a554746755932686862444343415349774451594a4b6f5a496876634e4151454242514144676745504144434341516f4367674542414d523334316141787774710a364d5250305a6c574b7559654434622b32697148576f69736676734749344b6554677362616654584c4c31514d31504b4542366b655a3737666c70766d6b68630a38643543524565566b3774635639655162324363525648494339796e526f5578444246302f345356774b695a58474b4c315679365646425948744d75536f36480a4c5468524763694f696e3945437272357077664b5655764e614a5568582f742b68524b30594c2b51726750554d337a4243632f795449353259313969365977660a36664c393931657853612b394665646b6f5336546444356c7333702f707155302f2b777849485a444c6e6b6e635a515a6e6e4d4374574637597654452f70704e0a5641486878785577543439755474656174475276634f453142726e424c414f74637a754778644a7873345369317351727871746665456161676363754c5537390a6475665a4a565762337263434177454141614f42344443423354416442674e5648513445466751554e694d4b4f522b4a4943585273556358756e5976554b58730a564c30776761414741315564497753426d4443426c5941554e694d4b4f522b4a4943585273556358756e5976554b5873564c3268637152774d473478437a414a0a42674e5642415954416b6c754d52417744675944565151494577644864577068636d46304d52457744775944565151484577684c61474674596d6868644445530a4d4241474131554543684d4a525778706447566a62334a6c4d517777436759445651514c45774e44553030784744415742674e5642414d54443074316247526c0a5a58416755474675593268686249494a414c4c336c493455584573684d41774741315564457751464d414d4241663877437759445652305042415144416745470a4d41304743537147534962334451454242515541413449424151416b31596e584e76564575346556645232443033414f7655513465515a58754864336c75794d0a4c4c56666e53676166387274334f3375614b433838626f396e6e47594c4d4348494b494c437341693636374b6d66795759376934333257574538314f763330490a686e443235637a394954736b682f44367a654c70584534796965376c667844536e797272484d366c4f766e6165673165514754317271555a5241582b6c6569730a6b2b75706156786c39352f49524951516174642b32627762334f376e395442377261463243334d6b4c77394743596f787a7867436e3972615a4866486c75616b0a5937716c57334f59506646507357502f2f347454754f5a686d494f4465587545724336644c6443746c6b7a2b4374612f4b7669554c513978734a65575a3363770a5141354e4c554d77525a38346f6a2f6e7a393262435155424e49564b524b4c31533346654f79556d39797878513163460a2d2d2d2d2d454e442043455254494649434154452d2d2d2d2d0a";
		static final String CLIENT_TRUSTED_CERTIFICATE_DSA = "0x2d2d2d2d2d424547494e2043455254494649434154452d2d2d2d2d0a4d4949433544434341715367417749424167494a4149495352565537596336584d416b4742797147534d343442414d7767593878437a414a42674e56424159540a416b6c4f4d52417744675944565151494441644864577068636d46304d524977454159445651514844416c426147316c5a47466959575178456a415142674e560a42416f4d435556736158526c593239795a54454d4d416f47413155454377774451314e4e4d5134774441594456515144444156455a584e686154456f4d4359470a43537147534962334451454a4152595a62574673595859755a47567a59576c415a5778706447566a62334a6c4c6d4e7662544165467730784d7a41334d7a41770a4e6a51314e545261467730794d7a41334d6a67774e6a51314e5452614d4947504d517377435159445651514745774a4a546a45514d41344741315545434177480a5233567159584a68644445534d424147413155454277774a515768745a575268596d466b4d524977454159445651514b44416c4662476c305a574e76636d55780a4444414b42674e564241734d41304e545454454f4d41774741315545417777465247567a59576b784b44416d42676b71686b69473977304243514557475731680a624746324c6d526c63324670514756736158526c593239795a53356a62323077676641776761674742797147534d343442414577675a77435151434c336134760a456451484c64492f77786b737133332b69554835422b34536b516830666c69576a4d7537713270533850376c726b6b6f6865745543356b42413158566859444f0a4e3553325a70674c6f66707a3575736c416855412b7048506d716e687a66395a36576567334f5676634d674652464d43514866426a617a4f507966554179422b0a4344787751564173304a454a316e5234472f336a532f326a45464c3762475a37772f38754b49654d542f396c7164794e41716379617a5466445874624b6f4e6a0a6d575842666b73445177414351424d346d78325130624e666841427635794b687a77547364326f6259427068726b7741436e38666236466c63557634594a79780a7171645370794d4b5a54416a562f50447039654963537755712b50704c376966306a656a5544424f4d42304741315564446751574242526c42536734544174690a31646d344562346f736d686a67467272507a416642674e5648534d45474441576742526c425367345441746931646d344562346f736d686a67467272507a414d0a42674e5648524d45425441444151482f4d416b4742797147534d343442414d444c7741774c41495562586b7662735032493437344f77386b4d505a3359316e700a684a38434643756e5a3052684e383857323165414d4b715a4e4c5666584148580a2d2d2d2d2d454e442043455254494649434154452d2d2d2d2d0a";
		

		private ProtocolVersion minProtocolVersion;
		private ProtocolVersion maxProtocolVersion;
		private String serverCertificateHexString;
		private String serverPrivateKeyHexString;
		private List<String> trustedCertificateHexStrings;
		private String serverPrivateKeyPassword;
		private PrivateKeyAlgo privateKeyAlgo;
		private String userName;
		private String password;
		private boolean skipOnPrivateKeyError;
		private boolean skipOnCertificateChainError;
		
		private PrivateKey privateKey;
		private List<byte[]> serverCertificateChainBytes;
		private List<X509Certificate> x509TrustedCertificate;
		private AccountData accountData;
		private List<Integer> enabledCipherSuites;
		private int defaultNegotiationMethod;
		private List<Integer> enabledEapMethods;
		private boolean sendCertificateRequest;
		private boolean isFailureExpected;
		private Map<String,X509Certificate> subjectDnNameToTrustedCertificate;
		private String scenario = "UNDEFINED";
		private List<Integer> certificateTypes;
		private List<VendorSpecificCertificate> vendorSpecificCertificates;
		private String strStatus;
		
		public RadEapHandlerValuesBuilder() {
			enabledCipherSuites = new ArrayList<Integer>();
			enabledEapMethods = new ArrayList<Integer>();
			x509TrustedCertificate = new ArrayList<X509Certificate>();
			subjectDnNameToTrustedCertificate = new HashMap<String, X509Certificate>();
			trustedCertificateHexStrings = new ArrayList<String>();
			serverCertificateChainBytes = new ArrayList<byte[]>();
			certificateTypes = new ArrayList<Integer>();
			vendorSpecificCertificates = new ArrayList<EAPConfigurationData.VendorSpecificCertificate>();
		}
		
		private RadEapHandlerValuesBuilder withOUI(final String oui,final List<byte[]> chain, final PrivateKey pk) {
			VendorSpecificCertificate vendorSpecificCertificate = new VendorSpecificCertificate() {

				@Override
				public PrivateKey getVendorPrivateKey() {
					return pk;
				}

				@Override
				public List<byte[]> getServerCertificateChain() {
					return chain;
				}

				@Override
				public String getOui() {
					return oui;
				}
			};

			vendorSpecificCertificates.add(vendorSpecificCertificate);
			return this;
		}

		public RadEapHandlerValuesBuilder(RadEapHandlerValuesBuilder builder) {
			this();
			minProtocolVersion = builder.minProtocolVersion;
			maxProtocolVersion = builder.maxProtocolVersion;
			serverCertificateHexString = builder.serverCertificateHexString;
			serverPrivateKeyHexString = builder.serverPrivateKeyHexString;
			trustedCertificateHexStrings = new ArrayList<String>(builder.trustedCertificateHexStrings);
			serverPrivateKeyPassword = builder.serverPrivateKeyPassword;
			privateKeyAlgo = builder.privateKeyAlgo;
			userName = builder.userName;
			password = builder.password;
			privateKey = builder.privateKey;
			serverCertificateChainBytes = new ArrayList<byte[]>(builder.serverCertificateChainBytes);
			x509TrustedCertificate = new ArrayList<X509Certificate>(builder.x509TrustedCertificate);
			accountData = builder.accountData;
			enabledCipherSuites = new ArrayList<Integer>();
			defaultNegotiationMethod = builder.defaultNegotiationMethod;
			enabledEapMethods = new ArrayList<Integer>();
			sendCertificateRequest = builder.sendCertificateRequest;
			isFailureExpected = builder.isFailureExpected;
			certificateTypes = new ArrayList<Integer>(builder.certificateTypes);
			vendorSpecificCertificates = new ArrayList<EAPConfigurationData.VendorSpecificCertificate>(builder.vendorSpecificCertificates);
		}
		
		
		public RadEapHandlerValuesBuilder getDefaultRSABuilder() throws Exception{
			new RadEapHandlerValuesBuilder();
			withDefaultRSAHandlerValues();
			return this;
		}
		
		public RadEapHandlerValuesBuilder getDefaultDSABuilder() throws Exception{
			new RadEapHandlerValuesBuilder();
			withDefaultDSAHandlerValues();
			return this;
		}
		
		private void withDefaultDSAHandlerValues() {
			withDefaultHandlerValues();
			withUser("eliteaaa1", "eliteaaa1");
			withCertificate(SERVER_CERTIFICATE_CHAIN_DSA_HEXSTR);
			withPrivateKey(PRIVATE_KEY_PASSWORD, PrivateKeyAlgo.DSA, PRIVATE_KEY_DSA_BYTES_HEXSTR);
			withCertificateType(CertificateTypeConstants.DSS.getID());
		}

		private void withDefaultHandlerValues() {
			withProtocolVersion(ProtocolVersion.TLS1_0.getMinor(),ProtocolVersion.TLS1_2.getMinor());
			withCipherSuite(CipherSuites.TLS_RSA_WITH_AES_128_CBC_SHA.code);
			withDefaultNegotiationMethod(EapTypeConstants.MD5_CHALLENGE.typeId);
			withEapMethod(EapTypeConstants.TTLS.typeId);
			withEapMethod(EapTypeConstants.MD5_CHALLENGE.typeId);
			withEapMethod(EapTypeConstants.PEAP.typeId);
			withTrustedCertificate(CLIENT_TRUSTED_CERTIFICATE_RSA);
			withTrustedCertificate(CLIENT_TRUSTED_CERTIFICATE_DSA);
		}


		private void withDefaultRSAHandlerValues() throws Exception {
			withDefaultHandlerValues();
			withUser(USERNAME_PASSWORD, USERNAME_PASSWORD);
			withCertificate(SERVER_CERTIFICATE_CHAIN_RSA_HEXSTR);
			withPrivateKey(PRIVATE_KEY_PASSWORD, PrivateKeyAlgo.RSA, PRIVATE_KEY_RSA_BYTES_HEXSTR);
			withCertificateType(CertificateTypeConstants.RSA.getID());
		}

		private void buildAccountData() {
			accountData = new AccountData();
			accountData.setUserIdentity(userName);
			accountData.setUserName(userName);
			accountData.setPassword(password);
			accountData.setAccountStatus(strStatus);
		}


		private void buildTrustedCertificates() throws Exception {
			for(String trustedCertificateHexString : trustedCertificateHexStrings){
				X509Certificate certificate = ((List<X509Certificate>)generateCerficate(trustedCertificateHexString)).get(0);
				x509TrustedCertificate.add(certificate);
				subjectDnNameToTrustedCertificate.put(certificate.getSubjectDN().getName(),certificate);
			}
		}


		private void buildServerPrivateKey() throws Exception {
			privateKey = getPrivateKey(serverPrivateKeyPassword,privateKeyAlgo,serverPrivateKeyHexString,skipOnPrivateKeyError);
		}


		private void buildServerCertificateChain() throws Exception {
			serverCertificateChainBytes = buildChain(serverCertificateHexString,skipOnCertificateChainError);
		}
		
		private List<byte[]> buildChain(String certificateStringInHex, boolean skipOnError) throws Exception{
			List<byte[]> chainBytes = new ArrayList<byte[]>();
			try {
				Collection<X509Certificate> serverCertificate = generateCerficate(certificateStringInHex);
				if(serverCertificate.size() > 1){
					chainBytes = certificateChainToBytes(serverCertificate);
				}else{
					chainBytes = new ArrayList<byte[]>();
					chainBytes.addAll(lookUpChain(((List<X509Certificate>)serverCertificate).get(0)));
				}
				return chainBytes;	
			} catch (Exception e) {
				if(!skipOnError)
					throw e;
			}
			
			return chainBytes;
		}
		
		@SuppressWarnings("unchecked")
		private List<byte[]> certificateChainToBytes(Collection<? extends Certificate> certificateList) throws CertificateEncodingException {
			List<byte[]> certificateChainList = new ArrayList<byte[]>();
			for(int index = 0 ; index < certificateList.size() ; index ++){
					certificateChainList.add(((List<X509Certificate>)certificateList).get(index).getEncoded());
			}
			return certificateChainList;
		}

		public RadEapHandlerValuesBuilder withCertificateType(int certificateType){
			this.certificateTypes.add(certificateType);
			return this;
		}
		
		public RadEapHandlerValuesBuilder withProtocolVersion(int minProtocolVersion, int maxProtocolVersion) {
			this.minProtocolVersion = ProtocolVersion.getProtocolVersion(3, minProtocolVersion);
			this.maxProtocolVersion = ProtocolVersion.getProtocolVersion(3, maxProtocolVersion);
			return this;
		}
		
		public RadEapHandlerValuesBuilder withCertificate(String certificateHexString){
			this.serverCertificateHexString = certificateHexString;
			return this;
		}
		
		public RadEapHandlerValuesBuilder skipOnCertificateFormationError(){
			this.skipOnCertificateChainError = true;
			return this;
		}

		public RadEapHandlerValuesBuilder withPrivateKey(String privateKeyPassword,PrivateKeyAlgo privateKeyAlgo,  String serverPrivateKeyHexString){
			this.serverPrivateKeyHexString = serverPrivateKeyHexString;
			this.privateKeyAlgo = privateKeyAlgo;
			this.serverPrivateKeyPassword = privateKeyPassword;
			return this;
		}
		
		public RadEapHandlerValuesBuilder skipOnPrivateKeyFormationError(){
			this.skipOnPrivateKeyError = true;
			return this;
		}

		public RadEapHandlerValuesBuilder withTrustedCertificate(String trustedCertificateHexString){
			this.trustedCertificateHexStrings.add(trustedCertificateHexString);
			return this;
		}		
		
		public RadEapHandlerValuesBuilder withUser(String userName, String password){
			this.userName = userName;
			this.password = password;
			return this;
		}
		
		public RadEapHandlerValuesBuilder withInactiveAccount() {
			this.strStatus = "I";
			return this;
		}
		
		public RadEapHandlerValuesBuilder withCipherSuite(int cipherSuiteId){
			this.enabledCipherSuites.add(cipherSuiteId);
			return this;
		}
		
		public RadEapHandlerValuesBuilder withDefaultNegotiationMethod(int eapTypeConstant){
			this.defaultNegotiationMethod = eapTypeConstant;
			return this;
		}
		
		public RadEapHandlerValuesBuilder withEapMethod(int eapTypeConstant){
			this.enabledEapMethods.add(eapTypeConstant);
			return this;
		}
		
		public RadEapHandlerValuesBuilder withScenario(String scenario) {
			this.scenario = scenario;
			return this;
		}
		
		public RadEapHandlerValuesBuilder sendCertificateRequest(){
			this.sendCertificateRequest = true;
			return this;
		}
		
		public RadEapHandlerValuesBuilder failureExpected(){
			this.isFailureExpected = true;
			return this;
		}
		
		public OUIBuilder withOUI(String oui){
			return new OUIBuilder(oui);
		}
		
		public RadEapHandlerValues build() throws Exception{
			buildTrustedCertificates();
			buildServerCertificateChain();
			buildServerPrivateKey();
			buildAccountData();
			
			return new RadEapHandlerValues(
					scenario,
					minProtocolVersion,
					maxProtocolVersion,
					serverCertificateChainBytes,
					privateKey,
					x509TrustedCertificate,
					accountData,
					enabledCipherSuites,
					enabledEapMethods,
					sendCertificateRequest,
					defaultNegotiationMethod,
					isFailureExpected,
					certificateTypes,
					vendorSpecificCertificates);
		}

		@SuppressWarnings("unchecked")
		private static Collection<X509Certificate> generateCerficate(String certificateBytesHex) throws Exception{
			byte[] certificateBytes = TLSUtility.HexToBytes(certificateBytesHex);
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			ByteArrayInputStream inputStream = new ByteArrayInputStream(certificateBytes);
			return (Collection<X509Certificate>) certificateFactory.generateCertificates(inputStream);
		}
		
		private static PrivateKey getPrivateKey(String privateKeyPassword, PrivateKeyAlgo algo, String privateKeyHexStr, boolean skipOnError) throws Exception {
			PrivateKey priKey = null;
			try {
				byte[] privateKeyBytes = TLSUtility.HexToBytes(privateKeyHexStr);
				PKCS8EncodedKeySpec keysp = null;
				if(privateKeyPassword != null && !"".equals(privateKeyPassword)){
					javax.crypto.EncryptedPrivateKeyInfo encPriKey = new javax.crypto.EncryptedPrivateKeyInfo(privateKeyBytes);
					Cipher cipher = Cipher.getInstance(encPriKey.getAlgName());
					PBEKeySpec pbeKeySpec = new PBEKeySpec(privateKeyPassword.toCharArray());
					SecretKeyFactory secKeyFactory = SecretKeyFactory.getInstance(encPriKey.getAlgName());
					cipher.init(Cipher.DECRYPT_MODE, secKeyFactory.generateSecret(pbeKeySpec), encPriKey.getAlgParameters());
					keysp = encPriKey.getKeySpec(cipher);
					
				}else{
					keysp = new PKCS8EncodedKeySpec(privateKeyBytes);
				}
				
				KeyFactory kf = KeyFactory.getInstance(algo.name);
				priKey = kf.generatePrivate (keysp);
				
			} catch (Exception e) {
				if(!skipOnError)
					throw e;
			}
			
			return priKey;
		}
		
		private List<byte[]> lookUpChain(X509Certificate serverCertificate) throws CertificateEncodingException {
			String issuer = serverCertificate.getIssuerDN().getName();
			List<byte[]> certificateChainList = new ArrayList<byte[]>();
			
			certificateChainList.add(serverCertificate.getEncoded());
			
			X509Certificate issuerCertificate = null;
			do {
				issuerCertificate = subjectDnNameToTrustedCertificate.get(issuer);
				if(issuerCertificate == null) {
					break;
				}

				certificateChainList.add(issuerCertificate.getEncoded());
				issuer = issuerCertificate.getIssuerDN().getName();

			} while(!isRootCertificate(issuerCertificate));
				
			return certificateChainList;
		}
		
		private boolean isRootCertificate(X509Certificate certificate) {
			return certificate.getSubjectDN().getName().equals(certificate.getIssuerDN().getName());
		}
	}
	
}
