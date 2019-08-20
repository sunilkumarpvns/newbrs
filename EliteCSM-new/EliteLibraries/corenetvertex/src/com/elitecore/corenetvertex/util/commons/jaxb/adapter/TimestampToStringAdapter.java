package com.elitecore.corenetvertex.util.commons.jaxb.adapter;

import com.elitecore.commons.base.Strings;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.sql.Timestamp;

/**
 * Created by Ishani on 6/12/16.
 */
public class TimestampToStringAdapter extends XmlAdapter<String, Timestamp> {


    @Override
    public String marshal(Timestamp timestamp) throws Exception {
        if(timestamp == null){
            return null;
        }
        Long time = timestamp.getTime();
        return time.toString();
    }

    @Override
    public Timestamp unmarshal(String timeStampStr) throws Exception {
        if(Strings.isNullOrBlank(timeStampStr)){
            return null;
        }
        return new Timestamp(Long.parseLong(timeStampStr));
    }
}
