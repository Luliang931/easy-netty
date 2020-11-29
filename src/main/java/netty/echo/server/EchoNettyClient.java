package netty.echo.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EchoNettyClient {
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final Integer PORT = Integer.parseInt(System.getProperty("port","9090"));
    static final Integer size = Integer.parseInt(System.getProperty("size","256"));

    public static void main(String[] args) throws InterruptedException, IOException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new EchoNettyClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
            System.out.println("please type param:");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String clientMsg = bufferedReader.readLine();
            System.out.println("client input param:" + clientMsg);
            // start the client
            future.channel().writeAndFlush(Unpooled.copiedBuffer(clientMsg, CharsetUtil.UTF_8));
            // wait until the connection is closed
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }

    }

}
