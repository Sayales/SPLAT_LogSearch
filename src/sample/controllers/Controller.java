package sample.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import sample.filework.PathGetter;
import sample.filework.SearchElement;
import sample.filework.StringPathGetter;
import sample.util.FileHelper;
import sample.util.SearchHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    @FXML
    private TabPane tabPane;

    @FXML
    private TreeView<String> resultTree;

   /* @FXML
    private TextArea resultText;*/

    @FXML
    private TextField extensionField;

    @FXML
    private TextField folderField;

    @FXML
    private TextField searchField;

    private HashMap<Path,SearchElement> searchResults = new HashMap<>();

    private HashMap<String, Tab> openedTab = new HashMap<>();

    private Path openFile;

    @FXML
    public void onSearchButtonHandle(ActionEvent actionEvent) throws IOException {
        PathGetter pathGetter = new StringPathGetter(folderField.getText());
        searchResults = SearchHelper.searchAllPosInFolder(pathGetter.getPath(),searchField.getText(),extensionField.getText());
        Set<Path> files = searchResults.keySet();
        getTreeNodes(pathGetter.getPath(), files);
        resultTree.setRoot(getTreeNodes(pathGetter.getPath(),files));
    }


    private TreeItem<String> getTreeNodes(Path directory, Set<Path> searchResults)  {
        TreeItem<String> root = new TreeItem<>(directory.toString());
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path file : directoryStream) {
                if (Files.isDirectory(file))
                    root.getChildren().add(getTreeNodes(file, searchResults));
                else {
                    if (searchResults.contains(file)) //добавляем в дерево только требуемые файлы
                        root.getChildren().add(new TreeItem<>(file.toString()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    public void onMouseClickedEvent(MouseEvent event) throws IOException {
        TreeItem<String> clickedItem = resultTree.getSelectionModel().getSelectedItem();
        if (clickedItem != null) {
            openFile = Paths.get(clickedItem.getValue());
            if (!Files.isDirectory(openFile)) {
               tabOpening();
            }
        }
    }

    private void tabOpening() throws IOException {
        if (!openedTab.containsKey(openFile.toString()))
        {
            Tab tab = new Tab();
            tab.setText(openFile.toString());
            tab.setOnClosed(event -> {
                System.out.print(tab.getText() + " closed");
                openedTab.remove(tab.getText());
            });
            TextArea resultText = new TextArea();
            resultText.setText(FileHelper.readFile(openFile));
            int caretPos = searchResults.get(openFile).getNextFindPosition();
            resultText.positionCaret(caretPos);
            resultText.selectRange(caretPos, caretPos + searchField.getText().length());
            tab.setContent(resultText);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            openedTab.put(openFile.toString(),tab);
        }
        else
        {
            Tab tab = openedTab.get(openFile.toString());
            tab.setText(openFile.toString());
            TextArea resultText = new TextArea();
            resultText.setText(FileHelper.readFile(openFile));
            int caretPos = searchResults.get(openFile).getNextFindPosition();
            resultText.positionCaret(caretPos);
            resultText.selectRange(caretPos, caretPos + searchField.getText().length());
            tab.setContent(resultText);
            tabPane.getSelectionModel().select(tab);
        }
    }
}
