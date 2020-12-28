package com;

import com.constants.druid.PrintDruidSQLReplacer;
import com.constants.feign.PrintFeignRequestReplacer;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static com.constants.druid.PrintDruidSQLReplacer.*;

/**
 * @author by yze on 2020/12/21
 * 在DruidPooledPreparedStatement中打印sql
 * @since 202012
 */
public class SQLPrepareAgent {


    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new DefineTransformer(), true);
    }

    static class DefineTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            PrintDruidSQLReplacer sqlReplacer = new PrintDruidSQLReplacer();
            PrintFeignRequestReplacer feignReplacer = new PrintFeignRequestReplacer();

            if (sqlReplacer.support(className)) {
                return sqlReplacer.replaceMethod(className);
            } else if (feignReplacer.support(className)) {
                return feignReplacer.replaceMethod(className);
            }
            return null;
//            if (PrintDruidSQLReplacer.CLASS_PATH.equals(className)) {
//                try {
//                    final ClassPool classPool = ClassPool.getDefault();
//                    final CtClass ctClass = classPool.get(PrintDruidSQLReplacer.CLASS_REFERENCE);
//                    replaceMethod(ctClass, "execute", EXECUTE_METHOD_BODY);
//                    replaceMethod(ctClass, "executeBatch", EXECUTE_BATCH_METHOD_BODY);
//                    replaceMethod(ctClass, "executeQuery", EXECUTE_QUERY_METHOD_BODY);
//                    replaceMethod(ctClass, "executeUpdate", EXECUTE_UPDATE_METHOD_BODY);
//                    byte[] byteCode = ctClass.toBytecode();
//                    ctClass.detach();
//                    return byteCode;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
        }

        private void replaceMethod(CtClass ctClass, String methodname, String replaceMethod) throws NotFoundException, CannotCompileException {
            CtMethod method = ctClass.getDeclaredMethod(methodname);
            method.setBody(replaceMethod);
        }
    }
}