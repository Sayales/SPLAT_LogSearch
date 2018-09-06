package sample.util;

import javafx.util.Pair;
import sample.filework.SearchElement;
import sample.filework.SearchElementImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

/**
 * Created by Pavel on 04.09.2018.
 */
public class SearchHelper {
    //TODO: good algorithm instead of mock
  /*  public static boolean contains(Path file, String txt) {
        if (Files.isDirectory(file))
            return false;
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = new String(data, StandardCharsets.UTF_8);
        return content.contains(txt);
    }*/
    public static Pair<Path,SearchElement> searchAllPositions(Path file, String txt) {
        Pair<Path, SearchElement> pairResult = new Pair<>(file, new SearchElementImpl(file));
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = new String(data, StandardCharsets.UTF_8).replace("\r\n","\n").replace('\r','\n');
        int index = content.indexOf(txt);
        if (index == -1) {
            return null;
        }
        pairResult.getValue().addPosition(index);
        while (index >= 0) {
            index = content.indexOf(txt, index + txt.length());
            pairResult.getValue().addPosition(index);
        }
        return pairResult;
    }


  /*  public static Stream<Path> searchInFolder(Path parentFolder, String content, String extension) throws IOException {
        return Files.walk(parentFolder)
                .filter(file ->  SearchHelper.contains(file, content) && (file.toString().endsWith(extension)));
    }*/
    public static HashMap<Path, SearchElement> searchAllPosInFolder(Path parentFolder, String content, String extension) throws IOException {
        HashMap<Path, SearchElement> result = new HashMap<>();
        Files.walk(parentFolder)
                .forEach(file -> {
                    if (!Files.isDirectory(file) && file.getFileName().toString().endsWith(extension)) {
                        Pair<Path, SearchElement> e = searchAllPositions(file, content);
                        if (e != null)
                            result.put(e.getKey(), e.getValue());
                    }
                });
        return result;
    }

}
