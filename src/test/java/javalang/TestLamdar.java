package javalang;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.config.FileConfiguration;
import com.xz.ajiaedu.common.cryption.HexStringConverter;
import com.xz.ajiaedu.common.io.FileUtils;
import com.xz.examscore.util.DoubleUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/6/3.
 */
public class TestLamdar {
    public void test1() {
        Param param = new Param().setParameter("date", new Date())
                .setParameter("name", "fengye").setParameter("age", 20)
                .setParameter("hobbies", new String[]{"basket", "music", "programming"});
        System.out.println(param.getParameters().toString());
        Arrays.asList(param.getStringValues("hobbies")).forEach(p ->
                System.out.println(p.toString())
        );
    }

    public void testFileConfig() {
        try {
            FileConfiguration fc = new FileConfiguration("F://1.json");
            System.out.println(fc.getString("project"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testHexToString() {
        try {
            String hex = HexStringConverter.stringToHex("123");
            System.out.println("123--" + "stringToHex-->" + hex);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void testFileUtils() {
        String path = "F:\\mongoData\\db\\dbConf\\mongodb.log.2016-05-19T16-06-56";
        System.out.println(new File(path).getAbsolutePath());
        System.out.println(new File(path).getParent());
        System.out.println(FileUtils.getFileName(path));
        File file = new File("F://abc//content.txt");
        try {
            FileUtils.writeFile("狼王加内特退役", file, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] subjects = new String[]{"001", "002", "003"};
        for (String subject : subjects) {
            for (int i = 1; i < 4; i++) {
                Map<String, Object> map = new HashMap<>();
                map.put("subjectId", subject);
                map.put("score", 100 * i);
                list.add(map);
            }
        }
        System.out.println(list.toString());
        Map mm = list.stream()
                .collect(Collectors.groupingBy(foo -> foo.get("subjectId"), Collectors.summingDouble(f -> Double.parseDouble(f.get("score").toString()))));
        System.out.println(mm.toString());
    }

    public static void main(String[] args) {
        new TestLamdar().test();
        List<String> subjectList = new ArrayList<>();
        subjectList.add("001");
        subjectList.add("002");
        subjectList.add("003");
        subjectList.add("004005006");
        subjectList.add("007008009");
        List<String> combinedSubject = subjectList.stream().filter(subject -> subject.length() != 3).collect(Collectors.toList());
        System.out.println(combinedSubject.toString());
        String s1 = "004005006";
        String s2 = "400";
        System.out.println(s1.contains(s2));
    }
}
