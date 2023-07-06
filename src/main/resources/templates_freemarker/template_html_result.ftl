<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <meta content="text/html; charset=utf-8">
  <title>HTML KPIs Result From Compare JMeter Report Csv</title>
  </head>
  <body>
<style>
table.table_jp {border-collapse: collapse;}
table.table_jp, table.table_jp th, table.table_jp tr, table.table_jp td {
border: 1px solid black;
text-align: left;
font-family: sans-serif;
font-size:medium; }
table.table_jp th:{background-color: #f8f8f8;}
table.table_jp tr:nth-child(even) {background-color: #f2f2f2;}
table.table_jp td:nth-child(5) { text-align: right; }
</style>
<h1>HTML KPIs Result From JMeter Report Csv</h1>
<h2>Files In<h2>
<table class="table_jp">
  <tr><td>File with KPIs</td><td>${globalResult.kpiFile}</td></tr>
  <tr><td>File CSV Current Report</td><td>${globalResult.csvJmeterCurrentReport}</td></tr>
  <tr><td>File CSV Reference Report</td><td>${globalResult.csvJmeterReferenceReport }</td></tr>
  </table>
<br>
<h2>Test Summary</h2>
  <table class="table_jp">
  <tr><td>Number of failed tests</td><td <#if (globalResult.numberFailed &gt; 0)>style="color:Red;bold"</#if>><b>${globalResult.numberFailed}</b></td></tr>
  <tr><td>Number of tests</td><td><b>${globalResult.numberOfKpis}</b></td></tr>
  </table>
<br>
<h2>Table KPIs Results<h2>
  <table class="table_jp">
  <tr><th>name_kpi</th><th>metric_csv_column_name</th><th>label_regex</th><th>comparator</th><th>compareTo</th><th>thresholdDelta</th><th>result</th><th>fail_msg</th></tr>
  <#list globalResult.checkKpiCompareResults as checkKpiCompareResult>
    <tr>
        <td>${checkKpiCompareResult.nameKpi}</td>
        <td>${checkKpiCompareResult.metricCsvColumnName}</td>
        <td>${checkKpiCompareResult.labelRegex}</td>
        <td>${checkKpiCompareResult.comparator}</td>
        <td>${checkKpiCompareResult.compareTo}</td>
        <td>${checkKpiCompareResult.thresholdDelta}</td>
        <td><#if checkKpiCompareResult.kpiFail>fail<#else>sucess</#if></td>
        <td><#if checkKpiCompareResult.kpiFail>${checkKpiCompareResult.failMessage}</#if></td>
    </tr>
  </#list>
  </table>  
  </body>
</html>
