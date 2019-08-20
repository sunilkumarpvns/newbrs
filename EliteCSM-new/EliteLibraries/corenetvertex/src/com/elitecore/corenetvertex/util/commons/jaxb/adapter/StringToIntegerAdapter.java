package com.elitecore.corenetvertex.util.commons.jaxb.adapter;

import com.elitecore.commons.base.Strings;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

/**
 * it will convert Integer value to string and vice-a-versa
 */
public class StringToIntegerAdapter extends XmlAdapter<String, Integer> {

        private static final String MODULE = "STR-INTEGER-ADP";

        @Override
        public Integer unmarshal(String v) throws Exception {

            if (Strings.isNullOrBlank(v)) {
                return null;
            }

            try{
                return Integer.parseInt(v.trim());

            }catch(Exception ex){ 
                getLogger().warn(MODULE, "Invalid Integer value '" + v);
                ignoreTrace(ex);
                return null;
            }
        }

        @Override
        public String marshal(Integer v) throws Exception {
            if (v == null) {
                return "";
            } else {
                return v.toString();
            }
        }

}
