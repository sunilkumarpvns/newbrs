
/**
	*Function for trimming white spaces from string.
	*Usage:	strVar = strVar.trim();
*/
function trim(strValue)
{
	return strValue.replace(/(^\s*)|(\s*$)/g, "");
}

/**
 * 
 * @param caller
 * @param headerName - Header of Dialog
 * @param title - Key name of Title of Help Text
 * @param helpDescription - Key name of Help Description
 * @param tip - Key name of tip text shown in dialog
 * @param note - Key name of note message shown in dialog
 * @param caution - Key name of caution message shown in dialog
 * @param example - Key name of example message shown in dialog
 */
function openHelp(caller, headerName, title, helpDescription, tip, note, caution, example){
	$(caller).css({'background-color':'#015198'});
	var helpContent = '';
	var helpPopup = '';
	if(title!=null && title.lenth>0){		
		helpContent += ("<b>"+title+"</b><br/>");
	}
	helpContent += (helpDescription + "<br/>");
	helpPopup +=("<table width='100%' cellspacing='0' cellpadding='0' border='0'>");
	
	if(helpDescription != null && !(trim(helpDescription)=='')){
		var helpText = "<tr>"+
					   "<td width='5%' valign='top' style='padding-bottom:10px;padding-right:10px;' align='right'>" +
					   "<img class='desc-class' src='images/images.png' height='20' width='20' title='Description'></img></td>"+
					   "<td class='help-title-css' valign='top'> Description </td>"+
					   "</tr>"+
					   "<tr><td style='padding-bottom:10px;padding-right:10px;'>&nbsp;</td><td class='help-desc-css' valign='top' style='padding-bottom:10px;padding-right:10px;'>"+helpDescription+"</td>";
		helpPopup +=(helpText);
	}
	
	if(example!=null && !(trim(example)=='') ){	
		var helpText = "<tr>"+
		   "<td width='5%' valign='top' style='padding-bottom:10px;padding-right:10px;' align='right'>" +
		   "<img class='desc-class' src='images/example.png' height='20' width='20' title='Example'></img></td>"+
		   "<td class='help-title-css' valign='top'> Example </td>"+
		   "</tr>"+
		   "<tr><td style='padding-bottom:10px;padding-right:10px;'>&nbsp;</td><td class='help-desc-css' valign='top' style='padding-bottom:10px;padding-right:10px;'>"+example+"</td>";
		helpPopup +=(helpText);
	}
	
	if(tip!=null && !(trim(tip)=='')){	
		var helpText = "<tr>"+
		   "<td width='5%' valign='top' style='padding-bottom:10px;padding-right:10px;' align='right'>" +
		   "<img class='desc-class' src='images/tips.png' height='20' width='20' title='Tips'></img></td>"+
		   "<td class='help-title-css' valign='top'> Tips </td>"+
		   "</tr>"+
		   "<tr><td style='padding-bottom:10px;padding-right:10px;'>&nbsp;</td><td class='help-desc-css' valign='top' style='padding-bottom:10px;padding-right:10px;'>"+tip+"</td>";
		helpPopup +=(helpText);
	}
	
	if(caution!=null && !(trim(caution)=='') ){	
		var helpText = "<tr>"+
		   "<td width='5%' valign='top' style='padding-bottom:10px;padding-right:10px;' align='right'>" +
		   "<img class='desc-class' src='images/configuration.png' height='20' width='20' title='Caution'></img></td>"+
		   "<td class='help-title-css' valign='top'> Caution </td>"+
		   "</tr>"+
		   "<tr><td style='padding-bottom:10px;padding-right:10px;'>&nbsp;</td><td class='help-desc-css' valign='top' style='padding-bottom:10px;padding-right:10px;'>"+caution+"</td>";
		helpPopup +=(helpText);
	}
	
	if(note!=null && !(trim(note)=='') ){	
		var helpText = "<tr>"+
		   "<td width='5%' valign='top' style='padding-bottom:10px;padding-right:10px;' align='right'>" +
		   "<img class='desc-class' src='images/Sticky_Notes.png' height='20' width='20' title='Note'></img></td>"+
		   "<td class='help-title-css' valign='top'> Note </td>"+
		   "</tr>"+
		   "<tr><td style='padding-bottom:10px;padding-right:10px;'>&nbsp;</td><td class='help-desc-css' valign='top' style='padding-bottom:10px;padding-right:10px;'>"+note+"</td>";
		helpPopup +=(helpText);
	}
	helpPopup +=("</table>");
	
	if(typeof $('#helpDialogDiv') !== undefined &&  $('#helpDialogDiv').length > 0){
		$('#helpDialogDiv').remove();
	}
	
	$('<div/>', {
	    id: "helpDialogDiv"
	}).appendTo(document.body);

	
	$("#helpDialogDiv").html(helpPopup);
	$("#helpDialogDiv").dialog({
		modal: false,
		autoOpen: false,
		maxHeight : 550,
		width : 550,
		position : 'top',
		title : headerName,
		close : function(){
			$("#helpDialogDiv").remove();
			$(caller).css({'background-color':'#2369A6'});
		}
	}).css("font-size", "12px");
	
	/*if($("#helpDialogDiv").dialog("isOpen")){
		$("#helpDialogDiv").dialog("option","title",headerName);
		$("#helpDialogDiv").html(helpContent);
	}*/
	$("#helpDialogDiv").dialog("open");
	
	/*$(document).mouseup(function (e)
	{
	    var container = $("#helpDialogDiv");

	    if (!container.is(e.target) // if the target of the click isn't the container...
	        && container.has(e.target).length === 0) // ... nor a descendant of the container
	    {
	        container.remove();
	    }
	});
*/
	}
	
function setTimeoutHandle(){
	return setTimeout(function(){
        		$('#helpDiv').dialog('close');                
	  		}, 10000);
}