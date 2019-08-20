<%--
  Created by IntelliJ IDEA.
  User: aditya
  Date: 11/5/16
  Time: 2:02 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page import="com.elitecore.netvertexsm.web.servergroup.form.ServerInstanceGroupForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<link href="<%=basePath%>/chosen/css/chosen.css" rel="stylesheet">
<script src="<%=basePath%>/chosen/js/chosen.jquery.js"></script>

<%
	ServerInstanceGroupForm serverInstanceGroupForm = (ServerInstanceGroupForm)request.getAttribute("serverInstanceGroupForm");
%>
<script language="javascript">
  $(document).ready(function () {
	  setTitle('<bean:message  bundle="serverGroupDataMgmtResources" key="group.title"/>');
    document.forms[0].name.focus();
    <%
	for(String string : serverInstanceGroupForm.getGroupNameList()){%>
			$("#accessGroups option").each(function(){
			    var temp = '<%=string%>';
			    if(this.value == temp){
			    	$(this).attr("selected","selected");
			    }
			});
	<%}
%>
    $(".chosen").children().css('background-color','#FFFACD');
    $(".chosen").chosen();

  });

  var isValidName;
  function validate(){
    if(isNull(document.forms[0].name.value)){
      alert('Group Name must be specified');
      document.forms[0].name.focus();
      return false;
    }else if(!isValidName) {
      alert('Enter Valid Group Name');
      document.forms[0].name.focus();
      return false;
    }else{
      document.forms[0].submit();
      return true;
    }
  }
  function verifyFormat (){
    var searchName = document.getElementById("name").value;
    callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.SERVER_GROUP_MANAGEMENT%>',searchName:searchName,mode:'update',id:''},'verifyNameDiv');
  }
  function verifyName() {
    var searchName = document.getElementById("name").value;
    isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.SERVER_GROUP_MANAGEMENT%>',searchName:searchName,mode:'update',id:''},'verifyNameDiv');
  }

</script>

<html:form action="/serverGroupManagement.do?method=update" onsubmit="return validate();">
  <html:hidden styleId="id" property="id"/>
  <html:hidden property="orderNo" styleId="orderNo"/>
  <table cellpadding="0" cellspacing="0" border="0" width="100%" >
    <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
    <tr>
      <td width="10">&nbsp;</td>
      <td width="100%" colspan="2" valign="top" class="box">
        <table cellSpacing="0" cellPadding="0" width="100%" border="0">
          <tr>
            <td class="table-header" colspan="5">
              <bean:message bundle="serverGroupDataMgmtResources" key="group.update.title"/>
            </td>
          </tr>
          <tr>
            <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3">
              <table width="97%" id="c_tblCrossProductList" align="right" border="0" >

                <tr>
                  <td align="left" class="labeltext" valign="top" width="10%">
                    <bean:message bundle="serverGroupDataMgmtResources" key="group.name" />
                  </td>
                  <sm:nvNameField size="30" maxLength="50"  id="name" name="name" value="${serverInstanceGroupForm.name}" >
                  </sm:nvNameField>
                </tr>
                <tr>
                  <td align="left" class="labeltext" valign="top" width="10%">
                    <bean:message bundle="serverGroupDataMgmtResources" key="group.accessgroups" /></td>
                  <td align="left" class="labeltext" valign="top" width="32%">
                    <html:select styleId="accessGroups" property="accessGroups" tabindex="2" styleClass="chosen" multiple="multiple" style="width:340px" >
                      <html:optionsCollection name="staffBelongingGroupList" value="id" label="name"/>
                    </html:select>
                  </td>
                </tr>
                <tr>
                  <td class="btns-td" valign="middle">&nbsp;</td>
                  <td class="btns-td" valign="middle" colspan="2">
                    <input type="button" name="c_btnCreate" value="   Update   " class="light-btn" onclick="return validate()" tabindex="8">
                    <input type="button" align="left" value=" Cancel " tabindex="8" class="light-btn" onclick="javascript:location.href='<%=basePath%>/serverGroupManagement.do?method=initSearch'"/>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
          </tr>
        </table>
      </td>
    </tr>
    <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
  </table>
</html:form> 

