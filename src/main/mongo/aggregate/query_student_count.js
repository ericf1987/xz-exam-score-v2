var queryStudentCount = function (projectId, subjectId, rangeName, rangeId) {
    if (!subjectId) {
        subjectId = '000';
    }

    var match = {'_id.projectId': projectId, '_id.subjectId': subjectId};
    if (rangeName && rangeId) {
        match['_id.' + rangeName] = rangeId;
    }

    var result = db.student_list.aggregate([
        {$match: match},
        {$group: {_id: null, sum: {$sum: {$size: '$value.studentIds'}}}}
    ]);

    return result.hasNext() ? result.next().sum : 0;
};

queryStudentCount('FAKE_PROJECT_2', '000', 'city', '430100');


