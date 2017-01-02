package org.ngo.common.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class ServiceLocator{
	
	private ServiceLocator() {
	}

	public static final ServiceLocator Instance = new ServiceLocator();
	
	public final Object get(Class clazz, String serviceName, String address) {
		Object proxy =  getProxy(clazz, serviceName, address);
		return proxy;
	}
	
	private final Map<String,Object> CACHE = new ConcurrentHashMap<String, Object>();

	private Object getProxy(Class clazz, String servcieName, String address) {
		if (CACHE.containsKey(clazz.getName()+servcieName+address))
			return CACHE.get(clazz.getName()+servcieName+address);
		
		Class[] interfaces = {clazz};
		InvocationHandler handler = new UnderlayingInvocationHandler(clazz, servcieName, address);
		ClassLoader classloader = handler.getClass().getClassLoader();
		Object proxy =  Proxy.newProxyInstance(classloader, interfaces, handler);

		CACHE.put(clazz.getName()+servcieName+address, proxy);
		return proxy;
	}

	private static class UnderlayingInvocationHandler implements InvocationHandler {
		private String remoteAddress;
		private Class clazz;
		private String serviceName;

		public UnderlayingInvocationHandler(Class serviceClazz, String serviceName, String address) {
			this.clazz = serviceClazz;
			this.serviceName = serviceName;
			this.remoteAddress = address;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			return RemoteProcessInvoker.Instance.invoke(serviceName, remoteAddress, clazz.getName(), method.getName(), args);
			
		}
	}

	private static class RemoteProcessInvoker {
		private RemoteProcessInvoker() {
		}
		
		static final RemoteProcessInvoker Instance = new RemoteProcessInvoker();
		
		Object invoke(String serviceName, String remoteAddress, String clazz, String method, Object[] args) {
			
			//HXY: please be aware that a proxy which might be cached in front end by key "clazz.getName()+servcieName+address" 
			
			System.out.println("connect to remote service node...");
			System.out.println(String.format("invoke remote service [%s@%s(%s)]",serviceName,method,args));
			System.out.println("get returned object");
			return new User((Long)args[0], "RemoteUser", 10);

		}
	}



}
