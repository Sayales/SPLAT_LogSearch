package sample.filework;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Pavel on 05.09.2018.
 */
public class SearchElementImpl implements SearchElement {

    private int currentPos;

    private List<Integer> findPos = new ArrayList<>();

    public SearchElementImpl() {
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
        currentPos++;
        if (currentPos < findPos.size()) {
            return findPos.get(currentPos);
        }
        else {
            currentPos = -1;
            return findPos.get(0);
        }
    }

}
