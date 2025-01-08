package com.driverspa.model;

import java.util.List;

/**
 * Created by Yerzhan Tanatov on 27/09/16.
 */
public class Statistic {

    String title;
    List<StatisticDetail> statisticsList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<StatisticDetail> getStatisticsList() {
        return statisticsList;
    }

    public void setStatisticsList(List<StatisticDetail> statisticsList) {
        this.statisticsList = statisticsList;
    }

    public static class StatisticDetail{
        String itemName;
        String itemValue;

        public StatisticDetail(String itemName, String itemValue) {
            this.itemName = itemName;
            this.itemValue = itemValue;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }
    }
}
