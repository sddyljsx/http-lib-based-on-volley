package neal.http.impl.request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import neal.http.base.NetworkResponse;
import neal.http.base.Request;
import neal.http.base.Response;
import neal.http.impl.HttpErrorCollection;
import neal.http.utils.HttpHeaderParser;
import neal.http.utils.HttpHeaderProvider.ContentType;
import neal.http.utils.MultipartEntity;
import static neal.http.utils.HttpHeaderProvider.generateBoundary;

/**
 * Created by neal on 2014/11/4.
 */

/**
    http报文格式如下所示：
         POST /upload.jsp HTTP/1.1
         Accept: txt/plain
         Accept-Language: zh-cn
         Content-Type: multipart/form-data; boundary=7da29f2d890386; charset=UTF-8
         Host: abc.com
         Content-Length: 1516663
         Connection: Keep-Alive
         Cache-Control: no-cache

         --7da29f2d890386

         Content-Disposition: form-data; name="ServerPath"
         Content-Type: text/plain; charset=UTF-8
         Content-Transfer-Encoding: 8bit

         localhost

         --7da29f2d890386

         Content-Disposition: form-data; name="id"
         Content-Type: text/plain; charset=UTF-8
         Content-Transfer-Encoding: 8bit

         12323123

         --7da29f2d890386

         Content-Disposition: form-data; name="FileData"; filename="FileName"
         Content-Type: application/octet-stream
         Content-Transfer-Encoding: binary

         文件数据

         --7da29f2d890386--

 */
public class MultipartRequest extends Request<String> {

    private String boundary = null;

    private final Map<String, String> mParams;
    private final Map<String, File> mFiles;
    private final Response.Listener<String> mListener;

    public MultipartRequest(String url, Map<String, String> params, Map<String, File> files, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST,url,null,errorListener);
        mParams=params;
        mFiles=files;
        mListener=listener;
        boundary=generateBoundary();
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }
    @Override
    public String getBodyContentType() {
        return new ContentType(ContentType.MULTIPART_FORM_DATA,getParamsEncoding(),boundary).toString();
    }
   @Override
    public byte[] getBody() throws HttpErrorCollection.AuthFailureError {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        new MultipartEntity(boundary,mParams,mFiles,getParamsEncoding()).writeTo(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public MultipartEntity getBodyEntity() throws HttpErrorCollection.AuthFailureError {
        return new MultipartEntity(boundary, mParams, mFiles, getParamsEncoding());
    }

    @Override
    public Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public void deliverResponse(String response) {
        mListener.onResponse(response);
    }

}
