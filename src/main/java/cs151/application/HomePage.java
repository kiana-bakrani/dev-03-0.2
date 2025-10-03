import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomePage extends Application {
	private Stage home;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		openHomePage();
	}
	
	public void openHomePage() {
		home = new Stage();
		BorderPane border = new BorderPane();
		Scene sc = new Scene(border);
		VBox stuff = new VBox();
		Button b = new Button("hello");
		stuff.getChildren().add(b);
		border.setLeft(stuff);
		Image logo = new Image("images/logo.png");
		ImageView logoView = new ImageView(logo);
		logoView.setFitHeight(500);
		logoView.setFitWidth(660);
		border.setRight(logoView);
		home.setScene(sc);
		home.setMinHeight(500);
		home.setMinWidth(800);
		home.show();
	}
}