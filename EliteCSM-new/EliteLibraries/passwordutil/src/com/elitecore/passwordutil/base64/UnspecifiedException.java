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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Signals an {@link java.lang.Exception} that a method is not allowed to throw.
 * <p>
 * This class is used to work around design bugs in an interface's exception
 * specification. If a particular implementation needs to throw an exception
 * that is not permitted by the exception specification system, then it can
 * wrap the exception in a {@link java.lang.RuntimeException} to subvert the
 * exception specification.
 * </p>
 * <p>
 * <strong>Exercise restraint</strong> in using this class. Use of this
 * class does signal a design flaw. If possible, it is better to correct 
 * the design flaw than to use this class.
 * </p>
 * 
 */
public final
class UnspecifiedException extends RuntimeException {
    /**
     * @serial  The unexpected exception.
     */
    private Exception subject;
    
	/**
	 * Constructs a <code>UnspecifiedException</code> with no detail message.
	 */
	public 
	UnspecifiedException(Exception subject)
	{
        this.subject = subject;
    }

    /**
     * Constructs a <code>UnspecifiedException</code> with a detail message.
     * @param   detail  The detail message.
     */
	public
	UnspecifiedException(Exception subject, String detail)
	{
		super(detail);
        this.subject = subject;
	}
    
    // Throwable interface.
    
    public
    void printStackTrace()
    {
        super.printStackTrace();
        System.err.println("Caused by:");
        subject.printStackTrace();
    }
    
    public
    void printStackTrace(PrintStream out)
    {
        super.printStackTrace(out);
        out.println("Caused by:");
        subject.printStackTrace(out);
    }
    
    public
    void printStackTrace(PrintWriter out)
    {
        super.printStackTrace(out);
        out.println("Caused by:");
        subject.printStackTrace(out);
    }
    
    // UnspecifiedException interface.
    
    /**
     * Retrieve the exception.
     * @return  The unexpected exception.
     */
    public
    Exception getSubject()
    {
        return subject;
    }
}
