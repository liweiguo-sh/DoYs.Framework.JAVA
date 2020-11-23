package com.doys.thirdparty.hualala.util;

public class BaseVo {
    private Long shopID;
    private Long groupID;

    public void setGroupID(Long GROUP_ID) {
        this.groupID = GROUP_ID;
    }
    public void setShopID(Long SHOP_ID) {
        this.shopID = SHOP_ID;
    }

    public Long getGroupID() {
        return this.groupID;
    }
    public Long getShopID() {
        return this.shopID;
    }
}