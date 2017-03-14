package com.yioks.lzclib.Helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;

/**
 * Created by ${User} on 2017/3/6 0006.
 */

public class RequestParams implements Serializable {
    private static final MediaType MEDIA_TYPE_PNG=MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG=MediaType.parse("image/jpeg");
    private static final MediaType MEDIA_TYPE_XML=MediaType.parse("image/xml");
    private static final MediaType MEDIA_TYPE_Plain=MediaType.parse("image/plain");
    private static final MediaType MEDIA_TYPE_File=MediaType.parse("application/octet-stream");
    protected final ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<String, String>();
    protected final ConcurrentHashMap<String, FileWrapper> fileParams = new ConcurrentHashMap<String, FileWrapper>();
    protected final ConcurrentHashMap<String, List<FileWrapper>> fileArrayParams = new ConcurrentHashMap<String, List<FileWrapper>>();
    public String getDataFromUrlParams(String key)
    {
        return urlParams.get(key);
    }

    public boolean fileIsEmpty()
    {
        return fileParams.isEmpty()&&fileArrayParams.isEmpty();
    }

    public File getDataFromFileListParams(String key,int index)
    {
        return fileArrayParams.get(key).get(index).file;
    }
    
    public  Set<Map.Entry<String,List<FileWrapper>>> getFileListParams()
    {
        return fileArrayParams.entrySet();
    }

    public  Set<Map.Entry<String,FileWrapper>> getFileParams()
    {
        return fileParams.entrySet();
    }

    public Set<Map.Entry<String,String>> getUrlParamsEntry()
    {
        return urlParams.entrySet();
    }


    /**
     * Adds a key/value string pair to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value string for the new param.
     */
    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    /**
     * Adds files array to the request.
     *
     * @param key   the key name for the new param.
     * @param files the files array to add.
     * @throws FileNotFoundException if one of passed files is not found at time of assembling the requestparams into request
     */
    public void put(String key, File files[]) throws FileNotFoundException {
        put(key, files,MEDIA_TYPE_File,"files");
    }

    /**
     * Adds files array to the request with both custom provided file content-type and files name
     *
     * @param key            the key name for the new param.
     * @param files          the files array to add.
     * @param contentType    the content type of the file, eg. application/json
     * @param customFileName file name to use instead of real file name
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public void put(String key, File files[], MediaType contentType, String customFileName) throws FileNotFoundException {

        if (key != null) {
            List<FileWrapper> fileWrappers = new ArrayList<FileWrapper>();
            for (File file : files) {
                if (file == null || !file.exists()) {
                    throw new FileNotFoundException();
                }
                fileWrappers.add(new FileWrapper(file, contentType, customFileName));
            }
            fileArrayParams.put(key, fileWrappers);
        }
    }

    /**
     * Adds a file to the request.
     *
     * @param key  the key name for the new param.
     * @param file the file to add.
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public void put(String key, File file) throws FileNotFoundException {
        put(key, file, MEDIA_TYPE_File, file.getName());
    }

    /**
     * Adds a file to the request with custom provided file name
     *
     * @param key            the key name for the new param.
     * @param file           the file to add.
     * @param customFileName file name to use instead of real file name
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public void put(String key, String customFileName, File file) throws FileNotFoundException {
        put(key, file, MEDIA_TYPE_File, customFileName);
    }

    /**
     * Adds a file to the request with custom provided file content-type
     *
     * @param key         the key name for the new param.
     * @param file        the file to add.
     * @param contentType the content type of the file, eg. application/json
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public void put(String key, File file, MediaType contentType) throws FileNotFoundException {
        put(key, file, contentType, file.getName());
    }

    /**
     * Adds a file to the request with both custom provided file content-type and file name
     *
     * @param key            the key name for the new param.
     * @param file           the file to add.
     * @param contentType    the content type of the file, eg. application/json
     * @param customFileName file name to use instead of real file name
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public void put(String key, File file, MediaType contentType, String customFileName) throws FileNotFoundException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException();
        }
        if (key != null) {
            fileParams.put(key, new FileWrapper(file, contentType, customFileName));
        }
    }
    

    public static class FileWrapper implements Serializable {
        public final File file;
        public final MediaType contentType;
        public final String customFileName;

        public FileWrapper(File file, MediaType mediaType, String customFileName) {
            this.file = file;
            this.contentType = mediaType;
            this.customFileName = customFileName;
        }
    }


    @Override
    public String toString() {
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("String"+"\n");
        for (Map.Entry<String, String> entry : urlParams.entrySet()) {
            stringBuffer.append(entry.getKey()+":"+entry.getValue()+"\n");
        }
        if(!fileParams.isEmpty())
        {
            stringBuffer.append("\n"+"File"+"\n");
            for (Map.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
                stringBuffer.append(entry.getKey()+":"+entry.getValue().file.getName()+"\n");
            }
        }

        if(!fileArrayParams.isEmpty())
        {
            stringBuffer.append("\n"+"FileList"+"\n");
            for (Map.Entry<String, List<FileWrapper>> entry : fileArrayParams.entrySet()) {
                stringBuffer.append(entry.getKey()+"\n");
                for (FileWrapper fileWrapper : entry.getValue()) {
                    stringBuffer.append(fileWrapper.file.getName()+"\n");
                }
            }
        }

        return stringBuffer.toString();
    }
}
