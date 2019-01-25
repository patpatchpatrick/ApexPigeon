package io.github.patpatchpatrick.alphapigeon.resources;

import java.awt.Cursor;
import java.util.ArrayList;

public interface MobileCallbacks {

    //Interface to receive callbacks from mobile devices

    public void requestedHighScoresReceived(ArrayList<String> playerCenteredHighScores);

    public void playerLocalScoresReceived(ArrayList<String> localScores);

}
