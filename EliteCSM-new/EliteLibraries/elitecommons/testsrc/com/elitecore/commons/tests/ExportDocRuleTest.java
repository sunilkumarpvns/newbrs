package com.elitecore.commons.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import com.elitecore.commons.tests.ExportDocRule.HTMLExporter;
import com.elitecore.commons.tests.ExportDocRule.TestDocExporter;
import com.elitecore.commons.tests.ExportDocRule.TextExporter;
import com.elitecore.commons.tests.ExportDocRuleTest.HostClass.Context;
import com.elitecore.commons.tests.ExportDocRuleTest.HostClass.Context.ContextInsideContext;
import com.elitecore.commons.tests.ExportDocRuleTest.HostClass.Context.ContextInsideContext.ContextInsideContextInsideContext;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ExportDocRuleTest {

	@Test
	public void verifyExecutionOrder() throws Exception {
		InOrder inorder = inOrder(HostClass.mockExporter);

		JUnitCore.runClasses(HostClass.class);
		
		inorder.verify(HostClass.mockExporter).start();

		inorder.verify(HostClass.mockExporter).classStart(HostClass.class);
		
		inorder.verify(HostClass.mockExporter).method(HostClass.class.getDeclaredMethod("testThisIsFirstTestCase", new Class<?>[] {}));
		inorder.verify(HostClass.mockExporter).method(HostClass.class.getDeclaredMethod("testThisIsSecondTestCase", new Class<?>[] {}));
	
		inorder.verify(HostClass.mockExporter).classStart(Context.class);
		inorder.verify(HostClass.mockExporter).method(Context.class.getDeclaredMethod("testThisIsNestedTestCase", new Class<?>[] {}));
		
		inorder.verify(HostClass.mockExporter).classStart(ContextInsideContext.class);
		inorder.verify(HostClass.mockExporter).method(ContextInsideContext.class.getDeclaredMethod("testThisIsContextInsideContextTest", new Class<?>[] {}));
		
		inorder.verify(HostClass.mockExporter).classStart(ContextInsideContextInsideContext.class);
		inorder.verify(HostClass.mockExporter).method(ContextInsideContextInsideContext.class.getDeclaredMethod("testThisIsContextInsideContextInsideContextTest", new Class<?>[] {}));
		inorder.verify(HostClass.mockExporter).classEnd(ContextInsideContextInsideContext.class);
		
		inorder.verify(HostClass.mockExporter).classEnd(ContextInsideContext.class);
		
		inorder.verify(HostClass.mockExporter).classEnd(Context.class);
		
		inorder.verify(HostClass.mockExporter).classEnd(HostClass.class);
		
		inorder.verify(HostClass.mockExporter).end();
	}
	
	public class HTMLExporterTest {
		
		private List<String> lines = new ArrayList<>();
		
		private HTMLExporter htmlExporter = new HTMLExporter(new Writer() {
			
			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				lines.add(new String(cbuf, off, len));
			}
			
			@Override
			public void flush() throws IOException {
				// no-op
				
			}
			
			@Override
			public void close() throws IOException {
				// no-op
			}
		});
		
		@Before
		public void setUp() throws Exception {
			htmlExporter.start();
		}
		
		@Test
		public void putsClassNameInHeading1() throws Exception {
			htmlExporter.classStart(getClass());
			htmlExporter.classEnd(getClass());
			assertThat(lines, hasItem(equalTo("<h1>HTMLExporterTest</h1>")));
		}
		
		@Test
		public void putsMethodNameInUnorderedList() throws Exception {
			Method anyMethod = getClass().getMethods()[0];
			htmlExporter.method(anyMethod);
			assertThat(lines, hasItem(equalTo(
					String.format("<li>%s</li>", TestDocExporter.splitCamelCase(anyMethod.getName())))));
		}
		
		@After
		public void cleanUp() throws Exception {
			htmlExporter.end();
		}
	}
	
	public class TextExporterTest {
		
		private List<String> lines = new ArrayList<>();
		
		private TextExporter textExporter = new TextExporter(new Writer() {
			
			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				lines.add(new String(cbuf, off, len));
			}
			
			@Override
			public void flush() throws IOException {
				// no-op
			}
			
			@Override
			public void close() throws IOException {
				// no-op
			}
		});
		
		@Before
		public void setUp() throws Exception {
			textExporter.start();
		}
		
		@Test
		public void putsClassNameInDoc() throws Exception {
			textExporter.classStart(getClass());
			textExporter.classEnd(getClass());
			assertThat(lines, hasItem(equalTo(getClass().getSimpleName())));
		}
		
		@Test
		public void putsMethodNameInBulletedList() throws Exception {
			Method anyMethod = getClass().getMethods()[0];
			textExporter.method(anyMethod);
			assertThat(lines, hasItem(
					String.format(" - " + TestDocExporter.splitCamelCase(anyMethod.getName()))));
		}
		
		@After
		public void cleanUp() throws Exception {
			textExporter.end();
		}
	}
	
	@RunWith(HierarchicalContextRunner.class)
	public static class HostClass {
		public static ExportDocRule.TestDocExporter mockExporter = mock(TestDocExporter.class);
		
		@ClassRule public static ExportDocRule confluence = new ExportDocRule(HostClass.class, mockExporter);
		
		@Test
		public void testThisIsFirstTestCase() {
			
		}
		
		@Test
		public void testThisIsSecondTestCase() {
			
		}
		
		public class Context {
			
			@Test
			public void testThisIsNestedTestCase() {
				
			}
			
			public class ContextInsideContext {
				
				@Test
				public void testThisIsContextInsideContextTest() {
					
				}
				
				public class ContextInsideContextInsideContext {
					
					@Test
					public void testThisIsContextInsideContextInsideContextTest() {
						
					}
				}
			}
		}
		
		public class Context2 {
			
			@Test
			public void testThisIsNestedTestCase() {
				
			}
			
			public class ContextInsideContext {
				
				@Test
				public void testThisIsContextInsideContextTest() {
					
				}
				
				public class ContextInsideContextInsideContext {
					
					@Test
					public void testThisIsContextInsideContextInsideContextTest() {
						
					}
				}
			}
		}
	}
}
