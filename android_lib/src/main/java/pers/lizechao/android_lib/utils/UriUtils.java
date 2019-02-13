package pers.lizechao.android_lib.utils;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Lzc on 2018/6/29 0029.
 */
public class UriUtils {

    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";

    /**
     * File scheme for URIs
     */
    private static final String LOCAL_FILE_SCHEME = "file";

    /**
     * Content URI scheme for URIs
     */
    private static final String LOCAL_CONTENT_SCHEME = "content";

    /**
     * URI prefix (including scheme) for contact photos
     */
    private static final String LOCAL_CONTACT_IMAGE_PREFIX =
      Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "display_photo").getPath();

    /**
     * Asset scheme for URIs
     */
    private static final String LOCAL_ASSET_SCHEME = "asset";

    /**
     * Resource scheme for URIs
     */
    private static final String LOCAL_RESOURCE_SCHEME = "res";

    /**
     * Resource scheme for fully qualified resources which might have a package name that is different
     * than the application one. This has the constant value of "android.resource".
     */
    private static final String QUALIFIED_RESOURCE_SCHEME = ContentResolver.SCHEME_ANDROID_RESOURCE;

    /**
     * Data scheme for URIs
     */
    private static final String DATA_SCHEME = "data";


    public static Uri resToUri(int res, Resources r) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
          + r.getResourcePackageName(res) + "/"
          + r.getResourceTypeName(res) + "/"
          + r.getResourceEntryName(res));
    }


    /**
     * Check if uri represents network resource
     *
     * @param uri uri to check
     * @return true if uri's scheme is equal to "http" or "https"
     */
    public static boolean isNetworkUri(Uri uri) {
        final String scheme = getSchemeOrNull(uri);
        return HTTPS_SCHEME.equals(scheme) || HTTP_SCHEME.equals(scheme);
    }

    /**
     * Check if uri represents local file
     *
     * @param uri uri to check
     * @return true if uri's scheme is equal to "file"
     */
    public static boolean isFileUri(Uri uri) {
        final String scheme = getSchemeOrNull(uri);
        return LOCAL_FILE_SCHEME.equals(scheme);
    }

    /**
     * Check if uri represents local content
     *
     * @param uri uri to check
     * @return true if uri's scheme is equal to "content"
     */
    public static boolean isLocalContentUri(Uri uri) {
        final String scheme = getSchemeOrNull(uri);
        return LOCAL_CONTENT_SCHEME.equals(scheme);
    }


    /**
     * Check if uri represents local asset
     *
     * @param uri uri to check
     * @return true if uri's scheme is equal to "asset"
     */
    public static boolean isLocalAssetUri(Uri uri) {
        final String scheme = getSchemeOrNull(uri);
        return LOCAL_ASSET_SCHEME.equals(scheme);
    }

    /**
     * Check if uri represents local resource
     *
     * @param uri uri to check
     * @return true if uri's scheme is equal to {@link #LOCAL_RESOURCE_SCHEME}
     */
    public static boolean isLocalResourceUri(Uri uri) {
        final String scheme = getSchemeOrNull(uri);
        return LOCAL_RESOURCE_SCHEME.equals(scheme);
    }

    /**
     * Check if uri represents fully qualified resource URI.
     *
     * @param uri uri to check
     * @return true if uri's scheme is equal to {@link #QUALIFIED_RESOURCE_SCHEME}
     */
    public static boolean isQualifiedResourceUri(Uri uri) {
        final String scheme = getSchemeOrNull(uri);
        return QUALIFIED_RESOURCE_SCHEME.equals(scheme);
    }


    /**
     * @param uri uri to extract scheme from, possibly null
     * @return null if uri is null, result of uri.getScheme() otherwise
     */

    public static String getSchemeOrNull(Uri uri) {
        return uri == null ? null : uri.getScheme();
    }

    /**
     * A wrapper around {@link Uri#parse} that returns null if the input is null.
     *
     * @param uriAsString the uri as a string
     * @return the parsed Uri or null if the input was null
     */
    public static Uri parseUriOrNull(String uriAsString) {
        return uriAsString != null ? Uri.parse(uriAsString) : null;
    }

    /**
     * Get the path of a file from the Uri.
     *
     * @param contentResolver the content resolver which will query for the source file
     * @param srcUri          The source uri
     * @return The Path for the file or null if doesn't exists
     */

    public static String getRealPathFromUri(ContentResolver contentResolver, final Uri srcUri) {
        String result = null;
        if (isLocalContentUri(srcUri)) {
            Cursor cursor = null;
            try {
                cursor = contentResolver.query(srcUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (idx != -1) {
                        result = cursor.getString(idx);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (isFileUri(srcUri)) {
            result = srcUri.getPath();
        }
        return result;
    }

    public static Uri getUriForFile(File file) {
        return Uri.fromFile(file);
    }
}
