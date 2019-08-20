/** Add/Delete Billing Areas(Rows) and ReIndex them
	
	Author: Sumedha Jagushte
	Created Date : 1st August 2003
**/

function addbillingareas()
{
	listLen=document.all.c_strBillingCycleList.options.length;
	listSelected=false;
	totalRows=thisDoc.children.length;
	selectedCount = 0;
	for(i=0;i<listLen;i++)
	{
		if(document.all.c_strBillingCycleList[i].selected)
		{
			listSelected=true;
			//selectedCount++;		
			totalRows=parseInt(thisDoc.children.length);
			rowId=0
			mynewrow = eval("document.all.configurationTable.insertRow(" + totalRows + ")");
			for (j=0;j<5;j++)
			{
				mynewrow.insertCell();
				mynewrow.cells(j).innerHTML = "<td class='labeltext'>&nbsp;</td>";
				if(j==0)
					mynewrow.cells(j).className= 'tblfirstcol-right';
				else if(j==1)
					mynewrow.cells(j).className= 'tblrows';
				else if(j==4)
				{
					mynewrow.cells(j).className= 'tblrows';
					mynewrow.cells(j).align='center';
				}
				else
					mynewrow.cells(j).className='tblrows-right';
			}
			thisDoc.childNodes[totalRows].childNodes[0].innerText=totalRows+".";
			thisDoc.childNodes[totalRows].childNodes[1].innerHTML=document.all.c_strBillingCycleList[i].text+"<input type='hidden' name='strConfigureBillingCycleKey"+totalRows+"' id='strConfigureBillingCycleKey"+totalRows+"' value='"+document.all.c_strBillingCycleList[i].value+"'>";
			thisDoc.childNodes[totalRows].childNodes[2].innerHTML=
					"<input type='text' class='amount-text' size='3' name='c_nPeriod"+totalRows+"' id='c_nPeriod"+totalRows+"' value='0'  >"
			thisDoc.childNodes[totalRows].childNodes[3].innerHTML=
					"<input type='text' class='amount-text' size='15' name='c_fPrice"+totalRows+"' id='c_fPrice"+totalRows+"' value='0.0' >"
			thisDoc.childNodes[totalRows].childNodes[4].innerHTML="<a href='javascript:void(0)' onclick='removebillingarea(this.parentElement)'><img src='/images/minus.jpg' border='0'></a>"

		}
	}
	if(!listSelected)
	{
		alert("Select the Billing Cycle to be added");
		return false;
	}
}




function removebillingarea(object)
{	
		while (object.tagName !=  'TR')
		{
			object = object.parentNode;
		}
		RowNumber = object.rowIndex;

	eval("document.all.configurationTable.deleteRow(" + RowNumber + ")");
	AssignIds();
}

function AssignIds()
{
	totalRows=thisDoc.children.length;
	for(i=1;i<totalRows;i++)
	{
			//alert(thisDoc.childNodes[i].childNodes[0].innerText);
			thisDoc.childNodes[i].childNodes[0].innerText=i;
			thisDoc.childNodes[i].childNodes[1].innerHTML=thisDoc.childNodes[i].childNodes[1].innerText+"<input type='hidden' name='strConfigureBillingCycleKey"+i+"' id='strConfigureBillingCycleKey"+i+"' value='"+thisDoc.childNodes[i].childNodes[1].childNodes[1].value +"'>";
			thisDoc.childNodes[i].childNodes[2].innerHTML=
					"<input type='text' maxlength='3' size='3' name='c_nPeriod"+i+"' id='c_nPeriod"+i+"' value='"+thisDoc.childNodes[i].childNodes[2].childNodes[0].value +"'>"
			thisDoc.childNodes[i].childNodes[3].innerHTML=
					"<input type='text' maxlength='15' size='15' name='c_fPrice"+i+"' id='c_fPrice"+i+"' value='"+thisDoc.childNodes[i].childNodes[3].childNodes[0].value +"'>"
			thisDoc.childNodes[i].childNodes[4].innerHTML="<a href='javascript:void(0)' onclick='removebillingarea(this.parentElement)'><img src='/images/minus.jpg' border='0'></a>"
	}	
}

function viewGeneratedDOM(){
    x=window.open();
    x.document.write('<pre>&lt;html&gt;' + htmlEscape(document.documentElement.innerHTML) + '&lt;/html&gt;</pre>');
    x.document.close();
}

function htmlEscape(s){
    s=s.replace(/&/g,'&amp;');
    s=s.replace(/>/g,'&gt;');
    s=s.replace(/</g,'\n&lt;');
    return s;
}


function viewGeneratedDOM(){
    x=window.open();
    x.document.write('<pre>&lt;html&gt;' + htmlEscape(document.documentElement.innerHTML) + '&lt;/html&gt;</pre>');
    x.document.close();
}

function htmlEscape(s){
    s=s.replace(/&/g,'&amp;');
    s=s.replace(/>/g,'&gt;');
    s=s.replace(/</g,'\n&lt;');
    return s;
}



