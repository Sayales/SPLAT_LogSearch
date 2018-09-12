package sample.filework;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Pavel on 05.09.2018.
 */
public interface SearchElement {
    void addPosition(long position);
    List<Long> getFindPositions();
    long getNextFindPosition();
    long getPrevFindPosition();
    Path getPath();
}
