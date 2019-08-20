<%@page import="java.util.List"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.data.DiameterDictionaryData"%>
<%@ page import="org.apache.struts.util.MessageResources" %>

<% 
    List<RadiusDictionaryData> radiusDictionaryList=(List<RadiusDictionaryData>)request.getSession().getAttribute("radiusDictionaryList");
    List<DiameterDictionaryData> diameterDictionaryList=(List<DiameterDictionaryData>)request.getSession().getAttribute("diameterDictionaryList");    
%>

<script>
	function validateSynchronize(){
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.updatenetdictionarysynchronizejsp.update.query"/>';        
        var agree = confirm(msg);
        if(agree){
            javascript:location.href='<%=basePath%>/synchronizeNetDictionary.do?netserverid=<bean:write name="netServerInstanceBean" property="netServerId"/>';
        }
	}
		
	$(document).ready(function(){
		$("#c_btnSynchronize").focus();
	});
	
</script>



<html:form action="/synchronizeNetDictionary">
<html:hidden name="updateNetDictionarySynchronizeForm" styleId="action" property="action" />
<html:hidden name="updateNetDictionarySynchronizeForm" styleId="netServerId" property="netServerId"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <bean:define id="netServerInstanceBean" name="netServerInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
	        <tr>
			  	<td>&nbsp;</td>
			</tr>
			<tr> 
			  <td class="tblheader-bold" colspan="2"><bean:message bundle="servermgrResources" key="servermgr.synchronizedictionarydetails"/></td>
			</tr>
			<tr> 
			  <td align="left" class="labeltext" valign="top"  colspan="2">&nbsp;</td>
			</tr>	
			<tr>
			  <td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.admininterfaceip"/></td>
			  <td align="left" class="labeltext" valign="top" width="32%" ><bean:write name="netServerInstanceBean" property="adminHost"/>
						<!--  <input type="text" name="adminip" value="192.168.1.1"/> -->
  			  </td>
			</tr>	
			
			<tr>
			  <td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.admininterfaceport"/></td>
			  <td align="left" class="labeltext" valign="top" width="32%" ><bean:write name="netServerInstanceBean" property="adminPort"/>
						<!-- <input type="text" name="adminport" value="8090" /> -->
			  </td>
			</tr>
			<tr>
			<td>&nbsp;</td>
			</tr>
			
			<!--Display Dictionary List -->
			
			<tr>
							
							<td align="left" class="labeltext" valign="top" colspan="2" >
							<table width="70%" cellpadding="0" cellspacing="0" border="0">
							<tr>
							<td  valign="top">
							<table id="radiusDic" class="box" cellpadding="0" cellspacing="0" border="0" width="90%">
								<tr>
									<td colspan="2" class="table-header">Radius Dictionary</td>
								</tr>
								<tr>
								   <td >
								     <table  cellpadding="0" cellspacing="0"  width="100%">
								          <tr>
								            <td class="tblfirstcol" >Name </td>
								            <td class="tblrows" >VendorId</td>
								          </tr>
								          <logic:iterate id="radiusDicBean" name="radiusDictionaryList" type="com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryData">
								            
								            <tr>
								            <td class="tblfirstcol"><%=radiusDicBean.getName()%></td>
								            <td class="tblrows"><%=radiusDicBean.getVendorId()%></td>
								            </tr>
								          
								          </logic:iterate>
								     
								     </table>
									</td>
								</tr>
							</table>
							</td>
							<td  valign="top">
							<table id="diameterDic"  class="box" cellpadding="0" cellspacing="0" width="100%">
								<tr>
									<td colspan="2" class="table-header" align="center">Diameter Dictionary</td>
								</tr>
								<tr>
								   <td >
								     <table  cellpadding="0" cellspacing="0"  width="100%">
								          <tr>
								            <td class="tblfirstcol" >Name </td>
								            <td class="tblrows" >VendorId</td>
								          </tr>
								          <logic:iterate id="diameterDicBean" name="diameterDictionaryList" type="com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.data.DiameterDictionaryData">
								            
								            <tr>
								            <td class="tblfirstcol"><%=diameterDicBean.getVendorName() %></td>
								            <td class="tblrows"><%=diameterDicBean.getVendorId()%></td>
								            </tr>
								          
								          </logic:iterate>
								     
								     </table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						</table>
						</td>
					</tr>
        </table>
	  </td>
    </tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
    <tr>
       <td></td>
    </tr>
    
    <tr> 
      <td>&nbsp;</td>
    </tr>
	
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
		  <tr>         
      		<td class="small-text-grey" style="-webkit-animation-name: blink; important!">Note : All Dictionaries within this Server would be Updated.</td>
      	  </tr>
      	</table>
      </td>
    </tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
   	<tr> 
      <td class="btns-td" valign="middle"  >
      	 <input type="button" name="c_btnSynchronize"  onclick="validateSynchronize()"  id="c_btnSynchronize"  value=" Synchronize To"  class="light-btn">                   
         <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewNetServerInstance.do?netserverid=<bean:write name="netServerInstanceBean" property="netServerId"/>'" value="Cancel" class="light-btn"> 
      </td>
	</tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
</table>
</html:form>