package org.ngo.common.service;

import org.apache.log4j.Logger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ServiceInjectBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
	private ApplicationContext applicationContext;
	private static Logger LOGGER = Logger.getLogger(ServiceInjectBeanPostProcessor.class);
	
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (hasAnnotation(bean.getClass().getAnnotations(), Component.class.getName()) !=null) {
			processServiceInject(bean);
		}
		return bean;
	}

	private void processServiceInject(Object bean) {
		Class beanClass = bean.getClass();
		do {
			Field[] fields = beanClass.getDeclaredFields();
			for (Field field : fields) {
				Annotation annotation =  hasAnnotation(field.getAnnotations(), ServiceComponent.class.getName());
				if (annotation != null && annotation instanceof ServiceComponent)
					setField(bean, field, (ServiceComponent)annotation);
			}
		} while ((beanClass = beanClass.getSuperclass()) != null);
	}

	private void setField(Object bean, Field field,ServiceComponent annotation) {
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		try {
			Class clazz = field.getType();
			String serviceName = annotation.value().isEmpty() ? clazz.getName() : (String)annotation.value();
			String address = (String)annotation.address();
			Object serviceBean = null;
			if (!address.equals("Local"))
				serviceBean = ServiceLocator.Instance.get(clazz, serviceName, address);
			else
				 serviceBean = applicationContext.getBean(serviceName);
			field.set(bean, serviceBean);
			LOGGER.info(String.format("inject %s service [%s]  to [%s#%s]", address.equals("Local") ? "Local" : address, serviceName, clazz.getName() ,field.getName()));
		} catch (Exception e) {
			LOGGER.error(String.format("service injection failed -%s", e.getLocalizedMessage()));
		}
	}

	private Annotation hasAnnotation(Annotation[] annotations, String annotationName) {
		if (annotations == null) {
			return null;
		}
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().getName().equals(annotationName)) {
				return annotation;
			}
		}
		return null;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * HXY: is the synchronized modifier is necessary ?
	 */
	public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (this.applicationContext == null)
			this.applicationContext = applicationContext;
	}
}
