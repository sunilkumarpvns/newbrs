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

import java.io.*;

/**
 * 
 */
public final
class StreamCopying
{
    /**
     * The default block size to use for copying.
     */
    public static final int BLOCK_SIZE  = 1024;
    
    private
    StreamCopying()
    {}
    
    /**
     * Copies <code>byte</code>s, one at a time.
     * The input stream will be closed after copying completes.
     * @param   in      The input stream.
     * @param   out     The output stream.
     * @return  The <code>out</code> parameter.
     */
    public static
    OutputStream transfer(InputStream in, OutputStream out) throws IOException
    {
        int b;
        while((b = in.read()) != -1){
            out.write(b);
        }    
        out.close();
        return out;
    }
    
    /**
     * Copies <code>byte</code>s, in blocks.
     * The input stream will be closed after copying completes.
     * @param   in      The input stream.
     * @param   out     The output stream.
     * @param   block   The block size to use.
     * @return  The <code>out</code> parameter.
     */
    public static
    OutputStream copy(InputStream in, OutputStream out, int block) throws IOException
    {
        byte[] buffer = new byte[block];
        int n = 0;
        while((n = in.read(buffer)) != -1){
            out.write(buffer, 0, n);
        }    
        in.close();
        return out;
    }
    
    /**
     * Copies <code>byte</code>s, in blocks.
     * The input stream will be closed after copying completes.
     * @param   in      The input stream.
     * @param   out     The output stream.
     * @return  The <code>out</code> parameter.
     */
    public static
    OutputStream copy(InputStream in, OutputStream out) throws IOException
    {
        return copy(in, out, BLOCK_SIZE);
    }
    
    /**
     * Copies <code>char</code>s, one at a time.
     * The input stream will be closed after copying completes.
     * @param   in      The input stream.
     * @param   out     The output stream.
     * @return  The <code>out</code> parameter.
     */
    public static
    Writer transfer(Reader in, Writer out) throws IOException
    {
        int b;
        while((b = in.read()) != -1){
            out.write(b);
        }            
        out.close();
        return out;
    }
    
    /**
     * Copies <code>char</code>s, in blocks.
     * The input stream will be closed after copying completes.
     * @param   in      The input stream.
     * @param   out     The output stream.
     * @param   block   The block size to use.
     * @return  The <code>out</code> parameter.
     */
    public static
    Writer copy(Reader in, Writer out, int block) throws IOException
    {
        char[] buffer = new char[block];
        int n = 0;
        while((n = in.read(buffer)) != -1){
            out.write(buffer, 0, n);
        }
        in.close();
        return out;
    }
    
    /**
     * Copies <code>char</code>s, in blocks.
     * The input stream will be closed after copying completes.
     * @param   in      The input stream.
     * @param   out     The output stream.
     * @return  The <code>out</code> parameter.
     */
    public static
    Writer copy(Reader in, Writer out) throws IOException
    {
        return copy(in, out, BLOCK_SIZE);
    }
}
