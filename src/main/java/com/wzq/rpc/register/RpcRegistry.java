package com.wzq.rpc.register;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


/**
 * @author wzq.Jolin
 * @company none
 * @create 2019-06-18 22:47
 */
public class RpcRegistry {
    private int port;

    public RpcRegistry(int port) {
        this.port = port;
    }


    public void start() throws InterruptedException {

        //ServerSocketChannel/ServerSocket
        //机遇nio实现
        //Selector主线程   work线程

        try {


            //初始化主线程池 Selector
            EventLoopGroup bossGroup = new NioEventLoopGroup();  //工作效率是cpu的2倍，以保证cpu不会达到100%的状态
            //子线程初始化，对应客户端的处理逻辑
            EventLoopGroup workGroup = new NioEventLoopGroup();

            ServerBootstrap bootstrapServer = new ServerBootstrap();
            //采用链式编程，配置参数
            bootstrapServer.group(bossGroup, workGroup)
                    //主线程轮询
                    .channel(NioServerSocketChannel.class)
                    //子线程执行
                    .childHandler(new ChannelInitializer< SocketChannel >() {

                        protected void initChannel(SocketChannel ch) {

                            //在netty中，把所有的业务逻辑处理全部都归总到了一个队列中
                            //这个队列中包含了各种各样的处理逻辑，对这这些处理逻辑在netty中有一个封装。
                            //封装成了一个对象，无锁化串行对象 这个对象叫做PipLine
                            ChannelPipeline pipeline = ch.pipeline();//即对客户端处理逻辑的封装

                            //由于客户端发过来的请求 netty不认识，所以需要先进行编码和解码
                            //解码
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            //编码
                            pipeline.addLast(new LengthFieldPrepender(4));
                            //实参处理
                            //对象参数类型编码器
                            pipeline.addLast("encoder", new ObjectEncoder());
                            //对象参数类型解码器
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            //前面的编辑码，就是完成对数据的解析
                            //接着执行自己的逻辑
                            //1. 注册 给每一个对象起名字，对外提供服务的名字
                            //2.服务位置要做一个登记
                            pipeline.addLast(new RegistryHandler());

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)//设置最大的Selector
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置子线程保持长连接
            ;
            //正式启动服务，相当于一个死循环 轮询开始
            ChannelFuture future = bootstrapServer.bind(this.port).sync();
            System.out.println("WZQ RPC REGISTRY Start listen at  "+this.port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws InterruptedException {


        new RpcRegistry(8080).start();
    }
}
