function commonfn()
{
	chkselected = false;
	totalRows = thisTable.children.length;
	if(totalRows == 1)
	{
		alert("There are no records in the table");
		return false;
	}
	
	else if(totalRows == 2)
	{
		alert("Only one record in the table \nCannot be moved up or down");
//		clearChkBox();
		return false;
	}
	else if(totalRows > 2)
	{
		//checks that atleast one checkbox is selected
		for(i=1; i<totalRows; i++)
		{
			if(thisTable.childNodes[i].childNodes[0].childNodes[0].checked)
			{
				chkselected = true;
				break;
			}
		}
		
		if(!chkselected)
		{
			alert("Please select a record");
			return false;
		}
	}
}

function clearChkBox()
{
	totalRows = thisTable.children.length;
	if(totalRows >= 2)
	{
		for(k=1;k<totalRows;k++)
		{
			thisTable.childNodes[k].childNodes[0].childNodes[0].checked = false;
		}
	}
}

function MoveUp()
{
	commonfn();
	if(chkselected)
	{
		for(j=1;j<totalRows;j++)
		{
			if(thisTable.childNodes[j].childNodes[0].childNodes[0].checked)
			{
				if(j==1)
				{
					alert("Cannot move the first record");
					break;
				}
				else
				{
					thisTable.childNodes[j].childNodes[2].swapNode(thisTable.childNodes[j-1].childNodes[2]);
					thisTable.childNodes[j].childNodes[3].swapNode(thisTable.childNodes[j-1].childNodes[3]);

								
					thisTable.childNodes[j].childNodes[0].childNodes[0].checked = false;
					thisTable.childNodes[j-1].childNodes[0].childNodes[0].checked = true;


				}
			}//if	
		}//for

//		clearChkBox();
	}//if
}//end of fn

function MoveDn()
{
	commonfn();
	lastRow=totalRows-1;
	if(chkselected)
	{
		for(j=totalRows-1;j>0;j--)
		{
			if(thisTable.childNodes[j].childNodes[0].childNodes[0].checked)
			{
				if(j==lastRow)
				{
					alert("Cannot move the last record");
					break;
				}
				else
				{
					thisTable.childNodes[j].childNodes[2].swapNode(thisTable.childNodes[j+1].childNodes[2]);
					thisTable.childNodes[j].childNodes[3].swapNode(thisTable.childNodes[j+1].childNodes[3]);

					thisTable.childNodes[j].childNodes[0].childNodes[0].checked = false;
					thisTable.childNodes[j+1].childNodes[0].childNodes[0].checked = true;

				}
			}//if	
		}//for
//		clearChkBox();
	}//if
}//end of fn

function UpdatePaymentPriority()
{
	if(document.c_frmSetPaymentPriority.rpriority[1].checked)
	{
		totalRows = parseInt(thisTable.children.length)
		contentRows = parseInt(thisTable.children.length) - 1;
		if(totalRows == 2)
		{
			document.c_frmSetPaymentPriority.RowCount.value=contentRows;
			document.c_frmSetPaymentPriority.ACNos.value=thisTable.childNodes[1].childNodes[2].childNodes[0].value;
//			document.c_frmSetPaymentPriority.ACNames.value=thisTable.childNodes[1].childNodes[3].childNodes[0].value;
		}
		else if(totalRows > 2)
		{
			document.c_frmSetPaymentPriority.RowCount.value=contentRows;
			tempACNo = "";
//			tempACName = "";
			for(i=1;i<totalRows;i++)
			{
				if(i==1)
				{
					tempACNo = thisTable.childNodes[i].childNodes[2].childNodes[0].value;
//					tempACName = thisTable.childNodes[i].childNodes[3].childNodes[0].value;
				}
				else
				{
					tempACNo = tempACNo+"$"+thisTable.childNodes[i].childNodes[2].childNodes[0].value;
//					tempACName = tempACName+"$"+thisTable.childNodes[i].childNodes[3].childNodes[0].value;
				}
			}
			document.c_frmSetPaymentPriority.ACNos.value=tempACNo;
//			document.c_frmSetPaymentPriority.ACNames.value=tempACName;
		}
//	alert("Total Rows : "+document.c_frmSetPaymentPriority.RowCount.value+"\nACCT No : "+document.c_frmSetPaymentPriority.ACNos.value);
	}
}
