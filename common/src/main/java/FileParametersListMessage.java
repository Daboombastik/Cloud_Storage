import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 *  Класс для передачи сообщения со  списком {@link FileParameters} между сервером и клиеном
 */
@Getter
@Setter
@AllArgsConstructor
public class FileParametersListMessage extends AbstractMessage{

	private static final long serialVersionUID = 122661028145564867L;
	
	private List<FileParameters> fileParameterList;

	public FileParametersListMessage(String fileStorage) {
		List<FileParameters> fileParameterList = null;
		try {
			fileParameterList = Files.list(Paths.get(fileStorage)).map(e -> new FileParameters(e)).collect(Collectors.toCollection(ArrayList::new));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.fileParameterList = fileParameterList;
	}

	public FileParametersListMessage() {

	}


	public List<FileParameters> getFileParameterList() {
		return fileParameterList;
	}

	public void setFileParameterList(List<FileParameters> fileParameterList) {
		this.fileParameterList = fileParameterList;
	}
}
