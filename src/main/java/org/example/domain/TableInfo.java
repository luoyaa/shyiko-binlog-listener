package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author luoyaa
 * @date 2023-09-15 10:05:24
 */
@Data
@Builder
@AllArgsConstructor
public class TableInfo {
    private String database;
    private String table;
    private String databaseTable;
}
