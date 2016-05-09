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
                    subject: "$_id.subjectId",
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

// 查询区县列表
var generateAreaList = function (projectId) {
    var areaResult = db.score.aggregate([
        {$match: {projectId: projectId}},
        {$group: {_id: {areaId: "$areaId"}}}
    ]);

    if (areaResult.hasNext()) {
        db.area_list.remove({projejctId: projectId});

        areaResult.forEach(function (key) {
            db.area_list.update(
                {projectId: projectId},
                {$push: {areaIds: key._id.areaId}},
                {upsert: true}
            );
        });
    }
};

// 查询市列表
var generateCityList = function (projectId) {
    var areaResult = db.score.aggregate([
        {$match: {projectId: projectId}},
        {$project: {cityId: {$substr: ["$areaId", 0, 4]}}},
        {$group: {_id: {cityId: "$cityId"}}}
    ]);

    if (areaResult.hasNext()) {
        db.city_list.remove({projejctId: projectId});

        areaResult.forEach(function (key) {
            db.city_list.update(
                {projectId: projectId},
                {$push: {cityIds: key._id.cityId + "00"}},
                {upsert: true}
            );
        });
    }
};

// 查询省列表
var generateProvinceList = function (projectId) {
    var areaResult = db.score.aggregate([
        {$match: {projectId: projectId}},
        {$project: {provinceId: {$substr: ["$areaId", 0, 2]}}},
        {$group: {_id: {provinceId: "$provinceId"}}}
    ]);

    if (areaResult.hasNext()) {
        db.province_list.remove({projejctId: projectId});

        areaResult.forEach(function (key) {
            db.province_list.update(
                {projectId: projectId},
                {$push: {provinceIds: key._id.provinceId + "0000"}},
                {upsert: true}
            );
        });
    }
};

//////////////////////////////////////////////////////////////

// f(rangeName, rangeId)
var iterateRanges = function (projectId, f) {

    var classes = [];
    var classesCursor = db.class_list.find({projectId: projectId}, {classIds: 1});
    if (classesCursor.hasNext()) {
        classesCursor.forEach(function (doc) {
            classes = classes.concat(doc.classIds);
        });
    }
    classes.forEach(function (classId) {
        f("class", classId);
    });

    var schools = db.school_list.findOne({projectId: projectId}, {schoolIds: 1});
    if (schools) {
        schools.schoolIds.forEach(function (schoolId) {
            f("school", schoolId);
        });
    }

    var areas = db.area_list.findOne({projectId: projectId}, {areaIds: 1});
    if (areas) {
        areas.areaIds.forEach(function (areaId) {
            f("area", areaId);
        });
    }

    var cities = db.city_list.findOne({projectId: projectId}, {cityIds: 1});
    if (cities) {
        cities.cityIds.forEach(function (cityId) {
            f("city", cityId);
        });
    }

    var provinces = db.province_list.findOne({projectId: projectId}, {provinceIds: 1});
    if (provinces) {
        provinces.provinceIds.forEach(function (provinceId) {
            f("province", provinceId);
        });
    }
};

//////////////////////////////////////////////////////////////

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
