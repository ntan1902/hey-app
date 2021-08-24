package com.hey.integration;

import com.hey.integration.test_scenario.NUserTransferToOneUser;
import com.hey.integration.utils.RestTemplateUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;



@SpringBootTest
class IntegrationApplicationTests {

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Test
    public void nUserTransferToOneUser() throws IOException, InterruptedException {
        String[] HEADERS = {"username", "fullName", "email", "password"};
//        Reader in = new FileReader("./../../../resource/RegisterData.csv");
        Reader in =new FileReader(ResourceUtils.getFile("classpath:RegisterData.csv"));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(in);
        List<NUserTransferToOneUser> threads = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            CSVRecord record = records.iterator().next();
            NUserTransferToOneUser nUserTransferToOneUser = new NUserTransferToOneUser(restTemplateUtil,record.get("username"),record.get("password"),"41422bae-ceb2-4332-b2fa-e65fc55f6a9e");
//            Thread thread = new Thread(nUserTransferToOneUser);
//            thread.start();
            nUserTransferToOneUser.start();
            threads.add(nUserTransferToOneUser);
        }
        for (NUserTransferToOneUser thread : threads){
            thread.join();
        }
        long total = 0L;
        for (NUserTransferToOneUser thread: threads){
            total += thread.getBalance();
        }

        assertEquals(total,100*2000_000);
    }

}
