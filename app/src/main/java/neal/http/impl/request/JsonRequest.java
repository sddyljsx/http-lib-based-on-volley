/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neal.http.impl.request;



import java.io.UnsupportedEncodingException;

import neal.http.base.NetworkResponse;
import neal.http.base.Request;
import neal.http.base.Response;
import neal.http.utils.HttpHeaderProvider.ContentType;
import neal.http.utils.HttpHeaderProvider.Charset;
import neal.http.utils.HttpLog;

/**
 * A request for retrieving a T type response body at a given URL that also
 * optionally sends along a JSON body in the request specified.
 *
 * @param <T> JSON type of response expected
 */
public abstract class JsonRequest<T> extends Request<T> {
    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = Charset.UTF_8;
    private final Response.Listener<T> mListener;
    private final String mRequestBody;

    /**
     * Deprecated constructor for a JsonRequest which defaults to GET unless {@link #getPostBody()}
     * or {@link #getPostParams()} is overridden (which defaults to POST).
     *
     * @deprecated Use {@link #JsonRequest(int, String, String, Response.Listener, Response.ErrorListener)}.
     */
    @Deprecated
    public JsonRequest(String url, String requestBody, Response.Listener<T> listener,
            Response.ErrorListener errorListener) {
        this(Method.DEPRECATED_GET_OR_POST, url, requestBody, listener, errorListener);
    }

    public JsonRequest(int method, String url, String requestBody, Response.Listener<T> listener,
            Response.ErrorListener errorListener) {
        super(method, url, null,errorListener);
        mListener = listener;
        mRequestBody = requestBody;
    }

    @Override
    public void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    abstract public Response<T> parseNetworkResponse(NetworkResponse response);

    /**
     * @deprecated Use {@link #getBodyContentType()}.
     */
    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    /**
     * @deprecated Use {@link #getBody()}.
     */
    @Override
    public byte[] getPostBody() {
        return getBody();
    }

    @Override
    public String getBodyContentType() {
        return new ContentType(ContentType.APPLICATION_JSON,getParamsEncoding()).toString();
    }

    @Override
    protected String getParamsEncoding() {
        return PROTOCOL_CHARSET;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(getParamsEncoding());
        } catch (UnsupportedEncodingException uee) {
            HttpLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, getParamsEncoding());
            return null;
        }
    }
}
