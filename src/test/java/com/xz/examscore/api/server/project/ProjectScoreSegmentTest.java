package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/7/11.
 */
public class ProjectScoreSegmentTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectScoreSegment projectScoreSegment;

    public static final String PROJECT_ID = "430200-ceb62b9fa81f47e480731d1f70e57509";

    public static final String SUBJECT_ID = "001";

    public static final String[] SCHOOL_IDS = new String[]{
            "c910da64-ea8e-4388-b438-ff7a24fff441",
            "1cf8f145-157d-4c3b-86cf-d8b52880ace2",
            "528654bb-3529-4ef2-9d71-5870d3f55d49",
            "823b1f0a-a263-4f72-bf51-760a93ea17c9"
    };

    @Test
    public void testExecute() throws Exception {
        Result result = projectScoreSegment.execute(new Param()
                .setParameter("projectId", PROJECT_ID)
                .setParameter("subjectId", SUBJECT_ID)
                .setParameter("schoolIds", SCHOOL_IDS)
        );

        System.out.println(result.getData().toString());
    }
}