package jaxws.async.wsfvt.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanProfiler {

	private static String SEP = System.getProperty("line.separator");
	private static Object[] PARAMS = new Object[]{};
	
	public static String profile(Object o){
		if (o == null) return null;
		
		String profile = o.getClass().getName() + SEP;
		
		Method[] methods = o.getClass().getMethods();
		
		for (Method m: methods){
			if (m.getName().startsWith("get") || m.getName().startsWith("is")){
				Class[] params = m.getParameterTypes();
				if (params == null || params.length == 0){
					try {
						profile += "  ." + m.getName() + "() = " + m.invoke(o, PARAMS) + SEP;
					} catch (Exception e) {}
				}
			}
		}
		return profile;
	}
}
