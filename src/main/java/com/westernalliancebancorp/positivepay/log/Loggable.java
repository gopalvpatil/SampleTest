package com.westernalliancebancorp.positivepay.log;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Custom annotation to be used to inject a logger class during runtime on every class.
 * @author <a href="mailto:akumar1@intraedge.com">Anand Kumar</a>
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
@Documented
public @interface Loggable {
}
