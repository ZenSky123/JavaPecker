import java.net.URL;
import java.net.URLClassLoader;

public class PeckerClassLoader extends URLClassLoader {

    public PeckerClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }


    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("java")) {
            return super.loadClass(name, resolve);
        }
        Class<?> loadedClass = this.findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        } else {
            try {
                Class<?> aClass = this.findClass(name);
                if (resolve) {
                    this.resolveClass(aClass);
                }
                if (aClass == null) {
                    return super.loadClass(name, resolve);
                }
                return aClass;
            } catch (Exception var5) {
                return super.loadClass(name, resolve);
            }
        }
    }
}
