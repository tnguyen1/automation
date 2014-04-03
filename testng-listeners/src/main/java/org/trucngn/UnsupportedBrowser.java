package org.trucngn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UnsupportedBrowser method annotation.
 * 
 * @author Truc Nguyen
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UnsupportedBrowser {

    String value() default "ie";

}
