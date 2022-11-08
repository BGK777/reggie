package com.BGK.reggie.dto;

import com.BGK.reggie.pojo.Setmeal;
import com.BGK.reggie.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
