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
package neal.http.base;

/**
 * Created by Neal on 2014/10/28.
 */
/**
 * Exception style class encapsulating HttpError
 */
@SuppressWarnings("serial")
public class HttpError extends Exception{

    public final NetworkResponse mNetworkResponse;

    public HttpError(){
        mNetworkResponse=null;
    }
    public HttpError(NetworkResponse networkResponse){
        mNetworkResponse=networkResponse;
    }

    public HttpError(String exceptionMessage){
        super(exceptionMessage);
        mNetworkResponse=null;
    }

    public HttpError(String exceptionMessage,Throwable reason){
        super(exceptionMessage,reason);
        mNetworkResponse=null;
    }

    public HttpError(Throwable reason){
        super(reason);
        mNetworkResponse=null;
    }
}
