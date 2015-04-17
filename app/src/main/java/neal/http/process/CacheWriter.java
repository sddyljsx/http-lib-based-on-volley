package neal.http.process;

import android.os.HandlerThread;
import android.os.Handler;

import neal.http.base.Cache;

/**
 * Created by Neal on 2014/11/1.
 */
public class CacheWriter {
    private Cache mCache;
    private HandlerThread handlerThread;
    private Handler mHandler;
    public CacheWriter(Cache cache){
        mCache=cache;
        init();
    }
    private void init(){
        handlerThread=new HandlerThread("cache_writer");
        handlerThread.start();
        mHandler=new Handler(handlerThread.getLooper());
    }
    public void postCacheWrite(String cacheKey, Cache.Entry entry){
        mHandler.post(new WriteRunnable(cacheKey,entry));
    }

    private class WriteRunnable implements Runnable{
        private String mCacheKey;
        private Cache.Entry mEntry;
        public WriteRunnable(String cacheKey, Cache.Entry entry){
            mCacheKey=cacheKey;
            mEntry=entry;
        }
        @Override
        public void run() {
            mCache.put(mCacheKey,mEntry);
        }
    }
}
