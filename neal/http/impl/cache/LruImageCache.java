package neal.http.impl.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;

import neal.http.process.ImageLoader;
import neal.http.utils.RecyclingBitmapDrawable;

/**
 * Created by Neal on 2014/10/2.
 */
public class LruImageCache extends LruCache<String, BitmapDrawable> implements ImageLoader.ImageCache {

    private Context mContext;

    public LruImageCache(Context context,int maxSize){
        super(maxSize);
        mContext=context;
    }

    public LruImageCache(Context context) {
        this(context,getCacheSize());
    }

    public static int getCacheSize(){
        //获取可用的内存最大值，使用内存超过这个值会引起OutOfMemory异常
        //以KB为单位，要与sizeof()的单位一致
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 8;
        return cacheSize;
    }

    @Override
    protected int sizeOf(String key, BitmapDrawable value) {
        if(value.getBitmap()==null || value.getBitmap().isRecycled()){
            return 0;
        }
        return value.getBitmap().getRowBytes() * value.getBitmap().getHeight()/1024;
    }

    @Override
    @Deprecated
    public Bitmap getBitmap(String url) {
        if(getDrawable(url)==null){
            return null;
        }
        return get(url).getBitmap();
    }

    @Override
    public BitmapDrawable getDrawable(String url) {
        BitmapDrawable d= get(url);
        if(d == null || d.getBitmap() == null ||d.getBitmap().isRecycled()){
            return null;
        }
        return d;
    }

    @Override
    @Deprecated
    public void putBitmap(String url, Bitmap bitmap) {
        if(bitmap==null || bitmap.isRecycled()){
            return;
        }
        put(url, new BitmapDrawable(mContext.getResources(),bitmap));
    }

    @Override
    public void putDrawable(String url, BitmapDrawable drawable) {
        if(drawable==null || drawable.getBitmap()==null || drawable.getBitmap().isRecycled()){
            return;
        }
        if(RecyclingBitmapDrawable.class.isInstance(drawable)){
            ((RecyclingBitmapDrawable)drawable).setIsCached(true);
        }
        put(url,drawable);
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
        //RecyclingBitmapDrawable类型的Drawable需要回收内存
        if(RecyclingBitmapDrawable.class.isInstance(oldValue)){
            // The removed entry is a recycling drawable, so notify it
            // that it has been removed from the memory cache
            ((RecyclingBitmapDrawable) oldValue).setIsCached(false);
        }
        super.entryRemoved(evicted, key, oldValue, newValue);

    }

}
