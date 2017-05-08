package com.xz.examscore.intclient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.aliyun.Api;
import com.xz.ajiaedu.common.appauth.AppAuthClient;
import com.xz.ajiaedu.common.lang.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * @author by fengye on 2017/5/8.
 */
@Service
public class InterfaceAuthClient {

    @Autowired
    AppAuthClient appAuthClient;

    /**
     * 查询知识点接口
     *
     * @param pointId 知识点ID
     * @return
     */
    public JSONObject queryKnowledgePointById(String pointId) {
        Result result = appAuthClient.callApi(
                Api.ApiName.QueryKnowledgePointById.name(),
                new Param().setParameter("pointId", pointId)
        );

        return result.get("point");
    }

    /**
     * 查询试题接口
     *
     * @param projectId 考试项目ID
     * @return
     */
    public JSONArray queryQuestionByProject(String projectId) {
        Result result = appAuthClient.callApi(
                Api.ApiName.QueryQuestionByProject.name(),
                new Param().setParameter("projectId", projectId)
        );
        return result.get("quests");
    }

    /**
     * 查询选做题接口
     *
     * @param projectId 考试项目ID
     * @param optional  是否可选
     * @return
     */
    public Map<String, Object> queryQuestionByProject(String projectId, boolean optional) {
        Result result = appAuthClient.callApi(
                Api.ApiName.QueryQuestionByProject.name(),
                new Param().setParameter("projectId", projectId)
        );
        return optional ? result.get("optionalGroups") : Collections.emptyMap();
    }

    /**
     * 查询学校接口
     *
     * @param projectId        考试项目ID
     * @param needStudentCount 是否查询学生数
     * @return
     */
    public JSONArray queryExamSchoolByProject(String projectId, boolean needStudentCount) {
        Result result = appAuthClient.callApi(
                Api.ApiName.QueryExamSchoolByProject.name(),
                new Param().setParameter("projectId", projectId).setParameter("needStudentCount", needStudentCount)
        );
        return result.get("schools");
    }

    /**
     * 查询班级接口
     *
     * @param projectId        考试项目ID
     * @param schoolId         学校ID
     * @param needStudentCount 是否查询学生数
     * @return
     */
    public JSONArray queryExamClassByProject(String projectId, String schoolId, boolean needStudentCount) {
        Result result = appAuthClient.callApi(
                Api.ApiName.QueryExamClassByProject.name(),
                new Param().setParameter("projectId", projectId)
                        .setParameter("schoolId", schoolId)
                        .setParameter("needStudentCount", needStudentCount)
        );
        return result.get("classes");
    }

    /**
     * 查询班级考生
     *
     * @param projectId 考试项目ID
     * @param classId   学校ID
     * @return
     */
    public JSONArray queryClassExamStudent(String projectId, String classId) {
        Result result = appAuthClient.callApi(
                Api.ApiName.QueryClassExamStudent.name(),
                new Param().setParameter("projectId", projectId)
                        .setParameter("classId", classId)
        );
        return result.get("examStudents");
    }

    /**
     * 查询考试科目
     *
     * @param projectId 考试项目ID
     * @return
     */
    public JSONArray querySubjectListByProjectId(String projectId) {
        Result result = appAuthClient.callApi(
                Api.ApiName.QuerySubjectListByProjectId.name(),
                new Param().setParameter("projectId", projectId)
        );

        return result.isSuccess() ? result.get("result") : null;
    }

    /**
     * 查询考试信息
     *
     * @param projectId 考试项目ID
     * @return
     */
    public JSONObject queryProjectById(String projectId) {
        Result result = appAuthClient.callApi(
                Api.ApiName.QueryProjectById.name(),
                new Param().setParameter("projectId", projectId)
        );

        return result.isSuccess() ? result.get("result") : null;
    }

    /**
     * 通知CMS接口导入成绩
     *
     * @param ossPath OSS路径
     */
    public void importExamScoreFromOSS(String ossPath) {
        appAuthClient.callApi(
                Api.ApiName.ImportExamScoreFromOSS.name(),
                new Param().setParameter("ossPath", ossPath)
        );
    }

    /**
     * 云报表反馈信息
     *
     * @param param 反馈参数
     */
    public void addRpFeedbackInfo(Param param) {
        appAuthClient.callApi(
                Api.ApiName.AddRpFeedbackInfo.name(),
                param
        );
    }

    /**
     * 发布成绩
     *
     * @param projectId 考试项目ID
     */
    public void releaseExamScore(String projectId) {
        appAuthClient.callApi(
                Api.ApiName.ReleaseExamScore.name(),
                new Param().setParameter("projectId", projectId)
        );
    }

    /**
     * 申请免费启用服务
     *
     * @param param 参数
     */
    public void addRpApplyOpen(Param param) {
        appAuthClient.callApi(
                Api.ApiName.AddRpApplyOpen.name(),
                param
        );
    }

    /**
     * 查询考试配置参数接口
     *
     * @param projectId 考试项目ID
     * @return
     */
    public Result queryProjectReportConfig(String projectId) {
        return appAuthClient.callApi(
                Api.ApiName.QueryProjectReportConfig.name(),
                new Param().setParameter("projectId", projectId)
        );
    }

    /**
     * 设置考试配置参数
     *
     * @param param 配置参数
     * @return
     */
    public Result setProjectConfig(Param param) {
        return appAuthClient.callApi(
                Api.ApiName.QueryProjectReportConfig.name(),
                param
        );
    }
}
