package com.elitecore.nvsmx.policydesigner.controller.ratinggroup;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.google.common.collect.Iterators;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.StrutsJUnit4TestCase;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class will test the behaviour of Create, Update, Delete and view operation performed
 * by RatingGroupCTRL
 * @author ishani.bhatt
 */
@Ignore
public class RatingGroupCTRLCRUDOperationTest extends StrutsJUnit4TestCase<RatingGroupCTRL> {

    public static final String GROUP_IDS = "groupIds";
    private Session session = null;


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void before() throws InitializationFailedException {
        Configuration cfg = new Configuration();
        cfg.configure("/hibernate/test-hibernate.cfg.xml");
        HibernateConfigurationUtil.setConfigurationClasses(cfg);
        HibernateSessionFactory.buildSessionFactory(cfg);


    }

    @Before
    public void createSession() throws InitializationFailedException {
        session = HibernateSessionFactory.getSession();
        session.beginTransaction();
    }

    //testcase for create rating group
    @Test
    public void initCreateRedirectsToCreateAction() throws Exception {
        setStaffBelongingGroup();
        request.setAttribute("allServices", generateServices());
        ActionProxy proxy = createProxyOf("/initCreate.action");
        String result = proxy.execute();
        Assert.assertEquals(Results.CREATE.getValue(), result);
    }

    private  List<DataServiceTypeData> generateServices() {
        List<DataServiceTypeData> services = Collectionz.newArrayList();
        for(int i=1;i<5;i++){
            services.add(generateService("Service"+i,"Service_"+i,i+1L));
        }
        return services;
    }


    @Test
    public void initCreateThrowsNullPointerExceptionWhenStaffBelongingGroupsNotProvided() throws Exception {
        expectedException.expect(NullPointerException.class);
        ActionProxy proxy = createProxyOf("/initCreate.action");
        proxy.execute();
    }

    @Test
    public void createRedirectsToRedirectActionWithActionMessage_RatingGroupCreatedSuccessfully_WhenGroupIdNotProvided() throws Exception {
        setRatingGroupData("1", "RatingGroup1", 1L);
        String groupIds = null;
        request.setParameter(GROUP_IDS, groupIds);
        request.getSession().setAttribute("staffData", getStaffData());
        ActionProxy proxy = createProxyOf("/create.action");
        String result = proxy.execute();
        String expectedMessage = "Rating Group Created Successfully";
        String actualMessage = getActionMessage(proxy);
        Assert.assertEquals(expectedMessage,actualMessage);
        Assert.assertEquals(Results.REDIRECT_ACTION.getValue(), result);
    }

    @Test
    public void createRedirectsToRedirectActionWithActionMessage_RatingGroupCreatedSuccessfully_WhenGroupIdProvidedAndServicesNotConfigured() throws Exception {
        request.setParameter(GROUP_IDS, "1");
        setRatingGroupData("1", "RatingGroup1", 1L);
        request.getSession().setAttribute("staffData", getStaffData());
        ActionProxy proxy = createProxyOf("/create.action");
        String result = proxy.execute();
        String expectedMessage = "Rating Group Created Successfully";
        String actualMessage = getActionMessage(proxy);
        Assert.assertEquals(expectedMessage,actualMessage);
        Assert.assertEquals(Results.REDIRECT_ACTION.getValue(), result);
    }


    @Test
    public void createRedirectsToRedirectActionWithActionMessage_RatingGroupCreatedSuccessfully_WhenRatingGroupWithServiceInfoProvided() throws Exception {
        request.setParameter(GROUP_IDS, "1");
        request.setParameter("selectedServiceTypes",getSelectedServicesForRatingGroup());
        setRatingGroupData("1", "RatingGroup1", 1L);
        request.getSession().setAttribute("staffData", getStaffData());

        ActionProxy proxy = createProxyOf("/create.action");
        String result = proxy.execute();

        String expectedMessage = "Rating Group Created Successfully";
        String actualMessage = getActionMessage(proxy);
        Assert.assertEquals(expectedMessage,actualMessage);
        Assert.assertEquals(Results.REDIRECT_ACTION.getValue(), result);
    }

    @Test public void createRatingGroupRedirectToListWhenSessionIsClosedWithinOperation() throws Exception {
        request.setParameter(GROUP_IDS, "1");
        String selectedServices = getSelectedServicesForRatingGroup();
        request.setParameter("selectedServiceTypes",selectedServices);
        setRatingGroupData("1", "RatingGroup1", 1L);
        request.getSession().setAttribute("staffData", getStaffData());

        session.close();
        ActionProxy proxy = createProxyOf("/create.action");
        String result = proxy.execute();

        Assert.assertEquals("Unknown Internal Error",getActionError(proxy,1));
        Assert.assertEquals(Results.LIST.getValue(),result);


    }

    //test case for initUpdate and update

    @Test
    public void initUpdateRedirectsToListWhenIdNotProvidedInReqParamAndSession() throws Exception {
        String id = null;
        request.setParameter("ratingGroupId", id);

        ActionProxy proxy = createProxyOf("/initUpdate.action");
        String result = proxy.execute();

        Assert.assertEquals(Results.LIST.getValue(), result);

    }

    @Test
    public void initUpdateRedirectsToSearchActionMessage_UNKNOWN_INTERNAL_ERROR_WhenStaffBelongingGroupsNotProvided() throws Exception {
        request.setParameter("ratingGroupId", "1");
        generateRatingGroup("1","RatingGroupTest",1L,"1", Collections.EMPTY_LIST);

        ActionProxy proxy = createProxyOf("/initUpdate.action");
        String result = proxy.execute();

        String expectedMessage = "Unknown Internal Error";
        String actualMessage = getActionError(proxy,1);
        Assert.assertEquals(expectedMessage,actualMessage);
        Assert.assertEquals(Results.LIST.getValue(), result);

    }

    @Test
    public void initUpdateRedirectsToUpdateActionForRatingGroupUpdate() throws Exception {
        request.setParameter("ratingGroupId", "1");
        setStaffBelongingGroup();
        generateRatingGroup("1", "RatingGroupTest", 1L, "1",Collections.EMPTY_LIST);
        request.setParameter("selectedServiceTypes",getSelectedServicesForRatingGroup());
        ActionProxy proxy = createProxyOf("/initUpdate.action");
        String result = proxy.execute();
        Assert.assertEquals(Results.UPDATE.getValue(), result);

    }

    @Test
    public void updateRedirectsToRedirectActionWithActionMessage_RatingGroupUpdatedSuccessfully_WhenGroupIdNotProvided() throws Exception {
        String groupId = null;
        request.setParameter(GROUP_IDS, groupId);
        generateRatingGroup("1", "RatingGroup1", 1L, "1",Collections.EMPTY_LIST);
        setRatingGroupData("1", "RatingGroup121", 5L);
        request.getSession().setAttribute("staffData", getStaffData());
        ActionProxy proxy = createProxyOf("/update.action");
        String result = proxy.execute();
        String expectedMessage = "Rating Group Updated Successfully";
        String actualMessage = getActionMessage(proxy);
        Assert.assertEquals(expectedMessage,actualMessage);
        Assert.assertEquals(Results.REDIRECT_ACTION, result);
    }

    @Test
    public void updateRedirectsToRedirectActionWithMessage_RatingGroupUpdatedSuccessfully_WhenGroupIdProvidedAndServiceNotConfigured() throws Exception {
        request.setParameter(GROUP_IDS, "1");
        generateRatingGroup("1","RatingGroup1",1L,"1",Collections.EMPTY_LIST);
        setRatingGroupData("1", "RatingGroup121", 5L);
        request.getSession().setAttribute("staffData", getStaffData());
        ActionProxy proxy = createProxyOf("/update.action");
        String result = proxy.execute();
        String expectedMessage = "Rating Group Updated Successfully";
        String actualMessage = getActionMessage(proxy);
        Assert.assertEquals(expectedMessage,actualMessage);
        Assert.assertEquals(Results.REDIRECT_ACTION, result);
    }

    @Test
    public void updateRedirectsToRedirectActionWithActionMessage_RatingGroupUpdatedSuccessfully_WhenRatingGroupWithServiceInfoProvided() throws Exception {
        request.setParameter(GROUP_IDS, "1");
        request.setParameter("selectedServiceTypes",getSelectedServicesForRatingGroup());
        generateRatingGroup("1","RatingGroup1",1L,"1",Collections.EMPTY_LIST);
        setRatingGroupData("1", "RatingGroup121", 2L);
        request.getSession().setAttribute("staffData", getStaffData());
        ActionProxy proxy = createProxyOf("/update.action");
        String result = proxy.execute();
        String expectedMessage = "Rating Group Updated Successfully";
        String actualMessage = getActionMessage(proxy);
        Assert.assertEquals(expectedMessage,actualMessage);
        Assert.assertEquals(Results.REDIRECT_ACTION, result);
    }

    @Test public void updateRatingGroupRedirectToListWhenSessionIsClosedWithinOperation() throws Exception {
        request.setParameter(GROUP_IDS, "1");
        request.setParameter("selectedServiceTypes",getSelectedServicesForRatingGroup());
        generateRatingGroup("1","RatingGroup1",1L,"1",Collections.EMPTY_LIST);
        setRatingGroupData("1", "RatingGroup121", 2L);
        request.getSession().setAttribute("staffData", getStaffData());
        session.close();
        ActionProxy proxy = createProxyOf("/update.action");
        String result = proxy.execute();
        String expectedMessage = "Unknown Internal Error";
        String actualMessage = getActionError(proxy,1);
        Assert.assertEquals(expectedMessage, actualMessage);
        Assert.assertEquals(Results.REDIRECT_ERROR.getValue(), result);
    }

    //// test case for view

    @Test
    public void viewRedirectsToSearchActionWhenRatingGroupIdNotFoundInReqScope() throws Exception {
        String ratingGroupId = null;
        request.setParameter("ratingGroupId",ratingGroupId);
        ActionProxy proxy = createProxyOf("/view.action");
        String result = proxy.execute();
        Assert.assertEquals(Results.LIST.getValue(), result);
    }

    @Test
    public void viewRedirectsToViewActionWhenRatingGroupIdProvidedInSessionScope() throws Exception {
        request.getSession().setAttribute("ratingGroupId", "1");
        generateRatingGroup("1","RatingGroup1",1L,"1",Collections.EMPTY_LIST);
        ActionProxy proxy = createProxyOf("/view.action");
        String result = proxy.execute();
        Assert.assertEquals(Results.VIEW.getValue(), result);
    }

    @Test
    public void viewRedirectsToViewActionWhenRatingGroupIdProvidedInReqParam() throws Exception {
        request.setParameter("ratingGroupId", "1");
        generateRatingGroup("1","RatingGroup1",1L,"1",Collections.EMPTY_LIST);
        ActionProxy proxy = createProxyOf("/view.action");
        String result = proxy.execute();
        Assert.assertEquals(Results.VIEW.getValue(), result);
    }

    @Test
    public void viewRedirectsToViewActionWhenRatingGroupIdProvidedInReqAttribute() throws Exception {
        request.setAttribute("ratingGroupId", "1");
        generateRatingGroup("1","RatingGroup1",1L,"1",Collections.EMPTY_LIST);
        ActionProxy proxy = createProxyOf("/view.action");
        String result = proxy.execute();
        Assert.assertEquals(Results.VIEW.getValue(), result);
    }

    //test case for delete operation

    @Test
    public void deleteRedirectsToRedirectActionWithMessage_RatingGroupDeletedSuccessfully_WhenIdsProvidedInReqParam() throws Exception {
        String[] ids = new String[3];
        ids[0] = "1";
        ids[1] = "2";
        ids[2] = "3";
        request.setParameter("ids", ids);
        for(int i=0;i<3;i++){
            generateRatingGroup(String.valueOf(i+1),"RatingGroup"+i,1L+i,"1",Collections.EMPTY_LIST);
        }
        ActionProxy proxy = createProxyOf("/delete.action");
        String result = proxy.execute();
        String expectedMessage = "Rating Group Deleted Successfully";
        String actualMessage = getActionMessage(proxy);
        Assert.assertEquals(expectedMessage,actualMessage);
        Assert.assertEquals(Results.REDIRECT_ACTION, result);
    }

    @Test
    public void deleteRedirectsToSearchWithMessage_RatingGroupIsConfiguredWithPCCRule_WhenRatingGroupIsAssociatedWithPccRule() throws Exception {
        String[] ids = new String[3];
        ids[0] = "1";
        ids[1] = "2";
        ids[2] = "3";
        request.setParameter("ids", ids);
        generatePccRule();
        DataServiceTypeData serviceType = generateService("1","ALL-Service",1L);
        session.save(serviceType);
        List<DataServiceTypeData> dataServiceTypeData = Collectionz.newArrayList();
        dataServiceTypeData.add(serviceType);

        generateRatingGroup("1", "RatingGroup0", 1L, "1",Collections.EMPTY_LIST);
        generateRatingGroup("2", "RatingGroup1", 2L, "1", dataServiceTypeData);
        generateRatingGroup("3", "RatingGroup2", 3L, "1",Collections.EMPTY_LIST);

        ActionProxy proxy = createProxyOf("/delete.action");
        String result = proxy.execute();
        String expectedMessage = "Rating Group: RatingGroup1 is configured with Pcc Rule.";
        String actualMessage = getActionError(proxy,0);
        Assert.assertEquals(expectedMessage,actualMessage);
        Assert.assertEquals(Results.LIST.getValue(), result);
    }

    @Test
    public void deleteRedirectsToRedirectActionWithMessage_DELETE_SUCCESS_WhenRatingGroupIsNotConfiguredWithPccRuleAndServicesAreDefined() throws Exception {
        String[] ids = new String[3];
        ids[0] = "1";
        ids[1] = "2";
        ids[2] = "3";
        request.setParameter("ids", ids);
        DataServiceTypeData serviceType = generateService("1","ALL-Service",1L);
        session.save(serviceType);
        List<DataServiceTypeData> dataServiceTypeData = Collectionz.newArrayList();
        dataServiceTypeData.add(serviceType);

        generateRatingGroup("1", "RatingGroup0", 1L, "1",Collections.EMPTY_LIST);
        generateRatingGroup("2", "RatingGroup1", 2L, "1", dataServiceTypeData);
        generateRatingGroup("3", "RatingGroup2", 3L, "1",Collections.EMPTY_LIST);

        ActionProxy proxy = createProxyOf("/delete.action");
        String result = proxy.execute();
        Assert.assertEquals(Results.REDIRECT_ACTION, result);
    }

    private void generatePccRule(){
        PCCRuleData pccRule = new PCCRuleData();
        pccRule.setChargingKey("2");
        pccRule.setMonitoringKey("PccRule");
        session.save(pccRule);
    }


    private void setStaffBelongingGroup() {
        List<GroupData> staffBelongingGroups = Collectionz.newArrayList();
        staffBelongingGroups.add(createDefaultGroup());
        request.getSession().setAttribute("staffBelongingGroups",staffBelongingGroups);
    }


    private String getSelectedServicesForRatingGroup() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=1;i<3;i++){
                session.save(generateService("Service"+i,"Service_"+i,i+1L));
                stringBuilder.append("Service");
                stringBuilder.append(i);
                stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    private void setRatingGroupData(String id, String name, Long identifier) {
        request.setParameter("ratingGroupData.id", id);
        request.setParameter("ratingGroupData.name", name);
        request.setParameter("ratingGroupData.identifier", String.valueOf(identifier));

    }

    private void generateRatingGroup(String id, String name, Long identifier, String groups, List<DataServiceTypeData> services) {
        RatingGroupData ratingGroup = new RatingGroupData();
        ratingGroup.setId(id);
        ratingGroup.setName(name);
        ratingGroup.setIdentifier(identifier);
        ratingGroup.setStatus("ACTIVE");
        ratingGroup.setGroups(groups);
        if(Collectionz.isNullOrEmpty(services) == false) {
            ratingGroup.setDataServiceTypeData(services);
        }
        session.save(ratingGroup);
    }

    private GroupData createDefaultGroup() {
        GroupData group = new GroupData();
        group.setId("1");
        group.setName("DefaultGroup");
        return group;
    }

    private StaffData getStaffData() {
        StaffData adminStaff = new StaffData();
        adminStaff.setUserName("admin1");
        adminStaff.setId("1");
        return adminStaff;
    }

    private String getActionError(ActionProxy proxy,int index) {
        ActionSupport action = (ActionSupport) proxy.getAction();
        Collection<String> actionErrors = action.getActionErrors();
        return Iterators.get(actionErrors.iterator(), index);
    }

    private String getActionMessage(ActionProxy proxy) {
        ActionSupport action = (ActionSupport) proxy.getAction();
        Collection<String> actionMessages = action.getActionMessages();
        return Iterators.get(actionMessages.iterator(), 0);
    }

    private ActionProxy createProxyOf(String action) {
        ActionProxy proxy = getActionProxy(action);
        proxy.setExecuteResult(false);
        return proxy;
    }

    private DataServiceTypeData generateService(String id, String name, Long identifier){
        DataServiceTypeData service = new DataServiceTypeData();
        service.setId(id);
        service.setName(name);
        service.setServiceIdentifier(identifier);
        return service;
    }


    @After
    public void after() {
        if(session.isOpen()) {
            session.getTransaction().rollback();
        }
    }

    @Override
    protected String getConfigPath() {
        return "resource/struts/struts-test.xml";
    }
}
