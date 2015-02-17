package neal.http.impl.request;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import neal.http.base.NetworkResponse;
import neal.http.base.Request;
import neal.http.base.Response;
import neal.http.impl.HttpErrorCollection;
import neal.http.utils.HttpHeaderParser;

/**
 * Created by neal on 2014/11/3.
 */
public class GsonRequest<T> extends Request<T> {

    private final Map<String,String> mParams;
    private final Class<T> mClazz;
    private final Response.Listener<T> mListener;
    private final Gson mGson;

    public GsonRequest(String url,Class<T> clazz,Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        this(Method.GET, url, null,clazz, listener, errorListener);
    }

    public GsonRequest(int method, String url, Map<String,String> params,Class<T> clazz,Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method,url,params,errorListener);
        mParams=params;
        mClazz=clazz;
        mListener=listener;
        mGson=new Gson();
    }

    @Override
    protected Map<String, String> getParams() throws HttpErrorCollection.AuthFailureError {
        return mParams;
    }

    @Override
    public Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(jsonString,mClazz),HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new HttpErrorCollection.ParseError());
        }catch (JsonSyntaxException e) {
            e.printStackTrace();
            return Response.error(new HttpErrorCollection.ParseError());
        }

    }

    @Override
    public void deliverResponse(T response) {
        if(mListener!=null) {
            mListener.onResponse(response);
        }
    }
}
