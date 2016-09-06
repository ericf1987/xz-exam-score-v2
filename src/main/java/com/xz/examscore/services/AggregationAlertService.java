package com.xz.examscore.services;

import com.xz.ajiaedu.common.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 将统计异常发送提醒
 * @author by fengye on 2016/9/6.
 */
@Service
public class AggregationAlertService {

    public static final Logger LOG = LoggerFactory.getLogger(AggregationAlertService.class);

    @Value("${aggregation.alert.server}")
    private String server;

    @Value("${aggregation.alert.source}")
    private String source;

    @Value("${aggregation.alert.receiver}")
    private String receiver;

    public void sendAlertMessage(String title, String content){
        sendMessage(server, source, receiver, title, content);
    }

    public void sendMessage(String url, String source, String receiver, String title, String content){
        HttpRequest request = new HttpRequest(url)
                .setParameter("source", source)
                .setParameter("receiver", receiver)
                .setParameter("title", title)
                .setParameter("content", content);
        try {
            request.requestPost();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.warn("发送统计异常提醒出现异常！");
        }
    }

    public List<String> getReceivers(String receivers){
        return Arrays.asList(receivers.split(","));
    }
}
