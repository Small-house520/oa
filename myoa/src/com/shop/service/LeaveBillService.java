package com.shop.service;

import com.shop.pojo.Leavebill;

public interface LeaveBillService {
	void saveLeaveBill(Leavebill leaveBill);

	void deleteLeavebill(Long id);
}
