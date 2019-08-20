package com.elitecore.commons.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.elitecore.commons.io.IndentingPrintWriter;

/**
 * A JUnit rule that exports the test methods as a human readable document. Unit test cases provide not only regression
 * but they also serve as documentation which describes how exactly the class works. So the naming of methods and the 
 * exported form holds paramount value to understand class behavior.
 * 
 * <p><b>NOTE: </b>It supports hierarchical test cases. It supports n level contexts.
 * 
 * <p>The document can be exported in various formats such as in text, html, etc. 
 * 
 * <p>Example:
 * <pre>
 * <code>
 * public class TestClass {
 * 	
 * 	{@literal @}ClassRule public static ExportDocRule exportDoc = new ExportDocRule(new ExportDocRule.TextExporter("output.txt"));
 * 
 * 	{@literal @}Test
 * 	public void test1() {
 * 	
 * 	}
 * 
 * 	public class Context {
 * 		{@literal @}Test
 * 		public void contextTest1() {
 * 
 * 		}
 * 
 * 		{@literal @}Test
 * 		public void contextTest2() {
 * 		
 * 		}
 * 
 * 		public class InnerContext {
 * 			
 * 			{@literal @}Test
 * 			public void contextTest1() {
 * 
 * 			}
 * 	
 * 		}
 * 	}
 * 
 * }
 * </code>
 * </pre>
 * 
 * <p>One can implement custom exporters by providing custom {@link TestDocExporter} to the rule.
 * 
 * @see HTMLExporter
 * @see TextExporter
 * 
 * @author narendra.pathai
 *
 */
public class ExportDocRule implements TestRule {

	private Class<?> testClass;
	private TestDocExporter exporter;

	public ExportDocRule(Class<?> testClass, TestDocExporter exporter) {
		this.testClass = testClass;
		this.exporter = exporter;
	}
	
	@Override
	public Statement apply(Statement base, Description description) {
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				exporter.start();
				exportDocs(testClass);
				exporter.end();
			}

			private void exportDocs(Class<?> clazz) throws Exception {
				exporter.classStart(clazz);
				
				for (Method method : clazz.getDeclaredMethods()) {
					if (method.getAnnotation(Test.class) == null) {
						continue;
					}
					
					exporter.method(method);
				}
				
				for (Class<?> nestedClazz : clazz.getDeclaredClasses()) {
					exportDocs(nestedClazz);
				}
				
				exporter.classEnd(clazz);
			}
		};
	}
	
	/**
	 * A listener interface having various hook methods called by {@link ExportDocRule} as per the 
	 * Test class structure.
	 * 
	 * @author narendra.pathai
	 *
	 */
	public interface TestDocExporter {
		/**
		 * Is called when export is about to be started.
		 * 
		 * @throws Exception in case export cannot be started due to some reason
		 */
		void start() throws Exception;
		
		/**
		 * Is called when a class methods and its nested classes are about to be exported.
		 */
		void classStart(Class<?> clazz) throws Exception;
		
		/**
		 * Is called when a class methods and its nested classes have been exported.
		 */
		void classEnd(Class<?> clazz) throws Exception;
		
		/**
		 * Is called when a test method is encountered.
		 */
		void method(Method method) throws Exception;

		/**
		 * Is called when export is about to be ended.
		 */
		void end() throws Exception;
		
		public static String splitCamelCase(String s) {
			return s.replaceAll(
					String.format("%s|%s|%s",
							"(?<=[A-Z])(?=[A-Z][a-z])",
							"(?<=[^A-Z])(?=[A-Z])",
							"(?<=[A-Za-z])(?=[^A-Za-z])"
							),
					" "
					);
		}
	}
	
	/**
	 * Exports test name document in text format.
	 * 
	 * @author narendra.pathai
	 *
	 */
	public static class TextExporter implements TestDocExporter {
		private IndentingPrintWriter printWriter;
		private Writer writer;
		
		public TextExporter(Writer writer) {
			this.writer = writer;
		}
		
		public TextExporter(String file) {
			try {
				this.writer = new FileWriter(file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void classStart(Class<?> clazz) throws Exception {
			printWriter.print("\n");
			printWriter.incrementIndentation();
			
			printWriter.print(clazz.getSimpleName());
			printWriter.print("\n");
			printWriter.print("---");
			printWriter.print("\n");
		}

		@Override
		public void classEnd(Class<?> clazz) throws Exception {
			printWriter.decrementIndentation();
		}

		@Override
		public void method(Method method) throws Exception {
			printWriter.print(" - " + TestDocExporter.splitCamelCase(method.getName()));
			printWriter.print("\n");
		}

		@Override
		public void start() throws Exception {
			this.printWriter = new IndentingPrintWriter(writer);
		}

		@Override
		public void end() throws Exception {
			printWriter.close();
		}
	}
	
	/**
	 * Exports test name document in html format.
	 * 
	 * @author narendra.pathai
	 *
	 */
	public static class HTMLExporter implements TestDocExporter {
		private IndentingPrintWriter printWriter;
		private Writer writer;
		
		public HTMLExporter(Writer writer) {
			this.writer = writer;
		}
		
		public HTMLExporter(String file) {
			try {
				this.writer = new FileWriter(file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void classStart(Class<?> clazz) throws Exception {
			br()
			.h1(clazz.getSimpleName())
			.ul();
		}

		private HTMLExporter ul() {
			printWriter.print("<ul>");
			return this;
		}

		private HTMLExporter h1(String content) {
			printWriter.print("<h1>" + content + "</h1>");
			return this;
		}

		@Override
		public void classEnd(Class<?> clazz) throws Exception {
			printWriter.print("</ul>");
		}

		@Override
		public void method(Method method) throws Exception {
			li(TestDocExporter.splitCamelCase(method.getName()))
			.br();
		}

		private HTMLExporter br() {
			printWriter.print("<br/>");
			return this;
		}

		private HTMLExporter li(String content) {
			printWriter.println("<li>" + content + "</li>");
			return this;
		}

		@Override
		public void start() throws Exception {
			this.printWriter = new IndentingPrintWriter(writer);
		}

		@Override
		public void end() throws Exception {
			printWriter.flush();
			printWriter.close();
		}
	}
}
