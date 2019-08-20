<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.AddAttributeDetailForm" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.radius.dictionary.data.DictionaryData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.radius.dictionary.data.DictionaryParameterDetailData" %>


<script type="text/javascript">

function validateUpdate(){

	var selectedDictionaryIndex=document.getElementById('attributecombo');
	
	if(selectedDictionaryIndex.selectedIndex==0){
		alert("Please Select Dictionary");
		return false;
	}
	window.opener.document.forms[0].attributeId.value = selectedDictionaryIndex.value;
	window.opener.document.forms[0].attributeIdName.value = selectedDictionaryIndex.options[selectedDictionaryIndex.selectedIndex].text;
	window.opener.document.forms[0].action.value = 'addAttribute';
	window.close();
	
	if(window.opener && !window.opener.closed){
		window.opener.document.forms[0].action.value = 'addAttribute';
		window.opener.document.forms[0].submit();
	}
	
}
</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%" >
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <%
	AddAttributeDetailForm addAttributeDetailForm = (AddAttributeDetailForm)request.getAttribute("addAttributeDetailForm");
  %>
  <tr>
    <td width="2" height="179">&nbsp;</td>
    <td width="100%" valign="top" class="box" height="179">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" height="140">
		<html:form action="/addAttributeDetail" >
		<html:hidden name="addAttributeDetailForm" styleId="" property="action" />
		<tr>
          <td height="2" class="table-header">ADD ATTRIBUTE PARAMETER </td>
        </tr>
		<tr>
		  <td class="small-gap" height="2">&nbsp;</td>
		</tr>
        <tr>
          <td valign="top" >
            <table width="97%" border="0" cellspacing=0 cellpadding=0 height="75" align="right">
              <tr bgcolor="#FFFFFF">
				<td align="left" class="labeltext" valign="top" width="20%"><bean:message bundle="servermgrResources" key="servermgr.userfile.dictionarylist" /></td>
				<td align="left" class="labeltext" valign="top" width="40%">
				<% List dictionaryList=addAttributeDetailForm.getListDictionary();%>
					<select name="attributecombo" id="attributecombo" size="1">
						<option value="0" >Dictionary List</option>
						<%
						Collections.sort(dictionaryList); 
						for(int i=0;i<dictionaryList.size();i++){ 
						DictionaryData dictionaryData = (DictionaryData)dictionaryList.get(i);
						
						%>
							<optgroup label="<%=dictionaryData.getName()%>" class="labeltext">
							<% Set dictionaryParameterDetail=dictionaryData.getDictionaryParameterDetail();
									List lstDictionaryParameterDetail = new ArrayList(dictionaryParameterDetail);
									Collections.sort(lstDictionaryParameterDetail) ;
							    	Iterator iter=lstDictionaryParameterDetail.iterator();
							    	while(iter.hasNext()){
							    		DictionaryParameterDetailData dictionaryParameterDetailData=(DictionaryParameterDetailData)iter.next(); %>
							    		<option value="<%=dictionaryParameterDetailData.getVendorId()+":"+dictionaryParameterDetailData.getVendorParameterId()%>"><%=dictionaryParameterDetailData.getName()%></option>
							    	<% } %>
							    	</optgroup>
							<% } %>
					</select>
				  <font color="#FF0000"> *</font>
				</td>
			  </tr>
			   <tr bgcolor="#FFFFFF">
                <td  class="labeltext"  height="44" width="20%" >&nbsp;</td>
                <td  height="44"  class="labeltext" >
                    <input name="c_btnAdd" type="button" class="light-btn"  onclick="validateUpdate()" id="c_btnAdd" value="Add">
                    <input name="c_btnClose" type="button" class="light-btn" id="c_btnClose" onClick="window.close()" value="Close">
                </td>
         </tr>	
			</table>
		   </td>
		</tr>
	</html:form>
	</table>
	</td>
	</tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>

