package sample.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Created by Pavel on 04.09.2018.
 */
public class SearchHelper {
    //TODO: good algorithm instead of mock
    public static boolean contains(Path file, String txt) {
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
    }

    public static Stream<Path> searchInFolder(Path parentFolder, String content, String extension) throws IOException {
        return Files.walk(parentFolder)
                .filter(file ->  SearchHelper.contains(file,"жопа") && (file.toString().endsWith(extension)));
    }

}
