package com.jp.parkapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JasperService {

    @Autowired
    ResourceLoader resourceLoader; //ler o recursos do projeto
    @Autowired
    DataSource dataSource; //fornece o objeto de conexão com banco de dados (refere-se ao datasource do application.properties


    private Map<String, Object> params = new HashMap<>();

    private static final String JASPER_DIRETORIO = "classpath:reports/";

    public void addParms(String key, Object value) {
        params.put("IMAGE_DIR", JASPER_DIRETORIO);
        params.put("REPORT_LOCALE", new Locale("pt", "BR"));
        params.put(key, value);
    }

    public byte[] createPdf() {
        byte[] bytes = null;
        try {
            Resource resource = resourceLoader.getResource(JASPER_DIRETORIO.concat("parkings.jasper"));
            InputStream stream = resource.getInputStream();
            JasperPrint jasperPrint = JasperFillManager.fillReport(stream, params, dataSource.getConnection());
            bytes = JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (IOException | JRException | SQLException e) {
            log.error("Jasper Resports ----> ", e.getCause());
            throw new RuntimeException(e.getMessage());
        }
        return bytes;
    }
}
