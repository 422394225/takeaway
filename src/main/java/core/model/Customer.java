package core.model;

import com.jfinal.plugin.activerecord.Model;

public class Customer extends Model<Customer> {
	/** 如何描述serialVersionUID */
	private static final long serialVersionUID = 3221380422226497741L;
	public static final Customer dao = new Customer();
}
