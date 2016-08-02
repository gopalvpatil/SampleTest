package com.westernalliancebancorp.positivepay.log;

import java.lang.reflect.Field;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
/**
 * This class injects a custom logger on every class that has a private variable annotated with @Loggable
 * @author <a href="mailto:akumar1@intraedge.com">Anand Kumar</a>
 *
 */
@Component
public class LoggerInjector implements BeanPostProcessor {
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	public Object postProcessBeforeInitialization(final Object bean,
			String beanName) throws BeansException {
		ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				ReflectionUtils.makeAccessible(field);
				if (field.getAnnotation(Loggable.class) != null) {
					field.set(bean, LoggerFactory.getLogger(bean.getClass()));
				}
			}
		});
		return bean;
	}
}
