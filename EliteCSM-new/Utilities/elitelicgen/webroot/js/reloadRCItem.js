var rcArray = new Array();
function addRCObject(BaseItemArray)
{
	rcArray = new Array();	
	for(i=0;i<BaseItemArray.length;i++)
	{
		tempObject = new Object();
		tempObject.ItemId=BaseItemArray[i].ItemId;
		tempObject.ItemName=BaseItemArray[i].ItemName;
		tempObject.ItemTypeId=BaseItemArray[i].ItemTypeId;
		tempObject.ItemBillingType=BaseItemArray[i].ItemBillingType;		
		tempObject.Quantity=BaseItemArray[i].Quantity;
		tempObject.TaxValue=BaseItemArray[i].TaxValue;
		tempObject.DepositId=BaseItemArray[i].DepositId;
		tempObject.DepositType=BaseItemArray[i].DepositType;
		tempObject.DepositCharge=BaseItemArray[i].DepositCharge;
		tempObject.SBillCycleId = BaseItemArray[i].SBillCycleId;
		
		tempBillArray = new Array();

		for(j=0; j<BaseItemArray[i].BillingCycle.length ; j++)
		{
			tempBillObject = new Object();
			billObject = BaseItemArray[i].BillingCycle[j];
			tempBillObject.BillingCycleId=billObject.BillingCycleId;
			tempBillObject.BillingCycleName=billObject.BillingCycleName;
			tempBillObject.BillingCycleRate=billObject.BillingCycleRate;

			tempBillArray[j]=tempBillObject;
		}

		tempObject.BillingCycle = tempBillArray;

		rcArray[i] = tempObject;
	}	
}

function reloadRCItems(BaseItemArray)
{
	for(i=0;i<rcArray.length;i++)
	{
		tempObject = new Object();
		tempObject.ItemId=rcArray[i].ItemId;		

		tempObject.ItemName=rcArray[i].ItemName;
		tempObject.ItemTypeId=rcArray[i].ItemTypeId;
		tempObject.ItemBillingType=rcArray[i].ItemBillingType;
		tempObject.Quantity = rcArray[i].Quantity;
		//alert('tax = '+rcArray[i].TaxValue);
		tempObject.TaxValue=rcArray[i].TaxValue;
		tempObject.DepositId=rcArray[i].DepositId;
		tempObject.DepositType=rcArray[i].DepositType;
		tempObject.DepositCharge=rcArray[i].DepositCharge;
		
		tempObject.SBillCycleId = rcArray[i].SBillCycleId;
		
		
		tempBillArray = new Array();
		for(j=0; j<rcArray[i].BillingCycle.length ; j++)
		{
			tempBillObject = new Object();
			billObject = rcArray[i].BillingCycle[j];
			tempBillObject.BillingCycleId=billObject.BillingCycleId;
			tempBillObject.BillingCycleName=billObject.BillingCycleName;
			tempBillObject.BillingCycleRate=billObject.BillingCycleRate;

			tempBillArray[j]=tempBillObject;
		}

		tempObject.BillingCycle = tempBillArray;

		BaseItemArray[i] = tempObject;		
	}		
}