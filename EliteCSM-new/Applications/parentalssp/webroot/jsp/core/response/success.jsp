<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-nested.tld" prefix="nested" %>
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant" %>

<script language="JavaScript">
    window.focus();
</script>
<% 
	String selectedLink=(String)request.getSession().getAttribute(SessionAttributeKeyConstant.SELECTED_LINK);
	String successMessage="The operation was successfullly completed";
	String strResponseUrl=null;
    String basePath = request.getContextPath();
	if(request.getAttribute("successMessage") != null){
		successMessage = (String)request.getAttribute("successMessage");
	}
	
	if(request.getAttribute("responseUrl") != null){
		strResponseUrl =(String)request.getAttribute("responseUrl");		
		strResponseUrl = strResponseUrl.trim();
	}else{
	}
%>
<script>
	function responseUrl(value){
		if(value!=null){
			document.forms[0].action=value;
			document.forms[0].submit();
		}else{
			history.back();
		}
	}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0" height="">
 
	<tr>	
		<td height="35"">
		</td>
	</tr>
	<tr>	
		<td align="center">
		<table width="80%" cellpadding="0" cellspacing="0" border="0" class="orange-border"">
			<tr height="20px">
				<td align="left"   class="table-org-column"><b>Success:</b></td>
			</tr>
			<tr height="50">
                <td align="center" class="tblfirstcol">	
					<ul id="content-body-messages" >								
						<logic:messagesPresent message="true" >
						           <html:messages bundle="resultMessageResources" id="msg" message="true">
						               <bean:write name="msg" /><br>
						           </html:messages>
						</logic:messagesPresent>
					</ul>
				</td>
			</tr>		
			<tr> 
					<td class="labeltext" valign="top" align="center" width="100%">
					 &nbsp;
					</td>
			</tr>
			
			<tr> 
					<td class="labeltext" valign="top" align="center" width="100%"> 
						<html:form action="<%=strResponseUrl%>">
							
							<html:button property="c_btnOK" value="  OK  " styleClass="orange-btn"  onclick="document.forms[0].submit();"/>										
						</html:form>
					</td>
			</tr>
			<tr> 
					<td class="labeltext" valign="top" align="center" width="100%">
					 &nbsp;
					</td>
			</tr>
		</table>
		</td>
	</tr>
</table>