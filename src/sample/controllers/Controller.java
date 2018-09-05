package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import sample.filework.PathGetter;
import sample.filework.StringPathGetter;
import sample.util.SearchHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    @FXML
    private TreeView<String> resultTree;

    @FXML
    private TextArea resultText;

    @FXML
    private TextField extensionField;

    @FXML
    private TextField folderField;

    @FXML
    private TextField searchField;

    private HashSet<Path> searchResults = new HashSet<>();

    @FXML
    public void onSearchButtonHandle(ActionEvent actionEvent) throws IOException {
        PathGetter pathGetter = new StringPathGetter(folderField.getText());
        HashSet<Path> files = SearchHelper.searchInFolder(pathGetter.getPath(),searchField.getText(),extensionField.getText())
                .collect(Collectors.toCollection(HashSet<Path>::new));
        getTreeNodes(pathGetter.getPath(), files);
        resultTree.setRoot(getTreeNodes(pathGetter.getPath(),files));
    }

    private TreeItem<String> getTreeNodes(Path directory, HashSet<Path> searchResults)  {
        TreeItem<String> root = new TreeItem<>(directory.getFileName().toString());
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path file : directoryStream) {
                if (Files.isDirectory(file))
                    root.getChildren().add(getTreeNodes(file, searchResults));
                else {
                    if (searchResults.contains(file)) //добавляем в дерево только требуемые файлы
                        root.getChildren().add(new TreeItem<>(file.getFileName().toString()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return root;
    }

}
