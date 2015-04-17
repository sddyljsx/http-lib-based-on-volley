package neal.http.utils;

import java.util.Random;

/**
 * Created by neal on 2014/11/5.
 */
public class HttpHeaderProvider {

    public static class HeaderName{
        public static final String TRANSFER_ENCODING = "Transfer-Encoding";
        public static final String CONTENT_LEN = "Content-Length";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String CONTENT_ENCODING = "Content-Encoding";
        public static final String EXPECT_DIRECTIVE = "Expect";
        public static final String CONN_DIRECTIVE = "Connection";
        public static final String TARGET_HOST = "Host";
        public static final String USER_AGENT = "User-Agent";
        public static final String DATE_HEADER = "Date";
        public static final String SERVER_HEADER = "Server";
        public static final String EXPECT_CONTINUE = "100-continue";
        public static final String CONN_CLOSE = "Close";
        public static final String CONN_KEEP_ALIVE = "Keep-Alive";
        public static final String CHUNK_CODING = "chunked";
        public static final String IDENTITY_CODING = "identity";
    }

    /**
     * 编码集合
     */
    public static class Charset {
        public static final String UTF_8 = "UTF-8";
        public static final String UTF_16 = "UTF-16";
        public static final String US_ASCII = "US-ASCII";
        public static final String ASCII = "ASCII";
        public static final String ISO_8859_1 = "ISO-8859-1";
        public static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";
        public static final String DEFAULT_PROTOCOL_CHARSET = "US-ASCII";
        /**
         * here use utf-8 as default charset for http request
         */
        /**
         * Default encoding for POST or PUT parameters. See {@link neal.http.base.Request#getParamsEncoding()}.
         * 网络请求参数默认编码
         */
        public static final String DEFAULT_REQUEST_CHARSET = "UTF-8";
    }

    /**
     * The pool of ASCII chars to be used for generating a multipart boundary.
     */
    private final static char[] MULTIPART_CHARS ="-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String generateBoundary() {
        final StringBuilder buffer = new StringBuilder();
        final Random rand = new Random();
        final int count = rand.nextInt(11) + 30; // a random size from 30 to 40
        for (int i = 0; i < count; i++) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }

    public static class ContentType{
        public static final String APPLICATION_ATOM_XML = "application/atom+xml";
        public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
        public static final String APPLICATION_JSON ="application/json";
        public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
        public static final String APPLICATION_SVG_XML = "application/svg+xml";
        public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";
        public static final String APPLICATION_XML = "application/xml";
        public static final String MULTIPART_FORM_DATA = "multipart/form-data";
        public static final String TEXT_HTML = "text/html";
        public static final String TEXT_PLAIN = "text/plain";
        public static final String TEXT_XML = "text/xml";
        public static final String WILDCARD = "*/*";

        public static final String CHARSET_PARAM = "; charset=";
        public static final String BOUNDARY_PARAM = "; boundary=";
        private String mimeType;
        private String charSet;
        private String boundary;
        public ContentType(String mimeType,String charSet,String boundary){
            this.mimeType=mimeType;
            this.charSet=charSet;
            this.boundary=boundary;
        }
        public ContentType(String mimeType,String charSet){
            this(mimeType,charSet,null);
        }
        public String toString(){
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(mimeType);
            if(boundary!=null){
                stringBuilder.append(BOUNDARY_PARAM).append(boundary);
            }
            if(charSet!=null) {
                stringBuilder.append(CHARSET_PARAM).append(charSet);
            }
            return stringBuilder.toString();
        }
    }

    /**
     * MIME相关资源
     */
    public static class MIME {
        public static final String CONTENT_TYPE          = "Content-Type";
        public static final String CONTENT_TRANSFER_ENC  = "Content-Transfer-Encoding";
        public static final String CONTENT_DISPOSITION   = "Content-Disposition";

        public static final String FIELD_SEP = ": ";
        public static final String CR_LF = "\r\n";
        public static final String TWO_DASHES = "--";

        /**
         * Content-Transfer-Encoding values
         */
        public static final String ENC_8BIT              = "8bit";
        public static final String ENC_BINARY            = "binary";
    }
}
