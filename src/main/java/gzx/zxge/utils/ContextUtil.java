package gzx.zxge.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ContextUtil implements ApplicationContextAware
{
	/**
	 * ApplicationContext实例
	 */
	private static ApplicationContext context;

	public static ApplicationContext getContext()
	{

		return context;
	}

	public static Object getBean(String name)
	{
		if(context == null) {
			return null;
		}
		return context.getBean(name);
	}

	// public static <T> T getBean(String name, Class<T> requiredType)
	// {
	// return context.getBean(name, requiredType);
	// }

	public void setContext(ApplicationContext context)
	{
		ContextUtil.context = context;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException
	{
		ContextUtil.context = arg0;
	}
}