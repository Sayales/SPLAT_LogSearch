package sample.filework;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Pavel on 05.09.2018.
 */
public interface SearchElement {
    void addPosition(int position);
    List<Integer> getFindPositions();
    int getNextFindPosition();
    int getPrevFindPosition();
    Path getPath();
}
