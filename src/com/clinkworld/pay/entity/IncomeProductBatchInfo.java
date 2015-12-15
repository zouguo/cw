package com.clinkworld.pay.entity;

import java.io.Serializable;

/**
 * Created by shirenhua on 2015/10/14.
 * <p/>
 * 入库单信息
 */
public class IncomeProductBatchInfo implements Serializable {

    /**
     * 入库单号
     */
    private String id;
    /**
     * 商品号
     */
    private String merchantId;
    /**
     * 商场id
     */
    private String StoreId;
    /**
     * 单号
     */
    private String recordId;

    /**
     * 入库时间
     */
    private String date;

    /**
     * 入库商品总成本
     */
    private float moneryIn;

    /**
     * 入库商品总值
     */
    private float moneryOut;
    /**
     * 入库单用户id
     */
    private String createUserId;

    /**
     * 毛利
     */
    private float grossProfit;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getMoneryIn() {
        return moneryIn;
    }

    public void setMoneryIn(float moneryIn) {
        this.moneryIn = moneryIn;
    }

    public float getMoneryOut() {
        return moneryOut;
    }

    public void setMoneryOut(float moneryOut) {
        this.moneryOut = moneryOut;
    }

    public float getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(float grossProfit) {
        this.grossProfit = grossProfit;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getStoreId() {
        return StoreId;
    }

    public void setStoreId(String storeId) {
        StoreId = storeId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
}
