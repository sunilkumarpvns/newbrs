var ppArray = new Array();

function addPPObject(PPBaseItemArray)
{
	//alert('Adding PP object '+PPBaseItemArray.length);
	ppArray = new Array();	
	for(i=0;i<PPBaseItemArray.length;i++)
	{
		tempObject = new Object();	
		tempObject.PPItemId=PPBaseItemArray[i].PPItemId;
		tempObject.PPItemName=PPBaseItemArray[i].PPItemName;
		tempObject.PPBillingType = PPBaseItemArray[i].PPBillingType; 
		tempObject.PPItemTypeId=PPBaseItemArray[i].PPItemTypeId;
		tempObject.PPCalledFrom=PPBaseItemArray[i].PPCalledFrom;
		tempObject.PPQuantity=PPBaseItemArray[i].PPQuantity;
		tempObject.PPTaxValue=PPBaseItemArray[i].PPTaxValue;
		tempObject.PPTaxId = PPBaseItemArray[i].PPTaxId;
		tempObject.PPRate=PPBaseItemArray[i].PPRate;
		tempObject.PPDepositId=PPBaseItemArray[i].PPDepositId;
		tempObject.PPDepositType=PPBaseItemArray[i].PPDepositType;
		tempObject.PPDepositCharge=PPBaseItemArray[i].PPDepositCharge;

		ppArray[i] = tempObject;
		//alert( 'ppArray[i] '  +ppArray[i]);
	}	
}

function reloadPPItems(PPBaseItemArray)
{
	//alert('Inside pp refreshed reload '+ppArray.length)	
	for(i=0;i<ppArray.length;i++)
	{
		tempObject = new Object();			
		tempObject.PPItemId=ppArray[i].PPItemId;
		tempObject.PPItemName=ppArray[i].PPItemName;
		tempObject.PPBillingType = ppArray[i].PPBillingType; 
		tempObject.PPItemTypeId=ppArray[i].PPItemTypeId;
		tempObject.PPCalledFrom=ppArray[i].PPCalledFrom;
		tempObject.PPQuantity=ppArray[i].PPQuantity;
		tempObject.PPTaxValue=ppArray[i].PPTaxValue;
		tempObject.PPTaxId = ppArray[i].PPTaxId;
		tempObject.PPRate=ppArray[i].PPRate;
		tempObject.PPDepositId=ppArray[i].PPDepositId;
		tempObject.PPDepositType=ppArray[i].PPDepositType;
		tempObject.PPDepositCharge=ppArray[i].PPDepositCharge;

		PPBaseItemArray[i] = tempObject;
	}	
	//alert('Returning from reload pp');
}