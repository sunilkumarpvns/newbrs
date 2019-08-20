// JavaScript Document
nre= /^\d+$/;
var BaseItemArray = new Array();
var ItemsArray = new Array();
//var TempItemsArray = new Array();
var popupOpen = false;
var tRows =0;

///////DATA STRUCTURE//////////////////

/*	var ItemObject = new Object();
	var BillingCycleArray = new Array();
	
	ItemObject.ItemId = "I0001";
	ItemObject.ItemName="Item 1 - RC";
	ItemObject.ItemTypeId="Item";
	ItemObject.TaxValue=50;
	ItemObject.Quantity=1;

	//FIRST BILLING CYCLE ATTACHED WITH THE ITEM
	var BillingCycleObject = new Object();
	BillingCycleObject.BillingCycleId="BCY004";
	BillingCycleObject.BillingCycleName="Monthly";
	BillingCycleObject.BillingCycleRate=1000;
	BillingCycleArray[0] = BillingCycleObject;

	ItemObject.BillingCycle = BillingCycleArray;

	ItemsArray[0] = ItemObject;

	BaseItemArray[0]=ItemsArray[0]; */
////////////END OF DATA STRUCTURE////////


function populateTable() //Read from the Data Structure and draw the table
{
	DeleteAllRows();
	//document.frmCreatePackage.RCCount.value=BaseItemArray.length
	eval("document."+formname+".RCCount.value=BaseItemArray.length");
	for(i=0;i<BaseItemArray.length;i++)
	{
		ItemId=BaseItemArray[i].ItemId;
		ItemName=BaseItemArray[i].ItemName;
		ItemTypeId=BaseItemArray[i].ItemTypeId;
		ItemBillingType=BaseItemArray[i].ItemBillingType;
		Quantity=BaseItemArray[i].Quantity;
		TaxValue=BaseItemArray[i].TaxValue;
		DepositId=BaseItemArray[i].DepositId;
		DepositType=BaseItemArray[i].DepositType;
		DepositCharge=BaseItemArray[i].DepositCharge;
		BillingCycleObjectLen = BaseItemArray[i].BillingCycle.length;
	
		addRows();
	}
/////////DRAW THE TOTALS ROW IN THE TABLE
		totalRCRows = RCTableObject.children.length;
		mynewrow = RCTableObject.insertRow(totalRCRows);
		for (c=0;c<10;c++)
		{
			mynewrow.insertCell();
			mynewrow.cells(c).innerHTML = "<td><b>&nbsp;<b></td>";
			mynewrow.cells(c).className="tblrows-right-bold";
		}
		mynewrow.cells(9).className="tblcol";
		
		RCTableObject.childNodes[totalRCRows].childNodes[4].innerText="Total";
		updateTotalsRow();
}

function addRows() //draws the content rows in the table
{
		totalRCRows = RCTableObject.children.length;
		RCContentRows = totalRCRows-2;
		RCLastRow=totalRCRows-1;
		FirstRow=RCLastRow+2;
		mynewrow = RCTableObject.insertRow(totalRCRows);
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
		
		//Sr. No. + TaxValue + DepositId
		RCTableObject.childNodes[totalRCRows].childNodes[0].innerHTML=totalRCRows+"."+"<input type='hidden' name='TaxValue"+totalRCRows+"' id='TaxValue"+totalRCRows+"' value='"+TaxValue+"'>"+"<input type='hidden' name='DepositId"+totalRCRows+"' id='DepositId"+totalRCRows+"' value='"+DepositId+"'>";
		
		//ItemName and ItemID and ItemTypeId
		RCTableObject.childNodes[totalRCRows].childNodes[1].innerHTML=ItemName+"<input type='hidden' name='RCItemId"+totalRCRows+"' id='RCItemId"+totalRCRows+"' value='"+ItemId+"'>"+"<input type='hidden' name='RCItemTypeId"+totalRCRows+"' id='RCItemTypeId"+totalRCRows+"' value='"+ItemTypeId+"'>";
		
		// ItemBillingType
		RCTableObject.childNodes[totalRCRows].childNodes[2].innerHTML=ItemBillingType;

		//BillngCycle ComboBox
		RCTableObject.childNodes[totalRCRows].childNodes[3].innerHTML="<select name='RCBillingCycle"+totalRCRows+"'  id='RCBillingCycle"+totalRCRows+"' onChange='changeRate("+totalRCRows+")'></select>";

		//Quantity Textbox
		RCTableObject.childNodes[totalRCRows].childNodes[4].innerHTML="<input type='text' class='amount-text' maxlength='2' size='5' name='RCQty"+totalRCRows+"' value='"+Quantity+"' onChange='changeTotalValues("+totalRCRows+");updateTotalsRow();'>";

		//RemoveImage
		RCTableObject.childNodes[totalRCRows].childNodes[9].innerHTML=" <a href='javascript:void(0)' onclick='removeRow(this.parentElement)'><img src='/images/minus.jpg' border='0' alt='Remove' ></a>"
		//alert(RCContentRows);

//POPULATE THE COMBOBOX
		totalElements=eval("document.frmCreatePackage.RCBillingCycle"+totalRCRows+".length");
		//alert("totalElements : "+totalElements);
		for(j=0;j<BillingCycleObjectLen;j++)
		{
			BillingCycleId=BaseItemArray[i].BillingCycle[j].BillingCycleId;
			BillingCycleName=BaseItemArray[i].BillingCycle[j].BillingCycleName;
			BillingCycleRate=BaseItemArray[i].BillingCycle[j].BillingCycleRate;
			
			var newoption=new Option (BillingCycleName,BillingCycleId);
			eval("document.frmCreatePackage.RCBillingCycle"+totalRCRows+".options[totalElements]= newoption");
			totalElements=totalElements+1;

			//Rate
			RCTableObject.childNodes[totalRCRows].childNodes[5].innerHTML=BaseItemArray[i].BillingCycle[0].BillingCycleRate+"<input type='hidden' name='RCRate"+totalRCRows+"' id='RCRate"+totalRCRows+"' value='"+BaseItemArray[i].BillingCycle[0].BillingCycleRate+"'>";
		}
			changeTotalValues(totalRCRows);
}

function changeTotalValues(RNumber) //Updates the values of 'Amount' and 'Total' column for each row
{
		RowNumber = RNumber;
		//amount=qty*rate
		
		if(nre.test(RCTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value) && parseInt(RCTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value)>0)
		{
			
			//Amount
			Amount=parseFloat(RCTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value)*parseFloat(RCTableObject.childNodes[RowNumber].childNodes[5].innerText)
			Amount=(Math.round(Amount*100.0)/100.00);			
			RCTableObject.childNodes[RowNumber].childNodes[6].innerHTML=Amount;

			//Tax
			TaxValue =RCTableObject.childNodes[RowNumber].childNodes[0].childNodes[1].value;
			
			TaxRatio=(Amount * TaxValue)/100;
			TaxRatio=(Math.round(TaxRatio*100.0)/100.00);
			RCTableObject.childNodes[RowNumber].childNodes[7].innerHTML=TaxRatio+"<input type='hidden' name='RCTax"+RowNumber+"' id='RCTax"+RowNumber+"' value='"+TaxRatio+"'>";

//if Item has Deposit, change the qty in NRC Table to equalise it with the qty in RC table
			//NRCQty
			//(DepositId != null)
			if(RCTableObject.childNodes[RowNumber].childNodes[0].childNodes[2].value != null)
			{
				NRCRows = NRCTableObject.children.length-2;
				for(rctr=1;rctr<=NRCRows;rctr++)
				{
					//NRCTable.ItemId.value == RCTable.ItemId.value
					if(NRCTableObject.childNodes[rctr].childNodes[1].childNodes[1].value == RCTableObject.childNodes[RowNumber].childNodes[1].childNodes[1].value)
					{
						//NRCQty = RCQty
						NRCQty = parseInt(RCTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value);
						NRCTableObject.childNodes[rctr].childNodes[3].innerHTML = NRCQty+"<input type='hidden' name='NRCQty"+rctr+"' id='NRCQty"+rctr+"' value='"+NRCQty+"'>";
						
						//NRCAmount=NRCQty * NRCDepositCharge(Rate)
						NRCDepositCharge = parseFloat(NRCTableObject.childNodes[rctr].childNodes[4].innerText);
						NRCTableObject.childNodes[rctr].childNodes[5].innerText=NRCQty * NRCDepositCharge;
						
						//NRCTtotal = NRCAmount;
						NRCTotal=NRCTableObject.childNodes[rctr].childNodes[5].innerText;
						NRCTableObject.childNodes[RowNumber].childNodes[7].innerHTML=NRCTotal+"<input type='hidden' name='NRCTotal"+rctr+"' id='NRCTotal"+rctr+"' value='"+NRCTotal+"'>";
						
						//update the totals row
						NRCupdateTotalsRow();
					}
				}
			}

//end of changes in nrc table

			
			//total=amount+tax;(if qty>0,total=amount+tax, else total=0)
			if(parseFloat(RCTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value)>0)
			{
				RCTotal = parseFloat(RCTableObject.childNodes[RowNumber].childNodes[6].innerText)+parseFloat(RCTableObject.childNodes[RowNumber].childNodes[7].innerText)
				
				RCTotal=(Math.round(RCTotal*100.0)/100.00);
				RCTableObject.childNodes[RowNumber].childNodes[8].innerHTML=RCTotal+"<input type='hidden' name='RCTotal"+RowNumber+"' id='RCTotal"+RowNumber+"' value='"+RCTotal+"'>";

			}
			else
			{
				RCTableObject.childNodes[RowNumber].childNodes[8].innerHTML=0+"<input type='hidden' name='RCTotal"+RowNumber+"' id='RCTotal"+RowNumber+"' value='"+0+"'>";
			}
//Updating the Qty in DataStructure
//alert('New quantity = '+RCTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value+' and base item quantity '+BaseItemArray[RowNumber-1].Quantity);
BaseItemArray[RowNumber-1].Quantity = RCTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value;

for(actr=0;actr<BaseItemArray.length;actr++)
   {
    //BaseItemArray[actr].NRCItemId = RCTable.ItemId   
	
	if(BaseItemArray[actr].ItemId == RCTableObject.childNodes[RowNumber].childNodes[1].childNodes[1].value)
    {
     BaseItemArray[actr].Quantity = RCTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value;
     break;
    }
   }
/////////////////////////////
		}
		else
		{
			alert("Quantity should be an integer greater than zero.");
			BaseItemArray[RowNumber-1].Quantity =0;
			RCTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].value=0;
			RCTableObject.childNodes[RowNumber].childNodes[6].innerText=0;
			RCTableObject.childNodes[RowNumber].childNodes[8].innerHTML=0+"<input type='hidden' name='RCTotal"+RowNumber+"' id='RCTotal"+RowNumber+"' value='"+0+"'>";
			RCTableObject.childNodes[RowNumber].childNodes[4].childNodes[0].select();
			return false;
		}
}

function changeRate(RNum) //change the Rate according the the BillingCycle Selected
{
	for(i=0;i<BaseItemArray.length;i++)
	{
		if(RCTableObject.childNodes[RNum].childNodes[1].childNodes[1].value==BaseItemArray[i].ItemId)
		{
			//billing cycke
			BillingCycleObjLen = BaseItemArray[i].BillingCycle.length;
			for(j=0;j<BillingCycleObjLen;j++)
			{
				BCId=BaseItemArray[i].BillingCycle[j].BillingCycleId;
				BCName=BaseItemArray[i].BillingCycle[j].BillingCycleName;
				BCRate=BaseItemArray[i].BillingCycle[j].BillingCycleRate;
				//alert(RCTableObject.childNodes[RNum].childNodes[2].childNodes[0].value);
				if (RCTableObject.childNodes[RNum].childNodes[3].childNodes[0].value==BCId)
				{
					//Rate
					RCTableObject.childNodes[RNum].childNodes[5].innerHTML=parseFloat(BCRate)+"<input type='hidden' name='RCRate"+RNum+"' id='RCRate"+RNum+"' value='"+parseFloat(BCRate)+"'>";
			
				}
			}
		}
	}
	BaseItemArray[RNum-1].SBillCycleId = document.getElementById('RCBillingCycle'+RNum).value;
	changeTotalValues(RNum);
	updateTotalsRow();
}

function updateTotalsRow() //Updates the values of the Totals Row
{

		TotalRate=0;
		TotalAmount=0;
		TotalTax=0;
		TotalValue=0;
		tRows = parseFloat(RCTableObject.children.length)-1;
		for(r=1;r<tRows;r++)
		{
			TotalRate = TotalRate+parseFloat(RCTableObject.childNodes[r].childNodes[5].innerText);			
			TotalAmount = TotalAmount+parseFloat(RCTableObject.childNodes[r].childNodes[6].innerText);			
			TotalTax = TotalTax+parseFloat(RCTableObject.childNodes[r].childNodes[7].innerText);
			TotalValue = TotalValue+parseFloat(RCTableObject.childNodes[r].childNodes[8].innerText);
		}

		TotalRate=(Math.round(TotalRate*100.0)/100.00);
		TotalAmount=(Math.round(TotalAmount*100.0)/100.00);
		TotalTax=(Math.round(TotalTax*100.0)/100.00);
		TotalValue=(Math.round(TotalValue*100.0)/100.00);
		RCTableObject.childNodes[tRows].childNodes[5].innerText=TotalRate;
		RCTableObject.childNodes[tRows].childNodes[6].innerText=TotalAmount;
		RCTableObject.childNodes[tRows].childNodes[7].innerText=TotalTax;
		RCTableObject.childNodes[tRows].childNodes[8].innerText=TotalValue;
		
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
		for(i=0,j=1;i<BaseItemArray.length;i++,j++)
		{
			BaseItemArray[i].Quantity = RCTableObject.childNodes[j].childNodes[4].childNodes[0].value;
		}
	
		RCTableObject.deleteRow(RowNumber); //Removes the row via DOM
		BaseItemArray.splice(ArrayIndex, 1); //deletes the ITEM from the Data Structure
	//The following two functions are called just to rename the names of the controls on the form
		DeleteAllRows(); //Deletes all rows of the table except the header
		populateTable(); //Draws the complete table based on the data structure
	//
		//AssignIds(); //Regenerates the Serial Nos.
	}
}

function DeleteAllRows() // Deletes all rows of the table except the header
{
	totalRCRows = RCTableObject.children.length;
	for(i=1;i<totalRCRows;i++)
	{
		RCTableObject.deleteRow(1);
	}
	
}

function AssignIds() //Regenerates the Serial Nos.
{
	totalRCRows = RCTableObject.children.length;
	RCContentRows = totalRCRows-2;
	for(i=1;i<=RCContentRows;i++)
	{
		RCTableObject.childNodes[i].childNodes[0].innerText=i;
	}
	
}

function updateRCBillingCycles()
{
	for(i=0; i<BaseItemArray.length; i++)
	{
		comboName = 'RCBillingCycle'+(i+1);
		bCombo = document.getElementById(comboName);
		//alert('array selected value '+BaseItemArray[i].SBillCycleId);
		for(x=0;x<bCombo.length;x++)
		{
			if(bCombo.options[x].value==BaseItemArray[i].SBillCycleId)
			{
				bCombo.options[x].selected = true;
				break;
			}
		}
	}
}
