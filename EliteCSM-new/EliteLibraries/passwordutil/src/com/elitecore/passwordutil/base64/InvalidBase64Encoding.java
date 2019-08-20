/* The contents of this file are subject to the Mozilla Public License 
 * Version 1.0 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/.
 * 
 * Software distributed under the License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See 
 * the License for the specific language governing rights and limitations 
 * under the License. 
 * 
 * The Original Code is the Waterken Hydro product, released April 5, 1999.
 * 
 * The Initial Developer of the Original Code is Waterken Inc. Portions 
 * created by Waterken are Copyright (C) 1998-1999 Waterken Inc. All 
 * Rights Reserved.
 */
package com.elitecore.passwordutil.base64;

import java.io.IOException;

/**
 * 
 */
public final
class InvalidBase64Encoding extends IOException
{
    /**
     * Constructs a <code>InvalidBase64Encoding</code>.
     * @param   detail  The detail message.
     */
    public
    InvalidBase64Encoding(String detail)
    {
        super(detail);
    }
    
    /**
     * Constructs a <code>InvalidBase64Encoding</code>.
     */
    public
    InvalidBase64Encoding()
    {}
}
