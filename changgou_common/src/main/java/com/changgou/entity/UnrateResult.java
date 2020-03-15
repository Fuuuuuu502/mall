package com.changgou.entity;
import java.util.HashMap;
import java.util.List;

public class UnrateResult<T> {

    private Long total;//总记录数
    private List<HashMap<String,Object>> rows;//记录


    public UnrateResult(Long total,List<HashMap<String,Object>> rows) {
        this.total = total;
        this.rows = rows;
    }

    public UnrateResult() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<HashMap<String,Object>> getRows() {
        return rows;
    }

    public void setRows(List<HashMap<String,Object>> rows) {
        this.rows = rows;
    }
}
