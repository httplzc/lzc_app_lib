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
public class PickAlbumData {
    private String name;
    private Uri uri;
    private String count;
    private String id;
    private boolean check;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getCount() {
        return count + "张";
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public PickAlbumData(String name, Uri uri, String id) {
        this.name = name;
        this.uri = uri;
        this.id = id;
    }

    public PickAlbumData(Album album) {
        if (album == null)
            return;
        this.name = album.getName();
        this.id = album.getId();
        this.count = album.getPhotoList() != null ? album.getPhotoList().size() + "" : "0";
        if (album.getAlbumImage() != null) {
            this.uri = Uri.fromFile(new File(album.getAlbumImage()));
        }
    }
}
