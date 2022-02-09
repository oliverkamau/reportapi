package com.reportapi.jasper.reports;

import com.alibaba.fastjson.JSONObject;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@RestController
public class ReportController {

    private DataSource dataSource;


    public ReportController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("getReport")
    @ResponseBody
    public JSONObject createReport(@RequestParam(required = false) Long payment,
                                   @RequestParam(required = false) String report,
                                   @RequestParam(required = false) BigDecimal balance,
                                   @RequestParam(required = false) String path,
                                   @RequestParam(required = false) String paymentdate,
                                   @RequestParam(required = false) String format,
                                   @RequestParam(required = false) Long id,
                                   @RequestParam(required = false) String dateFrom,
                                   @RequestParam(required = false) String dateTo,
                                   @RequestParam(required = false) String dateAsof
                                  ) throws JRException, SQLException, IOException {
        if(format==null){
            format = "pdf";
        }
        JSONObject j = new JSONObject();
        if(format.equalsIgnoreCase("pdf")) {
            String p = "C:\\pdf\\" + report + ".pdf";
//        String p= "C:\\pdf\\"+report+".pdf";
            System.out.println(path);
            File f = new File(path);
            byte[] logo = Files.readAllBytes(Paths.get(path));
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("logo", logo);
            parameters.put("balance", balance);
            parameters.put("payment", payment);
            parameters.put("receiptdate", paymentdate);
            parameters.put("id", id);
            parameters.put("dateFrom", dateFrom);
            parameters.put("dateTo", dateTo);
            parameters.put("dateAsof", dateAsof);

            boolean exist = Files.deleteIfExists(Paths.get("C:\\pdf\\" + report + ".pdf"));
            if (exist) {
                System.out.println("File exists and was deleted");
            } else {
                System.out.println("File does not exist and was not deleted");
            }

            File file = ResourceUtils.getFile("classpath:reports/" + report + ".jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());


            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, p);
            j.put("path",p);
        }
        else if(format.equalsIgnoreCase("excel")) {
            File f = new File(path);
            byte[] logo = Files.readAllBytes(Paths.get(path));
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("logo", logo);
            parameters.put("balance", balance);
            parameters.put("payment", payment);
            parameters.put("receiptdate", paymentdate);
            parameters.put("id", id);
            parameters.put("dateFrom", dateFrom);
            parameters.put("dateTo", dateTo);
            parameters.put("dateAsof", dateAsof);
            File file = ResourceUtils.getFile("classpath:reports/" + report + ".jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setOnePagePerSheet(true);
            configuration.setIgnoreGraphics(false);

            File outputFile = new File(report+".xlsx");
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 OutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                Exporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                exporter.setConfiguration(configuration);
                exporter.exportReport();
                byteArrayOutputStream.writeTo(fileOutputStream);
            }

            Path src = Paths.get(report+".xlsx");
            boolean exist = Files.deleteIfExists(Paths.get("C:\\excel\\" + report + ".xlsx"));
            Path dest = Paths.get("C:\\excel\\" + report + ".xlsx");
            FileUtils.copyFile(src.toFile(), dest.toFile());
            boolean rm = Files.deleteIfExists(Paths.get(report+".xlsx"));
            j.put("path","C:\\excel\\" + report + ".xlsx");

        }


        return j;

    }
//    @GetMapping("tryout")
//    @ResponseBody
//    public JSONObject tryout(@RequestParam String user, @RequestParam String id) throws JRException, SQLException, IOException {
//
//
//    }
}

