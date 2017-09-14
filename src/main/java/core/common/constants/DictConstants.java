package core.common.constants;

import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictConstants {
    private static Log log = Log.getLog(DictConstants.class);
    private static Map<String, Map> baseMap = new HashMap<>();

    private static void load() {
        String sql = "SELECT FIELD,`CODE`,`NAME` FROM t_dict_code";
        List<Record> list = Db.find(sql);
        for (Record dic : list) {
            String field = dic.getStr("FIELD");
            String code = dic.getInt("CODE")+"";
            String name = dic.getStr("NAME");
            log.info("正在加载字典[FIELD:"+field+"\tCODE:"+code+"\tNAME:"+name+"]");
            Map<String, String> m = baseMap.get(field);
            if (m != null) {
                m.putAll(Kv.by(code, name));
                baseMap.put(field, m);
            }else{
                baseMap.put(field, Kv.by(code, name));
            }

        }
    }

    public static String getName(String field,String key){
        try {
            if(baseMap.size()==0){
                load();
            }
            String value = baseMap.get(field).get(key).toString();
            return value;
        }catch (Exception e){
            return null;
        }
    }

}
