import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

class DefineTransformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        String modifyClassName = "com.github.realsky.aweb.HelloController";
        String modifyClassMethod = "hello";
        String loadClassName = modifyClassName.replace(".", "/");
        if (className.equals(loadClassName)) {
            ClassPool pool = ClassPool.getDefault();
            pool.appendSystemPath();
            pool.appendClassPath(new LoaderClassPath(loader)); // 追加
            CtClass cc;
            try {
                cc = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
                CtMethod declaredMethod = cc.getDeclaredMethod(modifyClassMethod);

                declaredMethod.addLocalVariable("start", CtClass.longType);
                declaredMethod.insertBefore("start = System.currentTimeMillis();");
                declaredMethod.insertAfter("System.out.println(\"exec time is :\" + (System.currentTimeMillis() - start) + \"ms\");");
                declaredMethod.insertAfter("Client.send(String.valueOf(System.currentTimeMillis() - start));");
                return cc.toBytecode();
            } catch (NotFoundException | CannotCompileException | IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
