package com.wzq.rpc.consumer;


import com.wzq.rpc.api.IRpcHelloService;
import com.wzq.rpc.api.IRpcService;
import com.wzq.rpc.consumer.proxy.RpcProxy;
import com.wzq.rpc.provider.RpcServiceImpl;

/**客户端发起调用
 * @author wzq.Jolin
 * @company none
 * @create 2019-06-18 19:00
 */
public class RpcConsumer {
	
    public static void main(String [] args){

        //本地调用
//        IRpcService service=new RpcServiceImpl();
//        System.out.println("8 + 2 = " + service.add(8, 2));

        IRpcHelloService rpcHello = RpcProxy.create(IRpcHelloService.class);
        
        System.out.println(rpcHello.hello("Jolin"));

        IRpcService service = RpcProxy.create(IRpcService.class);
        
        System.out.println("8 + 2 = " + service.add(8, 2));
        System.out.println("8 - 2 = " + service.sub(8, 2));
        System.out.println("8 * 2 = " + service.mult(8, 2));
        System.out.println("8 / 2 = " + service.div(8, 2));
    }
    
}
