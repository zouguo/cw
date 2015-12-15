package com.clinkworld.pay.entity;

import java.io.Serializable;

/**
 * Created by srh on 2015/11/1.
 * <p/>
 * 订单实例
 */
public class OrderInfo implements Serializable {
    /**
     * 订单编号
     */
    private String orderNumber;
    /**
     * POS机号
     */
    private String posNumber;
    /**
     * 订单金额
     */
    private float orderMoney;
    /**
     * 订单状态
     * 1：待付款
     * 2：待发货
     * 。。。
     * 9：退货完成
     */
    private int status;
    /**
     * 订单生成时间
     */
    private String orderCreateTime;
    /**
     * 订单完成时间
     */
    private String orderCompleteTime;
    /**
     * 收款时间
     */
    private String orderGatheringTime;
    /**
     * 收款方式
     * 0：现金支付
     * 2：支付宝支付
     * 1：微信支付
     */
    private int gatheringType;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPosNumber() {
        return posNumber;
    }

    public void setPosNumber(String posNumber) {
        this.posNumber = posNumber;
    }

    public float getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(float orderMoney) {
        this.orderMoney = orderMoney;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getOrderCompleteTime() {
        return orderCompleteTime;
    }

    public void setOrderCompleteTime(String orderCompleteTime) {
        this.orderCompleteTime = orderCompleteTime;
    }

    public int getGatheringType() {
        return gatheringType;
    }

    public void setGatheringType(int gatheringType) {
        this.gatheringType = gatheringType;
    }

    public String getOrderGatheringTime() {
        return orderGatheringTime;
    }

    public void setOrderGatheringTime(String orderGatheringTime) {
        this.orderGatheringTime = orderGatheringTime;
    }
}
