package com.gmail.zahusek.tinyprotocolapi.asm.reflection;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;

class ClassManager extends ClassLoader {

	private static final HashMap<String, Class<?>> classes = new HashMap<>();
    private static final String v = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

	private static final WeakHashMap<ClassLoader, WeakReference<ClassManager>> accessClassLoaders = new WeakHashMap<>();
	private static final ClassLoader selfContextParentClassLoader = getParentClassLoader(ClassManager.class);
	private static volatile ClassManager selfContextAccessClassLoader = new ClassManager(selfContextParentClassLoader);

	static ClassManager get(String type) throws ClassNotFoundException {
		Class<?> clazz = classes.get(type) != null ? classes.get(type) : Class.forName(type.replace("{v}", v)) ;
		if(!classes.containsKey(type))
			classes.put(type, clazz);
		return get(clazz);
	}
	
	static ClassManager get(Class<?> type) {
		ClassLoader parent = getParentClassLoader(type);
		if (selfContextParentClassLoader.equals(parent)) {
			if (selfContextAccessClassLoader == null) {
				synchronized (accessClassLoaders) {
					if (selfContextAccessClassLoader == null)
						selfContextAccessClassLoader = new ClassManager(
								selfContextParentClassLoader);
				}
			}
			return selfContextAccessClassLoader;
		}
		synchronized (accessClassLoaders) {
			WeakReference<ClassManager> ref = accessClassLoaders.get(parent);
			if (ref != null) {
				ClassManager accessClassLoader = ref.get();
				if (accessClassLoader != null)
					return accessClassLoader;
				else
					accessClassLoaders.remove(parent); // the value has been
														// GC-reclaimed, but
														// still not the key
														// (defensive sanity)
			}
			ClassManager accessClassLoader = new ClassManager(parent);
			accessClassLoaders.put(parent, new WeakReference<ClassManager>(
					accessClassLoader));
			return accessClassLoader;
		}
	}

	public static void remove(ClassLoader parent) {
		if (selfContextParentClassLoader.equals(parent)) {
			selfContextAccessClassLoader = null;
		} else {
			synchronized (accessClassLoaders) {
				accessClassLoaders.remove(parent);
			}
		}
	}

	public static int activeAccessClassLoaders() {
		int sz = accessClassLoaders.size();
		if (selfContextAccessClassLoader != null)
			sz++;
		return sz;
	}

	private ClassManager(ClassLoader parent) {
		super(parent);
	}

	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		if (name.equals(Access.class.getName()))
			return Access.class;
		return super.loadClass(name, resolve);
	}
	Class<?> defineClass(String name, byte[] bytes) throws ClassFormatError {
		try {
			Method method = ClassLoader.class.getDeclaredMethod("defineClass",
					new Class[]{String.class, byte[].class, int.class,
							int.class, ProtectionDomain.class});
			if (!method.isAccessible())
				method.setAccessible(true);
			return (Class<?>) method.invoke(getParent(), new Object[]{name,
					bytes, Integer.valueOf(0), Integer.valueOf(bytes.length),
					getClass().getProtectionDomain()});
		} catch (Exception ignored) {
		}
		return defineClass(name, bytes, 0, bytes.length, getClass()
				.getProtectionDomain());
	}

	static ClassLoader getParentClassLoader(Class<?> type) {
		ClassLoader parent = type.getClassLoader();
		if (parent == null)
			parent = ClassLoader.getSystemClassLoader();
		return parent;
	}
}