package neal.http.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import neal.http.utils.HttpHeaderProvider.MIME;
import neal.http.utils.HttpHeaderProvider.ContentType;
import neal.http.utils.HttpHeaderProvider.Charset;
/**
 * Created by neal on 2014/11/4.
 */
public class MultipartEntity {

    private String boundary;
    private Map<String, String> params;
    private Map<String, File> files;
    private String charset;

    public MultipartEntity(String boundary, Map<String, String> params, Map<String, File> files, String charset) {
        this.boundary = boundary;
        this.params = params;
        this.files = files;
        this.charset=charset;
    }

    /**
     *得到的格式如下

    --7da29f2d890386

    Content-Disposition: form-data; name="ServerPath"
    Content-Type: text/plain; charset=UTF-8
    Content-Transfer-Encoding: 8bit
                          -->此处的换行必须有
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
                          -->此处的换行必须有
    文件数据

    --7da29f2d890386--

     */
    public void writeTo(final OutputStream outstream) {
        StringBuilder stringBuilder1=new StringBuilder();
        StringBuilder stringBuilder2;
        stringBuilder1.append(MIME.TWO_DASHES).append(boundary).append(MIME.CR_LF);
        try {
            byte[] boundaryLine=stringBuilder1.toString().getBytes(charset);
            if(params!=null) {
                for (Map.Entry entry : params.entrySet()) {
                    stringBuilder2 = new StringBuilder();
                    /**
                     * --7da29f2d890386
                     */
                    outstream.write(boundaryLine);
                    /**
                     * Content-Disposition: form-data; name="id"
                     */
                    stringBuilder2.append(MIME.CONTENT_DISPOSITION).append(MIME.FIELD_SEP);
                    stringBuilder2.append("form-data; name=\"");
                    stringBuilder2.append(entry.getKey());
                    stringBuilder2.append("\"");
                    stringBuilder2.append(MIME.CR_LF);
                    /**
                     * Content-Type: text/plain; charset=UTF-8
                     */
                    stringBuilder2.append(MIME.CONTENT_TYPE).append(MIME.FIELD_SEP);
                    stringBuilder2.append(new ContentType(ContentType.TEXT_PLAIN, Charset.UTF_8).toString());
                    stringBuilder2.append(MIME.CR_LF);
                    /**
                     * Content-Transfer-Encoding: 8bit
                     */
                    stringBuilder2.append(MIME.CONTENT_TRANSFER_ENC).append(MIME.FIELD_SEP);
                    stringBuilder2.append(MIME.ENC_8BIT);
                    stringBuilder2.append(MIME.CR_LF);
                    /**
                     * 写入内容
                     */
                    stringBuilder2.append(MIME.CR_LF);
                    stringBuilder2.append(entry.getValue());
                    stringBuilder2.append(MIME.CR_LF);
                    outstream.write(stringBuilder2.toString().getBytes(charset));
                }
            }
            for (Map.Entry entry : files.entrySet()) {
                /**
                 * --7da29f2d890386
                 */
                outstream.write(boundaryLine);
                stringBuilder2 = new StringBuilder();
                /**
                 * Content-Disposition: form-data; name="FileData"; filename="FileName"
                 */
                stringBuilder2.append(MIME.CONTENT_DISPOSITION).append(MIME.FIELD_SEP);
                stringBuilder2.append("form-data; name=\"");
                stringBuilder2.append(entry.getKey());
                stringBuilder2.append("\"");
                if (((File)(entry.getValue())).getName()!= null) {
                    stringBuilder2.append("; filename=\"");
                    stringBuilder2.append(((File)(entry.getValue())).getName());
                    stringBuilder2.append("\"");
                }
                stringBuilder2.append(MIME.CR_LF);
                /**
                 * Content-Type: application/octet-stream
                 */
                stringBuilder2.append(MIME.CONTENT_TYPE).append(MIME.FIELD_SEP);
                stringBuilder2.append(new ContentType(ContentType.APPLICATION_OCTET_STREAM, null).toString());
                stringBuilder2.append(MIME.CR_LF);
                /**
                 * Content-Transfer-Encoding: binary
                 */
                stringBuilder2.append(MIME.CONTENT_TRANSFER_ENC).append(MIME.FIELD_SEP);
                stringBuilder2.append(MIME.ENC_BINARY);
                stringBuilder2.append(MIME.CR_LF);
                outstream.write(stringBuilder2.toString().getBytes(charset));
                /**
                 * 写入文件内容
                 */
                outstream.write(MIME.CR_LF.getBytes(charset));
                final InputStream in = new FileInputStream((File)entry.getValue());
                try {
                    final byte[] tmp = new byte[1024];
                    int l;
                    while ((l = in.read(tmp)) != -1) {
                        outstream.write(tmp, 0, l);
                    }
                    outstream.flush();
                } finally {
                    in.close();
                }
                outstream.write(MIME.CR_LF.getBytes(charset));

            }
            /**
             * --7da29f2d890386--
             */
            stringBuilder1=new StringBuilder();
            stringBuilder1.append(MIME.TWO_DASHES).append(boundary).append(MIME.TWO_DASHES).append(MIME.CR_LF);
            outstream.write(stringBuilder1.toString().getBytes(charset));
            outstream.flush();

        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            stringBuilder1=null;
            stringBuilder2=null;
        }

    }
}
