package com.tl_connect.dev.core.common.ultility.importer;

@FunctionalInterface
public interface ColumnMapper<T> {
    T map(RowAccesor row);
}
