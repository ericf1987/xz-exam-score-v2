var generateTotalScore = function (outputCollection, QUERY, RANGE) {

    var SCOPE = {query: QUERY, range: RANGE};

    db.runCommand({
        mapReduce: "score",
        query: QUERY,
        scope: {SCOPE: SCOPE},
        map: function () {
            var key = {};

            for (var pq in SCOPE.query) {
                if (SCOPE.query.hasOwnProperty(pq)) {
                    key[pq] = SCOPE.query[pq];
                }
            }

            var rangeId = SCOPE.range == 'area' ? this.areaId :
                (SCOPE.range == 'city' ? (this.areaId.substring(0, 4) + "00") :
                    (SCOPE.range == 'province' ? (this.areaId.substring(0, 2) + "0000") : this[SCOPE.range]));

            key[SCOPE.range] = rangeId;

            emit(key, {totalScore: this.score});
        },
        reduce: function (key, values) {
            var totalScore = 0;
            values.forEach(function (value) {
                totalScore += value.totalScore;
            });
            return {totalScore: totalScore};
        },
        finalize: function (key, reducedResult) {
            return reducedResult;
        },
        out: {
            merge: outputCollection,
            sharded: true
        }
    });
};

var iterateRanges = function (f) {
    var RANGES = ["classId", "schoolId", "area", "city", "province"];
    RANGES.forEach(f);
};

// 统计班校区市省的项目科目题目总分
var generateAllTotalScores = function (projectId, totalEnabled, subjectEnabled, questEnabled) {
    iterateRanges(function (rangeName) {

        if (totalEnabled) {
            generateTotalScore("total_score_project", {projectId: projectId}, rangeName);
        }

        if (subjectEnabled) {
            iterateSubjects(projectId, function (s) {
                var query = {projectId: projectId, subjectId: s};
                generateTotalScore("total_score_subject", query, rangeName);
            });
        }

        if (questEnabled) {
            iterateQuests(projectId, function (s, q) {
                var query = {projectId: projectId, subjectId: s, questNo: q};
                generateTotalScore("total_score_quest", query, rangeName);
            });
        }

        print("range '" + rangeName + "' finished.");
    });
};

var queryTotalScore = function (query) {
    var result = db.total_score_project.findOne({_id: query});
    if (result != null) {
        return result.value.totalScore || 0;
    }
};

var generateAllAvgs = function (projectId) {
    iterateRanges(function (rangeName) {
        var studentCount = queryStudentCount(projectId, "000", rangeName);

        if (studentCount == 0) {
            print("ERROR: project student count is 0 - " + projectId + ":" + rangeName);
            return;
        }


    });
};
