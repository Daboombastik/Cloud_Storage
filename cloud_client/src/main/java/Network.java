import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.Exchanger;

public class Network {

    private static final int PORT = 8189;
    private static final String ADDR = "localhost";
    private static final int MAX_OBJ_SIZE = 100 * 1024 * 1024;
    private static Channel channel;

    private static Exchanger<AbstractMessage> absMesExchanger;
    private static Exchanger<AuthenticationMessage> authMesExchanger;

    public Network () {
        absMesExchanger = new Exchanger<>();
        authMesExchanger = new Exchanger<>();

        Thread thread = new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new FileChainInit());
                ChannelFuture channelFuture = bootstrap.connect(ADDR, PORT).sync();
                System.out.println("Network started");
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    private static class FileChainInit extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            channel = socketChannel;
            socketChannel.pipeline().addLast("ObjectDecoder", new ObjectDecoder(MAX_OBJ_SIZE, ClassResolvers.cacheDisabled(null)));
            socketChannel.pipeline().addLast("ObjectEncoder", new ObjectEncoder());
            socketChannel.pipeline().addLast("MainHandler", new MainHandler(authMesExchanger, absMesExchanger));
        }
    }

    public static boolean sendMsg(AbstractMessage msg) {
        channel.writeAndFlush(msg);
        return true;
    }

    public static Exchanger<AbstractMessage> getAbsMesExchanger() {
        return absMesExchanger;
    }
    public static Exchanger<AuthenticationMessage> getAuthMesExchanger() {
        return authMesExchanger;
    }

}
