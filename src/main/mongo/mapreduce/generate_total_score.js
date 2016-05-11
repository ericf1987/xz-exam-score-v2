var mapFunction = function () {
    var iterateRanges = function (f) {
        var RANGES = ["student", "class", "school", "area", "city", "province"];
        RANGES.forEach(f);
    };
    var iterateTargets = function (f) {
        var TARGETS = ["quest", "subject", "project"];
        TARGETS.forEach(f);
    };
    var getRangeId = function (rangeName, obj) {
        if (rangeName == 'province') {
            return obj.areaId.substring(0, 2) + "0000";
        } else if (rangeName == 'city') {
            return obj.areaId.substring(0, 4) + "00";
        } else if (rangeName == 'area') {
            return obj.areaId;
        } else if (rangeName == 'school') {
            return obj.schoolId;
        } else if (rangeName == 'class') {
            return obj.classId;
        } else if (rangeName == 'student') {
            return obj.studentId;
        }
    };
    var getTargetId = function (targetName, obj) {
        if (targetName == 'project') {
            return obj.projectId;
        } else if (targetName == 'subject') {
            return obj.subjectId;
        } else if (targetName == 'quest') {
            return obj.subjectId + ":" + obj.questNo;  // 科目和题目组合才是唯一
        }
    };
    var t = this;
    var projectId = this.projectId;
    var isObjective = this.isObjective;
    iterateRanges(function (rangeName) {
        var range = {name: rangeName, id: getRangeId(rangeName, t)};
        iterateTargets(function (targetName) {
            if (rangeName == 'student' && targetName == 'quest') {
                return;
            }
            var target = {name: targetName, id: getTargetId(targetName, t)};
            var key = {projectId: projectId, range: range, target: target};
            emit(key, {totalScore: t.score});
            if (targetName == 'subject') {  // 科目需要统计主客观得分
                key.isObjective = isObjective;
                emit(key, {totalScore: t.score});
            }
        });
    });
};
var reduceFunction = function (key, values) {
    var totalScore = 0;
    values.forEach(function (value) {
        totalScore += value.totalScore;
    });
    return {totalScore: totalScore};
};
// 统计班校区市省的项目科目题目总分
var generateTotalScore = function (projectId) {
    db.total_score.remove({"id.projectId": projectId});
    db.runCommand({
        mapReduce: "score",
        query: {projectId: projectId},
        sort: {schoolId: 1, classId: 1},
        map: mapFunction,
        reduce: reduceFunction,
        out: {merge: "total_score", sharded: true}
    });
};