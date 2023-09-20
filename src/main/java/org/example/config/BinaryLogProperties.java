package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * binlog常量
 *
 * @author luoyaa
 */
@Data
@Component
@ConfigurationProperties(prefix = "binlog")
public class BinaryLogProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private long serverId;
    private List<String> tables;
}