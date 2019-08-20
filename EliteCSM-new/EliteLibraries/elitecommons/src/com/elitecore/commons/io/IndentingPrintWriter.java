package com.elitecore.commons.io;

import java.io.PrintWriter;
import java.io.Writer;

import com.elitecore.commons.base.Strings;

/**
 * Provides a print writer implementation that prepends tab ('\t') character/s 
 * based on the indentation count, which can be manipulated using {@link IndentingWriter#incrementIndentation()}
 * and {@link IndentingWriter#decrementIndentation()} methods.
 * 
 * <p>Tabs are prepended with each print call {@code print(...)} or {@code println(...)}.
 * 
 * @author narendra.pathai
 *
 */
public class IndentingPrintWriter extends PrintWriter implements IndentingWriter {

	private int indentationCount = 0;

	/**
	 * Creates a new TabbingPrintWriter
	 * 
	 * @param writer  a character output stream
	 */
	public IndentingPrintWriter(Writer writer) {
		super(writer);
	}

	/**
	 * Increments the indentation
	 */
	@Override
	public void incrementIndentation() {
		indentationCount++;
	}

	/**
	 * Decrements the indentation
	 * 
	 * @throws IllegalStateException when called with indentation level 0 (negative indentation is invalid)
	 */
	@Override
	public void decrementIndentation() {
		if (indentationCount == 0) {
			throw new IllegalStateException("indentation cannot be negative");
		}
		indentationCount--;
	}

	private void appendTabs() {
		super.print(Strings.repeat("\t", indentationCount));
	}

	@Override
	public void print(String s) {
		appendTabs();
		super.print(s);
	}

	@Override
	public void print(boolean b) {
		appendTabs();
		super.print(b);
	}

	@Override
	public void print(char c) {
		appendTabs();
		super.print(c);
	}

	@Override
	public void print(char[] s) {
		appendTabs();
		super.print(s);
	}

	@Override
	public void print(double d) {
		appendTabs();
		super.print(d);
	}

	@Override
	public void print(float f) {
		appendTabs();
		super.print(f);
	}

	@Override
	public void print(int i) {
		appendTabs();
		super.print(i);
	}

	@Override
	public void print(long l) {
		appendTabs();
		super.print(l);
	}

	@Override
	public void print(Object obj) {
		appendTabs();
		super.print(obj);
	}

	@Override
	public IndentingPrintWriter append(CharSequence csq) {
		super.append(csq);
		return this;
	}
}
