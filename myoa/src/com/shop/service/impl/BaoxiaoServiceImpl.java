package com.shop.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.BaoxiaobillMapper;
import com.shop.pojo.Baoxiaobill;
import com.shop.pojo.BaoxiaobillExample;
import com.shop.service.BaoxiaoService;

@Service
public class BaoxiaoServiceImpl implements BaoxiaoService {

	@Autowired
	private BaoxiaobillMapper baoxiaoBillMapper;

	@Override
	public void saveBaoxiao(Baoxiaobill baoxiaobill) {
		/** 新增保存 */
		if (baoxiaobill.getId() == null) {
			// 从Session中获取当前用户对象，将LeaveBill对象中user与Session中获取的用户对象进行关联
			// leaveBill.setUser(SessionContext.get());//建立管理关系
			// 保存请假单表，添加一条数据
			baoxiaoBillMapper.insert(baoxiaobill);
		}
		/** 更新保存 */
		else {
			// 执行update的操作，完成更新
			baoxiaoBillMapper.updateByPrimaryKey(baoxiaobill);
		}
	}

	@Override
	public List<Baoxiaobill> findLeaveBillListByUser(Long id) {
		BaoxiaobillExample example = new BaoxiaobillExample();
		BaoxiaobillExample.Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(id.intValue());
		return baoxiaoBillMapper.selectByExample(example);
	}

	// 根据id删除请假单信息
	@Override
	public void deleteBaoxiaobill(int id) {
		this.baoxiaoBillMapper.deleteByPrimaryKey(id);
	}

}
