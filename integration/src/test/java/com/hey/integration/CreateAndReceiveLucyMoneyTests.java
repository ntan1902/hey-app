package com.hey.integration;

import com.hey.integration.entity.Wallet;
import com.hey.integration.repository.TransferStatementRepository;
import com.hey.integration.repository.UserRepository;
import com.hey.integration.repository.WalletRepository;
import com.hey.integration.test_scenario.CreateLuckyMoneyThread;
import com.hey.integration.test_scenario.ReceiveLuckyMoneyThread;
import com.hey.integration.utils.RestTemplateUtil;
import com.hey.integration.utils.RestTemplateUtilImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hey.integration.constants.Constant.BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CreateAndReceiveLucyMoneyTests {

    @Autowired
    TransferStatementRepository transferStatementRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletRepository walletRepository;

    @Test
    public void createLuckyMoney() throws IOException, InterruptedException {
        long expected = walletRepository.sumAllBalance();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

        RestTemplateUtil restTemplateUtil = new RestTemplateUtilImpl(restTemplate);
        String[] HEADERS = {"username", "fullName", "email", "password"};
        Reader in = new FileReader(ResourceUtils.getFile("classpath:Data_100.csv"));
        List<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(in)
                .getRecords();
        restTemplateUtil.login(records.get(0).get("username"),records.get(0).get("password"));
        Map<String, Object> getChatListRes = restTemplateUtil.getChatList();
        Map<String, Map<String,Object>> payload = (Map<String, Map<String, Object>>) getChatListRes.get("payload");
        List<Object> items = (List<Object>) payload.get("items");
        Map<String, Object> item0 = (Map<String, Object>)items.get(0);
        String sessionId = (String)item0.get("sessionId");

//        List<CreateLuckyMoneyThread> createLuckyMoneyThreads = new ArrayList<>();
//
//        records.forEach(record ->{
//            CreateLuckyMoneyThread createLuckyMoneyThread = new CreateLuckyMoneyThread(record.get("username"), record.get("password"),sessionId);
//            createLuckyMoneyThread.start();
//            createLuckyMoneyThreads.add(createLuckyMoneyThread);
//        });
//
//        for (CreateLuckyMoneyThread t : createLuckyMoneyThreads){
//            t.join();
//        }

        List<ReceiveLuckyMoneyThread> receiveLuckyMoneyThreads = new ArrayList<>();
        records.forEach(record ->{
            ReceiveLuckyMoneyThread receiveLuckyMoneyThread = new ReceiveLuckyMoneyThread(record.get("username"),record.get("password"),sessionId);
            receiveLuckyMoneyThread.start();
            receiveLuckyMoneyThreads.add(receiveLuckyMoneyThread);
        });

        for (ReceiveLuckyMoneyThread t:receiveLuckyMoneyThreads){
            t.join();
        }
        long actual = walletRepository.sumAllBalance();

        assertThat(expected).isEqualTo(actual);

    }
}
