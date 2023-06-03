package com.chenchen.reggie.dto;

import com.chenchen.reggie.entity.Dish;
import com.chenchen.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 菜品实体类的继承方法，用于菜品实体类无法获取页面传送的数据，需要一些新的变量来接收
 Data：自动生成get，set等方法
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
