package io.github.vdaburon.jmeter.utils.comparekpi;

import java.util.List;

public class GlobalResult {
    private String csvJmeterCurrentReport;
    private String csvJmeterReferenceReport;
    private String kpiFile;
    private int numberOfKpis;
    private int numberFailed;
    List checkKpiCompareResults;

    public String getCsvJmeterCurrentReport() {
        return csvJmeterCurrentReport;
    }

    public void setCsvJmeterCurrentReport(String csvJmeterCurrentReport) {
        this.csvJmeterCurrentReport = csvJmeterCurrentReport;
    }

    public String getCsvJmeterReferenceReport() {
        return csvJmeterReferenceReport;
    }

    public void setCsvJmeterReferenceReport(String csvJmeterReferenceReport) {
        this.csvJmeterReferenceReport = csvJmeterReferenceReport;
    }

    public String getKpiFile() {
        return kpiFile;
    }

    public void setKpiFile(String kpiFile) {
        this.kpiFile = kpiFile;
    }

    public int getNumberOfKpis() {
        return numberOfKpis;
    }

    public void setNumberOfKpis(int numberOfKpis) {
        this.numberOfKpis = numberOfKpis;
    }

    public int getNumberFailed() {
        return numberFailed;
    }

    public void setNumberFailed(int numberFailed) {
        this.numberFailed = numberFailed;
    }

    public List getCheckKpiCompareResults() {
        return checkKpiCompareResults;
    }

    public void setCheckKpiCompareResults(List checkKpiCompareResults) {
        this.checkKpiCompareResults = checkKpiCompareResults;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GlobalResult{");
        sb.append("csvJmeterCurrentReport='").append(csvJmeterCurrentReport).append('\'');
        sb.append(", csvJmeterReferenceReport='").append(csvJmeterReferenceReport).append('\'');
        sb.append(", kpiFile='").append(kpiFile).append('\'');
        sb.append(", numberOfKpis=").append(numberOfKpis);
        sb.append(", numberFailed=").append(numberFailed);
        sb.append(", checkKpiCompareResults=").append(checkKpiCompareResults);
        sb.append('}');
        return sb.toString();
    }
}
