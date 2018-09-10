package sample.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import sample.exceptions.NothingFindException;
import sample.filework.PathGetter;
import sample.filework.SearchElement;

import sample.filework.StringPathGetter;
import sample.util.FileHelper;
import sample.util.SearchHelper;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Controller {

    @FXML
    private TabPane tabPane;

    @FXML
    private TreeView<String> resultTree;

    @FXML
    private TextField extensionField;

    @FXML
    private TextField folderField;

    @FXML
    private TextField searchField;


    private Thread searchThread;

    private HashMap<Path,SearchElement> searchResults = new HashMap<>(); //мапа результатов поиска

    private HashMap<Tab, SearchElement> openedTab = new HashMap<>(); //мапа открытых вкладок

    private SearchElement openFile; //файл, вкладка с которым открыта в данный момент

    @FXML
    public void onSearchButtonHandle(ActionEvent actionEvent) throws IOException {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    PathGetter pathGetter = null;
                    try {
                        pathGetter = new StringPathGetter(folderField.getText());
                        searchResults = SearchHelper.searchAllPosInFolder(pathGetter.getPath(),searchField.getText(),extensionField.getText());
                        Set<Path> files = searchResults.keySet();
                        getTreeNodes(pathGetter.getPath(), files);
                        resultTree.setRoot(getTreeNodes(pathGetter.getPath(),files));
                    } catch (Exception uriE) {
                        alert(uriE);
                    }
                });
                return null;
            }
        };
        searchThread = new Thread(task);
        searchThread.start();
    }

    @FXML
    public void onMouseClickedEvent(MouseEvent event) throws IOException, InterruptedException { //на тестовых файлах - основное время затрачивается на отрисовку текста в треде gui
        if (searchThread != null) {
            searchThread.join();
        }
        TreeItem<String> clickedItem = resultTree.getSelectionModel().getSelectedItem();
        if (clickedItem != null) {
            openFile = searchResults.get(Paths.get(clickedItem.getValue()));
            if (openFile != null && !Files.isDirectory(openFile.getPath())) {
                tabOpening();
            }
        }
    }

    @FXML
    public void onNextButton(ActionEvent actionEvent) {
        Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
        VirtualizedScrollPane tabContent = (VirtualizedScrollPane) activeTab.getContent();
        CodeArea tabText = (CodeArea) tabContent.getContent();
        int caretPos = openedTab.get(activeTab).getNextFindPosition();
        setCarete(tabText, caretPos);
        tabText.selectRange(caretPos, caretPos + searchField.getText().length());
        activeTab.setContent(tabContent);
    }

    @FXML
    public void onSelectAllButton(ActionEvent actionEvent) {
        Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
        VirtualizedScrollPane tabContent = (VirtualizedScrollPane) activeTab.getContent();
        CodeArea tabText = (CodeArea) tabContent.getContent();
        tabText.selectRange(0,tabText.getLength());
    }

    @FXML
    public void onPrevButton(ActionEvent actionEvent) {
        Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
        VirtualizedScrollPane tabContent = (VirtualizedScrollPane) activeTab.getContent();
        CodeArea tabText = (CodeArea) tabContent.getContent();
        int caretPos = openedTab.get(activeTab).getPrevFindPosition();
        setCarete(tabText, caretPos);
        tabText.selectRange(caretPos, caretPos + searchField.getText().length());
        activeTab.setContent(tabContent);
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

    private void tabOpening() throws IOException {
        if (!openedTab.containsValue(searchResults.get(openFile.getPath()))) //если такой файл не открыт: создаём новую вкладку
        {
            Tab tab = new Tab();
            tab.setText(openFile.toString());
            tab.setOnClosed(event -> {
                openedTab.remove(tab); //удалить вкладку из мапы открытых вкладок при закрытии
            });
            CodeArea resultText = new CodeArea();
            resultText.appendText(FileHelper.readFile(openFile.getPath()));
            int caretPos = openFile.getNextFindPosition();
            setCarete(resultText, caretPos);
            resultText.selectRange(caretPos, caretPos + searchField.getText().length());
            VirtualizedScrollPane<CodeArea> area = new VirtualizedScrollPane<>(resultText);
            tab.setContent(area);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            openedTab.put(tab, searchResults.get(openFile.getPath()));
        }
    }

    private void setCarete(CodeArea area, int pos) {
        area.moveTo(pos);
        area.requestFollowCaret();
    }

    private void alert(Exception e) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK);
        if (e instanceof IllegalArgumentException)
            alert.setContentText("Missing scheme, for example \" file://");
        else
        if (e instanceof URISyntaxException)
            alert.setContentText("URI syntax excecption " + e.getMessage());
        else
        if (e instanceof IOException)
            alert.setContentText("No such directory or file " + e.getMessage());
        else
        if (e instanceof NothingFindException)
            alert.setContentText("Nothing was finded");
        else
            alert.setContentText("Some more troubles " + e.getMessage());
        alert.getDialogPane().setPrefSize(400, 100);
        alert.showAndWait();
    }
}
