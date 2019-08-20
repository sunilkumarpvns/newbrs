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
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class will test export and exportAll functionality of RatingGroupCTRL
 * @author ishani.bhatt
 */
@Ignore
public class RatingGroupCTRLExportTest extends StrutsJUnit4TestCase<RatingGroupCTRL> {

        private Session session = null;

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

        @Test
        public void exportSendsRedirectErrorResultWithActionMessageKey_UNKNOWN_INTERNAL_ERROR_WhenIdNotPassed() throws Exception {
            String[] ids = null;
            request.setParameter("ids", ids);
            ActionProxy proxy = createProxyOf("/export.action");
            String result = proxy.execute();
            String actualKey = getActionError(proxy, 1);
            Assert.assertEquals("Unknown Internal Error", actualKey);
            Assert.assertEquals(Results.REDIRECT_ERROR.getValue(), result);
        }

        @Test
        public void exportRedirectsToSearchActionWithMessage_ImportExportOperationAlreadyInProcess_WhenImportExportOperationInProgress() throws Exception {
            request.setParameter("ids", "RATING_GROUP_1");
            request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, true);
            ActionProxy proxy = createProxyOf("/export.action");
            String result = proxy.execute();
            String expectedMessage = "Fail to perform operation. Reason: Import/Export operation is already been processed";
            String actualMessage = getActionError(proxy, 1);
            Assert.assertEquals(expectedMessage, actualMessage);
            Assert.assertEquals(Results.LIST.getValue(), result);

        }

        @Test
        public void exportRedirectsToSearchActionWithMessage_CanNotFindRatingGroup_forUnknownRatingGroupId() throws Exception {
            request.setParameter("ids", "TEST");
            request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
            ActionProxy proxy = createProxyOf("/export.action");
            String result = proxy.execute();
            String expectedMessage = "Can not find Rating Group in Database";
            String actualMessage = getActionError(proxy, 1);
            Assert.assertEquals(expectedMessage, actualMessage);
            Assert.assertEquals(Results.LIST.getValue(), result);

        }

        @Test
        public void exportRatingGroupWithValidIds() throws Exception {
            session.save(generateRatingGroupDataExt("RATING_GROUP_1", "DEFAULT_RATING_GROUP", 1L, null));
            request.setParameter("ids", "RATING_GROUP_1");
            request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
            ActionProxy proxy = createProxyOf("/export.action");
            String result = proxy.execute();
            String expectedMessage = "Rating Group are exported successfully";
            String actualMessage = getActionMessage(proxy);
            Assert.assertEquals(expectedMessage, actualMessage);
            Assert.assertEquals("EXPORT_COMPLETED", result);

        }

        //TEST CASES FOR EXPORT ALL

        @Test
        public void exportAllRedirectsToSearchActionWithMessage_ImportExportOperationAlreadyInProcess_WhenImportExportOperationInProgress() throws Exception {
            request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, true);
            ActionProxy proxy = createProxyOf("/exportAll.action");
            String result = proxy.execute();
            String expectedMessage = "Fail to perform operation. Reason: Import/Export operation is already been processed";
            String actualMessage = getActionError(proxy, 1);
            Assert.assertEquals(expectedMessage, actualMessage);
            Assert.assertEquals(Results.LIST.getValue(), result);

        }

        /*@Test
        public void exportAllRedirectsToSearchActionWithMessage_NoRatingGroupDataFoundForExportALLOperation_IfNoRatingGroupIsPresent() throws Exception {
            request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
            ActionProxy proxy = createProxyOf("/exportAll.action");
            String result = proxy.execute();
            String expectedMessage = "No Rating Group Data found for Export ALL operation";
            String actualMessage = getActionError(proxy, 1);
            Assert.assertEquals(expectedMessage, actualMessage);
            Assert.assertEquals(Results.LIST.getValue(), result);

        }*/

       /* @Test
        public void exportAllRedirectsToSearchActionWithMessage_NoRatingGroupDataFoundForExportALLOperation_IfInvalidStaffBelongingGroupsNotProvided() throws Exception {
            for (int i = 1; i < 5; i++) {
                session.save(generateRatingGroupDataExt("RATING_GROUP_1" + i, "DEFAULT_RATING_GROUP" + i, 1L + i, "1"));
            }
            request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
            request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP_IDS, "2");
            ActionProxy proxy = createProxyOf("/exportAll.action");
            String result = proxy.execute();
            String expectedMessage = "No Rating Group Data found for Export ALL operation";
            String actualMessage = getActionError(proxy, 1);
            Assert.assertEquals(expectedMessage, actualMessage);
            Assert.assertEquals(Results.LIST.getValue(), result);

        }
*/
       /* @Test
        public void exportAllRedirectsToSearchActionWithMessage_NoRatingGroupDataFoundForExportALLOperation_IfInvalidStaffGroupRoleRelationProvided() throws Exception {
            for (int i = 1; i < 5; i++) {
                session.save(generateRatingGroupDataExt("RATING_GROUP_1" + i, "DEFAULT_RATING_GROUP" + i, 1L + i, "1"));
            }
            request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
            request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP_IDS, "1");
            request.getSession().setAttribute("staffData", getStaffData());
            request.getSession().setAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP, getInvalidStaffBelongingGroupWiseRoleMap());
            ActionProxy proxy = createProxyOf("/exportAll.action");
            String result = proxy.execute();
            String expectedMessage = "No Rating Group Data found for Export ALL operation";
            String actualMessage = getActionError(proxy, 1);
            Assert.assertEquals(expectedMessage, actualMessage);
            Assert.assertEquals(Results.LIST.getValue(), result);

        }*/

        @Test
        public void exportAllRatingGroupWithValidStaffGroupRoleRelation() throws Exception {
            for (int i = 1; i < 5; i++) {
                session.save(generateRatingGroupDataExt("RATING_GROUP_1" + i, "DEFAULT_RATING_GROUP" + i, 1L + i, "1"));
            }
            request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
            request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP_IDS, "1");
            request.getSession().setAttribute("staffData", getStaffData());
            request.getSession().setAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP, getStaffBelongingGroupWiseRoleMap());
            ActionProxy proxy = createProxyOf("/exportAll.action");
            String result = proxy.execute();
            Assert.assertEquals("EXPORT_COMPLETED", result);

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
        private String getActionError(ActionProxy proxy, int index) {
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
        private Map<String, RoleData> getStaffBelongingGroupWiseRoleMap() {
                Map<String, RoleData> groupWiseRole = new HashMap<String, RoleData>();
                RoleData role = getRoleData();
                groupWiseRole.put(getGroupData().getId(), role);
                return groupWiseRole;

        }

        private Map<String, RoleData> getInvalidStaffBelongingGroupWiseRoleMap() {
                Map<String, RoleData> groupWiseRole = new HashMap<String, RoleData>();
                RoleData role = getNoAccessRole();
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
                List<RoleModuleActionData> set = Collectionz.newArrayList();
                set.add(roleModuleActionData);
                roleData.setRoleModuleActionData(set);
                Set<GroupData> groups = Collectionz.newHashSet();
                groups.add(getGroupData());
                //roleData.setGroupDatas(groups);
                return roleData;
        }

        private RoleData getNoAccessRole() {
                RoleData roleData = new RoleData();
                roleData.setId("2");
                roleData.setName("SupportRole");
                RoleModuleActionData roleModuleActionData = new RoleModuleActionData();
                roleModuleActionData.setModuleName("RATINGGROUP");
                roleModuleActionData.setActions("CREATE,DELETE,UPDATE");
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
