// JavaScript Document
nre= /^\d+$/;
var PPBaseItemArray = new Array();
var PPItemsArray = new Array();
//var PPTempPPItemsArray = new Array();
var PPtRows =0;

///////DATA STRUCTURE//////////////////
	/*var PPItemObject = new Object();
	var PPBillingCycleArray = new Array();
	
	PPItemObject.PPItemId = "I0004";
	PPItemObject.PPItemName="PP Item";
	PPItemObject.PPItemTypeId="Item";
	PPItemObject.PPTaxValue=40;
	PPItemObject.PPRate=4000;
	PPItemObject.PPQuantity=2;
	PPItemObject.PPDepositId=null;
	PPItemObject.PPCalledFrom="PP";

	PPItemsArray[0] = PPItemObject;

	PPBaseItemArray[0]=PPItemsArray[0];*/
////////////END OF DATA STRUCTURE////////


function PPpopulateTable() //Read from the Data Structure and draw the table
{
	//////alert("Inside PPpopulateTable");

	PPDeleteAllRows();
	//////alert("Setting Pre Paid Item" + PPBaseItemArray.length);
	eval("document."+formname+".PPCount.value=PPBaseItemArray.length")
	//document.frmCreatePackage.PPCount.value=PPBaseItemArray.length
	for(i=0;i<PPBaseItemArray.length;i++)
	{
		PPItemId=PPBaseItemArray[i].PPItemId;
		PPItemName=PPBaseItemArray[i].PPItemName;
		PPBillingType = PPBaseItemArray[i].PPBillingType; 
		PPItemTypeId=PPBaseItemArray[i].PPItemTypeId;
		PPCalledFrom=PPBaseItemArray[i].PPCalledFrom;
		PPQuantity=PPBaseItemArray[i].PPQuantity;
		PPTaxValue=PPBaseItemArray[i].PPTaxValue;
		PPTaxId = PPBaseItemArray[i].PPTaxId;
		PPRate=PPBaseItemArray[i].PPRate;
		PPDepositId=PPBaseItemArray[i].PPDepositId;
		PPDepositType=PPBaseItemArray[i].PPDepositType;
		PPDepositCharge=PPBaseItemArray[i].PPDepositCharge;
		//////alert(PPBaseItemArray[i].PPDepositId);
	/*	if((PPBaseItemArray[i].PPDepositId != null)&&(PPCalledFrom != "PP"))
		{
			PPAddDepositRows();
		}
		else if((PPBaseItemArray[i].PPDepositId != null)&&(PPCalledFrom == "PP"))
		{
			PPaddRows();
			PPAddDepositRows();
		}
		else if(PPBaseItemArray[i].PPDepositId == null)
		{*/
			PPaddRows();
		//}
	}
/////////DRAW THE TOTALS ROW IN THE TABLE
		totalPPRows = PPTableObject.children.length;
		mynewrow = PPTableObject.insertRow(totalPPRows);
		for (c=0;c<9;c++)
		{
			mynewrow.insertCell();
			mynewrow.cells(c).innerHTML = "<td><b>&nbsp;<b></td>";
			mynewrow.cells(c).className="tblrows-right-bold";
		}
		mynewrow.cells(8).className="tblcol";
		
		PPTableObject.childNodes[totalPPRows].childNodes[3].innerText="Total";
		PPupdateTotalsRow();
}

function PPaddRows() //draws the content rows in the table
{
		totalPPRows = PPTableObject.children.length;
		PPContentRows = totalPPRows-2;
		RCLastRow=totalPPRows-1;
		FirstRow=RCLastRow+2;
		mynewrow = PPTableObject.insertRow(totalPPRows);
		for (c=0;c<9;c++)
		{
			mynewrow.insertCell();
			mynewrow.cells(c).innerHTML = "<td>&nbsp;</td>";
			mynewrow.cells(c).className="tblrows";
		}
		for(c=4;c<8;c++)
		{
			mynewrow.cells(c).className="tblrows-right";
		}
		mynewrow.cells(0).align="right";
		mynewrow.cells(8).className="tblcol";
		mynewrow.cells(8).align="center";
		
		//Sr. No. + TaxValue + DepositId + CalledFrom
		PPTableObject.childNodes[totalPPRows].childNodes[0].innerHTML=totalPPRows+"."+"<input type='hidden' name='PPTaxValue"+totalPPRows+"' id='PPTaxValue"+totalPPRows+"' value='"+PPTaxValue+"'>"+"<input type='hidden' name='PPDepositId"+totalPPRows+"' id='PPDepositId"+totalPPRows+"' value='"+PPDepositId+"'>"+"<input type='hidden' name='CalledFrom"+totalPPRows+"' id='CalledFrom"+totalPPRows+"' value='"+PPCalledFrom+"'>";
		
		//ItemName and ItemID and ItemTypeId
		PPTableObject.childNodes[totalPPRows].childNodes[1].innerHTML=PPItemName+"<input type='hidden' name='PPItemId"+totalPPRows+"' id='PPItemId"+totalPPRows+"' value='"+PPItemId+"'>"+"<input type='hidden' name='PPItemTypeId"+totalPPRows+"' id='PPItemTypeId"+totalPPRows+"' value='"+PPItemTypeId+"'>"+"<input type='hidden' name='PPItemName"+totalPPRows+"' id='PPItemName"+totalPPRows+"' value='"+PPItemName+"'>";

		//BillingType
		PPTableObject.childNodes[totalPPRows].childNodes[2].innerHTML=PPBillingType;
		
		//Quantity Textbox
		PPTableObject.childNodes[totalPPRows].childNodes[3].innerHTML="<input type='text' size='5' class='amount-text' maxlength='2' name='PPQty"+totalPPRows+"' value='"+PPQuantity+"' onChange='PPchangeTotalValues("+totalPPRows+");PPupdateTotalsRow();'>";

		//Rate
		PPTableObject.childNodes[totalPPRows].childNodes[4].innerHTML=PPRate+"<input type='hidden' name='PPRate"+totalPPRows+"' id='PPRate"+totalPPRows+"' value='"+PPRate+"'>";


		//RemoveImage
		PPTableObject.childNodes[totalPPRows].childNodes[8].innerHTML=" <a href='javascript:void(0)' onclick='PPremoveRow(this.parentElement)'><img src='/images/minus.jpg' border='0' alt='Remove' ></a>"
		//////alert(PPContentRows);

		PPchangeTotalValues(totalPPRows);

}

function PPAddDepositRows() //draws the content rows in the table
{
		totalPPRows = PPTableObject.children.length;
		PPContentRows = totalPPRows-2;
		RCLastRow=totalPPRows-1;
		FirstRow=RCLastRow+2;
		mynewrow = PPTableObject.insertRow(totalPPRows);
		for (c=0;c<9;c++)
		{
			mynewrow.insertCell();
			mynewrow.cells(c).innerHTML = "<td>&nbsp;</td>";
			mynewrow.cells(c).className="tblrows";
		}
		for(c=4;c<8;c++)
		{
			mynewrow.cells(c).className="tblrows-right";
		}
		mynewrow.cells(8).className="tblcol";
		mynewrow.cells(8).align="center";
		
		//Sr. No. + TaxValue + DepositId + CalledFrom
		PPTableObject.childNodes[totalPPRows].childNodes[0].innerHTML=totalPPRows+"<input type='hidden' name='PPTaxValue"+totalPPRows+"' id='PPTaxValue"+totalPPRows+"' value='"+PPTaxValue+"'>"+"<input type='hidden' name='PPDepositId"+totalPPRows+"' id='PPDepositId"+totalPPRows+"' value='"+PPDepositId+"'>"+"<input type='hidden' name='CalledFrom"+totalPPRows+"' id='CalledFrom"+totalPPRows+"' value='"+PPCalledFrom+"'>";
		
		//ItemName and ItemID and ItemTypeId
		PPTableObject.childNodes[totalPPRows].childNodes[1].innerHTML=PPDepositType+" : "+PPItemName+"<input type='hidden' name='PPItemId"+totalPPRows+"' id='PPItemId"+totalPPRows+"' value='"+PPItemId+"'>"+"<input type='hidden' name='PPItemTypeId"+totalPPRows+"' id='PPItemTypeId"+totalPPRows+"' value='"+PPItemTypeId+"'>"+"<input type='hidden' name='PPItemName"+totalPPRows+"' id='PPItemName"+totalPPRows+"' value='"+PPItemName+"'>";
	
		//BillingType
		PPTableObject.childNodes[totalPPRows].childNodes[2].innerHTML=PPBillingType;
	
		//Quantity
		PPTableObject.childNodes[totalPPRows].childNodes[3].innerHTML=PPQuantity+"<input type='hidden' name='PPQty"+totalPPRows+"' id='PPQty"+totalPPRows+"' value='"+PPQuantity+"'>";
		
		//Rate
		PPTableObject.childNodes[totalPPRows].childNodes[4].innerHTML=PPDepositCharge+"<input type='hidden' name='PPRate"+totalPPRows+"' id='PPRate"+totalPPRows+"' value='"+parseFloat(PPDepositCharge)+"'>";
		
		//Amount
		PPAmount = (parseFloat(PPQuantity) * parseFloat(PPDepositCharge));
		PPTableObject.childNodes[totalPPRows].childNodes[5].innerHTML = PPAmount+"<input type='hidden' name='PPAmount"+totalPPRows+"' id='PPAmount"+totalPPRows+"' value='"+PPAmount+"'>";
		
		//Tax=
		Tax=0;
		PPTableObject.childNodes[totalPPRows].childNodes[6].innerHTML=0+"<input type='hidden' name='PPTax"+totalPPRows+"' id='PPTax"+totalPPRows+"' value='"+0+"'>"+"<input type='hidden' name='PPTaxId"+totalPPRows+"' id='PPTaxId"+totalPPRows+"' value='"+PPTaxId+"'>";
		
		//PPTotal
		PPTotal = parseFloat(PPAmount) + parseFloat(Tax);
		PPTableObject.childNodes[totalPPRows].childNodes[7].innerHTML=0+"<input type='hidden' name='PPTotal"+totalPPRows+"' id='PPTotal"+totalPPRows+"' value='"+PPTotal+"'>";

		
}//PPAddDepositRows

function PPchangeTotalValues(RNumber) //Updates the values of 'Amount' and 'Total' column for each row
{
		RowNumber = RNumber;
		//amount=qty*rate
		if(nre.test(PPTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value) && parseInt(PPTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value)>0)
		{
			//Amount
			PPAmount = parseFloat(PPTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value)*parseFloat(PPTableObject.childNodes[RowNumber].childNodes[4].innerText);
			PPAmount=(Math.round(PPAmount*100.0)/100.00);

			PPAmount = convertCurrency(PPAmount);
//			////alert("after currency PPAmount="+PPAmount);

			PPTableObject.childNodes[RowNumber].childNodes[5].innerHTML=PPAmount + "<input type='hidden' name='PPAmount"+RowNumber+"' id='PPAmount"+RowNumber+"' value='"+PPAmount+"'>";;
			//Tax
			PPTaxValue =PPTableObject.childNodes[RowNumber].childNodes[0].childNodes[1].value;
			PPTaxRatio=(PPAmount * PPTaxValue)/100;
			PPTaxRatio=(Math.round(PPTaxRatio*100.0)/100.00);

			PPTaxRatio = convertCurrency(PPTaxRatio);
//			////alert("after currency PPTaxRatio="+PPTaxRatio);

			PPTableObject.childNodes[RowNumber].childNodes[6].innerHTML=PPTaxRatio+"<input type='hidden' name='PPTax"+RowNumber+"' id='PPTax"+RowNumber+"' value='"+PPTaxRatio+"'>"+"<input type='hidden' name='PPTaxId"+RowNumber+"' id='PPTaxId"+RowNumber+"' value='"+PPTaxId+"'>";

//if Item has Deposit, change the qty of the Item Deposit to equalise it with the qty of Item
			//PPQty
			//(DepositId != null)
/*			
			if(PPTableObject.childNodes[RowNumber].childNodes[0].childNodes[2].value != null)
			{
				MainItemId = PPTableObject.childNodes[RowNumber].childNodes[1].childNodes[1].value;
				PPRows = PPTableObject.children.length-2;
				for(rctr=2;rctr<=PPRows;rctr++)
				{
					if(PPTableObject.childNodes[rctr].childNodes[1].childNodes[1].value == MainItemId)
					{
						//Deposit Row Qty = Main Qty
						//////alert(rctr);
						PPTableObject.childNodes[rctr+1].childNodes[2].innerHTML = PPTableObject.childNodes[RowNumber].childNodes[2].childNodes[0].value;
						break;
					}
				}
			}
*/			
////end of changes in deposit row


			
			//total=amount+tax;(if qty>0,total=amount+tax, else total=0)
			if(parseFloat(PPTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value)>0)
			{
				PPTotal = parseFloat(PPTableObject.childNodes[RowNumber].childNodes[5].innerText)+parseFloat(PPTableObject.childNodes[RowNumber].childNodes[6].innerText)
				PPTotal=(Math.round(PPTotal*100.0)/100.00);
				PPTotal = convertCurrency(PPTotal);
//				////alert("after currency PPTotal*="+PPTotal);

				PPTableObject.childNodes[RowNumber].childNodes[7].innerHTML=PPTotal+"<input type='hidden' name='PPTotal"+RowNumber+"' id='PPTotal"+RowNumber+"' value='"+PPTotal+"'>";
			}
			else
			{
				PPTableObject.childNodes[RowNumber].childNodes[7].innerHTML=0+"<input type='hidden' name='PPTotal"+RowNumber+"' id='PPTotal"+RowNumber+"' value='"+0+"'>";
			}

			//Updating the Qty in DataStructure
			PPBaseItemArray[RowNumber-1].PPQuantity = PPTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value;

			for(actr=0;actr<PPBaseItemArray.length;actr++)
			   {
				//BaseItemArray[actr].PPItemId = RCTable.ItemId
				if(PPBaseItemArray[actr].PPItemId == PPTableObject.childNodes[RowNumber].childNodes[1].childNodes[1].value)
				{
				 PPBaseItemArray[actr].PPQuantity = PPTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value;
				 break;
				}
			   }
			/////////////////////////////

		}
		else
		{
			alert("Quantity should be an integer greater than zero.");
			PPBaseItemArray[RowNumber-1].PPQuantity =0;
			PPTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value=0;
			PPTableObject.childNodes[RowNumber].childNodes[5].innerText=0;
			PPTableObject.childNodes[RowNumber].childNodes[7].innerHTML=0+"<input type='hidden' name='PPTotal"+RowNumber+"' id='PPTotal"+RowNumber+"' value='"+0+"'>";
			PPTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].select();
			return false;
		}
}

function PPupdateTotalsRow() //Updates the values of the Totals Row
{
		PPTotalRate=0.00;
		PPTotalAmount=0.00;
		PPTotalTax=0.00;
		PPTotalValue=0.00;

		PPtRows = parseFloat(PPTableObject.children.length)-1;
		for(r=1;r<PPtRows;r++)
		{
			PPTotalRate = PPTotalRate+parseFloat(PPTableObject.childNodes[r].childNodes[4].innerText);
			PPTotalAmount = PPTotalAmount+parseFloat(PPTableObject.childNodes[r].childNodes[5].innerText);
			PPTotalTax = PPTotalTax+parseFloat(PPTableObject.childNodes[r].childNodes[6].innerText);
			PPTotalValue = PPTotalValue+parseFloat(PPTableObject.childNodes[r].childNodes[7].innerText);
		}
		PPTotalRate = (Math.round(PPTotalRate*100.0)/100.0);
		PPTotalAmount = (Math.round(PPTotalAmount*100.0)/100.0);
		PPTotalTax = (Math.round(PPTotalTax*100.0)/100.0);
		PPTotalValue = (Math.round(PPTotalValue*100.0)/100.0);
		PPTotalRate = convertCurrency(PPTotalRate);
//		////alert("after currency PPTotalRate="+PPTotalRate);

		PPTotalAmount = convertCurrency(PPTotalAmount);
//		////alert("after currency PPTotalAmount="+PPTotalAmount);

		PPTotalTax = convertCurrency(PPTotalTax);
//		////alert("after currency PPTotalTax="+PPTotalTax);

		PPTotalValue = convertCurrency(PPTotalValue);
//		////alert("after currency PPTotalValue="+PPTotalValue);

		PPTableObject.childNodes[PPtRows].childNodes[4].innerText=PPTotalRate;
		PPTableObject.childNodes[PPtRows].childNodes[5].innerText=PPTotalAmount;
		PPTableObject.childNodes[PPtRows].childNodes[6].innerText=PPTotalTax;
		PPTableObject.childNodes[PPtRows].childNodes[7].innerText=PPTotalValue;

		if(eval("document."+formname+".c_bApplyDiscount"))
		{
			calcDiscount();
		}
}

function calcDiscount()
{
//check whether disocount is applied and update total value
	if(eval("document."+formname+".c_bApplyDiscount"))
	{
		totalRows = PPTableObject.children.length-1;
		totalDiscountRows = DiscountTableObject.children.length-1;
		TotalTableRows = TotalTableObject.children.length;
		RoundingAmountTotal=0;
		discountApplied=0;
		AmountBeforeDiscount=0;
		BillAmount=0;
		
		if (eval("document."+formname+".c_bApplyDiscount.checked == false"))
		{

			for(ctr=0;ctr<TotalTableRows;ctr++)
			{
				/*if(TotalTableObject.childNodes[ctr].id=='RoundingAmount')
				{
					RoundingAmountTotal = parseFloat(TotalTableObject.childNodes[ctr].childNodes[1].innerText);
				}*/


				if(TotalTableObject.childNodes[ctr].id=='TotalAmount')
				{
					//Total = Total - Rounding Amount
					BillAmount=parseFloat(PPTableObject.childNodes[totalRows].childNodes[7].innerText);
					BillAmount = convertCurrency(BillAmount);
//					////alert("after currency BillAmount*="+BillAmount);
					var finalBillAmt = convertCurrency(BillAmount-RoundingAmountTotal);
					TotalTableObject.childNodes[ctr].childNodes[1].innerText=finalBillAmt;

				}
			}
		}
		else
		{
			//////alert("true")
			discountApplied=0;
			
			for(ctr=1;ctr<=totalDiscountRows;ctr++)
			{
				if(DiscountTableObject.childNodes[ctr].id=='discountContentRow')
				{
					discountApplied = discountApplied + parseFloat(DiscountTableObject.childNodes[ctr].childNodes[4].innerText)		
				}
			}

			for(ctr=1;ctr<=totalDiscountRows;ctr++)
			{
				if(DiscountTableObject.childNodes[ctr].id=='discountAmount')
				{
					discountAmountRow = ctr;
					AmountBeforeDiscount = parseFloat(PPTableObject.childNodes[totalRows].childNodes[7].innerText);

					var finalBillAmt = convertCurrency(AmountBeforeDiscount-discountApplied);
					DiscountTableObject.childNodes[ctr].childNodes[1].innerText = finalBillAmt;
					
				}
				/*if(DiscountTableObject.childNodes[ctr].id=='discountRoundingAmount')
				{
					discountRoundingAmountRow = ctr;
				}*/
				if(DiscountTableObject.childNodes[ctr].id=='discountBillAmount')
				{
					discountBillAmountRow = ctr;
					//Total Amount = AmountAfterDiscount - RoundingDiscountAmount
					DiscountTableObject.childNodes[ctr].childNodes[1].innerText=convertCurrency(parseFloat(DiscountTableObject.childNodes[discountAmountRow].childNodes[1].innerText));
				}
				
			}
		}
	}

}


/////////////////////////////////---------------
/*
function calcDiscount()
{
//check whether disocount is applied and update total value
 if(eval("document."+formname+".c_bApplyDiscount"))
 {
  totalRows = PPTableObject.children.length-1;
  totalDiscountRows = DiscountTableObject.children.length-1;
  TotalTableRows = TotalTableObject.children.length;
  RoundingAmountTotal=0;
  discountApplied=0;
  AmountBeforeDiscount=0;
  BillAmount=0;

  if (eval("document."+formname+".c_bApplyDiscount.checked == false"))
  {

   for(ctr=0;ctr<TotalTableRows;ctr++)
   {
    if(TotalTableObject.childNodes[ctr].id=='RoundingAmount')
    {
     if(totalRows>1)
     {
      RoundingAmountTotal =
parseFloat(TotalTableObject.childNodes[ctr].childNodes[1].innerText);
     }
     else
     {
      RoundingAmountTotal=0;
     }
    }

    if(TotalTableObject.childNodes[ctr].id=='TotalAmount')
    {
     //Total = Total - Rounding Amount
     if(totalRows>1)
     {

BillAmount=parseFloat(PPTableObject.childNodes[totalRows].childNodes[6].inn
erText);
     }
     else
     {
      BillAmount=0;
     }

TotalTableObject.childNodes[ctr].childNodes[1].innerText=BillAmount-Rounding
AmountTotal;
    }

   }
  }
  else
  {
   //////alert("true")
   discountApplied=0;

   for(ctr=1;ctr<=totalDiscountRows;ctr++)
   {
    if(DiscountTableObject.childNodes[ctr].id=='discountContentRow')
    {
     discountApplied = discountApplied +
parseFloat(DiscountTableObject.childNodes[ctr].childNodes[3].innerText)
    }
   }

   for(ctr=1;ctr<=totalDiscountRows;ctr++)
   {
    if(DiscountTableObject.childNodes[ctr].id=='discountAmount')
    {
     discountAmountRow = ctr;
     if(totalRows>1)
     {
      AmountBeforeDiscount =
parseFloat(PPTableObject.childNodes[totalRows].childNodes[6].innerText);
     }
     else
     {
      AmountBeforeDiscount=0;
     }
     DiscountTableObject.childNodes[ctr].childNodes[1].innerText =
AmountBeforeDiscount-discountApplied;

    }
    if(DiscountTableObject.childNodes[ctr].id=='discountRoundingAmount')
    {
     discountRoundingAmountRow = ctr;
    }
    if(DiscountTableObject.childNodes[ctr].id=='discountBillAmount')
    {
     discountBillAmountRow = ctr;
     //Total Amount = AmountAfterDiscount - RoundingDiscountAmount

DiscountTableObject.childNodes[ctr].childNodes[1].innerText=parseFloat(Disco
untTableObject.childNodes[discountAmountRow].childNodes[1].innerText)-parseF
loat(DiscountTableObject.childNodes[discountRoundingAmountRow].childNodes[1]
.innerText);
    }

   }
  }
 }

}
*/

/////////////////////////////////---------------------





function PPremoveRow(object) //Removes the row via DOM and also deletes the ITEM from the Data Structure
{	

	if(confirm("Are You Sure you want to remove the Particular"))		
	{
		while (object.tagName !=  'TR')
		{
			object = object.parentNode;
		}
		RowNumber = object.rowIndex;
	
		ArrayIndex=RowNumber-1;
		for(i=0,j=1;i<PPBaseItemArray.length;i++,j++)
		{
			PPBaseItemArray[i].PPQuantity = PPTableObject.childNodes[j].childNodes[3].childNodes[0].value;
		}
	
		PPTableObject.deleteRow(RowNumber); //Removes the row via DOM
		PPBaseItemArray.splice(ArrayIndex, 1); //deletes the ITEM from the Data Structure
	//The following two functions are called just to rename the names of the controls on the form
		PPDeleteAllRows(); //Deletes all rows of the table except the header
		PPpopulateTable(); //Draws the complete table based on the data structure
	//
		//PPAssignIds(); //Regenerates the Serial Nos.
	}
}

function PPDeleteAllRows() // Deletes all rows of the table except the header
{
	totalPPRows = PPTableObject.children.length;
	for(i=1;i<totalPPRows;i++)
	{
		PPTableObject.deleteRow(1);
	}
	
}

function PPAssignIds() //Regenerates the Serial Nos.
{
	totalPPRows = PPTableObject.children.length;
	PPContentRows = totalPPRows-2;
	for(i=1;i<=PPContentRows;i++)
	{
		PPTableObject.childNodes[i].childNodes[0].innerText=i;
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
