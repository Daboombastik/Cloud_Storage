import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Client extends Application {
    public static void main(String... args) {
        launch(args);
    }

	@Override
	public void init() {
		new Network();
	}

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent rootPane = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("SignUp.fxml")));
        primaryStage.setTitle("My Cloud Storage");
        primaryStage.setScene(new Scene(rootPane));
        primaryStage.show();
    }
}
