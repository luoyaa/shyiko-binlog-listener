package org.example.listener;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import lombok.extern.slf4j.Slf4j;
import org.example.config.BinaryLogProperties;
import org.example.domain.TableInfo;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luoyaa
 * @date 2023-09-14 18:04:21
 */
@Slf4j
@Component
public class BinaryLogEventListener {

    @Resource
    private BinaryLogClient binaryLogClient;
    @Resource
    private BinaryLogProperties binaryLogProperties;
    private static volatile Map<Long, TableInfo> eventDataMap = new ConcurrentHashMap<>();


    @EventListener(ApplicationReadyEvent.class)
    public void listenToBinaryLogEvents() {
        // 要监听的表
        List<String> tables = binaryLogProperties.getTables();

        binaryLogClient.registerEventListener(event -> {
            this.processEvent(event.getData(), tables);
        });
        try {
            binaryLogClient.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理监听事件
     */
    private void processEvent(EventData data, List<String> tables) {
        // 监听的数据库和表相关信息
        if (data instanceof TableMapEventData) {
            this.processTableMapEvent(data, tables);
        }
        // 对应的数据库和表对应的事件
        if (data instanceof WriteRowsEventData) {
            this.processWriteEvent(data, tables);
        } else if (data instanceof UpdateRowsEventData) {
            this.processUpdateEvent(data, tables);
        } else if (data instanceof DeleteRowsEventData) {
            this.processDeleteEvent(data, tables);
        }
    }

    /**
     * 删除监听
     */
    private void processDeleteEvent(EventData data, List<String> tables) {
        log.info("全部的删除事件");
        DeleteRowsEventData eventData = (DeleteRowsEventData) data;

        TableInfo tableInfo = this.isListener(eventData.getTableId(), tables);
        if (tableInfo != null) {
            log.info("我监听到了我想要监听的表的事件了，表名：{}", tableInfo.getTable());
            // TODO: 2023/9/14
        }
    }

    /**
     * 更新监听
     */
    private void processUpdateEvent(EventData data, List<String> tables) {
        log.info("全部的修改事件");
        UpdateRowsEventData eventData = (UpdateRowsEventData) data;

        TableInfo tableInfo = this.isListener(eventData.getTableId(), tables);
        if (tableInfo != null) {
            log.info("我监听到了我想要监听的表的事件了，表名：{}", tableInfo.getTable());
            // TODO: 2023/9/14
        }
    }

    /**
     * 插入监听
     */
    private void processWriteEvent(EventData data, List<String> tables) {
        log.info("全部的插入事件");
        WriteRowsEventData eventData = (WriteRowsEventData) data;

        TableInfo tableInfo = this.isListener(eventData.getTableId(), tables);
        if (tableInfo != null) {
            log.info("我监听到了我想要监听的表的事件了，表名：{}", tableInfo.getTable());
            // TODO: 2023/9/14
        }
    }

    /**
     * 监听到表数据库以及表的信息
     */
    private void processTableMapEvent(EventData data, List<String> tables) {
        TableMapEventData tableMapEventData = (TableMapEventData) data;
        long tableId = tableMapEventData.getTableId();
        String database = tableMapEventData.getDatabase();
        String table = tableMapEventData.getTable();

        log.info("数据库：{}, 表：{}", database, table);

        StringBuilder builder = new StringBuilder();
        builder.append(database).append(".").append(table);

        if (tables.contains(builder.toString())) {
            TableInfo tableInfo = TableInfo.builder()
                    .database(database)
                    .table(table)
                    .build();
            eventDataMap.put(tableId, tableInfo);
        }
    }

    /**
     * 过滤自己想要监听的表名，不是我们需要的表返回null
     */
    private TableInfo isListener(long tableId, List<String> tables) {

        if (CollectionUtils.isEmpty(tables)) {
            return null;
        }

        TableInfo tableInfo = eventDataMap.get(tableId);
        if (tableInfo == null) {
            return null;
        }

        String database = tableInfo.getDatabase();
        String table = tableInfo.getTable();

        StringBuilder builder = new StringBuilder();
        builder.append(database).append(".").append(table);

        if (!tables.contains(builder.toString())) {
            return null;
        }

        tableInfo.setDatabaseTable(builder.toString());
        return tableInfo;
    }
}
