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
import java.util.Map;

import neal.http.base.NetworkResponse;
import neal.http.base.Request;
import neal.http.base.Response;
import neal.http.utils.HttpHeaderParser;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringRequest extends Request<String> {
    private final Response.Listener<String> mListener;

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link neal.http.base.Request.Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(int method, String url,Map<String,String> params, Response.Listener<String> listener,
            Response.ErrorListener errorListener) {
        super(method, url,params, errorListener);
        mListener = listener;
    }
    public StringRequest(String url, Response.Listener<String> listener,
                         Response.ErrorListener errorListener) {
        this(Method.GET, url,null, listener,errorListener);
    }


    @Override
    public void deliverResponse(String response) {
        mListener.onResponse(response);
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
}
