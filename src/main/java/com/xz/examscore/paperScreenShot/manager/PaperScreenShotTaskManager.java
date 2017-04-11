package com.xz.examscore.paperScreenShot.manager;

import com.xz.ajiaedu.common.concurrent.Executors;
import com.xz.examscore.paperScreenShot.bean.TaskProcess;
import com.xz.examscore.paperScreenShot.service.MonitorService;
import com.xz.examscore.paperScreenShot.service.PaperScreenShotService;
import com.xz.examscore.services.ClassService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.concurrent.Executors.newBlockingThreadPoolExecutor;

/**
 * @author by fengye on 2017/3/3.
 */
@Component
public class PaperScreenShotTaskManager {
    static final Logger LOG = LoggerFactory.getLogger(PaperScreenShotTaskManager.class);

    private ThreadPoolExecutor threadPoolExecutor;

    @Value("${paper.screenshot.task.poolsize}")
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
        this.threadPoolExecutor = Executors.newBlockingThreadPoolExecutor(poolSize, poolSize, 100);
    }

    public void generatePaperScreenShots(final String projectId, boolean async) {
        //遍历考试学校ID
        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().map(s -> s.getString("school")).collect(Collectors.toList());

        //所有参考科目
        List<String> subjectIds = subjectService.querySubjects(projectId);

        List<Map<String, List<String>>> list = new ArrayList<>();

        //获取所有学校的班级ID列表
        schoolIds.forEach(schoolId -> {
            Map<String, List<String>> map = new HashMap<>();
            map.put(schoolId, classService.listClasses(projectId, schoolId).stream().map(c -> c.getString("class")).collect(Collectors.toList()));
            list.add(map);
        });

        //每个班级生成一个任务

        LOG.info("====项目{}======, 试卷截图任务开始执行======", projectId);
        monitorService.reset(projectId, TaskProcess.GENERATE_PAPER_SCREEN_SHOT);
        monitorService.clearFailedStudents(projectId);

        ThreadPoolExecutor pool = async ? threadPoolExecutor : newBlockingThreadPoolExecutor(10, 10, 100);
        for (Map<String, List<String>> map : list) {
            for (String schoolId : map.keySet()) {

                LOG.info("====项目{}, 学校{}, 试卷截图生成开始", projectId, schoolId);

                List<String> classIds = map.get(schoolId);

                CountDownLatch countDownLatch = new CountDownLatch(classIds.size());

                for (String classId : classIds) {
                    Runnable runnable = () -> {
                        try {
                            paperScreenShotService.dispatchOneClassTask(projectId, schoolId, classId, subjectIds);
                        } catch (Exception e) {
                            LOG.info("生成试卷截图失败, 项目{}， 学校{}， 班级{}", projectId, schoolId, classId);
                        } finally {
                            countDownLatch.countDown();
                        }
                    };
                    pool.submit(runnable);
                }

                if (!async) {
                    try {
                        pool.shutdown();
                        pool.awaitTermination(1, TimeUnit.DAYS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    countDownLatch.await(1, TimeUnit.HOURS);
                    LOG.info("====项目{}, 学校{}, 试卷截图生成完毕", projectId, schoolId);
                } catch (InterruptedException e) {
                    LOG.info("====项目{}, 学校{}, 试卷截图生成超时！", projectId, schoolId);
                }
            }
        }
        LOG.info("====项目{}======, 试卷截图任务执行完毕！======", projectId);
    }
}
