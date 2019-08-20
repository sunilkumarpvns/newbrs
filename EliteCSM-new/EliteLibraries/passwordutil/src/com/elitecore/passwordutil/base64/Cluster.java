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
import java.io.Writer;

/**
 * 
 */
public final
class Cluster extends Writer
{
    private Writer out;
    private String spacer;
    private int max;
    private int i;
    
    /**
     * Constructs a <code>Cluster</code>.
     * @param   out     The character output stream.
     * @param   spacer  The spacing string to use.
     * @param   max     The maximum number of characters per cluster.
     */
    public
    Cluster(Writer out, String spacer, int max)
    {
        this.out = out;
        this.spacer = spacer;
        this.max = max;
        i = max;
    }
    
    // OutputStream interface.
    
    /**
     * Writes a <code>char</code>.
     * @param   c       The <code>char</code> to write.
     * @see java.lang.Character#forDigit
     */
    public
    void write(int c) throws IOException
    {
        if(i == 0)
        {
            out.write(spacer);
            i = max;
        }
        out.write(c);
        --i;
    }
    
    /**
     * Writes <code>len</code> bytes from the specified <code>char</code> array 
     * starting at offset <code>off</code> to this output stream. 
     * @param      c     The data.
     * @param      off   The start offset in the data.
     * @param      len   The number of characters to write.
     */
    public
    void write(char[] c, int off, int len) throws IOException
    {
        while(len > i)
        {
            out.write(c, off, i);
            out.write(spacer);
            off += i;
            len -= i;
            i = max;
        }
        out.write(c, off, len);
        i -= len;
    }
    
    /**
     * Writes <code>len</code> bytes from the specified {@link java.lang.String}
     * starting at offset <code>off</code> to this output stream. 
     * @param      s     The data.
     * @param      off   The start offset in the data.
     * @param      len   The number of characters to write.
     */
    public
    void write(String s, int off, int len) throws IOException
    {
        while(len > i)
        {
            out.write(s, off, i);
            out.write(spacer);
            off += i;
            len -= i;
            i = max;
        }
        out.write(s, off, len);
        i -= len;
    }
    
    /**
     * Flushes the contained stream.
     */
    public
    void flush() throws IOException
    {
        out.flush();
    }
    
    /**
     * Flushes all data and closes the output stream.
     */
    public
    void close() throws IOException
    {
        out.close();
    }
}
