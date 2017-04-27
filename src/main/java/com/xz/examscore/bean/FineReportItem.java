package com.xz.examscore.bean;

/**
 * @author by fengye on 2017/4/26.
 */
public class FineReportItem {
    private String itemId;

    private String itemUrl;

    private String itemName;

    private String itemType;

    private int position;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "FineReportItem{" +
                "itemId='" + itemId + '\'' +
                ", itemUrl='" + itemUrl + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemType='" + itemType + '\'' +
                ", position=" + position +
                '}';
    }
}
