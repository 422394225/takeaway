package core.admin.service.audit.impl;

import java.util.List;

import core.admin.service.audit.AuditService;
import core.model.Audit;

public class AuditServiceImpl implements AuditService {

	@Override
	public List<Audit> getByShopId(String shopId) {
		String sql = "SELECT * FROM t_audit WHERE SHOP_ID = ?";
		List<Audit> list = Audit.dao.find(sql, shopId);
		return list;
	}

}
