package com.chenchen.reggie.dto;

import com.chenchen.reggie.entity.Setmeal;
import com.chenchen.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

/**
 *  套餐实体类的继承方法，用于套餐实体类无法获取页面传送的数据，需要一些新的变量来接收
 *  Data：自动生成get，set等方法
 */
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
