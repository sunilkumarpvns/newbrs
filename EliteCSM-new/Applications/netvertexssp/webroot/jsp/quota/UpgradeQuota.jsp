<script type="text/JavaScript">
function showPolicy(obj){
	if(obj.value==-1){
	document.getElementById('policy').innerHTML = '';
	return;
	}
	
	document.getElementById('policy').innerHTML = document.getElementById(obj.value).innerHTML;
}

function showPolicyByService(obj){
	document.getElementById('policy').innerHTML = document.getElementById(obj).innerHTML;
}
function doFAP(obj, form){
	conf = confirm('Quota will not be upgraded for '+form.type.value+' service, Do you still want to continue?'); 
	if(conf){
		form.action = '<%= request.getContextPath() %>/servlet/SSSCServlet?actionMode=18';
		form.submit();
	}
}

function validate(obj){
	isChecked = false;
	var name;
	if(!isNaN(obj.policies.length)){
	for(i=0;i<obj.policies.length;i++){
		if(obj.policies[i].checked){
			name = (obj.policies[i].value);
			isChecked = true;
			break;
		}
	}
	} else {
		if(obj.policies.checked){
		name = obj.policies.value;
			isChecked = true;
		}
	}
	if(isChecked){
		if(confirm('Do you want to apply '+name+" Quota for "+obj.type.value+" service?")){
			return true;
		} else{
			return false;
		}
		
	} else {
		alert("Please Select Quota For "+obj.type.value+" Service");
		return false;
	}
}

</script>

<%--div id="<%=serviceValue.get(key) %>" >
                
                <form method="post" name="<%=(key) %>_form" action="<%=request.getContextPath() %>/servlet/SSSCServlet?actionMode=17" onsubmit="return validate(this);">
                <input type="hidden" name="type" value="<%=serviceValue.get(key) %>"/>
                <input type="hidden" name="srid" value="<%=(key) %>"/>
                <fieldset>
                <legend>Select Quota for <%=serviceValue.get(key) %> Service</legend>
                <table width="100%">
                <tr><td width="92%">
                <table>
               
                <tr class="blueheader">
                  	<td width="100%"></td>
                  	<td></td>
                </tr>
                <tr>
	                <td>&nbsp;</td>
	                <td>&nbsp;</td>
                </tr>
                <%
                for(PolicyObject pOb : po){ %>
                <tr>
                <td class="blueheader" width="10%">
                <label for="<%=pOb.getPolicyName() %>">
                <input type="radio" id="<%=pOb.getPolicyName() %>" name="policies" value="<%=pOb.getPolicyName() %>" onchange="//validate(this)" />
                <%=pOb.getPolicyName() %>
                </label>
                
                 </td>
                <td class="blueheader">&nbsp;</td>
                </tr>
                <%}
                %>
                 <tr>
	                <td>&nbsp;</td>
	                <td>&nbsp;</td>
                </tr>
                 <tr>
                    <td width="100%" class="blueheader" align="center">
	                <input type="button" id="<%=serviceValue.get(key) %>_dont_update" value="Do Not Update Quota" onclick="//doFAP(this, form);"/>
	                <input type="submit" id="<%=serviceValue.get(key) %>_update" value="Update Quota" />
	                <td>
	                <td>&nbsp;</td>
	                
                </tr>
                </table>
                </td></tr>
                </table>
                </fieldset>
                </form>
                </div>
                <%
                }}}}--%>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="35"
			style="border:#172734 solid 1px; background-image:url(<%=request.getContextPath() %>/images/menu_bkgd.jpg);">
		<table width="95%" border="0" align="center" cellpadding="0"
			cellspacing="0">
			<tr>
				<td width="50%" class="whiteheader">Upgrade Quota</td>
				<td align="right" class="smalltext">&nbsp;</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td height="15"></td>
	</tr>
	<tr>
		<td bgcolor="#eaeaea" height="100%">
			<table width="92%" border="0" align="center" cellpadding="0" cellspacing="0">
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="blueheader">Select Service</td>
				<td class="blueheader">
			    <select name="services" onchange="showPolicy(this);">
					<option name="select" value="-1">--- Please Select Service ---</option>
					<option name="servicetype" value="HTTPS"> HTTPS </option>
					<option name="servicetype" value="Net Admin"> Net Admin </option>
					<option name="servicetype" value="Other"> Other </option>
					
				</select>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>

		</table>
		<table width="100%">
			<tr>
				<td width="100%">
				<div id="policy"></div>
				</td>
				<td>&nbsp;</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
