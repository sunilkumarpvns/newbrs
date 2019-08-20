
function toggleImages(imageName)
{
	var Images = new Image();
	Images.src = '/images/dnarrow.jpg';
	Images.src = '/images/uparrow.jpg';

	if (refer) {
	  eval(formName+imageName).src='/images/dnarrow.jpg';
	  refer=false;
	}
	else {
	  eval(formName+imageName).src='/images/uparrow.jpg';
	  refer=true;
	}

}

function compareAlessThanB(A,B)
{
  return A < B ? - 1 :(A == B ? 0 : 1);
}

function compareBlessThanA(A,B)
{
  return B < A ? - 1 :(A == B ? 0 : 1);
}

function doSortTable( col, table,direction, hasTotalRow,startIndex, chkBoxName) 
{
  sortAscending = true;
  if( direction == true )
  {
    sortAscending  = compareBlessThanA;
  }
  else 
  {
    sortAscending  = compareAlessThanB;
  }
  sortTable(col, table,sortAscending, hasTotalRow,startIndex, chkBoxName);
  return direction = (direction!=true)?true:false;
}
<!-- Begin
function setDataType(cValue)
  {
    // THIS FUNCTION CONVERTS DATES AND NUMBERS FOR PROPER ARRAY
    // SORTING WHEN IN THE SORT FUNCTION
    var isDate = new Date(cValue);
    if (isDate == "NaN")
      {
        if (isNaN(cValue))
          {
            // THE VALUE IS A STRING, MAKE ALL CHARACTERS IN
            // STRING UPPER CASE TO ASSURE PROPER A-Z SORT
            cValue = cValue.toUpperCase();
   		    //alert(cValue);
            return cValue;
          }
        else
          {
            // VALUE IS A NUMBER, TO PREVENT STRING SORTING OF A NUMBER
            // ADD AN ADDITIONAL DIGIT THAT IS THE + TO THE LENGTH OF
            // THE NUMBER WHEN IT IS A STRING
            var myNum;
			//alert(cValue);
            myNum = String.fromCharCode(48 + cValue.length) + cValue;
            return myNum;
          }
        }
  else
      {
        // VALUE TO SORT IS A DATE, REMOVE ALL OF THE PUNCTUATION AND
        // AND RETURN THE STRING NUMBER
        //BUG - STRING AND NOT NUMERICAL SORT .....
        // ( 1 - 10 - 11 - 2 - 3 - 4 - 41 - 5  etc.)
        var myDate = new String();
        myDate = isDate.getFullYear() + "/" ;
		myMonth=parseInt(isDate.getMonth())+1;
        myDate = myDate + myMonth + "/";
        myDate = myDate + isDate.getDate(); 
       // alert(myDate)
		/*myDate = myDate + isDate.getHours(); + " ";
        myDate = myDate + isDate.getMinutes(); + " ";
        myDate = myDate + isDate.getSeconds();*/
        //myDate = String.fromCharCode(48 + myDate.length) + myDate;
        return myDate ;
      }
  }
function sortTable(col, tableToSort, sortMethod, hasTotalRow,startIndex, chkBoxName)
  {
    var iCurCell = col + tableToSort.cols;
	var imagecell = iCurCell;

	if(tableToSort.cells(iCurCell).getElementsByTagName("IMG").length == 0 )
	  {
		
		imgTag=false;
	  }
    else
	  {
		imgTag=true;
		//alert("is image");
	  }
	var TotalsArray = new Array();
	if(!hasTotalRow)
	{
    	var totalRows = tableToSort.rows.length;
		lastRow=totalRows;
	}
	else if(hasTotalRow)
	{
		var totalRows = tableToSort.rows.length;
		for (c=0; c<tableToSort.cols; c++)
		{
			TotalsArray[c]=tableToSort.rows[totalRows-1].cells[c].innerHTML;
		}
		lastRow=totalRows-1;
		tableToSort.deleteRow(lastRow);

	}
	
    var bSort = 0;
    var colArray = new Array();
    var oldIndex = new Array();
    var indexArray = new Array();
    var bArray = new Array();
    var newRow;
    var newCell;
    var i;
    var c;
    var j;
    // ** POPULATE THE ARRAY colArray WITH CONTENTS OF THE COLUMN SELECTED
	
	if(imgTag==false)
	{
		for (i=1; i < tableToSort.rows.length; i++)
		  {
			colArray[i - 1] = setDataType(tableToSort.cells(iCurCell).innerText);		
			iCurCell = iCurCell + tableToSort.cols;
		  }
	}
	else if(imgTag==true)
	{
		for (i=1; i < tableToSort.rows.length; i++)
		  {
			imgName = tableToSort.cells(iCurCell).getElementsByTagName("IMG");
			colArray[i - 1] = setDataType(imgName[0].src);
			iCurCell = iCurCell + tableToSort.cols;
		  }
	}
    // ** COPY ARRAY FOR COMPARISON AFTER SORT
    for (i=0; i < colArray.length; i++)
      {
        bArray[i] = colArray[i];
      }
    // ** SORT THE COLUMN ITEMS
    //alert ( colArray );
    colArray.sort(sortMethod);
//alert(colArray);	
    for (i=0; i < colArray.length; i++)
      { // LOOP THROUGH THE NEW SORTED ARRAY
        indexArray[i] = (i+1);
        for(j=0; j < bArray.length; j++)
          { // LOOP THROUGH THE OLD ARRAY
            if (colArray[i] == bArray[j])
              {  // WHEN THE ITEM IN THE OLD AND NEW MATCH, PLACE THE
                // CURRENT ROW NUMBER IN THE PROPER POSITION IN THE
                // NEW ORDER ARRAY SO ROWS CAN BE MOVED ....
                // MAKE SURE CURRENT ROW NUMBER IS NOT ALREADY IN THE
                // NEW ORDER ARRAY
                for (c=0; c<i; c++)
                  {
                    if ( oldIndex[c] == (j+1) )
                    {
                      bSort = 1;
                    }
                      }
                      if (bSort == 0)
                        {
                          oldIndex[i] = (j+1);
                        }
                          bSort = 0;
                        }
          }
    }
  // ** SORTING COMPLETE, ADD NEW ROWS TO BASE OF TABLE ....
 // alert("oldIndex.length : "+oldIndex.length);
  for (i=0; i<oldIndex.length; i++)
    {
      newRow = tableToSort.insertRow();
      for (c=0; c<tableToSort.cols; c++)
        {
          newCell = newRow.insertCell();
          newCell.innerHTML = tableToSort.rows(oldIndex[i]).cells(c).innerHTML;
  		  newCell.className="tblfirstcol";
        }
      }
  //MOVE NEW ROWS TO TOP OF TABLE ....
  for (i=1; i<lastRow; i++)
    {
      tableToSort.moveRow((tableToSort.rows.length -1),1);
    }
  //DELETE THE OLD ROWS FROM THE BOTTOM OF THE TABLE ....
  for (i=1; i<lastRow; i++)
    {
      tableToSort.deleteRow();
    }
	
	for(i=1;i<lastRow;i++)
	{
		tableToSort.rows[i].cells[1].innerText=((startIndex-1)+i)+".";
	}

	if(hasTotalRow)
	{
		newRow = tableToSort.insertRow(lastRow);
		for (c=0; c<tableToSort.cols; c++)
		{
		  newRow.insertCell();
		  newRow.cells(c).className="tblrows";
		  newRow.cells(c).innerHTML=TotalsArray[c];
		}
	}

	
	makeToggle(tableToSort.name,chkBoxName)
	

 }


function getRowId(tblToSearch,objToSearch)
{
		var totalRows = tblToSearch.rows.length;
		var rowId = -1;
		for (c=1; c<totalRows; c++)
		{
			if(tblToSearch.rows[c].cells[0].contains(objToSearch))
			{
				rowId=c;
				break;
			}
			
		}
		return c;
}

function makeToggle(tableId,chkBoxName)
{
	//alert(tableId);
	var x = document.getElementsByName(chkBoxName);
	for(i=0 ; i<x.length; i++)
	{
		if(x[i].checked)
		{
			changeRowColor(tableId,i+1,x[i].checked);
		}
	}
}
function makeUrl(ancTag,colNo,sortOrder)
{
	ancTag.href += "&colNo=" + colNo+ "&sortOrder=" + sortOrder;
}


//  End -->

