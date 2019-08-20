var nrcMBillArray = new Array();

function addMBillNRCObject(NRCBaseItemArray)
{
	
	nrcMBillArray = new Array();	
	for(i=0;i<NRCBaseItemArray.length;i++)
	{
		tempObject = new Object();
		tempObject.NRCItemId=NRCBaseItemArray[i].NRCItemId;
		tempObject.NRCItemName=NRCBaseItemArray[i].NRCItemName;
		tempObject.NRCBillingType = NRCBaseItemArray[i].NRCBillingType; 
		tempObject.NRCItemTypeId=NRCBaseItemArray[i].NRCItemTypeId;
		tempObject.NRCCalledFrom=NRCBaseItemArray[i].NRCCalledFrom;
		tempObject.NRCQuantity=NRCBaseItemArray[i].NRCQuantity;
		tempObject.NRCTaxValue=NRCBaseItemArray[i].NRCTaxValue;
		tempObject.NRCTaxId = NRCBaseItemArray[i].NRCTaxId;
		tempObject.NRCRate=NRCBaseItemArray[i].NRCRate;
		tempObject.NRCDepositId=NRCBaseItemArray[i].NRCDepositId;
		tempObject.NRCDepositType=NRCBaseItemArray[i].NRCDepositType;
		tempObject.NRCDepositCharge=NRCBaseItemArray[i].NRCDepositCharge;
		
		tempObject.NRCDiscount=NRCBaseItemArray[i].NRCDiscount;
		tempObject.NRCDiscountType=NRCBaseItemArray[i].NRCDiscountType;
		tempObject.NRCDiscountAmount=NRCBaseItemArray[i].NRCDiscountAmount;

		nrcMBillArray[i] = tempObject;
		//alert( 'nrcMBillArray[i] '  +nrcMBillArray[i]);
	}	

	//alert('Added NRC object '+nrcMBillArray.length);
}

function reloadMBillNRCItems(NRCBaseItemArray)
{
	//alert('Inside nrc refreshed reload '+nrcMBillArray.length)	
	for(i=0;i<nrcMBillArray.length;i++)
	{
		tempObject = new Object();			
		tempObject.NRCItemId=nrcMBillArray[i].NRCItemId;
		tempObject.NRCItemName=nrcMBillArray[i].NRCItemName;
		tempObject.NRCBillingType = nrcMBillArray[i].NRCBillingType; 
		tempObject.NRCItemTypeId=nrcMBillArray[i].NRCItemTypeId;
		tempObject.NRCCalledFrom=nrcMBillArray[i].NRCCalledFrom;
		tempObject.NRCQuantity=nrcMBillArray[i].NRCQuantity;
		tempObject.NRCTaxValue=nrcMBillArray[i].NRCTaxValue;
		tempObject.NRCTaxId = nrcMBillArray[i].NRCTaxId;
		tempObject.NRCRate=nrcMBillArray[i].NRCRate;
		tempObject.NRCDepositId=nrcMBillArray[i].NRCDepositId;
		tempObject.NRCDepositType=nrcMBillArray[i].NRCDepositType;
		tempObject.NRCDepositCharge=nrcMBillArray[i].NRCDepositCharge;

		tempObject.NRCDiscount=nrcMBillArray[i].NRCDiscount;
		tempObject.NRCDiscountType=nrcMBillArray[i].NRCDiscountType;
		tempObject.NRCDiscountAmount=nrcMBillArray[i].NRCDiscountAmount;

		NRCBaseItemArray[i] = tempObject;
	}	
	//alert('Returning from reload nrc For Manual Bill');
}