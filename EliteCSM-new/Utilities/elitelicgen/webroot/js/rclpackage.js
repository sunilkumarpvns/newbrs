// JavaScript Document
nre= /^\d+$/;
var RCLBaseItemArray = new Array();
var RCLItemsArray = new Array();
//var TempRCLItemsArray = new Array();
var RCLtRows =0;

///////DATA STRUCTURE//////////////////
/*	var RCLItemObject = new Object();
	var RCLBillingCycleArray = new Array();
	
	RCLItemObject.RCLItemId = "I0007";
	RCLItemObject.RCLItemName="RCL Item";
	RCLItemObject.RCLItemTypeId="Charge";
	RCLItemObject.RCLTaxValue=5;
	RCLItemObject.RCLQuantity=1;

	//FIRST BILLING CYCLE ATTACHED WITH THE ITEM
	var RCLBillingCycleObject = new Object();
	RCLBillingCycleObject.RCLBillingCycleId="BCY004";
	RCLBillingCycleObject.RCLBillingCycleName="Monthly";
	RCLBillingCycleObject.RCLBillingCyclePeriod=5;
	RCLBillingCycleObject.RCLBillingCycleRate=1000;
	RCLBillingCycleArray[0] = RCLBillingCycleObject;

	RCLItemObject.RCLBillingCycle = RCLBillingCycleArray;

	RCLItemsArray[0] = RCLItemObject;

	RCLBaseItemArray[0]=RCLItemsArray[0];*/
////////////END OF DATA STRUCTURE////////


function RCLpopulateTable() //Read from the Data Structure and draw the table
{
	RCLDeleteAllRows();
	//document.frmCreatePackage.RCLCount.value=RCLBaseItemArray.length
	eval("document."+formname+".RCLCount.value=RCLBaseItemArray.length");
	for(i=0;i<RCLBaseItemArray.length;i++)
	{
		RCLItemId=RCLBaseItemArray[i].RCLItemId;
		RCLItemName=RCLBaseItemArray[i].RCLItemName;
		RCLBillingType=RCLBaseItemArray[i].RCLBillingType;
		RCLItemTypeId=RCLBaseItemArray[i].RCLItemTypeId;
		RCLQuantity=RCLBaseItemArray[i].RCLQuantity;
		RCLTaxValue=RCLBaseItemArray[i].RCLTaxValue;
		RCLBillingCycleObjectLen = RCLBaseItemArray[i].RCLBillingCycle.length;
		RCLaddRows();
		
	}
/////////DRAW THE TOTALS ROW IN THE TABLE
		totalRCLRows = RCLTableObject.children.length;
		mynewrow = RCLTableObject.insertRow(totalRCLRows);
		for (c=0;c<10;c++)
		{
			mynewrow.insertCell();
			mynewrow.cells(c).innerHTML = "<td><b>&nbsp;<b></td>";
			mynewrow.cells(c).className="tblrows-right-bold";
		}
		mynewrow.cells(9).className="tblcol";
		
		RCLTableObject.childNodes[totalRCLRows].childNodes[4].innerText="Total";
		RCLupdateTotalsRow();
}

function RCLaddRows() //draws the content rows in the table
{
		totalRCLRows = RCLTableObject.children.length;
		RCLContentRows = totalRCLRows-2;
		RCLLastRow=totalRCLRows-1;
		RCLFirstRow=RCLLastRow+2;
		mynewrow = RCLTableObject.insertRow(totalRCLRows);
		for (c=0;c<10;c++)
		{
			mynewrow.insertCell();
			mynewrow.cells(c).innerHTML = "<td>&nbsp;</td>";
			mynewrow.cells(c).className="tblrows";
		}
		for(c=4;c<9;c++)
		{
			mynewrow.cells(c).className="tblrows-right";
		}
		mynewrow.cells(0).align="right";
		mynewrow.cells(9).className="tblcol";
		mynewrow.cells(9).align="center";
		
		//Sr. No.
		RCLTableObject.childNodes[totalRCLRows].childNodes[0].innerHTML=totalRCLRows+"."+"<input type='hidden' name='RCLTaxValue"+totalRCLRows+"' id='RCLTaxValue"+totalRCLRows+"' value='"+RCLTaxValue+"'>";
		
		//RCLItemName and RCLItemId and RCLItemTypeId
		RCLTableObject.childNodes[totalRCLRows].childNodes[1].innerHTML=RCLItemName+"<input type='hidden' name='RCLItemId"+totalRCLRows+"' id='RCLItemId"+totalRCLRows+"' value='"+RCLItemId+"'>"+"<input type='hidden' name='RCLItemTypeId"+totalRCLRows+"' id='RCLItemTypeId"+totalRCLRows+"' value='"+RCLItemTypeId+"'>";

		// RCLBillingType
		RCLTableObject.childNodes[totalRCLRows].childNodes[2].innerHTML=RCLBillingType;

		//BillngCycle ComboBox
		RCLTableObject.childNodes[totalRCLRows].childNodes[3].innerHTML="<select name='RCLBillingCycle"+totalRCLRows+"' onChange='RCLchangeRate("+totalRCLRows+")'></select>"+"<input type='hidden' name='RCLBillingCyclePeriod"+totalRCLRows+"' value='' >";
		//RCLQuantity Textbox
		RCLTableObject.childNodes[totalRCLRows].childNodes[4].innerHTML="<input type='text' size='5' class='amount-text' maxlength='2' name='RCLQty"+totalRCLRows+"' value='"+RCLQuantity+"' onChange='RCLchangeTotalValues("+totalRCLRows+");RCLupdateTotalsRow();'>";

		//Tax
		RCLTableObject.childNodes[totalRCLRows].childNodes[7].innerHTML=RCLTaxValue+"<input type='hidden' name='RCLTax"+totalRCLRows+"' id='RCLTax"+totalRCLRows+"' value='"+RCLTaxValue+"'>";
		//RCLTableObject.childNodes[totalRCLRows].childNodes[7].innerHTML="&nbsp;";
		
		//RemoveImage
		RCLTableObject.childNodes[totalRCLRows].childNodes[9].innerHTML=" <a href='javascript:void(0)' onclick='RCLremoveRow(this.parentElement)'><img src='/images/minus.jpg' border='0' alt='Remove' ></a>"
		//alert(RCLContentRows);

//POPULATE THE COMBOBOX
		totalElements=eval("document.frmCreatePackage.RCLBillingCycle"+totalRCLRows+".length");
		//alert("totalElements : "+totalElements);
		for(j=0;j<RCLBillingCycleObjectLen;j++)
		{
			RCLBillingCycleId=RCLBaseItemArray[i].RCLBillingCycle[j].RCLBillingCycleId;
			RCLBillingCycleName=RCLBaseItemArray[i].RCLBillingCycle[j].RCLBillingCycleName;
			RCLBillingCycleRate=RCLBaseItemArray[i].RCLBillingCycle[j].RCLBillingCycleRate;
			RCLBillingCyclePeriod=RCLBaseItemArray[i].RCLBillingCycle[j].RCLBillingCyclePeriod;
			
			RCLBillingCycleName = RCLBillingCycleName+" - "+RCLBillingCyclePeriod;
			
			var newoption=new Option (RCLBillingCycleName,RCLBillingCycleId);
			eval("document.frmCreatePackage.RCLBillingCycle"+totalRCLRows+".options[totalElements]= newoption");
			totalElements=totalElements+1;
			
			//BillingCycle Period
			RCLTableObject.childNodes[totalRCLRows].childNodes[3].childNodes[1].value=RCLBaseItemArray[i].RCLBillingCycle[0].RCLBillingCyclePeriod;
			
			//Rate
			RCLTableObject.childNodes[totalRCLRows].childNodes[5].innerHTML=RCLBaseItemArray[i].RCLBillingCycle[0].RCLBillingCycleRate+"<input type='hidden' name='RCLRate"+totalRCLRows+"' id='RCLRate"+totalRCLRows+"' value='"+RCLBaseItemArray[i].RCLBillingCycle[0].RCLBillingCycleRate+"'>";
		}
			RCLchangeTotalValues(totalRCLRows);
}

function RCLchangeTotalValues(RNumber) //Updates the values of 'Amount' and 'Total' column for each row
{
		RowNumber = RNumber;
		//amount=qty*rate
		if(nre.test(RCLTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value) && parseInt(RCLTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value)>0)
		{
			//Amount
			RCLAmount = parseFloat(RCLTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value)*parseFloat(RCLTableObject.childNodes[RowNumber].childNodes[5].innerText);
			RCLAmount = (Math.round(RCLAmount*100.0)/100.0);
			RCLTableObject.childNodes[RowNumber].childNodes[6].innerHTML=RCLAmount;
			
			//Tax
			RCLTaxValue =RCLTableObject.childNodes[RowNumber].childNodes[0].childNodes[1].value;
			RCLTaxRatio=(RCLAmount * RCLTaxValue)/100;
			RCLTaxRatio=(Math.round(RCLTaxRatio*100.0)/100.00);
			RCLTableObject.childNodes[RowNumber].childNodes[7].innerHTML=RCLTaxRatio+"<input type='hidden' name='RCLTax"+RowNumber+"' id='RCLTax"+RowNumber+"' value='"+RCLTaxRatio+"'>";
			
			//total=amount+tax;(if qty>0,total=amount+tax, else total=0)
	
			if(parseFloat(RCLTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value)>0)
			{
			
				RCLTotal = parseFloat(RCLTableObject.childNodes[RowNumber].childNodes[6].innerText)+parseFloat(RCLTableObject.childNodes[RowNumber].childNodes[7].innerText)
				RCLTotal = (Math.round(RCLTotal*100.0)/100.0);
				RCLTableObject.childNodes[RowNumber].childNodes[8].innerHTML=RCLTotal+"<input type='hidden' name='RCLTotal"+RowNumber+"' id='RCLTotal"+RowNumber+"' value='"+RCLTotal+"'>";
			}
			else
			{
				RCLTableObject.childNodes[RowNumber].childNodes[8].innerHTML=0+"<input type='hidden' name='RCLTotal"+RowNumber+"' id='RCLTotal"+RowNumber+"' value='"+0+"'>";
			}
//Updating the Qty in DataStructure
RCLBaseItemArray[RowNumber-1].Quantity = RCLTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value;

for(actr=0;actr<RCLBaseItemArray.length;actr++)
   {
    //BaseItemArray[actr].NRCItemId = RCTable.ItemId
    if(RCLBaseItemArray[actr].RCLItemId == RCLTableObject.childNodes[RowNumber].childNodes[1].childNodes[1].value)
    {
     RCLBaseItemArray[actr].RCLQuantity = RCLTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value;
     break;
    }
   }
/////////////////////////////
		}
		else
		{
			alert("Quantity should be an integer greater than zero.");
			RCLBaseItemArray[RowNumber-1].Quantity =0;
			RCLTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value=0;
			RCLTableObject.childNodes[RowNumber].childNodes[6].innerText=0;
			RCLTableObject.childNodes[RowNumber].childNodes[8].innerHTML=0+"<input type='hidden' name='RCLTotal"+RowNumber+"' id='RCLTotal"+RowNumber+"' value='"+0+"'>";
			RCLTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].select();
			return false;
		}
}

function RCLchangeRate(RNum) //change the Rate according the the RCLBillingCycle Selected
{
	for(i=0;i<RCLBaseItemArray.length;i++)
	{
		if(RCLTableObject.childNodes[RNum].childNodes[1].childNodes[1].value==RCLBaseItemArray[i].RCLItemId)
		{
			//billing cycke
			RCLBillingCycleObjLen = RCLBaseItemArray[i].RCLBillingCycle.length;
			for(j=0;j<RCLBillingCycleObjLen;j++)
			{
				RCLBCId=RCLBaseItemArray[i].RCLBillingCycle[j].RCLBillingCycleId;
				RCLBCName=RCLBaseItemArray[i].RCLBillingCycle[j].RCLBillingCycleName;
				RCLBCRate=RCLBaseItemArray[i].RCLBillingCycle[j].RCLBillingCycleRate;
				RCLBCPeriod=RCLBaseItemArray[i].RCLBillingCycle[j].RCLBillingCyclePeriod;
				//alert(RCLTableObject.childNodes[RNum].childNodes[2].childNodes[0].value);
				if (RCLTableObject.childNodes[RNum].childNodes[3].childNodes[0].value==RCLBCId)
				{
					//alert(RCLBCId + " : " + RCLBCName + " : " + RCLBCRate)
					RCLTableObject.childNodes[RNum].childNodes[5].innerHTML=parseFloat(RCLBCRate)+"<input type='hidden' name='RCLRate"+RNum+"' id='RCLRate"+RNum+"' value='"+parseFloat(RCLBCRate)+"'>";
					
					RCLTableObject.childNodes[RNum].childNodes[3].childNodes[1].value=RCLBCPeriod;
				}
			}
		}
	}
	RCLBaseItemArray[RNum-1].SBillCycleId = document.getElementById('RCLBillingCycle'+RNum).value;
	RCLchangeTotalValues(RNum);
	RCLupdateTotalsRow();
}

function RCLupdateTotalsRow() //Updates the values of the Totals Row
{

		TotalRate=0;
		TotalAmount=0;
		TotalTax=0;
		TotalValue=0;
		RCLtRows = parseFloat(RCLTableObject.children.length)-1;
		for(r=1;r<RCLtRows;r++)
		{
			TotalRate = TotalRate+parseFloat(RCLTableObject.childNodes[r].childNodes[5].innerText);
			TotalAmount = TotalAmount+parseFloat(RCLTableObject.childNodes[r].childNodes[6].innerText);
			TotalTax = TotalTax+parseFloat(RCLTableObject.childNodes[r].childNodes[7].innerText);
			TotalValue = TotalValue+parseFloat(RCLTableObject.childNodes[r].childNodes[8].innerText);
		}
		TotalRate = (Math.round(TotalRate*100.0)/100.0);
		TotalAmount = (Math.round(TotalAmount*100.0)/100.0);
		TotalTax = (Math.round(TotalTax*100.0)/100.0);
		TotalValue = (Math.round(TotalValue*100.0)/100.0);
		RCLTableObject.childNodes[RCLtRows].childNodes[5].innerText=TotalRate;
		RCLTableObject.childNodes[RCLtRows].childNodes[6].innerText=TotalAmount;
		RCLTableObject.childNodes[RCLtRows].childNodes[7].innerText=TotalTax;
		RCLTableObject.childNodes[RCLtRows].childNodes[8].innerText=TotalValue;

}

function RCLremoveRow(object) //Removes the row via DOM and also deletes the ITEM from the Data Structure
{	

	if(confirm("Are You Sure you want to remove the Particular"))		
	{
		while (object.tagName !=  'TR')
		{
			object = object.parentNode;
		}
		RowNumber = object.rowIndex;
	
		ArrayIndex=RowNumber-1;
		for(i=0,j=1;i<RCLBaseItemArray.length;i++,j++)
		{
			RCLBaseItemArray[i].RCLQuantity = RCLTableObject.childNodes[j].childNodes[4].childNodes[0].value;
		}
	
		RCLTableObject.deleteRow(RowNumber); //Removes the row via DOM
		RCLBaseItemArray.splice(ArrayIndex, 1); //deletes the ITEM from the Data Structure
	//The following two functions are called just to rename the names of the controls on the form
		RCLDeleteAllRows(); //Deletes all rows of the table except the header
		RCLpopulateTable(); //Draws the complete table based on the data structure
	//
		//RCLAssignIds(); //Regenerates the Serial Nos.
	}
}

function RCLDeleteAllRows() // Deletes all rows of the table except the header
{
	totalRCLRows = RCLTableObject.children.length;
	for(i=1;i<totalRCLRows;i++)
	{
		RCLTableObject.deleteRow(1);
	}
}

function RCLAssignIds() //Regenerates the Serial Nos.
{
	totalRCLRows = RCLTableObject.children.length;
	RCLContentRows = totalRCLRows-2;
	for(i=1;i<=RCLContentRows;i++)
	{
		RCLTableObject.childNodes[i].childNodes[0].innerText=i;
	}
	
}

function updateRCLBillingCycles()
{
	for(i=0; i<RCLBaseItemArray.length; i++)
	{
		comboName = 'RCLBillingCycle'+(i+1);
		bCombo = document.getElementById(comboName);
		//alert('array selected value '+RCLBaseItemArray[i].SBillCycleId);
		for(x=0;x<bCombo.length;x++)
		{
			if(bCombo.options[x].value==RCLBaseItemArray[i].SBillCycleId)
			{
				bCombo.options[x].selected = true;
				break;
			}
		}
	}
}