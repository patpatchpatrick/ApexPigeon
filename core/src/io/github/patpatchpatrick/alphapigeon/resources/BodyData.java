package io.github.patpatchpatrick.alphapigeon.resources;

public class BodyData {

    // Class used to store data on bodies

    private Boolean flaggedForDelete;

    public BodyData(boolean flagForDelete) {
        flaggedForDelete = flagForDelete;
    }

    public Boolean isFlaggedForDelete() {
        if (flaggedForDelete != null) {
            return flaggedForDelete;
        } else {
            return false;
        }
    }

}
