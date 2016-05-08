var p1 = "FAKE_PROJECT_1";
var p2 = "FAKE_PROJECT_2";

// 查询科目列表
var generateSubjectList = function (projectId) {
    var subjectsResult = db.score.aggregate([
        {$match: {projectId: projectId}},
        {$group: {_id: {subjectId: "$subjectId"}}}
    ]);

    if (subjectsResult.hasNext()) {
        db.subject_list.remove({projectId: projectId});

        subjectsResult.forEach(function (key) {
            db.subject_list.update(
                {projectId: projectId},
                {$push: {subjectIds: key._id.subjectId}},
                {upsert: true});
        })
    }
};

// 查询题目列表
var generateQuestList = function (projectId) {
    var questsResult = db.score.aggregate([
        {$match: {projectId: projectId}},
        {$group: {_id: {subjectId: "$subjectId", questNo: "$questNo"}}}
    ]);

    if (questsResult.hasNext()) {
        db.quest_list.remove({projectId: projectId});

        questsResult.forEach(function (key) {
            db.quest_list.update(
                {projectId: projectId, subjectId: key._id.subjectId},
                {$push: {questNos: key._id.questNo}},
                {upsert: true}
            );
        })
    }
};

// 查询学校列表
var generateSchoolList = function (projectId) {
    var schoolResult = db.score.aggregate([
        {$match: {projectId: projectId}},
        {$group: {_id: {schoolId: "$schoolId"}}}
    ]);

    if (schoolResult.hasNext()) {
        db.school_list.remove({projectId: projectId});

        schoolResult.forEach(function (key) {
            db.school_list.update(
                {projectId: projectId},
                {$push: {schoolIds: key._id.schoolId}},
                {upsert: true});
        })
    }
};

// 查询班级列表
var generateClassList = function (projectId) {
    var classResult = db.score.aggregate([
        {$match: {projectId: projectId}},
        {$group: {_id: {schoolId: "$schoolId", classId: "$classId"}}}
    ]);

    if (classResult.hasNext()) {
        db.class_list.remove({projectId: projectId});

        classResult.forEach(function (key) {
            db.class_list.update(
                {projectId: projectId, schoolId: key._id.schoolId},
                {$push: {classIds: key._id.classId}},
                {upsert: true});
        })
    }
};

// 查询区县列表
var generateAreaList = function (projectId) {
    var areaResult = db.score.aggregate([
        {$match: {projectId: projectId}},
        {$group: {_id: {areaId: "$areaId"}}}
    ]);


};

//////////////////////////////////////////////////////////////

// 遍历所有题目
// f(subjectId, questNo)
var iterateQuests = function (projectId, f) {
    var docs = [];
    db.quest_list.find({projectId: projectId}).forEach(function (doc) {
        docs.push(doc);
    });

    docs.forEach(function (doc) {
        var subjectId = doc.subjectId;
        doc.questNos.forEach(function (questNo) {
            f(subjectId, questNo);
        })
    });
};

// 遍历所有科目
// f(subjectId)
var iterateSubjects = function (projectId, f) {
    var docs = [];
    db.subject_list.find({projectId: projectId}).forEach(function (doc) {
        docs.push(doc);
    });

    docs.forEach(function (doc) {
        doc.subjectIds.forEach(function (subjectId) {
            f(subjectId);
        })
    })
};