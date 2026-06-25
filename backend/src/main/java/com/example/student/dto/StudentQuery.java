package com.example.student.dto;

public class StudentQuery {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private String keyword;
    private String className;
    private String status;
    private Integer page = DEFAULT_PAGE;
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPage() {
        return page == null || page < 1 ? DEFAULT_PAGE : page;
    }

    public void setPage(Integer page) {
        this.page = page == null || page < 1 ? DEFAULT_PAGE : page;
    }

    public Integer getPageSize() {
        if (pageSize == null) {
            return DEFAULT_PAGE_SIZE;
        }
        if (pageSize == 10 || pageSize == 20 || pageSize == 50) {
            return pageSize;
        }
        return DEFAULT_PAGE_SIZE;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        return (getPage() - 1) * getPageSize();
    }
}
