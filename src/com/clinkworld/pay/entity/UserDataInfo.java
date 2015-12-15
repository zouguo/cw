package com.clinkworld.pay.entity;

import java.io.Serializable;

/**
 * Created by srh on 2015/10/19.
 * <p/>
 * 用户数据实体
 */
public class UserDataInfo implements Serializable {
    /**
     * 真实姓名
     */
    private String trueName;
    /**
     * POS机编号
     */
    private String POSId;

    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 平台logo
     */
    private String platformLogo;

    /**
     * 平台号
     */
    private String platfromId;

    /**
     * 公司编号
     */
    private String companyId;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别 男：1  女：0
     */
    private int sex;

    /**
     * 用户工号
     */
    private String workId;

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getPlatformLogo() {
        return platformLogo;
    }

    public void setPlatformLogo(String platformLogo) {
        this.platformLogo = platformLogo;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getPlatfromId() {
        return platfromId;
    }

    public void setPlatfromId(String platfromId) {
        this.platfromId = platfromId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPOSId() {
        return POSId;
    }

    public void setPOSId(String POSId) {
        this.POSId = POSId;
    }
}
