package com.elitecore.core.commons.config.core.annotations;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elitecore.core.commons.config.core.Reader;
import com.elitecore.core.commons.config.core.Writer;
import com.elitecore.core.commons.config.core.readerimpl.NullReloader;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.config.core.writerimpl.NullWriter;


/**
 * 
 * @author narendra.pathai
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigurationProperties {
	public String moduleName();
	public Class<? extends Reader> readWith();
	public Class<? extends Writer> writeWith() default NullWriter.class;
	public Class<? extends Reader> reloadWith() default NullReloader.class;
	public Class<? extends Reader> fallbackReadWith() default XMLReader.class;
	public String synchronizeKey();
}
