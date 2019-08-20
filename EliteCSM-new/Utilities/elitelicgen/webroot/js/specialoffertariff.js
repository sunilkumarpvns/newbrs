re = /\w{1,}/; 
function removeRow(object)
{	
	while (object.tagName !=  'TR')
	{
		object = object.parentNode;
	}
	RowNumber = object.rowIndex;
	totalRows=thisDoc.children.length;
	lastRow = totalRows-1;
	
	if((totalRows > 3) && (RowNumber != lastRow ))
	{
	thisDoc.childNodes[RowNumber-1].childNodes[2].childNodes[0].value = thisDoc.childNodes[RowNumber+1].childNodes[1].childNodes[0].value;
	}
	
	if((totalRows == 3) || (RowNumber == lastRow))
	{
		PRow = RowNumber-1;
		thisDoc.childNodes[PRow].childNodes[2].innerHTML = "<input type='text' name='ToDate"+PRow+"' id='ToDate"+PRow+"' value='-' class='text-noborders' onFocus='this.blur()'>";
	}

	
	thisDoc.deleteRow(RowNumber);
	AssignIds();
}


function AssignIds()
{
	totalRows=thisDoc.children.length;

	for(k=1;k<totalRows;k++)
	{
		thisDoc.childNodes[k].childNodes[0].innerText=k;
		orgFromDate = thisDoc.childNodes[k].childNodes[1].childNodes[0].value;
		thisDoc.childNodes[k].childNodes[1].innerHTML= "<input type='text' name='FromDate"+k+"' id='FromDate"+k+"' value='"+orgFromDate+"' class='text-noborders' onFocus='this.blur()'>"

		orgToDate = thisDoc.childNodes[k].childNodes[2].childNodes[0].value;
		thisDoc.childNodes[k].childNodes[2].innerHTML= "<input type='text' name='ToDate"+k+"' id='ToDate"+k+"' value='"+orgToDate+"' class='text-noborders' onFocus='this.blur()'>"

		if(k>1)
		{
			orgTariffValue = thisDoc.childNodes[k].childNodes[3].childNodes[0].value;
			orgTariffText = thisDoc.childNodes[k].childNodes[3].childNodes[1].value;
			thisDoc.childNodes[k].childNodes[3].innerHTML= "<input type='hidden' name='Channel"+k+"' id='Channel"+k+"' value='"+orgTariffValue+"' >" + "<input type='text' name='ChannelText"+k+"' id='ChannelText"+k+"' value='"+orgTariffText+"' class='text-noborders' onFocus='this.blur()'>";
	
			orgCombinedLLValue = thisDoc.childNodes[k].childNodes[4].childNodes[0].value;
			orgCombineLLText = thisDoc.childNodes[k].childNodes[4].childNodes[1].value;
			thisDoc.childNodes[k].childNodes[4].innerHTML= "<input type='hidden' name='CombinedLocalLead"+k+"' id='CombinedLocalLead"+k+"' value='"+orgCombinedLLValue+"' >" + "<input type='text' name='CombinedLLText"+k+"' id='CombinedLLText"+k+"' value='"+orgCombineLLText+"' class='text-noborders' onFocus='this.blur()'>";
			
			//alert(thisDoc.childNodes[k].childNodes[5].childNodes[0].value);
			orgep1LLValue = thisDoc.childNodes[k].childNodes[5].childNodes[0].value;
			orgep1LLText = thisDoc.childNodes[k].childNodes[5].childNodes[1].value;
			thisDoc.childNodes[k].childNodes[5].innerHTML ="<input type='hidden' name='EndPoint1LocalLead"+k+"' id='EndPoint1LocalLead"+k+"' value='"+orgep1LLValue+"' >"  + "<input type='text' name='EndPoint1LLText"+k+"' id='EndPoint1LLText"+k+"' value='"+orgep1LLText+"' class='text-noborders' onFocus='this.blur()'>" ; 
			
			orgep2LLValue = thisDoc.childNodes[k].childNodes[6].childNodes[0].value;
			orgep2LLText = thisDoc.childNodes[k].childNodes[6].childNodes[1].value;
			thisDoc.childNodes[k].childNodes[6].innerHTML ="<input type='hidden' name='EndPoint2LocalLead"+k+"' id='EndPoint2LocalLead"+k+"' value='"+orgep2LLValue+"' >"  + "<input type='text' name='EndPoint2LLText"+k+"' id='EndPoint2LLText"+k+"' value='"+orgep2LLText+"' class='text-noborders' onFocus='this.blur()'>" ; 
		}


	}
	document.frmTariff.totalrows.value = parseInt(totalRows) - 1;

}


function AddDetails(thisform)
{
	//alert(thisform.NewDate.value);
	if(!re.test(thisform.NewDate.value))
	{
		alert("Select a from date greater than today's Date");
		return false;
	}

	if(!re.test(thisform.NewTariff.value))
	{
		alert("Please Select the Tariff");
		thisform.NewTariff.focus();
		return false;
	}
	
	newDate = new Date(getDateFromFormat(thisform.NewDate.value,'<%=(String)session.getAttribute("date_format")%>'));
	
	//alert('Session check '+ newDate);
	
	nMonth=parseInt(newDate.getMonth())+1;
	nDate = newDate.getDate(); 
	nYear = newDate.getFullYear()  ;

	newDate = nMonth+"/"+nDate+"/"+nYear;
	newDate = Date.parse(newDate);	
	
	//alert(newDate + ", " + newDate);

	//code added by novin to get the server date 

	todaysDate = new Date();
	todaysDay = todaysDate.getDate();
	todaysMonth = todaysDate.getMonth()+1;
	todaysYear = todaysDate.getFullYear();
	todaysDate = todaysMonth + "/" + todaysDay + "/" + todaysYear;
	todaysDate=Date.parse(todaysDate);

	totalRows = thisDoc.children.length;

	if(totalRows<2)
	{
		alert("There is no Default Date specified");
		return false;
	}
	else if(totalRows==2)
	{
		fromDate = new Date(getDateFromFormat(thisform.FromDate1.value,'<%=(String)session.getAttribute("date_format")%>'));//eval("thisform.FromDate"+ctr+".value");	

		//alert(thisform.FromDate1.value);
		fMonth=parseInt(fromDate.getMonth())+1;
		fDate = fromDate.getDate(); 
		fYear = fromDate.getFullYear()  ;

		fromDate = fMonth+"/"+fDate+"/"+fYear;
		fromDate = Date.parse(fromDate);
		
		if(newDate <= todaysDate)
		{
			alert("The New from date has to be greater than Today's Date");
			return false;
		}


		//alert(newDate + ", " + todaysDate + ", " + fromDate);
		//alert(((newDate > todaysDate) && (newDate > fromDate)));
			
		if((newDate > todaysDate) && (newDate > fromDate))
		{
			addRow(totalRows);
			thisDoc.childNodes[1].childNodes[2].innerHTML = "<input type='text' name='ToDate1' id='ToDate1' value='"+thisform.NewDate.value+"' class='text-noborders' onFocus='this.blur()'>"
			thisDoc.childNodes[2].childNodes[1].childNodes[0].value = thisform.NewDate.value;
			thisDoc.childNodes[2].childNodes[2].innerHTML = "<input type='text' name='ToDate2' id='ToDate2' value='-' onFocus='this.blur()'>";
			AssignIds();

		}

	}
	else if(totalRows > 2)
	{
		//alert(totalRows);
		lastRow = totalRows-1;
		for(ctr=1;ctr<totalRows;ctr++)
		{
			ctrnext=ctr+1;
///FromDate
			fromDate = eval("thisform.FromDate"+ctr+".value");
			//alert(eval("thisform.FromDate"+ctr+".value"));
			fromDate = new Date(getDateFromFormat(fromDate,'<%=(String)session.getAttribute("date_format")%>'));

			fMonth=parseInt(fromDate.getMonth())+1;
			fDate = fromDate.getDate(); 
			fYear = fromDate.getFullYear()  ;
			
			fromDate = fMonth+"/"+fDate+"/"+fYear;
			fromDate = Date.parse(fromDate);
			
			//alert(ctr + ", fromDate : " + fromDate);

//ToDate

			toDate = eval("thisform.ToDate"+ctr+".value");
			toDate = new Date(getDateFromFormat(toDate,'<%=(String)session.getAttribute("date_format")%>'));

			tMonth=parseInt(toDate.getMonth())+1;
			tDate = toDate.getDate(); 
			tYear = toDate.getFullYear()  ;

			toDate = tMonth+"/"+tDate+"/"+tYear;
			toDate = Date.parse(toDate);
			
			//alert(ctr + ", toDate : " + toDate)

//NextFromDate			

			NextfromDate = eval("thisform.FromDate"+ctrnext+".value");
			NextfromDate = new Date(getDateFromFormat(NextfromDate,'<%=(String)session.getAttribute("date_format")%>'));

			fnMonth=parseInt(NextfromDate.getMonth())+1;
			fnDate = NextfromDate.getDate(); 
			fnYear = NextfromDate.getFullYear()  ;

			NextfromDate = fnMonth+"/"+fnDate+"/"+fnYear;
			NextfromDate = Date.parse(NextfromDate);
			
			//alert(ctr + ", NextfromDate : " + NextfromDate);

//LastFromDate

			LastfromDate = eval("thisform.FromDate"+lastRow+".value");
			LastfromDate = new Date(getDateFromFormat(LastfromDate,'<%=(String)session.getAttribute("date_format")%>'));
			lMonth=parseInt(LastfromDate.getMonth())+1;
			lDate = LastfromDate.getDate(); 
			lYear = LastfromDate.getFullYear()  ;

			LastfromDate = lMonth+"/"+lDate+"/"+lYear;
			LastfromDate = Date.parse(LastfromDate);
			
			//alert(ctr + ", LastfromDate : " + LastfromDate)


			if(newDate <= todaysDate)
			{
				alert("The New from date has to be greater than Today's Date");
				break;
			}

			else if ((newDate > fromDate) && (newDate > LastfromDate))
			{
				//alert("insert at last Row");
				addRow(totalRows);
				thisDoc.childNodes[totalRows-1].childNodes[2].childNodes[0].value=document.frmTariff.NewDate.value;
				AssignIds();
				break;
			}
			else if((newDate > fromDate) && (newDate < NextfromDate))
			{
				//alert("insert inbetween at row : "+ctrnext);
				addRow(ctrnext);
				
				thisDoc.childNodes[ctrnext].childNodes[2].childNodes[0].value=thisDoc.childNodes[ctr].childNodes[2].childNodes[0].value;
				thisDoc.childNodes[ctr].childNodes[2].childNodes[0].value=document.frmTariff.NewDate.value;
				AssignIds();
				break;
			}
			else if((newDate == fromDate) || (newDate == NextfromDate) || (newDate == LastfromDate))
			{
				alert("Same date can not be selected.");
				return false;
			}
 
		}//end of for		
	}//(totalRows > 2)
}//AddDetails

function addRow(RowNumber)
{
		PrevRow = RowNumber;
		//	RowNumber=RowNumber+1;
		mynewrow = thisDoc.insertRow(RowNumber);

		for (i=0;i<8;i++)
		{
			mynewrow.insertCell();
			mynewrow.cells(i).innerHTML = "<td>&nbsp;</td>";
			mynewrow.cells(i).className = "tblrows";
		}
		mynewrow.cells(0).className = "tblfirstcol-right";

		//FromDate
		thisDoc.childNodes[RowNumber].childNodes[1].innerHTML = '<input type="text" name="FromDate'+RowNumber+'"  id="FromDate'+RowNumber+'" value="'+document.frmTariff.NewDate.value+'" class="text-noborders" onFocus="this.blur()">'//+'<a href="javascript:void(0)" onclick="popUpCalendar(this, frmTariff.FromDate'+RowNumber+', \'dd/mm/yyyy\')"><img src="/images/calendar.jpg" alt="Calendar" border="0" align="absmiddle"></a>';
		
		//ToDate
			thisDoc.childNodes[RowNumber].childNodes[2].innerHTML = "<input type='text' name='ToDate"+RowNumber+"' id='ToDate"+RowNumber+"' value='-' class='text-noborders' onFocus='this.blur()'>";
		
		//Tariff
		thisDoc.childNodes[RowNumber].childNodes[3].innerHTML ="<input type='hidden' name='Channel"+RowNumber+"' id='Channel"+RowNumber+"' value='"+document.frmTariff.NewTariff.value+"' class='text-noborders' onFocus='this.blur()'>"  + "<input type='text' name='ChannelText"+RowNumber+"' id='ChannelText"+RowNumber+"' value='"+document.frmTariff.NewTariff[document.frmTariff.NewTariff.selectedIndex].text+"' class='text-noborders' onFocus='this.blur()'>" ; 

		//Local Lead
		
		//Make default settings as No Local Lead Distance Configured.
		var ep1LLValue = null;
		var ep2LLValue = null;
		var combinedLLValue = null;
			
		var CombineLLText = "-";
		var EndPoint1LLText  = "-";
		var EndPoint2LLText  = "-";
			
		if(document.getElementById('selCombinedLocalLead')!=null)
		{
			//alert("Combined Local Lead");
			if(document.frmTariff.selCombinedLocalLead.selectedIndex!=0)
			{
				CombineLLText = document.frmTariff.selCombinedLocalLead[document.frmTariff.selCombinedLocalLead.selectedIndex].text;
				combinedLLValue = document.frmTariff.selCombinedLocalLead.value;
			}
		}//End of if for Combined Local Lead
		else if(document.getElementById('selEP1LocalLead')!=null && document.getElementById('selEP2LocalLead')!=null)
		{
			//alert("Seperate Local Lead");
			
			if(document.frmTariff.selEP1LocalLead.selectedIndex!=0)
			{
				EndPoint1LLText  = document.frmTariff.selEP1LocalLead[document.frmTariff.selEP1LocalLead.selectedIndex].text;
				ep1LLValue = document.frmTariff.selEP1LocalLead.value;
			}
			
			if(document.frmTariff.selEP2LocalLead.selectedIndex!=0)
			{
				EndPoint2LLText  = document.frmTariff.selEP2LocalLead[document.frmTariff.selEP2LocalLead.selectedIndex].text;
				ep2LLValue = document.frmTariff.selEP2LocalLead.value;
			}
		}//End of if for Separate Local Lead
		
		thisDoc.childNodes[RowNumber].childNodes[4].innerHTML ="<input type='hidden' name='CombinedLocalLead"+RowNumber+"' id='CombinedLocalLead"+RowNumber+"' value='"+((combinedLLValue==null)?'':combinedLLValue)+"' class='text-noborders' onFocus='this.blur()'>"  + "<input type='text' name='CombinedLLText"+RowNumber+"' id='CombinedLLText"+RowNumber+"' value='"+CombineLLText+"' class='text-noborders' onFocus='this.blur()'>" ; 
		thisDoc.childNodes[RowNumber].childNodes[5].innerHTML ="<input type='hidden' name='EndPoint1LocalLead"+RowNumber+"' id='EndPoint1LocalLead"+RowNumber+"' value='"+((ep1LLValue==null)?'':ep1LLValue)+"' class='text-noborders' onFocus='this.blur()'>"  + "<input type='text' name='EndPoint1LLText"+RowNumber+"' id='EndPoint1LLText"+RowNumber+"' value='"+EndPoint1LLText+"' class='text-noborders' onFocus='this.blur()'>" ; 
		thisDoc.childNodes[RowNumber].childNodes[6].innerHTML ="<input type='hidden' name='EndPoint2LocalLead"+RowNumber+"' id='EndPoint2LocalLead"+RowNumber+"' value='"+((ep2LLValue==null)?'':ep2LLValue)+"' class='text-noborders' onFocus='this.blur()'>"  + "<input type='text' name='EndPoint2LLText"+RowNumber+"' id='EndPoint2LLText"+RowNumber+"' value='"+EndPoint2LLText+"' class='text-noborders' onFocus='this.blur()'>" ; 
		//Remove Image	
		thisDoc.childNodes[RowNumber].childNodes[7].innerHTML = "<a href='javascript:void(0)' onclick='removeRow(this.parentElement)'><img src='/images/minus.jpg' alt='Remove' border='0' align='center'></a>";

}

