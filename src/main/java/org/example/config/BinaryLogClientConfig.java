package org.example.config;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author luoyaa
 * @date 2023-09-14 18:00:58
 */
@Configuration
public class BinaryLogClientConfig {

    @Resource
    private BinaryLogProperties binaryLogProperties;

    @Bean
    public BinaryLogClient binaryLogClient() {
        BinaryLogClient client = new BinaryLogClient(
                binaryLogProperties.getHost(),
                binaryLogProperties.getPort(),
                binaryLogProperties.getUsername(),
                binaryLogProperties.getPassword()
        );
        client.setServerId(binaryLogProperties.getServerId());

        // 数据序列化为java 对象
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                // 此模式下，时间戳表示为自 Unix 纪元（1970 年 1 月 1 日 UTC）以来的毫秒数。这与其他兼容模式不同，后者可能使用不同的格式来表示时间戳
                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                // 在此模式下，通过使用字节数组来表示字符和二进制数据，同时保留其编码和格式信息。这使得在读取时更容易处理这些数据，并将它们重新还原为原始格式
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
        );
        return client;
    }
}
