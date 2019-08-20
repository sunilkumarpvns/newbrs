/**Customized objects for list of combo**/
function selectList(dependant,optionValue,fillUpArray)
{
	this.depName = dependant;
	this.option = optionValue;
	this.list = fillUpArray;
}

/**Customized objects for single combo option**/
function selectCombo(text,value)
{
	this.text = text;
	this.value = value;
}

/**Function for emptying combo and its dependant combo's**/
function emptyCombo(combo)
{
	var emptyArray = new Array();

	/*empty the combo being passed*/
	var text = "";
	for(var i=combo.length-1; i>=0; i--)
	{
		if(combo.options[i].value=="")
			text = combo.options[i].text;
		combo.remove(i);
	}
	combo.options[0] = new Option(text,"");

	/*Collect all dependants of the combo being passed in the empty Array*/
	var arrayName = combo.name+"_dependants";
	try
	{	
		emptyArray = eval(arrayName);	
	}
	catch(e)
	{
		return true;
	}	

	/*Empty the dependant combo in case there are any dependants present*/
	if(emptyArray==null || emptyArray.length == 0)
		return true;
	else
	{
		for(var i=0 ; i<emptyArray.length ; i++)
		{
			child = document.getElementById(emptyArray[i]);
			if(child ==null)
			{
				displayParent = combo.name;
					if(displayParent.indexOf("$")>0)
						displayParent = displayParent.substring(0,displayParent.indexOf("$"));
				alert("Dependant '"+emptyArray[i]+"' for '"+displayParent+"' does not exist");
				return false;
			}
			else
			{
				emptyCombo(child);
			}
		}
	}
	return true;
}

/**Empty the combo and fill it up with values from the array**/
function fillUpCombo(combo,fillUpArray)
{
	bValid = true;
	if(emptyCombo(combo))
	{
		var count =1;
		if(combo.options[0].text == "")
			count = 0;		
		for(var i=0 ; i<fillUpArray.length; i++)
		{
			combo.options[i+count] = new Option(fillUpArray[i].text,fillUpArray[i].value);	
		}		
	}
	else
		bValid = false;	
	return bValid;
}

/**fill up the dependants for the specified combo**/
function fillUpDependant(parentCombo)
{
	parentComboName = parentCombo.name;		
	/*Get the selected value of the parent combo*/	
	var selectedValue = document.getElementById(parentComboName).value;
	var fillArray = new Array();
	/*Get all the dependant combo's of the parent*/
	arrayName = parentComboName+"_dependants";		
	try
	{
		fillArray = eval(arrayName);	
	}
	catch(e)
	{
		return;
	}	
	/*If dependants are present fill them up else return*/
	if(fillArray==null || fillArray.length == 0)
	{
		return;
	}
	else
	{
		/*Dependants are present so get the parent array*/
		var parentArray = new Array();
		arrayName = parentComboName+"_fillarray";
		try
		{
			parentArray = eval(arrayName);		
		}
		catch(e)
		{
			return;
		}
		if(parentArray == null || parentArray.length==0)
			return;
		else
		{
			/*For each dependant check the selected value and the dependant name in the array and fill up the combo*/
			for(var i=0; i<fillArray.length ; i++)
			{
				childComboName = fillArray[i];				
				var fillUpArray = new Array();
				
				/*Get the fill up array for the dependant combo*/
				for(var j=0; j<parentArray.length ; j++)
				{
					if(parentArray[j].depName == childComboName && parentArray[j].option == selectedValue)
					{
						fillUpArray = parentArray[j].list;
						break;
					}
				}			
				/*Fill up the combo*/
				child = document.getElementById(childComboName);
				if(child==null)
				{
					displayParent = parentComboName;
					if(displayParent.indexOf("$")>0)
						displayParent = displayParent.substring(0,displayParent.indexOf("$"));
					alert("Dependant '"+childComboName+"' for '"+displayParent+"' does not exist");
					return;
				}
				else
				{
					if(fillUpCombo(child,fillUpArray))
						fillUpDependant(child);
					else
						return;
				}
			}
		}
	}
}


/**
*Function for selecting specified value of combo
*/
function setSelectedValue(combo,value)
{
	if(value.length==0 && combo.length==2)
	{
		combo.options[1].selected = true;
	}
	else
	{
		for(var i=0; i<combo.length ; i++)
		{
			if(value == combo.options[i].value)
			{
				combo.options[i].selected = true;
				break;
			}
		}
	}
	fillUpDependant(combo);
}