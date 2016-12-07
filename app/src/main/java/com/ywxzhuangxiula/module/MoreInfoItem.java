package com.ywxzhuangxiula.module;

public class MoreInfoItem {
	public String itemLable;
	public String itemValue;
	public int 	  itemType;
	
    public MoreInfoItem(String label, String value, int type) {
    	itemLable = label;
    	itemValue = value;
    	itemType = type;
    }
    

	public void SetItemValue(String value)
	{
		itemValue = value;
	}
	
	public String GetItemValue()
	{
		return itemValue;
	}
}
