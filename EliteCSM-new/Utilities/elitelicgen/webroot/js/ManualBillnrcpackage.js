
var nre= /^\d+$/;
var fre=/^((\d+(\.\d*)?)|((\d*\.)?\d+))$/;
var NRCBaseItemArray = new Array();
var NRCItemsArray = new Array();
var NRCtRows =0;

function NRCManualBillpopulateTable() //Read from the Data Structure and draw the table
{

	NRCDeleteAllRows();	
	eval("document."+formname+".NRCCount.value=NRCBaseItemArray.length")	
	for(i=0;i<NRCBaseItemArray.length;i++)
	{
		NRCItemId=NRCBaseItemArray[i].NRCItemId;
		NRCItemName=NRCBaseItemArray[i].NRCItemName;
		NRCBillingType = NRCBaseItemArray[i].NRCBillingType; 
		NRCItemTypeId=NRCBaseItemArray[i].NRCItemTypeId;
		NRCCalledFrom=NRCBaseItemArray[i].NRCCalledFrom;
		NRCQuantity=NRCBaseItemArray[i].NRCQuantity;		
		NRCRate=NRCBaseItemArray[i].NRCRate;
		
		NRCaddRows();	
	}

//DRAW THE TOTALS ROW IN THE TABLE
		totalNRCRows = NRCTableObject.children.length;
		mynewrow = NRCTableObject.insertRow(totalNRCRows);
		for (c=0;c<6;c++)
		{
			mynewrow.insertCell();
			mynewrow.cells(c).innerHTML = "<td><b>&nbsp;<b></td>";
			mynewrow.cells(c).className="tblrows-right-bold";
		}
		
		NRCTableObject.childNodes[totalNRCRows].childNodes[3].innerText="Total";

		NRCupdateTotalsRow();
}

function NRCaddRows() //draws the content rows in the table
{
		totalNRCRows = NRCTableObject.children.length;
		NRCContentRows = totalNRCRows-2;
		RCLastRow=totalNRCRows-1;
		FirstRow=RCLastRow+2;
		mynewrow = NRCTableObject.insertRow(totalNRCRows);
		var NRCPackageId = '';
		var NRCPackageName = '';
		for (c=0;c<6;c++)
		{
			mynewrow.insertCell();
			mynewrow.cells(c).innerHTML = "<td>&nbsp;</td>";
			mynewrow.cells(c).className="tblrows";
		}
		for(c=3;c<5;c++)
		{
			mynewrow.cells(c).className="tblrows-right";
		}
		mynewrow.cells(0).align="right";
		mynewrow.cells(5).align="center";
		
		//Sr. No. + CalledFrom
		NRCTableObject.childNodes[totalNRCRows].childNodes[0].innerHTML=totalNRCRows+"."+"<input type='hidden' name='CalledFrom"+totalNRCRows+"' id='CalledFrom"+totalNRCRows+"' value='"+NRCCalledFrom+"'>";
		
		//ItemName and ItemID and ItemTypeId
		NRCTableObject.childNodes[totalNRCRows].childNodes[1].innerHTML=NRCItemName+"<input type='hidden' name='NRCItemId"+totalNRCRows+"' id='NRCItemId"+totalNRCRows+"' value='"+NRCItemId+"'>"+"<input type='hidden' name='NRCItemTypeId"+totalNRCRows+"' id='NRCItemTypeId"+totalNRCRows+"' value='"+NRCItemTypeId+"'>"+"<input type='hidden' name='NRCItemName"+totalNRCRows+"' id='NRCItemName"+totalNRCRows+"' value='"+NRCItemName+"'>"+"<input type='hidden' name='NRCPackageId"+totalNRCRows+"' id='NRCPackageId"+totalNRCRows+"' value='"+NRCPackageId+"'>"+"<input type='hidden' name='NRCPackageName"+totalNRCRows+"' id='NRCPackageName"+totalNRCRows+"' value='"+NRCPackageName+"'>";
		
		//Quantity Textbox
		NRCTableObject.childNodes[totalNRCRows].childNodes[2].innerHTML="<input type='text' size='3' class='amount-text' maxlength='3' name='NRCQty"+totalNRCRows+"' value='"+NRCQuantity+"' onBlur='NRCchangeTotalValues("+totalNRCRows+");NRCupdateTotalsRow();'>";

		//Rate
		NRCTableObject.childNodes[totalNRCRows].childNodes[3].innerHTML=NRCRate+"<input type='hidden' name='NRCRate"+totalNRCRows+"' id='NRCRate"+totalNRCRows+"' value='"+NRCRate+"'>";	

		//RemoveImage
		NRCTableObject.childNodes[totalNRCRows].childNodes[5].innerHTML=" <a href='javascript:void(0)' onclick='NRCremoveRow(this.parentElement)'><img src='/images/minus.jpg' border='0' alt='Remove' ></a>"
		
				
		NRCchangeTotalValues(totalNRCRows);


}



function NRCchangeTotalValues(RNumber) //Updates the values of 'Amount' and 'Total' column for each row
{
		RowNumber = RNumber;
		var discount = 0;
		var rowCount;
		var TotalAmt=0;
		nre = /^[1-9]\d*$/;
		if(nre.test(NRCTableObject.childNodes[RowNumber].childNodes[2].childNodes[0].value) && parseInt(NRCTableObject.childNodes[RowNumber].childNodes[2].childNodes[0].value)>0)
		{
			NRCAmount = parseFloat(NRCTableObject.childNodes[RowNumber].childNodes[2].childNodes[0].value)*parseFloat(NRCTableObject.childNodes[RowNumber].childNodes[3].innerText);
			NRCAmount=(Math.round(NRCAmount*100.0)/100.00);

			NRCAmount = convertCurrency(NRCAmount);		

			NRCTableObject.childNodes[RowNumber].childNodes[4].innerHTML=NRCAmount + "<input type='hidden' name='NRCAmount"+RowNumber+"' id='NRCAmount"+RowNumber+"' value='"+NRCAmount+"'>";;
			
			//Updating the Qty in DataStructure
			NRCBaseItemArray[RowNumber-1].NRCQuantity = NRCTableObject.childNodes[RowNumber].childNodes[2].childNodes[0].value;
					
			for(actr=0;actr<NRCBaseItemArray.length;actr++)
			{
				//BaseItemArray[actr].NRCItemId = RCTable.ItemId
				if(NRCBaseItemArray[actr].NRCItemId == NRCTableObject.childNodes[RowNumber].childNodes[1].childNodes[1].value)
				{
					NRCBaseItemArray[actr].NRCQuantity = NRCTableObject.childNodes[RowNumber].childNodes[2].childNodes[0].value;
					break;
				}
			}


		}
		else
		{
			alert("Quantity should be an integer greater than zero.");
			//NRCBaseItemArray[RowNumber-1].NRCQuantity =0;
			//NRCTableObject.childNodes[RowNumber].childNodes[2].childNodes[0].value=0;
			NRCTableObject.childNodes[RowNumber].childNodes[4].innerText=convertCurrency(0);			
			NRCTableObject.childNodes[RowNumber].childNodes[2].childNodes[0].select();
			return false;
		}
				
}


function NRCupdateTotalsRow() //Updates the values of the Totals Row
{
		NRCTotalAmount=0.00;
		NRCtRows = parseFloat(NRCTableObject.children.length)-1;
		
		for(r=1;r<NRCtRows;r++)
		{			
			NRCTotalAmount = NRCTotalAmount+parseFloat(NRCTableObject.childNodes[r].childNodes[4].innerText);
		}
		
		NRCTotalAmount = (Math.round(NRCTotalAmount*100.0)/100.0);

		NRCTotalAmount = convertCurrency(NRCTotalAmount);
		
		NRCTableObject.childNodes[NRCtRows].childNodes[4].innerText=NRCTotalAmount;		
		
}



function NRCremoveRow(object) //Removes the row via DOM and also deletes the ITEM from the Data Structure
{	

	if(confirm("Are You Sure you want to remove the Particular"))		
	{
		while (object.tagName !=  'TR')
		{
			object = object.parentNode;
		}
		RowNumber = object.rowIndex;
	
		ArrayIndex=RowNumber-1;
		for(i=0,j=1;i<NRCBaseItemArray.length;i++,j++)
		{
			NRCBaseItemArray[i].NRCQuantity = NRCTableObject.childNodes[j].childNodes[2].childNodes[0].value;
		}
	
		NRCTableObject.deleteRow(RowNumber); //Removes the row via DOM
		NRCBaseItemArray.splice(ArrayIndex, 1); //deletes the ITEM from the Data Structure
	//The following two functions are called just to rename the names of the controls on the form
		NRCDeleteAllRows(); //Deletes all rows of the table except the header
		NRCManualBillpopulateTable(); //Draws the complete table based on the data structure
	//
		//NRCAssignIds(); //Regenerates the Serial Nos.
	}
}

function NRCDeleteAllRows() // Deletes all rows of the table except the header
{
	totalNRCRows = NRCTableObject.children.length;
	for(i=1;i<totalNRCRows;i++)
	{
		NRCTableObject.deleteRow(1);
	}
	
}

function NRCAssignIds() //Regenerates the Serial Nos.
{
	totalNRCRows = NRCTableObject.children.length;
	NRCContentRows = totalNRCRows-2;
	for(i=1;i<=NRCContentRows;i++)
	{
		NRCTableObject.childNodes[i].childNodes[0].innerText=i;
	}
	
}

function convertCurrency(num)
{
	 num = eval(num);
	 num *= 100;
	 num = Math.round(num)/100;

	 if(num - Math.floor(num) == 0) 
	 {
		num = num + ".00";
	 }
	 else 
	 {
		 string = num.toString();
		 parts = string.split(".");
		 cents = parts[1];
		 if (cents.length == 1) 
		 {
			 num = num + "0";
		 }
	}
	return (num);
}

/*function NRCUpdateAllDiscountValues()
{
	NRCtRows = parseFloat(NRCTableObject.children.length)-1;		
	for(r=1;r<NRCtRows;r++)
	{	
		NRCUpdateDiscountValues(r);
	}
}
function applyTaxRule()
{
	//alert("Inside Apply Tax Rule " + taxRule); 
	NRCUpdateAllDiscountValues();
	NRCupdateTotalsRow();
}*/