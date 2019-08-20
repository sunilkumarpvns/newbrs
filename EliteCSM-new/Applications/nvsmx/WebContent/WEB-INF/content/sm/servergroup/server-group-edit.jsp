
<script type="text/javascript">

    $(function(){
        $( ".select2" ).select2();
    });

  var thisServerGroupId ;
  function openServerGroupUpdateDialogBox(serverGroupId,name,accessGroupIds,databaseDataId,serverGroupType,notifcationDataSourceId) {

    clearErrorMessages();

    thisServerGroupId = serverGroupId;

    $("#serverGroupNameUpdate").val(name);
    $("#serverGroupId").val(serverGroupId);
    
    if(serverGroupType === "<s:property value='@com.elitecore.corenetvertex.constants.ServerGroups@OFFLINE_RNC.getValue()'/>"){
   	 $("#dataSourceIdUpdate").hide();
     $("#notificationDataSourceIdUpdate").hide();
    }
    else{
   	$("#dataSourceIdUpdate").show();
    $("#notificationDataSourceIdUpdate").show();
   	$("#databaseDatasourceId").val(databaseDataId);
    }
    
    $("#serverGroupTypeIdUpdate").val(serverGroupType);
    $("#databaseDatasourceId").val(databaseDataId);
    $("#notificationDatabaseDatasourceId").val(notifcationDataSourceId);


    var groupIdsArray = accessGroupIds.trim().split(/\s*,\s*/);
    $("#groupNamesUpdate").select2().val(groupIdsArray).trigger("change");

    $('#serverGroupUpdateForm').attr('action', "${pageContext.request.contextPath}/sm/servergroup/server-group/"+serverGroupId)
    $("#serverGroupUpdateDialog").modal('show');
    $('#btnUpdate').unbind('click');
    $("#btnUpdate").on('click', function () {
      $("#serverGroupNameUpdate").modal('hide');
    });
    $('#serverGroupNameUpdate').focus();

  }

  function verifyUniquenessOfGroupName(){
      return verifyUniquenessOnSubmit('serverGroupNameUpdate','update',thisServerGroupId,'com.elitecore.corenetvertex.sm.servergroup.ServerGroupData','','') ;
  }

</script>

<s:form namespace="/sm/servergroup" action="server-group" id="serverGroupUpdateForm" method="post" cssClass="form-horizontal" validate="true" validator="verifyUniquenessOfGroupName()" >
  <s:token />
  <s:hidden name="_method" value="put" />
<div class="modal" id="serverGroupUpdateDialog" tabindex="-1" role="dialog" aria-labelledby="serviceDialog" aria-hidden="true" >

  <div class="modal-dialog" >

    <div class="modal-content" >

      <div class="modal-header" >
        <button type="button" class="close" data-dismiss="modal"
                aria-label="Close" onclick="clearDialog()">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title set-title" id="serviceDialogTitle">
          <s:text name="servergroup.update"/>
        </h4>
      </div>

      <div class="modal-body" style="padding-top: 15px !important; padding-bottom: 0px !important;" >
           <div class="row">
              <div class="col-xs-12">
                <s:textfield 	name="name"
                                key="servergroup.name"
                                maxlength="255"
                                id="serverGroupNameUpdate"
                                cssClass="form-control focusElement"
                                onblur="return verifyUniquenessOfGroupName();" />
                <s:hidden 	name="serverGroupId" id="serverGroupId" />
              </div>
            </div>
            <div class="row">
              <div class="col-xs-12">
                  <s:textfield name="serverGroupType" key="serverinstance.serverinstance.type"
                               cssClass="form-control"  id="serverGroupTypeIdUpdate"
                               readonly="true" />
				</div>
            </div>
            <div class="row">
              <div class="col-xs-12">
                <s:select 	name="groups"
                             value="groupValuesForUpdate"
                             key="servergroup.groups"
                             cssClass="form-control select2"
                             list="#session.staffBelongingGroups"
                             id="groupNamesUpdate" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" />
              </div>
            </div>
            <div class="row" id="dataSourceIdUpdate">
              <div class="col-xs-12">

                <s:select name="sessionDataSourceId"
                          id="databaseDatasourceId"
                          key="serverinstance.sessiondatasource"
                          cssClass="form-control"
                          listKey="id"
                          listValue="name"
                          list="databaseDatas"/>

              </div>
            </div>
          <div class="row" id="notificationDataSourceIdUpdate">
              <div class="col-xs-12">

                  <s:select name="notificationDataSourceId"
                            id="notificationDatabaseDatasourceId"
                            key="servergroup.notification.datasource"
                            cssClass="form-control"
                            listKey="id"
                            listValue="name"
                            headerKey=""
                            headerValue="SELECT"
                            list="databaseDatas"/>

              </div>
          </div>
      </div>

      <div class="modal-footer">
          <s:submit type="button" role="button" cssClass="btn btn-primary" >
              <span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
          </s:submit>
        <button type="button" class="btn btn-primary" data-dismiss="modal"id="btnCancel" onclick="clearDialog()">Cancel</button>
      </div>

    </div>
  </div>
</div>
</s:form>
<script type="text/javascript">
	$(function() {
		onServerGroupTypeChange("serverGroupTypeIdUpdate", "dataSourceIdUpdate");
        onServerGroupTypeChange("serverGroupTypeIdUpdate", "notificationDataSourceIdUpdate");
	});
</script>