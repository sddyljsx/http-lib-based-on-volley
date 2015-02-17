package neal.http.impl.request;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import neal.http.base.NetworkResponse;
import neal.http.base.Request;
import neal.http.base.Response;
import neal.http.utils.HttpHeaderParser;

/**
 * Created by neal on 2014/11/6.
 */
public class FileRequest extends Request<File> {

    private final Response.Listener<File> mListener;
    private String mFileName;

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link neal.http.base.Request.Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public FileRequest(int method, String url, String fileName,Response.Listener<File> listener,
                         Response.ErrorListener errorListener) {
        super(method, url, null,errorListener);
        if(fileName==null){
            fileName=(null==url.substring(url.lastIndexOf("/")+1) ? String.valueOf(url.hashCode()):url.substring(url.lastIndexOf("/")+1));
        }
        mFileName=fileName;
        mListener = listener;
        this.setShouldCache(false);
    }

    /**
     * Creates a new GET request.
     *
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public FileRequest(String url,String fileName, Response.Listener<File> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url,fileName, listener, errorListener);
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }
    @Override
    public Response<File> parseNetworkResponse(NetworkResponse response) {
        //TODO PAATH
        File file=new File(Environment.getExternalStorageDirectory(),mFileName);
        FileOutputStream fo;
        try {
            fo=new FileOutputStream(file);
            fo.write(response.data);
            fo.close();
            fo.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
        return Response.success(file, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public void deliverResponse(File response) {
        mListener.onResponse(response);

    }
}
