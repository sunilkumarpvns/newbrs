(function(){
	var o = { exec:function(p){
				 var p={};
				 url="GetSomeData.js";
				 $.post(url,p,function(response){
						response=eval(response);
						if(editorInstance){
						//CKEDITOR.instances[editorInstance.name].insertHtml(response[0].value+'<br/>');
						editorInstance.insertHtml(response[0].value+'<br/>');
						}
						else
							alert(response[0].value);
				});
			}
	};
	CKEDITOR.plugins.add('newplugin',{
					init:function(editor){
						/*editor.addCommand('newplugin',o);
						editor.ui.addButton('newplugin',{label:'My New Plugin',icon:this.path+'newplugin.png',command:'newplugin'});						
						if(editor.addMenuItems)editor.addMenuItem("newplugin",{label:'New Plugin',command:'newplugin',group:'clipboard',order:9});						
						if(editor.contextMenu)editor.contextMenu.addListener(function(){
							return { "newplugin": CKEDITOR.TRISTATE_OFF};
						});*/
																		
						var strings = [];
						strings.push(['{Sub.UserName}', 'User Name', 'User Name']);
                        strings.push(['{Sub.CustomerType}', 'Customer Type', 'User Name']);
                        strings.push(['{Sub.AccountStatus}', 'Account Status', 'Account Status']);
                        strings.push(['{Sub.ExpiryDate}', 'Expiry Date', 'Expiry Date']);
                        strings.push(['{Sub.SubscriberPackage}', 'Subscriber Package', 'Subscriber Package']);
                        strings.push(['{Sub.BillingDate}', 'Billing Date', 'Billing Date']);
                        strings.push(['{Sub.Area}', 'Area', 'Area']);
                        strings.push(['{Sub.City}', 'City', 'City']);
                        strings.push(['{Sub.Zone}', 'Zone', 'Zone']);
                        strings.push(['{Sub.Country}', 'Country', 'Country']);
                        strings.push(['{Sub.Birthdate}', 'Birth Date', 'Birth Date']);
                        strings.push(['{Sub.Role}', 'Role', 'Role']);
                        strings.push(['{Sub.Company}', 'Company', 'Company']);
                        strings.push(['{Sub.Department}', 'Department', 'Department']);
                        strings.push(['{Sub.DeviceType}', 'DeviceType', 'DeviceType']);
                        strings.push(['{Sub.FAP}', 'FAP', 'FAP']);
                        strings.push(['{Sub.Cadre}', 'Cadre', 'Cadre']);
                        strings.push(['{Sub.ARPU}', 'ARPU', 'ARPU']);
                        strings.push(['{Sub.Subscriberidentity}', 'Subscriber Identity', 'Subscriber Identity']);                                              
                        strings.push(['{Sub.ParentId}', 'Parent ID', 'Parent ID']);                                               
                        strings.push(['{Sub.GroupName}', 'Group Name', 'Group Name']);  											  
						strings.push(['{Sub.Email}', 'Email', 'Email']);  											  
						strings.push(['{Sub.Phone}', 'Phone', 'Phone']);    										  
						strings.push(['{Sub.SIPURL}', 'SIPURL', 'SIPURL']);    										  
						strings.push(['{Sub.CUI}', 'CUI', 'CUI']);    										  
						strings.push(['{Sub.IMSI}', 'IMSI', 'IMSI']);    										  
						strings.push(['{Sub.MSISDN}', 'MSISDN', 'MSISDN']);    										  
						strings.push(['{Sub.IMEI}', 'IMEI', 'IMEI']);  											  
						strings.push(['{Sub.ESN}', 'ESN', 'ESN']);    										  
						strings.push(['{Sub.MEID}', 'MEID', 'MEID']);    										  
						strings.push(['{Sub.MAC}', 'MAC', 'MAC']);    										  
						strings.push(['{Sub.EUI64}', 'EUI64', 'EUI64']);
						strings.push(['{Sub.MODIFIED_EUI64}', 'MODIFIED_EUI64', 'MODIFIED_EUI64']);    										                                                
						// add the menu to the editor
						editor.ui.addRichCombo('netvertex',
						{
							label: 		'Select Tag',
							title: 		'Select Tag',							
							voiceLabel: '-Select Tag-',
							className: 	'cke_format',
							order:9,
							multiSelect:false,
							panel:
							{
								css: [ editor.config.contentsCss, CKEDITOR.skin.getPath('editor') ],
								voiceLabel: editor.lang.panelVoiceLabel
							},

							init: function()
							{
								this.startGroup( "Insert Content" );
								for (var i in strings)
								{
									this.add(strings[i][0], strings[i][1], strings[i][2]);
								}
							},

							onClick: function( value )
							{
								editor.focus();
								editor.fire( 'saveSnapshot' );
								editor.insertHtml(value);
								editor.fire( 'saveSnapshot' );
							}
						});											
				}
	});
})();