package com.clinkworld.pay.entity;

import java.io.Serializable;

/**
 * Created by srh on 2015/11/13.
 */
public class OrderFlowInfo implements Serializable {
    /**
     * 订单时间类型
     */
    private int orderType;
    /**
     * 全部订单数
     */
    private int orderAllNumber;
    /**
     * 成交订单数
     */
    private int orderFinishNumber;
    /**
     * 未成交订单数
     */
    private int orderUnfinishNumber;
    /**
     * 总收入
     */
    private float money;
    /**
     * 现金收入
     */
    private float cashMoney;
    /**
     * 微信收入
     */
    private float weixinMoney;
    /**
     * 支付宝收入
     */
    private float zhifubaoMoney;


    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getOrderAllNumber() {
        return orderAllNumber;
    }

    public void setOrderAllNumber(int orderAllNumber) {
        this.orderAllNumber = orderAllNumber;
    }

    public int getOrderFinishNumber() {
        return orderFinishNumber;
    }

    public void setOrderFinishNumber(int orderFinishNumber) {
        this.orderFinishNumber = orderFinishNumber;
    }

    public int getOrderUnfinishNumber() {
        return orderUnfinishNumber;
    }

    public void setOrderUnfinishNumber(int orderUnfinishNumber) {
        this.orderUnfinishNumber = orderUnfinishNumber;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public float getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(float cashMoney) {
        this.cashMoney = cashMoney;
    }

    public float getWeixinMoney() {
        return weixinMoney;
    }

    public void setWeixinMoney(float weixinMoney) {
        this.weixinMoney = weixinMoney;
    }

    public float getZhifubaoMoney() {
        return zhifubaoMoney;
    }

    public void setZhifubaoMoney(float zhifubaoMoney) {
        this.zhifubaoMoney = zhifubaoMoney;
    }
}
