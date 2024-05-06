package com.hartehanks.optimized;

public class ServerParamsDto {
    private String InputTableName;
    private String TargetKeyFileName;
    private String ConnectionName;
    private String SourceDB;
    private String WhereClause;

    public String getInputTableName() {
        return InputTableName;
    }

    public void setInputTableName(String inputTableName) {
        InputTableName = inputTableName;
    }

    public String getTargetKeyFileName() {
        return TargetKeyFileName;
    }

    public void setTargetKeyFileName(String targetKeyFileName) {
        TargetKeyFileName = targetKeyFileName;
    }

    public String getConnectionName() {
        return ConnectionName;
    }

    public void setConnectionName(String connectionName) {
        ConnectionName = connectionName;
    }

    public String getSourceDB() {
        return SourceDB;
    }

    public void setSourceDB(String sourceDB) {
        SourceDB = sourceDB;
    }

    public String getWhereClause() {
        return WhereClause;
    }

    public void setWhereClause(String whereClause) {
        WhereClause = whereClause;
    }
}
