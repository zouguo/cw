package com.clinkworld.pay.entity;

import java.io.Serializable;

/**
 * Created by srh on 2015/10/30.
 * <p/>
 * 入库单详情
 */
public class StorageDetailInfo implements Serializable {
    /**
     * 唯一键
     */
    private String id;
    /**
     * 入库单号
     */
    private String recordId;

    /**
     * 商品条形码
     */
    private String productBarcode;
    /**
     * 商品名称
     */
    private String productProductName;
    /**
     * 成本价
     */
    private float productPriceIn;
    /**
     * 售价
     */
    private float productPriceOut;
    /**
     * 商品数量
     */
    private int productNumber;
    /**
     * 商品入库时间
     */
    private String addTime;
    /**
     * 商品图片地址
     */
    private String imageUrl;


    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public String getProductProductName() {
        return productProductName;
    }

    public void setProductProductName(String productProductName) {
        this.productProductName = productProductName;
    }

    public float getProductPriceIn() {
        return productPriceIn;
    }

    public void setProductPriceIn(float productPriceIn) {
        this.productPriceIn = productPriceIn;
    }

    public float getProductPriceOut() {
        return productPriceOut;
    }

    public void setProductPriceOut(float productPriceOut) {
        this.productPriceOut = productPriceOut;
    }

    public int getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(int productNumber) {
        this.productNumber = productNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
