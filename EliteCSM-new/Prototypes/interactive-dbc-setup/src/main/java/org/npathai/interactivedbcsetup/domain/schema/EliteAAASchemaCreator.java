package org.npathai.interactivedbcsetup.domain.schema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class EliteAAASchemaCreator {

	static {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private List<SchemaElement> schemaElements;

	private SchemaEventListener listener;

	public EliteAAASchemaCreator() {

		schemaElements = new ArrayList<>();

		ClassPathScanningCandidateComponentProvider scanner =
				new ClassPathScanningCandidateComponentProvider(true);

		scanner.addIncludeFilter(new AnnotationTypeFilter(Table.class));
		
		for (BeanDefinition bean : scanner.findCandidateComponents("org.npathai.interactivedbcsetup.domain.schema")) {
			schemaElements.add(createInstance(bean));
		}
	}

	private SchemaElement createInstance(BeanDefinition bean) {
		try {
			Class<?> clazz = Class.forName(bean.getBeanClassName());
			return SchemaElement.class.cast(clazz.newInstance());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new AssertionError("Should not happen");
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new AssertionError("Should not happen");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new AssertionError("Should not happen");
		}
	}

	public void addSchemaEventListener(SchemaEventListener listener) {
		this.listener = listener;
		schemaElements.forEach(element -> element.addSchemaEventListener(listener));
	}

	public void create() throws SQLException {
		listener.starting(schemaElements.size());
		try (Connection connection = DriverManager.getConnection("jdbc:derby:derbyDB;create=true")) {
			for (SchemaElement element : schemaElements) {
				element.create(connection);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			listener.error("", ex.getMessage());
			throw ex;
		}
	}

	public void drop() throws SQLException {
		listener.starting(schemaElements.size());
		try (Connection connection = DriverManager.getConnection("jdbc:derby:derbyDB;create=true")) {
			for (SchemaElement element : schemaElements) {
				element.drop(connection);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			listener.error("", ex.getMessage());
			throw ex;
		}
	}

	public void dropForcefully() {
		listener.starting(schemaElements.size());
		try (Connection connection = DriverManager.getConnection("jdbc:derby:derbyDB;create=true")) {
			for (SchemaElement element : schemaElements) {
				try {
					element.drop(connection);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			listener.error("", ex.getMessage());
		}
	}
}
