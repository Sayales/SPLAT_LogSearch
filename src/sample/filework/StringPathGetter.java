package sample.filework;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Pavel on 04.09.2018.
 */
public class StringPathGetter implements PathGetter {
    private Path path;

    public StringPathGetter(String uri) {
        path = Paths.get(uri);
    }

    @Override
    public Path getPath() {
        return path;
    }
}
