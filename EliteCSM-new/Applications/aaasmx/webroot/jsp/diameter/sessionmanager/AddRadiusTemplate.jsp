<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.elitecore.core.serverx.sessionx.FieldMapping" %>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData" %>
<%@page import="com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData" %>
<%@page import="java.util.List" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@taglib prefix="ec" uri="/elitetags" %>
<% String index = request.getParameter("mappingIndexParam");%>
<tr>
	<td class="allborder"><input class="dbFieldName noborder" type="text" name="dbFieldName<%=index%>" value="SESSION_ID" maxlength="1000" size="28" style="width:100%" /></td>
	<td class="tblrows"><input  class="referringAttribute noborder" type="text" name="referringAttribute<%=index%>"  value="0:44" maxlength="1000" size="28" style="width:100%"  /></td>
	<td class="tblrows">
		<select class="dataType" name="dataType<%=index%>" id="dataType<%=index%>" size="1"  style="width: 100%;">
			<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
			<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
		</select>
	</td>
	<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>"  maxlength="1000" size="30" style="width:100%" /></td>
	<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td>
</tr>
<tr>
	<td class="allborder"><input class="dbFieldName noborder" type="text" name="dbFieldName<%=index%>"  value="FRAMED_IP_ADDRESS" maxlength="1000" size="28" style="width:100%"  /></td>
	<td class="tblrows"><input  class="referringAttribute noborder" type="text" name="referringAttribute<%=index%>"  value="0:8" maxlength="1000" size="28" style="width:100%"  /></td>
	<td class="tblrows">
		<select class="dataType" name="dataType<%=index%>" id="dataType<%=index%>" size="1"  style="width: 100%;" >
			<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
			<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
		</select>
	</td>
	<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>"  maxlength="1000" size="30" style="width:100%" /></td>
	<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td>
</tr>
<tr>
	<td class="allborder"><input class="dbFieldName noborder" type="text" name="dbFieldName<%=index%>"  value="SESSION_TIMEOUT" maxlength="1000" size="28" style="width:100%" /></td>
	<td class="tblrows"><input class="referringAttribute noborder" type="text" name="referringAttribute<%=index%>"   value="$RES(0:27)" maxlength="1000" size="28" style="width:100%"  /></td>
	<td class="tblrows">
		<select class="dataType" name="dataType<%=index%>" id="dataType<%=index%>" size="1"  style="width: 100%;" >
			<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
			<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
		</select>
	</td>
	<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>"  maxlength="1000" size="30" style="width:100%" /></td>
	<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td>
</tr>
<tr>
	<td class="allborder"><input class="dbFieldName noborder" type="text" name="dbFieldName<%=index%>" value="AAA_ID" maxlength="1000" size="28" style="width:100%"  /></td>
	<td class="tblrows"><input class="referringAttribute noborder" type="text" name="referringAttribute<%=index%>"   value="21067:143" maxlength="1000" size="28" style="width:100%"   /></td>
	<td class="tblrows">
		<select class="dataType" name="dataType<%=index%>" id="dataType<%=index%>" size="1"  style="width: 100%;">
			<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
			<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
		</select>
	</td>
	<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>"  maxlength="1000" size="30" style="width:100%" /></td>
	<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td>
</tr>
<tr>
	<td class="allborder"><input class="dbFieldName noborder" type="text" name="dbFieldName<%=index%>"  value="CLIENT_IP_ADDRESS" maxlength="1000" size="28" style="width:100%"  /></td>
	<td class="tblrows"><input class="referringAttribute noborder" type="text" name="referringAttribute<%=index%>"  value="0:4" maxlength="1000" size="28" style="width:100%"  /></td>
	<td class="tblrows">
		<select class="dataType" name="dataType<%=index%>" id="dataType<%=index%>" size="1"  style="width: 100%;" >
			<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
			<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
	   </select>
	</td>
	<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>"  maxlength="1000" size="30" style="width:100%" /></td>
	<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td>
</tr>
<tr>
	<td class="allborder"><input class="dbFieldName noborder" type="text" name="dbFieldName<%=index%>"  value="SUBSCRIBER_ID" maxlength="1000" size="28" style="width:100%"  /></td>
	<td class="tblrows"><input class="referringAttribute noborder" type="text" name="referringAttribute<%=index%>"   value="0:1" maxlength="1000" size="28" style="width:100%" /></td>
	<td class="tblrows">
		<select class="dataType" name="dataType<%=index%>" id="dataType<%=index%>" size="1"  style="width: 100%;" >
			<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
			<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
		</select>
	</td>
	<td class="tblrows"><input class="defaultValue noborder" type="text" name="defaultValue<%=index%>"  maxlength="1000" size="30" style="width:100%"/></td>
	<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td>
</tr>