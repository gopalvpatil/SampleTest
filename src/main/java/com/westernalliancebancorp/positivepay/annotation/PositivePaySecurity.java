package com.westernalliancebancorp.positivepay.annotation;

import com.westernalliancebancorp.positivepay.model.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: gduggirala
 * Date: 29/4/14
 * Time: 10:45 AM
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RUNTIME)
public @interface PositivePaySecurity {
    Permission.TYPE group();
    String resource();
    String errorMessage();
}
