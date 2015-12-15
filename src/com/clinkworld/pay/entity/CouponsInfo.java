package com.clinkworld.pay.entity;

import java.io.Serializable;

/**
 * Created by srh on 2015/11/5.
 * <p/>
 * 优惠券实体
 */
public class CouponsInfo implements Serializable {
    /**
     * 批次id
     */
    private int id;
    /**
     * 平台id
     */
    private String merchantId;
    /**
     * 标题
     */
    private String title;
    /**
     * 礼券类型
     * 1：折扣
     * 2：面值
     */
    private int type;
    /**
     * 礼券面值
     */
    private float couponValue;
    /**
     * 最大优惠
     */
    private int max;
    /**
     * 最大优惠
     */
    private int useCodition;
    /**
     * 有效期开始时间
     */
    private String startTime;
    /**
     * 有效期结束时间
     */
    private String endTime;
    /**
     * 优惠券数量
     */
    private int quantity;
    /**
     * 已领取数量
     */
    private int sendQuantity;
    /**
     * 已使用数量
     */
    private int useQuantity;
    /**
     * 礼券状态
     * 1：正常
     * 0：停用
     */
    private int couponStatus;
    /**
     * 链接发放地址；二维码信息
     */
    private String pushUrl;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(float couponValue) {
        this.couponValue = couponValue;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getUseCodition() {
        return useCodition;
    }

    public void setUseCodition(int useCodition) {
        this.useCodition = useCodition;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getSendQuantity() {
        return sendQuantity;
    }

    public void setSendQuantity(int sendQuantity) {
        this.sendQuantity = sendQuantity;
    }

    public int getUseQuantity() {
        return useQuantity;
    }

    public void setUseQuantity(int useQuantity) {
        this.useQuantity = useQuantity;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public int getCouponStatus() {
        return couponStatus;
    }

    public void setCouponStatus(int couponStatus) {
        this.couponStatus = couponStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
