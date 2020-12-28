package com.constants.feign;

import com.constants.MethodReplacer;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author by yze on 2020/12/25
 * 打印feign请求
 * @since 202012
 */
public class PrintFeignRequestReplacer implements MethodReplacer {

    /**
     * 目标方法
     */
    private static final String CLASS_PATH = "feign/SynchronousMethodHandler";

    /**
     * 目标方法
     */
    private static final String CLASS_REFERENCE = "feign.SynchronousMethodHandler";

    /**
     * 加强的内容
     */
    private static final String ENHANCE_LINE = "com.nuonuo.accounting.common.logger.ChainCode chainCode = com.nuonuo.accounting.common.logger.LogChainUtil.getChainCode();" +
            "org.slf4j.LoggerFactory.getLogger(getClass())" +
            ".info(\"[shallow_log_begin]: log_no:[\" + chainCode.getReqGuid() + \"], " +
            "app_tag:[\" + chainCode.getAppTag() + \"], " +
            "req:\" + template.toString() +\" [shallow_log_end]\");";

    @Override
    public boolean support(String className) {
        return CLASS_PATH.equals(className);
    }


    @Override
    public byte[] replaceMethod(String className) {
        try {
            final ClassPool classPool = ClassPool.getDefault();
            final CtClass ctClass = classPool.get(CLASS_REFERENCE);
            CtMethod method = ctClass.getDeclaredMethod("invoke");
            method.insertAt(78, ENHANCE_LINE);
            byte[] byteCode = ctClass.toBytecode();
            ctClass.detach();
            return byteCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
