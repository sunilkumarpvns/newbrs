/*! AUI Flat Pack - version 5.2 - generated 2013-07-25 10:18:38 +0000 */
/*
 * jQuery JavaScript Library v1.8.3
 * http://jquery.com/
 *
 * Includes Sizzle.js
 * http://sizzlejs.com/
 *
 * Copyright 2012 jQuery Foundation and other contributors
 * Released under the MIT license
 * http://jquery.org/license
 *
 * Date: Tue Nov 13 2012 08:20:33 GMT-0500 (Eastern Standard Time)
 */
(function (a2, aB) {
    var w, af, o = a2.document,
        aI = a2.location,
        d = a2.navigator,
        bg = a2.jQuery,
        I = a2.$,
        am = Array.prototype.push,
        a4 = Array.prototype.slice,
        aK = Array.prototype.indexOf,
        z = Object.prototype.toString,
        V = Object.prototype.hasOwnProperty,
        aO = String.prototype.trim,
        bG = function (e, bZ) {
            return new bG.fn.init(e, bZ, w)
        }, bx = /[\-+]?(?:\d*\.|)\d+(?:[eE][\-+]?\d+|)/.source,
        aa = /\S/,
        aV = /\s+/,
        C = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,
        bo = /^(?:[^#<]*(<[\w\W]+>)[^>]*$|#([\w\-]*)$)/,
        a = /^<(\w+)\s*\/?>(?:<\/\1>|)$/,
        bf = /^[\],:{}\s]*$/,
        bi = /(?:^|:|,)(?:\s*\[)+/g,
        bD = /\\(?:["\\\/bfnrt]|u[\da-fA-F]{4})/g,
        a0 = /"[^"\\\r\n]*"|true|false|null|-?(?:\d\d*\.|)\d+(?:[eE][\-+]?\d+|)/g,
        bP = /^-ms-/,
        aU = /-([\da-z])/gi,
        N = function (e, bZ) {
            return (bZ + "").toUpperCase()
        }, aF = function () {
            if (o.addEventListener) {
                o.removeEventListener("DOMContentLoaded", aF, false);
                bG.ready()
            } else {
                if (o.readyState === "complete") {
                    o.detachEvent("onreadystatechange", aF);
                    bG.ready()
                }
            }
        }, Z = {};
    bG.fn = bG.prototype = {
        constructor: bG,
        init: function (e, b2, b1) {
            var b0, b3, bZ, b4;
            if (!e) {
                return this
            }
            if (e.nodeType) {
                this.context = this[0] = e;
                this.length = 1;
                return this
            }
            if (typeof e === "string") {
                if (e.charAt(0) === "<" && e.charAt(e.length - 1) === ">" && e.length >= 3) {
                    b0 = [null, e, null]
                } else {
                    b0 = bo.exec(e)
                } if (b0 && (b0[1] || !b2)) {
                    if (b0[1]) {
                        b2 = b2 instanceof bG ? b2[0] : b2;
                        b4 = (b2 && b2.nodeType ? b2.ownerDocument || b2 : o);
                        e = bG.parseHTML(b0[1], b4, true);
                        if (a.test(b0[1]) && bG.isPlainObject(b2)) {
                            this.attr.call(e, b2, true)
                        }
                        return bG.merge(this, e)
                    } else {
                        b3 = o.getElementById(b0[2]);
                        if (b3 && b3.parentNode) {
                            if (b3.id !== b0[2]) {
                                return b1.find(e)
                            }
                            this.length = 1;
                            this[0] = b3
                        }
                        this.context = o;
                        this.selector = e;
                        return this
                    }
                } else {
                    if (!b2 || b2.jquery) {
                        return (b2 || b1).find(e)
                    } else {
                        return this.constructor(b2).find(e)
                    }
                }
            } else {
                if (bG.isFunction(e)) {
                    return b1.ready(e)
                }
            } if (e.selector !== aB) {
                this.selector = e.selector;
                this.context = e.context
            }
            return bG.makeArray(e, this)
        },
        selector: "",
        jquery: "1.8.3",
        length: 0,
        size: function () {
            return this.length
        },
        toArray: function () {
            return a4.call(this)
        },
        get: function (e) {
            return e == null ? this.toArray() : (e < 0 ? this[this.length + e] : this[e])
        },
        pushStack: function (bZ, b1, e) {
            var b0 = bG.merge(this.constructor(), bZ);
            b0.prevObject = this;
            b0.context = this.context;
            if (b1 === "find") {
                b0.selector = this.selector + (this.selector ? " " : "") + e
            } else {
                if (b1) {
                    b0.selector = this.selector + "." + b1 + "(" + e + ")"
                }
            }
            return b0
        },
        each: function (bZ, e) {
            return bG.each(this, bZ, e)
        },
        ready: function (e) {
            bG.ready.promise().done(e);
            return this
        },
        eq: function (e) {
            e = +e;
            return e === -1 ? this.slice(e) : this.slice(e, e + 1)
        },
        first: function () {
            return this.eq(0)
        },
        last: function () {
            return this.eq(-1)
        },
        slice: function () {
            return this.pushStack(a4.apply(this, arguments), "slice", a4.call(arguments).join(","))
        },
        map: function (e) {
            return this.pushStack(bG.map(this, function (b0, bZ) {
                return e.call(b0, bZ, b0)
            }))
        },
        end: function () {
            return this.prevObject || this.constructor(null)
        },
        push: am,
        sort: [].sort,
        splice: [].splice
    };
    bG.fn.init.prototype = bG.fn;
    bG.extend = bG.fn.extend = function () {
        var b7, b0, e, bZ, b4, b5, b3 = arguments[0] || {}, b2 = 1,
            b1 = arguments.length,
            b6 = false;
        if (typeof b3 === "boolean") {
            b6 = b3;
            b3 = arguments[1] || {};
            b2 = 2
        }
        if (typeof b3 !== "object" && !bG.isFunction(b3)) {
            b3 = {}
        }
        if (b1 === b2) {
            b3 = this;
            --b2
        }
        for (; b2 < b1; b2++) {
            if ((b7 = arguments[b2]) != null) {
                for (b0 in b7) {
                    e = b3[b0];
                    bZ = b7[b0];
                    if (b3 === bZ) {
                        continue
                    }
                    if (b6 && bZ && (bG.isPlainObject(bZ) || (b4 = bG.isArray(bZ)))) {
                        if (b4) {
                            b4 = false;
                            b5 = e && bG.isArray(e) ? e : []
                        } else {
                            b5 = e && bG.isPlainObject(e) ? e : {}
                        }
                        b3[b0] = bG.extend(b6, b5, bZ)
                    } else {
                        if (bZ !== aB) {
                            b3[b0] = bZ
                        }
                    }
                }
            }
        }
        return b3
    };
    bG.extend({
        noConflict: function (e) {
            if (a2.$ === bG) {
                a2.$ = I
            }
            if (e && a2.jQuery === bG) {
                a2.jQuery = bg
            }
            return bG
        },
        isReady: false,
        readyWait: 1,
        holdReady: function (e) {
            if (e) {
                bG.readyWait++
            } else {
                bG.ready(true)
            }
        },
        ready: function (e) {
            if (e === true ? --bG.readyWait : bG.isReady) {
                return
            }
            if (!o.body) {
                return setTimeout(bG.ready, 1)
            }
            bG.isReady = true;
            if (e !== true && --bG.readyWait > 0) {
                return
            }
            af.resolveWith(o, [bG]);
            if (bG.fn.trigger) {
                bG(o).trigger("ready").off("ready")
            }
        },
        isFunction: function (e) {
            return bG.type(e) === "function"
        },
        isArray: Array.isArray || function (e) {
            return bG.type(e) === "array"
        },
        isWindow: function (e) {
            return e != null && e == e.window
        },
        isNumeric: function (e) {
            return !isNaN(parseFloat(e)) && isFinite(e)
        },
        type: function (e) {
            return e == null ? String(e) : Z[z.call(e)] || "object"
        },
        isPlainObject: function (b1) {
            if (!b1 || bG.type(b1) !== "object" || b1.nodeType || bG.isWindow(b1)) {
                return false
            }
            try {
                if (b1.constructor && !V.call(b1, "constructor") && !V.call(b1.constructor.prototype, "isPrototypeOf")) {
                    return false
                }
            } catch (b0) {
                return false
            }
            var bZ;
            for (bZ in b1) {}
            return bZ === aB || V.call(b1, bZ)
        },
        isEmptyObject: function (bZ) {
            var e;
            for (e in bZ) {
                return false
            }
            return true
        },
        error: function (e) {
            throw new Error(e)
        },
        parseHTML: function (b1, b0, e) {
            var bZ;
            if (!b1 || typeof b1 !== "string") {
                return null
            }
            if (typeof b0 === "boolean") {
                e = b0;
                b0 = 0
            }
            b0 = b0 || o;
            if ((bZ = a.exec(b1))) {
                return [b0.createElement(bZ[1])]
            }
            bZ = bG.buildFragment([b1], b0, e ? null : []);
            return bG.merge([], (bZ.cacheable ? bG.clone(bZ.fragment) : bZ.fragment).childNodes)
        },
        parseJSON: function (e) {
            if (!e || typeof e !== "string") {
                return null
            }
            e = bG.trim(e);
            if (a2.JSON && a2.JSON.parse) {
                return a2.JSON.parse(e)
            }
            if (bf.test(e.replace(bD, "@").replace(a0, "]").replace(bi, ""))) {
                return (new Function("return " + e))()
            }
            bG.error("Invalid JSON: " + e)
        },
        parseXML: function (b1) {
            var bZ, b0;
            if (!b1 || typeof b1 !== "string") {
                return null
            }
            try {
                if (a2.DOMParser) {
                    b0 = new DOMParser();
                    bZ = b0.parseFromString(b1, "text/xml")
                } else {
                    bZ = new ActiveXObject("Microsoft.XMLDOM");
                    bZ.async = "false";
                    bZ.loadXML(b1)
                }
            } catch (b2) {
                bZ = aB
            }
            if (!bZ || !bZ.documentElement || bZ.getElementsByTagName("parsererror").length) {
                bG.error("Invalid XML: " + b1)
            }
            return bZ
        },
        noop: function () {},
        globalEval: function (e) {
            if (e && aa.test(e)) {
                (a2.execScript || function (bZ) {
                    a2["eval"].call(a2, bZ)
                })(e)
            }
        },
        camelCase: function (e) {
            return e.replace(bP, "ms-").replace(aU, N)
        },
        nodeName: function (bZ, e) {
            return bZ.nodeName && bZ.nodeName.toLowerCase() === e.toLowerCase()
        },
        each: function (b3, b4, b0) {
            var bZ, b1 = 0,
                b2 = b3.length,
                e = b2 === aB || bG.isFunction(b3);
            if (b0) {
                if (e) {
                    for (bZ in b3) {
                        if (b4.apply(b3[bZ], b0) === false) {
                            break
                        }
                    }
                } else {
                    for (; b1 < b2;) {
                        if (b4.apply(b3[b1++], b0) === false) {
                            break
                        }
                    }
                }
            } else {
                if (e) {
                    for (bZ in b3) {
                        if (b4.call(b3[bZ], bZ, b3[bZ]) === false) {
                            break
                        }
                    }
                } else {
                    for (; b1 < b2;) {
                        if (b4.call(b3[b1], b1, b3[b1++]) === false) {
                            break
                        }
                    }
                }
            }
            return b3
        },
        trim: aO && !aO.call("\uFEFF\xA0") ? function (e) {
            return e == null ? "" : aO.call(e)
        } : function (e) {
            return e == null ? "" : (e + "").replace(C, "")
        },
        makeArray: function (e, b0) {
            var b1, bZ = b0 || [];
            if (e != null) {
                b1 = bG.type(e);
                if (e.length == null || b1 === "string" || b1 === "function" || b1 === "regexp" || bG.isWindow(e)) {
                    am.call(bZ, e)
                } else {
                    bG.merge(bZ, e)
                }
            }
            return bZ
        },
        inArray: function (b1, bZ, b0) {
            var e;
            if (bZ) {
                if (aK) {
                    return aK.call(bZ, b1, b0)
                }
                e = bZ.length;
                b0 = b0 ? b0 < 0 ? Math.max(0, e + b0) : b0 : 0;
                for (; b0 < e; b0++) {
                    if (b0 in bZ && bZ[b0] === b1) {
                        return b0
                    }
                }
            }
            return -1
        },
        merge: function (b2, b0) {
            var e = b0.length,
                b1 = b2.length,
                bZ = 0;
            if (typeof e === "number") {
                for (; bZ < e; bZ++) {
                    b2[b1++] = b0[bZ]
                }
            } else {
                while (b0[bZ] !== aB) {
                    b2[b1++] = b0[bZ++]
                }
            }
            b2.length = b1;
            return b2
        },
        grep: function (bZ, b4, e) {
            var b3, b0 = [],
                b1 = 0,
                b2 = bZ.length;
            e = !! e;
            for (; b1 < b2; b1++) {
                b3 = !! b4(bZ[b1], b1);
                if (e !== b3) {
                    b0.push(bZ[b1])
                }
            }
            return b0
        },
        map: function (e, b5, b6) {
            var b3, b4, b2 = [],
                b0 = 0,
                bZ = e.length,
                b1 = e instanceof bG || bZ !== aB && typeof bZ === "number" && ((bZ > 0 && e[0] && e[bZ - 1]) || bZ === 0 || bG.isArray(e));
            if (b1) {
                for (; b0 < bZ; b0++) {
                    b3 = b5(e[b0], b0, b6);
                    if (b3 != null) {
                        b2[b2.length] = b3
                    }
                }
            } else {
                for (b4 in e) {
                    b3 = b5(e[b4], b4, b6);
                    if (b3 != null) {
                        b2[b2.length] = b3
                    }
                }
            }
            return b2.concat.apply([], b2)
        },
        guid: 1,
        proxy: function (b2, b1) {
            var b0, e, bZ;
            if (typeof b1 === "string") {
                b0 = b2[b1];
                b1 = b2;
                b2 = b0
            }
            if (!bG.isFunction(b2)) {
                return aB
            }
            e = a4.call(arguments, 2);
            bZ = function () {
                return b2.apply(b1, e.concat(a4.call(arguments)))
            };
            bZ.guid = b2.guid = b2.guid || bG.guid++;
            return bZ
        },
        access: function (e, b4, b7, b5, b2, b8, b6) {
            var b0, b3 = b7 == null,
                b1 = 0,
                bZ = e.length;
            if (b7 && typeof b7 === "object") {
                for (b1 in b7) {
                    bG.access(e, b4, b1, b7[b1], 1, b8, b5)
                }
                b2 = 1
            } else {
                if (b5 !== aB) {
                    b0 = b6 === aB && bG.isFunction(b5);
                    if (b3) {
                        if (b0) {
                            b0 = b4;
                            b4 = function (ca, b9, cb) {
                                return b0.call(bG(ca), cb)
                            }
                        } else {
                            b4.call(e, b5);
                            b4 = null
                        }
                    }
                    if (b4) {
                        for (; b1 < bZ; b1++) {
                            b4(e[b1], b7, b0 ? b5.call(e[b1], b1, b4(e[b1], b7)) : b5, b6)
                        }
                    }
                    b2 = 1
                }
            }
            return b2 ? e : b3 ? b4.call(e) : bZ ? b4(e[0], b7) : b8
        },
        now: function () {
            return (new Date()).getTime()
        }
    });
    bG.ready.promise = function (b2) {
        if (!af) {
            af = bG.Deferred();
            if (o.readyState === "complete") {
                setTimeout(bG.ready, 1)
            } else {
                if (o.addEventListener) {
                    o.addEventListener("DOMContentLoaded", aF, false);
                    a2.addEventListener("load", bG.ready, false)
                } else {
                    o.attachEvent("onreadystatechange", aF);
                    a2.attachEvent("onload", bG.ready);
                    var b1 = false;
                    try {
                        b1 = a2.frameElement == null && o.documentElement
                    } catch (b0) {}
                    if (b1 && b1.doScroll) {
                        (function bZ() {
                            if (!bG.isReady) {
                                try {
                                    b1.doScroll("left")
                                } catch (b3) {
                                    return setTimeout(bZ, 50)
                                }
                                bG.ready()
                            }
                        })()
                    }
                }
            }
        }
        return af.promise(b2)
    };
    bG.each("Boolean Number String Function Array Date RegExp Object".split(" "), function (bZ, e) {
        Z["[object " + e + "]"] = e.toLowerCase()
    });
    w = bG(o);
    var bU = {};

    function ac(bZ) {
        var e = bU[bZ] = {};
        bG.each(bZ.split(aV), function (b1, b0) {
            e[b0] = true
        });
        return e
    }
    bG.Callbacks = function (b8) {
        b8 = typeof b8 === "string" ? (bU[b8] || ac(b8)) : bG.extend({}, b8);
        var b1, e, b2, b0, b3, b4, b5 = [],
            b6 = !b8.once && [],
            bZ = function (b9) {
                b1 = b8.memory && b9;
                e = true;
                b4 = b0 || 0;
                b0 = 0;
                b3 = b5.length;
                b2 = true;
                for (; b5 && b4 < b3; b4++) {
                    if (b5[b4].apply(b9[0], b9[1]) === false && b8.stopOnFalse) {
                        b1 = false;
                        break
                    }
                }
                b2 = false;
                if (b5) {
                    if (b6) {
                        if (b6.length) {
                            bZ(b6.shift())
                        }
                    } else {
                        if (b1) {
                            b5 = []
                        } else {
                            b7.disable()
                        }
                    }
                }
            }, b7 = {
                add: function () {
                    if (b5) {
                        var ca = b5.length;
                        (function b9(cb) {
                            bG.each(cb, function (cd, cc) {
                                var ce = bG.type(cc);
                                if (ce === "function") {
                                    if (!b8.unique || !b7.has(cc)) {
                                        b5.push(cc)
                                    }
                                } else {
                                    if (cc && cc.length && ce !== "string") {
                                        b9(cc)
                                    }
                                }
                            })
                        })(arguments);
                        if (b2) {
                            b3 = b5.length
                        } else {
                            if (b1) {
                                b0 = ca;
                                bZ(b1)
                            }
                        }
                    }
                    return this
                },
                remove: function () {
                    if (b5) {
                        bG.each(arguments, function (cb, b9) {
                            var ca;
                            while ((ca = bG.inArray(b9, b5, ca)) > -1) {
                                b5.splice(ca, 1);
                                if (b2) {
                                    if (ca <= b3) {
                                        b3--
                                    }
                                    if (ca <= b4) {
                                        b4--
                                    }
                                }
                            }
                        })
                    }
                    return this
                },
                has: function (b9) {
                    return bG.inArray(b9, b5) > -1
                },
                empty: function () {
                    b5 = [];
                    return this
                },
                disable: function () {
                    b5 = b6 = b1 = aB;
                    return this
                },
                disabled: function () {
                    return !b5
                },
                lock: function () {
                    b6 = aB;
                    if (!b1) {
                        b7.disable()
                    }
                    return this
                },
                locked: function () {
                    return !b6
                },
                fireWith: function (ca, b9) {
                    b9 = b9 || [];
                    b9 = [ca, b9.slice ? b9.slice() : b9];
                    if (b5 && (!e || b6)) {
                        if (b2) {
                            b6.push(b9)
                        } else {
                            bZ(b9)
                        }
                    }
                    return this
                },
                fire: function () {
                    b7.fireWith(this, arguments);
                    return this
                },
                fired: function () {
                    return !!e
                }
            };
        return b7
    };
    bG.extend({
        Deferred: function (b0) {
            var bZ = [
                ["resolve", "done", bG.Callbacks("once memory"), "resolved"],
                ["reject", "fail", bG.Callbacks("once memory"), "rejected"],
                ["notify", "progress", bG.Callbacks("memory")]
            ],
                b1 = "pending",
                b2 = {
                    state: function () {
                        return b1
                    },
                    always: function () {
                        e.done(arguments).fail(arguments);
                        return this
                    },
                    then: function () {
                        var b3 = arguments;
                        return bG.Deferred(function (b4) {
                            bG.each(bZ, function (b6, b5) {
                                var b8 = b5[0],
                                    b7 = b3[b6];
                                e[b5[1]](bG.isFunction(b7) ? function () {
                                    var b9 = b7.apply(this, arguments);
                                    if (b9 && bG.isFunction(b9.promise)) {
                                        b9.promise().done(b4.resolve).fail(b4.reject).progress(b4.notify)
                                    } else {
                                        b4[b8 + "With"](this === e ? b4 : this, [b9])
                                    }
                                } : b4[b8])
                            });
                            b3 = null
                        }).promise()
                    },
                    promise: function (b3) {
                        return b3 != null ? bG.extend(b3, b2) : b2
                    }
                }, e = {};
            b2.pipe = b2.then;
            bG.each(bZ, function (b4, b3) {
                var b6 = b3[2],
                    b5 = b3[3];
                b2[b3[1]] = b6.add;
                if (b5) {
                    b6.add(function () {
                        b1 = b5
                    }, bZ[b4 ^ 1][2].disable, bZ[2][2].lock)
                }
                e[b3[0]] = b6.fire;
                e[b3[0] + "With"] = b6.fireWith
            });
            b2.promise(e);
            if (b0) {
                b0.call(e, e)
            }
            return e
        },
        when: function (b2) {
            var b0 = 0,
                b4 = a4.call(arguments),
                e = b4.length,
                bZ = e !== 1 || (b2 && bG.isFunction(b2.promise)) ? e : 0,
                b7 = bZ === 1 ? b2 : bG.Deferred(),
                b1 = function (b9, ca, b8) {
                    return function (cb) {
                        ca[b9] = this;
                        b8[b9] = arguments.length > 1 ? a4.call(arguments) : cb;
                        if (b8 === b6) {
                            b7.notifyWith(ca, b8)
                        } else {
                            if (!(--bZ)) {
                                b7.resolveWith(ca, b8)
                            }
                        }
                    }
                }, b6, b3, b5;
            if (e > 1) {
                b6 = new Array(e);
                b3 = new Array(e);
                b5 = new Array(e);
                for (; b0 < e; b0++) {
                    if (b4[b0] && bG.isFunction(b4[b0].promise)) {
                        b4[b0].promise().done(b1(b0, b5, b4)).fail(b7.reject).progress(b1(b0, b3, b6))
                    } else {
                        --bZ
                    }
                }
            }
            if (!bZ) {
                b7.resolveWith(b5, b4)
            }
            return b7.promise()
        }
    });
    bG.support = (function () {
        var cb, ca, b8, b9, b2, b7, b6, b4, b3, b1, bZ, b0 = o.createElement("div");
        b0.setAttribute("className", "t");
        b0.innerHTML = "  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>";
        ca = b0.getElementsByTagName("*");
        b8 = b0.getElementsByTagName("a")[0];
        if (!ca || !b8 || !ca.length) {
            return {}
        }
        b9 = o.createElement("select");
        b2 = b9.appendChild(o.createElement("option"));
        b7 = b0.getElementsByTagName("input")[0];
        b8.style.cssText = "top:1px;float:left;opacity:.5";
        cb = {
            leadingWhitespace: (b0.firstChild.nodeType === 3),
            tbody: !b0.getElementsByTagName("tbody").length,
            htmlSerialize: !! b0.getElementsByTagName("link").length,
            style: /top/.test(b8.getAttribute("style")),
            hrefNormalized: (b8.getAttribute("href") === "/a"),
            opacity: /^0.5/.test(b8.style.opacity),
            cssFloat: !! b8.style.cssFloat,
            checkOn: (b7.value === "on"),
            optSelected: b2.selected,
            getSetAttribute: b0.className !== "t",
            enctype: !! o.createElement("form").enctype,
            html5Clone: o.createElement("nav").cloneNode(true).outerHTML !== "<:nav></:nav>",
            boxModel: (o.compatMode === "CSS1Compat"),
            submitBubbles: true,
            changeBubbles: true,
            focusinBubbles: false,
            deleteExpando: true,
            noCloneEvent: true,
            inlineBlockNeedsLayout: false,
            shrinkWrapBlocks: false,
            reliableMarginRight: true,
            boxSizingReliable: true,
            pixelPosition: false
        };
        b7.checked = true;
        cb.noCloneChecked = b7.cloneNode(true).checked;
        b9.disabled = true;
        cb.optDisabled = !b2.disabled;
        try {
            delete b0.test
        } catch (b5) {
            cb.deleteExpando = false
        }
        if (!b0.addEventListener && b0.attachEvent && b0.fireEvent) {
            b0.attachEvent("onclick", bZ = function () {
                cb.noCloneEvent = false
            });
            b0.cloneNode(true).fireEvent("onclick");
            b0.detachEvent("onclick", bZ)
        }
        b7 = o.createElement("input");
        b7.value = "t";
        b7.setAttribute("type", "radio");
        cb.radioValue = b7.value === "t";
        b7.setAttribute("checked", "checked");
        b7.setAttribute("name", "t");
        b0.appendChild(b7);
        b6 = o.createDocumentFragment();
        b6.appendChild(b0.lastChild);
        cb.checkClone = b6.cloneNode(true).cloneNode(true).lastChild.checked;
        cb.appendChecked = b7.checked;
        b6.removeChild(b7);
        b6.appendChild(b0);
        if (b0.attachEvent) {
            for (b3 in {
                submit: true,
                change: true,
                focusin: true
            }) {
                b4 = "on" + b3;
                b1 = (b4 in b0);
                if (!b1) {
                    b0.setAttribute(b4, "return;");
                    b1 = (typeof b0[b4] === "function")
                }
                cb[b3 + "Bubbles"] = b1
            }
        }
        bG(function () {
            var cc, cg, ce, cf, cd = "padding:0;margin:0;border:0;display:block;overflow:hidden;",
                e = o.getElementsByTagName("body")[0];
            if (!e) {
                return
            }
            cc = o.createElement("div");
            cc.style.cssText = "visibility:hidden;border:0;width:0;height:0;position:static;top:0;margin-top:1px";
            e.insertBefore(cc, e.firstChild);
            cg = o.createElement("div");
            cc.appendChild(cg);
            cg.innerHTML = "<table><tr><td></td><td>t</td></tr></table>";
            ce = cg.getElementsByTagName("td");
            ce[0].style.cssText = "padding:0;margin:0;border:0;display:none";
            b1 = (ce[0].offsetHeight === 0);
            ce[0].style.display = "";
            ce[1].style.display = "none";
            cb.reliableHiddenOffsets = b1 && (ce[0].offsetHeight === 0);
            cg.innerHTML = "";
            cg.style.cssText = "box-sizing:border-box;-moz-box-sizing:border-box;-webkit-box-sizing:border-box;padding:1px;border:1px;display:block;width:4px;margin-top:1%;position:absolute;top:1%;";
            cb.boxSizing = (cg.offsetWidth === 4);
            cb.doesNotIncludeMarginInBodyOffset = (e.offsetTop !== 1);
            if (a2.getComputedStyle) {
                cb.pixelPosition = (a2.getComputedStyle(cg, null) || {}).top !== "1%";
                cb.boxSizingReliable = (a2.getComputedStyle(cg, null) || {
                    width: "4px"
                }).width === "4px";
                cf = o.createElement("div");
                cf.style.cssText = cg.style.cssText = cd;
                cf.style.marginRight = cf.style.width = "0";
                cg.style.width = "1px";
                cg.appendChild(cf);
                cb.reliableMarginRight = !parseFloat((a2.getComputedStyle(cf, null) || {}).marginRight)
            }
            if (typeof cg.style.zoom !== "undefined") {
                cg.innerHTML = "";
                cg.style.cssText = cd + "width:1px;padding:1px;display:inline;zoom:1";
                cb.inlineBlockNeedsLayout = (cg.offsetWidth === 3);
                cg.style.display = "block";
                cg.style.overflow = "visible";
                cg.innerHTML = "<div></div>";
                cg.firstChild.style.width = "5px";
                cb.shrinkWrapBlocks = (cg.offsetWidth !== 3);
                cc.style.zoom = 1
            }
            e.removeChild(cc);
            cc = cg = ce = cf = null
        });
        b6.removeChild(b0);
        ca = b8 = b9 = b2 = b7 = b6 = b0 = null;
        return cb
    })();
    var bt = /(?:\{[\s\S]*\}|\[[\s\S]*\])$/,
        aL = /([A-Z])/g;
    bG.extend({
        cache: {},
        deletedIds: [],
        uuid: 0,
        expando: "jQuery" + (bG.fn.jquery + Math.random()).replace(/\D/g, ""),
        noData: {
            embed: true,
            object: "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000",
            applet: true
        },
        hasData: function (e) {
            e = e.nodeType ? bG.cache[e[bG.expando]] : e[bG.expando];
            return !!e && !O(e)
        },
        data: function (b1, bZ, b3, b2) {
            if (!bG.acceptData(b1)) {
                return
            }
            var b4, b6, b7 = bG.expando,
                b5 = typeof bZ === "string",
                b8 = b1.nodeType,
                e = b8 ? bG.cache : b1,
                b0 = b8 ? b1[b7] : b1[b7] && b7;
            if ((!b0 || !e[b0] || (!b2 && !e[b0].data)) && b5 && b3 === aB) {
                return
            }
            if (!b0) {
                if (b8) {
                    b1[b7] = b0 = bG.deletedIds.pop() || bG.guid++
                } else {
                    b0 = b7
                }
            }
            if (!e[b0]) {
                e[b0] = {};
                if (!b8) {
                    e[b0].toJSON = bG.noop
                }
            }
            if (typeof bZ === "object" || typeof bZ === "function") {
                if (b2) {
                    e[b0] = bG.extend(e[b0], bZ)
                } else {
                    e[b0].data = bG.extend(e[b0].data, bZ)
                }
            }
            b4 = e[b0];
            if (!b2) {
                if (!b4.data) {
                    b4.data = {}
                }
                b4 = b4.data
            }
            if (b3 !== aB) {
                b4[bG.camelCase(bZ)] = b3
            }
            if (b5) {
                b6 = b4[bZ];
                if (b6 == null) {
                    b6 = b4[bG.camelCase(bZ)]
                }
            } else {
                b6 = b4
            }
            return b6
        },
        removeData: function (b1, bZ, b2) {
            if (!bG.acceptData(b1)) {
                return
            }
            var b5, b4, b3, b6 = b1.nodeType,
                e = b6 ? bG.cache : b1,
                b0 = b6 ? b1[bG.expando] : bG.expando;
            if (!e[b0]) {
                return
            }
            if (bZ) {
                b5 = b2 ? e[b0] : e[b0].data;
                if (b5) {
                    if (!bG.isArray(bZ)) {
                        if (bZ in b5) {
                            bZ = [bZ]
                        } else {
                            bZ = bG.camelCase(bZ);
                            if (bZ in b5) {
                                bZ = [bZ]
                            } else {
                                bZ = bZ.split(" ")
                            }
                        }
                    }
                    for (b4 = 0, b3 = bZ.length; b4 < b3; b4++) {
                        delete b5[bZ[b4]]
                    }
                    if (!(b2 ? O : bG.isEmptyObject)(b5)) {
                        return
                    }
                }
            }
            if (!b2) {
                delete e[b0].data;
                if (!O(e[b0])) {
                    return
                }
            }
            if (b6) {
                bG.cleanData([b1], true)
            } else {
                if (bG.support.deleteExpando || e != e.window) {
                    delete e[b0]
                } else {
                    e[b0] = null
                }
            }
        },
        _data: function (bZ, e, b0) {
            return bG.data(bZ, e, b0, true)
        },
        acceptData: function (bZ) {
            var e = bZ.nodeName && bG.noData[bZ.nodeName.toLowerCase()];
            return !e || e !== true && bZ.getAttribute("classid") === e
        }
    });
    bG.fn.extend({
        data: function (b7, b6) {
            var b2, bZ, b5, e, b1, b0 = this[0],
                b4 = 0,
                b3 = null;
            if (b7 === aB) {
                if (this.length) {
                    b3 = bG.data(b0);
                    if (b0.nodeType === 1 && !bG._data(b0, "parsedAttrs")) {
                        b5 = b0.attributes;
                        for (b1 = b5.length; b4 < b1; b4++) {
                            e = b5[b4].name;
                            if (!e.indexOf("data-")) {
                                e = bG.camelCase(e.substring(5));
                                bv(b0, e, b3[e])
                            }
                        }
                        bG._data(b0, "parsedAttrs", true)
                    }
                }
                return b3
            }
            if (typeof b7 === "object") {
                return this.each(function () {
                    bG.data(this, b7)
                })
            }
            b2 = b7.split(".", 2);
            b2[1] = b2[1] ? "." + b2[1] : "";
            bZ = b2[1] + "!";
            return bG.access(this, function (b8) {
                if (b8 === aB) {
                    b3 = this.triggerHandler("getData" + bZ, [b2[0]]);
                    if (b3 === aB && b0) {
                        b3 = bG.data(b0, b7);
                        b3 = bv(b0, b7, b3)
                    }
                    return b3 === aB && b2[1] ? this.data(b2[0]) : b3
                }
                b2[1] = b8;
                this.each(function () {
                    var b9 = bG(this);
                    b9.triggerHandler("setData" + bZ, b2);
                    bG.data(this, b7, b8);
                    b9.triggerHandler("changeData" + bZ, b2)
                })
            }, null, b6, arguments.length > 1, null, false)
        },
        removeData: function (e) {
            return this.each(function () {
                bG.removeData(this, e)
            })
        }
    });

    function bv(b1, b0, b2) {
        if (b2 === aB && b1.nodeType === 1) {
            var bZ = "data-" + b0.replace(aL, "-$1").toLowerCase();
            b2 = b1.getAttribute(bZ);
            if (typeof b2 === "string") {
                try {
                    b2 = b2 === "true" ? true : b2 === "false" ? false : b2 === "null" ? null : +b2 + "" === b2 ? +b2 : bt.test(b2) ? bG.parseJSON(b2) : b2
                } catch (b3) {}
                bG.data(b1, b0, b2)
            } else {
                b2 = aB
            }
        }
        return b2
    }

    function O(bZ) {
        var e;
        for (e in bZ) {
            if (e === "data" && bG.isEmptyObject(bZ[e])) {
                continue
            }
            if (e !== "toJSON") {
                return false
            }
        }
        return true
    }
    bG.extend({
        queue: function (b0, bZ, b1) {
            var e;
            if (b0) {
                bZ = (bZ || "fx") + "queue";
                e = bG._data(b0, bZ);
                if (b1) {
                    if (!e || bG.isArray(b1)) {
                        e = bG._data(b0, bZ, bG.makeArray(b1))
                    } else {
                        e.push(b1)
                    }
                }
                return e || []
            }
        },
        dequeue: function (b3, b2) {
            b2 = b2 || "fx";
            var bZ = bG.queue(b3, b2),
                b4 = bZ.length,
                b1 = bZ.shift(),
                e = bG._queueHooks(b3, b2),
                b0 = function () {
                    bG.dequeue(b3, b2)
                };
            if (b1 === "inprogress") {
                b1 = bZ.shift();
                b4--
            }
            if (b1) {
                if (b2 === "fx") {
                    bZ.unshift("inprogress")
                }
                delete e.stop;
                b1.call(b3, b0, e)
            }
            if (!b4 && e) {
                e.empty.fire()
            }
        },
        _queueHooks: function (b0, bZ) {
            var e = bZ + "queueHooks";
            return bG._data(b0, e) || bG._data(b0, e, {
                empty: bG.Callbacks("once memory").add(function () {
                    bG.removeData(b0, bZ + "queue", true);
                    bG.removeData(b0, e, true)
                })
            })
        }
    });
    bG.fn.extend({
        queue: function (e, bZ) {
            var b0 = 2;
            if (typeof e !== "string") {
                bZ = e;
                e = "fx";
                b0--
            }
            if (arguments.length < b0) {
                return bG.queue(this[0], e)
            }
            return bZ === aB ? this : this.each(function () {
                var b1 = bG.queue(this, e, bZ);
                bG._queueHooks(this, e);
                if (e === "fx" && b1[0] !== "inprogress") {
                    bG.dequeue(this, e)
                }
            })
        },
        dequeue: function (e) {
            return this.each(function () {
                bG.dequeue(this, e)
            })
        },
        delay: function (bZ, e) {
            bZ = bG.fx ? bG.fx.speeds[bZ] || bZ : bZ;
            e = e || "fx";
            return this.queue(e, function (b1, b0) {
                var b2 = setTimeout(b1, bZ);
                b0.stop = function () {
                    clearTimeout(b2)
                }
            })
        },
        clearQueue: function (e) {
            return this.queue(e || "fx", [])
        },
        promise: function (b0, b4) {
            var bZ, b1 = 1,
                b5 = bG.Deferred(),
                b3 = this,
                e = this.length,
                b2 = function () {
                    if (!(--b1)) {
                        b5.resolveWith(b3, [b3])
                    }
                };
            if (typeof b0 !== "string") {
                b4 = b0;
                b0 = aB
            }
            b0 = b0 || "fx";
            while (e--) {
                bZ = bG._data(b3[e], b0 + "queueHooks");
                if (bZ && bZ.empty) {
                    b1++;
                    bZ.empty.add(b2)
                }
            }
            b2();
            return b5.promise(b4)
        }
    });
    var a7, bV, n, bJ = /[\t\r\n]/g,
        ai = /\r/g,
        j = /^(?:button|input)$/i,
        aA = /^(?:button|input|object|select|textarea)$/i,
        D = /^a(?:rea|)$/i,
        M = /^(?:autofocus|autoplay|async|checked|controls|defer|disabled|hidden|loop|multiple|open|readonly|required|scoped|selected)$/i,
        bL = bG.support.getSetAttribute;
    bG.fn.extend({
        attr: function (e, bZ) {
            return bG.access(this, bG.attr, e, bZ, arguments.length > 1)
        },
        removeAttr: function (e) {
            return this.each(function () {
                bG.removeAttr(this, e)
            })
        },
        prop: function (e, bZ) {
            return bG.access(this, bG.prop, e, bZ, arguments.length > 1)
        },
        removeProp: function (e) {
            e = bG.propFix[e] || e;
            return this.each(function () {
                try {
                    this[e] = aB;
                    delete this[e]
                } catch (bZ) {}
            })
        },
        addClass: function (b2) {
            var b4, b0, bZ, b1, b3, b5, e;
            if (bG.isFunction(b2)) {
                return this.each(function (b6) {
                    bG(this).addClass(b2.call(this, b6, this.className))
                })
            }
            if (b2 && typeof b2 === "string") {
                b4 = b2.split(aV);
                for (b0 = 0, bZ = this.length; b0 < bZ; b0++) {
                    b1 = this[b0];
                    if (b1.nodeType === 1) {
                        if (!b1.className && b4.length === 1) {
                            b1.className = b2
                        } else {
                            b3 = " " + b1.className + " ";
                            for (b5 = 0, e = b4.length; b5 < e; b5++) {
                                if (b3.indexOf(" " + b4[b5] + " ") < 0) {
                                    b3 += b4[b5] + " "
                                }
                            }
                            b1.className = bG.trim(b3)
                        }
                    }
                }
            }
            return this
        },
        removeClass: function (b4) {
            var b1, b2, b3, b5, bZ, b0, e;
            if (bG.isFunction(b4)) {
                return this.each(function (b6) {
                    bG(this).removeClass(b4.call(this, b6, this.className))
                })
            }
            if ((b4 && typeof b4 === "string") || b4 === aB) {
                b1 = (b4 || "").split(aV);
                for (b0 = 0, e = this.length; b0 < e; b0++) {
                    b3 = this[b0];
                    if (b3.nodeType === 1 && b3.className) {
                        b2 = (" " + b3.className + " ").replace(bJ, " ");
                        for (b5 = 0, bZ = b1.length; b5 < bZ; b5++) {
                            while (b2.indexOf(" " + b1[b5] + " ") >= 0) {
                                b2 = b2.replace(" " + b1[b5] + " ", " ")
                            }
                        }
                        b3.className = b4 ? bG.trim(b2) : ""
                    }
                }
            }
            return this
        },
        toggleClass: function (b1, bZ) {
            var b0 = typeof b1,
                e = typeof bZ === "boolean";
            if (bG.isFunction(b1)) {
                return this.each(function (b2) {
                    bG(this).toggleClass(b1.call(this, b2, this.className, bZ), bZ)
                })
            }
            return this.each(function () {
                if (b0 === "string") {
                    var b4, b3 = 0,
                        b2 = bG(this),
                        b5 = bZ,
                        b6 = b1.split(aV);
                    while ((b4 = b6[b3++])) {
                        b5 = e ? b5 : !b2.hasClass(b4);
                        b2[b5 ? "addClass" : "removeClass"](b4)
                    }
                } else {
                    if (b0 === "undefined" || b0 === "boolean") {
                        if (this.className) {
                            bG._data(this, "__className__", this.className)
                        }
                        this.className = this.className || b1 === false ? "" : bG._data(this, "__className__") || ""
                    }
                }
            })
        },
        hasClass: function (e) {
            var b1 = " " + e + " ",
                b0 = 0,
                bZ = this.length;
            for (; b0 < bZ; b0++) {
                if (this[b0].nodeType === 1 && (" " + this[b0].className + " ").replace(bJ, " ").indexOf(b1) >= 0) {
                    return true
                }
            }
            return false
        },
        val: function (b1) {
            var e, bZ, b2, b0 = this[0];
            if (!arguments.length) {
                if (b0) {
                    e = bG.valHooks[b0.type] || bG.valHooks[b0.nodeName.toLowerCase()];
                    if (e && "get" in e && (bZ = e.get(b0, "value")) !== aB) {
                        return bZ
                    }
                    bZ = b0.value;
                    return typeof bZ === "string" ? bZ.replace(ai, "") : bZ == null ? "" : bZ
                }
                return
            }
            b2 = bG.isFunction(b1);
            return this.each(function (b4) {
                var b5, b3 = bG(this);
                if (this.nodeType !== 1) {
                    return
                }
                if (b2) {
                    b5 = b1.call(this, b4, b3.val())
                } else {
                    b5 = b1
                } if (b5 == null) {
                    b5 = ""
                } else {
                    if (typeof b5 === "number") {
                        b5 += ""
                    } else {
                        if (bG.isArray(b5)) {
                            b5 = bG.map(b5, function (b6) {
                                return b6 == null ? "" : b6 + ""
                            })
                        }
                    }
                }
                e = bG.valHooks[this.type] || bG.valHooks[this.nodeName.toLowerCase()];
                if (!e || !("set" in e) || e.set(this, b5, "value") === aB) {
                    this.value = b5
                }
            })
        }
    });
    bG.extend({
        valHooks: {
            option: {
                get: function (e) {
                    var bZ = e.attributes.value;
                    return !bZ || bZ.specified ? e.value : e.text
                }
            },
            select: {
                get: function (e) {
                    var b4, b0, b6 = e.options,
                        b2 = e.selectedIndex,
                        b1 = e.type === "select-one" || b2 < 0,
                        b5 = b1 ? null : [],
                        b3 = b1 ? b2 + 1 : b6.length,
                        bZ = b2 < 0 ? b3 : b1 ? b2 : 0;
                    for (; bZ < b3; bZ++) {
                        b0 = b6[bZ];
                        if ((b0.selected || bZ === b2) && (bG.support.optDisabled ? !b0.disabled : b0.getAttribute("disabled") === null) && (!b0.parentNode.disabled || !bG.nodeName(b0.parentNode, "optgroup"))) {
                            b4 = bG(b0).val();
                            if (b1) {
                                return b4
                            }
                            b5.push(b4)
                        }
                    }
                    return b5
                },
                set: function (bZ, b0) {
                    var e = bG.makeArray(b0);
                    bG(bZ).find("option").each(function () {
                        this.selected = bG.inArray(bG(this).val(), e) >= 0
                    });
                    if (!e.length) {
                        bZ.selectedIndex = -1
                    }
                    return e
                }
            }
        },
        attrFn: {},
        attr: function (b4, b1, b5, b3) {
            var b0, e, b2, bZ = b4.nodeType;
            if (!b4 || bZ === 3 || bZ === 8 || bZ === 2) {
                return
            }
            if (b3 && bG.isFunction(bG.fn[b1])) {
                return bG(b4)[b1](b5)
            }
            if (typeof b4.getAttribute === "undefined") {
                return bG.prop(b4, b1, b5)
            }
            b2 = bZ !== 1 || !bG.isXMLDoc(b4);
            if (b2) {
                b1 = b1.toLowerCase();
                e = bG.attrHooks[b1] || (M.test(b1) ? bV : a7)
            }
            if (b5 !== aB) {
                if (b5 === null) {
                    bG.removeAttr(b4, b1);
                    return
                } else {
                    if (e && "set" in e && b2 && (b0 = e.set(b4, b5, b1)) !== aB) {
                        return b0
                    } else {
                        b4.setAttribute(b1, b5 + "");
                        return b5
                    }
                }
            } else {
                if (e && "get" in e && b2 && (b0 = e.get(b4, b1)) !== null) {
                    return b0
                } else {
                    b0 = b4.getAttribute(b1);
                    return b0 === null ? aB : b0
                }
            }
        },
        removeAttr: function (b1, b3) {
            var b2, b4, bZ, e, b0 = 0;
            if (b3 && b1.nodeType === 1) {
                b4 = b3.split(aV);
                for (; b0 < b4.length; b0++) {
                    bZ = b4[b0];
                    if (bZ) {
                        b2 = bG.propFix[bZ] || bZ;
                        e = M.test(bZ);
                        if (!e) {
                            bG.attr(b1, bZ, "")
                        }
                        b1.removeAttribute(bL ? bZ : b2);
                        if (e && b2 in b1) {
                            b1[b2] = false
                        }
                    }
                }
            }
        },
        attrHooks: {
            type: {
                set: function (e, bZ) {
                    if (j.test(e.nodeName) && e.parentNode) {
                        bG.error("type property can't be changed")
                    } else {
                        if (!bG.support.radioValue && bZ === "radio" && bG.nodeName(e, "input")) {
                            var b0 = e.value;
                            e.setAttribute("type", bZ);
                            if (b0) {
                                e.value = b0
                            }
                            return bZ
                        }
                    }
                }
            },
            value: {
                get: function (bZ, e) {
                    if (a7 && bG.nodeName(bZ, "button")) {
                        return a7.get(bZ, e)
                    }
                    return e in bZ ? bZ.value : null
                },
                set: function (bZ, b0, e) {
                    if (a7 && bG.nodeName(bZ, "button")) {
                        return a7.set(bZ, b0, e)
                    }
                    bZ.value = b0
                }
            }
        },
        propFix: {
            tabindex: "tabIndex",
            readonly: "readOnly",
            "for": "htmlFor",
            "class": "className",
            maxlength: "maxLength",
            cellspacing: "cellSpacing",
            cellpadding: "cellPadding",
            rowspan: "rowSpan",
            colspan: "colSpan",
            usemap: "useMap",
            frameborder: "frameBorder",
            contenteditable: "contentEditable"
        },
        prop: function (b3, b1, b4) {
            var b0, e, b2, bZ = b3.nodeType;
            if (!b3 || bZ === 3 || bZ === 8 || bZ === 2) {
                return
            }
            b2 = bZ !== 1 || !bG.isXMLDoc(b3);
            if (b2) {
                b1 = bG.propFix[b1] || b1;
                e = bG.propHooks[b1]
            }
            if (b4 !== aB) {
                if (e && "set" in e && (b0 = e.set(b3, b4, b1)) !== aB) {
                    return b0
                } else {
                    return (b3[b1] = b4)
                }
            } else {
                if (e && "get" in e && (b0 = e.get(b3, b1)) !== null) {
                    return b0
                } else {
                    return b3[b1]
                }
            }
        },
        propHooks: {
            tabIndex: {
                get: function (bZ) {
                    var e = bZ.getAttributeNode("tabindex");
                    return e && e.specified ? parseInt(e.value, 10) : aA.test(bZ.nodeName) || D.test(bZ.nodeName) && bZ.href ? 0 : aB
                }
            }
        }
    });
    bV = {
        get: function (bZ, e) {
            var b1, b0 = bG.prop(bZ, e);
            return b0 === true || typeof b0 !== "boolean" && (b1 = bZ.getAttributeNode(e)) && b1.nodeValue !== false ? e.toLowerCase() : aB
        },
        set: function (bZ, b1, e) {
            var b0;
            if (b1 === false) {
                bG.removeAttr(bZ, e)
            } else {
                b0 = bG.propFix[e] || e;
                if (b0 in bZ) {
                    bZ[b0] = true
                }
                bZ.setAttribute(e, e.toLowerCase())
            }
            return e
        }
    };
    if (!bL) {
        n = {
            name: true,
            id: true,
            coords: true
        };
        a7 = bG.valHooks.button = {
            get: function (b0, bZ) {
                var e;
                e = b0.getAttributeNode(bZ);
                return e && (n[bZ] ? e.value !== "" : e.specified) ? e.value : aB
            },
            set: function (b0, b1, bZ) {
                var e = b0.getAttributeNode(bZ);
                if (!e) {
                    e = o.createAttribute(bZ);
                    b0.setAttributeNode(e)
                }
                return (e.value = b1 + "")
            }
        };
        bG.each(["width", "height"], function (bZ, e) {
            bG.attrHooks[e] = bG.extend(bG.attrHooks[e], {
                set: function (b0, b1) {
                    if (b1 === "") {
                        b0.setAttribute(e, "auto");
                        return b1
                    }
                }
            })
        });
        bG.attrHooks.contenteditable = {
            get: a7.get,
            set: function (bZ, b0, e) {
                if (b0 === "") {
                    b0 = "false"
                }
                a7.set(bZ, b0, e)
            }
        }
    }
    if (!bG.support.hrefNormalized) {
        bG.each(["href", "src", "width", "height"], function (bZ, e) {
            bG.attrHooks[e] = bG.extend(bG.attrHooks[e], {
                get: function (b1) {
                    var b0 = b1.getAttribute(e, 2);
                    return b0 === null ? aB : b0
                }
            })
        })
    }
    if (!bG.support.style) {
        bG.attrHooks.style = {
            get: function (e) {
                return e.style.cssText.toLowerCase() || aB
            },
            set: function (e, bZ) {
                return (e.style.cssText = bZ + "")
            }
        }
    }
    if (!bG.support.optSelected) {
        bG.propHooks.selected = bG.extend(bG.propHooks.selected, {
            get: function (bZ) {
                var e = bZ.parentNode;
                if (e) {
                    e.selectedIndex;
                    if (e.parentNode) {
                        e.parentNode.selectedIndex
                    }
                }
                return null
            }
        })
    }
    if (!bG.support.enctype) {
        bG.propFix.enctype = "encoding"
    }
    if (!bG.support.checkOn) {
        bG.each(["radio", "checkbox"], function () {
            bG.valHooks[this] = {
                get: function (e) {
                    return e.getAttribute("value") === null ? "on" : e.value
                }
            }
        })
    }
    bG.each(["radio", "checkbox"], function () {
        bG.valHooks[this] = bG.extend(bG.valHooks[this], {
            set: function (e, bZ) {
                if (bG.isArray(bZ)) {
                    return (e.checked = bG.inArray(bG(e).val(), bZ) >= 0)
                }
            }
        })
    });
    var bE = /^(?:textarea|input|select)$/i,
        br = /^([^\.]*|)(?:\.(.+)|)$/,
        ba = /(?:^|\s)hover(\.\S+|)\b/,
        a3 = /^key/,
        bK = /^(?:mouse|contextmenu)|click/,
        by = /^(?:focusinfocus|focusoutblur)$/,
        aq = function (e) {
            return bG.event.special.hover ? e : e.replace(ba, "mouseenter$1 mouseleave$1")
        };
    bG.event = {
        add: function (b1, b5, cc, b3, b2) {
            var b6, b4, cd, cb, ca, b8, e, b9, bZ, b0, b7;
            if (b1.nodeType === 3 || b1.nodeType === 8 || !b5 || !cc || !(b6 = bG._data(b1))) {
                return
            }
            if (cc.handler) {
                bZ = cc;
                cc = bZ.handler;
                b2 = bZ.selector
            }
            if (!cc.guid) {
                cc.guid = bG.guid++
            }
            cd = b6.events;
            if (!cd) {
                b6.events = cd = {}
            }
            b4 = b6.handle;
            if (!b4) {
                b6.handle = b4 = function (ce) {
                    return typeof bG !== "undefined" && (!ce || bG.event.triggered !== ce.type) ? bG.event.dispatch.apply(b4.elem, arguments) : aB
                };
                b4.elem = b1
            }
            b5 = bG.trim(aq(b5)).split(" ");
            for (cb = 0; cb < b5.length; cb++) {
                ca = br.exec(b5[cb]) || [];
                b8 = ca[1];
                e = (ca[2] || "").split(".").sort();
                b7 = bG.event.special[b8] || {};
                b8 = (b2 ? b7.delegateType : b7.bindType) || b8;
                b7 = bG.event.special[b8] || {};
                b9 = bG.extend({
                    type: b8,
                    origType: ca[1],
                    data: b3,
                    handler: cc,
                    guid: cc.guid,
                    selector: b2,
                    needsContext: b2 && bG.expr.match.needsContext.test(b2),
                    namespace: e.join(".")
                }, bZ);
                b0 = cd[b8];
                if (!b0) {
                    b0 = cd[b8] = [];
                    b0.delegateCount = 0;
                    if (!b7.setup || b7.setup.call(b1, b3, e, b4) === false) {
                        if (b1.addEventListener) {
                            b1.addEventListener(b8, b4, false)
                        } else {
                            if (b1.attachEvent) {
                                b1.attachEvent("on" + b8, b4)
                            }
                        }
                    }
                }
                if (b7.add) {
                    b7.add.call(b1, b9);
                    if (!b9.handler.guid) {
                        b9.handler.guid = cc.guid
                    }
                }
                if (b2) {
                    b0.splice(b0.delegateCount++, 0, b9)
                } else {
                    b0.push(b9)
                }
                bG.event.global[b8] = true
            }
            b1 = null
        },
        global: {},
        remove: function (b1, b6, cc, b2, b5) {
            var cd, ce, b9, b0, bZ, b3, b4, cb, b8, e, ca, b7 = bG.hasData(b1) && bG._data(b1);
            if (!b7 || !(cb = b7.events)) {
                return
            }
            b6 = bG.trim(aq(b6 || "")).split(" ");
            for (cd = 0; cd < b6.length; cd++) {
                ce = br.exec(b6[cd]) || [];
                b9 = b0 = ce[1];
                bZ = ce[2];
                if (!b9) {
                    for (b9 in cb) {
                        bG.event.remove(b1, b9 + b6[cd], cc, b2, true)
                    }
                    continue
                }
                b8 = bG.event.special[b9] || {};
                b9 = (b2 ? b8.delegateType : b8.bindType) || b9;
                e = cb[b9] || [];
                b3 = e.length;
                bZ = bZ ? new RegExp("(^|\\.)" + bZ.split(".").sort().join("\\.(?:.*\\.|)") + "(\\.|$)") : null;
                for (b4 = 0; b4 < e.length; b4++) {
                    ca = e[b4];
                    if ((b5 || b0 === ca.origType) && (!cc || cc.guid === ca.guid) && (!bZ || bZ.test(ca.namespace)) && (!b2 || b2 === ca.selector || b2 === "**" && ca.selector)) {
                        e.splice(b4--, 1);
                        if (ca.selector) {
                            e.delegateCount--
                        }
                        if (b8.remove) {
                            b8.remove.call(b1, ca)
                        }
                    }
                }
                if (e.length === 0 && b3 !== e.length) {
                    if (!b8.teardown || b8.teardown.call(b1, bZ, b7.handle) === false) {
                        bG.removeEvent(b1, b9, b7.handle)
                    }
                    delete cb[b9]
                }
            }
            if (bG.isEmptyObject(cb)) {
                delete b7.handle;
                bG.removeData(b1, "events", true)
            }
        },
        customEvent: {
            getData: true,
            setData: true,
            changeData: true
        },
        trigger: function (bZ, b6, b4, cd) {
            if (b4 && (b4.nodeType === 3 || b4.nodeType === 8)) {
                return
            }
            var e, b1, b7, cb, b3, b2, b9, b8, b5, cc, ca = bZ.type || bZ,
                b0 = [];
            if (by.test(ca + bG.event.triggered)) {
                return
            }
            if (ca.indexOf("!") >= 0) {
                ca = ca.slice(0, -1);
                b1 = true
            }
            if (ca.indexOf(".") >= 0) {
                b0 = ca.split(".");
                ca = b0.shift();
                b0.sort()
            }
            if ((!b4 || bG.event.customEvent[ca]) && !bG.event.global[ca]) {
                return
            }
            bZ = typeof bZ === "object" ? bZ[bG.expando] ? bZ : new bG.Event(ca, bZ) : new bG.Event(ca);
            bZ.type = ca;
            bZ.isTrigger = true;
            bZ.exclusive = b1;
            bZ.namespace = b0.join(".");
            bZ.namespace_re = bZ.namespace ? new RegExp("(^|\\.)" + b0.join("\\.(?:.*\\.|)") + "(\\.|$)") : null;
            b2 = ca.indexOf(":") < 0 ? "on" + ca : "";
            if (!b4) {
                e = bG.cache;
                for (b7 in e) {
                    if (e[b7].events && e[b7].events[ca]) {
                        bG.event.trigger(bZ, b6, e[b7].handle.elem, true)
                    }
                }
                return
            }
            bZ.result = aB;
            if (!bZ.target) {
                bZ.target = b4
            }
            b6 = b6 != null ? bG.makeArray(b6) : [];
            b6.unshift(bZ);
            b9 = bG.event.special[ca] || {};
            if (b9.trigger && b9.trigger.apply(b4, b6) === false) {
                return
            }
            b5 = [
                [b4, b9.bindType || ca]
            ];
            if (!cd && !b9.noBubble && !bG.isWindow(b4)) {
                cc = b9.delegateType || ca;
                cb = by.test(cc + ca) ? b4 : b4.parentNode;
                for (b3 = b4; cb; cb = cb.parentNode) {
                    b5.push([cb, cc]);
                    b3 = cb
                }
                if (b3 === (b4.ownerDocument || o)) {
                    b5.push([b3.defaultView || b3.parentWindow || a2, cc])
                }
            }
            for (b7 = 0; b7 < b5.length && !bZ.isPropagationStopped(); b7++) {
                cb = b5[b7][0];
                bZ.type = b5[b7][1];
                b8 = (bG._data(cb, "events") || {})[bZ.type] && bG._data(cb, "handle");
                if (b8) {
                    b8.apply(cb, b6)
                }
                b8 = b2 && cb[b2];
                if (b8 && bG.acceptData(cb) && b8.apply && b8.apply(cb, b6) === false) {
                    bZ.preventDefault()
                }
            }
            bZ.type = ca;
            if (!cd && !bZ.isDefaultPrevented()) {
                if ((!b9._default || b9._default.apply(b4.ownerDocument, b6) === false) && !(ca === "click" && bG.nodeName(b4, "a")) && bG.acceptData(b4)) {
                    if (b2 && b4[ca] && ((ca !== "focus" && ca !== "blur") || bZ.target.offsetWidth !== 0) && !bG.isWindow(b4)) {
                        b3 = b4[b2];
                        if (b3) {
                            b4[b2] = null
                        }
                        bG.event.triggered = ca;
                        b4[ca]();
                        bG.event.triggered = aB;
                        if (b3) {
                            b4[b2] = b3
                        }
                    }
                }
            }
            return bZ.result
        },
        dispatch: function (e) {
            e = bG.event.fix(e || a2.event);
            var b5, b4, ce, b8, b7, bZ, b6, cc, b1, cd, b2 = ((bG._data(this, "events") || {})[e.type] || []),
                b3 = b2.delegateCount,
                ca = a4.call(arguments),
                b0 = !e.exclusive && !e.namespace,
                b9 = bG.event.special[e.type] || {}, cb = [];
            ca[0] = e;
            e.delegateTarget = this;
            if (b9.preDispatch && b9.preDispatch.call(this, e) === false) {
                return
            }
            if (b3 && !(e.button && e.type === "click")) {
                for (ce = e.target; ce != this; ce = ce.parentNode || this) {
                    if (ce.disabled !== true || e.type !== "click") {
                        b7 = {};
                        b6 = [];
                        for (b5 = 0; b5 < b3; b5++) {
                            cc = b2[b5];
                            b1 = cc.selector;
                            if (b7[b1] === aB) {
                                b7[b1] = cc.needsContext ? bG(b1, this).index(ce) >= 0 : bG.find(b1, this, null, [ce]).length
                            }
                            if (b7[b1]) {
                                b6.push(cc)
                            }
                        }
                        if (b6.length) {
                            cb.push({
                                elem: ce,
                                matches: b6
                            })
                        }
                    }
                }
            }
            if (b2.length > b3) {
                cb.push({
                    elem: this,
                    matches: b2.slice(b3)
                })
            }
            for (b5 = 0; b5 < cb.length && !e.isPropagationStopped(); b5++) {
                bZ = cb[b5];
                e.currentTarget = bZ.elem;
                for (b4 = 0; b4 < bZ.matches.length && !e.isImmediatePropagationStopped(); b4++) {
                    cc = bZ.matches[b4];
                    if (b0 || (!e.namespace && !cc.namespace) || e.namespace_re && e.namespace_re.test(cc.namespace)) {
                        e.data = cc.data;
                        e.handleObj = cc;
                        b8 = ((bG.event.special[cc.origType] || {}).handle || cc.handler).apply(bZ.elem, ca);
                        if (b8 !== aB) {
                            e.result = b8;
                            if (b8 === false) {
                                e.preventDefault();
                                e.stopPropagation()
                            }
                        }
                    }
                }
            }
            if (b9.postDispatch) {
                b9.postDispatch.call(this, e)
            }
            return e.result
        },
        props: "attrChange attrName relatedNode srcElement altKey bubbles cancelable ctrlKey currentTarget eventPhase metaKey relatedTarget shiftKey target timeStamp view which".split(" "),
        fixHooks: {},
        keyHooks: {
            props: "char charCode key keyCode".split(" "),
            filter: function (bZ, e) {
                if (bZ.which == null) {
                    bZ.which = e.charCode != null ? e.charCode : e.keyCode
                }
                return bZ
            }
        },
        mouseHooks: {
            props: "button buttons clientX clientY fromElement offsetX offsetY pageX pageY screenX screenY toElement".split(" "),
            filter: function (b1, b0) {
                var b2, b3, e, bZ = b0.button,
                    b4 = b0.fromElement;
                if (b1.pageX == null && b0.clientX != null) {
                    b2 = b1.target.ownerDocument || o;
                    b3 = b2.documentElement;
                    e = b2.body;
                    b1.pageX = b0.clientX + (b3 && b3.scrollLeft || e && e.scrollLeft || 0) - (b3 && b3.clientLeft || e && e.clientLeft || 0);
                    b1.pageY = b0.clientY + (b3 && b3.scrollTop || e && e.scrollTop || 0) - (b3 && b3.clientTop || e && e.clientTop || 0)
                }
                if (!b1.relatedTarget && b4) {
                    b1.relatedTarget = b4 === b1.target ? b0.toElement : b4
                }
                if (!b1.which && bZ !== aB) {
                    b1.which = (bZ & 1 ? 1 : (bZ & 2 ? 3 : (bZ & 4 ? 2 : 0)))
                }
                return b1
            }
        },
        fix: function (b0) {
            if (b0[bG.expando]) {
                return b0
            }
            var bZ, b3, e = b0,
                b1 = bG.event.fixHooks[b0.type] || {}, b2 = b1.props ? this.props.concat(b1.props) : this.props;
            b0 = bG.Event(e);
            for (bZ = b2.length; bZ;) {
                b3 = b2[--bZ];
                b0[b3] = e[b3]
            }
            if (!b0.target) {
                b0.target = e.srcElement || o
            }
            if (b0.target.nodeType === 3) {
                b0.target = b0.target.parentNode
            }
            b0.metaKey = !! b0.metaKey;
            return b1.filter ? b1.filter(b0, e) : b0
        },
        special: {
            load: {
                noBubble: true
            },
            focus: {
                delegateType: "focusin"
            },
            blur: {
                delegateType: "focusout"
            },
            beforeunload: {
                setup: function (b0, bZ, e) {
                    if (bG.isWindow(this)) {
                        this.onbeforeunload = e
                    }
                },
                teardown: function (bZ, e) {
                    if (this.onbeforeunload === e) {
                        this.onbeforeunload = null
                    }
                }
            }
        },
        simulate: function (b0, b2, b1, bZ) {
            var b3 = bG.extend(new bG.Event(), b1, {
                type: b0,
                isSimulated: true,
                originalEvent: {}
            });
            if (bZ) {
                bG.event.trigger(b3, null, b2)
            } else {
                bG.event.dispatch.call(b2, b3)
            } if (b3.isDefaultPrevented()) {
                b1.preventDefault()
            }
        }
    };
    bG.event.handle = bG.event.dispatch;
    bG.removeEvent = o.removeEventListener ? function (bZ, e, b0) {
        if (bZ.removeEventListener) {
            bZ.removeEventListener(e, b0, false)
        }
    } : function (b0, bZ, b1) {
        var e = "on" + bZ;
        if (b0.detachEvent) {
            if (typeof b0[e] === "undefined") {
                b0[e] = null
            }
            b0.detachEvent(e, b1)
        }
    };
    bG.Event = function (bZ, e) {
        if (!(this instanceof bG.Event)) {
            return new bG.Event(bZ, e)
        }
        if (bZ && bZ.type) {
            this.originalEvent = bZ;
            this.type = bZ.type;
            this.isDefaultPrevented = (bZ.defaultPrevented || bZ.returnValue === false || bZ.getPreventDefault && bZ.getPreventDefault()) ? R : X
        } else {
            this.type = bZ
        } if (e) {
            bG.extend(this, e)
        }
        this.timeStamp = bZ && bZ.timeStamp || bG.now();
        this[bG.expando] = true
    };

    function X() {
        return false
    }

    function R() {
        return true
    }
    bG.Event.prototype = {
        preventDefault: function () {
            this.isDefaultPrevented = R;
            var bZ = this.originalEvent;
            if (!bZ) {
                return
            }
            if (bZ.preventDefault) {
                bZ.preventDefault()
            } else {
                bZ.returnValue = false
            }
        },
        stopPropagation: function () {
            this.isPropagationStopped = R;
            var bZ = this.originalEvent;
            if (!bZ) {
                return
            }
            if (bZ.stopPropagation) {
                bZ.stopPropagation()
            }
            bZ.cancelBubble = true
        },
        stopImmediatePropagation: function () {
            this.isImmediatePropagationStopped = R;
            this.stopPropagation()
        },
        isDefaultPrevented: X,
        isPropagationStopped: X,
        isImmediatePropagationStopped: X
    };
    bG.each({
        mouseenter: "mouseover",
        mouseleave: "mouseout"
    }, function (bZ, e) {
        bG.event.special[bZ] = {
            delegateType: e,
            bindType: e,
            handle: function (b3) {
                var b1, b5 = this,
                    b4 = b3.relatedTarget,
                    b2 = b3.handleObj,
                    b0 = b2.selector;
                if (!b4 || (b4 !== b5 && !bG.contains(b5, b4))) {
                    b3.type = b2.origType;
                    b1 = b2.handler.apply(this, arguments);
                    b3.type = e
                }
                return b1
            }
        }
    });
    if (!bG.support.submitBubbles) {
        bG.event.special.submit = {
            setup: function () {
                if (bG.nodeName(this, "form")) {
                    return false
                }
                bG.event.add(this, "click._submit keypress._submit", function (b1) {
                    var b0 = b1.target,
                        bZ = bG.nodeName(b0, "input") || bG.nodeName(b0, "button") ? b0.form : aB;
                    if (bZ && !bG._data(bZ, "_submit_attached")) {
                        bG.event.add(bZ, "submit._submit", function (e) {
                            e._submit_bubble = true
                        });
                        bG._data(bZ, "_submit_attached", true)
                    }
                })
            },
            postDispatch: function (e) {
                if (e._submit_bubble) {
                    delete e._submit_bubble;
                    if (this.parentNode && !e.isTrigger) {
                        bG.event.simulate("submit", this.parentNode, e, true)
                    }
                }
            },
            teardown: function () {
                if (bG.nodeName(this, "form")) {
                    return false
                }
                bG.event.remove(this, "._submit")
            }
        }
    }
    if (!bG.support.changeBubbles) {
        bG.event.special.change = {
            setup: function () {
                if (bE.test(this.nodeName)) {
                    if (this.type === "checkbox" || this.type === "radio") {
                        bG.event.add(this, "propertychange._change", function (e) {
                            if (e.originalEvent.propertyName === "checked") {
                                this._just_changed = true
                            }
                        });
                        bG.event.add(this, "click._change", function (e) {
                            if (this._just_changed && !e.isTrigger) {
                                this._just_changed = false
                            }
                            bG.event.simulate("change", this, e, true)
                        })
                    }
                    return false
                }
                bG.event.add(this, "beforeactivate._change", function (b0) {
                    var bZ = b0.target;
                    if (bE.test(bZ.nodeName) && !bG._data(bZ, "_change_attached")) {
                        bG.event.add(bZ, "change._change", function (e) {
                            if (this.parentNode && !e.isSimulated && !e.isTrigger) {
                                bG.event.simulate("change", this.parentNode, e, true)
                            }
                        });
                        bG._data(bZ, "_change_attached", true)
                    }
                })
            },
            handle: function (bZ) {
                var e = bZ.target;
                if (this !== e || bZ.isSimulated || bZ.isTrigger || (e.type !== "radio" && e.type !== "checkbox")) {
                    return bZ.handleObj.handler.apply(this, arguments)
                }
            },
            teardown: function () {
                bG.event.remove(this, "._change");
                return !bE.test(this.nodeName)
            }
        }
    }
    if (!bG.support.focusinBubbles) {
        bG.each({
            focus: "focusin",
            blur: "focusout"
        }, function (b1, e) {
            var bZ = 0,
                b0 = function (b2) {
                    bG.event.simulate(e, b2.target, bG.event.fix(b2), true)
                };
            bG.event.special[e] = {
                setup: function () {
                    if (bZ++ === 0) {
                        o.addEventListener(b1, b0, true)
                    }
                },
                teardown: function () {
                    if (--bZ === 0) {
                        o.removeEventListener(b1, b0, true)
                    }
                }
            }
        })
    }
    bG.fn.extend({
        on: function (b0, e, b3, b2, bZ) {
            var b4, b1;
            if (typeof b0 === "object") {
                if (typeof e !== "string") {
                    b3 = b3 || e;
                    e = aB
                }
                for (b1 in b0) {
                    this.on(b1, e, b3, b0[b1], bZ)
                }
                return this
            }
            if (b3 == null && b2 == null) {
                b2 = e;
                b3 = e = aB
            } else {
                if (b2 == null) {
                    if (typeof e === "string") {
                        b2 = b3;
                        b3 = aB
                    } else {
                        b2 = b3;
                        b3 = e;
                        e = aB
                    }
                }
            } if (b2 === false) {
                b2 = X
            } else {
                if (!b2) {
                    return this
                }
            } if (bZ === 1) {
                b4 = b2;
                b2 = function (b5) {
                    bG().off(b5);
                    return b4.apply(this, arguments)
                };
                b2.guid = b4.guid || (b4.guid = bG.guid++)
            }
            return this.each(function () {
                bG.event.add(this, b0, b2, b3, e)
            })
        },
        one: function (bZ, e, b1, b0) {
            return this.on(bZ, e, b1, b0, 1)
        },
        off: function (b0, e, b2) {
            var bZ, b1;
            if (b0 && b0.preventDefault && b0.handleObj) {
                bZ = b0.handleObj;
                bG(b0.delegateTarget).off(bZ.namespace ? bZ.origType + "." + bZ.namespace : bZ.origType, bZ.selector, bZ.handler);
                return this
            }
            if (typeof b0 === "object") {
                for (b1 in b0) {
                    this.off(b1, e, b0[b1])
                }
                return this
            }
            if (e === false || typeof e === "function") {
                b2 = e;
                e = aB
            }
            if (b2 === false) {
                b2 = X
            }
            return this.each(function () {
                bG.event.remove(this, b0, b2, e)
            })
        },
        bind: function (e, b0, bZ) {
            return this.on(e, null, b0, bZ)
        },
        unbind: function (e, bZ) {
            return this.off(e, null, bZ)
        },
        live: function (e, b0, bZ) {
            bG(this.context).on(e, this.selector, b0, bZ);
            return this
        },
        die: function (e, bZ) {
            bG(this.context).off(e, this.selector || "**", bZ);
            return this
        },
        delegate: function (e, bZ, b1, b0) {
            return this.on(bZ, e, b1, b0)
        },
        undelegate: function (e, bZ, b0) {
            return arguments.length === 1 ? this.off(e, "**") : this.off(bZ, e || "**", b0)
        },
        trigger: function (e, bZ) {
            return this.each(function () {
                bG.event.trigger(e, bZ, this)
            })
        },
        triggerHandler: function (e, bZ) {
            if (this[0]) {
                return bG.event.trigger(e, bZ, this[0], true)
            }
        },
        toggle: function (b1) {
            var bZ = arguments,
                e = b1.guid || bG.guid++,
                b0 = 0,
                b2 = function (b3) {
                    var b4 = (bG._data(this, "lastToggle" + b1.guid) || 0) % b0;
                    bG._data(this, "lastToggle" + b1.guid, b4 + 1);
                    b3.preventDefault();
                    return bZ[b4].apply(this, arguments) || false
                };
            b2.guid = e;
            while (b0 < bZ.length) {
                bZ[b0++].guid = e
            }
            return this.click(b2)
        },
        hover: function (e, bZ) {
            return this.mouseenter(e).mouseleave(bZ || e)
        }
    });
    bG.each(("blur focus focusin focusout load resize scroll unload click dblclick mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave change select submit keydown keypress keyup error contextmenu").split(" "), function (bZ, e) {
        bG.fn[e] = function (b1, b0) {
            if (b0 == null) {
                b0 = b1;
                b1 = null
            }
            return arguments.length > 0 ? this.on(e, null, b1, b0) : this.trigger(e)
        };
        if (a3.test(e)) {
            bG.event.fixHooks[e] = bG.event.keyHooks
        }
        if (bK.test(e)) {
            bG.event.fixHooks[e] = bG.event.mouseHooks
        }
    });
    /*
     * Sizzle CSS Selector Engine
     * Copyright 2012 jQuery Foundation and other contributors
     * Released under the MIT license
     * http://sizzlejs.com/
     */
    (function (cS, ch) {
        var cX, ca, cL, b0, cm, cA, cd, cg, cc, cJ, b9 = true,
            cu = "undefined",
            cZ = ("sizcache" + Math.random()).replace(".", ""),
            b4 = String,
            b8 = cS.document,
            cb = b8.documentElement,
            cr = 0,
            cf = 0,
            cE = [].pop,
            cW = [].push,
            cl = [].slice,
            co = [].indexOf || function (c8) {
                var c7 = 0,
                    e = this.length;
                for (; c7 < e; c7++) {
                    if (this[c7] === c8) {
                        return c7
                    }
                }
                return -1
            }, c1 = function (e, c7) {
                e[cZ] = c7 == null || c7;
                return e
            }, c5 = function () {
                var e = {}, c7 = [];
                return c1(function (c8, c9) {
                    if (c7.push(c8) > cL.cacheLength) {
                        delete e[c7.shift()]
                    }
                    return (e[c8 + " "] = c9)
                }, e)
            }, cU = c5(),
            cV = c5(),
            cn = c5(),
            cy = "[\\x20\\t\\r\\n\\f]",
            ck = "(?:\\\\.|[-\\w]|[^\\x00-\\xa0])+",
            ci = ck.replace("w", "w#"),
            c4 = "([*^$|!~]?=)",
            cP = "\\[" + cy + "*(" + ck + ")" + cy + "*(?:" + c4 + cy + "*(?:(['\"])((?:\\\\.|[^\\\\])*?)\\3|(" + ci + ")|)|)" + cy + "*\\]",
            c6 = ":(" + ck + ")(?:\\((?:(['\"])((?:\\\\.|[^\\\\])*?)\\2|([^()[\\]]*|(?:(?:" + cP + ")|[^:]|\\\\.)*|.*))\\)|)",
            cz = ":(even|odd|eq|gt|lt|nth|first|last)(?:\\(" + cy + "*((?:-\\d)?\\d*)" + cy + "*\\)|)(?=[^-]|$)",
            cT = new RegExp("^" + cy + "+|((?:^|[^\\\\])(?:\\\\.)*)" + cy + "+$", "g"),
            b5 = new RegExp("^" + cy + "*," + cy + "*"),
            cH = new RegExp("^" + cy + "*([\\x20\\t\\r\\n\\f>+~])" + cy + "*"),
            cM = new RegExp(c6),
            cO = /^(?:#([\w\-]+)|(\w+)|\.([\w\-]+))$/,
            cD = /^:not/,
            cR = /[\x20\t\r\n\f]*[+~]/,
            c0 = /:not\($/,
            cs = /h\d/i,
            cN = /input|select|textarea|button/i,
            ct = /\\(?!\\)/g,
            cG = {
                ID: new RegExp("^#(" + ck + ")"),
                CLASS: new RegExp("^\\.(" + ck + ")"),
                NAME: new RegExp("^\\[name=['\"]?(" + ck + ")['\"]?\\]"),
                TAG: new RegExp("^(" + ck.replace("w", "w*") + ")"),
                ATTR: new RegExp("^" + cP),
                PSEUDO: new RegExp("^" + c6),
                POS: new RegExp(cz, "i"),
                CHILD: new RegExp("^:(only|nth|first|last)-child(?:\\(" + cy + "*(even|odd|(([+-]|)(\\d*)n|)" + cy + "*(?:([+-]|)" + cy + "*(\\d+)|))" + cy + "*\\)|)", "i"),
                needsContext: new RegExp("^" + cy + "*[>+~]|" + cz, "i")
            }, cK = function (c7) {
                var c9 = b8.createElement("div");
                try {
                    return c7(c9)
                } catch (c8) {
                    return false
                } finally {
                    c9 = null
                }
            }, b7 = cK(function (e) {
                e.appendChild(b8.createComment(""));
                return !e.getElementsByTagName("*").length
            }),
            cC = cK(function (e) {
                e.innerHTML = "<a href='#'></a>";
                return e.firstChild && typeof e.firstChild.getAttribute !== cu && e.firstChild.getAttribute("href") === "#"
            }),
            cq = cK(function (c7) {
                c7.innerHTML = "<select></select>";
                var e = typeof c7.lastChild.getAttribute("multiple");
                return e !== "boolean" && e !== "string"
            }),
            cB = cK(function (e) {
                e.innerHTML = "<div class='hidden e'></div><div class='hidden'></div>";
                if (!e.getElementsByClassName || !e.getElementsByClassName("e").length) {
                    return false
                }
                e.lastChild.className = "e";
                return e.getElementsByClassName("e").length === 2
            }),
            bZ = cK(function (c7) {
                c7.id = cZ + 0;
                c7.innerHTML = "<a name='" + cZ + "'></a><div name='" + cZ + "'></div>";
                cb.insertBefore(c7, cb.firstChild);
                var e = b8.getElementsByName && b8.getElementsByName(cZ).length === 2 + b8.getElementsByName(cZ + 0).length;
                ca = !b8.getElementById(cZ);
                cb.removeChild(c7);
                return e
            });
        try {
            cl.call(cb.childNodes, 0)[0].nodeType
        } catch (c3) {
            cl = function (c7) {
                var c8, e = [];
                for (;
                    (c8 = this[c7]); c7++) {
                    e.push(c8)
                }
                return e
            }
        }

        function cQ(c9, e, db, de) {
            db = db || [];
            e = e || b8;
            var dc, c7, dd, c8, da = e.nodeType;
            if (!c9 || typeof c9 !== "string") {
                return db
            }
            if (da !== 1 && da !== 9) {
                return []
            }
            dd = cm(e);
            if (!dd && !de) {
                if ((dc = cO.exec(c9))) {
                    if ((c8 = dc[1])) {
                        if (da === 9) {
                            c7 = e.getElementById(c8);
                            if (c7 && c7.parentNode) {
                                if (c7.id === c8) {
                                    db.push(c7);
                                    return db
                                }
                            } else {
                                return db
                            }
                        } else {
                            if (e.ownerDocument && (c7 = e.ownerDocument.getElementById(c8)) && cA(e, c7) && c7.id === c8) {
                                db.push(c7);
                                return db
                            }
                        }
                    } else {
                        if (dc[2]) {
                            cW.apply(db, cl.call(e.getElementsByTagName(c9), 0));
                            return db
                        } else {
                            if ((c8 = dc[3]) && cB && e.getElementsByClassName) {
                                cW.apply(db, cl.call(e.getElementsByClassName(c8), 0));
                                return db
                            }
                        }
                    }
                }
            }
            return cY(c9.replace(cT, "$1"), e, db, de, dd)
        }
        cQ.matches = function (c7, e) {
            return cQ(c7, null, null, e)
        };
        cQ.matchesSelector = function (e, c7) {
            return cQ(c7, null, null, [e]).length > 0
        };

        function cI(e) {
            return function (c8) {
                var c7 = c8.nodeName.toLowerCase();
                return c7 === "input" && c8.type === e
            }
        }

        function b3(e) {
            return function (c8) {
                var c7 = c8.nodeName.toLowerCase();
                return (c7 === "input" || c7 === "button") && c8.type === e
            }
        }

        function cF(e) {
            return c1(function (c7) {
                c7 = +c7;
                return c1(function (c8, dc) {
                    var da, c9 = e([], c8.length, c7),
                        db = c9.length;
                    while (db--) {
                        if (c8[(da = c9[db])]) {
                            c8[da] = !(dc[da] = c8[da])
                        }
                    }
                })
            })
        }
        b0 = cQ.getText = function (da) {
            var c9, c7 = "",
                c8 = 0,
                e = da.nodeType;
            if (e) {
                if (e === 1 || e === 9 || e === 11) {
                    if (typeof da.textContent === "string") {
                        return da.textContent
                    } else {
                        for (da = da.firstChild; da; da = da.nextSibling) {
                            c7 += b0(da)
                        }
                    }
                } else {
                    if (e === 3 || e === 4) {
                        return da.nodeValue
                    }
                }
            } else {
                for (;
                    (c9 = da[c8]); c8++) {
                    c7 += b0(c9)
                }
            }
            return c7
        };
        cm = cQ.isXML = function (e) {
            var c7 = e && (e.ownerDocument || e).documentElement;
            return c7 ? c7.nodeName !== "HTML" : false
        };
        cA = cQ.contains = cb.contains ? function (c7, e) {
            var c9 = c7.nodeType === 9 ? c7.documentElement : c7,
                c8 = e && e.parentNode;
            return c7 === c8 || !! (c8 && c8.nodeType === 1 && c9.contains && c9.contains(c8))
        } : cb.compareDocumentPosition ? function (c7, e) {
            return e && !! (c7.compareDocumentPosition(e) & 16)
        } : function (c7, e) {
            while ((e = e.parentNode)) {
                if (e === c7) {
                    return true
                }
            }
            return false
        };
        cQ.attr = function (c8, c7) {
            var c9, e = cm(c8);
            if (!e) {
                c7 = c7.toLowerCase()
            }
            if ((c9 = cL.attrHandle[c7])) {
                return c9(c8)
            }
            if (e || cq) {
                return c8.getAttribute(c7)
            }
            c9 = c8.getAttributeNode(c7);
            return c9 ? typeof c8[c7] === "boolean" ? c8[c7] ? c7 : null : c9.specified ? c9.value : null : null
        };
        cL = cQ.selectors = {
            cacheLength: 50,
            createPseudo: c1,
            match: cG,
            attrHandle: cC ? {} : {
                href: function (e) {
                    return e.getAttribute("href", 2)
                },
                type: function (e) {
                    return e.getAttribute("type")
                }
            },
            find: {
                ID: ca ? function (c9, c8, c7) {
                    if (typeof c8.getElementById !== cu && !c7) {
                        var e = c8.getElementById(c9);
                        return e && e.parentNode ? [e] : []
                    }
                } : function (c9, c8, c7) {
                    if (typeof c8.getElementById !== cu && !c7) {
                        var e = c8.getElementById(c9);
                        return e ? e.id === c9 || typeof e.getAttributeNode !== cu && e.getAttributeNode("id").value === c9 ? [e] : ch : []
                    }
                },
                TAG: b7 ? function (e, c7) {
                    if (typeof c7.getElementsByTagName !== cu) {
                        return c7.getElementsByTagName(e)
                    }
                } : function (e, da) {
                    var c9 = da.getElementsByTagName(e);
                    if (e === "*") {
                        var db, c8 = [],
                            c7 = 0;
                        for (;
                            (db = c9[c7]); c7++) {
                            if (db.nodeType === 1) {
                                c8.push(db)
                            }
                        }
                        return c8
                    }
                    return c9
                },
                NAME: bZ && function (e, c7) {
                    if (typeof c7.getElementsByName !== cu) {
                        return c7.getElementsByName(name)
                    }
                },
                CLASS: cB && function (c8, c7, e) {
                    if (typeof c7.getElementsByClassName !== cu && !e) {
                        return c7.getElementsByClassName(c8)
                    }
                }
            },
            relative: {
                ">": {
                    dir: "parentNode",
                    first: true
                },
                " ": {
                    dir: "parentNode"
                },
                "+": {
                    dir: "previousSibling",
                    first: true
                },
                "~": {
                    dir: "previousSibling"
                }
            },
            preFilter: {
                ATTR: function (e) {
                    e[1] = e[1].replace(ct, "");
                    e[3] = (e[4] || e[5] || "").replace(ct, "");
                    if (e[2] === "~=") {
                        e[3] = " " + e[3] + " "
                    }
                    return e.slice(0, 4)
                },
                CHILD: function (e) {
                    e[1] = e[1].toLowerCase();
                    if (e[1] === "nth") {
                        if (!e[2]) {
                            cQ.error(e[0])
                        }
                        e[3] = +(e[3] ? e[4] + (e[5] || 1) : 2 * (e[2] === "even" || e[2] === "odd"));
                        e[4] = +((e[6] + e[7]) || e[2] === "odd")
                    } else {
                        if (e[2]) {
                            cQ.error(e[0])
                        }
                    }
                    return e
                },
                PSEUDO: function (c7) {
                    var c8, e;
                    if (cG.CHILD.test(c7[0])) {
                        return null
                    }
                    if (c7[3]) {
                        c7[2] = c7[3]
                    } else {
                        if ((c8 = c7[4])) {
                            if (cM.test(c8) && (e = b1(c8, true)) && (e = c8.indexOf(")", c8.length - e) - c8.length)) {
                                c8 = c8.slice(0, e);
                                c7[0] = c7[0].slice(0, e)
                            }
                            c7[2] = c8
                        }
                    }
                    return c7.slice(0, 3)
                }
            },
            filter: {
                ID: ca ? function (e) {
                    e = e.replace(ct, "");
                    return function (c7) {
                        return c7.getAttribute("id") === e
                    }
                } : function (e) {
                    e = e.replace(ct, "");
                    return function (c8) {
                        var c7 = typeof c8.getAttributeNode !== cu && c8.getAttributeNode("id");
                        return c7 && c7.value === e
                    }
                },
                TAG: function (e) {
                    if (e === "*") {
                        return function () {
                            return true
                        }
                    }
                    e = e.replace(ct, "").toLowerCase();
                    return function (c7) {
                        return c7.nodeName && c7.nodeName.toLowerCase() === e
                    }
                },
                CLASS: function (e) {
                    var c7 = cU[cZ][e + " "];
                    return c7 || (c7 = new RegExp("(^|" + cy + ")" + e + "(" + cy + "|$)")) && cU(e, function (c8) {
                        return c7.test(c8.className || (typeof c8.getAttribute !== cu && c8.getAttribute("class")) || "")
                    })
                },
                ATTR: function (c8, c7, e) {
                    return function (db, da) {
                        var c9 = cQ.attr(db, c8);
                        if (c9 == null) {
                            return c7 === "!="
                        }
                        if (!c7) {
                            return true
                        }
                        c9 += "";
                        return c7 === "=" ? c9 === e : c7 === "!=" ? c9 !== e : c7 === "^=" ? e && c9.indexOf(e) === 0 : c7 === "*=" ? e && c9.indexOf(e) > -1 : c7 === "$=" ? e && c9.substr(c9.length - e.length) === e : c7 === "~=" ? (" " + c9 + " ").indexOf(e) > -1 : c7 === "|=" ? c9 === e || c9.substr(0, e.length + 1) === e + "-" : false
                    }
                },
                CHILD: function (e, c8, c9, c7) {
                    if (e === "nth") {
                        return function (dc) {
                            var db, dd, da = dc.parentNode;
                            if (c9 === 1 && c7 === 0) {
                                return true
                            }
                            if (da) {
                                dd = 0;
                                for (db = da.firstChild; db; db = db.nextSibling) {
                                    if (db.nodeType === 1) {
                                        dd++;
                                        if (dc === db) {
                                            break
                                        }
                                    }
                                }
                            }
                            dd -= c7;
                            return dd === c9 || (dd % c9 === 0 && dd / c9 >= 0)
                        }
                    }
                    return function (db) {
                        var da = db;
                        switch (e) {
                        case "only":
                        case "first":
                            while ((da = da.previousSibling)) {
                                if (da.nodeType === 1) {
                                    return false
                                }
                            }
                            if (e === "first") {
                                return true
                            }
                            da = db;
                        case "last":
                            while ((da = da.nextSibling)) {
                                if (da.nodeType === 1) {
                                    return false
                                }
                            }
                            return true
                        }
                    }
                },
                PSEUDO: function (c9, c8) {
                    var e, c7 = cL.pseudos[c9] || cL.setFilters[c9.toLowerCase()] || cQ.error("unsupported pseudo: " + c9);
                    if (c7[cZ]) {
                        return c7(c8)
                    }
                    if (c7.length > 1) {
                        e = [c9, c9, "", c8];
                        return cL.setFilters.hasOwnProperty(c9.toLowerCase()) ? c1(function (dc, de) {
                            var db, da = c7(dc, c8),
                                dd = da.length;
                            while (dd--) {
                                db = co.call(dc, da[dd]);
                                dc[db] = !(de[db] = da[dd])
                            }
                        }) : function (da) {
                            return c7(da, 0, e)
                        }
                    }
                    return c7
                }
            },
            pseudos: {
                not: c1(function (e) {
                    var c7 = [],
                        c8 = [],
                        c9 = cd(e.replace(cT, "$1"));
                    return c9[cZ] ? c1(function (db, dg, de, dc) {
                        var df, da = c9(db, null, dc, []),
                            dd = db.length;
                        while (dd--) {
                            if ((df = da[dd])) {
                                db[dd] = !(dg[dd] = df)
                            }
                        }
                    }) : function (dc, db, da) {
                        c7[0] = dc;
                        c9(c7, null, da, c8);
                        return !c8.pop()
                    }
                }),
                has: c1(function (e) {
                    return function (c7) {
                        return cQ(e, c7).length > 0
                    }
                }),
                contains: c1(function (e) {
                    return function (c7) {
                        return (c7.textContent || c7.innerText || b0(c7)).indexOf(e) > -1
                    }
                }),
                enabled: function (e) {
                    return e.disabled === false
                },
                disabled: function (e) {
                    return e.disabled === true
                },
                checked: function (e) {
                    var c7 = e.nodeName.toLowerCase();
                    return (c7 === "input" && !! e.checked) || (c7 === "option" && !! e.selected)
                },
                selected: function (e) {
                    if (e.parentNode) {
                        e.parentNode.selectedIndex
                    }
                    return e.selected === true
                },
                parent: function (e) {
                    return !cL.pseudos.empty(e)
                },
                empty: function (c7) {
                    var e;
                    c7 = c7.firstChild;
                    while (c7) {
                        if (c7.nodeName > "@" || (e = c7.nodeType) === 3 || e === 4) {
                            return false
                        }
                        c7 = c7.nextSibling
                    }
                    return true
                },
                header: function (e) {
                    return cs.test(e.nodeName)
                },
                text: function (c8) {
                    var c7, e;
                    return c8.nodeName.toLowerCase() === "input" && (c7 = c8.type) === "text" && ((e = c8.getAttribute("type")) == null || e.toLowerCase() === c7)
                },
                radio: cI("radio"),
                checkbox: cI("checkbox"),
                file: cI("file"),
                password: cI("password"),
                image: cI("image"),
                submit: b3("submit"),
                reset: b3("reset"),
                button: function (c7) {
                    var e = c7.nodeName.toLowerCase();
                    return e === "input" && c7.type === "button" || e === "button"
                },
                input: function (e) {
                    return cN.test(e.nodeName)
                },
                focus: function (e) {
                    var c7 = e.ownerDocument;
                    return e === c7.activeElement && (!c7.hasFocus || c7.hasFocus()) && !! (e.type || e.href || ~e.tabIndex)
                },
                active: function (e) {
                    return e === e.ownerDocument.activeElement
                },
                first: cF(function () {
                    return [0]
                }),
                last: cF(function (e, c7) {
                    return [c7 - 1]
                }),
                eq: cF(function (e, c8, c7) {
                    return [c7 < 0 ? c7 + c8 : c7]
                }),
                even: cF(function (e, c8) {
                    for (var c7 = 0; c7 < c8; c7 += 2) {
                        e.push(c7)
                    }
                    return e
                }),
                odd: cF(function (e, c8) {
                    for (var c7 = 1; c7 < c8; c7 += 2) {
                        e.push(c7)
                    }
                    return e
                }),
                lt: cF(function (e, c9, c8) {
                    for (var c7 = c8 < 0 ? c8 + c9 : c8; --c7 >= 0;) {
                        e.push(c7)
                    }
                    return e
                }),
                gt: cF(function (e, c9, c8) {
                    for (var c7 = c8 < 0 ? c8 + c9 : c8; ++c7 < c9;) {
                        e.push(c7)
                    }
                    return e
                })
            }
        };

        function b2(c7, e, c8) {
            if (c7 === e) {
                return c8
            }
            var c9 = c7.nextSibling;
            while (c9) {
                if (c9 === e) {
                    return -1
                }
                c9 = c9.nextSibling
            }
            return 1
        }
        cg = cb.compareDocumentPosition ? function (c7, e) {
            if (c7 === e) {
                cc = true;
                return 0
            }
            return (!c7.compareDocumentPosition || !e.compareDocumentPosition ? c7.compareDocumentPosition : c7.compareDocumentPosition(e) & 4) ? -1 : 1
        } : function (de, dd) {
            if (de === dd) {
                cc = true;
                return 0
            } else {
                if (de.sourceIndex && dd.sourceIndex) {
                    return de.sourceIndex - dd.sourceIndex
                }
            }
            var db, c7, c8 = [],
                e = [],
                da = de.parentNode,
                dc = dd.parentNode,
                df = da;
            if (da === dc) {
                return b2(de, dd)
            } else {
                if (!da) {
                    return -1
                } else {
                    if (!dc) {
                        return 1
                    }
                }
            }
            while (df) {
                c8.unshift(df);
                df = df.parentNode
            }
            df = dc;
            while (df) {
                e.unshift(df);
                df = df.parentNode
            }
            db = c8.length;
            c7 = e.length;
            for (var c9 = 0; c9 < db && c9 < c7; c9++) {
                if (c8[c9] !== e[c9]) {
                    return b2(c8[c9], e[c9])
                }
            }
            return c9 === db ? b2(de, e[c9], -1) : b2(c8[c9], dd, 1)
        };
        [0, 0].sort(cg);
        b9 = !cc;
        cQ.uniqueSort = function (c8) {
            var c9, da = [],
                c7 = 1,
                e = 0;
            cc = b9;
            c8.sort(cg);
            if (cc) {
                for (;
                    (c9 = c8[c7]); c7++) {
                    if (c9 === c8[c7 - 1]) {
                        e = da.push(c7)
                    }
                }
                while (e--) {
                    c8.splice(da[e], 1)
                }
            }
            return c8
        };
        cQ.error = function (e) {
            throw new Error("Syntax error, unrecognized expression: " + e)
        };

        function b1(da, df) {
            var c7, db, dd, de, dc, c8, e, c9 = cV[cZ][da + " "];
            if (c9) {
                return df ? 0 : c9.slice(0)
            }
            dc = da;
            c8 = [];
            e = cL.preFilter;
            while (dc) {
                if (!c7 || (db = b5.exec(dc))) {
                    if (db) {
                        dc = dc.slice(db[0].length) || dc
                    }
                    c8.push(dd = [])
                }
                c7 = false;
                if ((db = cH.exec(dc))) {
                    dd.push(c7 = new b4(db.shift()));
                    dc = dc.slice(c7.length);
                    c7.type = db[0].replace(cT, " ")
                }
                for (de in cL.filter) {
                    if ((db = cG[de].exec(dc)) && (!e[de] || (db = e[de](db)))) {
                        dd.push(c7 = new b4(db.shift()));
                        dc = dc.slice(c7.length);
                        c7.type = de;
                        c7.matches = db
                    }
                }
                if (!c7) {
                    break
                }
            }
            return df ? dc.length : dc ? cQ.error(da) : cV(da, c8).slice(0)
        }

        function cw(da, c8, c9) {
            var e = c8.dir,
                db = c9 && c8.dir === "parentNode",
                c7 = cf++;
            return c8.first ? function (de, dd, dc) {
                while ((de = de[e])) {
                    if (db || de.nodeType === 1) {
                        return da(de, dd, dc)
                    }
                }
            } : function (df, de, dd) {
                if (!dd) {
                    var dc, dg = cr + " " + c7 + " ",
                        dh = dg + cX;
                    while ((df = df[e])) {
                        if (db || df.nodeType === 1) {
                            if ((dc = df[cZ]) === dh) {
                                return df.sizset
                            } else {
                                if (typeof dc === "string" && dc.indexOf(dg) === 0) {
                                    if (df.sizset) {
                                        return df
                                    }
                                } else {
                                    df[cZ] = dh;
                                    if (da(df, de, dd)) {
                                        df.sizset = true;
                                        return df
                                    }
                                    df.sizset = false
                                }
                            }
                        }
                    }
                } else {
                    while ((df = df[e])) {
                        if (db || df.nodeType === 1) {
                            if (da(df, de, dd)) {
                                return df
                            }
                        }
                    }
                }
            }
        }

        function ce(e) {
            return e.length > 1 ? function (da, c9, c7) {
                var c8 = e.length;
                while (c8--) {
                    if (!e[c8](da, c9, c7)) {
                        return false
                    }
                }
                return true
            } : e[0]
        }

        function cv(e, c7, c8, c9, dc) {
            var da, df = [],
                db = 0,
                dd = e.length,
                de = c7 != null;
            for (; db < dd; db++) {
                if ((da = e[db])) {
                    if (!c8 || c8(da, c9, dc)) {
                        df.push(da);
                        if (de) {
                            c7.push(db)
                        }
                    }
                }
            }
            return df
        }

        function c2(c8, c7, da, c9, db, e) {
            if (c9 && !c9[cZ]) {
                c9 = c2(c9)
            }
            if (db && !db[cZ]) {
                db = c2(db, e)
            }
            return c1(function (dm, dj, de, dl) {
                var dp, dk, dg, df = [],
                    dn = [],
                    dd = dj.length,
                    dc = dm || cp(c7 || "*", de.nodeType ? [de] : de, []),
                    dh = c8 && (dm || !c7) ? cv(dc, df, c8, de, dl) : dc,
                    di = da ? db || (dm ? c8 : dd || c9) ? [] : dj : dh;
                if (da) {
                    da(dh, di, de, dl)
                }
                if (c9) {
                    dp = cv(di, dn);
                    c9(dp, [], de, dl);
                    dk = dp.length;
                    while (dk--) {
                        if ((dg = dp[dk])) {
                            di[dn[dk]] = !(dh[dn[dk]] = dg)
                        }
                    }
                }
                if (dm) {
                    if (db || c8) {
                        if (db) {
                            dp = [];
                            dk = di.length;
                            while (dk--) {
                                if ((dg = di[dk])) {
                                    dp.push((dh[dk] = dg))
                                }
                            }
                            db(null, (di = []), dp, dl)
                        }
                        dk = di.length;
                        while (dk--) {
                            if ((dg = di[dk]) && (dp = db ? co.call(dm, dg) : df[dk]) > -1) {
                                dm[dp] = !(dj[dp] = dg)
                            }
                        }
                    }
                } else {
                    di = cv(di === dj ? di.splice(dd, di.length) : di);
                    if (db) {
                        db(null, dj, di, dl)
                    } else {
                        cW.apply(dj, di)
                    }
                }
            })
        }

        function cx(dc) {
            var c7, da, c8, db = dc.length,
                df = cL.relative[dc[0].type],
                dg = df || cL.relative[" "],
                c9 = df ? 1 : 0,
                dd = cw(function (dh) {
                    return dh === c7
                }, dg, true),
                de = cw(function (dh) {
                    return co.call(c7, dh) > -1
                }, dg, true),
                e = [
                    function (dj, di, dh) {
                        return (!df && (dh || di !== cJ)) || ((c7 = di).nodeType ? dd(dj, di, dh) : de(dj, di, dh))
                    }
                ];
            for (; c9 < db; c9++) {
                if ((da = cL.relative[dc[c9].type])) {
                    e = [cw(ce(e), da)]
                } else {
                    da = cL.filter[dc[c9].type].apply(null, dc[c9].matches);
                    if (da[cZ]) {
                        c8 = ++c9;
                        for (; c8 < db; c8++) {
                            if (cL.relative[dc[c8].type]) {
                                break
                            }
                        }
                        return c2(c9 > 1 && ce(e), c9 > 1 && dc.slice(0, c9 - 1).join("").replace(cT, "$1"), da, c9 < c8 && cx(dc.slice(c9, c8)), c8 < db && cx((dc = dc.slice(c8))), c8 < db && dc.join(""))
                    }
                    e.push(da)
                }
            }
            return ce(e)
        }

        function b6(c9, c8) {
            var e = c8.length > 0,
                da = c9.length > 0,
                c7 = function (dk, de, dj, di, dr) {
                    var df, dg, dl, dq = [],
                        dp = 0,
                        dh = "0",
                        db = dk && [],
                        dm = dr != null,
                        dn = cJ,
                        dd = dk || da && cL.find.TAG("*", dr && de.parentNode || de),
                        dc = (cr += dn == null ? 1 : Math.E);
                    if (dm) {
                        cJ = de !== b8 && de;
                        cX = c7.el
                    }
                    for (;
                        (df = dd[dh]) != null; dh++) {
                        if (da && df) {
                            for (dg = 0;
                                (dl = c9[dg]); dg++) {
                                if (dl(df, de, dj)) {
                                    di.push(df);
                                    break
                                }
                            }
                            if (dm) {
                                cr = dc;
                                cX = ++c7.el
                            }
                        }
                        if (e) {
                            if ((df = !dl && df)) {
                                dp--
                            }
                            if (dk) {
                                db.push(df)
                            }
                        }
                    }
                    dp += dh;
                    if (e && dh !== dp) {
                        for (dg = 0;
                            (dl = c8[dg]); dg++) {
                            dl(db, dq, de, dj)
                        }
                        if (dk) {
                            if (dp > 0) {
                                while (dh--) {
                                    if (!(db[dh] || dq[dh])) {
                                        dq[dh] = cE.call(di)
                                    }
                                }
                            }
                            dq = cv(dq)
                        }
                        cW.apply(di, dq);
                        if (dm && !dk && dq.length > 0 && (dp + c8.length) > 1) {
                            cQ.uniqueSort(di)
                        }
                    }
                    if (dm) {
                        cr = dc;
                        cJ = dn
                    }
                    return db
                };
            c7.el = 0;
            return e ? c1(c7) : c7
        }
        cd = cQ.compile = function (e, db) {
            var c8, c7 = [],
                da = [],
                c9 = cn[cZ][e + " "];
            if (!c9) {
                if (!db) {
                    db = b1(e)
                }
                c8 = db.length;
                while (c8--) {
                    c9 = cx(db[c8]);
                    if (c9[cZ]) {
                        c7.push(c9)
                    } else {
                        da.push(c9)
                    }
                }
                c9 = cn(e, b6(da, c7))
            }
            return c9
        };

        function cp(c7, da, c9) {
            var c8 = 0,
                e = da.length;
            for (; c8 < e; c8++) {
                cQ(c7, da[c8], c9)
            }
            return c9
        }

        function cY(c8, e, da, de, dd) {
            var db, dh, c7, dg, df, dc = b1(c8),
                c9 = dc.length;
            if (!de) {
                if (dc.length === 1) {
                    dh = dc[0] = dc[0].slice(0);
                    if (dh.length > 2 && (c7 = dh[0]).type === "ID" && e.nodeType === 9 && !dd && cL.relative[dh[1].type]) {
                        e = cL.find.ID(c7.matches[0].replace(ct, ""), e, dd)[0];
                        if (!e) {
                            return da
                        }
                        c8 = c8.slice(dh.shift().length)
                    }
                    for (db = cG.POS.test(c8) ? -1 : dh.length - 1; db >= 0; db--) {
                        c7 = dh[db];
                        if (cL.relative[(dg = c7.type)]) {
                            break
                        }
                        if ((df = cL.find[dg])) {
                            if ((de = df(c7.matches[0].replace(ct, ""), cR.test(dh[0].type) && e.parentNode || e, dd))) {
                                dh.splice(db, 1);
                                c8 = de.length && dh.join("");
                                if (!c8) {
                                    cW.apply(da, cl.call(de, 0));
                                    return da
                                }
                                break
                            }
                        }
                    }
                }
            }
            cd(c8, dc)(de, e, dd, da, cR.test(c8));
            return da
        }
        if (b8.querySelectorAll) {
            (function () {
                var db, dc = cY,
                    da = /'|\\/g,
                    c8 = /\=[\x20\t\r\n\f]*([^'"\]]*)[\x20\t\r\n\f]*\]/g,
                    c7 = [":focus"],
                    e = [":active"],
                    c9 = cb.matchesSelector || cb.mozMatchesSelector || cb.webkitMatchesSelector || cb.oMatchesSelector || cb.msMatchesSelector;
                cK(function (dd) {
                    dd.innerHTML = "<select><option selected=''></option></select>";
                    if (!dd.querySelectorAll("[selected]").length) {
                        c7.push("\\[" + cy + "*(?:checked|disabled|ismap|multiple|readonly|selected|value)")
                    }
                    if (!dd.querySelectorAll(":checked").length) {
                        c7.push(":checked")
                    }
                });
                cK(function (dd) {
                    dd.innerHTML = "<p test=''></p>";
                    if (dd.querySelectorAll("[test^='']").length) {
                        c7.push("[*^$]=" + cy + "*(?:\"\"|'')")
                    }
                    dd.innerHTML = "<input type='hidden'/>";
                    if (!dd.querySelectorAll(":enabled").length) {
                        c7.push(":enabled", ":disabled")
                    }
                });
                c7 = new RegExp(c7.join("|"));
                cY = function (dj, de, dl, dp, dn) {
                    if (!dp && !dn && !c7.test(dj)) {
                        var dh, dm, dg = true,
                            dd = cZ,
                            df = de,
                            dk = de.nodeType === 9 && dj;
                        if (de.nodeType === 1 && de.nodeName.toLowerCase() !== "object") {
                            dh = b1(dj);
                            if ((dg = de.getAttribute("id"))) {
                                dd = dg.replace(da, "\\$&")
                            } else {
                                de.setAttribute("id", dd)
                            }
                            dd = "[id='" + dd + "'] ";
                            dm = dh.length;
                            while (dm--) {
                                dh[dm] = dd + dh[dm].join("")
                            }
                            df = cR.test(dj) && de.parentNode || de;
                            dk = dh.join(",")
                        }
                        if (dk) {
                            try {
                                cW.apply(dl, cl.call(df.querySelectorAll(dk), 0));
                                return dl
                            } catch (di) {} finally {
                                if (!dg) {
                                    de.removeAttribute("id")
                                }
                            }
                        }
                    }
                    return dc(dj, de, dl, dp, dn)
                };
                if (c9) {
                    cK(function (de) {
                        db = c9.call(de, "div");
                        try {
                            c9.call(de, "[test!='']:sizzle");
                            e.push("!=", c6)
                        } catch (dd) {}
                    });
                    e = new RegExp(e.join("|"));
                    cQ.matchesSelector = function (de, dg) {
                        dg = dg.replace(c8, "='$1']");
                        if (!cm(de) && !e.test(dg) && !c7.test(dg)) {
                            try {
                                var dd = c9.call(de, dg);
                                if (dd || db || de.document && de.document.nodeType !== 11) {
                                    return dd
                                }
                            } catch (df) {}
                        }
                        return cQ(dg, null, null, [de]).length > 0
                    }
                }
            })()
        }
        cL.pseudos.nth = cL.pseudos.eq;

        function cj() {}
        cL.filters = cj.prototype = cL.pseudos;
        cL.setFilters = new cj();
        cQ.attr = bG.attr;
        bG.find = cQ;
        bG.expr = cQ.selectors;
        bG.expr[":"] = bG.expr.pseudos;
        bG.unique = cQ.uniqueSort;
        bG.text = cQ.getText;
        bG.isXMLDoc = cQ.isXML;
        bG.contains = cQ.contains
    })(a2);
    var ag = /Until$/,
        bq = /^(?:parents|prev(?:Until|All))/,
        al = /^.[^:#\[\.,]*$/,
        y = bG.expr.match.needsContext,
        bu = {
            children: true,
            contents: true,
            next: true,
            prev: true
        };
    bG.fn.extend({
        find: function (e) {
            var b2, bZ, b4, b5, b3, b1, b0 = this;
            if (typeof e !== "string") {
                return bG(e).filter(function () {
                    for (b2 = 0, bZ = b0.length; b2 < bZ; b2++) {
                        if (bG.contains(b0[b2], this)) {
                            return true
                        }
                    }
                })
            }
            b1 = this.pushStack("", "find", e);
            for (b2 = 0, bZ = this.length; b2 < bZ; b2++) {
                b4 = b1.length;
                bG.find(e, this[b2], b1);
                if (b2 > 0) {
                    for (b5 = b4; b5 < b1.length; b5++) {
                        for (b3 = 0; b3 < b4; b3++) {
                            if (b1[b3] === b1[b5]) {
                                b1.splice(b5--, 1);
                                break
                            }
                        }
                    }
                }
            }
            return b1
        },
        has: function (b1) {
            var b0, bZ = bG(b1, this),
                e = bZ.length;
            return this.filter(function () {
                for (b0 = 0; b0 < e; b0++) {
                    if (bG.contains(this, bZ[b0])) {
                        return true
                    }
                }
            })
        },
        not: function (e) {
            return this.pushStack(aM(this, e, false), "not", e)
        },
        filter: function (e) {
            return this.pushStack(aM(this, e, true), "filter", e)
        },
        is: function (e) {
            return !!e && (typeof e === "string" ? y.test(e) ? bG(e, this.context).index(this[0]) >= 0 : bG.filter(e, this).length > 0 : this.filter(e).length > 0)
        },
        closest: function (b2, b1) {
            var b3, b0 = 0,
                e = this.length,
                bZ = [],
                b4 = y.test(b2) || typeof b2 !== "string" ? bG(b2, b1 || this.context) : 0;
            for (; b0 < e; b0++) {
                b3 = this[b0];
                while (b3 && b3.ownerDocument && b3 !== b1 && b3.nodeType !== 11) {
                    if (b4 ? b4.index(b3) > -1 : bG.find.matchesSelector(b3, b2)) {
                        bZ.push(b3);
                        break
                    }
                    b3 = b3.parentNode
                }
            }
            bZ = bZ.length > 1 ? bG.unique(bZ) : bZ;
            return this.pushStack(bZ, "closest", b2)
        },
        index: function (e) {
            if (!e) {
                return (this[0] && this[0].parentNode) ? this.prevAll().length : -1
            }
            if (typeof e === "string") {
                return bG.inArray(this[0], bG(e))
            }
            return bG.inArray(e.jquery ? e[0] : e, this)
        },
        add: function (e, bZ) {
            var b1 = typeof e === "string" ? bG(e, bZ) : bG.makeArray(e && e.nodeType ? [e] : e),
                b0 = bG.merge(this.get(), b1);
            return this.pushStack(aR(b1[0]) || aR(b0[0]) ? b0 : bG.unique(b0))
        },
        addBack: function (e) {
            return this.add(e == null ? this.prevObject : this.prevObject.filter(e))
        }
    });
    bG.fn.andSelf = bG.fn.addBack;

    function aR(e) {
        return !e || !e.parentNode || e.parentNode.nodeType === 11
    }

    function aY(bZ, e) {
        do {
            bZ = bZ[e]
        } while (bZ && bZ.nodeType !== 1);
        return bZ
    }
    bG.each({
        parent: function (bZ) {
            var e = bZ.parentNode;
            return e && e.nodeType !== 11 ? e : null
        },
        parents: function (e) {
            return bG.dir(e, "parentNode")
        },
        parentsUntil: function (bZ, e, b0) {
            return bG.dir(bZ, "parentNode", b0)
        },
        next: function (e) {
            return aY(e, "nextSibling")
        },
        prev: function (e) {
            return aY(e, "previousSibling")
        },
        nextAll: function (e) {
            return bG.dir(e, "nextSibling")
        },
        prevAll: function (e) {
            return bG.dir(e, "previousSibling")
        },
        nextUntil: function (bZ, e, b0) {
            return bG.dir(bZ, "nextSibling", b0)
        },
        prevUntil: function (bZ, e, b0) {
            return bG.dir(bZ, "previousSibling", b0)
        },
        siblings: function (e) {
            return bG.sibling((e.parentNode || {}).firstChild, e)
        },
        children: function (e) {
            return bG.sibling(e.firstChild)
        },
        contents: function (e) {
            return bG.nodeName(e, "iframe") ? e.contentDocument || e.contentWindow.document : bG.merge([], e.childNodes)
        }
    }, function (e, bZ) {
        bG.fn[e] = function (b2, b0) {
            var b1 = bG.map(this, bZ, b2);
            if (!ag.test(e)) {
                b0 = b2
            }
            if (b0 && typeof b0 === "string") {
                b1 = bG.filter(b0, b1)
            }
            b1 = this.length > 1 && !bu[e] ? bG.unique(b1) : b1;
            if (this.length > 1 && bq.test(e)) {
                b1 = b1.reverse()
            }
            return this.pushStack(b1, e, a4.call(arguments).join(","))
        }
    });
    bG.extend({
        filter: function (b0, e, bZ) {
            if (bZ) {
                b0 = ":not(" + b0 + ")"
            }
            return e.length === 1 ? bG.find.matchesSelector(e[0], b0) ? [e[0]] : [] : bG.find.matches(b0, e)
        },
        dir: function (b0, bZ, b2) {
            var e = [],
                b1 = b0[bZ];
            while (b1 && b1.nodeType !== 9 && (b2 === aB || b1.nodeType !== 1 || !bG(b1).is(b2))) {
                if (b1.nodeType === 1) {
                    e.push(b1)
                }
                b1 = b1[bZ]
            }
            return e
        },
        sibling: function (b0, bZ) {
            var e = [];
            for (; b0; b0 = b0.nextSibling) {
                if (b0.nodeType === 1 && b0 !== bZ) {
                    e.push(b0)
                }
            }
            return e
        }
    });

    function aM(b1, b0, e) {
        b0 = b0 || 0;
        if (bG.isFunction(b0)) {
            return bG.grep(b1, function (b3, b2) {
                var b4 = !! b0.call(b3, b2, b3);
                return b4 === e
            })
        } else {
            if (b0.nodeType) {
                return bG.grep(b1, function (b3, b2) {
                    return (b3 === b0) === e
                })
            } else {
                if (typeof b0 === "string") {
                    var bZ = bG.grep(b1, function (b2) {
                        return b2.nodeType === 1
                    });
                    if (al.test(b0)) {
                        return bG.filter(b0, bZ, !e)
                    } else {
                        b0 = bG.filter(b0, bZ)
                    }
                }
            }
        }
        return bG.grep(b1, function (b3, b2) {
            return (bG.inArray(b3, b0) >= 0) === e
        })
    }

    function A(e) {
        var b0 = c.split("|"),
            bZ = e.createDocumentFragment();
        if (bZ.createElement) {
            while (b0.length) {
                bZ.createElement(b0.pop())
            }
        }
        return bZ
    }
    var c = "abbr|article|aside|audio|bdi|canvas|data|datalist|details|figcaption|figure|footer|header|hgroup|mark|meter|nav|output|progress|section|summary|time|video",
        av = / jQuery\d+="(?:null|\d+)"/g,
        bY = /^\s+/,
        ay = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/gi,
        p = /<([\w:]+)/,
        bT = /<tbody/i,
        J = /<|&#?\w+;/,
        aj = /<(?:script|style|link)/i,
        ap = /<(?:script|object|embed|option|style)/i,
        K = new RegExp("<(?:" + c + ")[\\s/>]", "i"),
        aE = /^(?:checkbox|radio)$/,
        bR = /checked\s*(?:[^=]|=\s*.checked.)/i,
        bw = /\/(java|ecma)script/i,
        aH = /^\s*<!(?:\[CDATA\[|\-\-)|[\]\-]{2}>\s*$/g,
        T = {
            option: [1, "<select multiple='multiple'>", "</select>"],
            legend: [1, "<fieldset>", "</fieldset>"],
            thead: [1, "<table>", "</table>"],
            tr: [2, "<table><tbody>", "</tbody></table>"],
            td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],
            col: [2, "<table><tbody></tbody><colgroup>", "</colgroup></table>"],
            area: [1, "<map>", "</map>"],
            _default: [0, "", ""]
        }, aQ = A(o),
        l = aQ.appendChild(o.createElement("div"));
    T.optgroup = T.option;
    T.tbody = T.tfoot = T.colgroup = T.caption = T.thead;
    T.th = T.td;
    if (!bG.support.htmlSerialize) {
        T._default = [1, "X<div>", "</div>"]
    }
    bG.fn.extend({
        text: function (e) {
            return bG.access(this, function (bZ) {
                return bZ === aB ? bG.text(this) : this.empty().append((this[0] && this[0].ownerDocument || o).createTextNode(bZ))
            }, null, e, arguments.length)
        },
        wrapAll: function (e) {
            if (bG.isFunction(e)) {
                return this.each(function (b0) {
                    bG(this).wrapAll(e.call(this, b0))
                })
            }
            if (this[0]) {
                var bZ = bG(e, this[0].ownerDocument).eq(0).clone(true);
                if (this[0].parentNode) {
                    bZ.insertBefore(this[0])
                }
                bZ.map(function () {
                    var b0 = this;
                    while (b0.firstChild && b0.firstChild.nodeType === 1) {
                        b0 = b0.firstChild
                    }
                    return b0
                }).append(this)
            }
            return this
        },
        wrapInner: function (e) {
            if (bG.isFunction(e)) {
                return this.each(function (bZ) {
                    bG(this).wrapInner(e.call(this, bZ))
                })
            }
            return this.each(function () {
                var bZ = bG(this),
                    b0 = bZ.contents();
                if (b0.length) {
                    b0.wrapAll(e)
                } else {
                    bZ.append(e)
                }
            })
        },
        wrap: function (e) {
            var bZ = bG.isFunction(e);
            return this.each(function (b0) {
                bG(this).wrapAll(bZ ? e.call(this, b0) : e)
            })
        },
        unwrap: function () {
            return this.parent().each(function () {
                if (!bG.nodeName(this, "body")) {
                    bG(this).replaceWith(this.childNodes)
                }
            }).end()
        },
        append: function () {
            return this.domManip(arguments, true, function (e) {
                if (this.nodeType === 1 || this.nodeType === 11) {
                    this.appendChild(e)
                }
            })
        },
        prepend: function () {
            return this.domManip(arguments, true, function (e) {
                if (this.nodeType === 1 || this.nodeType === 11) {
                    this.insertBefore(e, this.firstChild)
                }
            })
        },
        before: function () {
            if (!aR(this[0])) {
                return this.domManip(arguments, false, function (bZ) {
                    this.parentNode.insertBefore(bZ, this)
                })
            }
            if (arguments.length) {
                var e = bG.clean(arguments);
                return this.pushStack(bG.merge(e, this), "before", this.selector)
            }
        },
        after: function () {
            if (!aR(this[0])) {
                return this.domManip(arguments, false, function (bZ) {
                    this.parentNode.insertBefore(bZ, this.nextSibling)
                })
            }
            if (arguments.length) {
                var e = bG.clean(arguments);
                return this.pushStack(bG.merge(this, e), "after", this.selector)
            }
        },
        remove: function (e, b1) {
            var b0, bZ = 0;
            for (;
                (b0 = this[bZ]) != null; bZ++) {
                if (!e || bG.filter(e, [b0]).length) {
                    if (!b1 && b0.nodeType === 1) {
                        bG.cleanData(b0.getElementsByTagName("*"));
                        bG.cleanData([b0])
                    }
                    if (b0.parentNode) {
                        b0.parentNode.removeChild(b0)
                    }
                }
            }
            return this
        },
        empty: function () {
            var bZ, e = 0;
            for (;
                (bZ = this[e]) != null; e++) {
                if (bZ.nodeType === 1) {
                    bG.cleanData(bZ.getElementsByTagName("*"))
                }
                while (bZ.firstChild) {
                    bZ.removeChild(bZ.firstChild)
                }
            }
            return this
        },
        clone: function (bZ, e) {
            bZ = bZ == null ? false : bZ;
            e = e == null ? bZ : e;
            return this.map(function () {
                return bG.clone(this, bZ, e)
            })
        },
        html: function (e) {
            return bG.access(this, function (b2) {
                var b1 = this[0] || {}, b0 = 0,
                    bZ = this.length;
                if (b2 === aB) {
                    return b1.nodeType === 1 ? b1.innerHTML.replace(av, "") : aB
                }
                if (typeof b2 === "string" && !aj.test(b2) && (bG.support.htmlSerialize || !K.test(b2)) && (bG.support.leadingWhitespace || !bY.test(b2)) && !T[(p.exec(b2) || ["", ""])[1].toLowerCase()]) {
                    b2 = b2.replace(ay, "<$1></$2>");
                    try {
                        for (; b0 < bZ; b0++) {
                            b1 = this[b0] || {};
                            if (b1.nodeType === 1) {
                                bG.cleanData(b1.getElementsByTagName("*"));
                                b1.innerHTML = b2
                            }
                        }
                        b1 = 0
                    } catch (b3) {}
                }
                if (b1) {
                    this.empty().append(b2)
                }
            }, null, e, arguments.length)
        },
        replaceWith: function (e) {
            if (!aR(this[0])) {
                if (bG.isFunction(e)) {
                    return this.each(function (b1) {
                        var b0 = bG(this),
                            bZ = b0.html();
                        b0.replaceWith(e.call(this, b1, bZ))
                    })
                }
                if (typeof e !== "string") {
                    e = bG(e).detach()
                }
                return this.each(function () {
                    var b0 = this.nextSibling,
                        bZ = this.parentNode;
                    bG(this).remove();
                    if (b0) {
                        bG(b0).before(e)
                    } else {
                        bG(bZ).append(e)
                    }
                })
            }
            return this.length ? this.pushStack(bG(bG.isFunction(e) ? e() : e), "replaceWith", e) : this
        },
        detach: function (e) {
            return this.remove(e, true)
        },
        domManip: function (b4, b8, b7) {
            b4 = [].concat.apply([], b4);
            var b0, b2, b3, b6, b1 = 0,
                b5 = b4[0],
                bZ = [],
                e = this.length;
            if (!bG.support.checkClone && e > 1 && typeof b5 === "string" && bR.test(b5)) {
                return this.each(function () {
                    bG(this).domManip(b4, b8, b7)
                })
            }
            if (bG.isFunction(b5)) {
                return this.each(function (ca) {
                    var b9 = bG(this);
                    b4[0] = b5.call(this, ca, b8 ? b9.html() : aB);
                    b9.domManip(b4, b8, b7)
                })
            }
            if (this[0]) {
                b0 = bG.buildFragment(b4, this, bZ);
                b3 = b0.fragment;
                b2 = b3.firstChild;
                if (b3.childNodes.length === 1) {
                    b3 = b2
                }
                if (b2) {
                    b8 = b8 && bG.nodeName(b2, "tr");
                    for (b6 = b0.cacheable || e - 1; b1 < e; b1++) {
                        b7.call(b8 && bG.nodeName(this[b1], "table") ? x(this[b1], "tbody") : this[b1], b1 === b6 ? b3 : bG.clone(b3, true, true))
                    }
                }
                b3 = b2 = null;
                if (bZ.length) {
                    bG.each(bZ, function (b9, ca) {
                        if (ca.src) {
                            if (bG.ajax) {
                                bG.ajax({
                                    url: ca.src,
                                    type: "GET",
                                    dataType: "script",
                                    async: false,
                                    global: false,
                                    "throws": true
                                })
                            } else {
                                bG.error("no ajax")
                            }
                        } else {
                            bG.globalEval((ca.text || ca.textContent || ca.innerHTML || "").replace(aH, ""))
                        } if (ca.parentNode) {
                            ca.parentNode.removeChild(ca)
                        }
                    })
                }
            }
            return this
        }
    });

    function x(bZ, e) {
        return bZ.getElementsByTagName(e)[0] || bZ.appendChild(bZ.ownerDocument.createElement(e))
    }

    function ao(b5, bZ) {
        if (bZ.nodeType !== 1 || !bG.hasData(b5)) {
            return
        }
        var b2, b1, e, b4 = bG._data(b5),
            b3 = bG._data(bZ, b4),
            b0 = b4.events;
        if (b0) {
            delete b3.handle;
            b3.events = {};
            for (b2 in b0) {
                for (b1 = 0, e = b0[b2].length; b1 < e; b1++) {
                    bG.event.add(bZ, b2, b0[b2][b1])
                }
            }
        }
        if (b3.data) {
            b3.data = bG.extend({}, b3.data)
        }
    }

    function F(bZ, e) {
        var b0;
        if (e.nodeType !== 1) {
            return
        }
        if (e.clearAttributes) {
            e.clearAttributes()
        }
        if (e.mergeAttributes) {
            e.mergeAttributes(bZ)
        }
        b0 = e.nodeName.toLowerCase();
        if (b0 === "object") {
            if (e.parentNode) {
                e.outerHTML = bZ.outerHTML
            }
            if (bG.support.html5Clone && (bZ.innerHTML && !bG.trim(e.innerHTML))) {
                e.innerHTML = bZ.innerHTML
            }
        } else {
            if (b0 === "input" && aE.test(bZ.type)) {
                e.defaultChecked = e.checked = bZ.checked;
                if (e.value !== bZ.value) {
                    e.value = bZ.value
                }
            } else {
                if (b0 === "option") {
                    e.selected = bZ.defaultSelected
                } else {
                    if (b0 === "input" || b0 === "textarea") {
                        e.defaultValue = bZ.defaultValue
                    } else {
                        if (b0 === "script" && e.text !== bZ.text) {
                            e.text = bZ.text
                        }
                    }
                }
            }
        }
        e.removeAttribute(bG.expando)
    }
    bG.buildFragment = function (b1, b2, bZ) {
        var b0, e, b3, b4 = b1[0];
        b2 = b2 || o;
        b2 = !b2.nodeType && b2[0] || b2;
        b2 = b2.ownerDocument || b2;
        if (b1.length === 1 && typeof b4 === "string" && b4.length < 512 && b2 === o && b4.charAt(0) === "<" && !ap.test(b4) && (bG.support.checkClone || !bR.test(b4)) && (bG.support.html5Clone || !K.test(b4))) {
            e = true;
            b0 = bG.fragments[b4];
            b3 = b0 !== aB
        }
        if (!b0) {
            b0 = b2.createDocumentFragment();
            bG.clean(b1, b2, b0, bZ);
            if (e) {
                bG.fragments[b4] = b3 && b0
            }
        }
        return {
            fragment: b0,
            cacheable: e
        }
    };
    bG.fragments = {};
    bG.each({
        appendTo: "append",
        prependTo: "prepend",
        insertBefore: "before",
        insertAfter: "after",
        replaceAll: "replaceWith"
    }, function (e, bZ) {
        bG.fn[e] = function (b0) {
            var b2, b4 = 0,
                b3 = [],
                b6 = bG(b0),
                b1 = b6.length,
                b5 = this.length === 1 && this[0].parentNode;
            if ((b5 == null || b5 && b5.nodeType === 11 && b5.childNodes.length === 1) && b1 === 1) {
                b6[bZ](this[0]);
                return this
            } else {
                for (; b4 < b1; b4++) {
                    b2 = (b4 > 0 ? this.clone(true) : this).get();
                    bG(b6[b4])[bZ](b2);
                    b3 = b3.concat(b2)
                }
                return this.pushStack(b3, e, b6.selector)
            }
        }
    });

    function m(e) {
        if (typeof e.getElementsByTagName !== "undefined") {
            return e.getElementsByTagName("*")
        } else {
            if (typeof e.querySelectorAll !== "undefined") {
                return e.querySelectorAll("*")
            } else {
                return []
            }
        }
    }

    function bS(e) {
        if (aE.test(e.type)) {
            e.defaultChecked = e.checked
        }
    }
    bG.extend({
        clone: function (b2, b4, b0) {
            var e, bZ, b1, b3;
            if (bG.support.html5Clone || bG.isXMLDoc(b2) || !K.test("<" + b2.nodeName + ">")) {
                b3 = b2.cloneNode(true)
            } else {
                l.innerHTML = b2.outerHTML;
                l.removeChild(b3 = l.firstChild)
            } if ((!bG.support.noCloneEvent || !bG.support.noCloneChecked) && (b2.nodeType === 1 || b2.nodeType === 11) && !bG.isXMLDoc(b2)) {
                F(b2, b3);
                e = m(b2);
                bZ = m(b3);
                for (b1 = 0; e[b1]; ++b1) {
                    if (bZ[b1]) {
                        F(e[b1], bZ[b1])
                    }
                }
            }
            if (b4) {
                ao(b2, b3);
                if (b0) {
                    e = m(b2);
                    bZ = m(b3);
                    for (b1 = 0; e[b1]; ++b1) {
                        ao(e[b1], bZ[b1])
                    }
                }
            }
            e = bZ = null;
            return b3
        },
        clean: function (cb, b0, e, b1) {
            var b8, b7, ca, cf, b4, ce, b5, b2, bZ, b9, cd, b6, b3 = b0 === o && aQ,
                cc = [];
            if (!b0 || typeof b0.createDocumentFragment === "undefined") {
                b0 = o
            }
            for (b8 = 0;
                (ca = cb[b8]) != null; b8++) {
                if (typeof ca === "number") {
                    ca += ""
                }
                if (!ca) {
                    continue
                }
                if (typeof ca === "string") {
                    if (!J.test(ca)) {
                        ca = b0.createTextNode(ca)
                    } else {
                        b3 = b3 || A(b0);
                        b5 = b0.createElement("div");
                        b3.appendChild(b5);
                        ca = ca.replace(ay, "<$1></$2>");
                        cf = (p.exec(ca) || ["", ""])[1].toLowerCase();
                        b4 = T[cf] || T._default;
                        ce = b4[0];
                        b5.innerHTML = b4[1] + ca + b4[2];
                        while (ce--) {
                            b5 = b5.lastChild
                        }
                        if (!bG.support.tbody) {
                            b2 = bT.test(ca);
                            bZ = cf === "table" && !b2 ? b5.firstChild && b5.firstChild.childNodes : b4[1] === "<table>" && !b2 ? b5.childNodes : [];
                            for (b7 = bZ.length - 1; b7 >= 0; --b7) {
                                if (bG.nodeName(bZ[b7], "tbody") && !bZ[b7].childNodes.length) {
                                    bZ[b7].parentNode.removeChild(bZ[b7])
                                }
                            }
                        }
                        if (!bG.support.leadingWhitespace && bY.test(ca)) {
                            b5.insertBefore(b0.createTextNode(bY.exec(ca)[0]), b5.firstChild)
                        }
                        ca = b5.childNodes;
                        b5.parentNode.removeChild(b5)
                    }
                }
                if (ca.nodeType) {
                    cc.push(ca)
                } else {
                    bG.merge(cc, ca)
                }
            }
            if (b5) {
                ca = b5 = b3 = null
            }
            if (!bG.support.appendChecked) {
                for (b8 = 0;
                    (ca = cc[b8]) != null; b8++) {
                    if (bG.nodeName(ca, "input")) {
                        bS(ca)
                    } else {
                        if (typeof ca.getElementsByTagName !== "undefined") {
                            bG.grep(ca.getElementsByTagName("input"), bS)
                        }
                    }
                }
            }
            if (e) {
                cd = function (cg) {
                    if (!cg.type || bw.test(cg.type)) {
                        return b1 ? b1.push(cg.parentNode ? cg.parentNode.removeChild(cg) : cg) : e.appendChild(cg)
                    }
                };
                for (b8 = 0;
                    (ca = cc[b8]) != null; b8++) {
                    if (!(bG.nodeName(ca, "script") && cd(ca))) {
                        e.appendChild(ca);
                        if (typeof ca.getElementsByTagName !== "undefined") {
                            b6 = bG.grep(bG.merge([], ca.getElementsByTagName("script")), cd);
                            cc.splice.apply(cc, [b8 + 1, 0].concat(b6));
                            b8 += b6.length
                        }
                    }
                }
            }
            return cc
        },
        cleanData: function (bZ, b7) {
            var b2, b0, b1, b6, b3 = 0,
                b8 = bG.expando,
                e = bG.cache,
                b4 = bG.support.deleteExpando,
                b5 = bG.event.special;
            for (;
                (b1 = bZ[b3]) != null; b3++) {
                if (b7 || bG.acceptData(b1)) {
                    b0 = b1[b8];
                    b2 = b0 && e[b0];
                    if (b2) {
                        if (b2.events) {
                            for (b6 in b2.events) {
                                if (b5[b6]) {
                                    bG.event.remove(b1, b6)
                                } else {
                                    bG.removeEvent(b1, b6, b2.handle)
                                }
                            }
                        }
                        if (e[b0]) {
                            delete e[b0];
                            if (b4) {
                                delete b1[b8]
                            } else {
                                if (b1.removeAttribute) {
                                    b1.removeAttribute(b8)
                                } else {
                                    b1[b8] = null
                                }
                            }
                            bG.deletedIds.push(b0)
                        }
                    }
                }
            }
        }
    });
    (function () {
        var e, bZ;
        bG.uaMatch = function (b1) {
            b1 = b1.toLowerCase();
            var b0 = /(chrome)[ \/]([\w.]+)/.exec(b1) || /(webkit)[ \/]([\w.]+)/.exec(b1) || /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(b1) || /(msie) ([\w.]+)/.exec(b1) || b1.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(b1) || [];
            return {
                browser: b0[1] || "",
                version: b0[2] || "0"
            }
        };
        e = bG.uaMatch(d.userAgent);
        bZ = {};
        if (e.browser) {
            bZ[e.browser] = true;
            bZ.version = e.version
        }
        if (bZ.chrome) {
            bZ.webkit = true
        } else {
            if (bZ.webkit) {
                bZ.safari = true
            }
        }
        bG.browser = bZ;
        bG.sub = function () {
            function b0(b3, b4) {
                return new b0.fn.init(b3, b4)
            }
            bG.extend(true, b0, this);
            b0.superclass = this;
            b0.fn = b0.prototype = this();
            b0.fn.constructor = b0;
            b0.sub = this.sub;
            b0.fn.init = function b2(b3, b4) {
                if (b4 && b4 instanceof bG && !(b4 instanceof b0)) {
                    b4 = b0(b4)
                }
                return bG.fn.init.call(this, b3, b4, b1)
            };
            b0.fn.init.prototype = b0.fn;
            var b1 = b0(o);
            return b0
        }
    })();
    var E, az, aW, be = /alpha\([^)]*\)/i,
        aS = /opacity=([^)]*)/,
        bk = /^(top|right|bottom|left)$/,
        G = /^(none|table(?!-c[ea]).+)/,
        aZ = /^margin/,
        a8 = new RegExp("^(" + bx + ")(.*)$", "i"),
        W = new RegExp("^(" + bx + ")(?!px)[a-z%]+$", "i"),
        S = new RegExp("^([-+])=(" + bx + ")", "i"),
        bh = {
            BODY: "block"
        }, a9 = {
            position: "absolute",
            visibility: "hidden",
            display: "block"
        }, bA = {
            letterSpacing: 0,
            fontWeight: 400
        }, bQ = ["Top", "Right", "Bottom", "Left"],
        ar = ["Webkit", "O", "Moz", "ms"],
        aJ = bG.fn.toggle;

    function b(b1, bZ) {
        if (bZ in b1) {
            return bZ
        }
        var b2 = bZ.charAt(0).toUpperCase() + bZ.slice(1),
            e = bZ,
            b0 = ar.length;
        while (b0--) {
            bZ = ar[b0] + b2;
            if (bZ in b1) {
                return bZ
            }
        }
        return e
    }

    function Q(bZ, e) {
        bZ = e || bZ;
        return bG.css(bZ, "display") === "none" || !bG.contains(bZ.ownerDocument, bZ)
    }

    function s(b3, e) {
        var b2, b4, bZ = [],
            b0 = 0,
            b1 = b3.length;
        for (; b0 < b1; b0++) {
            b2 = b3[b0];
            if (!b2.style) {
                continue
            }
            bZ[b0] = bG._data(b2, "olddisplay");
            if (e) {
                if (!bZ[b0] && b2.style.display === "none") {
                    b2.style.display = ""
                }
                if (b2.style.display === "" && Q(b2)) {
                    bZ[b0] = bG._data(b2, "olddisplay", bC(b2.nodeName))
                }
            } else {
                b4 = E(b2, "display");
                if (!bZ[b0] && b4 !== "none") {
                    bG._data(b2, "olddisplay", b4)
                }
            }
        }
        for (b0 = 0; b0 < b1; b0++) {
            b2 = b3[b0];
            if (!b2.style) {
                continue
            }
            if (!e || b2.style.display === "none" || b2.style.display === "") {
                b2.style.display = e ? bZ[b0] || "" : "none"
            }
        }
        return b3
    }
    bG.fn.extend({
        css: function (e, bZ) {
            return bG.access(this, function (b1, b0, b2) {
                return b2 !== aB ? bG.style(b1, b0, b2) : bG.css(b1, b0)
            }, e, bZ, arguments.length > 1)
        },
        show: function () {
            return s(this, true)
        },
        hide: function () {
            return s(this)
        },
        toggle: function (b0, bZ) {
            var e = typeof b0 === "boolean";
            if (bG.isFunction(b0) && bG.isFunction(bZ)) {
                return aJ.apply(this, arguments)
            }
            return this.each(function () {
                if (e ? b0 : Q(this)) {
                    bG(this).show()
                } else {
                    bG(this).hide()
                }
            })
        }
    });
    bG.extend({
        cssHooks: {
            opacity: {
                get: function (b0, bZ) {
                    if (bZ) {
                        var e = E(b0, "opacity");
                        return e === "" ? "1" : e
                    }
                }
            }
        },
        cssNumber: {
            fillOpacity: true,
            fontWeight: true,
            lineHeight: true,
            opacity: true,
            orphans: true,
            widows: true,
            zIndex: true,
            zoom: true
        },
        cssProps: {
            "float": bG.support.cssFloat ? "cssFloat" : "styleFloat"
        },
        style: function (b1, b0, b7, b2) {
            if (!b1 || b1.nodeType === 3 || b1.nodeType === 8 || !b1.style) {
                return
            }
            var b5, b6, b8, b3 = bG.camelCase(b0),
                bZ = b1.style;
            b0 = bG.cssProps[b3] || (bG.cssProps[b3] = b(bZ, b3));
            b8 = bG.cssHooks[b0] || bG.cssHooks[b3];
            if (b7 !== aB) {
                b6 = typeof b7;
                if (b6 === "string" && (b5 = S.exec(b7))) {
                    b7 = (b5[1] + 1) * b5[2] + parseFloat(bG.css(b1, b0));
                    b6 = "number"
                }
                if (b7 == null || b6 === "number" && isNaN(b7)) {
                    return
                }
                if (b6 === "number" && !bG.cssNumber[b3]) {
                    b7 += "px"
                }
                if (!b8 || !("set" in b8) || (b7 = b8.set(b1, b7, b2)) !== aB) {
                    try {
                        bZ[b0] = b7
                    } catch (b4) {}
                }
            } else {
                if (b8 && "get" in b8 && (b5 = b8.get(b1, false, b2)) !== aB) {
                    return b5
                }
                return bZ[b0]
            }
        },
        css: function (b4, b2, b3, bZ) {
            var b5, b1, e, b0 = bG.camelCase(b2);
            b2 = bG.cssProps[b0] || (bG.cssProps[b0] = b(b4.style, b0));
            e = bG.cssHooks[b2] || bG.cssHooks[b0];
            if (e && "get" in e) {
                b5 = e.get(b4, true, bZ)
            }
            if (b5 === aB) {
                b5 = E(b4, b2)
            }
            if (b5 === "normal" && b2 in bA) {
                b5 = bA[b2]
            }
            if (b3 || bZ !== aB) {
                b1 = parseFloat(b5);
                return b3 || bG.isNumeric(b1) ? b1 || 0 : b5
            }
            return b5
        },
        swap: function (b2, b1, b3) {
            var b0, bZ, e = {};
            for (bZ in b1) {
                e[bZ] = b2.style[bZ];
                b2.style[bZ] = b1[bZ]
            }
            b0 = b3.call(b2);
            for (bZ in b1) {
                b2.style[bZ] = e[bZ]
            }
            return b0
        }
    });
    if (a2.getComputedStyle) {
        E = function (b5, bZ) {
            var e, b2, b1, b4, b3 = a2.getComputedStyle(b5, null),
                b0 = b5.style;
            if (b3) {
                e = b3.getPropertyValue(bZ) || b3[bZ];
                if (e === "" && !bG.contains(b5.ownerDocument, b5)) {
                    e = bG.style(b5, bZ)
                }
                if (W.test(e) && aZ.test(bZ)) {
                    b2 = b0.width;
                    b1 = b0.minWidth;
                    b4 = b0.maxWidth;
                    b0.minWidth = b0.maxWidth = b0.width = e;
                    e = b3.width;
                    b0.width = b2;
                    b0.minWidth = b1;
                    b0.maxWidth = b4
                }
            }
            return e
        }
    } else {
        if (o.documentElement.currentStyle) {
            E = function (b2, b0) {
                var b3, e, bZ = b2.currentStyle && b2.currentStyle[b0],
                    b1 = b2.style;
                if (bZ == null && b1 && b1[b0]) {
                    bZ = b1[b0]
                }
                if (W.test(bZ) && !bk.test(b0)) {
                    b3 = b1.left;
                    e = b2.runtimeStyle && b2.runtimeStyle.left;
                    if (e) {
                        b2.runtimeStyle.left = b2.currentStyle.left
                    }
                    b1.left = b0 === "fontSize" ? "1em" : bZ;
                    bZ = b1.pixelLeft + "px";
                    b1.left = b3;
                    if (e) {
                        b2.runtimeStyle.left = e
                    }
                }
                return bZ === "" ? "auto" : bZ
            }
        }
    }

    function aG(e, b0, b1) {
        var bZ = a8.exec(b0);
        return bZ ? Math.max(0, bZ[1] - (b1 || 0)) + (bZ[2] || "px") : b0
    }

    function at(b1, bZ, e, b3) {
        var b0 = e === (b3 ? "border" : "content") ? 4 : bZ === "width" ? 1 : 0,
            b2 = 0;
        for (; b0 < 4; b0 += 2) {
            if (e === "margin") {
                b2 += bG.css(b1, e + bQ[b0], true)
            }
            if (b3) {
                if (e === "content") {
                    b2 -= parseFloat(E(b1, "padding" + bQ[b0])) || 0
                }
                if (e !== "margin") {
                    b2 -= parseFloat(E(b1, "border" + bQ[b0] + "Width")) || 0
                }
            } else {
                b2 += parseFloat(E(b1, "padding" + bQ[b0])) || 0;
                if (e !== "padding") {
                    b2 += parseFloat(E(b1, "border" + bQ[b0] + "Width")) || 0
                }
            }
        }
        return b2
    }

    function u(b1, bZ, e) {
        var b2 = bZ === "width" ? b1.offsetWidth : b1.offsetHeight,
            b0 = true,
            b3 = bG.support.boxSizing && bG.css(b1, "boxSizing") === "border-box";
        if (b2 <= 0 || b2 == null) {
            b2 = E(b1, bZ);
            if (b2 < 0 || b2 == null) {
                b2 = b1.style[bZ]
            }
            if (W.test(b2)) {
                return b2
            }
            b0 = b3 && (bG.support.boxSizingReliable || b2 === b1.style[bZ]);
            b2 = parseFloat(b2) || 0
        }
        return (b2 + at(b1, bZ, e || (b3 ? "border" : "content"), b0)) + "px"
    }

    function bC(b0) {
        if (bh[b0]) {
            return bh[b0]
        }
        var e = bG("<" + b0 + ">").appendTo(o.body),
            bZ = e.css("display");
        e.remove();
        if (bZ === "none" || bZ === "") {
            az = o.body.appendChild(az || bG.extend(o.createElement("iframe"), {
                frameBorder: 0,
                width: 0,
                height: 0
            }));
            if (!aW || !az.createElement) {
                aW = (az.contentWindow || az.contentDocument).document;
                aW.write("<!doctype html><html><body>");
                aW.close()
            }
            e = aW.body.appendChild(aW.createElement(b0));
            bZ = E(e, "display");
            o.body.removeChild(az)
        }
        bh[b0] = bZ;
        return bZ
    }
    bG.each(["height", "width"], function (bZ, e) {
        bG.cssHooks[e] = {
            get: function (b2, b1, b0) {
                if (b1) {
                    if (b2.offsetWidth === 0 && G.test(E(b2, "display"))) {
                        return bG.swap(b2, a9, function () {
                            return u(b2, e, b0)
                        })
                    } else {
                        return u(b2, e, b0)
                    }
                }
            },
            set: function (b1, b2, b0) {
                return aG(b1, b2, b0 ? at(b1, e, b0, bG.support.boxSizing && bG.css(b1, "boxSizing") === "border-box") : 0)
            }
        }
    });
    if (!bG.support.opacity) {
        bG.cssHooks.opacity = {
            get: function (bZ, e) {
                return aS.test((e && bZ.currentStyle ? bZ.currentStyle.filter : bZ.style.filter) || "") ? (0.01 * parseFloat(RegExp.$1)) + "" : e ? "1" : ""
            },
            set: function (b2, b3) {
                var b1 = b2.style,
                    bZ = b2.currentStyle,
                    e = bG.isNumeric(b3) ? "alpha(opacity=" + b3 * 100 + ")" : "",
                    b0 = bZ && bZ.filter || b1.filter || "";
                b1.zoom = 1;
                if (b3 >= 1 && bG.trim(b0.replace(be, "")) === "" && b1.removeAttribute) {
                    b1.removeAttribute("filter");
                    if (bZ && !bZ.filter) {
                        return
                    }
                }
                b1.filter = be.test(b0) ? b0.replace(be, e) : b0 + " " + e
            }
        }
    }
    bG(function () {
        if (!bG.support.reliableMarginRight) {
            bG.cssHooks.marginRight = {
                get: function (bZ, e) {
                    return bG.swap(bZ, {
                        display: "inline-block"
                    }, function () {
                        if (e) {
                            return E(bZ, "marginRight")
                        }
                    })
                }
            }
        }
        if (!bG.support.pixelPosition && bG.fn.position) {
            bG.each(["top", "left"], function (e, bZ) {
                bG.cssHooks[bZ] = {
                    get: function (b2, b1) {
                        if (b1) {
                            var b0 = E(b2, bZ);
                            return W.test(b0) ? bG(b2).position()[bZ] + "px" : b0
                        }
                    }
                }
            })
        }
    });
    if (bG.expr && bG.expr.filters) {
        bG.expr.filters.hidden = function (e) {
            return (e.offsetWidth === 0 && e.offsetHeight === 0) || (!bG.support.reliableHiddenOffsets && ((e.style && e.style.display) || E(e, "display")) === "none")
        };
        bG.expr.filters.visible = function (e) {
            return !bG.expr.filters.hidden(e)
        }
    }
    bG.each({
        margin: "",
        padding: "",
        border: "Width"
    }, function (e, bZ) {
        bG.cssHooks[e + bZ] = {
            expand: function (b2) {
                var b1, b3 = typeof b2 === "string" ? b2.split(" ") : [b2],
                    b0 = {};
                for (b1 = 0; b1 < 4; b1++) {
                    b0[e + bQ[b1] + bZ] = b3[b1] || b3[b1 - 2] || b3[0]
                }
                return b0
            }
        };
        if (!aZ.test(e)) {
            bG.cssHooks[e + bZ].set = aG
        }
    });
    var bs = /%20/g,
        aP = /\[\]$/,
        U = /\r?\n/g,
        bz = /^(?:color|date|datetime|datetime-local|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i,
        aD = /^(?:select|textarea)/i;
    bG.fn.extend({
        serialize: function () {
            return bG.param(this.serializeArray())
        },
        serializeArray: function () {
            return this.map(function () {
                return this.elements ? bG.makeArray(this.elements) : this
            }).filter(function () {
                return this.name && !this.disabled && (this.checked || aD.test(this.nodeName) || bz.test(this.type))
            }).map(function (e, bZ) {
                var b0 = bG(this).val();
                return b0 == null ? null : bG.isArray(b0) ? bG.map(b0, function (b2, b1) {
                    return {
                        name: bZ.name,
                        value: b2.replace(U, "\r\n")
                    }
                }) : {
                    name: bZ.name,
                    value: b0.replace(U, "\r\n")
                }
            }).get()
        }
    });
    bG.param = function (e, b0) {
        var b1, bZ = [],
            b2 = function (b3, b4) {
                b4 = bG.isFunction(b4) ? b4() : (b4 == null ? "" : b4);
                bZ[bZ.length] = encodeURIComponent(b3) + "=" + encodeURIComponent(b4)
            };
        if (b0 === aB) {
            b0 = bG.ajaxSettings && bG.ajaxSettings.traditional
        }
        if (bG.isArray(e) || (e.jquery && !bG.isPlainObject(e))) {
            bG.each(e, function () {
                b2(this.name, this.value)
            })
        } else {
            for (b1 in e) {
                k(b1, e[b1], b0, b2)
            }
        }
        return bZ.join("&").replace(bs, "+")
    };

    function k(b0, b2, bZ, b1) {
        var e;
        if (bG.isArray(b2)) {
            bG.each(b2, function (b4, b3) {
                if (bZ || aP.test(b0)) {
                    b1(b0, b3)
                } else {
                    k(b0 + "[" + (typeof b3 === "object" ? b4 : "") + "]", b3, bZ, b1)
                }
            })
        } else {
            if (!bZ && bG.type(b2) === "object") {
                for (e in b2) {
                    k(b0 + "[" + e + "]", b2[e], bZ, b1)
                }
            } else {
                b1(b0, b2)
            }
        }
    }
    var bX, Y, an = /#.*$/,
        ad = /^(.*?):[ \t]*([^\r\n]*)\r?$/mg,
        B = /^(?:about|app|app\-storage|.+\-extension|file|res|widget):$/,
        r = /^(?:GET|HEAD)$/,
        aC = /^\/\//,
        bN = /\?/,
        g = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
        P = /([?&])_=[^&]*/,
        aT = /^([\w\+\.\-]+:)(?:\/\/([^\/?#:]*)(?::(\d+)|)|)/,
        bW = bG.fn.load,
        v = {}, a6 = {}, aX = ["*/"] + ["*"];
    try {
        Y = aI.href
    } catch (bd) {
        Y = o.createElement("a");
        Y.href = "";
        Y = Y.href
    }
    bX = aT.exec(Y.toLowerCase()) || [];

    function bI(e) {
        return function (b2, b4) {
            if (typeof b2 !== "string") {
                b4 = b2;
                b2 = "*"
            }
            var bZ, b5, b6, b1 = b2.toLowerCase().split(aV),
                b0 = 0,
                b3 = b1.length;
            if (bG.isFunction(b4)) {
                for (; b0 < b3; b0++) {
                    bZ = b1[b0];
                    b6 = /^\+/.test(bZ);
                    if (b6) {
                        bZ = bZ.substr(1) || "*"
                    }
                    b5 = e[bZ] = e[bZ] || [];
                    b5[b6 ? "unshift" : "push"](b4)
                }
            }
        }
    }

    function q(bZ, b8, b3, b6, b5, b1) {
        b5 = b5 || b8.dataTypes[0];
        b1 = b1 || {};
        b1[b5] = true;
        var b7, b4 = bZ[b5],
            b0 = 0,
            e = b4 ? b4.length : 0,
            b2 = (bZ === v);
        for (; b0 < e && (b2 || !b7); b0++) {
            b7 = b4[b0](b8, b3, b6);
            if (typeof b7 === "string") {
                if (!b2 || b1[b7]) {
                    b7 = aB
                } else {
                    b8.dataTypes.unshift(b7);
                    b7 = q(bZ, b8, b3, b6, b7, b1)
                }
            }
        }
        if ((b2 || !b7) && !b1["*"]) {
            b7 = q(bZ, b8, b3, b6, "*", b1)
        }
        return b7
    }

    function t(b0, b1) {
        var bZ, e, b2 = bG.ajaxSettings.flatOptions || {};
        for (bZ in b1) {
            if (b1[bZ] !== aB) {
                (b2[bZ] ? b0 : (e || (e = {})))[bZ] = b1[bZ]
            }
        }
        if (e) {
            bG.extend(true, b0, e)
        }
    }
    bG.fn.load = function (b1, b4, b5) {
        if (typeof b1 !== "string" && bW) {
            return bW.apply(this, arguments)
        }
        if (!this.length) {
            return this
        }
        var e, b2, b0, bZ = this,
            b3 = b1.indexOf(" ");
        if (b3 >= 0) {
            e = b1.slice(b3, b1.length);
            b1 = b1.slice(0, b3)
        }
        if (bG.isFunction(b4)) {
            b5 = b4;
            b4 = aB
        } else {
            if (b4 && typeof b4 === "object") {
                b2 = "POST"
            }
        }
        bG.ajax({
            url: b1,
            type: b2,
            dataType: "html",
            data: b4,
            complete: function (b7, b6) {
                if (b5) {
                    bZ.each(b5, b0 || [b7.responseText, b6, b7])
                }
            }
        }).done(function (b6) {
            b0 = arguments;
            bZ.html(e ? bG("<div>").append(b6.replace(g, "")).find(e) : b6)
        });
        return this
    };
    bG.each("ajaxStart ajaxStop ajaxComplete ajaxError ajaxSuccess ajaxSend".split(" "), function (e, bZ) {
        bG.fn[bZ] = function (b0) {
            return this.on(bZ, b0)
        }
    });
    bG.each(["get", "post"], function (e, bZ) {
        bG[bZ] = function (b0, b2, b3, b1) {
            if (bG.isFunction(b2)) {
                b1 = b1 || b3;
                b3 = b2;
                b2 = aB
            }
            return bG.ajax({
                type: bZ,
                url: b0,
                data: b2,
                success: b3,
                dataType: b1
            })
        }
    });
    bG.extend({
        getScript: function (e, bZ) {
            return bG.get(e, aB, bZ, "script")
        },
        getJSON: function (e, bZ, b0) {
            return bG.get(e, bZ, b0, "json")
        },
        ajaxSetup: function (bZ, e) {
            if (e) {
                t(bZ, bG.ajaxSettings)
            } else {
                e = bZ;
                bZ = bG.ajaxSettings
            }
            t(bZ, e);
            return bZ
        },
        ajaxSettings: {
            url: Y,
            isLocal: B.test(bX[1]),
            global: true,
            type: "GET",
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            processData: true,
            async: true,
            accepts: {
                xml: "application/xml, text/xml",
                html: "text/html",
                text: "text/plain",
                json: "application/json, text/javascript",
                "*": aX
            },
            contents: {
                xml: /xml/,
                html: /html/,
                json: /json/
            },
            responseFields: {
                xml: "responseXML",
                text: "responseText"
            },
            converters: {
                "* text": a2.String,
                "text html": true,
                "text json": bG.parseJSON,
                "text xml": bG.parseXML
            },
            flatOptions: {
                context: true,
                url: true
            }
        },
        ajaxPrefilter: bI(v),
        ajaxTransport: bI(a6),
        ajax: function (b4, b1) {
            if (typeof b4 === "object") {
                b1 = b4;
                b4 = aB
            }
            b1 = b1 || {};
            var b7, cl, b2, cg, b9, cd, b0, cf, b8 = bG.ajaxSetup({}, b1),
                cn = b8.context || b8,
                cb = cn !== b8 && (cn.nodeType || cn instanceof bG) ? bG(cn) : bG.event,
                cm = bG.Deferred(),
                ci = bG.Callbacks("once memory"),
                b5 = b8.statusCode || {}, cc = {}, cj = {}, b3 = 0,
                b6 = "canceled",
                ce = {
                    readyState: 0,
                    setRequestHeader: function (co, cp) {
                        if (!b3) {
                            var e = co.toLowerCase();
                            co = cj[e] = cj[e] || co;
                            cc[co] = cp
                        }
                        return this
                    },
                    getAllResponseHeaders: function () {
                        return b3 === 2 ? cl : null
                    },
                    getResponseHeader: function (co) {
                        var e;
                        if (b3 === 2) {
                            if (!b2) {
                                b2 = {};
                                while ((e = ad.exec(cl))) {
                                    b2[e[1].toLowerCase()] = e[2]
                                }
                            }
                            e = b2[co.toLowerCase()]
                        }
                        return e === aB ? null : e
                    },
                    overrideMimeType: function (e) {
                        if (!b3) {
                            b8.mimeType = e
                        }
                        return this
                    },
                    abort: function (e) {
                        e = e || b6;
                        if (cg) {
                            cg.abort(e)
                        }
                        ca(0, e);
                        return this
                    }
                };

            function ca(cs, co, ct, cq) {
                var e, cw, cu, cr, cv, cp = co;
                if (b3 === 2) {
                    return
                }
                b3 = 2;
                if (b9) {
                    clearTimeout(b9)
                }
                cg = aB;
                cl = cq || "";
                ce.readyState = cs > 0 ? 4 : 0;
                if (ct) {
                    cr = h(b8, ce, ct)
                }
                if (cs >= 200 && cs < 300 || cs === 304) {
                    if (b8.ifModified) {
                        cv = ce.getResponseHeader("Last-Modified");
                        if (cv) {
                            bG.lastModified[b7] = cv
                        }
                        cv = ce.getResponseHeader("Etag");
                        if (cv) {
                            bG.etag[b7] = cv
                        }
                    }
                    if (cs === 304) {
                        cp = "notmodified";
                        e = true
                    } else {
                        e = ae(b8, cr);
                        cp = e.state;
                        cw = e.data;
                        cu = e.error;
                        e = !cu
                    }
                } else {
                    cu = cp;
                    if (!cp || cs) {
                        cp = "error";
                        if (cs < 0) {
                            cs = 0
                        }
                    }
                }
                ce.status = cs;
                ce.statusText = (co || cp) + "";
                if (e) {
                    cm.resolveWith(cn, [cw, cp, ce])
                } else {
                    cm.rejectWith(cn, [ce, cp, cu])
                }
                ce.statusCode(b5);
                b5 = aB;
                if (b0) {
                    cb.trigger("ajax" + (e ? "Success" : "Error"), [ce, b8, e ? cw : cu])
                }
                ci.fireWith(cn, [ce, cp]);
                if (b0) {
                    cb.trigger("ajaxComplete", [ce, b8]);
                    if (!(--bG.active)) {
                        bG.event.trigger("ajaxStop")
                    }
                }
            }
            cm.promise(ce);
            ce.success = ce.done;
            ce.error = ce.fail;
            ce.complete = ci.add;
            ce.statusCode = function (co) {
                if (co) {
                    var e;
                    if (b3 < 2) {
                        for (e in co) {
                            b5[e] = [b5[e], co[e]]
                        }
                    } else {
                        e = co[ce.status];
                        ce.always(e)
                    }
                }
                return this
            };
            b8.url = ((b4 || b8.url) + "").replace(an, "").replace(aC, bX[1] + "//");
            b8.dataTypes = bG.trim(b8.dataType || "*").toLowerCase().split(aV);
            if (b8.crossDomain == null) {
                cd = aT.exec(b8.url.toLowerCase());
                b8.crossDomain = !! (cd && (cd[1] !== bX[1] || cd[2] !== bX[2] || (cd[3] || (cd[1] === "http:" ? 80 : 443)) != (bX[3] || (bX[1] === "http:" ? 80 : 443))))
            }
            if (b8.data && b8.processData && typeof b8.data !== "string") {
                b8.data = bG.param(b8.data, b8.traditional)
            }
            q(v, b8, b1, ce);
            if (b3 === 2) {
                return ce
            }
            b0 = b8.global;
            b8.type = b8.type.toUpperCase();
            b8.hasContent = !r.test(b8.type);
            if (b0 && bG.active++ === 0) {
                bG.event.trigger("ajaxStart")
            }
            if (!b8.hasContent) {
                if (b8.data) {
                    b8.url += (bN.test(b8.url) ? "&" : "?") + b8.data;
                    delete b8.data
                }
                b7 = b8.url;
                if (b8.cache === false) {
                    var bZ = bG.now(),
                        ck = b8.url.replace(P, "$1_=" + bZ);
                    b8.url = ck + ((ck === b8.url) ? (bN.test(b8.url) ? "&" : "?") + "_=" + bZ : "")
                }
            }
            if (b8.data && b8.hasContent && b8.contentType !== false || b1.contentType) {
                ce.setRequestHeader("Content-Type", b8.contentType)
            }
            if (b8.ifModified) {
                b7 = b7 || b8.url;
                if (bG.lastModified[b7]) {
                    ce.setRequestHeader("If-Modified-Since", bG.lastModified[b7])
                }
                if (bG.etag[b7]) {
                    ce.setRequestHeader("If-None-Match", bG.etag[b7])
                }
            }
            ce.setRequestHeader("Accept", b8.dataTypes[0] && b8.accepts[b8.dataTypes[0]] ? b8.accepts[b8.dataTypes[0]] + (b8.dataTypes[0] !== "*" ? ", " + aX + "; q=0.01" : "") : b8.accepts["*"]);
            for (cf in b8.headers) {
                ce.setRequestHeader(cf, b8.headers[cf])
            }
            if (b8.beforeSend && (b8.beforeSend.call(cn, ce, b8) === false || b3 === 2)) {
                return ce.abort()
            }
            b6 = "abort";
            for (cf in {
                success: 1,
                error: 1,
                complete: 1
            }) {
                ce[cf](b8[cf])
            }
            cg = q(a6, b8, b1, ce);
            if (!cg) {
                ca(-1, "No Transport")
            } else {
                ce.readyState = 1;
                if (b0) {
                    cb.trigger("ajaxSend", [ce, b8])
                }
                if (b8.async && b8.timeout > 0) {
                    b9 = setTimeout(function () {
                        ce.abort("timeout")
                    }, b8.timeout)
                }
                try {
                    b3 = 1;
                    cg.send(cc, ca)
                } catch (ch) {
                    if (b3 < 2) {
                        ca(-1, ch)
                    } else {
                        throw ch
                    }
                }
            }
            return ce
        },
        active: 0,
        lastModified: {},
        etag: {}
    });

    function h(b7, b6, b3) {
        var b2, b4, b1, e, bZ = b7.contents,
            b5 = b7.dataTypes,
            b0 = b7.responseFields;
        for (b4 in b0) {
            if (b4 in b3) {
                b6[b0[b4]] = b3[b4]
            }
        }
        while (b5[0] === "*") {
            b5.shift();
            if (b2 === aB) {
                b2 = b7.mimeType || b6.getResponseHeader("content-type")
            }
        }
        if (b2) {
            for (b4 in bZ) {
                if (bZ[b4] && bZ[b4].test(b2)) {
                    b5.unshift(b4);
                    break
                }
            }
        }
        if (b5[0] in b3) {
            b1 = b5[0]
        } else {
            for (b4 in b3) {
                if (!b5[0] || b7.converters[b4 + " " + b5[0]]) {
                    b1 = b4;
                    break
                }
                if (!e) {
                    e = b4
                }
            }
            b1 = b1 || e
        } if (b1) {
            if (b1 !== b5[0]) {
                b5.unshift(b1)
            }
            return b3[b1]
        }
    }

    function ae(b9, b1) {
        var b7, bZ, b5, b3, b6 = b9.dataTypes.slice(),
            b0 = b6[0],
            b8 = {}, b2 = 0;
        if (b9.dataFilter) {
            b1 = b9.dataFilter(b1, b9.dataType)
        }
        if (b6[1]) {
            for (b7 in b9.converters) {
                b8[b7.toLowerCase()] = b9.converters[b7]
            }
        }
        for (;
            (b5 = b6[++b2]);) {
            if (b5 !== "*") {
                if (b0 !== "*" && b0 !== b5) {
                    b7 = b8[b0 + " " + b5] || b8["* " + b5];
                    if (!b7) {
                        for (bZ in b8) {
                            b3 = bZ.split(" ");
                            if (b3[1] === b5) {
                                b7 = b8[b0 + " " + b3[0]] || b8["* " + b3[0]];
                                if (b7) {
                                    if (b7 === true) {
                                        b7 = b8[bZ]
                                    } else {
                                        if (b8[bZ] !== true) {
                                            b5 = b3[0];
                                            b6.splice(b2--, 0, b5)
                                        }
                                    }
                                    break
                                }
                            }
                        }
                    }
                    if (b7 !== true) {
                        if (b7 && b9["throws"]) {
                            b1 = b7(b1)
                        } else {
                            try {
                                b1 = b7(b1)
                            } catch (b4) {
                                return {
                                    state: "parsererror",
                                    error: b7 ? b4 : "No conversion from " + b0 + " to " + b5
                                }
                            }
                        }
                    }
                }
                b0 = b5
            }
        }
        return {
            state: "success",
            data: b1
        }
    }
    var bp = [],
        aw = /\?/,
        a5 = /(=)\?(?=&|$)|\?\?/,
        bl = bG.now();
    bG.ajaxSetup({
        jsonp: "callback",
        jsonpCallback: function () {
            var e = bp.pop() || (bG.expando + "_" + (bl++));
            this[e] = true;
            return e
        }
    });
    bG.ajaxPrefilter("json jsonp", function (b8, b3, b7) {
        var b6, e, b5, b1 = b8.data,
            bZ = b8.url,
            b0 = b8.jsonp !== false,
            b4 = b0 && a5.test(bZ),
            b2 = b0 && !b4 && typeof b1 === "string" && !(b8.contentType || "").indexOf("application/x-www-form-urlencoded") && a5.test(b1);
        if (b8.dataTypes[0] === "jsonp" || b4 || b2) {
            b6 = b8.jsonpCallback = bG.isFunction(b8.jsonpCallback) ? b8.jsonpCallback() : b8.jsonpCallback;
            e = a2[b6];
            if (b4) {
                b8.url = bZ.replace(a5, "$1" + b6)
            } else {
                if (b2) {
                    b8.data = b1.replace(a5, "$1" + b6)
                } else {
                    if (b0) {
                        b8.url += (aw.test(bZ) ? "&" : "?") + b8.jsonp + "=" + b6
                    }
                }
            }
            b8.converters["script json"] = function () {
                if (!b5) {
                    bG.error(b6 + " was not called")
                }
                return b5[0]
            };
            b8.dataTypes[0] = "json";
            a2[b6] = function () {
                b5 = arguments
            };
            b7.always(function () {
                a2[b6] = e;
                if (b8[b6]) {
                    b8.jsonpCallback = b3.jsonpCallback;
                    bp.push(b6)
                }
                if (b5 && bG.isFunction(e)) {
                    e(b5[0])
                }
                b5 = e = aB
            });
            return "script"
        }
    });
    bG.ajaxSetup({
        accepts: {
            script: "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"
        },
        contents: {
            script: /javascript|ecmascript/
        },
        converters: {
            "text script": function (e) {
                bG.globalEval(e);
                return e
            }
        }
    });
    bG.ajaxPrefilter("script", function (e) {
        if (e.cache === aB) {
            e.cache = false
        }
        if (e.crossDomain) {
            e.type = "GET";
            e.global = false
        }
    });
    bG.ajaxTransport("script", function (b0) {
        if (b0.crossDomain) {
            var e, bZ = o.head || o.getElementsByTagName("head")[0] || o.documentElement;
            return {
                send: function (b1, b2) {
                    e = o.createElement("script");
                    e.async = "async";
                    if (b0.scriptCharset) {
                        e.charset = b0.scriptCharset
                    }
                    e.src = b0.url;
                    e.onload = e.onreadystatechange = function (b4, b3) {
                        if (b3 || !e.readyState || /loaded|complete/.test(e.readyState)) {
                            e.onload = e.onreadystatechange = null;
                            if (bZ && e.parentNode) {
                                bZ.removeChild(e)
                            }
                            e = aB;
                            if (!b3) {
                                b2(200, "success")
                            }
                        }
                    };
                    bZ.insertBefore(e, bZ.firstChild)
                },
                abort: function () {
                    if (e) {
                        e.onload(0, 1)
                    }
                }
            }
        }
    });
    var ah, aN = a2.ActiveXObject ? function () {
            for (var e in ah) {
                ah[e](0, 1)
            }
        } : false,
        au = 0;

    function bB() {
        try {
            return new a2.XMLHttpRequest()
        } catch (bZ) {}
    }

    function bb() {
        try {
            return new a2.ActiveXObject("Microsoft.XMLHTTP")
        } catch (bZ) {}
    }
    bG.ajaxSettings.xhr = a2.ActiveXObject ? function () {
        return !this.isLocal && bB() || bb()
    } : bB;
    (function (e) {
        bG.extend(bG.support, {
            ajax: !! e,
            cors: !! e && ("withCredentials" in e)
        })
    })(bG.ajaxSettings.xhr());
    if (bG.support.ajax) {
        bG.ajaxTransport(function (e) {
            if (!e.crossDomain || bG.support.cors) {
                var bZ;
                return {
                    send: function (b5, b0) {
                        var b3, b2, b4 = e.xhr();
                        if (e.username) {
                            b4.open(e.type, e.url, e.async, e.username, e.password)
                        } else {
                            b4.open(e.type, e.url, e.async)
                        } if (e.xhrFields) {
                            for (b2 in e.xhrFields) {
                                b4[b2] = e.xhrFields[b2]
                            }
                        }
                        if (e.mimeType && b4.overrideMimeType) {
                            b4.overrideMimeType(e.mimeType)
                        }
                        if (!e.crossDomain && !b5["X-Requested-With"]) {
                            b5["X-Requested-With"] = "XMLHttpRequest"
                        }
                        try {
                            for (b2 in b5) {
                                b4.setRequestHeader(b2, b5[b2])
                            }
                        } catch (b1) {}
                        b4.send((e.hasContent && e.data) || null);
                        bZ = function (ce, b8) {
                            var b9, b7, b6, cc, cb;
                            try {
                                if (bZ && (b8 || b4.readyState === 4)) {
                                    bZ = aB;
                                    if (b3) {
                                        b4.onreadystatechange = bG.noop;
                                        if (aN) {
                                            delete ah[b3]
                                        }
                                    }
                                    if (b8) {
                                        if (b4.readyState !== 4) {
                                            b4.abort()
                                        }
                                    } else {
                                        b9 = b4.status;
                                        b6 = b4.getAllResponseHeaders();
                                        cc = {};
                                        cb = b4.responseXML;
                                        if (cb && cb.documentElement) {
                                            cc.xml = cb
                                        }
                                        try {
                                            cc.text = b4.responseText
                                        } catch (cd) {}
                                        try {
                                            b7 = b4.statusText
                                        } catch (cd) {
                                            b7 = ""
                                        }
                                        if (!b9 && e.isLocal && !e.crossDomain) {
                                            b9 = cc.text ? 200 : 404
                                        } else {
                                            if (b9 === 1223) {
                                                b9 = 204
                                            }
                                        }
                                    }
                                }
                            } catch (ca) {
                                if (!b8) {
                                    b0(-1, ca)
                                }
                            }
                            if (cc) {
                                b0(b9, b7, cc, b6)
                            }
                        };
                        if (!e.async) {
                            bZ()
                        } else {
                            if (b4.readyState === 4) {
                                setTimeout(bZ, 0)
                            } else {
                                b3 = ++au;
                                if (aN) {
                                    if (!ah) {
                                        ah = {};
                                        bG(a2).unload(aN)
                                    }
                                    ah[b3] = bZ
                                }
                                b4.onreadystatechange = bZ
                            }
                        }
                    },
                    abort: function () {
                        if (bZ) {
                            bZ(0, 1)
                        }
                    }
                }
            }
        })
    }
    var L, ab, bO = /^(?:toggle|show|hide)$/,
        bH = new RegExp("^(?:([-+])=|)(" + bx + ")([a-z%]*)$", "i"),
        bM = /queueHooks$/,
        ax = [i],
        a1 = {
            "*": [
                function (e, b5) {
                    var b1, b6, b7 = this.createTween(e, b5),
                        b2 = bH.exec(b5),
                        b3 = b7.cur(),
                        bZ = +b3 || 0,
                        b0 = 1,
                        b4 = 20;
                    if (b2) {
                        b1 = +b2[2];
                        b6 = b2[3] || (bG.cssNumber[e] ? "" : "px");
                        if (b6 !== "px" && bZ) {
                            bZ = bG.css(b7.elem, e, true) || b1 || 1;
                            do {
                                b0 = b0 || ".5";
                                bZ = bZ / b0;
                                bG.style(b7.elem, e, bZ + b6)
                            } while (b0 !== (b0 = b7.cur() / b3) && b0 !== 1 && --b4)
                        }
                        b7.unit = b6;
                        b7.start = bZ;
                        b7.end = b2[1] ? bZ + (b2[1] + 1) * b1 : b1
                    }
                    return b7
                }
            ]
        };

    function bj() {
        setTimeout(function () {
            L = aB
        }, 0);
        return (L = bG.now())
    }

    function bc(bZ, e) {
        bG.each(e, function (b4, b2) {
            var b3 = (a1[b4] || []).concat(a1["*"]),
                b0 = 0,
                b1 = b3.length;
            for (; b0 < b1; b0++) {
                if (b3[b0].call(bZ, b4, b2)) {
                    return
                }
            }
        })
    }

    function f(b0, b4, b7) {
        var b8, b3 = 0,
            e = 0,
            bZ = ax.length,
            b6 = bG.Deferred().always(function () {
                delete b2.elem
            }),
            b2 = function () {
                var ce = L || bj(),
                    cb = Math.max(0, b1.startTime + b1.duration - ce),
                    b9 = cb / b1.duration || 0,
                    cd = 1 - b9,
                    ca = 0,
                    cc = b1.tweens.length;
                for (; ca < cc; ca++) {
                    b1.tweens[ca].run(cd)
                }
                b6.notifyWith(b0, [b1, cd, cb]);
                if (cd < 1 && cc) {
                    return cb
                } else {
                    b6.resolveWith(b0, [b1]);
                    return false
                }
            }, b1 = b6.promise({
                elem: b0,
                props: bG.extend({}, b4),
                opts: bG.extend(true, {
                    specialEasing: {}
                }, b7),
                originalProperties: b4,
                originalOptions: b7,
                startTime: L || bj(),
                duration: b7.duration,
                tweens: [],
                createTween: function (cc, b9, cb) {
                    var ca = bG.Tween(b0, b1.opts, cc, b9, b1.opts.specialEasing[cc] || b1.opts.easing);
                    b1.tweens.push(ca);
                    return ca
                },
                stop: function (ca) {
                    var b9 = 0,
                        cb = ca ? b1.tweens.length : 0;
                    for (; b9 < cb; b9++) {
                        b1.tweens[b9].run(1)
                    }
                    if (ca) {
                        b6.resolveWith(b0, [b1, ca])
                    } else {
                        b6.rejectWith(b0, [b1, ca])
                    }
                    return this
                }
            }),
            b5 = b1.props;
        ak(b5, b1.opts.specialEasing);
        for (; b3 < bZ; b3++) {
            b8 = ax[b3].call(b1, b0, b5, b1.opts);
            if (b8) {
                return b8
            }
        }
        bc(b1, b5);
        if (bG.isFunction(b1.opts.start)) {
            b1.opts.start.call(b0, b1)
        }
        bG.fx.timer(bG.extend(b2, {
            anim: b1,
            queue: b1.opts.queue,
            elem: b0
        }));
        return b1.progress(b1.opts.progress).done(b1.opts.done, b1.opts.complete).fail(b1.opts.fail).always(b1.opts.always)
    }

    function ak(b1, b3) {
        var b0, bZ, b4, b2, e;
        for (b0 in b1) {
            bZ = bG.camelCase(b0);
            b4 = b3[bZ];
            b2 = b1[b0];
            if (bG.isArray(b2)) {
                b4 = b2[1];
                b2 = b1[b0] = b2[0]
            }
            if (b0 !== bZ) {
                b1[bZ] = b2;
                delete b1[b0]
            }
            e = bG.cssHooks[bZ];
            if (e && "expand" in e) {
                b2 = e.expand(b2);
                delete b1[bZ];
                for (b0 in b2) {
                    if (!(b0 in b1)) {
                        b1[b0] = b2[b0];
                        b3[b0] = b4
                    }
                }
            } else {
                b3[bZ] = b4
            }
        }
    }
    bG.Animation = bG.extend(f, {
        tweener: function (bZ, b2) {
            if (bG.isFunction(bZ)) {
                b2 = bZ;
                bZ = ["*"]
            } else {
                bZ = bZ.split(" ")
            }
            var b1, e = 0,
                b0 = bZ.length;
            for (; e < b0; e++) {
                b1 = bZ[e];
                a1[b1] = a1[b1] || [];
                a1[b1].unshift(b2)
            }
        },
        prefilter: function (bZ, e) {
            if (e) {
                ax.unshift(bZ)
            } else {
                ax.push(bZ)
            }
        }
    });

    function i(b2, b8, e) {
        var b7, b0, ca, b1, ce, b4, cd, cc, cb, b3 = this,
            bZ = b2.style,
            b9 = {}, b6 = [],
            b5 = b2.nodeType && Q(b2);
        if (!e.queue) {
            cc = bG._queueHooks(b2, "fx");
            if (cc.unqueued == null) {
                cc.unqueued = 0;
                cb = cc.empty.fire;
                cc.empty.fire = function () {
                    if (!cc.unqueued) {
                        cb()
                    }
                }
            }
            cc.unqueued++;
            b3.always(function () {
                b3.always(function () {
                    cc.unqueued--;
                    if (!bG.queue(b2, "fx").length) {
                        cc.empty.fire()
                    }
                })
            })
        }
        if (b2.nodeType === 1 && ("height" in b8 || "width" in b8)) {
            e.overflow = [bZ.overflow, bZ.overflowX, bZ.overflowY];
            if (bG.css(b2, "display") === "inline" && bG.css(b2, "float") === "none") {
                if (!bG.support.inlineBlockNeedsLayout || bC(b2.nodeName) === "inline") {
                    bZ.display = "inline-block"
                } else {
                    bZ.zoom = 1
                }
            }
        }
        if (e.overflow) {
            bZ.overflow = "hidden";
            if (!bG.support.shrinkWrapBlocks) {
                b3.done(function () {
                    bZ.overflow = e.overflow[0];
                    bZ.overflowX = e.overflow[1];
                    bZ.overflowY = e.overflow[2]
                })
            }
        }
        for (b7 in b8) {
            ca = b8[b7];
            if (bO.exec(ca)) {
                delete b8[b7];
                b4 = b4 || ca === "toggle";
                if (ca === (b5 ? "hide" : "show")) {
                    continue
                }
                b6.push(b7)
            }
        }
        b1 = b6.length;
        if (b1) {
            ce = bG._data(b2, "fxshow") || bG._data(b2, "fxshow", {});
            if ("hidden" in ce) {
                b5 = ce.hidden
            }
            if (b4) {
                ce.hidden = !b5
            }
            if (b5) {
                bG(b2).show()
            } else {
                b3.done(function () {
                    bG(b2).hide()
                })
            }
            b3.done(function () {
                var cf;
                bG.removeData(b2, "fxshow", true);
                for (cf in b9) {
                    bG.style(b2, cf, b9[cf])
                }
            });
            for (b7 = 0; b7 < b1; b7++) {
                b0 = b6[b7];
                cd = b3.createTween(b0, b5 ? ce[b0] : 0);
                b9[b0] = ce[b0] || bG.style(b2, b0);
                if (!(b0 in ce)) {
                    ce[b0] = cd.start;
                    if (b5) {
                        cd.end = cd.start;
                        cd.start = b0 === "width" || b0 === "height" ? 1 : 0
                    }
                }
            }
        }
    }

    function H(b0, bZ, b2, e, b1) {
        return new H.prototype.init(b0, bZ, b2, e, b1)
    }
    bG.Tween = H;
    H.prototype = {
        constructor: H,
        init: function (b1, bZ, b3, e, b2, b0) {
            this.elem = b1;
            this.prop = b3;
            this.easing = b2 || "swing";
            this.options = bZ;
            this.start = this.now = this.cur();
            this.end = e;
            this.unit = b0 || (bG.cssNumber[b3] ? "" : "px")
        },
        cur: function () {
            var e = H.propHooks[this.prop];
            return e && e.get ? e.get(this) : H.propHooks._default.get(this)
        },
        run: function (b0) {
            var bZ, e = H.propHooks[this.prop];
            if (this.options.duration) {
                this.pos = bZ = bG.easing[this.easing](b0, this.options.duration * b0, 0, 1, this.options.duration)
            } else {
                this.pos = bZ = b0
            }
            this.now = (this.end - this.start) * bZ + this.start;
            if (this.options.step) {
                this.options.step.call(this.elem, this.now, this)
            }
            if (e && e.set) {
                e.set(this)
            } else {
                H.propHooks._default.set(this)
            }
            return this
        }
    };
    H.prototype.init.prototype = H.prototype;
    H.propHooks = {
        _default: {
            get: function (bZ) {
                var e;
                if (bZ.elem[bZ.prop] != null && (!bZ.elem.style || bZ.elem.style[bZ.prop] == null)) {
                    return bZ.elem[bZ.prop]
                }
                e = bG.css(bZ.elem, bZ.prop, false, "");
                return !e || e === "auto" ? 0 : e
            },
            set: function (e) {
                if (bG.fx.step[e.prop]) {
                    bG.fx.step[e.prop](e)
                } else {
                    if (e.elem.style && (e.elem.style[bG.cssProps[e.prop]] != null || bG.cssHooks[e.prop])) {
                        bG.style(e.elem, e.prop, e.now + e.unit)
                    } else {
                        e.elem[e.prop] = e.now
                    }
                }
            }
        }
    };
    H.propHooks.scrollTop = H.propHooks.scrollLeft = {
        set: function (e) {
            if (e.elem.nodeType && e.elem.parentNode) {
                e.elem[e.prop] = e.now
            }
        }
    };
    bG.each(["toggle", "show", "hide"], function (bZ, e) {
        var b0 = bG.fn[e];
        bG.fn[e] = function (b1, b3, b2) {
            return b1 == null || typeof b1 === "boolean" || (!bZ && bG.isFunction(b1) && bG.isFunction(b3)) ? b0.apply(this, arguments) : this.animate(bF(e, true), b1, b3, b2)
        }
    });
    bG.fn.extend({
        fadeTo: function (e, b1, b0, bZ) {
            return this.filter(Q).css("opacity", 0).show().end().animate({
                opacity: b1
            }, e, b0, bZ)
        },
        animate: function (b4, b1, b3, b2) {
            var b0 = bG.isEmptyObject(b4),
                e = bG.speed(b1, b3, b2),
                bZ = function () {
                    var b5 = f(this, bG.extend({}, b4), e);
                    if (b0) {
                        b5.stop(true)
                    }
                };
            return b0 || e.queue === false ? this.each(bZ) : this.queue(e.queue, bZ)
        },
        stop: function (b0, bZ, e) {
            var b1 = function (b2) {
                var b3 = b2.stop;
                delete b2.stop;
                b3(e)
            };
            if (typeof b0 !== "string") {
                e = bZ;
                bZ = b0;
                b0 = aB
            }
            if (bZ && b0 !== false) {
                this.queue(b0 || "fx", [])
            }
            return this.each(function () {
                var b5 = true,
                    b2 = b0 != null && b0 + "queueHooks",
                    b4 = bG.timers,
                    b3 = bG._data(this);
                if (b2) {
                    if (b3[b2] && b3[b2].stop) {
                        b1(b3[b2])
                    }
                } else {
                    for (b2 in b3) {
                        if (b3[b2] && b3[b2].stop && bM.test(b2)) {
                            b1(b3[b2])
                        }
                    }
                }
                for (b2 = b4.length; b2--;) {
                    if (b4[b2].elem === this && (b0 == null || b4[b2].queue === b0)) {
                        b4[b2].anim.stop(e);
                        b5 = false;
                        b4.splice(b2, 1)
                    }
                }
                if (b5 || !e) {
                    bG.dequeue(this, b0)
                }
            })
        }
    });

    function bF(b0, b2) {
        var b1, e = {
                height: b0
            }, bZ = 0;
        b2 = b2 ? 1 : 0;
        for (; bZ < 4; bZ += 2 - b2) {
            b1 = bQ[bZ];
            e["margin" + b1] = e["padding" + b1] = b0
        }
        if (b2) {
            e.opacity = e.width = b0
        }
        return e
    }
    bG.each({
        slideDown: bF("show"),
        slideUp: bF("hide"),
        slideToggle: bF("toggle"),
        fadeIn: {
            opacity: "show"
        },
        fadeOut: {
            opacity: "hide"
        },
        fadeToggle: {
            opacity: "toggle"
        }
    }, function (e, bZ) {
        bG.fn[e] = function (b0, b2, b1) {
            return this.animate(bZ, b0, b2, b1)
        }
    });
    bG.speed = function (b0, b1, bZ) {
        var e = b0 && typeof b0 === "object" ? bG.extend({}, b0) : {
            complete: bZ || !bZ && b1 || bG.isFunction(b0) && b0,
            duration: b0,
            easing: bZ && b1 || b1 && !bG.isFunction(b1) && b1
        };
        e.duration = bG.fx.off ? 0 : typeof e.duration === "number" ? e.duration : e.duration in bG.fx.speeds ? bG.fx.speeds[e.duration] : bG.fx.speeds._default;
        if (e.queue == null || e.queue === true) {
            e.queue = "fx"
        }
        e.old = e.complete;
        e.complete = function () {
            if (bG.isFunction(e.old)) {
                e.old.call(this)
            }
            if (e.queue) {
                bG.dequeue(this, e.queue)
            }
        };
        return e
    };
    bG.easing = {
        linear: function (e) {
            return e
        },
        swing: function (e) {
            return 0.5 - Math.cos(e * Math.PI) / 2
        }
    };
    bG.timers = [];
    bG.fx = H.prototype.init;
    bG.fx.tick = function () {
        var b0, bZ = bG.timers,
            e = 0;
        L = bG.now();
        for (; e < bZ.length; e++) {
            b0 = bZ[e];
            if (!b0() && bZ[e] === b0) {
                bZ.splice(e--, 1)
            }
        }
        if (!bZ.length) {
            bG.fx.stop()
        }
        L = aB
    };
    bG.fx.timer = function (e) {
        if (e() && bG.timers.push(e) && !ab) {
            ab = setInterval(bG.fx.tick, bG.fx.interval)
        }
    };
    bG.fx.interval = 13;
    bG.fx.stop = function () {
        clearInterval(ab);
        ab = null
    };
    bG.fx.speeds = {
        slow: 600,
        fast: 200,
        _default: 400
    };
    bG.fx.step = {};
    if (bG.expr && bG.expr.filters) {
        bG.expr.filters.animated = function (e) {
            return bG.grep(bG.timers, function (bZ) {
                return e === bZ.elem
            }).length
        }
    }
    var bm = /^(?:body|html)$/i;
    bG.fn.offset = function (b8) {
        if (arguments.length) {
            return b8 === aB ? this : this.each(function (b9) {
                bG.offset.setOffset(this, b8, b9)
            })
        }
        var bZ, b4, b5, b2, b6, e, b1, b3 = {
                top: 0,
                left: 0
            }, b0 = this[0],
            b7 = b0 && b0.ownerDocument;
        if (!b7) {
            return
        }
        if ((b4 = b7.body) === b0) {
            return bG.offset.bodyOffset(b0)
        }
        bZ = b7.documentElement;
        if (!bG.contains(bZ, b0)) {
            return b3
        }
        if (typeof b0.getBoundingClientRect !== "undefined") {
            b3 = b0.getBoundingClientRect()
        }
        b5 = bn(b7);
        b2 = bZ.clientTop || b4.clientTop || 0;
        b6 = bZ.clientLeft || b4.clientLeft || 0;
        e = b5.pageYOffset || bZ.scrollTop;
        b1 = b5.pageXOffset || bZ.scrollLeft;
        return {
            top: b3.top + e - b2,
            left: b3.left + b1 - b6
        }
    };
    bG.offset = {
        bodyOffset: function (e) {
            var b0 = e.offsetTop,
                bZ = e.offsetLeft;
            if (bG.support.doesNotIncludeMarginInBodyOffset) {
                b0 += parseFloat(bG.css(e, "marginTop")) || 0;
                bZ += parseFloat(bG.css(e, "marginLeft")) || 0
            }
            return {
                top: b0,
                left: bZ
            }
        },
        setOffset: function (b1, ca, b4) {
            var b5 = bG.css(b1, "position");
            if (b5 === "static") {
                b1.style.position = "relative"
            }
            var b3 = bG(b1),
                bZ = b3.offset(),
                e = bG.css(b1, "top"),
                b8 = bG.css(b1, "left"),
                b9 = (b5 === "absolute" || b5 === "fixed") && bG.inArray("auto", [e, b8]) > -1,
                b7 = {}, b6 = {}, b0, b2;
            if (b9) {
                b6 = b3.position();
                b0 = b6.top;
                b2 = b6.left
            } else {
                b0 = parseFloat(e) || 0;
                b2 = parseFloat(b8) || 0
            } if (bG.isFunction(ca)) {
                ca = ca.call(b1, b4, bZ)
            }
            if (ca.top != null) {
                b7.top = (ca.top - bZ.top) + b0
            }
            if (ca.left != null) {
                b7.left = (ca.left - bZ.left) + b2
            }
            if ("using" in ca) {
                ca.using.call(b1, b7)
            } else {
                b3.css(b7)
            }
        }
    };
    bG.fn.extend({
        position: function () {
            if (!this[0]) {
                return
            }
            var b0 = this[0],
                bZ = this.offsetParent(),
                b1 = this.offset(),
                e = bm.test(bZ[0].nodeName) ? {
                    top: 0,
                    left: 0
                } : bZ.offset();
            b1.top -= parseFloat(bG.css(b0, "marginTop")) || 0;
            b1.left -= parseFloat(bG.css(b0, "marginLeft")) || 0;
            e.top += parseFloat(bG.css(bZ[0], "borderTopWidth")) || 0;
            e.left += parseFloat(bG.css(bZ[0], "borderLeftWidth")) || 0;
            return {
                top: b1.top - e.top,
                left: b1.left - e.left
            }
        },
        offsetParent: function () {
            return this.map(function () {
                var e = this.offsetParent || o.body;
                while (e && (!bm.test(e.nodeName) && bG.css(e, "position") === "static")) {
                    e = e.offsetParent
                }
                return e || o.body
            })
        }
    });
    bG.each({
        scrollLeft: "pageXOffset",
        scrollTop: "pageYOffset"
    }, function (b0, bZ) {
        var e = /Y/.test(bZ);
        bG.fn[b0] = function (b1) {
            return bG.access(this, function (b2, b5, b4) {
                var b3 = bn(b2);
                if (b4 === aB) {
                    return b3 ? (bZ in b3) ? b3[bZ] : b3.document.documentElement[b5] : b2[b5]
                }
                if (b3) {
                    b3.scrollTo(!e ? b4 : bG(b3).scrollLeft(), e ? b4 : bG(b3).scrollTop())
                } else {
                    b2[b5] = b4
                }
            }, b0, b1, arguments.length, null)
        }
    });

    function bn(e) {
        return bG.isWindow(e) ? e : e.nodeType === 9 ? e.defaultView || e.parentWindow : false
    }
    bG.each({
        Height: "height",
        Width: "width"
    }, function (e, bZ) {
        bG.each({
            padding: "inner" + e,
            content: bZ,
            "": "outer" + e
        }, function (b0, b1) {
            bG.fn[b1] = function (b5, b4) {
                var b3 = arguments.length && (b0 || typeof b5 !== "boolean"),
                    b2 = b0 || (b5 === true || b4 === true ? "margin" : "border");
                return bG.access(this, function (b7, b6, b8) {
                    var b9;
                    if (bG.isWindow(b7)) {
                        return b7.document.documentElement["client" + e]
                    }
                    if (b7.nodeType === 9) {
                        b9 = b7.documentElement;
                        return Math.max(b7.body["scroll" + e], b9["scroll" + e], b7.body["offset" + e], b9["offset" + e], b9["client" + e])
                    }
                    return b8 === aB ? bG.css(b7, b6, b8, b2) : bG.style(b7, b6, b8, b2)
                }, bZ, b3 ? b5 : aB, b3, null)
            }
        })
    });
    a2.jQuery = a2.$ = bG;
    if (typeof define === "function" && define.amd && define.amd.jQuery) {
        define("jquery", [], function () {
            return bG
        })
    }
})(window);
/* THIS FILE HAS BEEN MODIFIED BY ATLASSIAN. See https://ecosystem.atlassian.net/browse/AUI-1535 for details. Modified lines are marked below, search "ATLASSIAN" */
(function (i) {
    var e = "0.3.4",
        j = "hasOwnProperty",
        b = /[\.\/]/,
        a = "*",
        g = function () {}, f = function (m, l) {
            return m - l
        }, d, h, k = {
            n: {}
        }, c = function (m, C) {
            var v = k,
                s = h,
                w = Array.prototype.slice.call(arguments, 2),
                y = c.listeners(m),
                x = 0,
                u = false,
                p, o = [],
                t = {}, q = [],
                n = d,
                A = [];
            d = m;
            h = 0;
            for (var r = 0, B = y.length; r < B; r++) {
                if ("zIndex" in y[r]) {
                    o.push(y[r].zIndex);
                    if (y[r].zIndex < 0) {
                        t[y[r].zIndex] = y[r]
                    }
                }
            }
            o.sort(f);
            while (o[x] < 0) {
                p = t[o[x++]];
                q.push(p.apply(C, w));
                if (h) {
                    h = s;
                    return q
                }
            }
            for (r = 0; r < B; r++) {
                p = y[r];
                if ("zIndex" in p) {
                    if (p.zIndex == o[x]) {
                        q.push(p.apply(C, w));
                        if (h) {
                            break
                        }
                        do {
                            x++;
                            p = t[o[x]];
                            p && q.push(p.apply(C, w));
                            if (h) {
                                break
                            }
                        } while (p)
                    } else {
                        t[p.zIndex] = p
                    }
                } else {
                    q.push(p.apply(C, w));
                    if (h) {
                        break
                    }
                }
            }
            h = s;
            d = n;
            return q.length ? q : null
        };
    c.listeners = function (l) {
        var t = l.split(b),
            r = k,
            x, s, m, p, w, o, q, u, v = [r],
            n = [];
        for (p = 0, w = t.length; p < w; p++) {
            u = [];
            for (o = 0, q = v.length; o < q; o++) {
                r = v[o].n;
                s = [r[t[p]], r[a]];
                m = 2;
                while (m--) {
                    x = s[m];
                    if (x) {
                        u.push(x);
                        n = n.concat(x.f || [])
                    }
                }
            }
            v = u
        }
        return n
    };
    c.on = function (l, o) {
        var q = l.split(b),
            p = k;
        for (var m = 0, n = q.length; m < n; m++) {
            p = p.n;
            !p[q[m]] && (p[q[m]] = {
                n: {}
            });
            p = p[q[m]]
        }
        p.f = p.f || [];
        for (m = 0, n = p.f.length; m < n; m++) {
            if (p.f[m] == o) {
                return g
            }
        }
        p.f.push(o);
        return function (r) {
            if (+r == +r) {
                o.zIndex = +r
            }
        }
    };
    c.stop = function () {
        h = 1
    };
    c.nt = function (l) {
        if (l) {
            return new RegExp("(?:\\.|\\/|^)" + l + "(?:\\.|\\/|$)").test(d)
        }
        return d
    };
    c.off = c.unbind = function (m, r) {
        var t = m.split(b),
            s, v, n, p, w, o, q, u = [k];
        for (p = 0, w = t.length; p < w; p++) {
            for (o = 0; o < u.length; o += n.length - 2) {
                n = [o, 1];
                s = u[o].n;
                if (t[p] != a) {
                    if (s[t[p]]) {
                        n.push(s[t[p]])
                    }
                } else {
                    for (v in s) {
                        if (s[j](v)) {
                            n.push(s[v])
                        }
                    }
                }
                u.splice.apply(u, n)
            }
        }
        for (p = 0, w = u.length; p < w; p++) {
            s = u[p];
            while (s.n) {
                if (r) {
                    if (s.f) {
                        for (o = 0, q = s.f.length; o < q; o++) {
                            if (s.f[o] == r) {
                                s.f.splice(o, 1);
                                break
                            }
                        }!s.f.length && delete s.f
                    }
                    for (v in s.n) {
                        if (s.n[j](v) && s.n[v].f) {
                            var l = s.n[v].f;
                            for (o = 0, q = l.length; o < q; o++) {
                                if (l[o] == r) {
                                    l.splice(o, 1);
                                    break
                                }
                            }!l.length && delete s.n[v].f
                        }
                    }
                } else {
                    delete s.f;
                    for (v in s.n) {
                        if (s.n[j](v) && s.n[v].f) {
                            delete s.n[v].f
                        }
                    }
                }
                s = s.n
            }
        }
    };
    c.once = function (l, m) {
        var n = function () {
            var o = m.apply(this, arguments);
            c.unbind(l, n);
            return o
        };
        return c.on(l, n)
    };
    c.version = e;
    c.toString = function () {
        return "You are running Eve " + e
    };
    (typeof module != "undefined" && module.exports) ? (module.exports = c) : (typeof define != "undefined" ? (define("eve", [], function () {
        return c
    })) : (i.eve = c))
})(this);
(function () {
    function aR(g) {
        if (aR.is(g, "function")) {
            return ao ? g() : eve.on("raphael.DOMload", g)
        } else {
            if (aR.is(g, bd)) {
                return aR._engine.create[bG](aR, g.splice(0, 3 + aR.is(g[0], aL))).add(g)
            } else {
                var b = Array.prototype.slice.call(arguments, 0);
                if (aR.is(b[b.length - 1], "function")) {
                    var d = b.pop();
                    return ao ? d.call(aR._engine.create[bG](aR, b)) : eve.on("raphael.DOMload", function () {
                        d.call(aR._engine.create[bG](aR, b))
                    })
                } else {
                    return aR._engine.create[bG](aR, arguments)
                }
            }
        }
    }
    aR.version = "2.1.0";
    aR.eve = eve;
    var ao, a = /[, ]+/,
        bw = {
            circle: 1,
            rect: 1,
            path: 1,
            ellipse: 1,
            text: 1,
            image: 1
        }, br = /\{(\d+)\}/g,
        bJ = "prototype",
        ak = "hasOwnProperty",
        aA = {
            doc: document,
            win: window
        }, s = {
            was: Object.prototype[ak].call(aA.win, "Raphael"),
            is: aA.win.Raphael
        }, bF = function () {
            this.ca = this.customAttributes = {}
        }, a4, bo = "appendChild",
        bG = "apply",
        bE = "concat",
        Z = "createTouch" in aA.doc,
        aX = "",
        aQ = " ",
        bH = String,
        F = "split",
        Q = "click dblclick mousedown mousemove mouseout mouseover mouseup touchstart touchmove touchend touchcancel" [F](aQ),
        bx = {
            mousedown: "touchstart",
            mousemove: "touchmove",
            mouseup: "touchend"
        }, bK = bH.prototype.toLowerCase,
        au = Math,
        m = au.max,
        bm = au.min,
        aw = au.abs,
        bp = au.pow,
        aV = au.PI,
        aL = "number",
        aj = "string",
        bd = "array",
        a5 = "toString",
        a9 = "fill",
        a1 = Object.prototype.toString,
        bz = {}, j = "push",
        f = aR._ISURL = /^url\(['"]?([^\)]+?)['"]?\)$/i,
        A = /^\s*((#[a-f\d]{6})|(#[a-f\d]{3})|rgba?\(\s*([\d\.]+%?\s*,\s*[\d\.]+%?\s*,\s*[\d\.]+%?(?:\s*,\s*[\d\.]+%?)?)\s*\)|hsba?\(\s*([\d\.]+(?:deg|\xb0|%)?\s*,\s*[\d\.]+%?\s*,\s*[\d\.]+(?:%?\s*,\s*[\d\.]+)?)%?\s*\)|hsla?\(\s*([\d\.]+(?:deg|\xb0|%)?\s*,\s*[\d\.]+%?\s*,\s*[\d\.]+(?:%?\s*,\s*[\d\.]+)?)%?\s*\))\s*$/i,
        av = {
            "NaN": 1,
            "Infinity": 1,
            "-Infinity": 1
        }, c = /^(?:cubic-)?bezier\(([^,]+),([^,]+),([^,]+),([^\)]+)\)/,
        ah = au.round,
        z = "setAttribute",
        an = parseFloat,
        U = parseInt,
        bt = bH.prototype.toUpperCase,
        r = aR._availableAttrs = {
            "arrow-end": "none",
            "arrow-start": "none",
            blur: 0,
            "clip-rect": "0 0 1e9 1e9",
            cursor: "default",
            cx: 0,
            cy: 0,
            fill: "#fff",
            "fill-opacity": 1,
            font: '10px "Arial"',
            "font-family": '"Arial"',
            "font-size": "10",
            "font-style": "normal",
            "font-weight": 400,
            gradient: 0,
            height: 0,
            href: "http://raphaeljs.com/",
            "letter-spacing": 0,
            opacity: 1,
            path: "M0,0",
            r: 0,
            rx: 0,
            ry: 0,
            src: "",
            stroke: "#000",
            "stroke-dasharray": "",
            "stroke-linecap": "butt",
            "stroke-linejoin": "butt",
            "stroke-miterlimit": 0,
            "stroke-opacity": 1,
            "stroke-width": 1,
            target: "_blank",
            "text-anchor": "middle",
            title: "Raphael",
            transform: "",
            width: 0,
            x: 0,
            y: 0
        }, ar = aR._availableAnimAttrs = {
            blur: aL,
            "clip-rect": "csv",
            cx: aL,
            cy: aL,
            fill: "colour",
            "fill-opacity": aL,
            "font-size": aL,
            height: aL,
            opacity: aL,
            path: "path",
            r: aL,
            rx: aL,
            ry: aL,
            stroke: "colour",
            "stroke-opacity": aL,
            "stroke-width": aL,
            transform: "transform",
            width: aL,
            x: aL,
            y: aL
        }, ac = /[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]/g,
        bi = /[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]*,[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]*/,
        n = {
            hs: 1,
            rg: 1
        }, bg = /,?([achlmqrstvxz]),?/gi,
        a0 = /([achlmrqstvz])[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029,]*((-?\d*\.?\d*(?:e[\-+]?\d+)?[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]*,?[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]*)+)/ig,
        ai = /([rstm])[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029,]*((-?\d*\.?\d*(?:e[\-+]?\d+)?[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]*,?[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]*)+)/ig,
        aP = /(-?\d*\.?\d*(?:e[\-+]?\d+)?)[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]*,?[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]*/ig,
        aW = aR._radial_gradient = /^r(?:\(([^,]+?)[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]*,[\x09\x0a\x0b\x0c\x0d\x20\xa0\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000\u2028\u2029]*([^\)]+?)\))?/,
        aU = {}, bq = function (g, d) {
            return g.key - d.key
        }, u = function (g, d) {
            return an(g) - an(d)
        }, I = function () {}, bB = function (b) {
            return b
        }, az = aR._rectPath = function (b, E, d, g, i) {
            if (i) {
                return [["M", b + i, E], ["l", d - i * 2, 0], ["a", i, i, 0, 0, 1, i, i], ["l", 0, g - i * 2], ["a", i, i, 0, 0, 1, -i, i], ["l", i * 2 - d, 0], ["a", i, i, 0, 0, 1, -i, -i], ["l", 0, i * 2 - g], ["a", i, i, 0, 0, 1, i, -i], ["z"]]
            }
            return [["M", b, E], ["l", d, 0], ["l", 0, g], ["l", -d, 0], ["z"]]
        }, K = function (b, i, g, d) {
            if (d == null) {
                d = g
            }
            return [["M", b, i], ["m", 0, -d], ["a", g, d, 0, 1, 1, 0, 2 * d], ["a", g, d, 0, 1, 1, 0, -2 * d], ["z"]]
        }, N = aR._getPath = {
            path: function (b) {
                return b.attr("path")
            },
            circle: function (d) {
                var b = d.attrs;
                return K(b.cx, b.cy, b.r)
            },
            ellipse: function (d) {
                var b = d.attrs;
                return K(b.cx, b.cy, b.rx, b.ry)
            },
            rect: function (d) {
                var b = d.attrs;
                return az(b.x, b.y, b.width, b.height, b.r)
            },
            image: function (d) {
                var b = d.attrs;
                return az(b.x, b.y, b.width, b.height)
            },
            text: function (b) {
                var d = b._getBBox();
                return az(d.x, d.y, d.width, d.height)
            }
        }, L = aR.mapPath = function (bN, S) {
            if (!S) {
                return bN
            }
            var bL, R, g, b, bM, E, d;
            bN = W(bN);
            for (g = 0, bM = bN.length; g < bM; g++) {
                d = bN[g];
                for (b = 1, E = d.length; b < E; b += 2) {
                    bL = S.x(d[b], d[b + 1]);
                    R = S.y(d[b], d[b + 1]);
                    d[b] = bL;
                    d[b + 1] = R
                }
            }
            return bN
        };
    aR._g = aA;
    aR.type = (aA.win.SVGAngle || aA.doc.implementation.hasFeature("http://www.w3.org/TR/SVG11/feature#BasicStructure", "1.1") ? "SVG" : "VML");
    if (aR.type == "VML") {
        var aE = aA.doc.createElement("div"),
            aH;
        aE.innerHTML = '<v:shape adj="1"/>';
        aH = aE.firstChild;
        aH.style.behavior = "url(#default#VML)";
        if (!(aH && typeof aH.adj == "object")) {
            return (aR.type = aX)
        }
        aE = null
    }
    aR.svg = !(aR.vml = aR.type == "VML");
    aR._Paper = bF;
    aR.fn = a4 = bF.prototype = aR.prototype;
    aR._id = 0;
    aR._oid = 0;
    aR.is = function (d, b) {
        b = bK.call(b);
        if (b == "finite") {
            return !av[ak](+d)
        }
        if (b == "array") {
            return d instanceof Array
        }
        return (b == "null" && d === null) || (b == typeof d && d !== null) || (b == "object" && d === Object(d)) || (b == "array" && Array.isArray && Array.isArray(d)) || a1.call(d).slice(8, -1).toLowerCase() == b
    };

    function X(g) {
        if (Object(g) !== g) {
            return g
        }
        var d = new g.constructor;
        for (var b in g) {
            if (g[ak](b)) {
                d[b] = X(g[b])
            }
        }
        return d
    }
    aR.angle = function (E, S, g, R, d, i) {
        if (d == null) {
            var b = E - g,
                bL = S - R;
            if (!b && !bL) {
                return 0
            }
            return (180 + au.atan2(-bL, -b) * 180 / aV + 360) % 360
        } else {
            return aR.angle(E, S, d, i) - aR.angle(g, R, d, i)
        }
    };
    aR.rad = function (b) {
        return b % 360 * aV / 180
    };
    aR.deg = function (b) {
        return b * 180 / aV % 360
    };
    aR.snapTo = function (d, E, b) {
        b = aR.is(b, "finite") ? b : 10;
        if (aR.is(d, bd)) {
            var g = d.length;
            while (g--) {
                if (aw(d[g] - E) <= b) {
                    return d[g]
                }
            }
        } else {
            d = +d;
            var R = E % d;
            if (R < b) {
                return E - R
            }
            if (R > d - b) {
                return E - R + d
            }
        }
        return E
    };
    var h = aR.createUUID = (function (b, d) {
        return function () {
            return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(b, d).toUpperCase()
        }
    })(/[xy]/g, function (g) {
        var d = au.random() * 16 | 0,
            b = g == "x" ? d : (d & 3 | 8);
        return b.toString(16)
    });
    aR.setWindow = function (b) {
        eve("raphael.setWindow", aR, aA.win, b);
        aA.win = b;
        aA.doc = aA.win.document;
        if (aR._engine.initWin) {
            aR._engine.initWin(aA.win)
        }
    };
    var bf = function (g) {
        if (aR.vml) {
            var b = /^\s+|\s+$/g;
            var R;
            try {
                var S = new ActiveXObject("htmlfile");
                S.write("<body>");
                S.close();
                R = S.body
            } catch (bL) {
                R = createPopup().document.body
            }
            var d = R.createTextRange();
            bf = aG(function (i) {
                try {
                    R.style.color = bH(i).replace(b, aX);
                    var bM = d.queryCommandValue("ForeColor");
                    bM = ((bM & 255) << 16) | (bM & 65280) | ((bM & 16711680) >>> 16);
                    return "#" + ("000000" + bM.toString(16)).slice(-6)
                } catch (bN) {
                    return "none"
                }
            })
        } else {
            var E = aA.doc.createElement("i");
            E.title = "Rapha\xebl Colour Picker";
            E.style.display = "none";
            aA.doc.body.appendChild(E);
            bf = aG(function (i) {
                E.style.color = i;
                return aA.doc.defaultView.getComputedStyle(E, aX).getPropertyValue("color")
            })
        }
        return bf(g)
    }, aI = function () {
            return "hsb(" + [this.h, this.s, this.b] + ")"
        }, M = function () {
            return "hsl(" + [this.h, this.s, this.l] + ")"
        }, x = function () {
            return this.hex
        }, aY = function (R, E, d) {
            if (E == null && aR.is(R, "object") && "r" in R && "g" in R && "b" in R) {
                d = R.b;
                E = R.g;
                R = R.r
            }
            if (E == null && aR.is(R, aj)) {
                var i = aR.getRGB(R);
                R = i.r;
                E = i.g;
                d = i.b
            }
            if (R > 1 || E > 1 || d > 1) {
                R /= 255;
                E /= 255;
                d /= 255
            }
            return [R, E, d]
        }, a2 = function (R, E, d, S) {
            R *= 255;
            E *= 255;
            d *= 255;
            var i = {
                r: R,
                g: E,
                b: d,
                hex: aR.rgb(R, E, d),
                toString: x
            };
            aR.is(S, "finite") && (i.opacity = S);
            return i
        };
    aR.color = function (b) {
        var d;
        if (aR.is(b, "object") && "h" in b && "s" in b && "b" in b) {
            d = aR.hsb2rgb(b);
            b.r = d.r;
            b.g = d.g;
            b.b = d.b;
            b.hex = d.hex
        } else {
            if (aR.is(b, "object") && "h" in b && "s" in b && "l" in b) {
                d = aR.hsl2rgb(b);
                b.r = d.r;
                b.g = d.g;
                b.b = d.b;
                b.hex = d.hex
            } else {
                if (aR.is(b, "string")) {
                    b = aR.getRGB(b)
                }
                if (aR.is(b, "object") && "r" in b && "g" in b && "b" in b) {
                    d = aR.rgb2hsl(b);
                    b.h = d.h;
                    b.s = d.s;
                    b.l = d.l;
                    d = aR.rgb2hsb(b);
                    b.v = d.b
                } else {
                    b = {
                        hex: "none"
                    };
                    b.r = b.g = b.b = b.h = b.s = b.v = b.l = -1
                }
            }
        }
        b.toString = x;
        return b
    };
    aR.hsb2rgb = function (S, bN, bL, i) {
        if (this.is(S, "object") && "h" in S && "s" in S && "b" in S) {
            bL = S.b;
            bN = S.s;
            S = S.h;
            i = S.o
        }
        S *= 360;
        var E, bM, d, g, b;
        S = (S % 360) / 60;
        b = bL * bN;
        g = b * (1 - aw(S % 2 - 1));
        E = bM = d = bL - b;
        S = ~~S;
        E += [b, g, 0, 0, g, b][S];
        bM += [g, b, b, g, 0, 0][S];
        d += [0, 0, g, b, b, g][S];
        return a2(E, bM, d, i)
    };
    aR.hsl2rgb = function (bL, bN, E, i) {
        if (this.is(bL, "object") && "h" in bL && "s" in bL && "l" in bL) {
            E = bL.l;
            bN = bL.s;
            bL = bL.h
        }
        if (bL > 1 || bN > 1 || E > 1) {
            bL /= 360;
            bN /= 100;
            E /= 100
        }
        bL *= 360;
        var S, bM, d, g, b;
        bL = (bL % 360) / 60;
        b = 2 * bN * (E < 0.5 ? E : 1 - E);
        g = b * (1 - aw(bL % 2 - 1));
        S = bM = d = E - b / 2;
        bL = ~~bL;
        S += [b, g, 0, 0, g, b][bL];
        bM += [g, b, b, g, 0, 0][bL];
        d += [0, 0, g, b, b, g][bL];
        return a2(S, bM, d, i)
    };
    aR.rgb2hsb = function (bM, bL, d) {
        d = aY(bM, bL, d);
        bM = d[0];
        bL = d[1];
        d = d[2];
        var R, E, i, bN;
        i = m(bM, bL, d);
        bN = i - bm(bM, bL, d);
        R = (bN == 0 ? null : i == bM ? (bL - d) / bN : i == bL ? (d - bM) / bN + 2 : (bM - bL) / bN + 4);
        R = ((R + 360) % 6) * 60 / 360;
        E = bN == 0 ? 0 : bN / i;
        return {
            h: R,
            s: E,
            b: i,
            toString: aI
        }
    };
    aR.rgb2hsl = function (d, bL, bO) {
        bO = aY(d, bL, bO);
        d = bO[0];
        bL = bO[1];
        bO = bO[2];
        var bP, R, bN, bM, E, i;
        bM = m(d, bL, bO);
        E = bm(d, bL, bO);
        i = bM - E;
        bP = (i == 0 ? null : bM == d ? (bL - bO) / i : bM == bL ? (bO - d) / i + 2 : (d - bL) / i + 4);
        bP = ((bP + 360) % 6) * 60 / 360;
        bN = (bM + E) / 2;
        R = (i == 0 ? 0 : bN < 0.5 ? i / (2 * bN) : i / (2 - 2 * bN));
        return {
            h: bP,
            s: R,
            l: bN,
            toString: M
        }
    };
    aR._path2string = function () {
        return this.join(",").replace(bg, "$1")
    };

    function bk(E, g) {
        for (var b = 0, d = E.length; b < d; b++) {
            if (E[b] === g) {
                return E.push(E.splice(b, 1)[0])
            }
        }
    }

    function aG(i, d, b) {
        function g() {
            var E = Array.prototype.slice.call(arguments, 0),
                S = E.join("\u2400"),
                R = g.cache = g.cache || {}, bL = g.count = g.count || [];
            if (R[ak](S)) {
                bk(bL, S);
                return b ? b(R[S]) : R[S]
            }
            bL.length >= 1000 && delete R[bL.shift()];
            bL.push(S);
            R[S] = i[bG](d, E);
            return b ? b(R[S]) : R[S]
        }
        return g
    }
    var bv = aR._preload = function (g, d) {
        var b = aA.doc.createElement("img");
        b.style.cssText = "position:absolute;left:-9999em;top:-9999em";
        b.onload = function () {
            d.call(this);
            this.onload = null;
            aA.doc.body.removeChild(this)
        };
        b.onerror = function () {
            aA.doc.body.removeChild(this)
        };
        aA.doc.body.appendChild(b);
        b.src = g
    };

    function aq() {
        return this.hex
    }
    aR.getRGB = aG(function (b) {
        if (!b || !! ((b = bH(b)).indexOf("-") + 1)) {
            return {
                r: -1,
                g: -1,
                b: -1,
                hex: "none",
                error: 1,
                toString: aq
            }
        }
        if (b == "none") {
            return {
                r: -1,
                g: -1,
                b: -1,
                hex: "none",
                toString: aq
            }
        }!(n[ak](b.toLowerCase().substring(0, 2)) || b.charAt() == "#") && (b = bf(b));
        var E, d, g, S, i, bM, bL, R = b.match(A);
        if (R) {
            if (R[2]) {
                S = U(R[2].substring(5), 16);
                g = U(R[2].substring(3, 5), 16);
                d = U(R[2].substring(1, 3), 16)
            }
            if (R[3]) {
                S = U((bM = R[3].charAt(3)) + bM, 16);
                g = U((bM = R[3].charAt(2)) + bM, 16);
                d = U((bM = R[3].charAt(1)) + bM, 16)
            }
            if (R[4]) {
                bL = R[4][F](bi);
                d = an(bL[0]);
                bL[0].slice(-1) == "%" && (d *= 2.55);
                g = an(bL[1]);
                bL[1].slice(-1) == "%" && (g *= 2.55);
                S = an(bL[2]);
                bL[2].slice(-1) == "%" && (S *= 2.55);
                R[1].toLowerCase().slice(0, 4) == "rgba" && (i = an(bL[3]));
                bL[3] && bL[3].slice(-1) == "%" && (i /= 100)
            }
            if (R[5]) {
                bL = R[5][F](bi);
                d = an(bL[0]);
                bL[0].slice(-1) == "%" && (d *= 2.55);
                g = an(bL[1]);
                bL[1].slice(-1) == "%" && (g *= 2.55);
                S = an(bL[2]);
                bL[2].slice(-1) == "%" && (S *= 2.55);
                (bL[0].slice(-3) == "deg" || bL[0].slice(-1) == "\xb0") && (d /= 360);
                R[1].toLowerCase().slice(0, 4) == "hsba" && (i = an(bL[3]));
                bL[3] && bL[3].slice(-1) == "%" && (i /= 100);
                return aR.hsb2rgb(d, g, S, i)
            }
            if (R[6]) {
                bL = R[6][F](bi);
                d = an(bL[0]);
                bL[0].slice(-1) == "%" && (d *= 2.55);
                g = an(bL[1]);
                bL[1].slice(-1) == "%" && (g *= 2.55);
                S = an(bL[2]);
                bL[2].slice(-1) == "%" && (S *= 2.55);
                (bL[0].slice(-3) == "deg" || bL[0].slice(-1) == "\xb0") && (d /= 360);
                R[1].toLowerCase().slice(0, 4) == "hsla" && (i = an(bL[3]));
                bL[3] && bL[3].slice(-1) == "%" && (i /= 100);
                return aR.hsl2rgb(d, g, S, i)
            }
            R = {
                r: d,
                g: g,
                b: S,
                toString: aq
            };
            R.hex = "#" + (16777216 | S | (g << 8) | (d << 16)).toString(16).slice(1);
            aR.is(i, "finite") && (R.opacity = i);
            return R
        }
        return {
            r: -1,
            g: -1,
            b: -1,
            hex: "none",
            error: 1,
            toString: aq
        }
    }, aR);
    aR.hsb = aG(function (i, g, d) {
        return aR.hsb2rgb(i, g, d).hex
    });
    aR.hsl = aG(function (g, d, b) {
        return aR.hsl2rgb(g, d, b).hex
    });
    aR.rgb = aG(function (E, i, d) {
        return "#" + (16777216 | d | (i << 8) | (E << 16)).toString(16).slice(1)
    });
    aR.getColor = function (d) {
        var g = this.getColor.start = this.getColor.start || {
            h: 0,
            s: 1,
            b: d || 0.75
        }, b = this.hsb2rgb(g.h, g.s, g.b);
        g.h += 0.075;
        if (g.h > 1) {
            g.h = 0;
            g.s -= 0.2;
            g.s <= 0 && (this.getColor.start = {
                h: 0,
                s: 1,
                b: g.b
            })
        }
        return b.hex
    };
    aR.getColor.reset = function () {
        delete this.start
    };

    function bb(E, bL) {
        var S = [];
        for (var g = 0, b = E.length; b - 2 * !bL > g; g += 2) {
            var R = [{
                x: +E[g - 2],
                y: +E[g - 1]
            }, {
                x: +E[g],
                y: +E[g + 1]
            }, {
                x: +E[g + 2],
                y: +E[g + 3]
            }, {
                x: +E[g + 4],
                y: +E[g + 5]
            }];
            if (bL) {
                if (!g) {
                    R[0] = {
                        x: +E[b - 2],
                        y: +E[b - 1]
                    }
                } else {
                    if (b - 4 == g) {
                        R[3] = {
                            x: +E[0],
                            y: +E[1]
                        }
                    } else {
                        if (b - 2 == g) {
                            R[2] = {
                                x: +E[0],
                                y: +E[1]
                            };
                            R[3] = {
                                x: +E[2],
                                y: +E[3]
                            }
                        }
                    }
                }
            } else {
                if (b - 4 == g) {
                    R[3] = R[2]
                } else {
                    if (!g) {
                        R[0] = {
                            x: +E[g],
                            y: +E[g + 1]
                        }
                    }
                }
            }
            S.push(["C", (-R[0].x + 6 * R[1].x + R[2].x) / 6, (-R[0].y + 6 * R[1].y + R[2].y) / 6, (R[1].x + 6 * R[2].x - R[3].x) / 6, (R[1].y + 6 * R[2].y - R[3].y) / 6, R[2].x, R[2].y])
        }
        return S
    }
    aR.parsePathString = function (b) {
        if (!b) {
            return null
        }
        var g = Y(b);
        if (g.arr) {
            return aZ(g.arr)
        }
        var i = {
            a: 7,
            c: 6,
            h: 1,
            l: 2,
            m: 2,
            r: 4,
            q: 4,
            s: 4,
            t: 2,
            v: 1,
            z: 0
        }, d = [];
        if (aR.is(b, bd) && aR.is(b[0], bd)) {
            d = aZ(b)
        }
        if (!d.length) {
            bH(b).replace(a0, function (R, E, bM) {
                var bL = [],
                    S = E.toLowerCase();
                bM.replace(aP, function (bO, bN) {
                    bN && bL.push(+bN)
                });
                if (S == "m" && bL.length > 2) {
                    d.push([E][bE](bL.splice(0, 2)));
                    S = "l";
                    E = E == "m" ? "l" : "L"
                }
                if (S == "r") {
                    d.push([E][bE](bL))
                } else {
                    while (bL.length >= i[S]) {
                        d.push([E][bE](bL.splice(0, i[S])));
                        if (!i[S]) {
                            break
                        }
                    }
                }
            })
        }
        d.toString = aR._path2string;
        g.arr = aZ(d);
        return d
    };
    aR.parseTransformString = aG(function (d) {
        if (!d) {
            return null
        }
        var g = {
            r: 3,
            s: 4,
            t: 2,
            m: 6
        }, b = [];
        if (aR.is(d, bd) && aR.is(d[0], bd)) {
            b = aZ(d)
        }
        if (!b.length) {
            bH(d).replace(ai, function (E, i, bL) {
                var S = [],
                    R = bK.call(i);
                bL.replace(aP, function (bN, bM) {
                    bM && S.push(+bM)
                });
                b.push([i][bE](S))
            })
        }
        b.toString = aR._path2string;
        return b
    });
    var Y = function (d) {
        var b = Y.ps = Y.ps || {};
        if (b[d]) {
            b[d].sleep = 100
        } else {
            b[d] = {
                sleep: 100
            }
        }
        setTimeout(function () {
            for (var g in b) {
                if (b[ak](g) && g != d) {
                    b[g].sleep--;
                    !b[g].sleep && delete b[g]
                }
            }
        });
        return b[d]
    };
    aR.findDotsAtSegment = function (d, b, b2, b0, S, E, bN, bL, bV) {
        var bS = 1 - bV,
            bX = bp(bS, 3),
            bY = bp(bS, 2),
            bP = bV * bV,
            bM = bP * bV,
            bR = bX * d + bY * 3 * bV * b2 + bS * 3 * bV * bV * S + bM * bN,
            bO = bX * b + bY * 3 * bV * b0 + bS * 3 * bV * bV * E + bM * bL,
            bW = d + 2 * bV * (b2 - d) + bP * (S - 2 * b2 + d),
            bU = b + 2 * bV * (b0 - b) + bP * (E - 2 * b0 + b),
            b1 = b2 + 2 * bV * (S - b2) + bP * (bN - 2 * S + b2),
            bZ = b0 + 2 * bV * (E - b0) + bP * (bL - 2 * E + b0),
            bT = bS * d + bV * b2,
            bQ = bS * b + bV * b0,
            i = bS * S + bV * bN,
            g = bS * E + bV * bL,
            R = (90 - au.atan2(bW - b1, bU - bZ) * 180 / aV);
        (bW > b1 || bU < bZ) && (R += 180);
        return {
            x: bR,
            y: bO,
            m: {
                x: bW,
                y: bU
            },
            n: {
                x: b1,
                y: bZ
            },
            start: {
                x: bT,
                y: bQ
            },
            end: {
                x: i,
                y: g
            },
            alpha: R
        }
    };
    aR.bezierBBox = function (d, b, i, g, bM, S, R, E) {
        if (!aR.is(d, "array")) {
            d = [d, b, i, g, bM, S, R, E]
        }
        var bL = ba.apply(null, d);
        return {
            x: bL.min.x,
            y: bL.min.y,
            x2: bL.max.x,
            y2: bL.max.y,
            width: bL.max.x - bL.min.x,
            height: bL.max.y - bL.min.y
        }
    };
    aR.isPointInsideBBox = function (d, b, g) {
        return b >= d.x && b <= d.x2 && g >= d.y && g <= d.y2
    };
    aR.isBBoxIntersect = function (g, d) {
        var b = aR.isPointInsideBBox;
        return b(d, g.x, g.y) || b(d, g.x2, g.y) || b(d, g.x, g.y2) || b(d, g.x2, g.y2) || b(g, d.x, d.y) || b(g, d.x2, d.y) || b(g, d.x, d.y2) || b(g, d.x2, d.y2) || (g.x < d.x2 && g.x > d.x || d.x < g.x2 && d.x > g.x) && (g.y < d.y2 && g.y > d.y || d.y < g.y2 && d.y > g.y)
    };

    function bj(b, S, R, E, i) {
        var g = -3 * S + 9 * R - 9 * E + 3 * i,
            d = b * g + 6 * S - 12 * R + 6 * E;
        return b * d - 3 * S + 3 * R
    }

    function q(bW, R, bV, g, bU, d, bR, b, bO) {
        if (bO == null) {
            bO = 1
        }
        bO = bO > 1 ? 1 : bO < 0 ? 0 : bO;
        var bP = bO / 2,
            bQ = 12,
            bL = [-0.1252, 0.1252, -0.3678, 0.3678, -0.5873, 0.5873, -0.7699, 0.7699, -0.9041, 0.9041, -0.9816, 0.9816],
            bT = [0.2491, 0.2491, 0.2335, 0.2335, 0.2032, 0.2032, 0.1601, 0.1601, 0.1069, 0.1069, 0.0472, 0.0472],
            E = 0;
        for (var bS = 0; bS < bQ; bS++) {
            var bM = bP * bL[bS] + bP,
                bN = bj(bM, bW, bV, bU, bR),
                bX = bj(bM, R, g, d, b),
                S = bN * bN + bX * bX;
            E += bT[bS] * au.sqrt(S)
        }
        return bP * E
    }

    function C(g, bP, d, bO, b, bM, bR, bL, bN) {
        if (bN < 0 || q(g, bP, d, bO, b, bM, bR, bL) < bN) {
            return
        }
        var bQ = 1,
            i = bQ / 2,
            R = bQ - i,
            E, S = 0.01;
        E = q(g, bP, d, bO, b, bM, bR, bL, R);
        while (aw(E - bN) > S) {
            i /= 2;
            R += (E < bN ? 1 : -1) * i;
            E = q(g, bP, d, bO, b, bM, bR, bL, R)
        }
        return R
    }

    function O(i, bQ, g, bO, b, bN, bS, bM) {
        if (m(i, g) < bm(b, bS) || bm(i, g) > m(b, bS) || m(bQ, bO) < bm(bN, bM) || bm(bQ, bO) > m(bN, bM)) {
            return
        }
        var bL = (i * bO - bQ * g) * (b - bS) - (i - g) * (b * bM - bN * bS),
            S = (i * bO - bQ * g) * (bN - bM) - (bQ - bO) * (b * bM - bN * bS),
            E = (i - g) * (bN - bM) - (bQ - bO) * (b - bS);
        if (!E) {
            return
        }
        var bR = bL / E,
            bP = S / E,
            R = +bR.toFixed(2),
            d = +bP.toFixed(2);
        if (R < +bm(i, g).toFixed(2) || R > +m(i, g).toFixed(2) || R < +bm(b, bS).toFixed(2) || R > +m(b, bS).toFixed(2) || d < +bm(bQ, bO).toFixed(2) || d > +m(bQ, bO).toFixed(2) || d < +bm(bN, bM).toFixed(2) || d > +m(bN, bM).toFixed(2)) {
            return
        }
        return {
            x: bR,
            y: bP
        }
    }

    function ay(d, b) {
        return af(d, b)
    }

    function t(d, b) {
        return af(d, b, 1)
    }

    function af(b2, b1, b0) {
        var E = aR.bezierBBox(b2),
            d = aR.bezierBBox(b1);
        if (!aR.isBBoxIntersect(E, d)) {
            return b0 ? 0 : []
        }
        var bV = q.apply(0, b2),
            bU = q.apply(0, b1),
            bM = ~~ (bV / 5),
            bL = ~~ (bU / 5),
            bS = [],
            bR = [],
            g = {}, b3 = b0 ? 0 : [];
        for (var bX = 0; bX < bM + 1; bX++) {
            var bT = aR.findDotsAtSegment.apply(aR, b2.concat(bX / bM));
            bS.push({
                x: bT.x,
                y: bT.y,
                t: bX / bM
            })
        }
        for (bX = 0; bX < bL + 1; bX++) {
            bT = aR.findDotsAtSegment.apply(aR, b1.concat(bX / bL));
            bR.push({
                x: bT.x,
                y: bT.y,
                t: bX / bL
            })
        }
        for (bX = 0; bX < bM; bX++) {
            for (var bW = 0; bW < bL; bW++) {
                var bZ = bS[bX],
                    b = bS[bX + 1],
                    bY = bR[bW],
                    S = bR[bW + 1],
                    bQ = aw(b.x - bZ.x) < 0.001 ? "y" : "x",
                    bP = aw(S.x - bY.x) < 0.001 ? "y" : "x",
                    R = O(bZ.x, bZ.y, b.x, b.y, bY.x, bY.y, S.x, S.y);
                if (R) {
                    if (g[R.x.toFixed(4)] == R.y.toFixed(4)) {
                        continue
                    }
                    g[R.x.toFixed(4)] = R.y.toFixed(4);
                    var bO = bZ.t + aw((R[bQ] - bZ[bQ]) / (b[bQ] - bZ[bQ])) * (b.t - bZ.t),
                        bN = bY.t + aw((R[bP] - bY[bP]) / (S[bP] - bY[bP])) * (S.t - bY.t);
                    if (bO >= 0 && bO <= 1 && bN >= 0 && bN <= 1) {
                        if (b0) {
                            b3++
                        } else {
                            b3.push({
                                x: R.x,
                                y: R.y,
                                t1: bO,
                                t2: bN
                            })
                        }
                    }
                }
            }
        }
        return b3
    }
    aR.pathIntersection = function (d, b) {
        return D(d, b)
    };
    aR.pathIntersectionNumber = function (d, b) {
        return D(d, b, 1)
    };

    function D(g, b, bW) {
        g = aR._path2curve(g);
        b = aR._path2curve(b);
        var bU, S, bT, E, bR, bL, d, bO, b0, bZ, b1 = bW ? 0 : [];
        for (var bS = 0, bM = g.length; bS < bM; bS++) {
            var bY = g[bS];
            if (bY[0] == "M") {
                bU = bR = bY[1];
                S = bL = bY[2]
            } else {
                if (bY[0] == "C") {
                    b0 = [bU, S].concat(bY.slice(1));
                    bU = b0[6];
                    S = b0[7]
                } else {
                    b0 = [bU, S, bU, S, bR, bL, bR, bL];
                    bU = bR;
                    S = bL
                }
                for (var bQ = 0, bV = b.length; bQ < bV; bQ++) {
                    var bX = b[bQ];
                    if (bX[0] == "M") {
                        bT = d = bX[1];
                        E = bO = bX[2]
                    } else {
                        if (bX[0] == "C") {
                            bZ = [bT, E].concat(bX.slice(1));
                            bT = bZ[6];
                            E = bZ[7]
                        } else {
                            bZ = [bT, E, bT, E, d, bO, d, bO];
                            bT = d;
                            E = bO
                        }
                        var bN = af(b0, bZ, bW);
                        if (bW) {
                            b1 += bN
                        } else {
                            for (var bP = 0, R = bN.length; bP < R; bP++) {
                                bN[bP].segment1 = bS;
                                bN[bP].segment2 = bQ;
                                bN[bP].bez1 = b0;
                                bN[bP].bez2 = bZ
                            }
                            b1 = b1.concat(bN)
                        }
                    }
                }
            }
        }
        return b1
    }
    aR.isPointInsidePath = function (d, b, i) {
        var g = aR.pathBBox(d);
        return aR.isPointInsideBBox(g, b, i) && D(d, [
            ["M", b, i],
            ["H", g.x2 + 10]
        ], 1) % 2 == 1
    };
    aR._removedFactory = function (b) {
        return function () {
            eve("raphael.log", null, "Rapha\xebl: you are calling to method \u201c" + b + "\u201d of removed object", b)
        }
    };
    var am = aR.pathBBox = function (bT) {
        var bN = Y(bT);
        if (bN.bbox) {
            return bN.bbox
        }
        if (!bT) {
            return {
                x: 0,
                y: 0,
                width: 0,
                height: 0,
                x2: 0,
                y2: 0
            }
        }
        bT = W(bT);
        var bQ = 0,
            bP = 0,
            R = [],
            d = [],
            g;
        for (var bL = 0, bS = bT.length; bL < bS; bL++) {
            g = bT[bL];
            if (g[0] == "M") {
                bQ = g[1];
                bP = g[2];
                R.push(bQ);
                d.push(bP)
            } else {
                var bM = ba(bQ, bP, g[1], g[2], g[3], g[4], g[5], g[6]);
                R = R[bE](bM.min.x, bM.max.x);
                d = d[bE](bM.min.y, bM.max.y);
                bQ = g[5];
                bP = g[6]
            }
        }
        var b = bm[bG](0, R),
            bR = bm[bG](0, d),
            S = m[bG](0, R),
            E = m[bG](0, d),
            bO = {
                x: b,
                y: bR,
                x2: S,
                y2: E,
                width: S - b,
                height: E - bR
            };
        bN.bbox = X(bO);
        return bO
    }, aZ = function (d) {
            var b = X(d);
            b.toString = aR._path2string;
            return b
        }, aC = aR._pathToRelative = function (E) {
            var bM = Y(E);
            if (bM.rel) {
                return aZ(bM.rel)
            }
            if (!aR.is(E, bd) || !aR.is(E && E[0], bd)) {
                E = aR.parsePathString(E)
            }
            var bP = [],
                bR = 0,
                bQ = 0,
                bU = 0,
                bT = 0,
                g = 0;
            if (E[0][0] == "M") {
                bR = E[0][1];
                bQ = E[0][2];
                bU = bR;
                bT = bQ;
                g++;
                bP.push(["M", bR, bQ])
            }
            for (var bL = g, bV = E.length; bL < bV; bL++) {
                var b = bP[bL] = [],
                    bS = E[bL];
                if (bS[0] != bK.call(bS[0])) {
                    b[0] = bK.call(bS[0]);
                    switch (b[0]) {
                    case "a":
                        b[1] = bS[1];
                        b[2] = bS[2];
                        b[3] = bS[3];
                        b[4] = bS[4];
                        b[5] = bS[5];
                        b[6] = +(bS[6] - bR).toFixed(3);
                        b[7] = +(bS[7] - bQ).toFixed(3);
                        break;
                    case "v":
                        b[1] = +(bS[1] - bQ).toFixed(3);
                        break;
                    case "m":
                        bU = bS[1];
                        bT = bS[2];
                    default:
                        for (var S = 1, bN = bS.length; S < bN; S++) {
                            b[S] = +(bS[S] - ((S % 2) ? bR : bQ)).toFixed(3)
                        }
                    }
                } else {
                    b = bP[bL] = [];
                    if (bS[0] == "m") {
                        bU = bS[1] + bR;
                        bT = bS[2] + bQ
                    }
                    for (var R = 0, d = bS.length; R < d; R++) {
                        bP[bL][R] = bS[R]
                    }
                }
                var bO = bP[bL].length;
                switch (bP[bL][0]) {
                case "z":
                    bR = bU;
                    bQ = bT;
                    break;
                case "h":
                    bR += +bP[bL][bO - 1];
                    break;
                case "v":
                    bQ += +bP[bL][bO - 1];
                    break;
                default:
                    bR += +bP[bL][bO - 2];
                    bQ += +bP[bL][bO - 1]
                }
            }
            bP.toString = aR._path2string;
            bM.rel = aZ(bP);
            return bP
        }, w = aR._pathToAbsolute = function (bQ) {
            var g = Y(bQ);
            if (g.abs) {
                return aZ(g.abs)
            }
            if (!aR.is(bQ, bd) || !aR.is(bQ && bQ[0], bd)) {
                bQ = aR.parsePathString(bQ)
            }
            if (!bQ || !bQ.length) {
                return [["M", 0, 0]]
            }
            var bW = [],
                bL = 0,
                S = 0,
                bO = 0,
                bN = 0,
                E = 0;
            if (bQ[0][0] == "M") {
                bL = +bQ[0][1];
                S = +bQ[0][2];
                bO = bL;
                bN = S;
                E++;
                bW[0] = ["M", bL, S]
            }
            var bV = bQ.length == 3 && bQ[0][0] == "M" && bQ[1][0].toUpperCase() == "R" && bQ[2][0].toUpperCase() == "Z";
            for (var bP, b, bT = E, bM = bQ.length; bT < bM; bT++) {
                bW.push(bP = []);
                b = bQ[bT];
                if (b[0] != bt.call(b[0])) {
                    bP[0] = bt.call(b[0]);
                    switch (bP[0]) {
                    case "A":
                        bP[1] = b[1];
                        bP[2] = b[2];
                        bP[3] = b[3];
                        bP[4] = b[4];
                        bP[5] = b[5];
                        bP[6] = +(b[6] + bL);
                        bP[7] = +(b[7] + S);
                        break;
                    case "V":
                        bP[1] = +b[1] + S;
                        break;
                    case "H":
                        bP[1] = +b[1] + bL;
                        break;
                    case "R":
                        var R = [bL, S][bE](b.slice(1));
                        for (var bS = 2, bU = R.length; bS < bU; bS++) {
                            R[bS] = +R[bS] + bL;
                            R[++bS] = +R[bS] + S
                        }
                        bW.pop();
                        bW = bW[bE](bb(R, bV));
                        break;
                    case "M":
                        bO = +b[1] + bL;
                        bN = +b[2] + S;
                    default:
                        for (bS = 1, bU = b.length; bS < bU; bS++) {
                            bP[bS] = +b[bS] + ((bS % 2) ? bL : S)
                        }
                    }
                } else {
                    if (b[0] == "R") {
                        R = [bL, S][bE](b.slice(1));
                        bW.pop();
                        bW = bW[bE](bb(R, bV));
                        bP = ["R"][bE](b.slice(-2))
                    } else {
                        for (var bR = 0, d = b.length; bR < d; bR++) {
                            bP[bR] = b[bR]
                        }
                    }
                }
                switch (bP[0]) {
                case "Z":
                    bL = bO;
                    S = bN;
                    break;
                case "H":
                    bL = bP[1];
                    break;
                case "V":
                    S = bP[1];
                    break;
                case "M":
                    bO = bP[bP.length - 2];
                    bN = bP[bP.length - 1];
                default:
                    bL = bP[bP.length - 2];
                    S = bP[bP.length - 1]
                }
            }
            bW.toString = aR._path2string;
            g.abs = aZ(bW);
            return bW
        }, bI = function (d, i, b, g) {
            return [d, i, b, g, b, g]
        }, bn = function (d, i, S, E, b, g) {
            var R = 1 / 3,
                bL = 2 / 3;
            return [R * d + bL * S, R * i + bL * E, R * b + bL * S, R * g + bL * E, b, g]
        }, ae = function (bS, cn, b1, bZ, bT, bN, E, bR, cm, bU) {
            var bY = aV * 120 / 180,
                b = aV / 180 * (+bT || 0),
                b5 = [],
                b2, cj = aG(function (co, cr, i) {
                    var cq = co * au.cos(i) - cr * au.sin(i),
                        cp = co * au.sin(i) + cr * au.cos(i);
                    return {
                        x: cq,
                        y: cp
                    }
                });
            if (!bU) {
                b2 = cj(bS, cn, -b);
                bS = b2.x;
                cn = b2.y;
                b2 = cj(bR, cm, -b);
                bR = b2.x;
                cm = b2.y;
                var d = au.cos(aV / 180 * bT),
                    bP = au.sin(aV / 180 * bT),
                    b7 = (bS - bR) / 2,
                    b6 = (cn - cm) / 2;
                var ch = (b7 * b7) / (b1 * b1) + (b6 * b6) / (bZ * bZ);
                if (ch > 1) {
                    ch = au.sqrt(ch);
                    b1 = ch * b1;
                    bZ = ch * bZ
                }
                var g = b1 * b1,
                    ca = bZ * bZ,
                    cc = (bN == E ? -1 : 1) * au.sqrt(aw((g * ca - g * b6 * b6 - ca * b7 * b7) / (g * b6 * b6 + ca * b7 * b7))),
                    bW = cc * b1 * b6 / bZ + (bS + bR) / 2,
                    bV = cc * -bZ * b7 / b1 + (cn + cm) / 2,
                    bM = au.asin(((cn - bV) / bZ).toFixed(9)),
                    bL = au.asin(((cm - bV) / bZ).toFixed(9));
                bM = bS < bW ? aV - bM : bM;
                bL = bR < bW ? aV - bL : bL;
                bM < 0 && (bM = aV * 2 + bM);
                bL < 0 && (bL = aV * 2 + bL);
                if (E && bM > bL) {
                    bM = bM - aV * 2
                }
                if (!E && bL > bM) {
                    bL = bL - aV * 2
                }
            } else {
                bM = bU[0];
                bL = bU[1];
                bW = bU[2];
                bV = bU[3]
            }
            var bQ = bL - bM;
            if (aw(bQ) > bY) {
                var bX = bL,
                    b0 = bR,
                    bO = cm;
                bL = bM + bY * (E && bL > bM ? 1 : -1);
                bR = bW + b1 * au.cos(bL);
                cm = bV + bZ * au.sin(bL);
                b5 = ae(bR, cm, b1, bZ, bT, 0, E, b0, bO, [bL, bX, bW, bV])
            }
            bQ = bL - bM;
            var S = au.cos(bM),
                cl = au.sin(bM),
                R = au.cos(bL),
                ck = au.sin(bL),
                b8 = au.tan(bQ / 4),
                cb = 4 / 3 * b1 * b8,
                b9 = 4 / 3 * bZ * b8,
                ci = [bS, cn],
                cg = [bS + cb * cl, cn - b9 * S],
                cf = [bR + cb * ck, cm - b9 * R],
                cd = [bR, cm];
            cg[0] = 2 * ci[0] - cg[0];
            cg[1] = 2 * ci[1] - cg[1];
            if (bU) {
                return [cg, cf, cd][bE](b5)
            } else {
                b5 = [cg, cf, cd][bE](b5).join()[F](",");
                var b3 = [];
                for (var ce = 0, b4 = b5.length; ce < b4; ce++) {
                    b3[ce] = ce % 2 ? cj(b5[ce - 1], b5[ce], b).y : cj(b5[ce], b5[ce + 1], b).x
                }
                return b3
            }
        }, ag = function (d, b, i, g, bM, bL, S, R, bN) {
            var E = 1 - bN;
            return {
                x: bp(E, 3) * d + bp(E, 2) * 3 * bN * i + E * 3 * bN * bN * bM + bp(bN, 3) * S,
                y: bp(E, 3) * b + bp(E, 2) * 3 * bN * g + E * 3 * bN * bN * bL + bp(bN, 3) * R
            }
        }, ba = aG(function (i, d, R, E, bU, bT, bQ, bN) {
            var bS = (bU - 2 * R + i) - (bQ - 2 * bU + R),
                bP = 2 * (R - i) - 2 * (bU - R),
                bM = i - R,
                bL = (-bP + au.sqrt(bP * bP - 4 * bS * bM)) / 2 / bS,
                S = (-bP - au.sqrt(bP * bP - 4 * bS * bM)) / 2 / bS,
                bO = [d, bN],
                bR = [i, bQ],
                g;
            aw(bL) > "1e12" && (bL = 0.5);
            aw(S) > "1e12" && (S = 0.5);
            if (bL > 0 && bL < 1) {
                g = ag(i, d, R, E, bU, bT, bQ, bN, bL);
                bR.push(g.x);
                bO.push(g.y)
            }
            if (S > 0 && S < 1) {
                g = ag(i, d, R, E, bU, bT, bQ, bN, S);
                bR.push(g.x);
                bO.push(g.y)
            }
            bS = (bT - 2 * E + d) - (bN - 2 * bT + E);
            bP = 2 * (E - d) - 2 * (bT - E);
            bM = d - E;
            bL = (-bP + au.sqrt(bP * bP - 4 * bS * bM)) / 2 / bS;
            S = (-bP - au.sqrt(bP * bP - 4 * bS * bM)) / 2 / bS;
            aw(bL) > "1e12" && (bL = 0.5);
            aw(S) > "1e12" && (S = 0.5);
            if (bL > 0 && bL < 1) {
                g = ag(i, d, R, E, bU, bT, bQ, bN, bL);
                bR.push(g.x);
                bO.push(g.y)
            }
            if (S > 0 && S < 1) {
                g = ag(i, d, R, E, bU, bT, bQ, bN, S);
                bR.push(g.x);
                bO.push(g.y)
            }
            return {
                min: {
                    x: bm[bG](0, bR),
                    y: bm[bG](0, bO)
                },
                max: {
                    x: m[bG](0, bR),
                    y: m[bG](0, bO)
                }
            }
        }),
        W = aR._path2curve = aG(function (bU, bP) {
            var bN = !bP && Y(bU);
            if (!bP && bN.curve) {
                return aZ(bN.curve)
            }
            var E = w(bU),
                bQ = bP && w(bP),
                bR = {
                    x: 0,
                    y: 0,
                    bx: 0,
                    by: 0,
                    X: 0,
                    Y: 0,
                    qx: null,
                    qy: null
                }, d = {
                    x: 0,
                    y: 0,
                    bx: 0,
                    by: 0,
                    X: 0,
                    Y: 0,
                    qx: null,
                    qy: null
                }, S = function (bV, bW) {
                    var i, bX;
                    if (!bV) {
                        return ["C", bW.x, bW.y, bW.x, bW.y, bW.x, bW.y]
                    }!(bV[0] in {
                        T: 1,
                        Q: 1
                    }) && (bW.qx = bW.qy = null);
                    switch (bV[0]) {
                    case "M":
                        bW.X = bV[1];
                        bW.Y = bV[2];
                        break;
                    case "A":
                        bV = ["C"][bE](ae[bG](0, [bW.x, bW.y][bE](bV.slice(1))));
                        break;
                    case "S":
                        i = bW.x + (bW.x - (bW.bx || bW.x));
                        bX = bW.y + (bW.y - (bW.by || bW.y));
                        bV = ["C", i, bX][bE](bV.slice(1));
                        break;
                    case "T":
                        bW.qx = bW.x + (bW.x - (bW.qx || bW.x));
                        bW.qy = bW.y + (bW.y - (bW.qy || bW.y));
                        bV = ["C"][bE](bn(bW.x, bW.y, bW.qx, bW.qy, bV[1], bV[2]));
                        break;
                    case "Q":
                        bW.qx = bV[1];
                        bW.qy = bV[2];
                        bV = ["C"][bE](bn(bW.x, bW.y, bV[1], bV[2], bV[3], bV[4]));
                        break;
                    case "L":
                        bV = ["C"][bE](bI(bW.x, bW.y, bV[1], bV[2]));
                        break;
                    case "H":
                        bV = ["C"][bE](bI(bW.x, bW.y, bV[1], bW.y));
                        break;
                    case "V":
                        bV = ["C"][bE](bI(bW.x, bW.y, bW.x, bV[1]));
                        break;
                    case "Z":
                        bV = ["C"][bE](bI(bW.x, bW.y, bW.X, bW.Y));
                        break
                    }
                    return bV
                }, b = function (bV, bW) {
                    if (bV[bW].length > 7) {
                        bV[bW].shift();
                        var bX = bV[bW];
                        while (bX.length) {
                            bV.splice(bW++, 0, ["C"][bE](bX.splice(0, 6)))
                        }
                        bV.splice(bW, 1);
                        bS = m(E.length, bQ && bQ.length || 0)
                    }
                }, g = function (bZ, bY, bW, bV, bX) {
                    if (bZ && bY && bZ[bX][0] == "M" && bY[bX][0] != "M") {
                        bY.splice(bX, 0, ["M", bV.x, bV.y]);
                        bW.bx = 0;
                        bW.by = 0;
                        bW.x = bZ[bX][1];
                        bW.y = bZ[bX][2];
                        bS = m(E.length, bQ && bQ.length || 0)
                    }
                };
            for (var bM = 0, bS = m(E.length, bQ && bQ.length || 0); bM < bS; bM++) {
                E[bM] = S(E[bM], bR);
                b(E, bM);
                bQ && (bQ[bM] = S(bQ[bM], d));
                bQ && b(bQ, bM);
                g(E, bQ, bR, d, bM);
                g(bQ, E, d, bR, bM);
                var bL = E[bM],
                    bT = bQ && bQ[bM],
                    R = bL.length,
                    bO = bQ && bT.length;
                bR.x = bL[R - 2];
                bR.y = bL[R - 1];
                bR.bx = an(bL[R - 4]) || bR.x;
                bR.by = an(bL[R - 3]) || bR.y;
                d.bx = bQ && (an(bT[bO - 4]) || d.x);
                d.by = bQ && (an(bT[bO - 3]) || d.y);
                d.x = bQ && bT[bO - 2];
                d.y = bQ && bT[bO - 1]
            }
            if (!bQ) {
                bN.curve = aZ(E)
            }
            return bQ ? [E, bQ] : E
        }, null, aZ),
        v = aR._parseDots = aG(function (bO) {
            var bN = [];
            for (var S = 0, bP = bO.length; S < bP; S++) {
                var b = {}, bM = bO[S].match(/^([^:]*):?([\d\.]*)/);
                b.color = aR.getRGB(bM[1]);
                if (b.color.error) {
                    return null
                }
                b.color = b.color.hex;
                bM[2] && (b.offset = bM[2] + "%");
                bN.push(b)
            }
            for (S = 1, bP = bN.length - 1; S < bP; S++) {
                if (!bN[S].offset) {
                    var g = an(bN[S - 1].offset || 0),
                        E = 0;
                    for (var R = S + 1; R < bP; R++) {
                        if (bN[R].offset) {
                            E = bN[R].offset;
                            break
                        }
                    }
                    if (!E) {
                        E = 100;
                        R = bP
                    }
                    E = an(E);
                    var bL = (E - g) / (R - S + 1);
                    for (; S < R; S++) {
                        g += bL;
                        bN[S].offset = g + "%"
                    }
                }
            }
            return bN
        }),
        aK = aR._tear = function (b, d) {
            b == d.top && (d.top = b.prev);
            b == d.bottom && (d.bottom = b.next);
            b.next && (b.next.prev = b.prev);
            b.prev && (b.prev.next = b.next)
        }, ap = aR._tofront = function (b, d) {
            if (d.top === b) {
                return
            }
            aK(b, d);
            b.next = null;
            b.prev = d.top;
            d.top.next = b;
            d.top = b
        }, p = aR._toback = function (b, d) {
            if (d.bottom === b) {
                return
            }
            aK(b, d);
            b.next = d.bottom;
            b.prev = null;
            d.bottom.prev = b;
            d.bottom = b
        }, G = aR._insertafter = function (d, b, g) {
            aK(d, g);
            b == g.top && (g.top = d);
            b.next && (b.next.prev = d);
            d.next = b.next;
            d.prev = b;
            b.next = d
        }, aT = aR._insertbefore = function (d, b, g) {
            aK(d, g);
            b == g.bottom && (g.bottom = d);
            b.prev && (b.prev.next = d);
            d.prev = b.prev;
            b.prev = d;
            d.next = b
        }, bl = aR.toMatrix = function (g, b) {
            var i = am(g),
                d = {
                    _: {
                        transform: aX
                    },
                    getBBox: function () {
                        return i
                    }
                };
            aO(d, b);
            return d.matrix
        }, T = aR.transformPath = function (d, b) {
            return L(d, bl(d, b))
        }, aO = aR._extractTransform = function (d, bZ) {
            if (bZ == null) {
                return d._.transform
            }
            bZ = bH(bZ).replace(/\.{3}|\u2026/g, d._.transform || aX);
            var bR = aR.parseTransformString(bZ),
                bP = 0,
                bN = 0,
                bM = 0,
                bT = 1,
                bS = 1,
                b0 = d._,
                bU = new aF;
            b0.transform = bR || [];
            if (bR) {
                for (var bV = 0, bO = bR.length; bV < bO; bV++) {
                    var bQ = bR[bV],
                        b = bQ.length,
                        R = bH(bQ[0]).toLowerCase(),
                        bY = bQ[0] != R,
                        bL = bY ? bU.invert() : 0,
                        bX, E, bW, g, S;
                    if (R == "t" && b == 3) {
                        if (bY) {
                            bX = bL.x(0, 0);
                            E = bL.y(0, 0);
                            bW = bL.x(bQ[1], bQ[2]);
                            g = bL.y(bQ[1], bQ[2]);
                            bU.translate(bW - bX, g - E)
                        } else {
                            bU.translate(bQ[1], bQ[2])
                        }
                    } else {
                        if (R == "r") {
                            if (b == 2) {
                                S = S || d.getBBox(1);
                                bU.rotate(bQ[1], S.x + S.width / 2, S.y + S.height / 2);
                                bP += bQ[1]
                            } else {
                                if (b == 4) {
                                    if (bY) {
                                        bW = bL.x(bQ[2], bQ[3]);
                                        g = bL.y(bQ[2], bQ[3]);
                                        bU.rotate(bQ[1], bW, g)
                                    } else {
                                        bU.rotate(bQ[1], bQ[2], bQ[3])
                                    }
                                    bP += bQ[1]
                                }
                            }
                        } else {
                            if (R == "s") {
                                if (b == 2 || b == 3) {
                                    S = S || d.getBBox(1);
                                    bU.scale(bQ[1], bQ[b - 1], S.x + S.width / 2, S.y + S.height / 2);
                                    bT *= bQ[1];
                                    bS *= bQ[b - 1]
                                } else {
                                    if (b == 5) {
                                        if (bY) {
                                            bW = bL.x(bQ[3], bQ[4]);
                                            g = bL.y(bQ[3], bQ[4]);
                                            bU.scale(bQ[1], bQ[2], bW, g)
                                        } else {
                                            bU.scale(bQ[1], bQ[2], bQ[3], bQ[4])
                                        }
                                        bT *= bQ[1];
                                        bS *= bQ[2]
                                    }
                                }
                            } else {
                                if (R == "m" && b == 7) {
                                    bU.add(bQ[1], bQ[2], bQ[3], bQ[4], bQ[5], bQ[6])
                                }
                            }
                        }
                    }
                    b0.dirtyT = 1;
                    d.matrix = bU
                }
            }
            d.matrix = bU;
            b0.sx = bT;
            b0.sy = bS;
            b0.deg = bP;
            b0.dx = bN = bU.e;
            b0.dy = bM = bU.f;
            if (bT == 1 && bS == 1 && !bP && b0.bbox) {
                b0.bbox.x += +bN;
                b0.bbox.y += +bM
            } else {
                b0.dirtyT = 1
            }
        }, l = function (d) {
            var b = d[0];
            switch (b.toLowerCase()) {
            case "t":
                return [b, 0, 0];
            case "m":
                return [b, 1, 0, 0, 1, 0, 0];
            case "r":
                if (d.length == 4) {
                    return [b, 0, d[2], d[3]]
                } else {
                    return [b, 0]
                }
            case "s":
                if (d.length == 5) {
                    return [b, 1, 1, d[3], d[4]]
                } else {
                    if (d.length == 3) {
                        return [b, 1, 1]
                    } else {
                        return [b, 1]
                    }
                }
            }
        }, aB = aR._equaliseTransform = function (R, E) {
            E = bH(E).replace(/\.{3}|\u2026/g, R);
            R = aR.parseTransformString(R) || [];
            E = aR.parseTransformString(E) || [];
            var b = m(R.length, E.length),
                bN = [],
                bO = [],
                g = 0,
                d, S, bM, bL;
            for (; g < b; g++) {
                bM = R[g] || l(E[g]);
                bL = E[g] || l(bM);
                if ((bM[0] != bL[0]) || (bM[0].toLowerCase() == "r" && (bM[2] != bL[2] || bM[3] != bL[3])) || (bM[0].toLowerCase() == "s" && (bM[3] != bL[3] || bM[4] != bL[4]))) {
                    return
                }
                bN[g] = [];
                bO[g] = [];
                for (d = 0, S = m(bM.length, bL.length); d < S; d++) {
                    d in bM && (bN[g][d] = bM[d]);
                    d in bL && (bO[g][d] = bL[d])
                }
            }
            return {
                from: bN,
                to: bO
            }
        };
    aR._getContainer = function (b, E, g, i) {
        var d;
        d = i == null && !aR.is(b, "object") ? aA.doc.getElementById(b) : b;
        if (d == null) {
            return
        }
        if (d.tagName) {
            if (E == null) {
                return {
                    container: d,
                    width: d.style.pixelWidth || d.offsetWidth,
                    height: d.style.pixelHeight || d.offsetHeight
                }
            } else {
                return {
                    container: d,
                    width: E,
                    height: g
                }
            }
        }
        return {
            container: 1,
            x: b,
            y: E,
            width: g,
            height: i
        }
    };
    aR.pathToRelative = aC;
    aR._engine = {};
    aR.path2curve = W;
    aR.matrix = function (i, g, bL, S, R, E) {
        return new aF(i, g, bL, S, R, E)
    };

    function aF(i, g, bL, S, R, E) {
        if (i != null) {
            this.a = +i;
            this.b = +g;
            this.c = +bL;
            this.d = +S;
            this.e = +R;
            this.f = +E
        } else {
            this.a = 1;
            this.b = 0;
            this.c = 0;
            this.d = 1;
            this.e = 0;
            this.f = 0
        }
    }(function (g) {
        g.add = function (bT, bQ, bO, bM, S, R) {
            var E = [
                [],
                [],
                []
            ],
                i = [
                    [this.a, this.c, this.e],
                    [this.b, this.d, this.f],
                    [0, 0, 1]
                ],
                bS = [
                    [bT, bO, S],
                    [bQ, bM, R],
                    [0, 0, 1]
                ],
                bR, bP, bN, bL;
            if (bT && bT instanceof aF) {
                bS = [
                    [bT.a, bT.c, bT.e],
                    [bT.b, bT.d, bT.f],
                    [0, 0, 1]
                ]
            }
            for (bR = 0; bR < 3; bR++) {
                for (bP = 0; bP < 3; bP++) {
                    bL = 0;
                    for (bN = 0; bN < 3; bN++) {
                        bL += i[bR][bN] * bS[bN][bP]
                    }
                    E[bR][bP] = bL
                }
            }
            this.a = E[0][0];
            this.b = E[1][0];
            this.c = E[0][1];
            this.d = E[1][1];
            this.e = E[0][2];
            this.f = E[1][2]
        };
        g.invert = function () {
            var E = this,
                i = E.a * E.d - E.b * E.c;
            return new aF(E.d / i, -E.b / i, -E.c / i, E.a / i, (E.c * E.f - E.d * E.e) / i, (E.b * E.e - E.a * E.f) / i)
        };
        g.clone = function () {
            return new aF(this.a, this.b, this.c, this.d, this.e, this.f)
        };
        g.translate = function (i, E) {
            this.add(1, 0, 0, 1, i, E)
        };
        g.scale = function (E, S, i, R) {
            S == null && (S = E);
            (i || R) && this.add(1, 0, 0, 1, i, R);
            this.add(E, 0, 0, S, 0, 0);
            (i || R) && this.add(1, 0, 0, 1, -i, -R)
        };
        g.rotate = function (E, i, bL) {
            E = aR.rad(E);
            i = i || 0;
            bL = bL || 0;
            var S = +au.cos(E).toFixed(9),
                R = +au.sin(E).toFixed(9);
            this.add(S, R, -R, S, i, bL);
            this.add(1, 0, 0, 1, -i, -bL)
        };
        g.x = function (i, E) {
            return i * this.a + E * this.c + this.e
        };
        g.y = function (i, E) {
            return i * this.b + E * this.d + this.f
        };
        g.get = function (E) {
            return +this[bH.fromCharCode(97 + E)].toFixed(4)
        };
        g.toString = function () {
            return aR.svg ? "matrix(" + [this.get(0), this.get(1), this.get(2), this.get(3), this.get(4), this.get(5)].join() + ")" : [this.get(0), this.get(2), this.get(1), this.get(3), 0, 0].join()
        };
        g.toFilter = function () {
            return "progid:DXImageTransform.Microsoft.Matrix(M11=" + this.get(0) + ", M12=" + this.get(2) + ", M21=" + this.get(1) + ", M22=" + this.get(3) + ", Dx=" + this.get(4) + ", Dy=" + this.get(5) + ", sizingmethod='auto expand')"
        };
        g.offset = function () {
            return [this.e.toFixed(4), this.f.toFixed(4)]
        };

        function d(i) {
            return i[0] * i[0] + i[1] * i[1]
        }

        function b(i) {
            var E = au.sqrt(d(i));
            i[0] && (i[0] /= E);
            i[1] && (i[1] /= E)
        }
        g.split = function () {
            var E = {};
            E.dx = this.e;
            E.dy = this.f;
            var S = [
                [this.a, this.c],
                [this.b, this.d]
            ];
            E.scalex = au.sqrt(d(S[0]));
            b(S[0]);
            E.shear = S[0][0] * S[1][0] + S[0][1] * S[1][1];
            S[1] = [S[1][0] - S[0][0] * E.shear, S[1][1] - S[0][1] * E.shear];
            E.scaley = au.sqrt(d(S[1]));
            b(S[1]);
            E.shear /= E.scaley;
            var i = -S[0][1],
                R = S[1][1];
            if (R < 0) {
                E.rotate = aR.deg(au.acos(R));
                if (i < 0) {
                    E.rotate = 360 - E.rotate
                }
            } else {
                E.rotate = aR.deg(au.asin(i))
            }
            E.isSimple = !+E.shear.toFixed(9) && (E.scalex.toFixed(9) == E.scaley.toFixed(9) || !E.rotate);
            E.isSuperSimple = !+E.shear.toFixed(9) && E.scalex.toFixed(9) == E.scaley.toFixed(9) && !E.rotate;
            E.noRotation = !+E.shear.toFixed(9) && !E.rotate;
            return E
        };
        g.toTransformString = function (i) {
            var E = i || this[F]();
            if (E.isSimple) {
                E.scalex = +E.scalex.toFixed(4);
                E.scaley = +E.scaley.toFixed(4);
                E.rotate = +E.rotate.toFixed(4);
                return (E.dx || E.dy ? "t" + [E.dx, E.dy] : aX) + (E.scalex != 1 || E.scaley != 1 ? "s" + [E.scalex, E.scaley, 0, 0] : aX) + (E.rotate ? "r" + [E.rotate, 0, 0] : aX)
            } else {
                return "m" + [this.get(0), this.get(1), this.get(2), this.get(3), this.get(4), this.get(5)]
            }
        }
    })(aF.prototype);
    var V = navigator.userAgent.match(/Version\/(.*?)\s/) || navigator.userAgent.match(/Chrome\/(\d+)/);
    if ((navigator.vendor == "Apple Computer, Inc.") && (V && V[1] < 4 || navigator.platform.slice(0, 2) == "iP") || (navigator.vendor == "Google Inc." && V && V[1] < 8)) {
        a4.safari = function () {
            var b = this.rect(-99, -99, this.width + 99, this.height + 99).attr({
                stroke: "none"
            });
            setTimeout(function () {
                b.remove()
            })
        }
    } else {
        a4.safari = I
    }
    var P = function () {
        this.returnValue = false
    }, bD = function () {
            return this.originalEvent.preventDefault()
        }, a8 = function () {
            this.cancelBubble = true
        }, aJ = function () {
            return this.originalEvent.stopPropagation()
        }, aD = (function () {
            if (aA.doc.addEventListener) {
                return function (R, i, g, d) {
                    var b = Z && bx[i] ? bx[i] : i,
                        E = function (bP) {
                            var bO = aA.doc.documentElement.scrollTop || aA.doc.body.scrollTop,
                                bQ = aA.doc.documentElement.scrollLeft || aA.doc.body.scrollLeft,
                                S = bP.clientX + bQ,
                                bR = bP.clientY + bO;
                            if (Z && bx[ak](i)) {
                                for (var bM = 0, bN = bP.targetTouches && bP.targetTouches.length; bM < bN; bM++) {
                                    if (bP.targetTouches[bM].target == R) {
                                        var bL = bP;
                                        bP = bP.targetTouches[bM];
                                        bP.originalEvent = bL;
                                        bP.preventDefault = bD;
                                        bP.stopPropagation = aJ;
                                        break
                                    }
                                }
                            }
                            return g.call(d, bP, S, bR)
                        };
                    R.addEventListener(b, E, false);
                    return function () {
                        R.removeEventListener(b, E, false);
                        return true
                    }
                }
            } else {
                if (aA.doc.attachEvent) {
                    return function (R, i, g, d) {
                        var E = function (bM) {
                            bM = bM || aA.win.event;
                            var bL = aA.doc.documentElement.scrollTop || aA.doc.body.scrollTop,
                                bN = aA.doc.documentElement.scrollLeft || aA.doc.body.scrollLeft,
                                S = bM.clientX + bN,
                                bO = bM.clientY + bL;
                            bM.preventDefault = bM.preventDefault || P;
                            bM.stopPropagation = bM.stopPropagation || a8;
                            return g.call(d, bM, S, bO)
                        };
                        R.attachEvent("on" + i, E);
                        var b = function () {
                            R.detachEvent("on" + i, E);
                            return true
                        };
                        return b
                    }
                }
            }
        })(),
        be = [],
        by = function (bM) {
            var bP = bM.clientX,
                bO = bM.clientY,
                bR = aA.doc.documentElement.scrollTop || aA.doc.body.scrollTop,
                bS = aA.doc.documentElement.scrollLeft || aA.doc.body.scrollLeft,
                g, E = be.length;
            while (E--) {
                g = be[E];
                if (Z) {
                    var S = bM.touches.length,
                        R;
                    while (S--) {
                        R = bM.touches[S];
                        if (R.identifier == g.el._drag.id) {
                            bP = R.clientX;
                            bO = R.clientY;
                            (bM.originalEvent ? bM.originalEvent : bM).preventDefault();
                            break
                        }
                    }
                } else {
                    bM.preventDefault()
                }
                var d = g.el.node,
                    b, bL = d.nextSibling,
                    bQ = d.parentNode,
                    bN = d.style.display;
                aA.win.opera && bQ.removeChild(d);
                d.style.display = "none";
                b = g.el.paper.getElementByPoint(bP, bO);
                d.style.display = bN;
                aA.win.opera && (bL ? bQ.insertBefore(d, bL) : bQ.appendChild(d));
                b && eve("raphael.drag.over." + g.el.id, g.el, b);
                bP += bS;
                bO += bR;
                eve("raphael.drag.move." + g.el.id, g.move_scope || g.el, bP - g.el._drag.x, bO - g.el._drag.y, bP, bO, bM)
            }
        }, e = function (g) {
            aR.unmousemove(by).unmouseup(e);
            var d = be.length,
                b;
            while (d--) {
                b = be[d];
                b.el._drag = {};
                eve("raphael.drag.end." + b.el.id, b.end_scope || b.start_scope || b.move_scope || b.el, g)
            }
            be = []
        }, bh = aR.el = {};
    for (var ax = Q.length; ax--;) {
        (function (b) {
            aR[b] = bh[b] = function (g, d) {
                if (aR.is(g, "function")) {
                    this.events = this.events || [];
                    this.events.push({
                        name: b,
                        f: g,
                        unbind: aD(this.shape || this.node || aA.doc, b, g, d || this)
                    })
                }
                return this
            };
            aR["un" + b] = bh["un" + b] = function (i) {
                var g = this.events || [],
                    d = g.length;
                while (d--) {
                    if (g[d].name == b && g[d].f == i) {
                        g[d].unbind();
                        g.splice(d, 1);
                        !g.length && delete this.events;
                        return this
                    }
                }
                return this
            }
        })(Q[ax])
    }
    bh.data = function (d, E) {
        var g = aU[this.id] = aU[this.id] || {};
        if (arguments.length == 1) {
            if (aR.is(d, "object")) {
                for (var b in d) {
                    if (d[ak](b)) {
                        this.data(b, d[b])
                    }
                }
                return this
            }
            eve("raphael.data.get." + this.id, this, g[d], d);
            return g[d]
        }
        g[d] = E;
        eve("raphael.data.set." + this.id, this, E, d);
        return this
    };
    bh.removeData = function (b) {
        if (b == null) {
            aU[this.id] = {}
        } else {
            aU[this.id] && delete aU[this.id][b]
        }
        return this
    };
    bh.hover = function (i, b, g, d) {
        return this.mouseover(i, g).mouseout(b, d || g)
    };
    bh.unhover = function (d, b) {
        return this.unmouseover(d).unmouseout(b)
    };
    var bu = [];
    bh.drag = function (d, R, E, b, g, i) {
        function S(bM) {
            (bM.originalEvent || bM).preventDefault();
            var bL = aA.doc.documentElement.scrollTop || aA.doc.body.scrollTop,
                bN = aA.doc.documentElement.scrollLeft || aA.doc.body.scrollLeft;
            this._drag.x = bM.clientX + bN;
            this._drag.y = bM.clientY + bL;
            this._drag.id = bM.identifier;
            !be.length && aR.mousemove(by).mouseup(e);
            be.push({
                el: this,
                move_scope: b,
                start_scope: g,
                end_scope: i
            });
            R && eve.on("raphael.drag.start." + this.id, R);
            d && eve.on("raphael.drag.move." + this.id, d);
            E && eve.on("raphael.drag.end." + this.id, E);
            eve("raphael.drag.start." + this.id, g || b || this, bM.clientX + bN, bM.clientY + bL, bM)
        }
        this._drag = {};
        bu.push({
            el: this,
            start: S
        });
        this.mousedown(S);
        return this
    };
    bh.onDragOver = function (b) {
        b ? eve.on("raphael.drag.over." + this.id, b) : eve.unbind("raphael.drag.over." + this.id)
    };
    bh.undrag = function () {
        var b = bu.length;
        while (b--) {
            if (bu[b].el == this) {
                this.unmousedown(bu[b].start);
                bu.splice(b, 1);
                eve.unbind("raphael.drag.*." + this.id)
            }
        }!bu.length && aR.unmousemove(by).unmouseup(e)
    };
    a4.circle = function (b, i, g) {
        var d = aR._engine.circle(this, b || 0, i || 0, g || 0);
        this.__set__ && this.__set__.push(d);
        return d
    };
    a4.rect = function (b, R, d, i, E) {
        var g = aR._engine.rect(this, b || 0, R || 0, d || 0, i || 0, E || 0);
        this.__set__ && this.__set__.push(g);
        return g
    };
    a4.ellipse = function (b, E, i, g) {
        var d = aR._engine.ellipse(this, b || 0, E || 0, i || 0, g || 0);
        this.__set__ && this.__set__.push(d);
        return d
    };
    a4.path = function (b) {
        b && !aR.is(b, aj) && !aR.is(b[0], bd) && (b += aX);
        var d = aR._engine.path(aR.format[bG](aR, arguments), this);
        this.__set__ && this.__set__.push(d);
        return d
    };
    a4.image = function (E, b, R, d, i) {
        var g = aR._engine.image(this, E || "about:blank", b || 0, R || 0, d || 0, i || 0);
        this.__set__ && this.__set__.push(g);
        return g
    };
    a4.text = function (b, i, g) {
        var d = aR._engine.text(this, b || 0, i || 0, bH(g));
        this.__set__ && this.__set__.push(d);
        return d
    };
    a4.set = function (d) {
        !aR.is(d, "array") && (d = Array.prototype.splice.call(arguments, 0, arguments.length));
        var b = new al(d);
        this.__set__ && this.__set__.push(b);
        return b
    };
    a4.setStart = function (b) {
        this.__set__ = b || this.set()
    };
    a4.setFinish = function (d) {
        var b = this.__set__;
        delete this.__set__;
        return b
    };
    a4.setSize = function (d, b) {
        return aR._engine.setSize.call(this, d, b)
    };
    a4.setViewBox = function (b, E, d, i, g) {
        return aR._engine.setViewBox.call(this, b, E, d, i, g)
    };
    a4.top = a4.bottom = null;
    a4.raphael = aR;
    var bs = function (g) {
        var E = g.getBoundingClientRect(),
            bM = g.ownerDocument,
            R = bM.body,
            b = bM.documentElement,
            i = b.clientTop || R.clientTop || 0,
            S = b.clientLeft || R.clientLeft || 0,
            bL = E.top + (aA.win.pageYOffset || b.scrollTop || R.scrollTop) - i,
            d = E.left + (aA.win.pageXOffset || b.scrollLeft || R.scrollLeft) - S;
        return {
            y: bL,
            x: d
        }
    };
    a4.getElementByPoint = function (d, bL) {
        var S = this,
            g = S.canvas,
            R = aA.doc.elementFromPoint(d, bL);
        if (aA.win.opera && R.tagName == "svg") {
            var E = bs(g),
                i = g.createSVGRect();
            i.x = d - E.x;
            i.y = bL - E.y;
            i.width = i.height = 1;
            var b = g.getIntersectionList(i, null);
            if (b.length) {
                R = b[b.length - 1]
            }
        }
        if (!R) {
            return null
        }
        while (R.parentNode && R != g.parentNode && !R.raphael) {
            R = R.parentNode
        }
        R == S.canvas.parentNode && (R = g);
        R = R && R.raphael ? S.getById(R.raphaelid) : null;
        return R
    };
    a4.getById = function (d) {
        var b = this.bottom;
        while (b) {
            if (b.id == d) {
                return b
            }
            b = b.next
        }
        return null
    };
    a4.forEach = function (g, b) {
        var d = this.bottom;
        while (d) {
            if (g.call(b, d) === false) {
                return this
            }
            d = d.next
        }
        return this
    };
    a4.getElementsByPoint = function (b, g) {
        var d = this.set();
        this.forEach(function (i) {
            if (i.isPointInside(b, g)) {
                d.push(i)
            }
        });
        return d
    };

    function y() {
        return this.x + aQ + this.y
    }

    function at() {
        return this.x + aQ + this.y + aQ + this.width + " \xd7 " + this.height
    }
    bh.isPointInside = function (b, g) {
        var d = this.realPath = this.realPath || N[this.type](this);
        return aR.isPointInsidePath(d, b, g)
    };
    bh.getBBox = function (d) {
        if (this.removed) {
            return {}
        }
        var b = this._;
        if (d) {
            if (b.dirty || !b.bboxwt) {
                this.realPath = N[this.type](this);
                b.bboxwt = am(this.realPath);
                b.bboxwt.toString = at;
                b.dirty = 0
            }
            return b.bboxwt
        }
        if (b.dirty || b.dirtyT || !b.bbox) {
            if (b.dirty || !this.realPath) {
                b.bboxwt = 0;
                this.realPath = N[this.type](this)
            }
            b.bbox = am(L(this.realPath, this.matrix));
            b.bbox.toString = at;
            b.dirty = b.dirtyT = 0
        }
        return b.bbox
    };
    bh.clone = function () {
        if (this.removed) {
            return null
        }
        var b = this.paper[this.type]().attr(this.attr());
        this.__set__ && this.__set__.push(b);
        return b
    };
    bh.glow = function (bL) {
        if (this.type == "text") {
            return null
        }
        bL = bL || {};
        var g = {
            width: (bL.width || 10) + (+this.attr("stroke-width") || 1),
            fill: bL.fill || false,
            opacity: bL.opacity || 0.5,
            offsetx: bL.offsetx || 0,
            offsety: bL.offsety || 0,
            color: bL.color || "#000"
        }, S = g.width / 2,
            E = this.paper,
            b = E.set(),
            R = this.realPath || N[this.type](this);
        R = this.matrix ? L(R, this.matrix) : R;
        for (var d = 1; d < S + 1; d++) {
            b.push(E.path(R).attr({
                stroke: g.color,
                fill: g.fill ? g.color : "none",
                "stroke-linejoin": "round",
                "stroke-linecap": "round",
                "stroke-width": +(g.width / S * d).toFixed(3),
                opacity: +(g.opacity / S).toFixed(3)
            }))
        }
        return b.insertBefore(this).translate(g.offsetx, g.offsety)
    };
    var a7 = {}, k = function (d, b, E, i, bM, bL, S, R, g) {
            if (g == null) {
                return q(d, b, E, i, bM, bL, S, R)
            } else {
                return aR.findDotsAtSegment(d, b, E, i, bM, bL, S, R, C(d, b, E, i, bM, bL, S, R, g))
            }
        }, a6 = function (b, d) {
            return function (bT, R, S) {
                bT = W(bT);
                var bP, bO, g, bL, E = "",
                    bS = {}, bQ, bN = 0;
                for (var bM = 0, bR = bT.length; bM < bR; bM++) {
                    g = bT[bM];
                    if (g[0] == "M") {
                        bP = +g[1];
                        bO = +g[2]
                    } else {
                        bL = k(bP, bO, g[1], g[2], g[3], g[4], g[5], g[6]);
                        if (bN + bL > R) {
                            if (d && !bS.start) {
                                bQ = k(bP, bO, g[1], g[2], g[3], g[4], g[5], g[6], R - bN);
                                E += ["C" + bQ.start.x, bQ.start.y, bQ.m.x, bQ.m.y, bQ.x, bQ.y];
                                if (S) {
                                    return E
                                }
                                bS.start = E;
                                E = ["M" + bQ.x, bQ.y + "C" + bQ.n.x, bQ.n.y, bQ.end.x, bQ.end.y, g[5], g[6]].join();
                                bN += bL;
                                bP = +g[5];
                                bO = +g[6];
                                continue
                            }
                            if (!b && !d) {
                                bQ = k(bP, bO, g[1], g[2], g[3], g[4], g[5], g[6], R - bN);
                                return {
                                    x: bQ.x,
                                    y: bQ.y,
                                    alpha: bQ.alpha
                                }
                            }
                        }
                        bN += bL;
                        bP = +g[5];
                        bO = +g[6]
                    }
                    E += g.shift() + g
                }
                bS.end = E;
                bQ = b ? bN : d ? bS : aR.findDotsAtSegment(bP, bO, g[0], g[1], g[2], g[3], g[4], g[5], 1);
                bQ.alpha && (bQ = {
                    x: bQ.x,
                    y: bQ.y,
                    alpha: bQ.alpha
                });
                return bQ
            }
        };
    var aS = a6(1),
        J = a6(),
        ad = a6(0, 1);
    aR.getTotalLength = aS;
    aR.getPointAtLength = J;
    aR.getSubpath = function (d, i, g) {
        if (this.getTotalLength(d) - g < 0.000001) {
            return ad(d, i).end
        }
        var b = ad(d, g, 1);
        return i ? ad(b, i).end : b
    };
    bh.getTotalLength = function () {
        if (this.type != "path") {
            return
        }
        if (this.node.getTotalLength) {
            return this.node.getTotalLength()
        }
        return aS(this.attrs.path)
    };
    bh.getPointAtLength = function (b) {
        if (this.type != "path") {
            return
        }
        return J(this.attrs.path, b)
    };
    bh.getSubpath = function (d, b) {
        if (this.type != "path") {
            return
        }
        return aR.getSubpath(this.attrs.path, d, b)
    };
    var o = aR.easing_formulas = {
        linear: function (b) {
            return b
        },
        "<": function (b) {
            return bp(b, 1.7)
        },
        ">": function (b) {
            return bp(b, 0.48)
        },
        "<>": function (bL) {
            var i = 0.48 - bL / 1.04,
                g = au.sqrt(0.1734 + i * i),
                b = g - i,
                S = bp(aw(b), 1 / 3) * (b < 0 ? -1 : 1),
                R = -g - i,
                E = bp(aw(R), 1 / 3) * (R < 0 ? -1 : 1),
                d = S + E + 0.5;
            return (1 - d) * 3 * d * d + d * d * d
        },
        backIn: function (d) {
            var b = 1.70158;
            return d * d * ((b + 1) * d - b)
        },
        backOut: function (d) {
            d = d - 1;
            var b = 1.70158;
            return d * d * ((b + 1) * d + b) + 1
        },
        elastic: function (b) {
            if (b == !! b) {
                return b
            }
            return bp(2, -10 * b) * au.sin((b - 0.075) * (2 * aV) / 0.3) + 1
        },
        bounce: function (i) {
            var d = 7.5625,
                g = 2.75,
                b;
            if (i < (1 / g)) {
                b = d * i * i
            } else {
                if (i < (2 / g)) {
                    i -= (1.5 / g);
                    b = d * i * i + 0.75
                } else {
                    if (i < (2.5 / g)) {
                        i -= (2.25 / g);
                        b = d * i * i + 0.9375
                    } else {
                        i -= (2.625 / g);
                        b = d * i * i + 0.984375
                    }
                }
            }
            return b
        }
    };
    o.easeIn = o["ease-in"] = o["<"];
    o.easeOut = o["ease-out"] = o[">"];
    o.easeInOut = o["ease-in-out"] = o["<>"];
    o["back-in"] = o.backIn;
    o["back-out"] = o.backOut;
    var ab = [],
        aN = window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame || function (b) {
            setTimeout(b, 16)
        }, bC = function () {
            var bL = +new Date,
                bT = 0;
            for (; bT < ab.length; bT++) {
                var bZ = ab[bT];
                if (bZ.el.removed || bZ.paused) {
                    continue
                }
                var E = bL - bZ.start,
                    bR = bZ.ms,
                    bQ = bZ.easing,
                    bU = bZ.from,
                    bO = bZ.diff,
                    d = bZ.to,
                    bN = bZ.t,
                    S = bZ.el,
                    bP = {}, b, bX = {}, b1;
                if (bZ.initstatus) {
                    E = (bZ.initstatus * bZ.anim.top - bZ.prev) / (bZ.percent - bZ.prev) * bR;
                    bZ.status = bZ.initstatus;
                    delete bZ.initstatus;
                    bZ.stop && ab.splice(bT--, 1)
                } else {
                    bZ.status = (bZ.prev + (bZ.percent - bZ.prev) * (E / bR)) / bZ.anim.top
                } if (E < 0) {
                    continue
                }
                if (E < bR) {
                    var g = bQ(E / bR);
                    for (var bS in bU) {
                        if (bU[ak](bS)) {
                            switch (ar[bS]) {
                            case aL:
                                b = +bU[bS] + g * bR * bO[bS];
                                break;
                            case "colour":
                                b = "rgb(" + [H(ah(bU[bS].r + g * bR * bO[bS].r)), H(ah(bU[bS].g + g * bR * bO[bS].g)), H(ah(bU[bS].b + g * bR * bO[bS].b))].join(",") + ")";
                                break;
                            case "path":
                                b = [];
                                for (var bW = 0, bM = bU[bS].length; bW < bM; bW++) {
                                    b[bW] = [bU[bS][bW][0]];
                                    for (var bV = 1, bY = bU[bS][bW].length; bV < bY; bV++) {
                                        b[bW][bV] = +bU[bS][bW][bV] + g * bR * bO[bS][bW][bV]
                                    }
                                    b[bW] = b[bW].join(aQ)
                                }
                                b = b.join(aQ);
                                break;
                            case "transform":
                                if (bO[bS].real) {
                                    b = [];
                                    for (bW = 0, bM = bU[bS].length; bW < bM; bW++) {
                                        b[bW] = [bU[bS][bW][0]];
                                        for (bV = 1, bY = bU[bS][bW].length; bV < bY; bV++) {
                                            b[bW][bV] = bU[bS][bW][bV] + g * bR * bO[bS][bW][bV]
                                        }
                                    }
                                } else {
                                    var b0 = function (b2) {
                                        return +bU[bS][b2] + g * bR * bO[bS][b2]
                                    };
                                    b = [
                                        ["m", b0(0), b0(1), b0(2), b0(3), b0(4), b0(5)]
                                    ]
                                }
                                break;
                            case "csv":
                                if (bS == "clip-rect") {
                                    b = [];
                                    bW = 4;
                                    while (bW--) {
                                        b[bW] = +bU[bS][bW] + g * bR * bO[bS][bW]
                                    }
                                }
                                break;
                            default:
                                var R = [][bE](bU[bS]);
                                b = [];
                                bW = S.paper.customAttributes[bS].length;
                                while (bW--) {
                                    b[bW] = +R[bW] + g * bR * bO[bS][bW]
                                }
                                break
                            }
                            bP[bS] = b
                        }
                    }
                    S.attr(bP);
                    (function (b3, i, b2) {
                        setTimeout(function () {
                            eve("raphael.anim.frame." + b3, i, b2)
                        })
                    })(S.id, S, bZ.anim)
                } else {
                    (function (b3, b2, i) {
                        setTimeout(function () {
                            eve("raphael.anim.frame." + b2.id, b2, i);
                            eve("raphael.anim.finish." + b2.id, b2, i);
                            aR.is(b3, "function") && b3.call(b2)
                        })
                    })(bZ.callback, S, bZ.anim);
                    S.attr(d);
                    ab.splice(bT--, 1);
                    if (bZ.repeat > 1 && !bZ.next) {
                        for (b1 in d) {
                            if (d[ak](b1)) {
                                bX[b1] = bZ.totalOrigin[b1]
                            }
                        }
                        bZ.el.attr(bX);
                        aM(bZ.anim, bZ.el, bZ.anim.percents[0], null, bZ.totalOrigin, bZ.repeat - 1)
                    }
                    if (bZ.next && !bZ.stop) {
                        aM(bZ.anim, bZ.el, bZ.next, null, bZ.totalOrigin, bZ.repeat)
                    }
                }
            }
            aR.svg && S && S.paper && S.paper.safari();
            ab.length && aN(bC)
        }, H = function (b) {
            return b > 255 ? 255 : b < 0 ? 0 : b
        };
    bh.animateWith = function (d, E, g, b, bL, bQ) {
        var S = this;
        if (S.removed) {
            bQ && bQ.call(S);
            return S
        }
        var bO = g instanceof bA ? g : aR.animation(g, b, bL, bQ),
            bN, bM;
        aM(bO, S, bO.percents[0], null, S.attr());
        for (var R = 0, bP = ab.length; R < bP; R++) {
            if (ab[R].anim == E && ab[R].el == d) {
                ab[bP - 1].start = ab[R].start;
                break
            }
        }
        return S
    };

    function a3(bR, i, d, bQ, bP, bL) {
        var bM = 3 * i,
            bO = 3 * (bQ - i) - bM,
            b = 1 - bM - bO,
            S = 3 * d,
            bN = 3 * (bP - d) - S,
            bS = 1 - S - bN;

        function R(bT) {
            return ((b * bT + bO) * bT + bM) * bT
        }

        function g(bT, bV) {
            var bU = E(bT, bV);
            return ((bS * bU + bN) * bU + S) * bU
        }

        function E(bT, b0) {
            var bZ, bY, bW, bU, bX, bV;
            for (bW = bT, bV = 0; bV < 8; bV++) {
                bU = R(bW) - bT;
                if (aw(bU) < b0) {
                    return bW
                }
                bX = (3 * b * bW + 2 * bO) * bW + bM;
                if (aw(bX) < 0.000001) {
                    break
                }
                bW = bW - bU / bX
            }
            bZ = 0;
            bY = 1;
            bW = bT;
            if (bW < bZ) {
                return bZ
            }
            if (bW > bY) {
                return bY
            }
            while (bZ < bY) {
                bU = R(bW);
                if (aw(bU - bT) < b0) {
                    return bW
                }
                if (bT > bU) {
                    bZ = bW
                } else {
                    bY = bW
                }
                bW = (bY - bZ) / 2 + bZ
            }
            return bW
        }
        return g(bR, 1 / (200 * bL))
    }
    bh.onAnimation = function (b) {
        b ? eve.on("raphael.anim.frame." + this.id, b) : eve.unbind("raphael.anim.frame." + this.id);
        return this
    };

    function bA(E, g) {
        var d = [],
            i = {};
        this.ms = g;
        this.times = 1;
        if (E) {
            for (var b in E) {
                if (E[ak](b)) {
                    i[an(b)] = E[b];
                    d.push(an(b))
                }
            }
            d.sort(u)
        }
        this.anim = i;
        this.top = d[d.length - 1];
        this.percents = d
    }
    bA.prototype.delay = function (d) {
        var b = new bA(this.anim, this.ms);
        b.times = this.times;
        b.del = +d || 0;
        return b
    };
    bA.prototype.repeat = function (d) {
        var b = new bA(this.anim, this.ms);
        b.del = this.del;
        b.times = au.floor(m(d, 0)) || 1;
        return b
    };

    function aM(b3, g, b, b1, bL, bP) {
        b = an(b);
        var ca, S, bO, cb = [],
            bV, bU, R, bX = b3.ms,
            b2 = {}, E = {}, bR = {};
        if (b1) {
            for (b6 = 0, bQ = ab.length; b6 < bQ; b6++) {
                var b8 = ab[b6];
                if (b8.el.id == g.id && b8.anim == b3) {
                    if (b8.percent != b) {
                        ab.splice(b6, 1);
                        bO = 1
                    } else {
                        S = b8
                    }
                    g.attr(b8.totalOrigin);
                    break
                }
            }
        } else {
            b1 = +E
        }
        for (var b6 = 0, bQ = b3.percents.length; b6 < bQ; b6++) {
            if (b3.percents[b6] == b || b3.percents[b6] > b1 * b3.top) {
                b = b3.percents[b6];
                bU = b3.percents[b6 - 1] || 0;
                bX = bX / b3.top * (b - bU);
                bV = b3.percents[b6 + 1];
                ca = b3.anim[b];
                break
            } else {
                if (b1) {
                    g.attr(b3.anim[b3.percents[b6]])
                }
            }
        }
        if (!ca) {
            return
        }
        if (!S) {
            for (var bZ in ca) {
                if (ca[ak](bZ)) {
                    if (ar[ak](bZ) || g.paper.customAttributes[ak](bZ)) {
                        b2[bZ] = g.attr(bZ);
                        (b2[bZ] == null) && (b2[bZ] = r[bZ]);
                        E[bZ] = ca[bZ];
                        switch (ar[bZ]) {
                        case aL:
                            bR[bZ] = (E[bZ] - b2[bZ]) / bX;
                            break;
                        case "colour":
                            b2[bZ] = aR.getRGB(b2[bZ]);
                            var b0 = aR.getRGB(E[bZ]);
                            bR[bZ] = {
                                r: (b0.r - b2[bZ].r) / bX,
                                g: (b0.g - b2[bZ].g) / bX,
                                b: (b0.b - b2[bZ].b) / bX
                            };
                            break;
                        case "path":
                            var bM = W(b2[bZ], E[bZ]),
                                bT = bM[1];
                            b2[bZ] = bM[0];
                            bR[bZ] = [];
                            for (b6 = 0, bQ = b2[bZ].length; b6 < bQ; b6++) {
                                bR[bZ][b6] = [0];
                                for (var b5 = 1, b7 = b2[bZ][b6].length; b5 < b7; b5++) {
                                    bR[bZ][b6][b5] = (bT[b6][b5] - b2[bZ][b6][b5]) / bX
                                }
                            }
                            break;
                        case "transform":
                            var cd = g._,
                                cc = aB(cd[bZ], E[bZ]);
                            if (cc) {
                                b2[bZ] = cc.from;
                                E[bZ] = cc.to;
                                bR[bZ] = [];
                                bR[bZ].real = true;
                                for (b6 = 0, bQ = b2[bZ].length; b6 < bQ; b6++) {
                                    bR[bZ][b6] = [b2[bZ][b6][0]];
                                    for (b5 = 1, b7 = b2[bZ][b6].length; b5 < b7; b5++) {
                                        bR[bZ][b6][b5] = (E[bZ][b6][b5] - b2[bZ][b6][b5]) / bX
                                    }
                                }
                            } else {
                                var bY = (g.matrix || new aF),
                                    b9 = {
                                        _: {
                                            transform: cd.transform
                                        },
                                        getBBox: function () {
                                            return g.getBBox(1)
                                        }
                                    };
                                b2[bZ] = [bY.a, bY.b, bY.c, bY.d, bY.e, bY.f];
                                aO(b9, E[bZ]);
                                E[bZ] = b9._.transform;
                                bR[bZ] = [(b9.matrix.a - bY.a) / bX, (b9.matrix.b - bY.b) / bX, (b9.matrix.c - bY.c) / bX, (b9.matrix.d - bY.d) / bX, (b9.matrix.e - bY.e) / bX, (b9.matrix.f - bY.f) / bX]
                            }
                            break;
                        case "csv":
                            var d = bH(ca[bZ])[F](a),
                                bN = bH(b2[bZ])[F](a);
                            if (bZ == "clip-rect") {
                                b2[bZ] = bN;
                                bR[bZ] = [];
                                b6 = bN.length;
                                while (b6--) {
                                    bR[bZ][b6] = (d[b6] - b2[bZ][b6]) / bX
                                }
                            }
                            E[bZ] = d;
                            break;
                        default:
                            d = [][bE](ca[bZ]);
                            bN = [][bE](b2[bZ]);
                            bR[bZ] = [];
                            b6 = g.paper.customAttributes[bZ].length;
                            while (b6--) {
                                bR[bZ][b6] = ((d[b6] || 0) - (bN[b6] || 0)) / bX
                            }
                            break
                        }
                    }
                }
            }
            var bW = ca.easing,
                b4 = aR.easing_formulas[bW];
            if (!b4) {
                b4 = bH(bW).match(c);
                if (b4 && b4.length == 5) {
                    var bS = b4;
                    b4 = function (i) {
                        return a3(i, +bS[1], +bS[2], +bS[3], +bS[4], bX)
                    }
                } else {
                    b4 = bB
                }
            }
            R = ca.start || b3.start || +new Date;
            b8 = {
                anim: b3,
                percent: b,
                timestamp: R,
                start: R + (b3.del || 0),
                status: 0,
                initstatus: b1 || 0,
                stop: false,
                ms: bX,
                easing: b4,
                from: b2,
                diff: bR,
                to: E,
                el: g,
                callback: ca.callback,
                prev: bU,
                next: bV,
                repeat: bP || b3.times,
                origin: g.attr(),
                totalOrigin: bL
            };
            ab.push(b8);
            if (b1 && !S && !bO) {
                b8.stop = true;
                b8.start = new Date - bX * b1;
                if (ab.length == 1) {
                    return bC()
                }
            }
            if (bO) {
                b8.start = new Date - b8.ms * b1
            }
            ab.length == 1 && aN(bC)
        } else {
            S.initstatus = b1;
            S.start = new Date - S.ms * b1
        }
        eve("raphael.anim.start." + g.id, g, b3)
    }
    aR.animation = function (E, d, S, R) {
        if (E instanceof bA) {
            return E
        }
        if (aR.is(S, "function") || !S) {
            R = R || S || null;
            S = null
        }
        E = Object(E);
        d = +d || 0;
        var i = {}, g, b;
        for (b in E) {
            if (E[ak](b) && an(b) != b && an(b) + "%" != b) {
                g = true;
                i[b] = E[b]
            }
        }
        if (!g) {
            return new bA(E, d)
        } else {
            S && (i.easing = S);
            R && (i.callback = R);
            return new bA({
                100: i
            }, d)
        }
    };
    bh.animate = function (i, b, R, E) {
        var d = this;
        if (d.removed) {
            E && E.call(d);
            return d
        }
        var g = i instanceof bA ? i : aR.animation(i, b, R, E);
        aM(g, d, g.percents[0], null, d.attr());
        return d
    };
    bh.setTime = function (d, b) {
        if (d && b != null) {
            this.status(d, bm(b, d.ms) / d.ms)
        }
        return this
    };
    bh.status = function (R, E) {
        var d = [],
            g = 0,
            b, S;
        if (E != null) {
            aM(R, this, -1, bm(E, 1));
            return this
        } else {
            b = ab.length;
            for (; g < b; g++) {
                S = ab[g];
                if (S.el.id == this.id && (!R || S.anim == R)) {
                    if (R) {
                        return S.status
                    }
                    d.push({
                        anim: S.anim,
                        status: S.status
                    })
                }
            }
            if (R) {
                return 0
            }
            return d
        }
    };
    bh.pause = function (d) {
        for (var b = 0; b < ab.length; b++) {
            if (ab[b].el.id == this.id && (!d || ab[b].anim == d)) {
                if (eve("raphael.anim.pause." + this.id, this, ab[b].anim) !== false) {
                    ab[b].paused = true
                }
            }
        }
        return this
    };
    bh.resume = function (d) {
        for (var b = 0; b < ab.length; b++) {
            if (ab[b].el.id == this.id && (!d || ab[b].anim == d)) {
                var g = ab[b];
                if (eve("raphael.anim.resume." + this.id, this, g.anim) !== false) {
                    delete g.paused;
                    this.status(g.anim, g.status)
                }
            }
        }
        return this
    };
    bh.stop = function (d) {
        for (var b = 0; b < ab.length; b++) {
            if (ab[b].el.id == this.id && (!d || ab[b].anim == d)) {
                if (eve("raphael.anim.stop." + this.id, this, ab[b].anim) !== false) {
                    ab.splice(b--, 1)
                }
            }
        }
        return this
    };

    function aa(d) {
        for (var b = 0; b < ab.length; b++) {
            if (ab[b].el.paper == d) {
                ab.splice(b--, 1)
            }
        }
    }
    eve.on("raphael.remove", aa);
    eve.on("raphael.clear", aa);
    bh.toString = function () {
        return "Rapha\xebl\u2019s object"
    };
    var al = function (b) {
        this.items = [];
        this.length = 0;
        this.type = "set";
        if (b) {
            for (var d = 0, g = b.length; d < g; d++) {
                if (b[d] && (b[d].constructor == bh.constructor || b[d].constructor == al)) {
                    this[this.items.length] = this.items[this.items.length] = b[d];
                    this.length++
                }
            }
        }
    }, bc = al.prototype;
    bc.push = function () {
        var E, b;
        for (var d = 0, g = arguments.length; d < g; d++) {
            E = arguments[d];
            if (E && (E.constructor == bh.constructor || E.constructor == al)) {
                b = this.items.length;
                this[b] = this.items[b] = E;
                this.length++
            }
        }
        return this
    };
    bc.pop = function () {
        this.length && delete this[this.length--];
        return this.items.pop()
    };
    bc.forEach = function (E, b) {
        for (var d = 0, g = this.items.length; d < g; d++) {
            if (E.call(b, this.items[d], d) === false) {
                return this
            }
        }
        return this
    };
    for (var B in bh) {
        if (bh[ak](B)) {
            bc[B] = (function (b) {
                return function () {
                    var d = arguments;
                    return this.forEach(function (g) {
                        g[b][bG](g, d)
                    })
                }
            })(B)
        }
    }
    bc.attr = function (d, S) {
        if (d && aR.is(d, bd) && aR.is(d[0], "object")) {
            for (var b = 0, R = d.length; b < R; b++) {
                this.items[b].attr(d[b])
            }
        } else {
            for (var g = 0, E = this.items.length; g < E; g++) {
                this.items[g].attr(d, S)
            }
        }
        return this
    };
    bc.clear = function () {
        while (this.length) {
            this.pop()
        }
    };
    bc.splice = function (E, bL, bM) {
        E = E < 0 ? m(this.length + E, 0) : E;
        bL = m(0, bm(this.length - E, bL));
        var g = [],
            b = [],
            d = [],
            R;
        for (R = 2; R < arguments.length; R++) {
            d.push(arguments[R])
        }
        for (R = 0; R < bL; R++) {
            b.push(this[E + R])
        }
        for (; R < this.length - E; R++) {
            g.push(this[E + R])
        }
        var S = d.length;
        for (R = 0; R < S + g.length; R++) {
            this.items[E + R] = this[E + R] = R < S ? d[R] : g[R - S]
        }
        R = this.items.length = this.length -= bL - S;
        while (this[R]) {
            delete this[R++]
        }
        return new al(b)
    };
    bc.exclude = function (g) {
        for (var b = 0, d = this.length; b < d; b++) {
            if (this[b] == g) {
                this.splice(b, 1);
                return true
            }
        }
    };
    bc.animate = function (g, b, bL, bN) {
        (aR.is(bL, "function") || !bL) && (bN = bL || null);
        var S = this.items.length,
            E = S,
            bO, bM = this,
            R;
        if (!S) {
            return this
        }
        bN && (R = function () {
            !--S && bN.call(bM)
        });
        bL = aR.is(bL, aj) ? bL : R;
        var d = aR.animation(g, b, bL, R);
        bO = this.items[--E].animate(d);
        while (E--) {
            this.items[E] && !this.items[E].removed && this.items[E].animateWith(bO, d, d)
        }
        return this
    };
    bc.insertAfter = function (d) {
        var b = this.items.length;
        while (b--) {
            this.items[b].insertAfter(d)
        }
        return this
    };
    bc.getBBox = function () {
        var b = [],
            S = [],
            d = [],
            E = [];
        for (var g = this.items.length; g--;) {
            if (!this.items[g].removed) {
                var R = this.items[g].getBBox();
                b.push(R.x);
                S.push(R.y);
                d.push(R.x + R.width);
                E.push(R.y + R.height)
            }
        }
        b = bm[bG](0, b);
        S = bm[bG](0, S);
        d = m[bG](0, d);
        E = m[bG](0, E);
        return {
            x: b,
            y: S,
            x2: d,
            y2: E,
            width: d - b,
            height: E - S
        }
    };
    bc.clone = function (g) {
        g = new al;
        for (var b = 0, d = this.items.length; b < d; b++) {
            g.push(this.items[b].clone())
        }
        return g
    };
    bc.toString = function () {
        return "Rapha\xebl\u2018s set"
    };
    aR.registerFont = function (d) {
        if (!d.face) {
            return d
        }
        this.fonts = this.fonts || {};
        var i = {
            w: d.w,
            face: {},
            glyphs: {}
        }, g = d.face["font-family"];
        for (var S in d.face) {
            if (d.face[ak](S)) {
                i.face[S] = d.face[S]
            }
        }
        if (this.fonts[g]) {
            this.fonts[g].push(i)
        } else {
            this.fonts[g] = [i]
        } if (!d.svg) {
            i.face["units-per-em"] = U(d.face["units-per-em"], 10);
            for (var E in d.glyphs) {
                if (d.glyphs[ak](E)) {
                    var R = d.glyphs[E];
                    i.glyphs[E] = {
                        w: R.w,
                        k: {},
                        d: R.d && "M" + R.d.replace(/[mlcxtrv]/g, function (bL) {
                            return {
                                l: "L",
                                c: "C",
                                x: "z",
                                t: "m",
                                r: "l",
                                v: "c"
                            }[bL] || "M"
                        }) + "z"
                    };
                    if (R.k) {
                        for (var b in R.k) {
                            if (R[ak](b)) {
                                i.glyphs[E].k[b] = R.k[b]
                            }
                        }
                    }
                }
            }
        }
        return d
    };
    a4.getFont = function (bM, bN, d, E) {
        E = E || "normal";
        d = d || "normal";
        bN = +bN || {
            normal: 400,
            bold: 700,
            lighter: 300,
            bolder: 800
        }[bN] || 400;
        if (!aR.fonts) {
            return
        }
        var R = aR.fonts[bM];
        if (!R) {
            var g = new RegExp("(^|\\s)" + bM.replace(/[^\w\d\s+!~.:_-]/g, aX) + "(\\s|$)", "i");
            for (var b in aR.fonts) {
                if (aR.fonts[ak](b)) {
                    if (g.test(b)) {
                        R = aR.fonts[b];
                        break
                    }
                }
            }
        }
        var S;
        if (R) {
            for (var bL = 0, bO = R.length; bL < bO; bL++) {
                S = R[bL];
                if (S.face["font-weight"] == bN && (S.face["font-style"] == d || !S.face["font-style"]) && S.face["font-stretch"] == E) {
                    break
                }
            }
        }
        return S
    };
    a4.print = function (bL, S, b, bO, bP, bY, d) {
        bY = bY || "middle";
        d = m(bm(d || 0, 1), -1);
        var bX = bH(b)[F](aX),
            bU = 0,
            bW = 0,
            bS = aX,
            bZ;
        aR.is(bO, b) && (bO = this.getFont(bO));
        if (bO) {
            bZ = (bP || 16) / bO.face["units-per-em"];
            var E = bO.face.bbox[F](a),
                bN = +E[0],
                g = E[3] - E[1],
                R = 0,
                bQ = +E[1] + (bY == "baseline" ? g + (+bO.face.descent) : g / 2);
            for (var bT = 0, bM = bX.length; bT < bM; bT++) {
                if (bX[bT] == "\n") {
                    bU = 0;
                    bV = 0;
                    bW = 0;
                    R += g
                } else {
                    var bR = bW && bO.glyphs[bX[bT - 1]] || {}, bV = bO.glyphs[bX[bT]];
                    bU += bW ? (bR.w || bO.w) + (bR.k && bR.k[bX[bT]] || 0) + (bO.w * d) : 0;
                    bW = 1
                } if (bV && bV.d) {
                    bS += aR.transformPath(bV.d, ["t", bU * bZ, R * bZ, "s", bZ, bZ, bN, bQ, "t", (bL - bN) / bZ, (S - bQ) / bZ])
                }
            }
        }
        return this.path(bS).attr({
            fill: "#000",
            stroke: "none"
        })
    };
    a4.add = function (E) {
        if (aR.is(E, "array")) {
            var g = this.set(),
                d = 0,
                R = E.length,
                b;
            for (; d < R; d++) {
                b = E[d] || {};
                bw[ak](b.type) && g.push(this[b.type]().attr(b))
            }
        }
        return g
    };
    aR.format = function (d, g) {
        var b = aR.is(g, bd) ? [0][bE](g) : arguments;
        d && aR.is(d, aj) && b.length - 1 && (d = d.replace(br, function (R, E) {
            return b[++E] == null ? aX : b[E]
        }));
        return d || aX
    };
    aR.fullfill = (function () {
        var g = /\{([^\}]+)\}/g,
            b = /(?:(?:^|\.)(.+?)(?=\[|\.|$|\()|\[('|")(.+?)\2\])(\(\))?/g,
            d = function (R, E, S) {
                var i = S;
                E.replace(b, function (bN, bM, bL, bP, bO) {
                    bM = bM || bP;
                    if (i) {
                        if (bM in i) {
                            i = i[bM]
                        }
                        typeof i == "function" && bO && (i = i())
                    }
                });
                i = (i == null || i == S ? R : i) + "";
                return i
            };
        return function (E, i) {
            return String(E).replace(g, function (S, R) {
                return d(S, R, i)
            })
        }
    })();
    aR.ninja = function () {
        s.was ? (aA.win.Raphael = s.is) : delete Raphael;
        return aR
    };
    aR.st = bc;
    (function (i, d, g) {
        if (i.readyState == null && i.addEventListener) {
            i.addEventListener(d, g = function () {
                i.removeEventListener(d, g, false);
                i.readyState = "complete"
            }, false);
            i.readyState = "loading"
        }

        function b() {
            (/in/).test(i.readyState) ? setTimeout(b, 9) : aR.eve("raphael.DOMload")
        }
        b()
    })(document, "DOMContentLoaded");
    s.was ? (aA.win.Raphael = aR) : (Raphael = aR);
    eve.on("raphael.DOMload", function () {
        ao = true
    })
})();
window.Raphael && window.Raphael.svg && function (l) {
    var d = "hasOwnProperty",
        B = String,
        n = parseFloat,
        q = parseInt,
        f = Math,
        C = f.max,
        s = f.abs,
        h = f.pow,
        g = /[, ]+/,
        z = l.eve,
        r = "",
        j = " ";
    var o = "http://www.w3.org/1999/xlink",
        y = {
            block: "M5,0 0,2.5 5,5z",
            classic: "M5,0 0,2.5 5,5 3.5,3 3.5,2z",
            diamond: "M2.5,0 5,2.5 2.5,5 0,2.5z",
            open: "M6,1 1,3.5 6,6",
            oval: "M2.5,0A2.5,2.5,0,0,1,2.5,5 2.5,2.5,0,0,1,2.5,0z"
        }, u = {};
    l.toString = function () {
        return "Your browser supports SVG.\nYou are running Rapha\xebl " + this.version
    };
    var i = function (F, D) {
        if (D) {
            if (typeof F == "string") {
                F = i(F)
            }
            for (var E in D) {
                if (D[d](E)) {
                    if (E.substring(0, 6) == "xlink:") {
                        F.setAttributeNS(o, E.substring(6), B(D[E]))
                    } else {
                        F.setAttribute(E, B(D[E]))
                    }
                }
            }
        } else {
            F = l._g.doc.createElementNS("http://www.w3.org/2000/svg", F);
            F.style && (F.style.webkitTapHighlightColor = "rgba(0,0,0,0)")
        }
        return F
    }, a = function (M, Q) {
            var O = "linear",
                E = M.id + Q,
                K = 0.5,
                I = 0.5,
                G = M.node,
                D = M.paper,
                S = G.style,
                F = l._g.doc.getElementById(E);
            if (!F) {
                Q = B(Q).replace(l._radial_gradient, function (V, T, W) {
                    O = "radial";
                    if (T && W) {
                        K = n(T);
                        I = n(W);
                        var U = ((I > 0.5) * 2 - 1);
                        h(K - 0.5, 2) + h(I - 0.5, 2) > 0.25 && (I = f.sqrt(0.25 - h(K - 0.5, 2)) * U + 0.5) && I != 0.5 && (I = I.toFixed(5) - 0.00001 * U)
                    }
                    return r
                });
                Q = Q.split(/\s*\-\s*/);
                if (O == "linear") {
                    var J = Q.shift();
                    J = -n(J);
                    if (isNaN(J)) {
                        return null
                    }
                    var H = [0, 0, f.cos(l.rad(J)), f.sin(l.rad(J))],
                        P = 1 / (C(s(H[2]), s(H[3])) || 1);
                    H[2] *= P;
                    H[3] *= P;
                    if (H[2] < 0) {
                        H[0] = -H[2];
                        H[2] = 0
                    }
                    if (H[3] < 0) {
                        H[1] = -H[3];
                        H[3] = 0
                    }
                }
                var N = l._parseDots(Q);
                if (!N) {
                    return null
                }
                E = E.replace(/[\(\)\s,\xb0#]/g, "_");
                if (M.gradient && E != M.gradient.id) {
                    D.defs.removeChild(M.gradient);
                    delete M.gradient
                }
                if (!M.gradient) {
                    F = i(O + "Gradient", {
                        id: E
                    });
                    M.gradient = F;
                    i(F, O == "radial" ? {
                        fx: K,
                        fy: I
                    } : {
                        x1: H[0],
                        y1: H[1],
                        x2: H[2],
                        y2: H[3],
                        gradientTransform: M.matrix.invert()
                    });
                    D.defs.appendChild(F);
                    for (var L = 0, R = N.length; L < R; L++) {
                        F.appendChild(i("stop", {
                            offset: N[L].offset ? N[L].offset : L ? "100%" : "0%",
                            "stop-color": N[L].color || "#fff"
                        }))
                    }
                }
            }
            i(G, {
                fill: "url(#" + E + ")",
                opacity: 1,
                "fill-opacity": 1
            });
            S.fill = r;
            S.opacity = 1;
            S.fillOpacity = 1;
            return 1
        }, b = function (E) {
            var D = E.getBBox(1);
            i(E.pattern, {
                patternTransform: E.matrix.invert() + " translate(" + D.x + "," + D.y + ")"
            })
        }, c = function (O, Q, J) {
            if (O.type == "path") {
                var D = B(Q).toLowerCase().split("-"),
                    N = O.paper,
                    ab = J ? "end" : "start",
                    S = O.node,
                    P = O.attrs,
                    I = P["stroke-width"],
                    W = D.length,
                    G = "classic",
                    V, F, L, T, R, K = 3,
                    X = 3,
                    M = 5;
                while (W--) {
                    switch (D[W]) {
                    case "block":
                    case "classic":
                    case "oval":
                    case "diamond":
                    case "open":
                    case "none":
                        G = D[W];
                        break;
                    case "wide":
                        X = 5;
                        break;
                    case "narrow":
                        X = 2;
                        break;
                    case "long":
                        K = 5;
                        break;
                    case "short":
                        K = 2;
                        break
                    }
                }
                if (G == "open") {
                    K += 2;
                    X += 2;
                    M += 2;
                    L = 1;
                    T = J ? 4 : 1;
                    R = {
                        fill: "none",
                        stroke: P.stroke
                    }
                } else {
                    T = L = K / 2;
                    R = {
                        fill: P.stroke,
                        stroke: "none"
                    }
                } if (O._.arrows) {
                    if (J) {
                        O._.arrows.endPath && u[O._.arrows.endPath]--;
                        O._.arrows.endMarker && u[O._.arrows.endMarker]--
                    } else {
                        O._.arrows.startPath && u[O._.arrows.startPath]--;
                        O._.arrows.startMarker && u[O._.arrows.startMarker]--
                    }
                } else {
                    O._.arrows = {}
                } if (G != "none") {
                    var E = "raphael-marker-" + G,
                        aa = "raphael-marker-" + ab + G + K + X;
                    if (!l._g.doc.getElementById(E)) {
                        N.defs.appendChild(i(i("path"), {
                            "stroke-linecap": "round",
                            d: y[G],
                            id: E
                        }));
                        u[E] = 1
                    } else {
                        u[E]++
                    }
                    var H = l._g.doc.getElementById(aa),
                        U;
                    if (!H) {
                        H = i(i("marker"), {
                            id: aa,
                            markerHeight: X,
                            markerWidth: K,
                            orient: "auto",
                            refX: T,
                            refY: X / 2
                        });
                        U = i(i("use"), {
                            "xlink:href": "#" + E,
                            transform: (J ? "rotate(180 " + K / 2 + " " + X / 2 + ") " : r) + "scale(" + K / M + "," + X / M + ")",
                            "stroke-width": (1 / ((K / M + X / M) / 2)).toFixed(4)
                        });
                        H.appendChild(U);
                        N.defs.appendChild(H);
                        u[aa] = 1
                    } else {
                        u[aa]++;
                        U = H.getElementsByTagName("use")[0]
                    }
                    i(U, R);
                    var Z = L * (G != "diamond" && G != "oval");
                    if (J) {
                        V = O._.arrows.startdx * I || 0;
                        F = l.getTotalLength(P.path) - Z * I
                    } else {
                        V = Z * I;
                        F = l.getTotalLength(P.path) - (O._.arrows.enddx * I || 0)
                    }
                    R = {};
                    R["marker-" + ab] = "url(#" + aa + ")";
                    if (F || V) {
                        R.d = Raphael.getSubpath(P.path, V, F)
                    }
                    i(S, R);
                    O._.arrows[ab + "Path"] = E;
                    O._.arrows[ab + "Marker"] = aa;
                    O._.arrows[ab + "dx"] = Z;
                    O._.arrows[ab + "Type"] = G;
                    O._.arrows[ab + "String"] = Q
                } else {
                    if (J) {
                        V = O._.arrows.startdx * I || 0;
                        F = l.getTotalLength(P.path) - V
                    } else {
                        V = 0;
                        F = l.getTotalLength(P.path) - (O._.arrows.enddx * I || 0)
                    }
                    O._.arrows[ab + "Path"] && i(S, {
                        d: Raphael.getSubpath(P.path, V, F)
                    });
                    delete O._.arrows[ab + "Path"];
                    delete O._.arrows[ab + "Marker"];
                    delete O._.arrows[ab + "dx"];
                    delete O._.arrows[ab + "Type"];
                    delete O._.arrows[ab + "String"]
                }
                for (R in u) {
                    if (u[d](R) && !u[R]) {
                        var Y = l._g.doc.getElementById(R);
                        Y && Y.parentNode.removeChild(Y)
                    }
                }
            }
        }, v = {
            "": [0],
            none: [0],
            "-": [3, 1],
            ".": [1, 1],
            "-.": [3, 1, 1, 1],
            "-..": [3, 1, 1, 1, 1, 1],
            ". ": [1, 3],
            "- ": [4, 3],
            "--": [8, 3],
            "- .": [4, 3, 1, 3],
            "--.": [8, 3, 1, 3],
            "--..": [8, 3, 1, 3, 1, 3]
        }, k = function (J, H, I) {
            H = v[B(H).toLowerCase()];
            if (H) {
                var F = J.attrs["stroke-width"] || "1",
                    D = {
                        round: F,
                        square: F,
                        butt: 0
                    }[J.attrs["stroke-linecap"] || I["stroke-linecap"]] || 0,
                    G = [],
                    E = H.length;
                while (E--) {
                    G[E] = H[E] * F + ((E % 2) ? 1 : -1) * D
                }
                i(J.node, {
                    "stroke-dasharray": G.join(",")
                })
            }
        }, w = function (O, W) {
            var S = O.node,
                P = O.attrs,
                M = S.style.visibility;
            S.style.visibility = "hidden";
            for (var R in W) {
                if (W[d](R)) {
                    if (!l._availableAttrs[d](R)) {
                        continue
                    }
                    var Q = W[R];
                    P[R] = Q;
                    switch (R) {
                    case "blur":
                        O.blur(Q);
                        break;
                    case "href":
                    case "title":
                    case "target":
                        var U = S.parentNode;
                        if (U.tagName.toLowerCase() != "a") {
                            var H = i("a");
                            U.insertBefore(H, S);
                            H.appendChild(S);
                            U = H
                        }
                        if (R == "target") {
                            U.setAttributeNS(o, "show", Q == "blank" ? "new" : Q)
                        } else {
                            U.setAttributeNS(o, R, Q)
                        }
                        break;
                    case "cursor":
                        S.style.cursor = Q;
                        break;
                    case "transform":
                        O.transform(Q);
                        break;
                    case "arrow-start":
                        c(O, Q);
                        break;
                    case "arrow-end":
                        c(O, Q, 1);
                        break;
                    case "clip-rect":
                        var E = B(Q).split(g);
                        if (E.length == 4) {
                            O.clip && O.clip.parentNode.parentNode.removeChild(O.clip.parentNode);
                            var F = i("clipPath"),
                                T = i("rect");
                            F.id = l.createUUID();
                            i(T, {
                                x: E[0],
                                y: E[1],
                                width: E[2],
                                height: E[3]
                            });
                            F.appendChild(T);
                            O.paper.defs.appendChild(F);
                            i(S, {
                                "clip-path": "url(#" + F.id + ")"
                            });
                            O.clip = T
                        }
                        if (!Q) {
                            var N = S.getAttribute("clip-path");
                            if (N) {
                                var V = l._g.doc.getElementById(N.replace(/(^url\(#|\)$)/g, r));
                                V && V.parentNode.removeChild(V);
                                i(S, {
                                    "clip-path": r
                                });
                                delete O.clip
                            }
                        }
                        break;
                    case "path":
                        if (O.type == "path") {
                            i(S, {
                                d: Q ? P.path = l._pathToAbsolute(Q) : "M0,0"
                            });
                            O._.dirty = 1;
                            if (O._.arrows) {
                                "startString" in O._.arrows && c(O, O._.arrows.startString);
                                "endString" in O._.arrows && c(O, O._.arrows.endString, 1)
                            }
                        }
                        break;
                    case "width":
                        S.setAttribute(R, Q);
                        O._.dirty = 1;
                        if (P.fx) {
                            R = "x";
                            Q = P.x
                        } else {
                            break
                        }
                    case "x":
                        if (P.fx) {
                            Q = -P.x - (P.width || 0)
                        }
                    case "rx":
                        if (R == "rx" && O.type == "rect") {
                            break
                        }
                    case "cx":
                        S.setAttribute(R, Q);
                        O.pattern && b(O);
                        O._.dirty = 1;
                        break;
                    case "height":
                        S.setAttribute(R, Q);
                        O._.dirty = 1;
                        if (P.fy) {
                            R = "y";
                            Q = P.y
                        } else {
                            break
                        }
                    case "y":
                        if (P.fy) {
                            Q = -P.y - (P.height || 0)
                        }
                    case "ry":
                        if (R == "ry" && O.type == "rect") {
                            break
                        }
                    case "cy":
                        S.setAttribute(R, Q);
                        O.pattern && b(O);
                        O._.dirty = 1;
                        break;
                    case "r":
                        if (O.type == "rect") {
                            i(S, {
                                rx: Q,
                                ry: Q
                            })
                        } else {
                            S.setAttribute(R, Q)
                        }
                        O._.dirty = 1;
                        break;
                    case "src":
                        if (O.type == "image") {
                            S.setAttributeNS(o, "href", Q)
                        }
                        break;
                    case "stroke-width":
                        if (O._.sx != 1 || O._.sy != 1) {
                            Q /= C(s(O._.sx), s(O._.sy)) || 1
                        }
                        if (O.paper._vbSize) {
                            Q *= O.paper._vbSize
                        }
                        S.setAttribute(R, Q);
                        if (P["stroke-dasharray"]) {
                            k(O, P["stroke-dasharray"], W)
                        }
                        if (O._.arrows) {
                            "startString" in O._.arrows && c(O, O._.arrows.startString);
                            "endString" in O._.arrows && c(O, O._.arrows.endString, 1)
                        }
                        break;
                    case "stroke-dasharray":
                        k(O, Q, W);
                        break;
                    case "fill":
                        var I = B(Q).match(l._ISURL);
                        if (I) {
                            F = i("pattern");
                            var L = i("image");
                            F.id = l.createUUID();
                            i(F, {
                                x: 0,
                                y: 0,
                                patternUnits: "userSpaceOnUse",
                                height: 1,
                                width: 1
                            });
                            i(L, {
                                x: 0,
                                y: 0,
                                "xlink:href": I[1]
                            });
                            F.appendChild(L);
                            (function (X) {
                                l._preload(I[1], function () {
                                    var Y = this.offsetWidth,
                                        Z = this.offsetHeight;
                                    i(X, {
                                        width: Y,
                                        height: Z
                                    });
                                    i(L, {
                                        width: Y,
                                        height: Z
                                    });
                                    O.paper.safari()
                                })
                            })(F);
                            O.paper.defs.appendChild(F);
                            i(S, {
                                fill: "url(#" + F.id + ")"
                            });
                            O.pattern = F;
                            O.pattern && b(O);
                            break
                        }
                        var G = l.getRGB(Q);
                        if (!G.error) {
                            delete W.gradient;
                            delete P.gradient;
                            !l.is(P.opacity, "undefined") && l.is(W.opacity, "undefined") && i(S, {
                                opacity: P.opacity
                            });
                            !l.is(P["fill-opacity"], "undefined") && l.is(W["fill-opacity"], "undefined") && i(S, {
                                "fill-opacity": P["fill-opacity"]
                            })
                        } else {
                            if ((O.type == "circle" || O.type == "ellipse" || B(Q).charAt() != "r") && a(O, Q)) {
                                if ("opacity" in P || "fill-opacity" in P) {
                                    var D = l._g.doc.getElementById(S.getAttribute("fill").replace(/^url\(#|\)$/g, r));
                                    if (D) {
                                        var J = D.getElementsByTagName("stop");
                                        i(J[J.length - 1], {
                                            "stop-opacity": ("opacity" in P ? P.opacity : 1) * ("fill-opacity" in P ? P["fill-opacity"] : 1)
                                        })
                                    }
                                }
                                P.gradient = Q;
                                P.fill = "none";
                                break
                            }
                        }
                        G[d]("opacity") && i(S, {
                            "fill-opacity": G.opacity > 1 ? G.opacity / 100 : G.opacity
                        });
                    case "stroke":
                        G = l.getRGB(Q);
                        S.setAttribute(R, G.hex);
                        R == "stroke" && G[d]("opacity") && i(S, {
                            "stroke-opacity": G.opacity > 1 ? G.opacity / 100 : G.opacity
                        });
                        if (R == "stroke" && O._.arrows) {
                            "startString" in O._.arrows && c(O, O._.arrows.startString);
                            "endString" in O._.arrows && c(O, O._.arrows.endString, 1)
                        }
                        break;
                    case "gradient":
                        (O.type == "circle" || O.type == "ellipse" || B(Q).charAt() != "r") && a(O, Q);
                        break;
                    case "opacity":
                        if (P.gradient && !P[d]("stroke-opacity")) {
                            i(S, {
                                "stroke-opacity": Q > 1 ? Q / 100 : Q
                            })
                        }
                    case "fill-opacity":
                        if (P.gradient) {
                            D = l._g.doc.getElementById(S.getAttribute("fill").replace(/^url\(#|\)$/g, r));
                            if (D) {
                                J = D.getElementsByTagName("stop");
                                i(J[J.length - 1], {
                                    "stop-opacity": Q
                                })
                            }
                            break
                        }
                    default:
                        R == "font-size" && (Q = q(Q, 10) + "px");
                        var K = R.replace(/(\-.)/g, function (X) {
                            return X.substring(1).toUpperCase()
                        });
                        S.style[K] = Q;
                        O._.dirty = 1;
                        S.setAttribute(R, Q);
                        break
                    }
                }
            }
            p(O, W);
            S.style.visibility = M
        }, A = 1.2,
        p = function (D, H) {
            if (D.type != "text" || !(H[d]("text") || H[d]("font") || H[d]("font-size") || H[d]("x") || H[d]("y"))) {
                return
            }
            var M = D.attrs,
                F = D.node,
                O = F.firstChild ? q(l._g.doc.defaultView.getComputedStyle(F.firstChild, r).getPropertyValue("font-size"), 10) : 10;
            if (H[d]("text")) {
                M.text = H.text;
                while (F.firstChild) {
                    F.removeChild(F.firstChild)
                }
                var G = B(H.text).split("\n"),
                    E = [],
                    K;
                for (var I = 0, N = G.length; I < N; I++) {
                    K = i("tspan");
                    I && i(K, {
                        dy: O * A,
                        x: M.x
                    });
                    K.appendChild(l._g.doc.createTextNode(G[I]));
                    F.appendChild(K);
                    E[I] = K
                }
            } else {
                E = F.getElementsByTagName("tspan");
                for (I = 0, N = E.length; I < N; I++) {
                    if (I) {
                        i(E[I], {
                            dy: O * A,
                            x: M.x
                        })
                    } else {
                        i(E[0], {
                            dy: 0
                        })
                    }
                }
            }
            i(F, {
                x: M.x,
                y: M.y
            });
            D._.dirty = 1;
            var J = D._getBBox(),
                L = M.y - (J.y + J.height / 2);
            L && l.is(L, "finite") && i(E[0], {
                dy: L
            })
        }, t = function (E, D) {
            var G = 0,
                F = 0;
            this[0] = this.node = E;
            E.raphael = true;
            this.id = l._oid++;
            E.raphaelid = this.id;
            this.matrix = l.matrix();
            this.realPath = null;
            this.paper = D;
            this.attrs = this.attrs || {};
            this._ = {
                transform: [],
                sx: 1,
                sy: 1,
                deg: 0,
                dx: 0,
                dy: 0,
                dirty: 1
            };
            !D.bottom && (D.bottom = this);
            this.prev = D.top;
            D.top && (D.top.next = this);
            D.top = this;
            this.next = null
        }, m = l.el;
    t.prototype = m;
    m.constructor = t;
    l._engine.path = function (D, G) {
        var E = i("path");
        G.canvas && G.canvas.appendChild(E);
        var F = new t(E, G);
        F.type = "path";
        w(F, {
            fill: "none",
            stroke: "#000",
            path: D
        });
        return F
    };
    m.rotate = function (E, D, G) {
        if (this.removed) {
            return this
        }
        E = B(E).split(g);
        if (E.length - 1) {
            D = n(E[1]);
            G = n(E[2])
        }
        E = n(E[0]);
        (G == null) && (D = G);
        if (D == null || G == null) {
            var F = this.getBBox(1);
            D = F.x + F.width / 2;
            G = F.y + F.height / 2
        }
        this.transform(this._.transform.concat([
            ["r", E, D, G]
        ]));
        return this
    };
    m.scale = function (H, F, D, G) {
        if (this.removed) {
            return this
        }
        H = B(H).split(g);
        if (H.length - 1) {
            F = n(H[1]);
            D = n(H[2]);
            G = n(H[3])
        }
        H = n(H[0]);
        (F == null) && (F = H);
        (G == null) && (D = G);
        if (D == null || G == null) {
            var E = this.getBBox(1)
        }
        D = D == null ? E.x + E.width / 2 : D;
        G = G == null ? E.y + E.height / 2 : G;
        this.transform(this._.transform.concat([
            ["s", H, F, D, G]
        ]));
        return this
    };
    m.translate = function (E, D) {
        if (this.removed) {
            return this
        }
        E = B(E).split(g);
        if (E.length - 1) {
            D = n(E[1])
        }
        E = n(E[0]) || 0;
        D = +D || 0;
        this.transform(this._.transform.concat([
            ["t", E, D]
        ]));
        return this
    };
    m.transform = function (E) {
        var F = this._;
        if (E == null) {
            return F.transform
        }
        l._extractTransform(this, E);
        this.clip && i(this.clip, {
            transform: this.matrix.invert()
        });
        this.pattern && b(this);
        this.node && i(this.node, {
            transform: this.matrix
        });
        if (F.sx != 1 || F.sy != 1) {
            var D = this.attrs[d]("stroke-width") ? this.attrs["stroke-width"] : 1;
            this.attr({
                "stroke-width": D
            })
        }
        return this
    };
    m.hide = function () {
        !this.removed && this.paper.safari(this.node.style.display = "none");
        return this
    };
    m.show = function () {
        !this.removed && this.paper.safari(this.node.style.display = "");
        return this
    };
    m.remove = function () {
        if (this.removed || !this.node.parentNode) {
            return
        }
        var E = this.paper;
        E.__set__ && E.__set__.exclude(this);
        z.unbind("raphael.*.*." + this.id);
        if (this.gradient) {
            E.defs.removeChild(this.gradient)
        }
        l._tear(this, E);
        if (this.node.parentNode.tagName.toLowerCase() == "a") {
            this.node.parentNode.parentNode.removeChild(this.node.parentNode)
        } else {
            this.node.parentNode.removeChild(this.node)
        }
        for (var D in this) {
            this[D] = typeof this[D] == "function" ? l._removedFactory(D) : null
        }
        this.removed = true
    };
    m._getBBox = function () {
        if (this.node.style.display == "none") {
            this.show();
            var D = true
        }
        var F = {};
        try {
            F = this.node.getBBox()
        } catch (E) {} finally {
            F = F || {}
        }
        D && this.hide();
        return F
    };
    m.attr = function (D, M) {
        if (this.removed) {
            return this
        }
        if (D == null) {
            var J = {};
            for (var L in this.attrs) {
                if (this.attrs[d](L)) {
                    J[L] = this.attrs[L]
                }
            }
            J.gradient && J.fill == "none" && (J.fill = J.gradient) && delete J.gradient;
            J.transform = this._.transform;
            return J
        }
        if (M == null && l.is(D, "string")) {
            if (D == "fill" && this.attrs.fill == "none" && this.attrs.gradient) {
                return this.attrs.gradient
            }
            if (D == "transform") {
                return this._.transform
            }
            var K = D.split(g),
                G = {};
            for (var H = 0, O = K.length; H < O; H++) {
                D = K[H];
                if (D in this.attrs) {
                    G[D] = this.attrs[D]
                } else {
                    if (l.is(this.paper.customAttributes[D], "function")) {
                        G[D] = this.paper.customAttributes[D].def
                    } else {
                        G[D] = l._availableAttrs[D]
                    }
                }
            }
            return O - 1 ? G : G[K[0]]
        }
        if (M == null && l.is(D, "array")) {
            G = {};
            for (H = 0, O = D.length; H < O; H++) {
                G[D[H]] = this.attr(D[H])
            }
            return G
        }
        if (M != null) {
            var E = {};
            E[D] = M
        } else {
            if (D != null && l.is(D, "object")) {
                E = D
            }
        }
        for (var N in E) {
            z("raphael.attr." + N + "." + this.id, this, E[N])
        }
        for (N in this.paper.customAttributes) {
            if (this.paper.customAttributes[d](N) && E[d](N) && l.is(this.paper.customAttributes[N], "function")) {
                var I = this.paper.customAttributes[N].apply(this, [].concat(E[N]));
                this.attrs[N] = E[N];
                for (var F in I) {
                    if (I[d](F)) {
                        E[F] = I[F]
                    }
                }
            }
        }
        w(this, E);
        return this
    };
    m.toFront = function () {
        if (this.removed) {
            return this
        }
        if (this.node.parentNode.tagName.toLowerCase() == "a") {
            this.node.parentNode.parentNode.appendChild(this.node.parentNode)
        } else {
            this.node.parentNode.appendChild(this.node)
        }
        var D = this.paper;
        D.top != this && l._tofront(this, D);
        return this
    };
    m.toBack = function () {
        if (this.removed) {
            return this
        }
        var E = this.node.parentNode;
        if (E.tagName.toLowerCase() == "a") {
            E.parentNode.insertBefore(this.node.parentNode, this.node.parentNode.parentNode.firstChild)
        } else {
            if (E.firstChild != this.node) {
                E.insertBefore(this.node, this.node.parentNode.firstChild)
            }
        }
        l._toback(this, this.paper);
        var D = this.paper;
        return this
    };
    m.insertAfter = function (D) {
        if (this.removed) {
            return this
        }
        var E = D.node || D[D.length - 1].node;
        if (E.nextSibling) {
            E.parentNode.insertBefore(this.node, E.nextSibling)
        } else {
            E.parentNode.appendChild(this.node)
        }
        l._insertafter(this, D, this.paper);
        return this
    };
    m.insertBefore = function (D) {
        if (this.removed) {
            return this
        }
        var E = D.node || D[0].node;
        E.parentNode.insertBefore(this.node, E);
        l._insertbefore(this, D, this.paper);
        return this
    };
    m.blur = function (E) {
        var D = this;
        if (+E !== 0) {
            var F = i("filter"),
                G = i("feGaussianBlur");
            D.attrs.blur = E;
            F.id = l.createUUID();
            i(G, {
                stdDeviation: +E || 1.5
            });
            F.appendChild(G);
            D.paper.defs.appendChild(F);
            D._blur = F;
            i(D.node, {
                filter: "url(#" + F.id + ")"
            })
        } else {
            if (D._blur) {
                D._blur.parentNode.removeChild(D._blur);
                delete D._blur;
                delete D.attrs.blur
            }
            D.node.removeAttribute("filter")
        }
    };
    l._engine.circle = function (E, D, I, H) {
        var G = i("circle");
        E.canvas && E.canvas.appendChild(G);
        var F = new t(G, E);
        F.attrs = {
            cx: D,
            cy: I,
            r: H,
            fill: "none",
            stroke: "#000"
        };
        F.type = "circle";
        i(G, F.attrs);
        return F
    };
    l._engine.rect = function (F, D, K, E, I, J) {
        var H = i("rect");
        F.canvas && F.canvas.appendChild(H);
        var G = new t(H, F);
        G.attrs = {
            x: D,
            y: K,
            width: E,
            height: I,
            r: J || 0,
            rx: J || 0,
            ry: J || 0,
            fill: "none",
            stroke: "#000"
        };
        G.type = "rect";
        i(H, G.attrs);
        return G
    };
    l._engine.ellipse = function (E, D, J, I, H) {
        var G = i("ellipse");
        E.canvas && E.canvas.appendChild(G);
        var F = new t(G, E);
        F.attrs = {
            cx: D,
            cy: J,
            rx: I,
            ry: H,
            fill: "none",
            stroke: "#000"
        };
        F.type = "ellipse";
        i(G, F.attrs);
        return F
    };
    l._engine.image = function (F, J, D, K, E, I) {
        var H = i("image");
        i(H, {
            x: D,
            y: K,
            width: E,
            height: I,
            preserveAspectRatio: "none"
        });
        H.setAttributeNS(o, "href", J);
        F.canvas && F.canvas.appendChild(H);
        var G = new t(H, F);
        G.attrs = {
            x: D,
            y: K,
            width: E,
            height: I,
            src: J
        };
        G.type = "image";
        return G
    };
    l._engine.text = function (E, D, I, H) {
        var G = i("text");
        E.canvas && E.canvas.appendChild(G);
        var F = new t(G, E);
        F.attrs = {
            x: D,
            y: I,
            "text-anchor": "middle",
            text: H,
            font: l._availableAttrs.font,
            stroke: "none",
            fill: "#000"
        };
        F.type = "text";
        w(F, F.attrs);
        return F
    };
    l._engine.setSize = function (E, D) {
        this.width = E || this.width;
        this.height = D || this.height;
        this.canvas.setAttribute("width", this.width);
        this.canvas.setAttribute("height", this.height);
        if (this._viewBox) {
            this.setViewBox.apply(this, this._viewBox)
        }
        return this
    };
    l._engine.create = function () {
        var G = l._getContainer.apply(0, arguments),
            E = G && G.container,
            K = G.x,
            J = G.y,
            F = G.width,
            L = G.height;
        if (!E) {
            throw new Error("SVG container not found.")
        }
        var D = i("svg"),
            I = "overflow:hidden;",
            H;
        K = K || 0;
        J = J || 0;
        F = F || 512;
        L = L || 342;
        i(D, {
            height: L,
            version: 1.1,
            width: F,
            xmlns: "http://www.w3.org/2000/svg"
        });
        if (E == 1) {
            D.style.cssText = I + "position:absolute;left:" + K + "px;top:" + J + "px";
            l._g.doc.body.appendChild(D);
            H = 1
        } else {
            D.style.cssText = I + "position:relative";
            if (E.firstChild) {
                E.insertBefore(D, E.firstChild)
            } else {
                E.appendChild(D)
            }
        }
        E = new l._Paper;
        E.width = F;
        E.height = L;
        E.canvas = D;
        E.clear();
        E._left = E._top = 0;
        H && (E.renderfix = function () {});
        E.renderfix();
        return E
    };
    l._engine.setViewBox = function (I, G, K, D, E) {
        z("raphael.setViewBox", this, this._viewBox, [I, G, K, D, E]);
        var M = C(K / this.width, D / this.height),
            H = this.top,
            L = E ? "meet" : "xMinYMin",
            F, J;
        if (I == null) {
            if (this._vbSize) {
                M = 1
            }
            delete this._vbSize;
            F = "0 0 " + this.width + j + this.height
        } else {
            this._vbSize = M;
            F = I + j + G + j + K + j + D
        }
        i(this.canvas, {
            viewBox: F,
            preserveAspectRatio: L
        });
        while (M && H) {
            J = "stroke-width" in H.attrs ? H.attrs["stroke-width"] : 1;
            H.attr({
                "stroke-width": J
            });
            H._.dirty = 1;
            H._.dirtyT = 1;
            H = H.prev
        }
        this._viewBox = [I, G, K, D, !! E];
        return this
    };
    l.prototype.renderfix = function () {
        var I = this.canvas,
            D = I.style,
            H;
        try {
            H = I.getScreenCTM() || I.createSVGMatrix()
        } catch (G) {
            H = I.createSVGMatrix()
        }
        var F = -H.e % 1,
            E = -H.f % 1;
        if (F || E) {
            if (F) {
                this._left = (this._left + F) % 1;
                D.left = this._left + "px"
            }
            if (E) {
                this._top = (this._top + E) % 1;
                D.top = this._top + "px"
            }
        }
    };
    l.prototype.clear = function () {
        l.eve("raphael.clear", this);
        var D = this.canvas;
        while (D.firstChild) {
            D.removeChild(D.firstChild)
        }
        this.bottom = this.top = null;
        (this.desc = i("desc")).appendChild(l._g.doc.createTextNode("Created with Rapha\xebl " + l.version));
        D.appendChild(this.desc);
        D.appendChild(this.defs = i("defs"))
    };
    l.prototype.remove = function () {
        z("raphael.remove", this);
        this.canvas.parentNode && this.canvas.parentNode.removeChild(this.canvas);
        for (var D in this) {
            this[D] = typeof this[D] == "function" ? l._removedFactory(D) : null
        }
    };
    var x = l.st;
    for (var e in m) {
        if (m[d](e) && !x[d](e)) {
            x[e] = (function (D) {
                return function () {
                    var E = arguments;
                    return this.forEach(function (F) {
                        F[D].apply(F, E)
                    })
                }
            })(e)
        }
    }
}(window.Raphael);
window.Raphael && window.Raphael.vml && function (l) {
    var e = "hasOwnProperty",
        F = String,
        n = parseFloat,
        h = Math,
        B = h.round,
        I = h.max,
        C = h.min,
        s = h.abs,
        v = "fill",
        i = /[, ]+/,
        A = l.eve,
        w = " progid:DXImageTransform.Microsoft",
        k = " ",
        q = "",
        D = {
            M: "m",
            L: "l",
            C: "c",
            Z: "x",
            m: "t",
            l: "r",
            c: "v",
            z: "x"
        }, j = /([clmz]),?([^clmz]*)/gi,
        t = / progid:\S+Blur\([^\)]+\)/g,
        H = /-?[^,\s-]+/g,
        d = "position:absolute;left:0;top:0;width:1px;height:1px",
        b = 21600,
        z = {
            path: 1,
            rect: 1,
            image: 1
        }, r = {
            circle: 1,
            ellipse: 1
        }, f = function (S) {
            var P = /[ahqstv]/ig,
                K = l._pathToAbsolute;
            F(S).match(P) && (K = l._path2curve);
            P = /[clmz]/g;
            if (K == l._pathToAbsolute && !F(S).match(P)) {
                var O = F(S).replace(j, function (W, Y, U) {
                    var X = [],
                        T = Y.toLowerCase() == "m",
                        V = D[Y];
                    U.replace(H, function (Z) {
                        if (T && X.length == 2) {
                            V += X + D[Y == "m" ? "l" : "L"];
                            X = []
                        }
                        X.push(B(Z * b))
                    });
                    return V + X
                });
                return O
            }
            var Q = K(S),
                J, E;
            O = [];
            for (var M = 0, R = Q.length; M < R; M++) {
                J = Q[M];
                E = Q[M][0].toLowerCase();
                E == "z" && (E = "x");
                for (var L = 1, N = J.length; L < N; L++) {
                    E += B(J[L] * b) + (L != N - 1 ? "," : q)
                }
                O.push(E)
            }
            return O.join(k)
        }, o = function (L, K, J) {
            var E = l.matrix();
            E.rotate(-L, 0.5, 0.5);
            return {
                dx: E.x(K, J),
                dy: E.y(K, J)
            }
        }, p = function (R, Q, P, M, L, N) {
            var Z = R._,
                T = R.matrix,
                E = Z.fillpos,
                S = R.node,
                O = S.style,
                K = 1,
                J = "",
                V, X = b / Q,
                W = b / P;
            O.visibility = "hidden";
            if (!Q || !P) {
                return
            }
            S.coordsize = s(X) + k + s(W);
            O.rotation = N * (Q * P < 0 ? -1 : 1);
            if (N) {
                var Y = o(N, M, L);
                M = Y.dx;
                L = Y.dy
            }
            Q < 0 && (J += "x");
            P < 0 && (J += " y") && (K = -1);
            O.flip = J;
            S.coordorigin = (M * -X) + k + (L * -W);
            if (E || Z.fillsize) {
                var U = S.getElementsByTagName(v);
                U = U && U[0];
                S.removeChild(U);
                if (E) {
                    Y = o(N, T.x(E[0], E[1]), T.y(E[0], E[1]));
                    U.position = Y.dx * K + k + Y.dy * K
                }
                if (Z.fillsize) {
                    U.size = Z.fillsize[0] * s(Q) + k + Z.fillsize[1] * s(P)
                }
                S.appendChild(U)
            }
            O.visibility = "visible"
        };
    l.toString = function () {
        return "Your browser doesn\u2019t support SVG. Falling down to VML.\nYou are running Rapha\xebl " + this.version
    };
    var c = function (E, O, J) {
        var Q = F(O).toLowerCase().split("-"),
            M = J ? "end" : "start",
            K = Q.length,
            N = "classic",
            P = "medium",
            L = "medium";
        while (K--) {
            switch (Q[K]) {
            case "block":
            case "classic":
            case "oval":
            case "diamond":
            case "open":
            case "none":
                N = Q[K];
                break;
            case "wide":
            case "narrow":
                L = Q[K];
                break;
            case "long":
            case "short":
                P = Q[K];
                break
            }
        }
        var R = E.node.getElementsByTagName("stroke")[0];
        R[M + "arrow"] = N;
        R[M + "arrowlength"] = P;
        R[M + "arrowwidth"] = L
    }, x = function (Z, aj) {
            Z.attrs = Z.attrs || {};
            var ae = Z.node,
                an = Z.attrs,
                V = ae.style,
                R, ah = z[Z.type] && (aj.x != an.x || aj.y != an.y || aj.width != an.width || aj.height != an.height || aj.cx != an.cx || aj.cy != an.cy || aj.rx != an.rx || aj.ry != an.ry || aj.r != an.r),
                Y = r[Z.type] && (an.cx != aj.cx || an.cy != aj.cy || an.r != aj.r || an.rx != aj.rx || an.ry != aj.ry),
                aq = Z;
            for (var W in aj) {
                if (aj[e](W)) {
                    an[W] = aj[W]
                }
            }
            if (ah) {
                an.path = l._getPath[Z.type](Z);
                Z._.dirty = 1
            }
            aj.href && (ae.href = aj.href);
            aj.title && (ae.title = aj.title);
            aj.target && (ae.target = aj.target);
            aj.cursor && (V.cursor = aj.cursor);
            "blur" in aj && Z.blur(aj.blur);
            if (aj.path && Z.type == "path" || ah) {
                ae.path = f(~F(an.path).toLowerCase().indexOf("r") ? l._pathToAbsolute(an.path) : an.path);
                if (Z.type == "image") {
                    Z._.fillpos = [an.x, an.y];
                    Z._.fillsize = [an.width, an.height];
                    p(Z, 1, 1, 0, 0, 0)
                }
            }
            "transform" in aj && Z.transform(aj.transform);
            if (Y) {
                var M = +an.cx,
                    K = +an.cy,
                    Q = +an.rx || +an.r || 0,
                    P = +an.ry || +an.r || 0;
                ae.path = l.format("ar{0},{1},{2},{3},{4},{1},{4},{1}x", B((M - Q) * b), B((K - P) * b), B((M + Q) * b), B((K + P) * b), B(M * b))
            }
            if ("clip-rect" in aj) {
                var J = F(aj["clip-rect"]).split(i);
                if (J.length == 4) {
                    J[2] = +J[2] + (+J[0]);
                    J[3] = +J[3] + (+J[1]);
                    var X = ae.clipRect || l._g.doc.createElement("div"),
                        ap = X.style;
                    ap.clip = l.format("rect({1}px {2}px {3}px {0}px)", J);
                    if (!ae.clipRect) {
                        ap.position = "absolute";
                        ap.top = 0;
                        ap.left = 0;
                        ap.width = Z.paper.width + "px";
                        ap.height = Z.paper.height + "px";
                        ae.parentNode.insertBefore(X, ae);
                        X.appendChild(ae);
                        ae.clipRect = X
                    }
                }
                if (!aj["clip-rect"]) {
                    ae.clipRect && (ae.clipRect.style.clip = "auto")
                }
            }
            if (Z.textpath) {
                var al = Z.textpath.style;
                aj.font && (al.font = aj.font);
                aj["font-family"] && (al.fontFamily = '"' + aj["font-family"].split(",")[0].replace(/^['"]+|['"]+$/g, q) + '"');
                aj["font-size"] && (al.fontSize = aj["font-size"]);
                aj["font-weight"] && (al.fontWeight = aj["font-weight"]);
                aj["font-style"] && (al.fontStyle = aj["font-style"])
            }
            if ("arrow-start" in aj) {
                c(aq, aj["arrow-start"])
            }
            if ("arrow-end" in aj) {
                c(aq, aj["arrow-end"], 1)
            }
            if (aj.opacity != null || aj["stroke-width"] != null || aj.fill != null || aj.src != null || aj.stroke != null || aj["stroke-width"] != null || aj["stroke-opacity"] != null || aj["fill-opacity"] != null || aj["stroke-dasharray"] != null || aj["stroke-miterlimit"] != null || aj["stroke-linejoin"] != null || aj["stroke-linecap"] != null) {
                var af = ae.getElementsByTagName(v),
                    am = false;
                af = af && af[0];
                !af && (am = af = G(v));
                if (Z.type == "image" && aj.src) {
                    af.src = aj.src
                }
                aj.fill && (af.on = true);
                if (af.on == null || aj.fill == "none" || aj.fill === null) {
                    af.on = false
                }
                if (af.on && aj.fill) {
                    var O = F(aj.fill).match(l._ISURL);
                    if (O) {
                        af.parentNode == ae && ae.removeChild(af);
                        af.rotate = true;
                        af.src = O[1];
                        af.type = "tile";
                        var E = Z.getBBox(1);
                        af.position = E.x + k + E.y;
                        Z._.fillpos = [E.x, E.y];
                        l._preload(O[1], function () {
                            Z._.fillsize = [this.offsetWidth, this.offsetHeight]
                        })
                    } else {
                        af.color = l.getRGB(aj.fill).hex;
                        af.src = q;
                        af.type = "solid";
                        if (l.getRGB(aj.fill).error && (aq.type in {
                            circle: 1,
                            ellipse: 1
                        } || F(aj.fill).charAt() != "r") && a(aq, aj.fill, af)) {
                            an.fill = "none";
                            an.gradient = aj.fill;
                            af.rotate = false
                        }
                    }
                }
                if ("fill-opacity" in aj || "opacity" in aj) {
                    var N = ((+an["fill-opacity"] + 1 || 2) - 1) * ((+an.opacity + 1 || 2) - 1) * ((+l.getRGB(aj.fill).o + 1 || 2) - 1);
                    N = C(I(N, 0), 1);
                    af.opacity = N;
                    if (af.src) {
                        af.color = "none"
                    }
                }
                ae.appendChild(af);
                var S = (ae.getElementsByTagName("stroke") && ae.getElementsByTagName("stroke")[0]),
                    ao = false;
                !S && (ao = S = G("stroke"));
                if ((aj.stroke && aj.stroke != "none") || aj["stroke-width"] || aj["stroke-opacity"] != null || aj["stroke-dasharray"] || aj["stroke-miterlimit"] || aj["stroke-linejoin"] || aj["stroke-linecap"]) {
                    S.on = true
                }(aj.stroke == "none" || aj.stroke === null || S.on == null || aj.stroke == 0 || aj["stroke-width"] == 0) && (S.on = false);
                var ad = l.getRGB(aj.stroke);
                S.on && aj.stroke && (S.color = ad.hex);
                N = ((+an["stroke-opacity"] + 1 || 2) - 1) * ((+an.opacity + 1 || 2) - 1) * ((+ad.o + 1 || 2) - 1);
                var aa = (n(aj["stroke-width"]) || 1) * 0.75;
                N = C(I(N, 0), 1);
                aj["stroke-width"] == null && (aa = an["stroke-width"]);
                aj["stroke-width"] && (S.weight = aa);
                aa && aa < 1 && (N *= aa) && (S.weight = 1);
                S.opacity = N;
                aj["stroke-linejoin"] && (S.joinstyle = aj["stroke-linejoin"] || "miter");
                S.miterlimit = aj["stroke-miterlimit"] || 8;
                aj["stroke-linecap"] && (S.endcap = aj["stroke-linecap"] == "butt" ? "flat" : aj["stroke-linecap"] == "square" ? "square" : "round");
                if (aj["stroke-dasharray"]) {
                    var ac = {
                        "-": "shortdash",
                        ".": "shortdot",
                        "-.": "shortdashdot",
                        "-..": "shortdashdotdot",
                        ". ": "dot",
                        "- ": "dash",
                        "--": "longdash",
                        "- .": "dashdot",
                        "--.": "longdashdot",
                        "--..": "longdashdotdot"
                    };
                    S.dashstyle = ac[e](aj["stroke-dasharray"]) ? ac[aj["stroke-dasharray"]] : q
                }
                ao && ae.appendChild(S)
            }
            if (aq.type == "text") {
                aq.paper.canvas.style.display = q;
                var ag = aq.paper.span,
                    ab = 100,
                    L = an.font && an.font.match(/\d+(?:\.\d*)?(?=px)/);
                V = ag.style;
                an.font && (V.font = an.font);
                an["font-family"] && (V.fontFamily = an["font-family"]);
                an["font-weight"] && (V.fontWeight = an["font-weight"]);
                an["font-style"] && (V.fontStyle = an["font-style"]);
                L = n(an["font-size"] || L && L[0]) || 10;
                V.fontSize = L * ab + "px";
                aq.textpath.string && (ag.innerHTML = F(aq.textpath.string).replace(/</g, "&#60;").replace(/&/g, "&#38;").replace(/\n/g, "<br>"));
                var U = ag.getBoundingClientRect();
                aq.W = an.w = (U.right - U.left) / ab;
                aq.H = an.h = (U.bottom - U.top) / ab;
                aq.X = an.x;
                aq.Y = an.y + aq.H / 2;
                ("x" in aj || "y" in aj) && (aq.path.v = l.format("m{0},{1}l{2},{1}", B(an.x * b), B(an.y * b), B(an.x * b) + 1));
                var T = ["x", "y", "text", "font", "font-family", "font-weight", "font-style", "font-size"];
                for (var ai = 0, ak = T.length; ai < ak; ai++) {
                    if (T[ai] in aj) {
                        aq._.dirty = 1;
                        break
                    }
                }
                switch (an["text-anchor"]) {
                case "start":
                    aq.textpath.style["v-text-align"] = "left";
                    aq.bbx = aq.W / 2;
                    break;
                case "end":
                    aq.textpath.style["v-text-align"] = "right";
                    aq.bbx = -aq.W / 2;
                    break;
                default:
                    aq.textpath.style["v-text-align"] = "center";
                    aq.bbx = 0;
                    break
                }
                aq.textpath.style["v-text-kern"] = true
            }
        }, a = function (E, R, U) {
            E.attrs = E.attrs || {};
            var S = E.attrs,
                L = Math.pow,
                M, N, P = "linear",
                Q = ".5 .5";
            E.attrs.gradient = R;
            R = F(R).replace(l._radial_gradient, function (X, Y, W) {
                P = "radial";
                if (Y && W) {
                    Y = n(Y);
                    W = n(W);
                    L(Y - 0.5, 2) + L(W - 0.5, 2) > 0.25 && (W = h.sqrt(0.25 - L(Y - 0.5, 2)) * ((W > 0.5) * 2 - 1) + 0.5);
                    Q = Y + k + W
                }
                return q
            });
            R = R.split(/\s*\-\s*/);
            if (P == "linear") {
                var J = R.shift();
                J = -n(J);
                if (isNaN(J)) {
                    return null
                }
            }
            var O = l._parseDots(R);
            if (!O) {
                return null
            }
            E = E.shape || E.node;
            if (O.length) {
                E.removeChild(U);
                U.on = true;
                U.method = "none";
                U.color = O[0].color;
                U.color2 = O[O.length - 1].color;
                var V = [];
                for (var K = 0, T = O.length; K < T; K++) {
                    O[K].offset && V.push(O[K].offset + k + O[K].color)
                }
                U.colors = V.length ? V.join() : "0% " + U.color;
                if (P == "radial") {
                    U.type = "gradientTitle";
                    U.focus = "100%";
                    U.focussize = "0 0";
                    U.focusposition = Q;
                    U.angle = 0
                } else {
                    U.type = "gradient";
                    U.angle = (270 - J) % 360
                }
                E.appendChild(U)
            }
            return 1
        }, u = function (J, E) {
            this[0] = this.node = J;
            J.raphael = true;
            this.id = l._oid++;
            J.raphaelid = this.id;
            this.X = 0;
            this.Y = 0;
            this.attrs = {};
            this.paper = E;
            this.matrix = l.matrix();
            this._ = {
                transform: [],
                sx: 1,
                sy: 1,
                dx: 0,
                dy: 0,
                deg: 0,
                dirty: 1,
                dirtyT: 1
            };
            !E.bottom && (E.bottom = this);
            this.prev = E.top;
            E.top && (E.top.next = this);
            E.top = this;
            this.next = null
        };
    var m = l.el;
    u.prototype = m;
    m.constructor = u;
    m.transform = function (M) {
        if (M == null) {
            return this._.transform
        }
        var O = this.paper._viewBoxShift,
            N = O ? "s" + [O.scale, O.scale] + "-1-1t" + [O.dx, O.dy] : q,
            R;
        if (O) {
            R = M = F(M).replace(/\.{3}|\u2026/g, this._.transform || q)
        }
        l._extractTransform(this, N + M);
        var S = this.matrix.clone(),
            U = this.skew,
            K = this.node,
            Q, L = ~F(this.attrs.fill).indexOf("-"),
            E = !F(this.attrs.fill).indexOf("url(");
        S.translate(-0.5, -0.5);
        if (E || L || this.type == "image") {
            U.matrix = "1 0 0 1";
            U.offset = "0 0";
            Q = S.split();
            if ((L && Q.noRotation) || !Q.isSimple) {
                K.style.filter = S.toFilter();
                var P = this.getBBox(),
                    J = this.getBBox(1),
                    V = P.x - J.x,
                    T = P.y - J.y;
                K.coordorigin = (V * -b) + k + (T * -b);
                p(this, 1, 1, V, T, 0)
            } else {
                K.style.filter = q;
                p(this, Q.scalex, Q.scaley, Q.dx, Q.dy, Q.rotate)
            }
        } else {
            K.style.filter = q;
            U.matrix = F(S);
            U.offset = S.offset()
        }
        R && (this._.transform = R);
        return this
    };
    m.rotate = function (J, E, L) {
        if (this.removed) {
            return this
        }
        if (J == null) {
            return
        }
        J = F(J).split(i);
        if (J.length - 1) {
            E = n(J[1]);
            L = n(J[2])
        }
        J = n(J[0]);
        (L == null) && (E = L);
        if (E == null || L == null) {
            var K = this.getBBox(1);
            E = K.x + K.width / 2;
            L = K.y + K.height / 2
        }
        this._.dirtyT = 1;
        this.transform(this._.transform.concat([
            ["r", J, E, L]
        ]));
        return this
    };
    m.translate = function (J, E) {
        if (this.removed) {
            return this
        }
        J = F(J).split(i);
        if (J.length - 1) {
            E = n(J[1])
        }
        J = n(J[0]) || 0;
        E = +E || 0;
        if (this._.bbox) {
            this._.bbox.x += J;
            this._.bbox.y += E
        }
        this.transform(this._.transform.concat([
            ["t", J, E]
        ]));
        return this
    };
    m.scale = function (M, K, E, L) {
        if (this.removed) {
            return this
        }
        M = F(M).split(i);
        if (M.length - 1) {
            K = n(M[1]);
            E = n(M[2]);
            L = n(M[3]);
            isNaN(E) && (E = null);
            isNaN(L) && (L = null)
        }
        M = n(M[0]);
        (K == null) && (K = M);
        (L == null) && (E = L);
        if (E == null || L == null) {
            var J = this.getBBox(1)
        }
        E = E == null ? J.x + J.width / 2 : E;
        L = L == null ? J.y + J.height / 2 : L;
        this.transform(this._.transform.concat([
            ["s", M, K, E, L]
        ]));
        this._.dirtyT = 1;
        return this
    };
    m.hide = function () {
        !this.removed && (this.node.style.display = "none");
        return this
    };
    m.show = function () {
        !this.removed && (this.node.style.display = q);
        return this
    };
    m._getBBox = function () {
        if (this.removed) {
            return {}
        }
        return {
            x: this.X + (this.bbx || 0) - this.W / 2,
            y: this.Y - this.H,
            width: this.W,
            height: this.H
        }
    };
    m.remove = function () {
        if (this.removed || !this.node.parentNode) {
            return
        }
        this.paper.__set__ && this.paper.__set__.exclude(this);
        l.eve.unbind("raphael.*.*." + this.id);
        l._tear(this, this.paper);
        this.node.parentNode.removeChild(this.node);
        this.shape && this.shape.parentNode.removeChild(this.shape);
        for (var E in this) {
            this[E] = typeof this[E] == "function" ? l._removedFactory(E) : null
        }
        this.removed = true
    };
    m.attr = function (E, R) {
        if (this.removed) {
            return this
        }
        if (E == null) {
            var O = {};
            for (var Q in this.attrs) {
                if (this.attrs[e](Q)) {
                    O[Q] = this.attrs[Q]
                }
            }
            O.gradient && O.fill == "none" && (O.fill = O.gradient) && delete O.gradient;
            O.transform = this._.transform;
            return O
        }
        if (R == null && l.is(E, "string")) {
            if (E == v && this.attrs.fill == "none" && this.attrs.gradient) {
                return this.attrs.gradient
            }
            var P = E.split(i),
                L = {};
            for (var M = 0, T = P.length; M < T; M++) {
                E = P[M];
                if (E in this.attrs) {
                    L[E] = this.attrs[E]
                } else {
                    if (l.is(this.paper.customAttributes[E], "function")) {
                        L[E] = this.paper.customAttributes[E].def
                    } else {
                        L[E] = l._availableAttrs[E]
                    }
                }
            }
            return T - 1 ? L : L[P[0]]
        }
        if (this.attrs && R == null && l.is(E, "array")) {
            L = {};
            for (M = 0, T = E.length; M < T; M++) {
                L[E[M]] = this.attr(E[M])
            }
            return L
        }
        var J;
        if (R != null) {
            J = {};
            J[E] = R
        }
        R == null && l.is(E, "object") && (J = E);
        for (var S in J) {
            A("raphael.attr." + S + "." + this.id, this, J[S])
        }
        if (J) {
            for (S in this.paper.customAttributes) {
                if (this.paper.customAttributes[e](S) && J[e](S) && l.is(this.paper.customAttributes[S], "function")) {
                    var N = this.paper.customAttributes[S].apply(this, [].concat(J[S]));
                    this.attrs[S] = J[S];
                    for (var K in N) {
                        if (N[e](K)) {
                            J[K] = N[K]
                        }
                    }
                }
            }
            if (J.text && this.type == "text") {
                this.textpath.string = J.text
            }
            x(this, J)
        }
        return this
    };
    m.toFront = function () {
        !this.removed && this.node.parentNode.appendChild(this.node);
        this.paper && this.paper.top != this && l._tofront(this, this.paper);
        return this
    };
    m.toBack = function () {
        if (this.removed) {
            return this
        }
        if (this.node.parentNode.firstChild != this.node) {
            this.node.parentNode.insertBefore(this.node, this.node.parentNode.firstChild);
            l._toback(this, this.paper)
        }
        return this
    };
    m.insertAfter = function (E) {
        if (this.removed) {
            return this
        }
        if (E.constructor == l.st.constructor) {
            E = E[E.length - 1]
        }
        if (E.node.nextSibling) {
            E.node.parentNode.insertBefore(this.node, E.node.nextSibling)
        } else {
            E.node.parentNode.appendChild(this.node)
        }
        l._insertafter(this, E, this.paper);
        return this
    };
    m.insertBefore = function (E) {
        if (this.removed) {
            return this
        }
        if (E.constructor == l.st.constructor) {
            E = E[0]
        }
        E.node.parentNode.insertBefore(this.node, E.node);
        l._insertbefore(this, E, this.paper);
        return this
    };
    m.blur = function (E) {
        var J = this.node.runtimeStyle,
            K = J.filter;
        K = K.replace(t, q);
        if (+E !== 0) {
            this.attrs.blur = E;
            J.filter = K + k + w + ".Blur(pixelradius=" + (+E || 1.5) + ")";
            J.margin = l.format("-{0}px 0 0 -{0}px", B(+E || 1.5))
        } else {
            J.filter = K;
            J.margin = 0;
            delete this.attrs.blur
        }
    };
    l._engine.path = function (L, J) {
        var M = G("shape");
        M.style.cssText = d;
        M.coordsize = b + k + b;
        M.coordorigin = J.coordorigin;
        var N = new u(M, J),
            E = {
                fill: "none",
                stroke: "#000"
            };
        L && (E.path = L);
        N.type = "path";
        N.path = [];
        N.Path = q;
        x(N, E);
        J.canvas.appendChild(M);
        var K = G("skew");
        K.on = true;
        M.appendChild(K);
        N.skew = K;
        N.transform(q);
        return N
    };
    l._engine.rect = function (J, O, M, P, K, E) {
        var Q = l._rectPath(O, M, P, K, E),
            L = J.path(Q),
            N = L.attrs;
        L.X = N.x = O;
        L.Y = N.y = M;
        L.W = N.width = P;
        L.H = N.height = K;
        N.r = E;
        N.path = Q;
        L.type = "rect";
        return L
    };
    l._engine.ellipse = function (J, E, O, N, M) {
        var L = J.path(),
            K = L.attrs;
        L.X = E - N;
        L.Y = O - M;
        L.W = N * 2;
        L.H = M * 2;
        L.type = "ellipse";
        x(L, {
            cx: E,
            cy: O,
            rx: N,
            ry: M
        });
        return L
    };
    l._engine.circle = function (J, E, N, M) {
        var L = J.path(),
            K = L.attrs;
        L.X = E - M;
        L.Y = N - M;
        L.W = L.H = M * 2;
        L.type = "circle";
        x(L, {
            cx: E,
            cy: N,
            r: M
        });
        return L
    };
    l._engine.image = function (J, E, P, N, Q, L) {
        var S = l._rectPath(P, N, Q, L),
            M = J.path(S).attr({
                stroke: "none"
            }),
            O = M.attrs,
            K = M.node,
            R = K.getElementsByTagName(v)[0];
        O.src = E;
        M.X = O.x = P;
        M.Y = O.y = N;
        M.W = O.width = Q;
        M.H = O.height = L;
        O.path = S;
        M.type = "image";
        R.parentNode == K && K.removeChild(R);
        R.rotate = true;
        R.src = E;
        R.type = "tile";
        M._.fillpos = [P, N];
        M._.fillsize = [Q, L];
        K.appendChild(R);
        p(M, 1, 1, 0, 0, 0);
        return M
    };
    l._engine.text = function (E, O, N, P) {
        var L = G("shape"),
            R = G("path"),
            K = G("textpath");
        O = O || 0;
        N = N || 0;
        P = P || "";
        R.v = l.format("m{0},{1}l{2},{1}", B(O * b), B(N * b), B(O * b) + 1);
        R.textpathok = true;
        K.string = F(P);
        K.on = true;
        L.style.cssText = d;
        L.coordsize = b + k + b;
        L.coordorigin = "0 0";
        var J = new u(L, E),
            M = {
                fill: "#000",
                stroke: "none",
                font: l._availableAttrs.font,
                text: P
            };
        J.shape = L;
        J.path = R;
        J.textpath = K;
        J.type = "text";
        J.attrs.text = F(P);
        J.attrs.x = O;
        J.attrs.y = N;
        J.attrs.w = 1;
        J.attrs.h = 1;
        x(J, M);
        L.appendChild(K);
        L.appendChild(R);
        E.canvas.appendChild(L);
        var Q = G("skew");
        Q.on = true;
        L.appendChild(Q);
        J.skew = Q;
        J.transform(q);
        return J
    };
    l._engine.setSize = function (K, E) {
        var J = this.canvas.style;
        this.width = K;
        this.height = E;
        K == +K && (K += "px");
        E == +E && (E += "px");
        J.width = K;
        J.height = E;
        J.clip = "rect(0 " + K + " " + E + " 0)";
        if (this._viewBox) {
            l._engine.setViewBox.apply(this, this._viewBox)
        }
        return this
    };
    l._engine.setViewBox = function (N, M, O, K, L) {
        l.eve("raphael.setViewBox", this, this._viewBox, [N, M, O, K, L]);
        var E = this.width,
            Q = this.height,
            R = 1 / I(O / E, K / Q),
            P, J;
        if (L) {
            P = Q / K;
            J = E / O;
            if (O * P < E) {
                N -= (E - O * P) / 2 / P
            }
            if (K * J < Q) {
                M -= (Q - K * J) / 2 / J
            }
        }
        this._viewBox = [N, M, O, K, !! L];
        this._viewBoxShift = {
            dx: -N,
            dy: -M,
            scale: R
        };
        this.forEach(function (S) {
            S.transform("...")
        });
        return this
    };
    var G;
    l._engine.initWin = function (K) {
        var J = K.document;
        J.createStyleSheet().addRule(".rvml", "behavior:url(#default#VML)");
        try {
            !J.namespaces.rvml && J.namespaces.add("rvml", "urn:schemas-microsoft-com:vml");
            G = function (L) {
                return J.createElement("<rvml:" + L + ' class="rvml">')
            }
        } catch (E) {
            G = function (L) {
                return J.createElement("<" + L + ' xmlns="urn:schemas-microsoft.com:vml" class="rvml">')
            }
        }
    };
    l._engine.initWin(l._g.win);
    l._engine.create = function () {
        var K = l._getContainer.apply(0, arguments),
            E = K.container,
            Q = K.height,
            R, J = K.width,
            P = K.x,
            O = K.y;
        if (!E) {
            throw new Error("VML container not found.")
        }
        var M = new l._Paper,
            N = M.canvas = l._g.doc.createElement("div"),
            L = N.style;
        P = P || 0;
        O = O || 0;
        J = J || 512;
        Q = Q || 342;
        M.width = J;
        M.height = Q;
        J == +J && (J += "px");
        Q == +Q && (Q += "px");
        M.coordsize = b * 1000 + k + b * 1000;
        M.coordorigin = "0 0";
        M.span = l._g.doc.createElement("span");
        M.span.style.cssText = "position:absolute;left:-9999em;top:-9999em;padding:0;margin:0;line-height:1;";
        N.appendChild(M.span);
        L.cssText = l.format("top:0;left:0;width:{0};height:{1};display:inline-block;position:relative;clip:rect(0 {0} {1} 0);overflow:hidden", J, Q);
        if (E == 1) {
            l._g.doc.body.appendChild(N);
            L.left = P + "px";
            L.top = O + "px";
            L.position = "absolute"
        } else {
            if (E.firstChild) {
                E.insertBefore(N, E.firstChild)
            } else {
                E.appendChild(N)
            }
        }
        M.renderfix = function () {};
        return M
    };
    l.prototype.clear = function () {
        l.eve("raphael.clear", this);
        this.canvas.innerHTML = q;
        this.span = l._g.doc.createElement("span");
        this.span.style.cssText = "position:absolute;left:-9999em;top:-9999em;padding:0;margin:0;line-height:1;display:inline;";
        this.canvas.appendChild(this.span);
        this.bottom = this.top = null
    };
    l.prototype.remove = function () {
        l.eve("raphael.remove", this);
        this.canvas.parentNode.removeChild(this.canvas);
        for (var E in this) {
            this[E] = typeof this[E] == "function" ? l._removedFactory(E) : null
        }
        return true
    };
    var y = l.st;
    for (var g in m) {
        if (m[e](g) && !y[e](g)) {
            y[g] = (function (E) {
                return function () {
                    var J = arguments;
                    return this.forEach(function (K) {
                        K[E].apply(K, J)
                    })
                }
            })(g)
        }
    }
}(window.Raphael);
/*
 * jQuery UI 1.8.24
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI
 */
(function (a, d) {
    a.ui = a.ui || {};
    if (a.ui.version) {
        return
    }
    a.extend(a.ui, {
        version: "1.8.24",
        keyCode: {
            ALT: 18,
            BACKSPACE: 8,
            CAPS_LOCK: 20,
            COMMA: 188,
            COMMAND: 91,
            COMMAND_LEFT: 91,
            COMMAND_RIGHT: 93,
            CONTROL: 17,
            DELETE: 46,
            DOWN: 40,
            END: 35,
            ENTER: 13,
            ESCAPE: 27,
            HOME: 36,
            INSERT: 45,
            LEFT: 37,
            MENU: 93,
            NUMPAD_ADD: 107,
            NUMPAD_DECIMAL: 110,
            NUMPAD_DIVIDE: 111,
            NUMPAD_ENTER: 108,
            NUMPAD_MULTIPLY: 106,
            NUMPAD_SUBTRACT: 109,
            PAGE_DOWN: 34,
            PAGE_UP: 33,
            PERIOD: 190,
            RIGHT: 39,
            SHIFT: 16,
            SPACE: 32,
            TAB: 9,
            UP: 38,
            WINDOWS: 91
        }
    });
    a.fn.extend({
        propAttr: a.fn.prop || a.fn.attr,
        _focus: a.fn.focus,
        focus: function (e, f) {
            return typeof e === "number" ? this.each(function () {
                var g = this;
                setTimeout(function () {
                    a(g).focus();
                    if (f) {
                        f.call(g)
                    }
                }, e)
            }) : this._focus.apply(this, arguments)
        },
        scrollParent: function () {
            var e;
            if ((a.browser.msie && (/(static|relative)/).test(this.css("position"))) || (/absolute/).test(this.css("position"))) {
                e = this.parents().filter(function () {
                    return (/(relative|absolute|fixed)/).test(a.curCSS(this, "position", 1)) && (/(auto|scroll)/).test(a.curCSS(this, "overflow", 1) + a.curCSS(this, "overflow-y", 1) + a.curCSS(this, "overflow-x", 1))
                }).eq(0)
            } else {
                e = this.parents().filter(function () {
                    return (/(auto|scroll)/).test(a.curCSS(this, "overflow", 1) + a.curCSS(this, "overflow-y", 1) + a.curCSS(this, "overflow-x", 1))
                }).eq(0)
            }
            return (/fixed/).test(this.css("position")) || !e.length ? a(document) : e
        },
        zIndex: function (h) {
            if (h !== d) {
                return this.css("zIndex", h)
            }
            if (this.length) {
                var f = a(this[0]),
                    e, g;
                while (f.length && f[0] !== document) {
                    e = f.css("position");
                    if (e === "absolute" || e === "relative" || e === "fixed") {
                        g = parseInt(f.css("zIndex"), 10);
                        if (!isNaN(g) && g !== 0) {
                            return g
                        }
                    }
                    f = f.parent()
                }
            }
            return 0
        },
        disableSelection: function () {
            return this.bind((a.support.selectstart ? "selectstart" : "mousedown") + ".ui-disableSelection", function (e) {
                e.preventDefault()
            })
        },
        enableSelection: function () {
            return this.unbind(".ui-disableSelection")
        }
    });
    if (!a("<a>").outerWidth(1).jquery) {
        a.each(["Width", "Height"], function (g, e) {
            var f = e === "Width" ? ["Left", "Right"] : ["Top", "Bottom"],
                h = e.toLowerCase(),
                k = {
                    innerWidth: a.fn.innerWidth,
                    innerHeight: a.fn.innerHeight,
                    outerWidth: a.fn.outerWidth,
                    outerHeight: a.fn.outerHeight
                };

            function j(m, l, i, n) {
                a.each(f, function () {
                    l -= parseFloat(a.curCSS(m, "padding" + this, true)) || 0;
                    if (i) {
                        l -= parseFloat(a.curCSS(m, "border" + this + "Width", true)) || 0
                    }
                    if (n) {
                        l -= parseFloat(a.curCSS(m, "margin" + this, true)) || 0
                    }
                });
                return l
            }
            a.fn["inner" + e] = function (i) {
                if (i === d) {
                    return k["inner" + e].call(this)
                }
                return this.each(function () {
                    a(this).css(h, j(this, i) + "px")
                })
            };
            a.fn["outer" + e] = function (i, l) {
                if (typeof i !== "number") {
                    return k["outer" + e].call(this, i)
                }
                return this.each(function () {
                    a(this).css(h, j(this, i, true, l) + "px")
                })
            }
        })
    }

    function c(g, e) {
        var j = g.nodeName.toLowerCase();
        if ("area" === j) {
            var i = g.parentNode,
                h = i.name,
                f;
            if (!g.href || !h || i.nodeName.toLowerCase() !== "map") {
                return false
            }
            f = a("img[usemap=#" + h + "]")[0];
            return !!f && b(f)
        }
        return (/input|select|textarea|button|object/.test(j) ? !g.disabled : "a" == j ? g.href || e : e) && b(g)
    }

    function b(e) {
        return !a(e).parents().andSelf().filter(function () {
            return a.curCSS(this, "visibility") === "hidden" || a.expr.filters.hidden(this)
        }).length
    }
    a.extend(a.expr[":"], {
        data: a.expr.createPseudo ? a.expr.createPseudo(function (e) {
            return function (f) {
                return !!a.data(f, e)
            }
        }) : function (g, f, e) {
            return !!a.data(g, e[3])
        },
        focusable: function (e) {
            return c(e, !isNaN(a.attr(e, "tabindex")))
        },
        tabbable: function (g) {
            var e = a.attr(g, "tabindex"),
                f = isNaN(e);
            return (f || e >= 0) && c(g, !f)
        }
    });
    a(function () {
        var e = document.body,
            f = e.appendChild(f = document.createElement("div"));
        f.offsetHeight;
        a.extend(f.style, {
            minHeight: "100px",
            height: "auto",
            padding: 0,
            borderWidth: 0
        });
        a.support.minHeight = f.offsetHeight === 100;
        a.support.selectstart = "onselectstart" in f;
        e.removeChild(f).style.display = "none"
    });
    if (!a.curCSS) {
        a.curCSS = a.css
    }
    a.extend(a.ui, {
        plugin: {
            add: function (f, g, j) {
                var h = a.ui[f].prototype;
                for (var e in j) {
                    h.plugins[e] = h.plugins[e] || [];
                    h.plugins[e].push([g, j[e]])
                }
            },
            call: function (e, g, f) {
                var j = e.plugins[g];
                if (!j || !e.element[0].parentNode) {
                    return
                }
                for (var h = 0; h < j.length; h++) {
                    if (e.options[j[h][0]]) {
                        j[h][1].apply(e.element, f)
                    }
                }
            }
        },
        contains: function (f, e) {
            return document.compareDocumentPosition ? f.compareDocumentPosition(e) & 16 : f !== e && f.contains(e)
        },
        hasScroll: function (h, f) {
            if (a(h).css("overflow") === "hidden") {
                return false
            }
            var e = (f && f === "left") ? "scrollLeft" : "scrollTop",
                g = false;
            if (h[e] > 0) {
                return true
            }
            h[e] = 1;
            g = (h[e] > 0);
            h[e] = 0;
            return g
        },
        isOverAxis: function (f, e, g) {
            return (f > e) && (f < (e + g))
        },
        isOver: function (j, f, i, h, e, g) {
            return a.ui.isOverAxis(j, i, e) && a.ui.isOverAxis(f, h, g)
        }
    })
})(jQuery);
/*
 * jQuery UI Widget 1.8.24
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Widget
 */
(function (b, d) {
    if (b.cleanData) {
        var c = b.cleanData;
        b.cleanData = function (f) {
            for (var g = 0, h;
                (h = f[g]) != null; g++) {
                try {
                    b(h).triggerHandler("remove")
                } catch (j) {}
            }
            c(f)
        }
    } else {
        var a = b.fn.remove;
        b.fn.remove = function (e, f) {
            return this.each(function () {
                if (!f) {
                    if (!e || b.filter(e, [this]).length) {
                        b("*", this).add([this]).each(function () {
                            try {
                                b(this).triggerHandler("remove")
                            } catch (g) {}
                        })
                    }
                }
                return a.call(b(this), e, f)
            })
        }
    }
    b.widget = function (f, h, e) {
        var g = f.split(".")[0],
            j;
        f = f.split(".")[1];
        j = g + "-" + f;
        if (!e) {
            e = h;
            h = b.Widget
        }
        b.expr[":"][j] = function (k) {
            return !!b.data(k, f)
        };
        b[g] = b[g] || {};
        b[g][f] = function (k, l) {
            if (arguments.length) {
                this._createWidget(k, l)
            }
        };
        var i = new h();
        i.options = b.extend(true, {}, i.options);
        b[g][f].prototype = b.extend(true, i, {
            namespace: g,
            widgetName: f,
            widgetEventPrefix: b[g][f].prototype.widgetEventPrefix || f,
            widgetBaseClass: j
        }, e);
        b.widget.bridge(f, b[g][f])
    };
    b.widget.bridge = function (f, e) {
        b.fn[f] = function (i) {
            var g = typeof i === "string",
                h = Array.prototype.slice.call(arguments, 1),
                j = this;
            i = !g && h.length ? b.extend.apply(null, [true, i].concat(h)) : i;
            if (g && i.charAt(0) === "_") {
                return j
            }
            if (g) {
                this.each(function () {
                    var k = b.data(this, f),
                        l = k && b.isFunction(k[i]) ? k[i].apply(k, h) : k;
                    if (l !== k && l !== d) {
                        j = l;
                        return false
                    }
                })
            } else {
                this.each(function () {
                    var k = b.data(this, f);
                    if (k) {
                        k.option(i || {})._init()
                    } else {
                        b.data(this, f, new e(i, this))
                    }
                })
            }
            return j
        }
    };
    b.Widget = function (e, f) {
        if (arguments.length) {
            this._createWidget(e, f)
        }
    };
    b.Widget.prototype = {
        widgetName: "widget",
        widgetEventPrefix: "",
        options: {
            disabled: false
        },
        _createWidget: function (f, g) {
            b.data(g, this.widgetName, this);
            this.element = b(g);
            this.options = b.extend(true, {}, this.options, this._getCreateOptions(), f);
            var e = this;
            this.element.bind("remove." + this.widgetName, function () {
                e.destroy()
            });
            this._create();
            this._trigger("create");
            this._init()
        },
        _getCreateOptions: function () {
            return b.metadata && b.metadata.get(this.element[0])[this.widgetName]
        },
        _create: function () {},
        _init: function () {},
        destroy: function () {
            this.element.unbind("." + this.widgetName).removeData(this.widgetName);
            this.widget().unbind("." + this.widgetName).removeAttr("aria-disabled").removeClass(this.widgetBaseClass + "-disabled ui-state-disabled")
        },
        widget: function () {
            return this.element
        },
        option: function (f, g) {
            var e = f;
            if (arguments.length === 0) {
                return b.extend({}, this.options)
            }
            if (typeof f === "string") {
                if (g === d) {
                    return this.options[f]
                }
                e = {};
                e[f] = g
            }
            this._setOptions(e);
            return this
        },
        _setOptions: function (f) {
            var e = this;
            b.each(f, function (g, h) {
                e._setOption(g, h)
            });
            return this
        },
        _setOption: function (e, f) {
            this.options[e] = f;
            if (e === "disabled") {
                this.widget()[f ? "addClass" : "removeClass"](this.widgetBaseClass + "-disabled ui-state-disabled").attr("aria-disabled", f)
            }
            return this
        },
        enable: function () {
            return this._setOption("disabled", false)
        },
        disable: function () {
            return this._setOption("disabled", true)
        },
        _trigger: function (e, f, g) {
            var j, i, h = this.options[e];
            g = g || {};
            f = b.Event(f);
            f.type = (e === this.widgetEventPrefix ? e : this.widgetEventPrefix + e).toLowerCase();
            f.target = this.element[0];
            i = f.originalEvent;
            if (i) {
                for (j in i) {
                    if (!(j in f)) {
                        f[j] = i[j]
                    }
                }
            }
            this.element.trigger(f, g);
            return !(b.isFunction(h) && h.call(this.element[0], f, g) === false || f.isDefaultPrevented())
        }
    }
})(jQuery);
/*
 * jQuery UI Mouse 1.8.24
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Mouse
 *
 * Depends:
 *	jquery.ui.widget.js
 */
(function (b, c) {
    var a = false;
    b(document).mouseup(function (d) {
        a = false
    });
    b.widget("ui.mouse", {
        options: {
            cancel: ":input,option",
            distance: 1,
            delay: 0
        },
        _mouseInit: function () {
            var d = this;
            this.element.bind("mousedown." + this.widgetName, function (e) {
                return d._mouseDown(e)
            }).bind("click." + this.widgetName, function (e) {
                if (true === b.data(e.target, d.widgetName + ".preventClickEvent")) {
                    b.removeData(e.target, d.widgetName + ".preventClickEvent");
                    e.stopImmediatePropagation();
                    return false
                }
            });
            this.started = false
        },
        _mouseDestroy: function () {
            this.element.unbind("." + this.widgetName);
            if (this._mouseMoveDelegate) {
                b(document).unbind("mousemove." + this.widgetName, this._mouseMoveDelegate).unbind("mouseup." + this.widgetName, this._mouseUpDelegate)
            }
        },
        _mouseDown: function (f) {
            if (a) {
                return
            }(this._mouseStarted && this._mouseUp(f));
            this._mouseDownEvent = f;
            var e = this,
                g = (f.which == 1),
                d = (typeof this.options.cancel == "string" && f.target.nodeName ? b(f.target).closest(this.options.cancel).length : false);
            if (!g || d || !this._mouseCapture(f)) {
                return true
            }
            this.mouseDelayMet = !this.options.delay;
            if (!this.mouseDelayMet) {
                this._mouseDelayTimer = setTimeout(function () {
                    e.mouseDelayMet = true
                }, this.options.delay)
            }
            if (this._mouseDistanceMet(f) && this._mouseDelayMet(f)) {
                this._mouseStarted = (this._mouseStart(f) !== false);
                if (!this._mouseStarted) {
                    f.preventDefault();
                    return true
                }
            }
            if (true === b.data(f.target, this.widgetName + ".preventClickEvent")) {
                b.removeData(f.target, this.widgetName + ".preventClickEvent")
            }
            this._mouseMoveDelegate = function (h) {
                return e._mouseMove(h)
            };
            this._mouseUpDelegate = function (h) {
                return e._mouseUp(h)
            };
            b(document).bind("mousemove." + this.widgetName, this._mouseMoveDelegate).bind("mouseup." + this.widgetName, this._mouseUpDelegate);
            f.preventDefault();
            a = true;
            return true
        },
        _mouseMove: function (d) {
            if (b.browser.msie && !(document.documentMode >= 9) && !d.button) {
                return this._mouseUp(d)
            }
            if (this._mouseStarted) {
                this._mouseDrag(d);
                return d.preventDefault()
            }
            if (this._mouseDistanceMet(d) && this._mouseDelayMet(d)) {
                this._mouseStarted = (this._mouseStart(this._mouseDownEvent, d) !== false);
                (this._mouseStarted ? this._mouseDrag(d) : this._mouseUp(d))
            }
            return !this._mouseStarted
        },
        _mouseUp: function (d) {
            b(document).unbind("mousemove." + this.widgetName, this._mouseMoveDelegate).unbind("mouseup." + this.widgetName, this._mouseUpDelegate);
            if (this._mouseStarted) {
                this._mouseStarted = false;
                if (d.target == this._mouseDownEvent.target) {
                    b.data(d.target, this.widgetName + ".preventClickEvent", true)
                }
                this._mouseStop(d)
            }
            return false
        },
        _mouseDistanceMet: function (d) {
            return (Math.max(Math.abs(this._mouseDownEvent.pageX - d.pageX), Math.abs(this._mouseDownEvent.pageY - d.pageY)) >= this.options.distance)
        },
        _mouseDelayMet: function (d) {
            return this.mouseDelayMet
        },
        _mouseStart: function (d) {},
        _mouseDrag: function (d) {},
        _mouseStop: function (d) {},
        _mouseCapture: function (d) {
            return true
        }
    })
})(jQuery);
/*
 * jQuery UI Draggable 1.8.24
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Draggables
 *
 * Depends:
 *	jquery.ui.core.js
 *	jquery.ui.mouse.js
 *	jquery.ui.widget.js
 */
(function (a, b) {
    a.widget("ui.draggable", a.ui.mouse, {
        widgetEventPrefix: "drag",
        options: {
            addClasses: true,
            appendTo: "parent",
            axis: false,
            connectToSortable: false,
            containment: false,
            cursor: "auto",
            cursorAt: false,
            grid: false,
            handle: false,
            helper: "original",
            iframeFix: false,
            opacity: false,
            refreshPositions: false,
            revert: false,
            revertDuration: 500,
            scope: "default",
            scroll: true,
            scrollSensitivity: 20,
            scrollSpeed: 20,
            snap: false,
            snapMode: "both",
            snapTolerance: 20,
            stack: false,
            zIndex: false
        },
        _create: function () {
            if (this.options.helper == "original" && !(/^(?:r|a|f)/).test(this.element.css("position"))) {
                this.element[0].style.position = "relative"
            }(this.options.addClasses && this.element.addClass("ui-draggable"));
            (this.options.disabled && this.element.addClass("ui-draggable-disabled"));
            this._mouseInit()
        },
        destroy: function () {
            if (!this.element.data("draggable")) {
                return
            }
            this.element.removeData("draggable").unbind(".draggable").removeClass("ui-draggable ui-draggable-dragging ui-draggable-disabled");
            this._mouseDestroy();
            return this
        },
        _mouseCapture: function (c) {
            var d = this.options;
            if (this.helper || d.disabled || a(c.target).is(".ui-resizable-handle")) {
                return false
            }
            this.handle = this._getHandle(c);
            if (!this.handle) {
                return false
            }
            if (d.iframeFix) {
                a(d.iframeFix === true ? "iframe" : d.iframeFix).each(function () {
                    a('<div class="ui-draggable-iframeFix" style="background: #fff;"></div>').css({
                        width: this.offsetWidth + "px",
                        height: this.offsetHeight + "px",
                        position: "absolute",
                        opacity: "0.001",
                        zIndex: 1000
                    }).css(a(this).offset()).appendTo("body")
                })
            }
            return true
        },
        _mouseStart: function (c) {
            var d = this.options;
            this.helper = this._createHelper(c);
            this.helper.addClass("ui-draggable-dragging");
            this._cacheHelperProportions();
            if (a.ui.ddmanager) {
                a.ui.ddmanager.current = this
            }
            this._cacheMargins();
            this.cssPosition = this.helper.css("position");
            this.scrollParent = this.helper.scrollParent();
            this.offset = this.positionAbs = this.element.offset();
            this.offset = {
                top: this.offset.top - this.margins.top,
                left: this.offset.left - this.margins.left
            };
            a.extend(this.offset, {
                click: {
                    left: c.pageX - this.offset.left,
                    top: c.pageY - this.offset.top
                },
                parent: this._getParentOffset(),
                relative: this._getRelativeOffset()
            });
            this.originalPosition = this.position = this._generatePosition(c);
            this.originalPageX = c.pageX;
            this.originalPageY = c.pageY;
            (d.cursorAt && this._adjustOffsetFromHelper(d.cursorAt));
            if (d.containment) {
                this._setContainment()
            }
            if (this._trigger("start", c) === false) {
                this._clear();
                return false
            }
            this._cacheHelperProportions();
            if (a.ui.ddmanager && !d.dropBehaviour) {
                a.ui.ddmanager.prepareOffsets(this, c)
            }
            this._mouseDrag(c, true);
            if (a.ui.ddmanager) {
                a.ui.ddmanager.dragStart(this, c)
            }
            return true
        },
        _mouseDrag: function (c, e) {
            this.position = this._generatePosition(c);
            this.positionAbs = this._convertPositionTo("absolute");
            if (!e) {
                var d = this._uiHash();
                if (this._trigger("drag", c, d) === false) {
                    this._mouseUp({});
                    return false
                }
                this.position = d.position
            }
            if (!this.options.axis || this.options.axis != "y") {
                this.helper[0].style.left = this.position.left + "px"
            }
            if (!this.options.axis || this.options.axis != "x") {
                this.helper[0].style.top = this.position.top + "px"
            }
            if (a.ui.ddmanager) {
                a.ui.ddmanager.drag(this, c)
            }
            return false
        },
        _mouseStop: function (e) {
            var g = false;
            if (a.ui.ddmanager && !this.options.dropBehaviour) {
                g = a.ui.ddmanager.drop(this, e)
            }
            if (this.dropped) {
                g = this.dropped;
                this.dropped = false
            }
            var d = this.element[0],
                f = false;
            while (d && (d = d.parentNode)) {
                if (d == document) {
                    f = true
                }
            }
            if (!f && this.options.helper === "original") {
                return false
            }
            if ((this.options.revert == "invalid" && !g) || (this.options.revert == "valid" && g) || this.options.revert === true || (a.isFunction(this.options.revert) && this.options.revert.call(this.element, g))) {
                var c = this;
                a(this.helper).animate(this.originalPosition, parseInt(this.options.revertDuration, 10), function () {
                    if (c._trigger("stop", e) !== false) {
                        c._clear()
                    }
                })
            } else {
                if (this._trigger("stop", e) !== false) {
                    this._clear()
                }
            }
            return false
        },
        _mouseUp: function (c) {
            a("div.ui-draggable-iframeFix").each(function () {
                this.parentNode.removeChild(this)
            });
            if (a.ui.ddmanager) {
                a.ui.ddmanager.dragStop(this, c)
            }
            return a.ui.mouse.prototype._mouseUp.call(this, c)
        },
        cancel: function () {
            if (this.helper.is(".ui-draggable-dragging")) {
                this._mouseUp({})
            } else {
                this._clear()
            }
            return this
        },
        _getHandle: function (c) {
            var d = !this.options.handle || !a(this.options.handle, this.element).length ? true : false;
            a(this.options.handle, this.element).find("*").andSelf().each(function () {
                if (this == c.target) {
                    d = true
                }
            });
            return d
        },
        _createHelper: function (d) {
            var e = this.options;
            var c = a.isFunction(e.helper) ? a(e.helper.apply(this.element[0], [d])) : (e.helper == "clone" ? this.element.clone().removeAttr("id") : this.element);
            if (!c.parents("body").length) {
                c.appendTo((e.appendTo == "parent" ? this.element[0].parentNode : e.appendTo))
            }
            if (c[0] != this.element[0] && !(/(fixed|absolute)/).test(c.css("position"))) {
                c.css("position", "absolute")
            }
            return c
        },
        _adjustOffsetFromHelper: function (c) {
            if (typeof c == "string") {
                c = c.split(" ")
            }
            if (a.isArray(c)) {
                c = {
                    left: +c[0],
                    top: +c[1] || 0
                }
            }
            if ("left" in c) {
                this.offset.click.left = c.left + this.margins.left
            }
            if ("right" in c) {
                this.offset.click.left = this.helperProportions.width - c.right + this.margins.left
            }
            if ("top" in c) {
                this.offset.click.top = c.top + this.margins.top
            }
            if ("bottom" in c) {
                this.offset.click.top = this.helperProportions.height - c.bottom + this.margins.top
            }
        },
        _getParentOffset: function () {
            this.offsetParent = this.helper.offsetParent();
            var c = this.offsetParent.offset();
            if (this.cssPosition == "absolute" && this.scrollParent[0] != document && a.ui.contains(this.scrollParent[0], this.offsetParent[0])) {
                c.left += this.scrollParent.scrollLeft();
                c.top += this.scrollParent.scrollTop()
            }
            if ((this.offsetParent[0] == document.body) || (this.offsetParent[0].tagName && this.offsetParent[0].tagName.toLowerCase() == "html" && a.browser.msie)) {
                c = {
                    top: 0,
                    left: 0
                }
            }
            return {
                top: c.top + (parseInt(this.offsetParent.css("borderTopWidth"), 10) || 0),
                left: c.left + (parseInt(this.offsetParent.css("borderLeftWidth"), 10) || 0)
            }
        },
        _getRelativeOffset: function () {
            if (this.cssPosition == "relative") {
                var c = this.element.position();
                return {
                    top: c.top - (parseInt(this.helper.css("top"), 10) || 0) + this.scrollParent.scrollTop(),
                    left: c.left - (parseInt(this.helper.css("left"), 10) || 0) + this.scrollParent.scrollLeft()
                }
            } else {
                return {
                    top: 0,
                    left: 0
                }
            }
        },
        _cacheMargins: function () {
            this.margins = {
                left: (parseInt(this.element.css("marginLeft"), 10) || 0),
                top: (parseInt(this.element.css("marginTop"), 10) || 0),
                right: (parseInt(this.element.css("marginRight"), 10) || 0),
                bottom: (parseInt(this.element.css("marginBottom"), 10) || 0)
            }
        },
        _cacheHelperProportions: function () {
            this.helperProportions = {
                width: this.helper.outerWidth(),
                height: this.helper.outerHeight()
            }
        },
        _setContainment: function () {
            var g = this.options;
            if (g.containment == "parent") {
                g.containment = this.helper[0].parentNode
            }
            if (g.containment == "document" || g.containment == "window") {
                this.containment = [g.containment == "document" ? 0 : a(window).scrollLeft() - this.offset.relative.left - this.offset.parent.left, g.containment == "document" ? 0 : a(window).scrollTop() - this.offset.relative.top - this.offset.parent.top, (g.containment == "document" ? 0 : a(window).scrollLeft()) + a(g.containment == "document" ? document : window).width() - this.helperProportions.width - this.margins.left, (g.containment == "document" ? 0 : a(window).scrollTop()) + (a(g.containment == "document" ? document : window).height() || document.body.parentNode.scrollHeight) - this.helperProportions.height - this.margins.top]
            }
            if (!(/^(document|window|parent)$/).test(g.containment) && g.containment.constructor != Array) {
                var h = a(g.containment);
                var e = h[0];
                if (!e) {
                    return
                }
                var f = h.offset();
                var d = (a(e).css("overflow") != "hidden");
                this.containment = [(parseInt(a(e).css("borderLeftWidth"), 10) || 0) + (parseInt(a(e).css("paddingLeft"), 10) || 0), (parseInt(a(e).css("borderTopWidth"), 10) || 0) + (parseInt(a(e).css("paddingTop"), 10) || 0), (d ? Math.max(e.scrollWidth, e.offsetWidth) : e.offsetWidth) - (parseInt(a(e).css("borderLeftWidth"), 10) || 0) - (parseInt(a(e).css("paddingRight"), 10) || 0) - this.helperProportions.width - this.margins.left - this.margins.right, (d ? Math.max(e.scrollHeight, e.offsetHeight) : e.offsetHeight) - (parseInt(a(e).css("borderTopWidth"), 10) || 0) - (parseInt(a(e).css("paddingBottom"), 10) || 0) - this.helperProportions.height - this.margins.top - this.margins.bottom];
                this.relative_container = h
            } else {
                if (g.containment.constructor == Array) {
                    this.containment = g.containment
                }
            }
        },
        _convertPositionTo: function (g, i) {
            if (!i) {
                i = this.position
            }
            var e = g == "absolute" ? 1 : -1;
            var f = this.options,
                c = this.cssPosition == "absolute" && !(this.scrollParent[0] != document && a.ui.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent,
                h = (/(html|body)/i).test(c[0].tagName);
            return {
                top: (i.top + this.offset.relative.top * e + this.offset.parent.top * e - (a.browser.safari && a.browser.version < 526 && this.cssPosition == "fixed" ? 0 : (this.cssPosition == "fixed" ? -this.scrollParent.scrollTop() : (h ? 0 : c.scrollTop())) * e)),
                left: (i.left + this.offset.relative.left * e + this.offset.parent.left * e - (a.browser.safari && a.browser.version < 526 && this.cssPosition == "fixed" ? 0 : (this.cssPosition == "fixed" ? -this.scrollParent.scrollLeft() : h ? 0 : c.scrollLeft()) * e))
            }
        },
        _generatePosition: function (d) {
            var e = this.options,
                l = this.cssPosition == "absolute" && !(this.scrollParent[0] != document && a.ui.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent,
                i = (/(html|body)/i).test(l[0].tagName);
            var h = d.pageX;
            var g = d.pageY;
            if (this.originalPosition) {
                var c;
                if (this.containment) {
                    if (this.relative_container) {
                        var k = this.relative_container.offset();
                        c = [this.containment[0] + k.left, this.containment[1] + k.top, this.containment[2] + k.left, this.containment[3] + k.top]
                    } else {
                        c = this.containment
                    } if (d.pageX - this.offset.click.left < c[0]) {
                        h = c[0] + this.offset.click.left
                    }
                    if (d.pageY - this.offset.click.top < c[1]) {
                        g = c[1] + this.offset.click.top
                    }
                    if (d.pageX - this.offset.click.left > c[2]) {
                        h = c[2] + this.offset.click.left
                    }
                    if (d.pageY - this.offset.click.top > c[3]) {
                        g = c[3] + this.offset.click.top
                    }
                }
                if (e.grid) {
                    var j = e.grid[1] ? this.originalPageY + Math.round((g - this.originalPageY) / e.grid[1]) * e.grid[1] : this.originalPageY;
                    g = c ? (!(j - this.offset.click.top < c[1] || j - this.offset.click.top > c[3]) ? j : (!(j - this.offset.click.top < c[1]) ? j - e.grid[1] : j + e.grid[1])) : j;
                    var f = e.grid[0] ? this.originalPageX + Math.round((h - this.originalPageX) / e.grid[0]) * e.grid[0] : this.originalPageX;
                    h = c ? (!(f - this.offset.click.left < c[0] || f - this.offset.click.left > c[2]) ? f : (!(f - this.offset.click.left < c[0]) ? f - e.grid[0] : f + e.grid[0])) : f
                }
            }
            return {
                top: (g - this.offset.click.top - this.offset.relative.top - this.offset.parent.top + (a.browser.safari && a.browser.version < 526 && this.cssPosition == "fixed" ? 0 : (this.cssPosition == "fixed" ? -this.scrollParent.scrollTop() : (i ? 0 : l.scrollTop())))),
                left: (h - this.offset.click.left - this.offset.relative.left - this.offset.parent.left + (a.browser.safari && a.browser.version < 526 && this.cssPosition == "fixed" ? 0 : (this.cssPosition == "fixed" ? -this.scrollParent.scrollLeft() : i ? 0 : l.scrollLeft())))
            }
        },
        _clear: function () {
            this.helper.removeClass("ui-draggable-dragging");
            if (this.helper[0] != this.element[0] && !this.cancelHelperRemoval) {
                this.helper.remove()
            }
            this.helper = null;
            this.cancelHelperRemoval = false
        },
        _trigger: function (c, d, e) {
            e = e || this._uiHash();
            a.ui.plugin.call(this, c, [d, e]);
            if (c == "drag") {
                this.positionAbs = this._convertPositionTo("absolute")
            }
            return a.Widget.prototype._trigger.call(this, c, d, e)
        },
        plugins: {},
        _uiHash: function (c) {
            return {
                helper: this.helper,
                position: this.position,
                originalPosition: this.originalPosition,
                offset: this.positionAbs
            }
        }
    });
    a.extend(a.ui.draggable, {
        version: "1.8.24"
    });
    a.ui.plugin.add("draggable", "connectToSortable", {
        start: function (d, f) {
            var e = a(this).data("draggable"),
                g = e.options,
                c = a.extend({}, f, {
                    item: e.element
                });
            e.sortables = [];
            a(g.connectToSortable).each(function () {
                var h = a.data(this, "sortable");
                if (h && !h.options.disabled) {
                    e.sortables.push({
                        instance: h,
                        shouldRevert: h.options.revert
                    });
                    h.refreshPositions();
                    h._trigger("activate", d, c)
                }
            })
        },
        stop: function (d, f) {
            var e = a(this).data("draggable"),
                c = a.extend({}, f, {
                    item: e.element
                });
            a.each(e.sortables, function () {
                if (this.instance.isOver) {
                    this.instance.isOver = 0;
                    e.cancelHelperRemoval = true;
                    this.instance.cancelHelperRemoval = false;
                    if (this.shouldRevert) {
                        this.instance.options.revert = true
                    }
                    this.instance._mouseStop(d);
                    this.instance.options.helper = this.instance.options._helper;
                    if (e.options.helper == "original") {
                        this.instance.currentItem.css({
                            top: "auto",
                            left: "auto"
                        })
                    }
                } else {
                    this.instance.cancelHelperRemoval = false;
                    this.instance._trigger("deactivate", d, c)
                }
            })
        },
        drag: function (d, g) {
            var f = a(this).data("draggable"),
                c = this;
            var e = function (j) {
                var p = this.offset.click.top,
                    n = this.offset.click.left;
                var h = this.positionAbs.top,
                    l = this.positionAbs.left;
                var k = j.height,
                    m = j.width;
                var q = j.top,
                    i = j.left;
                return a.ui.isOver(h + p, l + n, q, i, k, m)
            };
            a.each(f.sortables, function (h) {
                this.instance.positionAbs = f.positionAbs;
                this.instance.helperProportions = f.helperProportions;
                this.instance.offset.click = f.offset.click;
                if (this.instance._intersectsWith(this.instance.containerCache)) {
                    if (!this.instance.isOver) {
                        this.instance.isOver = 1;
                        this.instance.currentItem = a(c).clone().removeAttr("id").appendTo(this.instance.element).data("sortable-item", true);
                        this.instance.options._helper = this.instance.options.helper;
                        this.instance.options.helper = function () {
                            return g.helper[0]
                        };
                        d.target = this.instance.currentItem[0];
                        this.instance._mouseCapture(d, true);
                        this.instance._mouseStart(d, true, true);
                        this.instance.offset.click.top = f.offset.click.top;
                        this.instance.offset.click.left = f.offset.click.left;
                        this.instance.offset.parent.left -= f.offset.parent.left - this.instance.offset.parent.left;
                        this.instance.offset.parent.top -= f.offset.parent.top - this.instance.offset.parent.top;
                        f._trigger("toSortable", d);
                        f.dropped = this.instance.element;
                        f.currentItem = f.element;
                        this.instance.fromOutside = f
                    }
                    if (this.instance.currentItem) {
                        this.instance._mouseDrag(d)
                    }
                } else {
                    if (this.instance.isOver) {
                        this.instance.isOver = 0;
                        this.instance.cancelHelperRemoval = true;
                        this.instance.options.revert = false;
                        this.instance._trigger("out", d, this.instance._uiHash(this.instance));
                        this.instance._mouseStop(d, true);
                        this.instance.options.helper = this.instance.options._helper;
                        this.instance.currentItem.remove();
                        if (this.instance.placeholder) {
                            this.instance.placeholder.remove()
                        }
                        f._trigger("fromSortable", d);
                        f.dropped = false
                    }
                }
            })
        }
    });
    a.ui.plugin.add("draggable", "cursor", {
        start: function (d, e) {
            var c = a("body"),
                f = a(this).data("draggable").options;
            if (c.css("cursor")) {
                f._cursor = c.css("cursor")
            }
            c.css("cursor", f.cursor)
        },
        stop: function (c, d) {
            var e = a(this).data("draggable").options;
            if (e._cursor) {
                a("body").css("cursor", e._cursor)
            }
        }
    });
    a.ui.plugin.add("draggable", "opacity", {
        start: function (d, e) {
            var c = a(e.helper),
                f = a(this).data("draggable").options;
            if (c.css("opacity")) {
                f._opacity = c.css("opacity")
            }
            c.css("opacity", f.opacity)
        },
        stop: function (c, d) {
            var e = a(this).data("draggable").options;
            if (e._opacity) {
                a(d.helper).css("opacity", e._opacity)
            }
        }
    });
    a.ui.plugin.add("draggable", "scroll", {
        start: function (d, e) {
            var c = a(this).data("draggable");
            if (c.scrollParent[0] != document && c.scrollParent[0].tagName != "HTML") {
                c.overflowOffset = c.scrollParent.offset()
            }
        },
        drag: function (e, f) {
            var d = a(this).data("draggable"),
                g = d.options,
                c = false;
            if (d.scrollParent[0] != document && d.scrollParent[0].tagName != "HTML") {
                if (!g.axis || g.axis != "x") {
                    if ((d.overflowOffset.top + d.scrollParent[0].offsetHeight) - e.pageY < g.scrollSensitivity) {
                        d.scrollParent[0].scrollTop = c = d.scrollParent[0].scrollTop + g.scrollSpeed
                    } else {
                        if (e.pageY - d.overflowOffset.top < g.scrollSensitivity) {
                            d.scrollParent[0].scrollTop = c = d.scrollParent[0].scrollTop - g.scrollSpeed
                        }
                    }
                }
                if (!g.axis || g.axis != "y") {
                    if ((d.overflowOffset.left + d.scrollParent[0].offsetWidth) - e.pageX < g.scrollSensitivity) {
                        d.scrollParent[0].scrollLeft = c = d.scrollParent[0].scrollLeft + g.scrollSpeed
                    } else {
                        if (e.pageX - d.overflowOffset.left < g.scrollSensitivity) {
                            d.scrollParent[0].scrollLeft = c = d.scrollParent[0].scrollLeft - g.scrollSpeed
                        }
                    }
                }
            } else {
                if (!g.axis || g.axis != "x") {
                    if (e.pageY - a(document).scrollTop() < g.scrollSensitivity) {
                        c = a(document).scrollTop(a(document).scrollTop() - g.scrollSpeed)
                    } else {
                        if (a(window).height() - (e.pageY - a(document).scrollTop()) < g.scrollSensitivity) {
                            c = a(document).scrollTop(a(document).scrollTop() + g.scrollSpeed)
                        }
                    }
                }
                if (!g.axis || g.axis != "y") {
                    if (e.pageX - a(document).scrollLeft() < g.scrollSensitivity) {
                        c = a(document).scrollLeft(a(document).scrollLeft() - g.scrollSpeed)
                    } else {
                        if (a(window).width() - (e.pageX - a(document).scrollLeft()) < g.scrollSensitivity) {
                            c = a(document).scrollLeft(a(document).scrollLeft() + g.scrollSpeed)
                        }
                    }
                }
            } if (c !== false && a.ui.ddmanager && !g.dropBehaviour) {
                a.ui.ddmanager.prepareOffsets(d, e)
            }
        }
    });
    a.ui.plugin.add("draggable", "snap", {
        start: function (d, e) {
            var c = a(this).data("draggable"),
                f = c.options;
            c.snapElements = [];
            a(f.snap.constructor != String ? (f.snap.items || ":data(draggable)") : f.snap).each(function () {
                var h = a(this);
                var g = h.offset();
                if (this != c.element[0]) {
                    c.snapElements.push({
                        item: this,
                        width: h.outerWidth(),
                        height: h.outerHeight(),
                        top: g.top,
                        left: g.left
                    })
                }
            })
        },
        drag: function (u, p) {
            var g = a(this).data("draggable"),
                q = g.options;
            var y = q.snapTolerance;
            var x = p.offset.left,
                w = x + g.helperProportions.width,
                f = p.offset.top,
                e = f + g.helperProportions.height;
            for (var v = g.snapElements.length - 1; v >= 0; v--) {
                var s = g.snapElements[v].left,
                    n = s + g.snapElements[v].width,
                    m = g.snapElements[v].top,
                    A = m + g.snapElements[v].height;
                if (!((s - y < x && x < n + y && m - y < f && f < A + y) || (s - y < x && x < n + y && m - y < e && e < A + y) || (s - y < w && w < n + y && m - y < f && f < A + y) || (s - y < w && w < n + y && m - y < e && e < A + y))) {
                    if (g.snapElements[v].snapping) {
                        (g.options.snap.release && g.options.snap.release.call(g.element, u, a.extend(g._uiHash(), {
                            snapItem: g.snapElements[v].item
                        })))
                    }
                    g.snapElements[v].snapping = false;
                    continue
                }
                if (q.snapMode != "inner") {
                    var c = Math.abs(m - e) <= y;
                    var z = Math.abs(A - f) <= y;
                    var j = Math.abs(s - w) <= y;
                    var k = Math.abs(n - x) <= y;
                    if (c) {
                        p.position.top = g._convertPositionTo("relative", {
                            top: m - g.helperProportions.height,
                            left: 0
                        }).top - g.margins.top
                    }
                    if (z) {
                        p.position.top = g._convertPositionTo("relative", {
                            top: A,
                            left: 0
                        }).top - g.margins.top
                    }
                    if (j) {
                        p.position.left = g._convertPositionTo("relative", {
                            top: 0,
                            left: s - g.helperProportions.width
                        }).left - g.margins.left
                    }
                    if (k) {
                        p.position.left = g._convertPositionTo("relative", {
                            top: 0,
                            left: n
                        }).left - g.margins.left
                    }
                }
                var h = (c || z || j || k);
                if (q.snapMode != "outer") {
                    var c = Math.abs(m - f) <= y;
                    var z = Math.abs(A - e) <= y;
                    var j = Math.abs(s - x) <= y;
                    var k = Math.abs(n - w) <= y;
                    if (c) {
                        p.position.top = g._convertPositionTo("relative", {
                            top: m,
                            left: 0
                        }).top - g.margins.top
                    }
                    if (z) {
                        p.position.top = g._convertPositionTo("relative", {
                            top: A - g.helperProportions.height,
                            left: 0
                        }).top - g.margins.top
                    }
                    if (j) {
                        p.position.left = g._convertPositionTo("relative", {
                            top: 0,
                            left: s
                        }).left - g.margins.left
                    }
                    if (k) {
                        p.position.left = g._convertPositionTo("relative", {
                            top: 0,
                            left: n - g.helperProportions.width
                        }).left - g.margins.left
                    }
                }
                if (!g.snapElements[v].snapping && (c || z || j || k || h)) {
                    (g.options.snap.snap && g.options.snap.snap.call(g.element, u, a.extend(g._uiHash(), {
                        snapItem: g.snapElements[v].item
                    })))
                }
                g.snapElements[v].snapping = (c || z || j || k || h)
            }
        }
    });
    a.ui.plugin.add("draggable", "stack", {
        start: function (d, e) {
            var g = a(this).data("draggable").options;
            var f = a.makeArray(a(g.stack)).sort(function (i, h) {
                return (parseInt(a(i).css("zIndex"), 10) || 0) - (parseInt(a(h).css("zIndex"), 10) || 0)
            });
            if (!f.length) {
                return
            }
            var c = parseInt(f[0].style.zIndex) || 0;
            a(f).each(function (h) {
                this.style.zIndex = c + h
            });
            this[0].style.zIndex = c + f.length
        }
    });
    a.ui.plugin.add("draggable", "zIndex", {
        start: function (d, e) {
            var c = a(e.helper),
                f = a(this).data("draggable").options;
            if (c.css("zIndex")) {
                f._zIndex = c.css("zIndex")
            }
            c.css("zIndex", f.zIndex)
        },
        stop: function (c, d) {
            var e = a(this).data("draggable").options;
            if (e._zIndex) {
                a(d.helper).css("zIndex", e._zIndex)
            }
        }
    })
})(jQuery);
/*
 * jQuery UI Sortable 1.8.24
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Sortables
 *
 * Depends:
 *	jquery.ui.core.js
 *	jquery.ui.mouse.js
 *	jquery.ui.widget.js
 */
(function (a, b) {
    a.widget("ui.sortable", a.ui.mouse, {
        widgetEventPrefix: "sort",
        ready: false,
        options: {
            appendTo: "parent",
            axis: false,
            connectWith: false,
            containment: false,
            cursor: "auto",
            cursorAt: false,
            dropOnEmpty: true,
            forcePlaceholderSize: false,
            forceHelperSize: false,
            grid: false,
            handle: false,
            helper: "original",
            items: "> *",
            opacity: false,
            placeholder: false,
            revert: false,
            scroll: true,
            scrollSensitivity: 20,
            scrollSpeed: 20,
            scope: "default",
            tolerance: "intersect",
            zIndex: 1000
        },
        _create: function () {
            var c = this.options;
            this.containerCache = {};
            this.element.addClass("ui-sortable");
            this.refresh();
            this.floating = this.items.length ? c.axis === "x" || (/left|right/).test(this.items[0].item.css("float")) || (/inline|table-cell/).test(this.items[0].item.css("display")) : false;
            this.offset = this.element.offset();
            this._mouseInit();
            this.ready = true
        },
        destroy: function () {
            a.Widget.prototype.destroy.call(this);
            this.element.removeClass("ui-sortable ui-sortable-disabled");
            this._mouseDestroy();
            for (var c = this.items.length - 1; c >= 0; c--) {
                this.items[c].item.removeData(this.widgetName + "-item")
            }
            return this
        },
        _setOption: function (c, d) {
            if (c === "disabled") {
                this.options[c] = d;
                this.widget()[d ? "addClass" : "removeClass"]("ui-sortable-disabled")
            } else {
                a.Widget.prototype._setOption.apply(this, arguments)
            }
        },
        _mouseCapture: function (g, h) {
            var f = this;
            if (this.reverting) {
                return false
            }
            if (this.options.disabled || this.options.type == "static") {
                return false
            }
            this._refreshItems(g);
            var e = null,
                d = this,
                c = a(g.target).parents().each(function () {
                    if (a.data(this, f.widgetName + "-item") == d) {
                        e = a(this);
                        return false
                    }
                });
            if (a.data(g.target, f.widgetName + "-item") == d) {
                e = a(g.target)
            }
            if (!e) {
                return false
            }
            if (this.options.handle && !h) {
                var i = false;
                a(this.options.handle, e).find("*").andSelf().each(function () {
                    if (this == g.target) {
                        i = true
                    }
                });
                if (!i) {
                    return false
                }
            }
            this.currentItem = e;
            this._removeCurrentsFromItems();
            return true
        },
        _mouseStart: function (f, g, c) {
            var h = this.options,
                d = this;
            this.currentContainer = this;
            this.refreshPositions();
            this.helper = this._createHelper(f);
            this._cacheHelperProportions();
            this._cacheMargins();
            this.scrollParent = this.helper.scrollParent();
            this.offset = this.currentItem.offset();
            this.offset = {
                top: this.offset.top - this.margins.top,
                left: this.offset.left - this.margins.left
            };
            a.extend(this.offset, {
                click: {
                    left: f.pageX - this.offset.left,
                    top: f.pageY - this.offset.top
                },
                parent: this._getParentOffset(),
                relative: this._getRelativeOffset()
            });
            this.helper.css("position", "absolute");
            this.cssPosition = this.helper.css("position");
            this.originalPosition = this._generatePosition(f);
            this.originalPageX = f.pageX;
            this.originalPageY = f.pageY;
            (h.cursorAt && this._adjustOffsetFromHelper(h.cursorAt));
            this.domPosition = {
                prev: this.currentItem.prev()[0],
                parent: this.currentItem.parent()[0]
            };
            if (this.helper[0] != this.currentItem[0]) {
                this.currentItem.hide()
            }
            this._createPlaceholder();
            if (h.containment) {
                this._setContainment()
            }
            if (h.cursor) {
                if (a("body").css("cursor")) {
                    this._storedCursor = a("body").css("cursor")
                }
                a("body").css("cursor", h.cursor)
            }
            if (h.opacity) {
                if (this.helper.css("opacity")) {
                    this._storedOpacity = this.helper.css("opacity")
                }
                this.helper.css("opacity", h.opacity)
            }
            if (h.zIndex) {
                if (this.helper.css("zIndex")) {
                    this._storedZIndex = this.helper.css("zIndex")
                }
                this.helper.css("zIndex", h.zIndex)
            }
            if (this.scrollParent[0] != document && this.scrollParent[0].tagName != "HTML") {
                this.overflowOffset = this.scrollParent.offset()
            }
            this._trigger("start", f, this._uiHash());
            if (!this._preserveHelperProportions) {
                this._cacheHelperProportions()
            }
            if (!c) {
                for (var e = this.containers.length - 1; e >= 0; e--) {
                    this.containers[e]._trigger("activate", f, d._uiHash(this))
                }
            }
            if (a.ui.ddmanager) {
                a.ui.ddmanager.current = this
            }
            if (a.ui.ddmanager && !h.dropBehaviour) {
                a.ui.ddmanager.prepareOffsets(this, f)
            }
            this.dragging = true;
            this.helper.addClass("ui-sortable-helper");
            this._mouseDrag(f);
            return true
        },
        _mouseDrag: function (g) {
            this.position = this._generatePosition(g);
            this.positionAbs = this._convertPositionTo("absolute");
            if (!this.lastPositionAbs) {
                this.lastPositionAbs = this.positionAbs
            }
            if (this.options.scroll) {
                var h = this.options,
                    c = false;
                if (this.scrollParent[0] != document && this.scrollParent[0].tagName != "HTML") {
                    if ((this.overflowOffset.top + this.scrollParent[0].offsetHeight) - g.pageY < h.scrollSensitivity) {
                        this.scrollParent[0].scrollTop = c = this.scrollParent[0].scrollTop + h.scrollSpeed
                    } else {
                        if (g.pageY - this.overflowOffset.top < h.scrollSensitivity) {
                            this.scrollParent[0].scrollTop = c = this.scrollParent[0].scrollTop - h.scrollSpeed
                        }
                    } if ((this.overflowOffset.left + this.scrollParent[0].offsetWidth) - g.pageX < h.scrollSensitivity) {
                        this.scrollParent[0].scrollLeft = c = this.scrollParent[0].scrollLeft + h.scrollSpeed
                    } else {
                        if (g.pageX - this.overflowOffset.left < h.scrollSensitivity) {
                            this.scrollParent[0].scrollLeft = c = this.scrollParent[0].scrollLeft - h.scrollSpeed
                        }
                    }
                } else {
                    if (g.pageY - a(document).scrollTop() < h.scrollSensitivity) {
                        c = a(document).scrollTop(a(document).scrollTop() - h.scrollSpeed)
                    } else {
                        if (a(window).height() - (g.pageY - a(document).scrollTop()) < h.scrollSensitivity) {
                            c = a(document).scrollTop(a(document).scrollTop() + h.scrollSpeed)
                        }
                    } if (g.pageX - a(document).scrollLeft() < h.scrollSensitivity) {
                        c = a(document).scrollLeft(a(document).scrollLeft() - h.scrollSpeed)
                    } else {
                        if (a(window).width() - (g.pageX - a(document).scrollLeft()) < h.scrollSensitivity) {
                            c = a(document).scrollLeft(a(document).scrollLeft() + h.scrollSpeed)
                        }
                    }
                } if (c !== false && a.ui.ddmanager && !h.dropBehaviour) {
                    a.ui.ddmanager.prepareOffsets(this, g)
                }
            }
            this.positionAbs = this._convertPositionTo("absolute");
            if (!this.options.axis || this.options.axis != "y") {
                this.helper[0].style.left = this.position.left + "px"
            }
            if (!this.options.axis || this.options.axis != "x") {
                this.helper[0].style.top = this.position.top + "px"
            }
            for (var e = this.items.length - 1; e >= 0; e--) {
                var f = this.items[e],
                    d = f.item[0],
                    j = this._intersectsWithPointer(f);
                if (!j) {
                    continue
                }
                if (f.instance !== this.currentContainer) {
                    continue
                }
                if (d != this.currentItem[0] && this.placeholder[j == 1 ? "next" : "prev"]()[0] != d && !a.ui.contains(this.placeholder[0], d) && (this.options.type == "semi-dynamic" ? !a.ui.contains(this.element[0], d) : true)) {
                    this.direction = j == 1 ? "down" : "up";
                    if (this.options.tolerance == "pointer" || this._intersectsWithSides(f)) {
                        this._rearrange(g, f)
                    } else {
                        break
                    }
                    this._trigger("change", g, this._uiHash());
                    break
                }
            }
            this._contactContainers(g);
            if (a.ui.ddmanager) {
                a.ui.ddmanager.drag(this, g)
            }
            this._trigger("sort", g, this._uiHash());
            this.lastPositionAbs = this.positionAbs;
            return false
        },
        _mouseStop: function (d, e) {
            if (!d) {
                return
            }
            if (a.ui.ddmanager && !this.options.dropBehaviour) {
                a.ui.ddmanager.drop(this, d)
            }
            if (this.options.revert) {
                var c = this;
                var f = c.placeholder.offset();
                c.reverting = true;
                a(this.helper).animate({
                    left: f.left - this.offset.parent.left - c.margins.left + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollLeft),
                    top: f.top - this.offset.parent.top - c.margins.top + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollTop)
                }, parseInt(this.options.revert, 10) || 500, function () {
                    c._clear(d)
                })
            } else {
                this._clear(d, e)
            }
            return false
        },
        cancel: function () {
            var c = this;
            if (this.dragging) {
                this._mouseUp({
                    target: null
                });
                if (this.options.helper == "original") {
                    this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper")
                } else {
                    this.currentItem.show()
                }
                for (var d = this.containers.length - 1; d >= 0; d--) {
                    this.containers[d]._trigger("deactivate", null, c._uiHash(this));
                    if (this.containers[d].containerCache.over) {
                        this.containers[d]._trigger("out", null, c._uiHash(this));
                        this.containers[d].containerCache.over = 0
                    }
                }
            }
            if (this.placeholder) {
                if (this.placeholder[0].parentNode) {
                    this.placeholder[0].parentNode.removeChild(this.placeholder[0])
                }
                if (this.options.helper != "original" && this.helper && this.helper[0].parentNode) {
                    this.helper.remove()
                }
                a.extend(this, {
                    helper: null,
                    dragging: false,
                    reverting: false,
                    _noFinalSort: null
                });
                if (this.domPosition.prev) {
                    a(this.domPosition.prev).after(this.currentItem)
                } else {
                    a(this.domPosition.parent).prepend(this.currentItem)
                }
            }
            return this
        },
        serialize: function (e) {
            var c = this._getItemsAsjQuery(e && e.connected);
            var d = [];
            e = e || {};
            a(c).each(function () {
                var f = (a(e.item || this).attr(e.attribute || "id") || "").match(e.expression || (/(.+)[-=_](.+)/));
                if (f) {
                    d.push((e.key || f[1] + "[]") + "=" + (e.key && e.expression ? f[1] : f[2]))
                }
            });
            if (!d.length && e.key) {
                d.push(e.key + "=")
            }
            return d.join("&")
        },
        toArray: function (e) {
            var c = this._getItemsAsjQuery(e && e.connected);
            var d = [];
            e = e || {};
            c.each(function () {
                d.push(a(e.item || this).attr(e.attribute || "id") || "")
            });
            return d
        },
        _intersectsWith: function (m) {
            var e = this.positionAbs.left,
                d = e + this.helperProportions.width,
                k = this.positionAbs.top,
                j = k + this.helperProportions.height;
            var f = m.left,
                c = f + m.width,
                n = m.top,
                i = n + m.height;
            var o = this.offset.click.top,
                h = this.offset.click.left;
            var g = (k + o) > n && (k + o) < i && (e + h) > f && (e + h) < c;
            if (this.options.tolerance == "pointer" || this.options.forcePointerForContainers || (this.options.tolerance != "pointer" && this.helperProportions[this.floating ? "width" : "height"] > m[this.floating ? "width" : "height"])) {
                return g
            } else {
                return (f < e + (this.helperProportions.width / 2) && d - (this.helperProportions.width / 2) < c && n < k + (this.helperProportions.height / 2) && j - (this.helperProportions.height / 2) < i)
            }
        },
        _intersectsWithPointer: function (e) {
            var f = (this.options.axis === "x") || a.ui.isOverAxis(this.positionAbs.top + this.offset.click.top, e.top, e.height),
                d = (this.options.axis === "y") || a.ui.isOverAxis(this.positionAbs.left + this.offset.click.left, e.left, e.width),
                h = f && d,
                c = this._getDragVerticalDirection(),
                g = this._getDragHorizontalDirection();
            if (!h) {
                return false
            }
            return this.floating ? (((g && g == "right") || c == "down") ? 2 : 1) : (c && (c == "down" ? 2 : 1))
        },
        _intersectsWithSides: function (f) {
            var d = a.ui.isOverAxis(this.positionAbs.top + this.offset.click.top, f.top + (f.height / 2), f.height),
                e = a.ui.isOverAxis(this.positionAbs.left + this.offset.click.left, f.left + (f.width / 2), f.width),
                c = this._getDragVerticalDirection(),
                g = this._getDragHorizontalDirection();
            if (this.floating && g) {
                return ((g == "right" && e) || (g == "left" && !e))
            } else {
                return c && ((c == "down" && d) || (c == "up" && !d))
            }
        },
        _getDragVerticalDirection: function () {
            var c = this.positionAbs.top - this.lastPositionAbs.top;
            return c != 0 && (c > 0 ? "down" : "up")
        },
        _getDragHorizontalDirection: function () {
            var c = this.positionAbs.left - this.lastPositionAbs.left;
            return c != 0 && (c > 0 ? "right" : "left")
        },
        refresh: function (c) {
            this._refreshItems(c);
            this.refreshPositions();
            return this
        },
        _connectWith: function () {
            var c = this.options;
            return c.connectWith.constructor == String ? [c.connectWith] : c.connectWith
        },
        _getItemsAsjQuery: function (c) {
            var m = this;
            var h = [];
            var f = [];
            var k = this._connectWith();
            if (k && c) {
                for (var e = k.length - 1; e >= 0; e--) {
                    var l = a(k[e]);
                    for (var d = l.length - 1; d >= 0; d--) {
                        var g = a.data(l[d], this.widgetName);
                        if (g && g != this && !g.options.disabled) {
                            f.push([a.isFunction(g.options.items) ? g.options.items.call(g.element) : a(g.options.items, g.element).not(".ui-sortable-helper").not(".ui-sortable-placeholder"), g])
                        }
                    }
                }
            }
            f.push([a.isFunction(this.options.items) ? this.options.items.call(this.element, null, {
                options: this.options,
                item: this.currentItem
            }) : a(this.options.items, this.element).not(".ui-sortable-helper").not(".ui-sortable-placeholder"), this]);
            for (var e = f.length - 1; e >= 0; e--) {
                f[e][0].each(function () {
                    h.push(this)
                })
            }
            return a(h)
        },
        _removeCurrentsFromItems: function () {
            var e = this.currentItem.find(":data(" + this.widgetName + "-item)");
            for (var d = 0; d < this.items.length; d++) {
                for (var c = 0; c < e.length; c++) {
                    if (e[c] == this.items[d].item[0]) {
                        this.items.splice(d, 1)
                    }
                }
            }
        },
        _refreshItems: function (c) {
            this.items = [];
            this.containers = [this];
            var k = this.items;
            var q = this;
            var g = [
                [a.isFunction(this.options.items) ? this.options.items.call(this.element[0], c, {
                    item: this.currentItem
                }) : a(this.options.items, this.element), this]
            ];
            var m = this._connectWith();
            if (m && this.ready) {
                for (var f = m.length - 1; f >= 0; f--) {
                    var n = a(m[f]);
                    for (var e = n.length - 1; e >= 0; e--) {
                        var h = a.data(n[e], this.widgetName);
                        if (h && h != this && !h.options.disabled) {
                            g.push([a.isFunction(h.options.items) ? h.options.items.call(h.element[0], c, {
                                item: this.currentItem
                            }) : a(h.options.items, h.element), h]);
                            this.containers.push(h)
                        }
                    }
                }
            }
            for (var f = g.length - 1; f >= 0; f--) {
                var l = g[f][1];
                var d = g[f][0];
                for (var e = 0, o = d.length; e < o; e++) {
                    var p = a(d[e]);
                    p.data(this.widgetName + "-item", l);
                    k.push({
                        item: p,
                        instance: l,
                        width: 0,
                        height: 0,
                        left: 0,
                        top: 0
                    })
                }
            }
        },
        refreshPositions: function (c) {
            if (this.offsetParent && this.helper) {
                this.offset.parent = this._getParentOffset()
            }
            for (var e = this.items.length - 1; e >= 0; e--) {
                var f = this.items[e];
                if (f.instance != this.currentContainer && this.currentContainer && f.item[0] != this.currentItem[0]) {
                    continue
                }
                var d = this.options.toleranceElement ? a(this.options.toleranceElement, f.item) : f.item;
                if (!c) {
                    f.width = d.outerWidth();
                    f.height = d.outerHeight()
                }
                var g = d.offset();
                f.left = g.left;
                f.top = g.top
            }
            if (this.options.custom && this.options.custom.refreshContainers) {
                this.options.custom.refreshContainers.call(this)
            } else {
                for (var e = this.containers.length - 1; e >= 0; e--) {
                    var g = this.containers[e].element.offset();
                    this.containers[e].containerCache.left = g.left;
                    this.containers[e].containerCache.top = g.top;
                    this.containers[e].containerCache.width = this.containers[e].element.outerWidth();
                    this.containers[e].containerCache.height = this.containers[e].element.outerHeight()
                }
            }
            return this
        },
        _createPlaceholder: function (e) {
            var c = e || this,
                f = c.options;
            if (!f.placeholder || f.placeholder.constructor == String) {
                var d = f.placeholder;
                f.placeholder = {
                    element: function () {
                        var g = a(document.createElement(c.currentItem[0].nodeName)).addClass(d || c.currentItem[0].className + " ui-sortable-placeholder").removeClass("ui-sortable-helper")[0];
                        if (!d) {
                            g.style.visibility = "hidden"
                        }
                        return g
                    },
                    update: function (g, h) {
                        if (d && !f.forcePlaceholderSize) {
                            return
                        }
                        if (!h.height()) {
                            h.height(c.currentItem.innerHeight() - parseInt(c.currentItem.css("paddingTop") || 0, 10) - parseInt(c.currentItem.css("paddingBottom") || 0, 10))
                        }
                        if (!h.width()) {
                            h.width(c.currentItem.innerWidth() - parseInt(c.currentItem.css("paddingLeft") || 0, 10) - parseInt(c.currentItem.css("paddingRight") || 0, 10))
                        }
                    }
                }
            }
            c.placeholder = a(f.placeholder.element.call(c.element, c.currentItem));
            c.currentItem.after(c.placeholder);
            f.placeholder.update(c, c.placeholder)
        },
        _contactContainers: function (c) {
            var e = null,
                l = null;
            for (var g = this.containers.length - 1; g >= 0; g--) {
                if (a.ui.contains(this.currentItem[0], this.containers[g].element[0])) {
                    continue
                }
                if (this._intersectsWith(this.containers[g].containerCache)) {
                    if (e && a.ui.contains(this.containers[g].element[0], e.element[0])) {
                        continue
                    }
                    e = this.containers[g];
                    l = g
                } else {
                    if (this.containers[g].containerCache.over) {
                        this.containers[g]._trigger("out", c, this._uiHash(this));
                        this.containers[g].containerCache.over = 0
                    }
                }
            }
            if (!e) {
                return
            }
            if (this.containers.length === 1) {
                this.containers[l]._trigger("over", c, this._uiHash(this));
                this.containers[l].containerCache.over = 1
            } else {
                if (this.currentContainer != this.containers[l]) {
                    var k = 10000;
                    var h = null;
                    var d = this.positionAbs[this.containers[l].floating ? "left" : "top"];
                    for (var f = this.items.length - 1; f >= 0; f--) {
                        if (!a.ui.contains(this.containers[l].element[0], this.items[f].item[0])) {
                            continue
                        }
                        var m = this.containers[l].floating ? this.items[f].item.offset().left : this.items[f].item.offset().top;
                        if (Math.abs(m - d) < k) {
                            k = Math.abs(m - d);
                            h = this.items[f];
                            this.direction = (m - d > 0) ? "down" : "up"
                        }
                    }
                    if (!h && !this.options.dropOnEmpty) {
                        return
                    }
                    this.currentContainer = this.containers[l];
                    h ? this._rearrange(c, h, null, true) : this._rearrange(c, null, this.containers[l].element, true);
                    this._trigger("change", c, this._uiHash());
                    this.containers[l]._trigger("change", c, this._uiHash(this));
                    this.options.placeholder.update(this.currentContainer, this.placeholder);
                    this.containers[l]._trigger("over", c, this._uiHash(this));
                    this.containers[l].containerCache.over = 1
                }
            }
        },
        _createHelper: function (d) {
            var e = this.options;
            var c = a.isFunction(e.helper) ? a(e.helper.apply(this.element[0], [d, this.currentItem])) : (e.helper == "clone" ? this.currentItem.clone() : this.currentItem);
            if (!c.parents("body").length) {
                a(e.appendTo != "parent" ? e.appendTo : this.currentItem[0].parentNode)[0].appendChild(c[0])
            }
            if (c[0] == this.currentItem[0]) {
                this._storedCSS = {
                    width: this.currentItem[0].style.width,
                    height: this.currentItem[0].style.height,
                    position: this.currentItem.css("position"),
                    top: this.currentItem.css("top"),
                    left: this.currentItem.css("left")
                }
            }
            if (c[0].style.width == "" || e.forceHelperSize) {
                c.width(this.currentItem.width())
            }
            if (c[0].style.height == "" || e.forceHelperSize) {
                c.height(this.currentItem.height())
            }
            return c
        },
        _adjustOffsetFromHelper: function (c) {
            if (typeof c == "string") {
                c = c.split(" ")
            }
            if (a.isArray(c)) {
                c = {
                    left: +c[0],
                    top: +c[1] || 0
                }
            }
            if ("left" in c) {
                this.offset.click.left = c.left + this.margins.left
            }
            if ("right" in c) {
                this.offset.click.left = this.helperProportions.width - c.right + this.margins.left
            }
            if ("top" in c) {
                this.offset.click.top = c.top + this.margins.top
            }
            if ("bottom" in c) {
                this.offset.click.top = this.helperProportions.height - c.bottom + this.margins.top
            }
        },
        _getParentOffset: function () {
            this.offsetParent = this.helper.offsetParent();
            var c = this.offsetParent.offset();
            if (this.cssPosition == "absolute" && this.scrollParent[0] != document && a.ui.contains(this.scrollParent[0], this.offsetParent[0])) {
                c.left += this.scrollParent.scrollLeft();
                c.top += this.scrollParent.scrollTop()
            }
            if ((this.offsetParent[0] == document.body) || (this.offsetParent[0].tagName && this.offsetParent[0].tagName.toLowerCase() == "html" && a.browser.msie)) {
                c = {
                    top: 0,
                    left: 0
                }
            }
            return {
                top: c.top + (parseInt(this.offsetParent.css("borderTopWidth"), 10) || 0),
                left: c.left + (parseInt(this.offsetParent.css("borderLeftWidth"), 10) || 0)
            }
        },
        _getRelativeOffset: function () {
            if (this.cssPosition == "relative") {
                var c = this.currentItem.position();
                return {
                    top: c.top - (parseInt(this.helper.css("top"), 10) || 0) + this.scrollParent.scrollTop(),
                    left: c.left - (parseInt(this.helper.css("left"), 10) || 0) + this.scrollParent.scrollLeft()
                }
            } else {
                return {
                    top: 0,
                    left: 0
                }
            }
        },
        _cacheMargins: function () {
            this.margins = {
                left: (parseInt(this.currentItem.css("marginLeft"), 10) || 0),
                top: (parseInt(this.currentItem.css("marginTop"), 10) || 0)
            }
        },
        _cacheHelperProportions: function () {
            this.helperProportions = {
                width: this.helper.outerWidth(),
                height: this.helper.outerHeight()
            }
        },
        _setContainment: function () {
            var f = this.options;
            if (f.containment == "parent") {
                f.containment = this.helper[0].parentNode
            }
            if (f.containment == "document" || f.containment == "window") {
                this.containment = [0 - this.offset.relative.left - this.offset.parent.left, 0 - this.offset.relative.top - this.offset.parent.top, a(f.containment == "document" ? document : window).width() - this.helperProportions.width - this.margins.left, (a(f.containment == "document" ? document : window).height() || document.body.parentNode.scrollHeight) - this.helperProportions.height - this.margins.top]
            }
            if (!(/^(document|window|parent)$/).test(f.containment)) {
                var d = a(f.containment)[0];
                var e = a(f.containment).offset();
                var c = (a(d).css("overflow") != "hidden");
                this.containment = [e.left + (parseInt(a(d).css("borderLeftWidth"), 10) || 0) + (parseInt(a(d).css("paddingLeft"), 10) || 0) - this.margins.left, e.top + (parseInt(a(d).css("borderTopWidth"), 10) || 0) + (parseInt(a(d).css("paddingTop"), 10) || 0) - this.margins.top, e.left + (c ? Math.max(d.scrollWidth, d.offsetWidth) : d.offsetWidth) - (parseInt(a(d).css("borderLeftWidth"), 10) || 0) - (parseInt(a(d).css("paddingRight"), 10) || 0) - this.helperProportions.width - this.margins.left, e.top + (c ? Math.max(d.scrollHeight, d.offsetHeight) : d.offsetHeight) - (parseInt(a(d).css("borderTopWidth"), 10) || 0) - (parseInt(a(d).css("paddingBottom"), 10) || 0) - this.helperProportions.height - this.margins.top]
            }
        },
        _convertPositionTo: function (g, i) {
            if (!i) {
                i = this.position
            }
            var e = g == "absolute" ? 1 : -1;
            var f = this.options,
                c = this.cssPosition == "absolute" && !(this.scrollParent[0] != document && a.ui.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent,
                h = (/(html|body)/i).test(c[0].tagName);
            return {
                top: (i.top + this.offset.relative.top * e + this.offset.parent.top * e - (a.browser.safari && this.cssPosition == "fixed" ? 0 : (this.cssPosition == "fixed" ? -this.scrollParent.scrollTop() : (h ? 0 : c.scrollTop())) * e)),
                left: (i.left + this.offset.relative.left * e + this.offset.parent.left * e - (a.browser.safari && this.cssPosition == "fixed" ? 0 : (this.cssPosition == "fixed" ? -this.scrollParent.scrollLeft() : h ? 0 : c.scrollLeft()) * e))
            }
        },
        _generatePosition: function (f) {
            var i = this.options,
                c = this.cssPosition == "absolute" && !(this.scrollParent[0] != document && a.ui.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent,
                j = (/(html|body)/i).test(c[0].tagName);
            if (this.cssPosition == "relative" && !(this.scrollParent[0] != document && this.scrollParent[0] != this.offsetParent[0])) {
                this.offset.relative = this._getRelativeOffset()
            }
            var e = f.pageX;
            var d = f.pageY;
            if (this.originalPosition) {
                if (this.containment) {
                    if (f.pageX - this.offset.click.left < this.containment[0]) {
                        e = this.containment[0] + this.offset.click.left
                    }
                    if (f.pageY - this.offset.click.top < this.containment[1]) {
                        d = this.containment[1] + this.offset.click.top
                    }
                    if (f.pageX - this.offset.click.left > this.containment[2]) {
                        e = this.containment[2] + this.offset.click.left
                    }
                    if (f.pageY - this.offset.click.top > this.containment[3]) {
                        d = this.containment[3] + this.offset.click.top
                    }
                }
                if (i.grid) {
                    var h = this.originalPageY + Math.round((d - this.originalPageY) / i.grid[1]) * i.grid[1];
                    d = this.containment ? (!(h - this.offset.click.top < this.containment[1] || h - this.offset.click.top > this.containment[3]) ? h : (!(h - this.offset.click.top < this.containment[1]) ? h - i.grid[1] : h + i.grid[1])) : h;
                    var g = this.originalPageX + Math.round((e - this.originalPageX) / i.grid[0]) * i.grid[0];
                    e = this.containment ? (!(g - this.offset.click.left < this.containment[0] || g - this.offset.click.left > this.containment[2]) ? g : (!(g - this.offset.click.left < this.containment[0]) ? g - i.grid[0] : g + i.grid[0])) : g
                }
            }
            return {
                top: (d - this.offset.click.top - this.offset.relative.top - this.offset.parent.top + (a.browser.safari && this.cssPosition == "fixed" ? 0 : (this.cssPosition == "fixed" ? -this.scrollParent.scrollTop() : (j ? 0 : c.scrollTop())))),
                left: (e - this.offset.click.left - this.offset.relative.left - this.offset.parent.left + (a.browser.safari && this.cssPosition == "fixed" ? 0 : (this.cssPosition == "fixed" ? -this.scrollParent.scrollLeft() : j ? 0 : c.scrollLeft())))
            }
        },
        _rearrange: function (h, g, d, f) {
            d ? d[0].appendChild(this.placeholder[0]) : g.item[0].parentNode.insertBefore(this.placeholder[0], (this.direction == "down" ? g.item[0] : g.item[0].nextSibling));
            this.counter = this.counter ? ++this.counter : 1;
            var e = this,
                c = this.counter;
            window.setTimeout(function () {
                if (c == e.counter) {
                    e.refreshPositions(!f)
                }
            }, 0)
        },
        _clear: function (e, f) {
            this.reverting = false;
            var g = [],
                c = this;
            if (!this._noFinalSort && this.currentItem.parent().length) {
                this.placeholder.before(this.currentItem)
            }
            this._noFinalSort = null;
            if (this.helper[0] == this.currentItem[0]) {
                for (var d in this._storedCSS) {
                    if (this._storedCSS[d] == "auto" || this._storedCSS[d] == "static") {
                        this._storedCSS[d] = ""
                    }
                }
                this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper")
            } else {
                this.currentItem.show()
            } if (this.fromOutside && !f) {
                g.push(function (h) {
                    this._trigger("receive", h, this._uiHash(this.fromOutside))
                })
            }
            if ((this.fromOutside || this.domPosition.prev != this.currentItem.prev().not(".ui-sortable-helper")[0] || this.domPosition.parent != this.currentItem.parent()[0]) && !f) {
                g.push(function (h) {
                    this._trigger("update", h, this._uiHash())
                })
            }
            if (this !== this.currentContainer) {
                if (!f) {
                    g.push(function (h) {
                        this._trigger("remove", h, this._uiHash())
                    });
                    g.push((function (h) {
                        return function (i) {
                            h._trigger("receive", i, this._uiHash(this))
                        }
                    }).call(this, this.currentContainer));
                    g.push((function (h) {
                        return function (i) {
                            h._trigger("update", i, this._uiHash(this))
                        }
                    }).call(this, this.currentContainer))
                }
            }
            for (var d = this.containers.length - 1; d >= 0; d--) {
                if (!f) {
                    g.push((function (h) {
                        return function (i) {
                            h._trigger("deactivate", i, this._uiHash(this))
                        }
                    }).call(this, this.containers[d]))
                }
                if (this.containers[d].containerCache.over) {
                    g.push((function (h) {
                        return function (i) {
                            h._trigger("out", i, this._uiHash(this))
                        }
                    }).call(this, this.containers[d]));
                    this.containers[d].containerCache.over = 0
                }
            }
            if (this._storedCursor) {
                a("body").css("cursor", this._storedCursor)
            }
            if (this._storedOpacity) {
                this.helper.css("opacity", this._storedOpacity)
            }
            if (this._storedZIndex) {
                this.helper.css("zIndex", this._storedZIndex == "auto" ? "" : this._storedZIndex)
            }
            this.dragging = false;
            if (this.cancelHelperRemoval) {
                if (!f) {
                    this._trigger("beforeStop", e, this._uiHash());
                    for (var d = 0; d < g.length; d++) {
                        g[d].call(this, e)
                    }
                    this._trigger("stop", e, this._uiHash())
                }
                this.fromOutside = false;
                return false
            }
            if (!f) {
                this._trigger("beforeStop", e, this._uiHash())
            }
            this.placeholder[0].parentNode.removeChild(this.placeholder[0]);
            if (this.helper[0] != this.currentItem[0]) {
                this.helper.remove()
            }
            this.helper = null;
            if (!f) {
                for (var d = 0; d < g.length; d++) {
                    g[d].call(this, e)
                }
                this._trigger("stop", e, this._uiHash())
            }
            this.fromOutside = false;
            return true
        },
        _trigger: function () {
            if (a.Widget.prototype._trigger.apply(this, arguments) === false) {
                this.cancel()
            }
        },
        _uiHash: function (d) {
            var c = d || this;
            return {
                helper: c.helper,
                placeholder: c.placeholder || a([]),
                position: c.position,
                originalPosition: c.originalPosition,
                offset: c.positionAbs,
                item: c.currentItem,
                sender: d ? d.element : null
            }
        }
    });
    a.extend(a.ui.sortable, {
        version: "1.8.24"
    })
})(jQuery);

/*! AUI Flat Pack - version 5.2 - generated 2013-07-25 10:18:38 +0000 */


jQuery.noConflict();
/*
 * jQuery Form Plugin
 * version: 2.67 (12-MAR-2011)
 * @requires jQuery v1.3.2 or later
 *
 * Examples and documentation at: http://malsup.com/jquery/form/
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 */
(function (b) {
    b.fn.ajaxSubmit = function (t) {
        if (!this.length) {
            a("ajaxSubmit: skipping submit process - no element selected");
            return this
        }
        if (typeof t == "function") {
            t = {
                success: t
            }
        }
        var h = this.attr("action");
        var d = (typeof h === "string") ? b.trim(h) : "";
        if (d) {
            d = (d.match(/^([^#]+)/) || [])[1]
        }
        d = d || window.location.href || "";
        t = b.extend(true, {
            url: d,
            type: this[0].getAttribute("method") || "GET",
            iframeSrc: /^https/i.test(window.location.href || "") ? "javascript:false" : "about:blank"
        }, t);
        var u = {};
        this.trigger("form-pre-serialize", [this, t, u]);
        if (u.veto) {
            a("ajaxSubmit: submit vetoed via form-pre-serialize trigger");
            return this
        }
        if (t.beforeSerialize && t.beforeSerialize(this, t) === false) {
            a("ajaxSubmit: submit aborted via beforeSerialize callback");
            return this
        }
        var f, p, m = this.formToArray(t.semantic);
        if (t.data) {
            t.extraData = t.data;
            for (f in t.data) {
                if (t.data[f] instanceof Array) {
                    for (var i in t.data[f]) {
                        m.push({
                            name: f,
                            value: t.data[f][i]
                        })
                    }
                } else {
                    p = t.data[f];
                    p = b.isFunction(p) ? p() : p;
                    m.push({
                        name: f,
                        value: p
                    })
                }
            }
        }
        if (t.beforeSubmit && t.beforeSubmit(m, this, t) === false) {
            a("ajaxSubmit: submit aborted via beforeSubmit callback");
            return this
        }
        this.trigger("form-submit-validate", [m, this, t, u]);
        if (u.veto) {
            a("ajaxSubmit: submit vetoed via form-submit-validate trigger");
            return this
        }
        var c = b.param(m);
        if (t.type.toUpperCase() == "GET") {
            t.url += (t.url.indexOf("?") >= 0 ? "&" : "?") + c;
            t.data = null
        } else {
            t.data = c
        }
        var s = this,
            l = [];
        if (t.resetForm) {
            l.push(function () {
                s.resetForm()
            })
        }
        if (t.clearForm) {
            l.push(function () {
                s.clearForm()
            })
        }
        if (!t.dataType && t.target) {
            var r = t.success || function () {};
            l.push(function (n) {
                var k = t.replaceTarget ? "replaceWith" : "html";
                b(t.target)[k](n).each(r, arguments)
            })
        } else {
            if (t.success) {
                l.push(t.success)
            }
        }
        t.success = function (w, n, x) {
            var v = t.context || t;
            for (var q = 0, k = l.length; q < k; q++) {
                l[q].apply(v, [w, n, x || s, s])
            }
        };
        var g = b("input:file", this).length > 0;
        var e = "multipart/form-data";
        var j = (s.attr("enctype") == e || s.attr("encoding") == e);
        if (t.iframe !== false && (g || t.iframe || j)) {
            if (t.closeKeepAlive) {
                b.get(t.closeKeepAlive, o)
            } else {
                o()
            }
        } else {
            b.ajax(t)
        }
        this.trigger("form-submit-notify", [this, t]);
        return this;

        function o() {
            var v = s[0];
            if (b(":input[name=submit],:input[id=submit]", v).length) {
                alert('Error: Form elements must not have name or id of "submit".');
                return
            }
            var B = b.extend(true, {}, b.ajaxSettings, t);
            B.context = B.context || B;
            var E = "jqFormIO" + (new Date().getTime()),
                z = "_" + E;
            var w = b('<iframe id="' + E + '" name="' + E + '" src="' + B.iframeSrc + '" />');
            var A = w[0];
            w.css({
                position: "absolute",
                top: "-1000px",
                left: "-1000px"
            });
            var x = {
                aborted: 0,
                responseText: null,
                responseXML: null,
                status: 0,
                statusText: "n/a",
                getAllResponseHeaders: function () {},
                getResponseHeader: function () {},
                setRequestHeader: function () {},
                abort: function () {
                    a("aborting upload...");
                    var n = "aborted";
                    this.aborted = 1;
                    w.attr("src", B.iframeSrc);
                    x.error = n;
                    B.error && B.error.call(B.context, x, "error", n);
                    I && b.event.trigger("ajaxError", [x, B, n]);
                    B.complete && B.complete.call(B.context, x, "error")
                }
            };
            var I = B.global;
            if (I && !b.active++) {
                b.event.trigger("ajaxStart")
            }
            if (I) {
                b.event.trigger("ajaxSend", [x, B])
            }
            if (B.beforeSend && B.beforeSend.call(B.context, x, B) === false) {
                if (B.global) {
                    b.active--
                }
                return
            }
            if (x.aborted) {
                return
            }
            var H = 0;
            var y = v.clk;
            if (y) {
                var F = y.name;
                if (F && !y.disabled) {
                    B.extraData = B.extraData || {};
                    B.extraData[F] = y.value;
                    if (y.type == "image") {
                        B.extraData[F + ".x"] = v.clk_x;
                        B.extraData[F + ".y"] = v.clk_y
                    }
                }
            }

            function G() {
                var O = s.attr("target"),
                    M = s.attr("action");
                v.setAttribute("target", E);
                if (v.getAttribute("method") != "POST") {
                    v.setAttribute("method", "POST")
                }
                if (v.getAttribute("action") != B.url) {
                    v.setAttribute("action", B.url)
                }
                if (!B.skipEncodingOverride) {
                    s.attr({
                        encoding: "multipart/form-data",
                        enctype: "multipart/form-data"
                    })
                }
                if (B.timeout) {
                    setTimeout(function () {
                        H = true;
                        D()
                    }, B.timeout)
                }
                var N = [];
                try {
                    if (B.extraData) {
                        for (var P in B.extraData) {
                            N.push(b('<input type="hidden" name="' + P + '" value="' + B.extraData[P] + '" />').appendTo(v)[0])
                        }
                    }
                    w.appendTo("body");
                    A.attachEvent ? A.attachEvent("onload", D) : A.addEventListener("load", D, false);
                    v.submit()
                } finally {
                    v.setAttribute("action", M);
                    if (O) {
                        v.setAttribute("target", O)
                    } else {
                        s.removeAttr("target")
                    }
                    b(N).remove()
                }
            }
            if (B.forceSync) {
                G()
            } else {
                setTimeout(G, 10)
            }
            var K, L, J = 50;

            function D() {
                if (x.aborted) {
                    return
                }
                var R = A.contentWindow ? A.contentWindow.document : A.contentDocument ? A.contentDocument : A.document;
                if (!R || R.location.href == B.iframeSrc) {
                    return
                }
                A.detachEvent ? A.detachEvent("onload", D) : A.removeEventListener("load", D, false);
                var N = true;
                try {
                    if (H) {
                        throw "timeout"
                    }
                    var S = B.dataType == "xml" || R.XMLDocument || b.isXMLDoc(R);
                    a("isXml=" + S);
                    if (!S && window.opera && (R.body == null || R.body.innerHTML == "")) {
                        if (--J) {
                            a("requeing onLoad callback, DOM not available");
                            setTimeout(D, 250);
                            return
                        }
                    }
                    x.responseText = R.body ? R.body.innerHTML : R.documentElement ? R.documentElement.innerHTML : null;
                    x.responseXML = R.XMLDocument ? R.XMLDocument : R;
                    x.getResponseHeader = function (U) {
                        var T = {
                            "content-type": B.dataType
                        };
                        return T[U]
                    };
                    var Q = /(json|script)/.test(B.dataType);
                    if (Q || B.textarea) {
                        var M = R.getElementsByTagName("textarea")[0];
                        if (M) {
                            x.responseText = M.value
                        } else {
                            if (Q) {
                                var P = R.getElementsByTagName("pre")[0];
                                var n = R.getElementsByTagName("body")[0];
                                if (P) {
                                    x.responseText = P.textContent
                                } else {
                                    if (n) {
                                        x.responseText = n.innerHTML
                                    }
                                }
                            }
                        }
                    } else {
                        if (B.dataType == "xml" && !x.responseXML && x.responseText != null) {
                            x.responseXML = C(x.responseText)
                        }
                    }
                    K = k(x, B.dataType, B)
                } catch (O) {
                    a("error caught:", O);
                    N = false;
                    x.error = O;
                    B.error && B.error.call(B.context, x, "error", O);
                    I && b.event.trigger("ajaxError", [x, B, O])
                }
                if (x.aborted) {
                    a("upload aborted");
                    N = false
                }
                if (N) {
                    B.success && B.success.call(B.context, K, "success", x);
                    I && b.event.trigger("ajaxSuccess", [x, B])
                }
                I && b.event.trigger("ajaxComplete", [x, B]);
                if (I && !--b.active) {
                    b.event.trigger("ajaxStop")
                }
                B.complete && B.complete.call(B.context, x, N ? "success" : "error");
                setTimeout(function () {
                    w.removeData("form-plugin-onload");
                    w.remove();
                    x.responseXML = null
                }, 100)
            }
            var C = b.parseXML || function (n, M) {
                    if (window.ActiveXObject) {
                        M = new ActiveXObject("Microsoft.XMLDOM");
                        M.async = "false";
                        M.loadXML(n)
                    } else {
                        M = (new DOMParser()).parseFromString(n, "text/xml")
                    }
                    return (M && M.documentElement && M.documentElement.nodeName != "parsererror") ? M : null
                };
            var q = b.parseJSON || function (n) {
                    return window["eval"]("(" + n + ")")
                };
            var k = function (Q, O, N) {
                var M = Q.getResponseHeader("content-type") || "",
                    n = O === "xml" || !O && M.indexOf("xml") >= 0,
                    P = n ? Q.responseXML : Q.responseText;
                if (n && P.documentElement.nodeName === "parsererror") {
                    b.error && b.error("parsererror")
                }
                if (N && N.dataFilter) {
                    P = N.dataFilter(P, O)
                }
                if (typeof P === "string") {
                    if (O === "json" || !O && M.indexOf("json") >= 0) {
                        P = q(P)
                    } else {
                        if (O === "script" || !O && M.indexOf("javascript") >= 0) {
                            b.globalEval(P)
                        }
                    }
                }
                return P
            }
        }
    };
    b.fn.ajaxForm = function (c) {
        if (this.length === 0) {
            var d = {
                s: this.selector,
                c: this.context
            };
            if (!b.isReady && d.s) {
                a("DOM not ready, queuing ajaxForm");
                b(function () {
                    b(d.s, d.c).ajaxForm(c)
                });
                return this
            }
            a("terminating; zero elements found by selector" + (b.isReady ? "" : " (DOM not ready)"));
            return this
        }
        return this.ajaxFormUnbind().bind("submit.form-plugin", function (f) {
            if (!f.isDefaultPrevented()) {
                f.preventDefault();
                b(this).ajaxSubmit(c)
            }
        }).bind("click.form-plugin", function (j) {
            var i = j.target;
            var g = b(i);
            if (!(g.is(":submit,input:image"))) {
                var f = g.closest(":submit");
                if (f.length == 0) {
                    return
                }
                i = f[0]
            }
            var h = this;
            h.clk = i;
            if (i.type == "image") {
                if (j.offsetX != undefined) {
                    h.clk_x = j.offsetX;
                    h.clk_y = j.offsetY
                } else {
                    if (typeof b.fn.offset == "function") {
                        var k = g.offset();
                        h.clk_x = j.pageX - k.left;
                        h.clk_y = j.pageY - k.top
                    } else {
                        h.clk_x = j.pageX - i.offsetLeft;
                        h.clk_y = j.pageY - i.offsetTop
                    }
                }
            }
            setTimeout(function () {
                h.clk = h.clk_x = h.clk_y = null
            }, 100)
        })
    };
    b.fn.ajaxFormUnbind = function () {
        return this.unbind("submit.form-plugin click.form-plugin")
    };
    b.fn.formToArray = function (q) {
        var p = [];
        if (this.length === 0) {
            return p
        }
        var d = this[0];
        var g = q ? d.getElementsByTagName("*") : d.elements;
        if (!g) {
            return p
        }
        var k, h, f, r, e, m, c;
        for (k = 0, m = g.length; k < m; k++) {
            e = g[k];
            f = e.name;
            if (!f) {
                continue
            }
            if (q && d.clk && e.type == "image") {
                if (!e.disabled && d.clk == e) {
                    p.push({
                        name: f,
                        value: b(e).val()
                    });
                    p.push({
                        name: f + ".x",
                        value: d.clk_x
                    }, {
                        name: f + ".y",
                        value: d.clk_y
                    })
                }
                continue
            }
            r = b.fieldValue(e, true);
            if (r && r.constructor == Array) {
                for (h = 0, c = r.length; h < c; h++) {
                    p.push({
                        name: f,
                        value: r[h]
                    })
                }
            } else {
                if (r !== null && typeof r != "undefined") {
                    p.push({
                        name: f,
                        value: r
                    })
                }
            }
        }
        if (!q && d.clk) {
            var l = b(d.clk),
                o = l[0];
            f = o.name;
            if (f && !o.disabled && o.type == "image") {
                p.push({
                    name: f,
                    value: l.val()
                });
                p.push({
                    name: f + ".x",
                    value: d.clk_x
                }, {
                    name: f + ".y",
                    value: d.clk_y
                })
            }
        }
        return p
    };
    b.fn.formSerialize = function (c) {
        return b.param(this.formToArray(c))
    };
    b.fn.fieldSerialize = function (d) {
        var c = [];
        this.each(function () {
            var h = this.name;
            if (!h) {
                return
            }
            var f = b.fieldValue(this, d);
            if (f && f.constructor == Array) {
                for (var g = 0, e = f.length; g < e; g++) {
                    c.push({
                        name: h,
                        value: f[g]
                    })
                }
            } else {
                if (f !== null && typeof f != "undefined") {
                    c.push({
                        name: this.name,
                        value: f
                    })
                }
            }
        });
        return b.param(c)
    };
    b.fn.fieldValue = function (h) {
        for (var g = [], e = 0, c = this.length; e < c; e++) {
            var f = this[e];
            var d = b.fieldValue(f, h);
            if (d === null || typeof d == "undefined" || (d.constructor == Array && !d.length)) {
                continue
            }
            d.constructor == Array ? b.merge(g, d) : g.push(d)
        }
        return g
    };
    b.fieldValue = function (c, j) {
        var e = c.name,
            p = c.type,
            q = c.tagName.toLowerCase();
        if (j === undefined) {
            j = true
        }
        if (j && (!e || c.disabled || p == "reset" || p == "button" || (p == "checkbox" || p == "radio") && !c.checked || (p == "submit" || p == "image") && c.form && c.form.clk != c || q == "select" && c.selectedIndex == -1)) {
            return null
        }
        if (q == "select") {
            var k = c.selectedIndex;
            if (k < 0) {
                return null
            }
            var m = [],
                d = c.options;
            var g = (p == "select-one");
            var l = (g ? k + 1 : d.length);
            for (var f = (g ? k : 0); f < l; f++) {
                var h = d[f];
                if (h.selected) {
                    var o = h.value;
                    if (!o) {
                        o = (h.attributes && h.attributes.value && !(h.attributes.value.specified)) ? h.text : h.value
                    }
                    if (g) {
                        return o
                    }
                    m.push(o)
                }
            }
            return m
        }
        return b(c).val()
    };
    b.fn.clearForm = function () {
        return this.each(function () {
            b("input,select,textarea", this).clearFields()
        })
    };
    b.fn.clearFields = b.fn.clearInputs = function () {
        return this.each(function () {
            var d = this.type,
                c = this.tagName.toLowerCase();
            if (d == "text" || d == "password" || c == "textarea") {
                this.value = ""
            } else {
                if (d == "checkbox" || d == "radio") {
                    this.checked = false
                } else {
                    if (c == "select") {
                        this.selectedIndex = -1
                    }
                }
            }
        })
    };
    b.fn.resetForm = function () {
        return this.each(function () {
            if (typeof this.reset == "function" || (typeof this.reset == "object" && !this.reset.nodeType)) {
                this.reset()
            }
        })
    };
    b.fn.enable = function (c) {
        if (c === undefined) {
            c = true
        }
        return this.each(function () {
            this.disabled = !c
        })
    };
    b.fn.selected = function (c) {
        if (c === undefined) {
            c = true
        }
        return this.each(function () {
            var d = this.type;
            if (d == "checkbox" || d == "radio") {
                this.checked = c
            } else {
                if (this.tagName.toLowerCase() == "option") {
                    var e = b(this).parent("select");
                    if (c && e[0] && e[0].type == "select-one") {
                        e.find("option").selected(false)
                    }
                    this.selected = c
                }
            }
        })
    };

    function a() {
        if (b.fn.ajaxSubmit.debug) {
            var c = "[jquery.form] " + Array.prototype.join.call(arguments, "");
            if (window.console && window.console.log) {
                window.console.log(c)
            } else {
                if (window.opera && window.opera.postError) {
                    window.opera.postError(c)
                }
            }
        }
    }
})(jQuery);
(function () {
    var _after = 1;
    var _afterThrow = 2;
    var _afterFinally = 3;
    var _before = 4;
    var _around = 5;
    var _intro = 6;
    var _regexEnabled = true;
    var _arguments = "arguments";
    var _undef = "undefined";
    var getType = (function () {
        var toString = Object.prototype.toString,
            toStrings = {}, nodeTypes = {
                1: "element",
                3: "textnode",
                9: "document",
                11: "fragment"
            }, types = "Arguments Array Boolean Date Document Element Error Fragment Function NodeList Null Number Object RegExp String TextNode Undefined Window".split(" ");
        for (var i = types.length; i--;) {
            var type = types[i],
                constructor = window[type];
            if (constructor) {
                try {
                    toStrings[toString.call(new constructor)] = type.toLowerCase()
                } catch (e) {}
            }
        }
        return function (item) {
            return item == null && (item === undefined ? _undef : "null") || item.nodeType && nodeTypes[item.nodeType] || typeof item.length == "number" && (item.callee && _arguments || item.alert && "window" || item.item && "nodelist") || toStrings[toString.call(item)]
        }
    })();
    var isFunc = function (obj) {
        return getType(obj) == "function"
    };
    var weaveOne = function (source, method, advice) {
        var old = source[method];
        if (advice.type != _intro && !isFunc(old)) {
            var oldObject = old;
            old = function () {
                var code = arguments.length > 0 ? _arguments + "[0]" : "";
                for (var i = 1; i < arguments.length; i++) {
                    code += "," + _arguments + "[" + i + "]"
                }
                return eval("oldObject(" + code + ");")
            }
        }
        var aspect;
        if (advice.type == _after || advice.type == _afterThrow || advice.type == _afterFinally) {
            aspect = function () {
                var returnValue, exceptionThrown = null;
                try {
                    returnValue = old.apply(this, arguments)
                } catch (e) {
                    exceptionThrown = e
                }
                if (advice.type == _after) {
                    if (exceptionThrown == null) {
                        returnValue = advice.value.apply(this, [returnValue, method])
                    } else {
                        throw exceptionThrown
                    }
                } else {
                    if (advice.type == _afterThrow && exceptionThrown != null) {
                        returnValue = advice.value.apply(this, [exceptionThrown, method])
                    } else {
                        if (advice.type == _afterFinally) {
                            returnValue = advice.value.apply(this, [returnValue, exceptionThrown, method])
                        }
                    }
                }
                return returnValue
            }
        } else {
            if (advice.type == _before) {
                aspect = function () {
                    advice.value.apply(this, [arguments, method]);
                    return old.apply(this, arguments)
                }
            } else {
                if (advice.type == _intro) {
                    aspect = function () {
                        return advice.value.apply(this, arguments)
                    }
                } else {
                    if (advice.type == _around) {
                        aspect = function () {
                            var invocation = {
                                object: this,
                                args: Array.prototype.slice.call(arguments)
                            };
                            return advice.value.apply(invocation.object, [{
                                arguments: invocation.args,
                                method: method,
                                proceed: function () {
                                    return old.apply(invocation.object, invocation.args)
                                }
                            }])
                        }
                    }
                }
            }
        }
        aspect.unweave = function () {
            source[method] = old;
            pointcut = source = aspect = old = null
        };
        source[method] = aspect;
        return aspect
    };
    var search = function (source, pointcut, advice) {
        var methods = [];
        for (var method in source) {
            var item = null;
            try {
                item = source[method]
            } catch (e) {}
            if (item != null && method.match(pointcut.method) && isFunc(item)) {
                methods[methods.length] = {
                    source: source,
                    method: method,
                    advice: advice
                }
            }
        }
        return methods
    };
    var weave = function (pointcut, advice) {
        var source = typeof (pointcut.target.prototype) != _undef ? pointcut.target.prototype : pointcut.target;
        var advices = [];
        if (advice.type != _intro && typeof (source[pointcut.method]) == _undef) {
            var methods = search(pointcut.target, pointcut, advice);
            if (methods.length == 0) {
                methods = search(source, pointcut, advice)
            }
            for (var i in methods) {
                advices[advices.length] = weaveOne(methods[i].source, methods[i].method, methods[i].advice)
            }
        } else {
            advices[0] = weaveOne(source, pointcut.method, advice)
        }
        return _regexEnabled ? advices : advices[0]
    };
    jQuery.aop = {
        after: function (pointcut, advice) {
            return weave(pointcut, {
                type: _after,
                value: advice
            })
        },
        afterThrow: function (pointcut, advice) {
            return weave(pointcut, {
                type: _afterThrow,
                value: advice
            })
        },
        afterFinally: function (pointcut, advice) {
            return weave(pointcut, {
                type: _afterFinally,
                value: advice
            })
        },
        before: function (pointcut, advice) {
            return weave(pointcut, {
                type: _before,
                value: advice
            })
        },
        around: function (pointcut, advice) {
            return weave(pointcut, {
                type: _around,
                value: advice
            })
        },
        introduction: function (pointcut, advice) {
            return weave(pointcut, {
                type: _intro,
                value: advice
            })
        },
        setup: function (settings) {
            _regexEnabled = settings.regexMatch
        }
    }
})();
if (window.Raphael) {
    Raphael.shadow = function (l, k, m, f, r) {
        r = r || {};
        var b = jQuery(r.target),
            o = jQuery("<div/>", {
                "class": "aui-shadow"
            }),
            a = r.shadow || r.color || "#000",
            q = r.size * 10 || 0,
            p = r.offsetSize || 3,
            n = r.zindex || 0,
            i = r.radius || 0,
            g = "0.4",
            d = r.blur || 3,
            c, j, e;
        m += q + 2 * d;
        f += q + 2 * d;
        if (Raphael.shadow.BOX_SHADOW_SUPPORT) {
            b.addClass("aui-box-shadow");
            return o.addClass("hidden")
        }
        if (l === 0 && k === 0 && b.length > 0) {
            e = b.offset();
            l = p - d + e.left;
            k = p - d + e.top
        }
        if (jQuery.browser.msie && jQuery.browser.version < "9") {
            a = "#f0f0f0";
            g = "0.2"
        }
        o.css({
            position: "absolute",
            left: l,
            top: k,
            width: m,
            height: f,
            zIndex: n
        });
        if (b.length > 0) {
            o.appendTo(document.body);
            c = Raphael(o[0], m, f, i)
        } else {
            c = Raphael(l, k, m, f, i)
        }
        c.canvas.style.position = "absolute";
        j = c.rect(d, d, m - 2 * d, f - 2 * d).attr({
            fill: a,
            stroke: a,
            blur: "" + d,
            opacity: g
        });
        return o
    };
    Raphael.shadow.BOX_SHADOW_SUPPORT = (function () {
        var c = document.documentElement.style;
        var a = ["boxShadow", "MozBoxShadow", "WebkitBoxShadow", "msBoxShadow"];
        for (var b = 0; b < a.length; b++) {
            if (a[b] in c) {
                return true
            }
        }
        return false
    })()
};
jQuery.os = {};
var jQueryOSplatform = navigator.platform.toLowerCase();
jQuery.os.windows = (jQueryOSplatform.indexOf("win") != -1);
jQuery.os.mac = (jQueryOSplatform.indexOf("mac") != -1);
jQuery.os.linux = (jQueryOSplatform.indexOf("linux") != -1);
(function (d) {
    d.hotkeys = {
        version: "0.8",
        specialKeys: {
            8: "backspace",
            9: "tab",
            13: "return",
            16: "shift",
            17: "ctrl",
            18: "alt",
            19: "pause",
            20: "capslock",
            27: "esc",
            32: "space",
            33: "pageup",
            34: "pagedown",
            35: "end",
            36: "home",
            37: "left",
            38: "up",
            39: "right",
            40: "down",
            45: "insert",
            46: "del",
            91: "meta",
            96: "0",
            97: "1",
            98: "2",
            99: "3",
            100: "4",
            101: "5",
            102: "6",
            103: "7",
            104: "8",
            105: "9",
            106: "*",
            107: "+",
            109: "-",
            110: ".",
            111: "/",
            112: "f1",
            113: "f2",
            114: "f3",
            115: "f4",
            116: "f5",
            117: "f6",
            118: "f7",
            119: "f8",
            120: "f9",
            121: "f10",
            122: "f11",
            123: "f12",
            144: "numlock",
            145: "scroll",
            188: ",",
            190: ".",
            191: "/",
            224: "meta",
            219: "[",
            221: "]"
        },
        keypressKeys: ["<", ">", "?"],
        shiftNums: {
            "`": "~",
            "1": "!",
            "2": "@",
            "3": "#",
            "4": "$",
            "5": "%",
            "6": "^",
            "7": "&",
            "8": "*",
            "9": "(",
            "0": ")",
            "-": "_",
            "=": "+",
            ";": ":",
            "'": '"',
            ",": "<",
            ".": ">",
            "/": "?",
            "\\": "|"
        }
    };
    d.each(d.hotkeys.keypressKeys, function (e, f) {
        d.hotkeys.shiftNums[f] = f
    });

    function a(e) {
        this.num = 0;
        this.timer = e > 0 ? e : false
    }
    a.prototype.val = function () {
        return this.num
    };
    a.prototype.inc = function () {
        if (this.timer) {
            clearTimeout(this.timeout);
            this.timeout = setTimeout(d.proxy(a.prototype.reset, this), this.timer)
        }
        this.num++
    };
    a.prototype.reset = function () {
        if (this.timer) {
            clearTimeout(this.timeout)
        }
        this.num = 0
    };

    function c(g) {
        if (!(d.isPlainObject(g.data) || d.isArray(g.data) || typeof g.data === "string")) {
            return
        }
        var f = g.handler,
            e = {
                timer: 700
            };
        (function (h) {
            if (typeof h === "string") {
                e.combo = [h]
            } else {
                if (d.isArray(h)) {
                    e.combo = h
                } else {
                    d.extend(e, h)
                }
            }
            e.combo = d.map(e.combo, function (i) {
                return i.toLowerCase()
            })
        })(g.data);
        g.index = new a(e.timer);
        g.handler = function (m) {
            if (this !== m.target && (/textarea|select|input/i.test(m.target.nodeName))) {
                return
            }
            var j = m.type !== "keypress" ? d.hotkeys.specialKeys[m.which] : null,
                n = String.fromCharCode(m.which).toLowerCase(),
                k, l = "",
                i = {};
            if (m.altKey && j !== "alt") {
                l += "alt+"
            }
            if (m.ctrlKey && j !== "ctrl") {
                l += "ctrl+"
            }
            if (m.metaKey && !m.ctrlKey && j !== "meta") {
                l += "meta+"
            }
            if (m.shiftKey && j !== "shift") {
                l += "shift+"
            }
            if (j) {
                i[l + j] = true
            }
            if (n) {
                i[l + n] = true
            }
            if (/shift+/.test(l)) {
                i[l.replace("shift+", "") + d.hotkeys.shiftNums[(j || n)]] = true
            }
            var h = g.index,
                o = e.combo;
            if (b(o[h.val()], i)) {
                if (h.val() === o.length - 1) {
                    h.reset();
                    return f.apply(this, arguments)
                } else {
                    h.inc()
                }
            } else {
                h.reset();
                if (b(o[0], i)) {
                    h.inc()
                }
            }
        }
    }

    function b(h, f) {
        var j = h.split(" ");
        for (var g = 0, e = j.length; g < e; g++) {
            if (f[j[g]]) {
                return true
            }
        }
        return false
    }
    d.each(["keydown", "keyup", "keypress"], function () {
        d.event.special[this] = {
            add: c
        }
    })
})(jQuery);
jQuery.fn.moveTo = function (c) {
    var g = {
        transition: false,
        scrollOffset: 35
    };
    var e = jQuery.extend(g, c),
        a = this,
        d = a.offset().top,
        b;
    if ((jQuery(window).scrollTop() + jQuery(window).height() - this.outerHeight() < d || jQuery(window).scrollTop() + e.scrollOffset > d) && jQuery(window).height() > e.scrollOffset) {
        if (jQuery(window).scrollTop() + e.scrollOffset > d) {
            b = d - (jQuery(window).height() - this.outerHeight()) + e.scrollOffset
        } else {
            b = d - e.scrollOffset
        } if (!jQuery.fn.moveTo.animating && e.transition) {
            jQuery(document).trigger("moveToStarted", this);
            jQuery.fn.moveTo.animating = true;
            jQuery("html,body").animate({
                scrollTop: b
            }, 1000, function () {
                jQuery(document).trigger("moveToFinished", a);
                delete jQuery.fn.moveTo.animating
            });
            return this
        } else {
            var f = jQuery("html, body");
            if (f.is(":animated")) {
                f.stop();
                delete jQuery.fn.moveTo.animating
            }
            jQuery(document).trigger("moveToStarted");
            jQuery(window).scrollTop(b);
            setTimeout(function () {
                jQuery(document).trigger("moveToFinished", a)
            }, 100);
            return this
        }
    }
    jQuery(document).trigger("moveToFinished", this);
    return this
};
/*
 * jQuery UI Datepicker 1.8.24
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Datepicker
 *
 * Depends:
 *	jquery.ui.core.js
 */
(function ($, undefined) {
    $.extend($.ui, {
        datepicker: {
            version: "1.8.24"
        }
    });
    var PROP_NAME = "datepicker";
    var dpuuid = new Date().getTime();
    var instActive;

    function Datepicker() {
        this.debug = false;
        this._curInst = null;
        this._keyEvent = false;
        this._disabledInputs = [];
        this._datepickerShowing = false;
        this._inDialog = false;
        this._mainDivId = "ui-datepicker-div";
        this._inlineClass = "ui-datepicker-inline";
        this._appendClass = "ui-datepicker-append";
        this._triggerClass = "ui-datepicker-trigger";
        this._dialogClass = "ui-datepicker-dialog";
        this._disableClass = "ui-datepicker-disabled";
        this._unselectableClass = "ui-datepicker-unselectable";
        this._currentClass = "ui-datepicker-current-day";
        this._dayOverClass = "ui-datepicker-days-cell-over";
        this.regional = [];
        this.regional[""] = {
            closeText: "Done",
            prevText: "Prev",
            nextText: "Next",
            currentText: "Today",
            monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            monthNamesShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
            dayNames: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
            dayNamesShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
            dayNamesMin: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
            weekHeader: "Wk",
            dateFormat: "mm/dd/yy",
            firstDay: 0,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: ""
        };
        this._defaults = {
            showOn: "focus",
            showAnim: "fadeIn",
            showOptions: {},
            defaultDate: null,
            appendText: "",
            buttonText: "...",
            buttonImage: "",
            buttonImageOnly: false,
            hideIfNoPrevNext: false,
            navigationAsDateFormat: false,
            gotoCurrent: false,
            changeMonth: false,
            changeYear: false,
            yearRange: "c-10:c+10",
            showOtherMonths: false,
            selectOtherMonths: false,
            showWeek: false,
            calculateWeek: this.iso8601Week,
            shortYearCutoff: "+10",
            minDate: null,
            maxDate: null,
            duration: "fast",
            beforeShowDay: null,
            beforeShow: null,
            onSelect: null,
            onChangeMonthYear: null,
            onClose: null,
            numberOfMonths: 1,
            showCurrentAtPos: 0,
            stepMonths: 1,
            stepBigMonths: 12,
            altField: "",
            altFormat: "",
            constrainInput: true,
            showButtonPanel: false,
            autoSize: false,
            disabled: false
        };
        $.extend(this._defaults, this.regional[""]);
        this.dpDiv = bindHover($('<div id="' + this._mainDivId + '" class="ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all"></div>'))
    }
    $.extend(Datepicker.prototype, {
        markerClassName: "hasDatepicker",
        maxRows: 4,
        log: function () {
            if (this.debug) {
                console.log.apply("", arguments)
            }
        },
        _widgetDatepicker: function () {
            return this.dpDiv
        },
        setDefaults: function (settings) {
            extendRemove(this._defaults, settings || {});
            return this
        },
        _attachDatepicker: function (target, settings) {
            var inlineSettings = null;
            for (var attrName in this._defaults) {
                var attrValue = target.getAttribute("date:" + attrName);
                if (attrValue) {
                    inlineSettings = inlineSettings || {};
                    try {
                        inlineSettings[attrName] = eval(attrValue)
                    } catch (err) {
                        inlineSettings[attrName] = attrValue
                    }
                }
            }
            var nodeName = target.nodeName.toLowerCase();
            var inline = (nodeName == "div" || nodeName == "span");
            if (!target.id) {
                this.uuid += 1;
                target.id = "dp" + this.uuid
            }
            var inst = this._newInst($(target), inline);
            inst.settings = $.extend({}, settings || {}, inlineSettings || {});
            if (nodeName == "input") {
                this._connectDatepicker(target, inst)
            } else {
                if (inline) {
                    this._inlineDatepicker(target, inst)
                }
            }
        },
        _newInst: function (target, inline) {
            var id = target[0].id.replace(/([^A-Za-z0-9_-])/g, "\\\\$1");
            return {
                id: id,
                input: target,
                selectedDay: 0,
                selectedMonth: 0,
                selectedYear: 0,
                drawMonth: 0,
                drawYear: 0,
                inline: inline,
                dpDiv: (!inline ? this.dpDiv : bindHover($('<div class="' + this._inlineClass + ' ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all"></div>')))
            }
        },
        _connectDatepicker: function (target, inst) {
            var input = $(target);
            inst.append = $([]);
            inst.trigger = $([]);
            if (input.hasClass(this.markerClassName)) {
                return
            }
            this._attachments(input, inst);
            input.addClass(this.markerClassName).keydown(this._doKeyDown).keypress(this._doKeyPress).keyup(this._doKeyUp).bind("setData.datepicker", function (event, key, value) {
                inst.settings[key] = value
            }).bind("getData.datepicker", function (event, key) {
                return this._get(inst, key)
            });
            this._autoSize(inst);
            $.data(target, PROP_NAME, inst);
            if (inst.settings.disabled) {
                this._disableDatepicker(target)
            }
        },
        _attachments: function (input, inst) {
            var appendText = this._get(inst, "appendText");
            var isRTL = this._get(inst, "isRTL");
            if (inst.append) {
                inst.append.remove()
            }
            if (appendText) {
                inst.append = $('<span class="' + this._appendClass + '">' + appendText + "</span>");
                input[isRTL ? "before" : "after"](inst.append)
            }
            input.unbind("focus", this._showDatepicker);
            if (inst.trigger) {
                inst.trigger.remove()
            }
            var showOn = this._get(inst, "showOn");
            if (showOn == "focus" || showOn == "both") {
                input.focus(this._showDatepicker)
            }
            if (showOn == "button" || showOn == "both") {
                var buttonText = this._get(inst, "buttonText");
                var buttonImage = this._get(inst, "buttonImage");
                inst.trigger = $(this._get(inst, "buttonImageOnly") ? $("<img/>").addClass(this._triggerClass).attr({
                    src: buttonImage,
                    alt: buttonText,
                    title: buttonText
                }) : $('<button type="button"></button>').addClass(this._triggerClass).html(buttonImage == "" ? buttonText : $("<img/>").attr({
                    src: buttonImage,
                    alt: buttonText,
                    title: buttonText
                })));
                input[isRTL ? "before" : "after"](inst.trigger);
                inst.trigger.click(function () {
                    if ($.datepicker._datepickerShowing && $.datepicker._lastInput == input[0]) {
                        $.datepicker._hideDatepicker()
                    } else {
                        if ($.datepicker._datepickerShowing && $.datepicker._lastInput != input[0]) {
                            $.datepicker._hideDatepicker();
                            $.datepicker._showDatepicker(input[0])
                        } else {
                            $.datepicker._showDatepicker(input[0])
                        }
                    }
                    return false
                })
            }
        },
        _autoSize: function (inst) {
            if (this._get(inst, "autoSize") && !inst.inline) {
                var date = new Date(2009, 12 - 1, 20);
                var dateFormat = this._get(inst, "dateFormat");
                if (dateFormat.match(/[DM]/)) {
                    var findMax = function (names) {
                        var max = 0;
                        var maxI = 0;
                        for (var i = 0; i < names.length; i++) {
                            if (names[i].length > max) {
                                max = names[i].length;
                                maxI = i
                            }
                        }
                        return maxI
                    };
                    date.setMonth(findMax(this._get(inst, (dateFormat.match(/MM/) ? "monthNames" : "monthNamesShort"))));
                    date.setDate(findMax(this._get(inst, (dateFormat.match(/DD/) ? "dayNames" : "dayNamesShort"))) + 20 - date.getDay())
                }
                inst.input.attr("size", this._formatDate(inst, date).length)
            }
        },
        _inlineDatepicker: function (target, inst) {
            var divSpan = $(target);
            if (divSpan.hasClass(this.markerClassName)) {
                return
            }
            divSpan.addClass(this.markerClassName).append(inst.dpDiv).bind("setData.datepicker", function (event, key, value) {
                inst.settings[key] = value
            }).bind("getData.datepicker", function (event, key) {
                return this._get(inst, key)
            });
            $.data(target, PROP_NAME, inst);
            this._setDate(inst, this._getDefaultDate(inst), true);
            this._updateDatepicker(inst);
            this._updateAlternate(inst);
            if (inst.settings.disabled) {
                this._disableDatepicker(target)
            }
            inst.dpDiv.css("display", "block")
        },
        _dialogDatepicker: function (input, date, onSelect, settings, pos) {
            var inst = this._dialogInst;
            if (!inst) {
                this.uuid += 1;
                var id = "dp" + this.uuid;
                this._dialogInput = $('<input type="text" id="' + id + '" style="position: absolute; top: -100px; width: 0px;"/>');
                this._dialogInput.keydown(this._doKeyDown);
                $("body").append(this._dialogInput);
                inst = this._dialogInst = this._newInst(this._dialogInput, false);
                inst.settings = {};
                $.data(this._dialogInput[0], PROP_NAME, inst)
            }
            extendRemove(inst.settings, settings || {});
            date = (date && date.constructor == Date ? this._formatDate(inst, date) : date);
            this._dialogInput.val(date);
            this._pos = (pos ? (pos.length ? pos : [pos.pageX, pos.pageY]) : null);
            if (!this._pos) {
                var browserWidth = document.documentElement.clientWidth;
                var browserHeight = document.documentElement.clientHeight;
                var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
                var scrollY = document.documentElement.scrollTop || document.body.scrollTop;
                this._pos = [(browserWidth / 2) - 100 + scrollX, (browserHeight / 2) - 150 + scrollY]
            }
            this._dialogInput.css("left", (this._pos[0] + 20) + "px").css("top", this._pos[1] + "px");
            inst.settings.onSelect = onSelect;
            this._inDialog = true;
            this.dpDiv.addClass(this._dialogClass);
            this._showDatepicker(this._dialogInput[0]);
            if ($.blockUI) {
                $.blockUI(this.dpDiv)
            }
            $.data(this._dialogInput[0], PROP_NAME, inst);
            return this
        },
        _destroyDatepicker: function (target) {
            var $target = $(target);
            var inst = $.data(target, PROP_NAME);
            if (!$target.hasClass(this.markerClassName)) {
                return
            }
            var nodeName = target.nodeName.toLowerCase();
            $.removeData(target, PROP_NAME);
            if (nodeName == "input") {
                inst.append.remove();
                inst.trigger.remove();
                $target.removeClass(this.markerClassName).unbind("focus", this._showDatepicker).unbind("keydown", this._doKeyDown).unbind("keypress", this._doKeyPress).unbind("keyup", this._doKeyUp)
            } else {
                if (nodeName == "div" || nodeName == "span") {
                    $target.removeClass(this.markerClassName).empty()
                }
            }
        },
        _enableDatepicker: function (target) {
            var $target = $(target);
            var inst = $.data(target, PROP_NAME);
            if (!$target.hasClass(this.markerClassName)) {
                return
            }
            var nodeName = target.nodeName.toLowerCase();
            if (nodeName == "input") {
                target.disabled = false;
                inst.trigger.filter("button").each(function () {
                    this.disabled = false
                }).end().filter("img").css({
                    opacity: "1.0",
                    cursor: ""
                })
            } else {
                if (nodeName == "div" || nodeName == "span") {
                    var inline = $target.children("." + this._inlineClass);
                    inline.children().removeClass("ui-state-disabled");
                    inline.find("select.ui-datepicker-month, select.ui-datepicker-year").removeAttr("disabled")
                }
            }
            this._disabledInputs = $.map(this._disabledInputs, function (value) {
                return (value == target ? null : value)
            })
        },
        _disableDatepicker: function (target) {
            var $target = $(target);
            var inst = $.data(target, PROP_NAME);
            if (!$target.hasClass(this.markerClassName)) {
                return
            }
            var nodeName = target.nodeName.toLowerCase();
            if (nodeName == "input") {
                target.disabled = true;
                inst.trigger.filter("button").each(function () {
                    this.disabled = true
                }).end().filter("img").css({
                    opacity: "0.5",
                    cursor: "default"
                })
            } else {
                if (nodeName == "div" || nodeName == "span") {
                    var inline = $target.children("." + this._inlineClass);
                    inline.children().addClass("ui-state-disabled");
                    inline.find("select.ui-datepicker-month, select.ui-datepicker-year").attr("disabled", "disabled")
                }
            }
            this._disabledInputs = $.map(this._disabledInputs, function (value) {
                return (value == target ? null : value)
            });
            this._disabledInputs[this._disabledInputs.length] = target
        },
        _isDisabledDatepicker: function (target) {
            if (!target) {
                return false
            }
            for (var i = 0; i < this._disabledInputs.length; i++) {
                if (this._disabledInputs[i] == target) {
                    return true
                }
            }
            return false
        },
        _getInst: function (target) {
            try {
                return $.data(target, PROP_NAME)
            } catch (err) {
                throw "Missing instance data for this datepicker"
            }
        },
        _optionDatepicker: function (target, name, value) {
            var inst = this._getInst(target);
            if (arguments.length == 2 && typeof name == "string") {
                return (name == "defaults" ? $.extend({}, $.datepicker._defaults) : (inst ? (name == "all" ? $.extend({}, inst.settings) : this._get(inst, name)) : null))
            }
            var settings = name || {};
            if (typeof name == "string") {
                settings = {};
                settings[name] = value
            }
            if (inst) {
                if (this._curInst == inst) {
                    this._hideDatepicker()
                }
                var date = this._getDateDatepicker(target, true);
                var minDate = this._getMinMaxDate(inst, "min");
                var maxDate = this._getMinMaxDate(inst, "max");
                extendRemove(inst.settings, settings);
                if (minDate !== null && settings.dateFormat !== undefined && settings.minDate === undefined) {
                    inst.settings.minDate = this._formatDate(inst, minDate)
                }
                if (maxDate !== null && settings.dateFormat !== undefined && settings.maxDate === undefined) {
                    inst.settings.maxDate = this._formatDate(inst, maxDate)
                }
                this._attachments($(target), inst);
                this._autoSize(inst);
                this._setDate(inst, date);
                this._updateAlternate(inst);
                this._updateDatepicker(inst)
            }
        },
        _changeDatepicker: function (target, name, value) {
            this._optionDatepicker(target, name, value)
        },
        _refreshDatepicker: function (target) {
            var inst = this._getInst(target);
            if (inst) {
                this._updateDatepicker(inst)
            }
        },
        _setDateDatepicker: function (target, date) {
            var inst = this._getInst(target);
            if (inst) {
                this._setDate(inst, date);
                this._updateDatepicker(inst);
                this._updateAlternate(inst)
            }
        },
        _getDateDatepicker: function (target, noDefault) {
            var inst = this._getInst(target);
            if (inst && !inst.inline) {
                this._setDateFromField(inst, noDefault)
            }
            return (inst ? this._getDate(inst) : null)
        },
        _doKeyDown: function (event) {
            var inst = $.datepicker._getInst(event.target);
            var handled = true;
            var isRTL = inst.dpDiv.is(".ui-datepicker-rtl");
            inst._keyEvent = true;
            if ($.datepicker._datepickerShowing) {
                switch (event.keyCode) {
                case 9:
                    $.datepicker._hideDatepicker();
                    handled = false;
                    break;
                case 13:
                    var sel = $("td." + $.datepicker._dayOverClass + ":not(." + $.datepicker._currentClass + ")", inst.dpDiv);
                    if (sel[0]) {
                        $.datepicker._selectDay(event.target, inst.selectedMonth, inst.selectedYear, sel[0])
                    }
                    var onSelect = $.datepicker._get(inst, "onSelect");
                    if (onSelect) {
                        var dateStr = $.datepicker._formatDate(inst);
                        onSelect.apply((inst.input ? inst.input[0] : null), [dateStr, inst])
                    } else {
                        $.datepicker._hideDatepicker()
                    }
                    return false;
                    break;
                case 27:
                    $.datepicker._hideDatepicker();
                    break;
                case 33:
                    $.datepicker._adjustDate(event.target, (event.ctrlKey ? -$.datepicker._get(inst, "stepBigMonths") : -$.datepicker._get(inst, "stepMonths")), "M");
                    break;
                case 34:
                    $.datepicker._adjustDate(event.target, (event.ctrlKey ? +$.datepicker._get(inst, "stepBigMonths") : +$.datepicker._get(inst, "stepMonths")), "M");
                    break;
                case 35:
                    if (event.ctrlKey || event.metaKey) {
                        $.datepicker._clearDate(event.target)
                    }
                    handled = event.ctrlKey || event.metaKey;
                    break;
                case 36:
                    if (event.ctrlKey || event.metaKey) {
                        $.datepicker._gotoToday(event.target)
                    }
                    handled = event.ctrlKey || event.metaKey;
                    break;
                case 37:
                    if (event.ctrlKey || event.metaKey) {
                        $.datepicker._adjustDate(event.target, (isRTL ? +1 : -1), "D")
                    }
                    handled = event.ctrlKey || event.metaKey;
                    if (event.originalEvent.altKey) {
                        $.datepicker._adjustDate(event.target, (event.ctrlKey ? -$.datepicker._get(inst, "stepBigMonths") : -$.datepicker._get(inst, "stepMonths")), "M")
                    }
                    break;
                case 38:
                    if (event.ctrlKey || event.metaKey) {
                        $.datepicker._adjustDate(event.target, -7, "D")
                    }
                    handled = event.ctrlKey || event.metaKey;
                    break;
                case 39:
                    if (event.ctrlKey || event.metaKey) {
                        $.datepicker._adjustDate(event.target, (isRTL ? -1 : +1), "D")
                    }
                    handled = event.ctrlKey || event.metaKey;
                    if (event.originalEvent.altKey) {
                        $.datepicker._adjustDate(event.target, (event.ctrlKey ? +$.datepicker._get(inst, "stepBigMonths") : +$.datepicker._get(inst, "stepMonths")), "M")
                    }
                    break;
                case 40:
                    if (event.ctrlKey || event.metaKey) {
                        $.datepicker._adjustDate(event.target, +7, "D")
                    }
                    handled = event.ctrlKey || event.metaKey;
                    break;
                default:
                    handled = false
                }
            } else {
                if (event.keyCode == 36 && event.ctrlKey) {
                    $.datepicker._showDatepicker(this)
                } else {
                    handled = false
                }
            } if (handled) {
                event.preventDefault();
                event.stopPropagation()
            }
        },
        _doKeyPress: function (event) {
            var inst = $.datepicker._getInst(event.target);
            if ($.datepicker._get(inst, "constrainInput")) {
                var chars = $.datepicker._possibleChars($.datepicker._get(inst, "dateFormat"));
                var chr = String.fromCharCode(event.charCode == undefined ? event.keyCode : event.charCode);
                return event.ctrlKey || event.metaKey || (chr < " " || !chars || chars.indexOf(chr) > -1)
            }
        },
        _doKeyUp: function (event) {
            var inst = $.datepicker._getInst(event.target);
            if (inst.input.val() != inst.lastVal) {
                try {
                    var date = $.datepicker.parseDate($.datepicker._get(inst, "dateFormat"), (inst.input ? inst.input.val() : null), $.datepicker._getFormatConfig(inst));
                    if (date) {
                        $.datepicker._setDateFromField(inst);
                        $.datepicker._updateAlternate(inst);
                        $.datepicker._updateDatepicker(inst)
                    }
                } catch (err) {
                    $.datepicker.log(err)
                }
            }
            return true
        },
        _showDatepicker: function (input) {
            input = input.target || input;
            if (input.nodeName.toLowerCase() != "input") {
                input = $("input", input.parentNode)[0]
            }
            if ($.datepicker._isDisabledDatepicker(input) || $.datepicker._lastInput == input) {
                return
            }
            var inst = $.datepicker._getInst(input);
            if ($.datepicker._curInst && $.datepicker._curInst != inst) {
                $.datepicker._curInst.dpDiv.stop(true, true);
                if (inst && $.datepicker._datepickerShowing) {
                    $.datepicker._hideDatepicker($.datepicker._curInst.input[0])
                }
            }
            var beforeShow = $.datepicker._get(inst, "beforeShow");
            var beforeShowSettings = beforeShow ? beforeShow.apply(input, [input, inst]) : {};
            if (beforeShowSettings === false) {
                return
            }
            extendRemove(inst.settings, beforeShowSettings);
            inst.lastVal = null;
            $.datepicker._lastInput = input;
            $.datepicker._setDateFromField(inst);
            if ($.datepicker._inDialog) {
                input.value = ""
            }
            if (!$.datepicker._pos) {
                $.datepicker._pos = $.datepicker._findPos(input);
                $.datepicker._pos[1] += input.offsetHeight
            }
            var isFixed = false;
            $(input).parents().each(function () {
                isFixed |= $(this).css("position") == "fixed";
                return !isFixed
            });
            if (isFixed && $.browser.opera) {
                $.datepicker._pos[0] -= document.documentElement.scrollLeft;
                $.datepicker._pos[1] -= document.documentElement.scrollTop
            }
            var offset = {
                left: $.datepicker._pos[0],
                top: $.datepicker._pos[1]
            };
            $.datepicker._pos = null;
            inst.dpDiv.empty();
            inst.dpDiv.css({
                position: "absolute",
                display: "block",
                top: "-1000px"
            });
            $.datepicker._updateDatepicker(inst);
            offset = $.datepicker._checkOffset(inst, offset, isFixed);
            inst.dpDiv.css({
                position: ($.datepicker._inDialog && $.blockUI ? "static" : (isFixed ? "fixed" : "absolute")),
                display: "none",
                left: offset.left + "px",
                top: offset.top + "px"
            });
            if (!inst.inline) {
                var showAnim = $.datepicker._get(inst, "showAnim");
                var duration = $.datepicker._get(inst, "duration");
                var postProcess = function () {
                    var cover = inst.dpDiv.find("iframe.ui-datepicker-cover");
                    if ( !! cover.length) {
                        var borders = $.datepicker._getBorders(inst.dpDiv);
                        cover.css({
                            left: -borders[0],
                            top: -borders[1],
                            width: inst.dpDiv.outerWidth(),
                            height: inst.dpDiv.outerHeight()
                        })
                    }
                };
                inst.dpDiv.zIndex($(input).zIndex() + 1);
                $.datepicker._datepickerShowing = true;
                if ($.effects && $.effects[showAnim]) {
                    inst.dpDiv.show(showAnim, $.datepicker._get(inst, "showOptions"), duration, postProcess)
                } else {
                    inst.dpDiv[showAnim || "show"]((showAnim ? duration : null), postProcess)
                } if (!showAnim || !duration) {
                    postProcess()
                }
                if (inst.input.is(":visible") && !inst.input.is(":disabled")) {
                    inst.input.focus()
                }
                $.datepicker._curInst = inst
            }
        },
        _updateDatepicker: function (inst) {
            var self = this;
            self.maxRows = 4;
            var borders = $.datepicker._getBorders(inst.dpDiv);
            instActive = inst;
            inst.dpDiv.empty().append(this._generateHTML(inst));
            this._attachHandlers(inst);
            var cover = inst.dpDiv.find("iframe.ui-datepicker-cover");
            if ( !! cover.length) {
                cover.css({
                    left: -borders[0],
                    top: -borders[1],
                    width: inst.dpDiv.outerWidth(),
                    height: inst.dpDiv.outerHeight()
                })
            }
            inst.dpDiv.find("." + this._dayOverClass + " a").mouseover();
            var numMonths = this._getNumberOfMonths(inst);
            var cols = numMonths[1];
            var width = 17;
            inst.dpDiv.removeClass("ui-datepicker-multi-2 ui-datepicker-multi-3 ui-datepicker-multi-4").width("");
            if (cols > 1) {
                inst.dpDiv.addClass("ui-datepicker-multi-" + cols).css("width", (width * cols) + "em")
            }
            inst.dpDiv[(numMonths[0] != 1 || numMonths[1] != 1 ? "add" : "remove") + "Class"]("ui-datepicker-multi");
            inst.dpDiv[(this._get(inst, "isRTL") ? "add" : "remove") + "Class"]("ui-datepicker-rtl");
            if (inst == $.datepicker._curInst && $.datepicker._datepickerShowing && inst.input && inst.input.is(":visible") && !inst.input.is(":disabled") && inst.input[0] != document.activeElement) {
                inst.input.focus()
            }
            if (inst.yearshtml) {
                var origyearshtml = inst.yearshtml;
                setTimeout(function () {
                    if (origyearshtml === inst.yearshtml && inst.yearshtml) {
                        inst.dpDiv.find("select.ui-datepicker-year:first").replaceWith(inst.yearshtml)
                    }
                    origyearshtml = inst.yearshtml = null
                }, 0)
            }
        },
        _getBorders: function (elem) {
            var convert = function (value) {
                return {
                    thin: 1,
                    medium: 2,
                    thick: 3
                }[value] || value
            };
            return [parseFloat(convert(elem.css("border-left-width"))), parseFloat(convert(elem.css("border-top-width")))]
        },
        _checkOffset: function (inst, offset, isFixed) {
            var dpWidth = inst.dpDiv.outerWidth();
            var dpHeight = inst.dpDiv.outerHeight();
            var inputWidth = inst.input ? inst.input.outerWidth() : 0;
            var inputHeight = inst.input ? inst.input.outerHeight() : 0;
            var viewWidth = document.documentElement.clientWidth + (isFixed ? 0 : $(document).scrollLeft());
            var viewHeight = document.documentElement.clientHeight + (isFixed ? 0 : $(document).scrollTop());
            offset.left -= (this._get(inst, "isRTL") ? (dpWidth - inputWidth) : 0);
            offset.left -= (isFixed && offset.left == inst.input.offset().left) ? $(document).scrollLeft() : 0;
            offset.top -= (isFixed && offset.top == (inst.input.offset().top + inputHeight)) ? $(document).scrollTop() : 0;
            offset.left -= Math.min(offset.left, (offset.left + dpWidth > viewWidth && viewWidth > dpWidth) ? Math.abs(offset.left + dpWidth - viewWidth) : 0);
            offset.top -= Math.min(offset.top, (offset.top + dpHeight > viewHeight && viewHeight > dpHeight) ? Math.abs(dpHeight + inputHeight) : 0);
            return offset
        },
        _findPos: function (obj) {
            var inst = this._getInst(obj);
            var isRTL = this._get(inst, "isRTL");
            while (obj && (obj.type == "hidden" || obj.nodeType != 1 || $.expr.filters.hidden(obj))) {
                obj = obj[isRTL ? "previousSibling" : "nextSibling"]
            }
            var position = $(obj).offset();
            return [position.left, position.top]
        },
        _hideDatepicker: function (input) {
            var inst = this._curInst;
            if (!inst || (input && inst != $.data(input, PROP_NAME))) {
                return
            }
            if (this._datepickerShowing) {
                var showAnim = this._get(inst, "showAnim");
                var duration = this._get(inst, "duration");
                var postProcess = function () {
                    $.datepicker._tidyDialog(inst)
                };
                if ($.effects && $.effects[showAnim]) {
                    inst.dpDiv.hide(showAnim, $.datepicker._get(inst, "showOptions"), duration, postProcess)
                } else {
                    inst.dpDiv[(showAnim == "slideDown" ? "slideUp" : (showAnim == "fadeIn" ? "fadeOut" : "hide"))]((showAnim ? duration : null), postProcess)
                } if (!showAnim) {
                    postProcess()
                }
                this._datepickerShowing = false;
                var onClose = this._get(inst, "onClose");
                if (onClose) {
                    onClose.apply((inst.input ? inst.input[0] : null), [(inst.input ? inst.input.val() : ""), inst])
                }
                this._lastInput = null;
                if (this._inDialog) {
                    this._dialogInput.css({
                        position: "absolute",
                        left: "0",
                        top: "-100px"
                    });
                    if ($.blockUI) {
                        $.unblockUI();
                        $("body").append(this.dpDiv)
                    }
                }
                this._inDialog = false
            }
        },
        _tidyDialog: function (inst) {
            inst.dpDiv.removeClass(this._dialogClass).unbind(".ui-datepicker-calendar")
        },
        _checkExternalClick: function (event) {
            if (!$.datepicker._curInst) {
                return
            }
            var $target = $(event.target),
                inst = $.datepicker._getInst($target[0]);
            if ((($target[0].id != $.datepicker._mainDivId && $target.parents("#" + $.datepicker._mainDivId).length == 0 && !$target.hasClass($.datepicker.markerClassName) && !$target.closest("." + $.datepicker._triggerClass).length && $.datepicker._datepickerShowing && !($.datepicker._inDialog && $.blockUI))) || ($target.hasClass($.datepicker.markerClassName) && $.datepicker._curInst != inst)) {
                $.datepicker._hideDatepicker()
            }
        },
        _adjustDate: function (id, offset, period) {
            var target = $(id);
            var inst = this._getInst(target[0]);
            if (this._isDisabledDatepicker(target[0])) {
                return
            }
            this._adjustInstDate(inst, offset + (period == "M" ? this._get(inst, "showCurrentAtPos") : 0), period);
            this._updateDatepicker(inst)
        },
        _gotoToday: function (id) {
            var target = $(id);
            var inst = this._getInst(target[0]);
            if (this._get(inst, "gotoCurrent") && inst.currentDay) {
                inst.selectedDay = inst.currentDay;
                inst.drawMonth = inst.selectedMonth = inst.currentMonth;
                inst.drawYear = inst.selectedYear = inst.currentYear
            } else {
                var date = new Date();
                inst.selectedDay = date.getDate();
                inst.drawMonth = inst.selectedMonth = date.getMonth();
                inst.drawYear = inst.selectedYear = date.getFullYear()
            }
            this._notifyChange(inst);
            this._adjustDate(target)
        },
        _selectMonthYear: function (id, select, period) {
            var target = $(id);
            var inst = this._getInst(target[0]);
            inst["selected" + (period == "M" ? "Month" : "Year")] = inst["draw" + (period == "M" ? "Month" : "Year")] = parseInt(select.options[select.selectedIndex].value, 10);
            this._notifyChange(inst);
            this._adjustDate(target)
        },
        _selectDay: function (id, month, year, td) {
            var target = $(id);
            if ($(td).hasClass(this._unselectableClass) || this._isDisabledDatepicker(target[0])) {
                return
            }
            var inst = this._getInst(target[0]);
            inst.selectedDay = inst.currentDay = $("a", td).html();
            inst.selectedMonth = inst.currentMonth = month;
            inst.selectedYear = inst.currentYear = year;
            this._selectDate(id, this._formatDate(inst, inst.currentDay, inst.currentMonth, inst.currentYear))
        },
        _clearDate: function (id) {
            var target = $(id);
            var inst = this._getInst(target[0]);
            this._selectDate(target, "")
        },
        _selectDate: function (id, dateStr) {
            var target = $(id);
            var inst = this._getInst(target[0]);
            dateStr = (dateStr != null ? dateStr : this._formatDate(inst));
            if (inst.input) {
                inst.input.val(dateStr)
            }
            this._updateAlternate(inst);
            var onSelect = this._get(inst, "onSelect");
            if (onSelect) {
                onSelect.apply((inst.input ? inst.input[0] : null), [dateStr, inst])
            } else {
                if (inst.input) {
                    inst.input.trigger("change")
                }
            } if (inst.inline) {
                this._updateDatepicker(inst)
            } else {
                this._hideDatepicker();
                this._lastInput = inst.input[0];
                if (typeof (inst.input[0]) != "object") {
                    inst.input.focus()
                }
                this._lastInput = null
            }
        },
        _updateAlternate: function (inst) {
            var altField = this._get(inst, "altField");
            if (altField) {
                var altFormat = this._get(inst, "altFormat") || this._get(inst, "dateFormat");
                var date = this._getDate(inst);
                var dateStr = this.formatDate(altFormat, date, this._getFormatConfig(inst));
                $(altField).each(function () {
                    $(this).val(dateStr)
                })
            }
        },
        noWeekends: function (date) {
            var day = date.getDay();
            return [(day > 0 && day < 6), ""]
        },
        iso8601Week: function (date) {
            var checkDate = new Date(date.getTime());
            checkDate.setDate(checkDate.getDate() + 4 - (checkDate.getDay() || 7));
            var time = checkDate.getTime();
            checkDate.setMonth(0);
            checkDate.setDate(1);
            return Math.floor(Math.round((time - checkDate) / 86400000) / 7) + 1
        },
        parseDate: function (format, value, settings) {
            if (format == null || value == null) {
                throw "Invalid arguments"
            }
            value = (typeof value == "object" ? value.toString() : value + "");
            if (value == "") {
                return null
            }
            var shortYearCutoff = (settings ? settings.shortYearCutoff : null) || this._defaults.shortYearCutoff;
            shortYearCutoff = (typeof shortYearCutoff != "string" ? shortYearCutoff : new Date().getFullYear() % 100 + parseInt(shortYearCutoff, 10));
            var dayNamesShort = (settings ? settings.dayNamesShort : null) || this._defaults.dayNamesShort;
            var dayNames = (settings ? settings.dayNames : null) || this._defaults.dayNames;
            var monthNamesShort = (settings ? settings.monthNamesShort : null) || this._defaults.monthNamesShort;
            var monthNames = (settings ? settings.monthNames : null) || this._defaults.monthNames;
            var year = -1;
            var month = -1;
            var day = -1;
            var doy = -1;
            var literal = false;
            var lookAhead = function (match) {
                var matches = (iFormat + 1 < format.length && format.charAt(iFormat + 1) == match);
                if (matches) {
                    iFormat++
                }
                return matches
            };
            var getNumber = function (match) {
                var isDoubled = lookAhead(match);
                var size = (match == "@" ? 14 : (match == "!" ? 20 : (match == "y" && isDoubled ? 4 : (match == "o" ? 3 : 2))));
                var digits = new RegExp("^\\d{1," + size + "}");
                var num = value.substring(iValue).match(digits);
                if (!num) {
                    throw "Missing number at position " + iValue
                }
                iValue += num[0].length;
                return parseInt(num[0], 10)
            };
            var getName = function (match, shortNames, longNames) {
                var names = $.map(lookAhead(match) ? longNames : shortNames, function (v, k) {
                    return [[k, v]]
                }).sort(function (a, b) {
                    return -(a[1].length - b[1].length)
                });
                var index = -1;
                $.each(names, function (i, pair) {
                    var name = pair[1];
                    if (value.substr(iValue, name.length).toLowerCase() == name.toLowerCase()) {
                        index = pair[0];
                        iValue += name.length;
                        return false
                    }
                });
                if (index != -1) {
                    return index + 1
                } else {
                    throw "Unknown name at position " + iValue
                }
            };
            var checkLiteral = function () {
                if (value.charAt(iValue) != format.charAt(iFormat)) {
                    throw "Unexpected literal at position " + iValue
                }
                iValue++
            };
            var iValue = 0;
            for (var iFormat = 0; iFormat < format.length; iFormat++) {
                if (literal) {
                    if (format.charAt(iFormat) == "'" && !lookAhead("'")) {
                        literal = false
                    } else {
                        checkLiteral()
                    }
                } else {
                    switch (format.charAt(iFormat)) {
                    case "d":
                        day = getNumber("d");
                        break;
                    case "D":
                        getName("D", dayNamesShort, dayNames);
                        break;
                    case "o":
                        doy = getNumber("o");
                        break;
                    case "m":
                        month = getNumber("m");
                        break;
                    case "M":
                        month = getName("M", monthNamesShort, monthNames);
                        break;
                    case "y":
                        year = getNumber("y");
                        break;
                    case "@":
                        var date = new Date(getNumber("@"));
                        year = date.getFullYear();
                        month = date.getMonth() + 1;
                        day = date.getDate();
                        break;
                    case "!":
                        var date = new Date((getNumber("!") - this._ticksTo1970) / 10000);
                        year = date.getFullYear();
                        month = date.getMonth() + 1;
                        day = date.getDate();
                        break;
                    case "'":
                        if (lookAhead("'")) {
                            checkLiteral()
                        } else {
                            literal = true
                        }
                        break;
                    default:
                        checkLiteral()
                    }
                }
            }
            if (iValue < value.length) {
                throw "Extra/unparsed characters found in date: " + value.substring(iValue)
            }
            if (year == -1) {
                year = new Date().getFullYear()
            } else {
                if (year < 100) {
                    year += new Date().getFullYear() - new Date().getFullYear() % 100 + (year <= shortYearCutoff ? 0 : -100)
                }
            } if (doy > -1) {
                month = 1;
                day = doy;
                do {
                    var dim = this._getDaysInMonth(year, month - 1);
                    if (day <= dim) {
                        break
                    }
                    month++;
                    day -= dim
                } while (true)
            }
            var date = this._daylightSavingAdjust(new Date(year, month - 1, day));
            if (date.getFullYear() != year || date.getMonth() + 1 != month || date.getDate() != day) {
                throw "Invalid date"
            }
            return date
        },
        ATOM: "yy-mm-dd",
        COOKIE: "D, dd M yy",
        ISO_8601: "yy-mm-dd",
        RFC_822: "D, d M y",
        RFC_850: "DD, dd-M-y",
        RFC_1036: "D, d M y",
        RFC_1123: "D, d M yy",
        RFC_2822: "D, d M yy",
        RSS: "D, d M y",
        TICKS: "!",
        TIMESTAMP: "@",
        W3C: "yy-mm-dd",
        _ticksTo1970: (((1970 - 1) * 365 + Math.floor(1970 / 4) - Math.floor(1970 / 100) + Math.floor(1970 / 400)) * 24 * 60 * 60 * 10000000),
        formatDate: function (format, date, settings) {
            if (!date) {
                return ""
            }
            var dayNamesShort = (settings ? settings.dayNamesShort : null) || this._defaults.dayNamesShort;
            var dayNames = (settings ? settings.dayNames : null) || this._defaults.dayNames;
            var monthNamesShort = (settings ? settings.monthNamesShort : null) || this._defaults.monthNamesShort;
            var monthNames = (settings ? settings.monthNames : null) || this._defaults.monthNames;
            var lookAhead = function (match) {
                var matches = (iFormat + 1 < format.length && format.charAt(iFormat + 1) == match);
                if (matches) {
                    iFormat++
                }
                return matches
            };
            var formatNumber = function (match, value, len) {
                var num = "" + value;
                if (lookAhead(match)) {
                    while (num.length < len) {
                        num = "0" + num
                    }
                }
                return num
            };
            var formatName = function (match, value, shortNames, longNames) {
                return (lookAhead(match) ? longNames[value] : shortNames[value])
            };
            var output = "";
            var literal = false;
            if (date) {
                for (var iFormat = 0; iFormat < format.length; iFormat++) {
                    if (literal) {
                        if (format.charAt(iFormat) == "'" && !lookAhead("'")) {
                            literal = false
                        } else {
                            output += format.charAt(iFormat)
                        }
                    } else {
                        switch (format.charAt(iFormat)) {
                        case "d":
                            output += formatNumber("d", date.getDate(), 2);
                            break;
                        case "D":
                            output += formatName("D", date.getDay(), dayNamesShort, dayNames);
                            break;
                        case "o":
                            output += formatNumber("o", Math.round((new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime() - new Date(date.getFullYear(), 0, 0).getTime()) / 86400000), 3);
                            break;
                        case "m":
                            output += formatNumber("m", date.getMonth() + 1, 2);
                            break;
                        case "M":
                            output += formatName("M", date.getMonth(), monthNamesShort, monthNames);
                            break;
                        case "y":
                            output += (lookAhead("y") ? date.getFullYear() : (date.getYear() % 100 < 10 ? "0" : "") + date.getYear() % 100);
                            break;
                        case "@":
                            output += date.getTime();
                            break;
                        case "!":
                            output += date.getTime() * 10000 + this._ticksTo1970;
                            break;
                        case "'":
                            if (lookAhead("'")) {
                                output += "'"
                            } else {
                                literal = true
                            }
                            break;
                        default:
                            output += format.charAt(iFormat)
                        }
                    }
                }
            }
            return output
        },
        _possibleChars: function (format) {
            var chars = "";
            var literal = false;
            var lookAhead = function (match) {
                var matches = (iFormat + 1 < format.length && format.charAt(iFormat + 1) == match);
                if (matches) {
                    iFormat++
                }
                return matches
            };
            for (var iFormat = 0; iFormat < format.length; iFormat++) {
                if (literal) {
                    if (format.charAt(iFormat) == "'" && !lookAhead("'")) {
                        literal = false
                    } else {
                        chars += format.charAt(iFormat)
                    }
                } else {
                    switch (format.charAt(iFormat)) {
                    case "d":
                    case "m":
                    case "y":
                    case "@":
                        chars += "0123456789";
                        break;
                    case "D":
                    case "M":
                        return null;
                    case "'":
                        if (lookAhead("'")) {
                            chars += "'"
                        } else {
                            literal = true
                        }
                        break;
                    default:
                        chars += format.charAt(iFormat)
                    }
                }
            }
            return chars
        },
        _get: function (inst, name) {
            return inst.settings[name] !== undefined ? inst.settings[name] : this._defaults[name]
        },
        _setDateFromField: function (inst, noDefault) {
            if (inst.input.val() == inst.lastVal) {
                return
            }
            var dateFormat = this._get(inst, "dateFormat");
            var dates = inst.lastVal = inst.input ? inst.input.val() : null;
            var date, defaultDate;
            date = defaultDate = this._getDefaultDate(inst);
            var settings = this._getFormatConfig(inst);
            try {
                date = this.parseDate(dateFormat, dates, settings) || defaultDate
            } catch (event) {
                this.log(event);
                dates = (noDefault ? "" : dates)
            }
            inst.selectedDay = date.getDate();
            inst.drawMonth = inst.selectedMonth = date.getMonth();
            inst.drawYear = inst.selectedYear = date.getFullYear();
            inst.currentDay = (dates ? date.getDate() : 0);
            inst.currentMonth = (dates ? date.getMonth() : 0);
            inst.currentYear = (dates ? date.getFullYear() : 0);
            this._adjustInstDate(inst)
        },
        _getDefaultDate: function (inst) {
            return this._restrictMinMax(inst, this._determineDate(inst, this._get(inst, "defaultDate"), new Date()))
        },
        _determineDate: function (inst, date, defaultDate) {
            var offsetNumeric = function (offset) {
                var date = new Date();
                date.setDate(date.getDate() + offset);
                return date
            };
            var offsetString = function (offset) {
                try {
                    return $.datepicker.parseDate($.datepicker._get(inst, "dateFormat"), offset, $.datepicker._getFormatConfig(inst))
                } catch (e) {}
                var date = (offset.toLowerCase().match(/^c/) ? $.datepicker._getDate(inst) : null) || new Date();
                var year = date.getFullYear();
                var month = date.getMonth();
                var day = date.getDate();
                var pattern = /([+-]?[0-9]+)\s*(d|D|w|W|m|M|y|Y)?/g;
                var matches = pattern.exec(offset);
                while (matches) {
                    switch (matches[2] || "d") {
                    case "d":
                    case "D":
                        day += parseInt(matches[1], 10);
                        break;
                    case "w":
                    case "W":
                        day += parseInt(matches[1], 10) * 7;
                        break;
                    case "m":
                    case "M":
                        month += parseInt(matches[1], 10);
                        day = Math.min(day, $.datepicker._getDaysInMonth(year, month));
                        break;
                    case "y":
                    case "Y":
                        year += parseInt(matches[1], 10);
                        day = Math.min(day, $.datepicker._getDaysInMonth(year, month));
                        break
                    }
                    matches = pattern.exec(offset)
                }
                return new Date(year, month, day)
            };
            var newDate = (date == null || date === "" ? defaultDate : (typeof date == "string" ? offsetString(date) : (typeof date == "number" ? (isNaN(date) ? defaultDate : offsetNumeric(date)) : new Date(date.getTime()))));
            newDate = (newDate && newDate.toString() == "Invalid Date" ? defaultDate : newDate);
            if (newDate) {
                newDate.setHours(0);
                newDate.setMinutes(0);
                newDate.setSeconds(0);
                newDate.setMilliseconds(0)
            }
            return this._daylightSavingAdjust(newDate)
        },
        _daylightSavingAdjust: function (date) {
            if (!date) {
                return null
            }
            date.setHours(date.getHours() > 12 ? date.getHours() + 2 : 0);
            return date
        },
        _setDate: function (inst, date, noChange) {
            var clear = !date;
            var origMonth = inst.selectedMonth;
            var origYear = inst.selectedYear;
            var newDate = this._restrictMinMax(inst, this._determineDate(inst, date, new Date()));
            inst.selectedDay = inst.currentDay = newDate.getDate();
            inst.drawMonth = inst.selectedMonth = inst.currentMonth = newDate.getMonth();
            inst.drawYear = inst.selectedYear = inst.currentYear = newDate.getFullYear();
            if ((origMonth != inst.selectedMonth || origYear != inst.selectedYear) && !noChange) {
                this._notifyChange(inst)
            }
            this._adjustInstDate(inst);
            if (inst.input) {
                inst.input.val(clear ? "" : this._formatDate(inst))
            }
        },
        _getDate: function (inst) {
            var startDate = (!inst.currentYear || (inst.input && inst.input.val() == "") ? null : this._daylightSavingAdjust(new Date(inst.currentYear, inst.currentMonth, inst.currentDay)));
            return startDate
        },
        _attachHandlers: function (inst) {
            var stepMonths = this._get(inst, "stepMonths");
            var id = "#" + inst.id.replace(/\\\\/g, "\\");
            inst.dpDiv.find("[data-handler]").map(function () {
                var handler = {
                    prev: function () {
                        window["DP_jQuery_" + dpuuid].datepicker._adjustDate(id, -stepMonths, "M")
                    },
                    next: function () {
                        window["DP_jQuery_" + dpuuid].datepicker._adjustDate(id, +stepMonths, "M")
                    },
                    hide: function () {
                        window["DP_jQuery_" + dpuuid].datepicker._hideDatepicker()
                    },
                    today: function () {
                        window["DP_jQuery_" + dpuuid].datepicker._gotoToday(id)
                    },
                    selectDay: function () {
                        window["DP_jQuery_" + dpuuid].datepicker._selectDay(id, +this.getAttribute("data-month"), +this.getAttribute("data-year"), this);
                        return false
                    },
                    selectMonth: function () {
                        window["DP_jQuery_" + dpuuid].datepicker._selectMonthYear(id, this, "M");
                        return false
                    },
                    selectYear: function () {
                        window["DP_jQuery_" + dpuuid].datepicker._selectMonthYear(id, this, "Y");
                        return false
                    }
                };
                $(this).bind(this.getAttribute("data-event"), handler[this.getAttribute("data-handler")])
            })
        },
        _generateHTML: function (inst) {
            var today = new Date();
            today = this._daylightSavingAdjust(new Date(today.getFullYear(), today.getMonth(), today.getDate()));
            var isRTL = this._get(inst, "isRTL");
            var showButtonPanel = this._get(inst, "showButtonPanel");
            var hideIfNoPrevNext = this._get(inst, "hideIfNoPrevNext");
            var navigationAsDateFormat = this._get(inst, "navigationAsDateFormat");
            var numMonths = this._getNumberOfMonths(inst);
            var showCurrentAtPos = this._get(inst, "showCurrentAtPos");
            var stepMonths = this._get(inst, "stepMonths");
            var isMultiMonth = (numMonths[0] != 1 || numMonths[1] != 1);
            var currentDate = this._daylightSavingAdjust((!inst.currentDay ? new Date(9999, 9, 9) : new Date(inst.currentYear, inst.currentMonth, inst.currentDay)));
            var minDate = this._getMinMaxDate(inst, "min");
            var maxDate = this._getMinMaxDate(inst, "max");
            var drawMonth = inst.drawMonth - showCurrentAtPos;
            var drawYear = inst.drawYear;
            if (drawMonth < 0) {
                drawMonth += 12;
                drawYear--
            }
            if (maxDate) {
                var maxDraw = this._daylightSavingAdjust(new Date(maxDate.getFullYear(), maxDate.getMonth() - (numMonths[0] * numMonths[1]) + 1, maxDate.getDate()));
                maxDraw = (minDate && maxDraw < minDate ? minDate : maxDraw);
                while (this._daylightSavingAdjust(new Date(drawYear, drawMonth, 1)) > maxDraw) {
                    drawMonth--;
                    if (drawMonth < 0) {
                        drawMonth = 11;
                        drawYear--
                    }
                }
            }
            inst.drawMonth = drawMonth;
            inst.drawYear = drawYear;
            var prevText = this._get(inst, "prevText");
            prevText = (!navigationAsDateFormat ? prevText : this.formatDate(prevText, this._daylightSavingAdjust(new Date(drawYear, drawMonth - stepMonths, 1)), this._getFormatConfig(inst)));
            var prev = (this._canAdjustMonth(inst, -1, drawYear, drawMonth) ? '<a class="ui-datepicker-prev ui-corner-all" data-handler="prev" data-event="click" title="' + prevText + '"><span class="ui-icon ui-icon-circle-triangle-' + (isRTL ? "e" : "w") + '">' + prevText + "</span></a>" : (hideIfNoPrevNext ? "" : '<a class="ui-datepicker-prev ui-corner-all ui-state-disabled" title="' + prevText + '"><span class="ui-icon ui-icon-circle-triangle-' + (isRTL ? "e" : "w") + '">' + prevText + "</span></a>"));
            var nextText = this._get(inst, "nextText");
            nextText = (!navigationAsDateFormat ? nextText : this.formatDate(nextText, this._daylightSavingAdjust(new Date(drawYear, drawMonth + stepMonths, 1)), this._getFormatConfig(inst)));
            var next = (this._canAdjustMonth(inst, +1, drawYear, drawMonth) ? '<a class="ui-datepicker-next ui-corner-all" data-handler="next" data-event="click" title="' + nextText + '"><span class="ui-icon ui-icon-circle-triangle-' + (isRTL ? "w" : "e") + '">' + nextText + "</span></a>" : (hideIfNoPrevNext ? "" : '<a class="ui-datepicker-next ui-corner-all ui-state-disabled" title="' + nextText + '"><span class="ui-icon ui-icon-circle-triangle-' + (isRTL ? "w" : "e") + '">' + nextText + "</span></a>"));
            var currentText = this._get(inst, "currentText");
            var gotoDate = (this._get(inst, "gotoCurrent") && inst.currentDay ? currentDate : today);
            currentText = (!navigationAsDateFormat ? currentText : this.formatDate(currentText, gotoDate, this._getFormatConfig(inst)));
            var controls = (!inst.inline ? '<button type="button" class="ui-datepicker-close ui-state-default ui-priority-primary ui-corner-all" data-handler="hide" data-event="click">' + this._get(inst, "closeText") + "</button>" : "");
            var buttonPanel = (showButtonPanel) ? '<div class="ui-datepicker-buttonpane ui-widget-content">' + (isRTL ? controls : "") + (this._isInRange(inst, gotoDate) ? '<button type="button" class="ui-datepicker-current ui-state-default ui-priority-secondary ui-corner-all" data-handler="today" data-event="click">' + currentText + "</button>" : "") + (isRTL ? "" : controls) + "</div>" : "";
            var firstDay = parseInt(this._get(inst, "firstDay"), 10);
            firstDay = (isNaN(firstDay) ? 0 : firstDay);
            var showWeek = this._get(inst, "showWeek");
            var dayNames = this._get(inst, "dayNames");
            var dayNamesShort = this._get(inst, "dayNamesShort");
            var dayNamesMin = this._get(inst, "dayNamesMin");
            var monthNames = this._get(inst, "monthNames");
            var monthNamesShort = this._get(inst, "monthNamesShort");
            var beforeShowDay = this._get(inst, "beforeShowDay");
            var showOtherMonths = this._get(inst, "showOtherMonths");
            var selectOtherMonths = this._get(inst, "selectOtherMonths");
            var calculateWeek = this._get(inst, "calculateWeek") || this.iso8601Week;
            var defaultDate = this._getDefaultDate(inst);
            var html = "";
            for (var row = 0; row < numMonths[0]; row++) {
                var group = "";
                this.maxRows = 4;
                for (var col = 0; col < numMonths[1]; col++) {
                    var selectedDate = this._daylightSavingAdjust(new Date(drawYear, drawMonth, inst.selectedDay));
                    var cornerClass = " ui-corner-all";
                    var calender = "";
                    if (isMultiMonth) {
                        calender += '<div class="ui-datepicker-group';
                        if (numMonths[1] > 1) {
                            switch (col) {
                            case 0:
                                calender += " ui-datepicker-group-first";
                                cornerClass = " ui-corner-" + (isRTL ? "right" : "left");
                                break;
                            case numMonths[1] - 1:
                                calender += " ui-datepicker-group-last";
                                cornerClass = " ui-corner-" + (isRTL ? "left" : "right");
                                break;
                            default:
                                calender += " ui-datepicker-group-middle";
                                cornerClass = "";
                                break
                            }
                        }
                        calender += '">'
                    }
                    calender += '<div class="ui-datepicker-header ui-widget-header ui-helper-clearfix' + cornerClass + '">' + (/all|left/.test(cornerClass) && row == 0 ? (isRTL ? next : prev) : "") + (/all|right/.test(cornerClass) && row == 0 ? (isRTL ? prev : next) : "") + this._generateMonthYearHeader(inst, drawMonth, drawYear, minDate, maxDate, row > 0 || col > 0, monthNames, monthNamesShort) + '</div><table class="ui-datepicker-calendar"><thead><tr>';
                    var thead = (showWeek ? '<th class="ui-datepicker-week-col">' + this._get(inst, "weekHeader") + "</th>" : "");
                    for (var dow = 0; dow < 7; dow++) {
                        var day = (dow + firstDay) % 7;
                        thead += "<th" + ((dow + firstDay + 6) % 7 >= 5 ? ' class="ui-datepicker-week-end"' : "") + '><span title="' + dayNames[day] + '">' + dayNamesMin[day] + "</span></th>"
                    }
                    calender += thead + "</tr></thead><tbody>";
                    var daysInMonth = this._getDaysInMonth(drawYear, drawMonth);
                    if (drawYear == inst.selectedYear && drawMonth == inst.selectedMonth) {
                        inst.selectedDay = Math.min(inst.selectedDay, daysInMonth)
                    }
                    var leadDays = (this._getFirstDayOfMonth(drawYear, drawMonth) - firstDay + 7) % 7;
                    var curRows = Math.ceil((leadDays + daysInMonth) / 7);
                    var numRows = (isMultiMonth ? this.maxRows > curRows ? this.maxRows : curRows : curRows);
                    this.maxRows = numRows;
                    var printDate = this._daylightSavingAdjust(new Date(drawYear, drawMonth, 1 - leadDays));
                    for (var dRow = 0; dRow < numRows; dRow++) {
                        calender += "<tr>";
                        var tbody = (!showWeek ? "" : '<td class="ui-datepicker-week-col">' + this._get(inst, "calculateWeek")(printDate) + "</td>");
                        for (var dow = 0; dow < 7; dow++) {
                            var daySettings = (beforeShowDay ? beforeShowDay.apply((inst.input ? inst.input[0] : null), [printDate]) : [true, ""]);
                            var otherMonth = (printDate.getMonth() != drawMonth);
                            var unselectable = (otherMonth && !selectOtherMonths) || !daySettings[0] || (minDate && printDate < minDate) || (maxDate && printDate > maxDate);
                            tbody += '<td class="' + ((dow + firstDay + 6) % 7 >= 5 ? " ui-datepicker-week-end" : "") + (otherMonth ? " ui-datepicker-other-month" : "") + ((printDate.getTime() == selectedDate.getTime() && drawMonth == inst.selectedMonth && inst._keyEvent) || (defaultDate.getTime() == printDate.getTime() && defaultDate.getTime() == selectedDate.getTime()) ? " " + this._dayOverClass : "") + (unselectable ? " " + this._unselectableClass + " ui-state-disabled" : "") + (otherMonth && !showOtherMonths ? "" : " " + daySettings[1] + (printDate.getTime() == currentDate.getTime() ? " " + this._currentClass : "") + (printDate.getTime() == today.getTime() ? " ui-datepicker-today" : "")) + '"' + ((!otherMonth || showOtherMonths) && daySettings[2] ? ' title="' + daySettings[2] + '"' : "") + (unselectable ? "" : ' data-handler="selectDay" data-event="click" data-month="' + printDate.getMonth() + '" data-year="' + printDate.getFullYear() + '"') + ">" + (otherMonth && !showOtherMonths ? "&#xa0;" : (unselectable ? '<span class="ui-state-default">' + printDate.getDate() + "</span>" : '<a class="ui-state-default' + (printDate.getTime() == today.getTime() ? " ui-state-highlight" : "") + (printDate.getTime() == currentDate.getTime() ? " ui-state-active" : "") + (otherMonth ? " ui-priority-secondary" : "") + '" href="#">' + printDate.getDate() + "</a>")) + "</td>";
                            printDate.setDate(printDate.getDate() + 1);
                            printDate = this._daylightSavingAdjust(printDate)
                        }
                        calender += tbody + "</tr>"
                    }
                    drawMonth++;
                    if (drawMonth > 11) {
                        drawMonth = 0;
                        drawYear++
                    }
                    calender += "</tbody></table>" + (isMultiMonth ? "</div>" + ((numMonths[0] > 0 && col == numMonths[1] - 1) ? '<div class="ui-datepicker-row-break"></div>' : "") : "");
                    group += calender
                }
                html += group
            }
            html += buttonPanel + ($.browser.msie && parseInt($.browser.version, 10) < 7 && !inst.inline ? '<iframe src="javascript:false;" class="ui-datepicker-cover" frameborder="0"></iframe>' : "");
            inst._keyEvent = false;
            return html
        },
        _generateMonthYearHeader: function (inst, drawMonth, drawYear, minDate, maxDate, secondary, monthNames, monthNamesShort) {
            var changeMonth = this._get(inst, "changeMonth");
            var changeYear = this._get(inst, "changeYear");
            var showMonthAfterYear = this._get(inst, "showMonthAfterYear");
            var html = '<div class="ui-datepicker-title">';
            var monthHtml = "";
            if (secondary || !changeMonth) {
                monthHtml += '<span class="ui-datepicker-month">' + monthNames[drawMonth] + "</span>"
            } else {
                var inMinYear = (minDate && minDate.getFullYear() == drawYear);
                var inMaxYear = (maxDate && maxDate.getFullYear() == drawYear);
                monthHtml += '<select class="ui-datepicker-month" data-handler="selectMonth" data-event="change">';
                for (var month = 0; month < 12; month++) {
                    if ((!inMinYear || month >= minDate.getMonth()) && (!inMaxYear || month <= maxDate.getMonth())) {
                        monthHtml += '<option value="' + month + '"' + (month == drawMonth ? ' selected="selected"' : "") + ">" + monthNamesShort[month] + "</option>"
                    }
                }
                monthHtml += "</select>"
            } if (!showMonthAfterYear) {
                html += monthHtml + (secondary || !(changeMonth && changeYear) ? "&#xa0;" : "")
            }
            if (!inst.yearshtml) {
                inst.yearshtml = "";
                if (secondary || !changeYear) {
                    html += '<span class="ui-datepicker-year">' + drawYear + "</span>"
                } else {
                    var years = this._get(inst, "yearRange").split(":");
                    var thisYear = new Date().getFullYear();
                    var determineYear = function (value) {
                        var year = (value.match(/c[+-].*/) ? drawYear + parseInt(value.substring(1), 10) : (value.match(/[+-].*/) ? thisYear + parseInt(value, 10) : parseInt(value, 10)));
                        return (isNaN(year) ? thisYear : year)
                    };
                    var year = determineYear(years[0]);
                    var endYear = Math.max(year, determineYear(years[1] || ""));
                    year = (minDate ? Math.max(year, minDate.getFullYear()) : year);
                    endYear = (maxDate ? Math.min(endYear, maxDate.getFullYear()) : endYear);
                    inst.yearshtml += '<select class="ui-datepicker-year" data-handler="selectYear" data-event="change">';
                    for (; year <= endYear; year++) {
                        inst.yearshtml += '<option value="' + year + '"' + (year == drawYear ? ' selected="selected"' : "") + ">" + year + "</option>"
                    }
                    inst.yearshtml += "</select>";
                    html += inst.yearshtml;
                    inst.yearshtml = null
                }
            }
            html += this._get(inst, "yearSuffix");
            if (showMonthAfterYear) {
                html += (secondary || !(changeMonth && changeYear) ? "&#xa0;" : "") + monthHtml
            }
            html += "</div>";
            return html
        },
        _adjustInstDate: function (inst, offset, period) {
            var year = inst.drawYear + (period == "Y" ? offset : 0);
            var month = inst.drawMonth + (period == "M" ? offset : 0);
            var day = Math.min(inst.selectedDay, this._getDaysInMonth(year, month)) + (period == "D" ? offset : 0);
            var date = this._restrictMinMax(inst, this._daylightSavingAdjust(new Date(year, month, day)));
            inst.selectedDay = date.getDate();
            inst.drawMonth = inst.selectedMonth = date.getMonth();
            inst.drawYear = inst.selectedYear = date.getFullYear();
            if (period == "M" || period == "Y") {
                this._notifyChange(inst)
            }
        },
        _restrictMinMax: function (inst, date) {
            var minDate = this._getMinMaxDate(inst, "min");
            var maxDate = this._getMinMaxDate(inst, "max");
            var newDate = (minDate && date < minDate ? minDate : date);
            newDate = (maxDate && newDate > maxDate ? maxDate : newDate);
            return newDate
        },
        _notifyChange: function (inst) {
            var onChange = this._get(inst, "onChangeMonthYear");
            if (onChange) {
                onChange.apply((inst.input ? inst.input[0] : null), [inst.selectedYear, inst.selectedMonth + 1, inst])
            }
        },
        _getNumberOfMonths: function (inst) {
            var numMonths = this._get(inst, "numberOfMonths");
            return (numMonths == null ? [1, 1] : (typeof numMonths == "number" ? [1, numMonths] : numMonths))
        },
        _getMinMaxDate: function (inst, minMax) {
            return this._determineDate(inst, this._get(inst, minMax + "Date"), null)
        },
        _getDaysInMonth: function (year, month) {
            return 32 - this._daylightSavingAdjust(new Date(year, month, 32)).getDate()
        },
        _getFirstDayOfMonth: function (year, month) {
            return new Date(year, month, 1).getDay()
        },
        _canAdjustMonth: function (inst, offset, curYear, curMonth) {
            var numMonths = this._getNumberOfMonths(inst);
            var date = this._daylightSavingAdjust(new Date(curYear, curMonth + (offset < 0 ? offset : numMonths[0] * numMonths[1]), 1));
            if (offset < 0) {
                date.setDate(this._getDaysInMonth(date.getFullYear(), date.getMonth()))
            }
            return this._isInRange(inst, date)
        },
        _isInRange: function (inst, date) {
            var minDate = this._getMinMaxDate(inst, "min");
            var maxDate = this._getMinMaxDate(inst, "max");
            return ((!minDate || date.getTime() >= minDate.getTime()) && (!maxDate || date.getTime() <= maxDate.getTime()))
        },
        _getFormatConfig: function (inst) {
            var shortYearCutoff = this._get(inst, "shortYearCutoff");
            shortYearCutoff = (typeof shortYearCutoff != "string" ? shortYearCutoff : new Date().getFullYear() % 100 + parseInt(shortYearCutoff, 10));
            return {
                shortYearCutoff: shortYearCutoff,
                dayNamesShort: this._get(inst, "dayNamesShort"),
                dayNames: this._get(inst, "dayNames"),
                monthNamesShort: this._get(inst, "monthNamesShort"),
                monthNames: this._get(inst, "monthNames")
            }
        },
        _formatDate: function (inst, day, month, year) {
            if (!day) {
                inst.currentDay = inst.selectedDay;
                inst.currentMonth = inst.selectedMonth;
                inst.currentYear = inst.selectedYear
            }
            var date = (day ? (typeof day == "object" ? day : this._daylightSavingAdjust(new Date(year, month, day))) : this._daylightSavingAdjust(new Date(inst.currentYear, inst.currentMonth, inst.currentDay)));
            return this.formatDate(this._get(inst, "dateFormat"), date, this._getFormatConfig(inst))
        }
    });

    function bindHover(dpDiv) {
        var selector = "button, .ui-datepicker-prev, .ui-datepicker-next, .ui-datepicker-calendar td a";
        return dpDiv.bind("mouseout", function (event) {
            var elem = $(event.target).closest(selector);
            if (!elem.length) {
                return
            }
            elem.removeClass("ui-state-hover ui-datepicker-prev-hover ui-datepicker-next-hover")
        }).bind("mouseover", function (event) {
            var elem = $(event.target).closest(selector);
            if ($.datepicker._isDisabledDatepicker(instActive.inline ? dpDiv.parent()[0] : instActive.input[0]) || !elem.length) {
                return
            }
            elem.parents(".ui-datepicker-calendar").find("a").removeClass("ui-state-hover");
            elem.addClass("ui-state-hover");
            if (elem.hasClass("ui-datepicker-prev")) {
                elem.addClass("ui-datepicker-prev-hover")
            }
            if (elem.hasClass("ui-datepicker-next")) {
                elem.addClass("ui-datepicker-next-hover")
            }
        })
    }

    function extendRemove(target, props) {
        $.extend(target, props);
        for (var name in props) {
            if (props[name] == null || props[name] == undefined) {
                target[name] = props[name]
            }
        }
        return target
    }

    function isArray(a) {
        return (a && (($.browser.safari && typeof a == "object" && a.length) || (a.constructor && a.constructor.toString().match(/\Array\(\)/))))
    }
    $.fn.datepicker = function (options) {
        if (!this.length) {
            return this
        }
        if (!$.datepicker.initialized) {
            $(document).mousedown($.datepicker._checkExternalClick).find("body").append($.datepicker.dpDiv);
            $.datepicker.initialized = true
        }
        var otherArgs = Array.prototype.slice.call(arguments, 1);
        if (typeof options == "string" && (options == "isDisabled" || options == "getDate" || options == "widget")) {
            return $.datepicker["_" + options + "Datepicker"].apply($.datepicker, [this[0]].concat(otherArgs))
        }
        if (options == "option" && arguments.length == 2 && typeof arguments[1] == "string") {
            return $.datepicker["_" + options + "Datepicker"].apply($.datepicker, [this[0]].concat(otherArgs))
        }
        return this.each(function () {
            typeof options == "string" ? $.datepicker["_" + options + "Datepicker"].apply($.datepicker, [this].concat(otherArgs)) : $.datepicker._attachDatepicker(this, options)
        })
    };
    $.datepicker = new Datepicker();
    $.datepicker.initialized = false;
    $.datepicker.uuid = new Date().getTime();
    $.datepicker.version = "1.8.24";
    window["DP_jQuery_" + dpuuid] = $
})(jQuery);
/* Atlassian UI and the Atlassian Design Guidelines are created by Atlassian. See https://developer.atlassian.com/display/AUI/ for API documentation and https://developer.atlassian.com/design/ for license details. */
if (typeof jQuery != "undefined") {
    var AJS = (function () {
        var f = [],
            a, d, h = 0;

        function e(i) {
            switch (i) {
            case "<":
                return "&lt;";
            case ">":
                return "&gt;";
            case "&":
                return "&amp;";
            case "'":
                return "&#39;";
            case "`":
                return "&#96;";
            default:
                return "&quot;"
            }
        }
        var k = /[&"'<>`]/g;
        var g = {
            version: "5.2",
            params: {},
            $: jQuery,
            log: function () {
                if (typeof console != "undefined" && console.log) {
                    Function.prototype.apply.apply(console.log, [console, arguments])
                }
            },
            I18n: {
                getText: function (i) {
                    return i
                }
            },
            stopEvent: function (i) {
                i.stopPropagation();
                return false
            },
            include: function (i) {
                if (!this.contains(f, i)) {
                    f.push(i);
                    var j = document.createElement("script");
                    j.src = i;
                    this.$("body").append(j)
                }
            },
            toggleClassName: function (i, j) {
                if (!(i = this.$(i))) {
                    return
                }
                i.toggleClass(j)
            },
            setVisible: function (j, i) {
                if (!(j = this.$(j))) {
                    return
                }
                var m = this.$;
                m(j).each(function () {
                    var n = m(this).hasClass("hidden");
                    if (n && i) {
                        m(this).removeClass("hidden")
                    } else {
                        if (!n && !i) {
                            m(this).addClass("hidden")
                        }
                    }
                })
            },
            setCurrent: function (i, j) {
                if (!(i = this.$(i))) {
                    return
                }
                if (j) {
                    i.addClass("current")
                } else {
                    i.removeClass("current")
                }
            },
            isVisible: function (i) {
                return !this.$(i).hasClass("hidden")
            },
            isClipped: function (i) {
                i = AJS.$(i);
                return (i.prop("scrollWidth") > i.prop("clientWidth"))
            },
            populateParameters: function () {
                var i = this;
                this.$(".parameters input").each(function () {
                    var j = this.value,
                        m = this.title || this.id;
                    if (i.$(this).hasClass("list")) {
                        if (i.params[m]) {
                            i.params[m].push(j)
                        } else {
                            i.params[m] = [j]
                        }
                    } else {
                        i.params[m] = (j.match(/^(tru|fals)e$/i) ? j.toLowerCase() == "true" : j)
                    }
                })
            },
            toInit: function (j) {
                var i = this;
                this.$(function () {
                    try {
                        j.apply(this, arguments)
                    } catch (m) {
                        i.log("Failed to run init function: " + m + "\n" + j.toString())
                    }
                });
                return this
            },
            indexOf: function (p, o, m) {
                var n = p.length;
                if (m == null) {
                    m = 0
                } else {
                    if (m < 0) {
                        m = Math.max(0, n + m)
                    }
                }
                for (var j = m; j < n; j++) {
                    if (p[j] === o) {
                        return j
                    }
                }
                return -1
            },
            contains: function (j, i) {
                return this.indexOf(j, i) > -1
            },
            format: function (m) {
                var i = /^((?:(?:[^']*'){2})*?[^']*?)\{(\d+)\}/,
                    j = /'(?!')/g;
                AJS.format = function (q) {
                    var o = arguments,
                        p = "",
                        n = q.match(i);
                    while (n) {
                        q = q.substring(n[0].length);
                        p += n[1].replace(j, "") + (o.length > ++n[2] ? o[n[2]] : "");
                        n = q.match(i)
                    }
                    return p += q.replace(j, "")
                };
                return AJS.format.apply(AJS, arguments)
            },
            firebug: function () {
                AJS.log("DEPRECATED: AJS.firebug should no longer be used.");
                var i = this.$(document.createElement("script"));
                i.attr("src", "https://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js");
                this.$("head").append(i);
                (function () {
                    if (window.firebug) {
                        firebug.init()
                    } else {
                        setTimeout(arguments.callee, 0)
                    }
                })()
            },
            clone: function (i) {
                return AJS.$(i).clone().removeAttr("id")
            },
            alphanum: function (s, r) {
                s = (s + "").toLowerCase();
                r = (r + "").toLowerCase();
                var n = /(\d+|\D+)/g,
                    o = s.match(n),
                    j = r.match(n),
                    q = Math.max(o.length, j.length);
                for (var m = 0; m < q; m++) {
                    if (m == o.length) {
                        return -1
                    }
                    if (m == j.length) {
                        return 1
                    }
                    var t = parseInt(o[m], 10),
                        p = parseInt(j[m], 10);
                    if (t == o[m] && p == j[m] && t != p) {
                        return (t - p) / Math.abs(t - p)
                    }
                    if ((t != o[m] || p != j[m]) && o[m] != j[m]) {
                        return o[m] < j[m] ? -1 : 1
                    }
                }
                return 0
            },
            dim: function (m) {
                if (!AJS.dim.$dim) {
                    AJS.dim.$dim = AJS("div").addClass("aui-blanket");
                    if (AJS.$.browser.msie) {
                        AJS.dim.$dim.css({
                            width: "200%",
                            height: Math.max(AJS.$(document).height(), AJS.$(window).height()) + "px"
                        })
                    }
                    AJS.$("body").append(AJS.dim.$dim);
                    AJS.hasFlash = false;
                    var j = /^[^:]*:\/*[^/]*|/;
                    var i = j.exec(location.href)[0];
                    if (AJS.$.browser.msie && typeof AJS.hasFlash === "undefined" && m === false) {
                        AJS.$("object, embed, iframe").each(function () {
                            if (this.nodeName.toLowerCase() === "iframe") {
                                if (j.exec(this.src)[0] === i && AJS.$(this).contents().find("object, embed").length === 0) {
                                    return true
                                }
                            }
                            AJS.hasFlash = true;
                            return false
                        })
                    }
                    if (AJS.$.browser.msie && (m !== false || AJS.hasFlash)) {
                        AJS.dim.shim = AJS.$('<iframe frameBorder="0" class="aui-blanket-shim" src="javascript:false;"/>');
                        AJS.dim.shim.css({
                            height: Math.max(AJS.$(document).height(), AJS.$(window).height()) + "px"
                        });
                        AJS.$("body").append(AJS.dim.shim)
                    }
                    if (AJS.$.browser.msie && parseInt(AJS.$.browser.version, 10) < 8) {
                        AJS.dim.cachedOverflow = AJS.$("html").css("overflow");
                        AJS.$("html").css("overflow", "hidden")
                    } else {
                        AJS.dim.cachedOverflow = AJS.$("body").css("overflow");
                        AJS.$("body").css("overflow", "hidden")
                    }
                }
            },
            undim: function () {
                if (AJS.dim.$dim) {
                    AJS.dim.$dim.remove();
                    AJS.dim.$dim = null;
                    if (AJS.dim.shim) {
                        AJS.dim.shim.remove()
                    }
                    if (AJS.$.browser.msie && parseInt(AJS.$.browser.version, 10) < 8) {
                        AJS.$("html").css("overflow", AJS.dim.cachedOverflow)
                    } else {
                        AJS.$("body").css("overflow", AJS.dim.cachedOverflow)
                    } if (AJS.$.browser.safari) {
                        var i = AJS.$(window).scrollTop();
                        AJS.$(window).scrollTop(10 + 5 * (i == 10)).scrollTop(i)
                    }
                }
            },
            onTextResize: function (j) {
                if (typeof j == "function") {
                    if (AJS.onTextResize["on-text-resize"]) {
                        AJS.onTextResize["on-text-resize"].push(function (m) {
                            j(m)
                        })
                    } else {
                        var i = AJS("div");
                        i.css({
                            width: "1em",
                            height: "1em",
                            position: "absolute",
                            top: "-9999em",
                            left: "-9999em"
                        });
                        this.$("body").append(i);
                        i.size = i.width();
                        setInterval(function () {
                            if (i.size != i.width()) {
                                i.size = i.width();
                                for (var m = 0, n = AJS.onTextResize["on-text-resize"].length; m < n; m++) {
                                    AJS.onTextResize["on-text-resize"][m](i.size)
                                }
                            }
                        }, 0);
                        AJS.onTextResize.em = i;
                        AJS.onTextResize["on-text-resize"] = [
                            function (m) {
                                j(m)
                            }
                        ]
                    }
                }
            },
            unbindTextResize: function (n) {
                for (var j = 0, m = AJS.onTextResize["on-text-resize"].length; j < m; j++) {
                    if (AJS.onTextResize["on-text-resize"][j] == n) {
                        return AJS.onTextResize["on-text-resize"].splice(j, 1)
                    }
                }
            },
            escape: function (i) {
                return escape(i).replace(/%u\w{4}/gi, function (j) {
                    return unescape(j)
                })
            },
            escapeHtml: function (i) {
                return i.replace(k, e)
            },
            filterBySearch: function (o, t, u) {
                if (t == "") {
                    return []
                }
                var m = this.$;
                var r = (u && u.keywordsField) || "keywords";
                var q = (u && u.ignoreForCamelCase) ? "i" : "";
                var n = (u && u.matchBoundary) ? "\\b" : "";
                var j = (u && u.splitRegex) || (/\s+/);
                var p = t.split(j);
                var i = [];
                m.each(p, function () {
                    var w = [new RegExp(n + this, "i")];
                    if (/^([A-Z][a-z]*){2,}$/.test(this)) {
                        var v = this.replace(/([A-Z][a-z]*)/g, "\\b$1[^,]*");
                        w.push(new RegExp(v, q))
                    }
                    i.push(w)
                });
                var s = [];
                m.each(o, function () {
                    for (var x = 0; x < i.length; x++) {
                        var v = false;
                        for (var w = 0; w < i[x].length; w++) {
                            if (i[x][w].test(this[r])) {
                                v = true;
                                break
                            }
                        }
                        if (!v) {
                            return
                        }
                    }
                    s.push(this)
                });
                return s
            },
            drawLogo: function (t) {
                AJS.log("DEPRECATED: AJS.drawLogo should no longer be used.");
                var m = t.scaleFactor || 1,
                    r = t.fill || "#fff",
                    s = t.stroke || "#000",
                    i = 400 * m,
                    p = 40 * m,
                    o = t.strokeWidth || 1,
                    q = t.containerID || ".aui-logo";
                if (AJS.$(".aui-logo").size() == 0) {
                    AJS.$("body").append("<div id='aui-logo' class='aui-logo'><div>")
                }
                var n = Raphael(q, i + 50 * m, p + 100 * m);
                var j = n.path("M 0,0 c 3.5433333,-4.7243333 7.0866667,-9.4486667 10.63,-14.173 -14.173,0 -28.346,0 -42.519,0 C -35.432667,-9.4486667 -38.976333,-4.7243333 -42.52,0 -28.346667,0 -14.173333,0 0,0 z m 277.031,28.346 c -14.17367,0 -28.34733,0 -42.521,0 C 245.14,14.173 255.77,0 266.4,-14.173 c -14.17267,0 -28.34533,0 -42.518,0 C 213.25167,0 202.62133,14.173 191.991,28.346 c -14.17333,0 -28.34667,0 -42.52,0 14.17333,-18.8976667 28.34667,-37.7953333 42.52,-56.693 -7.08667,-9.448667 -14.17333,-18.897333 -21.26,-28.346 -14.173,0 -28.346,0 -42.519,0 7.08667,9.448667 14.17333,18.897333 21.26,28.346 -14.17333,18.8976667 -28.34667,37.7953333 -42.52,56.693 -14.173333,0 -28.346667,0 -42.52,0 10.63,-14.173 21.26,-28.346 31.89,-42.519 -14.390333,0 -28.780667,0 -43.171,0 C 42.520733,1.330715e-4 31.889933,14.174867 21.26,28.347 c -42.520624,6.24e-4 -85.039187,-8.13e-4 -127.559,-0.001 11.220667,-14.961 22.441333,-29.922 33.662,-44.883 -6.496,-8.661 -12.992,-17.322 -19.488,-25.983 5.905333,0 11.810667,0 17.716,0 -10.63,-14.173333 -21.26,-28.346667 -31.89,-42.52 14.173333,0 28.346667,0 42.52,0 10.63,14.173333 21.26,28.346667 31.89,42.52 14.173333,0 28.3466667,0 42.52,0 -10.63,-14.173333 -21.26,-28.346667 -31.89,-42.52 14.1733333,0 28.3466667,0 42.52,0 10.63,14.173333 21.26,28.346667 31.89,42.52 14.390333,0 28.780667,0 43.171,0 -10.63,-14.173333 -21.26,-28.346667 -31.89,-42.52 42.51967,0 85.03933,0 127.559,0 10.63033,14.173333 21.26067,28.346667 31.891,42.52 14.17267,0 28.34533,0 42.518,0 -10.63,-14.173333 -21.26,-28.346667 -31.89,-42.52 14.17367,0 28.34733,0 42.521,0 14.17333,18.897667 28.34667,37.795333 42.52,56.693 -14.17333,18.8976667 -28.34667,37.7953333 -42.52,56.693 z");
                j.scale(m, -m, 0, 0);
                j.translate(120 * m, p);
                j.attr("fill", r);
                j.attr("stroke", s);
                j.attr("stroke-width", o)
            },
            debounce: function (j, n) {
                var m;
                var i;
                return function () {
                    var o = arguments;
                    var p = this;
                    var q = function () {
                        i = j.apply(p, o)
                    };
                    clearTimeout(m);
                    m = setTimeout(q, n);
                    return i
                }
            },
            id: function (i) {
                a = h+++"";
                d = i ? i + a : "aui-uid-" + a;
                if (document.getElementById(d) === null) {
                    return d
                } else {
                    d = d + "-" + new Date().getTime();
                    if (document.getElementById(d) === null) {
                        return d
                    } else {
                        throw new Error("ERROR: timestamped fallback ID " + d + " exists. AJS.id stopped.")
                    }
                }
            },
            _addID: function (j, m) {
                var i = AJS.$(j),
                    n = m || false;
                i.each(function (p) {
                    var o = AJS.$(this);
                    if (!o.attr("id")) {
                        o.attr("id", AJS.id(n))
                    }
                })
            },
            enable: function (m, i) {
                var j = AJS.$(m);
                if (i === undefined) {
                    i = true
                }
                return j.each(function () {
                    this.disabled = !i
                })
            }
        };
        if (typeof AJS != "undefined") {
            for (var c in AJS) {
                g[c] = AJS[c]
            }
        }
        var l = function () {
            var i = null;
            if (arguments.length && typeof arguments[0] == "string") {
                i = arguments.callee.$(document.createElement(arguments[0]));
                if (arguments.length == 2) {
                    i.html(arguments[1])
                }
            }
            return i
        };
        for (var b in g) {
            l[b] = g[b]
        }
        return l
    })();
    AJS.$(function () {
        var a = AJS.$("body");
        if (!a.data("auiVersion")) {
            a.attr("data-aui-version", AJS.version)
        }
        AJS.populateParameters()
    })
}
if (typeof console == "undefined") {
    console = {
        messages: [],
        log: function (a) {
            this.messages.push(a)
        },
        show: function () {
            alert(this.messages.join("\n"));
            this.messages = []
        }
    }
} else {
    console.show = function () {}
}
AJS.$.ajaxSettings.traditional = true;
AJS = AJS || {};
(function () {
    var a = "%CONTEXT_PATH%";
    a = (a.indexOf("%_CONTEXT_PATH") == 0 ? false : a);
    AJS.contextPath = function () {
        var b = null;
        var d = [a, window.contextPath, window.Confluence && Confluence.getContextPath(), window.BAMBOO && BAMBOO.contextPath, window.FECRU && FECRU.pageContext];
        for (var c = 0; c < d.length; c++) {
            if (typeof d[c] === "string") {
                b = d[c];
                break
            }
        }
        return b
    }
})();
(function () {
    var b = "AJS.conglomerate.cookie",
        f = /(\\|^"|"$)/g,
        g = /\|\|+/g,
        i = /"/g,
        c = /[.*+?|^$()[\]{\\]/g;

    function e(l, o) {
        o = o || "";
        var n = new RegExp(d(l) + "=([^|]+)"),
            m = o.match(n);
        return m && m[1]
    }

    function j(l, n, p) {
        var m = new RegExp("(\\s|\\|)*\\b" + d(l) + "=[^|]*[|]*");
        p = p || "";
        p = p.replace(m, "|");
        if (n !== "") {
            var o = l + "=" + n;
            if (p.length + o.length < 4020) {
                p += "|" + o
            }
        }
        return p.replace(g, "|")
    }

    function h(l) {
        return l.replace(f, "")
    }

    function k(l) {
        var n = new RegExp("\\b" + d(l) + "=((?:[^\\\\;]+|\\\\.)*)(?:;|$)"),
            m = document.cookie.match(n);
        return m && h(m[1])
    }

    function a(m, o, q) {
        var n = "",
            p, l = '"' + o.replace(i, '\\"') + '"';
        if (q) {
            p = new Date();
            p.setTime(+p + q * 24 * 60 * 60 * 1000);
            n = "; expires=" + p.toGMTString()
        }
        document.cookie = m + "=" + l + n + ";path=/"
    }

    function d(l) {
        return l.replace(c, "\\$&")
    }
    AJS.Cookie = {
        save: function (m, n, l) {
            var o = k(b);
            o = j(m, n, o);
            a(b, o, l || 365)
        },
        read: function (m, l) {
            var o = k(b);
            var n = e(m, o);
            if (n != null) {
                return n
            }
            return l
        },
        erase: function (l) {
            this.save(l, "")
        }
    }
})();
AJS.popup = function (i) {
    var d = {
        width: 800,
        height: 600,
        closeOnOutsideClick: false,
        keypressListener: function (j) {
            if (j.keyCode === 27 && b.is(":visible")) {
                f.hide()
            }
        }
    };
    if (typeof i != "object") {
        i = {
            width: arguments[0],
            height: arguments[1],
            id: arguments[2]
        };
        i = AJS.$.extend({}, i, arguments[3])
    }
    i = AJS.$.extend({}, d, i);
    var b = AJS("div").addClass("aui-popup");
    if (i.id) {
        b.attr("id", i.id)
    }
    var e = 3000;
    AJS.$(".aui-dialog").each(function () {
        var j = AJS.$(this);
        e = (j.css("z-index") > e) ? j.css("z-index") : e
    });
    var g = (function (k, j) {
        i.width = (k = (k || i.width));
        i.height = (j = (j || i.height));
        b.css({
            marginTop: -Math.round(j / 2) + "px",
            marginLeft: -Math.round(k / 2) + "px",
            width: k,
            height: j,
            "z-index": parseInt(e, 10) + 2
        });
        return arguments.callee
    })(i.width, i.height);
    AJS.$("body").append(b);
    b.hide();
    AJS.enable(b);
    var c = AJS.$(".aui-blanket"),
        a = function (j, k) {
            var l = AJS.$(j, k);
            if (l.length) {
                l.focus();
                return true
            }
            return false
        }, h = function (j) {
            if (AJS.$(".dialog-page-body", j).find(":focus").length !== 0) {
                return
            }
            if (i.focusSelector) {
                return a(i.focusSelector, j)
            }
            var k = ":input:visible:enabled:first";
            if (a(k, AJS.$(".dialog-page-body", j))) {
                return
            }
            if (a(k, AJS.$(".dialog-button-panel", j))) {
                return
            }
            a(k, AJS.$(".dialog-page-menu", j))
        };
    var f = {
        changeSize: function (j, k) {
            if ((j && j != i.width) || (k && k != i.height)) {
                g(j, k)
            }
            this.show()
        },
        show: function () {
            var j = function () {
                AJS.$(document).off("keydown", i.keypressListener).on("keydown", i.keypressListener);
                AJS.dim();
                c = AJS.$(".aui-blanket");
                if (c.size() != 0 && i.closeOnOutsideClick) {
                    c.click(function () {
                        if (b.is(":visible")) {
                            f.hide()
                        }
                    })
                }
                b.show();
                AJS.popup.current = this;
                h(b);
                AJS.$(document).trigger("showLayer", ["popup", this])
            };
            j.call(this);
            this.show = j
        },
        hide: function () {
            AJS.$(document).unbind("keydown", i.keypressListener);
            c.unbind();
            this.element.hide();
            if (AJS.$(".aui-dialog:visible").size() == 0) {
                AJS.undim()
            }
            var j = document.activeElement;
            if (this.element.has(j).length) {
                j.blur()
            }
            AJS.$(document).trigger("hideLayer", ["popup", this]);
            AJS.popup.current = null;
            this.enable()
        },
        element: b,
        remove: function () {
            b.remove();
            this.element = null
        },
        disable: function () {
            if (!this.disabled) {
                this.popupBlanket = AJS.$("<div class='dialog-blanket'> </div>").css({
                    height: b.height(),
                    width: b.width()
                });
                b.append(this.popupBlanket);
                this.disabled = true
            }
        },
        enable: function () {
            if (this.disabled) {
                this.disabled = false;
                this.popupBlanket.remove();
                this.popupBlanket = null
            }
        }
    };
    return f
};
(function () {
    function f(n, l, k, m) {
        if (!n.buttonpanel) {
            n.addButtonPanel()
        }
        this.page = n;
        this.onclick = k;
        this._onclick = function () {
            return k.call(this, n.dialog, n) === true
        };
        this.item = AJS("button", l).addClass("button-panel-button");
        if (m) {
            this.item.addClass(m)
        }
        if (typeof k == "function") {
            this.item.click(this._onclick)
        }
        n.buttonpanel.append(this.item);
        this.id = n.button.length;
        n.button[this.id] = this
    }

    function a(o, m, l, n, k) {
        if (!o.buttonpanel) {
            o.addButtonPanel()
        }
        if (!k) {
            k = "#"
        }
        this.page = o;
        this.onclick = l;
        this._onclick = function () {
            return l.call(this, o.dialog, o) === true
        };
        this.item = AJS("a", m).attr("href", k).addClass("button-panel-link");
        if (n) {
            this.item.addClass(n)
        }
        if (typeof l == "function") {
            this.item.click(this._onclick)
        }
        o.buttonpanel.append(this.item);
        this.id = o.button.length;
        o.button[this.id] = this
    }

    function b(m, l) {
        var k = m == "left" ? -1 : 1;
        return function (q) {
            var o = this.page[l];
            if (this.id != ((k == 1) ? o.length - 1 : 0)) {
                k *= (q || 1);
                o[this.id + k].item[(k < 0 ? "before" : "after")](this.item);
                o.splice(this.id, 1);
                o.splice(this.id + k, 0, this);
                for (var n = 0, p = o.length; n < p; n++) {
                    if (l == "panel" && this.page.curtab == o[n].id) {
                        this.page.curtab = n
                    }
                    o[n].id = n
                }
            }
            return this
        }
    }

    function g(k) {
        return function () {
            this.page[k].splice(this.id, 1);
            for (var l = 0, m = this.page[k].length; l < m; l++) {
                this.page[k][l].id = l
            }
            this.item.remove()
        }
    }
    f.prototype.moveUp = f.prototype.moveLeft = b("left", "button");
    f.prototype.moveDown = f.prototype.moveRight = b("right", "button");
    f.prototype.remove = g("button");
    f.prototype.html = function (k) {
        return this.item.html(k)
    };
    f.prototype.onclick = function (k) {
        if (typeof k == "undefined") {
            return this.onclick
        } else {
            this.item.unbind("click", this._onclick);
            this._onclick = function () {
                return k.call(this, page.dialog, page) === true
            };
            if (typeof k == "function") {
                this.item.click(this._onclick)
            }
        }
    };
    var d = 20;
    var h = function (q, r, k, p, n) {
        if (!(k instanceof AJS.$)) {
            k = AJS.$(k)
        }
        this.dialog = q.dialog;
        this.page = q;
        this.id = q.panel.length;
        this.button = AJS("button").html(r).addClass("item-button");
        if (n) {
            this.button[0].id = n
        }
        this.item = AJS("li").append(this.button).addClass("page-menu-item");
        this.body = AJS("div").append(k).addClass("dialog-panel-body").css("height", q.dialog.height + "px");
        this.padding = d;
        if (p) {
            this.body.addClass(p)
        }
        var m = q.panel.length,
            o = this;
        q.menu.append(this.item);
        q.body.append(this.body);
        q.panel[m] = this;
        var l = function () {
            var s;
            if (q.curtab + 1) {
                s = q.panel[q.curtab];
                s.body.hide();
                s.item.removeClass("selected");
                (typeof s.onblur == "function") && s.onblur()
            }
            q.curtab = o.id;
            o.body.show();
            o.item.addClass("selected");
            (typeof o.onselect == "function") && o.onselect();
            (typeof q.ontabchange == "function") && q.ontabchange(o, s)
        };
        if (!this.button.click) {
            AJS.log("atlassian-dialog:Panel:constructor - this.button.click false");
            this.button.onclick = l
        } else {
            this.button.click(l)
        }
        l();
        if (m == 0) {
            q.menu.css("display", "none")
        } else {
            q.menu.show()
        }
    };
    h.prototype.select = function () {
        this.button.click()
    };
    h.prototype.moveUp = h.prototype.moveLeft = b("left", "panel");
    h.prototype.moveDown = h.prototype.moveRight = b("right", "panel");
    h.prototype.remove = g("panel");
    h.prototype.html = function (k) {
        if (k) {
            this.body.html(k);
            return this
        } else {
            return this.body.html()
        }
    };
    h.prototype.setPadding = function (k) {
        if (!isNaN(+k)) {
            this.body.css("padding", +k);
            this.padding = +k;
            this.page.recalcSize()
        }
        return this
    };
    var e = 56;
    var c = 51;
    var i = 50;
    var j = function (k, l) {
        this.dialog = k;
        this.id = k.page.length;
        this.element = AJS("div").addClass("dialog-components");
        this.body = AJS("div").addClass("dialog-page-body");
        this.menu = AJS("ul").addClass("dialog-page-menu").css("height", k.height + "px");
        this.body.append(this.menu);
        this.curtab;
        this.panel = [];
        this.button = [];
        if (l) {
            this.body.addClass(l)
        }
        k.popup.element.append(this.element.append(this.menu).append(this.body));
        k.page[k.page.length] = this
    };
    j.prototype.recalcSize = function () {
        var k = this.header ? e : 0;
        var n = this.buttonpanel ? c : 0;
        for (var m = this.panel.length; m--;) {
            var l = this.dialog.height - k - n;
            this.panel[m].body.css("height", l);
            this.menu.css("height", l)
        }
    };
    j.prototype.addButtonPanel = function () {
        this.buttonpanel = AJS("div").addClass("dialog-button-panel");
        this.element.append(this.buttonpanel)
    };
    j.prototype.addPanel = function (n, k, m, l) {
        new h(this, n, k, m, l);
        this.recalcSize();
        return this
    };
    j.prototype.addHeader = function (l, k) {
        if (this.header) {
            this.header.remove()
        }
        this.header = AJS("h2").text(l || "").addClass("dialog-title");
        k && this.header.addClass(k);
        this.element.prepend(this.header);
        this.recalcSize();
        return this
    };
    j.prototype.addButton = function (l, k, m) {
        new f(this, l, k, m);
        this.recalcSize();
        return this
    };
    j.prototype.addLink = function (m, l, n, k) {
        new a(this, m, l, n, k);
        this.recalcSize();
        return this
    };
    j.prototype.gotoPanel = function (k) {
        this.panel[k.id || k].select()
    };
    j.prototype.getCurrentPanel = function () {
        return this.panel[this.curtab]
    };
    j.prototype.hide = function () {
        this.element.hide()
    };
    j.prototype.show = function () {
        this.element.show()
    };
    j.prototype.remove = function () {
        this.element.remove()
    };
    AJS.Dialog = function (m, k, n) {
        var l = {};
        if (!+m) {
            l = Object(m);
            m = l.width;
            k = l.height;
            n = l.id
        }
        this.height = k || 480;
        this.width = m || 640;
        this.id = n;
        l = AJS.$.extend({}, l, {
            width: this.width,
            height: this.height,
            id: this.id
        });
        this.popup = AJS.popup(l);
        this.popup.element.addClass("aui-dialog");
        this.page = [];
        this.curpage = 0;
        new j(this)
    };
    AJS.Dialog.prototype.addHeader = function (l, k) {
        this.page[this.curpage].addHeader(l, k);
        return this
    };
    AJS.Dialog.prototype.addButton = function (l, k, m) {
        this.page[this.curpage].addButton(l, k, m);
        return this
    };
    AJS.Dialog.prototype.addLink = function (m, l, n, k) {
        this.page[this.curpage].addLink(m, l, n, k);
        return this
    };
    AJS.Dialog.prototype.addSubmit = function (l, k) {
        this.page[this.curpage].addButton(l, k, "button-panel-submit-button");
        return this
    };
    AJS.Dialog.prototype.addCancel = function (l, k) {
        this.page[this.curpage].addLink(l, k, "button-panel-cancel-link");
        return this
    };
    AJS.Dialog.prototype.addButtonPanel = function () {
        this.page[this.curpage].addButtonPanel();
        return this
    };
    AJS.Dialog.prototype.addPanel = function (n, k, m, l) {
        this.page[this.curpage].addPanel(n, k, m, l);
        return this
    };
    AJS.Dialog.prototype.addPage = function (k) {
        new j(this, k);
        this.page[this.curpage].hide();
        this.curpage = this.page.length - 1;
        return this
    };
    AJS.Dialog.prototype.nextPage = function () {
        this.page[this.curpage++].hide();
        if (this.curpage >= this.page.length) {
            this.curpage = 0
        }
        this.page[this.curpage].show();
        return this
    };
    AJS.Dialog.prototype.prevPage = function () {
        this.page[this.curpage--].hide();
        if (this.curpage < 0) {
            this.curpage = this.page.length - 1
        }
        this.page[this.curpage].show();
        return this
    };
    AJS.Dialog.prototype.gotoPage = function (k) {
        this.page[this.curpage].hide();
        this.curpage = k;
        if (this.curpage < 0) {
            this.curpage = this.page.length - 1
        } else {
            if (this.curpage >= this.page.length) {
                this.curpage = 0
            }
        }
        this.page[this.curpage].show();
        return this
    };
    AJS.Dialog.prototype.getPanel = function (l, m) {
        var k = (m == null) ? this.curpage : l;
        if (m == null) {
            m = l
        }
        return this.page[k].panel[m]
    };
    AJS.Dialog.prototype.getPage = function (k) {
        return this.page[k]
    };
    AJS.Dialog.prototype.getCurrentPanel = function () {
        return this.page[this.curpage].getCurrentPanel()
    };
    AJS.Dialog.prototype.gotoPanel = function (m, l) {
        if (l != null) {
            var k = m.id || m;
            this.gotoPage(k)
        }
        this.page[this.curpage].gotoPanel(typeof l == "undefined" ? m : l)
    };
    AJS.Dialog.prototype.show = function () {
        this.popup.show();
        AJS.trigger("show.dialog", {
            dialog: this
        });
        return this
    };
    AJS.Dialog.prototype.hide = function () {
        this.popup.hide();
        AJS.trigger("hide.dialog", {
            dialog: this
        });
        return this
    };
    AJS.Dialog.prototype.remove = function () {
        this.popup.hide();
        this.popup.remove();
        AJS.trigger("remove.dialog", {
            dialog: this
        })
    };
    AJS.Dialog.prototype.disable = function () {
        this.popup.disable();
        return this
    };
    AJS.Dialog.prototype.enable = function () {
        this.popup.enable();
        return this
    };
    AJS.Dialog.prototype.get = function (s) {
        var l = [],
            r = this;
        var t = '#([^"][^ ]*|"[^"]*")';
        var u = ":(\\d+)";
        var m = "page|panel|button|header";
        var n = "(?:(" + m + ")(?:" + t + "|" + u + ")?|" + t + ")";
        var p = new RegExp("(?:^|,)\\s*" + n + "(?:\\s+" + n + ")?\\s*(?=,|$)", "ig");
        (s + "").replace(p, function (F, v, E, w, C, B, y, G, D) {
            v = v && v.toLowerCase();
            var x = [];
            if (v == "page" && r.page[w]) {
                x.push(r.page[w]);
                v = B;
                v = v && v.toLowerCase();
                E = y;
                w = G;
                C = D
            } else {
                x = r.page
            }
            E = E && (E + "").replace(/"/g, "");
            y = y && (y + "").replace(/"/g, "");
            C = C && (C + "").replace(/"/g, "");
            D = D && (D + "").replace(/"/g, "");
            if (v || C) {
                for (var A = x.length; A--;) {
                    if (C || (v == "panel" && (E || (!E && w == null)))) {
                        for (var z = x[A].panel.length; z--;) {
                            if (x[A].panel[z].button.html() == C || x[A].panel[z].button.html() == E || (v == "panel" && !E && w == null)) {
                                l.push(x[A].panel[z])
                            }
                        }
                    }
                    if (C || (v == "button" && (E || (!E && w == null)))) {
                        for (var z = x[A].button.length; z--;) {
                            if (x[A].button[z].item.html() == C || x[A].button[z].item.html() == E || (v == "button" && !E && w == null)) {
                                l.push(x[A].button[z])
                            }
                        }
                    }
                    if (x[A][v] && x[A][v][w]) {
                        l.push(x[A][v][w])
                    }
                    if (v == "header" && x[A].header) {
                        l.push(x[A].header)
                    }
                }
            } else {
                l = l.concat(x)
            }
        });
        var q = {
            length: l.length
        };
        for (var o = l.length; o--;) {
            q[o] = l[o];
            for (var k in l[o]) {
                if (!(k in q)) {
                    (function (v) {
                        q[v] = function () {
                            for (var w = this.length; w--;) {
                                if (typeof this[w][v] == "function") {
                                    this[w][v].apply(this[w], arguments)
                                }
                            }
                        }
                    })(k)
                }
            }
        }
        return q
    };
    AJS.Dialog.prototype.updateHeight = function () {
        var k = 0;
        var m = AJS.$(window).height() - e - c - (i * 2);
        for (var l = 0; this.getPanel(l); l++) {
            if (this.getPanel(l).body.css({
                height: "auto",
                display: "block"
            }).outerHeight() > k) {
                k = Math.min(m, this.getPanel(l).body.outerHeight())
            }
            if (l !== this.page[this.curpage].curtab) {
                this.getPanel(l).body.css({
                    display: "none"
                })
            }
        }
        for (l = 0; this.getPanel(l); l++) {
            this.getPanel(l).body.css({
                height: k || this.height
            })
        }
        this.page[0].menu.height(k);
        this.height = k + e + c + 1;
        this.popup.changeSize(undefined, this.height)
    };
    AJS.Dialog.prototype.isMaximised = function () {
        return this.popup.element.outerHeight() >= AJS.$(window).height() - (i * 2)
    };
    AJS.Dialog.prototype.getCurPanel = function () {
        return this.getPanel(this.page[this.curpage].curtab)
    };
    AJS.Dialog.prototype.getCurPanelButton = function () {
        return this.getCurPanel().button
    }
})();
(function (a) {
    var b = 0;
    AJS.DatePicker = function (h, d) {
        var g, c, e, f;
        g = {};
        f = b++;
        e = a(h);
        e.attr("data-aui-dp-uuid", f);
        d = a.extend(undefined, AJS.DatePicker.prototype.defaultOptions, d);
        g.getField = function () {
            return e
        };
        g.getOptions = function () {
            return d
        };
        c = function () {
            var l, k, r, m, p, o, q, s, n, i, j;
            g.hide = function () {
                q = true;
                i.hide();
                q = false
            };
            g.show = function () {
                i.show()
            };
            o = function () {
                j.off();
                l = a("<div/>");
                l.attr("data-aui-dp-popup-uuid", f);
                j.append(l);
                var t = {
                    dateFormat: a.datepicker.W3C,
                    defaultDate: e.val(),
                    maxDate: e.attr("max"),
                    minDate: e.attr("min"),
                    nextText: ">",
                    onSelect: function (v, u) {
                        e.val(v);
                        g.hide();
                        s = true;
                        e.focus()
                    },
                    prevText: "<"
                };
                if (!(d.languageCode in AJS.DatePicker.prototype.localisations)) {
                    d.languageCode = ""
                }
                a.extend(t, AJS.DatePicker.prototype.localisations[d.languageCode]);
                if (d.firstDay > -1) {
                    t.firstDay = d.firstDay
                }
                if (typeof e.attr("step") !== "undefined") {
                    AJS.log("WARNING: The AJS date picker polyfill currently does not support the step attribute!")
                }
                l.datepicker(t);
                e.on("focusout", r);
                e.on("propertychange keyup input paste", p)
            };
            k = function (u) {
                var t = a(u.target);
                u.preventDefault();
                if (!(t.closest(j).length || t.is(e))) {
                    if (t.closest("body").length) {
                        g.hide()
                    }
                }
            };
            r = function (t) {
                if (!(n)) {
                    a("body").on("focus blur click mousedown", "*", k);
                    n = true
                }
            };
            m = function (t) {
                if (!(s)) {
                    g.show()
                } else {
                    s = false
                }
            };
            p = function (t) {
                l.datepicker("setDate", e.val());
                l.datepicker("option", {
                    maxDate: e.attr("max"),
                    minDate: e.attr("min")
                })
            };
            g.destroyPolyfill = function () {
                g.hide();
                e.attr("placeholder", null);
                e.off("propertychange keyup input paste", p);
                e.off("focus click", m);
                e.off("focusout", r);
                e.attr("type", "date");
                if (typeof l !== "undefined") {
                    l.datepicker("destroy")
                }
                delete g.destroyPolyfill;
                delete g.show;
                delete g.hide
            };
            q = false;
            s = false;
            n = false;
            i = AJS.InlineDialog(e, undefined, function (u, t, v) {
                if (typeof l === "undefined") {
                    j = u;
                    o()
                }
                v()
            }, {
                hideCallback: function () {
                    a("body").off("focus blur click mousedown", "*", k);
                    n = false
                },
                hideDelay: null,
                noBind: true,
                preHideCallback: function () {
                    return q
                },
                width: 300
            });
            e.on("focus click", m);
            e.attr("placeholder", "YYYY-MM-DD");
            if (d.overrideBrowserDefault && AJS.DatePicker.prototype.browserSupportsDateField) {
                e[0].type = "text"
            }
        };
        g.reset = function () {
            if (typeof g.destroyPolyfill === "function") {
                g.destroyPolyfill()
            }
            if ((!(AJS.DatePicker.prototype.browserSupportsDateField)) || d.overrideBrowserDefault) {
                c()
            }
        };
        g.reset();
        return g
    };
    AJS.DatePicker.prototype.browserSupportsDateField = (a('<input type="date" />')[0].type === "date");
    AJS.DatePicker.prototype.defaultOptions = {
        overrideBrowserDefault: false,
        firstDay: -1,
        languageCode: AJS.$("html").attr("lang") || "en-AU"
    };
    AJS.DatePicker.prototype.localisations = {
        "": {
            dayNames: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
            dayNamesMin: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        af: {
            dayNames: ["Sondag", "Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrydag", "Saterdag"],
            dayNamesMin: ["Son", "Maa", "Din", "Woe", "Don", "Vry", "Sat"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Januarie", "Februarie", "Maart", "April", "Mei", "Junie", "Julie", "Augustus", "September", "Oktober", "November", "Desember"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        "ar-DZ": {
            dayNames: ["Ã˜Â§Ã™â€žÃ˜Â£Ã˜Ã˜Â¯", "Ã˜Â§Ã™â€žÃ˜Â§Ã˜Â«Ã™â€ Ã™Å Ã™â€ ", "Ã˜Â§Ã™â€žÃ˜Â«Ã™â€žÃ˜Â§Ã˜Â«Ã˜Â§Ã˜Â¡", "Ã˜Â§Ã™â€žÃ˜Â£Ã˜Â±Ã˜Â¨Ã˜Â¹Ã˜Â§Ã˜Â¡", "Ã˜Â§Ã™â€žÃ˜Â®Ã™â€¦Ã™Å Ã˜Â³", "Ã˜Â§Ã™â€žÃ˜Â¬Ã™â€¦Ã˜Â¹Ã˜Â©", "Ã˜Â§Ã™â€žÃ˜Â³Ã˜Â¨Ã˜Âª"],
            dayNamesMin: ["Ã˜Â§Ã™â€žÃ˜Â£Ã˜Ã˜Â¯", "Ã˜Â§Ã™â€žÃ˜Â§Ã˜Â«Ã™â€ Ã™Å Ã™â€ ", "Ã˜Â§Ã™â€žÃ˜Â«Ã™â€žÃ˜Â§Ã˜Â«Ã˜Â§Ã˜Â¡", "Ã˜Â§Ã™â€žÃ˜Â£Ã˜Â±Ã˜Â¨Ã˜Â¹Ã˜Â§Ã˜Â¡", "Ã˜Â§Ã™â€žÃ˜Â®Ã™â€¦Ã™Å Ã˜Â³", "Ã˜Â§Ã™â€žÃ˜Â¬Ã™â€¦Ã˜Â¹Ã˜Â©", "Ã˜Â§Ã™â€žÃ˜Â³Ã˜Â¨Ã˜Âª"],
            firstDay: 6,
            isRTL: true,
            monthNames: ["Ã˜Â¬Ã˜Â§Ã™â€ Ã™Â�Ã™Å ", "Ã™Â�Ã™Å Ã™Â�Ã˜Â±Ã™Å ", "Ã™â€¦Ã˜Â§Ã˜Â±Ã˜Â³", "Ã˜Â£Ã™Â�Ã˜Â±Ã™Å Ã™â€ž", "Ã™â€¦Ã˜Â§Ã™Å ", "Ã˜Â¬Ã™Ë†Ã˜Â§Ã™â€ ", "Ã˜Â¬Ã™Ë†Ã™Å Ã™â€žÃ™Å Ã˜Â©", "Ã˜Â£Ã™Ë†Ã˜Âª", "Ã˜Â³Ã˜Â¨Ã˜ÂªÃ™â€¦Ã˜Â¨Ã˜Â±", "Ã˜Â£Ã™Æ’Ã˜ÂªÃ™Ë†Ã˜Â¨Ã˜Â±", "Ã™â€ Ã™Ë†Ã™Â�Ã™â€¦Ã˜Â¨Ã˜Â±", "Ã˜Â¯Ã™Å Ã˜Â³Ã™â€¦Ã˜Â¨Ã˜Â±"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        ar: {
            dayNames: ["Ã˜Â§Ã™â€žÃ˜Â£Ã˜Ã˜Â¯", "Ã˜Â§Ã™â€žÃ˜Â§Ã˜Â«Ã™â€ Ã™Å Ã™â€ ", "Ã˜Â§Ã™â€žÃ˜Â«Ã™â€žÃ˜Â§Ã˜Â«Ã˜Â§Ã˜Â¡", "Ã˜Â§Ã™â€žÃ˜Â£Ã˜Â±Ã˜Â¨Ã˜Â¹Ã˜Â§Ã˜Â¡", "Ã˜Â§Ã™â€žÃ˜Â®Ã™â€¦Ã™Å Ã˜Â³", "Ã˜Â§Ã™â€žÃ˜Â¬Ã™â€¦Ã˜Â¹Ã˜Â©", "Ã˜Â§Ã™â€žÃ˜Â³Ã˜Â¨Ã˜Âª"],
            dayNamesMin: ["Ã˜Â§Ã™â€žÃ˜Â£Ã˜Ã˜Â¯", "Ã˜Â§Ã™â€žÃ˜Â§Ã˜Â«Ã™â€ Ã™Å Ã™â€ ", "Ã˜Â§Ã™â€žÃ˜Â«Ã™â€žÃ˜Â§Ã˜Â«Ã˜Â§Ã˜Â¡", "Ã˜Â§Ã™â€žÃ˜Â£Ã˜Â±Ã˜Â¨Ã˜Â¹Ã˜Â§Ã˜Â¡", "Ã˜Â§Ã™â€žÃ˜Â®Ã™â€¦Ã™Å Ã˜Â³", "Ã˜Â§Ã™â€žÃ˜Â¬Ã™â€¦Ã˜Â¹Ã˜Â©", "Ã˜Â§Ã™â€žÃ˜Â³Ã˜Â¨Ã˜Âª"],
            firstDay: 6,
            isRTL: true,
            monthNames: ["Ã™Æ’Ã˜Â§Ã™â€ Ã™Ë†Ã™â€  Ã˜Â§Ã™â€žÃ˜Â«Ã˜Â§Ã™â€ Ã™Å ", "Ã˜Â´Ã˜Â¨Ã˜Â§Ã˜Â·", "Ã˜Â¢Ã˜Â°Ã˜Â§Ã˜Â±", "Ã™â€ Ã™Å Ã˜Â³Ã˜Â§Ã™â€ ", "Ã™â€¦Ã˜Â§Ã™Å Ã™Ë†", "Ã˜Ã˜Â²Ã™Å Ã˜Â±Ã˜Â§Ã™â€ ", "Ã˜ÂªÃ™â€¦Ã™Ë†Ã˜Â²", "Ã˜Â¢Ã˜Â¨", "Ã˜Â£Ã™Å Ã™â€žÃ™Ë†Ã™â€ž", "Ã˜ÂªÃ˜Â´Ã˜Â±Ã™Å Ã™â€  Ã˜Â§Ã™â€žÃ˜Â£Ã™Ë†Ã™â€ž", "Ã˜ÂªÃ˜Â´Ã˜Â±Ã™Å Ã™â€  Ã˜Â§Ã™â€žÃ˜Â«Ã˜Â§Ã™â€ Ã™Å ", "Ã™Æ’Ã˜Â§Ã™â€ Ã™Ë†Ã™â€  Ã˜Â§Ã™â€žÃ˜Â£Ã™Ë†Ã™â€ž"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        az: {
            dayNames: ["Bazar", "Bazar ertÃ‰â„¢si", "Ãƒâ€¡Ã‰â„¢rÃ…Å¸Ã‰â„¢nbÃ‰â„¢ axÃ…Å¸amÃ„Â±", "Ãƒâ€¡Ã‰â„¢rÃ…Å¸Ã‰â„¢nbÃ‰â„¢", "CÃƒÂ¼mÃ‰â„¢ axÃ…Å¸amÃ„Â±", "CÃƒÂ¼mÃ‰â„¢", "Ã…Å¾Ã‰â„¢nbÃ‰â„¢"],
            dayNamesMin: ["B", "Be", "Ãƒâ€¡a", "Ãƒâ€¡", "Ca", "C", "Ã…Å¾"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Yanvar", "Fevral", "Mart", "Aprel", "May", "Ã„Â°yun", "Ã„Â°yul", "Avqust", "Sentyabr", "Oktyabr", "Noyabr", "Dekabr"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        bg: {
            dayNames: ["Ã�Â�Ã�ÂµÃ�Â´Ã�ÂµÃ�Â»Ã‘Â�", "Ã�Å¸Ã�Â¾Ã�Â½Ã�ÂµÃ�Â´Ã�ÂµÃ�Â»Ã�Â½Ã�Â¸Ã�Âº", "Ã�â€™Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â½Ã�Â¸Ã�Âº", "Ã�Â¡Ã‘â‚¬Ã‘Â�Ã�Â´Ã�Â°", "Ã�Â§Ã�ÂµÃ‘â€šÃ�Â²Ã‘Å Ã‘â‚¬Ã‘â€šÃ‘Å Ã�Âº", "Ã�Å¸Ã�ÂµÃ‘â€šÃ‘Å Ã�Âº", "Ã�Â¡Ã‘Å Ã�Â±Ã�Â¾Ã‘â€šÃ�Â°"],
            dayNamesMin: ["Ã�Â�Ã�ÂµÃ�Â´", "Ã�Å¸Ã�Â¾Ã�Â½", "Ã�â€™Ã‘â€šÃ�Â¾", "Ã�Â¡Ã‘â‚¬Ã‘Â�", "Ã�Â§Ã�ÂµÃ‘â€š", "Ã�Å¸Ã�ÂµÃ‘â€š", "Ã�Â¡Ã‘Å Ã�Â±"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã�Â¯Ã�Â½Ã‘Æ’Ã�Â°Ã‘â‚¬Ã�Â¸", "Ã�Â¤Ã�ÂµÃ�Â²Ã‘â‚¬Ã‘Æ’Ã�Â°Ã‘â‚¬Ã�Â¸", "Ã�Å“Ã�Â°Ã‘â‚¬Ã‘â€š", "Ã�Â�Ã�Â¿Ã‘â‚¬Ã�Â¸Ã�Â»", "Ã�Å“Ã�Â°Ã�Â¹", "Ã�Â®Ã�Â½Ã�Â¸", "Ã�Â®Ã�Â»Ã�Â¸", "Ã�Â�Ã�Â²Ã�Â³Ã‘Æ’Ã‘Â�Ã‘â€š", "Ã�Â¡Ã�ÂµÃ�Â¿Ã‘â€šÃ�ÂµÃ�Â¼Ã�Â²Ã‘â‚¬Ã�Â¸", "Ã�Å¾Ã�ÂºÃ‘â€šÃ�Â¾Ã�Â¼Ã�Â²Ã‘â‚¬Ã�Â¸", "Ã�Â�Ã�Â¾Ã�ÂµÃ�Â¼Ã�Â²Ã‘â‚¬Ã�Â¸", "Ã�â€�Ã�ÂµÃ�ÂºÃ�ÂµÃ�Â¼Ã�Â²Ã‘â‚¬Ã�Â¸"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        bs: {
            dayNames: ["Nedelja", "Ponedeljak", "Utorak", "Srijeda", "Ã„Å’etvrtak", "Petak", "Subota"],
            dayNamesMin: ["Ned", "Pon", "Uto", "Sri", "Ã„Å’et", "Pet", "Sub"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Januar", "Februar", "Mart", "April", "Maj", "Juni", "Juli", "August", "Septembar", "Oktobar", "Novembar", "Decembar"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        ca: {
            dayNames: ["Diumenge", "Dilluns", "Dimarts", "Dimecres", "Dijous", "Divendres", "Dissabte"],
            dayNamesMin: ["Dug", "Dln", "Dmt", "Dmc", "Djs", "Dvn", "Dsb"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Gener", "Febrer", "Mar&ccedil;", "Abril", "Maig", "Juny", "Juliol", "Agost", "Setembre", "Octubre", "Novembre", "Desembre"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        cs: {
            dayNames: ["nedÃ„â€ºle", "pondÃ„â€ºlÃƒ", "ÃƒÂºterÃƒÂ½", "stÃ…â„¢eda", "Ã„Â�tvrtek", "pÃƒÂ¡tek", "sobota"],
            dayNamesMin: ["ne", "po", "ÃƒÂºt", "st", "Ã„Â�t", "pÃƒÂ¡", "so"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["leden", "ÃƒÂºnor", "bÃ…â„¢ezen", "duben", "kvÃ„â€ºten", "Ã„Â�erven", "Ã„Â�ervenec", "srpen", "zÃƒÂ¡Ã…â„¢Ãƒ", "Ã…â„¢Ãƒjen", "listopad", "prosinec"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        "cy-GB": {
            dayNames: ["Dydd Sul", "Dydd Llun", "Dydd Mawrth", "Dydd Mercher", "Dydd Iau", "Dydd Gwener", "Dydd Sadwrn"],
            dayNamesMin: ["Sul", "Llu", "Maw", "Mer", "Iau", "Gwe", "Sad"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ionawr", "Chwefror", "Mawrth", "Ebrill", "Mai", "Mehefin", "Gorffennaf", "Awst", "Medi", "Hydref", "Tachwedd", "Rhagfyr"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        da: {
            dayNames: ["SÃƒÂ¸ndag", "Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "LÃƒÂ¸rdag"],
            dayNamesMin: ["SÃƒÂ¸n", "Man", "Tir", "Ons", "Tor", "Fre", "LÃƒÂ¸r"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Januar", "Februar", "Marts", "April", "Maj", "Juni", "Juli", "August", "September", "Oktober", "November", "December"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        de: {
            dayNames: ["Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"],
            dayNamesMin: ["So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Januar", "Februar", "MÃƒÂ¤rz", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        el: {
            dayNames: ["ÃŽÅ¡Ã�â€¦Ã�Â�ÃŽÂ¹ÃŽÂ±ÃŽÂºÃŽÂ®", "ÃŽâ€�ÃŽÂµÃ�â€¦Ã�â€žÃŽÃ�Â�ÃŽÂ±", "ÃŽÂ¤Ã�Â�ÃŽÂ¯Ã�â€žÃŽÂ·", "ÃŽÂ¤ÃŽÂµÃ�â€žÃŽÂ¬Ã�Â�Ã�â€žÃŽÂ·", "ÃŽ ÃŽÃŽÂ¼Ã�â‚¬Ã�â€žÃŽÂ·", "ÃŽ ÃŽÂ±Ã�Â�ÃŽÂ±Ã�Æ’ÃŽÂºÃŽÂµÃ�â€¦ÃŽÂ®", "ÃŽÂ£ÃŽÂ¬ÃŽÂ²ÃŽÂ²ÃŽÂ±Ã�â€žÃŽÂ¿"],
            dayNamesMin: ["ÃŽÅ¡Ã�â€¦Ã�Â�", "ÃŽâ€�ÃŽÂµÃ�â€¦", "ÃŽÂ¤Ã�Â�ÃŽÂ¹", "ÃŽÂ¤ÃŽÂµÃ�â€ž", "ÃŽ ÃŽÂµÃŽÂ¼", "ÃŽ ÃŽÂ±Ã�Â�", "ÃŽÂ£ÃŽÂ±ÃŽÂ²"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["ÃŽâ„¢ÃŽÂ±ÃŽÂ½ÃŽÂ¿Ã�â€¦ÃŽÂ¬Ã�Â�ÃŽÂ¹ÃŽÂ¿Ã�â€š", "ÃŽÂ¦ÃŽÂµÃŽÂ²Ã�Â�ÃŽÂ¿Ã�â€¦ÃŽÂ¬Ã�Â�ÃŽÂ¹ÃŽÂ¿Ã�â€š", "ÃŽÅ“ÃŽÂ¬Ã�Â�Ã�â€žÃŽÂ¹ÃŽÂ¿Ã�â€š", "ÃŽâ€˜Ã�â‚¬Ã�Â�ÃŽÂ¯ÃŽÂ»ÃŽÂ¹ÃŽÂ¿Ã�â€š", "ÃŽÅ“ÃŽÂ¬ÃŽÂ¹ÃŽÂ¿Ã�â€š", "ÃŽâ„¢ÃŽÂ¿Ã�Â�ÃŽÂ½ÃŽÂ¹ÃŽÂ¿Ã�â€š", "ÃŽâ„¢ÃŽÂ¿Ã�Â�ÃŽÂ»ÃŽÂ¹ÃŽÂ¿Ã�â€š", "ÃŽâ€˜Ã�Â�ÃŽÂ³ÃŽÂ¿Ã�â€¦Ã�Æ’Ã�â€žÃŽÂ¿Ã�â€š", "ÃŽÂ£ÃŽÂµÃ�â‚¬Ã�â€žÃŽÃŽÂ¼ÃŽÂ²Ã�Â�ÃŽÂ¹ÃŽÂ¿Ã�â€š", "ÃŽÅ¸ÃŽÂºÃ�â€žÃ�Å½ÃŽÂ²Ã�Â�ÃŽÂ¹ÃŽÂ¿Ã�â€š", "ÃŽÂ�ÃŽÂ¿ÃŽÃŽÂ¼ÃŽÂ²Ã�Â�ÃŽÂ¹ÃŽÂ¿Ã�â€š", "ÃŽâ€�ÃŽÂµÃŽÂºÃŽÃŽÂ¼ÃŽÂ²Ã�Â�ÃŽÂ¹ÃŽÂ¿Ã�â€š"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        "en-AU": {
            dayNames: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
            dayNamesMin: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        "en-GB": {
            dayNames: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
            dayNamesMin: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        "en-NZ": {
            dayNames: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
            dayNamesMin: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        eo: {
            dayNames: ["DimanÃ„â€°o", "Lundo", "Mardo", "Merkredo", "Ã„Â´aÃ…do", "Vendredo", "Sabato"],
            dayNamesMin: ["Dim", "Lun", "Mar", "Mer", "Ã„Â´aÃ…", "Ven", "Sab"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["Januaro", "Februaro", "Marto", "Aprilo", "Majo", "Junio", "Julio", "AÃ…gusto", "Septembro", "Oktobro", "Novembro", "Decembro"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        es: {
            dayNames: ["Domingo", "Lunes", "Martes", "Mi&eacute;rcoles", "Jueves", "Viernes", "S&aacute;bado"],
            dayNamesMin: ["Dom", "Lun", "Mar", "Mi&eacute;", "Juv", "Vie", "S&aacute;b"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        et: {
            dayNames: ["PÃƒÂ¼hapÃƒÂ¤ev", "EsmaspÃƒÂ¤ev", "TeisipÃƒÂ¤ev", "KolmapÃƒÂ¤ev", "NeljapÃƒÂ¤ev", "Reede", "LaupÃƒÂ¤ev"],
            dayNamesMin: ["PÃƒÂ¼hap", "Esmasp", "Teisip", "Kolmap", "Neljap", "Reede", "Laup"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Jaanuar", "Veebruar", "MÃƒÂ¤rts", "Aprill", "Mai", "Juuni", "Juuli", "August", "September", "Oktoober", "November", "Detsember"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        eu: {
            dayNames: ["Igandea", "Astelehena", "Asteartea", "Asteazkena", "Osteguna", "Ostirala", "Larunbata"],
            dayNamesMin: ["Iga", "Ast", "Ast", "Ast", "Ost", "Ost", "Lar"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Urtarrila", "Otsaila", "Martxoa", "Apirila", "Maiatza", "Ekaina", "Uztaila", "Abuztua", "Iraila", "Urria", "Azaroa", "Abendua"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        fa: {
            dayNames: ["Ã™Å ÃšÂ©Ã˜Â´Ã™â€ Ã˜Â¨Ã™â€¡", "Ã˜Â¯Ã™Ë†Ã˜Â´Ã™â€ Ã˜Â¨Ã™â€¡", "Ã˜Â³Ã™â€¡Ã¢â‚¬Å’Ã˜Â´Ã™â€ Ã˜Â¨Ã™â€¡", "Ãšâ€ Ã™â€¡Ã˜Â§Ã˜Â±Ã˜Â´Ã™â€ Ã˜Â¨Ã™â€¡", "Ã™Â¾Ã™â€ Ã˜Â¬Ã˜Â´Ã™â€ Ã˜Â¨Ã™â€¡", "Ã˜Â¬Ã™â€¦Ã˜Â¹Ã™â€¡", "Ã˜Â´Ã™â€ Ã˜Â¨Ã™â€¡"],
            dayNamesMin: ["Ã™Å ", "Ã˜Â¯", "Ã˜Â³", "Ãšâ€ ", "Ã™Â¾", "Ã˜Â¬", "Ã˜Â´"],
            firstDay: 6,
            isRTL: true,
            monthNames: ["Ã™Â�Ã˜Â±Ã™Ë†Ã˜Â±Ã˜Â¯Ã™Å Ã™â€ ", "Ã˜Â§Ã˜Â±Ã˜Â¯Ã™Å Ã˜Â¨Ã™â€¡Ã˜Â´Ã˜Âª", "Ã˜Â®Ã˜Â±Ã˜Â¯Ã˜Â§Ã˜Â¯", "Ã˜ÂªÃ™Å Ã˜Â±", "Ã™â€¦Ã˜Â±Ã˜Â¯Ã˜Â§Ã˜Â¯", "Ã˜Â´Ã™â€¡Ã˜Â±Ã™Å Ã™Ë†Ã˜Â±", "Ã™â€¦Ã™â€¡Ã˜Â±", "Ã˜Â¢Ã˜Â¨Ã˜Â§Ã™â€ ", "Ã˜Â¢Ã˜Â°Ã˜Â±", "Ã˜Â¯Ã™Å ", "Ã˜Â¨Ã™â€¡Ã™â€¦Ã™â€ ", "Ã˜Â§Ã˜Â³Ã™Â�Ã™â€ Ã˜Â¯"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        fi: {
            dayNames: ["Sunnuntai", "Maanantai", "Tiistai", "Keskiviikko", "Torstai", "Perjantai", "Lauantai"],
            dayNamesMin: ["Su", "Ma", "Ti", "Ke", "To", "Pe", "Su"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Tammikuu", "Helmikuu", "Maaliskuu", "Huhtikuu", "Toukokuu", "Kes&auml;kuu", "Hein&auml;kuu", "Elokuu", "Syyskuu", "Lokakuu", "Marraskuu", "Joulukuu"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        fo: {
            dayNames: ["Sunnudagur", "MÃƒÂ¡nadagur", "TÃƒÂ½sdagur", "Mikudagur", "HÃƒÂ³sdagur", "FrÃƒggjadagur", "Leyardagur"],
            dayNamesMin: ["Sun", "MÃƒÂ¡n", "TÃƒÂ½s", "Mik", "HÃƒÂ³s", "FrÃƒ", "Ley"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["Januar", "Februar", "Mars", "AprÃƒl", "Mei", "Juni", "Juli", "August", "September", "Oktober", "November", "Desember"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        "fr-CH": {
            dayNames: ["Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"],
            dayNamesMin: ["Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Janvier", "FÃƒÂ©vrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "AoÃƒÂ»t", "Septembre", "Octobre", "Novembre", "DÃƒÂ©cembre"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        fr: {
            dayNames: ["Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"],
            dayNamesMin: ["Dim.", "Lun.", "Mar.", "Mer.", "Jeu.", "Ven.", "Sam."],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Janvier", "FÃƒÂ©vrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "AoÃƒÂ»t", "Septembre", "Octobre", "Novembre", "DÃƒÂ©cembre"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        gl: {
            dayNames: ["Domingo", "Luns", "Martes", "M&eacute;rcores", "Xoves", "Venres", "S&aacute;bado"],
            dayNamesMin: ["Dom", "Lun", "Mar", "M&eacute;r", "Xov", "Ven", "S&aacute;b"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Xaneiro", "Febreiro", "Marzo", "Abril", "Maio", "XuÃƒÂ±o", "Xullo", "Agosto", "Setembro", "Outubro", "Novembro", "Decembro"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        he: {
            dayNames: ["Ã—Â¨Ã—Â�Ã—Â©Ã—â€¢Ã—Å¸", "Ã—Â©Ã— Ã—â„¢", "Ã—Â©Ã—Å“Ã—â„¢Ã—Â©Ã—â„¢", "Ã—Â¨Ã—â€˜Ã—â„¢Ã—Â¢Ã—â„¢", "Ã—â€”Ã—Å¾Ã—â„¢Ã—Â©Ã—â„¢", "Ã—Â©Ã—â„¢Ã—Â©Ã—â„¢", "Ã—Â©Ã—â€˜Ã—Âª"],
            dayNamesMin: ["Ã—Â�'", "Ã—â€˜'", "Ã—â€™'", "Ã—â€œ'", "Ã—â€�'", "Ã—â€¢'", "Ã—Â©Ã—â€˜Ã—Âª"],
            firstDay: 0,
            isRTL: true,
            monthNames: ["Ã—â„¢Ã— Ã—â€¢Ã—Â�Ã—Â¨", "Ã—Â¤Ã—â€˜Ã—Â¨Ã—â€¢Ã—Â�Ã—Â¨", "Ã—Å¾Ã—Â¨Ã—Â¥", "Ã—Â�Ã—Â¤Ã—Â¨Ã—â„¢Ã—Å“", "Ã—Å¾Ã—Â�Ã—â„¢", "Ã—â„¢Ã—â€¢Ã— Ã—â„¢", "Ã—â„¢Ã—â€¢Ã—Å“Ã—â„¢", "Ã—Â�Ã—â€¢Ã—â€™Ã—â€¢Ã—Â¡Ã—Ëœ", "Ã—Â¡Ã—Â¤Ã—ËœÃ—Å¾Ã—â€˜Ã—Â¨", "Ã—Â�Ã—â€¢Ã—Â§Ã—ËœÃ—â€¢Ã—â€˜Ã—Â¨", "Ã— Ã—â€¢Ã—â€˜Ã—Å¾Ã—â€˜Ã—Â¨", "Ã—â€œÃ—Â¦Ã—Å¾Ã—â€˜Ã—Â¨"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        hr: {
            dayNames: ["Nedjelja", "Ponedjeljak", "Utorak", "Srijeda", "Ã„Å’etvrtak", "Petak", "Subota"],
            dayNamesMin: ["Ned", "Pon", "Uto", "Sri", "Ã„Å’et", "Pet", "Sub"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["SijeÃ„Â�anj", "VeljaÃ„Â�a", "OÃ…Â¾ujak", "Travanj", "Svibanj", "Lipanj", "Srpanj", "Kolovoz", "Rujan", "Listopad", "Studeni", "Prosinac"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        hu: {
            dayNames: ["VasÃƒÂ¡rnap", "HÃƒÂ©tfÃ…â€˜", "Kedd", "Szerda", "CsÃƒÂ¼tÃƒÂ¶rtÃƒÂ¶k", "PÃƒÂ©ntek", "Szombat"],
            dayNamesMin: ["Vas", "HÃƒÂ©t", "Ked", "Sze", "CsÃƒÂ¼", "PÃƒÂ©n", "Szo"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["JanuÃƒÂ¡r", "FebruÃƒÂ¡r", "MÃƒÂ¡rcius", "ÃƒÂ�prilis", "MÃƒÂ¡jus", "JÃƒÂºnius", "JÃƒÂºlius", "Augusztus", "Szeptember", "OktÃƒÂ³ber", "November", "December"],
            showMonthAfterYear: true,
            yearSuffix: ""
        },
        hy: {
            dayNames: ["Ã•Â¯Ã•Â«Ã–â‚¬Ã•Â¡Ã•Â¯Ã•Â«", "Ã•Â¥Ã•Â¯Ã•Â¸Ã–â€šÃ•Â·Ã•Â¡Ã•Â¢Ã•Â©Ã•Â«", "Ã•Â¥Ã–â‚¬Ã•Â¥Ã–â€žÃ•Â·Ã•Â¡Ã•Â¢Ã•Â©Ã•Â«", "Ã•Â¹Ã•Â¸Ã–â‚¬Ã•Â¥Ã–â€žÃ•Â·Ã•Â¡Ã•Â¢Ã•Â©Ã•Â«", "Ã•Â°Ã•Â«Ã•Â¶Ã•Â£Ã•Â·Ã•Â¡Ã•Â¢Ã•Â©Ã•Â«", "Ã•Â¸Ã–â€šÃ–â‚¬Ã•Â¢Ã•Â¡Ã•Â©", "Ã•Â·Ã•Â¡Ã•Â¢Ã•Â¡Ã•Â©"],
            dayNamesMin: ["Ã•Â¯Ã•Â«Ã–â‚¬", "Ã•Â¥Ã–â‚¬Ã•Â¯", "Ã•Â¥Ã–â‚¬Ã–â€ž", "Ã•Â¹Ã–â‚¬Ã–â€ž", "Ã•Â°Ã•Â¶Ã•Â£", "Ã•Â¸Ã–â€šÃ–â‚¬Ã•Â¢", "Ã•Â·Ã•Â¢Ã•Â©"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã•â‚¬Ã•Â¸Ã–â€šÃ•Â¶Ã•Â¾Ã•Â¡Ã–â‚¬", "Ã•â€œÃ•Â¥Ã•Â¿Ã–â‚¬Ã•Â¾Ã•Â¡Ã–â‚¬", "Ã•â€žÃ•Â¡Ã–â‚¬Ã•Â¿", "Ã”Â±Ã•ÂºÃ–â‚¬Ã•Â«Ã•Â¬", "Ã•â€žÃ•Â¡Ã•ÂµÃ•Â«Ã•Â½", "Ã•â‚¬Ã•Â¸Ã–â€šÃ•Â¶Ã•Â«Ã•Â½", "Ã•â‚¬Ã•Â¸Ã–â€šÃ•Â¬Ã•Â«Ã•Â½", "Ã•â€¢Ã•Â£Ã•Â¸Ã•Â½Ã•Â¿Ã•Â¸Ã•Â½", "Ã•Â�Ã•Â¥Ã•ÂºÃ•Â¿Ã•Â¥Ã•Â´Ã•Â¢Ã•Â¥Ã–â‚¬", "Ã•â‚¬Ã•Â¸Ã•Â¯Ã•Â¿Ã•Â¥Ã•Â´Ã•Â¢Ã•Â¥Ã–â‚¬", "Ã•â€ Ã•Â¸Ã•ÂµÃ•Â¥Ã•Â´Ã•Â¢Ã•Â¥Ã–â‚¬", "Ã”Â´Ã•Â¥Ã•Â¯Ã•Â¿Ã•Â¥Ã•Â´Ã•Â¢Ã•Â¥Ã–â‚¬"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        id: {
            dayNames: ["Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"],
            dayNamesMin: ["Min", "Sen", "Sel", "Rab", "kam", "Jum", "Sab"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "Nopember", "Desember"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        is: {
            dayNames: ["Sunnudagur", "M&aacute;nudagur", "&THORN;ri&eth;judagur", "Mi&eth;vikudagur", "Fimmtudagur", "F&ouml;studagur", "Laugardagur"],
            dayNamesMin: ["Sun", "M&aacute;n", "&THORN;ri", "Mi&eth;", "Fim", "F&ouml;s", "Lau"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["Jan&uacute;ar", "Febr&uacute;ar", "Mars", "Apr&iacute;l", "Ma&iacute", "J&uacute;n&iacute;", "J&uacute;l&iacute;", "&Aacute;g&uacute;st", "September", "Okt&oacute;ber", "N&oacute;vember", "Desember"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        it: {
            dayNames: ["Domenica", "Luned&#236", "Marted&#236", "Mercoled&#236", "Gioved&#236", "Venerd&#236", "Sabato"],
            dayNamesMin: ["Dom", "Lun", "Mar", "Mer", "Gio", "Ven", "Sab"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        ja: {
            dayNames: ["Ã¦â€”Â¥Ã¦â€ºÅ“Ã¦â€”Â¥", "Ã¦Å“Ë†Ã¦â€ºÅ“Ã¦â€”Â¥", "Ã§Â�Â«Ã¦â€ºÅ“Ã¦â€”Â¥", "Ã¦Â°Â´Ã¦â€ºÅ“Ã¦â€”Â¥", "Ã¦Å“Â¨Ã¦â€ºÅ“Ã¦â€”Â¥", "Ã©â€¡â€˜Ã¦â€ºÅ“Ã¦â€”Â¥", "Ã¥Å“Å¸Ã¦â€ºÅ“Ã¦â€”Â¥"],
            dayNamesMin: ["Ã¦â€”Â¥", "Ã¦Å“Ë†", "Ã§Â�Â«", "Ã¦Â°Â´", "Ã¦Å“Â¨", "Ã©â€¡â€˜", "Ã¥Å“Å¸"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["1Ã¦Å“Ë†", "2Ã¦Å“Ë†", "3Ã¦Å“Ë†", "4Ã¦Å“Ë†", "5Ã¦Å“Ë†", "6Ã¦Å“Ë†", "7Ã¦Å“Ë†", "8Ã¦Å“Ë†", "9Ã¦Å“Ë†", "10Ã¦Å“Ë†", "11Ã¦Å“Ë†", "12Ã¦Å“Ë†"],
            showMonthAfterYear: true,
            yearSuffix: "Ã¥Â¹Â´"
        },
        kk: {
            dayNames: ["Ã�â€“Ã�ÂµÃ�ÂºÃ‘Â�Ã�ÂµÃ�Â½Ã�Â±Ã‘â€“", "Ã�â€�Ã’Â¯Ã�Â¹Ã‘Â�Ã�ÂµÃ�Â½Ã�Â±Ã‘â€“", "Ã�Â¡Ã�ÂµÃ�Â¹Ã‘Â�Ã�ÂµÃ�Â½Ã�Â±Ã‘â€“", "Ã�Â¡Ã“â„¢Ã‘â‚¬Ã‘Â�Ã�ÂµÃ�Â½Ã�Â±Ã‘â€“", "Ã�â€˜Ã�ÂµÃ�Â¹Ã‘Â�Ã�ÂµÃ�Â½Ã�Â±Ã‘â€“", "Ã�â€“Ã’Â±Ã�Â¼Ã�Â°", "Ã�Â¡Ã�ÂµÃ�Â½Ã�Â±Ã‘â€“"],
            dayNamesMin: ["Ã�Â¶Ã�ÂºÃ‘Â�", "Ã�Â´Ã‘Â�Ã�Â½", "Ã‘Â�Ã‘Â�Ã�Â½", "Ã‘Â�Ã‘â‚¬Ã‘Â�", "Ã�Â±Ã‘Â�Ã�Â½", "Ã�Â¶Ã�Â¼Ã�Â°", "Ã‘Â�Ã�Â½Ã�Â±"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã’Å¡Ã�Â°Ã’Â£Ã‘â€šÃ�Â°Ã‘â‚¬", "Ã�Â�Ã’â€ºÃ�Â¿Ã�Â°Ã�Â½", "Ã�Â�Ã�Â°Ã‘Æ’Ã‘â‚¬Ã‘â€¹Ã�Â·", "Ã�Â¡Ã“â„¢Ã‘Æ’Ã‘â€“Ã‘â‚¬", "Ã�Å“Ã�Â°Ã�Â¼Ã‘â€¹Ã‘â‚¬", "Ã�Å“Ã�Â°Ã‘Æ’Ã‘Â�Ã‘â€¹Ã�Â¼", "Ã�Â¨Ã‘â€“Ã�Â»Ã�Â´Ã�Âµ", "Ã�Â¢Ã�Â°Ã�Â¼Ã‘â€¹Ã�Â·", "Ã’Å¡Ã‘â€¹Ã‘â‚¬Ã�ÂºÃ’Â¯Ã�Â¹Ã�ÂµÃ�Âº", "Ã’Å¡Ã�Â°Ã�Â·Ã�Â°Ã�Â½", "Ã’Å¡Ã�Â°Ã‘â‚¬Ã�Â°Ã‘Ë†Ã�Â°", "Ã�â€“Ã�ÂµÃ�Â»Ã‘â€šÃ�Â¾Ã’â€ºÃ‘Â�Ã�Â°Ã�Â½"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        ko: {
            dayNames: ["Ã¬Â�Â¼", "Ã¬â€ºâ€�", "Ã­â„¢â€�", "Ã¬Ë†Ëœ", "Ã«ÂªÂ©", "ÃªÂ¸Ë†", "Ã­â€  "],
            dayNamesMin: ["Ã¬Â�Â¼", "Ã¬â€ºâ€�", "Ã­â„¢â€�", "Ã¬Ë†Ëœ", "Ã«ÂªÂ©", "ÃªÂ¸Ë†", "Ã­â€  "],
            firstDay: 0,
            isRTL: false,
            monthNames: ["1Ã¬â€ºâ€�(JAN)", "2Ã¬â€ºâ€�(FEB)", "3Ã¬â€ºâ€�(MAR)", "4Ã¬â€ºâ€�(APR)", "5Ã¬â€ºâ€�(MAY)", "6Ã¬â€ºâ€�(JUN)", "7Ã¬â€ºâ€�(JUL)", "8Ã¬â€ºâ€�(AUG)", "9Ã¬â€ºâ€�(SEP)", "10Ã¬â€ºâ€�(OCT)", "11Ã¬â€ºâ€�(NOV)", "12Ã¬â€ºâ€�(DEC)"],
            showMonthAfterYear: false,
            yearSuffix: "Ã«â€¦â€ž"
        },
        lb: {
            dayNames: ["Sonndeg", "MÃƒÂ©indeg", "DÃƒÂ«nschdeg", "MÃƒÂ«ttwoch", "Donneschdeg", "Freideg", "Samschdeg"],
            dayNamesMin: ["Son", "MÃƒÂ©i", "DÃƒÂ«n", "MÃƒÂ«t", "Don", "Fre", "Sam"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Januar", "Februar", "MÃƒÂ¤erz", "AbrÃƒÂ«ll", "Mee", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        lt: {
            dayNames: ["sekmadienis", "pirmadienis", "antradienis", "treÃ„Â�iadienis", "ketvirtadienis", "penktadienis", "Ã…Â¡eÃ…Â¡tadienis"],
            dayNamesMin: ["sek", "pir", "ant", "tre", "ket", "pen", "Ã…Â¡eÃ…Â¡"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Sausis", "Vasaris", "Kovas", "Balandis", "GeguÃ…Â¾Ã„â€”", "BirÃ…Â¾elis", "Liepa", "RugpjÃ…Â«tis", "RugsÃ„â€”jis", "Spalis", "Lapkritis", "Gruodis"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        lv: {
            dayNames: ["svÃ„â€œtdiena", "pirmdiena", "otrdiena", "treÃ…Â¡diena", "ceturtdiena", "piektdiena", "sestdiena"],
            dayNamesMin: ["svt", "prm", "otr", "tre", "ctr", "pkt", "sst"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["JanvÃ„Â�ris", "FebruÃ„Â�ris", "Marts", "AprÃ„Â«lis", "Maijs", "JÃ…Â«nijs", "JÃ…Â«lijs", "Augusts", "Septembris", "Oktobris", "Novembris", "Decembris"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        mk: {
            dayNames: ["Ã�Â�Ã�ÂµÃ�Â´Ã�ÂµÃ�Â»Ã�Â°", "Ã�Å¸Ã�Â¾Ã�Â½Ã�ÂµÃ�Â´Ã�ÂµÃ�Â»Ã�Â½Ã�Â¸Ã�Âº", "Ã�â€™Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â½Ã�Â¸Ã�Âº", "Ã�Â¡Ã‘â‚¬Ã�ÂµÃ�Â´Ã�Â°", "Ã�Â§Ã�ÂµÃ‘â€šÃ�Â²Ã‘â‚¬Ã‘â€šÃ�Â¾Ã�Âº", "Ã�Å¸Ã�ÂµÃ‘â€šÃ�Â¾Ã�Âº", "Ã�Â¡Ã�Â°Ã�Â±Ã�Â¾Ã‘â€šÃ�Â°"],
            dayNamesMin: ["Ã�Â�Ã�ÂµÃ�Â´", "Ã�Å¸Ã�Â¾Ã�Â½", "Ã�â€™Ã‘â€šÃ�Â¾", "Ã�Â¡Ã‘â‚¬Ã�Âµ", "Ã�Â§Ã�ÂµÃ‘â€š", "Ã�Å¸Ã�ÂµÃ‘â€š", "Ã�Â¡Ã�Â°Ã�Â±"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã�Ë†Ã�Â°Ã�Â½Ã‘Æ’Ã�Â°Ã‘â‚¬Ã�Â¸", "Ã�Â¤Ã�ÂµÃ�Â±Ã‘â‚¬Ã‘Æ’Ã�Â°Ã‘â‚¬Ã�Â¸", "Ã�Å“Ã�Â°Ã‘â‚¬Ã‘â€š", "Ã�Â�Ã�Â¿Ã‘â‚¬Ã�Â¸Ã�Â»", "Ã�Å“Ã�Â°Ã‘Ëœ", "Ã�Ë†Ã‘Æ’Ã�Â½Ã�Â¸", "Ã�Ë†Ã‘Æ’Ã�Â»Ã�Â¸", "Ã�Â�Ã�Â²Ã�Â³Ã‘Æ’Ã‘Â�Ã‘â€š", "Ã�Â¡Ã�ÂµÃ�Â¿Ã‘â€šÃ�ÂµÃ�Â¼Ã�Â²Ã‘â‚¬Ã�Â¸", "Ã�Å¾Ã�ÂºÃ‘â€šÃ�Â¾Ã�Â¼Ã�Â²Ã‘â‚¬Ã�Â¸", "Ã�Â�Ã�Â¾Ã�ÂµÃ�Â¼Ã�Â²Ã‘â‚¬Ã�Â¸", "Ã�â€�Ã�ÂµÃ�ÂºÃ�ÂµÃ�Â¼Ã�Â²Ã‘â‚¬Ã�Â¸"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        ml: {
            dayNames: ["Ã Â´Å¾Ã Â´Â¾Ã Â´Â¯Ã Â´Â°Ã ÂµÂ�Ã¢â‚¬Â�", "Ã Â´Â¤Ã Â´Â¿Ã Â´â„¢Ã ÂµÂ�Ã Â´â€¢Ã Â´Â³Ã ÂµÂ�Ã¢â‚¬Â�", "Ã Â´Å¡Ã ÂµÅ Ã Â´ÂµÃ ÂµÂ�Ã Â´Âµ", "Ã Â´Â¬Ã ÂµÂ�Ã Â´Â§Ã Â´Â¨Ã ÂµÂ�Ã¢â‚¬Â�", "Ã Â´ÂµÃ ÂµÂ�Ã Â´Â¯Ã Â´Â¾Ã Â´Â´Ã Â´â€š", "Ã Â´ÂµÃ Âµâ€ Ã Â´Â³Ã ÂµÂ�Ã Â´Â³Ã Â´Â¿", "Ã Â´Â¶Ã Â´Â¨Ã Â´Â¿"],
            dayNamesMin: ["Ã Â´Å¾Ã Â´Â¾Ã Â´Â¯", "Ã Â´Â¤Ã Â´Â¿Ã Â´â„¢Ã ÂµÂ�Ã Â´â€¢", "Ã Â´Å¡Ã ÂµÅ Ã Â´ÂµÃ ÂµÂ�Ã Â´Âµ", "Ã Â´Â¬Ã ÂµÂ�Ã Â´Â§", "Ã Â´ÂµÃ ÂµÂ�Ã Â´Â¯Ã Â´Â¾Ã Â´Â´Ã Â´â€š", "Ã Â´ÂµÃ Âµâ€ Ã Â´Â³Ã ÂµÂ�Ã Â´Â³Ã Â´Â¿", "Ã Â´Â¶Ã Â´Â¨Ã Â´Â¿"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã Â´Å“Ã Â´Â¨Ã ÂµÂ�Ã Â´ÂµÃ Â´Â°Ã Â´Â¿", "Ã Â´Â«Ã Âµâ€ Ã Â´Â¬Ã ÂµÂ�Ã Â´Â°Ã ÂµÂ�Ã Â´ÂµÃ Â´Â°Ã Â´Â¿", "Ã Â´Â®Ã Â´Â¾Ã Â´Â°Ã ÂµÂ�Ã¢â‚¬Â�Ã Â´Å¡Ã ÂµÂ�Ã Â´Å¡Ã ÂµÂ�", "Ã Â´Â�Ã Â´ÂªÃ ÂµÂ�Ã Â´Â°Ã Â´Â¿Ã Â´Â²Ã ÂµÂ�Ã¢â‚¬Â�", "Ã Â´Â®Ã Âµâ€¡Ã Â´Â¯Ã ÂµÂ�", "Ã Â´Å“Ã Âµâ€šÃ Â´Â£Ã ÂµÂ�Ã¢â‚¬Â�", "Ã Â´Å“Ã Âµâ€šÃ Â´Â²Ã ÂµË†", "Ã Â´â€ Ã Â´â€”Ã Â´Â¸Ã ÂµÂ�Ã Â´Â±Ã ÂµÂ�Ã Â´Â±Ã ÂµÂ�", "Ã Â´Â¸Ã Âµâ€ Ã Â´ÂªÃ ÂµÂ�Ã Â´Â±Ã ÂµÂ�Ã Â´Â±Ã Â´â€šÃ Â´Â¬Ã Â´Â°Ã ÂµÂ�Ã¢â‚¬Â�", "Ã Â´â€™Ã Â´â€¢Ã ÂµÂ�Ã Â´Å¸Ã Âµâ€¹Ã Â´Â¬Ã Â´Â°Ã ÂµÂ�Ã¢â‚¬Â�", "Ã Â´Â¨Ã Â´ÂµÃ Â´â€šÃ Â´Â¬Ã Â´Â°Ã ÂµÂ�Ã¢â‚¬Â�", "Ã Â´Â¡Ã Â´Â¿Ã Â´Â¸Ã Â´â€šÃ Â´Â¬Ã Â´Â°Ã ÂµÂ�Ã¢â‚¬Â�"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        ms: {
            dayNames: ["Ahad", "Isnin", "Selasa", "Rabu", "Khamis", "Jumaat", "Sabtu"],
            dayNamesMin: ["Aha", "Isn", "Sel", "Rab", "kha", "Jum", "Sab"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["Januari", "Februari", "Mac", "April", "Mei", "Jun", "Julai", "Ogos", "September", "Oktober", "November", "Disember"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        "nl-BE": {
            dayNames: ["zondag", "maandag", "dinsdag", "woensdag", "donderdag", "vrijdag", "zaterdag"],
            dayNamesMin: ["zon", "maa", "din", "woe", "don", "vri", "zat"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        nl: {
            dayNames: ["zondag", "maandag", "dinsdag", "woensdag", "donderdag", "vrijdag", "zaterdag"],
            dayNamesMin: ["zon", "maa", "din", "woe", "don", "vri", "zat"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        no: {
            dayNames: ["sÃƒÂ¸ndag", "mandag", "tirsdag", "onsdag", "torsdag", "fredag", "lÃƒÂ¸rdag"],
            dayNamesMin: ["sÃƒÂ¸n", "man", "tir", "ons", "tor", "fre", "lÃƒÂ¸r"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["januar", "februar", "mars", "april", "mai", "juni", "juli", "august", "september", "oktober", "november", "desember"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        pl: {
            dayNames: ["Niedziela", "PoniedziaÃ…â€šek", "Wtorek", "Ã…Å¡roda", "Czwartek", "PiÃ„â€¦tek", "Sobota"],
            dayNamesMin: ["Nie", "Pn", "Wt", "Ã…Å¡r", "Czw", "Pt", "So"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["StyczeÃ…â€ž", "Luty", "Marzec", "KwiecieÃ…â€ž", "Maj", "Czerwiec", "Lipiec", "SierpieÃ…â€ž", "WrzesieÃ…â€ž", "PaÃ…Âºdziernik", "Listopad", "GrudzieÃ…â€ž"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        "pt-BR": {
            dayNames: ["Domingo", "Segunda-feira", "Ter&ccedil;a-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "S&aacute;bado"],
            dayNamesMin: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "S&aacute;b"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["Janeiro", "Fevereiro", "Mar&ccedil;o", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        pt: {
            dayNames: ["Domingo", "Segunda-feira", "Ter&ccedil;a-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "S&aacute;bado"],
            dayNamesMin: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "S&aacute;b"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["Janeiro", "Fevereiro", "Mar&ccedil;o", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        rm: {
            dayNames: ["Dumengia", "Glindesdi", "Mardi", "Mesemna", "Gievgia", "Venderdi", "Sonda"],
            dayNamesMin: ["Dum", "Gli", "Mar", "Mes", "Gie", "Ven", "Som"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Schaner", "Favrer", "Mars", "Avrigl", "Matg", "Zercladur", "Fanadur", "Avust", "Settember", "October", "November", "December"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        ro: {
            dayNames: ["DuminicÃ„Æ’", "Luni", "MarÃ…Â£i", "Miercuri", "Joi", "Vineri", "SÃƒÂ¢mbÃ„Æ’tÃ„Æ’"],
            dayNamesMin: ["Dum", "Lun", "Mar", "Mie", "Joi", "Vin", "SÃƒÂ¢m"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ianuarie", "Februarie", "Martie", "Aprilie", "Mai", "Iunie", "Iulie", "August", "Septembrie", "Octombrie", "Noiembrie", "Decembrie"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        ru: {
            dayNames: ["Ã�Â²Ã�Â¾Ã‘Â�Ã�ÂºÃ‘â‚¬Ã�ÂµÃ‘Â�Ã�ÂµÃ�Â½Ã‘Å’Ã�Âµ", "Ã�Â¿Ã�Â¾Ã�Â½Ã�ÂµÃ�Â´Ã�ÂµÃ�Â»Ã‘Å’Ã�Â½Ã�Â¸Ã�Âº", "Ã�Â²Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â½Ã�Â¸Ã�Âº", "Ã‘Â�Ã‘â‚¬Ã�ÂµÃ�Â´Ã�Â°", "Ã‘â€¡Ã�ÂµÃ‘â€šÃ�Â²Ã�ÂµÃ‘â‚¬Ã�Â³", "Ã�Â¿Ã‘Â�Ã‘â€šÃ�Â½Ã�Â¸Ã‘â€ Ã�Â°", "Ã‘Â�Ã‘Æ’Ã�Â±Ã�Â±Ã�Â¾Ã‘â€šÃ�Â°"],
            dayNamesMin: ["Ã�Â²Ã‘Â�Ã�Âº", "Ã�Â¿Ã�Â½Ã�Â´", "Ã�Â²Ã‘â€šÃ‘â‚¬", "Ã‘Â�Ã‘â‚¬Ã�Â´", "Ã‘â€¡Ã‘â€šÃ�Â²", "Ã�Â¿Ã‘â€šÃ�Â½", "Ã‘Â�Ã�Â±Ã‘â€š"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã�Â¯Ã�Â½Ã�Â²Ã�Â°Ã‘â‚¬Ã‘Å’", "Ã�Â¤Ã�ÂµÃ�Â²Ã‘â‚¬Ã�Â°Ã�Â»Ã‘Å’", "Ã�Å“Ã�Â°Ã‘â‚¬Ã‘â€š", "Ã�Â�Ã�Â¿Ã‘â‚¬Ã�ÂµÃ�Â»Ã‘Å’", "Ã�Å“Ã�Â°Ã�Â¹", "Ã�ËœÃ‘Å½Ã�Â½Ã‘Å’", "Ã�ËœÃ‘Å½Ã�Â»Ã‘Å’", "Ã�Â�Ã�Â²Ã�Â³Ã‘Æ’Ã‘Â�Ã‘â€š", "Ã�Â¡Ã�ÂµÃ�Â½Ã‘â€šÃ‘Â�Ã�Â±Ã‘â‚¬Ã‘Å’", "Ã�Å¾Ã�ÂºÃ‘â€šÃ‘Â�Ã�Â±Ã‘â‚¬Ã‘Å’", "Ã�Â�Ã�Â¾Ã‘Â�Ã�Â±Ã‘â‚¬Ã‘Å’", "Ã�â€�Ã�ÂµÃ�ÂºÃ�Â°Ã�Â±Ã‘â‚¬Ã‘Å’"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        sk: {
            dayNames: ["NedeÃ„Â¾a", "Pondelok", "Utorok", "Streda", "Ã… tvrtok", "Piatok", "Sobota"],
            dayNamesMin: ["Ned", "Pon", "Uto", "Str", "Ã… tv", "Pia", "Sob"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["JanuÃƒÂ¡r", "FebruÃƒÂ¡r", "Marec", "AprÃƒl", "MÃƒÂ¡j", "JÃƒÂºn", "JÃƒÂºl", "August", "September", "OktÃƒÂ³ber", "November", "December"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        sl: {
            dayNames: ["Nedelja", "Ponedeljek", "Torek", "Sreda", "&#x10C;etrtek", "Petek", "Sobota"],
            dayNamesMin: ["Ned", "Pon", "Tor", "Sre", "&#x10C;et", "Pet", "Sob"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Januar", "Februar", "Marec", "April", "Maj", "Junij", "Julij", "Avgust", "September", "Oktober", "November", "December"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        sq: {
            dayNames: ["E Diel", "E HÃƒÂ«nÃƒÂ«", "E MartÃƒÂ«", "E MÃƒÂ«rkurÃƒÂ«", "E Enjte", "E Premte", "E Shtune"],
            dayNamesMin: ["Di", "HÃƒÂ«", "Ma", "MÃƒÂ«", "En", "Pr", "Sh"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Janar", "Shkurt", "Mars", "Prill", "Maj", "Qershor", "Korrik", "Gusht", "Shtator", "Tetor", "NÃƒÂ«ntor", "Dhjetor"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        "sr-SR": {
            dayNames: ["Nedelja", "Ponedeljak", "Utorak", "Sreda", "Ã„Å’etvrtak", "Petak", "Subota"],
            dayNamesMin: ["Ned", "Pon", "Uto", "Sre", "Ã„Å’et", "Pet", "Sub"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Januar", "Februar", "Mart", "April", "Maj", "Jun", "Jul", "Avgust", "Septembar", "Oktobar", "Novembar", "Decembar"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        sr: {
            dayNames: ["Ã�Â�Ã�ÂµÃ�Â´Ã�ÂµÃ‘â„¢Ã�Â°", "Ã�Å¸Ã�Â¾Ã�Â½Ã�ÂµÃ�Â´Ã�ÂµÃ‘â„¢Ã�Â°Ã�Âº", "Ã�Â£Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â°Ã�Âº", "Ã�Â¡Ã‘â‚¬Ã�ÂµÃ�Â´Ã�Â°", "Ã�Â§Ã�ÂµÃ‘â€šÃ�Â²Ã‘â‚¬Ã‘â€šÃ�Â°Ã�Âº", "Ã�Å¸Ã�ÂµÃ‘â€šÃ�Â°Ã�Âº", "Ã�Â¡Ã‘Æ’Ã�Â±Ã�Â¾Ã‘â€šÃ�Â°"],
            dayNamesMin: ["Ã�Â�Ã�ÂµÃ�Â´", "Ã�Å¸Ã�Â¾Ã�Â½", "Ã�Â£Ã‘â€šÃ�Â¾", "Ã�Â¡Ã‘â‚¬Ã�Âµ", "Ã�Â§Ã�ÂµÃ‘â€š", "Ã�Å¸Ã�ÂµÃ‘â€š", "Ã�Â¡Ã‘Æ’Ã�Â±"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã�Ë†Ã�Â°Ã�Â½Ã‘Æ’Ã�Â°Ã‘â‚¬", "Ã�Â¤Ã�ÂµÃ�Â±Ã‘â‚¬Ã‘Æ’Ã�Â°Ã‘â‚¬", "Ã�Å“Ã�Â°Ã‘â‚¬Ã‘â€š", "Ã�Â�Ã�Â¿Ã‘â‚¬Ã�Â¸Ã�Â»", "Ã�Å“Ã�Â°Ã‘Ëœ", "Ã�Ë†Ã‘Æ’Ã�Â½", "Ã�Ë†Ã‘Æ’Ã�Â»", "Ã�Â�Ã�Â²Ã�Â³Ã‘Æ’Ã‘Â�Ã‘â€š", "Ã�Â¡Ã�ÂµÃ�Â¿Ã‘â€šÃ�ÂµÃ�Â¼Ã�Â±Ã�Â°Ã‘â‚¬", "Ã�Å¾Ã�ÂºÃ‘â€šÃ�Â¾Ã�Â±Ã�Â°Ã‘â‚¬", "Ã�Â�Ã�Â¾Ã�Â²Ã�ÂµÃ�Â¼Ã�Â±Ã�Â°Ã‘â‚¬", "Ã�â€�Ã�ÂµÃ‘â€ Ã�ÂµÃ�Â¼Ã�Â±Ã�Â°Ã‘â‚¬"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        sv: {
            dayNames: ["SÃƒÂ¶ndag", "MÃƒÂ¥ndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "LÃƒÂ¶rdag"],
            dayNamesMin: ["SÃƒÂ¶n", "MÃƒÂ¥n", "Tis", "Ons", "Tor", "Fre", "LÃƒÂ¶r"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Januari", "Februari", "Mars", "April", "Maj", "Juni", "Juli", "Augusti", "September", "Oktober", "November", "December"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        ta: {
            dayNames: ["Ã Â®Å¾Ã Â®Â¾Ã Â®Â¯Ã Â®Â¿Ã Â®Â±Ã Â¯Â�Ã Â®Â±Ã Â¯Â�Ã Â®â€¢Ã Â¯Â�Ã Â®â€¢Ã Â®Â¿Ã Â®Â´Ã Â®Â®Ã Â¯Ë†", "Ã Â®Â¤Ã Â®Â¿Ã Â®â„¢Ã Â¯Â�Ã Â®â€¢Ã Â®Å¸Ã Â¯Â�Ã Â®â€¢Ã Â®Â¿Ã Â®Â´Ã Â®Â®Ã Â¯Ë†", "Ã Â®Å¡Ã Â¯â€ Ã Â®ÂµÃ Â¯Â�Ã Â®ÂµÃ Â®Â¾Ã Â®Â¯Ã Â¯Â�Ã Â®â€¢Ã Â¯Â�Ã Â®â€¢Ã Â®Â¿Ã Â®Â´Ã Â®Â®Ã Â¯Ë†", "Ã Â®ÂªÃ Â¯Â�Ã Â®Â¤Ã Â®Â©Ã Â¯Â�Ã Â®â€¢Ã Â®Â¿Ã Â®Â´Ã Â®Â®Ã Â¯Ë†", "Ã Â®ÂµÃ Â®Â¿Ã Â®Â¯Ã Â®Â¾Ã Â®Â´Ã Â®â€¢Ã Â¯Â�Ã Â®â€¢Ã Â®Â¿Ã Â®Â´Ã Â®Â®Ã Â¯Ë†", "Ã Â®ÂµÃ Â¯â€ Ã Â®Â³Ã Â¯Â�Ã Â®Â³Ã Â®Â¿Ã Â®â€¢Ã Â¯Â�Ã Â®â€¢Ã Â®Â¿Ã Â®Â´Ã Â®Â®Ã Â¯Ë†", "Ã Â®Å¡Ã Â®Â©Ã Â®Â¿Ã Â®â€¢Ã Â¯Â�Ã Â®â€¢Ã Â®Â¿Ã Â®Â´Ã Â®Â®Ã Â¯Ë†"],
            dayNamesMin: ["Ã Â®Å¾Ã Â®Â¾Ã Â®Â¯Ã Â®Â¿Ã Â®Â±Ã Â¯Â�", "Ã Â®Â¤Ã Â®Â¿Ã Â®â„¢Ã Â¯Â�Ã Â®â€¢Ã Â®Â³Ã Â¯Â�", "Ã Â®Å¡Ã Â¯â€ Ã Â®ÂµÃ Â¯Â�Ã Â®ÂµÃ Â®Â¾Ã Â®Â¯Ã Â¯Â�", "Ã Â®ÂªÃ Â¯Â�Ã Â®Â¤Ã Â®Â©Ã Â¯Â�", "Ã Â®ÂµÃ Â®Â¿Ã Â®Â¯Ã Â®Â¾Ã Â®Â´Ã Â®Â©Ã Â¯Â�", "Ã Â®ÂµÃ Â¯â€ Ã Â®Â³Ã Â¯Â�Ã Â®Â³Ã Â®Â¿", "Ã Â®Å¡Ã Â®Â©Ã Â®Â¿"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã Â®Â¤Ã Â¯Ë†", "Ã Â®Â®Ã Â®Â¾Ã Â®Å¡Ã Â®Â¿", "Ã Â®ÂªÃ Â®â„¢Ã Â¯Â�Ã Â®â€¢Ã Â¯Â�Ã Â®Â©Ã Â®Â¿", "Ã Â®Å¡Ã Â®Â¿Ã Â®Â¤Ã Â¯Â�Ã Â®Â¤Ã Â®Â¿Ã Â®Â°Ã Â¯Ë†", "Ã Â®ÂµÃ Â¯Ë†Ã Â®â€¢Ã Â®Â¾Ã Â®Å¡Ã Â®Â¿", "Ã Â®â€ Ã Â®Â©Ã Â®Â¿", "Ã Â®â€ Ã Â®Å¸Ã Â®Â¿", "Ã Â®â€ Ã Â®ÂµÃ Â®Â£Ã Â®Â¿", "Ã Â®ÂªÃ Â¯Â�Ã Â®Â°Ã Â®Å¸Ã Â¯Â�Ã Â®Å¸Ã Â®Â¾Ã Â®Å¡Ã Â®Â¿", "Ã Â®Â�Ã Â®ÂªÃ Â¯Â�Ã Â®ÂªÃ Â®Å¡Ã Â®Â¿", "Ã Â®â€¢Ã Â®Â¾Ã Â®Â°Ã Â¯Â�Ã Â®Â¤Ã Â¯Â�Ã Â®Â¤Ã Â®Â¿Ã Â®â€¢Ã Â¯Ë†", "Ã Â®Â®Ã Â®Â¾Ã Â®Â°Ã Â¯Â�Ã Â®â€¢Ã Â®Â´Ã Â®Â¿"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        th: {
            dayNames: ["Ã Â¸Ã Â¸Â²Ã Â¸â€”Ã Â¸Â´Ã Â¸â€¢Ã Â¸Â¢Ã Â¹Å’", "Ã Â¸Ë†Ã Â¸Â±Ã Â¸â„¢Ã Â¸â€”Ã Â¸Â£Ã Â¹Å’", "Ã Â¸Ã Â¸Â±Ã Â¸â€¡Ã Â¸â€žÃ Â¸Â²Ã Â¸Â£", "Ã Â¸Å¾Ã Â¸Â¸Ã Â¸Ëœ", "Ã Â¸Å¾Ã Â¸Â¤Ã Â¸Â«Ã Â¸Â±Ã Â¸ÂªÃ Â¸Å¡Ã Â¸â€�Ã Â¸Âµ", "Ã Â¸Â¨Ã Â¸Â¸Ã Â¸Â�Ã Â¸Â£Ã Â¹Å’", "Ã Â¹â‚¬Ã Â¸ÂªÃ Â¸Â²Ã Â¸Â£Ã Â¹Å’"],
            dayNamesMin: ["Ã Â¸Ã Â¸Â².", "Ã Â¸Ë†.", "Ã Â¸.", "Ã Â¸Å¾.", "Ã Â¸Å¾Ã Â¸Â¤.", "Ã Â¸Â¨.", "Ã Â¸Âª."],
            firstDay: 0,
            isRTL: false,
            monthNames: ["Ã Â¸Â¡Ã Â¸Â�Ã Â¸Â£Ã Â¸Â²Ã Â¸â€žÃ Â¸Â¡", "Ã Â¸Â�Ã Â¸Â¸Ã Â¸Â¡Ã Â¸ Ã Â¸Â²Ã Â¸Å¾Ã Â¸Â±Ã Â¸â„¢Ã Â¸ËœÃ Â¹Å’", "Ã Â¸Â¡Ã Â¸ÂµÃ Â¸â„¢Ã Â¸Â²Ã Â¸â€žÃ Â¸Â¡", "Ã Â¹â‚¬Ã Â¸Â¡Ã Â¸Â©Ã Â¸Â²Ã Â¸Â¢Ã Â¸â„¢", "Ã Â¸Å¾Ã Â¸Â¤Ã Â¸Â©Ã Â¸ Ã Â¸Â²Ã Â¸â€žÃ Â¸Â¡", "Ã Â¸Â¡Ã Â¸Â´Ã Â¸â€“Ã Â¸Â¸Ã Â¸â„¢Ã Â¸Â²Ã Â¸Â¢Ã Â¸â„¢", "Ã Â¸Â�Ã Â¸Â£Ã Â¸Â�Ã Â¸Å½Ã Â¸Â²Ã Â¸â€žÃ Â¸Â¡", "Ã Â¸ÂªÃ Â¸Â´Ã Â¸â€¡Ã Â¸Â«Ã Â¸Â²Ã Â¸â€žÃ Â¸Â¡", "Ã Â¸Â�Ã Â¸Â±Ã Â¸â„¢Ã Â¸Â¢Ã Â¸Â²Ã Â¸Â¢Ã Â¸â„¢", "Ã Â¸â€¢Ã Â¸Â¸Ã Â¸Â¥Ã Â¸Â²Ã Â¸â€žÃ Â¸Â¡", "Ã Â¸Å¾Ã Â¸Â¤Ã Â¸Â¨Ã Â¸Ë†Ã Â¸Â´Ã Â¸Â�Ã Â¸Â²Ã Â¸Â¢Ã Â¸â„¢", "Ã Â¸ËœÃ Â¸Â±Ã Â¸â„¢Ã Â¸Â§Ã Â¸Â²Ã Â¸â€žÃ Â¸Â¡"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        tj: {
            dayNames: ["Ã‘Â�Ã�ÂºÃ‘Ë†Ã�Â°Ã�Â½Ã�Â±Ã�Âµ", "Ã�Â´Ã‘Æ’Ã‘Ë†Ã�Â°Ã�Â½Ã�Â±Ã�Âµ", "Ã‘Â�Ã�ÂµÃ‘Ë†Ã�Â°Ã�Â½Ã�Â±Ã�Âµ", "Ã‘â€¡Ã�Â¾Ã‘â‚¬Ã‘Ë†Ã�Â°Ã�Â½Ã�Â±Ã�Âµ", "Ã�Â¿Ã�Â°Ã�Â½Ã’Â·Ã‘Ë†Ã�Â°Ã�Â½Ã�Â±Ã�Âµ", "Ã’Â·Ã‘Æ’Ã�Â¼Ã‘Å Ã�Â°", "Ã‘Ë†Ã�Â°Ã�Â½Ã�Â±Ã�Âµ"],
            dayNamesMin: ["Ã‘Â�Ã�ÂºÃ‘Ë†", "Ã�Â´Ã‘Æ’Ã‘Ë†", "Ã‘Â�Ã�ÂµÃ‘Ë†", "Ã‘â€¡Ã�Â¾Ã‘â‚¬", "Ã�Â¿Ã�Â°Ã�Â½", "Ã’Â·Ã‘Æ’Ã�Â¼", "Ã‘Ë†Ã�Â°Ã�Â½"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã�Â¯Ã�Â½Ã�Â²Ã�Â°Ã‘â‚¬", "Ã�Â¤Ã�ÂµÃ�Â²Ã‘â‚¬Ã�Â°Ã�Â»", "Ã�Å“Ã�Â°Ã‘â‚¬Ã‘â€š", "Ã�Â�Ã�Â¿Ã‘â‚¬Ã�ÂµÃ�Â»", "Ã�Å“Ã�Â°Ã�Â¹", "Ã�ËœÃ‘Å½Ã�Â½", "Ã�ËœÃ‘Å½Ã�Â»", "Ã�Â�Ã�Â²Ã�Â³Ã‘Æ’Ã‘Â�Ã‘â€š", "Ã�Â¡Ã�ÂµÃ�Â½Ã‘â€šÃ‘Â�Ã�Â±Ã‘â‚¬", "Ã�Å¾Ã�ÂºÃ‘â€šÃ‘Â�Ã�Â±Ã‘â‚¬", "Ã�Â�Ã�Â¾Ã‘Â�Ã�Â±Ã‘â‚¬", "Ã�â€�Ã�ÂµÃ�ÂºÃ�Â°Ã�Â±Ã‘â‚¬"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        tr: {
            dayNames: ["Pazar", "Pazartesi", "SalÃ„Â±", "Ãƒâ€¡arÃ…Å¸amba", "PerÃ…Å¸embe", "Cuma", "Cumartesi"],
            dayNamesMin: ["Pz", "Pt", "Sa", "Ãƒâ€¡a", "Pe", "Cu", "Ct"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ocak", "Ã…Å¾ubat", "Mart", "Nisan", "MayÃ„Â±s", "Haziran", "Temmuz", "AÃ„Å¸ustos", "EylÃƒÂ¼l", "Ekim", "KasÃ„Â±m", "AralÃ„Â±k"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        uk: {
            dayNames: ["Ã�Â½Ã�ÂµÃ�Â´Ã‘â€“Ã�Â»Ã‘Â�", "Ã�Â¿Ã�Â¾Ã�Â½Ã�ÂµÃ�Â´Ã‘â€“Ã�Â»Ã�Â¾Ã�Âº", "Ã�Â²Ã‘â€“Ã�Â²Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â¾Ã�Âº", "Ã‘Â�Ã�ÂµÃ‘â‚¬Ã�ÂµÃ�Â´Ã�Â°", "Ã‘â€¡Ã�ÂµÃ‘â€šÃ�Â²Ã�ÂµÃ‘â‚¬", "Ã�Â¿Ã¢â‚¬â„¢Ã‘Â�Ã‘â€šÃ�Â½Ã�Â¸Ã‘â€ Ã‘Â�", "Ã‘Â�Ã‘Æ’Ã�Â±Ã�Â¾Ã‘â€šÃ�Â°"],
            dayNamesMin: ["Ã�Â½Ã�ÂµÃ�Â´", "Ã�Â¿Ã�Â½Ã�Â´", "Ã�Â²Ã‘â€“Ã�Â²", "Ã‘Â�Ã‘â‚¬Ã�Â´", "Ã‘â€¡Ã‘â€šÃ�Â²", "Ã�Â¿Ã‘â€šÃ�Â½", "Ã‘Â�Ã�Â±Ã‘â€š"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã�Â¡Ã‘â€“Ã‘â€¡Ã�ÂµÃ�Â½Ã‘Å’", "Ã�â€ºÃ‘Å½Ã‘â€šÃ�Â¸Ã�Â¹", "Ã�â€˜Ã�ÂµÃ‘â‚¬Ã�ÂµÃ�Â·Ã�ÂµÃ�Â½Ã‘Å’", "Ã�Å¡Ã�Â²Ã‘â€“Ã‘â€šÃ�ÂµÃ�Â½Ã‘Å’", "Ã�Â¢Ã‘â‚¬Ã�Â°Ã�Â²Ã�ÂµÃ�Â½Ã‘Å’", "Ã�Â§Ã�ÂµÃ‘â‚¬Ã�Â²Ã�ÂµÃ�Â½Ã‘Å’", "Ã�â€ºÃ�Â¸Ã�Â¿Ã�ÂµÃ�Â½Ã‘Å’", "Ã�Â¡Ã�ÂµÃ‘â‚¬Ã�Â¿Ã�ÂµÃ�Â½Ã‘Å’", "Ã�â€™Ã�ÂµÃ‘â‚¬Ã�ÂµÃ‘Â�Ã�ÂµÃ�Â½Ã‘Å’", "Ã�â€“Ã�Â¾Ã�Â²Ã‘â€šÃ�ÂµÃ�Â½Ã‘Å’", "Ã�â€ºÃ�Â¸Ã‘Â�Ã‘â€šÃ�Â¾Ã�Â¿Ã�Â°Ã�Â´", "Ã�â€œÃ‘â‚¬Ã‘Æ’Ã�Â´Ã�ÂµÃ�Â½Ã‘Å’"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        vi: {
            dayNames: ["ChÃ¡Â»Â§ NhÃ¡Âºt", "ThÃ¡Â»Â© Hai", "ThÃ¡Â»Â© Ba", "ThÃ¡Â»Â© TÃ†Â°", "ThÃ¡Â»Â© NÃ„Æ’m", "ThÃ¡Â»Â© SÃƒÂ¡u", "ThÃ¡Â»Â© BÃ¡ÂºÂ£y"],
            dayNamesMin: ["CN", "T2", "T3", "T4", "T5", "T6", "T7"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["ThÃƒÂ¡ng MÃ¡Â»â„¢t", "ThÃƒÂ¡ng Hai", "ThÃƒÂ¡ng Ba", "ThÃƒÂ¡ng TÃ†Â°", "ThÃƒÂ¡ng NÃ„Æ’m", "ThÃƒÂ¡ng SÃƒÂ¡u", "ThÃƒÂ¡ng BÃ¡ÂºÂ£y", "ThÃƒÂ¡ng TÃƒÂ¡m", "ThÃƒÂ¡ng ChÃƒn", "ThÃƒÂ¡ng MÃ†Â°Ã¡Â»Â�i", "ThÃƒÂ¡ng MÃ†Â°Ã¡Â»Â�i MÃ¡Â»â„¢t", "ThÃƒÂ¡ng MÃ†Â°Ã¡Â»Â�i Hai"],
            showMonthAfterYear: false,
            yearSuffix: ""
        },
        "zh-CN": {
            dayNames: ["Ã¦ËœÅ¸Ã¦Å“Å¸Ã¦â€”Â¥", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤Â¸â‚¬", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤ÂºÅ’", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤Â¸â€°", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¥â€ºâ€º", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤Âºâ€�", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¥â€¦"],
            dayNamesMin: ["Ã¥â€˜Â¨Ã¦â€”Â¥", "Ã¥â€˜Â¨Ã¤Â¸â‚¬", "Ã¥â€˜Â¨Ã¤ÂºÅ’", "Ã¥â€˜Â¨Ã¤Â¸â€°", "Ã¥â€˜Â¨Ã¥â€ºâ€º", "Ã¥â€˜Â¨Ã¤Âºâ€�", "Ã¥â€˜Â¨Ã¥â€¦"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã¤Â¸â‚¬Ã¦Å“Ë†", "Ã¤ÂºÅ’Ã¦Å“Ë†", "Ã¤Â¸â€°Ã¦Å“Ë†", "Ã¥â€ºâ€ºÃ¦Å“Ë†", "Ã¤Âºâ€�Ã¦Å“Ë†", "Ã¥â€¦Ã¦Å“Ë†", "Ã¤Â¸Æ’Ã¦Å“Ë†", "Ã¥â€¦Â«Ã¦Å“Ë†", "Ã¤Â¹Â�Ã¦Å“Ë†", "Ã¥Â�Â�Ã¦Å“Ë†", "Ã¥Â�Â�Ã¤Â¸â‚¬Ã¦Å“Ë†", "Ã¥Â�Â�Ã¤ÂºÅ’Ã¦Å“Ë†"],
            showMonthAfterYear: true,
            yearSuffix: "Ã¥Â¹Â´"
        },
        "zh-HK": {
            dayNames: ["Ã¦ËœÅ¸Ã¦Å“Å¸Ã¦â€”Â¥", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤Â¸â‚¬", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤ÂºÅ’", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤Â¸â€°", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¥â€ºâ€º", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤Âºâ€�", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¥â€¦"],
            dayNamesMin: ["Ã¥â€˜Â¨Ã¦â€”Â¥", "Ã¥â€˜Â¨Ã¤Â¸â‚¬", "Ã¥â€˜Â¨Ã¤ÂºÅ’", "Ã¥â€˜Â¨Ã¤Â¸â€°", "Ã¥â€˜Â¨Ã¥â€ºâ€º", "Ã¥â€˜Â¨Ã¤Âºâ€�", "Ã¥â€˜Â¨Ã¥â€¦"],
            firstDay: 0,
            isRTL: false,
            monthNames: ["Ã¤Â¸â‚¬Ã¦Å“Ë†", "Ã¤ÂºÅ’Ã¦Å“Ë†", "Ã¤Â¸â€°Ã¦Å“Ë†", "Ã¥â€ºâ€ºÃ¦Å“Ë†", "Ã¤Âºâ€�Ã¦Å“Ë†", "Ã¥â€¦Ã¦Å“Ë†", "Ã¤Â¸Æ’Ã¦Å“Ë†", "Ã¥â€¦Â«Ã¦Å“Ë†", "Ã¤Â¹Â�Ã¦Å“Ë†", "Ã¥Â�Â�Ã¦Å“Ë†", "Ã¥Â�Â�Ã¤Â¸â‚¬Ã¦Å“Ë†", "Ã¥Â�Â�Ã¤ÂºÅ’Ã¦Å“Ë†"],
            showMonthAfterYear: true,
            yearSuffix: "Ã¥Â¹Â´"
        },
        "zh-TW": {
            dayNames: ["Ã¦ËœÅ¸Ã¦Å“Å¸Ã¦â€”Â¥", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤Â¸â‚¬", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤ÂºÅ’", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤Â¸â€°", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¥â€ºâ€º", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¤Âºâ€�", "Ã¦ËœÅ¸Ã¦Å“Å¸Ã¥â€¦"],
            dayNamesMin: ["Ã¥â€˜Â¨Ã¦â€”Â¥", "Ã¥â€˜Â¨Ã¤Â¸â‚¬", "Ã¥â€˜Â¨Ã¤ÂºÅ’", "Ã¥â€˜Â¨Ã¤Â¸â€°", "Ã¥â€˜Â¨Ã¥â€ºâ€º", "Ã¥â€˜Â¨Ã¤Âºâ€�", "Ã¥â€˜Â¨Ã¥â€¦"],
            firstDay: 1,
            isRTL: false,
            monthNames: ["Ã¤Â¸â‚¬Ã¦Å“Ë†", "Ã¤ÂºÅ’Ã¦Å“Ë†", "Ã¤Â¸â€°Ã¦Å“Ë†", "Ã¥â€ºâ€ºÃ¦Å“Ë†", "Ã¤Âºâ€�Ã¦Å“Ë†", "Ã¥â€¦Ã¦Å“Ë†", "Ã¤Â¸Æ’Ã¦Å“Ë†", "Ã¥â€¦Â«Ã¦Å“Ë†", "Ã¤Â¹Â�Ã¦Å“Ë†", "Ã¥Â�Â�Ã¦Å“Ë†", "Ã¥Â�Â�Ã¤Â¸â‚¬Ã¦Å“Ë†", "Ã¥Â�Â�Ã¤ÂºÅ’Ã¦Å“Ë†"],
            showMonthAfterYear: true,
            yearSuffix: "Ã¥Â¹Â´"
        }
    };
    a.fn.datePicker = function (c) {
        return (new AJS.DatePicker(this, c))
    }
}(jQuery));
AJS.dropDown = function (n, e) {
    var w = null,
        k = [],
        s = false,
        h = AJS.$(document),
        c = {
            item: "li:has(a)",
            activeClass: "active",
            alignment: "right",
            displayHandler: function (i) {
                return i.name
            },
            escapeHandler: function () {
                this.hide("escape");
                return false
            },
            hideHandler: function () {},
            moveHandler: function (j, i) {},
            useDisabled: false
        };
    AJS.$.extend(c, e);
    c.alignment = {
        left: "left",
        right: "right"
    }[c.alignment.toLowerCase()] || "left";
    if (n && n.jquery) {
        w = n
    } else {
        if (typeof n == "string") {
            w = AJS.$(n)
        } else {
            if (n && n.constructor == Array) {
                w = AJS("div").addClass("aui-dropdown").toggleClass("hidden", !! c.isHiddenByDefault);
                for (var r = 0, m = n.length; r < m; r++) {
                    var l = AJS("ol");
                    for (var q = 0, u = n[r].length; q < u; q++) {
                        var o = AJS("li");
                        var g = n[r][q];
                        if (g.href) {
                            o.append(AJS("a").html("<span>" + c.displayHandler(g) + "</span>").attr({
                                href: g.href
                            }).addClass(g.className));
                            AJS.$.data(AJS.$("a > span", o)[0], "properties", g)
                        } else {
                            o.html(g.html).addClass(g.className)
                        } if (g.icon) {
                            o.prepend(AJS("img").attr("src", g.icon))
                        }
                        if (g.insideSpanIcon) {
                            o.children("a").prepend(AJS("span").attr("class", "icon"))
                        }
                        AJS.$.data(o[0], "properties", g);
                        l.append(o)
                    }
                    if (r == m - 1) {
                        l.addClass("last")
                    }
                    w.append(l)
                }
                AJS.$("body").append(w)
            } else {
                throw new Error("AJS.dropDown function was called with illegal parameter. Should be AJS.$ object, AJS.$ selector or array.")
            }
        }
    }
    var f = function () {
        p(+1)
    };
    var v = function () {
        p(-1)
    };
    var p = function (z) {
        var y = !s,
            i = AJS.dropDown.current.$[0],
            j = AJS.dropDown.current.links,
            A = i.focused;
        s = true;
        if (j.length === 0) {
            return
        }
        i.focused = (typeof A === "number") ? A : -1;
        if (!AJS.dropDown.current) {
            AJS.log("move - not current, aborting");
            return true
        }
        i.focused += z;
        if (i.focused < 0) {
            i.focused = j.length - 1
        } else {
            if (i.focused >= j.length) {
                i.focused = 0
            }
        }
        c.moveHandler(AJS.$(j[i.focused]), z < 0 ? "up" : "down");
        if (y && j.length) {
            AJS.$(j[i.focused]).addClass(c.activeClass);
            s = false
        } else {
            if (!j.length) {
                s = false
            }
        }
    };
    var x = function (y) {
        if (!AJS.dropDown.current) {
            return true
        }
        var z = y.which,
            i = AJS.dropDown.current.$[0],
            j = AJS.dropDown.current.links;
        AJS.dropDown.current.cleanActive();
        switch (z) {
        case 40:
            f();
            break;
        case 38:
            v();
            break;
        case 27:
            return c.escapeHandler.call(AJS.dropDown.current, y);
        case 13:
            if (i.focused >= 0) {
                if (!c.selectionHandler) {
                    if (AJS.$(j[i.focused]).attr("nodeName") != "a") {
                        return AJS.$("a", j[i.focused]).trigger("focus")
                    } else {
                        return AJS.$(j[i.focused]).trigger("focus")
                    }
                } else {
                    return c.selectionHandler.call(AJS.dropDown.current, y, AJS.$(j[i.focused]))
                }
            }
            return true;
        default:
            if (j.length) {
                AJS.$(j[i.focused]).addClass(c.activeClass)
            }
            return true
        }
        y.stopPropagation();
        y.preventDefault();
        return false
    };
    var a = function (i) {
        if (!((i && i.which && (i.which == 3)) || (i && i.button && (i.button == 2)) || false)) {
            if (AJS.dropDown.current) {
                AJS.dropDown.current.hide("click")
            }
        }
    };
    var d = function (j) {
        return function () {
            if (!AJS.dropDown.current) {
                return
            }
            AJS.dropDown.current.cleanFocus();
            this.originalClass = this.className;
            AJS.$(this).addClass(c.activeClass);
            AJS.dropDown.current.$[0].focused = j
        }
    };
    var t = function (i) {
        if (i.button || i.metaKey || i.ctrlKey || i.shiftKey) {
            return true
        }
        if (AJS.dropDown.current && c.selectionHandler) {
            c.selectionHandler.call(AJS.dropDown.current, i, AJS.$(this))
        }
    };
    var b = function (j) {
        var i = false;
        if (j.data("events")) {
            AJS.$.each(j.data("events"), function (y, z) {
                AJS.$.each(z, function (B, A) {
                    if (t === A) {
                        i = true;
                        return false
                    }
                })
            })
        }
        return i
    };
    w.each(function () {
        var i = this,
            y = AJS.$(this),
            z = {};
        var j = {
            reset: function () {
                z = AJS.$.extend(z, {
                    $: y,
                    links: AJS.$(c.item || "li:has(a)", i),
                    cleanActive: function () {
                        if (i.focused + 1 && z.links.length) {
                            AJS.$(z.links[i.focused]).removeClass(c.activeClass)
                        }
                    },
                    cleanFocus: function () {
                        z.cleanActive();
                        i.focused = -1
                    },
                    moveDown: f,
                    moveUp: v,
                    moveFocus: x,
                    getFocusIndex: function () {
                        return (typeof i.focused == "number") ? i.focused : -1
                    }
                });
                z.links.each(function (A) {
                    var B = AJS.$(this);
                    if (!b(B)) {
                        B.hover(d(A), z.cleanFocus);
                        B.click(t)
                    }
                })
            },
            appear: function (A) {
                if (A) {
                    y.removeClass("hidden");
                    y.addClass("aui-dropdown-" + c.alignment)
                } else {
                    y.addClass("hidden")
                }
            },
            fade: function (A) {
                if (A) {
                    y.fadeIn("fast")
                } else {
                    y.fadeOut("fast")
                }
            },
            scroll: function (A) {
                if (A) {
                    y.slideDown("fast")
                } else {
                    y.slideUp("fast")
                }
            }
        };
        z.reset = j.reset;
        z.reset();
        z.addControlProcess = function (B, A) {
            AJS.$.aop.around({
                target: this,
                method: B
            }, A)
        };
        z.addCallback = function (B, A) {
            return AJS.$.aop.after({
                target: this,
                method: B
            }, A)
        };
        z.show = function (A) {
            if (c.useDisabled && this.$.closest(".aui-dd-parent").hasClass("disabled")) {
                return
            }
            this.alignment = c.alignment;
            a();
            AJS.dropDown.current = this;
            this.method = A || this.method || "appear";
            this.timer = setTimeout(function () {
                h.click(a)
            }, 0);
            h.keydown(x);
            if (c.firstSelected && this.links[0]) {
                d(0).call(this.links[0])
            }
            AJS.$(i.offsetParent).css({
                zIndex: 2000
            });
            j[this.method](true);
            AJS.$(document).trigger("showLayer", ["dropdown", AJS.dropDown.current])
        };
        z.hide = function (A) {
            this.method = this.method || "appear";
            AJS.$(y.get(0).offsetParent).css({
                zIndex: ""
            });
            this.cleanFocus();
            j[this.method](false);
            h.unbind("click", a).unbind("keydown", x);
            AJS.$(document).trigger("hideLayer", ["dropdown", AJS.dropDown.current]);
            AJS.dropDown.current = null;
            return A
        };
        z.addCallback("reset", function () {
            if (c.firstSelected && this.links[0]) {
                d(0).call(this.links[0])
            }
        });
        if (!AJS.dropDown.iframes) {
            AJS.dropDown.iframes = []
        }
        AJS.dropDown.createShims = function () {
            AJS.$("iframe").each(function (A) {
                var B = this;
                if (!B.shim) {
                    B.shim = AJS.$("<div />").addClass("shim hidden").appendTo("body");
                    AJS.dropDown.iframes.push(B)
                }
            });
            return arguments.callee
        }();
        z.addCallback("show", function () {
            AJS.$(AJS.dropDown.iframes).each(function () {
                var A = AJS.$(this);
                if (A.is(":visible")) {
                    var B = A.offset();
                    B.height = A.height();
                    B.width = A.width();
                    this.shim.css({
                        left: B.left + "px",
                        top: B.top + "px",
                        height: B.height + "px",
                        width: B.width + "px"
                    }).removeClass("hidden")
                }
            })
        });
        z.addCallback("hide", function () {
            AJS.$(AJS.dropDown.iframes).each(function () {
                this.shim.addClass("hidden")
            });
            c.hideHandler()
        });
        if (AJS.$.browser.msie && ~~(AJS.$.browser.version) < 9) {
            (function () {
                var A = function () {
                    if (this.$.is(":visible")) {
                        if (!this.iframeShim) {
                            this.iframeShim = AJS.$('<iframe class="dropdown-shim" src="javascript:false;" frameBorder="0" />').insertBefore(this.$)
                        }
                        this.iframeShim.css({
                            display: "block",
                            top: this.$.css("top"),
                            width: this.$.outerWidth() + "px",
                            height: this.$.outerHeight() + "px"
                        });
                        if (c.alignment == "left") {
                            this.iframeShim.css({
                                left: "0px"
                            })
                        } else {
                            this.iframeShim.css({
                                right: "0px"
                            })
                        }
                    }
                };
                z.addCallback("reset", A);
                z.addCallback("show", A);
                z.addCallback("hide", function () {
                    if (this.iframeShim) {
                        this.iframeShim.css({
                            display: "none"
                        })
                    }
                })
            })()
        }
        k.push(z)
    });
    return k
};
AJS.dropDown.getAdditionalPropertyValue = function (d, a) {
    var c = d[0];
    if (!c || (typeof c.tagName != "string") || c.tagName.toLowerCase() != "li") {
        AJS.log("AJS.dropDown.getAdditionalPropertyValue : item passed in should be an LI element wrapped by jQuery")
    }
    var b = AJS.$.data(c, "properties");
    return b ? b[a] : null
};
AJS.dropDown.removeAllAdditionalProperties = function (a) {};
AJS.dropDown.Standard = function (h) {
    var c = [],
        g, b = {
            selector: ".aui-dd-parent",
            dropDown: ".aui-dropdown",
            trigger: ".aui-dd-trigger"
        };
    AJS.$.extend(b, h);
    var f = function (i, l, k, j) {
        AJS.$.extend(j, {
            trigger: i
        });
        l.addClass("dd-allocated");
        k.addClass("hidden");
        if (b.isHiddenByDefault == false) {
            j.show()
        }
        j.addCallback("show", function () {
            l.addClass("active")
        });
        j.addCallback("hide", function () {
            l.removeClass("active")
        })
    };
    var a = function (k, i, l, j) {
        if (j != AJS.dropDown.current) {
            l.css({
                top: i.outerHeight()
            });
            j.show();
            k.stopImmediatePropagation()
        }
        k.preventDefault()
    };
    if (b.useLiveEvents) {
        var d = [];
        var e = [];
        AJS.$(b.trigger).live("click", function (l) {
            var i = AJS.$(this);
            var n, m, j;
            var k;
            if ((k = AJS.$.inArray(this, d)) >= 0) {
                var o = e[k];
                n = o.parent;
                m = o.dropdown;
                j = o.ddcontrol
            } else {
                n = i.closest(b.selector);
                m = n.find(b.dropDown);
                if (m.length === 0) {
                    return
                }
                j = AJS.dropDown(m, b)[0];
                if (!j) {
                    return
                }
                d.push(this);
                o = {
                    parent: n,
                    dropdown: m,
                    ddcontrol: j
                };
                f(i, n, m, j);
                e.push(o)
            }
            a(l, i, m, j)
        })
    } else {
        if (this instanceof AJS.$) {
            g = this
        } else {
            g = AJS.$(b.selector)
        }
        g = g.not(".dd-allocated").filter(":has(" + b.dropDown + ")").filter(":has(" + b.trigger + ")");
        g.each(function () {
            var l = AJS.$(this),
                k = AJS.$(b.dropDown, this),
                i = AJS.$(b.trigger, this),
                j = AJS.dropDown(k, b)[0];
            AJS.$.extend(j, {
                trigger: i
            });
            f(i, l, k, j);
            i.click(function (m) {
                a(m, i, k, j)
            });
            c.push(j)
        })
    }
    return c
};
AJS.dropDown.Ajax = function (c) {
    var b, a = {
            cache: true
        };
    AJS.$.extend(a, c || {});
    b = AJS.dropDown.Standard.call(this, a);
    AJS.$(b).each(function () {
        var d = this;
        AJS.$.extend(d, {
            getAjaxOptions: function (e) {
                var f = function (g) {
                    if (a.formatResults) {
                        g = a.formatResults(g)
                    }
                    if (a.cache) {
                        d.cache.set(d.getAjaxOptions(), g)
                    }
                    d.refreshSuccess(g)
                };
                if (a.ajaxOptions) {
                    if (AJS.$.isFunction(a.ajaxOptions)) {
                        return AJS.$.extend(a.ajaxOptions.call(d), {
                            success: f
                        })
                    } else {
                        return AJS.$.extend(a.ajaxOptions, {
                            success: f
                        })
                    }
                }
                return AJS.$.extend(e, {
                    success: f
                })
            },
            refreshSuccess: function (e) {
                this.$.html(e)
            },
            cache: function () {
                var e = {};
                return {
                    get: function (f) {
                        var g = f.data || "";
                        return e[(f.url + g).replace(/[\?\&]/gi, "")]
                    },
                    set: function (f, g) {
                        var h = f.data || "";
                        e[(f.url + h).replace(/[\?\&]/gi, "")] = g
                    },
                    reset: function () {
                        e = {}
                    }
                }
            }(),
            show: function (e) {
                return function (f) {
                    if (a.cache && !! d.cache.get(d.getAjaxOptions())) {
                        d.refreshSuccess(d.cache.get(d.getAjaxOptions()));
                        e.call(d)
                    } else {
                        AJS.$(AJS.$.ajax(d.getAjaxOptions())).throbber({
                            target: d.$,
                            end: function () {
                                d.reset()
                            }
                        });
                        e.call(d);
                        if (d.iframeShim) {
                            d.iframeShim.hide()
                        }
                    }
                }
            }(d.show),
            resetCache: function () {
                d.cache.reset()
            }
        });
        d.addCallback("refreshSuccess", function () {
            d.reset()
        })
    });
    return b
};
AJS.$.fn.dropDown = function (b, a) {
    b = (b || "Standard").replace(/^([a-z])/, function (c) {
        return c.toUpperCase()
    });
    return AJS.dropDown[b].call(this, a)
};
(function (g) {
    var e = g(document),
        j = (AJS.$.browser.msie && parseInt(AJS.$.browser.version, 10) == 8);
    var c = null;
    var b = (function () {
        var n = false;

        function l(q) {
            if (!n && q.which === 1) {
                n = true;
                e.bind("mouseup mouseleave", m);
                g(this).trigger("aui-button-invoke")
            }
        }

        function m() {
            e.unbind("mouseup mouseleave", m);
            setTimeout(function () {
                n = false
            }, 0)
        }

        function p() {
            if (!n) {
                g(this).trigger("aui-button-invoke")
            }
        }

        function o(q) {
            q.preventDefault()
        }
        if (typeof document.addEventListener === "undefined") {
            return {
                click: p,
                "click selectstart": o,
                mousedown: function (s) {
                    l.call(this, s);
                    var t = this;
                    var r = document.activeElement;
                    if (r !== null) {
                        r.attachEvent("onbeforedeactivate", q);
                        setTimeout(function () {
                            r.detachEvent("onbeforedeactivate", q)
                        }, 0)
                    }

                    function q(u) {
                        switch (u.toElement) {
                        case null:
                        case t:
                        case document.body:
                        case document.documentElement:
                            u.returnValue = false
                        }
                    }
                }
            }
        }
        return {
            click: p,
            "click mousedown": o,
            mousedown: l
        }
    })();
    var a = {
        "aui-button-invoke": function (H, r) {
            r = g.extend({
                selectFirst: true
            }, r);
            var o = i(this);
            var n = g(this).addClass("active");
            var R = n.hasClass("aui-dropdown2-sub-trigger");
            var M = o.parent()[0];
            var P = o.next()[0];
            var D = g(this).attr("data-dropdown2-hide-location");
            if (D) {
                var z = document.getElementById(D);
                if (z) {
                    M = g(z);
                    P = undefined
                } else {
                    throw new Error("The specified data-dropdown2-hide-location id doesn't exist")
                }
            }
            var t = r.$menu || n.closest(".aui-dropdown2-trigger-group");
            if (R) {
                var E = n.closest(".aui-dropdown2");
                o.addClass(E.attr("class")).addClass("aui-dropdown2-sub-menu")
            }
            var s = {
                click: function (T) {
                    var S = g(this);
                    if (!p(S)) {
                        return
                    }
                    if (!S.hasClass("interactive")) {
                        L()
                    }
                    if (v(S)) {
                        F(S, {
                            selectFirst: false
                        });
                        T.preventDefault()
                    }
                },
                mousemove: function () {
                    var T = g(this);
                    var S = u(T);
                    if (S) {
                        F(T, {
                            selectFirst: false
                        })
                    }
                }
            };
            var N = {
                "click focusin mousedown": function (S) {
                    var T = S.target;
                    if (!d(T, o[0]) && !d(T, n[0])) {
                        L()
                    }
                },
                keydown: function (U) {
                    if (U.shiftKey && U.keyCode == 9) {
                        J(-1)
                    } else {
                        switch (U.keyCode) {
                        case 13:
                            var T = y();
                            if (v(T)) {
                                F(T)
                            } else {
                                k(T[0])
                            }
                            break;
                        case 27:
                            A();
                            break;
                        case 37:
                            var T = y();
                            if (v(T)) {
                                var S = i(T);
                                if (S.is(":visible")) {
                                    o.trigger("aui-dropdown2-step-out");
                                    return
                                }
                            }
                            if (R) {
                                A()
                            } else {
                                Q(-1)
                            }
                            break;
                        case 38:
                            J(-1);
                            break;
                        case 39:
                            var T = y();
                            if (v(T)) {
                                F(T)
                            } else {
                                Q(1)
                            }
                            break;
                        case 40:
                            J(1);
                            break;
                        case 9:
                            J(1);
                            break;
                        default:
                            return
                        }
                    }
                    U.preventDefault()
                }
            };

            function C(S, T) {
                S.each(function () {
                    var U = g(this);
                    U.attr("role", T);
                    if (U.hasClass("checked")) {
                        U.attr("aria-checked", "true");
                        if (T == "radio") {
                            U.closest("ul").attr("role", "radiogroup")
                        }
                    } else {
                        U.attr("aria-checked", "false")
                    }
                })
            }
            n.attr("aria-controls", n.attr("aria-owns"));
            if (j) {
                o.removeClass("aui-dropdown2-tailed")
            }
            o.find(".disabled").attr("aria-disabled", "true");
            o.find("li.hidden > a").addClass("disabled").attr("aria-disabled", "true");
            C(o.find(".aui-dropdown2-checkbox"), "checkbox");
            C(o.find(".aui-dropdown2-radio"), "radio");

            function q() {
                var ab = n.offset();
                var af = n.outerWidth();
                var ae = o.outerWidth();
                var aa = g("body").outerWidth(true);
                var T = Math.max(parseInt(o.css("min-width"), 10), af);
                var U = n.data("container") || false;
                var ad = "left";
                if (j) {
                    var V = parseInt(o.css("border-left-width"), 10) + parseInt(o.css("border-right-width"), 10);
                    ae = ae - V;
                    T = T - V
                }
                if (!R) {
                    o.css("min-width", T + "px")
                }
                var W = ab.left,
                    ac = ab.top + n.outerHeight();
                if (R) {
                    var Y = 3;
                    W = ab.left + E.outerWidth() - Y;
                    ac = ab.top
                }
                if (aa < W + ae && ae <= W + af) {
                    W = ab.left + af - ae;
                    if (R) {
                        W = ab.left - ae
                    }
                    ad = "right"
                }
                if (U) {
                    var S = n.closest(U),
                        Z = n.offset().left + n.outerWidth(),
                        X = Z + ae;
                    if (T >= ae) {
                        ae = T
                    }
                    if (X > Z) {
                        W = Z - ae;
                        ad = "right"
                    }
                    if (j) {
                        W -= V
                    }
                }
                o.attr({
                    "data-dropdown2-alignment": ad,
                    "aria-hidden": "false"
                }).css({
                    display: "block",
                    left: W + "px",
                    top: ac + "px"
                });
                o.appendTo(document.body)
            }
            q();
            if (n.hasClass("toolbar-trigger")) {
                o.addClass("aui-dropdown2-in-toolbar")
            }
            if (n.parent().hasClass("aui-buttons")) {
                o.addClass("aui-dropdown2-in-buttons")
            }
            if (n.parents().hasClass("aui-header")) {
                o.addClass("aui-dropdown2-in-header")
            }
            o.trigger("aui-dropdown2-show", r);
            if (r.selectFirst) {
                m()
            }
            I("on");

            function A() {
                l();
                I("off");
                setTimeout(function () {
                    o.css("display", "none").css("min-width", "").insertAfter(n).attr("aria-hidden", "true");
                    if (!R) {
                        n.removeClass("active")
                    }
                    y().removeClass("active");
                    o.removeClass("aui-dropdown2-in-toolbar");
                    o.removeClass("aui-dropdown2-in-buttons");
                    if (P) {
                        o.insertBefore(P)
                    } else {
                        o.appendTo(M)
                    }
                    o.trigger("aui-dropdown2-hide")
                }, 0)
            }

            function L() {
                A();
                if (R) {
                    E.trigger("aui-dropdown2-hide-all")
                }
            }

            function G(S) {
                if (R && S.target === E[0]) {
                    A()
                }
            }

            function p(S) {
                return !S.is(".disabled, [aria-disabled=true]")
            }

            function v(S) {
                return S.hasClass("aui-dropdown2-sub-trigger")
            }

            function F(S, T) {
                if (!v(S)) {
                    return
                }
                T = g.extend({}, T, {
                    $menu: t
                });
                var U = i(S);
                if (U.is(":visible")) {
                    U.trigger("aui-dropdown2-select-first")
                } else {
                    S.trigger("aui-button-invoke", T)
                }
            }

            function y() {
                return o.find("a.active")
            }
            var w = null;

            function u(S) {
                if (w && w[0] === S[0]) {
                    return false
                }
                w = S;
                y().removeClass("active");
                if (p(S)) {
                    S.addClass("active")
                }
                o.trigger("aui-dropdown2-item-selected");
                x();
                return true
            }

            function m() {
                u(o.find("a:not(.disabled)").first())
            }

            function J(T) {
                var S = o.find("> ul > li > a, > .aui-dropdown2-section > ul > li > a").not(".disabled");
                u(K(S, T, true))
            }

            function B(S) {
                if (S.length > 0) {
                    L();
                    S.trigger("aui-button-invoke")
                }
            }

            function Q(S) {
                B(K(t.find(".aui-dropdown2-trigger").not(".disabled, [aria-disabled=true], .aui-dropdown2-sub-trigger"), S, false))
            }

            function K(U, V, T) {
                var S = U.index(U.filter(".active"));
                S += (S < 0 && V < 0) ? 1 : 0;
                S += V;
                if (T) {
                    S %= U.length
                } else {
                    if (S < 0) {
                        S = U.length
                    }
                }
                return U.eq(S)
            }

            function O() {
                B(g(this))
            }

            function l() {
                if (c === N) {
                    e.unbind(N);
                    c = null
                }
            }

            function x() {
                if (c === N) {
                    return
                }
                e.unbind(c);
                e.bind(N);
                c = N
            }

            function I(T) {
                var U = "bind";
                var S = "delegate";
                if (T !== "on") {
                    U = "unbind";
                    S = "undelegate"
                }
                if (!R) {
                    t[S](".aui-dropdown2-trigger:not(.active)", "mousemove", O);
                    n[U]("aui-button-invoke", A)
                } else {
                    E[U]("aui-dropdown2-hide aui-dropdown2-item-selected aui-dropdown2-step-out", G)
                }
                o[U]("aui-dropdown2-hide-all", L);
                o[S]("a", s);
                o[U]("aui-dropdown2-hide", x);
                o[U]("aui-dropdown2-select-first", m)
            }
        },
        mousedown: function (l) {
            if (l.which === 1) {
                g(this).bind(f)
            }
        }
    };
    var f = {
        mouseleave: function () {
            e.bind(h)
        },
        "mouseup mouseleave": function () {
            g(this).unbind(f)
        }
    };
    var h = {
        mouseup: function (l) {
            var m = g(l.target).closest(".aui-dropdown2 a, .aui-dropdown2-trigger")[0];
            if (m) {
                setTimeout(function () {
                    k(m)
                }, 0)
            }
        },
        "mouseup mouseleave": function () {
            g(this).unbind(h)
        }
    };

    function k(l) {
        if (l.click) {
            l.click()
        } else {
            var m = document.createEvent("MouseEvents");
            m.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
            l.dispatchEvent(m)
        }
    }

    function d(m, l) {
        return (m === l) || g.contains(l, m)
    }

    function i(n) {
        if (!(n instanceof AJS.$)) {
            n = g(n)
        }
        var o = n.attr("aria-owns"),
            l = n.attr("aria-haspopup"),
            m = document.getElementById(o);
        if (m) {
            return g(m)
        } else {
            if (!o) {
                throw new Error("Dropdown 2 trigger required attribute not set: aria-owns")
            }
            if (!l) {
                throw new Error("Dropdown 2 trigger required attribute not set: aria-haspopup")
            }
            if (!m) {
                throw new Error("Dropdown 2 trigger aria-owns attr set to nonexistent id: " + o)
            }
            throw new Error("Dropdown 2 trigger unknown error. I don't know what you did, but there's smoke everywhere. Consult the documentation.")
        }
    }
    e.delegate(".aui-dropdown2-trigger", b);
    e.delegate(".aui-dropdown2-trigger:not(.active):not([aria-disabled=true]),.aui-dropdown2-sub-trigger:not([aria-disabled=true])", a);
    e.delegate(".aui-dropdown2-checkbox:not(.disabled)", "click", function () {
        var l = g(this);
        if (l.hasClass("checked")) {
            l.removeClass("checked").attr("aria-checked", "false");
            l.trigger("aui-dropdown2-item-uncheck")
        } else {
            l.addClass("checked").attr("aria-checked", "true");
            l.trigger("aui-dropdown2-item-check")
        }
    });
    e.delegate(".aui-dropdown2-radio:not(.checked):not(.disabled)", "click", function () {
        var l = g(this);
        var m = l.closest("ul").find(".checked");
        m.removeClass("checked").attr("aria-checked", "false").trigger("aui-dropdown2-item-uncheck");
        l.addClass("checked").attr("aria-checked", "true").trigger("aui-dropdown2-item-check")
    });
    e.delegate(".aui-dropdown2 a.disabled", "click", function (l) {
        l.preventDefault()
    })
})(AJS.$);
AJS.bind = function (a, c, b) {
    try {
        if (typeof b === "function") {
            return AJS.$(window).bind(a, c, b)
        } else {
            return AJS.$(window).bind(a, c)
        }
    } catch (d) {
        AJS.log("error while binding: " + d.message)
    }
};
AJS.unbind = function (a, b) {
    try {
        return AJS.$(window).unbind(a, b)
    } catch (c) {
        AJS.log("error while unbinding: " + c.message)
    }
};
AJS.trigger = function (a, c) {
    try {
        return AJS.$(window).trigger(a, c)
    } catch (b) {
        AJS.log("error while triggering: " + b.message)
    }
};
AJS.warnAboutFirebug = function (a) {
    AJS.log("DEPRECATED: please remove all uses of AJS.warnAboutFirebug")
};
AJS.inlineHelp = function () {
    AJS.$(".icon-inline-help").click(function () {
        var a = AJS.$(this).siblings(".field-help");
        if (a.hasClass("hidden")) {
            a.removeClass("hidden")
        } else {
            a.addClass("hidden")
        }
    })
};
(function (a) {
    AJS.InlineDialog = function (t, h, k, i) {
        if (i && i.getArrowAttributes) {
            AJS.log("DEPRECATED: getArrowAttributes - See https://ecosystem.atlassian.net/browse/AUI-1362")
        }
        if (i && i.getArrowPath) {
            AJS.log("DEPRECATED: getArrowPath - See https://ecosystem.atlassian.net/browse/AUI-1362")
        }
        if (typeof h === "undefined") {
            h = String(Math.random()).replace(".", "");
            if (a("#inline-dialog-" + h + ", #arrow-" + h + ", #inline-dialog-shim-" + h).length) {
                throw "GENERATED_IDENTIFIER_NOT_UNIQUE"
            }
        }
        var r = a.extend(false, AJS.InlineDialog.opts, i);
        var e;
        var j;
        var B;
        var n = false;
        var s = false;
        var z = false;
        var A;
        var p;
        var b = a('<div id="inline-dialog-' + h + '" class="aui-inline-dialog"><div class="contents"></div><div id="arrow-' + h + '" class="arrow"></div></div>');
        var g = a("#arrow-" + h, b);
        var y = b.find(".contents");
        y.css("width", r.width + "px");
        y.mouseover(function (C) {
            clearTimeout(j);
            b.unbind("mouseover")
        }).mouseout(function () {
            w()
        });
        var v = function () {
            if (!e) {
                e = {
                    popup: b,
                    hide: function () {
                        w(0)
                    },
                    id: h,
                    show: function () {
                        q()
                    },
                    persistent: r.persistent ? true : false,
                    reset: function () {
                        function E(G, F) {
                            G.css(F.popupCss);
                            if (window.Raphael && i && (i.getArrowPath || i.getArrowAttributes)) {
                                if (F.displayAbove) {
                                    F.arrowCss.top -= AJS.$.browser.msie ? 10 : 9
                                }
                                if (!G.arrowCanvas) {
                                    G.arrowCanvas = Raphael("arrow-" + h, 16, 16)
                                }
                                var H = r.getArrowPath,
                                    I = a.isFunction(H) ? H(F) : H;
                                G.arrowCanvas.path(I).attr(r.getArrowAttributes())
                            } else {
                                if (!g.hasClass("aui-css-arrow")) {
                                    g.addClass("aui-css-arrow")
                                }
                                if (F.displayAbove && !g.hasClass("aui-bottom-arrow")) {
                                    g.addClass("aui-bottom-arrow")
                                } else {
                                    if (!F.displayAbove) {
                                        g.removeClass("aui-bottom-arrow")
                                    }
                                }
                            }
                            g.css(F.arrowCss)
                        }
                        var C = r.calculatePositions(b, p, A, r);
                        E(b, C);
                        b.fadeIn(r.fadeTime, function () {});
                        if (AJS.$.browser.msie && ~~(AJS.$.browser.version) < 10) {
                            var D = a("#inline-dialog-shim-" + h);
                            if (D.length == 0) {
                                a(b).prepend(a('<iframe class = "inline-dialog-shim" id="inline-dialog-shim-' + h + '" frameBorder="0" src="javascript:false;"></iframe>'))
                            }
                            D.css({
                                width: y.outerWidth(),
                                height: y.outerHeight()
                            })
                        }
                    }
                }
            }
            return e
        };
        var q = function () {
            if (b.is(":visible")) {
                return
            }
            B = setTimeout(function () {
                if (!z || !s) {
                    return
                }
                r.addActiveClass && a(t).addClass("active");
                n = true;
                !r.persistent && f();
                AJS.InlineDialog.current = v();
                AJS.$(document).trigger("showLayer", ["inlineDialog", v()]);
                v().reset()
            }, r.showDelay)
        };
        var w = function (C) {
            if (typeof C == "undefined" && r.persistent) {
                return
            }
            s = false;
            if (n && r.preHideCallback.call(b[0].popup)) {
                C = (C == null) ? r.hideDelay : C;
                clearTimeout(j);
                clearTimeout(B);
                if (C != null) {
                    j = setTimeout(function () {
                        u();
                        r.addActiveClass && a(t).removeClass("active");
                        b.fadeOut(r.fadeTime, function () {
                            r.hideCallback.call(b[0].popup)
                        });
                        if (b.arrowCanvas) {
                            b.arrowCanvas.remove();
                            b.arrowCanvas = null
                        }
                        n = false;
                        s = false;
                        AJS.$(document).trigger("hideLayer", ["inlineDialog", v()]);
                        AJS.InlineDialog.current = null;
                        if (!r.cacheContent) {
                            z = false;
                            o = false
                        }
                    }, C)
                }
            }
        };
        var x = function (F, D) {
            var C = a(D);
            r.upfrontCallback.call({
                popup: b,
                hide: function () {
                    w(0)
                },
                id: h,
                show: function () {
                    q()
                }
            });
            b.each(function () {
                if (typeof this.popup != "undefined") {
                    this.popup.hide()
                }
            });
            if (r.closeOthers) {
                AJS.$(".aui-inline-dialog").each(function () {
                    !this.popup.persistent && this.popup.hide()
                })
            }
            p = {
                target: C
            };
            if (!F) {
                A = {
                    x: C.offset().left,
                    y: C.offset().top
                }
            } else {
                A = {
                    x: F.pageX,
                    y: F.pageY
                }
            } if (!n) {
                clearTimeout(B)
            }
            s = true;
            var E = function () {
                o = false;
                z = true;
                r.initCallback.call({
                    popup: b,
                    hide: function () {
                        w(0)
                    },
                    id: h,
                    show: function () {
                        q()
                    }
                });
                q()
            };
            if (!o) {
                o = true;
                if (a.isFunction(k)) {
                    k(y, D, E)
                } else {
                    AJS.$.get(k, function (H, G, I) {
                        y.html(r.responseHandler(H, G, I));
                        z = true;
                        r.initCallback.call({
                            popup: b,
                            hide: function () {
                                w(0)
                            },
                            id: h,
                            show: function () {
                                q()
                            }
                        });
                        q()
                    })
                }
            }
            clearTimeout(j);
            if (!n) {
                q()
            }
            return false
        };
        b[0].popup = v();
        var o = false;
        var m = false;
        var l = function () {
            if (!m) {
                a(r.container).append(b);
                m = true
            }
        };
        if (r.onHover) {
            if (r.useLiveEvents) {
                a(t).live("mousemove", function (C) {
                    l();
                    x(C, this)
                }).live("mouseout", function () {
                    w()
                })
            } else {
                a(t).mousemove(function (C) {
                    l();
                    x(C, this)
                }).mouseout(function () {
                    w()
                })
            }
        } else {
            if (!r.noBind) {
                if (r.useLiveEvents) {
                    a(t).live("click", function (C) {
                        l();
                        x(C, this);
                        return false
                    }).live("mouseout", function () {
                        w()
                    })
                } else {
                    a(t).click(function (C) {
                        l();
                        x(C, this);
                        return false
                    }).mouseout(function () {
                        w()
                    })
                }
            }
        }
        var d = false;
        var c = h + ".inline-dialog-check";
        var f = function () {
            if (!d) {
                a("body").bind("click." + c, function (D) {
                    var C = a(D.target);
                    if (C.closest("#inline-dialog-" + h + " .contents").length === 0) {
                        w(0)
                    }
                });
                d = true
            }
        };
        var u = function () {
            if (d) {
                a("body").unbind("click." + c)
            }
            d = false
        };
        b.show = function (C) {
            if (C) {
                C.stopPropagation()
            }
            l();
            x(null, t)
        };
        b.hide = function () {
            w(0)
        };
        b.refresh = function () {
            if (n) {
                v().reset()
            }
        };
        b.getOptions = function () {
            return r
        };
        return b
    };
    AJS.InlineDialog.opts = {
        onTop: false,
        responseHandler: function (c, b, d) {
            return c
        },
        closeOthers: true,
        isRelativeToMouse: false,
        addActiveClass: true,
        onHover: false,
        useLiveEvents: false,
        noBind: false,
        fadeTime: 100,
        persistent: false,
        hideDelay: 10000,
        showDelay: 0,
        width: 300,
        offsetX: 0,
        offsetY: 10,
        arrowOffsetX: 0,
        container: "body",
        cacheContent: true,
        displayShadow: true,
        preHideCallback: function () {
            return true
        },
        hideCallback: function () {},
        initCallback: function () {},
        upfrontCallback: function () {},
        calculatePositions: function (c, j, t, n) {
            var k;
            var v = "auto";
            var q;
            var g = -7;
            var h;
            var l;
            var u = j.target.offset();
            var b = j.target.outerWidth();
            var e = u.left + b / 2;
            var p = (window.pageYOffset || document.documentElement.scrollTop) + a(window).height();
            var f = 10;
            q = u.top + j.target.outerHeight() + n.offsetY;
            k = u.left + n.offsetX;
            var i = u.top > c.height();
            var d = (q + c.height()) < p;
            l = (!d && i) || (n.onTop && i);
            var m = a(window).width() - (k + c.outerWidth() + f);
            if (l) {
                q = u.top - c.height() - 8;
                g = c.height()
            }
            h = e - k + n.arrowOffsetX;
            if (n.isRelativeToMouse) {
                if (m < 0) {
                    v = f;
                    k = "auto";
                    h = t.x - (a(window).width() - n.width)
                } else {
                    k = t.x - 20;
                    h = t.x - k
                }
            } else {
                if (m < 0) {
                    v = f;
                    k = "auto";
                    var s = a(window).width() - v;
                    var o = s - c.outerWidth();
                    var r = c.find(".arrow").outerWidth();
                    h = e - o - r / 2
                } else {
                    if (n.width <= b / 2) {
                        h = n.width / 2;
                        k = e - n.width / 2
                    }
                }
            }
            return {
                displayAbove: l,
                popupCss: {
                    left: k,
                    right: v,
                    top: q
                },
                arrowCss: {
                    position: "absolute",
                    left: h,
                    right: "auto",
                    top: g
                }
            }
        },
        getArrowPath: function (b) {
            return b.displayAbove ? "M0,8L8,16,16,8" : "M0,8L8,0,16,8"
        },
        getArrowAttributes: function () {
            return {
                fill: "#fff",
                stroke: "#ccc"
            }
        }
    }
})(AJS.$);
(function (a) {
    AJS.keyCode = {
        ALT: 18,
        BACKSPACE: 8,
        CAPS_LOCK: 20,
        COMMA: 188,
        COMMAND: 91,
        COMMAND_LEFT: 91,
        COMMAND_RIGHT: 93,
        CONTROL: 17,
        DELETE: 46,
        DOWN: 40,
        END: 35,
        ENTER: 13,
        ESCAPE: 27,
        HOME: 36,
        INSERT: 45,
        LEFT: 37,
        MENU: 93,
        NUMPAD_ADD: 107,
        NUMPAD_DECIMAL: 110,
        NUMPAD_DIVIDE: 111,
        NUMPAD_ENTER: 108,
        NUMPAD_MULTIPLY: 106,
        NUMPAD_SUBTRACT: 109,
        PAGE_DOWN: 34,
        PAGE_UP: 33,
        PERIOD: 190,
        RIGHT: 39,
        SHIFT: 16,
        SPACE: 32,
        TAB: 9,
        UP: 38,
        WINDOWS: 91
    }
}(AJS.$));
(function () {
    var a = 500;
    var c = 5000;
    var b = 100;
    AJS.messages = {
        setup: function () {
            AJS.messages.createMessage("generic");
            AJS.messages.createMessage("error");
            AJS.messages.createMessage("warning");
            AJS.messages.createMessage("info");
            AJS.messages.createMessage("success");
            AJS.messages.createMessage("hint");
            AJS.messages.makeCloseable();
            AJS.messages.makeFadeout()
        },
        makeCloseable: function (d) {
            AJS.$(d || "div.aui-message.closeable").each(function () {
                var f = AJS.$(this),
                    e = AJS.$('<span class="aui-icon icon-close" role="button" tabindex="0"></span>').click(function () {
                        f.closeMessage()
                    }).keypress(function (g) {
                        if ((g.which === AJS.keyCode.ENTER) || (g.which === AJS.keyCode.SPACE)) {
                            f.closeMessage();
                            g.preventDefault()
                        }
                    });
                f.append(e)
            })
        },
        makeFadeout: function (e, d, f) {
            d = (typeof d != "undefined") ? d : c;
            f = (typeof f != "undefined") ? f : a;
            AJS.$(e || "div.aui-message.fadeout").each(function () {
                var j = AJS.$(this);
                var l = false;
                var h = false;

                function g() {
                    j.stop(true, false).delay(d).fadeOut(f, function () {
                        j.closeMessage()
                    })
                }

                function k() {
                    j.stop(true, false).fadeTo(b, 1)
                }

                function i() {
                    return !l && !h
                }
                j.focusin(function () {
                    l = true;
                    k()
                }).focusout(function () {
                    l = false;
                    if (i()) {
                        g()
                    }
                }).hover(function () {
                    h = true;
                    k()
                }, function () {
                    h = false;
                    if (i()) {
                        g()
                    }
                });
                g()
            })
        },
        template: '<div class="aui-message {type} {closeable} {shadowed} {fadeout}"><p class="title"><span class="aui-icon icon-{type}"></span><strong>{title}</strong></p>{body}<!-- .aui-message --></div>',
        createMessage: function (d) {
            AJS.messages[d] = function (g, i) {
                var h = this.template,
                    f, e;
                if (!i) {
                    i = g;
                    g = "#aui-message-bar"
                }
                i.closeable = (i.closeable != false);
                i.shadowed = (i.shadowed != false);
                f = AJS.$(AJS.template(h).fill({
                    type: d,
                    closeable: i.closeable ? "closeable" : "",
                    shadowed: i.shadowed ? "shadowed" : "",
                    fadeout: i.fadeout ? "fadeout" : "",
                    title: i.title || "",
                    "body:html": i.body || ""
                }).toString());
                if (i.id) {
                    if (/[#\'\"\.\s]/g.test(i.id)) {
                        AJS.log("AJS.Messages error: ID rejected, must not include spaces, hashes, dots or quotes.")
                    } else {
                        f.attr("id", i.id)
                    }
                }
                e = i.insert || "append";
                if (e === "prepend") {
                    f.prependTo(g)
                } else {
                    f.appendTo(g)
                }
                i.closeable && AJS.messages.makeCloseable(f);
                i.fadeout && AJS.messages.makeFadeout(f, i.delay, i.duration);
                return f
            }
        }
    };
    AJS.$.fn.closeMessage = function () {
        var d = AJS.$(this);
        if (d.hasClass("aui-message", "closeable")) {
            d.stop(true);
            d.trigger("messageClose", [this]).remove()
        }
    };
    AJS.$(function () {
        AJS.messages.setup()
    })
})();
(function () {
    var c = /#.*/,
        d = "active-tab",
        a = "active-pane",
        b = "aria-selected",
        e = "aria-hidden";
    AJS.tabs = {
        setup: function () {
            var f = AJS.$(".aui-tabs:not(.aui-tabs-disabled)");
            f.attr("role", "application");
            f.find(".tabs-pane").each(function () {
                var i = AJS.$(this);
                i.attr("role", "tabpanel");
                if (i.hasClass(a)) {
                    i.attr(e, "false")
                } else {
                    i.attr(e, "true")
                }
            });
            for (var g = 0, h = f.length; g < h; g++) {
                var j = f.eq(g);
                if (!j.data("aui-tab-events-bound")) {
                    var k = j.children("ul.tabs-menu");
                    k.attr("role", "tablist");
                    k.children("li").attr("role", "presentation");
                    k.find("> .menu-item a").each(function () {
                        var l = AJS.$(this);
                        AJS._addID(l);
                        l.attr("role", "tab");
                        var i = l.attr("href");
                        AJS.$(i).attr("aria-labelledby", l.attr("id"));
                        if (l.parent().hasClass(d)) {
                            l.attr(b, "true")
                        } else {
                            l.attr(b, "false")
                        }
                    });
                    k.delegate("a", "click", function (i) {
                        AJS.tabs.change(AJS.$(this), i);
                        i && i.preventDefault()
                    });
                    j.data("aui-tab-events-bound", true)
                }
            }
            AJS.$(".aui-tabs.vertical-tabs").find("a").each(function (m) {
                var n = AJS.$(this);
                if (!n.attr("title")) {
                    var l = n.children("strong:first");
                    if (AJS.isClipped(l)) {
                        n.attr("title", n.text())
                    }
                }
            })
        },
      
        change: function (g, h) {
            var f = AJS.$(g.attr("href").match(c)[0]);
            f.addClass(a).attr(e, "false").siblings(".tabs-pane").removeClass(a).attr(e, "true");
            g.parent("li.menu-item").addClass(d).siblings(".menu-item").removeClass(d);
            g.closest(".tabs-menu").find("a").attr(b, "false");
            g.attr(b, "true");
            g.trigger("tabSelect", {
                tab: g,
                pane: f
            })
        }
    };
    AJS.$(AJS.tabs.setup)
})();
AJS.template = (function (g) {
    var j = /\{([^\}]+)\}/g,
        d = /(?:(?:^|\.)(.+?)(?=\[|\.|$|\()|\[('|")(.+?)\2\])(\(\))?/g,
        h = /([^\\])'/g,
        f = function (o, n, p, l) {
            var m = p;
            n.replace(d, function (s, r, q, u, t) {
                r = r || u;
                if (m) {
                    if (r + ":html" in m) {
                        m = m[r + ":html"];
                        l = true
                    } else {
                        if (r in m) {
                            m = m[r]
                        }
                    } if (t && typeof m == "function") {
                        m = m()
                    }
                }
            });
            if (m == null || m == p) {
                m = o
            }
            m = String(m);
            if (!l) {
                m = e.escape(m)
            }
            return m
        }, b = function (l) {
            this.template = this.template.replace(j, function (n, m) {
                return f(n, m, l, true)
            });
            return this
        }, k = function (l) {
            this.template = this.template.replace(j, function (n, m) {
                return f(n, m, l)
            });
            return this
        }, c = function () {
            return this.template
        };
    var e = function (m) {
        function l() {
            return l.template
        }
        l.template = String(m);
        l.toString = l.valueOf = c;
        l.fill = k;
        l.fillHtml = b;
        return l
    }, a = {}, i = [];
    e.load = function (l) {
        l = String(l);
        if (!a.hasOwnProperty(l)) {
            i.length >= 1000 && delete a[i.shift()];
            i.push(l);
            a[l] = g("script[title='" + l.replace(h, "$1\\'") + "']")[0].text
        }
        return this(a[l])
    };
    e.escape = AJS.escapeHtml;
    return e
})(AJS.$);
(function (a, e) {
    var d = navigator.platform.indexOf("Mac") !== -1;
    var b = /^(backspace|tab|r(ight|eturn)|s(hift|pace|croll)|c(trl|apslock)|alt|pa(use|ge(up|down))|e(sc|nd)|home|left|up|d(el|own)|insert|f\d\d?|numlock|meta)/i;
    a.whenIType = function (q) {
        var p = [],
            j = e.Callbacks();

        function g(r) {
            if (!AJS.popup.current && j) {
                j.fire(r)
            }
        }

        function h(r) {
            r.preventDefault()
        }

        function o(r) {
            var s = r && r.split ? e.trim(r).split(" ") : [r];
            e.each(s, function () {
                i(this)
            })
        }

        function l(s) {
            var r = s.length;
            while (r--) {
                if (s[r].length > 1 && s[r] !== "space") {
                    return true
                }
            }
            return false
        }

        function i(t) {
            var s = t instanceof Array ? t : n(t.toString());
            var r = l(s) ? "keydown" : "keypress";
            p.push(s);
            e(document).bind(r, s, g);
            e(document).bind(r + " keyup", s, h)
        }

        function n(t) {
            var v = [],
                u = "",
                s, r;
            while (t.length) {
                if (s = t.match(/^(ctrl|meta|shift|alt)\+/i)) {
                    u += s[0];
                    t = t.substring(s[0].length)
                } else {
                    if (r = t.match(b)) {
                        v.push(u + r[0]);
                        t = t.substring(r[0].length);
                        u = ""
                    } else {
                        v.push(u + t[0]);
                        t = t.substring(1);
                        u = ""
                    }
                }
            }
            return v
        }

        function k(t) {
            var v = e(t),
                x = v.attr("title") || "",
                w = p.slice();
            var s = v.data("kbShortcutAppended") || "";
            var r = !s;
            var u = r ? x : x.substring(0, x.length - s.length);
            while (w.length) {
                s = f(w.shift().slice(), s, r);
                r = false
            }
            if (d) {
                s = s.replace(/Meta/ig, "\u2318").replace(/Shift/ig, "\u21E7")
            }
            v.attr("title", u + s);
            v.data("kbShortcutAppended", s)
        }

        function m(s) {
            var t = e(s);
            var r = t.data("kbShortcutAppended");
            if (!r) {
                return
            }
            var u = t.attr("title");
            t.attr("title", u.replace(r, ""));
            t.removeData("kbShortcutAppended")
        }

        function f(t, s, r) {
            if (r) {
                s += " (" + AJS.I18n.getText("aui.keyboard.shortcut.type.x", t.shift())
            } else {
                s = s.replace(/\)$/, "");
                s += AJS.I18n.getText("aui.keyboard.shortcut.or.x", t.shift())
            }
            e.each(t, function () {
                s += " " + AJS.I18n.getText("aui.keyboard.shortcut.then.x", this)
            });
            s += ")";
            return s
        }
        o(q);
        return a.whenIType.makeShortcut({
            executor: j,
            bindKeys: o,
            addShortcutsToTitle: k,
            removeShortcutsFromTitle: m,
            keypressHandler: g,
            defaultPreventionHandler: h
        })
    };
    a.whenIType.makeShortcut = function (n) {
        var i = n.executor;
        var l = n.bindKeys;
        var j = n.addShortcutsToTitle;
        var k = n.removeShortcutsFromTitle;
        var g = n.keypressHandler;
        var h = n.defaultPreventionHandler;
        var m = [];

        function f(o) {
            return function (p, r) {
                r = r || {};
                var q = r.focusedClass || "focused";
                var s = r.hasOwnProperty("wrapAround") ? r.wrapAround : true;
                var t = r.hasOwnProperty("escToCancel") ? r.escToCancel : true;
                i.add(function () {
                    var w = e(p),
                        v = w.filter("." + q),
                        u = v.length === 0 ? undefined : {
                            transition: true
                        };
                    if (t) {
                        e(document).one("keydown", function (x) {
                            if (x.keyCode === AJS.keyCode.ESCAPE && v) {
                                v.removeClass(q)
                            }
                        })
                    }
                    if (v.length) {
                        v.removeClass(q)
                    }
                    v = o(v, w, s);
                    if (v && v.length > 0) {
                        v.addClass(q);
                        v.moveTo(u);
                        if (v.is("a")) {
                            v.focus()
                        } else {
                            v.find("a:first").focus()
                        }
                    }
                });
                return this
            }
        }
        return {
            moveToNextItem: f(function (q, r, p) {
                var o;
                if (p && q.length === 0) {
                    return r.eq(0)
                } else {
                    o = e.inArray(q.get(0), r);
                    if (o < r.length - 1) {
                        o = o + 1;
                        return r.eq(o)
                    } else {
                        if (p) {
                            return r.eq(0)
                        }
                    }
                }
                return q
            }),
            moveToPrevItem: f(function (q, r, p) {
                var o;
                if (p && q.length === 0) {
                    return r.filter(":last")
                } else {
                    o = e.inArray(q.get(0), r);
                    if (o > 0) {
                        o = o - 1;
                        return r.eq(o)
                    } else {
                        if (p) {
                            return r.filter(":last")
                        }
                    }
                }
                return q
            }),
            click: function (o) {
                m.push(o);
                j(o);
                i.add(function () {
                    var p = e(o);
                    if (p.length > 0) {
                        p.click()
                    }
                });
                return this
            },
            goTo: function (o) {
                i.add(function () {
                    window.location.href = o
                });
                return this
            },
            followLink: function (o) {
                m.push(o);
                j(o);
                i.add(function () {
                    var p = e(o)[0];
                    if (p && {
                        a: true,
                        link: true
                    }[p.nodeName.toLowerCase()]) {
                        window.location.href = p.href
                    }
                });
                return this
            },
            execute: function (p) {
                var o = this;
                i.add(function () {
                    p.apply(o, arguments)
                });
                return this
            },
            evaluate: function (o) {
                o.call(this)
            },
            moveToAndClick: function (o) {
                m.push(o);
                j(o);
                i.add(function () {
                    var p = e(o);
                    if (p.length > 0) {
                        p.click();
                        p.moveTo()
                    }
                });
                return this
            },
            moveToAndFocus: function (o) {
                m.push(o);
                j(o);
                i.add(function (q) {
                    var p = AJS.$(o);
                    if (p.length > 0) {
                        p.focus();
                        p.moveTo && p.moveTo();
                        if (p.is(":input")) {
                            q.preventDefault()
                        }
                    }
                });
                return this
            },
            or: function (o) {
                l(o);
                return this
            },
            unbind: function () {
                e(document).unbind("keydown keypress", g).unbind("keydown keypress keyup", h);
                for (var p = 0, o = m.length; p < o; p++) {
                    k(m[p])
                }
                m = []
            }
        }
    };
    a.whenIType.fromJSON = function (h, g) {
        var f = [];
        if (h) {
            e.each(h, function (k, l) {
                var j = l.op,
                    o = l.param,
                    n;
                if (j === "execute" || j === "evaluate") {
                    n = [new Function(o)]
                } else {
                    if (/^\[[^\]\[]*,[^\]\[]*\]$/.test(o)) {
                        try {
                            n = JSON.parse(o)
                        } catch (m) {
                            c("When using a parameter array, array must be in strict JSON format: " + o)
                        }
                        if (!e.isArray(n)) {
                            c("Badly formatted shortcut parameter. String or JSON Array of parameters required: " + o)
                        }
                    } else {
                        n = [o]
                    }
                }
                e.each(l.keys, function () {
                    var p = this;
                    if (g && d) {
                        p = e.map(this, function (q) {
                            return q.replace(/ctrl/i, "meta")
                        })
                    }
                    var i = AJS.whenIType(p);
                    i[j].apply(i, n);
                    f.push(i)
                })
            })
        }
        return f
    };

    function c(f) {
        if (console && console.error) {
            console.error(f)
        } else {
            AJS.log(f)
        }
    }
    e(document).bind("iframeAppended", function (g, f) {
        e(f).load(function () {
            var h = e(f).contents();
            h.bind("keyup keydown keypress", function (i) {
                if (e.browser.safari && i.type === "keypress") {
                    return
                }
                if (!e(i.target).is(":input")) {
                    e.event.trigger(i, arguments, document, true)
                }
            })
        })
    })
})(AJS, AJS.$);
(function (a) {
    AJS.responsiveheader = {};
    AJS.responsiveheader.setup = function () {
        var c = a(".aui-header");
        if (!c.length) {
            return
        }
        c.each(function (d, e) {
            b(a(e), d)
        });

        function b(j, o) {
            var t = j.find(".aui-header-secondary .aui-nav").first();
            a(".aui-header").attr("data-aui-responsive", "true");
            var u = [];
            var e = 0;
            var h = 0;
            var l;
            var i;
            var q = j.find("#logo");
            var p = 0;
            var n = (function () {
                var w = j.find(".aui-header-primary").first();
                return function (x) {
                    return w.find(x)
                }
            })();
            var s = 0;
            n(".aui-button").each(function (w, x) {
                s += a(x).parent().outerWidth(true)
            });
            n(".aui-nav > li > a:not(.aui-button)").each(function (x, y) {
                var w = a(y).parent();
                var z = w.outerWidth(true);
                u.push({
                    itemElement: w,
                    itemWidth: z
                });
                h += z
            });
            i = u.length;
            a(window).resize(function () {
                i = f(o)
            });
            d(o);
            var v = q.find("img");
            if (v.length !== 0) {
                v.attr("data-aui-responsive-header-index", o);
                v.load(function (w) {
                    i = f(o)
                })
            }
            i = f(o);
            n(".aui-nav").css("width", "auto");

            function f(y) {
                var x;
                r();
                if (e > h) {
                    m(y)
                } else {
                    l.show();
                    x = e - p;
                    for (var w = 0; x >= 0; w++) {
                        x -= u[w].itemWidth
                    }
                    w = w - 1;
                    k(w, y);
                    g(w, i, y);
                    return w
                }
            }

            function r() {
                var x = t.length != 0 ? t.position().left : a(window).width();
                var w = q.position().left + q.outerWidth(true) + s;
                e = x - w
            }

            function d(x) {
                var w = a("<li>" + aui.dropdown2.trigger({
                    menu: {
                        id: "aui-responsive-header-dropdown-content-" + x
                    },
                    text: AJS.I18n.getText("aui.words.more"),
                    extraAttributes: {
                        href: "#"
                    },
                    id: "aui-responsive-header-dropdown-trigger-" + x
                }) + "</li>");
                w.append(aui.dropdown2.contents({
                    id: "aui-responsive-header-dropdown-content-" + x,
                    extraClasses: "aui-style-default",
                    content: aui.dropdown2.section({
                        content: "<ul id='aui-responsive-header-dropdown-list-" + x + "'></ul>"
                    })
                }));
                if (s == 0) {
                    w.appendTo(n(".aui-nav"))
                } else {
                    w.insertBefore(n(".aui-nav > li > .aui-button").first().parent())
                }
                l = w;
                p = l.outerWidth(true)
            }

            function g(w, B, D) {
                if (w < 0 || B < 0 || w === B) {
                    return
                }
                var z = a("#aui-responsive-header-dropdown-trigger-" + D);
                var C = z.parent();
                var A;
                var x;
                if (z.hasClass("active")) {
                    z.trigger("aui-button-invoke")
                }
                var y = n(".aui-nav > li > a:not(.aui-button):not(#aui-responsive-header-dropdown-trigger-" + D + ")").length;
                while (w > B) {
                    A = u[B];
                    if (A && A.itemElement) {
                        x = A.itemElement;
                        if (y == 0) {
                            x.prependTo(n(".aui-nav"))
                        } else {
                            x.insertBefore(C)
                        }
                        x.children("a").removeClass("aui-dropdown2-sub-trigger active");
                        B = B + 1;
                        y = y + 1
                    }
                }
            }

            function k(w, A) {
                if (w < 0) {
                    return
                }
                var y = a("#aui-responsive-header-dropdown-list-" + A);
                for (var x = w; x < u.length; x++) {
                    u[x].itemElement.appendTo(y);
                    var z = u[x].itemElement.children("a");
                    if (z.hasClass("aui-dropdown2-trigger")) {
                        z.addClass("aui-dropdown2-sub-trigger")
                    }
                }
            }

            function m(w) {
                l.hide();
                g(u.length, i, w)
            }
        }
    }
})(AJS.$);
AJS.$(AJS.responsiveheader.setup);

/*! AUI Flat Pack - version 5.2 - generated 2013-07-25 10:18:38 +0000 */


(function (e) {
    function d(h, g) {
        return (typeof h == "function") ? (h.call(g)) : h
    }

    function f(g) {
        while (g = g.parentNode) {
            if (g == document) {
                return true
            }
        }
        return false
    }
    var a = 0;

    function b() {
        var g = a++;
        return "tipsyuid" + g
    }

    function c(h, g) {
        this.$element = e(h);
        this.options = g;
        this.enabled = true;
        this.fixTitle()
    }
    c.prototype = {
        show: function () {
            var p = this.getTitle();
            if (p && this.enabled) {
                var i = this.tip();
                i.find(".tipsy-inner")[this.options.html ? "html" : "text"](p);
                i[0].className = "tipsy";
                i.remove().css({
                    top: 0,
                    left: 0,
                    visibility: "hidden",
                    display: "block"
                }).prependTo(document.body);
                var l = this;

                function h() {
                    l.hoverTooltip = true
                }

                function m() {
                    if (l.hoverState == "in") {
                        return
                    }
                    l.hoverTooltip = false;
                    if (l.options.trigger != "manual") {
                        var r = l.options.trigger == "hover" ? "mouseleave.tipsy" : "blur.tipsy";
                        l.$element.trigger(r)
                    }
                }
                if (this.options.hoverable) {
                    i.hover(h, m)
                }
                var n = e.extend({}, this.$element.offset(), {
                    width: this.$element[0].offsetWidth,
                    height: this.$element[0].offsetHeight
                });
                var g = i[0].offsetWidth,
                    k = i[0].offsetHeight,
                    q = d(this.options.gravity, this.$element[0]);
                var o;
                switch (q.charAt(0)) {
                case "n":
                    o = {
                        top: n.top + n.height + this.options.offset,
                        left: n.left + n.width / 2 - g / 2
                    };
                    break;
                case "s":
                    o = {
                        top: n.top - k - this.options.offset,
                        left: n.left + n.width / 2 - g / 2
                    };
                    break;
                case "e":
                    o = {
                        top: n.top + n.height / 2 - k / 2,
                        left: n.left - g - this.options.offset
                    };
                    break;
                case "w":
                    o = {
                        top: n.top + n.height / 2 - k / 2,
                        left: n.left + n.width + this.options.offset
                    };
                    break
                }
                if (q.length == 2) {
                    if (q.charAt(1) == "w") {
                        o.left = n.left + n.width / 2 - 15
                    } else {
                        o.left = n.left + n.width / 2 - g + 15
                    }
                }
                i.css(o).addClass("tipsy-" + q);
                i.find(".tipsy-arrow")[0].className = "tipsy-arrow tipsy-arrow-" + q.charAt(0);
                if (this.options.className) {
                    i.addClass(d(this.options.className, this.$element[0]))
                }
                if (this.options.fade) {
                    i.stop().css({
                        opacity: 0,
                        display: "block",
                        visibility: "visible"
                    }).animate({
                        opacity: this.options.opacity
                    })
                } else {
                    i.css({
                        visibility: "visible",
                        opacity: this.options.opacity
                    })
                } if (this.options.aria) {
                    var j = b();
                    i.attr("id", j);
                    this.$element.attr("aria-describedby", j)
                }
            }
        },
        hide: function () {
            if (this.options.fade) {
                this.tip().stop().fadeOut(function () {
                    e(this).remove()
                })
            } else {
                this.tip().remove()
            } if (this.options.aria) {
                this.$element.removeAttr("aria-describedby")
            }
        },
        fixTitle: function () {
            var g = this.$element;
            if (g.attr("title") || typeof (g.attr("original-title")) != "string") {
                g.attr("original-title", g.attr("title") || "").removeAttr("title")
            }
        },
        getTitle: function () {
            var i, g = this.$element,
                h = this.options;
            this.fixTitle();
            var i, h = this.options;
            if (typeof h.title == "string") {
                i = g.attr(h.title == "title" ? "original-title" : h.title)
            } else {
                if (typeof h.title == "function") {
                    i = h.title.call(g[0])
                }
            }
            i = ("" + i).replace(/(^\s*|\s*$)/, "");
            return i || h.fallback
        },
        tip: function () {
            if (!this.$tip) {
                this.$tip = e('<div class="tipsy"></div>').html('<div class="tipsy-arrow"></div><div class="tipsy-inner"></div>').attr("role", "tooltip");
                this.$tip.data("tipsy-pointee", this.$element[0])
            }
            return this.$tip
        },
        validate: function () {
            if (!this.$element[0].parentNode) {
                this.hide();
                this.$element = null;
                this.options = null
            }
        },
        enable: function () {
            this.enabled = true
        },
        disable: function () {
            this.enabled = false
        },
        toggleEnabled: function () {
            this.enabled = !this.enabled
        }
    };
    e.fn.tipsy = function (j) {
        if (j === true) {
            return this.data("tipsy")
        } else {
            if (typeof j == "string") {
                var l = this.data("tipsy");
                if (l) {
                    l[j]()
                }
                return this
            }
        }
        j = e.extend({}, e.fn.tipsy.defaults, j);
        if (j.hoverable) {
            j.delayOut = j.delayOut || 20
        }

        function i(n) {
            var o = e.data(n, "tipsy");
            if (!o) {
                o = new c(n, e.fn.tipsy.elementOptions(n, j));
                e.data(n, "tipsy", o)
            }
            return o
        }

        function m() {
            var n = i(this);
            n.hoverState = "in";
            if (j.delayIn == 0) {
                n.show()
            } else {
                n.fixTitle();
                setTimeout(function () {
                    if (n.hoverState == "in") {
                        n.show()
                    }
                }, j.delayIn)
            }
        }

        function h() {
            var n = i(this);
            n.hoverState = "out";
            if (j.delayOut == 0) {
                n.hide()
            } else {
                setTimeout(function () {
                    if (n.hoverState == "out" && !n.hoverTooltip) {
                        n.hide()
                    }
                }, j.delayOut)
            }
        }
        if (!j.live) {
            this.each(function () {
                i(this)
            })
        }
        if (j.trigger != "manual") {
            var k = j.trigger == "hover" ? "mouseenter.tipsy" : "focus.tipsy",
                g = j.trigger == "hover" ? "mouseleave.tipsy" : "blur.tipsy";
            if (j.live) {
                e(this.context).on(k, this.selector, m).on(g, this.selector, h)
            } else {
                this.bind(k, m).bind(g, h)
            }
        }
        return this
    };
    e.fn.tipsy.defaults = {
        aria: false,
        className: null,
        delayIn: 0,
        delayOut: 0,
        fade: false,
        fallback: "",
        gravity: "n",
        html: false,
        live: false,
        hoverable: false,
        offset: 0,
        opacity: 0.8,
        title: "title",
        trigger: "hover"
    };
    e.fn.tipsy.revalidate = function () {
        e(".tipsy").each(function () {
            var g = e.data(this, "tipsy-pointee");
            if (!g || !f(g)) {
                e(this).remove()
            }
        })
    };
    e.fn.tipsy.elementOptions = function (h, g) {
        return e.metadata ? e.extend({}, g, e(h).metadata()) : g
    };
    e.fn.tipsy.autoNS = function () {
        return e(this).offset().top > (e(document).scrollTop() + e(window).height() / 2) ? "s" : "n"
    };
    e.fn.tipsy.autoWE = function () {
        return e(this).offset().left > (e(document).scrollLeft() + e(window).width() / 2) ? "e" : "w"
    };
    e.fn.tipsy.autoBounds = function (h, g) {
        return function () {
            var i = {
                ns: g[0],
                ew: (g.length > 1 ? g[1] : false)
            }, l = e(document).scrollTop() + h,
                j = e(document).scrollLeft() + h,
                k = e(this);
            if (k.offset().top < l) {
                i.ns = "n"
            }
            if (k.offset().left < j) {
                i.ew = "w"
            }
            if (e(window).width() + e(document).scrollLeft() - k.offset().left < h) {
                i.ew = "e"
            }
            if (e(window).height() + e(document).scrollTop() - k.offset().top < h) {
                i.ns = "s"
            }
            return i.ns + (i.ew ? i.ew : "")
        }
    }
})(jQuery);
(function (a) {
    a.fn.tooltip = function (b) {
        var e = a.extend({}, a.fn.tooltip.defaults, b),
            c = this.tipsy(e);
        if (e.hideOnClick && (e.trigger == "hover" || !e.trigger && this.tipsy.defaults.trigger == "hover")) {
            var d = function () {
                a(this).tipsy("hide")
            };
            if (e.live) {
                a(this.context).on("click.tipsy", this.selector, d)
            } else {
                this.bind("click.tipsy", d)
            }
        }
        return c
    };
    a.fn.tooltip.defaults = {
        opacity: 1,
        offset: 1,
        delayIn: 500,
        hoverable: true,
        hideOnClick: true
    }
}(AJS.$));
/*->
 #name>Sortable Tables
 #javascript>Yes
 #css>Yes
 #description> Standards Patterns and Styling for HTML Sortable Tables
 <-*/
(function () {
    var DEFAULT_SORT_OPTIONS = {
        sortMultiSortKey: '',
        headers: {},
        debug: false
    };

    function sortTable($table) {
        var options = DEFAULT_SORT_OPTIONS;
        $table.find("th").each(function (index, header) {

            var $header = AJS.$(header);
            options.headers[index] = {};
            if ($header.hasClass("aui-table-column-unsortable")) {
                options.headers[index].sorter = false;
            } else {
                $header.attr('tabindex', '0');
                $header.wrapInner("<span class='aui-table-header-content'/>");
                if ($header.hasClass("aui-table-column-issue-key")) {
                    options.headers[index].sorter = "issue-key";
                }
            }
        });
        $table.tablesorter(options);
    }

    AJS.tablessortable = {
        setup: function () {

            /*
             This parser is used for issue keys in the format <PROJECT_KEY>-<ISSUE_NUMBER>, where <PROJECT_KEY> is a maximum
             10 character string with characters(A-Z). Assumes that issue number is no larger than 999,999. e.g. not more
             than a million issues.
             This pads the issue key to allow for proper string sorting so that the project key is always 10 characters and the
             issue number is always 6 digits. e.g. it appends the project key '.' until it is 10 characters long and prepends 0
             so that the issue number is 6 digits long. e.g. CONF-102 == CONF......000102. This is to allow proper string sorting.
             */
            AJS.$.tablesorter.addParser({
                id: 'issue-key',
                is: function () {
                    return false;
                },

                format: function (s) {
                    var keyComponents = s.split("-");
                    var projectKey = keyComponents[0];
                    var issueNumber = keyComponents[1];

                    var PROJECT_KEY_TEMPLATE = "..........";
                    var ISSUE_NUMBER_TEMPLATE = "000000";
                    var stringRepresentation = (projectKey + PROJECT_KEY_TEMPLATE).slice(0, PROJECT_KEY_TEMPLATE.length);
                    stringRepresentation += (ISSUE_NUMBER_TEMPLATE + issueNumber).slice(-ISSUE_NUMBER_TEMPLATE.length);

                    return stringRepresentation;
                },

                type: 'text'
            });

            AJS.$(".aui-table-sortable").each(function () {
                sortTable(AJS.$(this));
            });
        },

        setTableSortable: function ($table) {
            sortTable($table);
        }
    };

    AJS.$(AJS.tablessortable.setup);
})();
/*!
 * TableSorter 2.10.8 - Client-side table sorting with ease!
 * @requires jQuery v1.2.6+
 *
 * Copyright (c) 2007 Christian Bach
 * Examples and docs at: http://tablesorter.com
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 *
 * @type jQuery
 * @name tablesorter
 * @cat Plugins/Tablesorter
 * @author Christian Bach/christian.bach@polyester.se
 * @contributor Rob Garrison/https://github.com/Mottie/tablesorter
 */
/*jshint browser:true, jquery:true, unused:false, expr: true */
/*global console:false, alert:false */
!(function ($) {
    "use strict";
    $.extend({
        /*jshint supernew:true */
        tablesorter: new function () {

            var ts = this;

            ts.version = "2.10.8";

            ts.parsers = [];
            ts.widgets = [];
            ts.defaults = {

                // *** appearance
                theme: 'default', // adds tablesorter-{theme} to the table for styling
                widthFixed: false, // adds colgroup to fix widths of columns
                showProcessing: false, // show an indeterminate timer icon in the header when the table is sorted or filtered.

                headerTemplate: '{content}', // header layout template (HTML ok); {content} = innerHTML, {icon} = <i/> (class from cssIcon)
                onRenderTemplate: null, // function(index, template){ return template; }, (template is a string)
                onRenderHeader: null, // function(index){}, (nothing to return)

                // *** functionality
                cancelSelection: true, // prevent text selection in the header
                dateFormat: 'mmddyyyy', // other options: "ddmmyyy" or "yyyymmdd"
                sortMultiSortKey: 'shiftKey', // key used to select additional columns
                sortResetKey: 'ctrlKey', // key used to remove sorting on a column
                usNumberFormat: true, // false for German "1.234.567,89" or French "1 234 567,89"
                delayInit: false, // if false, the parsed table contents will not update until the first sort
                serverSideSorting: false, // if true, server-side sorting should be performed because client-side sorting will be disabled, but the ui and events will still be used.

                // *** sort options
                headers: {}, // set sorter, string, empty, locked order, sortInitialOrder, filter, etc.
                ignoreCase: true, // ignore case while sorting
                sortForce: null, // column(s) first sorted; always applied
                sortList: [], // Initial sort order; applied initially; updated when manually sorted
                sortAppend: null, // column(s) sorted last; always applied

                sortInitialOrder: 'asc', // sort direction on first click
                sortLocaleCompare: false, // replace equivalent character (accented characters)
                sortReset: false, // third click on the header will reset column to default - unsorted
                sortRestart: false, // restart sort to "sortInitialOrder" when clicking on previously unsorted columns

                emptyTo: 'bottom', // sort empty cell to bottom, top, none, zero
                stringTo: 'max', // sort strings in numerical column as max, min, top, bottom, zero
                textExtraction: 'simple', // text extraction method/function - function(node, table, cellIndex){}
                textSorter: null, // use custom text sorter - function(a,b){ return a.sort(b); } // basic sort

                // *** widget options
                widgets: [], // method to add widgets, e.g. widgets: ['zebra']
                widgetOptions: {
                    zebra: ['even', 'odd'] // zebra widget alternating row class names
                },
                initWidgets: true, // apply widgets on tablesorter initialization

                // *** callbacks
                initialized: null, // function(table){},

                // *** css class names
                tableClass: 'tablesorter',
                cssAsc: 'tablesorter-headerAsc',
                cssChildRow: 'tablesorter-childRow', // previously "expand-child"
                cssDesc: 'tablesorter-headerDesc',
                cssHeader: 'tablesorter-header',
                cssHeaderRow: 'tablesorter-headerRow',
                cssIcon: 'tablesorter-icon', //  if this class exists, a <i> will be added to the header automatically
                cssInfoBlock: 'tablesorter-infoOnly', // don't sort tbody with this class name
                cssProcessing: 'tablesorter-processing', // processing icon applied to header during sort/filter

                // *** selectors
                selectorHeaders: '> thead th, > thead td',
                selectorSort: 'th, td', // jQuery selector of content within selectorHeaders that is clickable to trigger a sort
                selectorRemove: '.remove-me',

                // *** advanced
                debug: false,

                // *** Internal variables
                headerList: [],
                empties: {},
                strings: {},
                parsers: []

                // deprecated; but retained for backwards compatibility
                // widgetZebra: { css: ["even", "odd"] }

            };

            /* debuging utils */
            function log(s) {
                if (typeof console !== "undefined" && typeof console.log !== "undefined") {
                    console.log(s);
                } else {
                    alert(s);
                }
            }

            function benchmark(s, d) {
                log(s + " (" + (new Date().getTime() - d.getTime()) + "ms)");
            }

            ts.log = log;
            ts.benchmark = benchmark;

            function getElementText(table, node, cellIndex) {
                if (!node) {
                    return "";
                }
                var c = table.config,
                    t = c.textExtraction,
                    text = "";
                if (t === "simple") {
                    if (c.supportsTextContent) {
                        text = node.textContent; // newer browsers support this
                    } else {
                        text = $(node).text();
                    }
                } else {
                    if (typeof t === "function") {
                        text = t(node, table, cellIndex);
                    } else if (typeof t === "object" && t.hasOwnProperty(cellIndex)) {
                        text = t[cellIndex](node, table, cellIndex);
                    } else {
                        text = c.supportsTextContent ? node.textContent : $(node).text();
                    }
                }
                return $.trim(text);
            }

            function detectParserForColumn(table, rows, rowIndex, cellIndex) {
                var cur,
                    i = ts.parsers.length,
                    node = false,
                    nodeValue = '',
                    keepLooking = true;
                while (nodeValue === '' && keepLooking) {
                    rowIndex++;
                    if (rows[rowIndex]) {
                        node = rows[rowIndex].cells[cellIndex];
                        nodeValue = getElementText(table, node, cellIndex);
                        if (table.config.debug) {
                            log('Checking if value was empty on row ' + rowIndex + ', column: ' + cellIndex + ': "' + nodeValue + '"');
                        }
                    } else {
                        keepLooking = false;
                    }
                }
                while (--i >= 0) {
                    cur = ts.parsers[i];
                    // ignore the default text parser because it will always be true
                    if (cur && cur.id !== 'text' && cur.is && cur.is(nodeValue, table, node)) {
                        return cur;
                    }
                }
                // nothing found, return the generic parser (text)
                return ts.getParserById('text');
            }

            function buildParserCache(table) {
                var c = table.config,
                    // update table bodies in case we start with an empty table
                    tb = c.$tbodies = c.$table.children('tbody:not(.' + c.cssInfoBlock + ')'),
                    rows, list, l, i, h, ch, p, parsersDebug = "";
                if (tb.length === 0) {
                    return c.debug ? log('*Empty table!* Not building a parser cache') : '';
                }
                rows = tb[0].rows;
                if (rows[0]) {
                    list = [];
                    l = rows[0].cells.length;
                    for (i = 0; i < l; i++) {
                        // tons of thanks to AnthonyM1229 for working out the following selector (issue #74) to make this work in IE8!
                        // More fixes to this selector to work properly in iOS and jQuery 1.8+ (issue #132 & #174)
                        h = c.$headers.filter(':not([colspan])');
                        h = h.add(c.$headers.filter('[colspan="1"]')) // ie8 fix
                        .filter('[data-column="' + i + '"]:last');
                        ch = c.headers[i];
                        // get column parser
                        p = ts.getParserById(ts.getData(h, ch, 'sorter'));
                        // empty cells behaviour - keeping emptyToBottom for backwards compatibility
                        c.empties[i] = ts.getData(h, ch, 'empty') || c.emptyTo || (c.emptyToBottom ? 'bottom' : 'top');
                        // text strings behaviour in numerical sorts
                        c.strings[i] = ts.getData(h, ch, 'string') || c.stringTo || 'max';
                        if (!p) {
                            p = detectParserForColumn(table, rows, -1, i);
                        }
                        if (c.debug) {
                            parsersDebug += "column:" + i + "; parser:" + p.id + "; string:" + c.strings[i] + '; empty: ' + c.empties[i] + "\n";
                        }
                        list.push(p);
                    }
                }
                if (c.debug) {
                    log(parsersDebug);
                }
                c.parsers = list;
            }

            /* utils */
            function buildCache(table) {
                var b = table.tBodies,
                    tc = table.config,
                    totalRows,
                    totalCells,
                    parsers = tc.parsers,
                    t, v, i, j, k, c, cols, cacheTime, colMax = [];
                tc.cache = {};
                // if no parsers found, return - it's an empty table.
                if (!parsers) {
                    return tc.debug ? log('*Empty table!* Not building a cache') : '';
                }
                if (tc.debug) {
                    cacheTime = new Date();
                }
                // processing icon
                if (tc.showProcessing) {
                    ts.isProcessing(table, true);
                }
                for (k = 0; k < b.length; k++) {
                    tc.cache[k] = {
                        row: [],
                        normalized: []
                    };
                    // ignore tbodies with class name from css.cssInfoBlock
                    if (!$(b[k]).hasClass(tc.cssInfoBlock)) {
                        totalRows = (b[k] && b[k].rows.length) || 0;
                        totalCells = (b[k].rows[0] && b[k].rows[0].cells.length) || 0;
                        for (i = 0; i < totalRows; ++i) {
                            /** Add the table data to main data array */
                            c = $(b[k].rows[i]);
                            cols = [];
                            // if this is a child row, add it to the last row's children and continue to the next row
                            if (c.hasClass(tc.cssChildRow)) {
                                tc.cache[k].row[tc.cache[k].row.length - 1] = tc.cache[k].row[tc.cache[k].row.length - 1].add(c);
                                // go to the next for loop
                                continue;
                            }
                            tc.cache[k].row.push(c);
                            for (j = 0; j < totalCells; ++j) {
                                t = getElementText(table, c[0].cells[j], j);
                                // allow parsing if the string is empty, previously parsing would change it to zero,
                                // in case the parser needs to extract data from the table cell attributes
                                v = parsers[j].format(t, table, c[0].cells[j], j);
                                cols.push(v);
                                if ((parsers[j].type || '').toLowerCase() === "numeric") {
                                    colMax[j] = Math.max(Math.abs(v) || 0, colMax[j] || 0); // determine column max value (ignore sign)
                                }
                            }
                            cols.push(tc.cache[k].normalized.length); // add position for rowCache
                            tc.cache[k].normalized.push(cols);
                        }
                        tc.cache[k].colMax = colMax;
                    }
                }
                if (tc.showProcessing) {
                    ts.isProcessing(table); // remove processing icon
                }
                if (tc.debug) {
                    benchmark("Building cache for " + totalRows + " rows", cacheTime);
                }
            }

            // init flag (true) used by pager plugin to prevent widget application
            function appendToTable(table, init) {
                var c = table.config,
                    b = table.tBodies,
                    rows = [],
                    c2 = c.cache,
                    r, n, totalRows, checkCell, $bk, $tb,
                    i, j, k, l, pos, appendTime;
                if (!c2[0]) {
                    return;
                } // empty table - fixes #206
                if (c.debug) {
                    appendTime = new Date();
                }
                for (k = 0; k < b.length; k++) {
                    $bk = $(b[k]);
                    if ($bk.length && !$bk.hasClass(c.cssInfoBlock)) {
                        // get tbody
                        $tb = ts.processTbody(table, $bk, true);
                        r = c2[k].row;
                        n = c2[k].normalized;
                        totalRows = n.length;
                        checkCell = totalRows ? (n[0].length - 1) : 0;
                        for (i = 0; i < totalRows; i++) {
                            pos = n[i][checkCell];
                            rows.push(r[pos]);
                            // removeRows used by the pager plugin
                            if (!c.appender || !c.removeRows) {
                                l = r[pos].length;
                                for (j = 0; j < l; j++) {
                                    $tb.append(r[pos][j]);
                                }
                            }
                        }
                        // restore tbody
                        ts.processTbody(table, $tb, false);
                    }
                }
                if (c.appender) {
                    c.appender(table, rows);
                }
                if (c.debug) {
                    benchmark("Rebuilt table", appendTime);
                }
                // apply table widgets
                if (!init) {
                    ts.applyWidget(table);
                }
                // trigger sortend
                $(table).trigger("sortEnd", table);
            }

            // computeTableHeaderCellIndexes from:
            // http://www.javascripttoolbox.com/lib/table/examples.php
            // http://www.javascripttoolbox.com/temp/table_cellindex.html
            function computeThIndexes(t) {
                var matrix = [],
                    lookup = {},
                    cols = 0, // determine the number of columns
                    trs = $(t).find('thead:eq(0), tfoot').children('tr'), // children tr in tfoot - see issue #196
                    i, j, k, l, c, cells, rowIndex, cellId, rowSpan, colSpan, firstAvailCol, matrixrow;
                for (i = 0; i < trs.length; i++) {
                    cells = trs[i].cells;
                    for (j = 0; j < cells.length; j++) {
                        c = cells[j];
                        rowIndex = c.parentNode.rowIndex;
                        cellId = rowIndex + "-" + c.cellIndex;
                        rowSpan = c.rowSpan || 1;
                        colSpan = c.colSpan || 1;
                        if (typeof (matrix[rowIndex]) === "undefined") {
                            matrix[rowIndex] = [];
                        }
                        // Find first available column in the first row
                        for (k = 0; k < matrix[rowIndex].length + 1; k++) {
                            if (typeof (matrix[rowIndex][k]) === "undefined") {
                                firstAvailCol = k;
                                break;
                            }
                        }
                        lookup[cellId] = firstAvailCol;
                        cols = Math.max(firstAvailCol, cols);
                        // add data-column
                        $(c).attr({
                            'data-column': firstAvailCol
                        }); // 'data-row' : rowIndex
                        for (k = rowIndex; k < rowIndex + rowSpan; k++) {
                            if (typeof (matrix[k]) === "undefined") {
                                matrix[k] = [];
                            }
                            matrixrow = matrix[k];
                            for (l = firstAvailCol; l < firstAvailCol + colSpan; l++) {
                                matrixrow[l] = "x";
                            }
                        }
                    }
                }
                t.config.columns = cols; // may not be accurate if # header columns !== # tbody columns
                return lookup;
            }

            function formatSortingOrder(v) {
                // look for "d" in "desc" order; return true
                return (/^d/i.test(v) || v === 1);
            }

            function buildHeaders(table) {
                var header_index = computeThIndexes(table),
                    ch, $t,
                    h, i, t, lock, time, c = table.config;
                c.headerList = [];
                c.headerContent = [];
                if (c.debug) {
                    time = new Date();
                }
                i = c.cssIcon ? '<i class="' + c.cssIcon + '"></i>' : ''; // add icon if cssIcon option exists
                c.$headers = $(table).find(c.selectorHeaders).each(function (index) {
                    $t = $(this);
                    ch = c.headers[index];
                    c.headerContent[index] = this.innerHTML; // save original header content
                    // set up header template
                    t = c.headerTemplate.replace(/\{content\}/g, this.innerHTML).replace(/\{icon\}/g, i);
                    if (c.onRenderTemplate) {
                        h = c.onRenderTemplate.apply($t, [index, t]);
                        if (h && typeof h === 'string') {
                            t = h;
                        } // only change t if something is returned
                    }
                    this.innerHTML = '<div class="tablesorter-header-inner">' + t + '</div>'; // faster than wrapInner

                    if (c.onRenderHeader) {
                        c.onRenderHeader.apply($t, [index]);
                    }

                    this.column = header_index[this.parentNode.rowIndex + "-" + this.cellIndex];
                    this.order = formatSortingOrder(ts.getData($t, ch, 'sortInitialOrder') || c.sortInitialOrder) ? [1, 0, 2] : [0, 1, 2];
                    this.count = -1; // set to -1 because clicking on the header automatically adds one
                    this.lockedOrder = false;
                    lock = ts.getData($t, ch, 'lockedOrder') || false;
                    if (typeof lock !== 'undefined' && lock !== false) {
                        this.order = this.lockedOrder = formatSortingOrder(lock) ? [1, 1, 1] : [0, 0, 0];
                    }
                    $t.addClass(c.cssHeader);
                    // add cell to headerList
                    c.headerList[index] = this;
                    // add to parent in case there are multiple rows
                    $t.parent().addClass(c.cssHeaderRow);
                    // allow keyboard cursor to focus on element
                    $t.attr("tabindex", 0);
                });
                // enable/disable sorting
                updateHeader(table);
                if (c.debug) {
                    benchmark("Built headers:", time);
                    log(c.$headers);
                }
            }

            function commonUpdate(table, resort, callback) {
                var c = table.config;
                // remove rows/elements before update
                c.$table.find(c.selectorRemove).remove();
                // rebuild parsers
                buildParserCache(table);
                // rebuild the cache map
                buildCache(table);
                checkResort(c.$table, resort, callback);
            }

            function updateHeader(table) {
                var s, c = table.config;
                c.$headers.each(function (index, th) {
                    s = ts.getData(th, c.headers[index], 'sorter') === 'false';
                    th.sortDisabled = s;
                    $(th)[s ? 'addClass' : 'removeClass']('sorter-false');
                });
            }

            function setHeadersCss(table) {
                var f, i, j, l,
                    c = table.config,
                    list = c.sortList,
                    css = [c.cssAsc, c.cssDesc],
                    // find the footer
                    $t = $(table).find('tfoot tr').children().removeClass(css.join(' '));
                // remove all header information
                c.$headers.removeClass(css.join(' '));
                l = list.length;
                for (i = 0; i < l; i++) {
                    // direction = 2 means reset!
                    if (list[i][1] !== 2) {
                        // multicolumn sorting updating - choose the :last in case there are nested columns
                        f = c.$headers.not('.sorter-false').filter('[data-column="' + list[i][0] + '"]' + (l === 1 ? ':last' : ''));
                        if (f.length) {
                            for (j = 0; j < f.length; j++) {
                                if (!f[j].sortDisabled) {
                                    f.eq(j).addClass(css[list[i][1]]);
                                    // add sorted class to footer, if it exists
                                    if ($t.length) {
                                        $t.filter('[data-column="' + list[i][0] + '"]').eq(j).addClass(css[list[i][1]]);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // automatically add col group, and column sizes if set
            function fixColumnWidth(table) {
                if (table.config.widthFixed && $(table).find('colgroup').length === 0) {
                    var colgroup = $('<colgroup>'),
                        overallWidth = $(table).width();
                    $(table.tBodies[0]).find("tr:first").children("td").each(function () {
                        colgroup.append($('<col>').css('width', parseInt(($(this).width() / overallWidth) * 1000, 10) / 10 + '%'));
                    });
                    $(table).prepend(colgroup);
                }
            }

            function updateHeaderSortCount(table, list) {
                var s, t, o, c = table.config,
                    sl = list || c.sortList;
                c.sortList = [];
                $.each(sl, function (i, v) {
                    // ensure all sortList values are numeric - fixes #127
                    s = [parseInt(v[0], 10), parseInt(v[1], 10)];
                    // make sure header exists
                    o = c.headerList[s[0]];
                    if (o) { // prevents error if sorton array is wrong
                        c.sortList.push(s);
                        t = $.inArray(s[1], o.order); // fixes issue #167
                        o.count = t >= 0 ? t : s[1] % (c.sortReset ? 3 : 2);
                    }
                });
            }

            function getCachedSortType(parsers, i) {
                return (parsers && parsers[i]) ? parsers[i].type || '' : '';
            }

            function initSort(table, cell, e) {
                var a, i, j, o, s,
                    c = table.config,
                    k = !e[c.sortMultiSortKey],
                    $this = $(table);
                // Only call sortStart if sorting is enabled
                $this.trigger("sortStart", table);
                // get current column sort order
                cell.count = e[c.sortResetKey] ? 2 : (cell.count + 1) % (c.sortReset ? 3 : 2);
                // reset all sorts on non-current column - issue #30
                if (c.sortRestart) {
                    i = cell;
                    c.$headers.each(function () {
                        // only reset counts on columns that weren't just clicked on and if not included in a multisort
                        if (this !== i && (k || !$(this).is('.' + c.cssDesc + ',.' + c.cssAsc))) {
                            this.count = -1;
                        }
                    });
                }
                // get current column index
                i = cell.column;
                // user only wants to sort on one column
                if (k) {
                    // flush the sort list
                    c.sortList = [];
                    if (c.sortForce !== null) {
                        a = c.sortForce;
                        for (j = 0; j < a.length; j++) {
                            if (a[j][0] !== i) {
                                c.sortList.push(a[j]);
                            }
                        }
                    }
                    // add column to sort list
                    o = cell.order[cell.count];
                    if (o < 2) {
                        c.sortList.push([i, o]);
                        // add other columns if header spans across multiple
                        if (cell.colSpan > 1) {
                            for (j = 1; j < cell.colSpan; j++) {
                                c.sortList.push([i + j, o]);
                            }
                        }
                    }
                    // multi column sorting
                } else {
                    // get rid of the sortAppend before adding more - fixes issue #115
                    if (c.sortAppend && c.sortList.length > 1) {
                        if (ts.isValueInArray(c.sortAppend[0][0], c.sortList)) {
                            c.sortList.pop();
                        }
                    }
                    // the user has clicked on an already sorted column
                    if (ts.isValueInArray(i, c.sortList)) {
                        // reverse the sorting direction for all tables
                        for (j = 0; j < c.sortList.length; j++) {
                            s = c.sortList[j];
                            o = c.headerList[s[0]];
                            if (s[0] === i) {
                                s[1] = o.order[o.count];
                                if (s[1] === 2) {
                                    c.sortList.splice(j, 1);
                                    o.count = -1;
                                }
                            }
                        }
                    } else {
                        // add column to sort list array
                        o = cell.order[cell.count];
                        if (o < 2) {
                            c.sortList.push([i, o]);
                            // add other columns if header spans across multiple
                            if (cell.colSpan > 1) {
                                for (j = 1; j < cell.colSpan; j++) {
                                    c.sortList.push([i + j, o]);
                                }
                            }
                        }
                    }
                }
                if (c.sortAppend !== null) {
                    a = c.sortAppend;
                    for (j = 0; j < a.length; j++) {
                        if (a[j][0] !== i) {
                            c.sortList.push(a[j]);
                        }
                    }
                }
                // sortBegin event triggered immediately before the sort
                $this.trigger("sortBegin", table);
                // setTimeout needed so the processing icon shows up
                setTimeout(function () {
                    // set css for headers
                    setHeadersCss(table);
                    multisort(table);
                    appendToTable(table);
                }, 1);
            }

            // sort multiple columns
            function multisort(table) { /*jshint loopfunc:true */
                var dir = 0,
                    tc = table.config,
                    sortList = tc.sortList,
                    l = sortList.length,
                    bl = table.tBodies.length,
                    sortTime, i, k, c, colMax, cache, lc, s, order, orgOrderCol;
                if (tc.serverSideSorting || !tc.cache[0]) { // empty table - fixes #206
                    return;
                }
                if (tc.debug) {
                    sortTime = new Date();
                }
                for (k = 0; k < bl; k++) {
                    colMax = tc.cache[k].colMax;
                    cache = tc.cache[k].normalized;
                    lc = cache.length;
                    orgOrderCol = (cache && cache[0]) ? cache[0].length - 1 : 0;
                    cache.sort(function (a, b) {
                        // cache is undefined here in IE, so don't use it!
                        for (i = 0; i < l; i++) {
                            c = sortList[i][0];
                            order = sortList[i][1];
                            // fallback to natural sort since it is more robust
                            s = /n/i.test(getCachedSortType(tc.parsers, c)) ? "Numeric" : "Text";
                            s += order === 0 ? "" : "Desc";
                            if (/Numeric/.test(s) && tc.strings[c]) {
                                // sort strings in numerical columns
                                if (typeof (tc.string[tc.strings[c]]) === 'boolean') {
                                    dir = (order === 0 ? 1 : -1) * (tc.string[tc.strings[c]] ? -1 : 1);
                                } else {
                                    dir = (tc.strings[c]) ? tc.string[tc.strings[c]] || 0 : 0;
                                }
                            }
                            var sort = $.tablesorter["sort" + s](table, a[c], b[c], c, colMax[c], dir);
                            if (sort) {
                                return sort;
                            }
                        }
                        return a[orgOrderCol] - b[orgOrderCol];
                    });
                }
                if (tc.debug) {
                    benchmark("Sorting on " + sortList.toString() + " and dir " + order + " time", sortTime);
                }
            }

            function resortComplete($table, callback) {
                $table.trigger('updateComplete');
                if (typeof callback === "function") {
                    callback($table[0]);
                }
            }

            function checkResort($table, flag, callback) {
                // don't try to resort if the table is still processing
                // this will catch spamming of the updateCell method
                if (flag !== false && !$table[0].isProcessing) {
                    $table.trigger("sorton", [$table[0].config.sortList,
                        function () {
                            resortComplete($table, callback);
                        }
                    ]);
                } else {
                    resortComplete($table, callback);
                }
            }

            function bindEvents(table) {
                var c = table.config,
                    $this = c.$table,
                    j, downTime;
                // apply event handling to headers
                c.$headers
                // http://stackoverflow.com/questions/5312849/jquery-find-self;
                .find(c.selectorSort).add(c.$headers.filter(c.selectorSort))
                    .unbind('mousedown.tablesorter mouseup.tablesorter sort.tablesorter keypress.tablesorter')
                    .bind('mousedown.tablesorter mouseup.tablesorter sort.tablesorter keypress.tablesorter', function (e, external) {
                        // only recognize left clicks or enter
                        if (((e.which || e.button) !== 1 && !/sort|keypress/.test(e.type)) || (e.type === 'keypress' && e.which !== 13)) {
                            return false;
                        }
                        // ignore long clicks (prevents resizable widget from initializing a sort)
                        if (e.type === 'mouseup' && external !== true && (new Date().getTime() - downTime > 250)) {
                            return false;
                        }
                        // set timer on mousedown
                        if (e.type === 'mousedown') {
                            downTime = new Date().getTime();
                            return e.target.tagName === "INPUT" ? '' : !c.cancelSelection;
                        }
                        if (c.delayInit && !c.cache) {
                            buildCache(table);
                        }
                        // jQuery v1.2.6 doesn't have closest()
                        var $cell = /TH|TD/.test(this.tagName) ? $(this) : $(this).parents('th, td').filter(':first'),
                            cell = $cell[0];
                        if (!cell.sortDisabled) {
                            initSort(table, cell, e);
                        }
                    });
                if (c.cancelSelection) {
                    // cancel selection
                    c.$headers
                        .attr('unselectable', 'on')
                        .bind('selectstart', false)
                        .css({
                            'user-select': 'none',
                            'MozUserSelect': 'none' // not needed for jQuery 1.8+
                        });
                }
                // apply easy methods that trigger bound events
                $this
                    .unbind('sortReset update updateRows updateCell updateAll addRows sorton appendCache applyWidgetId applyWidgets refreshWidgets destroy mouseup mouseleave '.split(' ').join('.tablesorter '))
                    .bind("sortReset.tablesorter", function (e) {
                        e.stopPropagation();
                        c.sortList = [];
                        setHeadersCss(table);
                        multisort(table);
                        appendToTable(table);
                    })
                    .bind("updateAll.tablesorter", function (e, resort, callback) {
                        e.stopPropagation();
                        ts.refreshWidgets(table, true, true);
                        ts.restoreHeaders(table);
                        buildHeaders(table);
                        bindEvents(table);
                        commonUpdate(table, resort, callback);
                    })
                    .bind("update.tablesorter updateRows.tablesorter", function (e, resort, callback) {
                        e.stopPropagation();
                        // update sorting (if enabled/disabled)
                        updateHeader(table);
                        commonUpdate(table, resort, callback);
                    })
                    .bind("updateCell.tablesorter", function (e, cell, resort, callback) {
                        e.stopPropagation();
                        $this.find(c.selectorRemove).remove();
                        // get position from the dom
                        var l, row, icell,
                            $tb = $this.find('tbody'),
                            // update cache - format: function(s, table, cell, cellIndex)
                            // no closest in jQuery v1.2.6 - tbdy = $tb.index( $(cell).closest('tbody') ),$row = $(cell).closest('tr');
                            tbdy = $tb.index($(cell).parents('tbody').filter(':first')),
                            $row = $(cell).parents('tr').filter(':first');
                        cell = $(cell)[0]; // in case cell is a jQuery object
                        // tbody may not exist if update is initialized while tbody is removed for processing
                        if ($tb.length && tbdy >= 0) {
                            row = $tb.eq(tbdy).find('tr').index($row);
                            icell = cell.cellIndex;
                            l = c.cache[tbdy].normalized[row].length - 1;
                            c.cache[tbdy].row[table.config.cache[tbdy].normalized[row][l]] = $row;
                            c.cache[tbdy].normalized[row][icell] = c.parsers[icell].format(getElementText(table, cell, icell), table, cell, icell);
                            checkResort($this, resort, callback);
                        }
                    })
                    .bind("addRows.tablesorter", function (e, $row, resort, callback) {
                        e.stopPropagation();
                        var i, rows = $row.filter('tr').length,
                            dat = [],
                            l = $row[0].cells.length,
                            tbdy = $this.find('tbody').index($row.parents('tbody').filter(':first'));
                        // fixes adding rows to an empty table - see issue #179
                        if (!c.parsers) {
                            buildParserCache(table);
                        }
                        // add each row
                        for (i = 0; i < rows; i++) {
                            // add each cell
                            for (j = 0; j < l; j++) {
                                dat[j] = c.parsers[j].format(getElementText(table, $row[i].cells[j], j), table, $row[i].cells[j], j);
                            }
                            // add the row index to the end
                            dat.push(c.cache[tbdy].row.length);
                            // update cache
                            c.cache[tbdy].row.push([$row[i]]);
                            c.cache[tbdy].normalized.push(dat);
                            dat = [];
                        }
                        // resort using current settings
                        checkResort($this, resort, callback);
                    })
                    .bind("sorton.tablesorter", function (e, list, callback, init) {
                        e.stopPropagation();
                        $this.trigger("sortStart", this);
                        // update header count index
                        updateHeaderSortCount(table, list);
                        // set css for headers
                        setHeadersCss(table);
                        $this.trigger("sortBegin", this);
                        // sort the table and append it to the dom
                        multisort(table);
                        appendToTable(table, init);
                        if (typeof callback === "function") {
                            callback(table);
                        }
                    })
                    .bind("appendCache.tablesorter", function (e, callback, init) {
                        e.stopPropagation();
                        appendToTable(table, init);
                        if (typeof callback === "function") {
                            callback(table);
                        }
                    })
                    .bind("applyWidgetId.tablesorter", function (e, id) {
                        e.stopPropagation();
                        ts.getWidgetById(id).format(table, c, c.widgetOptions);
                    })
                    .bind("applyWidgets.tablesorter", function (e, init) {
                        e.stopPropagation();
                        // apply widgets
                        ts.applyWidget(table, init);
                    })
                    .bind("refreshWidgets.tablesorter", function (e, all, dontapply) {
                        e.stopPropagation();
                        ts.refreshWidgets(table, all, dontapply);
                    })
                    .bind("destroy.tablesorter", function (e, c, cb) {
                        e.stopPropagation();
                        ts.destroy(table, c, cb);
                    });
            }

            /* public methods */
            ts.construct = function (settings) {
                return this.each(function () {
                    // if no thead or tbody, or tablesorter is already present, quit
                    if (!this.tHead || this.tBodies.length === 0 || this.hasInitialized === true) {
                        return (this.config && this.config.debug) ? log('stopping initialization! No thead, tbody or tablesorter has already been initialized') : '';
                    }
                    // declare
                    var $this = $(this),
                        table = this,
                        c, k = '',
                        m = $.metadata;
                    // initialization flag
                    table.hasInitialized = false;
                    // table is being processed flag
                    table.isProcessing = true;
                    // new blank config object
                    table.config = {};
                    // merge and extend
                    c = $.extend(true, table.config, ts.defaults, settings);
                    // save the settings where they read
                    $.data(table, "tablesorter", c);
                    if (c.debug) {
                        $.data(table, 'startoveralltimer', new Date());
                    }
                    // constants
                    c.supportsTextContent = $('<span>x</span>')[0].textContent === 'x';
                    c.supportsDataObject = parseFloat($.fn.jquery) >= 1.4;
                    // digit sort text location; keeping max+/- for backwards compatibility
                    c.string = {
                        'max': 1,
                        'min': -1,
                        'max+': 1,
                        'max-': -1,
                        'zero': 0,
                        'none': 0,
                        'null': 0,
                        'top': true,
                        'bottom': false
                    };
                    // add table theme class only if there isn't already one there
                    if (!/tablesorter\-/.test($this.attr('class'))) {
                        k = (c.theme !== '' ? ' tablesorter-' + c.theme : '');
                    }
                    c.$table = $this.addClass(c.tableClass + k);
                    c.$tbodies = $this.children('tbody:not(.' + c.cssInfoBlock + ')');
                    // build headers
                    buildHeaders(table);
                    // fixate columns if the users supplies the fixedWidth option
                    // do this after theme has been applied
                    fixColumnWidth(table);
                    // try to auto detect column type, and store in tables config
                    buildParserCache(table);
                    // build the cache for the tbody cells
                    // delayInit will delay building the cache until the user starts a sort
                    if (!c.delayInit) {
                        buildCache(table);
                    }
                    // bind all header events and methods
                    bindEvents(table);
                    // get sort list from jQuery data or metadata
                    // in jQuery < 1.4, an error occurs when calling $this.data()
                    if (c.supportsDataObject && typeof $this.data().sortlist !== 'undefined') {
                        c.sortList = $this.data().sortlist;
                    } else if (m && ($this.metadata() && $this.metadata().sortlist)) {
                        c.sortList = $this.metadata().sortlist;
                    }
                    // apply widget init code
                    ts.applyWidget(table, true);
                    // if user has supplied a sort list to constructor
                    if (c.sortList.length > 0) {
                        $this.trigger("sorton", [c.sortList, {}, !c.initWidgets]);
                    } else if (c.initWidgets) {
                        // apply widget format
                        ts.applyWidget(table);
                    }

                    // show processesing icon
                    if (c.showProcessing) {
                        $this
                            .unbind('sortBegin.tablesorter sortEnd.tablesorter')
                            .bind('sortBegin.tablesorter sortEnd.tablesorter', function (e) {
                                ts.isProcessing(table, e.type === 'sortBegin');
                            });
                    }

                    // initialized
                    table.hasInitialized = true;
                    table.isProcessing = false;
                    if (c.debug) {
                        ts.benchmark("Overall initialization time", $.data(table, 'startoveralltimer'));
                    }
                    $this.trigger('tablesorter-initialized', table);
                    if (typeof c.initialized === 'function') {
                        c.initialized(table);
                    }
                });
            };

            // *** Process table ***
            // add processing indicator
            ts.isProcessing = function (table, toggle, $ths) {
                table = $(table);
                var c = table[0].config,
                    // default to all headers
                    $h = $ths || table.find('.' + c.cssHeader);
                if (toggle) {
                    if (c.sortList.length > 0) {
                        // get headers from the sortList
                        $h = $h.filter(function () {
                            // get data-column from attr to keep  compatibility with jQuery 1.2.6
                            return this.sortDisabled ? false : ts.isValueInArray(parseFloat($(this).attr('data-column')), c.sortList);
                        });
                    }
                    $h.addClass(c.cssProcessing);
                } else {
                    $h.removeClass(c.cssProcessing);
                }
            };

            // detach tbody but save the position
            // don't use tbody because there are portions that look for a tbody index (updateCell)
            ts.processTbody = function (table, $tb, getIt) {
                var holdr;
                if (getIt) {
                    table.isProcessing = true;
                    $tb.before('<span class="tablesorter-savemyplace"/>');
                    holdr = ($.fn.detach) ? $tb.detach() : $tb.remove();
                    return holdr;
                }
                holdr = $(table).find('span.tablesorter-savemyplace');
                $tb.insertAfter(holdr);
                holdr.remove();
                table.isProcessing = false;
            };

            ts.clearTableBody = function (table) {
                $(table)[0].config.$tbodies.empty();
            };

            // restore headers
            ts.restoreHeaders = function (table) {
                var c = table.config;
                // don't use c.$headers here in case header cells were swapped
                c.$table.find(c.selectorHeaders).each(function (i) {
                    // only restore header cells if it is wrapped
                    // because this is also used by the updateAll method
                    if ($(this).find('.tablesorter-header-inner').length) {
                        $(this).html(c.headerContent[i]);
                    }
                });
            };

            ts.destroy = function (table, removeClasses, callback) {
                table = $(table)[0];
                if (!table.hasInitialized) {
                    return;
                }
                // remove all widgets
                ts.refreshWidgets(table, true, true);
                var $t = $(table),
                    c = table.config,
                    $h = $t.find('thead:first'),
                    $r = $h.find('tr.' + c.cssHeaderRow).removeClass(c.cssHeaderRow),
                    $f = $t.find('tfoot:first > tr').children('th, td');
                // remove widget added rows, just in case
                $h.find('tr').not($r).remove();
                // disable tablesorter
                $t
                    .removeData('tablesorter')
                    .unbind('sortReset update updateAll updateRows updateCell addRows sorton appendCache applyWidgetId applyWidgets refreshWidgets destroy mouseup mouseleave keypress sortBegin sortEnd '.split(' ').join('.tablesorter '));
                c.$headers.add($f)
                    .removeClass(c.cssHeader + ' ' + c.cssAsc + ' ' + c.cssDesc)
                    .removeAttr('data-column');
                $r.find(c.selectorSort).unbind('mousedown.tablesorter mouseup.tablesorter keypress.tablesorter');
                ts.restoreHeaders(table);
                if (removeClasses !== false) {
                    $t.removeClass(c.tableClass + ' tablesorter-' + c.theme);
                }
                // clear flag in case the plugin is initialized again
                table.hasInitialized = false;
                if (typeof callback === 'function') {
                    callback(table);
                }
            };

            // *** sort functions ***
            // regex used in natural sort
            ts.regex = [
                /(^([+\-]?(?:0|[1-9]\d*)(?:\.\d*)?(?:[eE][+\-]?\d+)?)?$|^0x[0-9a-f]+$|\d+)/gi, // chunk/tokenize numbers & letters
                /(^([\w ]+,?[\w ]+)?[\w ]+,?[\w ]+\d+:\d+(:\d+)?[\w ]?|^\d{1,4}[\/\-]\d{1,4}[\/\-]\d{1,4}|^\w+, \w+ \d+, \d{4})/, //date
                /^0x[0-9a-f]+$/i // hex
            ];

            // Natural sort - https://github.com/overset/javascript-natural-sort
            ts.sortText = function (table, a, b, col) {
                if (a === b) {
                    return 0;
                }
                var c = table.config,
                    e = c.string[(c.empties[col] || c.emptyTo)],
                    r = ts.regex,
                    xN, xD, yN, yD, xF, yF, i, mx;
                if (a === '' && e !== 0) {
                    return typeof e === 'boolean' ? (e ? -1 : 1) : -e || -1;
                }
                if (b === '' && e !== 0) {
                    return typeof e === 'boolean' ? (e ? 1 : -1) : e || 1;
                }
                if (typeof c.textSorter === 'function') {
                    return c.textSorter(a, b, table, col);
                }
                // chunk/tokenize
                xN = a.replace(r[0], '\\0$1\\0').replace(/\\0$/, '').replace(/^\\0/, '').split('\\0');
                yN = b.replace(r[0], '\\0$1\\0').replace(/\\0$/, '').replace(/^\\0/, '').split('\\0');
                // numeric, hex or date detection
                xD = parseInt(a.match(r[2]), 16) || (xN.length !== 1 && a.match(r[1]) && Date.parse(a));
                yD = parseInt(b.match(r[2]), 16) || (xD && b.match(r[1]) && Date.parse(b)) || null;
                // first try and sort Hex codes or Dates
                if (yD) {
                    if (xD < yD) {
                        return -1;
                    }
                    if (xD > yD) {
                        return 1;
                    }
                }
                mx = Math.max(xN.length, yN.length);
                // natural sorting through split numeric strings and default strings
                for (i = 0; i < mx; i++) {
                    // find floats not starting with '0', string or 0 if not defined
                    xF = isNaN(xN[i]) ? xN[i] || 0 : parseFloat(xN[i]) || 0;
                    yF = isNaN(yN[i]) ? yN[i] || 0 : parseFloat(yN[i]) || 0;
                    // handle numeric vs string comparison - number < string - (Kyle Adams)
                    if (isNaN(xF) !== isNaN(yF)) {
                        return (isNaN(xF)) ? 1 : -1;
                    }
                    // rely on string comparison if different types - i.e. '02' < 2 != '02' < '2'
                    if (typeof xF !== typeof yF) {
                        xF += '';
                        yF += '';
                    }
                    if (xF < yF) {
                        return -1;
                    }
                    if (xF > yF) {
                        return 1;
                    }
                }
                return 0;
            };

            ts.sortTextDesc = function (table, a, b, col) {
                if (a === b) {
                    return 0;
                }
                var c = table.config,
                    e = c.string[(c.empties[col] || c.emptyTo)];
                if (a === '' && e !== 0) {
                    return typeof e === 'boolean' ? (e ? -1 : 1) : e || 1;
                }
                if (b === '' && e !== 0) {
                    return typeof e === 'boolean' ? (e ? 1 : -1) : -e || -1;
                }
                if (typeof c.textSorter === 'function') {
                    return c.textSorter(b, a, table, col);
                }
                return ts.sortText(table, b, a);
            };

            // return text string value by adding up ascii value
            // so the text is somewhat sorted when using a digital sort
            // this is NOT an alphanumeric sort
            ts.getTextValue = function (a, mx, d) {
                if (mx) {
                    // make sure the text value is greater than the max numerical value (mx)
                    var i, l = a ? a.length : 0,
                        n = mx + d;
                    for (i = 0; i < l; i++) {
                        n += a.charCodeAt(i);
                    }
                    return d * n;
                }
                return 0;
            };

            ts.sortNumeric = function (table, a, b, col, mx, d) {
                if (a === b) {
                    return 0;
                }
                var c = table.config,
                    e = c.string[(c.empties[col] || c.emptyTo)];
                if (a === '' && e !== 0) {
                    return typeof e === 'boolean' ? (e ? -1 : 1) : -e || -1;
                }
                if (b === '' && e !== 0) {
                    return typeof e === 'boolean' ? (e ? 1 : -1) : e || 1;
                }
                if (isNaN(a)) {
                    a = ts.getTextValue(a, mx, d);
                }
                if (isNaN(b)) {
                    b = ts.getTextValue(b, mx, d);
                }
                return a - b;
            };

            ts.sortNumericDesc = function (table, a, b, col, mx, d) {
                if (a === b) {
                    return 0;
                }
                var c = table.config,
                    e = c.string[(c.empties[col] || c.emptyTo)];
                if (a === '' && e !== 0) {
                    return typeof e === 'boolean' ? (e ? -1 : 1) : e || 1;
                }
                if (b === '' && e !== 0) {
                    return typeof e === 'boolean' ? (e ? 1 : -1) : -e || -1;
                }
                if (isNaN(a)) {
                    a = ts.getTextValue(a, mx, d);
                }
                if (isNaN(b)) {
                    b = ts.getTextValue(b, mx, d);
                }
                return b - a;
            };

            // used when replacing accented characters during sorting
            ts.characterEquivalents = {
                "a": "\u00e1\u00e0\u00e2\u00e3\u00e4\u0105\u00e5", // Ã¡Ã Ã¢Ã£Ã¤Ä…Ã¥
                "A": "\u00c1\u00c0\u00c2\u00c3\u00c4\u0104\u00c5", // Ã�Ã€Ã‚ÃƒÃ„Ä„Ã…
                "c": "\u00e7\u0107\u010d", // Ã§Ä‡Ä�
                "C": "\u00c7\u0106\u010c", // Ã‡Ä†ÄŒ
                "e": "\u00e9\u00e8\u00ea\u00eb\u011b\u0119", // Ã©Ã¨ÃªÃ«Ä›Ä™
                "E": "\u00c9\u00c8\u00ca\u00cb\u011a\u0118", // Ã‰ÃˆÃŠÃ‹ÄšÄ˜
                "i": "\u00ed\u00ec\u0130\u00ee\u00ef\u0131", // Ã­Ã¬Ä°Ã®Ã¯Ä±
                "I": "\u00cd\u00cc\u0130\u00ce\u00cf", // Ã�ÃŒÄ°ÃŽÃ�
                "o": "\u00f3\u00f2\u00f4\u00f5\u00f6", // Ã³Ã²Ã´ÃµÃ¶
                "O": "\u00d3\u00d2\u00d4\u00d5\u00d6", // Ã“Ã’Ã”Ã•Ã–
                "ss": "\u00df", // ÃŸ (s sharp)
                "SS": "\u1e9e", // áºž (Capital sharp s)
                "u": "\u00fa\u00f9\u00fb\u00fc\u016f", // ÃºÃ¹Ã»Ã¼Å¯
                "U": "\u00da\u00d9\u00db\u00dc\u016e" // ÃšÃ™Ã›ÃœÅ®
            };
            ts.replaceAccents = function (s) {
                var a, acc = '[',
                    eq = ts.characterEquivalents;
                if (!ts.characterRegex) {
                    ts.characterRegexArray = {};
                    for (a in eq) {
                        if (typeof a === 'string') {
                            acc += eq[a];
                            ts.characterRegexArray[a] = new RegExp('[' + eq[a] + ']', 'g');
                        }
                    }
                    ts.characterRegex = new RegExp(acc + ']');
                }
                if (ts.characterRegex.test(s)) {
                    for (a in eq) {
                        if (typeof a === 'string') {
                            s = s.replace(ts.characterRegexArray[a], a);
                        }
                    }
                }
                return s;
            };

            // *** utilities ***
            ts.isValueInArray = function (v, a) {
                var i, l = a.length;
                for (i = 0; i < l; i++) {
                    if (a[i][0] === v) {
                        return true;
                    }
                }
                return false;
            };

            ts.addParser = function (parser) {
                var i, l = ts.parsers.length,
                    a = true;
                for (i = 0; i < l; i++) {
                    if (ts.parsers[i].id.toLowerCase() === parser.id.toLowerCase()) {
                        a = false;
                    }
                }
                if (a) {
                    ts.parsers.push(parser);
                }
            };

            ts.getParserById = function (name) {
                var i, l = ts.parsers.length;
                for (i = 0; i < l; i++) {
                    if (ts.parsers[i].id.toLowerCase() === (name.toString()).toLowerCase()) {
                        return ts.parsers[i];
                    }
                }
                return false;
            };

            ts.addWidget = function (widget) {
                ts.widgets.push(widget);
            };

            ts.getWidgetById = function (name) {
                var i, w, l = ts.widgets.length;
                for (i = 0; i < l; i++) {
                    w = ts.widgets[i];
                    if (w && w.hasOwnProperty('id') && w.id.toLowerCase() === name.toLowerCase()) {
                        return w;
                    }
                }
            };

            ts.applyWidget = function (table, init) {
                table = $(table)[0]; // in case this is called externally
                var c = table.config,
                    wo = c.widgetOptions,
                    widgets = [],
                    time, i, w, wd;
                if (c.debug) {
                    time = new Date();
                }
                if (c.widgets.length) {
                    // ensure unique widget ids
                    c.widgets = $.grep(c.widgets, function (v, k) {
                        return $.inArray(v, c.widgets) === k;
                    });
                    // build widget array & add priority as needed
                    $.each(c.widgets || [], function (i, n) {
                        wd = ts.getWidgetById(n);
                        if (wd && wd.id) {
                            // set priority to 10 if not defined
                            if (!wd.priority) {
                                wd.priority = 10;
                            }
                            widgets[i] = wd;
                        }
                    });
                    // sort widgets by priority
                    widgets.sort(function (a, b) {
                        return a.priority < b.priority ? -1 : a.priority === b.priority ? 0 : 1;
                    });

                    // add/update selected widgets
                    $.each(widgets, function (i, w) {
                        if (w) {
                            if (init) {
                                if (w.hasOwnProperty('options')) {
                                    wo = table.config.widgetOptions = $.extend(true, {}, w.options, wo);
                                }
                                if (w.hasOwnProperty('init')) {
                                    w.init(table, w, c, wo);
                                }
                            } else if (!init && w.hasOwnProperty('format')) {
                                w.format(table, c, wo, false);
                            }
                        }
                    });
                }
                if (c.debug) {
                    w = c.widgets.length;
                    benchmark("Completed " + (init === true ? "initializing " : "applying ") + w + " widget" + (w !== 1 ? "s" : ""), time);
                }
            };

            ts.refreshWidgets = function (table, doAll, dontapply) {
                table = $(table)[0]; // see issue #243
                var i, c = table.config,
                    cw = c.widgets,
                    w = ts.widgets,
                    l = w.length;
                // remove previous widgets
                for (i = 0; i < l; i++) {
                    if (w[i] && w[i].id && (doAll || $.inArray(w[i].id, cw) < 0)) {
                        if (c.debug) {
                            log('Refeshing widgets: Removing ' + w[i].id);
                        }
                        if (w[i].hasOwnProperty('remove')) {
                            w[i].remove(table, c, c.widgetOptions);
                        }
                    }
                }
                if (dontapply !== true) {
                    ts.applyWidget(table, doAll);
                }
            };

            // get sorter, string, empty, etc options for each column from
            // jQuery data, metadata, header option or header class name ("sorter-false")
            // priority = jQuery data > meta > headers option > header class name
            ts.getData = function (h, ch, key) {
                var val = '',
                    $h = $(h),
                    m, cl;
                if (!$h.length) {
                    return '';
                }
                m = $.metadata ? $h.metadata() : false;
                cl = ' ' + ($h.attr('class') || '');
                if (typeof $h.data(key) !== 'undefined' || typeof $h.data(key.toLowerCase()) !== 'undefined') {
                    // "data-lockedOrder" is assigned to "lockedorder"; but "data-locked-order" is assigned to "lockedOrder"
                    // "data-sort-initial-order" is assigned to "sortInitialOrder"
                    val += $h.data(key) || $h.data(key.toLowerCase());
                } else if (m && typeof m[key] !== 'undefined') {
                    val += m[key];
                } else if (ch && typeof ch[key] !== 'undefined') {
                    val += ch[key];
                } else if (cl !== ' ' && cl.match(' ' + key + '-')) {
                    // include sorter class name "sorter-text", etc; now works with "sorter-my-custom-parser"
                    val = cl.match(new RegExp('\\s' + key + '-([\\w-]+)'))[1] || '';
                }
                return $.trim(val);
            };

            ts.formatFloat = function (s, table) {
                if (typeof s !== 'string' || s === '') {
                    return s;
                }
                // allow using formatFloat without a table; defaults to US number format
                var i,
                    t = table && table.config ? table.config.usNumberFormat !== false :
                        typeof table !== "undefined" ? table : true;
                if (t) {
                    // US Format - 1,234,567.89 -> 1234567.89
                    s = s.replace(/,/g, '');
                } else {
                    // German Format = 1.234.567,89 -> 1234567.89
                    // French Format = 1 234 567,89 -> 1234567.89
                    s = s.replace(/[\s|\.]/g, '').replace(/,/g, '.');
                }
                if (/^\s*\([.\d]+\)/.test(s)) {
                    // make (#) into a negative number -> (10) = -10
                    s = s.replace(/^\s*\(/, '-').replace(/\)/, '');
                }
                i = parseFloat(s);
                // return the text instead of zero
                return isNaN(i) ? $.trim(s) : i;
            };

            ts.isDigit = function (s) {
                // replace all unwanted chars and match
                return isNaN(s) ? (/^[\-+(]?\d+[)]?$/).test(s.toString().replace(/[,.'"\s]/g, '')) : true;
            };

        }()
    });

    // make shortcut
    var ts = $.tablesorter;

    // extend plugin scope
    $.fn.extend({
        tablesorter: ts.construct
    });

    // add default parsers
    ts.addParser({
        id: "text",
        is: function () {
            return true;
        },
        format: function (s, table) {
            var c = table.config;
            if (s) {
                s = $.trim(c.ignoreCase ? s.toLocaleLowerCase() : s);
                s = c.sortLocaleCompare ? ts.replaceAccents(s) : s;
            }
            return s;
        },
        type: "text"
    });

    ts.addParser({
        id: "digit",
        is: function (s) {
            return ts.isDigit(s);
        },
        format: function (s, table) {
            var n = ts.formatFloat((s || '').replace(/[^\w,. \-()]/g, ""), table);
            return s && typeof n === 'number' ? n : s ? $.trim(s && table.config.ignoreCase ? s.toLocaleLowerCase() : s) : s;
        },
        type: "numeric"
    });

    ts.addParser({
        id: "currency",
        is: function (s) {
            return (/^\(?\d+[\u00a3$\u20ac\u00a4\u00a5\u00a2?.]|[\u00a3$\u20ac\u00a4\u00a5\u00a2?.]\d+\)?$/).test((s || '').replace(/[,. ]/g, '')); // Â£$â‚¬Â¤Â¥Â¢
        },
        format: function (s, table) {
            var n = ts.formatFloat((s || '').replace(/[^\w,. \-()]/g, ""), table);
            return s && typeof n === 'number' ? n : s ? $.trim(s && table.config.ignoreCase ? s.toLocaleLowerCase() : s) : s;
        },
        type: "numeric"
    });

    ts.addParser({
        id: "ipAddress",
        is: function (s) {
            return (/^\d{1,3}[\.]\d{1,3}[\.]\d{1,3}[\.]\d{1,3}$/).test(s);
        },
        format: function (s, table) {
            var i, a = s ? s.split(".") : '',
                r = "",
                l = a.length;
            for (i = 0; i < l; i++) {
                r += ("00" + a[i]).slice(-3);
            }
            return s ? ts.formatFloat(r, table) : s;
        },
        type: "numeric"
    });

    ts.addParser({
        id: "url",
        is: function (s) {
            return (/^(https?|ftp|file):\/\//).test(s);
        },
        format: function (s) {
            return s ? $.trim(s.replace(/(https?|ftp|file):\/\//, '')) : s;
        },
        type: "text"
    });

    ts.addParser({
        id: "isoDate",
        is: function (s) {
            return (/^\d{4}[\/\-]\d{1,2}[\/\-]\d{1,2}/).test(s);
        },
        format: function (s, table) {
            return s ? ts.formatFloat((s !== "") ? (new Date(s.replace(/-/g, "/")).getTime() || "") : "", table) : s;
        },
        type: "numeric"
    });

    ts.addParser({
        id: "percent",
        is: function (s) {
            return (/(\d\s*?%|%\s*?\d)/).test(s) && s.length < 15;
        },
        format: function (s, table) {
            return s ? ts.formatFloat(s.replace(/%/g, ""), table) : s;
        },
        type: "numeric"
    });

    ts.addParser({
        id: "usLongDate",
        is: function (s) {
            // two digit years are not allowed cross-browser
            // Jan 01, 2013 12:34:56 PM or 01 Jan 2013
            return (/^[A-Z]{3,10}\.?\s+\d{1,2},?\s+(\d{4})(\s+\d{1,2}:\d{2}(:\d{2})?(\s+[AP]M)?)?$/i).test(s) || (/^\d{1,2}\s+[A-Z]{3,10}\s+\d{4}/i).test(s);
        },
        format: function (s, table) {
            return s ? ts.formatFloat((new Date(s.replace(/(\S)([AP]M)$/i, "$1 $2")).getTime() || ''), table) : s;
        },
        type: "numeric"
    });

    ts.addParser({
        id: "shortDate", // "mmddyyyy", "ddmmyyyy" or "yyyymmdd"
        is: function (s) {
            // testing for ##-##-#### or ####-##-##, so it's not perfect; time can be included
            return (/(^\d{1,2}[\/\s]\d{1,2}[\/\s]\d{4})|(^\d{4}[\/\s]\d{1,2}[\/\s]\d{1,2})/).test((s || '').replace(/\s+/g, " ").replace(/[\-.,]/g, "/"));
        },
        format: function (s, table, cell, cellIndex) {
            if (s) {
                var c = table.config,
                    ci = c.headerList[cellIndex],
                    format = ci.dateFormat || ts.getData(ci, c.headers[cellIndex], 'dateFormat') || c.dateFormat;
                s = s.replace(/\s+/g, " ").replace(/[\-.,]/g, "/"); // escaped - because JSHint in Firefox was showing it as an error
                if (format === "mmddyyyy") {
                    s = s.replace(/(\d{1,2})[\/\s](\d{1,2})[\/\s](\d{4})/, "$3/$1/$2");
                } else if (format === "ddmmyyyy") {
                    s = s.replace(/(\d{1,2})[\/\s](\d{1,2})[\/\s](\d{4})/, "$3/$2/$1");
                } else if (format === "yyyymmdd") {
                    s = s.replace(/(\d{4})[\/\s](\d{1,2})[\/\s](\d{1,2})/, "$1/$2/$3");
                }
            }
            return s ? ts.formatFloat((new Date(s).getTime() || ''), table) : s;
        },
        type: "numeric"
    });

    ts.addParser({
        id: "time",
        is: function (s) {
            return (/^(([0-2]?\d:[0-5]\d)|([0-1]?\d:[0-5]\d\s?([AP]M)))$/i).test(s);
        },
        format: function (s, table) {
            return s ? ts.formatFloat((new Date("2000/01/01 " + s.replace(/(\S)([AP]M)$/i, "$1 $2")).getTime() || ""), table) : s;
        },
        type: "numeric"
    });

    ts.addParser({
        id: "metadata",
        is: function () {
            return false;
        },
        format: function (s, table, cell) {
            var c = table.config,
                p = (!c.parserMetadataName) ? 'sortValue' : c.parserMetadataName;
            return $(cell).metadata()[p];
        },
        type: "numeric"
    });

    // add default widgets
    ts.addWidget({
        id: "zebra",
        priority: 90,
        format: function (table, c, wo) {
            var $tb, $tv, $tr, row, even, time, k, l,
                child = new RegExp(c.cssChildRow, 'i'),
                b = c.$tbodies;
            if (c.debug) {
                time = new Date();
            }
            for (k = 0; k < b.length; k++) {
                // loop through the visible rows
                $tb = b.eq(k);
                l = $tb.children('tr').length;
                if (l > 1) {
                    row = 0;
                    $tv = $tb.children('tr:visible');
                    // revered back to using jQuery each - strangely it's the fastest method
                    /*jshint loopfunc:true */
                    $tv.each(function () {
                        $tr = $(this);
                        // style children rows the same way the parent row was styled
                        if (!child.test(this.className)) {
                            row++;
                        }
                        even = (row % 2 === 0);
                        $tr.removeClass(wo.zebra[even ? 1 : 0]).addClass(wo.zebra[even ? 0 : 1]);
                    });
                }
            }
            if (c.debug) {
                ts.benchmark("Applying Zebra widget", time);
            }
        },
        remove: function (table, c, wo) {
            var k, $tb,
                b = c.$tbodies,
                rmv = (wo.zebra || ["even", "odd"]).join(' ');
            for (k = 0; k < b.length; k++) {
                $tb = $.tablesorter.processTbody(table, b.eq(k), true); // remove tbody
                $tb.children().removeClass(rmv);
                $.tablesorter.processTbody(table, $tb, false); // restore tbody
            }
        }
    });

})(jQuery);
(function ($) {
    var $document = $(document),

        //convenience function because this needs to be run for all the events.
        getExpanderProperties = function (event) {
            var properties = {};

            properties.$trigger = $(event.currentTarget);
            properties.$content = $document.find("#" + properties.$trigger.attr("aria-controls"));
            properties.triggerIsParent = properties.$content.parent().filter(properties.$trigger).length != 0;
            properties.$shortContent = properties.triggerIsParent ? properties.$trigger.find(".aui-expander-short-content") : null;
            properties.height = properties.$content.css("min-height");
            properties.isCollapsible = properties.$trigger.data("collapsible") != false;
            properties.replaceText = properties.$trigger.attr("data-replace-text"); //can't use .data here because it doesn't update after the first call
            properties.replaceSelector = properties.$trigger.data("replace-selector");

            return properties;
        },
        replaceText = function (properties) {
            if (properties.replaceText) {
                var $replaceElement = properties.replaceSelector ?
                    properties.$trigger.find(properties.replaceSelector) :
                    properties.$trigger;

                properties.$trigger.attr("data-replace-text", $replaceElement.text());
                $replaceElement.text(properties.replaceText);
            }
        };
    //events that the expander listens to
    EXPANDER_EVENTS = {
        "aui-expander-invoke": function (event) {
            var $trigger = $(event.currentTarget);
            var $content = $document.find("#" + $trigger.attr("aria-controls"));
            var isCollapsible = $trigger.data("collapsible") != false;

            //determine if content should be expanded or collapsed
            if ($content.attr("aria-expanded") == "true" && isCollapsible) {
                $trigger.trigger("aui-expander-collapse");
            } else {
                $trigger.trigger("aui-expander-expand");
            }
        },
        "aui-expander-expand": function (event) {
            var properties = getExpanderProperties(event);

            properties.$content.attr("aria-expanded", "true");
            if (properties.height != "0px") {
                properties.$content.css("height", "auto");
            } else {
                properties.$content.attr("aria-hidden", "false");
            }

            //handle replace text
            replaceText(properties);

            //if the trigger is the parent also hide the short-content (default)
            if (properties.triggerIsParent) {
                properties.$shortContent.hide();
            }
            properties.$trigger.trigger("aui-expander-expanded");

        },
        "aui-expander-collapse": function (event) {

            var properties = getExpanderProperties(event),
                isHeightPx,
                lineHeight = parseInt(properties.$content.css("line-height"), 10),
                heightCap = properties.$content.children().first().height();

            //handle the height option
            if (properties.height != "0px") {
                properties.$content.css("height", 0);
            } else {
                properties.$content.attr("aria-hidden", "true");
            }
            //handle replace text
            replaceText(properties);

            //collapse the expander
            properties.$content.attr("aria-expanded", "false");
            //if the trigger is the parent also hide the short-content (default)
            if (properties.triggerIsParent) {
                properties.$shortContent.show();
            }
            properties.$trigger.trigger("aui-expander-collapsed");
        },

        "click.aui-expander": function (event) {
            $target = $(event.currentTarget);
            $target.trigger("aui-expander-invoke", event.currentTarget);
        }
    };
    //delegate events to the triggers on the page
    $document.on(EXPANDER_EVENTS, ".aui-expander-trigger");

})(jQuery);
//API
AJS.progressBars = {
    update: function (element, value) {
        var $progressBarContainer = AJS.$(element).first();
        var $progressBar = $progressBarContainer.children(".aui-progress-indicator-value");
        var currentProgress = $progressBar.attr("data-value") || 0;

        var afterTransitionEvent = "aui-progress-indicator-after-update";
        var beforeTransitionEvent = "aui-progress-indicator-before-update";
        var transitionEnd = "transitionend webkitTransitionEnd";

        var isIndeterminate = !$progressBarContainer.attr("data-value");

        //if the progress bar is indeterminate switch it.
        if (isIndeterminate) {
            $progressBar.detach().css("width", 0).appendTo($progressBarContainer);
        }

        if (typeof value === "number" && value <= 1 && value >= 0) {
            //trigger before animation event
            $progressBarContainer.trigger(beforeTransitionEvent, [currentProgress, value]);

            //trigger after animation event

            //detect whether transitions are supported
            var documentBody = document.body || document.documentElement;
            var style = documentBody.style;
            var transition = 'transition';

            function updateProgress(value) {
                //update the progress bar, need to set timeout so that batching of dom updates doesn't happen
                window.setTimeout(function () {
                    $progressBar.css("width", value * 100 + "%")
                    $progressBarContainer.attr("data-value", value);
                }, 0);
            }

            //trigger the event after transition end if supported, otherwise just trigger it
            if (typeof style.transition === 'string' || typeof style.WebkitTransition === "string") {
                $progressBar.one(transitionEnd, function () {
                    $progressBarContainer.trigger(afterTransitionEvent, [currentProgress, value]);
                });
                updateProgress(value);
            } else {
                updateProgress(value);
                $progressBarContainer.trigger(afterTransitionEvent, [currentProgress, value]);
            }


        }
        return $progressBarContainer;
    },
    setIndeterminate: function (element) {
        var $progressBarContainer = AJS.$(element).first();
        var $progressBar = $progressBarContainer.children(".aui-progress-indicator-value");

        $progressBarContainer.removeAttr("data-value");
        $progressBar.css("width", "100%");
    }
};


//fgnass.github.com/spin.js#v1.2.7
! function (window, document, undefined) {

    /**
     * Copyright (c) 2011 Felix Gnass [fgnass at neteye dot de]
     * Licensed under the MIT license
     */

    var prefixes = ['webkit', 'Moz', 'ms', 'O'] /* Vendor prefixes */ ,
        animations = {} /* Animation rules keyed by their name */ , useCssAnimations

        /**
         * Utility function to create elements. If no tag name is given,
         * a DIV is created. Optionally properties can be passed.
         */
        function createEl(tag, prop) {
            var el = document.createElement(tag || 'div'),
                n

            for (n in prop) el[n] = prop[n]
            return el
        }

        /**
         * Appends children and returns the parent.
         */
        function ins(parent /* child1, child2, ...*/ ) {
            for (var i = 1, n = arguments.length; i < n; i++)
                parent.appendChild(arguments[i])

            return parent
        }

        /**
         * Insert a new stylesheet to hold the @keyframe or VML rules.
         */
    var sheet = function () {
        var el = createEl('style', {
            type: 'text/css'
        })
        ins(document.getElementsByTagName('head')[0], el)
        return el.sheet || el.styleSheet
    }()

    /**
     * Creates an opacity keyframe animation rule and returns its name.
     * Since most mobile Webkits have timing issues with animation-delay,
     * we create separate rules for each line/segment.
     */
        function addAnimation(alpha, trail, i, lines) {
            var name = ['opacity', trail, ~~ (alpha * 100), i, lines].join('-'),
                start = 0.01 + i / lines * 100,
                z = Math.max(1 - (1 - alpha) / trail * (100 - start), alpha),
                prefix = useCssAnimations.substring(0, useCssAnimations.indexOf('Animation')).toLowerCase(),
                pre = prefix && '-' + prefix + '-' || ''

            if (!animations[name]) {
                sheet.insertRule(
                    '@' + pre + 'keyframes ' + name + '{' +
                    '0%{opacity:' + z + '}' +
                    start + '%{opacity:' + alpha + '}' +
                    (start + 0.01) + '%{opacity:1}' +
                    (start + trail) % 100 + '%{opacity:' + alpha + '}' +
                    '100%{opacity:' + z + '}' +
                    '}', sheet.cssRules.length)

                animations[name] = 1
            }
            return name
        }

        /**
         * Tries various vendor prefixes and returns the first supported property.
         **/
        function vendor(el, prop) {
            var s = el.style,
                pp, i

            if (s[prop] !== undefined) return prop
            prop = prop.charAt(0).toUpperCase() + prop.slice(1)
            for (i = 0; i < prefixes.length; i++) {
                pp = prefixes[i] + prop
                if (s[pp] !== undefined) return pp
            }
        }

        /**
         * Sets multiple style properties at once.
         */
        function css(el, prop) {
            for (var n in prop)
                el.style[vendor(el, n) || n] = prop[n]

            return el
        }

        /**
         * Fills in default values.
         */
        function merge(obj) {
            for (var i = 1; i < arguments.length; i++) {
                var def = arguments[i]
                for (var n in def)
                    if (obj[n] === undefined) obj[n] = def[n]
            }
            return obj
        }

        /**
         * Returns the absolute page-offset of the given element.
         */
        function pos(el) {
            var o = {
                x: el.offsetLeft,
                y: el.offsetTop
            }
            while ((el = el.offsetParent))
                o.x += el.offsetLeft, o.y += el.offsetTop

            return o
        }

    var defaults = {
        lines: 12, // The number of lines to draw
        length: 7, // The length of each line
        width: 5, // The line thickness
        radius: 10, // The radius of the inner circle
        rotate: 0, // Rotation offset
        corners: 1, // Roundness (0..1)
        color: '#000', // #rgb or #rrggbb
        speed: 1, // Rounds per second
        trail: 100, // Afterglow percentage
        opacity: 1 / 4, // Opacity of the lines
        fps: 20, // Frames per second when using setTimeout()
        zIndex: 2e9, // Use a high z-index by default
        className: 'spinner', // CSS class to assign to the element
        top: 'auto', // center vertically
        left: 'auto', // center horizontally
        position: 'relative' // element position
    }

    /** The constructor */
    var Spinner = function Spinner(o) {
        if (!this.spin) return new Spinner(o)
        this.opts = merge(o || {}, Spinner.defaults, defaults)
    }

    Spinner.defaults = {}

    merge(Spinner.prototype, {
        spin: function (target) {
            this.stop()
            var self = this,
                o = self.opts,
                el = self.el = css(createEl(0, {
                    className: o.className
                }), {
                    position: o.position,
                    width: 0,
                    zIndex: o.zIndex
                }),
                mid = o.radius + o.length + o.width,
                ep // element position
                , tp // target position

            if (target) {
                target.insertBefore(el, target.firstChild || null)
                tp = pos(target)
                ep = pos(el)
                css(el, {
                    left: (o.left == 'auto' ? tp.x - ep.x + (target.offsetWidth >> 1) : parseInt(o.left, 10) + mid) + 'px',
                    top: (o.top == 'auto' ? tp.y - ep.y + (target.offsetHeight >> 1) : parseInt(o.top, 10) + mid) + 'px'
                })
            }

            el.setAttribute('aria-role', 'progressbar')
            self.lines(el, self.opts)

            if (!useCssAnimations) {
                // No CSS animation support, use setTimeout() instead
                var i = 0,
                    fps = o.fps,
                    f = fps / o.speed,
                    ostep = (1 - o.opacity) / (f * o.trail / 100),
                    astep = f / o.lines

                    ;
                (function anim() {
                    i++;
                    for (var s = o.lines; s; s--) {
                        var alpha = Math.max(1 - (i + s * astep) % f * ostep, o.opacity)
                        self.opacity(el, o.lines - s, alpha, o)
                    }
                    self.timeout = self.el && setTimeout(anim, ~~ (1000 / fps))
                })()
            }
            return self
        },

        stop: function () {
            var el = this.el
            if (el) {
                clearTimeout(this.timeout)
                if (el.parentNode) el.parentNode.removeChild(el)
                this.el = undefined
            }
            return this
        },

        lines: function (el, o) {
            var i = 0,
                seg

                function fill(color, shadow) {
                    return css(createEl(), {
                        position: 'absolute',
                        width: (o.length + o.width) + 'px',
                        height: o.width + 'px',
                        background: color,
                        boxShadow: shadow,
                        transformOrigin: 'left',
                        transform: 'rotate(' + ~~(360 / o.lines * i + o.rotate) + 'deg) translate(' + o.radius + 'px' + ',0)',
                        borderRadius: (o.corners * o.width >> 1) + 'px'
                    })
                }

            for (; i < o.lines; i++) {
                seg = css(createEl(), {
                    position: 'absolute',
                    top: 1 + ~(o.width / 2) + 'px',
                    transform: o.hwaccel ? 'translate3d(0,0,0)' : '',
                    opacity: o.opacity,
                    animation: useCssAnimations && addAnimation(o.opacity, o.trail, i, o.lines) + ' ' + 1 / o.speed + 's linear infinite'
                })

                if (o.shadow) ins(seg, css(fill('#000', '0 0 4px ' + '#000'), {
                    top: 2 + 'px'
                }))

                ins(el, ins(seg, fill(o.color, '0 0 1px rgba(0,0,0,.1)')))
            }
            return el
        },

        opacity: function (el, i, val) {
            if (i < el.childNodes.length) el.childNodes[i].style.opacity = val
        }

    })

    /////////////////////////////////////////////////////////////////////////
    // VML rendering for IE
    /////////////////////////////////////////////////////////////////////////

    /**
     * Check and init VML support
     */
    ;
    (function () {

        function vml(tag, attr) {
            return createEl('<' + tag + ' xmlns="urn:schemas-microsoft.com:vml" class="spin-vml">', attr)
        }

        var s = css(createEl('group'), {
            behavior: 'url(#default#VML)'
        })

        if (!vendor(s, 'transform') && s.adj) {

            // VML support detected. Insert CSS rule ...
            sheet.addRule('.spin-vml', 'behavior:url(#default#VML)')

            Spinner.prototype.lines = function (el, o) {
                var r = o.length + o.width,
                    s = 2 * r

                    function grp() {
                        return css(
                            vml('group', {
                                coordsize: s + ' ' + s,
                                coordorigin: -r + ' ' + -r
                            }), {
                                width: s,
                                height: s
                            }
                        )
                    }

                var margin = -(o.width + o.length) * 2 + 'px',
                    g = css(grp(), {
                        position: 'absolute',
                        top: margin,
                        left: margin
                    }),
                    i

                    function seg(i, dx, filter) {
                        ins(g,
                            ins(css(grp(), {
                                    rotation: 360 / o.lines * i + 'deg',
                                    left: ~~dx
                                }),
                                ins(css(vml('roundrect', {
                                        arcsize: o.corners
                                    }), {
                                        width: r,
                                        height: o.width,
                                        left: o.radius,
                                        top: -o.width >> 1,
                                        filter: filter
                                    }),
                                    vml('fill', {
                                        color: o.color,
                                        opacity: o.opacity
                                    }),
                                    vml('stroke', {
                                        opacity: 0
                                    }) // transparent stroke to fix color bleeding upon opacity change
                                )
                            )
                        )
                    }

                if (o.shadow)
                    for (i = 1; i <= o.lines; i++)
                        seg(i, -2, 'progid:DXImageTransform.Microsoft.Blur(pixelradius=2,makeshadow=1,shadowopacity=.3)')

                for (i = 1; i <= o.lines; i++) seg(i)
                return ins(el, g)
            }

            Spinner.prototype.opacity = function (el, i, val, o) {
                var c = el.firstChild
                o = o.shadow && o.lines || 0
                if (c && i + o < c.childNodes.length) {
                    c = c.childNodes[i + o];
                    c = c && c.firstChild;
                    c = c && c.firstChild
                    if (c) c.opacity = val
                }
            }
        } else
            useCssAnimations = vendor(s, 'animation')
    })()

    if (typeof define == 'function' && define.amd)
        define(function () {
            return Spinner
        })
    else
        window.Spinner = Spinner

}(window, document);

/*
 * Ideas from https://gist.github.com/its-florida/1290439 are acknowledged and used here.
 * Resulting file is heavily modified from that gist so is licensed under AUI's license.
 *
 * You can now create a spinner using any of the variants below:
 *
 * $("#el").spin(); // Produces default Spinner using the text color of #el.
 * $("#el").spin("small"); // Produces a 'small' Spinner using the text color of #el.
 * $("#el").spin("large", { ... }); // Produces a 'large' Spinner with your custom settings.
 * $("#el").spin({ ... }); // Produces a Spinner using your custom settings.
 *
 * $("#el").spin(false); // Kills the spinner.
 * $("#el").spinStop(); // Also kills the spinner.
 *
 */
(function ($) {
    $.fn.spin = function (optsOrPreset, opts) {
        var preset, options;

        if (typeof optsOrPreset === 'string') {
            if (!optsOrPreset in $.fn.spin.presets) {
                throw new Error("Preset '" + optsOrPreset + "' isn't defined");
            }
            preset = $.fn.spin.presets[optsOrPreset];
            options = opts || {};
        } else {
            if (opts) {
                throw new Error('Invalid arguments. Accepted arguments:\n' +
                    '$.spin([String preset[, Object options]]),\n' +
                    '$.spin(Object options),\n' +
                    '$.spin(Boolean shouldSpin)');
            }
            preset = $.fn.spin.presets.small;
            options = $.isPlainObject(optsOrPreset) ? optsOrPreset : {};
        }

        if (window.Spinner) {
            return this.each(function () {
                var $this = $(this),
                    data = $this.data();

                if (data.spinner) {
                    data.spinner.stop();
                    delete data.spinner;
                }

                if (optsOrPreset === false) { // just stop it spinning.
                    return;
                }

                options = $.extend({
                    color: $this.css('color')
                }, preset, options);
                data.spinner = new Spinner(options).spin(this);
            });
        } else {
            throw "Spinner class not available.";
        }
    };
    $.fn.spin.presets = {
        "small": {
            lines: 12,
            length: 3,
            width: 2,
            radius: 3,
            trail: 60,
            speed: 1.5
        },
        "medium": {
            lines: 12,
            length: 5,
            width: 3,
            radius: 8,
            trail: 60,
            speed: 1.5
        },
        "large": {
            lines: 12,
            length: 8,
            width: 4,
            radius: 10,
            trail: 60,
            speed: 1.5
        }
    };

    $.fn.spinStop = function () {
        if (window.Spinner) {
            return this.each(function () {
                var $this = $(this),
                    data = $this.data();

                if (data.spinner) {
                    data.spinner.stop();
                    delete data.spinner;
                }

            });
        } else {
            throw "Spinner class not available.";
        }
    };
})(jQuery);

/*! AUI Flat Pack - version 5.2 - generated 2013-07-25 10:18:38 +0000 */


/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var goog = goog || {};
goog.inherits = function (B, A) {
    function C() {}
    C.prototype = A.prototype;
    B.superClass_ = A.prototype;
    B.prototype = new C();
    B.prototype.constructor = B
};
if (!goog.userAgent) {
    goog.userAgent = (function () {
        var B = "";
        if ("undefined" !== typeof navigator && navigator && "string" == typeof navigator.userAgent) {
            B = navigator.userAgent
        }
        var A = B.indexOf("Opera") == 0;
        return {
            HAS_JSCRIPT: typeof "ScriptEngine" in this,
            IS_OPERA: A,
            IS_IE: !A && B.indexOf("MSIE") != -1,
            IS_WEBKIT: !A && B.indexOf("WebKit") != -1
        }
    })()
}
if (!goog.asserts) {
    goog.asserts = {
        fail: function (A) {}
    }
}
if (!goog.dom) {
    goog.dom = {};
    goog.dom.DomHelper = function (A) {
        this.document_ = A || document
    };
    goog.dom.DomHelper.prototype.getDocument = function () {
        return this.document_
    };
    goog.dom.DomHelper.prototype.createElement = function (A) {
        return this.document_.createElement(A)
    };
    goog.dom.DomHelper.prototype.createDocumentFragment = function () {
        return this.document_.createDocumentFragment()
    }
}
if (!goog.format) {
    goog.format = {
        insertWordBreaks: function (I, A) {
            I = String(I);
            var F = [];
            var H = 0;
            var B = false;
            var J = false;
            var G = 0;
            var D = 0;
            for (var E = 0, C = I.length; E < C;
                ++E) {
                var K = I.charCodeAt(E);
                if (G >= A && K != 32) {
                    F[H++] = I.substring(D, E);
                    D = E;
                    F[H++] = goog.format.WORD_BREAK;
                    G = 0
                }
                if (B) {
                    if (K == 62) {
                        B = false
                    }
                } else {
                    if (J) {
                        switch (K) {
                        case 59:
                            J = false;
                            ++G;
                            break;
                        case 60:
                            J = false;
                            B = true;
                            break;
                        case 32:
                            J = false;
                            G = 0;
                            break
                        }
                    } else {
                        switch (K) {
                        case 60:
                            B = true;
                            break;
                        case 38:
                            J = true;
                            break;
                        case 32:
                            G = 0;
                            break;
                        default:
                            ++G;
                            break
                        }
                    }
                }
            }
            F[H++] = I.substring(D);
            return F.join("")
        },
        WORD_BREAK: goog.userAgent.IS_WEBKIT ? "<wbr></wbr>" : goog.userAgent.IS_OPERA ? "&shy;" : "<wbr>"
    }
}
if (!goog.i18n) {
    goog.i18n = {
        bidi: {
            detectRtlDirectionality: function (B, A) {
                B = soyshim.$$bidiStripHtmlIfNecessary_(B, A);
                return soyshim.$$bidiRtlWordRatio_(B) > soyshim.$$bidiRtlDetectionThreshold_
            }
        }
    }
}
goog.i18n.bidi.Dir = {
    RTL: -1,
    UNKNOWN: 0,
    LTR: 1
};
goog.i18n.bidi.toDir = function (A) {
    if (typeof A == "number") {
        return A > 0 ? goog.i18n.bidi.Dir.LTR : A < 0 ? goog.i18n.bidi.Dir.RTL : goog.i18n.bidi.Dir.UNKNOWN
    } else {
        return A ? goog.i18n.bidi.Dir.RTL : goog.i18n.bidi.Dir.LTR
    }
};
goog.i18n.BidiFormatter = function (A) {
    this.dir_ = goog.i18n.bidi.toDir(A)
};
goog.i18n.BidiFormatter.prototype.dirAttr = function (C, A) {
    var B = soy.$$bidiTextDir(C, A);
    return B && B != this.dir_ ? B < 0 ? "dir=rtl" : "dir=ltr" : ""
};
goog.i18n.BidiFormatter.prototype.endEdge = function () {
    return this.dir_ < 0 ? "left" : "right"
};
goog.i18n.BidiFormatter.prototype.mark = function () {
    return ((this.dir_ > 0) ? "\u200E" : (this.dir_ < 0) ? "\u200F" : "")
};
goog.i18n.BidiFormatter.prototype.markAfter = function (C, A) {
    var B = soy.$$bidiTextDir(C, A);
    return soyshim.$$bidiMarkAfterKnownDir_(this.dir_, B, C, A)
};
goog.i18n.BidiFormatter.prototype.spanWrap = function (D, C) {
    D = String(D);
    var B = soy.$$bidiTextDir(D, true);
    var A = soyshim.$$bidiMarkAfterKnownDir_(this.dir_, B, D, true);
    if (B > 0 && this.dir_ <= 0) {
        D = "<span dir=ltr>" + D + "</span>"
    } else {
        if (B < 0 && this.dir_ >= 0) {
            D = "<span dir=rtl>" + D + "</span>"
        }
    }
    return D + A
};
goog.i18n.BidiFormatter.prototype.startEdge = function () {
    return this.dir_ < 0 ? "right" : "left"
};
goog.i18n.BidiFormatter.prototype.unicodeWrap = function (D, C) {
    D = String(D);
    var B = soy.$$bidiTextDir(D, true);
    var A = soyshim.$$bidiMarkAfterKnownDir_(this.dir_, B, D, true);
    if (B > 0 && this.dir_ <= 0) {
        D = "\u202A" + D + "\u202C"
    } else {
        if (B < 0 && this.dir_ >= 0) {
            D = "\u202B" + D + "\u202C"
        }
    }
    return D + A
};
goog.string = {
    newLineToBr: function (B, A) {
        B = String(B);
        if (!goog.string.NEWLINE_TO_BR_RE_.test(B)) {
            return B
        }
        return B.replace(/(\r\n|\r|\n)/g, A ? "<br />" : "<br>")
    },
    urlEncode: encodeURIComponent,
    NEWLINE_TO_BR_RE_: /[\r\n]/
};
goog.string.StringBuffer = function (A, B) {
    this.buffer_ = goog.userAgent.HAS_JSCRIPT ? [] : "";
    if (A != null) {
        this.append.apply(this, arguments)
    }
};
goog.string.StringBuffer.prototype.bufferLength_ = 0;
goog.string.StringBuffer.prototype.append = function (C, B, E) {
    if (goog.userAgent.HAS_JSCRIPT) {
        if (B == null) {
            this.buffer_[this.bufferLength_++] = C
        } else {
            var A = this.buffer_;
            A.push.apply(A, arguments);
            this.bufferLength_ = this.buffer_.length
        }
    } else {
        this.buffer_ += C;
        if (B != null) {
            for (var D = 1; D < arguments.length; D++) {
                this.buffer_ += arguments[D]
            }
        }
    }
    return this
};
goog.string.StringBuffer.prototype.clear = function () {
    if (goog.userAgent.HAS_JSCRIPT) {
        this.buffer_.length = 0;
        this.bufferLength_ = 0
    } else {
        this.buffer_ = ""
    }
};
goog.string.StringBuffer.prototype.toString = function () {
    if (goog.userAgent.HAS_JSCRIPT) {
        var A = this.buffer_.join("");
        this.clear();
        if (A) {
            this.append(A)
        }
        return A
    } else {
        return (this.buffer_)
    }
};
if (!goog.soy) {
    goog.soy = {
        renderAsElement: function (C, A, B, D) {
            return (soyshim.$$renderWithWrapper_(C, A, D, true, B))
        },
        renderAsFragment: function (C, A, B, D) {
            return soyshim.$$renderWithWrapper_(C, A, D, false, B)
        },
        renderElement: function (C, D, A, B) {
            C.innerHTML = D(A, null, B)
        }
    }
}
var soy = {
    esc: {}
};
var soydata = {};
var soyshim = {
    $$DEFAULT_TEMPLATE_DATA_: {}
};
soyshim.$$renderWithWrapper_ = function (H, F, B, C, I) {
    var D = B || document;
    var A = D.createElement("div");
    A.innerHTML = H(F || soyshim.$$DEFAULT_TEMPLATE_DATA_, undefined, I);
    if (A.childNodes.length == 1) {
        var G = A.firstChild;
        if (!C || G.nodeType == 1) {
            return (G)
        }
    }
    if (C) {
        return A
    }
    var E = D.createDocumentFragment();
    while (A.firstChild) {
        E.appendChild(A.firstChild)
    }
    return E
};
soyshim.$$bidiMarkAfterKnownDir_ = function (C, B, D, A) {
    return (C > 0 && (B < 0 || soyshim.$$bidiIsRtlExitText_(D, A)) ? "\u200E" : C < 0 && (B > 0 || soyshim.$$bidiIsLtrExitText_(D, A)) ? "\u200F" : "")
};
soyshim.$$bidiStripHtmlIfNecessary_ = function (B, A) {
    return A ? B.replace(soyshim.$$BIDI_HTML_SKIP_RE_, " ") : B
};
soyshim.$$BIDI_HTML_SKIP_RE_ = /<[^>]*>|&[^;]+;/g;
soyshim.$$bidiLtrChars_ = "A-Za-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02B8\u0300-\u0590\u0800-\u1FFF\u2C00-\uFB1C\uFDFE-\uFE6F\uFEFD-\uFFFF";
soyshim.$$bidiNeutralChars_ = "\u0000-\u0020!-@[-`{-\u00BF\u00D7\u00F7\u02B9-\u02FF\u2000-\u2BFF";
soyshim.$$bidiRtlChars_ = "\u0591-\u07FF\uFB1D-\uFDFD\uFE70-\uFEFC";
soyshim.$$bidiRtlDirCheckRe_ = new RegExp("^[^" + soyshim.$$bidiLtrChars_ + "]*[" + soyshim.$$bidiRtlChars_ + "]");
soyshim.$$bidiNeutralDirCheckRe_ = new RegExp("^[" + soyshim.$$bidiNeutralChars_ + "]*$|^http://");
soyshim.$$bidiIsRtlText_ = function (A) {
    return soyshim.$$bidiRtlDirCheckRe_.test(A)
};
soyshim.$$bidiIsNeutralText_ = function (A) {
    return soyshim.$$bidiNeutralDirCheckRe_.test(A)
};
soyshim.$$bidiRtlDetectionThreshold_ = 0.4;
soyshim.$$bidiRtlWordRatio_ = function (E) {
    var B = 0;
    var A = 0;
    var D = E.split(" ");
    for (var C = 0; C < D.length; C++) {
        if (soyshim.$$bidiIsRtlText_(D[C])) {
            B++;
            A++
        } else {
            if (!soyshim.$$bidiIsNeutralText_(D[C])) {
                A++
            }
        }
    }
    return A == 0 ? 0 : B / A
};
soyshim.$$bidiLtrExitDirCheckRe_ = new RegExp("[" + soyshim.$$bidiLtrChars_ + "][^" + soyshim.$$bidiRtlChars_ + "]*$");
soyshim.$$bidiRtlExitDirCheckRe_ = new RegExp("[" + soyshim.$$bidiRtlChars_ + "][^" + soyshim.$$bidiLtrChars_ + "]*$");
soyshim.$$bidiIsLtrExitText_ = function (B, A) {
    B = soyshim.$$bidiStripHtmlIfNecessary_(B, A);
    return soyshim.$$bidiLtrExitDirCheckRe_.test(B)
};
soyshim.$$bidiIsRtlExitText_ = function (B, A) {
    B = soyshim.$$bidiStripHtmlIfNecessary_(B, A);
    return soyshim.$$bidiRtlExitDirCheckRe_.test(B)
};
soy.StringBuilder = goog.string.StringBuffer;
soydata.SanitizedContentKind = {
    HTML: 0,
    JS_STR_CHARS: 1,
    URI: 2,
    HTML_ATTRIBUTE: 3
};
soydata.SanitizedContent = function (A) {
    this.content = A
};
soydata.SanitizedContent.prototype.contentKind;
soydata.SanitizedContent.prototype.toString = function () {
    return this.content
};
soydata.SanitizedHtml = function (A) {
    soydata.SanitizedContent.call(this, A)
};
goog.inherits(soydata.SanitizedHtml, soydata.SanitizedContent);
soydata.SanitizedHtml.prototype.contentKind = soydata.SanitizedContentKind.HTML;
soydata.SanitizedJsStrChars = function (A) {
    soydata.SanitizedContent.call(this, A)
};
goog.inherits(soydata.SanitizedJsStrChars, soydata.SanitizedContent);
soydata.SanitizedJsStrChars.prototype.contentKind = soydata.SanitizedContentKind.JS_STR_CHARS;
soydata.SanitizedUri = function (A) {
    soydata.SanitizedContent.call(this, A)
};
goog.inherits(soydata.SanitizedUri, soydata.SanitizedContent);
soydata.SanitizedUri.prototype.contentKind = soydata.SanitizedContentKind.URI;
soydata.SanitizedHtmlAttribute = function (A) {
    soydata.SanitizedContent.call(this, A)
};
goog.inherits(soydata.SanitizedHtmlAttribute, soydata.SanitizedContent);
soydata.SanitizedHtmlAttribute.prototype.contentKind = soydata.SanitizedContentKind.HTML_ATTRIBUTE;
soy.renderElement = goog.soy.renderElement;
soy.renderAsFragment = function (D, B, A, C) {
    return goog.soy.renderAsFragment(D, B, C, new goog.dom.DomHelper(A))
};
soy.renderAsElement = function (D, B, A, C) {
    return goog.soy.renderAsElement(D, B, C, new goog.dom.DomHelper(A))
};
soy.$$augmentData = function (D, A) {
    function E() {}
    E.prototype = D;
    var C = new E();
    for (var B in A) {
        C[B] = A[B]
    }
    return C
};
soy.$$getMapKeys = function (C) {
    var B = [];
    for (var A in C) {
        B.push(A)
    }
    return B
};
soy.$$getDelegateId = function (A) {
    return A
};
soy.$$DELEGATE_REGISTRY_PRIORITIES_ = {};
soy.$$DELEGATE_REGISTRY_FUNCTIONS_ = {};
soy.$$registerDelegateFn = function (E, C, B) {
    var D = "key_" + E;
    var A = soy.$$DELEGATE_REGISTRY_PRIORITIES_[D];
    if (A === undefined || C > A) {
        soy.$$DELEGATE_REGISTRY_PRIORITIES_[D] = C;
        soy.$$DELEGATE_REGISTRY_FUNCTIONS_[D] = B
    } else {
        if (C == A) {
            throw Error('Encountered two active delegates with same priority (id/name "' + E + '").')
        } else {}
    }
};
soy.$$getDelegateFn = function (B) {
    var A = soy.$$DELEGATE_REGISTRY_FUNCTIONS_["key_" + B];
    return A ? A : soy.$$EMPTY_TEMPLATE_FN_
};
soy.$$EMPTY_TEMPLATE_FN_ = function (A, C, B) {
    return ""
};
soy.$$escapeHtml = function (A) {
    if (typeof A === "object" && A && A.contentKind === soydata.SanitizedContentKind.HTML) {
        return A.content
    }
    return soy.esc.$$escapeHtmlHelper(A)
};
soy.$$escapeHtmlRcdata = function (A) {
    if (typeof A === "object" && A && A.contentKind === soydata.SanitizedContentKind.HTML) {
        return soy.esc.$$normalizeHtmlHelper(A.content)
    }
    return soy.esc.$$escapeHtmlHelper(A)
};
soy.$$stripHtmlTags = function (A) {
    return String(A).replace(soy.esc.$$HTML_TAG_REGEX_, "")
};
soy.$$escapeHtmlAttribute = function (A) {
    if (typeof A === "object" && A && A.contentKind === soydata.SanitizedContentKind.HTML) {
        return soy.esc.$$normalizeHtmlHelper(soy.$$stripHtmlTags(A.content))
    }
    return soy.esc.$$escapeHtmlHelper(A)
};
soy.$$escapeHtmlAttributeNospace = function (A) {
    if (typeof A === "object" && A && A.contentKind === soydata.SanitizedContentKind.HTML) {
        return soy.esc.$$normalizeHtmlNospaceHelper(soy.$$stripHtmlTags(A.content))
    }
    return soy.esc.$$escapeHtmlNospaceHelper(A)
};
soy.$$filterHtmlAttribute = function (A) {
    if (typeof A === "object" && A && A.contentKind === soydata.SanitizedContentKind.HTML_ATTRIBUTE) {
        return A.content.replace(/=([^"']*)$/, '="$1"')
    }
    return soy.esc.$$filterHtmlAttributeHelper(A)
};
soy.$$filterHtmlElementName = function (A) {
    return soy.esc.$$filterHtmlElementNameHelper(A)
};
soy.$$escapeJs = function (A) {
    return soy.$$escapeJsString(A)
};
soy.$$escapeJsString = function (A) {
    if (typeof A === "object" && A.contentKind === soydata.SanitizedContentKind.JS_STR_CHARS) {
        return A.content
    }
    return soy.esc.$$escapeJsStringHelper(A)
};
soy.$$escapeJsValue = function (A) {
    if (A == null) {
        return " null "
    }
    switch (typeof A) {
    case "boolean":
    case "number":
        return " " + A + " ";
    default:
        return "'" + soy.esc.$$escapeJsStringHelper(String(A)) + "'"
    }
};
soy.$$escapeJsRegex = function (A) {
    return soy.esc.$$escapeJsRegexHelper(A)
};
soy.$$problematicUriMarks_ = /['()]/g;
soy.$$pctEncode_ = function (A) {
    return "%" + A.charCodeAt(0).toString(16)
};
soy.$$escapeUri = function (A) {
    if (typeof A === "object" && A.contentKind === soydata.SanitizedContentKind.URI) {
        return soy.$$normalizeUri(A)
    }
    var B = soy.esc.$$escapeUriHelper(A);
    soy.$$problematicUriMarks_.lastIndex = 0;
    if (soy.$$problematicUriMarks_.test(B)) {
        return B.replace(soy.$$problematicUriMarks_, soy.$$pctEncode_)
    }
    return B
};
soy.$$normalizeUri = function (A) {
    return soy.esc.$$normalizeUriHelper(A)
};
soy.$$filterNormalizeUri = function (A) {
    return soy.esc.$$filterNormalizeUriHelper(A)
};
soy.$$escapeCssString = function (A) {
    return soy.esc.$$escapeCssStringHelper(A)
};
soy.$$filterCssValue = function (A) {
    if (A == null) {
        return ""
    }
    return soy.esc.$$filterCssValueHelper(A)
};
soy.$$changeNewlineToBr = function (A) {
    return goog.string.newLineToBr(String(A), false)
};
soy.$$insertWordBreaks = function (B, A) {
    return goog.format.insertWordBreaks(String(B), A)
};
soy.$$truncate = function (C, A, B) {
    C = String(C);
    if (C.length <= A) {
        return C
    }
    if (B) {
        if (A > 3) {
            A -= 3
        } else {
            B = false
        }
    }
    if (soy.$$isHighSurrogate_(C.charAt(A - 1)) && soy.$$isLowSurrogate_(C.charAt(A))) {
        A -= 1
    }
    C = C.substring(0, A);
    if (B) {
        C += "..."
    }
    return C
};
soy.$$isHighSurrogate_ = function (A) {
    return 55296 <= A && A <= 56319
};
soy.$$isLowSurrogate_ = function (A) {
    return 56320 <= A && A <= 57343
};
soy.$$bidiFormatterCache_ = {};
soy.$$getBidiFormatterInstance_ = function (A) {
    return soy.$$bidiFormatterCache_[A] || (soy.$$bidiFormatterCache_[A] = new goog.i18n.BidiFormatter(A))
};
soy.$$bidiTextDir = function (B, A) {
    if (!B) {
        return 0
    }
    return goog.i18n.bidi.detectRtlDirectionality(B, A) ? -1 : 1
};
soy.$$bidiDirAttr = function (B, C, A) {
    return new soydata.SanitizedHtmlAttribute(soy.$$getBidiFormatterInstance_(B).dirAttr(C, A))
};
soy.$$bidiMarkAfter = function (B, D, A) {
    var C = soy.$$getBidiFormatterInstance_(B);
    return C.markAfter(D, A)
};
soy.$$bidiSpanWrap = function (A, C) {
    var B = soy.$$getBidiFormatterInstance_(A);
    return B.spanWrap(C + "", true)
};
soy.$$bidiUnicodeWrap = function (A, C) {
    var B = soy.$$getBidiFormatterInstance_(A);
    return B.unicodeWrap(C + "", true)
};
soy.esc.$$escapeUriHelper = function (A) {
    return encodeURIComponent(String(A))
};
soy.esc.$$ESCAPE_MAP_FOR_ESCAPE_HTML__AND__NORMALIZE_HTML__AND__ESCAPE_HTML_NOSPACE__AND__NORMALIZE_HTML_NOSPACE_ = {
    "\x00": "\x26#0;",
    "\x22": "\x26quot;",
    "\x26": "\x26amp;",
    "\x27": "\x26#39;",
    "\x3c": "\x26lt;",
    "\x3e": "\x26gt;",
    "\x09": "\x26#9;",
    "\x0a": "\x26#10;",
    "\x0b": "\x26#11;",
    "\x0c": "\x26#12;",
    "\x0d": "\x26#13;",
    " ": "\x26#32;",
    "-": "\x26#45;",
    "/": "\x26#47;",
    "\x3d": "\x26#61;",
    "`": "\x26#96;",
    "\x85": "\x26#133;",
    "\xa0": "\x26#160;",
    "\u2028": "\x26#8232;",
    "\u2029": "\x26#8233;"
};
soy.esc.$$REPLACER_FOR_ESCAPE_HTML__AND__NORMALIZE_HTML__AND__ESCAPE_HTML_NOSPACE__AND__NORMALIZE_HTML_NOSPACE_ = function (A) {
    return soy.esc.$$ESCAPE_MAP_FOR_ESCAPE_HTML__AND__NORMALIZE_HTML__AND__ESCAPE_HTML_NOSPACE__AND__NORMALIZE_HTML_NOSPACE_[A]
};
soy.esc.$$ESCAPE_MAP_FOR_ESCAPE_JS_STRING__AND__ESCAPE_JS_REGEX_ = {
    "\x00": "\\x00",
    "\x08": "\\x08",
    "\x09": "\\t",
    "\x0a": "\\n",
    "\x0b": "\\x0b",
    "\x0c": "\\f",
    "\x0d": "\\r",
    "\x22": "\\x22",
    "\x26": "\\x26",
    "\x27": "\\x27",
    "/": "\\/",
    "\x3c": "\\x3c",
    "\x3d": "\\x3d",
    "\x3e": "\\x3e",
    "\\": "\\\\",
    "\x85": "\\x85",
    "\u2028": "\\u2028",
    "\u2029": "\\u2029",
    "$": "\\x24",
    "(": "\\x28",
    ")": "\\x29",
    "*": "\\x2a",
    "+": "\\x2b",
    ",": "\\x2c",
    "-": "\\x2d",
    ".": "\\x2e",
    ":": "\\x3a",
    "?": "\\x3f",
    "[": "\\x5b",
    "]": "\\x5d",
    "^": "\\x5e",
    "{": "\\x7b",
    "|": "\\x7c",
    "}": "\\x7d"
};
soy.esc.$$REPLACER_FOR_ESCAPE_JS_STRING__AND__ESCAPE_JS_REGEX_ = function (A) {
    return soy.esc.$$ESCAPE_MAP_FOR_ESCAPE_JS_STRING__AND__ESCAPE_JS_REGEX_[A]
};
soy.esc.$$ESCAPE_MAP_FOR_ESCAPE_CSS_STRING_ = {
    "\x00": "\\0 ",
    "\x08": "\\8 ",
    "\x09": "\\9 ",
    "\x0a": "\\a ",
    "\x0b": "\\b ",
    "\x0c": "\\c ",
    "\x0d": "\\d ",
    "\x22": "\\22 ",
    "\x26": "\\26 ",
    "\x27": "\\27 ",
    "(": "\\28 ",
    ")": "\\29 ",
    "*": "\\2a ",
    "/": "\\2f ",
    ":": "\\3a ",
    ";": "\\3b ",
    "\x3c": "\\3c ",
    "\x3d": "\\3d ",
    "\x3e": "\\3e ",
    "@": "\\40 ",
    "\\": "\\5c ",
    "{": "\\7b ",
    "}": "\\7d ",
    "\x85": "\\85 ",
    "\xa0": "\\a0 ",
    "\u2028": "\\2028 ",
    "\u2029": "\\2029 "
};
soy.esc.$$REPLACER_FOR_ESCAPE_CSS_STRING_ = function (A) {
    return soy.esc.$$ESCAPE_MAP_FOR_ESCAPE_CSS_STRING_[A]
};
soy.esc.$$ESCAPE_MAP_FOR_NORMALIZE_URI__AND__FILTER_NORMALIZE_URI_ = {
    "\x00": "%00",
    "\x01": "%01",
    "\x02": "%02",
    "\x03": "%03",
    "\x04": "%04",
    "\x05": "%05",
    "\x06": "%06",
    "\x07": "%07",
    "\x08": "%08",
    "\x09": "%09",
    "\x0a": "%0A",
    "\x0b": "%0B",
    "\x0c": "%0C",
    "\x0d": "%0D",
    "\x0e": "%0E",
    "\x0f": "%0F",
    "\x10": "%10",
    "\x11": "%11",
    "\x12": "%12",
    "\x13": "%13",
    "\x14": "%14",
    "\x15": "%15",
    "\x16": "%16",
    "\x17": "%17",
    "\x18": "%18",
    "\x19": "%19",
    "\x1a": "%1A",
    "\x1b": "%1B",
    "\x1c": "%1C",
    "\x1d": "%1D",
    "\x1e": "%1E",
    "\x1f": "%1F",
    " ": "%20",
    "\x22": "%22",
    "\x27": "%27",
    "(": "%28",
    ")": "%29",
    "\x3c": "%3C",
    "\x3e": "%3E",
    "\\": "%5C",
    "{": "%7B",
    "}": "%7D",
    "\x7f": "%7F",
    "\x85": "%C2%85",
    "\xa0": "%C2%A0",
    "\u2028": "%E2%80%A8",
    "\u2029": "%E2%80%A9",
    "\uff01": "%EF%BC%81",
    "\uff03": "%EF%BC%83",
    "\uff04": "%EF%BC%84",
    "\uff06": "%EF%BC%86",
    "\uff07": "%EF%BC%87",
    "\uff08": "%EF%BC%88",
    "\uff09": "%EF%BC%89",
    "\uff0a": "%EF%BC%8A",
    "\uff0b": "%EF%BC%8B",
    "\uff0c": "%EF%BC%8C",
    "\uff0f": "%EF%BC%8F",
    "\uff1a": "%EF%BC%9A",
    "\uff1b": "%EF%BC%9B",
    "\uff1d": "%EF%BC%9D",
    "\uff1f": "%EF%BC%9F",
    "\uff20": "%EF%BC%A0",
    "\uff3b": "%EF%BC%BB",
    "\uff3d": "%EF%BC%BD"
};
soy.esc.$$REPLACER_FOR_NORMALIZE_URI__AND__FILTER_NORMALIZE_URI_ = function (A) {
    return soy.esc.$$ESCAPE_MAP_FOR_NORMALIZE_URI__AND__FILTER_NORMALIZE_URI_[A]
};
soy.esc.$$MATCHER_FOR_ESCAPE_HTML_ = /[\x00\x22\x26\x27\x3c\x3e]/g;
soy.esc.$$MATCHER_FOR_NORMALIZE_HTML_ = /[\x00\x22\x27\x3c\x3e]/g;
soy.esc.$$MATCHER_FOR_ESCAPE_HTML_NOSPACE_ = /[\x00\x09-\x0d \x22\x26\x27\x2d\/\x3c-\x3e`\x85\xa0\u2028\u2029]/g;
soy.esc.$$MATCHER_FOR_NORMALIZE_HTML_NOSPACE_ = /[\x00\x09-\x0d \x22\x27\x2d\/\x3c-\x3e`\x85\xa0\u2028\u2029]/g;
soy.esc.$$MATCHER_FOR_ESCAPE_JS_STRING_ = /[\x00\x08-\x0d\x22\x26\x27\/\x3c-\x3e\\\x85\u2028\u2029]/g;
soy.esc.$$MATCHER_FOR_ESCAPE_JS_REGEX_ = /[\x00\x08-\x0d\x22\x24\x26-\/\x3a\x3c-\x3f\x5b-\x5e\x7b-\x7d\x85\u2028\u2029]/g;
soy.esc.$$MATCHER_FOR_ESCAPE_CSS_STRING_ = /[\x00\x08-\x0d\x22\x26-\x2a\/\x3a-\x3e@\\\x7b\x7d\x85\xa0\u2028\u2029]/g;
soy.esc.$$MATCHER_FOR_NORMALIZE_URI__AND__FILTER_NORMALIZE_URI_ = /[\x00- \x22\x27-\x29\x3c\x3e\\\x7b\x7d\x7f\x85\xa0\u2028\u2029\uff01\uff03\uff04\uff06-\uff0c\uff0f\uff1a\uff1b\uff1d\uff1f\uff20\uff3b\uff3d]/g;
soy.esc.$$FILTER_FOR_FILTER_CSS_VALUE_ = /^(?!-*(?:expression|(?:moz-)?binding))(?:[.#]?-?(?:[_a-z0-9-]+)(?:-[_a-z0-9-]+)*-?|-?(?:[0-9]+(?:\.[0-9]*)?|\.[0-9]+)(?:[a-z]{1,2}|%)?|!important|)$/i;
soy.esc.$$FILTER_FOR_FILTER_NORMALIZE_URI_ = /^(?:(?:https?|mailto):|[^&:\/?#]*(?:[\/?#]|$))/i;
soy.esc.$$FILTER_FOR_FILTER_HTML_ATTRIBUTE_ = /^(?!style|on|action|archive|background|cite|classid|codebase|data|dsync|href|longdesc|src|usemap)(?:[a-z0-9_$:-]*)$/i;
soy.esc.$$FILTER_FOR_FILTER_HTML_ELEMENT_NAME_ = /^(?!script|style|title|textarea|xmp|no)[a-z0-9_$:-]*$/i;
soy.esc.$$escapeHtmlHelper = function (A) {
    var B = String(A);
    return B.replace(soy.esc.$$MATCHER_FOR_ESCAPE_HTML_, soy.esc.$$REPLACER_FOR_ESCAPE_HTML__AND__NORMALIZE_HTML__AND__ESCAPE_HTML_NOSPACE__AND__NORMALIZE_HTML_NOSPACE_)
};
soy.esc.$$normalizeHtmlHelper = function (A) {
    var B = String(A);
    return B.replace(soy.esc.$$MATCHER_FOR_NORMALIZE_HTML_, soy.esc.$$REPLACER_FOR_ESCAPE_HTML__AND__NORMALIZE_HTML__AND__ESCAPE_HTML_NOSPACE__AND__NORMALIZE_HTML_NOSPACE_)
};
soy.esc.$$escapeHtmlNospaceHelper = function (A) {
    var B = String(A);
    return B.replace(soy.esc.$$MATCHER_FOR_ESCAPE_HTML_NOSPACE_, soy.esc.$$REPLACER_FOR_ESCAPE_HTML__AND__NORMALIZE_HTML__AND__ESCAPE_HTML_NOSPACE__AND__NORMALIZE_HTML_NOSPACE_)
};
soy.esc.$$normalizeHtmlNospaceHelper = function (A) {
    var B = String(A);
    return B.replace(soy.esc.$$MATCHER_FOR_NORMALIZE_HTML_NOSPACE_, soy.esc.$$REPLACER_FOR_ESCAPE_HTML__AND__NORMALIZE_HTML__AND__ESCAPE_HTML_NOSPACE__AND__NORMALIZE_HTML_NOSPACE_)
};
soy.esc.$$escapeJsStringHelper = function (A) {
    var B = String(A);
    return B.replace(soy.esc.$$MATCHER_FOR_ESCAPE_JS_STRING_, soy.esc.$$REPLACER_FOR_ESCAPE_JS_STRING__AND__ESCAPE_JS_REGEX_)
};
soy.esc.$$escapeJsRegexHelper = function (A) {
    var B = String(A);
    return B.replace(soy.esc.$$MATCHER_FOR_ESCAPE_JS_REGEX_, soy.esc.$$REPLACER_FOR_ESCAPE_JS_STRING__AND__ESCAPE_JS_REGEX_)
};
soy.esc.$$escapeCssStringHelper = function (A) {
    var B = String(A);
    return B.replace(soy.esc.$$MATCHER_FOR_ESCAPE_CSS_STRING_, soy.esc.$$REPLACER_FOR_ESCAPE_CSS_STRING_)
};
soy.esc.$$filterCssValueHelper = function (A) {
    var B = String(A);
    if (!soy.esc.$$FILTER_FOR_FILTER_CSS_VALUE_.test(B)) {
        return "zSoyz"
    }
    return B
};
soy.esc.$$normalizeUriHelper = function (A) {
    var B = String(A);
    return B.replace(soy.esc.$$MATCHER_FOR_NORMALIZE_URI__AND__FILTER_NORMALIZE_URI_, soy.esc.$$REPLACER_FOR_NORMALIZE_URI__AND__FILTER_NORMALIZE_URI_)
};
soy.esc.$$filterNormalizeUriHelper = function (A) {
    var B = String(A);
    if (!soy.esc.$$FILTER_FOR_FILTER_NORMALIZE_URI_.test(B)) {
        return "zSoyz"
    }
    return B.replace(soy.esc.$$MATCHER_FOR_NORMALIZE_URI__AND__FILTER_NORMALIZE_URI_, soy.esc.$$REPLACER_FOR_NORMALIZE_URI__AND__FILTER_NORMALIZE_URI_)
};
soy.esc.$$filterHtmlAttributeHelper = function (A) {
    var B = String(A);
    if (!soy.esc.$$FILTER_FOR_FILTER_HTML_ATTRIBUTE_.test(B)) {
        return "zSoyz"
    }
    return B
};
soy.esc.$$filterHtmlElementNameHelper = function (A) {
    var B = String(A);
    if (!soy.esc.$$FILTER_FOR_FILTER_HTML_ELEMENT_NAME_.test(B)) {
        return "zSoyz"
    }
    return B
};
soy.esc.$$HTML_TAG_REGEX_ = /<(?:!|\/?[a-zA-Z])(?:[^>'"]|"[^"]*"|'[^']*')*>/g;
if (typeof aui == "undefined") {
    var aui = {}
}
aui.renderExtraAttributes = function (a, h, g) {
    var d = h || new soy.StringBuilder();
    if (a != null && a.extraAttributes) {
        if (Object.prototype.toString.call(a.extraAttributes) === "[object Object]") {
            var f = soy.$$getMapKeys(a.extraAttributes);
            var c = f.length;
            for (var b = 0; b < c; b++) {
                var e = f[b];
                d.append(" ", soy.$$escapeHtml(e), '="', soy.$$escapeHtml(a.extraAttributes[e]), '"')
            }
        } else {
            d.append(" ", a.extraAttributes)
        }
    }
    return h ? "" : d.toString()
};
aui.renderExtraClasses = function (g, d, h) {
    var c = d || new soy.StringBuilder();
    if (g != null && g.extraClasses) {
        if (g.isFullAttr) {
            c.append(' class="');
            if ((g.extraClasses) instanceof Array) {
                var e = g.extraClasses;
                var i = e.length;
                for (var j = 0; j < i; j++) {
                    var f = e[j];
                    c.append((!(j == 0)) ? " " : "", soy.$$escapeHtml(f))
                }
            } else {
                c.append(soy.$$escapeHtml(g.extraClasses))
            }
            c.append('"')
        } else {
            if ((g.extraClasses) instanceof Array) {
                var l = g.extraClasses;
                var a = l.length;
                for (var b = 0; b < a; b++) {
                    var k = l[b];
                    c.append(" ", soy.$$escapeHtml(k))
                }
            } else {
                c.append(" ", soy.$$escapeHtml(g.extraClasses))
            }
        }
    }
    return d ? "" : c.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.avatar == "undefined") {
    aui.avatar = {}
}
aui.avatar.avatar = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "span"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-avatar aui-avatar-', soy.$$escapeHtml(a.size), soy.$$escapeHtml(a.isProject ? " aui-avatar-project" : ""), soy.$$escapeHtml(a.badgeContent ? " aui-avatar-badged" : ""));
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append('><span class="aui-avatar-inner"><img src="', soy.$$escapeHtml(a.avatarImageUrl), '"', (a.accessibilityText) ? ' alt="' + soy.$$escapeHtml(a.accessibilityText) + '"' : "", (a.title) ? ' title="' + soy.$$escapeHtml(a.title) + '"' : "", (a.imageClasses) ? ' class="' + soy.$$escapeHtml(a.imageClasses) + '"' : "", " /></span>", (a.badgeContent) ? a.badgeContent : "", "</", soy.$$escapeHtml(a.tagName ? a.tagName : "span"), ">");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.badges == "undefined") {
    aui.badges = {}
}
aui.badges.badge = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "span"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-badge');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", soy.$$escapeHtml(a.text), "</", soy.$$escapeHtml(a.tagName ? a.tagName : "span"), ">");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.buttons == "undefined") {
    aui.buttons = {}
}
aui.buttons.button = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    if (a.tagName == "input") {
        b.append('<input type="', soy.$$escapeHtml(a.inputType ? a.inputType : "button"), '" ');
        aui.buttons.buttonAttributes(a, b, c);
        b.append(' value="', soy.$$escapeHtml(a.text), '" />')
    } else {
        b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "button"), " ");
        aui.buttons.buttonAttributes(a, b, c);
        b.append(">");
        aui.buttons.buttonIcon(a, b, c);
        b.append(soy.$$escapeHtml(a.text), "</", soy.$$escapeHtml(a.tagName ? a.tagName : "button"), ">")
    }
    return d ? "" : b.toString()
};
aui.buttons.buttons = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<div", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-buttons');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</div>");
    return d ? "" : b.toString()
};
aui.buttons.buttonAttributes = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append((a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-button', (a.splitButtonType == "main") ? " aui-button-split-main" : "", (a.dropdown2Target) ? " aui-dropdown2-trigger" + ((a.splitButtonType == "more") ? " aui-button-split-more" : "") : "");
    switch (a.type) {
    case "primary":
        b.append(" aui-button-primary");
        break;
    case "link":
        b.append(" aui-button-link");
        break;
    case "subtle":
        b.append(" aui-button-subtle");
        break
    }
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append((a.isPressed) ? ' aria-pressed="' + soy.$$escapeHtml(a.isPressed) + '"' : "", (a.isDisabled) ? ' aria-disabled="' + soy.$$escapeHtml(a.isDisabled) + '"' + ((a.isDisabled == true) ? (a.tagName == "button" || a.tagName == "input") ? ' disabled="disabled" ' : "" : "") : "", (a.dropdown2Target) ? ' aria-owns="' + soy.$$escapeHtml(a.dropdown2Target) + '" aria-haspopup="true"' : "");
    return d ? "" : b.toString()
};
aui.buttons.buttonIcon = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append((a.iconType) ? '<span class="' + ((a.iconType == "aui") ? "aui-icon" : "") + ((a.iconClass) ? " " + soy.$$escapeHtml(a.iconClass) : "") + '">' + ((a.iconText) ? soy.$$escapeHtml(a.iconText) + " " : "") + "</span>" : "");
    return d ? "" : b.toString()
};
aui.buttons.splitButton = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.buttons.button(soy.$$augmentData(a.splitButtonMain, {
        splitButtonType: "main"
    }), b, c);
    aui.buttons.button(soy.$$augmentData(a.splitButtonMore, {
        splitButtonType: "more"
    }), b, c);
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.dropdown == "undefined") {
    aui.dropdown = {}
}
aui.dropdown.trigger = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<a", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-dd-trigger');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append('><span class="dropdown-text">', (a.accessibilityText) ? soy.$$escapeHtml(a.accessibilityText) : "", "</span>", (!(a.showIcon == false)) ? '<span class="icon icon-dropdown"></span>' : "", "</a>");
    return d ? "" : b.toString()
};
aui.dropdown.menu = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "ul"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-dropdown hidden');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "ul"), ">");
    return d ? "" : b.toString()
};
aui.dropdown.parent = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-dd-parent');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ">");
    return d ? "" : b.toString()
};
aui.dropdown.item = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "li"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="dropdown-item');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append('><a href="', soy.$$escapeHtml(a.url ? a.url : "#"), '">', soy.$$escapeHtml(a.text), "</a></", soy.$$escapeHtml(a.tagName ? a.tagName : "li"), ">");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.dropdown2 == "undefined") {
    aui.dropdown2 = {}
}
aui.dropdown2.dropdown2 = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.dropdown2.trigger(soy.$$augmentData(a.trigger, {
        menu: a.menu
    }), b, c);
    aui.dropdown2.contents(a.menu, b, c);
    return d ? "" : b.toString()
};
aui.dropdown2.trigger = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "a"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-dropdown2-trigger');
    aui.renderExtraClasses(a, b, c);
    b.append('" aria-owns="', soy.$$escapeHtml(a.menu.id), '" aria-haspopup="true"', (a.title) ? ' title="' + soy.$$escapeHtml(a.title) + '"' : "", (a.container) ? ' data-container="' + soy.$$escapeHtml(a.container) + '"' : "", ((!a.tagName || a.tagName == "a") && (!a.extraAttributes || Object.prototype.toString.call(a.extraAttributes) === "[object Object]" && !a.extraAttributes.href && !a.extraAttributes.tabindex)) ? ' tabindex="0"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", (a.content) ? a.content : "", (a.text) ? soy.$$escapeHtml(a.text) : "", (!(a.showIcon == false)) ? '<span class="icon aui-icon-dropdown"></span>' : "", "</", soy.$$escapeHtml(a.tagName ? a.tagName : "a"), ">");
    return d ? "" : b.toString()
};
aui.dropdown2.contents = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<div id="', soy.$$escapeHtml(a.id), '" class="aui-dropdown2');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", (a.content) ? a.content : "", "</div>");
    return d ? "" : b.toString()
};
aui.dropdown2.section = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<div", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-dropdown2-section');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", (a.label) ? "<strong>" + soy.$$escapeHtml(a.label) + "</strong>" : "", a.content, "</div>");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.expander == "undefined") {
    aui.expander = {}
}
aui.expander.content = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<div", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-expander-content');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append((a.initiallyExpanded) ? ' aria-expanded="' + soy.$$escapeHtml(a.initiallyExpanded) + '"' : "", ">", (a.content) ? a.content : "", "</div>");
    return d ? "" : b.toString()
};
aui.expander.trigger = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tag ? a.tag : "div"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", (a.replaceText) ? ' data-replace-text="' + soy.$$escapeHtml(a.replaceText) + '"' : "", (a.replaceSelector) ? ' data-replace-selector="' + soy.$$escapeHtml(a.replaceSelector) + '"' : "", ' class="aui-expander-trigger');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(' aria-controls="', soy.$$escapeHtml(a.contentId), '"', (a.collapsible) ? ' data-collapsible="' + soy.$$escapeHtml(a.collapsible) + '"' : "", ">", (a.content) ? a.content : "", "</", soy.$$escapeHtml(a.tag ? a.tag : "div"), ">");
    return d ? "" : b.toString()
};
aui.expander.revealText = function (a, e, c) {
    var b = e || new soy.StringBuilder();
    var d = new soy.StringBuilder(soy.$$escapeHtml(a.contentContent));
    aui.expander.trigger({
        id: a.triggerId,
        contentId: a.contentId,
        tag: "a",
        content: "<span class='reveal-text-trigger-text'>Show more</span>",
        replaceSelector: ".reveal-text-trigger-text",
        replaceText: "Show less",
        extraAttributes: a.triggerExtraAttributes,
        extraClasses: ((a.triggerExtraClasses) ? soy.$$escapeHtml(a.triggerExtraClasses) + "  " : "") + " aui-expander-reveal-text"
    }, d, c);
    aui.expander.content({
        id: a.contentId,
        content: d.toString(),
        extraAttributes: a.contentExtraAttributes,
        extraClasses: a.contentExtraClasses
    }, b, c);
    return e ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.form == "undefined") {
    aui.form = {}
}
aui.form.form = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<form", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui', (a.isUnsectioned) ? " unsectioned" : "", (a.isLongLabels) ? " long-label" : "", (a.isTopLabels) ? " top-label" : "");
    aui.renderExtraClasses(a, b, c);
    b.append('" action="', soy.$$escapeHtml(a.action), '" method="', soy.$$escapeHtml(a.method ? a.method : "post"), '"', (a.enctype) ? 'enctype="' + soy.$$escapeHtml(a.enctype) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</form>");
    return d ? "" : b.toString()
};
aui.form.formDescription = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<div", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="field-group');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</div>");
    return d ? "" : b.toString()
};
aui.form.fieldset = function (a, e, d) {
    var b = e || new soy.StringBuilder();
    var c = a.isInline || a.isDateSelect || a.isGroup || a.extraClasses;
    b.append("<fieldset", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "");
    if (c) {
        b.append(' class="', soy.$$escapeHtml(a.isInline ? "inline" : a.isDateSelect ? "date-select" : a.isGroup ? "group" : ""));
        aui.renderExtraClasses(a, b, d);
        b.append('"')
    }
    aui.renderExtraAttributes(a, b, d);
    b.append("><legend><span>", a.legendContent, "</span></legend>", a.content, "</fieldset>");
    return e ? "" : b.toString()
};
aui.form.fieldGroup = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<div", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="field-group');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</div>");
    return d ? "" : b.toString()
};
aui.form.buttons = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<div class="buttons-container', (a.alignment) ? " " + soy.$$escapeHtml(a.alignment) : "", '"><div class="buttons">', a.content, "</div></div>");
    return d ? "" : b.toString()
};
aui.form.label = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<label", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' for="', soy.$$escapeHtml(a.forField), '"');
    aui.renderExtraClasses(soy.$$augmentData(a, {
        isFullAttr: true
    }), b, c);
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, (a.isRequired) ? '<span class="aui-icon icon-required"></span>' : "", "</label>");
    return d ? "" : b.toString()
};
aui.form.input = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<input", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="', soy.$$escapeHtml(a.type == "password" ? "text" : a.type == "submit" ? "button" : a.type));
    aui.renderExtraClasses(a, b, c);
    b.append('" type="', soy.$$escapeHtml(a.type), '" name="', (a.name) ? soy.$$escapeHtml(a.name) : soy.$$escapeHtml(a.id), '"', (a.value) ? ' value="' + soy.$$escapeHtml(a.value) + '"' : "", ((a.type == "checkbox" || a.type == "radio") && a.isChecked) ? ' checked="checked"' : "", (a.type == "text" && a.maxLength) ? ' maxlength="' + soy.$$escapeHtml(a.maxLength) + '"' : "", (a.type == "text" && a.size) ? ' size="' + soy.$$escapeHtml(a.size) + '"' : "", (a.isDisabled) ? " disabled" : "");
    aui.renderExtraAttributes(a, b, c);
    b.append("/>");
    return d ? "" : b.toString()
};
aui.form.submit = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.form.input({
        id: a.id,
        name: a.name,
        type: "submit",
        value: a.text,
        isDisabled: a.isDisabled,
        extraClasses: a.extraClasses,
        extraAttributes: a.extraAttributes
    }, b, c);
    return d ? "" : b.toString()
};
aui.form.button = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.form.input({
        id: a.id,
        name: a.name,
        type: "button",
        value: a.text,
        isDisabled: a.isDisabled,
        extraClasses: a.extraClasses,
        extraAttributes: a.extraAttributes
    }, b, c);
    return d ? "" : b.toString()
};
aui.form.linkButton = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<a", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' name="', soy.$$escapeHtml(a.name ? a.name : a.id), '" href="', soy.$$escapeHtml(a.url ? a.url : "#"), '" class="cancel');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", soy.$$escapeHtml(a.text), "</a>");
    return d ? "" : b.toString()
};
aui.form.textarea = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<textarea", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' name="', (a.name) ? soy.$$escapeHtml(a.name) : soy.$$escapeHtml(a.id), '" class="textarea');
    aui.renderExtraClasses(a, b, c);
    b.append('"', (a.rows) ? ' rows="' + soy.$$escapeHtml(a.rows) + '"' : "", (a.cols) ? ' cols="' + soy.$$escapeHtml(a.cols) + '"' : "", (a.isDisabled) ? " disabled" : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", (a.value) ? soy.$$escapeHtml(a.value) : "", "</textarea>");
    return d ? "" : b.toString()
};
aui.form.select = function (c, h, f) {
    var d = h || new soy.StringBuilder();
    d.append("<select", (c.id) ? ' id="' + soy.$$escapeHtml(c.id) + '"' : "", ' name="', (c.name) ? soy.$$escapeHtml(c.name) : soy.$$escapeHtml(c.id), '" class="', soy.$$escapeHtml(c.isMultiple ? "multi-select" : "select"));
    aui.renderExtraClasses(c, d, f);
    d.append('"', (c.size) ? ' size="' + soy.$$escapeHtml(c.size) + '"' : "", (c.isDisabled) ? " disabled" : "", (c.isMultiple) ? " multiple" : "");
    aui.renderExtraAttributes(c, d, f);
    d.append(">");
    var b = c.options;
    var e = b.length;
    for (var a = 0; a < e; a++) {
        var g = b[a];
        aui.form.optionOrOptgroup(g, d, f)
    }
    d.append("</select>");
    return h ? "" : d.toString()
};
aui.form.optionOrOptgroup = function (a, h, e) {
    var d = h || new soy.StringBuilder();
    if (a.options) {
        d.append('<optgroup label="', soy.$$escapeHtml(a.text), '">');
        var c = a.options;
        var f = c.length;
        for (var b = 0; b < f; b++) {
            var g = c[b];
            aui.form.optionOrOptgroup(g, d, e)
        }
        d.append("</optgroup>")
    } else {
        d.append('<option value="', soy.$$escapeHtml(a.value), '" ', (a.selected) ? "selected" : "", ">", soy.$$escapeHtml(a.text), "</option>")
    }
    return h ? "" : d.toString()
};
aui.form.value = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<span", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="field-value');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</span>");
    return d ? "" : b.toString()
};
aui.form.field = function (h, f, i) {
    var c = f || new soy.StringBuilder();
    var d = h.type == "checkbox" || h.type == "radio";
    var j = h.fieldWidth ? h.fieldWidth + "-field" : "";
    c.append('<div class="', (d) ? soy.$$escapeHtml(h.type) : "field-group");
    aui.renderExtraClasses(h, c, i);
    c.append('"');
    aui.renderExtraAttributes(h, c, i);
    c.append(">");
    if (h.labelContent && !d) {
        aui.form.label({
            forField: h.id,
            isRequired: h.isRequired,
            content: h.labelContent
        }, c, i)
    }
    switch (h.type) {
    case "textarea":
        aui.form.textarea({
            id: h.id,
            name: h.name,
            value: h.value,
            rows: h.rows,
            cols: h.cols,
            isDisabled: h.isDisabled ? true : false,
            extraClasses: j
        }, c, i);
        break;
    case "select":
        aui.form.select({
            id: h.id,
            name: h.name,
            options: h.options,
            isMultiple: h.isMultiple,
            size: h.size,
            isDisabled: h.isDisabled ? true : false,
            extraClasses: j
        }, c, i);
        break;
    case "value":
        aui.form.value({
            id: h.id,
            content: soy.$$escapeHtml(h.value)
        }, c, i);
        break;
    case "text":
    case "password":
    case "file":
    case "radio":
    case "checkbox":
    case "button":
    case "submit":
    case "reset":
        aui.form.input({
            id: h.id,
            name: h.name,
            type: h.type,
            value: h.value,
            maxLength: h.maxLength,
            size: h.size,
            isChecked: h.isChecked,
            isDisabled: h.isDisabled ? true : false,
            extraClasses: j
        }, c, i);
        break
    }
    if (h.labelContent && d) {
        aui.form.label({
            forField: h.id,
            isRequired: h.isRequired,
            content: h.labelContent
        }, c, i)
    }
    if (h.descriptionText) {
        aui.form.fieldDescription({
            message: h.descriptionText
        }, c, i)
    }
    if (h.errorTexts) {
        var b = h.errorTexts;
        var g = b.length;
        for (var e = 0; e < g; e++) {
            var a = b[e];
            aui.form.fieldError({
                message: a
            }, c, i)
        }
    }
    c.append("</div>");
    return f ? "" : c.toString()
};
aui.form.fieldError = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<div class="error');
    aui.renderExtraClasses(a, b, c);
    b.append('">', soy.$$escapeHtml(a.message), "</div>");
    return d ? "" : b.toString()
};
aui.form.fieldDescription = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<div class="description');
    aui.renderExtraClasses(a, b, c);
    b.append('">', soy.$$escapeHtml(a.message), "</div>");
    return d ? "" : b.toString()
};
aui.form.textField = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.form.field({
        id: a.id,
        name: a.name,
        type: "text",
        labelContent: a.labelContent,
        value: a.value,
        maxLength: a.maxLength,
        size: a.size,
        isRequired: a.isRequired,
        isDisabled: a.isDisabled,
        descriptionText: a.descriptionText,
        errorTexts: a.errorTexts,
        extraClasses: a.extraClasses,
        extraAttributes: a.extraAttributes,
        fieldWidth: a.fieldWidth
    }, b, c);
    return d ? "" : b.toString()
};
aui.form.textareaField = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.form.field({
        id: a.id,
        name: a.name,
        type: "textarea",
        labelContent: a.labelContent,
        value: a.value,
        rows: a.rows,
        cols: a.cols,
        isRequired: a.isRequired,
        isDisabled: a.isDisabled,
        descriptionText: a.descriptionText,
        errorTexts: a.errorTexts,
        extraClasses: a.extraClasses,
        extraAttributes: a.extraAttributes,
        fieldWidth: a.fieldWidth
    }, b, c);
    return d ? "" : b.toString()
};
aui.form.passwordField = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.form.field({
        id: a.id,
        name: a.name,
        type: "password",
        labelContent: a.labelContent,
        value: a.value,
        isRequired: a.isRequired,
        isDisabled: a.isDisabled,
        descriptionText: a.descriptionText,
        errorTexts: a.errorTexts,
        extraClasses: a.extraClasses,
        extraAttributes: a.extraAttributes,
        fieldWidth: a.fieldWidth
    }, b, c);
    return d ? "" : b.toString()
};
aui.form.fileField = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.form.field({
        id: a.id,
        name: a.name,
        type: "file",
        labelContent: a.labelContent,
        value: a.value,
        isRequired: a.isRequired,
        isDisabled: a.isDisabled,
        descriptionText: a.descriptionText,
        errorTexts: a.errorTexts,
        extraClasses: a.extraClasses,
        extraAttributes: a.extraAttributes
    }, b, c);
    return d ? "" : b.toString()
};
aui.form.selectField = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.form.field({
        id: a.id,
        name: a.name,
        type: "select",
        labelContent: a.labelContent,
        options: a.options,
        isMultiple: a.isMultiple,
        size: a.size,
        isRequired: a.isRequired,
        isDisabled: a.isDisabled,
        descriptionText: a.descriptionText,
        errorTexts: a.errorTexts,
        extraClasses: a.extraClasses,
        extraAttributes: a.extraAttributes,
        fieldWidth: a.fieldWidth
    }, b, c);
    return d ? "" : b.toString()
};
aui.form.checkboxField = function (e, d, f) {
    var b = d || new soy.StringBuilder();
    var a = new soy.StringBuilder((e.isMatrix) ? '<div class="matrix">' : "");
    var i = e.fields;
    var c = i.length;
    for (var h = 0; h < c; h++) {
        var g = i[h];
        aui.form.field(soy.$$augmentData(g, {
            type: "checkbox",
            id: g.id,
            name: g.name,
            labelContent: soy.$$escapeHtml(g.labelText),
            isChecked: g.isChecked,
            isDisabled: g.isDisabled,
            descriptionText: g.descriptionText,
            errorTexts: g.errorTexts,
            extraClasses: g.extraClasses,
            extraAttributes: g.extraAttributes
        }), a, f)
    }
    a.append((e.isMatrix) ? "</div>" : "");
    if (e.descriptionText || e.errorTexts && e.errorTexts.length) {
        aui.form.field({
            descriptionText: e.descriptionText,
            errorTexts: e.errorTexts,
            isDisabled: false
        }, a, f)
    }
    aui.form.fieldset({
        legendContent: e.legendContent + (e.isRequired ? '<span class="aui-icon icon-required"></span>' : ""),
        isGroup: true,
        id: e.id,
        extraClasses: e.extraClasses,
        extraAttributes: e.extraAttributes,
        content: a.toString()
    }, b, f);
    return d ? "" : b.toString()
};
aui.form.radioField = function (f, e, g) {
    var a = e || new soy.StringBuilder();
    var h = new soy.StringBuilder((f.isMatrix) ? '<div class="matrix">' : "");
    var d = f.fields;
    var i = d.length;
    for (var c = 0; c < i; c++) {
        var b = d[c];
        aui.form.field(soy.$$augmentData(b, {
            type: "radio",
            name: f.name ? f.name : f.id,
            value: b.value,
            id: b.id,
            labelContent: soy.$$escapeHtml(b.labelText),
            isChecked: b.isChecked,
            isDisabled: b.isDisabled,
            descriptionText: b.descriptionText,
            errorTexts: b.errorTexts,
            extraClasses: b.extraClasses,
            extraAttributes: b.extraAttributes
        }), h, g)
    }
    h.append((f.isMatrix) ? "</div>" : "");
    if (f.descriptionText || f.errorTexts && f.errorTexts.length) {
        aui.form.field({
            descriptionText: f.descriptionText,
            errorTexts: f.errorTexts,
            isDisabled: false
        }, h, g)
    }
    aui.form.fieldset({
        legendContent: f.legendContent + (f.isRequired ? '<span class="aui-icon icon-required"></span>' : ""),
        isGroup: true,
        id: f.id,
        extraClasses: f.extraClasses,
        extraAttributes: f.extraAttributes,
        content: h.toString()
    }, a, g);
    return e ? "" : a.toString()
};
aui.form.valueField = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.form.field({
        id: a.id,
        type: "value",
        value: a.value,
        labelContent: a.labelContent,
        isRequired: a.isRequired,
        descriptionText: a.descriptionText,
        errorTexts: a.errorTexts,
        extraClasses: a.extraClasses,
        extraAttributes: a.extraAttributes
    }, b, c);
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.group == "undefined") {
    aui.group = {}
}
aui.group.group = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-group', (a.isSplit) ? " aui-group-split" : "");
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ">");
    return d ? "" : b.toString()
};
aui.group.item = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-item');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ">");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.icons == "undefined") {
    aui.icons = {}
}
aui.icons.icon = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "span"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-icon', (a.useIconFont) ? " aui-icon-" + soy.$$escapeHtml(a.size ? a.size : "small") : "", " aui", soy.$$escapeHtml(a.useIconFont ? "-iconfont" : "-icon"), soy.$$escapeHtml(a.iconFontSet ? "-" + a.iconFontSet : ""), "-", soy.$$escapeHtml(a.icon));
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", (a.accessibilityText) ? soy.$$escapeHtml(a.accessibilityText) : "", "</", soy.$$escapeHtml(a.tagName ? a.tagName : "span"), ">");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.labels == "undefined") {
    aui.labels = {}
}
aui.labels.label = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    if (a.url && a.isCloseable == true) {
        b.append("<span", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-label aui-label-closeable aui-label-split');
        aui.renderExtraClasses(a, b, c);
        b.append('"');
        aui.renderExtraAttributes(a, b, c);
        b.append('><a class="aui-label-split-main" href="', soy.$$escapeHtml(a.url), '">', soy.$$escapeHtml(a.text), '</a><span class="aui-label-split-close" >');
        aui.labels.closeIcon(a, b, c);
        b.append("</span></span>")
    } else {
        b.append("<", soy.$$escapeHtml(a.url ? "a" : "span"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-label', (a.isCloseable) ? " aui-label-closeable" : "");
        aui.renderExtraClasses(a, b, c);
        b.append('"');
        aui.renderExtraAttributes(a, b, c);
        b.append((a.url) ? ' href="' + soy.$$escapeHtml(a.url) + '"' : "", ">", soy.$$escapeHtml(a.text));
        if (a.isCloseable) {
            aui.labels.closeIcon(a, b, c)
        }
        b.append("</", soy.$$escapeHtml(a.url ? "a" : "span"), ">")
    }
    return d ? "" : b.toString()
};
aui.labels.closeIcon = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<span tabindex="0" class="aui-icon aui-icon-close"');
    if (a.hasTitle != false) {
        b.append(' title="');
        aui.labels.closeIconText(a, b, c);
        b.append('"')
    }
    b.append(">");
    aui.labels.closeIconText(a, b, c);
    b.append("</span>");
    return d ? "" : b.toString()
};
aui.labels.closeIconText = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append((a.closeIconText) ? soy.$$escapeHtml(a.closeIconText) : "(" + soy.$$escapeHtml("Remove") + " " + soy.$$escapeHtml(a.text) + ")");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.message == "undefined") {
    aui.message = {}
}
aui.message.info = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.message.message(soy.$$augmentData(a, {
        type: "info"
    }), b, c);
    return d ? "" : b.toString()
};
aui.message.warning = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.message.message(soy.$$augmentData(a, {
        type: "warning"
    }), b, c);
    return d ? "" : b.toString()
};
aui.message.error = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.message.message(soy.$$augmentData(a, {
        type: "error"
    }), b, c);
    return d ? "" : b.toString()
};
aui.message.success = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.message.message(soy.$$augmentData(a, {
        type: "success"
    }), b, c);
    return d ? "" : b.toString()
};
aui.message.hint = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.message.message(soy.$$augmentData(a, {
        type: "hint"
    }), b, c);
    return d ? "" : b.toString()
};
aui.message.generic = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.message.message(soy.$$augmentData(a, {
        type: "generic"
    }), b, c);
    return d ? "" : b.toString()
};
aui.message.message = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-message ', soy.$$escapeHtml(a.type ? a.type : "generic"), (a.isCloseable) ? " closeable" : "", (a.isShadowed) ? " shadowed" : "");
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", (a.titleContent) ? '<p class="title"><strong>' + a.titleContent + "</strong></p>" : "", a.content, '<span class="aui-icon icon-', soy.$$escapeHtml(a.type ? a.type : "generic"), '"></span>', (a.isCloseable) ? '<span class="aui-icon icon-close" role="button" tabindex="0"></span>' : "", "</", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ">");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.page == "undefined") {
    aui.page = {}
}
aui.page.document = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<!DOCTYPE html><html lang="', soy.$$escapeHtml(c.language ? c.language : "en"), '"><head><meta charset="utf-8" /><meta http-equiv="X-UA-Compatible" content="IE=EDGE"><title>', soy.$$escapeHtml(a.windowTitle), "</title>", (a.headContent) ? a.headContent : "", "</head><body");
    if (a.pageType) {
        if (a.pageType == "generic") {
            b.append(" ");
            aui.renderExtraClasses(soy.$$augmentData(a, {
                isFullAttr: true
            }), b, c)
        } else {
            if (a.pageType == "focused") {
                b.append(' class="aui-page-focused aui-page-focused-', soy.$$escapeHtml(a.focusedPageSize ? a.focusedPageSize : "xlarge"));
                aui.renderExtraClasses(a, b, c);
                b.append('"')
            } else {
                b.append(' class="aui-page-', soy.$$escapeHtml(a.pageType));
                aui.renderExtraClasses(a, b, c);
                b.append('"')
            }
        }
    } else {
        b.append(' class="');
        aui.renderExtraClasses(a, b, c);
        b.append('"')
    }
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</body></html>");
    return d ? "" : b.toString()
};
aui.page.page = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<div id="page"><header id="header" role="banner">', a.headerContent, '</header><!-- #header --><section id="content" role="main">', a.contentContent, '</section><!-- #content --><footer id="footer" role="contentinfo">', a.footerContent, "</footer><!-- #footer --></div><!-- #page -->");
    return d ? "" : b.toString()
};
aui.page.header = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<nav", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-header aui-dropdown2-trigger-group');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(' role="navigation"><div class="aui-header-inner">', (a.headerBeforeContent) ? '<div class="aui-header-before">' + a.headerBeforeContent + "</div>" : "", '<div class="aui-header-primary"><h1 id="logo" class="aui-header-logo', (a.headerLogoImageUrl) ? " aui-header-logo-custom" : (a.logo) ? " aui-header-logo-" + soy.$$escapeHtml(a.logo) : "", '"><a href="', soy.$$escapeHtml(a.headerLink ? a.headerLink : "/"), '">', (a.headerLogoImageUrl) ? '<img src="' + soy.$$escapeHtml(a.headerLogoImageUrl) + '" alt="' + soy.$$escapeHtml(a.headerLogoText) + '" />' : '<span class="aui-header-logo-device">' + soy.$$escapeHtml(a.headerLogoText ? a.headerLogoText : "") + "</span>", (a.headerText) ? '<span class="aui-header-logo-text">' + soy.$$escapeHtml(a.headerText) + "</span>" : "", "</a></h1>", (a.primaryNavContent) ? a.primaryNavContent : "", "</div>", (a.secondaryNavContent) ? '<div class="aui-header-secondary">' + a.secondaryNavContent + "</div>" : "", (a.headerAfterContent) ? '<div class="aui-header-after">' + a.headerAfterContent + "</div>" : "", "</div><!-- .aui-header-inner--></nav><!-- .aui-header -->");
    return d ? "" : b.toString()
};
aui.page.pagePanel = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ' class="aui-page-panel');
    aui.renderExtraClasses(a, b, c);
    b.append('"', (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append('><div class="aui-page-panel-inner">', a.content, "</div><!-- .aui-page-panel-inner --></", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), "><!-- .aui-page-panel -->");
    return d ? "" : b.toString()
};
aui.page.pagePanelNav = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ' class="aui-page-panel-nav');
    aui.renderExtraClasses(a, b, c);
    b.append('"', (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), "><!-- .aui-page-panel-nav -->");
    return d ? "" : b.toString()
};
aui.page.pagePanelContent = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "section"), ' class="aui-page-panel-content');
    aui.renderExtraClasses(a, b, c);
    b.append('"', (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "section"), "><!-- .aui-page-panel-content -->");
    return d ? "" : b.toString()
};
aui.page.pagePanelSidebar = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "aside"), ' class="aui-page-panel-sidebar');
    aui.renderExtraClasses(a, b, c);
    b.append('"', (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "aside"), "><!-- .aui-page-panel-sidebar -->");
    return d ? "" : b.toString()
};
aui.page.pagePanelItem = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "section"), ' class="aui-page-panel-item');
    aui.renderExtraClasses(a, b, c);
    b.append('"', (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "section"), "><!-- .aui-page-panel-item -->");
    return d ? "" : b.toString()
};
aui.page.pageHeader = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<header class="aui-page-header');
    aui.renderExtraClasses(a, b, c);
    b.append('"', (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append('><div class="aui-page-header-inner">', a.content, "</div><!-- .aui-page-header-inner --></header><!-- .aui-page-header -->");
    return d ? "" : b.toString()
};
aui.page.pageHeaderImage = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<div class="aui-page-header-image');
    aui.renderExtraClasses(a, b, c);
    b.append('"', (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</div><!-- .aui-page-header-image -->");
    return d ? "" : b.toString()
};
aui.page.pageHeaderMain = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<div class="aui-page-header-main');
    aui.renderExtraClasses(a, b, c);
    b.append('"', (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</div><!-- .aui-page-header-main -->");
    return d ? "" : b.toString()
};
aui.page.pageHeaderActions = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append('<div class="aui-page-header-actions');
    aui.renderExtraClasses(a, b, c);
    b.append('"', (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</div><!-- .aui-page-header-actions -->");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
aui.panel = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-panel');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ">");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.progressTracker == "undefined") {
    aui.progressTracker = {}
}
aui.progressTracker.progressTracker = function (h, g, k) {
    var e = g || new soy.StringBuilder();
    e.append("<ol", (h.id) ? ' id="' + soy.$$escapeHtml(h.id) + '"' : "", ' class="aui-progress-tracker', (h.isInverted) ? " aui-progress-tracker-inverted" : "");
    aui.renderExtraClasses(h, e, k);
    e.append('"');
    aui.renderExtraAttributes(h, e, k);
    e.append(">");
    var d = new soy.StringBuilder();
    var a = h.steps;
    var m = a.length;
    for (var c = 0; c < m; c++) {
        var l = a[c];
        if (l.isCurrent) {
            var j = h.steps;
            var b = j.length;
            for (var i = 0; i < b; i++) {
                var f = j[i];
                aui.progressTracker.step(soy.$$augmentData(f, {
                    width: Math.round(100 / h.steps.length * 10000) / 10000,
                    href: i < c ? f.href : null
                }), d, k)
            }
        }
    }
    aui.progressTracker.content({
        steps: h.steps,
        content: d.toString()
    }, e, k);
    e.append("</ol>");
    return g ? "" : e.toString()
};
aui.progressTracker.content = function (a, h, f) {
    var e = h || new soy.StringBuilder();
    if (a.content != "") {
        e.append(a.content)
    } else {
        var g = a.steps;
        var d = g.length;
        for (var c = 0; c < d; c++) {
            var b = g[c];
            aui.progressTracker.step(soy.$$augmentData(b, {
                isCurrent: c == 0,
                width: Math.round(100 / a.steps.length * 10000) / 10000,
                href: null
            }), e, f)
        }
    }
    return h ? "" : e.toString()
};
aui.progressTracker.step = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<li", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-progress-tracker-step', (a.isCurrent) ? " aui-progress-tracker-step-current" : "");
    aui.renderExtraClasses(a, b, c);
    b.append('" style="width: ', soy.$$escapeHtml(a.width), '%;"');
    aui.renderExtraAttributes(a, b, c);
    b.append("><", soy.$$escapeHtml(a.href ? "a" : "span"), (a.href) ? ' href="' + soy.$$escapeHtml(a.href) + '"' : "", ">", soy.$$escapeHtml(a.text), "</", soy.$$escapeHtml(a.href ? "a" : "span"), "></li>");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
aui.table = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<table", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", (a.columnsContent) ? a.columnsContent : "", (a.captionContent) ? "<caption>" + a.captionContent + "</caption>" : "", (a.theadContent) ? "<thead>" + a.theadContent + "</thead>" : "", (a.tfootContent) ? "<tfoot>" + a.tfootContent + "</tfoot>" : "", (!a.contentIncludesTbody) ? "<tbody>" : "", a.content, (!a.contentIncludesTbody) ? "</tbody>" : "", "</table>");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
aui.tabs = function (a, h, d) {
	alert('aui tabs1');
    var b = h || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-tabs ', soy.$$escapeHtml(a.isVertical ? "vertical-tabs" : "horizontal-tabs"), (a.isDisabled) ? " aui-tabs-disabled" : "");
    aui.renderExtraClasses(a, b, d);
    b.append('"');
    aui.renderExtraAttributes(a, b, d);
    b.append('><ul class="tabs-menu">');
    var f = a.menuItems;
    var g = f.length;
    for (var c = 0; c < g; c++) {
        var e = f[c];
        aui.tabMenuItem(e, b, d)
    }
    b.append("</ul>", a.paneContent, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ">");
    return h ? "" : b.toString()
};
aui.tabMenuItem = function (a, d, c) {
	alert('aui tabs2');
    var b = d || new soy.StringBuilder();
    b.append("<li", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="menu-item', (a.isActive) ? " active-tab" : "");
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append('><a href="', soy.$$escapeHtml(a.url), '"><strong>', soy.$$escapeHtml(a.text), "</strong></a></li>");
    return d ? "" : b.toString()
};
aui.tabPane = function (a, d, c) {
	alert('aui tabs3');
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="tabs-pane', (a.isActive) ? " active-pane" : "");
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ">");
    return d ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.toolbar == "undefined") {
    aui.toolbar = {}
}
aui.toolbar.toolbar = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-toolbar');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ">");
    return d ? "" : b.toString()
};
aui.toolbar.split = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="toolbar-split toolbar-split-', soy.$$escapeHtml(a.split));
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</", soy.$$escapeHtml(a.tagName ? a.tagName : "div"), ">");
    return d ? "" : b.toString()
};
aui.toolbar.group = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<ul", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="toolbar-group');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</ul>");
    return d ? "" : b.toString()
};
aui.toolbar.item = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<li ", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="toolbar-item', (a.isPrimary) ? " primary" : "", (a.isActive) ? " active" : "");
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</li>");
    return d ? "" : b.toString()
};
aui.toolbar.trigger = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<a", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="toolbar-trigger');
    aui.renderExtraClasses(a, b, c);
    b.append('" href="', soy.$$escapeHtml(a.url ? a.url : "#"), '"', (a.title) ? ' title="' + soy.$$escapeHtml(a.title) + '"' : "");
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</a>");
    return d ? "" : b.toString()
};
aui.toolbar.button = function (a, e, c) {
    var b = e || new soy.StringBuilder();
    if (!(a != null)) {
        b.append("Either $text or both $title and $iconClass must be provided.")
    } else {
        var d = new soy.StringBuilder();
        aui.toolbar.trigger({
            url: a.url,
            title: a.title,
            content: ((a.iconClass) ? '<span class="icon ' + soy.$$escapeHtml(a.iconClass) + '"></span>' : "") + ((a.text) ? '<span class="trigger-text">' + soy.$$escapeHtml(a.text) + "</span>" : "")
        }, d, c);
        aui.toolbar.item({
            isActive: a.isActive,
            isPrimary: a.isPrimary,
            id: a.id,
            extraClasses: a.extraClasses,
            extraAttributes: a.extraAttributes,
            content: d.toString()
        }, b, c)
    }
    return e ? "" : b.toString()
};
aui.toolbar.link = function (a, f, e) {
    var c = f || new soy.StringBuilder();
    var d = new soy.StringBuilder("toolbar-item-link");
    aui.renderExtraClasses(a, d, e);
    var b = new soy.StringBuilder();
    aui.toolbar.trigger({
        url: a.url,
        content: soy.$$escapeHtml(a.text)
    }, b, e);
    aui.toolbar.item({
        id: a.id,
        extraClasses: d.toString(),
        extraAttributes: a.extraAttributes,
        content: b.toString()
    }, c, e);
    return f ? "" : c.toString()
};
aui.toolbar.dropdownInternal = function (a, g, e) {
    var c = g || new soy.StringBuilder();
    var b = new soy.StringBuilder(a.itemClass);
    aui.renderExtraClasses(a, b, e);
    var f = new soy.StringBuilder((a.splitButtonContent) ? a.splitButtonContent : "");
    var d = new soy.StringBuilder();
    aui.dropdown.trigger({
        extraClasses: "toolbar-trigger",
        accessibilityText: a.text
    }, d, e);
    aui.dropdown.menu({
        content: a.dropdownItemsContent
    }, d, e);
    aui.dropdown.parent({
        content: d.toString()
    }, f, e);
    aui.toolbar.item({
        isPrimary: a.isPrimary,
        id: a.id,
        extraClasses: b.toString(),
        extraAttributes: a.extraAttributes,
        content: f.toString()
    }, c, e);
    return g ? "" : c.toString()
};
aui.toolbar.dropdown = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    aui.toolbar.dropdownInternal({
        isPrimary: a.isPrimary,
        id: a.id,
        itemClass: "toolbar-dropdown",
        extraClasses: a.extraClasses,
        extraAttributes: a.extraAttributes,
        text: a.text,
        dropdownItemsContent: a.dropdownItemsContent
    }, b, c);
    return d ? "" : b.toString()
};
aui.toolbar.splitButton = function (a, e, d) {
    var b = e || new soy.StringBuilder();
    var c = new soy.StringBuilder();
    aui.toolbar.trigger({
        url: a.url,
        content: soy.$$escapeHtml(a.text)
    }, c, d);
    aui.toolbar.dropdownInternal({
        isPrimary: a.isPrimary,
        id: a.id,
        itemClass: "toolbar-splitbutton",
        extraClasses: a.extraClasses,
        extraAttributes: a.extraAttributes,
        dropdownItemsContent: a.dropdownItemsContent,
        splitButtonContent: c.toString()
    }, b, d);
    return e ? "" : b.toString()
};
if (typeof aui == "undefined") {
    var aui = {}
}
if (typeof aui.toolbar2 == "undefined") {
    aui.toolbar2 = {}
}
aui.toolbar2.toolbar2 = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<div", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-toolbar2');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(' role="toolbar"><div class="aui-toolbar2-inner">', a.content, "</div></div>");
    return d ? "" : b.toString()
};
aui.toolbar2.item = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<div", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-toolbar2-', soy.$$escapeHtml(a.item));
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</div>");
    return d ? "" : b.toString()
};
aui.toolbar2.group = function (a, d, c) {
    var b = d || new soy.StringBuilder();
    b.append("<div", (a.id) ? ' id="' + soy.$$escapeHtml(a.id) + '"' : "", ' class="aui-toolbar2-group');
    aui.renderExtraClasses(a, b, c);
    b.append('"');
    aui.renderExtraAttributes(a, b, c);
    b.append(">", a.content, "</div>");
    return d ? "" : b.toString()
};