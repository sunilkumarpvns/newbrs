package com.elitecore.config.core.annotations;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elitecore.config.core.Reader;
import com.elitecore.config.core.Writer;
import com.elitecore.config.core.readerimpl.NullReloader;
import com.elitecore.config.core.writerimpl.NullWriter;


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
	public String synchronizeKey();
}
