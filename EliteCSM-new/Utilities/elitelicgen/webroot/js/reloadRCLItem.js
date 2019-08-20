var rclArray = new Array();

function addRCLObject(RCLBaseItemArray)
{
	//alert('Adding RCL object '+RCLBaseItemArray.length);
	rclArray = new Array();	
	for(i=0;i<RCLBaseItemArray.length;i++)
	{
		tempRCLObject = new Object();	
		tempRCLObject.RCLItemId=RCLBaseItemArray[i].RCLItemId;
		tempRCLObject.RCLItemName=RCLBaseItemArray[i].RCLItemName;
		tempRCLObject.RCLBillingType=RCLBaseItemArray[i].RCLBillingType;
		tempRCLObject.RCLItemTypeId=RCLBaseItemArray[i].RCLItemTypeId;
		tempRCLObject.RCLQuantity=RCLBaseItemArray[i].RCLQuantity;
		tempRCLObject.RCLTaxValue=RCLBaseItemArray[i].RCLTaxValue;
		tempRCLObject.SBillCycleId = RCLBaseItemArray[i].SBillCycleId;
		
		tempBillArray = new Array();

		for(j=0; j<RCLBaseItemArray[i].RCLBillingCycle.length ; j++)
		{
			tempBillObject = new Object();
			billObject = RCLBaseItemArray[i].RCLBillingCycle[j];

			tempBillObject.RCLBillingCycleId=billObject.RCLBillingCycleId;
			tempBillObject.RCLBillingCycleName=billObject.RCLBillingCycleName;
			tempBillObject.RCLBillingCycleRate=billObject.RCLBillingCycleRate;
			tempBillObject.RCLBillingCyclePeriod=billObject.RCLBillingCyclePeriod;
			tempBillArray[j]=tempBillObject;
			//alert('Position ' + j + 'Bill Array '  +tempBillArray[j]);
		}

		//alert( 'Bill Array '  +tempBillArray);
		tempRCLObject.RCLBillingCycle = tempBillArray;
		//alert( 'tempRCLObject.RCLBillingCycle '  +tempRCLObject.RCLBillingCycle);

		//alert( 'tempObject '  +tempRCLObject);
		rclArray[i] = tempRCLObject;
		//alert( 'rclArray[i] '  +rclArray[i]);
	}	
}

function reloadRCLItems(RCLBaseItemArray)
{
	//alert('Inside rcl refreshed reload '+rclArray.length)	
	for(i=0;i<rclArray.length;i++)
	{
		tempRCLObject = new Object();			
		tempRCLObject.RCLItemId=rclArray[i].RCLItemId;
		tempRCLObject.RCLItemName=rclArray[i].RCLItemName;
		tempRCLObject.RCLBillingType=rclArray[i].RCLBillingType;
		tempRCLObject.RCLItemTypeId=rclArray[i].RCLItemTypeId;
		tempRCLObject.RCLQuantity=rclArray[i].RCLQuantity;
		tempRCLObject.RCLTaxValue=rclArray[i].RCLTaxValue;
		tempRCLObject.SBillCycleId = rclArray[i].SBillCycleId;
		
		//alert('before array ');
		tempRCLBillArray = new Array();
		//alert('Reached near array '+rclArray[i]);
		//alert('Reached near array '+rclArray[i].RCLBillingCycle);
		//alert('Reached near array '+rclArray[i].RCLBillingCycle.length);

		for(j=0 ; j<rclArray[i].RCLBillingCycle.length ; j++)
		{
			//alert('Inside for loop');
			tempBillObject = new Object();
	
			billObject = rclArray[i].RCLBillingCycle[j];

			tempBillObject.RCLBillingCycleId=billObject.RCLBillingCycleId;
			tempBillObject.RCLBillingCycleName=billObject.RCLBillingCycleName;
			tempBillObject.RCLBillingCycleRate=billObject.RCLBillingCycleRate;
			tempBillObject.RCLBillingCyclePeriod=billObject.RCLBillingCyclePeriod;
			tempRCLBillArray[j]=tempBillObject;
		}

		tempRCLObject.RCLBillingCycle = tempRCLBillArray;

		RCLBaseItemArray[i] = tempRCLObject;
	}	
	//alert('Returning from reload rcl');
}