
/**
 * next
 * @param {type} seq 
 */
 function next(seq) {
  	document.forms[0].op.value='1';
  	document.forms[0].submit();
 }
 function back(seq) {
  	document.forms[0].op.value='-1';
  	document.forms[0].submit();


 }
 
 
  
 
 function  checkBox()
 {

 
 if( document.forms[0].checkBoxCon.value == true)
 {
 for (i = 0; i < document.forms[0].checkBox.length; i++)
	document.forms[0].checkBox.checked = true ;
}
else
{
	for (i = 0; i < document.forms[0].checkBox.length; i++)
	document.forms[0].checkBox.checked = false ;
 	
 }
}
 

 function genrateSource()
 {
 	document.forms[0].op.value='0';
  	document.forms[0].submit();
 	 
 }
 
 