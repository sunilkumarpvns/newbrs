package com.elitecore.core.serverx.snmp.mib.os.extended;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.nio.ByteBuffer;
import java.sql.Types;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.snmp.mib.os.autogen.EnumLaErrorFlag;
import com.elitecore.core.serverx.snmp.mib.os.autogen.LaEntry;
import com.elitecore.core.serverx.snmp.mib.os.data.SystemDetailProvider.LoadAveragetDetail;
import com.sun.management.snmp.BerDecoder;
import com.sun.management.snmp.BerException;
import com.sun.management.snmp.SnmpMsg;

public class LaEntryMBeanImpl extends LaEntry {

	private static final String MODULE = "LA-ENTRY-IMPL";
	private static final long serialVersionUID = 1L;
	transient private final LoadAveragetDetail loadAverageProvider;

	public LaEntryMBeanImpl(LoadAveragetDetail loadAveragetDetail) {
		this.loadAverageProvider = loadAveragetDetail;
	}
	
	@Override
	public Byte[] getLaLoadFloat(){
		return loadAverageProvider.getLaLoadFloat();
	}
	
	/**
	 * Extension Method for getLaLoadFloat.
	 * <p>
	 * Float ::= TEXTUAL-CONVENTION
    	STATUS      current
    	DESCRIPTION
        "A single precision floating-point number.  The semantics
         and encoding are identical for type 'single' defined in
         IEEE Standard for Binary Floating-Point,
         ANSI/IEEE Std 754-1985.
         The value is restricted to the BER serialization of
         the following ASN.1 type:
             FLOATTYPE ::= [120] IMPLICIT FloatType
         (note: the value 120 is the sum of '30'h and '48'h)
         The BER serialization of the length for values of
         this type must use the definite length, short
         encoding form.

         For example, the BER serialization of value 123
         of type FLOATTYPE is '9f780442f60000'h.  (The tag
         is '9f78'h; the length is '04'h; and the value is
         '42f60000'h.) The BER serialization of value
         '9f780442f60000'h of data type Opaque is
         '44079f780442f60000'h. (The tag is '44'h; the length
         is '07'h; and the value is '9f780442f60000'h."
    	SYNTAX Opaque (SIZE (7))
	 * 
	 * So, that needs to Decode the value of BER using BERDecoder as 
	 * snmp it self use this utility internally to get value of 
	 * Opaque type.
	 * 
	 * {@link BerDecoder -> fetchOctetString()}
	 * example {@link SnmpMsg -> decodeVarBindValue()} 
	 */
	@Column(name = "laLoadFloat", type = Types.FLOAT)
	public Float getLaLoadFloatExt() {
		byte[] primitive = ArrayUtils.toPrimitive(loadAverageProvider.getLaLoadFloat());
		byte[] destinationByteArray = new byte[primitive.length-1];
		System.arraycopy(primitive, 1, destinationByteArray, 0, destinationByteArray.length);
		float floatValue = 0.0F;
		BerDecoder decoder = new BerDecoder(destinationByteArray);
		try {
			int tag = decoder.getTag();
			byte[] fetchOctetString = decoder.fetchOctetString(tag);
			ByteBuffer wrap = ByteBuffer.wrap(fetchOctetString);
			floatValue = wrap.getFloat();
			return floatValue;
		} catch (BerException e) { 
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Exception while decoding BER Value for field LA Float. Reason: "+e.getMessage());
			}
			ignoreTrace(e);
		}
		return floatValue;
	}
	
	@Override
	@Column(name = "laLoadInt", type = Types.INTEGER)
	public Integer getLaLoadInt(){
		return loadAverageProvider.getLaLoadInt();
	}

	@Override
	@Column(name = "laConfig", type = Types.VARCHAR)
	public String getLaConfig(){
		return loadAverageProvider.getLaConfig();
	}

	@Override
	public void setLaConfig(String x){
		loadAverageProvider.setLaConfig(x);
	}

	@Override
	public void checkLaConfig(String x){
		
	}

	@Override
	@Column(name = "laLoad", type = Types.VARCHAR)
	public String getLaLoad(){
		return loadAverageProvider.getLaLoad();
	}

	@Override
	@Column(name = "laErrMessage", type = Types.VARCHAR)
	public String getLaErrMessage(){
		return loadAverageProvider.getLaErrMessage();
	}

	@Override
	@Column(name = "laNames", type = Types.VARCHAR)
	public String getLaNames(){
		return loadAverageProvider.getLaNames();
	}

	@Override
	@Column(name = "laIndex", type = Types.INTEGER)
	public Integer getLaIndex(){
		return loadAverageProvider.getLaIndex();
	}

	@Override
	@Column(name = "laErrorFlag", type = Types.VARCHAR)
	public EnumLaErrorFlag getLaErrorFlag(){
		return loadAverageProvider.getLaErrorFlag();
	}
}