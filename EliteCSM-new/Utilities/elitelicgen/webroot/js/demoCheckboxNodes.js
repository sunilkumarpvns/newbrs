// You can find general instructions for this file here:
// http://www.treeview.net
// Intructions on how to add checkboxes to a tree are only provided in this file.

USETEXTLINKS = 1  
STARTALLOPEN = 1
HIGHLIGHT = 1
PERSERVESTATE = 1
USEICONS = 1

// In this case we want the whole tree to be built,
// even those branches that are closed. The reason is that
// otherwise some form elements might not be built at all
// before the user presses "Get Values"
BUILDALL = 1

// This configuration file is used to demontrate how to add checkboxes to your tree.
// If your site will not display checkboxes, picka a different configuration file as 
// the example to follow and adapt

// Notes:
// If you are going to set USEICONS = 1, then you will want to edit the gif files and 
// remove the white space on the right


// Auxiliary functions for the contruction of the tree
// You will mcertainly want to change these functions for your own purposes

// If you want to add checkboxes to the folder you will have to create a function 
// similar to this one to do that and call it below in the tree construction section

// These functions are directly realted with the additional JavaScript in the 
// page holding the tree (demoCheckbox.html), where the form handling code
// resides
function generateCheckBox(parentfolderObject, itemLabel, checkBoxDOMId) {
	var newObj;

	// Read the online documentation for an explanation of insDoc and gLnk,
    // they are the base of the simplest Treeview trees
	newObj = insDoc(parentfolderObject, gLnk("R", itemLabel, "javascript:parent.op()"))

    // The trick to show checkboxes in a tree that was made to display links is to 
	// use the prependHTML. There are general instructions about this member
    // in the online documentation
	newObj.prependHTML = "<td valign=middle><input type=checkbox id="+checkBoxDOMId+"></td>"
}


// Function similar to the above, but instead of creating checkboxes, it creates
// radio buttons
function generateRadioB(parentfolderObject, itemLabel, checkBoxDOMId) {
	var newObj;

	// Read the online documentation for an explanation of insDoc and gLnk,
    // they are the base of the simplest Treeview trees
	newObj = insDoc(parentfolderObject, gLnk("R", itemLabel, "javascript:parent.op()"))

    // The trick to show checkboxes in a tree that was made to display links is to 
	// use the prependHTML. There are general instructions about this member
    // in the online documentation
	newObj.prependHTML = "<td valign=middle><input type=radio name=hourPick id="+checkBoxDOMId+"></td>"
}

// Construction of the tree

foldersTree = gFld("MLLN Problem Category", "/jsp/customerrelationship/ViewProblemCategory.jsp")
foldersTree.treeID = "checkboxTree"
aux1 = insFld(foldersTree, gFld("NetWork Problem","/jsp/customerrelationship/ViewProblemCategory.jsp"))
aux1.prependHTML ="<td valign=middle><input type=checkbox name=BOX6 id=BOX6></td>"
aux11 = insFld(aux1, gFld("Modem Problem","/jsp/customerrelationship/ViewProblemCategory.jsp"))
aux11.prependHTML ="<td valign=middle><input type=checkbox name=BOX6 id=BOX6></td>"
aux12 = insFld(aux1, gFld("Cable Problem","/jsp/customerrelationship/ViewProblemCategory.jsp"))
aux12.prependHTML ="<td valign=middle><input type=checkbox name=BOX6 id=BOX6></td>"

//aux3 = insFldDoc(auxparentfolderObject, gLnk("R", itemLabel, "javascript:parent.op()"))

aux2 = insFld(aux1, gFld("Bill", "/jsp/customerrelationship/ViewProblemCategory.jsp"))
aux2.prependHTML ="<td valign=middle><input type=checkbox name=BOX1 id=BOX1></td>"
aux21 = insFld(aux2, gFld("Payment","/jsp/customerrelationship/ViewProblemCategory.jsp"))
aux21.prependHTML ="<td valign=middle><input type=checkbox name=BOX6 id=BOX6></td>"
aux211 = insFld(aux21, gFld("Interest Calculation","/jsp/customerrelationship/ViewProblemCategory.jsp"))
aux211.prependHTML ="<td valign=middle><input type=checkbox name=BOX6 id=BOX6></td>"
aux212 = insFld(aux21, gFld("Late Charges","/jsp/customerrelationship/ViewProblemCategory.jsp"))
aux212.prependHTML ="<td valign=middle><input type=checkbox name=BOX6 id=BOX6></td>"

aux22 = insFld(aux2, gFld("Billing","/jsp/customerrelationship/ViewProblemCategory.jsp"))
aux22.prependHTML ="<td valign=middle><input type=checkbox name=BOX6 id=BOX6></td>"
aux221 = insFld(aux22, gFld("Address Problem","/jsp/customerrelationship/ViewProblemCategory.jsp"))
aux221.prependHTML ="<td valign=middle><input type=checkbox name=BOX6 id=BOX6></td>"
aux222 = insFld(aux22, gFld("Wrong Bill","/jsp/customerrelationship/ViewProblemCategory.jsp"))
aux222.prependHTML ="<td valign=middle><input type=checkbox name=BOX6 id=BOX6></td>"



//aux5 = insFld(aux1, gFld("Marketing", "javascript:parent.op()"))
//aux5.prependHTML ="<td valign=middle><input type=checkbox name=BOX3 id=BOX3></td>"

//aux5 = insFld(aux3, aux4)

//generateCheckBox(aux1, "Monday", "BOX5")

//generateCheckBox(aux1, "Monday", "BOX1")
/*generateCheckBox(aux1, "Wednesday", "BOX2")
generateCheckBox(aux1, "Friday", "BOX3")
aux2 = insFld(foldersTree, gFld("Hour", "javascript:parent.op()"))
generateRadioB(aux2, "10AM", "RD1")
generateRadioB(aux2, "2PM", "RD2")
generateRadioB(aux2, "6PM", "RD3")*/

/*foldersTree = gFld("Best time to try demos:", "demoCheckboxRightFrame.html")
foldersTree.treeID = "checkboxTree"
aux1 = insFld(foldersTree, gFld("Day of the week", "javascript:parent.op()"))
aux3 = insFld(aux1, gFld("January", "javascript:parent.op()"))

generateCheckBox(aux3, "Monday", "BOX1")
generateCheckBox(aux1, "Monday", "BOX1")
generateCheckBox(aux1, "Wednesday", "BOX2")
generateCheckBox(aux1, "Friday", "BOX3")
aux2 = insFld(foldersTree, gFld("Hour", "javascript:parent.op()"))
generateRadioB(aux2, "10AM", "RD1")
generateRadioB(aux2, "2PM", "RD2")
generateRadioB(aux2, "6PM", "RD3")*/

