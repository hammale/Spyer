package nickguletskii200.SpyerAdmin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Made specifically for Essentials. Damned bloody class.
 * 
 * @author nickguletskii200
 */
public class SimpleClassLoader {
	public Class<?> getCl(String jar, String cl) throws MalformedURLException,
			ClassNotFoundException {
		ClassLoader loader = URLClassLoader.newInstance(new URL[] { new File(
				jar).toURI().toURL() }, getClass().getClassLoader());
		Class<?> clazz = Class.forName(cl, true, loader);
		return clazz;
	}

	public Method getFirstMethod(@SuppressWarnings("rawtypes") Class cl, String name) {
		for (Method m : cl.getMethods()) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}

	public Object invokeMethod(Method meth, Object instance, Object[] args) {
		try {
			return meth.invoke(instance, args);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
