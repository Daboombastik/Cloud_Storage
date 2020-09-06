import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.Exchanger;

public class MainHandler extends ChannelInboundHandlerAdapter {

    private Exchanger<AuthenticationMessage> authExchanger;
    private Exchanger<AbstractMessage> absExchanger;


    public MainHandler (Exchanger<AuthenticationMessage> authExchanger, Exchanger<AbstractMessage> absExchanger) {
        this.authExchanger = authExchanger;
        this.absExchanger = absExchanger;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof AuthenticationMessage) {
            AuthenticationMessage authenticationMessage = (AuthenticationMessage) msg;
            authExchanger.exchange(authenticationMessage);
        }
        if(msg instanceof FileParametersListMessage || msg instanceof FileMessage) {
            absExchanger.exchange((AbstractMessage) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("Having issues with connecting to Server");
    }
}
