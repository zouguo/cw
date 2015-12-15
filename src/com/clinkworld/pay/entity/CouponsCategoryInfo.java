package com.clinkworld.pay.entity;

import java.io.Serializable;

/**
 * Created by srh on 2015/11/13.
 * <p/>
 * 优惠券使用实体
 */
public class CouponsCategoryInfo implements Serializable {

    /**
     * 会员名称
     */
    private String memberName;

    //*********************** 优惠券使用情况数据参数 *********************

    /**
     * 订单号
     */
    private String orderId;

    //************************ 优惠券领取情况数据参数 ****************************************

    /**
     * 优惠券领取时间
     */
    private String receiverTime;
    /**
     * 优惠券领取方式
     * 1：二维码
     * 2：链接
     */
    private int receiverChannel;

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReceiverTime() {
        return receiverTime;
    }

    public void setReceiverTime(String receiverTime) {
        this.receiverTime = receiverTime;
    }

    public int getReceiverChannel() {
        return receiverChannel;
    }

    public void setReceiverChannel(int receiverChannel) {
        this.receiverChannel = receiverChannel;
    }
}
