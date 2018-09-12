package sample;

import com.sun.javafx.stage.StageHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;


public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Log search");
        primaryStage.setScene(new Scene(root,1024,600));

        this.primaryStage = primaryStage;
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
       launch(args);
    }
}
