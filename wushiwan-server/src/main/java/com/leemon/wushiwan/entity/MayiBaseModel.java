package com.leemon.wushiwan.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MayiBaseModel<T>  implements Serializable {
    private Integer pageNo;
    private List<T> list;
}
