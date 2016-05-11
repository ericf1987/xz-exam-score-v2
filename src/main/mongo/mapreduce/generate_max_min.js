// 统计题目的最高分最低分
var generateQuestMaxMin = function (projectId) {
    db.runCommand({
        mapReduce: "score",
        query: {projectId: projectId},
        sort: {schoolId: 1, classId: 1},
        map: function () {
            var ranges = [
                {name: "province", id: this.areaId.substring(0, 2) + "0000"},
                {name: "city", id: this.areaId.substring(0, 4) + "00"},
                {name: "area", id: this.areaId},
                {name: "school", id: this.schoolId},
                {name: "class", id: this.classId},
            ];
            var t = this;
            ranges.forEach(function (range) {
                emit({
                    projectId: t.projectId,
                    range: range,
                    target: {name: "quest", id: t.subjectId + ":" + t.questNo}
                }, {min: t.score, max: t.score});
            });
        },
        reduce: function (key, values) {
            var min = 9999, max = 0;
            values.forEach(function (value) {
                if (value.min < min) {
                    min = value.min;
                }
                if (value.max > max) {
                    max = value.max;
                }
            });
            return {min: min, max: max};
        },
        out: {merge: "min_max_score", sharded: true}
    });
};
