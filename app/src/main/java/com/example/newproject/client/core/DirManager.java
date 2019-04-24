package com.example.newproject.client.core;


import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.example.newproject.web.cons.CommonConstant;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//用于管理文件夹
public class DirManager {

    private static final String TAG = "DirManager";

    //获取软件根目录的文件夹  具体位于Android/Data/new.example.project
    public static File getAppDirFile(Context context) {
        File appDir = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            appDir = Objects.requireNonNull(context.getExternalCacheDir()).getParentFile();
        } else {
            appDir = Environment.getDataDirectory();
        }
        return appDir;
    }

    //这东东一般不用
    public static String getAppDirPath(Context context) {
        return getAppDirFile(context).getPath();
    }


    //初始化所有文件夹
    public static void InitDirs(Context context) {

        File appDir = getAppDirFile(context);
        String appPath = appDir.getPath();

        //-------------图片目录及其子目录
        String imgPath = appPath + "/img";
        String trademarkImgPath = imgPath + "/trademarkImg";
        String trademarkTbImgPath = imgPath + "/trademarkTbImg";





        //------------文档目录及其子目录
        String docPath = appPath + "/doc";
        String trademarkApplicationFilesPath = docPath + "/trademarkApplicationFiles";




        //----------------数据目录及其子目录
        String dataPath = appPath + "/data";

        //----------------新闻目录及其子目录
        String newsPath = appPath + "/news";



        List<File> dirs = new ArrayList<File>();

        dirs.add(new File(imgPath));//图片文件夹
        dirs.add(new File(trademarkImgPath));//商标图像文件夹
        dirs.add(new File(trademarkTbImgPath));//商标缩略图

        dirs.add(new File(docPath));//文档文件夹
        dirs.add(new File(trademarkApplicationFilesPath));//商标申请文件目录

        dirs.add(new File(dataPath));//数据文件夹

        dirs.add(new File(newsPath));//新闻文件夹

        createDirs(dirs, context);

    }

    public static void createDir(File dir, Context context){

        if (dir.exists()) {
            Log.d(TAG, "createDir: 文件夹已存在");
            return;
        }

        if (dir.mkdir()) {
            Log.d(TAG, "createDir: 成功建立文件夹" );
        } else {
            Log.d(TAG, "createDir: 建立文件夹失败！！");
        }

    }


    public static void createDirs(List<File> dirs, Context context) {

        for (File dir : dirs) {
            createDir(dir, context);
        }

    }

    public static File getDirFile(String dirCode, Context context) {

        File appDir = getAppDirFile(context);
        String appPath = appDir.getPath();
        String path = "";

        switch (dirCode) {
            case CommonConstant.APP_DIR:
                path = appPath;
                break;
            case CommonConstant.IMG_DIR:
                path = appPath + "/img";
                break;
            case CommonConstant.TRADEMARK_IMG_DIR:
                path = appPath + "/img/trademarkImg";
                break;
            case CommonConstant.TRADEMARK_TB_IMG_DIR:
                path = appPath + "/img/trademarkTbImg";
                break;
            case CommonConstant.DOC_DIR:
                path = appPath + "/doc";
                break;
            case CommonConstant.TRADEMARK_APPLICATION_FILES_DIR:
                path = appPath + "/doc/trademarkApplicationFiles";
                break;
            case CommonConstant.DATA_DIR:
                path = appPath + "/data";
                break;
            case CommonConstant.NEWS_DIR:
                path = appPath +"/news";
                break;
            case CommonConstant.CACHE_DIR:
                path = context.getExternalCacheDir().getPath();
                break;
            default:
                break;
        }
        if (path.equals("")) {
            return null;
        }

        return new File(path);
    }

    //获取文件路径
    public static String getFilePath(String str, String fileCode, Context context) {

        String path = "";
        switch (fileCode) {
            case CommonConstant.TRADEMARK_IMG_FILE:
                //str 为trademarkId
                path = getDirFile(CommonConstant.TRADEMARK_IMG_DIR, context).getPath() + "/" + str + ".JPG";
                break;
            case CommonConstant.TRADEMARK_TB_IMG_FILE:
                path = getDirFile(CommonConstant.TRADEMARK_TB_IMG_DIR, context).getPath() + "/tb" + str + ".JPG";
                break;
            case CommonConstant.NEWS_DETAIL_DIR_FILE:
                path = getDirFile(CommonConstant.NEWS_DIR, context).getPath() + "/new" + str;
                break;
            case CommonConstant.NEWS_DETAIL_DIR_CONTENT_FILE:
                path = getFilePath(str, CommonConstant.NEWS_DETAIL_DIR_FILE, context) + "/content";
                break;
            case CommonConstant.NEWS_DETAIL_DIR_INFO_FILE:
                path = getFilePath(str, CommonConstant.NEWS_DETAIL_DIR_FILE, context) + "/info";
                break;
            default:
                break;



        }
        return path;

    }

    /**
     * 获取文件夹大小
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file){

        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
            {
                if (fileList[i].isDirectory())
                {
                    size = size + getFolderSize(fileList[i]);

                }else{
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //return size/1048576;
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     * @param deleteThisPath
     * @param filepath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size/1024;
        if(kiloByte < 1) {
            return size + "B";
        }

        double megaByte = kiloByte/1024;
        if(megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte/1024;
        if(gigaByte < 1) {
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte/1024;
        if(teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    private static void cleanCache(String... filePaths){
        for (String filePath : filePaths) {
            DirManager.deleteFolderFile(filePath, false);
        }
    }

    public static void cleanTrademarkImg(Context context){

        String trademarkImgFile = DirManager.getDirFile(CommonConstant.TRADEMARK_IMG_DIR, context).getPath();
        String trademarkTbImgFile = DirManager.getDirFile(CommonConstant.TRADEMARK_TB_IMG_DIR, context).getPath();
        DirManager.cleanCache(trademarkImgFile, trademarkTbImgFile);


    }

    public static void cleanNewsCache(Context context){

        String newsFile = DirManager.getDirFile(CommonConstant.NEWS_DIR, context).getPath();
        DirManager.cleanCache(newsFile);

    }

    public static void cleanTrademarkImgAndNewsCache(Context context) {

        cleanTrademarkImg(context);
        cleanNewsCache(context);

    }





}
