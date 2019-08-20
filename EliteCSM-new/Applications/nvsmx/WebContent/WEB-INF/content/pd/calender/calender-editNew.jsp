<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="calender.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/calender" action="calender" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:token/>
            <div>
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="calenderList" key="calender.calenderList" id="calenderList" cssClass="form-control focusElement" tabindex="1" maxLength="100"/>
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
                            <th><s:text name="calender.details.name"/></th>
                            <th><s:text name="calender.details.fromDate"/></th>
                            <th><s:text name="calender.details.toDate"/></th>
                            <th style="width:35px;">&nbsp;</th>                           
                                    </thead>
                            
                        </table>
                        <div class="col-xs-12" id="generalError"></div>
                    </div>
                </div>
               
                <div class="row">
                    <div class="col-xs-12" align="center">
                         <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="4" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/calender/calender'" tabindex="5"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">

function validateForm() {
	clearErrorMessages();
    var calendarDetailTableBodyLength = $("#calenderDetailsTable tbody tr").length;
    var isValidName = verifyUniquenessOnSubmit('calenderList','create','','com.elitecore.corenetvertex.pd.calender.CalenderData','','calenderList');
    if (isValidName == false) {
        return false;
    } 
    else if(calendarDetailTableBodyLength < 1) {
        $("#generalError").addClass("bg-danger");
        $("#generalError").text("<s:text name='error.calender.calendardetail'/>");
        return false;
    } 
    else if(calendarDetailTableBodyLength >= 1) {
    	return validateChildTable("#calenderDetailsTable tbody tr");
    }
 }
	var i = document.getElementsByName("calendarDetailRow").length;
	function addCalenderRow(){
	  createTable(i);
	  i++;
	}

</script>
<%@include file="/WEB-INF/content/pd/calender/CommonCalendar.jsp"%>
