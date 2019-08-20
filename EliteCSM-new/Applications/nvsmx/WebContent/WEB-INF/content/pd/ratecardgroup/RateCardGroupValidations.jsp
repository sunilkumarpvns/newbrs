<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<script type="text/javascript">

function createChildTable(tableId, listName, rowNum){
    var tableRow= "<tr name='rateCardGroupRow'>"+
            "<td><input class='form-control' name='"+ listName +"["+rowNum+"].dayOfWeek'  type='text' maxlength='10'/></td>"+
            "<td><input class='form-control' name='"+ listName +"["+rowNum+"].timePeriod'  type='text' maxlength='50' /></td>"+
            "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
            "</tr>";
    $(tableRow).appendTo('#' + tableId );
    i++;
}

  
function validate(method){

	    var isValidName = false;
	    if(method == 'update'){
		     isValidName = verifyUniquenessOnSubmit('rateCardGroupName','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData','','');
	    }
	    else{
		     isValidName = verifyUniquenessOnSubmit('rateCardGroupName','create','','com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData','','');

	    }
	    
	    return isValidName;
	    
}
</script>

