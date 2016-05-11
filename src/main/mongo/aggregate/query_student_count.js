var queryStudentCount = function (projectId, subjectId, rangeName, rangeId) {
    if (!subjectId) {
        subjectId = '000';
    }
    var match = {'projectId': projectId, 'subjectId': subjectId};
    if (rangeName && rangeId) {
        match[rangeName] = rangeId;
    }
    var result = db.student_count.aggregate([
        {$match: match},
        {$group: {_id: null, sum: {$sum: "$studentCount"}}}
    ]);
    return result.hasNext() ? result.next().sum : 0;
};

