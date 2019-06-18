package com.wzq.rpc.register;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;

/**
 * @author wzq.Jolin
 * @company none
 * @create 2019-06-18 22:47
 */
public class RpcRegistry {
    private  int  port;

    public RpcRegistry(int port) {
        this.port = port;
    }


    public  void  start(){

        //ServerSocketChannel/ServerSocket
        //机遇nio实现
        //Selector主线程   work线程

        //初始化主线程池 Selector
        EventLoopGroup bossGroup = new NioEventLoopGroup();  //工作效率是cpu的2倍，以保证cpu不会达到100%的状态
      //子线程初始化，对应客户端的处理逻辑
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrapServer = new ServerBootstrap();
        bootstrapServer.group(bossGroup,workGroup)
                //主线程轮询
                .channel(NioServerSocketChannel.class)
                //子线程执行
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {

                        //在netty中，把所有的业务逻辑处理全部都归总到了一个队列中
                        //这个队列中包含了各种各样的处理逻辑，对这这些处理逻辑在netty中有一个封装。
                        //封装成了一个对象，无锁化串行对象 这个对象叫做PipLine
                        ChannelPipeline pipeline = ch.pipeline();//即对客户端处理逻辑的封装
                    }
                });

    }

    public static void main(String[] args) {


        new RpcRegistry(8080).start();
    }
}
