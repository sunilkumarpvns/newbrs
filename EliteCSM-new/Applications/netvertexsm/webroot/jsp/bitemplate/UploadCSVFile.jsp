
<script>
function validateCreate(){
	if(isNull(document.forms[0].csvFile.value)){
		alert('CSV file must be specified');
	}else{
		document.forms[0].submit();
	}
}
</script>
		 
<html:form action="/uploadFile" enctype="multipart/form-data">		 
	<html:hidden name="biTemplateForm" styleId="name" property="name" value='<%=request.getParameter("name")%>'/>
	<html:hidden name="biTemplateForm" styleId="biId" property="biId" value='<%=request.getParameter("id")%>'/>
		 <table width="100%" border="0" cellspacing="0" cellpadding="0" class="">
		    <tr> 
		      <td class="tblheader-bold" colspan="3"><bean:message bundle="biTemplateResources" key="bitemplate.upload.csvfile" /></td>
		    </tr>
		    <tr> 
		      <td  width="3%">&nbsp;</td>			
		      <td  width="97%">&nbsp;</td>
		    </tr>
		    
		    <tr> 
		      <td  width="3%">&nbsp;</td>			
		      <td valign="top" width="97%"> 
		        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >

		          <tr> 
		            <td class="labeltext" width="15%" height="20%"><bean:message bundle="biTemplateResources" key="bitemplate.csvfile" /></td>
		            <td class="labeltext" width="85%" height="20%">
				  		<html:file name="biTemplateForm" property="csvFile" size="40" style="font-family: Verdana; "/>			
		            </td>
		          </tr>
		          <tr> 
				      <td  width="3%">&nbsp;</td>			
				      <td  width="97%">&nbsp;</td>
				    </tr>
	               <tr> 
		                <td class="btns-td" valign="middle">&nbsp;</td>
	                    <td class="btns-td" valign="middle">
	                      	<input type="button" name="c_btnCreate" onclick="validateCreate()" value="   Upload   " class="light-btn">
                        	<input type="button" value=" Cancel " tabindex="29" class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchBITemplate.do?/>'" /></td>
		                </td>
    				</tr>

		        </table>
				</td>
		    </tr>
		
		</table>
</html:form>		
