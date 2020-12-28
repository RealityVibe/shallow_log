package com.constants;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public interface MethodReplacer {

    /**
     * 是否支持该方法
     *
     * @param className 方法
     * @return 是否支持
     */
    boolean support(String className);

    /**
     * 替换方法
     *
     * @param className 方法
     */
    byte[] replaceMethod(String className);

    /**
     * @param ctClass           类
     * @param methodname        方法名
     * @param replaceMethodBody 新方法体
     * @throws NotFoundException      异常
     * @throws CannotCompileException 异常
     */
    default void doReplace(CtClass ctClass, String methodname, String replaceMethodBody) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod(methodname);
        method.setBody(replaceMethodBody);
    }
}
