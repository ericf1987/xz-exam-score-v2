package com.xz.examscore.importaggrdata.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.io.ZipFileReader;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.ZipEntry;

import static com.xz.ajiaedu.common.concurrent.Executors.newBlockingThreadPoolExecutor;

/**
 * @author by fengye on 2017/7/18.
 */
@Service
public class ImportFromMysqlDump {

    @Autowired
    MongoDatabase scoreDatabase;

    static final Logger LOG = LoggerFactory.getLogger(ImportFromMysqlDump.class);

    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void init() {
        this.threadPoolExecutor = newBlockingThreadPoolExecutor(10, 10, 100);
    }


    public Result importData(String projectId, String filePath) {
        File file = new File(filePath);

        if(null != file && file.length() != 0){
            ZipFileReader zipFileReader = new ZipFileReader(file);

            LOG.info("==========开始执行项目 {} 的统计数据导入, 文件大小为 {} ==========", projectId, file.length());

            doImportProcess(projectId, zipFileReader);

            LOG.info("==========导入执行完毕！==========");

            return Result.success();
        }

        return Result.fail(99, "数据文件为空，无法导入！");

    }

    public void doImportProcess(String projectId, ZipFileReader zipFileReader) {
        zipFileReader.readZipEntries("*", consumer -> readEntry(projectId, consumer, zipFileReader));
    }

    //读取单个文件
    private void readEntry(String projectId, ZipEntry zipEntry, ZipFileReader zipFileReader) {
        String fileName = zipEntry.getName().substring(0, zipEntry.getName().lastIndexOf("."));

        List<Document> list = new ArrayList<>();

        //将每行记录读出，转化为document对象，批量插入表中
        zipFileReader.readEntryByLine(zipEntry, "UTF-8", line -> readEntryLine(line, list));

        if(list.isEmpty()){
            LOG.error("当前文件 {} 中没有数据！", fileName);
            return;
        }

        Runnable runnable = () -> executeTask(projectId, fileName, list);

        threadPoolExecutor.submit(runnable);

    }

    private void executeTask(String projectId, String fileName, List<Document> list) {
        //清除原有数据
        LOG.info("----------当前数据表为 {}, 开始执行导入----------", fileName);

        MongoCollection<Document> collection = scoreDatabase.getCollection(fileName);

        Document query = MongoUtils.doc("project", projectId);

        LOG.info("-----清理旧数据-----");
        collection.deleteMany(query);
        LOG.info("-----完成清理, 开始写入新数据-----");
        collection.insertMany(list);
        LOG.info("----------数据表 {} 的导入操作执行完成, 共写入 {} 条记录----------", fileName, collection.count(query));
    }

    private void readEntryLine(String line, List<Document> list) {
        Document doc = Document.parse(line.trim());
        list.add(doc);
    }

}
