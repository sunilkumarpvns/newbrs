<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<script language="JavaScript">
    window.focus();
</script>
<% 
	String successMessage="The operation was successfullly completed";
	String strResponseUrl=null;
    String basePath = request.getContextPath();
	if(request.getAttribute("successMessage") != null){
		successMessage = (String)request.getAttribute("successMessage");
	}
	
	if(request.getAttribute("responseUrl") != null){
		strResponseUrl =(String)request.getAttribute("responseUrl");		
		//strResponseUrl	= basePath+strResponseUrl;
		strResponseUrl = strResponseUrl.trim();
//		strResponseUrl = strResponseUrl.replaceAll("//","/");
	}else{
		//System.out.println("responseUrl");
	}
	System.out.println(strResponseUrl);
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
		<table width="80%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td align="left" class="tableheader">Success:</td>
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
							<html:button property="c_btnOK" value="  OK  " styleClass="sspbutton" onclick="document.forms[0].submit();"/>										
						</html:form>
					</td>
			</tr>
			
		</table>
		</td>
	</tr>
</table>