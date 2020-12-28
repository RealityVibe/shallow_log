package com.constants.druid;

import com.constants.MethodReplacer;
import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author by yze on 2020/12/25
 * Druid方法替换
 * @since 202012
 */
public class PrintDruidSQLReplacer implements MethodReplacer {
    /**
     * 目标方法
     */
    private static final String CLASS_PATH = "com/alibaba/druid/pool/DruidPooledPreparedStatement";
    /**
     * 目标方法
     */
    private static final String CLASS_REFERENCE = "com.alibaba.druid.pool.DruidPooledPreparedStatement";

    /**
     * 加强的内容
     */
    private static final String ENHANCE_LINES = "java.lang.reflect.Field field = com.alibaba.druid.proxy.jdbc.WrapperProxyImpl.class.getDeclaredField(\"raw\");field.setAccessible(true);" +
            "  com.nuonuo.accounting.common.logger.ChainCode chainCode = com.nuonuo.accounting.common.logger.LogChainUtil.getChainCode();\n" +
            "        java.lang.String sql = field.get(stmt).toString();\n" +
            "        if(chainCode != null){\n" +
            "            long costTime = java.lang.System.currentTimeMillis() - bt;\n" +
            "            org.slf4j.LoggerFactory.getLogger(getClass()).info(\"[shallow_log_begin]: log_no:[\" + chainCode.getReqGuid() + \"], app_tag:[\" + chainCode.getAppTag() + \"], cost time: [\" + costTime + \"ms], sql:\" + sql +\" [shallow_log_end]\");\n" +
            "        } else {\n" +
            "            org.slf4j.LoggerFactory.getLogger(getClass()).info(\"[shallow_log_begin]: sql:\" + sql + \" [shallow_log_end]\");\n" +
            "        }";

    /**
     * 查询sql
     */
    private static final String EXECUTE_QUERY_METHOD_BODY = "{checkOpen();incrementExecuteQueryCount();transactionRecord(sql);long bt = java.lang.System.currentTimeMillis();oracleSetRowPrefetch();conn.beforeExecute();try {java.sql.ResultSet rs = stmt.executeQuery();if (rs == null) {    return null;}com.alibaba.druid.pool.DruidPooledResultSet poolableResultSet = new com.alibaba.druid.pool.DruidPooledResultSet(this, rs);addResultSetTrace(poolableResultSet);return poolableResultSet;} catch (Throwable t) {errorCheck(t);throw checkException(t);} finally {" + ENHANCE_LINES + "conn.afterExecute();}}";

    /**
     * 执行insert
     */
    private static final String EXECUTE_METHOD_BODY = "{checkOpen();incrementExecuteQueryCount();transactionRecord(sql);long bt = java.lang.System.currentTimeMillis();incrementExecuteCount();transactionRecord(sql);conn.beforeExecute();try {return stmt.execute();} catch (Throwable t) {errorCheck(t);throw checkException(t);} finally {" + ENHANCE_LINES + "conn.afterExecute();}}";

    /**
     * 执行update
     */
    private static final String EXECUTE_UPDATE_METHOD_BODY = "{checkOpen();incrementExecuteQueryCount();transactionRecord(sql);long bt = java.lang.System.currentTimeMillis();incrementExecuteUpdateCount();transactionRecord(sql);conn.beforeExecute();try {return stmt.executeUpdate();} catch (Throwable t) {errorCheck(t);throw checkException(t);} finally {" + ENHANCE_LINES + "conn.afterExecute();}}";

    /**
     * 执行batch批量操作
     */
    private static final String EXECUTE_BATCH_METHOD_BODY = "{checkOpen();incrementExecuteQueryCount();transactionRecord(sql);long bt = java.lang.System.currentTimeMillis();incrementExecuteBatchCount();transactionRecord(sql);conn.beforeExecute();try {return stmt.executeBatch();} catch (Throwable t) {errorCheck(t);throw checkException(t);} finally {" + ENHANCE_LINES + "conn.afterExecute();}}";

    @Override
    public boolean support(String className) {
        return CLASS_PATH.equals(className);
    }

    @Override
    public byte[] replaceMethod(String className) {
        try {
            final ClassPool classPool = ClassPool.getDefault();
            final CtClass ctClass = classPool.get(PrintDruidSQLReplacer.CLASS_REFERENCE);
            doReplace(ctClass, "execute", EXECUTE_METHOD_BODY);
            doReplace(ctClass, "executeBatch", EXECUTE_BATCH_METHOD_BODY);
            doReplace(ctClass, "executeQuery", EXECUTE_QUERY_METHOD_BODY);
            doReplace(ctClass, "executeUpdate", EXECUTE_UPDATE_METHOD_BODY);
            byte[] byteCode = ctClass.toBytecode();
            ctClass.detach();
            return byteCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
