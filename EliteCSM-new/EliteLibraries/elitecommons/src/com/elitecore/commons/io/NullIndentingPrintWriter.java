package com.elitecore.commons.io;

public class NullIndentingPrintWriter implements IndentingWriter {
    @Override
    public void incrementIndentation() {
        //will not take any action
    }

    @Override
    public void decrementIndentation() {
        //will not take any action
    }

    @Override
    public void print(String s) {
        //will not take any action
    }

    @Override
    public void print(boolean b) {
        //will not take any action
    }

    @Override
    public void print(char c) {
        //will not take any action
    }

    @Override
    public void print(char[] s) {
        //will not take any action
    }

    @Override
    public void print(double d) {
        //will not take any action
    }

    @Override
    public void print(float f) {
        //will not take any action
    }

    @Override
    public void print(int i) {
        //will not take any action
    }

    @Override
    public void print(long l) {
        //will not take any action
    }

    @Override
    public void print(Object obj) {
        //will not take any action
    }

    @Override
    public void println() {
        //will not take any action
    }

    @Override
    public void println(String s) {
        //will not take any action
    }

    @Override
    public void println(boolean b) {
        //will not take any action
    }

    @Override
    public void println(char c) {
        //will not take any action
    }

    @Override
    public void println(char[] s) {
        //will not take any action
    }

    @Override
    public void println(double d) {
        //will not take any action
    }

    @Override
    public void println(float f) {
        //will not take any action
    }

    @Override
    public void println(int i) {
        //will not take any action
    }

    @Override
    public void println(long l) {
        //will not take any action
    }

    @Override
    public void println(Object l) {
        //will not take any action
    }

    @Override
    public void close() {
        //will not take any action
    }

    @Override
    public void flush() {
        //will not take any action
    }

    @Override
    public IndentingWriter append(CharSequence csq) {
        return this;
    }

    @Override
    public IndentingWriter append(CharSequence csq, int start, int end) {
        return this;
    }

    @Override
    public IndentingWriter append(char c) {
        return this;
    }
}
