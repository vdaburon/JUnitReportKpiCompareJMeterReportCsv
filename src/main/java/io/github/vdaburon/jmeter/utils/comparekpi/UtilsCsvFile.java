package io.github.vdaburon.jmeter.utils.comparekpi;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class UtilsCsvFile {
    /**
     * Read all lines in a csv file
     * @param fileIn the csv file name to read
     * @return a ArrayList of CSVRecord contains all lines
     * @throws IOException error when read the CSV file
     */
    public static List<CSVRecord> readCsvFile(String fileIn) throws IOException {
        Reader in = new FileReader(fileIn);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

        List<CSVRecord> listRecordsBetweenFirstAndLast = new ArrayList();

        for (CSVRecord record : records) {
            listRecordsBetweenFirstAndLast.add(record);
        }
        in.close();
        return listRecordsBetweenFirstAndLast;
    }

    /**
     * Write the KPIs Lines Result in a CSV file (
     * @param globalResult Global KPIs result
     * @param csvFile Csv out file result
     * @throws IOException
     */
    public static void saveCsvFile(GlobalResult globalResult, String csvFile) throws IOException {
        String[] headers = {
                JUnitReportCompareJMReportCsv.K_CSV_COL_NAME_KPI,
                JUnitReportCompareJMReportCsv.K_CSV_LABEL_COLUMN_NAME_OPT,
                JUnitReportCompareJMReportCsv.K_CSV_COL_LABEL_REGEX,
                JUnitReportCompareJMReportCsv.K_CSV_COL_COMPARATOR,
                JUnitReportCompareJMReportCsv.K_CSV_COL_COMPARE_TO,
                JUnitReportCompareJMReportCsv.K_CSV_COL_THREASHOLD_DELTA,
                JUnitReportCompareJMReportCsv.K_CSV_COL_OUT_RESULT,
                JUnitReportCompareJMReportCsv.K_CSV_COL_OUT_FAIL_MSG
        };

        FileWriter fileWrite = new FileWriter(csvFile);

        CSVFormat csvFormat = CSVFormat.RFC4180.builder()
                .setHeader(headers)
                .build();
        List checkKpiCompareResults = globalResult.getCheckKpiCompareResults();
        CSVPrinter printer = new CSVPrinter(fileWrite, csvFormat);
        for (int i = 0; i < checkKpiCompareResults.size(); i++) {
            CheckKpiCompareResult checkKpiCompareResult = (CheckKpiCompareResult) checkKpiCompareResults.get(i);
            String sResult = checkKpiCompareResult.isKpiFail()?"fail":"sucess";
            String sFailMessage = checkKpiCompareResult.getFailMessage() != null?checkKpiCompareResult.getFailMessage():"";

            printer.printRecord(checkKpiCompareResult.getNameKpi(), checkKpiCompareResult.getMetricCsvColumnName(), checkKpiCompareResult.getLabelRegex(),
                    checkKpiCompareResult.getComparator(),checkKpiCompareResult.getCompareTo(),
                    checkKpiCompareResult.getThresholdDelta(), sResult, sFailMessage);
        }
        printer.close();
    }
}
