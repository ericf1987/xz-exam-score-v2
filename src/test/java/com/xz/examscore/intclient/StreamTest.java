package com.xz.examscore.intclient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/5/14.
 */
public class StreamTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    InterfaceAuthClient interfaceAuthClient;

    public static final String PROJECT_ID = "430100-b05a111c72c740f4898660a057c48e28";

    @Test
    public void getQuests() throws Exception {
        JSONArray jsonArray = interfaceAuthClient.queryQuestionByProject(PROJECT_ID);
        /*jsonArray.stream().forEachOrdered(o -> {
            JSONObject jo = (JSONObject) o;
            System.out.println(jo.getString("paperQuestNum"));
        });*/

        //查询题目中包含的科目
        String subjectIds = jsonArray.stream().map(o -> {
            JSONObject o1 = (JSONObject) o;
            return o1.getString("subjectId");
        }).distinct().collect(Collectors.joining(","));
        System.out.println(subjectIds);

        //查询题目中包含的答题卡科目
        String cardSubjectIds = jsonArray.stream().map(o -> {
            JSONObject o1 = (JSONObject) o;
            return o1.getString("cardSubjectId");
        }).distinct().collect(Collectors.joining("-"));
        System.out.println(cardSubjectIds);

        //根据科目查询题目结构
        Map<String, List<Object>> group1 = jsonArray.stream().collect(
                Collectors.groupingBy(o -> {
                    JSONObject o1 = (JSONObject) o;
                    return o1.getString("subjectId");
                })
        );


        List<Map<String, Object>> quests = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject o1 = (JSONObject)o;
            HashMap<String, Object> m = new HashMap<>();
            m.putAll(o1);
            quests.add(m);
        }

        //各科总分求和
        Map<String, Double> group2 = quests.stream().collect(
                Collectors.groupingBy(
                        quest -> MapUtils.getString(quest, "subjectId"),
                        Collectors.summingDouble(
                                quest -> MapUtils.getDouble(quest, "score")
                        )
                )
        );
        System.out.println(group2.toString());

        //每一科有多少道题
        Map<String, Long> group3 = quests.stream().collect(
                Collectors.groupingBy(
                        quest -> MapUtils.getString(quest, "subjectId"),
                        Collectors.counting()
                )
        );
        System.out.println(group3.toString());
    }

    //是否为客观题
    Predicate isObjective = o -> {
        JSONObject o1 = (JSONObject) o;
        return "o".equals(o1.getString("subObjTag"));
    };

    interface Function1<V, R>{
        R A(V v);
    }


    public class A implements BiFunction<Integer, Integer, String>{
        @Override
        public String apply(Integer integer, Integer integer2) {
            return String.valueOf("结果是:" + integer * integer2);
        }
    }

}
