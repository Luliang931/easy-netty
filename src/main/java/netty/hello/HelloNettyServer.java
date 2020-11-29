package netty.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * HelloNettyServer
 *
 * @author luliangliang
 */
public class HelloNettyServer {

    private Integer port;

    public HelloNettyServer(Integer port){
        this.port = port;
    }

    private void run() throws Exception {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(boosGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel){
                        socketChannel.pipeline().addLast(new HelloNettyServerHandler());
                    }
                }).option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            workerGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception {
        System.out.println("开始建立Netty服务器...");
        int port = 8080;

        if (args.length > 0){
            port = Integer.parseInt(args[0]);
        }

        new HelloNettyServer(port).run();
    }
}
