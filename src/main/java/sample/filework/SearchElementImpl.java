package sample.filework;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Pavel on 05.09.2018.
 */
public class SearchElementImpl implements SearchElement {

    private Path path;

    private int currentPos;

    private List<Integer> findPos = new ArrayList<>();

    public SearchElementImpl(Path path) {
        this.path = path;
        currentPos = -1;
    }



    @Override
    public void addPosition(int position) {
        if (position >= 0)
            findPos.add(position);
    }

    @Override
    public List<Integer> getFindPositions() {
        return findPos;
    }

    @Override
    public int getNextFindPosition() {
        if (currentPos < findPos.size() - 1) {
            currentPos++;
            return findPos.get(currentPos);
        }
        else {
            currentPos = 0;
            return findPos.get(currentPos);
        }
    }

    @Override
    public int getPrevFindPosition() {
        if (currentPos > 0) {
            currentPos = currentPos - 1;
            return findPos.get(currentPos);
        }
        else {
            currentPos = findPos.size() - 1;
            return findPos.get(currentPos);
        }
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchElementImpl that = (SearchElementImpl) o;

        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
