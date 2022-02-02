package com.reportapi.jasper.reports;

import com.alibaba.fastjson.JSONObject;
import net.sf.jasperreports.engine.*;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
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
                                   @RequestParam(required = false) String paymentdate
                                  ) throws JRException, SQLException, IOException {

        String p= "C:\\pdf\\"+report+".pdf";
        System.out.println(path);
        File f = new File(path);
        byte[] logo = Files.readAllBytes(Paths.get(path));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("logo", logo);
        parameters.put("balance", balance);
        parameters.put("payment", payment);
        parameters.put("receiptdate", paymentdate);


        boolean exist = Files.deleteIfExists(Paths.get("C:\\pdf\\"+report+".pdf"));
        if(exist){
            System.out.println("File exists and was deleted");
        }
        else{
            System.out.println("File does not exist and was not deleted");
        }

        File file = ResourceUtils.getFile("classpath:reports/"+report+".jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters,dataSource.getConnection());
        JasperExportManager.exportReportToPdfFile(jasperPrint,p);

        JSONObject j = new JSONObject();
        j.put("path",p);

        return j;

    }
//    @GetMapping("tryout")
//    @ResponseBody
//    public JSONObject tryout(@RequestParam String user, @RequestParam String id) throws JRException, SQLException, IOException {
//
//
//    }
}

