<div class="modal fade" id="addPccRule-${id}" tabindex="-1" role="dialog" aria-labelledby="addSubscriptionLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="clearDialog();">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title" id="addSubscriptionLabel"><s:text name="qosprofile.addpccrule.global.modal.title"/></h4>
      </div>
        <s:set value="id" var="qosprofiledetailid"/>
        <s:form namespace="/" action="policydesigner/qos/QosProfile/attachPccRule" id="subscribeForm" cssClass="form-vertical form-group-sm " labelCssClass="col-xs-4" elementCssClass="col-xs-8" validate="true" validator="validateForm()">
          <s:token />
          <s:set value="%{@com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@GLOBAL.name()}" var="scope"/>
        <s:hidden name="qosProfileDetailId" value="%{qosprofiledetailid}"/>
            <s:hidden name="groupIds" value="%{qosProfile.pkgData.groups}" />
            <s:hidden name="entityOldGroups" value="%{qosProfile.pkgData.groups}" />
            <s:hidden name="qosProfileId" value="%{qosProfile.id}" />

            <div class="modal-body" >
          <nv:dataTable
                  id="PCCRuleDataGlobalSearch-${id}"
                  actionUrl="/genericSearch/policydesigner/pccrule/PccRule/searchData?scope=${scope}&qosProfileDetailId=${qosprofiledetailid}&isCallFromModal=true&groupIds=${qosProfile.pkgData.groups}"
                  beanType="com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData"
                  width="100%"
                  showPagination="false"
                  showInfo="false"
                  cssClass="table table-blue"
                  showFilter="true">
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  class='selectAllCheckBox' />"  beanProperty="id"  style="width:5px !important;" />
            <nv:dataTableColumn title="PCCRule" beanProperty="name"
                                tdCssClass="text-left text-middle word-break"
                                hrefurl="${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/view?pccRuleId=id"
                                tdStyle="width:200px" />
            <nv:dataTableColumn title="Service"
                                beanProperty="dataServiceTypeData.name"
                                tdCssClass="text-left text-middle word-break" tdStyle="width:100px" />
            <nv:dataTableColumn title="Type"
                                beanProperty="type"
                                tdCssClass="text-left text-middle word-break" tdStyle="width:100px" />
            <nv:dataTableColumn title="Monitoring Key"
                                beanProperty="monitoringKey"
                                tdCssClass="text-left text-middle word-break" tdStyle="width:200px" />
          </nv:dataTable>
       </div>
          <div class="col-xs-12">
              <div class="col-xs-12 generalError" ></div>
          </div>

        <div class="modal-footer">
          <s:submit cssClass="btn btn-primary" name="Add" value="Add" type="button" />
          <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="clearDialog()" >Close</button>
        </div>
      </s:form>
    </div></div></div>

      <script type="text/javascript">

          function clearDialog(){
              $('table').find('input:checkbox').prop('checked', false).closest('tr').removeClass('selected');
              removeGeneralErrors();
          }

          function removeGeneralErrors(){
              $(".generalError").removeClass("bg-danger");
              $(".generalError").text("");
          }
      function validateForm(){
          var selectVar = getSelectedValues();
    		if(selectVar == false){
    	        $(".generalError").addClass("bg-danger");
                $(".generalError").text("At least select one PCC Rule for Add");
                return false;
    	    }else{
                var serviceTypeSet = [];
                var flag = true;
                $('[name = ids]:checked').each(function(){
                    var dataServiceType = $(this).closest('tr').children().eq(2).text().trim();
                    if($.inArray(dataServiceType,serviceTypeSet) == -1){
                        serviceTypeSet.push(dataServiceType);
                    }else{
                        $(".generalError").addClass("bg-danger");
                        $(".generalError").text("Global PCCRule with same Data Service Type can not be attached");
                        flag = false;
                        return;
                    }
                });
                if(flag == false){
                    return flag;
                }
                removeGeneralErrors();
                return true;

            }
      }
      
      function getSelectedValues(){
    		var selectedData = false;
    		var selectedDatas = document.getElementsByName("ids");
    		for (var i=0; i < selectedDatas.length; i++){
    			if(selectedDatas[i].name == 'ids'){
    				if(selectedDatas[i].checked == true){
    					selectedData = true;
                        break;
    				}
    			}
    		}
    		return selectedData;
    	}
          function wrapTable(tableId,fupLevel,id){
              $('#'+tableId).wrap("<div style='position:relative;overflow:auto;height:400px;'/>");
              sameServiceTypeCanNotBeAttached(tableId,fupLevel);
              $('#addPccRule-'+id).modal('show');
          }
          /**
           * Used when global pccrule with same service type is already added in qos profile detail
           * @param tableId
           * @param fupLevel
           */
          function sameServiceTypeCanNotBeAttached(tableId,fupLevel){
              var attachServiceType = [];
              $("#qosProfileDetail-"+fupLevel + " tbody tr").each(function(){
                  var dataServiceType = $(this).children().eq(2).text().trim();
                  attachServiceType.push(dataServiceType);
              });

              $('#'+tableId +' tbody tr').each(function(){
                  var dataServiceType = $(this).children().eq(2).text().trim();
                  if($.inArray(dataServiceType,attachServiceType) !== -1){
                      $(this).children().eq(0).children().eq(1).attr('disabled','true');
                      console.log(dataServiceType);
                  }
              });

          }
      </script>
