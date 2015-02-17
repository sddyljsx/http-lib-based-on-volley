package neal.utils;

import android.os.Environment;
import android.os.StatFs;


import java.io.File;

import pku.isharebook.base.BaseApplication;

/**
 * Created by neal on 2015/1/10.
 */
public class StorageUtil {

    public static boolean hasStorage(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 判断是否用写sd卡目录的权限
     * @return
     */
    public static boolean isExternalStorageCanWrite(){
        return Environment.getExternalStorageDirectory().canWrite();
    }

    /**
     * get the free space of a specified file path
     *
     * @param path: the specified file path
     * @return the free space size or 0 if the path is not exist or is not a
     *         real file path
     */
    public static long getFreeSpace(File path) {
        long size = 0;
        if (path.exists() && path.isDirectory()) {
            StatFs stat = new StatFs(path.getAbsolutePath());
            size = ((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize());
        }
        return size;
    }

    /**
     *
     * @return 程序存放图片的文件夹路径
     */
    public static File getPicStorageDirPath(){
        return BaseApplication.getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

    }

}
