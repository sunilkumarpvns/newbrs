<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionManagerFieldMappingData" %>
<%@page import="com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData" %>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.Set" %>
<%@page import="com.elitecore.core.serverx.sessionx.FieldMapping" %>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@taglib prefix="ec" uri="/elitetags" %>
<%
	String index = request.getParameter("mappingIndexParam"); 
	String defaultTemplateId = request.getParameter("defaultTemplateValue");	
	String mappingIds = defaultTemplateId;
	DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
	DiameterSessionManagerMappingData diameterSessionManagerMappingData = diameterSessionManagerBLManager.getDiameterSessionManagerMappingDataById(mappingIds);
		
	Set<SessionManagerFieldMappingData> sessionManagerFieldMappingDatas = diameterSessionManagerMappingData.getSessionManagerFieldMappingData();
%>
<%for(SessionManagerFieldMappingData sessionManagerFieldMappingData : sessionManagerFieldMappingDatas){ %>
<tr>
	<td class="allborder"><input class="dbFieldName noborder" type="text" name="dbFieldName<%=index%>" id="dbFieldName<%=index%>" value="<%=(sessionManagerFieldMappingData.getDbFieldName() == null)?"":sessionManagerFieldMappingData.getDbFieldName() %>" maxlength="1000" size="28" style="width:100%" /></td>
	<td class="tblrows"><input class="referringAttribute noborder" type="text" name="referringAttribute<%=index%>"  id="referringAttribute<%=index%>" value="<%=(sessionManagerFieldMappingData.getReferringAttr() == null)?"":sessionManagerFieldMappingData.getReferringAttr()%>" maxlength="1000" val size="28" style="width:100%" /></td>
	<td class="tblrows">
		<% if(sessionManagerFieldMappingData.getDataType() == 0){%>
			<select class="dataType" name="dataType<%=index%>" id="dataType<%=index%>" size="1"  style="width: 100%;height: 100%" >
				<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
				<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
			</select>
		<%}else{ %>
			<select class="dataType" name="dataType<%=index%>" id="dataType<%=index%>" size="1"  style="width: 100%;height: 100%" >
				<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
				<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
			</select>
		<%} %>
	</td>
	<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>" id="defaultValue<%=index%>" value="<%=(sessionManagerFieldMappingData.getDefaultValue() == null)?"":sessionManagerFieldMappingData.getDefaultValue()%>" maxlength="1000" size="30" style="width:100%"/></td>
	<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td>
</tr>
<%}%>