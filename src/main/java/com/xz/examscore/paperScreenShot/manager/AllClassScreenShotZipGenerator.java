package com.xz.examscore.paperScreenShot.manager;

import com.xz.examscore.AppException;
import com.xz.examscore.paperScreenShot.bean.TaskProcess;
import com.xz.examscore.paperScreenShot.service.MonitorService;
import com.xz.examscore.paperScreenShot.service.PaperScreenShotService;
import com.xz.examscore.services.ClassService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.SubjectService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.concurrent.Executors.newBlockingThreadPoolExecutor;

/**
 * @author by fengye on 2017/3/16.
 */
@Component
public class AllClassScreenShotZipGenerator {

    static final Logger LOG = LoggerFactory.getLogger(AllClassScreenShotZipGenerator.class);

    private ThreadPoolExecutor threadPoolExecutor;

    @Value("${report.generator.poolsize}")
    private int poolSize;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    MonitorService monitorService;

    @PostConstruct
    public void init() {
        this.threadPoolExecutor = newBlockingThreadPoolExecutor(poolSize, poolSize, 100);
    }

    /**
     * 生成考试项目所有班级的试卷截图压缩包
     * @param projectId    考试项目
     * @param async        是否异步执行
     */
    public void generateClassPaperScreenShot(final String projectId, boolean async) {
        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().map(s -> s.getString("school")).collect(Collectors.toList());
        List<String> subjects = subjectService.querySubjects(projectId);

        ThreadPoolExecutor pool = async ? threadPoolExecutor : newBlockingThreadPoolExecutor(10, 10, 100);

        List<Document> allClasses = new ArrayList<>();
        for (String schoolId : schoolIds) {
            List<Document> classDocs = classService.listClasses(projectId, schoolId);
            allClasses.addAll(classDocs);
        }

        monitorService.reset(projectId, TaskProcess.GENERATE_CLASS_ZIP);


        CountDownLatch countDownLatch = new CountDownLatch(allClasses.size());

        for (Document classDoc : allClasses) {
            String schoolId = classDoc.getString("school");
            String classId = classDoc.getString("class");

            Runnable runnable = () -> {
                try {
                    paperScreenShotService.generateOneClassZip(projectId, schoolId, classId, subjects);
                } catch (Exception e) {
                    LOG.error("生成班级试卷截图压缩包失败！项目:{}， 学校ID:{}， 班级:ID{}", projectId, schoolId, classId);
                } finally {
                    countDownLatch.countDown();
                }
            };

            pool.submit(runnable);

        }

        if(!async){
            try {
                pool.shutdown();
                pool.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                throw new AppException(e);
            }
        }

        try {
            countDownLatch.await(1, TimeUnit.HOURS);
            LOG.info("====项目{}, 班级截图压缩包全部生成完毕！");
        } catch (InterruptedException e) {
            LOG.info("====项目{}, 班级截图压缩包生成超时！");
        }

    }
}
