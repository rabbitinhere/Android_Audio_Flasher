package com.ra.com.audioflasher;

import java.io.File;

/**
 * Created by Administrator on 2015/10/2.
 * add from file2
 */
public class Tools {

    public static String getSDCardPath() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return android.os.Environment.getExternalStorageDirectory().toString() + "/";
        } else {
            return "/";
        }
    }

    public static boolean ensureFilePathExist(String urlFile){
        boolean res;
        int lastIndex = urlFile.lastIndexOf("/");
        String filePath = urlFile.substring(0, lastIndex);
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            res =  fileDir.mkdirs();//建立多级级目录，建立一级目录是：fileDir.mkdir()
        }else
        {
            res = true;
        }

        return res;
    }

    /**
     * 注意：此方法不会进行递归子文件夹
     *
     * @param filePath
     * @return
     */
    public static boolean clearFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return false;
        if (!file.isDirectory())
            return false;
        File[] arrayF = file.listFiles();
        for (File f : arrayF) {
            if (f.exists())
                f.delete();
        }
        return true;
    }
}
