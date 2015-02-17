package neal.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import neal.http.base.HttpStack;
import neal.http.base.Network;
import neal.http.base.Request;
import neal.http.base.Response;
import neal.http.impl.BasicNetwork;
import neal.http.impl.cache.DiskBasedCache;
import neal.http.impl.httpstack.HttpClientStack;
import neal.http.impl.httpstack.HurlStack;
import neal.http.impl.cache.LruImageCache;
import neal.http.impl.request.FileRequest;
import neal.http.impl.request.GsonRequest;
import neal.http.impl.request.MultipartRequest;
import neal.http.impl.request.RequestFuture;
import neal.http.impl.request.StringRequest;
import neal.http.process.ImageLoader;
import neal.http.process.RequestQueue;
import neal.utils.VersionUtil;

/**
 * Created by Neal on 2014/10/28.
 */
public class Http {

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "http_cache";

    private static RequestQueue mRequestQueue=null;

    private static ImageLoader mImageLoader=null;

    private static LruImageCache mMemCache=null;

    /**
     * Creates a default instance of the worker pool and calls {@link neal.http.process.RequestQueue#start()} on it.
     *
     * @param context A {@link android.content.Context} to use for creating the cache dir.
     * @param stack An {@link neal.http.base.HttpStack} to use for the network, or null for default.
     * @return A started {@link neal.http.process.RequestQueue} instance.
     */
    private static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        // TODO 缓存目录选择
       // File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        File cacheDir = new File(context.getExternalCacheDir(), DEFAULT_CACHE_DIR);
        String userAgent = "http/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (stack == null) {
            if (VersionUtil.hasGingerbread()) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }
        Network network = new BasicNetwork(stack);

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.start();

        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link neal.http.process.RequestQueue#start()} on it.
     *
     * @param context A {@link android.content.Context} to use for creating the cache dir.
     * @return A started {@link neal.http.process.RequestQueue} instance.
     */
    private static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }

    /**
     *
     * @param context
     */
    public static void init(Context context){
        mRequestQueue=newRequestQueue(context);
        mMemCache=new LruImageCache(context);
        mImageLoader=new ImageLoader(context,mRequestQueue,mMemCache);
    }

    /**
     * @param context 传Application Context
     * @return current RequestQueue
     */
    private static RequestQueue getRequestQueue(Context context){
        if(mRequestQueue==null){
            init(context);
        }
        return mRequestQueue;
    }
    /**
     * 注意：所有的 listener 的 onResponse onErrorResponse 均在主UI线程调用，可以放心在里面进行UI操作
     */
    /**
     * @param context 传Application Context
     * @return current RequestQueue
     */
    public static ImageLoader getImageLoader(Context context){
        if(mRequestQueue==null || mMemCache==null || mImageLoader==null){
            init(context);
        }
        return mImageLoader;
    }

    /**
     *  Post 方式上传参数，并且得到String形式的响应.
     *
     * @param url
     * @param params
     * @param listener
     * @param errorListener
     */

    public static void post(String url,Map<String,String> params,Response.Listener<String> listener,
                                Response.ErrorListener errorListener){
        if(mRequestQueue==null){
            return;
        }
        mRequestQueue.add(new StringRequest(Request.Method.POST,url,params,listener,errorListener));
    }

    /**
     *   Get 方式上传参数，并且得到String形式的响应.
     *
     * @param url
     * @param params
     * @param listener
     * @param errorListener
     */
    public static void get(String url,Map<String,String> params,Response.Listener<String> listener,
                               Response.ErrorListener errorListener){
        if(mRequestQueue==null){
            return;
        }
        mRequestQueue.add(new StringRequest(Request.Method.GET,url,params,listener,errorListener));
    }

    /**
     *  Get 方式访问，不上传参数，并且得到String形式的响应.
     * @param url
     * @param listener
     * @param errorListener
     */
    public static void get(String url,Response.Listener<String> listener,
                               Response.ErrorListener errorListener){
        if(mRequestQueue==null){
            return;
        }
        mRequestQueue.add(new StringRequest(url,listener,errorListener));
    }

    /**
     *Post 方式上传参数，并且得到json形式的响应，通过gson转化为对象类型
     * @param url
     * @param params
     * @param clazz
     * @param listener
     * @param errorListener
     * @param <T>
     */
    public static <T> void post(String url,Map<String,String> params,Class<T> clazz,Response.Listener<T> listener,
                                Response.ErrorListener errorListener){
        if(mRequestQueue==null){
            return;
        }
        mRequestQueue.add(new GsonRequest<T>(Request.Method.POST,url,params,clazz,listener,errorListener));
    }

    /**
     * Get方式上传参数，并且得到json形式的响应，通过gson转化为对象类型
     * @param url
     * @param params
     * @param clazz
     * @param listener
     * @param errorListener
     * @param <T>
     */
    public static <T> void get(String url,Map<String,String> params,Class<T> clazz,Response.Listener<T> listener,
                               Response.ErrorListener errorListener){
        if(mRequestQueue==null){
            return;
        }
        mRequestQueue.add(new GsonRequest<T>(Request.Method.GET,url,params,clazz,listener,errorListener));
    }

    /**
     * Get方式访问，不上传参数，并且得到json形式的响应，通过gson转化为对象类型
     * @param url
     * @param clazz
     * @param listener
     * @param errorListener
     * @param <T>
     */

    public static <T> void get(String url,Class<T> clazz,Response.Listener<T> listener,
                               Response.ErrorListener errorListener){
        if(mRequestQueue==null){
            return;
        }
        mRequestQueue.add(new GsonRequest<T>(url,clazz,listener,errorListener));
    }

    /**
     * upload a file by post method, and the result is returned by a string
     * 通过post方式带参数上传文件，并且得到字符串形式的响应，不要上传大文件
     * @param url
     * @param params
     * @param files
     * @param listener
     * @param errorListener
     */
   public static void uploadFile(String url, Map<String, String> params, Map<String, File> files, Response.Listener<String> listener,
                               Response.ErrorListener errorListener){
       if(mRequestQueue==null){
           return;
       }
       mRequestQueue.add(new MultipartRequest(url,params,files,listener,errorListener));

   }

    /**
     *
     * upload a file by post method, and the result is returned by a string
     * 通过post方式不带参数上传文件，并且得到字符串形式的响应，不要上传大文件
     * @param url
     * @param files
     * @param listener
     * @param errorListener
     */
   public static void uploadFile(String url, Map<String, File> files, Response.Listener<String> listener,
                                Response.ErrorListener errorListener){
       uploadFile(url, null, files, listener, errorListener);

   }

    /**
     * download a file by get method
     * 通过get方式下载文件，给定文件名，不要下载大文件
     * @param url
     * @param fileName
     * @param listener
     * @param errorListener
     */
    public static void downloadFile(String url,String fileName, Response.Listener<File> listener, Response.ErrorListener errorListener){
        if(mRequestQueue==null){
            return;
        }
        mRequestQueue.add(new FileRequest(url,fileName,listener,errorListener));

    }

    /**
     * download a file by get method
     * 通过get方式下载文件，不给定文件名 ，根据url生成文件名，不要下载大文件
     * @param url
     * @param listener
     * @param errorListener
     */
    public static void downloadFile(String url,Response.Listener<File> listener, Response.ErrorListener errorListener){
        downloadFile(url, null, listener, errorListener);
    }
    /**
     * 同步的请求，只写了一个作为例子，其他的需求可以仿照写出
     * * RequestFuture&lt;JSONObject&gt; future = RequestFuture.newFuture();
     * MyRequest request = new MyRequest(URL, future, future);
     *
     * // If you want to be able to cancel the request:
     * future.setRequest(requestQueue.add(request));
     *
     * // Otherwise:
     * requestQueue.add(request);
     *
     * try {
     *   JSONObject response = future.get();
     *   // do something with response
     * } catch (InterruptedException e) {
     *   // handle the error
     * } catch (ExecutionException e) {
     *   // handle the error
     * }
     */
    public static String postSyn(String url,Map<String,String> params){
        if(mRequestQueue==null){
            return null;
        }
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,url,params,future,future);
        future.setRequest(mRequestQueue.add(stringRequest));
        try {
            String response =future.get();
            return response;
            // do something with response
        } catch (InterruptedException e) {
            // handle the error
        } catch (ExecutionException e) {
            // handle the error
        }
        return null;

    }

    public static <T> T postSyn(String url,Map<String,String> params,Class<T> clazz){
        if(mRequestQueue==null){
            return null;
        }
        RequestFuture<T> future = RequestFuture.newFuture();
        future.setRequest(mRequestQueue.add(new GsonRequest<T>(Request.Method.POST,url,params,clazz,future,future)));
        try {
            T response =future.get();
            return response;
            // do something with response
        } catch (InterruptedException e) {
            // handle the error
        } catch (ExecutionException e) {
            // handle the error
        }
        return null;

    }

}
