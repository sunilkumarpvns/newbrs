var nrcArray = new Array();

function addNRCObject(NRCBaseItemArray)
{
	//alert('Adding RCL object '+NRCBaseItemArray.length);
	nrcArray = new Array();	
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

		nrcArray[i] = tempObject;
		//alert( 'nrcArray[i] '  +nrcArray[i]);
	}	
}

function reloadNRCItems(NRCBaseItemArray)
{
	//alert('Inside nrc refreshed reload '+nrcArray.length)	
	for(i=0;i<nrcArray.length;i++)
	{
		tempObject = new Object();			
		tempObject.NRCItemId=nrcArray[i].NRCItemId;
		tempObject.NRCItemName=nrcArray[i].NRCItemName;
		tempObject.NRCBillingType = nrcArray[i].NRCBillingType; 
		tempObject.NRCItemTypeId=nrcArray[i].NRCItemTypeId;
		tempObject.NRCCalledFrom=nrcArray[i].NRCCalledFrom;
		tempObject.NRCQuantity=nrcArray[i].NRCQuantity;
		tempObject.NRCTaxValue=nrcArray[i].NRCTaxValue;
		tempObject.NRCTaxId = nrcArray[i].NRCTaxId;
		tempObject.NRCRate=nrcArray[i].NRCRate;
		tempObject.NRCDepositId=nrcArray[i].NRCDepositId;
		tempObject.NRCDepositType=nrcArray[i].NRCDepositType;
		tempObject.NRCDepositCharge=nrcArray[i].NRCDepositCharge;

		NRCBaseItemArray[i] = tempObject;
	}	
	//alert('Returning from reload nrc');
}