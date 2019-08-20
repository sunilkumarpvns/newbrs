<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<script type="text/javascript">

$(function(){
    $( ".select2" ).select2();
});

</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="pg.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/partnergroup" action="partner-group" id="partnerGroupForm" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div>
            	<div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="pg.name" id="pgName" cssClass="form-control focusElement" maxlength="100" tabindex="1"/>
                    <s:textarea name="description" key="pg.description" id="pgDescription" cssClass="form-control" rows="2" maxlength="2000" tabindex="2"/>
                    <s:select name="lobId" key="pg.lobgroup" id = "pgLobId" headerValue="Select Lob Group" list="lobDataList" listKey="id" listValue="name" cssClass="form-control" tabindex="3"/>
                    <s:select name="status" key="pg.status" id="pgStatus" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" tabindex="4"/>
                    <s:select 	name="groups" value="groupValuesForUpdate"  id="groupNamesUpdate"	key="pg.server.groups"    cssClass="form-control select2"	list="#session.staffBelongingGroups" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" tabindex="5"/>
                </div>

				<div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" id="btnSave" class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/pd/partnergroup/partner-group/${id}" tabindex="6" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" id="btnCancel" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/partnergroup/partner-group/${id}'" tabindex="7"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">
 
  function validateForm() {
 	
	  return verifyUniquenessOnSubmit('pgName','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.partnergroup.PartnerGroupData','','');
 } 
 </script>