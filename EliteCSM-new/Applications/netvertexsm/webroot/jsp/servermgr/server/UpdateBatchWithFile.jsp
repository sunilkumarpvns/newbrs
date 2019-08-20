



<script language="javascript">
	function validate() {
		if(document.forms[0].batchId.value == "") {
			alert("Batch id should not be empty");
			return;
		}
		if(document.forms[0].batchFile.value == "") {
			alert("Must give the batch file location");
			return;
		}
		document.forms[0].checkAction.value="UploadFile";
		document.forms[0].submit();
	}
	
	function clearData() {
		document.forms[0].batchId.value = "";
		document.forms[0].batchFile.value = "";
	}
</script>

<html:form action="/updateBatchWithFile" enctype="multipart/form-data">	
 <html:hidden styleId="" property="checkAction" />
    <html:hidden styleId="" property="serverId" />
 	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    	<tr> 
      		<td valign="top" align="right"> 
      		
        		<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
				  <tr>
				  	<td colspan="2">&nbsp;</td>
				  </tr>
				  
				  <tr> 
					<td class="tblheader-bold" colspan="2">Update Batch</td>
				  </tr>
				  
				  <tr>
				  	<td colspan="2">&nbsp;</td>
				  </tr>
				  <tr>
					<td align="left" class="labeltext" valign="top" width="5%"><strong>Batch Id</strong></td>
					<td align="left" class="labeltext" valign="top" width="18%" ><html:text size="20" styleId="" property="batchId"/><strong style="color=red;">*</strong></td>
				  </tr>
				  <tr>
					<td align="left" class="labeltext" valign="top" width="5%"><strong>Upload File</strong></td>
					<td align="left" class="labeltext" valign="top" width="18%" ><html:file styleId="" property="batchFile" size="40" /></td>
				  </tr>
				  
				  <tr>
					<td align="left" class="labeltext" valign="top" width="5%">&nbsp;</td>
					<td align="left" class="labeltext" valign="top" width="18%" >
						<input type="button" name="c_btnupladbatch" id="c_btnupladbatch" value="Upload" class="light-btn" onclick="validate()"/>
<%--						<input type="button" name="c_btnreset" id="c_btnreset" value="Reset" class="light-btn" onclick="clearData()"/>--%>
					</td>
				  </tr>
				</table>
			</td>
		</tr>		  
 	</table>
</html:form>
