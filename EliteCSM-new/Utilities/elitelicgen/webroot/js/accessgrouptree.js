// You can find general instructions for this file here:
// http://www.treeview.net
// Intructions on how to add checkboxes to a tree are only provided in this file.

USETEXTLINKS = 0  
STARTALLOPEN = 1
HIGHLIGHT = 0
PERSERVESTATE = 1
USEICONS = 1
BUILDALL = 1
ICONPATH = '/images/'

// In this case we want the whole tree to be built,
// even those branches that are closed. The reason is that
// otherwise some form elements might not be built at all
// before the user presses "Get Values"


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
foldersTree = gFld("ACCESS RIGHTS FOR STAFF", "")
foldersTree.treeID = "accesstree"
foldersTree.iconSrc = ICONPATH + "tick.jpg"
foldersTree.iconSrcClosed = ICONPATH + "tick.jpg"

aux1 = insFld(foldersTree, gFld("System Configuration",""))
aux1.iconSrc = ICONPATH + "cross.jpg"
aux1.iconSrcClosed = ICONPATH + "cross.jpg"

aux2 = insFld(foldersTree, gFld("Access Management", ""))
aux2.iconSrc = ICONPATH + "tick.jpg"
aux2.iconSrcClosed = ICONPATH + "tick.jpg"

aux21 = insFld(aux2, gFld("Create",""))
aux21.iconSrc = ICONPATH + "tick.jpg"
aux21.iconSrcClosed = ICONPATH + "tick.jpg"

aux22 = insFld(aux2, gFld("Activate",""))
aux22.iconSrc = ICONPATH + "cross.jpg"
aux22.iconSrcClosed = ICONPATH + "cross.jpg"

aux23 = insFld(aux2, gFld("Deactivate",""))
aux23.iconSrc = ICONPATH + "tick.jpg"
aux23.iconSrcClosed = ICONPATH + "tick.jpg"

aux3 = insFld(foldersTree, gFld("Staff Management", ""))
aux3.iconSrc = ICONPATH + "tick.jpg"
aux3.iconSrcClosed = ICONPATH + "tick.jpg"
