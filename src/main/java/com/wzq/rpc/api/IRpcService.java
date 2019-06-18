package com.wzq.rpc.api;

/**
 * @author wzq.Jolin
 * @company none
 * @create 2019-06-18 18:46
 */
public interface IRpcService {


    /** 加 */
    public int add(int a,int b);

    /** 减 */
    public int sub(int a,int b);

    /** 乘 */
    public int mult(int a,int b);

    /** 除 */
    public int div(int a,int b);
}
