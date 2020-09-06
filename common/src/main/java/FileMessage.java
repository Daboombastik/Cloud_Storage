

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Тип сообщения для передачи файла по сети
 */

public class FileMessage extends AbstractMessage {	
 
	private static final long serialVersionUID = -3810868633835335417L;
	
	private String filename;
    private byte[] data;

    public FileMessage(Path path) throws IOException {
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }

    public String getFilename() {
        return filename;
    }
    public byte[] getData() {
        return data;
    }
}
