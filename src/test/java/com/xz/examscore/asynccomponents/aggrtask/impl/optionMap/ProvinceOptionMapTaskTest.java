package com.xz.examscore.asynccomponents.aggrtask.impl.optionMap;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ProvinceService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/2/3.
 */
public class ProvinceOptionMapTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProvinceOptionMapTask provinceOptionMapTask;

    @Autowired
    ProvinceService provinceService;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "FAKE_PROJ_1486107115530_0";
        String province = provinceService.getProjectProvince(projectId);
        Range provinceRange = Range.province(province);
        provinceOptionMapTask.runTask(new AggrTaskMessage(projectId, "187686", "province_option_map")
                .setRange(provinceRange).setTarget(Target.quest("FAKE_PROJ_1486107115530_0:005:19")));
    }
}