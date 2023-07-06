package io.github.vdaburon.jmeter.utils.comparekpi;

/**
 * Classe to save the comparaison parameters for a KPI and the result
 */
public class CheckKpiCompareResult {
    private String nameKpi;
    private String metricCsvColumnName;
    private String labelRegex;
    private String comparator;
    private String compareTo;
    private String thresholdDelta;
    private String comment;
    private boolean isKpiFail;
    private String failMessage;

    public String getNameKpi() {
        return nameKpi;
    }

    public void setNameKpi(String nameKpi) {
        this.nameKpi = nameKpi;
    }

    public String getMetricCsvColumnName() {
        return metricCsvColumnName;
    }

    public void setMetricCsvColumnName(String metricCsvColumnName) {
        this.metricCsvColumnName = metricCsvColumnName;
    }

    public String getLabelRegex() {
        return labelRegex;
    }

    public void setLabelRegex(String labelRegex) {
        this.labelRegex = labelRegex;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String getCompareTo() {
        return compareTo;
    }

    public void setCompareTo(String compareTo) {
        this.compareTo = compareTo;
    }

    public String getThresholdDelta() {
        return thresholdDelta;
    }

    public void setThresholdDelta(String thresholdDelta) {
        this.thresholdDelta = thresholdDelta;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isKpiFail() {
        return isKpiFail;
    }

    // ajout pour freemaker qui fait un get (getKpiFail) et pas un is (isKpiFail)
    public boolean getKpiFail() {
        return isKpiFail;
    }

    public void setKpiFail(boolean kpiFail) {
        isKpiFail = kpiFail;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CheckKpiCompareResult{");
        sb.append("nameKpi='").append(nameKpi).append('\'');
        sb.append(", metricCsvColumnName='").append(metricCsvColumnName).append('\'');
        sb.append(", labelRegex='").append(labelRegex).append('\'');
        sb.append(", comparator='").append(comparator).append('\'');
        sb.append(", compareTo='").append(compareTo).append('\'');
        sb.append(", thresholdDelta='").append(thresholdDelta).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", isKpiFail=").append(isKpiFail);
        sb.append(", failMessage='").append(failMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
