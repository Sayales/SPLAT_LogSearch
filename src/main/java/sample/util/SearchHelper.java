package sample.util;

import javafx.util.Pair;
import sample.exceptions.NothingFindException;
import sample.filework.SearchElement;
import sample.filework.SearchElementImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Pavel on 04.09.2018.
 */
public class SearchHelper {

    private static final long CHUNK_SIZE = 8192;

    public static HashMap<Path, SearchElement> searchAllPosInFolder(Path parentFolder, String content, String extension) throws IOException, NothingFindException {
        HashMap<Path, SearchElement> result = new HashMap<>();
        Files.walk(parentFolder)
                .forEach(file -> {
                    if (!Files.isDirectory(file) && file.getFileName().toString().endsWith(extension)) {
                        Pair<Path, SearchElement> e = null;
                        try {
                            e = searchAllPositions(file, content);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        if (e != null)
                            result.put(e.getKey(), e.getValue());
                    }
                });
        if (result.size() == 0) {
            throw new NothingFindException();
        }
        return result;
    }

    private static Pair<Path,SearchElement> searchAllPositions(Path file, String txt) throws IOException {
        Pair<Path, SearchElement> pairResult = new Pair<>(file, new SearchElementImpl(file));
        RandomAccessFile RAFile = new RandomAccessFile(file.toFile(), "r");
        byte[] data = new byte[(int) CHUNK_SIZE];
        long currentFilePos = 0;
        while (currentFilePos <= RAFile.length()) {
            RAFile.seek(currentFilePos);
            RAFile.read(data);
            String content = new String(data, StandardCharsets.UTF_8);
            int index = content.indexOf(txt);
            while (index >= 0) {
                pairResult.getValue().addPosition(index + currentFilePos);
                index = content.indexOf(txt, index + txt.length());
            }
            currentFilePos = (currentFilePos + CHUNK_SIZE) - txt.getBytes(StandardCharsets.UTF_8).length;
        }
        if (pairResult.getValue().getFindPositions().size() == 0){
            return null;
        }
        RAFile.close();
        return pairResult;
    }
/*
    private static Pair<Path,SearchElement> searchAllPositionsHungry(Path file, String txt) throws IOException {
        Pair<Path, SearchElement> pairResult = new Pair<>(file, new SearchElementImpl(file));
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = new String(data, StandardCharsets.UTF_8);//.replace("\r\n","\n").replace('\r','\n');
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
    }*/



}
