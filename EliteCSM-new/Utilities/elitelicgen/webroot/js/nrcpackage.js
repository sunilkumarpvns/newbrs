// JavaScript Document
nre= /^\d+$/;
var NRCBaseItemArray = new Array();
var NRCItemsArray = new Array();
//var NRCTempNRCItemsArray = new Array();
var NRCtRows =0;

///////DATA STRUCTURE//////////////////
	/*var NRCItemObject = new Object();
	var NRCBillingCycleArray = new Array();
	
	NRCItemObject.NRCItemId = "I0004";
	NRCItemObject.NRCItemName="NRC Item";
	NRCItemObject.NRCItemTypeId="Item";
	NRCItemObject.NRCTaxValue=40;
	NRCItemObject.NRCRate=4000;
	NRCItemObject.NRCQuantity=2;
	NRCItemObject.NRCDepositId=null;
	NRCItemObject.NRCCalledFrom="NRC";

	NRCItemsArray[0] = NRCItemObject;

	NRCBaseItemArray[0]=NRCItemsArray[0];*/
////////////END OF DATA STRUCTURE////////


function NRCpopulateTable() //Read from the Data Structure and draw the table
{

	NRCDeleteAllRows();
	eval("document."+formname+".NRCCount.value=NRCBaseItemArray.length")
	//document.frmCreatePackage.NRCCount.value=NRCBaseItemArray.length
	for(i=0;i<NRCBaseItemArray.length;i++)
	{
		NRCItemId=NRCBaseItemArray[i].NRCItemId;
		NRCItemName=NRCBaseItemArray[i].NRCItemName;
		NRCBillingType = NRCBaseItemArray[i].NRCBillingType; 
		NRCItemTypeId=NRCBaseItemArray[i].NRCItemTypeId;
		NRCCalledFrom=NRCBaseItemArray[i].NRCCalledFrom;
		NRCQuantity=NRCBaseItemArray[i].NRCQuantity;
		NRCTaxValue=NRCBaseItemArray[i].NRCTaxValue;
		NRCTaxId = NRCBaseItemArray[i].NRCTaxId;
		NRCRate=NRCBaseItemArray[i].NRCRate;
		NRCDepositId=NRCBaseItemArray[i].NRCDepositId;
		NRCDepositType=NRCBaseItemArray[i].NRCDepositType;
		NRCDepositCharge=NRCBaseItemArray[i].NRCDepositCharge;
		//alert(NRCBaseItemArray[i].NRCDepositId);
	/*	if((NRCBaseItemArray[i].NRCDepositId != null)&&(NRCCalledFrom != "NRC"))
		{
			NRCAddDepositRows();
		}
		else if((NRCBaseItemArray[i].NRCDepositId != null)&&(NRCCalledFrom == "NRC"))
		{
			NRCaddRows();
			NRCAddDepositRows();
		}
		else if(NRCBaseItemArray[i].NRCDepositId == null)
		{*/
			NRCaddRows();
		//}
	}
/////////DRAW THE TOTALS ROW IN THE TABLE
		totalNRCRows = NRCTableObject.children.length;
		mynewrow = NRCTableObject.insertRow(totalNRCRows);
		for (c=0;c<9;c++)
		{
			mynewrow.insertCell();
			mynewrow.cells(c).innerHTML = "<td><b>&nbsp;<b></td>";
			mynewrow.cells(c).className="tblrows-right-bold";
		}
		mynewrow.cells(8).className="tblcol";
		
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
		NRCTableObject.childNodes[totalNRCRows].childNodes[0].innerHTML=totalNRCRows+"."+"<input type='hidden' name='NRCTaxValue"+totalNRCRows+"' id='NRCTaxValue"+totalNRCRows+"' value='"+NRCTaxValue+"'>"+"<input type='hidden' name='NRCDepositId"+totalNRCRows+"' id='NRCDepositId"+totalNRCRows+"' value='"+NRCDepositId+"'>"+"<input type='hidden' name='CalledFrom"+totalNRCRows+"' id='CalledFrom"+totalNRCRows+"' value='"+NRCCalledFrom+"'>";
		
		//ItemName and ItemID and ItemTypeId
		NRCTableObject.childNodes[totalNRCRows].childNodes[1].innerHTML=NRCItemName+"<input type='hidden' name='NRCItemId"+totalNRCRows+"' id='NRCItemId"+totalNRCRows+"' value='"+NRCItemId+"'>"+"<input type='hidden' name='NRCItemTypeId"+totalNRCRows+"' id='NRCItemTypeId"+totalNRCRows+"' value='"+NRCItemTypeId+"'>"+"<input type='hidden' name='NRCItemName"+totalNRCRows+"' id='NRCItemName"+totalNRCRows+"' value='"+NRCItemName+"'>";

		//BillingType
		NRCTableObject.childNodes[totalNRCRows].childNodes[2].innerHTML=NRCBillingType;
		
		//Quantity Textbox
		NRCTableObject.childNodes[totalNRCRows].childNodes[3].innerHTML="<input type='text' size='5' class='amount-text' maxlength='2' name='NRCQty"+totalNRCRows+"' value='"+NRCQuantity+"' onChange='NRCchangeTotalValues("+totalNRCRows+");NRCupdateTotalsRow();'>";

		//Rate
		NRCTableObject.childNodes[totalNRCRows].childNodes[4].innerHTML=NRCRate+"<input type='hidden' name='NRCRate"+totalNRCRows+"' id='NRCRate"+totalNRCRows+"' value='"+NRCRate+"'>";


		//RemoveImage
		NRCTableObject.childNodes[totalNRCRows].childNodes[8].innerHTML=" <a href='javascript:void(0)' onclick='NRCremoveRow(this.parentElement)'><img src='/images/minus.jpg' border='0' alt='Remove' ></a>"
		//alert(NRCContentRows);

		NRCchangeTotalValues(totalNRCRows);

}

function NRCAddDepositRows() //draws the content rows in the table
{
		totalNRCRows = NRCTableObject.children.length;
		NRCContentRows = totalNRCRows-2;
		RCLastRow=totalNRCRows-1;
		FirstRow=RCLastRow+2;
		mynewrow = NRCTableObject.insertRow(totalNRCRows);
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
		NRCTableObject.childNodes[totalNRCRows].childNodes[0].innerHTML=totalNRCRows+"<input type='hidden' name='NRCTaxValue"+totalNRCRows+"' id='NRCTaxValue"+totalNRCRows+"' value='"+NRCTaxValue+"'>"+"<input type='hidden' name='NRCDepositId"+totalNRCRows+"' id='NRCDepositId"+totalNRCRows+"' value='"+NRCDepositId+"'>"+"<input type='hidden' name='CalledFrom"+totalNRCRows+"' id='CalledFrom"+totalNRCRows+"' value='"+NRCCalledFrom+"'>";
		
		//ItemName and ItemID and ItemTypeId
		NRCTableObject.childNodes[totalNRCRows].childNodes[1].innerHTML=NRCDepositType+" : "+NRCItemName+"<input type='hidden' name='NRCItemId"+totalNRCRows+"' id='NRCItemId"+totalNRCRows+"' value='"+NRCItemId+"'>"+"<input type='hidden' name='NRCItemTypeId"+totalNRCRows+"' id='NRCItemTypeId"+totalNRCRows+"' value='"+NRCItemTypeId+"'>"+"<input type='hidden' name='NRCItemName"+totalNRCRows+"' id='NRCItemName"+totalNRCRows+"' value='"+NRCItemName+"'>";
	
		//BillingType
		NRCTableObject.childNodes[totalNRCRows].childNodes[2].innerHTML=NRCBillingType;
	
		//Quantity
		NRCTableObject.childNodes[totalNRCRows].childNodes[3].innerHTML=NRCQuantity+"<input type='hidden' name='NRCQty"+totalNRCRows+"' id='NRCQty"+totalNRCRows+"' value='"+NRCQuantity+"'>";
		
		//Rate
		NRCTableObject.childNodes[totalNRCRows].childNodes[4].innerHTML=NRCDepositCharge+"<input type='hidden' name='NRCRate"+totalNRCRows+"' id='NRCRate"+totalNRCRows+"' value='"+parseFloat(NRCDepositCharge)+"'>";
		
		//Amount
		NRCAmount = (parseFloat(NRCQuantity) * parseFloat(NRCDepositCharge));
		NRCTableObject.childNodes[totalNRCRows].childNodes[5].innerHTML = NRCAmount+"<input type='hidden' name='NRCAmount"+totalNRCRows+"' id='NRCAmount"+totalNRCRows+"' value='"+NRCAmount+"'>";
		
		//Tax=
		Tax=0;
		NRCTableObject.childNodes[totalNRCRows].childNodes[6].innerHTML=0+"<input type='hidden' name='NRCTax"+totalNRCRows+"' id='NRCTax"+totalNRCRows+"' value='"+0+"'>"+"<input type='hidden' name='NRCTaxId"+totalNRCRows+"' id='NRCTaxId"+totalNRCRows+"' value='"+NRCTaxId+"'>";
		
		//NRCTotal
		NRCTotal = parseFloat(NRCAmount) + parseFloat(Tax);
		NRCTableObject.childNodes[totalNRCRows].childNodes[7].innerHTML=0+"<input type='hidden' name='NRCTotal"+totalNRCRows+"' id='NRCTotal"+totalNRCRows+"' value='"+NRCTotal+"'>";

		
}//NRCAddDepositRows

function NRCchangeTotalValues(RNumber) //Updates the values of 'Amount' and 'Total' column for each row
{
		RowNumber = RNumber;
		var discount = 0;
		var rowCount;
		var TotalAmt=0;
		//amount=qty*rate
		if(nre.test(NRCTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value) && parseInt(NRCTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value)>0)
		{
			//Amount
			NRCAmount = parseFloat(NRCTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value)*parseFloat(NRCTableObject.childNodes[RowNumber].childNodes[4].innerText);
			NRCAmount=(Math.round(NRCAmount*100.0)/100.00);

			NRCAmount = convertCurrency(NRCAmount);
//			alert("after currency NRCAmount="+NRCAmount);

			NRCTableObject.childNodes[RowNumber].childNodes[5].innerHTML=NRCAmount + "<input type='hidden' name='NRCAmount"+RowNumber+"' id='NRCAmount"+RowNumber+"' value='"+NRCAmount+"'>";;
			if(eval("document."+formname+".c_strDiscountMode") != null){
				strselected=eval("document."+formname+".c_strDiscountMode.value");
				if(strselected=="percent")
				{	
					discount=0;
					if(eval("document."+formname+".c_fOtherDiscount.value")){
						discount=eval("document."+formname+".c_fOtherDiscount.value");
					}
					discount = parseFloat(discount)*(NRCAmount)/100.00;
					discount =(Math.round(discount*100.0)/100.00);
					discount = convertCurrency(discount);
					
				}
				else{
						if(eval("document."+formname+".NRCCount.value") > 0){
							if(eval("document."+formname+".NRCCount")!=null)
							{
								 rowCount = eval("document."+formname+".NRCCount.value");
								 rowCount=parseFloat(rowCount);
							}
			
						TotalAmt=0;
						var total=0;
						for(i=1;i<=parseFloat(rowCount);i++)
						{
							
							TotalAmt=TotalAmt+parseFloat(eval("document."+formname+".NRCAmount"+i+".value"));
						}
						if(eval("document."+formname+".c_fOtherDiscount.value")){
							discount=eval("document."+formname+".c_fOtherDiscount.value");
							discount=(discount*100)/TotalAmt;
						}
						discount = parseFloat(discount)*(NRCAmount)/100.00;
						discount =(Math.round(discount*100.0)/100.00);
						discount = convertCurrency(discount);

			
					}
				}
			}


//			alert('tax' + NRCTaxValue) ;

			//Tax
			NRCTaxValue =NRCTableObject.childNodes[RowNumber].childNodes[0].childNodes[1].value;
			NRCTaxRatio=((NRCAmount - discount) * NRCTaxValue)/100;
			NRCTaxRatio=(Math.round(NRCTaxRatio*100.0)/100.00);

			NRCTaxRatio = convertCurrency(NRCTaxRatio);
//			alert("after currency NRCTaxRatio="+NRCTaxRatio);

			NRCTableObject.childNodes[RowNumber].childNodes[6].innerHTML=NRCTaxRatio+"<input type='hidden' name='NRCTax"+RowNumber+"' id='NRCTax"+RowNumber+"' value='"+NRCTaxRatio+"'>"+"<input type='hidden' name='NRCTaxId"+RowNumber+"' id='NRCTaxId"+RowNumber+"' value='"+NRCTaxId+"'>";

//if Item has Deposit, change the qty of the Item Deposit to equalise it with the qty of Item
			//NRCQty
			//(DepositId != null)
/*			
			if(NRCTableObject.childNodes[RowNumber].childNodes[0].childNodes[2].value != null)
			{
				MainItemId = NRCTableObject.childNodes[RowNumber].childNodes[1].childNodes[1].value;
				NRCRows = NRCTableObject.children.length-2;
				for(rctr=2;rctr<=NRCRows;rctr++)
				{
					if(NRCTableObject.childNodes[rctr].childNodes[1].childNodes[1].value == MainItemId)
					{
						//Deposit Row Qty = Main Qty
						//alert(rctr);
						NRCTableObject.childNodes[rctr+1].childNodes[2].innerHTML = NRCTableObject.childNodes[RowNumber].childNodes[2].childNodes[0].value;
						break;
					}
				}
			}
*/			
////end of changes in deposit row


			
			//total=amount+tax;(if qty>0,total=amount+tax, else total=0)
			if(parseFloat(NRCTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value)>0)
			{
				NRCTotal = parseFloat(NRCTableObject.childNodes[RowNumber].childNodes[5].innerText)+parseFloat(NRCTableObject.childNodes[RowNumber].childNodes[6].innerText)
				NRCTotal=(Math.round(NRCTotal*100.0)/100.00);
				NRCTotal = convertCurrency(NRCTotal);
//				alert("after currency NRCTotal*="+NRCTotal);

				NRCTableObject.childNodes[RowNumber].childNodes[7].innerHTML=NRCTotal+"<input type='hidden' name='NRCTotal"+RowNumber+"' id='NRCTotal"+RowNumber+"' value='"+NRCTotal+"'>";
			}
			else
			{
				NRCTableObject.childNodes[RowNumber].childNodes[7].innerHTML=0+"<input type='hidden' name='NRCTotal"+RowNumber+"' id='NRCTotal"+RowNumber+"' value='"+0+"'>";
			}

			//Updating the Qty in DataStructure
			NRCBaseItemArray[RowNumber-1].NRCQuantity = NRCTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value;

			for(actr=0;actr<NRCBaseItemArray.length;actr++)
			   {
				//BaseItemArray[actr].NRCItemId = RCTable.ItemId
				if(NRCBaseItemArray[actr].NRCItemId == NRCTableObject.childNodes[RowNumber].childNodes[1].childNodes[1].value)
				{
				 NRCBaseItemArray[actr].NRCQuantity = NRCTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value;
				 break;
				}
			   }
			/////////////////////////////

		}
		else
		{
			alert("Quantity should be an integer greater than zero.");
			NRCBaseItemArray[RowNumber-1].NRCQuantity =0;
			NRCTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].value=0;
			NRCTableObject.childNodes[RowNumber].childNodes[5].innerText=0;
			NRCTableObject.childNodes[RowNumber].childNodes[7].innerHTML=0+"<input type='hidden' name='NRCTotal"+RowNumber+"' id='NRCTotal"+RowNumber+"' value='"+0+"'>";
			NRCTableObject.childNodes[RowNumber].childNodes[3].childNodes[0].select();
			return false;
		}
}

function NRCupdateTotalsRow() //Updates the values of the Totals Row
{
		NRCTotalRate=0.00;
		NRCTotalAmount=0.00;
		NRCTotalTax=0.00;
		NRCTotalValue=0.00;

		NRCtRows = parseFloat(NRCTableObject.children.length)-1;
		for(r=1;r<NRCtRows;r++)
		{
			NRCTotalRate = NRCTotalRate+parseFloat(NRCTableObject.childNodes[r].childNodes[4].innerText);
			NRCTotalAmount = NRCTotalAmount+parseFloat(NRCTableObject.childNodes[r].childNodes[5].innerText);
			NRCTotalTax = NRCTotalTax+parseFloat(NRCTableObject.childNodes[r].childNodes[6].innerText);
			NRCTotalValue = NRCTotalValue+parseFloat(NRCTableObject.childNodes[r].childNodes[7].innerText);
		}
		NRCTotalRate = (Math.round(NRCTotalRate*100.0)/100.0);
		NRCTotalAmount = (Math.round(NRCTotalAmount*100.0)/100.0);
		NRCTotalTax = (Math.round(NRCTotalTax*100.0)/100.0);
		NRCTotalValue = (Math.round(NRCTotalValue*100.0)/100.0);
		NRCTotalRate = convertCurrency(NRCTotalRate);
//		alert("after currency NRCTotalRate="+NRCTotalRate);

		NRCTotalAmount = convertCurrency(NRCTotalAmount);
//		alert("after currency NRCTotalAmount="+NRCTotalAmount);

		NRCTotalTax = convertCurrency(NRCTotalTax);
//		alert("after currency NRCTotalTax="+NRCTotalTax);

		NRCTotalValue = convertCurrency(NRCTotalValue);
//		alert("after currency NRCTotalValue="+NRCTotalValue);

		NRCTableObject.childNodes[NRCtRows].childNodes[4].innerText=NRCTotalRate;
		NRCTableObject.childNodes[NRCtRows].childNodes[5].innerText=NRCTotalAmount;
		NRCTableObject.childNodes[NRCtRows].childNodes[6].innerText=NRCTotalTax;
		NRCTableObject.childNodes[NRCtRows].childNodes[7].innerText=NRCTotalValue;

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
		totalRows = NRCTableObject.children.length-1;
		if(totalRows >0){
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
						BillAmount=parseFloat(NRCTableObject.childNodes[totalRows].childNodes[7].innerText);
						BillAmount = convertCurrency(BillAmount);
	//					alert("after currency BillAmount*="+BillAmount);
						var finalBillAmt = convertCurrency(BillAmount-RoundingAmountTotal);
						TotalTableObject.childNodes[ctr].childNodes[1].innerText=finalBillAmt;

					}
				}
			}
			else
			{
				//alert("true")
				discountApplied=0;
				
				for(ctr=1;ctr<=totalDiscountRows;ctr++)
				{
					if(DiscountTableObject.childNodes[ctr].id=='discountContentRow')
					{
						discountApplied = discountApplied + parseFloat(DiscountTableObject.childNodes[ctr].childNodes[3].innerText)		

					}
				}

				for(ctr=1;ctr<=totalDiscountRows;ctr++)
				{
					if(DiscountTableObject.childNodes[ctr].id=='discountAmount')
					{
						discountAmountRow = ctr;
						AmountBeforeDiscount = parseFloat(NRCTableObject.childNodes[totalRows].childNodes[7].innerText);

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
				if(discountApplied != 0)
				{
					changediscount();

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
  totalRows = NRCTableObject.children.length-1;
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

BillAmount=parseFloat(NRCTableObject.childNodes[totalRows].childNodes[6].inn
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
   //alert("true")
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
parseFloat(NRCTableObject.childNodes[totalRows].childNodes[6].innerText);
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
			NRCBaseItemArray[i].NRCQuantity = NRCTableObject.childNodes[j].childNodes[3].childNodes[0].value;
		}
	
		NRCTableObject.deleteRow(RowNumber); //Removes the row via DOM
		NRCBaseItemArray.splice(ArrayIndex, 1); //deletes the ITEM from the Data Structure
	//The following two functions are called just to rename the names of the controls on the form
		NRCDeleteAllRows(); //Deletes all rows of the table except the header
		NRCpopulateTable(); //Draws the complete table based on the data structure
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
