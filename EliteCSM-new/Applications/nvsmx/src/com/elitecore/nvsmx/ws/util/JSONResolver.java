package com.elitecore.nvsmx.ws.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;


/**
 * CXF default Object mapper was not able to properly handle null object and shows null value in the response. 
 * In such a scenario one has to manually initialize the object mapper and set its <b>Serialization Inclusion</b>
 * property.
 * <br>
 * <br>In case of future enhancement or if one has to set other properties for object mapper it can be done in
 * this class.
 * <br>
 * <br>This applies to all, so Object mapper instance for every request will be generated here.
 * @author aditya shrivastava
 */

@Provider
public class JSONResolver implements ContextResolver<ObjectMapper>{

    private static ObjectMapper mapper;

    static {
        init();
    }

    private static void init() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        mapper.setAnnotationIntrospector(
                AnnotationIntrospector.pair(
                        new JacksonAnnotationIntrospector(),
                        new JaxbAnnotationIntrospector(TypeFactory.defaultInstance())
                ));
    }

    @Override
	public ObjectMapper getContext(Class<?> arg0) {
        return mapper;
	}
}