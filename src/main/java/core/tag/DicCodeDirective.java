/**
 * Created At 2017年7月10日下午10:45:05 
 * 
 */

package core.tag;

import com.jfinal.plugin.activerecord.Db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.model.DictCode;
import freemarker.core.Environment;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月10日下午10:45:05
 */

public class DicCodeDirective implements TemplateDirectiveModel {

	@Override
	@SuppressWarnings("deprecation")
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		List<DictCode> list = new ArrayList<>();
		DictCode dictCode = new DictCode();
		list.add(dictCode);
		String field = String.valueOf(params.get("field"));
		String code = String.valueOf(params.get("code"));
		if (field != "null") {
			Map<String, Object> map = new HashMap<>();
			if (field.startsWith("t_") || field.startsWith("T_")) {
				map.put("field", field);
			} else {
				map.put("field", "t_" + field);
			}
			if (code != "null") {
				String[] codes = code.split(",");
				map.put("codes", code);
				if (codes.length <= 1) {
					dictCode = DictCode.dao.findFirst(Db.getSqlPara("dictCode.name", map));
					env.setVariable("dc", DefaultObjectWrapper.DEFAULT_WRAPPER.wrap(dictCode));
				} else {
					list = DictCode.dao.find(Db.getSqlPara("dictCode.name", map));
					env.setVariable("dc", DefaultObjectWrapper.DEFAULT_WRAPPER.wrap(list));
				}
			} else {
				list = DictCode.dao.find(Db.getSqlPara("dictCode.name", map));
				env.setVariable("dc", DefaultObjectWrapper.DEFAULT_WRAPPER.wrap(list));
			}
		}
		body.render(env.getOut());
	}

}
