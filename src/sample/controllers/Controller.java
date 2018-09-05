package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import sample.filework.PathGetter;
import sample.filework.StringPathGetter;
import sample.util.SearchHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    @FXML
    private Text actionText;

    @FXML
    private TextField extensionField;

    @FXML
    private TextField folderField;

    @FXML
    private TextField searchField;

    @FXML
    public void onSearchButtonHandle(ActionEvent actionEvent) throws IOException {
        PathGetter pathGetter = new StringPathGetter(folderField.getText());
        Stream<Path> files = SearchHelper.searchInFolder(pathGetter.getPath(),searchField.getText(),extensionField.getText());
        String result = files.map(file -> file.getFileName().toString())
                .collect(Collectors.joining(" "));
        actionText.setText(result);
    }
}
