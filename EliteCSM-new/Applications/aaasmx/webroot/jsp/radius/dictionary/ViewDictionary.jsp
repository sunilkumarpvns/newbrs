<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.radius.dictionary.forms.ViewDictionaryForm" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>

<%
    String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
function edit(){
	//document.forms[0].submit();
	
}
function download(){
	//document.forms[0].submit();
	
}
setTitle('<bean:message bundle="radiusResources" key="dictionary.module.name"/>');
</script>
      
<%
      ViewDictionaryForm viewDictionaryForm = (ViewDictionaryForm)request.getAttribute("viewDictionaryForm");
%>
<html:form action="/viewDictionary" >    
	<html:hidden name="viewDictionaryForm" styleId="dictionaryId" property="dictionaryId"/>
    <bean:define id="lastModifiedByStaffBean" name="viewDictionaryForm" property="lastModifiedByStaff" type="com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData" />
    <bean:define id="createdByStaffBean" name="viewDictionaryForm" property="createdByStaff" type="com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData" />    
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
  			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  				&nbsp;
  			</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
  		  		<tr>
		    		<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
					
						<tr>
							<td class="table-header" colspan="4">		
								<bean:message bundle="radiusResources" key="dictionary.details"/>
							</td>
		    			</tr>
		    <tr> 		
		      <td colspan="4">  
		        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
                  <tr> 
                    <td colspan="2" width="100%" class="tblheader-bold"><bean:message bundle="radiusResources" key="dictionary.summary"/></td>
                  </tr>
                
		          <tr> 
		            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="dictionary.name" /></td>
		            <td class="tblcol" width="70%" height="20%" >
		            	<bean:write name="viewDictionaryForm" property="name"/>&nbsp;&nbsp;&nbsp;&nbsp;
						<logic:equal name="viewDictionaryForm" property="commonStatusId" value="CST01">
						    <img src="<%=basePath%>/images/show.jpg"/>
						</logic:equal>
						<logic:equal name="viewDictionaryForm" property="commonStatusId" value="CST02">
						    <img src="<%=basePath%>/images/hide.jpg"/>
						</logic:equal>
						&nbsp;&nbsp;<bean:message key="general.status" />
		            </td>
		          </tr>

		          <tr> 
		            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="dictionary.description" /></td>
		            <td class="tblcol" width="70%" height="20%"><bean:write name="viewDictionaryForm" property="description"/>&nbsp;</td>
		          </tr>

		          <tr> 
		            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="dictionary.vendorid" /></td>
		            <td class="tblcol" width="70%" height="20%"><bean:write name="viewDictionaryForm" property="vendorId"/>&nbsp;</td>
		          </tr>

		          <tr> 
		            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="dictionary.createdon" /></td>
		            <td class="tblcol" width="70%" height="20%"><%=EliteUtility.dateToString(viewDictionaryForm.getCreateDate(), ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT))%>&nbsp;</td>		            

		          </tr>

		          <tr> 
		            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="dictionary.createdby" /></td>
		            <td class="tblcol" width="70%" height="20%"><bean:write name="createdByStaffBean" property="name"/>&nbsp;</td>
		          </tr>

		          <tr> 
		            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="dictionary.lastmodifiedon" /></td>
		            <td class="tblcol" width="70%" height="20%"><%=EliteUtility.dateToString(viewDictionaryForm.getLastModifiedDate(), ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT))%>&nbsp;</td>
		          </tr>

		          <tr> 
		            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="dictionary.lastmodifiedby" /></td>
		            <td class="tblcol" width="70%" height="20%"><bean:write name="lastModifiedByStaffBean" property="name"/>&nbsp;</td>
		          </tr>

		        </table>
				</td>
		    </tr>
			<tr> 			
		      <td  width="100%" class="tblheader-bold" colspan="4"><bean:message bundle="radiusResources" key="dictionary.parameters.details" /></td>
		    </tr>
		    
    		<tr>				    		
				<td colspan="4">
					<table align="center" border="0" cellspacing="0" cellpadding="0" width="100%">
					
					<% int index = 0; %>
							<tr>
							  <td align="left" class="tblheader" valign="top" width="5%" ><bean:message key="general.serialnumber" /></td>
							  <td align="left" class="tblheader" valign="top" width="10%"><bean:message bundle="radiusResources" key="dictionary.vendor.parameterid"/></td>
							  <td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="radiusResources" key="dictionary.name" /></td>
							  <td align="left" class="tblheader" valign="top" width="35%" ><bean:message bundle="radiusResources" key="dictionary.predefinedvalues" /></td>
							  <td align="left" class="tblheader" valign="top" width="10%" ><bean:message bundle="radiusResources" key="dictionary.datatype" /></td>
  							  <td align="left" class="tblheader" valign="top" width="5%" ><bean:message bundle="radiusResources" key="dictionary.avpair" /></td>
  							  <td align="left" class="tblheader" valign="top" width="5%" ><bean:message bundle="radiusResources" key="dictionary.hastag" /></td>
  							  <td align="left" class="tblheader" valign="top" width="5%" ><bean:message bundle="radiusResources" key="dictionary.paddingtype" /></td>
							</tr>
					
				    	<logic:iterate id="paramDetail" name="viewDictionaryForm" property="dictionaryParameterDetail" type="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData" >
				    	    <bean:define id="dataTypeBean" name="paramDetail" property="dataType" type="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDataTypeData" />    
							<tr>
							  	<td align="left" class="tblfirstcol"><%=(index+1)%></td>
								<td align="left" class="tblrows"><bean:write name="paramDetail" property="vendorParameterId"/></td>
							  	<td align="left" class="tblrows"><bean:write name="paramDetail" property="name" format="" /></td>
								<td align="left" class="tblrows">
								<%
								if(paramDetail.getPredefinedValues() != null && paramDetail.getPredefinedValues().trim().length() > 40){%>
								<a  href="javascript:void(0)"  onclick="window.open('<%=basePath%>/viewDictionaryParam.do?dictionaryId=<bean:write name="viewDictionaryForm" property="dictionaryId"/>&dictionaryDetailId=<bean:write name="paramDetail" property="dictionaryParameterDetailId"/>','CSVWin','top=0, left=0, height=300, width=500, scrollbars=yes, status')" >
									<%=EliteUtility.trimExtraChars(paramDetail.getPredefinedValues()==null?"":paramDetail.getPredefinedValues(),40)%>&nbsp;
								</a>
								<%}else{%>								
									<bean:write name="paramDetail" property="predefinedValues"  />&nbsp;								
								<%}%>																
								</td>
								<td align="left" class="tblrows"><bean:write name="dataTypeBean" property="name"/></td>								
								<td align="left" class="tblrows"><bean:write name="paramDetail" property="avPair"/></td>
								<td align="left" class="tblrows"><bean:write name="paramDetail" property="hasTag"/></td>	
								<td align="left" class="tblrows"><bean:write name="paramDetail" property="paddingType"/></td>									
							</tr>
						<% index++; %>
                    	</logic:iterate>
					</table>
				</td>
			</tr>
			
			<tr> 
		      <td  width="3%">&nbsp;</td>			
		      <td  width="97%">&nbsp;</td>
		    </tr>
			
            <tr>
   		      <td  >&nbsp;</td>			    		
              <td class="btns-td" valign="middle" align="center">
                <input type="button" name="c_btnEdit" onclick="javascript:location.href='<%=basePath%>/updateDictionary.do?dictionaryId=<bean:write name="viewDictionaryForm" property="dictionaryId"/>'" value="Edit" class="light-btn" />
               <!-- 	<html:button property="c_btnDownload"  onclick="download()"  value="Download"  styleClass="light-btn" />                   -->
               	<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/listDictionary.do'"  value="Cancel" class="light-btn"> 
              </td>
            </tr>
           </table>
          </td>
        </tr>
        <%@ include file="/jsp/core/includes/common/Footer.jsp" %>
      </table>
    </td>
  </tr>
</table>
</html:form>