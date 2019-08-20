/** Add/Delete Billing Areas(Rows) and ReIndex them
	
	Author: Sumedha Jagushte
	Created Date : 1st August 2003
**/

function addbillingareas()
{
	
	listLen=document.all.c_strBillingAreaTo.options.length;
	totalRows=thisDoc.children.length;

	for(i=0;i<listLen;i++)
	{
		TextMatch=false;
		for(k=1;k<totalRows;k++)
		{
			tableTextId=thisDoc.childNodes[k].childNodes[1].childNodes[1].value;
			listTextId=document.all.c_strBillingAreaTo.options[i].value;
			if(tableTextId==listTextId)
			{
				TextMatch=true;
				alert(document.all.c_strBillingAreaTo.options[i].text+" is already added")
				break;
			}
		}
		if(!TextMatch)
		{

			totalRows=parseInt(thisDoc.children.length);
			rowId=0
			mynewrow = eval("document.all.myTable.insertRow(" + totalRows + ")");
			for (j=0;j<4;j++)
			{
				mynewrow.insertCell();
				mynewrow.cells(j).innerHTML = "<td class='labeltext'>&nbsp;</td>";
			}
			thisDoc.childNodes[totalRows].childNodes[0].innerText=totalRows;
			thisDoc.childNodes[totalRows].childNodes[1].innerHTML=document.all.c_strBillingAreaTo.options[i].text+"<input type='hidden' name='h_strBillingArea"+totalRows+"' value='"+document.all.c_strBillingAreaTo.options[i].value+"'>";
			thisDoc.childNodes[totalRows].childNodes[2].innerHTML=
					"<input type='text' size='7' maxlen='7' name='c_fTax"+totalRows+"' value=' '><input type='hidden'  name='h_fTaxValue"+totalRows+"' value='"+thisDoc.childNodes[totalRows].childNodes[2].childNodes[0].value+"'>"
			thisDoc.childNodes[totalRows].childNodes[3].innerHTML="<a href='javascript:void(0)' onclick='removebillingarea(this.parentElement)'><img src='/images/minus.jpg' border='0'></a>"
		}			
	}
}


function removebillingarea(object)
{	
		while (object.tagName !=  'TR')
		{
			object = object.parentNode;
		}
		RowNumber = object.rowIndex;

	eval("document.all.myTable.deleteRow(" + RowNumber + ")");
	AssignId();
}

function AssignId()
{
	totalRows=thisDoc.children.length;
	for(i=1;i<totalRows;i++)
	{
		thisDoc.childNodes[i].childNodes[0].innerText=i;
		thisDoc.childNodes[i].childNodes[1].innerHTML=thisDoc.childNodes[i].childNodes[1].innerText+"<input type='hidden' name='h_strBillingArea"+i+"' value='"+thisDoc.childNodes[i].childNodes[1].childNodes[1].value+"'>";
		thisDoc.childNodes[i].childNodes[2].innerHTML=
				"<input type='text' size='7' maxlen='7' name='c_fTax"+i+"' value='"+thisDoc.childNodes[i].childNodes[2].childNodes[0].value+"'><input type='hidden'  name='h_fTaxValue"+i+"' value='"+thisDoc.childNodes[i].childNodes[2].childNodes[0].value+"'>"
		thisDoc.childNodes[i].childNodes[3].innerHTML="<a href='javascript:void(0)' onclick='removebillingarea(this.parentElement)'><img src='/images/minus.jpg' border='0'></a>"
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


