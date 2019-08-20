 <%@page import="com.elitecore.commons.base.Collectionz"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.ActivePassiveCommunicatorData" %>
<%@ page import="com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.CommunicatorData" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
 <%@ page import="com.elitecore.elitesm.web.radius.radiusesigroup.form.RadiusESIGroupForm" %>

 <%
	 RadiusESIGroupForm radiusESIGroupForm = (RadiusESIGroupForm)request.getAttribute("radiusESIGroupForm");
 %>
 <script type="text/javascript" src="<%=basePath%>/js/radius/radius-esigroup.js"></script>

 <script type="text/javascript">

     var configuredPrimaryEsi = [];
     var configuredSecondaryEsi = [];
     var activePassiveEsi= [];

     $( document ).ready(function() {

         var esiIdSequence = 0;
         <% if(Collectionz.isNullOrEmpty(radiusESIGroupForm.getPrimaryEsiValues()) == false){%>
         <% for(CommunicatorData esiDeta : radiusESIGroupForm.getPrimaryEsiValues()) { %>
         var esiDetail = {
             esiName: '<%=esiDeta.getName() %>',
             esiID: '<%=esiDeta.getName() %>',
             weightage:'<%=esiDeta.getLoadFactor()%>'
         }
         esiDetail.esiID = esiDetail.esiID + esiIdSequence;
         esiIdSequence++;
         configuredPrimaryEsi.push(esiDetail);
         <% } %>

         <% for(CommunicatorData esiDeta : radiusESIGroupForm.getSecondaryEsiValues()) { %>
         var esiDetail = {
             esiName: '<%=esiDeta.getName() %>',
             esiID: '<%=esiDeta.getName() %>',
             weightage:'<%=esiDeta.getLoadFactor()%>'
         }
         esiDetail.esiID = esiDetail.esiID + esiIdSequence;
         esiIdSequence++;
         configuredSecondaryEsi.push(esiDetail);
         <% } %>
         $("#c_tblCreateRadiusESIGroup tr.esidropdownbox").hide();
         populateConfiguredEsi(".selectedPrimaryEsiIds",configuredPrimaryEsi);
         populateConfiguredEsi(".selectedSecondaryEsiIds",configuredSecondaryEsi);
         <%} else if(Collectionz.isNullOrEmpty(radiusESIGroupForm.getActivePassiveEsiList()) == false){%>
         <% for(ActivePassiveCommunicatorData esiDeta : radiusESIGroupForm.getActivePassiveEsiList()) { %>
         var esiDetail = {
             activeEsiName: '<%=esiDeta.getActiveEsiName() %>',
             passiveEsiName: '<%=esiDeta.getPassiveEsiName() %>',
             weightage:'<%=esiDeta.getLoadFactor()%>'
         }
         activePassiveEsi.push(esiDetail);
         <% } %>
		 $("#c_tblCreateRadiusESIGroup tr.nplusm").hide();
         populateActivePassiveEsi(activePassiveEsi);
         <%}%>
     });
 </script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  	<tr>
      <td valign="top" align="right">
        <table name="c_tblCreateRadiusESIGroup" id="c_tblCreateRadiusESIGroup" width="100%" border="0" cellspacing="0" cellpadding="0" >
	          <tr>
	            <td class="tblheader-bold" colspan="5" height="20%">
	              <bean:message bundle="radiusResources" key="radiusesigroup.view"/>
	            </td>
	          </tr>
	          <tr>
	            <td class="tblfirstcol" width="20%" ><bean:message bundle="radiusResources" key="radiusesigroup.name" /></td>
	            <td class="tblcol" width="*" ><bean:write name="radiusESIGroupForm" property="esiGroupName"/>&nbsp;</td>
	          </tr>
	          <tr>
	            <td class="tblfirstcol" width="20%" ><bean:message bundle="radiusResources" key="radiusesigroup.description" /></td>
	            <td class="tblcol" width="*" colspan="3"><bean:write name="radiusESIGroupForm" property="description"/>&nbsp;</td>
	          </tr>
				<tr>
				<td class="tblfirstcol" width="20%" ><bean:message bundle="radiusResources" key="radiusesigroup.redundancymode" /></td>
				<td class="tblcol" width="*" colspan="3"><bean:write name="radiusESIGroupForm" property="redundancyMode"/>&nbsp;</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="20%" ><bean:message bundle="radiusResources" key="radiusesigroup.esitype" /></td>
				<td class="tblcol" width="*" colspan="3"><bean:write name="radiusESIGroupForm" property="esiType"/>&nbsp;</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="20%" ><bean:message bundle="radiusResources" key="radiusesigroup.stickysession" /></td>
				<td class="tblcol" width="*" colspan="3"><bean:write name="radiusESIGroupForm" property="stickySession"/>&nbsp;</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="20%" ><bean:message bundle="radiusResources" key="radiusesigroup.switchback" /></td>
				<td class="tblcol" width="*" colspan="3"><bean:write name="radiusESIGroupForm" property="switchBack"/>&nbsp;</td>
			</tr>
			<tr class="nplusm">
				<td class="tblfirstcol" width="20%">
					<bean:message bundle="radiusResources" key="radiusesigroup.esi"/>
				</td>
				<td class="tblcol" width="*" align="left" colspan="4" style="padding-top: 5px;padding-bottom: 5px">
					<table id="nplusmtable" cellpadding="0" cellspacing="0" border="0" width="75%">
						<td class="labeltext"><bean:message bundle="radiusResources" key="radiusesigroup.primaryesi" /></td>
						<td valign="top">
							<select class="labeltext select-box-style selectedPrimaryEsiIds" name="selectedPrimaryEsi" id="selectedPrimaryEsi" multiple="multiple" style="width: 200px;" disabled="disabled"></select>
						</td>
						<td class="labeltext"><bean:message bundle="radiusResources" key="radiusesigroup.secondaryesi" /></td>
						<td valign="top">
							<select class="labeltext select-box-style selectedSecondaryEsiIds" name="selectedSecondaryEsi" id="selectedSecondaryEsi" multiple="multiple" style="width: 200px;" disabled="disabled"></select>
						</td>
					</table>
				</td>
			</tr>
			<tr class="esidropdownbox">
				<td class="tblfirstcol" width="20%">
					<bean:message bundle="radiusResources" key="radiusesigroup.esi"/>
				</td>
				<td class="tblcol" width="*" align="left" colspan="2" style="padding-top: 5px;padding-bottom: 5px">
					<table id="activePassiveEsiTbl" cellpadding="0" cellspacing="0" border="0" width="50%">
						<tr>
							<td class="tblheader" width="15%" align="middle">
								<bean:message bundle="radiusResources" key="radiusesigroup.activeesi" />
							</td>
							<td class="tblheader" width="15%" align="middle">
								<bean:message bundle="radiusResources" key="radiusesigroup.passiveesi" />
							</td>
							<td class="tblheader" width="10%" align="middle">
								<bean:message bundle="radiusResources" key="radiusesigroup.esiweightage" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</td>
    </tr>
</table>

 <table id="activePassiveTbl" class="activePassiveTbl" cellpadding="0" cellspacing="0" width="100%" style="display: none" readonly="readonly">
	 <tr>
		 <td class="tblfirstcol">
			 <input type="text" id="activeesi" class="activeesi" name="activeesi" readonly="readonly"/>
		 </td>
		 <td class="tblrows">
			 <input type="text" id="passiveesi" class="passiveesi" name="passiveesi" readonly="readonly"/>
		 </td>
		 <td class="tblrows" width="10%" align="middle">
			 <select  id="weightage" name="weightage" style="width: 100%" disabled="disabled">
				 <option value="0">0</option>
				 <option value="1" selected="selected">1</option>
				 <option value="2">2</option>
				 <option value="3">3</option>
				 <option value="4">4</option>
				 <option value="5">5</option>
				 <option value="6">6</option>
				 <option value="7">7</option>
				 <option value="8">8</option>
				 <option value="9">9</option>
				 <option value="10">10</option>
			 </select>
		 </td>
	 </tr>
 </table>


