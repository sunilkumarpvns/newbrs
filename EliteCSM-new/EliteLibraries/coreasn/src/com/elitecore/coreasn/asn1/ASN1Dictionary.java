package com.elitecore.coreasn.asn1;

import java.util.HashMap;

import com.elitecore.coreasn.asn1.attributes.ASN1Counter32Attribute;
import com.elitecore.coreasn.asn1.attributes.ASN1ErrorAttribute;
import com.elitecore.coreasn.asn1.attributes.ASN1IPAddressAttribute;
import com.elitecore.coreasn.asn1.attributes.ASN1Integer32Attribute;
import com.elitecore.coreasn.asn1.attributes.ASN1NullAttribute;
import com.elitecore.coreasn.asn1.attributes.ASN1OctetStringAttribute;
import com.elitecore.coreasn.asn1.attributes.ASN1TimeAttribute;
import com.elitecore.coreasn.asn1.attributes.IASN1Attribute;
import com.elitecore.coreasn.asn1.attributes.OID;

public class ASN1Dictionary {

	private static ASN1Dictionary dictionaryInstance;
	private HashMap <Integer, IASN1Attribute> tagAttributeMap;
	
	private ASN1Dictionary() {
		tagAttributeMap = new HashMap<Integer, IASN1Attribute>();
		tagAttributeMap.put(new Integer(BaseASN1TLV.ASN_INTEGER), new ASN1Integer32Attribute(0));
		tagAttributeMap.put(new Integer(BaseASN1TLV.ASN_OCTET_STRING), new ASN1OctetStringAttribute());
		tagAttributeMap.put(new Integer(BaseASN1TLV.ASN_NULL), new ASN1NullAttribute());
		tagAttributeMap.put(new Integer(BaseASN1TLV.ASN_OBJECT_IDENTIFIER), new OID());
		tagAttributeMap.put(new Integer(BaseASN1TLV.SNMP_COUNTER32), new ASN1Counter32Attribute());
		tagAttributeMap.put(new Integer(BaseASN1TLV.SNMP_TIMETICKS), new ASN1TimeAttribute());
		tagAttributeMap.put(new Integer(BaseASN1TLV.SNMP_IPADDRESS), new ASN1IPAddressAttribute());
		tagAttributeMap.put(new Integer(BaseASN1TLV.SNMP_END_OF_MIBVIEW), new ASN1ErrorAttribute(BaseASN1TLV.SNMP_END_OF_MIBVIEW));
		tagAttributeMap.put(new Integer(BaseASN1TLV.SNMP_NO_SUCH_OBJECT), new ASN1ErrorAttribute(BaseASN1TLV.SNMP_NO_SUCH_OBJECT));
		tagAttributeMap.put(new Integer(BaseASN1TLV.SNMP_NO_SUCH_INSTANCE), new ASN1ErrorAttribute(BaseASN1TLV.SNMP_NO_SUCH_INSTANCE));
	}

	public static final ASN1Dictionary getInstance() {
		if (dictionaryInstance == null) {
			synchronized (ASN1Dictionary.class) {
				if (dictionaryInstance == null)
					dictionaryInstance = new ASN1Dictionary();
			}
		}
		return dictionaryInstance;
	}

	public IASN1Attribute getAttribute(int tag){
		return tagAttributeMap.get( new Integer(tag));
	}
}
