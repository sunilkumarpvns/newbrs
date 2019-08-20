package com.elitecore.core.commons.config.core.annotations;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation marks the configuration entities in the class that are reloadable.
 * <br>
 * Now the annotation can be present at two levels : 1) Class Level 2) Method level
 * <br><br>
 * <b>Class Level :</b> When the annotation is present at the class level it means that the 
 * whole class is Reloadable.
 * <br>
 * <b>Method Level :</b> When the annotation is present at the method level then it marks the
 * particular entity of configuration as reloadable.
 * <br><br>
 * <b>NOTE: Marking the class reloadable and then also marking methods reloadable will have 
 * following effect, Class level annotation will be considered and whole class will be reloaded.
 * <b>
 * 
 * @author narendra.pathai
 *
 */

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Reloadable {
	public Class<?> type(); 
}
