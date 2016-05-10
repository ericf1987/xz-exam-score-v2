var generateObjectiveFullScore = function (projectId) {
    var cursor1 = db.quest_list.aggregate([
        {$match: {projectId: projectId}},
        {
            $group: {
                _id: {projectId: "$projectId", subjectId: "$subjectId", isObjective: "$isObjective"},
                fullScore: {$sum: "$score"}
            }
        }
    ]);
    if (cursor1.hasNext()) {
        cursor1.forEach(function (doc) {
            db.full_score.save(doc);
        });
    }
};