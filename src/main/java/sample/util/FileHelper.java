package sample.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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

    public static String readFileInPlace(Path file, long offset) throws IOException {
        RandomAccessFile RAFile = new RandomAccessFile(file.toFile(),"r");
        long eOffsetMinor =  offset - 1024;
        if (eOffsetMinor < 0)
            eOffsetMinor = 0;
        RAFile.seek(eOffsetMinor);
        byte[] data = new byte[2048];
        RAFile.read(data);
        RAFile.close();
        return new String(data, StandardCharsets.UTF_8);
    }

}
