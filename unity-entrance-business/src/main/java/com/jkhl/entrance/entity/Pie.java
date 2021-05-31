package com.jkhl.entrance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pie {

    private List<String> legendData;
    private List<Series> seriesData;

}
