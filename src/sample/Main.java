package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.filework.PathGetter;
import sample.filework.StringPathGetter;
import sample.util.SearchHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root,800,600));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
        /*PathGetter stringGetter = new StringPathGetter(readFilename());
        Stream<Path> walkedFilesStream = SearchHelper.searchInFolder(stringGetter.getPath(), "жопа", ".log");
        String result = walkedFilesStream.map(file -> file.getFileName().toString())
                .collect(Collectors.joining(" "));
        System.out.println(result);*/
       launch(args);
    }
    private static String readFilename() throws IOException, URISyntaxException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();

    }
}
