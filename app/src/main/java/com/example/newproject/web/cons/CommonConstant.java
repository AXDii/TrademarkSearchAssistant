package com.example.newproject.web.cons;


/*
*
* 这个类里面主要是经常使用的常量，成功码或错误码
* */
public class CommonConstant {

    public static final String INITIAL_STATE = "INITIAL_STATE";//每个bean的初始状态

    public static final String USER_EXISTS = "USER_EXISTS";//用户已存在

    public static final String USER_NOT_EXISTS = "USER_NOT_EXISTS";//用户不存在

    public static final String SERVER_ERROR = "SERVER_ERROR";//服务器出错

    public static final String REGISTER_SUCCESS = "REGISTER_SUCCESS";//注册成功

    public static final String PASSWORD_WRONG = "PASSWORD_WRONG";//密码错误

    public static final String OPERATE_SUCCESS = "OPERATE_SUCCESS";//操作成功

    public static final String LINKAGE_INTERRUPT = "LINKAGE_INTERRUP";//连接中断

    public static final String LOGIN_IN_NEW_DEVICE = "LOGIN_IN_NEW_DEVICE";//发生二次登陆

    public static final String TIME_OUT = "TIME_OUT";//发生超时

    public static final String USER_LOGOUT = "USER_LOGOUT";//用户登出

    public static final String JSON_TYPE = "application/json";

    public static final String NULL_STRING = "null";

    public static final int INIT_ID = -1;

    public static final int DEFAULT_NUMBER = 0;

    public static final String JSON_STRING = "JSON_STRING";//json string

    public static final boolean USER_OFFLINE = false;


    public static final boolean USER_ONLINE = true;

    public static final String SERVER_ADDRESS = "http://atzlt.top:8080/trademark";

    public static final String API_LOGIN = SERVER_ADDRESS + "/login";

    public static final String API_REGISTER = SERVER_ADDRESS + "/register";

    public static final String API_LOGOUT = SERVER_ADDRESS + "/logout";

    public static final String API_UPDATE_USER_INFO = SERVER_ADDRESS + "/updateUserInfo";

    public static final String API_GET_TRADEMARK_LIST_DATA = SERVER_ADDRESS + "/initData";

    public static final String API_GET_TRADEMARK_BY_ID = SERVER_ADDRESS + "/getTrademarkDomainById";

    public static final String API_GET_TRADEMARK_IMG_BY_ID = SERVER_ADDRESS + "/trademarkImg";

    public static final String API_GET_TRADEMARK_TB_IMG_BY_ID = SERVER_ADDRESS + "/trademarkTbImg";

    public static final String API_GEI_SEG_RESULT = SERVER_ADDRESS + "/seg";

    public static final String API_GET_TOP_TEN_SIMPLE_NEWS = SERVER_ADDRESS + "/getTopTenSimpleNews";//获取前十条数据的简单信息

    public static final String API_GET_NEWS_BY_ID = SERVER_ADDRESS + "/getNewsById";

    public static final String API_GET_OLD_NEWS_fROM_OLD_NEWS = SERVER_ADDRESS + "/getOldNewsFromOldNews";

    public static final String API_GET_LAST_NEWS_FROM_OLD_NEWS = SERVER_ADDRESS + "/getLastNewsFromOldNews";

    public static final String API_GET_NEWS_BY_IDS = SERVER_ADDRESS + "/getNewsByIds";

    public static final String API_IMAGE_SIMILAR_ENQUIRIES = SERVER_ADDRESS + "/getSimilarTrademarkEnquiriesDomainByImageEnquiries";

    public static final String API_TEXT_SIMILAR_ENQUIRIES = SERVER_ADDRESS + "/getSimilarTrademarkEnquiriesDomainByTextEnquiries";

    public static final String UPDATE_FAULT = "UPDATE_FAULT";

    public static final String GAIN_INIT_DATA_FAULT = "GAIN_INIT_DATA_FAULT";//获取初始数据失败

    public static final String GAIN_TRADEMARK_INFO_FAULT = "GAIN_TRADEMARK_INFO_FAULT";//获取商标数据失败






    public static final int MESSAGE_WHAT_BY_INTERACTION = 100;
    public static final int MESSAGE_WHAT_BY_UPDATE_USER_INFO = 101;
    public static final int MESSAGE_WHAT_BY_LOGIN = 102;
    public static final int MESSAGE_WHAT_REGISTER = 103;



    /**
     *
     * 转换为JSONString时常用的常量
     */
    public static final String SESSION_ID = "SESSION_ID";
    public static final String STATUS_CODE = "STATUS_CODE";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_ID = "USER_ID";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String USER_REAL_NAME = "USER_REAL_NAME";
    public static final String USER_PHONE = "USER_PHONE";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_ADDRESS = "USER_ADDRESS";
    public static final String USER_TYPE = "USER_TYPE";
    public static final String USER_LOCKED = "USER_LOCKED";


    /**
     *
     * 处理目录时使用的常量
     */

    public static final String APP_DIR = "APP_DIR";

    public static final String IMG_DIR = "IMG_DIR";
    public static final String TRADEMARK_IMG_DIR = "TRADEMARK_IMG_DIR";

    public static final String DOC_DIR = "DOC_DIR";
    public static final String TRADEMARK_APPLICATION_FILES_DIR = "TRADEMARK_APPLICATION_FILES_DIR";

    public static final String NEWS_DIR = "NEWS_DIR";

    public static final String NEWS_DETAIL_DIR_FILE = "NEWS_DETAIL_DIR_FILE";

    public static final String NEWS_DETAIL_DIR_CONTENT_FILE = "NEWS_DETAIL_DIR_CONTENT_FILE";

    public static final String NEWS_DETAIL_DIR_INFO_FILE = "NEWS_DETAIL_DIR_INFO_FILE";




    public static final String DATA_DIR = "DATA_DIR";

    public static final String CACHE_DIR = "CACHE_DIR";

    public static final String TRADEMARK_TB_IMG_DIR = "TRADEMARK_TB_IMG_DIR";



    public static final String TRADEMARK_IMG_FILE = "TRADEMARK_IMG_FILE";

    public static final String TRADEMARK_TB_IMG_FILE = "TRADEMARK_TB_IMG_FILE";


    public static final String WORD_SEG_DIR = "WORD_SEG_DIR";

    public static final String WORD_SEG_LOCAL_DIR = "WORD_SEG_LOCAL_DIR";

    public static final String SEGMENTATION_DOMAIN = "SEGMENTATION_DOMAIN";

}
