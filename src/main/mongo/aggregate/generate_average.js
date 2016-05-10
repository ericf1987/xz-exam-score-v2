var generateAverage = function (projectId) {

    var counter = 0;
    db.total_score.find({"_id.projectId": projectId}).forEach(function (score) {
        var range = score._id.range;
        var target = score._id.target;

        if (range.name == 'student') {
            return;   // 单个学生的成绩不进行平均分计算
        }

        var subjectId = target.name == 'project' ? "000" :
            (target.name == 'subject' ? target.id :
                (target.name == 'quest' ? target.id.substring(0, target.id.indexOf(":")) : ''));

        var studentCount = queryStudentCount(projectId, subjectId, range.name, range.id);
        if (studentCount) {
            score.value.average = score.value.totalScore / studentCount;
            db.total_score.save(score);
        } else {
            throw "student count missing: " + projectId + "," + subjectId + "," + range.name + "," + range.id;
        }

        counter++;
        if (counter % 1000 == 0) {
            print(counter + " averages calculated.")
        }
    });

    if (counter % 1000 != 0) {
        print(counter + " averages calculated.")
    }

    print("Average completed.")
};