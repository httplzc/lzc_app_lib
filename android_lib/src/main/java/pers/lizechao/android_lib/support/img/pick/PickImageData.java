package pers.lizechao.android_lib.support.img.pick;

import android.net.Uri;

import java.io.File;

/**
 * Created with
 * ********************************************************************************
 * #         ___                     ________                ________             *
 * #       |\  \                   |\_____  \              |\   ____\             *
 * #       \ \  \                   \|___/  /|             \ \  \___|             *
 * #        \ \  \                      /  / /              \ \  \                *
 * #         \ \  \____                /  /_/__              \ \  \____           *
 * #          \ \_______\             |\________\             \ \_______\         *
 * #           \|_______|              \|_______|              \|_______|         *
 * #                                                                              *
 * ********************************************************************************
 * Date: 2018-08-29
 * Time: 16:53
 */
public class PickImageData {
    private int choiceIndex;
    private Uri uri;
    private boolean haveChoice;

    public PickImageData(Uri uri) {
        this.uri = uri;
    }

    public PickImageData(String path) {
        if (path != null) {
            this.uri = Uri.fromFile(new File(path));
        }
    }


    public int getChoiceIndex() {
        return choiceIndex;
    }

    public void setChoiceIndex(int choiceIndex) {
        this.choiceIndex = choiceIndex;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public boolean isHaveChoice() {
        return haveChoice;
    }

    public void setHaveChoice(boolean haveChoice) {
        this.haveChoice = haveChoice;
    }
}
