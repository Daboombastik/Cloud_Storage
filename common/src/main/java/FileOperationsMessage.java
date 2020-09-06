
/**
 * Класс для передачи с клиента на сервер необходимой операции
 * с файлом в облачном хранилище
 */

public class FileOperationsMessage extends AbstractMessage  {

	public static final long serialVersionUID = 1344774173888738704L;
	
	public enum FileOperation {
		COPY, MOVE, DELETE		
	}
	
	private FileOperation fileOperation;	
	private String fileName;

	public FileOperation getFileOperation() {
		return fileOperation;
	}
	public String getFileName() {
		return fileName;
	}

	public FileOperationsMessage(FileOperation fileOperation, String fileName) {
		this.fileOperation = fileOperation;
		this.fileName = fileName;
	}
}
