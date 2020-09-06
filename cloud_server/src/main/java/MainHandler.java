import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


/**
 * Класс-обработчик всех входящих десериализованных классов-сообщений от клиента
 */
public class MainHandler extends ChannelInboundHandlerAdapter {

	private String userCloudStorage;
	private DBHandler dbHandler;

	public MainHandler(DBHandler dbHandler) {
		this.dbHandler = dbHandler;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (msg == null) {
				return;
			}
			if (msg instanceof AuthenticationMessage) {
				AuthMsg(ctx, msg);
			}
			if (msg instanceof FileOperationsMessage) {
				CommandMsg(ctx, msg);
			}
			if (msg instanceof FileParametersListMessage) {
				FileParametersMsg(ctx);
			}
			if (msg instanceof FileMessage) {
				FileMessage fileMessage = (FileMessage) msg;
				Files.write(Paths.get(userCloudStorage + fileMessage.getFilename()), fileMessage.getData(),
						StandardOpenOption.CREATE);
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	private void CommandMsg(ChannelHandlerContext ctx, Object msg) throws IOException {
		FileOperationsMessage fileOperationsMessage = (FileOperationsMessage) msg;
		switch (fileOperationsMessage.getFileOperation()) {
		case COPY:
			copyToClientStorage(ctx, fileOperationsMessage);
			break;
		case MOVE:
			copyToClientStorage(ctx, fileOperationsMessage);
			deleteFileFromCloudStorage(ctx, fileOperationsMessage);
			break;
		case DELETE:
			deleteFileFromCloudStorage(ctx, fileOperationsMessage);
			break;
		default:
			break;
		}
	}

	private void AuthMsg(ChannelHandlerContext ctx, Object msg) {
		AuthenticationMessage authMessage = (AuthenticationMessage) msg;
		switch (authMessage.getAuthCommandType()) {
		case AUTHORIZATION:
			if (dbHandler.authentication (authMessage.getLogin(), authMessage.getPassword())) {
				authMessage.setStatus(true);
				userCloudStorage = "D:\\" + authMessage.getLogin();
				ctx.writeAndFlush(authMessage);
			} else {
				ctx.writeAndFlush(authMessage);
			}
			break;
		case CHANGE_PASS:
			if (dbHandler.changePass(authMessage.getLogin(), authMessage.getPassword(),
					authMessage.getNewPassword())) {
				authMessage.setStatus(true);
				ctx.writeAndFlush(authMessage);
			} else {
				ctx.writeAndFlush(authMessage);
			}
			break;
		case REGISTRATION:
			if (dbHandler.selectUserByName(authMessage.getLogin()) == null) {
				authMessage.setStatus(true);
				dbHandler.insertUser(authMessage.getLogin(), authMessage.getPassword());
				ctx.writeAndFlush(authMessage);
			} else {
				ctx.writeAndFlush(authMessage);
			}
			break;
		default:
			break;
		}
	}

//	private void deleteUsersCloudStorage(String userCloudStorage) {
//		Path userStoragePath = Paths.get(userCloudStorage);
//		try {
//			Files.walk(userStoragePath).sorted(Comparator.reverseOrder()).peek(System.out::println).map(Path::toFile)
//					.forEach(File::delete);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	private void FileParametersMsg(ChannelHandlerContext ctx) {
		File directory = new File(userCloudStorage);

		if (!directory.exists()) {
			directory.mkdir();
		}
		FileParametersListMessage fileParametersList = new FileParametersListMessage(userCloudStorage);
		ctx.writeAndFlush(fileParametersList);
	}

	private void deleteFileFromCloudStorage(ChannelHandlerContext ctx, FileOperationsMessage fileOperationsMessage)
			throws IOException {
		Path path = Paths.get(userCloudStorage + fileOperationsMessage.getFileName());
		Files.delete(path);
		FileParametersMsg(ctx);
	}

	private void copyToClientStorage(ChannelHandlerContext ctx, FileOperationsMessage fileOperationsMessage)
			throws IOException {
		Path path = Paths.get(userCloudStorage + fileOperationsMessage.getFileName());
		FileMessage fileMessage = new FileMessage(path);
		ctx.writeAndFlush(fileMessage);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
