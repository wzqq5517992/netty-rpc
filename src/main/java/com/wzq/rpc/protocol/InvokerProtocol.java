package com.wzq.rpc.protocol;

import lombok.Data;

/**自定义传送协议层
 * @author wzq.Jolin
 * @company none
 * @create 2019-06-18 19:00
 */
@Data
public class InvokerProtocol {
    /**
     * 类名
     */
    private String className;
    /**
     * 函数名称
     */
    private String methodName;
    /**
     * 形参列表
     */
    private Class< ? >[] parames;
    /**
     * 实参列表
     */
    private Object[] values;
}
