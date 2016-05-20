package com.xz.controllers;

import ch.qos.logback.core.util.CloseUtil;
import com.xz.AppException;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.extractor.ProcessorFactory;
import com.xz.extractor.processor.DataProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * zip数据文件上传
 *
 * @author zhaorenwu
 */
@Controller
public class ZipUploadController {

    static final Logger LOG = LoggerFactory.getLogger(ZipUploadController.class);

    @Value("${zip.save.location}")
    private String zipSaveLocation;     // zip 保存位置

    @Autowired
    ProcessorFactory processorFactory;

    /**
     * 上传考试数据打包文件
     *
     * @param file  打包文件
     *
     * @return 上传结果
     *
     * @throws Exception 如果上传失败
     */
    @RequestMapping(value = "/upload-exam-data", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadExamZip(@RequestParam MultipartFile file) throws Exception {

        final File saveFile = saveToDisk(file);
        try {
            String filePath = saveFile.getAbsolutePath();
            extractAndSaveData(filePath);

        } catch (IOException e) {
            throw new AppException(e);
        }

        return Result.success();
    }

    //////////////////////////////////////////////////////////////

    private File saveToDisk(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID().toString() + ".zip";
        File saveFile = new File(zipSaveLocation, filename);
        LOG.info("保存 zip 到 " + saveFile.getAbsolutePath());

        if (!saveFile.exists() && !saveFile.createNewFile()) {
            throw new IllegalStateException("无法创建文件 " + saveFile.getAbsolutePath());
        }

        file.transferTo(saveFile);
        return saveFile;
    }

    public void extractAndSaveData(String zipPath) throws IOException, AppException {

        Supplier<InputStream> supplier = () -> {
            try {
                return new FileInputStream(zipPath);
            } catch (FileNotFoundException e) {
                throw new AppException(e);
            }
        };

        extractAndSaveData(supplier);
    }

    private void extractAndSaveData(Supplier<InputStream> supplier) throws IOException {

        try {
            readEntries(() -> new ZipInputStream(supplier.get()));
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            if (e.getMessage().contains("MALFORMED")) {
                LOG.error("读取 zip 失败，尝试更换编码...");
                readEntries(() -> new ZipInputStream(supplier.get(), Charset.forName("GBK")));
            } else {
                LOG.error("读取 zip 失败", e);
            }
        }
    }

    private void readEntries(Supplier<ZipInputStream> zipInputStreamSupplier) throws IOException {

        ZipInputStream zipInputStream = zipInputStreamSupplier.get();
        try {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {

                String fileName = zipEntry.getName().replace("\\", "/");
                readEntry(zipInputStream, fileName);
            }
        } finally {
            CloseUtil.closeQuietly(zipInputStream);
        }
    }

    private void readEntry(ZipInputStream zipInputStream, String fileName) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (zipInputStream.available() > 0) {
            int read = zipInputStream.read();
            if (read != -1) {
                bos.write(read);
            }
        }

        try {

            String filePattern = getFilePattern(fileName);
            DataProcessor dataProcessor = processorFactory.getDataProcessor(filePattern);
            if (dataProcessor == null) {
                LOG.info("跳过文件 '" + fileName);
            } else {
                LOG.info("正在读取文件 '" + fileName + "'...");
                dataProcessor.read(fileName, bos.toByteArray());
            }
        } finally {
            CloseUtil.closeQuietly(bos);
        }
    }

    private String getFilePattern(String fileName) {
        if (fileName.contains("/")) {
            return StringUtils.substringAfterLast(fileName, "/");
        }

        return fileName;
    }
}
