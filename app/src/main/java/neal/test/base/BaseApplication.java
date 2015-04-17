package neal.test.base;

import android.app.Application;

import neal.http.Http;

/**
 * Created by Neal on 2014/10/29.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Http.init(getApplicationContext());
    }
}
