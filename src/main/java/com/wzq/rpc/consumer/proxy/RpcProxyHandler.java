package com.wzq.rpc.consumer.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**客户端
 * @author wzq.Jolin
 * @company none
 * @create 2019-06-18 19:00
 */
public class RpcProxyHandler extends ChannelInboundHandlerAdapter {
	  
    private Object response;    
      
    public Object getResponse() {    
	    return response;    
	}    
  
    @Override    
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = msg;
    }    
        
    @Override    
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client exception is general");    
    }    
} 
