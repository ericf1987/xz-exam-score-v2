package com.xz.extractor.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.extractor.InvalidExamDataException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据处理器抽象类
 *
 * @author zhaorenwu
 */
public abstract class DataProcessor {

    protected void assertNotEmpty(Object obj, String propName) {
        if (obj == null ||
                (obj instanceof String && StringUtils.isBlank((String) obj))) {
            throw new ProcessorException("{0} 不能为空", propName);
        }
    }

    protected String getString(JSONObject jsonObject, String propName) {
        String value = jsonObject.getString(propName);
        assertNotEmpty(value, propName);
        return value;
    }

    protected String getString(JSONObject jsonObject, String propName, String defaultValue) {
        String value = jsonObject.getString(propName);
        return StringUtils.defaultIfBlank(value, defaultValue);
    }

    protected int getInt(JSONObject jsonObject, String propName) {
        Integer value = jsonObject.getInteger(propName);
        assertNotEmpty(value, propName);
        return value;
    }

    protected int getInt(JSONObject jsonObject, String propName, int defaultValue) {
        Integer value = jsonObject.getInteger(propName);
        return value == null ? defaultValue : value;
    }

    protected double getDouble(JSONObject jsonObject, String propName) {
        Double value = jsonObject.getDouble(propName);
        assertNotEmpty(value, propName);
        return value;
    }

    protected double getDouble(JSONObject jsonObject, String propName, double defaultValue) {
        Double value = jsonObject.getDouble(propName);
        return value == null ? defaultValue : value;
    }

    protected boolean getBoolean(JSONObject jsonObject, String propName) {
        Boolean value = jsonObject.getBoolean(propName);
        assertNotEmpty(value, propName);
        return value;
    }

    protected boolean getBoolean(JSONObject jsonObject, String propName, boolean defaultValue) {
        Boolean value = jsonObject.getBoolean(propName);
        return value == null ? defaultValue : value;
    }

    protected List<String> getStringList(JSONObject jsonObject, String propName) {
        JSONArray jsonArray = jsonObject.getJSONArray(propName);
        assertNotEmpty(jsonArray, propName);
        return jsonArray.stream().map(String::valueOf).collect(Collectors.toList());
    }

    protected JSONObject getJsonObject (JSONObject jsonObject, String propName) {
        JSONObject _jsonObject = jsonObject.getJSONObject(propName);
        assertNotEmpty(_jsonObject, propName);
        return _jsonObject;
    }

    /**
     * 读取文件
     *
     * @param filename  文件名
     * @param content   文件内容
     *
     * @throws IOException 如果读取失败
     */
    public void read(String project, String filename, byte[] content) throws IOException {

        if (!filename.endsWith(".json")) {
            return;
        }

        Context context = new Context();
        context.put("filename", filename);
        context.put("project", project);

        int lineCounter = 1;
        try {
            before(context); // 读取整个文件之前的处理

            // 按行读取文件内容
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            LineIterator lineIterator = IOUtils.lineIterator(inputStream, "UTF-8");

            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                context.put("currentline", line);

                processLine(context, line);
                lineCounter++;
            }

            after(context);  // 读取整个文件之后的处理
        } catch (InvalidExamDataException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidExamDataException(context, lineCounter, e);
        }
    }

    /**
     * 读取文件中的考试项目id
     *
     * @param filename  文件名称
     * @param content   文件内容
     *
     * @return  考试项目id
     */
    public String readExamProject(String filename, byte[] content) {
        if (!filename.endsWith(".json")) {
            return null;
        }

        try {

            // 按行读取文件内容
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            LineIterator lineIterator = IOUtils.lineIterator(inputStream, "UTF-8");

            String line = null;
            while (lineIterator.hasNext()) {
                line = lineIterator.nextLine();
                if (StringUtil.isEmpty(line)) {
                    break;
                }
            }

            if (StringUtil.isBlank(line)) {
                return null;
            }

            JSONObject jsonObject = JSON.parseObject(line);
            return getString(jsonObject, "project");
        } catch (InvalidExamDataException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidExamDataException("考试项目信息读取失败", e);
        }
    }

    /**
     * 生成缓存 key
     *
     * @param params 用于生成缓存 key 的参数
     *
     * @return 缓存 key
     */
    protected String key(Object... params) {
        return StringUtils.join(params, ":");
    }

    /////////////////////////////////////////////////////////////////

    protected void before(Context context) {
    }

    protected void after(Context context) {
    }

    public abstract String getFilePattern();

    protected abstract void processLine(Context context, String line);
}
