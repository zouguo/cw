package com.clinkworld.pay.entity;

import java.io.Serializable;

/**
 * Created by shirenhua on 2015/10/14.
 * <p/>
 * 订单支付信息实体
 */
public class OrderPayInfo implements Serializable {

    /**
     * 订单总额
     */
    private float moneryOrderCount;

    /**
     * 礼券抵现金额
     */
    private float moneryCoupouPay;

    /**
     * 应付总额
     */
    private float moneryRealPay;

    /**
     * 微信支付二维码图片地址
     */
    private String zxingWeixinUrl;

    /**
     * 支付宝支付二维码图片地址
     */
    private String zxingZhifubaoUrl;
}
