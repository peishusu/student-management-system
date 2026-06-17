package com.example.student.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public class StudentStats implements Serializable {
    private long total;
    private BigDecimal averageScore;
    private long excellentCount;
    private long classCount;
    private Map<String, Long> classDistribution;
    private Map<String, Long> scoreDistribution;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }

    public long getExcellentCount() {
        return excellentCount;
    }

    public void setExcellentCount(long excellentCount) {
        this.excellentCount = excellentCount;
    }

    public long getClassCount() {
        return classCount;
    }

    public void setClassCount(long classCount) {
        this.classCount = classCount;
    }

    public Map<String, Long> getClassDistribution() {
        return classDistribution;
    }

    public void setClassDistribution(Map<String, Long> classDistribution) {
        this.classDistribution = classDistribution;
    }

    public Map<String, Long> getScoreDistribution() {
        return scoreDistribution;
    }

    public void setScoreDistribution(Map<String, Long> scoreDistribution) {
        this.scoreDistribution = scoreDistribution;
    }
}
