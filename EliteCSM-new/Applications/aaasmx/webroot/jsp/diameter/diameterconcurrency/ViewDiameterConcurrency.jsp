<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@page import="com.elitecore.core.serverx.sessionx.FieldMapping"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <bean:define id="diameterConcurrencyDataBean" name="diameterConcurrencyData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData" />
   <bean:define id="datasourceBean" name="databaseDSData" scope="request" type="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData"></bean:define>
   <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources"  key="diameterpolicy.diameterpolicygroup.name" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterConcurrencyDataBean" property="name"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameter.diameterconcurrency.description" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterConcurrencyDataBean" property="description"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameter.diameterconcurrency.datasource" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3">
            	<logic:notEmpty name="datasourceBean" property="name">
			     	<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="datasourceBean" property="databaseId"/>','<bean:write name="datasourceBean" property="name"/>','<%=EliteViewCommonConstant.DATABASE_DATASOURCE%>');">
			     		<bean:write name="datasourceBean" property="name"/>
			        </span>
			     </logic:notEmpty>
			     <logic:empty name="datasourceBean" property="name">
			   		  &nbsp;
			     </logic:empty>
            </td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameter.diameterconcurrency.tablename" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterConcurrencyDataBean" property="tableName"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameter.diameterconcurrency.starttime" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterConcurrencyDataBean" property="startTimeField"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameter.diameterconcurrency.lastupdatetime" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterConcurrencyDataBean" property="lastUpdateTimeField"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameter.diameterconcurrency.concurrencyidentityfield" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterConcurrencyDataBean" property="concurrencyIdentityField"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameter.diameterconcurrency.dbfailureaction" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterConcurrencyDataBean" property="dbFailureAction"/>&nbsp;</td>
          </tr>
          <tr>
			<td class="tblheader-bold" colspan="2">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.sessionoverideproperties" />
			</td>
		  </tr>
		  <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameter.diameterconcurrency.action" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterConcurrencyDataBean" property="sessionOverrideAction"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameter.diameterconcurrency.sessionoverridefields" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterConcurrencyDataBean" property="sessionOverrideFields"/>&nbsp;</td>
          </tr>
          <tr>
			<td class="tblheader-bold" colspan="2">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.mandatorydbfieldmapping" />
			</td>
		  </tr>
		  <tr>
			<td colspan="2">
				<table cellspacing="0" cellpadding="0" border="0" width="100%" class="madatoryMappingsTable">
					<tr>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.fields" />
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.dbfieldname" />
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconurrency.referringattribute" />
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.datatype" />
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.defaultvalue" />
						</td>
						<td class="tblheader" width="12%" align="center">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.includeinasr"/>
						</td>
					</tr>
					<logic:iterate id="obj" name="mandatoryFieldMappingsList" scope="request"  type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyFieldMapping">
						<tr>
							<td class="labeltext allborder" width="17%">
								<bean:write property="logicalField" name="obj" /> &nbsp;
							</td>
							<td class="tblrows" width="17%">
								<bean:write property="dbFieldName" name="obj" />&nbsp; 
							</td>
							<td class="tblrows" width="17%">
								<bean:write property="referringAttribute" name="obj" /> &nbsp;
							</td>
							<td class="tblrows" width="17%">
								<bean:message bundle="diameterResources" key="diameter.diameterconurrency.datatypevalue" />
							</td>
							<td class="tblrows" width="17%">
								<bean:write property="defaultValue" name="obj" /> &nbsp;
							</td>
							<td class="labeltext tblrows capitalize" width="12%" align="center">
								<bean:write property="includeInASR" name="obj" /> &nbsp;
							</td>
						</tr> 	
					</logic:iterate> 
					</table>
			</td>
		  </tr>
		  <tr>
			<td class="tblheader-bold" colspan="2">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.additionaldbfieldmapping" />
			</td>
		  </tr>
		   <tr>
			<td colspan="2">
				<table cellspacing="0" cellpadding="0" border="0" width="100%" id="additionalMappingTable" class="additionalMappingTable">
					<tr>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.dbfieldname" />
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconurrency.referringattribute" />
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.datatype" />
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.defaultvalue" />
						</td>
						<td class="tblheader" width="17%" align="center">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.includeinasr"/>
						</td>
						<td width="12%">
							&nbsp;
						</td>
					</tr>
					<logic:iterate id="obj" name="additionalFieldMappingsList" scope="request"  type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyFieldMapping">
					<tr>
						<td class="allborder" width="17%">
							<bean:write property="dbFieldName" name="obj" />&nbsp;
						</td>
						<td class="tblrows" width="17%">
							<bean:write property="referringAttribute" name="obj" /> &nbsp;
						</td>
						<td class="tblrows" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconurrency.datatypevalue" />
						</td>
						<td class="tblrows" width="17%">
							<bean:write property="defaultValue" name="obj" /> &nbsp;
						</td>
						<td class="labeltext tblrows capitalize" width="17%" align="center">
							<bean:write property="includeInASR" name="obj" /> &nbsp;
						</td>
						<td width="12%">
							&nbsp;
						</td>
					</tr>
					</logic:iterate>
				</table>
			</td>
		  </tr>
		</table>
		</td>
    </tr>
</table>