// use admin;

var dbName = "project_scores";
var collection = "total_score";

db.adminCommand({shardCollection: (dbName + "." + collection), key: {"_id": 1}});

// use project_scores;