var projectId = "FAKE_PROJECT_2";
var subjectIds = ["001", "002", "003"];
var questCount = {"001": 20, "002": 30, "003": 50};
var questOptions = ["A", "B", "C", "D"];
var objQuestCount = {"001": 10, "002": 15, "003": 45};
var areaIds = ["430101", "430102", "430201", "430202"];
var scoreCollection = db.score;

var questScores = {
    "001": [
        {min: 1, max: 3, score: 1},
        {min: 4, max: 4, score: 7},
        {min: 5, max: 5, score: 8},
        {min: 6, max: 10, score: 3},
        {min: 11, max: 15, score: 3},
        {min: 16, max: 19, score: 3},
        {min: 20, max: 20, score: 40}
    ],
    "002": [
        {min: 1, max: 5, score: 2},
        {min: 6, max: 10, score: 2},
        {min: 11, max: 15, score: 2},
        {min: 16, max: 20, score: 2},
        {min: 21, max: 25, score: 2},
        {min: 26, max: 30, score: 10}
    ],
    "003": [
        {min: 1, max: 5, score: 1},
        {min: 6, max: 35, score: 2},
        {min: 36, max: 45, score: 1},
        {min: 46, max: 50, score: 5}
    ]
};

var schoolIdPrefix = "SCHOOL_";
var classIdPrefix = "CLASS_";

var schoolCount = 10;
var classCountPerSchool = 20;
var studentCountPerClass = 50;

var format = function (num, length) {
    var r = "" + num;
    while (r.length < length) {
        r = "0" + r;
    }
    return r;
};

var pickRandom = function (arr, without) {
    var result = arr[parseInt(Math.random() * arr.length)];
    while (without && result == without) {
        result = arr[parseInt(Math.random() * arr.length)];
    }
    return result;
};

var schools = [];

var createFakeSchools = function () {
    schools = [];

    for (var ischool = 0; ischool < schoolCount; ischool++) {
        var schoolId = schoolIdPrefix + format(ischool + 1, 3);
        var areaId = pickRandom(areaIds);

        schools.push({schoolId: schoolId, areaId: areaId});
    }
};

var quests = [];

var createFakeQuests = function () {
    quests = [];

    for (var isbj = 0; isbj < subjectIds.length; isbj++) {
        var subjectId = subjectIds[isbj];

        for (var iquest = 0; iquest < questCount[subjectId]; iquest++) {
            var isObj = iquest < objQuestCount[subjectId];
            var standardAnswer = pickRandom(questOptions);
            var score = 0;

            for (var i = 0; i < questScores[subjectId].length; i++) {
                var segment = questScores[subjectId][i];
                if (iquest + 1 >= segment.min && iquest + 1 <= segment.max) {
                    score = segment.score;
                    break;
                }
            }

            quests.push({
                projectId: projectId,
                subjectId: subjectId,
                isObjective: isObj,
                questNo: (iquest + 1).toString(),
                score: score,
                standardAnswer: standardAnswer
            });
        }
    }
};

var createFakeScores = function () {

    var counter = 0;
    for (var ischool = 0; ischool < schools.length; ischool++) {
        var school = schools[ischool];

        for (var iclass = 0; iclass < classCountPerSchool; iclass++) {
            var classId = school.schoolId + "_" + classIdPrefix + format(iclass + 1, 2);

            for (var istu = 0; istu < studentCountPerClass; istu++) {
                var studentId = classId + "_" + format(istu + 1, 2);

                createFakeScoreForStudent(school, classId, studentId);

                counter++;
                if (counter % 100 == 0) {
                    print(counter + " students generated for project " + projectId);
                }
            }
        }
    }
};

var createStudentScoreLevel = function () {
    return parseInt(Math.random() * 90 + 10);
};

var createFakeScoreForStudent = function (school, classId, studentId) {
    var scoreLevel = createStudentScoreLevel();

    for (var iq = 0; iq < quests.length; iq++) {
        var quest = quests[iq];
        var isRight = Math.random() * 100 >= scoreLevel;
        var score;
        var answer = "";

        if (isRight) {
            score = quest.score;
            if (quest.isObjective) {
                answer = quest.standardAnswer;
            }
        } else if (quest.isObjective) {
            score = 0;
            answer = pickRandom(questOptions, quest.standardAnswer);
        } else {
            score = parseInt(Math.random() * quest.score * 2 - 1) / 2;
            if (score < 0) {
                score = 0;
            }
        }

        scoreCollection.save({
            projectId: projectId,
            studentId: studentId,
            subjectId: quest.subjectId,
            questNo: quest.questNo,
            score: score,
            right: isRight,
            answer: answer,
            classId: classId,
            schoolId: school.schoolId,
            areaId: school.areaId
        });
    }
};
