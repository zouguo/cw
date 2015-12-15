package com.clinkworld.pay;

/**
 * Created by shirenhua on 2015/10/14.
 * <p/>
 * api接口地址
 */
public class ServerUrl {

    /**
     * 服务器ip
     */
    public static final String BASE_URL = "https://mapi.clinkworld.com";

    /**
     * 用户登录
     */
    public static final String LOGIN_USER_PATH = "/user/login";

    /**
     * 用户登出
     */
    public static final String LOGOUT_USER_PATH = "/user/logout";

    /**
     * 查询用户信息
     */
    public static final String USERINFO_PATH = "/user/info";

    /**
     * 查询平台信息
     */
    public static final String PLATFORMINFO_PATH = "/platform/info";


    //**************************** 收银部分 *******************************/

    /**
     * POS机登录
     */
    public static final String LOGIN_POS_PATH = "/pos/login";

    /**
     * POS机登出
     */
    public static final String LOGOUT_POS_PATH = "/pos/logout";

    /**
     * 商品信息查询
     */
    public static final String PRODUCT_INIFO_PATH = "/product/";


    /**
     * 优惠券有效性验证
     */
    public static final String COUPON_AVAILABLITITY_PATH = "/coupon/availability?coupon_no={coupon_no}";

    /**
     * 订单提交
     */
    public static final String ORDER_PATH = "/order";

    /**
     * 订单支付信息
     */
    public static final String ORDER_PAY_INFO_PATH = "/order/{order_id}/pay";

    /**
     * 现金收银处理
     */
    public static final String ORDER_PAY_CASH_PATH = "/order/{order_id}/pay/cash";

    /**
     * 订单支付状态
     */
    public static final String ORDER_PAY_STATUS_PATH = "/order/{order_id}/paystatus";
    /**
     * 订单支付状态获取
     */
    public static final String ORDER_PAY_TYPE_PATH = "/order/paytypes?";


    //*************************************** 入库部分 *************************************

    /**
     * 查询入库单列表
     */
    public static final String INCOME_BATCH_LIST_PATH = "/product/income/batch?";

    /**
     * 提交入库单
     */
    public static final String INCOME_BATCH_SUBMIT_PATH = "/product/income/batch";

    /**
     * 查询入库单
     */
    public static final String INCOME_BATCH_QUERY_PATH = "/product/income/batch/";
    /**
     * 商品图片上传
     */
    public static final String INCOME_UPLOAD_IAMGE_FILE = "/product/img/upload";


    //*************************************** 优惠券部分 ************************************


    /**
     * 查询优惠券列表
     */
    public static final String COUPON_QUERY_LIST_PATH = "/coupon?";

    /**
     * 新建优惠券
     */
    public static final String COUPON_CREATE_PATH = "/coupon";
    /**
     * 优惠券有效性
     */
    public static final String COUPONS_AVAILBITLY = "/coupon/availability?";

    //*************************************** 订单部分 ************************************
    /**
     * 订单流水
     */
    public static final String ORDER_FLOW_PATH = "/order/all?";

    /**
     * 查询订单号
     */
    public static final String ORDER_SEARCH_PATH = "/order/search?";
    /**
     * 指定类型订单查询
     */
    public static final String ORDER_TYPE_SEARCH_PATH = "/order/list?";
    /**
     * 订单详情
     */
    public static final String ORDER_DETAIL_PATH = "/order?";
    /**
     * 订单提交
     */
    public static final String ORDER_POST_PATH = "/order";

}
