package com.wzq.rpc.register;

import com.wzq.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wzq.Jolin
 * @company none
 * @create 2019-06-19 13:50
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {


    //保存所有相关的服务类
    private List< String > classNames = new ArrayList< String >();

    //5.通过远程调用Provider得到返回结果，并回复给客户端
    //用保存所有可用的服务
    public static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<String,Object>();





    public RegistryHandler() {
        //1.根据一个包名将所有符合条件的class全部扫描出来，放到一个容器中
        //如果是分布式，就是读取配置文件
        //完成递归扫描
        scannerClass("com.wzq.rpc.provider");
        //2.给每一个对应的class起一个唯一名字，作为服务名称，保存到一个容器中
        doRegister();
    }

    private void doRegister() {
        if(classNames.size() == 0){ return; }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                //接口名称作为服务名
                Class<?> i = clazz.getInterfaces()[0];
                //本来这里存的应该是网络路径，从配置文件读取
                //在调用的时候再去解析，这里直接反射调用
                registryMap.put(i.getName(), clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 递归扫描本地class文件
     *
     * @param packageName
     */
    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        System.out.println("当前路径:" + url);
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {

                scannerClass(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }

    }

    /**
     * 有客户端连接上以后会回调
     *  3.当有客户端连接过来之后，就会获取协议内容InvokerProtocol的对象
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        InvokerProtocol request = (InvokerProtocol)msg;

        //4.要去注册好的容器中去找到符合条件的服务
        //当客户端建立连接时，需要从自定义协议中获取信息，拿到具体的服务和实参
        //使用反射调用
        if(registryMap.containsKey(request.getClassName())){
            Object service = registryMap.get(request.getClassName());
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParames());
            result = method.invoke(service, request.getValues());
        }

        //5.通过远程调用Provider得到返回结果，并会回复给客户端
        ctx.write(result);
        ctx.flush();
        ctx.close();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}
