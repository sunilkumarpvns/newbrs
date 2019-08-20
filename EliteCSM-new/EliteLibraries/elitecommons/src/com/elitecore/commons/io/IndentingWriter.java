package com.elitecore.commons.io;

import java.io.Closeable;
import java.io.Flushable;

public interface IndentingWriter extends Appendable, Closeable, Flushable {


    void incrementIndentation();

    void decrementIndentation();

    void print(String s);

    void print(boolean b);

    void print(char c);

    void print(char[] s);

    void print(double d);

    void print(float f);

    void print(int i);

    void print(long l);

    void print(Object obj);

    void println();

    void println(String s);

    void println(boolean b);

    void println(char c);

    void println(char[] s);

    void println(double d);

    void println(float f);

    void println(int i);

    void println(long l);

    void println(Object l);

    void close();

    IndentingWriter append(CharSequence csq);
}
