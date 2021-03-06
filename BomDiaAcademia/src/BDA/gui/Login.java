package BDA.gui;

import java.io.File;
import java.util.List;

import BDA.files.ReadAndWriteXMLFile;
import BDA.other.XMLUserConfiguration;
import BDA.threads.ThreadPool;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The Class Login contains the main method that starts the application.
 * @author Rostislav Andreev
 * @version 2.0
 */
public class Login extends Application {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		ThreadPool.getInstance().startThreads();
		new File("Posts").mkdir();

		List<XMLUserConfiguration> userConfiguration = ReadAndWriteXMLFile.ReadConfigXMLFile();
		FXMLLoader loader = new FXMLLoader();
		loader.setController(new LoginController(userConfiguration.isEmpty() ? null : userConfiguration.get(0)));
		loader.setLocation(getClass().getResource("/res/LogIn.fxml"));
		Parent root = loader.load();
		Image icon = new Image(getClass().getResource("/res/logo0.png").toString());

		primaryStage.getIcons().add(icon);
		primaryStage.setTitle("Bom Dia Academia");
		primaryStage.setMinHeight(490);
		primaryStage.setMinWidth(370);
		primaryStage.setOnCloseRequest(e -> ThreadPool.getInstance().stopThreads());

		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		root.requestFocus();
	}
}