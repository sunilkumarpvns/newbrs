package com.elitecore.corenetvertex.util.commons.jaxb.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

/**
 * Created by Ishani on 14/9/16.
 */
public class DoubleToStringAdapter extends XmlAdapter<String, Double> {

	private static final String MODULE = "DOUBLE-TO-STR-ADP";

	@Override
    public Double unmarshal(String str) throws Exception {
        if (str == null || str.trim().isEmpty() == true) {
            return null;
        }
        try {
            return  Double.parseDouble(str.trim());
        } catch (Exception ex) { 
        	getLogger().warn(MODULE, "Invalid Double value " + str);
        	ignoreTrace(ex);
            return Double.MIN_VALUE;
        }
    }

    @Override
    public String marshal(Double d) throws Exception {
        if (d == null) {
            return "";
        }
        return  BigDecimal.valueOf(d).setScale(6, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString();
    }
}