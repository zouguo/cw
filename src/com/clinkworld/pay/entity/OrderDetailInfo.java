package com.clinkworld.pay.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by srh on 2015/11/15.
 */
public class OrderDetailInfo implements Serializable {
    /**
     * 订单编号
     */
    private String orderId;
    /**
     * 订单金额
     */
    private float orderMoney;
    /**
     * 支付方式
     */
    private int payChannelId;
    /**
     * 会员手机号
     */
    private String memberTelphone;
    /**
     * 优惠券编号
     */
    private String couponsKey;
    /**
     * 优惠券名称/标题
     */
    private String couponsTitle;
    /**
     * 优惠券折扣描述
     */
    private String couponsDiscountDescription;
    /**
     * 优惠券抵扣金额
     */
    private float couponsMoney;

    /**
     * 应收金额
     */
    private float shouldReceiveMoney;
    /**
     * 实收金额
     */
    private float realReceiveMoney;
    /**
     * 支付状态
     */
    private int payStatus;
    /**
     * 订单商品列表
     */
    private List<StorageDetailInfo> storageDetailInfoList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public float getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(float orderMoney) {
        this.orderMoney = orderMoney;
    }

    public int getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(int payChannelId) {
        this.payChannelId = payChannelId;
    }

    public String getMemberTelphone() {
        return memberTelphone;
    }

    public void setMemberTelphone(String memberTelphone) {
        this.memberTelphone = memberTelphone;
    }

    public String getCouponsTitle() {
        return couponsTitle;
    }

    public void setCouponsTitle(String couponsTitle) {
        this.couponsTitle = couponsTitle;
    }

    public String getCouponsDiscountDescription() {
        return couponsDiscountDescription;
    }

    public void setCouponsDiscountDescription(String couponsDiscountDescription) {
        this.couponsDiscountDescription = couponsDiscountDescription;
    }

    public float getShouldReceiveMoney() {
        return shouldReceiveMoney;
    }

    public void setShouldReceiveMoney(float shouldReceiveMoney) {
        this.shouldReceiveMoney = shouldReceiveMoney;
    }

    public float getRealReceiveMoney() {
        return realReceiveMoney;
    }

    public void setRealReceiveMoney(float realReceiveMoney) {
        this.realReceiveMoney = realReceiveMoney;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public List<StorageDetailInfo> getStorageDetailInfoList() {
        return storageDetailInfoList;
    }

    public void setStorageDetailInfoList(List<StorageDetailInfo> storageDetailInfoList) {
        this.storageDetailInfoList = storageDetailInfoList;
    }

    public String getCouponsKey() {
        return couponsKey;
    }

    public void setCouponsKey(String couponsKey) {
        this.couponsKey = couponsKey;
    }

    public float getCouponsMoney() {
        return couponsMoney;
    }

    public void setCouponsMoney(float couponsMoney) {
        this.couponsMoney = couponsMoney;
    }
}
