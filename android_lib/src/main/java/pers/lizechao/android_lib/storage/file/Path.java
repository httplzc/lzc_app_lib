package pers.lizechao.android_lib.storage.file;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Lzc on 2018/6/7 0007.
 */
public class Path {
    private final String basePath;
    private final static String separator = "/";

    private Path(String basePath) {
        this.basePath = basePath;
    }

    public boolean isDirectory() {
        return !getFileName().contains(".");
    }

    public String getFileName() {
        return basePath.substring(basePath.lastIndexOf(separator) + 1);
    }

    public String getSuffixName() {
        String fileName = getFileName();
        int indexPot = fileName.lastIndexOf(".");
        if (indexPot != -1 && indexPot != fileName.length() - 1) {
            return fileName.substring(indexPot + 1);
        }
        return null;
    }

    public String[] getParts() {
        String spits[] = basePath.split(separator);
        if (spits.length != 0) {
            spits = Arrays.copyOfRange(spits, 1, spits.length);
        }
        return spits;
    }

    public Path getRoot() {
        return Path.from(basePath.substring(0, basePath.indexOf(separator)));
    }

    public Path getParent() {
        return Path.from(basePath.substring(0, basePath.lastIndexOf(separator)));
    }

    public Path add(Path path) {
        return Path.parse(basePath + (path != null ? path.basePath : ""));
    }

    public File toFile() {
        return new File(basePath);
    }


    public static Path parse(String path) {
        if (path == null || path.trim().equals(""))
            return null;
        path = path.replace(" ", "");
        path = path.replaceAll("\\\\", separator);
        path = path.replaceAll("//+", separator);
        if (!path.startsWith(separator))
            path = separator + path;
        return new Path(path);
    }

    public static Path from(String basePath, String... path) {
        Path pathsTmp[] = new Path[path.length];
        for (int i = 0; i < pathsTmp.length; i++) {
            pathsTmp[i] = Path.parse(path[i]);
        }
        return from(Path.parse(basePath), pathsTmp);
    }

    public static Path from(Path basePath, Path... paths) {
        if (basePath == null)
            return null;
        Path backPath = basePath;
        for (Path path : paths) {
            if (path == null)
                continue;
            backPath = backPath.add(path);
        }
        return backPath;
    }

    @Override
    public String toString() {
        return basePath;
    }

    @Override
    public boolean equals(Object obj) {
        return basePath.equals(obj);
    }
}
