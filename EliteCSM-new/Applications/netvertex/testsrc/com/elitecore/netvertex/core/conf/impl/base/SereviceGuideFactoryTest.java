package com.elitecore.netvertex.core.conf.impl.base;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.sm.gateway.ServiceGuidingData;
import com.elitecore.netvertex.core.conf.impl.ServiceGuideFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SereviceGuideFactoryTest {

    private ServiceGuideFactory serviceGuideFactory = new ServiceGuideFactory();

    private ServiceData createServiceData(String id, String status){
        ServiceData serviceData = new ServiceData();
        serviceData.setId(id);
        serviceData.setStatus(status);
        return serviceData;
    }

    @Test
    public void testCreateAllowNullAdvancedCondition(){
        List<ServiceGuidingData> serviceGuidingDataList = new ArrayList<>();
        ServiceGuidingData serviceGuidingData = new ServiceGuidingData();
        serviceGuidingData.setId("voiceGuide");
        serviceGuidingData.setServiceData(createServiceData("VOICE", PkgStatus.ACTIVE.name()));
        serviceGuidingDataList.add(serviceGuidingData);

        Assert.assertEquals(1,serviceGuideFactory.create(serviceGuidingDataList).size());
    }

    @Test
    public void testCreateDoesNowAllowInvalidAdvancedCondition(){
        List<ServiceGuidingData> serviceGuidingDataList = new ArrayList<>();
        ServiceGuidingData serviceGuidingData = new ServiceGuidingData();
        serviceGuidingData.setId("voiceGuide");
        serviceGuidingData.setServiceData(createServiceData("VOICE", PkgStatus.ACTIVE.name()));
        serviceGuidingData.setCondition("FOO");
        serviceGuidingDataList.add(serviceGuidingData);

        Assert.assertEquals(0,serviceGuideFactory.create(serviceGuidingDataList).size());
    }

    @Test
    public void testCreateDoesNowAllowValidAdvancedCondition(){
        List<ServiceGuidingData> serviceGuidingDataList = new ArrayList<>();
        ServiceGuidingData serviceGuidingData = new ServiceGuidingData();
        serviceGuidingData.setId("voiceGuide");
        serviceGuidingData.setServiceData(createServiceData("VOICE", PkgStatus.ACTIVE.name()));
        serviceGuidingData.setCondition("\"1\"=\"1\"");
        serviceGuidingDataList.add(serviceGuidingData);

        Assert.assertEquals(1,serviceGuideFactory.create(serviceGuidingDataList).size());
    }
    @Test
    public void testCreateAllowDisabledServicesAtLast(){
        List<ServiceGuidingData> serviceGuidingDataList = new ArrayList<>();
        ServiceGuidingData serviceGuidingData = new ServiceGuidingData();
        serviceGuidingData.setId("voiceGuide");
        serviceGuidingData.setServiceData(createServiceData("VOICE", PkgStatus.INACTIVE.name()));
        serviceGuidingData.setCondition("\"1\"=\"1\"");
        serviceGuidingDataList.add(serviceGuidingData);

        Assert.assertEquals(1,serviceGuideFactory.create(serviceGuidingDataList).size());
    }

    @Test
    public void testCreatePutDisabledServicesAtLast(){
        List<ServiceGuidingData> serviceGuidingDataList = new ArrayList<>();
        ServiceGuidingData serviceGuidingData = new ServiceGuidingData();
        serviceGuidingData.setId("dataGuide");
        serviceGuidingData.setServiceData(createServiceData("DATA", PkgStatus.INACTIVE.name()));
        serviceGuidingData.setCondition("\"1\"=\"1\"");
        serviceGuidingDataList.add(serviceGuidingData);

        serviceGuidingData = new ServiceGuidingData();
        serviceGuidingData.setId("voiceGuide");
        serviceGuidingData.setServiceData(createServiceData("VOICE", PkgStatus.ACTIVE.name()));
        serviceGuidingData.setCondition("\"1\"=\"1\"");
        serviceGuidingDataList.add(serviceGuidingData);

        Assert.assertEquals(2,serviceGuideFactory.create(serviceGuidingDataList).size());
        Assert.assertEquals("DATA",serviceGuideFactory.create(serviceGuidingDataList).get(1).getServiceId());
    }
}
