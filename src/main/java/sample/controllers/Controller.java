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
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
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

    @FXML
    private TextField extensionField;

    @FXML
    private TextField folderField;

    @FXML
    private TextField searchField;

    private HashMap<Path,SearchElement> searchResults = new HashMap<>();

    private HashMap<Path, Tab> openedTab = new HashMap<>();

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
        if (!openedTab.containsKey(openFile)) //если такой файл не открыт: создаём новую вкладку
        {
            Tab tab = new Tab();
            tab.setText(openFile.toString());
            tab.setOnClosed(event -> {
                openedTab.remove(Paths.get(tab.getText())); //удалить вкладку из мапы открытых вкладок при закрытии
            });
            CodeArea resultText = new CodeArea();
            resultText.appendText(FileHelper.readFile(openFile));
            int caretPos = searchResults.get(openFile).getNextFindPosition();
            setCarete(resultText, caretPos);
            resultText.selectRange(caretPos, caretPos + searchField.getText().length());
            VirtualizedScrollPane<CodeArea> area = new VirtualizedScrollPane<>(resultText);
            tab.setContent(area);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            openedTab.put(openFile,tab);
        }
    }

    public void onNextButton(ActionEvent actionEvent) {
        Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
        VirtualizedScrollPane tabContent = (VirtualizedScrollPane) activeTab.getContent();
        CodeArea tabText = (CodeArea) tabContent.getContent();
        int caretPos = searchResults.get(Paths.get(activeTab.getText())).getNextFindPosition();
        setCarete(tabText, caretPos);
        tabText.selectRange(caretPos, caretPos + searchField.getText().length());
        activeTab.setContent(tabContent);
    }

    public void onSelectAllButton(ActionEvent actionEvent) {
    }

    public void onPrevButton(ActionEvent actionEvent) {
        Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
        VirtualizedScrollPane tabContent = (VirtualizedScrollPane) activeTab.getContent();
        CodeArea tabText = (CodeArea) tabContent.getContent();
        int caretPos = searchResults.get(Paths.get(activeTab.getText())).getPrevFindPosition();
        setCarete(tabText, caretPos);
        tabText.selectRange(caretPos, caretPos + searchField.getText().length());
        activeTab.setContent(tabContent);
    }

    private void setCarete(CodeArea area, int pos) {
        area.moveTo(pos);
        area.requestFollowCaret();
    }
}
