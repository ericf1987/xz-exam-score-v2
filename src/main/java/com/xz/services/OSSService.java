package com.xz.services;

import com.xz.ajiaedu.common.aliyun.OSSFileClient;
import com.xz.ajiaedu.common.aliyun.OSSTempCridentialKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * (description)
 * created at 16/06/14
 *
 * @author yiding_he
 */
@Service
public class OSSService {

    @Value("${oss.auth.url}")
    private String ossAuthUrl;

    @Value("${oss.auth.device}")
    private String ossAuthDevice;

    @Value("${oss.auth.key}")
    private String ossAuthKey;

    @Value("${oss.bucket}")
    private String ossBucketName;

    private OSSFileClient ossFileClient;

    @PostConstruct
    private void init() {

        OSSTempCridentialKeeper cridentialKeeper =
                new OSSTempCridentialKeeper(ossAuthUrl, ossAuthDevice, ossAuthKey, ossBucketName);

        this.ossFileClient = new OSSFileClient(cridentialKeeper);
    }

    public void uploadFile(String filePath, String uploadPath) {
        this.ossFileClient.uploadFile(new File(filePath), uploadPath);
    }
}
