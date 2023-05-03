package com.wjl.gmall.acitvity.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderRecode implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;

	private SeckillGoods seckillGoods;

	private Integer num;

	private String orderStr;
}
