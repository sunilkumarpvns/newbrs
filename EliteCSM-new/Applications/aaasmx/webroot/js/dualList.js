
function move(fbox,tbox)
{
  for(var i=0; i<fbox.options.length; i++)
  {
 
   if(fbox.options[i].selected && fbox.options[i].value != "")
   {
    var no = new Option();
    no.value = fbox.options[i].value;
    no.text = fbox.options[i].text;
    tbox.options[tbox.options.length] = no;
    fbox[i--] = null;
        cmbctr=i+1;
     }
  }
   if(cmbctr!=fbox.options.length)
   {
    if(cmbctr!=fbox.options.length)
     fbox.options[cmbctr].selected=true;
   }
   else if((cmbctr==fbox.options.length) && (fbox.options.length!=0))
   {
    fbox.options[0].selected=true;
   }
   if (sortitems)
   {
   SortDual(tbox);
   SortDual(fbox);
   }
}
 
function moveAll(fbox, tbox)
{
  for(i = 0; i < fbox.options.length; i++)
  {
   fbox.options[i].selected=true;
  }
  move(fbox, tbox);
}
 
function SortDual(box)
{
 
  var temp_opts = new Array();
  var temp = new Object();
  for(var i=0; i<box.options.length; i++)
  {
   temp_opts[i] = box.options[i];
  }
  for(var x=0; x<temp_opts.length-1; x++)
  {
   for(var y=(x+1); y<temp_opts.length; y++)
   {
    if(temp_opts[x].text.toUpperCase() > temp_opts[y].text.toUpperCase())
    {
     temp = temp_opts[x].text;
     temp_opts[x].text = temp_opts[y].text;
     temp_opts[y].text = temp;
     temp = temp_opts[x].value;
     temp_opts[x].value = temp_opts[y].value;
     temp_opts[y].value = temp;
     }
     }
  }
  for(var i=0; i<box.options.length; i++)
  {
   box.options[i].value = temp_opts[i].value;
   box.options[i].text = temp_opts[i].text;
 
  }
}

// Dual list move function

function moveDualList(srcList, destList, moveAll ) 

{
 if ((srcList.selectedIndex == -1 ) && ( moveAll == false )   )
  {
    return;
  }

  newDestList = new Array( destList.options.length );
  var len = 0;
  for( len = 0; len < destList.options.length; len++ ) 
  {
    if ( destList.options[ len ] != null )
    {
      newDestList[ len ] = new Option( destList.options[ len ].text, destList.options[ len ].value, destList.options[ len ].defaultSelected, destList.options[ len ].selected );
    }
  }

  for( var i = 0; i < srcList.options.length; i++ ) 
  { 
    if ( srcList.options[i] != null && ( srcList.options[i].selected == true || moveAll ) )
    {
       newDestList[ len ] = new Option( srcList.options[i].text, srcList.options[i].value, srcList.options[i].defaultSelected, srcList.options[i].selected );
       len++;
    }
  }

  // Sort out the new destination list
  newDestList.sort( compareOptionValues );   // BY VALUES
  //newDestList.sort( compareOptionText );   // BY TEXT



 // Populate the destination with the items from the new array
  for ( var j = 0; j < newDestList.length; j++ ) 
  {
    if ( newDestList[ j ] != null )
    {
      destList.options[ j ] = newDestList[ j ];
    }
  }
  
 // Erase source list selected elements
  for( var i = srcList.options.length - 1;i >= 0; i--) 
  { 
    if ( srcList.options[i] != null && ( srcList.options[i].selected == true || moveAll ) )
    {
       srcList.options[i]= null;
    }
  }
  document.forms[0].c_btnMoveAllRight; 
}

function compareOptionValues(a, b) 

{ 
  var sA = parseInt( a.value, 36 );  
  var sB = parseInt( b.value, 36 );  
  return sA - sB;
}

