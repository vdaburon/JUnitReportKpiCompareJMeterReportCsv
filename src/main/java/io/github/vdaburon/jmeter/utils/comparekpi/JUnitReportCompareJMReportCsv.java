package io.github.vdaburon.jmeter.utils.comparekpi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.template.TemplateException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class JUnitReportCompareJMReportCsv {

    private static final Logger LOGGER = Logger.getLogger(JUnitReportCompareJMReportCsv.class.getName());
    public static final int K_RETURN_OK = 0;
    public static final int K_RETURN_KO = 1;
    public static final String K_JUNIT_XML_FILE_DEFAULT = "TEST-jmeter-junit-plugin-compare-jmreport.xml";
    public static final String K_CVS_JM_REPORT_CURRENT_OPT = "csvJMReportCurrent";
    public static final String K_CVS_JM_REPORT_REFERENCE_OPT = "csvJMReportReference";
    public static final String K_CSV_LABEL_COLUMN_NAME_OPT = "csvLabelColumnName";
    public static final String K_KPI_FILE_OPT = "kpiFile";
    public static final String K_JUNIT_XML_FILE_OPT = "junitFile";
    public static final String K_OUT_HTML_FILE_OPT = "htmlOutFile";
    public static final String K_OUT_DIV_HTML_FILE_OPT = "divHtmlOutFile";
    public static final String K_OUT_CSV_FILE_OPT = "csvOutFile";
    public static final String K_OUT_JSON_FILE_OPT = "jsonOutFile";

    public static final String K_EXIT_RETURN_ON_FAIL_OPT = "exitReturnOnFail";


    // column name for the kpi csv file
    public static final String K_CSV_COL_NAME_KPI = "name_kpi";
    public static final String K_CSV_COL_METRIC_CSV_COLUM_NAME = "metric_csv_column_name";
    public static final String K_CSV_COL_LABEL_REGEX = "label_regex";
    public static final String K_CSV_COL_COMPARATOR = "comparator";
    public static final String K_CSV_COL_COMPARE_TO = "compare_to";
    public static final String K_CSV_COL_THREASHOLD_DELTA = "threshold_delta";
    public static final String K_CSV_COL_COMMENT = "comment";
    public static final String K_COMPARE_TO_REFERENCE = "REFERENCE";
    // Column name for Html or CSV out file
    public static final String K_CSV_COL_OUT_RESULT = "result";
    public static final String K_CSV_COL_OUT_FAIL_MSG = "fail_msg";

    // column name Label in jmeter csv report
    public static final String K_CSV_JMREPORT_COL_LABEL_DEFAULT = "Label";

    public static final String K_FREEMARKER_HTML_TEMPLATE_DIRECTORY = "/templates_freemarker_comparekpi";
    public static final String K_FREEMARKER_HTML_TEMPLATE = "template_html_result.ftl";
    public static final String K_FREEMARKER_DIV_HTML_TEMPLATE = "template_div_result.ftl";

    public static final int K_TYPE_HTML_TEMPLATE = 1;
    public static final int K_TYPE_DIV_HTML_TEMPLATE = 2;

    private static final String K_NOT_SET = "NOT SET";
    public static final int K_FAIL_MESSAGE_SIZE_MAX = 1024;
    public static final int K_FIND_LABEL_NOT_FOUND = -1;

    public static void main(String[] args) {
        long startTimeMs = System.currentTimeMillis();

        Options options = createOptions();
        Properties parseProperties = null;

        try {
            parseProperties = parseOption(options, args);
        } catch (ParseException ex) {
            helpUsage(options);
            System.exit(K_RETURN_KO);
        }
        int exitReturn = K_RETURN_KO;

        String csvJmeterCurrentReport = K_NOT_SET;
        String csvJmeterReferenceReport = K_NOT_SET;
        String csvLabelColumnName = K_CSV_JMREPORT_COL_LABEL_DEFAULT;
        String kpiFile = K_NOT_SET;
        String junitFile = K_JUNIT_XML_FILE_DEFAULT;
        String htmlFile = K_NOT_SET;
        String divHtmlFile = K_NOT_SET;
        String csvFile = K_NOT_SET;
        String jsonFile = K_NOT_SET;
        boolean exitOnFailKpi = false;

        String sTmp;
        sTmp = (String) parseProperties.get(K_CVS_JM_REPORT_CURRENT_OPT);
        if (sTmp != null) {
            csvJmeterCurrentReport = sTmp;
        }

        sTmp = (String) parseProperties.get(K_CVS_JM_REPORT_REFERENCE_OPT);
        if (sTmp != null) {
            csvJmeterReferenceReport = sTmp;
        }

        sTmp = (String) parseProperties.get(K_CSV_LABEL_COLUMN_NAME_OPT);
        if (sTmp != null) {
            csvLabelColumnName = sTmp;
        }

        sTmp = (String) parseProperties.get(K_KPI_FILE_OPT);
        if (sTmp != null) {
            kpiFile = sTmp;
        }

        sTmp = (String) parseProperties.get(K_JUNIT_XML_FILE_OPT);
        if (sTmp != null) {
            junitFile = sTmp;
        }

        sTmp = (String) parseProperties.get(K_OUT_HTML_FILE_OPT);
        if (sTmp != null && sTmp.length() > 1) {
            htmlFile = sTmp;
        }

        sTmp = (String) parseProperties.get(K_OUT_DIV_HTML_FILE_OPT);
        if (sTmp != null && sTmp.length() > 1) {
            divHtmlFile = sTmp;
        }

        sTmp = (String) parseProperties.get(K_OUT_CSV_FILE_OPT);
        if (sTmp != null && sTmp.length() > 1) {
            csvFile = sTmp;
        }

        sTmp = (String) parseProperties.get(K_OUT_JSON_FILE_OPT);
        if (sTmp != null && sTmp.length() > 1) {
            jsonFile = sTmp;
        }

        sTmp = (String) parseProperties.get(K_EXIT_RETURN_ON_FAIL_OPT);
        if (sTmp != null) {
            exitOnFailKpi = Boolean.parseBoolean(sTmp);
            LOGGER.fine("exitOnFailKpi:" + exitOnFailKpi);
        }

        boolean isKpiFail = false;
        LOGGER.info("Parameters CLI:" + parseProperties);
        try {
            isKpiFail = compareCsvJMReportWithKpiRules(csvJmeterCurrentReport, csvJmeterReferenceReport, csvLabelColumnName, kpiFile, junitFile, htmlFile, divHtmlFile, csvFile, jsonFile);
            LOGGER.info("isKpiFail=" + isKpiFail);
        } catch (Exception ex) {
            LOGGER.warning(ex.toString());
            ex.printStackTrace();
            exitReturn = K_RETURN_KO;
        }
        if (exitOnFailKpi && isKpiFail) {
            // at least one kpi rule failure => exit 1
            exitReturn = K_RETURN_KO;
            LOGGER.info("exitOnFailKpi=" + exitOnFailKpi + " and isKpiFail=" + isKpiFail + " set program exit=" + exitReturn);
        } else {
            exitReturn = K_RETURN_OK;
        }
        long endTimeMs = System.currentTimeMillis();
        LOGGER.info("Duration ms=" + (endTimeMs - startTimeMs));
        LOGGER.info("End main (exit " + exitReturn + ")");

        System.exit(exitReturn);
    }

    /**
     * Compare 2 JMeter Report values with KPIs
     * @param csvJmeterCurrentReport the JMeter Report Current CSV format
     * @param csvJmeterReferenceReport the JMeter Report Reference CSV format
     * @param csvLabelColumnName the Label Column Name (default : Label)
     * @param kpiFile the kpi contains kpi declaration and will save the result
     * @param junitFile the JUnit XML out file to create
     * @return is Fail true or false, a kpi is fail or not
     * @throws IOException file exception
     * @throws ParserConfigurationException error reading csv file
     * @throws TransformerException error writing JUnit XML file
     */
    private static boolean compareCsvJMReportWithKpiRules(String csvJmeterCurrentReport, String csvJmeterReferenceReport, String csvLabelColumnName, String kpiFile, String junitFile, String htmlFile, String divHtmlFile, String csvFile, String jsonFile) throws IOException, ParserConfigurationException, TransformerException, TemplateException {
        boolean isFail = false;
        List<CSVRecord> csvJMReportLinesCurrent = UtilsCsvFile.readCsvFile(csvJmeterCurrentReport);
        List<CSVRecord> csvJMReportLinesReference = UtilsCsvFile.readCsvFile(csvJmeterReferenceReport);
        List<MatchLabelsFiles> listMatchLabelsFiles = matchLabels2Files(csvJMReportLinesCurrent, csvJMReportLinesReference, csvLabelColumnName);
        GlobalResult globalResult = new GlobalResult();
        List<CheckKpiCompareResult> checkKpiCompareResults = new ArrayList<>();
        globalResult.setCheckKpiCompareResults(checkKpiCompareResults);
        globalResult.setCsvJmeterCurrentReport(csvJmeterCurrentReport);
        globalResult.setCsvJmeterReferenceReport(csvJmeterReferenceReport);
        globalResult.setKpiFile(kpiFile);

        int nbFailed = 0;

        int countSameLabelsIn2File =countSameLabelsIn2Files(listMatchLabelsFiles);
        if (countSameLabelsIn2File <= 1) {
            // 1 for last line TOTAL
            LOGGER.warning("Only " + countSameLabelsIn2File + " same label in the 2 files ! Comparison with a reference file does not work.");
        } else {
            LOGGER.info("Count " + countSameLabelsIn2File + " same labels in the 2 files for " + listMatchLabelsFiles.size() + " labels in the first file");
        }
        List<CSVRecord> csvKpiLines = UtilsCsvFile.readCsvFile(kpiFile);

        Document document = UtilsJUnitXml.createJUnitRootDocument();


        for (int i = 0; i < csvKpiLines.size(); i++) {
            CSVRecord recordKpiLine = csvKpiLines.get(i);
            if (recordKpiLine.size() < 3) {
                // not enough columns in the kpi declaration, continue to another line
                continue;
            }

            String valueGrouped = "";
            String withReference = "REFERENCE Value)";
            CheckKpiCompareResult checkKpiResult = verifyCompareKpi(recordKpiLine, csvJMReportLinesCurrent, csvJMReportLinesReference, csvLabelColumnName, listMatchLabelsFiles);
            if (checkKpiResult.isKpiFail()) {
                // a fail result
                isFail = true;
                nbFailed++;
                if (K_COMPARE_TO_REFERENCE.equals(checkKpiResult.getCompareTo())) {
                    valueGrouped = "(";
                }
                if (checkKpiResult.getThresholdDelta().length() > 0) {
                    withReference = " + " + withReference;
                }
                String className = checkKpiResult.getMetricCsvColumnName() + " (" + checkKpiResult.getLabelRegex() + ") " + checkKpiResult.getComparator() + " " + valueGrouped + checkKpiResult.getThresholdDelta();
                if (K_COMPARE_TO_REFERENCE.equals(checkKpiResult.getCompareTo())) {
                    className += withReference;
                }
                UtilsJUnitXml.addTestCaseFailure(document,checkKpiResult.getNameKpi(), className, checkKpiResult.getFailMessage());
            } else {
                // success result
                if (K_COMPARE_TO_REFERENCE.equals(checkKpiResult.getCompareTo())) {
                    valueGrouped = "(";
                }
                if (checkKpiResult.getThresholdDelta().length() > 0) {
                    withReference = " + " + withReference;
                }
                String className = checkKpiResult.getMetricCsvColumnName() + " (" + checkKpiResult.getLabelRegex() + ") " + checkKpiResult.getComparator() + " " + valueGrouped + checkKpiResult.getThresholdDelta();
                if (K_COMPARE_TO_REFERENCE.equals(checkKpiResult.getCompareTo())) {
                    className += withReference;
                }
                UtilsJUnitXml.addTestCaseOk(document,checkKpiResult.getNameKpi(), className);
            }
            globalResult.getCheckKpiCompareResults().add(checkKpiResult);
        }

        globalResult.setNumberOfKpis(csvKpiLines.size());
        globalResult.setNumberFailed(nbFailed);

        LOGGER.info("Write junitFile=" + junitFile);
        UtilsJUnitXml.saveXmFile(document, junitFile);
        if (!K_NOT_SET.equals(htmlFile)) {
            LOGGER.info("Write html file=" + htmlFile);
            UtilsHtml.saveHtmlFile(globalResult, htmlFile, K_TYPE_HTML_TEMPLATE);
        }

        if (!K_NOT_SET.equals(divHtmlFile)) {
            LOGGER.info("Write Div Html file=" + divHtmlFile);
            UtilsHtml.saveHtmlFile(globalResult, divHtmlFile, K_TYPE_DIV_HTML_TEMPLATE);
        }

        if (!K_NOT_SET.equals(csvFile)) {
            LOGGER.info("Write csv file=" + csvFile);
            UtilsCsvFile.saveCsvFile(globalResult, csvFile);
        }

        if (!K_NOT_SET.equals(jsonFile)) {
            LOGGER.info("Write json file=" + jsonFile);
            UtilsJsonFile.saveJsonFile(globalResult, jsonFile);
        }

        return isFail;
    }

    /**
     * verify one kpi for lines in csv JMeter Report current and reference
     * @param recordKpiLine a kpi line to verify
     * @param csvJMReportLinesCurrent all lines in JMeter Report Current
     * @param csvJMReportLinesReference all lines in JMeter Report Reference
     * @param csvLabelColumnName the Label Column name in the JMeter Report (usually : Label)
     * @return the result of the kpi verification and the failure message if kpi fail
     */
    private static CheckKpiCompareResult verifyCompareKpi(CSVRecord recordKpiLine, List<CSVRecord> csvJMReportLinesCurrent, List<CSVRecord> csvJMReportLinesReference, String csvLabelColumnName, List<MatchLabelsFiles> listMatchLabelsFiles) {
        CheckKpiCompareResult checkKpiResult = new CheckKpiCompareResult();
        String nameKpi = recordKpiLine.get(K_CSV_COL_NAME_KPI);
        checkKpiResult.setNameKpi(nameKpi.trim());

        String metricCsvColumnName = recordKpiLine.get(K_CSV_COL_METRIC_CSV_COLUM_NAME);
        checkKpiResult.setMetricCsvColumnName(metricCsvColumnName.trim());

        String labelRegex = recordKpiLine.get(K_CSV_COL_LABEL_REGEX);
        checkKpiResult.setLabelRegex(labelRegex);

        String comparator = recordKpiLine.get(K_CSV_COL_COMPARATOR);
        checkKpiResult.setComparator(comparator.trim());

        String comparatorTo = recordKpiLine.get(K_CSV_COL_COMPARE_TO);
        comparatorTo = comparatorTo.trim();
        checkKpiResult.setCompareTo(comparatorTo);
        boolean isCompareToReference = false;
        if (K_COMPARE_TO_REFERENCE.equalsIgnoreCase(comparatorTo)) {
            isCompareToReference = true;
        }

        String thresholdDelta = recordKpiLine.get(K_CSV_COL_THREASHOLD_DELTA);
        checkKpiResult.setThresholdDelta(thresholdDelta.trim());

        String comment = recordKpiLine.get(K_CSV_COL_COMMENT);
        checkKpiResult.setComment(comment.trim());

        checkKpiResult.setKpiFail(false);
        checkKpiResult.setFailMessage("");

        LOGGER.fine("checkKpiResult=" + checkKpiResult);
        Pattern patternRegex = Pattern.compile(labelRegex) ;

        boolean isFirstFail = true;
        for (int i = 0; i < csvJMReportLinesCurrent.size(); i++) {
            CSVRecord recordJMReportLineCurrent = csvJMReportLinesCurrent.get(i);
            MatchLabelsFiles matchLabelsFiles = listMatchLabelsFiles.get(i);
            String label = recordJMReportLineCurrent.get(csvLabelColumnName);

            Matcher matcherRegex = patternRegex.matcher(label) ;
            if (matcherRegex.matches()) {
                LOGGER.fine("recordJMReportLineCurrent num " + i + " =" + recordJMReportLineCurrent);
                String sMetricCurrent = recordJMReportLineCurrent.get(metricCsvColumnName);
                LOGGER.fine("sMetricCurrent=<" + sMetricCurrent + ">");
                double dMetricCurrent = 0;
                if (sMetricCurrent.endsWith("%")) {
                    sMetricCurrent = sMetricCurrent.replace('%', ' ');
                    dMetricCurrent = Double.parseDouble(sMetricCurrent) / 100;
                } else {
                    dMetricCurrent = Double.parseDouble(sMetricCurrent);
                }
                LOGGER.fine("dMetricCurrent=<" + dMetricCurrent + ">");

                CSVRecord recordJMReportLineRef = null;
                double dMetricRef = 0;
                int lineRef = K_FIND_LABEL_NOT_FOUND;
                if (isCompareToReference) {
                    lineRef = matchLabelsFiles.getLineFile2();
                    if (lineRef != K_FIND_LABEL_NOT_FOUND) {
                        recordJMReportLineRef = csvJMReportLinesReference.get(lineRef);
                        String sMetricRef = recordJMReportLineRef.get(metricCsvColumnName);
                        if (sMetricRef.endsWith("%")) {
                            sMetricRef = sMetricRef.replace('%', ' ');
                            dMetricRef = Double.parseDouble(sMetricRef) / 100;
                        } else {
                            dMetricRef = Double.parseDouble(sMetricRef);
                        }
                    } else {
                        // no comparaison with Reference because no match label in reference file so continue
                        continue;
                    }
                }

                String sThresholdDeltaTmp = checkKpiResult.getThresholdDelta();
                boolean isDeltaPctValue = false;
                if (sThresholdDeltaTmp.endsWith("%")) {
                    isDeltaPctValue = true;
                    sThresholdDeltaTmp = sThresholdDeltaTmp.replace('%', ' ');
                }

                boolean isNoThresholdDelta = false;
                double dThresholdDelta = 0;
                if (sThresholdDeltaTmp.length() == 0) {
                    isNoThresholdDelta = true;
                } else {
                    dThresholdDelta = Double.parseDouble(sThresholdDeltaTmp);
                }

                double dThreshold = 0;
                boolean isVerifyKpi = true;
                if (!isCompareToReference && isDeltaPctValue) {
                    isVerifyKpi = false;
                    LOGGER.warning(K_CSV_COL_THREASHOLD_DELTA + " with sign % is only for comparate to " + K_COMPARE_TO_REFERENCE + ", use value between 0 and 1 (E.g : 0.05 for 5%)");
                }
                if (isCompareToReference && isDeltaPctValue && !isNoThresholdDelta) {
                    // +5% => 1.05, or -10% = 0.90
                    dThreshold = (1 + (dThresholdDelta/100)) * dMetricRef;
                }

                if (isCompareToReference && !isDeltaPctValue && !isNoThresholdDelta) {
                    dThreshold = dMetricRef + dThresholdDelta;
                }

                if (isCompareToReference && isDeltaPctValue && isNoThresholdDelta) {
                    dThreshold = dMetricRef;
                }

                if (isCompareToReference && !isDeltaPctValue && isNoThresholdDelta) {
                    dThreshold = dMetricRef;
                }

                if (!isCompareToReference && !isNoThresholdDelta) {
                    dThreshold = dThresholdDelta;
                }

                if (!isCompareToReference && isNoThresholdDelta) {
                    isVerifyKpi = false;
                    LOGGER.warning(K_CSV_COL_THREASHOLD_DELTA + " must not be empty when " + K_CSV_COL_COMPARE_TO + " is empty");
                }

                if (!isVerifyKpi) {
                    continue;
                }

                String sComparator = checkKpiResult.getComparator();
                switch (sComparator) {
                    case "<":
                        if (dMetricCurrent < dThreshold) {
                            LOGGER.fine(dMetricCurrent + sComparator + dThreshold);
                        } else {
                            if (isFirstFail) {
                                isFirstFail = false;
                                String failMessage = "Actual value " +  dMetricCurrent + " exceeds or equals threshold " + dThreshold + " for samples matching \"" + labelRegex + "\"; fail label(s) \"" + label + "\""; // Actual value 2908,480000 exceeds threshold 2500,000000 for samples matching "@SC01_P03_DUMMY"
                                checkKpiResult.setKpiFail(true);
                                checkKpiResult.setFailMessage(failMessage);
                            } else {
                                String failMessage = concatFailMessage(checkKpiResult, label);
                                checkKpiResult.setFailMessage(failMessage);
                            }
                        }
                        break;
                    case "<=":
                        if (dMetricCurrent <= dThreshold) {
                            LOGGER.fine(dMetricCurrent + sComparator + dThreshold);
                        } else {
                            if (isFirstFail) {
                                isFirstFail = false;
                                String failMessage = "Actual value " + dMetricCurrent + " exceeds threshold " + dThreshold + " for samples matching \"" + labelRegex + "\"; fail label(s) \"" + label + "\"";
                                checkKpiResult.setKpiFail(true);
                                checkKpiResult.setFailMessage(failMessage);
                            } else {
                                String failMessage = concatFailMessage(checkKpiResult, label);
                                checkKpiResult.setFailMessage(failMessage);
                            }
                        }
                        break;
                    case ">":
                        if (dMetricCurrent > dThreshold) {
                            LOGGER.fine(dMetricCurrent + sComparator + dThreshold);
                        } else {
                            if (isFirstFail) {
                                isFirstFail = false;
                                String failMessage = "Actual value " + dMetricCurrent + " is less or equals then threshold " + dThreshold + " for samples matching \"" + labelRegex + "\"; fail label(s) \"" + label + "\"";
                                checkKpiResult.setKpiFail(true);
                                checkKpiResult.setFailMessage(failMessage);
                            } else {
                                String failMessage = concatFailMessage(checkKpiResult, label);
                                checkKpiResult.setFailMessage(failMessage);
                            }
                        }
                        break;
                    case ">=":
                        if (dMetricCurrent >= dThreshold) {
                            LOGGER.fine(dMetricCurrent + sComparator + dThreshold);
                        } else {
                            if (isFirstFail) {
                                isFirstFail = false;
                                String failMessage = "Actual value " + dMetricCurrent + " is less threshold " + dThreshold + " for samples matching \"" + labelRegex + "\"; fail label(s) \"" + label + "\"";
                                checkKpiResult.setKpiFail(true);
                                checkKpiResult.setFailMessage(failMessage);
                            } else {
                                String failMessage = concatFailMessage(checkKpiResult, label);
                                checkKpiResult.setFailMessage(failMessage);
                            }
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid comparator:" + sComparator);
                }
            }
        }
        return checkKpiResult;
    }

    private static String concatFailMessage(CheckKpiCompareResult checkKpiResult, String label) {
        String failMessage = checkKpiResult.getFailMessage();
        if ((failMessage.length() + label.length()) < K_FAIL_MESSAGE_SIZE_MAX) {
            failMessage += ", \"" + label + "\"";
        } else {
            if (!failMessage.endsWith(" ...")) {
                failMessage += " ...";
            }
        }
        return failMessage;
    }

    private static List<MatchLabelsFiles> matchLabels2Files(List<CSVRecord> csvJMReportLinesCurrent, List<CSVRecord> csvJMReportLinesReference, String csvLabelColumnName) {
        int currentSize = csvJMReportLinesCurrent.size();
        List<MatchLabelsFiles> listMatchLabelsFiles = new ArrayList<>(currentSize);

        for (int i = 0; i < currentSize; i++) {
            CSVRecord recordCurrentLine = csvJMReportLinesCurrent.get(i);
            String label = recordCurrentLine.get(csvLabelColumnName);
            MatchLabelsFiles matchLabelsFiles = new MatchLabelsFiles();
            matchLabelsFiles.setLabel(label);
            matchLabelsFiles.setLineFile1(i);
            int iFind = getLineWithLabel(csvJMReportLinesReference, label, csvLabelColumnName);
            matchLabelsFiles.setLineFile2(iFind);
            listMatchLabelsFiles.add(matchLabelsFiles);
        }
        return listMatchLabelsFiles;
    }

    private static int getLineWithLabel(List<CSVRecord> csvJMReportLines, String labelToFind, String csvLabelColumnName) {
        int iReturn = K_FIND_LABEL_NOT_FOUND; // NOT_FOUND
        int size = csvJMReportLines.size();
        boolean isFind = false;
        int i = 0;
        while (i < size && !isFind) {
            CSVRecord csvLine = csvJMReportLines.get(i);
            String label = csvLine.get(csvLabelColumnName);
            if (label != null && label.equals(labelToFind)) {
                isFind = true;
                iReturn = i;
            } else {
                i++;
            }
        }
        return iReturn;
    }

    private static int countSameLabelsIn2Files(List<MatchLabelsFiles> matchLabels2Files) {
        int size = matchLabels2Files.size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            MatchLabelsFiles matchLabelsFiles = matchLabels2Files.get(i);
            if (matchLabelsFiles.getLineFile2() != K_FIND_LABEL_NOT_FOUND) {
                count++;
            }
        }
        return count;
    }

    /**
     * If incorrect parameter or help, display usage
     * @param options  options and cli parameters
     */
    private static void helpUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        String footer = "E.g : java -jar junit-reporter-kpi-compare-jmeter-report-csv-<version>-jar-with-dependencies.jar -" + K_CVS_JM_REPORT_CURRENT_OPT + " summary.csv  -" +
                K_CVS_JM_REPORT_REFERENCE_OPT + " summary_ref.csv -" +
                K_KPI_FILE_OPT + " kpi_compare.csv -" + K_EXIT_RETURN_ON_FAIL_OPT + " true\n";
        footer += "or more parameters : java -jar junit-reporter-kpi-compare-jmeter-report-csv-<version>-jar-with-dependencies.jar -" + K_CVS_JM_REPORT_CURRENT_OPT + " aggreagate.csv  -" +
                K_CVS_JM_REPORT_REFERENCE_OPT + " aggregate_ref.csv -" +
                K_CSV_LABEL_COLUMN_NAME_OPT + " Label -" + K_KPI_FILE_OPT + " kpi_compare.csv -" + K_JUNIT_XML_FILE_OPT + " junit_compare.xml -" +
                K_OUT_HTML_FILE_OPT + " result.html -" + K_OUT_DIV_HTML_FILE_OPT + " div_result.html -" + K_OUT_CSV_FILE_OPT + " result.csv -" + K_OUT_JSON_FILE_OPT + " result.json -" +
                K_EXIT_RETURN_ON_FAIL_OPT + " true\n";
        formatter.printHelp(140, JUnitReportCompareJMReportCsv.class.getName(),
                JUnitReportCompareJMReportCsv.class.getName(), options, footer, true);
    }

    /**
     * Parse options enter in command line interface
     * @param optionsP parameters to parse
     * @param args parameters from cli
     * @return properties saved
     * @throws ParseException parsing error
     * @throws MissingOptionException mandatory parameter not set
     */
    private static Properties parseOption(Options optionsP, String[] args) throws ParseException, MissingOptionException {

        Properties properties = new Properties();

        CommandLineParser parser = new DefaultParser();

        // parse the command line arguments

        CommandLine line = parser.parse(optionsP, args);

        if (line.hasOption("help")) {
            properties.setProperty("help", "help value");
            return properties;
        }

        if (line.hasOption(K_CVS_JM_REPORT_CURRENT_OPT)) {
            properties.setProperty(K_CVS_JM_REPORT_CURRENT_OPT, line.getOptionValue(K_CVS_JM_REPORT_CURRENT_OPT));
        }

        if (line.hasOption(K_CVS_JM_REPORT_REFERENCE_OPT)) {
            properties.setProperty(K_CVS_JM_REPORT_REFERENCE_OPT, line.getOptionValue(K_CVS_JM_REPORT_REFERENCE_OPT));
        }

        if (line.hasOption(K_CSV_LABEL_COLUMN_NAME_OPT)) {
            properties.setProperty(K_CSV_LABEL_COLUMN_NAME_OPT, line.getOptionValue(K_CSV_LABEL_COLUMN_NAME_OPT));
        }

        if (line.hasOption(K_KPI_FILE_OPT)) {
            properties.setProperty(K_KPI_FILE_OPT, line.getOptionValue(K_KPI_FILE_OPT));
        }

        if (line.hasOption(K_JUNIT_XML_FILE_OPT)) {
            properties.setProperty(K_JUNIT_XML_FILE_OPT, line.getOptionValue(K_JUNIT_XML_FILE_OPT));
        }

        if (line.hasOption(K_OUT_HTML_FILE_OPT)) {
            properties.setProperty(K_OUT_HTML_FILE_OPT, line.getOptionValue(K_OUT_HTML_FILE_OPT));
        }

        if (line.hasOption(K_OUT_DIV_HTML_FILE_OPT)) {
            properties.setProperty(K_OUT_DIV_HTML_FILE_OPT, line.getOptionValue(K_OUT_DIV_HTML_FILE_OPT));
        }

        if (line.hasOption(K_OUT_CSV_FILE_OPT)) {
            properties.setProperty(K_OUT_CSV_FILE_OPT, line.getOptionValue(K_OUT_CSV_FILE_OPT));
        }

        if (line.hasOption(K_OUT_JSON_FILE_OPT)) {
            properties.setProperty(K_OUT_JSON_FILE_OPT, line.getOptionValue(K_OUT_JSON_FILE_OPT));
        }

        if (line.hasOption(K_EXIT_RETURN_ON_FAIL_OPT)) {
            properties.setProperty(K_EXIT_RETURN_ON_FAIL_OPT, line.getOptionValue(K_EXIT_RETURN_ON_FAIL_OPT));
        }

        return properties;
    }
    /**
     * Options or parameters for the command line interface
     * @return all options
     **/
    private static Options createOptions() {
        Options options = new Options();

        Option helpOpt = Option.builder("help").hasArg(false).desc("Help and show parameters").build();

        options.addOption(helpOpt);

        Option csvJmeterReportCurrentFileOpt = Option.builder(K_CVS_JM_REPORT_CURRENT_OPT).argName(K_CVS_JM_REPORT_CURRENT_OPT)
                .hasArg(true)
                .required(true)
                .desc("JMeter report current csv file (E.g : summary.csv or aggregate.csv or synthesis.csv)")
                .build();
        options.addOption(csvJmeterReportCurrentFileOpt);

        Option csvJmeterReportReferenceFileOpt = Option.builder(K_CVS_JM_REPORT_REFERENCE_OPT).argName(K_CVS_JM_REPORT_REFERENCE_OPT)
                .hasArg(true)
                .required(true)
                .desc("JMeter report reference csv file (E.g : summary_ref.csv or aggregate_ref.csv or synthesis_ref.csv)")
                .build();
        options.addOption(csvJmeterReportReferenceFileOpt);

        Option csvLabelColumnNameOpt = Option.builder(K_CSV_LABEL_COLUMN_NAME_OPT).argName(K_CSV_LABEL_COLUMN_NAME_OPT)
                .hasArg(true)
                .required(false)
                .desc("Label Column Name in CSV JMeter Report (Default : " + K_CSV_JMREPORT_COL_LABEL_DEFAULT + ")")
                .build();
        options.addOption(csvLabelColumnNameOpt);

        Option kpiFileOpt = Option.builder(K_KPI_FILE_OPT).argName(K_KPI_FILE_OPT)
                .hasArg(true)
                .required(true)
                .desc("KPI file contains rule to check and compare to reference value (E.g : kpi_compare.csv)")
                .build();
        options.addOption(kpiFileOpt);

        Option junitXmlOutOpt = Option.builder(K_JUNIT_XML_FILE_OPT).argName(K_JUNIT_XML_FILE_OPT)
                .hasArg(true)
                .required(false)
                .desc("junit file name out (Default : " + K_JUNIT_XML_FILE_DEFAULT + ")")
                .build();
        options.addOption(junitXmlOutOpt);

        Option htmlOutOpt = Option.builder(K_OUT_HTML_FILE_OPT).argName(K_OUT_HTML_FILE_OPT)
                .hasArg(true)
                .required(false)
                .desc("Html out file result optional (E.g: result.html)")
                .build();
        options.addOption(htmlOutOpt);

        Option divHtmlOutOpt = Option.builder(K_OUT_DIV_HTML_FILE_OPT).argName(K_OUT_DIV_HTML_FILE_OPT)
                .hasArg(true)
                .required(false)
                .desc("Div Partial Html Page out file result optional (E.g: div_result.html), to include in an another HTML Page")
                .build();
        options.addOption(divHtmlOutOpt);

        Option csvOutOpt = Option.builder(K_OUT_CSV_FILE_OPT).argName(K_OUT_CSV_FILE_OPT)
                .hasArg(true)
                .required(false)
                .desc("Csv out file result optional (E.g: result.csv)")
                .build();
        options.addOption(csvOutOpt);

        Option jsonOutOpt = Option.builder(K_OUT_JSON_FILE_OPT).argName(K_OUT_JSON_FILE_OPT)
                .hasArg(true)
                .required(false)
                .desc("Json out file result optional (E.g: result.json)")
                .build();
        options.addOption(jsonOutOpt);

        Option exitReturnOnFailOpt = Option.builder(K_EXIT_RETURN_ON_FAIL_OPT).argName(K_EXIT_RETURN_ON_FAIL_OPT)
                .hasArg(true)
                .required(false)
                .desc("if true then when kpi fail then create JUnit XML file and program return exit 1 (KO); if false (Default) then create JUnit XML File and exit 0 (OK)")
                .build();
        options.addOption(exitReturnOnFailOpt);

        return options;
    }
}
