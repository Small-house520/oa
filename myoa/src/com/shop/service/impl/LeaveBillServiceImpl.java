package com.shop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.LeavebillMapper;
import com.shop.pojo.Leavebill;
import com.shop.service.LeaveBillService;

@Service
public class LeaveBillServiceImpl implements LeaveBillService {

	@Autowired
	private LeavebillMapper leaveBillMapper;

	// 保存请假单
	@Override
	public void saveLeaveBill(Leavebill leaveBill) {
		this.leaveBillMapper.insert(leaveBill);

	}

	// 根据id删除请假单信息
	@Override
	public void deleteLeavebill(Long id) {
		this.leaveBillMapper.deleteByPrimaryKey(id);
	}

}
