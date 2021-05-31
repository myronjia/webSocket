package com.jkhl.entrance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateReport {

    private Title title = new Title();
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Title{
        private String text = "人员刷证进出统计表";
    }

    private Legend legend = new Legend();
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Legend {
        public String[] data = {"人数","次数"};
    }

    public XAxis xAxis = new XAxis();
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
     public class XAxis {
        private String type = "category";
        private boolean boundaryGap = false;
        private Object data;
    }


    public YAxis yAxis = new YAxis();
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class YAxis {
        private String type = "value";
    }

    public List<Serie> series = new ArrayList<>();
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Serie{
        private String name;
        private String type = "line";
        private String stack = "总量";
        private Object data;
    }



}
