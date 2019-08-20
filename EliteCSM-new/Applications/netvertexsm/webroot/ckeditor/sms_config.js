/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function(config) {
	config.extraPlugins = "newplugin";
	config.enterMode = CKEDITOR.ENTER_BR;
	config.skin = 'moono',
	/*config.startupMode='source',*/
	config.disableNativeSpellChecker = true,
	config.toolbarGroups = [
	                		{ name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
	                		{ name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
	                		{ name: 'others' },
	                		{ name: 'about' }
	                	];	
};
