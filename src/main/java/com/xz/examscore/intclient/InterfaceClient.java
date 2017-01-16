package com.xz.examscore.intclient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.aliyun.Api.ApiName;
import com.xz.ajiaedu.common.aliyun.ApiClient;
import com.xz.ajiaedu.common.aliyun.ApiRequest;
import com.xz.ajiaedu.common.aliyun.ApiResponse;
import com.xz.examscore.api.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class InterfaceClient {

    private final ApiClient apiClient;

    @Autowired
    public InterfaceClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public JSONObject queryKnowledgePointById(String pointId) {
        ApiResponse apiResponse = apiClient.call(
                new ApiRequest(ApiName.QueryKnowledgePointById).setParameter("pointId", pointId));

        return apiResponse.get("point");
    }

    public JSONArray queryQuestionByProject(String projectId) {
        ApiResponse apiResponse = apiClient.call(
                new ApiRequest(ApiName.QueryQuestionByProject).setParameter("projectId", projectId));

        return apiResponse.get("quests");
    }

    public Map<String, Object> queryQuestionByProject(String projectId, boolean optional) {
        ApiResponse apiResponse = apiClient.call(
                new ApiRequest(ApiName.QueryQuestionByProject).setParameter("projectId", projectId));

        return optional ? apiResponse.get("optionalGroups") : Collections.emptyMap();
    }

    public JSONArray queryExamSchoolByProject(String projectId, boolean needStudentCount) {
        ApiResponse apiResponse = apiClient.call(
                new ApiRequest(ApiName.QueryExamSchoolByProject)
                        .setParameter("projectId", projectId).setParameter("needStudentCount", needStudentCount));

        return apiResponse.get("schools");
    }

    public JSONArray queryExamClassByProject(String projectId, String schoolId, boolean needStudentCount) {
        ApiResponse apiResponse = apiClient.call(new ApiRequest(ApiName.QueryExamClassByProject)
                .setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId)
                .setParameter("needStudentCount", needStudentCount)
        );

        return apiResponse.get("classes");
    }

    public JSONArray queryClassExamStudent(String projectId, String classId) {
        ApiResponse apiResponse = apiClient.call(new ApiRequest(ApiName.QueryClassExamStudent)
                .setParameter("projectId", projectId)
                .setParameter("classId", classId)
        );

        return apiResponse.get("examStudents");
    }

    public JSONArray querySubjectListByProjectId(String projectId) {
        ApiResponse apiResponse = apiClient.call(new ApiRequest(ApiName.QuerySubjectListByProjectId)
                .setParameter("projectId", projectId)
        );

        return apiResponse.isSuccess() ? apiResponse.get("result") : null;
    }

    public JSONObject queryProjectById(String projectId) {
        ApiResponse apiResponse = apiClient.call(
                new ApiRequest(ApiName.QueryProjectById).setParameter("projectId", projectId));

        return apiResponse.isSuccess() ? apiResponse.get("result") : null;
    }

    public void importExamScoreFromOSS(String ossPath) {
        apiClient.call(new ApiRequest(ApiName.ImportExamScoreFromOSS).setParameter("ossPath", ossPath));
    }

    public void addRpFeedbackInfo(Param param) {
        ApiRequest apiRequest = new ApiRequest(ApiName.AddRpFeedbackInfo);
        apiRequest.setParameters(param.getParameters());
        apiClient.call(apiRequest);
    }

    public void releaseExamScore(String projectId) {
        ApiRequest apiRequest = new ApiRequest(ApiName.ReleaseExamScore).setParameter("projectId", projectId);
        apiClient.call(apiRequest);
    }

    public ApiResponse queryProjectReportConfig(String projectId) {
        return apiClient.call(new ApiRequest(ApiName.QueryProjectReportConfig)
                .setParameter("projectId", projectId)
        );
    }

    public ApiResponse setProjectConfig(Param param) {
        ApiRequest apiRequest = new ApiRequest(ApiName.SetProjectConfig);
        apiRequest.setParameters(param.getParameters());
        return apiClient.call(apiRequest);
    }

    public void addRpApplyOpen(Param param) {
        ApiRequest apiRequest = new ApiRequest(ApiName.AddRpApplyOpen);
        apiRequest.setParameters(param.getParameters());
        apiClient.call(apiRequest);
    }

    public ApiResponse listRpApplyOpen(Param param) {
        ApiRequest apiRequest = new ApiRequest(ApiName.ListRpApplyOpens);
        apiRequest.setParameters(param.getParameters());
        return apiClient.call(apiRequest);
    }

}
