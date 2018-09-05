package sample.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Pavel on 05.09.2018.
 */
public class FileHelper {

    public static String readFile(Path file) throws IOException {
        byte[] data = Files.readAllBytes(file);
        return new String(data, StandardCharsets.UTF_8);
    }

}
