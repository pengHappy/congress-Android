package other;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by coffee on 2016/11/21.
 */
public class JsonSort {

    public static JSONArray sort(JSONArray jsonArray, Comparator c) {
        List asList = new ArrayList(jsonArray.length());
        for(int i = 0; i < jsonArray.length(); i++) {
            asList.add(jsonArray.opt(i));
        }
        Collections.sort(asList, c);
        JSONArray res = new JSONArray();
        for(Object o : asList) {
            res.put(o);
        }
        return res;
    }

}
