package core.common.constants;

import java.util.HashMap;
import java.util.Map;

public class ShopConstants {
    private static Map<String,String> map = new HashMap<>();

    public static void load(){
        map.put("hotShop","热门店铺");
        map.put("neighbor","附近店铺");
        map.put("collections","我的收藏");
    }

    public static Map<String,String> get(){
        return map;
    }

    public static String getValue(String key){
        return map.get(key);
    }
}
