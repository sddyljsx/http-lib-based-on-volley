/*
 * Copyright (C) 2014 Neal
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
package neal.http.impl;

import android.content.Intent;

import neal.http.base.HttpError;
import neal.http.base.NetworkResponse;

/**
 * Created by Neal on 2014/10/28.
 */

/**
 * A List of different kinds of HttpErrors
 * 6种不同HttpError集合
 */
public class HttpErrorCollection {

    /**
     * Indicates that the connection or the socket timed out.
     * 连接超时
     */
    @SuppressWarnings("serial")
    public static class TimeoutError extends HttpError { }

    /**
     * Indicates that the error responded with an error response.
     * 服务器返回错误响应
     */
    @SuppressWarnings("serial")
    public static class ServerError extends HttpError {

        public ServerError(NetworkResponse networkResponse) {
            super(networkResponse);
        }

        public ServerError() {
            super();
        }
    }

    /**
     * Indicates that the server's response could not be parsed.
     * 服务器响应无法解析
     */
    @SuppressWarnings("serial")
    public static class ParseError extends HttpError {
        public ParseError() { }

        public ParseError(NetworkResponse networkResponse) {
            super(networkResponse);
        }

        public ParseError(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Error indicating that no connection could be established when performing a  request.
     * 无法建立http连接请求
     */
    @SuppressWarnings("serial")
    public static class NoConnectionError extends HttpError {
        public NoConnectionError() {
            super();
        }

        public NoConnectionError(Throwable reason) {
            super(reason);
        }
    }

    /**
     * Indicates that there was a network error when performing a request.
     * 网络错误
     */
    @SuppressWarnings("serial")
    public static class NetworkError extends HttpError {
        public NetworkError() {
            super();
        }

        public NetworkError(Throwable cause) {
            super(cause);
        }

        public NetworkError(NetworkResponse networkResponse) {
            super(networkResponse);
        }
    }

    /**
     * Error indicating that there was an authentication failure when performing a Request.
     * 认证失败错误
     */
    @SuppressWarnings("serial")
    public static class AuthFailureError extends HttpError {
        /**
         * An intent that can be used to resolve this exception. (Brings up the password dialog.)
         * 解决此处错误的intent，比如说显示输入密码对话框
         */
        private Intent mResolutionIntent;

        public AuthFailureError() { }

        public AuthFailureError(Intent intent) {
            mResolutionIntent = intent;
        }

        public AuthFailureError(NetworkResponse response) {
            super(response);
        }

        public AuthFailureError(String message) {
            super(message);
        }

        public AuthFailureError(String message, Exception reason) {
            super(message, reason);
        }

        public Intent getResolutionIntent() {
            return mResolutionIntent;
        }

        @Override
        public String getMessage() {
            if (mResolutionIntent != null) {
                return "User needs to (re)enter credentials.";
            }
            return super.getMessage();
        }
    }

}
