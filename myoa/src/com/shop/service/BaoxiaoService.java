package com.shop.service;

import java.util.List;

import com.shop.pojo.Baoxiaobill;

public interface BaoxiaoService {

	void saveBaoxiao(Baoxiaobill baoxiaobill);

	List<Baoxiaobill> findLeaveBillListByUser(Long id);

	void deleteBaoxiaobill(int id);

}
