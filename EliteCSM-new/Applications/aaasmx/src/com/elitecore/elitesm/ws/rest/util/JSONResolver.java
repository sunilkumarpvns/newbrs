package com.elitecore.elitesm.ws.rest.util;

import javax.ws.rs.ext.ContextResolver;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 * CXF default Object mapper was not able to properly handle null object and shows null value in the response. 
 * In such a scenario one has to manually initialize the object mapper and set its <b>Serialization Inclusion</b>
 * property.
 * <br>
 * <br>In case of future enhancement or if one has to set other properties for object mapper it can be done in
 * this class.
 * <br>
 * <br>This applies to all, so Object mapper instance for every request will be generated here.
 * @author animesh christie
 */

public class JSONResolver implements ContextResolver<ObjectMapper>{

    private static ObjectMapper getJSONResolver() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.setAnnotationIntrospector(
            AnnotationIntrospector.pair(
                new JacksonAnnotationIntrospector(),
                new JaxbAnnotationIntrospector()
        ));
        return (mapper);
    }
    
	@Override
	public ObjectMapper getContext(Class<?> arg0) {
		return getJSONResolver();
	}
}