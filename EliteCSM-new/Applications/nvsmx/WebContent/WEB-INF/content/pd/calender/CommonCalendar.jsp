<%@taglib uri="/struts-tags/ec" prefix="s"%>

<script type="text/javascript">

function  validateChildTable(domId){
	var isValidCalendarDetailValue = true
    $(domId).each(function () {
        var calendarName =$(this).children().find('.calendarName');
        var toDate =$(this).children().find('.toDate');
        var fromDate =$(this).children().find('.fromDate');
        
        if(isNullOrEmpty(calendarName.val())){
            setErrorOnElement(calendarName,"<s:text name='error.empty.calendar'/>");
            isValidCalendarDetailValue = false;
        }
        
        if(isNullOrEmpty(fromDate.val())){
            setErrorOnElement(fromDate,"<s:text name='error.empty.fromdate'/>");
            isValidCalendarDetailValue = false;
        }
        
        if(isNullOrEmpty(toDate.val())){
            setErrorOnElement(toDate,"<s:text name='error.empty.todate'/>");
            isValidCalendarDetailValue = false;
        }
        
    });
	return isValidCalendarDetailValue;
}



function createTable(rowNum){
    var tableRow= "<tr name='calendarDetailRow'>"+
            "<td><input class='col-xs-12 col-sm-12 calendarName'  name='calenderDetails["+rowNum+"].calenderName'  type='text'></td>"+
            "<td><input class='col-xs-12 col-sm-12 fromDate'   name='calenderDetails["+rowNum+"].fromDate' readonly='readonly'/></td>"+
            "<td><input class='col-xs-12 col-sm-12 toDate' name='calenderDetails["+rowNum+"].toDate' readonly='readonly'/></td>"+
            "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
            "</tr>";
    $(tableRow).appendTo('#calenderDetailsTable');
	$( ".toDate , .fromDate" ).datepicker();
	}

	$.datepicker.setDefaults({
		changeMonth :'true',
		changeYear :'true',
		dateFormat : 'dd-M-yy'
	});
</script>