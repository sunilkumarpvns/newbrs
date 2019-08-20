/*
 *  License Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 10th September 2007
 *  Created By Ezhava Baiju D
 */

package com.elitecore.license.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.elitecore.license.commons.util.constant.CommonConstants;

/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public class EncryptDecrypt {


    public static String padding(String s) {
        if(s.length() >= 10)
            return s.substring(0, 10);
        String s1 = "";
        for(int j = s.length(); j < 10; j++){
            s1 = s1 + "0";
        }
        s1 = s1 + s;
        return s1;
    }

    public static String encrypt(String s) {
        Date date = new Date();
        long l = date.getTime();
        String s1 = padding(Long.toString(l));
        x1a0 = new short[5];
        cle = new char[11];
        si = 0;
        x1a2 = 0;
        i = 0;
        String s2 = "";
        String s3 = s1;
        s2 = s2 + s1;
        s3.getChars(0, s3.length(), cle, 0);
        int k = s.length();
        byte abyte0[] = new byte[k*4];
        for(int j = 0; j < 10; j++){
            abyte0[j] = (byte)cle[j];
        }
        
        int j1 = 0;
        for(int k1 = 10; j1 < k; k1 += 3) {
            c = (short)s.charAt(j1);
            assemble();
            cfc = (short)(inter >> 8);
            cfd = (short)(inter & 0xff);
            for(compte = 0; compte <= 9; compte++){
                cle[compte] = (char)(cle[compte] ^ c);
            }
            c ^= cfc ^ cfd;
            c &= 0xff;
            byte abyte1[] = new byte[4];
            String s4 = (new Integer(c)).toString();
            if(s4.length() == 1)
                s4 = "00" + s4;
            else
            if(s4.length() == 2)
                s4 = "0" + s4;
            s2 = s2 + s4;
            try{
            	abyte1 = s4.getBytes(CommonConstants.UTF8);
            }catch(UnsupportedEncodingException e){
            	abyte1 = s4.getBytes();
            }
            if(abyte1.length >= 1)
                abyte0[k1] = abyte1[0];
            else
                abyte0[k1] = 0;
            if(abyte1.length >= 2)
                abyte0[k1 + 1] = abyte1[1];
            else
                abyte0[k1 + 1] = 0;
            if(abyte1.length >= 3)
                abyte0[k1 + 2] = abyte1[2];
            else
                abyte0[k1 + 2] = 0;
            j1++;
        }

        fin();
        return s2;
    }

    public static String decrypt(String s) {
        x1a0 = new short[5];
        cle = new char[11];
        si = 0;
        x1a2 = 0;
        i = 0;
        String s1 = s.substring(0, 10);
        s1.getChars(0, s1.length(), cle, 0);
        int j = s.length();
        String s2 = "";
        byte abyte0[] = null;
        try{
        	abyte0 = s.getBytes(CommonConstants.UTF8);
        }catch(UnsupportedEncodingException e){
        	abyte0 = s.getBytes();
        }        
        byte abyte1[] = new byte[j];
        int k = 0;
        int l = 10;
        for(int i1 = 0; l < j; i1++) {
            byte abyte2[] = new byte[4];
            abyte2[0] = abyte0[l];
            abyte2[1] = abyte0[l + 1];
            abyte2[2] = abyte0[l + 2];
            abyte2[3] = 0;
            c = (new Integer(s.substring(l, l + 3))).shortValue();
            assemble();
            cfc = (short)(inter >> 8);
            cfd = (short)(inter & 0xff);
            c ^= cfc ^ cfd;
            c &= 0xff;
            for(compte = 0; compte <= 9; compte++){
                cle[compte] = (char)(cle[compte] ^ (char)c);
            }
            abyte1[i1] = (byte)c;
            k = i1;
            l += 3;
        }

        s2 = new String(abyte1, 0, k + 1);
        fin();
        return s2;
    }

    public static void fin() {
        for(compte = 0; compte <= 9; compte++){
            cle[compte] = '\0';
        }
        ax = 0;
        bx = 0;
        cx = 0;
        dx = 0;
        si = 0;
        tmp = 0;
        x1a2 = 0;
        x1a0[0] = 0;
        x1a0[1] = 0;
        x1a0[2] = 0;
        x1a0[3] = 0;
        x1a0[4] = 0;
        res = 0;
        i = 0;
        inter = 0;
        cfc = 0;
        cfd = 0;
        compte = 0;
        c = 0;
    }

    public static void assemble() {
        x1a0[0] = (short)(cle[0] * 256 + cle[1]);
        code();
        inter = res;
        x1a0[1] = (short)(x1a0[0] ^ cle[2] * 256 + cle[3]);
        code();
        inter ^= res;
        x1a0[2] = (short)(x1a0[1] ^ cle[4] * 256 + cle[5]);
        code();
        inter ^= res;
        x1a0[3] = (short)(x1a0[2] ^ cle[6] * 256 + cle[7]);
        code();
        inter ^= res;
        x1a0[4] = (short)(x1a0[3] ^ cle[8] * 256 + cle[9]);
        code();
        inter ^= res;
        i = 0;
    }

    public static void code() {
        dx = (short)(x1a2 + i);
        ax = x1a0[i];
        cx = 346;
        bx = 20021;
        tmp = ax;
        ax = si;
        si = tmp;
        tmp = ax;
        ax = dx;
        dx = tmp;
        if(ax != 0)
            ax *= bx;
        tmp = ax;
        ax = cx;
        cx = tmp;
        if(ax != 0) {
            ax *= si;
            cx = (short)(ax + cx);
        }
        tmp = ax;
        ax = si;
        si = tmp;
        ax *= bx;
        dx = (short)(cx + dx);
        ax++;
        x1a2 = dx;
        x1a0[i] = ax;
        res = (short)(ax ^ dx);
        i++;
    }

    public static void main(String args[]) {
        if(args.length < 1) {
            return;
        } else {
            String s = encrypt(args[0]);
            System.out.println("Encoded : " + s + " Length : " + s.length());
            s = decrypt(s);
            System.out.println("Decoded : " + s + " Length : " + s.length());
            return;
        }
    }

    public static short ax;
    public static short bx;
    public static short cx;
    public static short dx;
    public static short si;
    public static short tmp;
    public static short x1a2;
    public static short x1a0[];
    public static short res;
    public static short i;
    public static short inter;
    public static short cfc;
    public static short cfd;
    public static short compte;
    public static char cle[];
    public static short c;
    
}