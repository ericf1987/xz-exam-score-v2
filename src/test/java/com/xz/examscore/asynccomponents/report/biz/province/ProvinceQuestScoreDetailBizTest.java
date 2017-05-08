package com.xz.examscore.asynccomponents.report.biz.province;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/2/20.
 */
public class ProvinceQuestScoreDetailBizTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProvinceQuestScoreDetailBiz provinceQuestScoreDetailBiz;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "FAKE_PROJ_1486524671547_0")
                .setParameter("subjectId", "001");
        long begin = System.currentTimeMillis();
        Result result = provinceQuestScoreDetailBiz.execute(param);
        System.out.println(result.getData());
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - begin));
    }
}