package org.ngo.common.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

class ApplicationContextRegister implements ApplicationContextAware {

	static ApplicationContext applicationContext;
	
	@Override
	public synchronized void setApplicationContext(ApplicationContext context) throws BeansException {
		if (applicationContext == null)
			applicationContext = context;
	}
}
