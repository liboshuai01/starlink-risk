package com.liboshuai.starlink.slr.admin.service.mock.impl;

import com.github.javafaker.Faker;
import com.liboshuai.starlink.slr.admin.common.annotation.TakeTime;
import com.liboshuai.starlink.slr.admin.common.component.snowflake.SnowflakeId;
import com.liboshuai.starlink.slr.admin.common.util.mock.MockEventUtils;
import com.liboshuai.starlink.slr.admin.service.mock.MockService;
import com.liboshuai.starlink.slr.connector.api.dto.EventDTO;
import com.liboshuai.starlink.slr.framework.common.exception.util.ServiceExceptionUtil;
import com.liboshuai.starlink.slr.framework.common.util.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.stream.Stream;

@Slf4j
@Service
public class MockServiceImpl implements MockService {

    @Resource
    private SnowflakeId snowflakeId;

    /**
     * 生成的文件路径
     */
    @Value("${slr-admin.load-test.file-path}")
    private String filePath;

    /**
     * 生成的文件名称
     */
    private static final String FILE_NAME = "event.log";

    /**
     * 生成测试数据并写入文件
     *
     * @param startMillis    开始的时间戳
     * @param durationMillis 时间跨度
     * @param perSecondCount 每秒生成的时间戳数量
     */
    @Override
    @TakeTime
    @Async("slrAsyncExecutor")
    public void generatorDataToFile(long startMillis, long durationMillis, int perSecondCount) {
        Faker faker = new Faker(Locale.CHINA);
        // 获取时间戳集合
        Stream<Long> timeStampStream = MockEventUtils.generateTimeStampStream(startMillis, durationMillis, perSecondCount);

        // 生成测试数据并写入文件
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath + File.separator + FILE_NAME), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            timeStampStream.forEach(timeStamp -> {
                EventDTO eventDTO = EventDTO.builder()
                        .userId(snowflakeId.nextIdStr())
                        .username(MockEventUtils.getUserName())
                        .eventId(snowflakeId.nextIdStr())
                        .eventTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeStamp))
                        .channel(MockEventUtils.getChannel())
                        .build();
                String json = JsonUtils.toJsonString(eventDTO);
                try {
                    writer.write(json);
                    writer.newLine();
                } catch (IOException e) {
                    log.error("生成测试数据并写入文件异常: {}", e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            });
            writer.flush();
        } catch (IOException e) {
            log.error("创建 BufferedWriter 出错: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}