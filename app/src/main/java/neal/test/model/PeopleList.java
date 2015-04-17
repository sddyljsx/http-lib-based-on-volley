package neal.test.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by neal on 2014/11/3.
 */
public class PeopleList {

    public static class Input{
        /**
         * 请求url
         */
        public static final String url="http://222.29.39.45/test.php";
        /**
         * post参数
         */
        public static Map<String, String> paramsMap;
        public static Map<String, String> getPostParams(int classNum){
            paramsMap=new HashMap<String, String>();
            paramsMap.put("classNum",Integer.toString(classNum));
            return paramsMap;
        }

    }

    public ArrayList<listItem> people=new ArrayList<listItem>();

    public static class listItem{
        public String firstName;
        public String lastName;
        public String email;

    }
}
