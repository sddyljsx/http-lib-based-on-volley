package neal.http.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;

import neal.http.Http;
import neal.http.base.HttpError;
import neal.http.process.ImageLoader;
import neal.http.utils.RecyclingBitmapDrawable;

/**
 * Created by Neal on 2014/10/30.
 */
/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request.automatically notifies the drawable when it is
 * being displayed. It is a combination of NetworkImageView and RecyclingImageView
 */
public class RecyclingNetImageView extends RecyclingImageView{

    /** The URL of the network image to load */
    private String mUrl;

    /**
     * Resource ID of the image to be used as a placeholder until the network image is loaded.
     */
    private int mDefaultImageId;

    private Drawable mDefaultDrawable;

    /**
     * Resource ID of the image to be used if the network response fails.
     */
    private int mErrorImageId;

    private Drawable mErrorDrawable;

    /** Local copy of the ImageLoader. */
    private ImageLoader mImageLoader;

    /** Current ImageContainer. (either in-flight or finished) */
    private ImageLoader.ImageContainer mImageContainer;

    private Context mContext;

    public RecyclingNetImageView(Context context) {
        this(context, null);
        mContext=context;
    }

    public RecyclingNetImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclingNetImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext=context;
    }

    /**
     * Sets URL of the image that should be loaded into this view. Note that calling this will
     * immediately either set the cached image (if available) or the default image specified by
     * {@link RecyclingNetImageView#setDefaultImageResId(int)} on the view.
     *
     * NOTE: If applicable, {@link RecyclingNetImageView#setDefaultImageResId(int)} and
     * {@link RecyclingNetImageView#setErrorImageResId(int)} should be called prior to calling
     * this function.
     *
     * @param url The URL that should be loaded into this ImageView.
     * @param imageLoader ImageLoader that will be used to make the request.
     */
    public void setImageUrl(String url, ImageLoader imageLoader,int defaultImage, int errorImage) {
        mUrl = url;
        if(null==imageLoader){
            imageLoader= Http.getImageLoader(mContext.getApplicationContext());
        }
        mImageLoader = imageLoader;
        setDefaultImageResId(defaultImage);
        setErrorImageResId(errorImage);
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
    }
    public void setImageUrl(String url,int defaultImage, int errorImage){

        setImageUrl(url,null,defaultImage,errorImage);

    }
/*    public void setImageUrl(String url, ImageLoader imageLoader) {
        mUrl = url;
        mImageLoader = imageLoader;
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
    }*/

    /**
     * Sets the default image resource ID to be used for this view until the attempt to load it
     * completes.
     */
    public void setDefaultImageResId(int defaultImage) {
        mDefaultImageId = defaultImage;
        //资源的drawable，系统会提供缓存机制，不做缓存，recycle等处理
        mDefaultDrawable=getResources().getDrawable(defaultImage);

    }

    /**
     * Sets the error image resource ID to be used for this view in the event that the image
     * requested fails to load.
     */
    public void setErrorImageResId(int errorImage) {
        mErrorImageId = errorImage;
        mErrorDrawable=getResources().getDrawable(errorImage);
    }

    /**
     * Loads the image for the view if it isn't already loaded.
     * @param isInLayoutPass True if this was invoked from a layout pass, false otherwise.
     */
    void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = getWidth();
        int height = getHeight();

        boolean wrapWidth = false, wrapHeight = false;
        if (getLayoutParams() != null) {
            wrapWidth = getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT;
            wrapHeight = getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        // if the view's bounds aren't known yet, and this is not a wrap-content/wrap-content
        // view, hold off on loading the image.
        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }

        // if the URL to be loaded in this view is empty, cancel any old requests and clear the
        // currently loaded image.
        if (TextUtils.isEmpty(mUrl)) {
            if (mImageContainer != null) {
                mImageContainer.cancelRequest();
                mImageContainer = null;
            }
            setDefaultImageOrNull();
            return;
        }

        // if there was an old request in this view, check if it needs to be canceled.
        if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
            if (mImageContainer.getRequestUrl().equals(mUrl)) {
                // if the request is from the same URL, return.
                return;
            } else {
                // if there is a pre-existing request, cancel it if it's fetching a different URL.
                mImageContainer.cancelRequest();
                setDefaultImageOrNull();
            }
        }

        // Calculate the max image width / height to use while ignoring WRAP_CONTENT dimens.
        int maxWidth = wrapWidth ? 0 : width;
        int maxHeight = wrapHeight ? 0 : height;

        // The pre-existing content of this view didn't match the current URL. Load the new image
        // from the network.
        ImageLoader.ImageContainer newContainer = mImageLoader.get(mUrl,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onErrorResponse(HttpError error) {
                        if (mErrorDrawable != null) {
                            setImageDrawable(mErrorDrawable);
                        }
                    }

                    @Override
                    public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                        // If this was an immediate response that was delivered inside of a layout
                        // pass do not set the image immediately as it will trigger a requestLayout
                        // inside of a layout. Instead, defer setting the image by posting back to
                        // the main thread.
                        if (isImmediate && isInLayoutPass) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    onResponse(response, false);
                                }
                            });
                            return;
                        }

                        if (response.getBitmapDrawable() != null) {
                            setImageDrawable(response.getBitmapDrawable());
                        } else if (mDefaultDrawable != null) {
                            setImageDrawable(mDefaultDrawable);
                        }
                    }
                }, maxWidth, maxHeight);

        // update the ImageContainer to be the new bitmap container.
        mImageContainer = newContainer;
    }

    private void setDefaultImageOrNull() {
        if(mDefaultDrawable != null) {
            //此处统一使用setImageDrawable，否则影响recycle。因为使用setImageResource也会改变getDrawable的值。
            setImageDrawable(mDefaultDrawable);
        }
        else {
            setImageDrawable(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mImageContainer != null) {
            // If the view was bound to an image request, cancel it and clear
            // out the image from the view.
            mImageContainer.cancelRequest();
            setImageDrawable(null);
            // also clear out the container so we can reload the image if necessary.
            mImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }
}
