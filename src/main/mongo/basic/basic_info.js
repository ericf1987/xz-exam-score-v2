var p1 = "FAKE_PROJECT_1";
var p2 = "FAKE_PROJECT_2";

var generateStudenCount = function (projectId) {
    var finalOutputCollection = "student_count";
    db.getCollection(finalOutputCollection).remove({"projectId": projectId});

    iterateSubjects(projectId, true, function (subjectId) {
        var tmpCollectionName = "tmp_student_list_" + projectId + "_" + subjectId;

        db.score.aggregate([
            {$match: {projectId: projectId}},
            {
                $group: {
                    _id: {
                        projectId: "$projectId",
                        subjectId: subjectId,
                        area: "$areaId",
                        schoolId: "$schoolId",
                        classId: "$classId"
                    },
                    studentIds: {$addToSet: "$studentId"}
                }
            },
            {
                $project: {
                    _id: 0,
                    projectId: "$_id.projectId",
                    subjectId: "$_id.subjectId",
                    class: "$_id.classId",
                    school: "$_id.schoolId",
                    area: "$_id.area",
                    city: {$concat: [{$substr: ['$_id.area', 0, 4]}, "00"]},
                    province: {$concat: [{$substr: ['$_id.area', 0, 2]}, "0000"]},
                    studentCount: {$size: "$studentIds"}
                }
            },
            {$out: tmpCollectionName}
        ]);

        db.getCollection(tmpCollectionName).find().forEach(function (doc) {
            db.getCollection(finalOutputCollection).save(doc);
        });
        db.getCollection(tmpCollectionName).drop();
    });
};

// 查询班级列表
var generateClassList = function (projectId) {
    var classResult = db.score.aggregate([
        {$match: {project: projectId}},
        {$group: {_id: {school: "$school", class: "$class"}}}
    ]);

    if (classResult.hasNext()) {
        db.class_list.remove({project: projectId});

        classResult.forEach(function (key) {
            db.class_list.update(
                {project: projectId, school: key._id.school},
                {$push: {classes: key._id.class}},
                {upsert: true});
        })
    }
};

// 查询学校列表
var generateSchoolList = function (projectId) {
    var schoolResult = db.score.aggregate([
        {$match: {project: projectId}},
        {$group: {_id: {school: "$school"}}}
    ]);

    if (schoolResult.hasNext()) {
        db.school_list.remove({project: projectId});

        schoolResult.forEach(function (key) {
            db.school_list.update(
                {project: projectId},
                {$push: {schools: key._id.school}},
                {upsert: true});
        })
    }
};

// 查询区县列表
var generateAreaList = function (projectId) {
    var areaResult = db.score.aggregate([
        {$match: {project: projectId}},
        {$group: {_id: {area: "$area"}}}
    ]);

    if (areaResult.hasNext()) {
        db.area_list.remove({project: projectId});

        areaResult.forEach(function (key) {
            db.area_list.update(
                {project: projectId},
                {$push: {areas: key._id.area}},
                {upsert: true}
            );
        });
    }
};

// 查询市列表
var generateCityList = function (projectId) {
    var areaResult = db.score.aggregate([
        {$match: {project: projectId}},
        {$group: {_id: {city: "$city"}}}
    ]);

    if (areaResult.hasNext()) {
        db.city_list.remove({project: projectId});

        areaResult.forEach(function (key) {
            db.city_list.update(
                {project: projectId},
                {$push: {citys: key._id.city}},
                {upsert: true}
            );
        });
    }
};

// 查询省列表
var generateProvinceList = function (projectId) {
    var areaResult = db.score.aggregate([
        {$match: {project: projectId}},
        {$group: {_id: {province: "$province"}}}
    ]);

    if (areaResult.hasNext()) {
        db.province_list.remove({project: projectId});

        areaResult.forEach(function (key) {
            db.province_list.update(
                {project: projectId},
                {$push: {provinces: key._id.province}},
                {upsert: true}
            );
        });
    }
};

var generateRangeLists = function (projectId) {
    generateClassList(projectId);
    generateSchoolList(projectId);
    generateAreaList(projectId);
    generateCityList(projectId);
    generateProvinceList(projectId);
};

//////////////////////////////////////////////////////////////

// 查询科目列表
var generateSubjectList = function (projectId) {
    var subjectsResult = db.score.aggregate([
        {$match: {project: projectId}},
        {$group: {_id: {subject: "$subject"}}}
    ]);

    if (subjectsResult.hasNext()) {
        db.subject_list.remove({project: projectId});

        subjectsResult.forEach(function (key) {
            db.subject_list.update(
                {project: projectId},
                {$push: {subjects: key._id.subject}},
                {upsert: true});
        })
    }
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
var iterateSubjects = function (projectId, containProject, f) {
    var subjectIds = [];
    var subjectDoc = db.subject_list.findOne({projectId: projectId});

    if (!subjectDoc) {
        return;
    } else {
        subjectDoc.subjectIds.forEach(function (subjectId) {
            subjectIds.push(subjectId);
        });
    }

    subjectIds.forEach(function (subjectId) {
        f(subjectId);
    });

    if (containProject) {
        f("000");
    }
};
