package com.elitecore.core.commons.config.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.config.core.factory.DefaultWriterFactory;
import com.elitecore.core.commons.config.core.factory.DummyReaderFactory;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class ConfigurationContextTest {

	private ConfigurationContext configurationContext;
	private DummyReader reader;
	
	@Rule public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp() {
		DummyReaderFactory readerFactory = new DummyReaderFactory();
		reader = spy(new DummyReader());
		readerFactory.setReader(reader);
		
		configurationContext = spy(new ConfigurationContext("", readerFactory, new DefaultWriterFactory()));
	}
	
	
	/**
	 * A leaf configurable is a configurable that has no inner configurables. It is leaf node of configuration tree.
	 */
	public class LeafConfigurableScenario {
		
		public class Read {
			
			private Runnable readProbe;

			@Before
			public void setUp() {
				readProbe = mock(Runnable.class);
			}
			
			@Test
			public void callsProvidedReaderToReadTheConfigurable() throws LoadConfigurationException {
				LeafConfigurable expectedConfigurable = new LeafConfigurable(readProbe);
				
				reader.addConfigurable(LeafConfigurable.class, expectedConfigurable);
				
				configurationContext.read(LeafConfigurable.class);
				
				verify(reader).read(configurationContext, LeafConfigurable.class);
			}
			
			@Test
			public void returnsTheConfigurableReadByTheReader() throws LoadConfigurationException {
				LeafConfigurable expectedConfigurable = new LeafConfigurable(readProbe);
				
				reader.addConfigurable(LeafConfigurable.class, expectedConfigurable);
				
				assertThat((LeafConfigurable)configurationContext.read(LeafConfigurable.class), 
						is(sameInstance(expectedConfigurable)));
			}
			
			@Test
			public void callsPostReadHookAfterConfigurableIsReadByReader() throws LoadConfigurationException {
				LeafConfigurable expectedConfigurable = new LeafConfigurable(readProbe);
				reader.addConfigurable(LeafConfigurable.class, expectedConfigurable);
				
				configurationContext.read(LeafConfigurable.class);
				
				verify(readProbe).run();
			}
			
			@Test
			public void throwsLoadConfigurationExceptionIfPostReadHookIsNotDefinedInConfigurable() throws LoadConfigurationException {
				LeafConfigurableWithoutHooks expectedConfigurable = new LeafConfigurableWithoutHooks();
				
				reader.addConfigurable(LeafConfigurableWithoutHooks.class, expectedConfigurable);
				
				thrown.expect(LoadConfigurationException.class);
				thrown.expectMessage("No method of class: LeafConfigurableWithoutHooks is annotated with PostRead or the method is not public.");
				
				configurationContext.read(LeafConfigurableWithoutHooks.class);
			}
			
			@Test
			public void throwsLoadConfigurationExceptionIfPostReadHookMethodOfConfigurableThrowsException() throws LoadConfigurationException {
				RuntimeException exception = new RuntimeException("Unexpected");
				doThrow(exception).when(readProbe).run();
				
				LeafConfigurable configurable = new LeafConfigurable(readProbe);
				
				reader.addConfigurable(LeafConfigurable.class,  configurable);
				
				thrown.expect(LoadConfigurationException.class);
				thrown.expectMessage("Unexpected");
				
				configurationContext.read(LeafConfigurable.class);
			}
			
			@Test
			public void onceReadSuccessfullyConfigurableIsCachedInContext() throws LoadConfigurationException {
				LeafConfigurable configurable = new LeafConfigurable(readProbe);
				
				reader.addConfigurable(LeafConfigurable.class,  configurable);
				
				Configurable readConfigurable = configurationContext.read(LeafConfigurable.class);
				assertThat(configurationContext.get(LeafConfigurable.class), 
						is(sameInstance(readConfigurable)));
			}
		}
		
		public class Reload {
			
			private Configurable configurable;
			private Runnable reloadProbe;

			@Before
			public void setUp() throws LoadConfigurationException {
				configurationContext.doneCloneInstanceOf(Runnable.class);

				reloadProbe = mock(Runnable.class);
				LeafConfigurable expectedConfigurable = new LeafConfigurable(mock(Runnable.class), reloadProbe);
				
				reader.addConfigurable(LeafConfigurable.class, expectedConfigurable);
				
				configurable = configurationContext.read(LeafConfigurable.class);
			}
			
			@Test
			public void callsReaderToPerformReloadOnAClonedConfigurable() throws LoadConfigurationException {
				configurationContext.reload(configurable);
				
				ArgumentCaptor<Configurable> captor = ArgumentCaptor.forClass(Configurable.class);
				
				verify(reader).reload(Mockito.eq(configurationContext), captor.capture());

				assertThat(captor.getValue(), not(sameInstance(configurable)));
			}
			
			@Test
			public void returnsANewReloadedConfigurable() throws LoadConfigurationException {
				Configurable reloadedConfigurable = configurationContext.reload(configurable);
				
				assertThat(reloadedConfigurable, not(sameInstance(configurable)));
			}
			
			@Test
			public void callsPostReloadHookOnNewConfigurableAfterReloadedByReader() throws LoadConfigurationException {
				configurationContext.reload(configurable);
				
				verify(reloadProbe).run();
			}
			
			@Test
			public void throwsLoadConfigurationExceptionIfPostReloadHookIsNotDefinedInConfigurable() throws LoadConfigurationException {
				LeafConfigurableWithoutHooks expectedConfigurable = new LeafConfigurableWithoutHooks();
				
				reader.addConfigurable(LeafConfigurableWithoutHooks.class, expectedConfigurable);
				
				thrown.expect(LoadConfigurationException.class);
				thrown.expectMessage("No method of class: LeafConfigurableWithoutHooks is annotated with PostRead or the method is not public.");
				
				configurationContext.read(LeafConfigurableWithoutHooks.class);
			}
			
			@Test
			public void throwsLoadConfigurationExceptionIfPostReloadHookMethodOfConfigurableThrowsException() throws LoadConfigurationException {
				RuntimeException exception = new RuntimeException("Unexpected");
				doThrow(exception).when(reloadProbe).run();
				
				LeafConfigurable configurable = new LeafConfigurable(mock(Runnable.class), reloadProbe);
				
				reader.addConfigurable(LeafConfigurable.class,  configurable);
				
				thrown.expect(LoadConfigurationException.class);
				thrown.expectMessage("Unexpected");
				
				configurationContext.reload(configurable);
			}
		}
	}
	
	/**
	 * A configurable that is container of other configurables, it itself does not have any reader or writer.
	 */
	public class CompositeConfigurableScenario {
		private Runnable leafReloadProbe;
		private Runnable leafReadProbe;
		private Runnable compositeReadProbe;
		private LeafConfigurable leafConfigurable;
		private TestCompositeConfigurable compositeConfigurable;
		
		@Before
		public void setUp() throws LoadConfigurationException {
			leafReadProbe = mock(Runnable.class);
			leafReloadProbe = mock(Runnable.class);
			leafConfigurable = new LeafConfigurable(leafReadProbe, leafReloadProbe);
			reader.addConfigurable(LeafConfigurable.class, leafConfigurable);
			
			compositeConfigurable = new TestCompositeConfigurable();
			compositeReadProbe = mock(Runnable.class);
			compositeConfigurable.postRead = compositeReadProbe;
			
			doReturn(compositeConfigurable).when(configurationContext).createCompositeConfigurableInstance(TestCompositeConfigurable.class);
		}
		
		@Test
		public void returnsConfigurableAfterReadingAllContainedConfigurablesIfEligible() throws LoadConfigurationException {
			TestCompositeConfigurable compositeConfigurable = (TestCompositeConfigurable) configurationContext.read(TestCompositeConfigurable.class);
			
			assertThat(compositeConfigurable, is(notNullValue()));
			assertThat(compositeConfigurable.leafConfigurable, is(sameInstance(leafConfigurable)));
		}
		
		/*
		 * In this test case I had to stub configuration context class to allow creation of composite configurable in my control
		 * because it allowed for easy way of sensing other hooks.
		 */
		@Test
		public void callsPostReadHookOfCompositeConfigurable() throws LoadConfigurationException {
			configurationContext.read(TestCompositeConfigurable.class);
			
			verify(compositeReadProbe).run();
		}
		
		@Test
		public void postReadHookOfCompositeConfigurableIsCalledAfterPostReadOfAllContainedConfigurables() throws LoadConfigurationException {
			InOrder inOrder = Mockito.inOrder(leafReadProbe, compositeConfigurable.postRead);
			
			configurationContext.read(TestCompositeConfigurable.class);
			
			inOrder.verify(leafReadProbe).run();
			inOrder.verify(compositeReadProbe).run();
		}
		
		@Test
		public void doesNotThrowAnyExceptionIfReadOrderAnnotationIsMissing() throws LoadConfigurationException {
			assertThat(configurationContext.read(TestCompositeConfigurableWithoutReadOrder.class), is(notNullValue()));
		}
		
		@Test
		public void throwsLoadConfigurationExceptionIfPostReadHookIsNotDefinedInConfigurable() throws LoadConfigurationException {
			TestCompositeConfigurableWithoutPostRead configurable = new TestCompositeConfigurableWithoutPostRead();
			
			reader.addConfigurable(TestCompositeConfigurableWithoutPostRead.class, configurable);
			
			thrown.expect(LoadConfigurationException.class);
			thrown.expectMessage("No method of class: TestCompositeConfigurableWithoutPostRead is annotated with PostRead or the method is not public.");
			
			configurationContext.read(TestCompositeConfigurableWithoutPostRead.class);
		}
	}
	
	@ConfigurationProperties(moduleName = "LEAF-CONFIGURABLE", readWith = DummyReader.class, synchronizeKey = "")
	public static class LeafConfigurable extends Configurable {
		
		private Runnable postRead;
		private Runnable postReload;

		public LeafConfigurable(Runnable postRead) {
			this.postRead = postRead;
		}
		
		public LeafConfigurable(Runnable postRead, Runnable postReload) {
			this.postRead = postRead;
			this.postReload = postReload;
		}

		@PostRead
		public void postRead() {
			postRead.run();
		}
		
		@PostReload
		public void postReload() {
			postReload.run();
		}
	}
	
	@ConfigurationProperties(moduleName = "LEAF-CONFIGURABLE-WITHOUT-HOOKS", readWith = DummyReader.class, synchronizeKey = "")
	public static class LeafConfigurableWithoutHooks extends Configurable {
		
	}
	
	public static class TestCompositeConfigurableWithoutPostRead extends CompositeConfigurable {
		
		@Override
		public boolean isEligible(Class<? extends Configurable> configurableClass) {
			return false;
		}
		
	}
	
	public static class TestCompositeConfigurableWithoutReadOrder extends CompositeConfigurable {

		@PostRead
		public void postRead() {
			
		}
		
		@Override
		public boolean isEligible(Class<? extends Configurable> configurableClass) {
			return false;
		}
		
	}

	@ReadOrder(order = {
			"leafConfigurable"
	})
	public static class TestCompositeConfigurable extends CompositeConfigurable {
		@Configuration private LeafConfigurable leafConfigurable;
		
		private Runnable postRead;
		
		@Override
		public boolean isEligible(Class<? extends Configurable> configurableClass) {
			return true;
		}
		
		@PostRead
		public void postRead() {
			postRead.run();
		}
	}
	
	public static class DummyReader extends Reader {

		private Map<String, Configurable> classNameToConfigurableInstance = new HashMap<String, Configurable>();

		public <T extends Configurable> void addConfigurable(Class<T> configurableClass, T configurable) {
			classNameToConfigurableInstance.put(configurableClass.getName(), configurable);
		}
		
		@Override
		public Configurable read(ConfigurationContext configurationContext,
				Class<? extends Configurable> configurableClass) throws LoadConfigurationException {
			return classNameToConfigurableInstance.get(configurableClass.getName());
		}

		@Override
		public void reload(ConfigurationContext configurationContext, Configurable configurable)
				throws LoadConfigurationException {
			
		}
	}
}
