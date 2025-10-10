package cs151.application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage home;
    private Button studentProfile;
    private Button generateReports;
    private Button createProgLang;
    private Button homePage;
    public static Main INSTANCE;
    @Override
    public void init() {
        INSTANCE = this;
    }
    @Override
    public void start(Stage stage) throws Exception {
        this.home = stage;
        openHomePage();
    }

    /**
     * Creates the HomePage.
     */
    public void openHomePage() {
        BorderPane border = new BorderPane();
        Scene sc = new Scene(border);
        VBox menu = new VBox();
        HBox title = new HBox();
        Image logo   = new Image(getClass().getResource("/images/logo.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        Image house  = new Image(getClass().getResource("/images/House.png").toExternalForm());
        ImageView houseView = new ImageView(house);
        Image navBar = new Image(getClass().getResource("/images/NavigationBar.png").toExternalForm());
        ImageView navBarView = new ImageView(navBar);
        Label homePageTitle = new Label("Home Page");
        Font titleSize = new Font(25);
        StackPane stack = new StackPane();
        Rectangle background = new Rectangle(180, 315, Color.web("#d7ddfe"));
        VBox buttonList = new VBox();
        studentProfile = new Button("Student Profile");
        generateReports = new Button("Generate Reports");
        createProgLang = new Button("Define Programming\nLanguages");
        homePage = new Button("Home Page");

        logoView.setFitHeight(440);
        logoView.setFitWidth(590);

        houseView.setFitHeight(50);
        houseView.setFitWidth(50);

        homePageTitle.setMinSize(100, 50);
        homePageTitle.setFont(titleSize);

        title.getChildren().addAll(houseView, homePageTitle);
        title.setSpacing(10);

        navBarView.setFitHeight(15);
        navBarView.setFitWidth(200);

        stack.getChildren().addAll(background, buttonList);
        stack.setAlignment(Pos.CENTER);

        studentProfile.setTextAlignment(TextAlignment.CENTER);
        generateReports.setTextAlignment(TextAlignment.CENTER);
        createProgLang.setTextAlignment(TextAlignment.CENTER);

        // FIXED: Add action handler to open Define Languages page
        createProgLang.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                openDefineLanguagesPage();
            }
        });

        homePage.setTextAlignment(TextAlignment.CENTER);

        buttonList.getChildren().addAll(studentProfile, generateReports, createProgLang, homePage);
        buttonList.setSpacing(30);
        buttonList.setAlignment(Pos.CENTER);

        menu.getChildren().addAll(title, navBarView, stack);
        menu.setSpacing(20);

        border.setLeft(menu);
        border.setRight(logoView);

        home.setScene(sc);
        home.setMinHeight(440);
        home.setMinWidth(800);
        home.setResizable(false);
        home.setTitle("RateMyStudent(v0.2)");
        home.show();
    }

    /**
     * Opens the Define Programming Languages page using FXML
     */
    private void openDefineLanguagesPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs151/application/define-languages.fxml"));
            Scene scene = new Scene(loader.load(), 600, 420);
            home.setScene(scene);
            home.setTitle("Define Programming Languages");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error loading Define Languages page: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
