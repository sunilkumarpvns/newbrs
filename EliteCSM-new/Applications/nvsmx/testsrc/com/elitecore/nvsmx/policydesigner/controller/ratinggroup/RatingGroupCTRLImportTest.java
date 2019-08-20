package com.elitecore.nvsmx.policydesigner.controller.ratinggroup;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupDataExt;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.google.common.collect.Iterators;
import com.google.gson.JsonArray;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.StrutsJUnit4TestCase;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * This class will test the behaviour of Import operation performed by RatingGroupCTRL
 * @author ishani.bhatt
 */
@Ignore
public class RatingGroupCTRLImportTest extends StrutsJUnit4TestCase<RatingGroupCTRL> {

    private Session session = null;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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

    //testcase for import operation
    @Test
    public void importRatingGroupRedirectsToSearchWithActionError_ImportExportOperationAlreadyInProgress_whenImportExportOperationIsAlreadyInProgress() throws Exception {
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, true);
        ActionProxy proxy = createProxyOf("/importRatingGroup.action");
        String result = proxy.execute();
        String expectedMessage = "Fail to perform operation. Reason: Import/Export operation is already been processed";
        String actualMessage = getActionError(proxy, 1);
        Assert.assertEquals(expectedMessage, actualMessage);
        Assert.assertEquals(Results.LIST.getValue(), result);
    }

    @Test
    public void importRatingGroupRedirectsToSearchWithActionError_DataNotFound_whenImportFileIsNotProvided() throws Exception {
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
        String test = null;
        request.setParameter("importedFile", test);
        ActionProxy proxy = createProxyOf("/importRatingGroup.action");
        String result = proxy.execute();
        String expectedMessage = "data not found";
        String actualMessage = getActionError(proxy, 1);
        Assert.assertEquals(expectedMessage, actualMessage);
        Assert.assertEquals(Results.LIST.getValue(), result);
    }

    @Test
    public void importRatingGroupRedirectsToSearchWithActionError_InvalidFileTypeForImport_whenInvalidImportFileContentProvided() throws Exception {
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
        request.setParameter("importedFileContentType", "text/html");
        ActionProxy proxy = createProxyOf("/importRatingGroup.action");
        RatingGroupCTRL action = (RatingGroupCTRL) proxy.getAction();
        File file = folder.newFile("rating-group.xml");
        action.setImportedFile(file);
        String result = proxy.execute();
        String expectedMessage = "Invalid File type for import";
        String actualMessage = getActionError(proxy, 1);
        Assert.assertEquals(expectedMessage, actualMessage);
        Assert.assertEquals(Results.LIST.getValue(), result);
    }

    @Test
    public void importRatingGroupRedirectsToSearchWithActionError_FileNotFound_whenInvalidFileNamePassed() throws Exception {
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
        request.setParameter("importedFileContentType", "text/xml");
        ActionProxy proxy = createProxyOf("/importRatingGroup.action");
        RatingGroupCTRL action = (RatingGroupCTRL) proxy.getAction();
        File file = new File("resource/rating-group.xml");
        action.setImportedFile(file);
        String result = proxy.execute();
        String expectedMessage = "data not found";
        String actualMessage = getActionError(proxy, 1);
        Assert.assertEquals(expectedMessage, actualMessage);
        Assert.assertEquals(Results.LIST.getValue(), result);
    }

    @Test
    public void importRatingGroupRedirectsToSearchWithActionError_ImportFailed_whenInvalidContentIsPassedInXML() throws Exception {
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
        request.setParameter("importedFileContentType", "text/xml");
        ActionProxy proxy = createProxyOf("/importRatingGroup.action");
        RatingGroupCTRL action = (RatingGroupCTRL) proxy.getAction();
        File file = folder.newFile("rating-group.xml");
        writeInvalidContentToXMLFile(file);
        action.setImportedFile(file);
        String result = proxy.execute();
        String expectedMessage = "import failed";
        String actualMessage = getActionError(proxy, 1);
        Assert.assertEquals(expectedMessage, actualMessage);
        Assert.assertEquals(Results.LIST.getValue(), result);
    }

    @Test
    public void importRatingGroupRedirectsToImportRatingGroupAction_whenValidXMLFileIsPassed() throws Exception {
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
        request.setParameter("importedFileContentType", "text/xml");
        ActionProxy proxy = createProxyOf("/importRatingGroup.action");
        RatingGroupCTRL action = (RatingGroupCTRL) proxy.getAction();
        File file = folder.newFile("rating-group.xml");
        writeValidContentToXMLFile(file);
        action.setImportedFile(file);
        String result = proxy.execute();
        Assert.assertEquals(Results.IMPORT_RATING_GROUP, result);
    }

    //test case for importData method
    @Test
    public void importDataRedirectsToSearchWithActionError_ImportExportOperationAlreadyInProgress_whenImportExportOperationIsAlreadyInProgress() throws Exception {
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, true);
        ActionProxy proxy = createProxyOf("/importData.action");
        String result = proxy.execute();
        String expectedMessage = "Fail to perform operation. Reason: Import/Export operation is already been processed";
        String actualMessage = getActionError(proxy, 1);
        Assert.assertEquals(expectedMessage, actualMessage);
        Assert.assertEquals(Results.LIST.getValue(), result);
    }

    @Test
    public void importRedirectsToImportStatusReportWhenSelectedRatingGroupsArePassedForImportOperation() throws Exception {
        String[] ids = new String[2];
        ids[0] = "1";
        ids[1] = "3";
        String[] names = new String[2];
        names[0] = "1|RatingGroup0";
        names[1] = "3|RatingGroup2";
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
        request.setParameter("ids",ids);
        request.setParameter("names",names);
        List<RatingGroupDataExt> ratingGroups = Collectionz.newArrayList();
        for(int i=0;i<3;i++) {
            ratingGroups.add(generateRatingGroupDataExt(String.valueOf(i + 1), "RatingGroup" + i, i + 1L, null));
        }
        request.getSession().setAttribute("ratingGroups",ratingGroups);
        session.save(getStaffData());
        request.getSession().setAttribute("staffData", getStaffData());
        request.getSession().setAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP, getStaffBelongingGroupWiseRoleMap());
        ActionProxy proxy = createProxyOf("/importData.action");
        String result = proxy.execute();
        Assert.assertEquals(Results.IMPORT_STATUS_REPORT,result);

    }

    @Test
    public void importRedirectsToImportStatusReportWhenSelectedRatingGroupsArePassedWithoutIdsForImportOperation() throws Exception {
        String[] ids = new String[2];
        ids[0] = "";
        ids[1] = "";
        String[] names = new String[2];
        names[0] = "null|RatingGroup0";
        names[1] = "null|RatingGroup2";
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
        request.setParameter("ids",ids);
        request.setParameter("names",names);
        request.setParameter("userAction" , "REPLACE");
        List<RatingGroupDataExt> ratingGroups = Collectionz.newArrayList();
        for(int i=0;i<3;i++) {
            ratingGroups.add(generateRatingGroupDataExt(String.valueOf(i + 1), "RatingGroup" + i, i + 1L, null));
        }
        request.getSession().setAttribute("ratingGroups",ratingGroups);
        session.save(getStaffData());
        request.getSession().setAttribute("staffData", getStaffData());
        request.getSession().setAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP, getStaffBelongingGroupWiseRoleMap());
        ActionProxy proxy = createProxyOf("/importData.action");
        String result = proxy.execute();
        Assert.assertEquals(Results.IMPORT_STATUS_REPORT,result);

    }

    @Test
    public void importDataWillSetImportStatusJsonObjectToRequestAttrWhenSelectedRatingGroupsArePassedForImportOperation() throws Exception {
        String[] ids = new String[2];
        ids[0] = "1";
        ids[1] = "3";
        String[] names = new String[2];
        names[0] = "1|RatingGroup0";
        names[1] = "3|RatingGroup2";
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
        request.setParameter("ids",ids);
        request.setParameter("names",names);
        request.setParameter("userAction" , "REPLACE");
        List<RatingGroupDataExt> ratingGroups = Collectionz.newArrayList();
        for(int i=0;i<3;i++) {
            ratingGroups.add(generateRatingGroupDataExt(String.valueOf(i + 1), "RatingGroup" + i, i + 1L, null));
        }
        request.getSession().setAttribute("ratingGroups",ratingGroups);
        request.getSession().setAttribute("staffData", getStaffData());
        request.getSession().setAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP, getStaffBelongingGroupWiseRoleMap());
        ActionProxy proxy = createProxyOf("/importData.action");
        proxy.execute();
        String expectedJsonString = "[{\"name\":\"RatingGroup0\",\"messages\":\"SUCCESS\",\"subReasons\":[],\"remarks\":\" ---- \"},{\"name\":\"RatingGroup2\",\"messages\":\"SUCCESS\",\"subReasons\":[],\"remarks\":\" ---- \"}]";
        JsonArray jsonArray = (JsonArray) request.getAttribute("importStatus");
        Assert.assertEquals(expectedJsonString, jsonArray.toString());

    }

    @Test
    public void importDataSendsImportStatusReportAndCreateRatingGroupsInDatabase() throws Exception {
        String[] ids = new String[2];
        ids[0] = "1";
        ids[1] = "3";
        String[] names = new String[2];
        names[0] = "1|RatingGroup0";
        names[1] = "3|RatingGroup2";
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
        request.setParameter("ids",ids);
        request.setParameter("names",names);
        request.setParameter("userAction" , "REPLACE");
        List<RatingGroupDataExt> ratingGroups = Collectionz.newArrayList();
        for(int i=0;i<3;i++) {
            ratingGroups.add(generateRatingGroupDataExt(String.valueOf(i + 1), "RatingGroup" + i, i + 1L, null));
        }
        request.getSession().setAttribute("ratingGroups",ratingGroups);
        StaffData staffData = getStaffData();
        request.getSession().setAttribute("staffData", staffData);
        request.getSession().setAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP, getStaffBelongingGroupWiseRoleMap());
        ActionProxy proxy = createProxyOf("/importData.action");
        proxy.execute();
        session = HibernateSessionFactory.getSession();
        session.beginTransaction();
        List<RatingGroupDataExt> ratingGroupsFromDB = CRUDOperationUtil.get(RatingGroupDataExt.class, Order.asc("name"));
        List<RatingGroupDataExt> ratingGroupsExpected = new ArrayList<RatingGroupDataExt>();
        ratingGroupsExpected.add(generateRatingGroupDataExt("1","RatingGroup0",1L,null));
        ratingGroupsExpected.add(generateRatingGroupDataExt("3", "RatingGroup2", 3L, null));
        assertReflectionEquals(ratingGroupsExpected.toArray(), ratingGroupsFromDB.toArray(), ReflectionComparatorMode.LENIENT_ORDER);

    }

    private RatingGroupDataExt generateRatingGroupDataExt(String id, String name, Long identifier, String groups) {
        RatingGroupDataExt ratingGroup = new RatingGroupDataExt();
        ratingGroup.setId(id);
        ratingGroup.setName(name);
        ratingGroup.setIdentifier(identifier);
        ratingGroup.setStatus("ACTIVE");
        ratingGroup.setGroups(groups);
        return ratingGroup;
    }

    private Map<String, RoleData> getStaffBelongingGroupWiseRoleMap() {
        Map<String, RoleData> groupWiseRole = new HashMap<String, RoleData>();
        RoleData role = getRoleData();
        groupWiseRole.put(getGroupData().getId(), role);
        return groupWiseRole;

    }


    private RoleData getRoleData() {
        RoleData roleData = new RoleData();
        roleData.setId("1");
        roleData.setName("AdminRole");
        RoleModuleActionData roleModuleActionData = new RoleModuleActionData();
        roleModuleActionData.setModuleName("RATINGGROUP");
        roleModuleActionData.setActions("CREATE,DELETE,EXPORT,IMPORT,UPDATE");
        List<RoleModuleActionData> set = new ArrayList<>();
        set.add(roleModuleActionData);
        roleData.setRoleModuleActionData(set);
        Set<GroupData> groups = Collectionz.newHashSet();
        groups.add(getGroupData());
        //roleData.setGroupDatas(groups);
        return roleData;
    }


    private GroupData getGroupData() {
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

    private String getActionError(ActionProxy proxy, int index) {
        ActionSupport action = (ActionSupport) proxy.getAction();
        Collection<String> actionErrors = action.getActionErrors();
        return Iterators.get(actionErrors.iterator(), index);
    }


    private ActionProxy createProxyOf(String action) {
        ActionProxy proxy = getActionProxy(action);
        proxy.setExecuteResult(false);
        return proxy;
    }

    private void writeInvalidContentToXMLFile(File file) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(new StringBuilder().append("<rating-group-container123>\n").append("    <ratingGroups>\n").append("        <ratingGroup>\n").append("            <id>RATING_GROUP_1</id>\n").append("            <identifier>0</identifier>\n").append("            <name>Default_Rating_Group</name>\n").append("        </ratingGroup>\n").append("    </ratingGroups>\n").append("</rating-group-container>").toString());
        bw.close();
    }
    private void writeValidContentToXMLFile(File file) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(new StringBuilder().append("<rating-group-container>\n").append("    <ratingGroups>\n").append("        <ratingGroup>\n").append("            <id>RATING_GROUP_1</id>\n").append("            <identifier>0</identifier>\n").append("            <name>Default_Rating_Group</name>\n").append("        </ratingGroup>\n").append("    </ratingGroups>\n").append("</rating-group-container>").toString());
        bw.close();
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
