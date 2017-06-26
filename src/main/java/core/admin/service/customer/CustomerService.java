package core.admin.service.customer;

import core.admin.service.base.DataTableService;
import core.model.Customer;

public interface CustomerService extends DataTableService {
	Customer findByKfAccount(String kfAccount);

	long countByKfAccount(String kfAccount);
}
