<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@ taglib uri="/struts-jquery-tags" prefix="s"%>


<%@include file="/WEB-INF/content/pd/calender/CommonCalendar.jsp"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="calender.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/calender" action="calender" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
              <s:hidden name="_method" value="put" />
             <s:token/>
            <div>
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="calenderList" key="calender.calenderList" id="calenderList" maxlength="100" cssClass="form-control focusElement" tabindex="1"/>
             	    <s:textarea name="description" key="calender.calenderDescription" id="calendardescription"  tabindex="2" maxLength="500" cssClass="form-control" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"/>
           	    </div>
           	    <div id="calendarDetailDiv">
                    <div class="col-xs-12 col-sm-12">
                        <table id='calenderDetailsTable'  class="table table-blue table-bordered">
                            <caption class="caption-header">
                            <s:text name="calender.details" />
                                <div align="right" class="display-btn">
                                    <span class="btn btn-group btn-group-xs defaultBtn" onclick="addCalenderRow();" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
                                </div>
                            </caption>
                            <thead>
								<th><s:text name="calender.details.name" /></th>
								<th><s:text name="calender.details.fromDate" /></th>
								<th><s:text name="calender.details.toDate" /></th>
								<th style="width: 35px;">&nbsp;</th>
							</thead>
                            <tbody>
                            <s:iterator value="calenderDetails" status="i" var="calenderListBaseDns">
                                <tr name='calenderDetailRow'>
                                    <td><s:textfield value="%{#calenderListBaseDns.calenderName}"	name="calenderDetails[%{#i.count - 1}].calenderName" cssClass="form-control calendarName"  elementCssClass="col-xs-12"  maxlength="50" tabindex="4"/></td>
                                    <td><s:textfield value="%{#calenderListBaseDns.fromDate}"	name="calenderDetails[%{#i.count - 1}].fromDate" cssClass="form-control fromDate"  elementCssClass="col-xs-12" readonly="true" maxlength="50" tabindex="5" /></td>
                                    <td><s:textfield value="%{#calenderListBaseDns.toDate}"	name="calenderDetails[%{#i.count - 1}].toDate" cssClass="form-control toDate"  elementCssClass="col-xs-12" readonly="true" maxlength="50" tabindex="6" /></td>
                                    <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                                </tr>
                            </s:iterator>
                            </tbody>
                            
                        </table>
                        <div class="col-xs-12" id="generalError"></div>
                    </div>
                </div>
               
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" class="btn btn-primary btn-sm"  id="btnSave" role="submit" formaction="${pageContext.request.contextPath}/pd/calender/calender/${id}" tabindex="8" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/calender/calender/${id}'" tabindex="9"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">
	
	$( ".toDate , .fromDate" ).datepicker();
	$.datepicker.setDefaults({
		changeMonth :'true',
		changeYear :'true',
	});
		
	
  var fromDates = $(".fromDate");
  for(var index = 0 ; index < fromDates.length ; index++){
      var fromDatesDom = fromDates[index];
      var fromDatesText = fromDatesDom.value;
      fromDatesDom.attributes.getNamedItem("value").value = fromDatesText.split(" ")[0];
      fromDatesDom.value = fromDatesText.split(" ")[0];
  }
	
  var toDates = $(".toDate");
  for(var index = 0 ; index < toDates.length ; index++){
      var toDatesDom = toDates[index];
      var toDatesText = toDatesDom.value;
      toDatesDom.attributes.getNamedItem("value").value = toDatesText.split(" ")[0];
      toDatesDom.value = toDatesText.split(" ")[0];
  }		

/* $.datepicker.setDefaults({
    changeMonth :'true',
    changeYear :'true',
});

$( ".toDate , .fromDate" ).datepicker(); */

function validateForm() {
	
	clearErrorMessages();
    var calendarDetailTableBodyLength = $("#calenderDetailsTable tbody tr").length;
    var isValidName = verifyUniquenessOnSubmit('calenderList','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.calender.CalenderData','','calenderList');
    
    if (isValidName == false) {
        return false;
    }else if(calendarDetailTableBodyLength < 1) {
        $("#generalError").addClass("bg-danger");
        $("#generalError").text("<s:text name='error.calender.calendardetail'/>");
        return false;
    } 
    else if(calendarDetailTableBodyLength >= 1) {
    	return  isValidCalendarDetailValue = validateChildTable("#calenderDetailsTable tbody tr")
    }
}

	var i = document.getElementsByName("calenderDetailRow").length;
	function addCalenderRow(){
	  createTable(i);
	  i++;
	}
</script>

