package com.elitecore.core.commons.config.core.readerimpl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.io.Files;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.ConfigurationContext;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class XMLReaderTest {

	private static final String UPDATED_VALUE = "ReloadablePropertyUpdated";
	private static final String INITIAL_VALUE = "Test";
	
	@Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private XMLReader reader;
	private ConfigurationContext configurationContext;
	private File configFile;

	@Before
	public void setUp() throws FileNotFoundException, IOException {
		ClasspathResource classpathResource = ClasspathResource.at("com/elitecore/core/commons/config/core/readerimpl/xml-reader-host.xml");
		File xmlFile = new File(classpathResource.getAbsolutePath());
		File tempXMLFileContainingDirectory = new File(temporaryFolder.getRoot().getAbsolutePath() + "/com/elitecore/core/commons/config/core/readerimpl/");
		tempXMLFileContainingDirectory.mkdirs();
		
		configFile = new File(tempXMLFileContainingDirectory.getAbsolutePath() + "/" + xmlFile.getName());
		
		copyContents(xmlFile, configFile);
		
		configurationContext = new ConfigurationContext(basePath());
		reader = new XMLReader();		
	}

	private String basePath() {
		return temporaryFolder.getRoot().getAbsolutePath() + File.separator;
	}
	
	@Test
	public void readsContentsFromXmlAndConvertsToJavaObject() throws LoadConfigurationException, IllegalArgumentException, IOException {
		XMLReaderHostConfigurable configurable = 
				(XMLReaderHostConfigurable) reader.read(configurationContext, XMLReaderHostConfigurable.class);
	
		assertThat(configurable.getProperty(), is(equalTo(INITIAL_VALUE)));
		assertThat(configurable.getReloadableProperty(), is(equalTo(INITIAL_VALUE)));
	}
	
	@Test
	public void injectsConfigurationContextIntoConfigurableAfterConvertingToJavaObject() throws LoadConfigurationException {
		XMLReaderHostConfigurable configurable = 
				(XMLReaderHostConfigurable) reader.read(configurationContext, XMLReaderHostConfigurable.class);
		
		assertThat(configurable.getConfigurationContext(), 
				is(sameInstance(configurationContext)));
	}
	
	public class Reload {
		
		@Test
		public void completelyReloadsTheConfigurableIfWholeConfigurableIsReloadable() throws LoadConfigurationException, IOException, JAXBException {
			ReloadableXMLReaderHostConfigurable configurable = 
					(ReloadableXMLReaderHostConfigurable) reader.read(configurationContext, ReloadableXMLReaderHostConfigurable.class);
		
			ReloadableXMLReaderHostConfigurable reloadedConfigurable = new ReloadableXMLReaderHostConfigurable();
			reloadedConfigurable.setProperty(UPDATED_VALUE);
			reloadedConfigurable.setReloadableProperty(UPDATED_VALUE);
			// TODO add other fields as well
			updateContent(configFile, contentsOf(reloadedConfigurable));
			
			reader.reload(configurationContext, configurable);
			
			assertThat(configurable.getProperty(), is(equalTo(UPDATED_VALUE)));
			assertThat(configurable.getReloadableProperty(), is(equalTo(UPDATED_VALUE)));
		}
		
		public class ConfigurableIsPartiallyReloadable {
			
			private XMLReaderHostConfigurable configurable;

			@Before
			public void setUp() throws LoadConfigurationException {
				configurable = (XMLReaderHostConfigurable) reader.read(configurationContext, XMLReaderHostConfigurable.class);
			}
			
			@Test
			public void reloadsOnlyTheContentsThatAreMarkedAsReloadable() throws LoadConfigurationException, IllegalArgumentException, IOException, JAXBException {
				XMLReaderHostConfigurable reloadedConfigurable = new XMLReaderHostConfigurable();
				reloadedConfigurable.setProperty(UPDATED_VALUE);
				reloadedConfigurable.setReloadableProperty(UPDATED_VALUE);
				
				updateContent(configFile, contentsOf(reloadedConfigurable));
				
				reader.reload(configurationContext, configurable);
				
				assertThat(configurable.getProperty(), is(equalTo(INITIAL_VALUE)));
				assertThat(configurable.getReloadableProperty(), is(equalTo(UPDATED_VALUE)));
			}
			
			public class NativeTypes {
				
				public class Collection {
					
					@Test
					public void completelyReloadsCollectionWithNativeTypes() throws LoadConfigurationException, IOException, JAXBException {
						XMLReaderHostConfigurable reloadedConfigurable = new XMLReaderHostConfigurable();
						reloadedConfigurable.getReloadableCollection().add(UPDATED_VALUE);
						reloadedConfigurable.getReloadableCollection().add(UPDATED_VALUE);
						
						updateContent(configFile, contentsOf(reloadedConfigurable));
						
						reader.reload(configurationContext, configurable);
						
						assertThat(configurable.getReloadableCollection(), 
								is(equalTo(reloadedConfigurable.getReloadableCollection())));
					}
				}
			}
			
			public class CustomTypes {
				
				@Test
				public void onlyReloadsPropertiesMarkedAsReloadableForCustomTypes() throws IOException, JAXBException, LoadConfigurationException {
					XMLReaderHostConfigurable reloadedConfigurable = new XMLReaderHostConfigurable();
					CustomType customType = new CustomType();
					customType.setCustomTypeProperty(UPDATED_VALUE);
					customType.setCustomTypeReloadableProperty(UPDATED_VALUE);
					reloadedConfigurable.setCustomType(customType);
					
					updateContent(configFile, contentsOf(reloadedConfigurable));
					
					reader.reload(configurationContext, configurable);
					
					assertThat(configurable.getCustomType().getCustomTypeProperty(), 
							is(equalTo(INITIAL_VALUE)));
					assertThat(configurable.getCustomType().getCustomTypeReloadableProperty(), 
							is(equalTo(UPDATED_VALUE)));
				}
				
				@Test
				public void completelyReloadsCustomTypeMarkedAsFullyReloadable() throws IOException, JAXBException, LoadConfigurationException {
					XMLReaderHostConfigurable reloadedConfigurable = new XMLReaderHostConfigurable();
					FullyReloadableCustomType customType = new FullyReloadableCustomType();
					customType.setId("2");
					customType.setCustomTypeProperty(UPDATED_VALUE);
					reloadedConfigurable.setFullyReloadableCustomType(customType);
					
					updateContent(configFile, contentsOf(reloadedConfigurable));
					
					reader.reload(configurationContext, configurable);
					
					assertThat(configurable.getFullyReloadableCustomType().getId(),
							is(equalTo("2")));
					assertThat(configurable.getFullyReloadableCustomType().getCustomTypeProperty(),
							is(equalTo(UPDATED_VALUE)));
				}
				
				public class Collection {
					
					@Test
					public void onlyReloadsThoseValuesOfCollectionWithCustomTypeWhichWereAlsoPresentInOldConfigurationButOnlyPropertiesMarkedAsReloadable() throws LoadConfigurationException, IOException, JAXBException {
						XMLReaderHostConfigurable reloadedConfigurable = new XMLReaderHostConfigurable();
						CustomType customType = new CustomType();
						customType.setId("1");
						customType.setCustomTypeProperty(UPDATED_VALUE);
						customType.setCustomTypeReloadableProperty(UPDATED_VALUE);
						reloadedConfigurable.getCustomTypeCollection().add(customType);
						
						updateContent(configFile, contentsOf(reloadedConfigurable));
						
						reader.reload(configurationContext, configurable);
						
						assertThat(configurable.getCustomTypeCollection().get(0).getCustomTypeProperty(), 
								is(equalTo(INITIAL_VALUE)));
						assertThat(configurable.getCustomTypeCollection().get(0).getCustomTypeReloadableProperty(), 
								is(equalTo(UPDATED_VALUE)));
					}
					
					@Test
					public void completelyReloadsCollectionIfCustomTypeIsFullyReloadable() throws IOException, JAXBException, LoadConfigurationException {
						XMLReaderHostConfigurable reloadedConfigurable = new XMLReaderHostConfigurable();
						FullyReloadableCustomType customType = new FullyReloadableCustomType();
						customType.setId("2");
						customType.setCustomTypeProperty(UPDATED_VALUE);
						reloadedConfigurable.getFullyReloadableCustomTypeCollection().add(customType);
						customType = new FullyReloadableCustomType();
						customType.setId("3");
						customType.setCustomTypeProperty(UPDATED_VALUE);
						reloadedConfigurable.getFullyReloadableCustomTypeCollection().add(customType);
						
						updateContent(configFile, contentsOf(reloadedConfigurable));
						
						reader.reload(configurationContext, configurable);
						
						assertThat(configurable.getFullyReloadableCustomTypeCollection(), 
								is(equalTo(reloadedConfigurable.getFullyReloadableCustomTypeCollection())));
					}
				}
			}
		}
	}
	
	private void updateContent(File configFile, String contentsOf) throws IOException {
		configFile.delete();
		FileWriter writer = null;
		try {
			writer = new FileWriter(configFile);
			writer.write(contentsOf);
		} finally {
			Closeables.closeQuietly(writer);
		}
	}

	private String contentsOf(Configurable reloadedConfigurable) throws JAXBException {
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(reloadedConfigurable.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(reloadedConfigurable, writer);
		return writer.toString();
	}

	@ConfigurationProperties(moduleName = "XML-READER-HOST", readWith = XMLReader.class, synchronizeKey = "")
	@XMLProperties(configDirectories = {"com" , "elitecore", "core", "commons", "config", "core", "readerimpl"}, schemaDirectories = "", name = "xml-reader-host")
	@XmlRootElement(name = "xml-reader-host")
	public static class XMLReaderHostConfigurable extends Configurable {
		
		private String property;
		private String reloadableProperty;
		private Collection<String> reloadableCollection = new ArrayList<String>();
		private CustomType customType = new CustomType();
		private List<CustomType> customTypeCollection = new ArrayList<CustomType>();
		private FullyReloadableCustomType fullyReloadableCustomType = new FullyReloadableCustomType();
		private List<FullyReloadableCustomType> fullyReloadableCustomTypeCollection = 
				new ArrayList<FullyReloadableCustomType>();
		
		@XmlElement(name = "property")
		public String getProperty() {
			return property;
		}
		
		public void setProperty(String property) {
			this.property = property;
		}
		
		@XmlElement(name = "reloadable-property")
		@Reloadable(type = String.class)
		public String getReloadableProperty() {
			return reloadableProperty;
		}

		public void setReloadableProperty(String reloadableProperty) {
			this.reloadableProperty = reloadableProperty;
		}
		
		@XmlElementWrapper(name = "reloadable-collection")
		@XmlElement(name = "collection-entry")
		@Reloadable(type = String.class)
		public Collection<String> getReloadableCollection() {
			return reloadableCollection;
		}

		public void setReloadableCollection(Collection<String> reloadableCollection) {
			this.reloadableCollection = reloadableCollection;
		}

		@XmlElement(name = "custom-type")
		@Reloadable(type = CustomType.class)
		public CustomType getCustomType() {
			return customType;
		}

		public void setCustomType(CustomType customType) {
			this.customType = customType;
		}

		@XmlElementWrapper(name = "custom-type-collection")
		@XmlElement(name = "custom-type")
		@Reloadable(type = CustomType.class)
		public List<CustomType> getCustomTypeCollection() {
			return customTypeCollection;
		}

		public void setCustomTypeCollection(List<CustomType> customTypeCollection) {
			this.customTypeCollection = customTypeCollection;
		}

		@XmlElement(name = "fully-reloadable-custom-type")
		@Reloadable(type = FullyReloadableCustomType.class)
		public FullyReloadableCustomType getFullyReloadableCustomType() {
			return fullyReloadableCustomType;
		}

		public void setFullyReloadableCustomType(FullyReloadableCustomType fullyReloadableCustomType) {
			this.fullyReloadableCustomType = fullyReloadableCustomType;
		}

		@XmlElementWrapper(name = "fully-reloadable-custom-type-collection")
		@XmlElement(name = "fully-reloadable-custom-type")
		@Reloadable(type = FullyReloadableCustomType.class)
		public List<FullyReloadableCustomType> getFullyReloadableCustomTypeCollection() {
			return fullyReloadableCustomTypeCollection;
		}

		public void setFullyReloadableCustomTypeCollection(
				List<FullyReloadableCustomType> fullyReloadableCustomTypeCollection) {
			this.fullyReloadableCustomTypeCollection = fullyReloadableCustomTypeCollection;
		}
		
		@Override
		@VisibleForTesting
		public ConfigurationContext getConfigurationContext() {
			return super.getConfigurationContext();
		}
	}
	
	public static class CustomType {
		private String id;
		private String customTypeProperty;
		private String customTypeReloadableProperty;

		@XmlElement(name = "id")
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		@XmlElement(name = "custom-property")
		public String getCustomTypeProperty() {
			return customTypeProperty;
		}
		public void setCustomTypeProperty(String customTypeProperty) {
			this.customTypeProperty = customTypeProperty;
		}
		
		@XmlElement(name = "custom-reloadable-property")
		@Reloadable(type = String.class)
		public String getCustomTypeReloadableProperty() {
			return customTypeReloadableProperty;
		}
		
		public void setCustomTypeReloadableProperty(String customTypeReloadableProperty) {
			this.customTypeReloadableProperty = customTypeReloadableProperty;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CustomType other = (CustomType) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "CustomType [id=" + id + ", customTypeProperty=" + customTypeProperty
					+ ", customTypeReloadableProperty=" + customTypeReloadableProperty + "]";
		}
	}
	
	@Reloadable(type = FullyReloadableCustomType.class)
	public static class FullyReloadableCustomType {
		private String id;
		private String customTypeProperty;

		@XmlElement(name = "id")
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		@XmlElement(name = "custom-property")
		public String getCustomTypeProperty() {
			return customTypeProperty;
		}
		
		public void setCustomTypeProperty(String customTypeProperty) {
			this.customTypeProperty = customTypeProperty;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FullyReloadableCustomType other = (FullyReloadableCustomType) obj;
			if (customTypeProperty == null) {
				if (other.customTypeProperty != null)
					return false;
			} else if (!customTypeProperty.equals(other.customTypeProperty))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "FullyReloadableCustomType [id=" + id + ", customTypeProperty=" + customTypeProperty + "]";
		}
	}
	
	@ConfigurationProperties(moduleName = "RELOADABLE-XML-READER-HOST", readWith = XMLReader.class, synchronizeKey = "")
	@XMLProperties(configDirectories = {"com" , "elitecore", "core", "commons", "config", "core", "readerimpl"}, schemaDirectories = "", name = "xml-reader-host")
	@XmlRootElement(name = "xml-reader-host")
	@Reloadable(type = ReloadableXMLReaderHostConfigurable.class)
	public static class ReloadableXMLReaderHostConfigurable extends Configurable {
		
		private String property;
		private String reloadableProperty;

		@XmlElement(name = "property")
		public String getProperty() {
			return property;
		}
		
		public void setProperty(String property) {
			this.property = property;
		}
		
		@XmlElement(name = "reloadable-property")
		public String getReloadableProperty() {
			return reloadableProperty;
		}

		public void setReloadableProperty(String reloadableProperty) {
			this.reloadableProperty = reloadableProperty;
		}
	}
	
	private void copyContents(File xmlFile, File tempXMLFile) throws FileNotFoundException, IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tempXMLFile);
			fos.write(Files.readFully(xmlFile));
		} finally {
			Closeables.closeQuietly(fos);
		}
	}
}
