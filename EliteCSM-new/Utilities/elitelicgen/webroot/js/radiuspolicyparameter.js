// JavaScript Document
nre= /^\d+$/;
var ParameterArray = new Array();
var DictionaryName = "";
var DictionaryId = "";
var ParameterName = "";
var ParameterId = "";
var RadiusName = "";
var RadiusId = "";
var OperatorName = "";
var OperatorId = "";
var Status = "";
var Key = "";
var Value = "";
var srcDictionaryID = "";
var srcDictParamID = "";
var srcAVPairAttName = "";
var srcDictComboID = "";
var	srcDictParamComboID = "";	
var	strReplyItemType = "";	
	
var popupOpen = false;
var totalRows =1;

function populateTable(rowAdd) //Read from the Data Structure and draw the table
{	
		TableObject = document.getElementById('PPTable');
		DeleteAllRows(rowAdd);	
		document.forms[0].count.value = ParameterArray.length;		
	    for(i=0;i<ParameterArray.length;i++)
		{
			DictionaryId=ParameterArray[i].dictionaryId;
			DictionaryName = ParameterArray[i].dictionaryName;
			ParameterName = ParameterArray[i].parameterName;
			ParameterId = ParameterArray[i].parameterId;
			RadiusName = ParameterArray[i].radiusName;
			RadiusId = ParameterArray[i].radiusId;
			OperatorName = ParameterArray[i].operatorName;
			OperatorId = ParameterArray[i].operatorId;
			Status = ParameterArray[i].status;
			Value = ParameterArray[i].value;
			Display = ParameterArray[i].display;
			srcDictionaryID = ParameterArray[i].srcDictionaryID;
			srcDictParamID  = ParameterArray[i].srcDictParamID;
			srcAVPairAttName = ParameterArray[i].srcAVPairAttName;
			srcDictComboID = ParameterArray[i].srcDictComboID;
			srcDictParamComboID = ParameterArray[i].srcDictParamComboID;			
			strReplyItemType = ParameterArray[i].strReplyItemType;
			addRows(i+1);
	  }
}

function addRows(index) //draws the content rows in the table
{
		mynewrow = TableObject.insertRow(index);
		
					
		

		
		//Sr. No.
		mynewrow.insertCell();
		mynewrow.cells(0).className="tblrows";
		mynewrow.cells(0).innerHTML=index+"."+"<input type='hidden' name='sr.no."+index+"' id='RCLTaxValue"+index+"' value='"+index+"'>";

		
		
		//Dictionary
		
		mynewrow.insertCell();
		mynewrow.cells(1).className="tblrows";
		mynewrow.cells(1).innerHTML=DictionaryName+"<input type='hidden' name='dictionary"+index+"' id='dictionary"+index+"' value='"+DictionaryId+"'>";

		// Parameter
		mynewrow.insertCell();
		mynewrow.cells(2).className="tblrows";
		mynewrow.cells(2).innerHTML=ParameterName+"<input type='hidden' name='parameter"+index+"' id='parameter"+index+"' value='"+ParameterId+"'>"+"<input type='hidden' name='replyItemType"+index+"' id='replyItemType"+index+"' value='"+strReplyItemType+"'>"+"<input type='hidden' name='parameterName"+index+"' id='parameterName"+index+"' value='"+ParameterName+"'>";

		//ParameterUsage
		mynewrow.insertCell();
		mynewrow.cells(3).className="tblrows";
		mynewrow.cells(3).innerHTML=RadiusName+"<input type='hidden' name='paramUsage"+index+"' id='paramUsage"+index+"' value='"+RadiusId+"'>";
		
		//OperatorUsage
		mynewrow.insertCell();
		mynewrow.cells(4).className="tblrows";
		mynewrow.cells(4).innerHTML=OperatorName+"<input type='hidden' name='operator"+index+"' id='operator"+index+"' value='"+OperatorId+"'>";

		//Value
		mynewrow.insertCell();
		mynewrow.cells(5).className="tblrows";
		mynewrow.cells(5).innerHTML=Display+"<input type='hidden' name='value"+index+"' id='value"+index+"' value='"+Value+"'>"+"<input type='hidden' name='display"+index+"' id='display"+index+"' value='"+Display+"'>";
			
		//Status
		mynewrow.insertCell();
		mynewrow.cells(6).className="tblrows";
		if( Status == "Y" )
		{
			mynewrow.cells(6).innerHTML=" <a href='javascript:void(0)' ><img src='images/active.jpg' border='0' alt='Status' ></a>"+"<input type='hidden' name='active"+index+"' id='active"+index+"' value='"+Status+"'>";
		}
		else
		{
			mynewrow.cells(6).innerHTML=" <a href='javascript:void(0)' ><img src='images/deactive.jpg' border='0' alt='Status' ></a>"+"<input type='hidden' name='active"+index+"' id='active"+index+"' value='"+Status+"'>";
		}
		
		//Remove
		mynewrow.insertCell();
		mynewrow.cells(7).className="tblrows";
		mynewrow.cells(7).innerHTML=" <a href='javascript:void(0)' onclick='removeRow(this.parentElement)'><img src='images/minus.jpg' border='0' alt='Remove' ></a>"

		//Edit
		mynewrow.insertCell();
		mynewrow.cells(8).className="tblrows";
		mynewrow.cells(8).innerHTML=" <a href='javascript:void(0)' onclick='updateRow(this.parentElement,DictionaryId,ParameterId,RadiusId,OperatorId,Status,Value)'><img src='images/edit.jpg' border='0' alt='Edit' ></a>"
}

function DeleteAllRows(rowAdd) // Deletes all rows of the table except the header
{
	for(i=1;i<ParameterArray.length+rowAdd;i++)
	{
		TableObject.deleteRow(1);
		
	}
}

function removeRow(object) //Removes the row via DOM and also deletes the ITEM from the Data Structure
{	
	if(confirm("Are You Sure you want to remove the Particular"))		
	{
		while (object.tagName !=  'TR')
		{
			object = object.parentNode;
		}
		RowNumber = object.rowIndex;
	
		ArrayIndex=RowNumber-1;
		
		TableObject.deleteRow(RowNumber); //Removes the row via DOM
		ParameterArray.splice(ArrayIndex, 1);  //deletes the ITEM from the Data Structure
	//The following two functions are called just to rename the names of the controls on the form
		
		populateTable(1); //Draws the complete table based on the data structure		
	}
}

function updateRow(object,DictionaryId,ParameterId,RadiusId,OperatorId,Status,Value)
{	
	if(confirm("Are You Sure you want to Edit the Particular"))		
	{
		while (object.tagName !=  'TR')
		{
			object = object.parentNode;
		}
		RowNumber = object.rowIndex;
		index=RowNumber-1;
//		var path = "/jsp/servicepackage/serviceparam/radiuspolicy/AddRadiusPolicyParameter.jsp?DictionaryId="+ParameterArray[index].dictionaryId+"&ParameterName="+ParameterArray[index].parameterName+"&ParameterId="+ParameterArray[index].parameterId+"&RadiusId="+ParameterArray[index].radiusId+"&OperatorId="+ParameterArray[index].operatorId+"&Status="+ParameterArray[index].status+"&Value="+escape(ParameterArray[index].display)+"&Key="+escape(ParameterArray[index].value)+"&Index="+index+"&srcDictionaryID="+ParameterArray[index].srcDictionaryID+"&srcDictParamID="+ParameterArray[index].srcDictParamID+"&srcAVPairAttName="+ParameterArray[index].srcAVPairAttName+"&srcDictComboID="+ParameterArray[index].srcDictComboID+"&srcDictParamComboID="+ParameterArray[index].srcDictParamComboID+"&strReplyItemType="+ParameterArray[index].strReplyItemType;
		var path = "jsp/radiuspolicy/AddRadiusPolicyParameter.jsp?DictionaryId="+ParameterArray[index].dictionaryId+"&ParameterName="+ParameterArray[index].parameterName+"&ParameterId="+ParameterArray[index].parameterId+"&RadiusId="+ParameterArray[index].radiusId+"&OperatorId="+ParameterArray[index].operatorId+"&Status="+ParameterArray[index].status+"&Value="+escape(ParameterArray[index].display)+"&Key="+escape(ParameterArray[index].value)+"&Index="+index+"&srcDictionaryID="+ParameterArray[index].srcDictionaryID+"&srcDictParamID="+ParameterArray[index].srcDictParamID+"&srcAVPairAttName="+ParameterArray[index].srcAVPairAttName+"&srcDictComboID="+ParameterArray[index].srcDictComboID+"&srcDictParamComboID="+ParameterArray[index].srcDictParamComboID+"&strReplyItemType="+ParameterArray[index].strReplyItemType;
		newWin=window.open(path,'selectPackage','status=yes,scrollbars=yes,width=850,height=550 left');
	}
}