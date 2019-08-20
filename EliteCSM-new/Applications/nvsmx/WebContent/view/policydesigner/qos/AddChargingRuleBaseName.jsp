<div class="modal fade" id="addChargingRuleBaseName-${id}" tabindex="-1" role="dialog" aria-labelledby="addChargingRuleBaseNameLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="clearCRBNDialog();">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title" id="addSubscriptionLabel1">Add Charging Rule Base Name</h4>
      </div>
        <s:set value="id" var="qosprofiledetailid"/>
        <s:form namespace="/" action="policydesigner/qos/QosProfile/attachChargingRuleBaseName" id="subscribeForm1" cssClass="form-vertical form-group-sm " labelCssClass="col-xs-4" elementCssClass="col-xs-8" validate="true" validator="validateCRBNForm()">
          <s:token />

            <s:hidden name="qosProfileDetailId" value="%{qosprofiledetailid}"/>
            <s:hidden name="groupIds" value="%{qosProfile.pkgData.groups}" />
            <s:hidden name="qosProfileId" value="%{qosProfile.id}" />
            <s:hidden name="entityOldGroups" value="%{qosProfile.pkgData.groups}" />

            <div class="modal-body" >

                <nv:dataTable
                        id="ChargingRuleBaseNameSearch-${id}"
                        beanType="com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData"
                        actionUrl="/genericSearch/policydesigner/chargingrulebasename/ChargingRuleBaseName/searchData?groupIds=${qosProfile.pkgData.groups}"
                        showPagination="false"
                        cssClass="table table-blue"
                        width="100%"
                        showInfo="false"
                        showFilter="true" >

                    <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll'  class='selectAllCheckBox' />"  beanProperty="id"  style="width:5px !important;" />
                    <nv:dataTableColumn title="Name"	beanProperty="name"	hrefurl="${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/view?chargingRuleBaseNameId=id" sortable="true" style="width:200px;"/>

                 </nv:dataTable>
           </div>

          <div class="col-xs-12">
              <div class="col-xs-12 generalError" ></div>
          </div>

        <div class="modal-footer">
          <s:submit cssClass="btn btn-primary" name="Add" value="Add" type="button" />
          <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="clearCRBNDialog()" >Close</button>
        </div>
      </s:form>
    </div></div></div>

<script type="text/javascript">

    function clearCRBNDialog(){
        console.log("called clearCRBNDialog()");
        $('table').find('input:checkbox').prop('checked', false).closest('tr').removeClass('selected');
        removeCRBNGeneralErrors();
    }

    function removeCRBNGeneralErrors(){
        console.log("called removeCRBNGeneralErrors()");
        $(".generalError").removeClass("bg-danger");
        $(".generalError").text("");
    }
    function validateCRBNForm(){
        console.log("called validateCRBNForm()");
        var selectVar = getCRBNSelectedValues();
        if(selectVar == false){
            $(".generalError").addClass("bg-danger");
            $(".generalError").text("At least select one Charging-Rule-Base-Name for Add");
            return false;
        }else{

            removeCRBNGeneralErrors();
            return true;
            //return true;

        }
    }

    function getCRBNSelectedValues(){
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
    function wrapCRBNTable(tableId,fupLevel){
        console.log("called wrapCRBNTable()");
        $('#'+tableId).wrap("<div style='position:relative;overflow:auto;height:400px;'/>");
        sameServiceTypeCanNotBeAttached1(tableId,fupLevel);
    }
    /**
     * Used when global pccrule with same service type is already added in qos profile detail
     * @param tableId
     * @param fupLevel
     */
    function sameServiceTypeCanNotBeAttached1(tableId,fupLevel){
        console.log("called sameServiceTypeCanNotBeAttached1()");
        var attachChargingRule = [];
        $("#qosProfileDetail-"+fupLevel + " tbody tr").each(function(){
            var dataServiceType = $(this).children().eq(1).text().trim();
            var chargingRule = $(this).children().eq(0).text().trim();

            if(dataServiceType == "CRBN") {
                attachChargingRule.push(chargingRule);

            }
        });

        $('#'+tableId +' tbody tr').each(function(){
            var chargingRuleToAttach = $(this).children().eq(1).text().trim();
            if($.inArray(chargingRuleToAttach,attachChargingRule) !== -1){
                $(this).children().eq(0).children().eq(1).attr('disabled','true');
                console.log("charging Rule Already Attach: "+chargingRuleToAttach);
            }
        });

    }
</script>
