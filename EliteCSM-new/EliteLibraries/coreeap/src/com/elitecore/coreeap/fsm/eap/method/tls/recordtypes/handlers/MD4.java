package com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers;

import java.io.UnsupportedEncodingException;

public class MD4 {

	private static final int FF(int a, int b, int c, int d, int x, int s)
    {
        a += (b & c | ~b & d) + x;
        return a << s | a >>> 32 - s;
    }

    private static final int GG(int a, int b, int c, int d, int x, int s)
    {
        a += (b & c | b & d | c & d) + x + 0x5a827999;
        return a << s | a >>> 32 - s;
    }

    private static final int HH(int a, int b, int c, int d, int x, int s)
    {
        a += (b ^ c ^ d) + x + 0x6ed9eba1;
        return a << s | a >>> 32 - s;
    }

    public static void getDigest(byte input[], int inputOffset, int inputLen, byte output[], int outputOffset)
    {
        byte buffer[] = new byte[64];
        int x[] = new int[16];
        int state[] = new int[4];
        state[0] = 0x67452301;
        state[1] = 0xefcdab89;
        state[2] = 0x98badcfe;
        state[3] = 0x10325476;
        if(inputOffset + inputLen > input.length)
            throw new IllegalArgumentException();
        long count = inputLen << 3;
        int i;
        for(i = inputOffset; i + 63 < inputOffset + inputLen; i += 64){
            Transform(input, i, state, x);
        }
        System.arraycopy(input, i, buffer, 0, inputLen - (i - inputOffset));
        int index = (int)(count >> 3) & 0x3f;
        if(index >= 56)
        {
            System.arraycopy(padding, 0, buffer, index, 64 - index);
            Transform(buffer, 0, state, x);
            System.arraycopy(padding, 1, buffer, 0, 56);
            buffer[56] = (byte)(int)(count >>> 0);
            buffer[57] = (byte)(int)(count >>> 8);
            buffer[58] = (byte)(int)(count >>> 16);
            buffer[59] = (byte)(int)(count >>> 24);
            buffer[60] = (byte)(int)(count >>> 32);
            buffer[61] = (byte)(int)(count >>> 40);
            buffer[62] = (byte)(int)(count >>> 48);
            buffer[63] = (byte)(int)(count >>> 56);
            Transform(buffer, 0, state, x);
        } else
        {
            System.arraycopy(padding, 0, buffer, index, 56 - index);
            buffer[56] = (byte)(int)(count >>> 0);
            buffer[57] = (byte)(int)(count >>> 8);
            buffer[58] = (byte)(int)(count >>> 16);
            buffer[59] = (byte)(int)(count >>> 24);
            buffer[60] = (byte)(int)(count >>> 32);
            buffer[61] = (byte)(int)(count >>> 40);
            buffer[62] = (byte)(int)(count >>> 48);
            buffer[63] = (byte)(int)(count >>> 56);
            Transform(buffer, 0, state, x);
        }
        output[outputOffset++] = (byte)(state[0] >>> 0);
        output[outputOffset++] = (byte)(state[0] >>> 8);
        output[outputOffset++] = (byte)(state[0] >>> 16);
        output[outputOffset++] = (byte)(state[0] >>> 24);
        output[outputOffset++] = (byte)(state[1] >>> 0);
        output[outputOffset++] = (byte)(state[1] >>> 8);
        output[outputOffset++] = (byte)(state[1] >>> 16);
        output[outputOffset++] = (byte)(state[1] >>> 24);
        output[outputOffset++] = (byte)(state[2] >>> 0);
        output[outputOffset++] = (byte)(state[2] >>> 8);
        output[outputOffset++] = (byte)(state[2] >>> 16);
        output[outputOffset++] = (byte)(state[2] >>> 24);
        output[outputOffset++] = (byte)(state[3] >>> 0);
        output[outputOffset++] = (byte)(state[3] >>> 8);
        output[outputOffset++] = (byte)(state[3] >>> 16);
        output[outputOffset] = (byte)(state[3] >>> 24);
    }

    public MD4()
    {
        m_buffer = new byte[64];
        m_intBuffer = new int[16];
        m_state = new int[4];
        init();
    }

    public void init()
    {
        m_count = 0L;
        m_state[0] = 0x67452301;
        m_state[1] = 0xefcdab89;
        m_state[2] = 0x98badcfe;
        m_state[3] = 0x10325476;
    }

    public void update(String input, String encoding)
        throws UnsupportedEncodingException
    {
        update(input.getBytes(encoding));
    }

    public void update8859_1(String input)
    {
        byte buf[] = new byte[input.length()];
        int i = 0;
        for(int length = input.length(); i < length; i++){
            buf[i] = (byte)input.charAt(i);
        }
        update(buf);
    }

    public final void update(byte input[])
    {
        update(input, 0, input.length);
    }

    public final void update(byte input[], int inputOffset, int inputLen)
    {
        if(inputOffset + inputLen > input.length)
            throw new IllegalArgumentException();
        int index = (int)(m_count >>> 3) & 0x3f;
        m_count += inputLen << 3;
        int partLen = 64 - index;
        int i;
        if(inputLen >= partLen)
        {
            System.arraycopy(input, inputOffset, m_buffer, index, partLen);
            Transform(m_buffer, 0, m_state, m_intBuffer);
            for(i = partLen + inputOffset; i + 63 < inputOffset + inputLen; i += 64){
                Transform(input, i, m_state, m_intBuffer);
            }
            index = 0;
        } else
        {
            i = inputOffset;
        }
        System.arraycopy(input, i, m_buffer, index, inputLen - (i - inputOffset));
    }

    public final void getDigest(byte output[], int offset)
    {
        int counts[] = new int[2];
        counts[0] = (int)m_count;
        counts[1] = (int)(m_count >>> 32);
        byte bits[] = new byte[8];
        Encode(counts, bits, 0, 8);
        int index = (int)(m_count >> 3) & 0x3f;
        int padLen = index >= 56 ? 120 - index : 56 - index;
        update(padding, 0, padLen);
        update(bits, 0, 8);
        Encode(m_state, output, offset, 16);
        init();
    }

    public byte[] getDigest()
    {
        byte digest[] = new byte[16];
        getDigest(digest, 0);
        return digest;
    }

    private static final void Transform(byte block[], int offset, int state[], int x[])
    {
        int a = state[0];
        int b = state[1];
        int c = state[2];
        int d = state[3];
        int i = 0;
        int j = offset;
        for(int size = offset + 64; j < size;)
        {
            x[i] = block[j++] & 0xff | (block[j++] & 0xff) << 8 | (block[j++] & 0xff) << 16 | (block[j++] & 0xff) << 24;
            i++;
        }

        a = FF(a, b, c, d, x[0], 3);
        d = FF(d, a, b, c, x[1], 7);
        c = FF(c, d, a, b, x[2], 11);
        b = FF(b, c, d, a, x[3], 19);
        a = FF(a, b, c, d, x[4], 3);
        d = FF(d, a, b, c, x[5], 7);
        c = FF(c, d, a, b, x[6], 11);
        b = FF(b, c, d, a, x[7], 19);
        a = FF(a, b, c, d, x[8], 3);
        d = FF(d, a, b, c, x[9], 7);
        c = FF(c, d, a, b, x[10], 11);
        b = FF(b, c, d, a, x[11], 19);
        a = FF(a, b, c, d, x[12], 3);
        d = FF(d, a, b, c, x[13], 7);
        c = FF(c, d, a, b, x[14], 11);
        b = FF(b, c, d, a, x[15], 19);
        a = GG(a, b, c, d, x[0], 3);
        d = GG(d, a, b, c, x[4], 5);
        c = GG(c, d, a, b, x[8], 9);
        b = GG(b, c, d, a, x[12], 13);
        a = GG(a, b, c, d, x[1], 3);
        d = GG(d, a, b, c, x[5], 5);
        c = GG(c, d, a, b, x[9], 9);
        b = GG(b, c, d, a, x[13], 13);
        a = GG(a, b, c, d, x[2], 3);
        d = GG(d, a, b, c, x[6], 5);
        c = GG(c, d, a, b, x[10], 9);
        b = GG(b, c, d, a, x[14], 13);
        a = GG(a, b, c, d, x[3], 3);
        d = GG(d, a, b, c, x[7], 5);
        c = GG(c, d, a, b, x[11], 9);
        b = GG(b, c, d, a, x[15], 13);
        a = HH(a, b, c, d, x[0], 3);
        d = HH(d, a, b, c, x[8], 9);
        c = HH(c, d, a, b, x[4], 11);
        b = HH(b, c, d, a, x[12], 15);
        a = HH(a, b, c, d, x[2], 3);
        d = HH(d, a, b, c, x[10], 9);
        c = HH(c, d, a, b, x[6], 11);
        b = HH(b, c, d, a, x[14], 15);
        a = HH(a, b, c, d, x[1], 3);
        d = HH(d, a, b, c, x[9], 9);
        c = HH(c, d, a, b, x[5], 11);
        b = HH(b, c, d, a, x[13], 15);
        a = HH(a, b, c, d, x[3], 3);
        d = HH(d, a, b, c, x[11], 9);
        c = HH(c, d, a, b, x[7], 11);
        b = HH(b, c, d, a, x[15], 15);
        state[0] += a;
        state[1] += b;
        state[2] += c;
        state[3] += d;
        for(i = 0; i < x.length; i++){
            x[i] = 0;
        }
    }

    private static final void Encode(int input[], byte output[], int offset, int len)
    {
        int i = 0;
        int j = offset;
        for(int size = offset + len; j < size;)
        {
            output[j++] = (byte)(input[i] >>> 0);
            output[j++] = (byte)(input[i] >>> 8);
            output[j++] = (byte)(input[i] >>> 16);
            output[j++] = (byte)(input[i] >>> 24);
            i++;
        }

    }

    public static final String toHexString(byte hash[])
    {
        StringBuilder buf = new StringBuilder(hash.length);
        for(int i = 0; i < hash.length; i++)
        {
            int hiNibble = hash[i] >> 4 & 0xf;
            int loNibble = hash[i] >> 0 & 0xf;
            buf.append(Character.forDigit(hiNibble, 16));
            buf.append(Character.forDigit(loNibble, 16));
        }

        return buf.toString();
    }
    
    public static  String bytesToBase64(byte buf[])
    {
        return bytesToBase64(buf, 0, buf.length);
    }
    
    public static  String bytesToBase64(byte buf[], int offset, int length)
    {
    	 final char s_base64Char[] = {
    	        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
    	        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
    	        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
    	        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
    	        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
    	        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
    	        '8', '9', '+', '/'
    	    };
        StringBuilder sb = new StringBuilder();
        int limit = offset + length;
        do
        {
            if(offset >= limit)
                break;
            sb.append(s_base64Char[buf[offset] >> 2 & 0x3f]);
            int leftOver = buf[offset++] << 4 & 0x30;
            if(offset == limit)
            {
                sb.append(s_base64Char[leftOver]).append('=').append('=');
                break;
            }
            sb.append(s_base64Char[buf[offset] >> 4 & 0xf | leftOver]);
            leftOver = buf[offset++] << 2 & 0x3c;
            if(offset == limit)
            {
                sb.append(s_base64Char[leftOver]).append('=');
                break;
            }
            sb.append(s_base64Char[buf[offset] >> 6 & 3 | leftOver]);
            sb.append(s_base64Char[buf[offset++] & 0x3f]);
        } while(true);
        return sb.toString();
    }
    
    private int m_state[];
    private long m_count;
    private byte m_buffer[];
    private int m_intBuffer[];
    private static byte padding[] = {
        -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0
    };
}

class DES
{

    public DES()
    {
    }

    static void permuteBits(byte src[], int srcOffset, byte dst[], int dstOffset, int count, int table[])
    {
        for(int j = 0; j < count; j++)
        {
            dst[dstOffset + j] = 0;
            for(int i = 0; i < 8; i++)
            {
                int tmp = table[j * 8 + i];
                if((src[srcOffset + (tmp >>> 3)] & bitmask[tmp & 7]) != 0)
                    dst[dstOffset + j] |= bitmask[i];
            }

        }

    }

    public static void permuteKey(byte key[], int permutedKey[])
    {
        byte pkey[] = new byte[7];
        permuteBits(key, 0, pkey, 0, 7, pch1);
        int lkey = pkey[0] & 0xff;
        lkey = lkey << 8 | pkey[1] & 0xff;
        lkey = lkey << 8 | pkey[2] & 0xff;
        lkey = lkey << 4 | (pkey[3] & 0xff) >> 4;
        int rkey = pkey[3] & 0xf;
        rkey = rkey << 8 | pkey[4] & 0xff;
        rkey = rkey << 8 | pkey[5] & 0xff;
        rkey = rkey << 8 | pkey[6] & 0xff;
        for(int i = 0; i < 16; i++)
        {
            int shift = round_shift[i];
            lkey = lkey << shift & 0xfffffff | lkey >>> 28 - shift;
            rkey = rkey << shift & 0xfffffff | rkey >>> 28 - shift;
            int rtmp;
            int ltmp = rtmp = 0;
            for(int j = 0; j < 24; j++)
            {
                ltmp <<= 1;
                if((lkey & left_pch2[j]) != 0)
                    ltmp |= 1;
                rtmp <<= 1;
                if((rkey & right_pch2[j]) != 0)
                    rtmp |= 1;
            }

            permutedKey[i << 1] = ltmp << 6 & 0x3f000000 | ltmp << 10 & 0x3f0000 | rtmp >>> 10 & 0x3f00 | rtmp >>> 6 & 0x3f;
            permutedKey[(i << 1) + 1] = ltmp << 12 & 0x3f000000 | ltmp << 16 & 0x3f0000 | rtmp >>> 4 & 0x3f00 | rtmp & 0x3f;
        }

    }

    public static void encrypt(byte src[], byte dst[], int pkey[])
    {
        encrypt(src, 0, dst, 0, 8, pkey);
    }

    public static void encrypt(byte src[], byte dst[], int length, int pkey[])
    {
        encrypt(src, 0, dst, 0, length, pkey);
    }

    public static void encrypt(byte src[], int srcOffset, byte dst[], int dstOffset, int length, int pkey[])
    {
        for(length /= 8; length > 0; length--)
        {
            byte sd2[] = new byte[8];
            permuteBits(src, srcOffset, sd2, 0, 8, ip);
            int left = sd2[0] << 24 | (sd2[1] & 0xff) << 16 | (sd2[2] & 0xff) << 8 | sd2[3] & 0xff;
            int right = sd2[4] << 24 | (sd2[5] & 0xff) << 16 | (sd2[6] & 0xff) << 8 | sd2[7] & 0xff;
            for(int i = 0; i <= 30; i += 2)
            {
                int temp1 = (right >>> 3 | right << 29) ^ pkey[i];
                int temp = sbox_1[temp1 >> 24 & 0x3f];
                temp |= sbox_3[temp1 >> 16 & 0x3f];
                temp |= sbox_5[temp1 >> 8 & 0x3f];
                temp |= sbox_7[temp1 & 0x3f];
                temp1 = (right << 1 | right >>> 31) ^ pkey[i + 1];
                temp |= sbox_2[temp1 >> 24 & 0x3f];
                temp |= sbox_4[temp1 >> 16 & 0x3f];
                temp |= sbox_6[temp1 >> 8 & 0x3f];
                temp |= sbox_8[temp1 & 0x3f];
                temp ^= left;
                left = right;
                right = temp;
            }

            sd2[0] = (byte)(left >> 24);
            sd2[1] = (byte)(left >> 16);
            sd2[2] = (byte)(left >> 8);
            sd2[3] = (byte)left;
            sd2[4] = (byte)(right >> 24);
            sd2[5] = (byte)(right >> 16);
            sd2[6] = (byte)(right >> 8);
            sd2[7] = (byte)right;
            permuteBits(sd2, 0, dst, dstOffset, 8, iip);
            srcOffset += 8;
            dstOffset += 8;
        }

    }

    public static void decrypt(byte src[], byte dst[], int pkey[])
    {
        decrypt(src, 0, dst, 0, 8, pkey);
    }

    public static void decrypt(byte src[], byte dst[], int length, int pkey[])
    {
        decrypt(src, 0, dst, 0, length, pkey);
    }

    public static void decrypt(byte src[], int srcOffset, byte dst[], int dstOffset, int length, int pkey[])
    {
        for(length /= 8; length > 0; length--)
        {
            byte sd2[] = new byte[8];
            permuteBits(src, srcOffset, sd2, 0, 8, ip);
            int left = sd2[0] << 24 | (sd2[1] & 0xff) << 16 | (sd2[2] & 0xff) << 8 | sd2[3] & 0xff;
            int right = sd2[4] << 24 | (sd2[5] & 0xff) << 16 | (sd2[6] & 0xff) << 8 | sd2[7] & 0xff;
            for(int i = 30; i >= 0; i -= 2)
            {
                int temp1 = (right >>> 3 | right << 29) ^ pkey[i];
                int temp = sbox_1[temp1 >> 24 & 0x3f];
                temp |= sbox_3[temp1 >> 16 & 0x3f];
                temp |= sbox_5[temp1 >> 8 & 0x3f];
                temp |= sbox_7[temp1 & 0x3f];
                temp1 = (right << 1 | right >>> 31) ^ pkey[i + 1];
                temp |= sbox_2[temp1 >> 24 & 0x3f];
                temp |= sbox_4[temp1 >> 16 & 0x3f];
                temp |= sbox_6[temp1 >> 8 & 0x3f];
                temp |= sbox_8[temp1 & 0x3f];
                temp ^= left;
                left = right;
                right = temp;
            }

            sd2[0] = (byte)(left >> 24);
            sd2[1] = (byte)(left >> 16);
            sd2[2] = (byte)(left >> 8);
            sd2[3] = (byte)left;
            sd2[4] = (byte)(right >> 24);
            sd2[5] = (byte)(right >> 16);
            sd2[6] = (byte)(right >> 8);
            sd2[7] = (byte)right;
            permuteBits(sd2, 0, dst, dstOffset, 8, iip);
            srcOffset += 8;
            dstOffset += 8;
        }

    }

    public static String byteToHex(byte value)
    {
        return (new StringBuilder()).append("").append(hex[value >> 4 & 0xf]).append(hex[value & 0xf]).toString();
    }

    public static String bytesToHex(byte buf[])
    {
        if(buf == null)
            return "";
        else
            return bytesToHex(buf, 0, buf.length);
    }

    public static String bytesToHex(byte buf[], int offset, int limit)
    {
        StringBuilder hexbuf = new StringBuilder((limit - offset) * 2);
        for(int i = offset; i < limit; i++)
        {
            hexbuf.append(hex[buf[i] >> 4 & 0xf]);
            hexbuf.append(hex[buf[i] & 0xf]);
        }

        return hexbuf.toString();
    }

//	    public static byte[] hexToBytes(String hexbuf)       
//	    {
//	        int len = hexbuf.length();
//	        if((len & 1) != 0)
//	        {
//	            hexbuf = (new StringBuilder()).append("0").append(hexbuf).toString();
//	            len++;
//	        }
//	        byte buf[] = new byte[len / 2];
//	        int j = 0;
//	        int value = 0;
//	        for(int i = 0; i < len; i++)
//	        {
//	            char ch = hexbuf.charAt(i);
//	            int digit;
//	            if(ch >= '0' && ch <= '9')
//	                digit = ch - 48;
//	            else
//	            if(ch >= 'A' && ch <= 'F')
//	                digit = (ch - 65) + 10;
//	            else
//	            if(ch >= 'a' && ch <= 'f')
//	                digit = (ch - 97) + 10;
//	            else
//	                throw new IOException((new StringBuilder()).append("Invalid hex digit: '").append(ch).append('\'').toString());
//	            if((i & 1) == 0)
//	            {
//	                value = digit << 4;
//	            } else
//	            {
//	                value += digit;
//	                buf[j++] = (byte)value;
//	            }
//	        }
//
//	        return buf;
//	    }

    public static int bytesToInt(byte buf[], int offset)
    {
        if(offset < 0)
            throw new IllegalArgumentException("offset negative");
        if(offset + 4 > buf.length)
            throw new IllegalArgumentException("buffer too short");
        else
            return buf[offset] << 24 & 0xff000000 | buf[offset + 1] << 16 & 0xff0000 | buf[offset + 2] << 8 & 0xff00 | buf[offset + 3] & 0xff;
    }

    public static int bytesToInt(byte buf[])
    {
        return bytesToInt(buf, 0);
    }


    public static void addParity(byte key56[], byte key64[])
    {
        int i = bytesToInt(key56);
        key64[0] = (byte)(i >> 24 & 0xfe);
        key64[1] = (byte)(i >> 17 & 0xfe);
        key64[2] = (byte)(i >> 10 & 0xfe);
        key64[3] = (byte)(i >> 3 & 0xfe);
        i = bytesToInt(key56, 3);
        key64[4] = (byte)(i >> 20 & 0xfe);
        key64[5] = (byte)(i >> 13 & 0xfe);
        key64[6] = (byte)(i >> 6 & 0xfe);
        key64[7] = (byte)(i << 1 & 0xfe);
    }
    static int BITMASK0;
    static int BITMASK1;
    static int BITMASK2;
    static int BITMASK3;
    static int BITMASK4;
    static int BITMASK5;
    static int BITMASK6;
    static int BITMASK7;
    static int bitmask[];
    static int round_shift[] = {
        1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 
        2, 2, 2, 2, 2, 1
    };
    static int pch1[] = {
        56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 
        41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 
        26, 18, 10, 2, 59, 51, 43, 35, 62, 54, 
        46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 
        29, 21, 13, 5, 60, 52, 44, 36, 28, 20, 
        12, 4, 27, 19, 11, 3
    };
    static int left_pch2[] = {
        16384, 2048, 0x20000, 16, 0x8000000, 0x800000, 0x2000000, 1, 8192, 0x400000, 
        128, 0x40000, 32, 512, 0x10000, 0x1000000, 4, 0x100000, 4096, 0x200000, 
        2, 256, 32768, 0x4000000
    };
    static int right_pch2[] = {
        32768, 16, 0x2000000, 0x80000, 512, 2, 0x4000000, 0x10000, 32, 2048, 
        0x800000, 256, 4096, 128, 0x20000, 1, 0x400000, 8, 1024, 16384, 
        64, 0x100000, 0x8000000, 0x1000000
    };
    static int ip[] = {
        57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 
        43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 
        29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 
        15, 7, 56, 48, 40, 32, 24, 16, 8, 0, 
        58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 
        44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 
        30, 22, 14, 6
    };
    static int iip[] = {
        7, 39, 15, 47, 23, 55, 31, 63, 6, 38, 
        14, 46, 22, 54, 30, 62, 5, 37, 13, 45, 
        21, 53, 29, 61, 4, 36, 12, 44, 20, 52, 
        28, 60, 3, 35, 11, 43, 19, 51, 27, 59, 
        2, 34, 10, 42, 18, 50, 26, 58, 1, 33, 
        9, 41, 17, 49, 25, 57, 0, 32, 8, 40, 
        16, 48, 24, 56
    };
    static int sbox_1[] = {
        0x808200, 0, 32768, 0x808202, 0x808002, 33282, 2, 32768, 512, 0x808200, 
        0x808202, 512, 0x800202, 0x808002, 0x800000, 2, 514, 0x800200, 0x800200, 33280, 
        33280, 0x808000, 0x808000, 0x800202, 32770, 0x800002, 0x800002, 32770, 0, 514, 
        33282, 0x800000, 32768, 0x808202, 2, 0x808000, 0x808200, 0x800000, 0x800000, 512, 
        0x808002, 32768, 33280, 0x800002, 512, 2, 0x800202, 33282, 0x808202, 32770, 
        0x808000, 0x800202, 0x800002, 514, 33282, 0x808200, 514, 0x800200, 0x800200, 0, 
        32770, 33280, 0, 0x808002
    };
    static int sbox_2[] = {
        0x40084010, 0x40004000, 16384, 0x84010, 0x80000, 16, 0x40080010, 0x40004010, 0x40000010, 0x40084010, 
        0x40084000, 0x40000000, 0x40004000, 0x80000, 16, 0x40080010, 0x84000, 0x80010, 0x40004010, 0, 
        0x40000000, 16384, 0x84010, 0x40080000, 0x80010, 0x40000010, 0, 0x84000, 16400, 0x40084000, 
        0x40080000, 16400, 0, 0x84010, 0x40080010, 0x80000, 0x40004010, 0x40080000, 0x40084000, 16384, 
        0x40080000, 0x40004000, 16, 0x40084010, 0x84010, 16, 16384, 0x40000000, 16400, 0x40084000, 
        0x80000, 0x40000010, 0x80010, 0x40004010, 0x40000010, 0x80010, 0x84000, 0, 0x40004000, 16400, 
        0x40000000, 0x40080010, 0x40084010, 0x84000
    };
    static int sbox_3[] = {
        260, 0x4010100, 0, 0x4010004, 0x4000100, 0, 0x10104, 0x4000100, 0x10004, 0x4000004, 
        0x4000004, 0x10000, 0x4010104, 0x10004, 0x4010000, 260, 0x4000000, 4, 0x4010100, 256, 
        0x10100, 0x4010000, 0x4010004, 0x10104, 0x4000104, 0x10100, 0x10000, 0x4000104, 4, 0x4010104, 
        256, 0x4000000, 0x4010100, 0x4000000, 0x10004, 260, 0x10000, 0x4010100, 0x4000100, 0, 
        256, 0x10004, 0x4010104, 0x4000100, 0x4000004, 256, 0, 0x4010004, 0x4000104, 0x10000, 
        0x4000000, 0x4010104, 4, 0x10104, 0x10100, 0x4000004, 0x4010000, 0x4000104, 260, 0x4010000, 
        0x10104, 4, 0x4010004, 0x10100
    };
    static int sbox_4[] = {
        0x80401000, 0x80001040, 0x80001040, 64, 0x401040, 0x80400040, 0x80400000, 0x80001000, 0, 0x401000, 
        0x401000, 0x80401040, 0x80000040, 0, 0x400040, 0x80400000, 0x80000000, 4096, 0x400000, 0x80401000, 
        64, 0x400000, 0x80001000, 4160, 0x80400040, 0x80000000, 4160, 0x400040, 4096, 0x401040, 
        0x80401040, 0x80000040, 0x400040, 0x80400000, 0x401000, 0x80401040, 0x80000040, 0, 0, 0x401000, 
        4160, 0x400040, 0x80400040, 0x80000000, 0x80401000, 0x80001040, 0x80001040, 64, 0x80401040, 0x80000040, 
        0x80000000, 4096, 0x80400000, 0x80001000, 0x401040, 0x80400040, 0x80001000, 4160, 0x400000, 0x80401000, 
        64, 0x400000, 4096, 0x401040
    };
    static int sbox_5[] = {
        128, 0x1040080, 0x1040000, 0x21000080, 0x40000, 128, 0x20000000, 0x1040000, 0x20040080, 0x40000, 
        0x1000080, 0x20040080, 0x21000080, 0x21040000, 0x40080, 0x20000000, 0x1000000, 0x20040000, 0x20040000, 0, 
        0x20000080, 0x21040080, 0x21040080, 0x1000080, 0x21040000, 0x20000080, 0, 0x21000000, 0x1040080, 0x1000000, 
        0x21000000, 0x40080, 0x40000, 0x21000080, 128, 0x1000000, 0x20000000, 0x1040000, 0x21000080, 0x20040080, 
        0x1000080, 0x20000000, 0x21040000, 0x1040080, 0x20040080, 128, 0x1000000, 0x21040000, 0x21040080, 0x40080, 
        0x21000000, 0x21040080, 0x1040000, 0, 0x20040000, 0x21000000, 0x40080, 0x1000080, 0x20000080, 0x40000, 
        0, 0x20040000, 0x1040080, 0x20000080
    };
    static int sbox_6[] = {
        0x10000008, 0x10200000, 8192, 0x10202008, 0x10200000, 8, 0x10202008, 0x200000, 0x10002000, 0x202008, 
        0x200000, 0x10000008, 0x200008, 0x10002000, 0x10000000, 8200, 0, 0x200008, 0x10002008, 8192, 
        0x202000, 0x10002008, 8, 0x10200008, 0x10200008, 0, 0x202008, 0x10202000, 8200, 0x202000, 
        0x10202000, 0x10000000, 0x10002000, 8, 0x10200008, 0x202000, 0x10202008, 0x200000, 8200, 0x10000008, 
        0x200000, 0x10002000, 0x10000000, 8200, 0x10000008, 0x10202008, 0x202000, 0x10200000, 0x202008, 0x10202000, 
        0, 0x10200008, 8, 8192, 0x10200000, 0x202008, 8192, 0x200008, 0x10002008, 0, 
        0x10202000, 0x10000000, 0x200008, 0x10002008
    };
    static int sbox_7[] = {
        0x100000, 0x2100001, 0x2000401, 0, 1024, 0x2000401, 0x100401, 0x2100400, 0x2100401, 0x100000, 
        0, 0x2000001, 1, 0x2000000, 0x2100001, 1025, 0x2000400, 0x100401, 0x100001, 0x2000400, 
        0x2000001, 0x2100000, 0x2100400, 0x100001, 0x2100000, 1024, 1025, 0x2100401, 0x100400, 1, 
        0x2000000, 0x100400, 0x2000000, 0x100400, 0x100000, 0x2000401, 0x2000401, 0x2100001, 0x2100001, 1, 
        0x100001, 0x2000000, 0x2000400, 0x100000, 0x2100400, 1025, 0x100401, 0x2100400, 1025, 0x2000001, 
        0x2100401, 0x2100000, 0x100400, 0, 1, 0x2100401, 0, 0x100401, 0x2100000, 1024, 
        0x2000001, 0x2000400, 1024, 0x100001
    };
    static int sbox_8[] = {
        0x8000820, 2048, 0x20000, 0x8020820, 0x8000000, 0x8000820, 32, 0x8000000, 0x20020, 0x8020000, 
        0x8020820, 0x20800, 0x8020800, 0x20820, 2048, 32, 0x8020000, 0x8000020, 0x8000800, 2080, 
        0x20800, 0x20020, 0x8020020, 0x8020800, 2080, 0, 0, 0x8020020, 0x8000020, 0x8000800, 
        0x20820, 0x20000, 0x20820, 0x20000, 0x8020800, 2048, 32, 0x8020020, 2048, 0x20820, 
        0x8000800, 32, 0x8000020, 0x8020000, 0x8020020, 0x8000000, 0x20000, 0x8000820, 0, 0x8020820, 
        0x20020, 0x8000020, 0x8020000, 0x8000800, 0x8000820, 0, 0x8020820, 0x20800, 0x20800, 2080, 
        2080, 0x20020, 0x8000000, 0x8020800
    };
    private static final char hex[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f'
    };

    static 
    {
        BITMASK0 = 128;
        BITMASK1 = 64;
        BITMASK2 = 32;
        BITMASK3 = 16;
        BITMASK4 = 8;
        BITMASK5 = 4;
        BITMASK6 = 2;
        BITMASK7 = 1;
        bitmask = (new int[] {
            BITMASK0, BITMASK1, BITMASK2, BITMASK3, BITMASK4, BITMASK5, BITMASK6, BITMASK7
        });
    }
}

