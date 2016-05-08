var generateStudentList = function (projectId, subjectId) {
    var outputCollection = "student_list";

    db.getCollection(outputCollection).remove({
        "_id.projectId": projectId,
        "_id.subjectId": subjectId
    });

    var query = {projectId: projectId};
    if (subjectId && subjectId != "000") {
        query.subjectId = subjectId;
    }

    db.runCommand({
        mapReduce: "score",
        query: query,
        map: function () {
            var key = {
                projectId: this.projectId,
                classId: this.classId,
                schoolId: this.schoolId,
                area: this.areaId,
                city: this.areaId.substring(0, 4) + "00",
                province: this.areaId.substring(0, 2) + "0000"
            };

            if (subjectId) {
                key.subjectId = subjectId;
            } else {
                key.subjectId = "000";
            }

            emit(key, {studentIds: [this.studentId]});
        },
        reduce: function (key, values) {
            var studentIds = [];
            var itemCount = 0;

            values.forEach(function (value) {
                studentIds = studentIds.concat(value.studentIds);
                itemCount += value.studentIds.length;
            });

            studentIds = studentIds.filter(function (item, index, arr) {
                return arr.indexOf(item) == index;
            });

            return {studentIds: studentIds};
        },
        scope: {subjectId: subjectId},
        out: {
            merge: outputCollection,
            sharded: true
        }
    })
};

generateStudentList("FAKE_PROJECT_2", "000");

db.student_list.find({'_id.projectId': 'FAKE_PROJECT_2', '_id.subjectId': '000'});