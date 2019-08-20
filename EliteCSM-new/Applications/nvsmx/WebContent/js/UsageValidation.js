/**
 * Created by Ishani on 21/12/15.
 */
function isValidQos(fieldId,value,unit){
    if(isNullOrEmpty(value) == true){
        return true;
    }
    var isValidQos=true;
    var maxQosValueInBps = 4294967295;
    var maxQosValueInKbps=4194303;
    var maxQosValueInMbps=4095;
    var maxQosValueInGbps=3;

    if(unit == 'Gbps'){
        isValidQos = (value > maxQosValueInGbps)?false:true;
        if(isValidQos==false){
            setError(fieldId,"Max Value in Gbps is "+maxQosValueInGbps);
        }
    }else if(unit == 'Mbps'){
        isValidQos = (value> maxQosValueInMbps)?false:true;
        if(isValidQos==false){
            setError(fieldId,"Max Value in Mbps is "+maxQosValueInMbps);
        }
    }else if(unit == 'Kbps'){
        isValidQos = (value  > maxQosValueInKbps)?false:true;
        if(isValidQos ==false){
            setError(fieldId,"Max Value in Kbps is "+maxQosValueInKbps);
        }
    }else if(unit == 'Bps'){
        isValidQos = (value  > maxQosValueInBps)?false:true;
        if(isValidQos ==false){
            setError(fieldId,"Max Value in bps is "+maxQosValueInBps);
        }
    }else{
        isValidQos=false;
    }
    return isValidQos;
}

function isValidQuota(fieldId,value,unit){
    if(isNullOrEmpty(value) == true){
        return true;
    }

    var isValidQuota=true;
    var maxQuotaValueInBytes = 1099511627776;//1024*1024*1024*1024
    var maxQuotaValueInKb=1073741824;//1024*1024*1024
    var maxQuotaValueInMb=1048576;//1024*1024
    var maxQuotaValueInGb=1024;
    if(unit == 'GB'){
        isValidQuota = (value > maxQuotaValueInGb)?false:true;
        if(isValidQuota==false){
            setError(fieldId,"Max Value in GB is "+maxQuotaValueInGb);
        }
    }else if(unit == 'MB'){
        isValidQuota = (value> maxQuotaValueInMb)?false:true;
        if(isValidQuota==false){
            setError(fieldId,"Max Value in MB is "+maxQuotaValueInMb);
        }
    }else if(unit == 'KB'){
        isValidQuota = (value  > maxQuotaValueInKb)?false:true;
        if(isValidQuota ==false){
            setError(fieldId,"Max Value in KB is "+maxQuotaValueInKb);
        }
    }else if(unit == 'BYTE'){
        isValidQuota = (value  > maxQuotaValueInBytes)?false:true;
        if(isValidQuota ==false){
            setError(fieldId,"Max Value in BYTE is "+maxQuotaValueInBytes);
        }
    }else{
        isValidQuota=false;
    }
    return isValidQuota;
}

function isValidSliceTime(fieldId,value,unit){
    if(isNullOrEmpty(value) == true){
        return true;
    }

    var isValidSliceTime=true;
    var maxSliceTimeValueInSecond = 86400;
    var maxSliceTimeValueInMinute=1440;
    var maxSliceTimeValueInHour=24;
    if(unit == 'HOUR'){
        isValidSliceTime = (value > maxSliceTimeValueInHour)?false:true;
        if(isValidSliceTime==false){
            setError(fieldId,"Max allowed value in HOUR is "+maxSliceTimeValueInHour);
        }
    }else if(unit == 'MINUTE'){
        isValidSliceTime = (value> maxSliceTimeValueInMinute)?false:true;
        if(isValidSliceTime==false){
            setError(fieldId,"Max allowed value in MINUTE is "+maxSliceTimeValueInMinute);
        }
    }else if(unit == 'SECOND'){
        isValidSliceTime = (value  > maxSliceTimeValueInSecond)?false:true;
        if(isValidSliceTime ==false){
            setError(fieldId,"Max allowed value in SECOND is "+maxSliceTimeValueInSecond);
        }
    }else{
        isValidSliceTime=false;
    }
    return isValidSliceTime;
}
