import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class KV {

    private static ArrayList<KV> list;
    private String k;
    private String v;

    KV(String k, String v) {
        this.k = k;
        this.v = v;
    }

    static String getLetter(String k) {
        List<String> strings = new ArrayList<>();
        for (KV kv : list) {
            if (kv.k.equals(k)) {
                strings.add(kv.v);
            }
        }
        if (strings.isEmpty()) {
            return "未识别" + k;
        } else if (strings.size() != 1) {
            StringBuilder base = new StringBuilder();
            for (String str : strings) {
                base.append(str).append("_");
            }
            base = base.deleteCharAt(base.length() - 1);
            return base.toString();
        } else {
            return strings.get(0);
        }
    }

    static {
        try {
            List<String> lineList = FileUtils.readLines(new File("dir.txt"));
            list = new ArrayList<>(lineList.size());
            for (String line : lineList) {
                String[] strings = line.split(" ");
                KV kv = new KV(strings[1], strings[0]);
                list.add(kv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}