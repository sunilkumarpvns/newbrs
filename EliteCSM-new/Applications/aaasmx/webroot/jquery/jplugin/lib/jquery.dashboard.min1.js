(function (a) {
	var parentId = ($(this).attr('id'));        	
    a.fn.dashboard = function (m) {
        var k = {};
        var f;
        var j;
        k.layout;
        k.element = this;
        k.id = this.attr("id");
        
        k.widgets = {};
        k.widgetsToAdd = {};
        k.widgetCategories = {};
        k.initialized = false;
        k.serialize = function () {
            k.log("entering serialize function", 1);
            var o = '{"layout": "' + k.layout.id + '", "data" : [';
            var n = 0;
            if (a("." + b.columnClass).length == 0) {
                k.log(b.columnClass + " class not found", 5)
            }
            
            a("." + b.columnClass).each(function () {
                a(this).children().each(function () {
                    if (a(this).hasClass(b.widgetClass)) {
                        if (n > 0) {
                            o += ","
                        }
                        o += (k.getWidget(a(this).attr("id"))).serialize();
                        n++
                    }
                })
            });
            o += "]}";
            return o
        };
        k.log = function (o, p) {
            if (p >= b.debuglevel && typeof console != "undefined") {
                var n = "";
                if (p == 1) {
                    n = "INFO"
                }
                if (p == 2) {
                    n = "EVENT"
                }
                if (p == 3) {
                    n = "WARNING"
                }
                if (p == 5) {
                    n = "ERROR"
                }
                console.log(n + " - " + o)
            }
        };
        k.setLayout = function (n) {
            if (n != null) {
                k.log("entering setLayout function with layout " + n.id, 1)
            } else {
                k.log("entering setLayout function with layout null", 1)
            }
            k.layout = n;
            f.remove();
            if (k.layout != null) {
                if (typeof b.layoutClass != "undefined") {
                    this.element.find("." + b.layoutClass).addClass(k.layout.classname)
                } else {
                    this.element.html(k.layout.html)
                }
            }
            a("." + b.columnClass).sortable({
                connectWith: a("." + b.columnClass),
                opacity: b.opacity,
                handle: "." + b.widgetHeaderClass,
                over: function (o, p) {
                    a(this).addClass("selectedcolumn");
                },
                out: function (o, p) {
                    a(this).removeClass("selectedcolumn");
                },
                receive: function (p, q) {
               
                	var columnId=$(this).attr('id');
					var sortOrderArray = $(this).sortable('toArray');
					sortOrderArray=JSON.stringify(sortOrderArray);
					
					$.ajax({url:'ChangeWidgetOrder',
		        	        type:'POST',
		        	        data:{columnId:columnId,sortOrderArray:sortOrderArray},
		        	        async:false,
		        	        success: function(transport){
		        	      }
		        	   });
                	
                	
                	var o = k.getWidget(q.item.attr("id"));
                    o.column = e(a(this).attr("class"));
                    k.log("dashboardStateChange event thrown for widget " + o.id, 2);
                    k.element.trigger("dashboardStateChange", {
                        stateChange: "widgetMoved",
                        widget: o
                    });
                    k.log("widgetDropped event thrown for widget " + o.id, 2);
                    o.element.trigger("widgetDropped", {
                        widget: o
                    });
                },
                deactivate: function (p, q) {
                    k.log("Widget is dropped: check if the column is now empty.", 1);
                    var o = a(this).children().length;
                    if (o == 0) {
                        k.log("adding the empty text to the column", 1);
                        a(this).html('<div class="emptycolumn">' + b.emptyColumnHtml + "</div>");
                    } else {
                        if (o == 2) {
                            a(this).find(".emptycolumn").remove();
                        }
                    }
                },
                start: function (o, p) {
                    p.item.find("." + b.widgetTitleClass).addClass("noclick");
                },
                stop: function (o, p) {
                    setTimeout(function () {
                        p.item.find("." + b.widgetTitleClass).removeClass("noclick");
                    }, 300);
                },update : function(o, p) {
                	var columnId=$(this).attr('id');
					var sortOrderArray = $(this).sortable('toArray');
					sortOrderArray=JSON.stringify(sortOrderArray);
					
					$.ajax({url:'ChangeWidgetOrder',
		        	        type:'POST',
		        	        data:{columnId:columnId,sortOrderArray:sortOrderArray},
		        	        async:false,
		        	        success: function(transport){
		        	      }
		        	   });
					
                }
            });
            g();
            k.log("dashboardLayoutLoaded event thrown", 2);
            k.element.trigger("dashboardLayoutLoaded")
        };

        function g() {
            k.log("entering fixSortableColumns function", 1);
            a(".nonsortablecolumn").removeClass("nonsortablecolumn").addClass(b.columnClass);
            a("." + b.columnClass).filter(function () {
                return a(this).css("display") == "none"
            }).addClass("nonsortablecolumn").removeClass(b.columnClass)
        }

        function e(o) {
            k.log("entering getColumnIdentifier function", 1);
            var q;
            var p = o.split(" ");
            for (var n = 0; n < p.length; n++) {
                if (p[n].indexOf(b.columnPrefix) === 0) {
                    q = p[n]
                }
            }
            return q.replace(b.columnPrefix, "")
        }
        k.loadLayout = function () {
            k.log("entering loadLayout function", 1);
            if (typeof b.json_data.url != "undefined" && b.json_data.url.length > 0) {
                k.log("Getting JSON feed : " + b.json_data.url, 1);
                a.getJSON(b.json_data.url, function (p) {
                    if (p == null) {
                        alert("Unable to get json. If you are using chrome: there is an issue with loading json with local files. It works on a server :-)", 5);
                        return
                    }
                    var q = p.result;
                    var o = (typeof k.layout != "undefined") ? k.layout : c(q.layout);
                    k.setLayout(o);
                    k.loadWidgets(q.data)
                })
            } else {
                var n = (typeof k.layout != "undefined") ? k.layout : c(json.layout);
                k.setLayout(n);
                k.loadWidgets(b.json_data.data)
            }
        };
        k.addWidget = function (r, p) {
            k.log("entering addWidget function", 1);
            var q = r.id;
            if (typeof k.widgets[q] != "undefined" && a("#" + q).length > 0) {
                var n = a("#" + q);
                p = k.widgets[q].column;
                n.appendTo(p)
            } else {
                k.log("Applying template : " + b.widgetTemplate, 1);
                if (a("#" + b.widgetTemplate).length == 0) {
                    k.log('Template "' + b.widgetTemplate + " not found", 5)
                }
                var o = tmpl(a("#" + b.widgetTemplate).html(), r);
                var n = a(o);
                n.appendTo(p);
                k.widgets[q] = h({
                    id: q,
                    element: n,
                    column: r.column,
                    url: (typeof r.url != "undefined" ? r.url : null),
                    editurl: r.editurl,
                    title: r.title,
                    open: r.open,
                    metadata: r.metadata
                })
            }
            k.log("widgetAdded event thrown for widget " + q, 2);
            k.widgets[q].element.trigger("widgetAdded", {
                widget: k.widgets[q]
            });
            if (k.initialized) {
                k.log("dashboardStateChange event thrown for widget " + q, 2);
                k.element.trigger("dashboardStateChange", {
                    stateChange: "widgetAdded",
                    widget: n
                })
            }
        };
        k.loadWidgets = function (n) {
            k.log("entering loadWidgets function", 1);
            k.element.find("." + b.columnClass).empty();
            a(n).each(function () {
                var o = this.column;
                var isElementExist="";
                var widgetId=$(this).attr('id');
                $.ajax({url:'CheckWidgetConfiguration',
        	        type:'GET',
        	        data:{widgetId:widgetId},
        	        async:false,
        	        success: function(transport){
        	        	isElementExist=transport;
        	      }
        	   });
                var jspUrl=$(this).attr('url');
                jspUrl+="?widgetId="+widgetId;
                $(this).attr('url',jspUrl);
                
                var configUrl=$(this).attr('editurl');
                configUrl+="?widgetId="+widgetId;
                $(this).attr('editurl',configUrl);
                
                if(isElementExist == "false"){
                	var editJspUrl=$(this).attr('editurl');
                    $(this).attr('url',editJspUrl);
                }
             
                k.addWidget(this, k.element.find("." + b.columnPrefix + o));
            });
            a("#tempdashboard").find("." + b.widgetClass).each(function () {
                var o = k.element.find("." + b.columnClass + ":first");
                a(this).appendTo(o);
                k.getWidget(a(this).attr("id")).column = o.attr("id")
            });
            a("#tempdashboard").remove();
            a("." + b.columnClass).each(function () {
                if (a(this).children().length == 0) {
                    a(this).html('<div class="emptycolumn">' + b.emptyColumnHtml + "</div>")
                }
            });
            k.initialized = true
        };
        k.init = function () {
            k.log("entering init function", 1);
            k.loadLayout()
        };
        k.getWidget = function (o) {
            k.log("entering getWidget function", 1);
            var n = k.widgets[o];
            if (typeof n != "undefined") {
                return n
            } else {
                return null
            }
        };
        var b = a.extend({}, a.fn.dashboard.defaults, m);
        var l = a.extend({}, a.fn.dashboard.defaults.addWidgetSettings, m.addWidgetSettings);
        var d = a.extend({}, a.fn.dashboard.defaults.editLayoutSettings, m.editLayoutSettings);
        var f = a(b.loadingHtml).appendTo(k.element);

        function h(n) {
        	//alert('load widget');
            k.log("entering widget constructor", 1);
            n = a.extend({}, a.fn.dashboard.widget.defaults, n);
            n.openContent = function () {
                n.element.find(".widgetOpen").hide();
                n.element.find(".widgetClose").show();
                k.log("entering openContent function", 1);
                n.open = true;
                if (!n.loaded) {
                    if (this.url != "" && this.url != null && typeof this.url != "undefined") {
                        a(b.loadingHtml).appendTo(n.element.find("." + b.widgetContentClass));
                        k.log("widgetShow event thrown for widget " + n.id, 2);
                        n.element.trigger("widgetShow", {
                            widget: n
                        });
                        n.element.find("." + b.widgetContentClass).load(this.url, function (p, o, q) {
                        	if (o == "error") {
                                n.element.find("." + b.widgetContentClass).html(p);
                            }
                            n.loaded = true;
                            k.log("widgetLoaded event thrown for widget " + n.id, 2);
                            n.element.trigger("widgetLoaded", {
                                widget: n
                            });
                        });
                    } else {
                        k.log("widgetShow event thrown for widget " + n.id, 2);
                        n.element.trigger("widgetShow", {
                            widget: n
                        });
                        k.log("widgetLoaded event thrown", 2);
                        n.element.trigger("widgetLoaded", {
                            widget: n
                        });
                    }
                } else {
                    k.log("widgetShow event thrown for widget " + n.id, 2);
                    n.element.trigger("widgetShow", {
                        widget: n
                    })
                }
                if (k.initialized) {
                    k.log("dashboardStateChange event thrown for widget " + n.id, 2);
                    k.element.trigger("dashboardStateChange", {
                        stateChange: "widgetOpened",
                        widget: n
                    })
                }
            };
          
            n.refreshContent = function () {
                k.log("entering refreshContent function", 1);
                n.loaded = false;
                if (n.open) {
                    n.openContent();
                }
            };
            n.setTitle = function (o) {
                k.log("entering setTitle function", 1);
                n.title = o;
                n.element.find("." + b.widgetTitleClass).html(o);
                if (k.initialized) {
                    k.log("dashboardStateChange event thrown for widget " + n.id, 2);
                    k.element.trigger("dashboardStateChange", {
                        stateChange: "titleChanged",
                        widget: n
                    })
                }
            };
            n.closeContent = function () {
                k.log("entering closeContent function", 1);
                n.open = false;
                k.log("widgetHide event thrown for widget " + n.id, 2);
                n.element.trigger("widgetHide", {
                    widget: n
                });
                n.element.find(".widgetOpen").show();
                n.element.find(".widgetClose").hide();
                k.log("dashboardStateChange event thrown for widget " + n.id, 2);
                k.element.trigger("dashboardStateChange", {
                    stateChange: "widgetClosed",
                    widget: n
                })
            };
            n.addMetadataValue = function (o, p) {
                k.log("entering addMetadataValue function", 1);
                if (typeof n.metadata == "undefined") {
                    n.metadata = {}
                }
                n.metadata[o] = p;
                k.log("dashboardStateChange event thrown for widget " + n.id, 2);
                k.element.trigger("dashboardStateChange", {
                    stateChange: "metadataChanged",
                    widget: n
                })
            };
            n.openMenu = function () {
                k.log("entering openMenu function", 1);
                n.element.find("." + b.menuClass).show()
            };
            n.closeMenu = function () {
                k.log("entering closeMenu function", 1);
                n.element.find("." + b.menuClass).hide()
            };
            n.remove = function () {
                k.log("entering remove function", 1);
                
                var widgetContentDiv = $('#'+n.id).find('#widgetcontent'+n.id).find('div.widget-class');
                var widgetIds = $(widgetContentDiv).attr('id');
                
                if(widgetIds){
                	var data = {
                			header : {
                				id : widgetIds,
                				type : 'DEREGISTER'
                			},
                			body : {
                				deRegister : 'deRegister'
                			}
                	};
                		
                	getDashBoardSocket().deRegister(widgetIds);
                	getDashBoardSocket().sendRequest(data);
                }
                
                n.element.remove();
                k.log("widgetDeleted event thrown for widget " + n.id, 2);
                var removeWidgetId=n.id;
               
                
                $.ajax({url:'RemoveWidgetDetails',
        	        type:'POST',
        	        data:{widgetId:removeWidgetId},
        	        async:false,
        	        success: function(transport){
        	      }
        	   });
                
                n.element.trigger("widgetDeleted", {
                    widget: n
                });
                k.log("dashboardStateChange event thrown for widget " + n.id, 2);
                k.element.trigger("dashboardStateChange", {
                    stateChange: "widgetRemoved",
                    widget: n
                })
            };
            n.serialize = function () {
                k.log("entering serialize function", 1);
                var q = '{"title" : "' + n.title + '", "id" : "' + n.id + '", "column" : "' + n.column + '","editurl" : "' + n.editurl + '","open" : ' + n.open + ',"url" : "' + n.url + '"';
                if (typeof n.metadata != "undefined") {
                    q += ',"metadata":{';
                    var s = n.metadata;
                    var o = 0;
                    for (var p in s) {
                        if (o > 0) {
                            q += ","
                        }
                        q += '"' + p + '":"' + s[p] + '"';
                        o++
                    }
                    q += "}"
                }
                q += "}";
                return q
            };
            n.openFullscreen = function () {
                k.log("entering openFullscreen function", 1);
                n.fullscreen = true;
                var p = a("." + b.columnClass);
                p.hide();
                var o = a('<ul id="fullscreen_' + k.id + '" style="padding-left:10px;padding-right:10px;"></ul>');
                o.appendTo(k.element);
                n.element.parent().attr("id", "placeholder");
                n.element.appendTo(o)
            };
            n.closeFullscreen = function () {
                k.log("entering closeFullscreen function", 1);
                n.fullscreen = false;
                var o = a("#placeholder");
                n.element.appendTo(o);
                a("#fullscreen_" + k.id).remove();
                var p = a("." + b.columnClass);
                p.show();
            };
            n.openSettings = function () {

                k.log("entering openSettings function", 1);
                n.element.trigger("editSettings", {
                    widget: n
                });
            };
            if (n.open) {
                n.openContent();
            }
            n.initialized = true;
            k.log("widgetInitialized event thrown", 2);
            n.element.trigger("widgetInitialized", {
                widget: n
            });
            return n;
        }

        function c(p) {
            k.log("entering getLayout function", 1);
            var n = null;
            var o = null;
            if (typeof b.layouts != "undefined") {
                a.each(b.layouts, function (q, r) {
                    if (q == 0) {
                        o = r;
                    }
                    if (r.id == p) {
                        n = r;
                    }
                });
            }
            if (n == null) {
                n = o;
            }
            return n;
        }
        a("#" + k.id + " .menutrigger").live("click", function () {
            k.log("widgetOpenMenu event thrown for widget " + h.id, 2);
            var n = k.getWidget(a(this).closest("." + b.widgetClass).attr("id"));
            n.element.trigger("widgetOpenMenu", {
                widget: n
            });
            return false
        });
        a("#" + k.id + " ." + b.widgetFullScreenClass).live("click", function (o) {
            k.log("widgetCloseMenu event thrown for widget " + h.id, 2);
            var n = k.getWidget(a(this).closest("." + b.widgetClass).attr("id"));
            n.element.trigger("widgetCloseMenu", {
                widget: n
            });
            if (n.fullscreen) {
                k.log("widgetCloseFullScreen event thrown for widget " + n.id, 2);
                n.element.trigger("widgetCloseFullScreen", {
                    widget: n
                });
            } else {
                k.log("widgetOpenFullScreen event thrown for widget " + n.id, 2);
                n.element.trigger("widgetOpenFullScreen", {
                    widget: n
                });
            }
            return false;
        });

        a("#" + k.id + " .controls li").live("click", function (o) {
            k.log("widgetCloseMenu event thrown for widget " + h.id, 2);
            var n = k.getWidget(a(this).closest("." + b.widgetClass).attr("id"));
            n.element.trigger("widgetCloseMenu", {
                widget: n
            });
            k.log(a(this).attr("class") + " event thrown for widget " + h.id, 2);
            var n = k.getWidget(a(this).closest("." + b.widgetClass).attr("id"));
            n.element.trigger(a(this).attr("class"), {
                widget: n
            });
            return false
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetCloseMenu", function (n, p) {
            k.log("Closing menu " + a(this).attr("id"), 1);
            p.widget.closeMenu()
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetOpenMenu", function (n, p) {
            k.log("Opening menu " + a(this).attr("id"), 1);
            p.widget.openMenu()
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetDelete", function (n, p) {
            if (confirm(b.deleteConfirmMessage)) {
                k.log("Removing widget " + a(this).attr("id"), 1);
                p.widget.remove()
            }
        });

        a("#" + k.id + " ." + b.widgetClass).live("widgetExport", function (n, p) {
        	var ids = a(this).attr("id"); 
        	$( "#exportbtn_"+ ids ).triggerHandler( "click" );
        });
        
        
        a("#" + k.id + " ." + b.widgetClass).live("widgetRefresh", function (n, p) {
        	p.widget.refreshContent()
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetSetTitle", function (n, p) {
            p.widget.setTitle(p.title)
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetClose", function (n, p) {
            k.log("Closing widget " + a(this).attr("id"), 1);
            p.widget.closeContent()
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetOpen", function (n, p) {
            k.log("Opening widget " + a(this).attr("id"), 1);
            p.widget.openContent()
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetShow", function () {
            a(this).find("." + b.widgetContentClass).show()
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetHide", function () {
            a(this).find("." + b.widgetContentClass).hide()
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetAddMetadataValue", function (n, p) {
            k.log("Changing metadata for widget " + a(this).attr("id") + ", metadata name: " + p.name + ", value: " + p.value, 1);
            p.widget.addMetadataValue(p.name, p.value)
        });
        a("#" + k.id + " ." + b.widgetTitleClass).live("click", function (o) {
            k.log("Click on the header detected for widget " + a(this).attr("id"), 1);
            if (!a(this).hasClass("noclick")) {
                var n = k.getWidget(a(this).closest("." + b.widgetClass).attr("id"));
                if (n.open) {
                    k.log("widgetClose event thrown for widget " + n, 2);
                    n.element.trigger("widgetClose", {
                        widget: n
                    })
                } else {
                    k.log("widgetOpen event thrown for widget " + n, 2);
                    n.element.trigger("widgetOpen", {
                        widget: n
                    })
                }
            }
            return false
        });
        a("#" + k.id + " ." + b.widgetHeaderClass).live("mouseover", function () {
            a(this).find("." + b.iconsClass).removeClass("hidden")
        });
        a("#" + k.id + " ." + b.widgetHeaderClass).live("mouseout", function () {
            a(this).find("." + b.iconsClass).addClass("hidden")
        });
        a("body").click(function () {
            a("." + b.menuClass).hide()
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetOpenFullScreen", function (n, p) {
            var Id = a(this).attr("id");
            $("#" + Id).find("#widgetcontent"+Id).find("#editConfigPage").val();
            if ($("#" + Id).find("#widgetcontent"+Id).find("#editConfigPage").val()) {
                p.widget.openFullscreen();
            } else {
                p.widget.openFullscreen();
                p.widget.refreshContent();
            }
        });
        a("." + b.widgetClass).live("widgetCloseFullScreen", function (n, p) {
            var Id = a(this).attr("id");
            var idpr = $('#widgetcontent'+Id).parent().attr('id');
            $("#" + Id).find("#widgetcontent"+Id).find("#editConfigPage").val();
            if ($("#" + Id).find("#widgetcontent"+Id).find("#editConfigPage").val()) {
                p.widget.closeFullscreen();
            } else {
                p.widget.closeFullscreen();
                p.widget.refreshContent();
            }
        });
        a("#" + k.id + " ." + b.widgetClass).live("widgetEdit", function (n, p) {
            var Id = a(this).attr("id");
            var widgetEditJsp=$('#widgetcontent'+Id).find('#editJsp').val();
            if(typeof(widgetEditJsp) !== "undefined"){
	            $.ajax({
	                    url: widgetEditJsp,
	                    success: function (result) {
	                        $("#widgetcontent"+Id).html(result);
	                    }
	            });
	            p.widget.openSettings();
            }
        });
        if (a("#" + l.dialogId).length == 0) {
            k.log("Unable to find " + l.dialogId, 5)
        }
        a("#" + l.dialogId).dialog({
            autoOpen: false,
            height: 470,
            width: 770,
            modal: true,
            buttons: {
                Finished: function () {
                    a(this).dialog("close")
                }
            },
            close: function () {}
        });

        if (a("#" + d.dialogId).length == 0) {
            k.log("Unable to find " + d.dialogId, 5)
        }
        a("#" + d.dialogId).dialog({
            autoOpen: false,
            height: 300,
            width: 600,
            modal: true
        });

        a("." + d.openDialogClass).live("click", function () {
            k.log("dashboardOpenLayoutDialog event thrown", 2);
            k.element.trigger("dashboardOpenLayoutDialog");
            return false
        });
        k.element.live("dashboardOpenLayoutDialog", function () {
            k.log("Opening dialog " + d.dialogId, 1);
            a("#" + d.dialogId).dialog("open");
            var n = a("#" + d.dialogId).find("." + d.layoutClass);
            n.empty();
            if (n.children().length == 0) {
                k.log("Number of layouts : " + b.layouts.length, 1);
                a.each(b.layouts, function (o, p) {
                    k.log("Applying template : " + d.layoutTemplate, 1);
                    if (a("#" + d.layoutTemplate).length == 0) {
                        k.log('Template "' + d.layoutTemplate + " not found", 5)
                    }
                    n.append(tmpl(a("#" + d.layoutTemplate).html(), p))
                })
            }
            a("." + d.selectLayoutClass).removeClass(d.selectedLayoutClass);
            a("#" + k.layout.id).addClass(d.selectedLayoutClass);
            i()
        });
        k.element.live("dashboardStateChange", function () {
            if (typeof b.stateChangeUrl != "undefined" && b.stateChangeUrl != null && b.stateChangeUrl != "") {
                a.ajax({
                    type: "POST",
                    url: b.stateChangeUrl,
                    data: {
                        dashboard: k.element.attr("id"),
                        settings: k.serialize()
                    },
                    success: function (n) {
                        if (n == "NOK" || n.indexOf("<response>NOK</response>") != -1) {
                            k.log("dashboardSaveFailed event thrown", 2);
                            k.element.trigger("dashboardSaveFailed")
                        } else {
                            k.log("dashboardSuccessfulSaved event thrown", 2);
                            k.element.trigger("dashboardSuccessfulSaved")
                        }
                    },
                    error: function (n, p, o) {
                        k.log("dashboardSaveFailed event thrown", 2);
                        k.element.trigger("dashboardSaveFailed")
                    },
                    dataType: "text"
                })
            }
        });
        k.element.live("dashboardCloseLayoutDialog", function () {
            a("#" + d.dialogId).dialog("close")
        });

        function i() {
            if (a("." + d.selectLayoutClass).length == 0) {
                k.log("Unable to find " + d.selectLayoutClass, 5)
            }
            a("." + d.selectLayoutClass).bind("click", function (q) {
                var o = k.layout;
                k.log("dashboardCloseLayoutDialog event thrown", 2);
                k.element.trigger("dashboardCloseLayoutDialog");
                var p = c(a(this).attr("id"));
                k.layout = p;
                if (typeof b.layoutClass != "undefined") {
                    k.element.find("." + b.layoutClass).removeClass(o.classname).addClass(p.classname);
                    g();
                    if (a("." + b.columnClass).length == 0) {
                        k.log("Unable to find " + b.columnClass, 5)
                    }
                    k.element.find(".nonsortablecolumn").each(function () {
                        a(this).children().appendTo(k.element.find("." + b.columnClass + ":first"));
                        a(".emptycolumn").remove();
                        a("." + b.columnClass).each(function () {
                            if (a(this).children().length == 0) {
                                a(this).html('<div class="emptycolumn">' + b.emptyColumnHtml + "</div>")
                            }
                        })
                    })
                } else {
                    var n = a('<div style="display:none" id="tempdashboard"></div>');
                    n.appendTo(a("body"));
                    k.element.children().appendTo(n);
                    k.init()
                }
                k.log("dashboardChangeLayout event thrown", 2);
                k.element.trigger("dashboardLayoutChanged")
            });
            return false
        }
        a("." + l.selectCategoryClass).live("click", function () {
            k.log("addWidgetDialogSelectCategory event thrown", 2);
            k.element.trigger("addWidgetDialogSelectCategory", {
                category: a(this)
            });
           // alert('select');
            return false
        });
        k.element.live("addWidgetDialogSelectCategory", function (o, n) {
            a("." + l.selectCategoryClass).removeClass(l.selectedCategoryClass);
            a("#" + l.dialogId).find("." + l.widgetClass).empty();
            a(n.category).addClass(l.selectedCategoryClass);
            url = k.widgetCategories[a(n.category).attr("id")];
            //alert("url : " +url);
            k.log("Getting JSON feed : " + url, 1);
            a.getJSON(url, {
                cache: true
            }, function (q) {
                if (q.result.data == 0) {
                    k.log("Empty data returned", 3)
                }
                var p = q.result.data;
                if (typeof q.result.data.length == "undefined") {
                    p = new Array(q.result.data)
                }
                a.each(p, function (s, t) {
                    k.widgetsToAdd[t.id] = t;
                    
                    k.log("Applying template : " + l.widgetTemplate, 1);
                    if (a("#" + l.widgetTemplate).length == 0) {
                        k.log('Template "' + l.widgetTemplate + " not found", 5)
                    }
                    var r = tmpl(a("#" + l.widgetTemplate).html(), t);
                    a("#" + l.dialogId).find("." + l.widgetClass).append(r);
                    
                    var seen = {};
                    $('.widgets li').each(function() {
                        var txt = $(this).text();
                        if (seen[txt])
                            $(this).remove();
                        else
                            seen[txt] = true;
                    });
                   
                })
            });
            k.log("addWidgetDialogWidgetsLoaded event thrown", 2);
            k.element.trigger("addWidgetDialogWidgetsLoaded")
        });
        a("." + l.addWidgetClass).live("click", function () {        	
        	var n = k.widgetsToAdd[a(this).attr("id").replace("addwidget", "")];
            k.log("dashboardAddWidget event thrown", 2);
            k.element.trigger("dashboardAddWidget", {
                widget: n
            });
            k.log("dashboardCloseWidgetDialog event thrown", 2);
            k.element.trigger("dashboardCloseWidgetDialog");
            return false
        });
        a("." + l.openDialogClass).live("click", function () {
            k.log("dashboardOpenWidgetDialog event thrown", 2);
            k.element.trigger("dashboardOpenWidgetDialog");
            return false;
        });
        k.element.live("dashboardCloseWidgetDialog", function () {
            a("#" + l.dialogId).dialog("close")
        });
        k.element.live("dashboardOpenWidgetDialog", function () {
            a("#" + l.dialogId).find("." + l.categoryClass).empty();
            a("#" + l.dialogId).find("." + l.widgetClass).empty();
            k.log("Opening dialog " + l.dialogId, 1);
            
            if(navigator.userAgent.toLowerCase().indexOf('firefox') > -1){
            	 a("#" + l.dialogId).dialog("open");
            }else{
            	 a("#" + l.dialogId).dialog({ 
                     height: 470,
                     width: 770,
                     modal: true,
                     buttons: {
                         Finished: function () {
                             a(this).dialog("close")
                         }
                     },
                     close: function () {}
                    });
            }
            
          //  alert(l.widgetDirectoryUrl);
            k.log("Getting JSON feed : " + l.widgetDirectoryUrl, 1);
            a.getJSON(l.widgetDirectoryUrl, function (n) {
                if (n.category == 0) {
                    k.log("Empty data returned", 3)
                }
                a.each(n.categories.category, function (p, q) {
                    k.widgetCategories[q.id] = q.url;
                    //alert(q.url);
                    k.log("Applying template : " + l.categoryTemplate, 1);
                    if (a("#" + l.categoryTemplate).length == 0) {
                        k.log('Template "' + l.categoryTemplate + " not found", 5)
                    }
                    var o = tmpl(a("#" + l.categoryTemplate).html(), q);
                    a("#" + l.dialogId).find("." + l.categoryClass).append(o)
                });
                k.log("addWidgetDialogCategoriesLoaded event thrown", 2);
                k.element.trigger("addWidgetDialogCategoriesLoaded");
                k.log("addWidgetDialogSelectCategory event thrown", 2);
                k.element.trigger("addWidgetDialogSelectCategory", {
                    category: a("#" + l.dialogId).find("." + l.categoryClass + ">li:first")
                })
            })
        });
        return k
    };
    a.fn.dashboard.defaults = {
        debuglevel: 3,
        json_data: {},
        loadingHtml: '<div class="loading"></div>',
        emptyColumnHtml: "",
        widgetTemplate: "widgettemplate",
        columnPrefix: "column-",
        opacity: "0.2",
        deleteConfirmMessage: "Are you sure you want to delete this widget?",
        widgetNotFoundHtml: "",
        columnClass: "column",
        widgetClass: "widget",
        menuClass: "controls",
        widgetContentClass: "widgetcontent",
        widgetTitleClass: "widgettitle",
        widgetHeaderClass: "widgetheader",
        widgetFullScreenClass: "widgetopenfullscreen",
        iconsClass: "icons",
        stateChangeUrl: "",
        addWidgetSettings: {
            openDialogClass: "openaddwidgetdialog",
            addWidgetClass: "addwidget",
            selectCategoryClass: "selectcategory",
            selectedCategoryClass: "selected",
            categoryClass: "categories",
            widgetClass: "widgets",
            dialogId: "addwidgetdialog",
            categoryTemplate: "categorytemplate",
            widgetTemplate: "addwidgettemplate"
        },
        editLayoutSettings: {
            dialogId: "editLayout",
            layoutClass: "layoutselection",
            selectLayoutClass: "layoutchoice",
            selectedLayoutClass: "selected",
            openDialogClass: "editlayout",
            layoutTemplate: "selectlayouttemplate"
        }
    };
    a.fn.dashboard.widget = {
        defaults: {
            open: true,
            fullscreen: false,
            loaded: false,
            url: "",
            metadata: {}
        }
    }
})(jQuery);
(function () {
    var b = {};
    this.tmpl = function a(e, d) {
        var c = !/\W/.test(e) ? b[e] = b[e] || a(document.getElementById(e).innerHTML) : new Function("obj", "var p=[],print=function(){p.push.apply(p,arguments);};with(obj){p.push('" + e.replace(/[\r\t\n]/g, " ").split("<%").join("\t").replace(/((^|%>)[^\t]*)'/g, "$1\r").replace(/\t=(.*?)%>/g, "',$1,'").split("\t").join("');").split("%>").join("p.push('").split("\r").join("\\'") + "');}return p.join('');");
        return d ? c(d) : c
    }
})();
