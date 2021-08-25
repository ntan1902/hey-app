package com.hey.integration;

import com.hey.integration.entity.User;
import com.hey.integration.repository.TransferStatementRepository;
import com.hey.integration.repository.UserRepository;
import com.hey.integration.repository.WalletRepository;
import com.hey.integration.test_scenario.TransferMoneyThread;
import com.hey.integration.utils.RestTemplateUtil;
import com.hey.integration.utils.RestTemplateUtilImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class IntegrationApplicationTests {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    TransferStatementRepository transferStatementRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void clearDB() {
        userRepository.deleteAll();
        walletRepository.deleteWalletsByRefFrom("users");
        transferStatementRepository.deleteAll();
    }

    @Test
    public void manyUserTransferToOneUsers() throws IOException, InterruptedException {
        Long expected = walletRepository.sumAllBalance();

        String[] HEADERS = {"username", "fullName", "email", "password"};
        Reader in = new FileReader(ResourceUtils.getFile("classpath:Data_100.csv"));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(in);
        List<TransferMoneyThread> threads = new ArrayList<>();

        // Start threads for the transaction in the same time
        List<User> users = userRepository.findAll();

        Random randomUser = new Random();
        User targetUser = users.get(randomUser.nextInt(users.size()));
        String targetId = targetUser.getId();

        records.forEach(record -> {
            String username = record.get("username");
            String password = record.get("password");

            if (!username.equals(targetUser.getUsername())) {

                TransferMoneyThread transferMoneyThread =
                        new TransferMoneyThread (
                                username,
                                password,
                                targetId
                        );

                transferMoneyThread.start();
                threads.add(transferMoneyThread);
            }
        });

        for (TransferMoneyThread thread : threads) {
            thread.join();
        }

        long actual = walletRepository.sumAllBalance();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void oneUserTransferToManyUsers() throws IOException, InterruptedException {
        Long expected = walletRepository.sumAllBalance();

        String[] HEADERS = {"username", "fullName", "email", "password"};
        Reader in = new FileReader(ResourceUtils.getFile("classpath:Data_100.csv"));
        List<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(in)
                .getRecords();
        List<TransferMoneyThread> threads = new ArrayList<>();

        // Start threads for the transaction in the same time
        List<User> users = userRepository.findAll();

        Random randomUser = new Random();
        CSVRecord sourceUser = records.get(randomUser.nextInt(records.size()));

        String username = sourceUser.get("username");
        String password = sourceUser.get("password");
        users.forEach(user -> {
            if (!username.equals(user.getUsername())) {
                TransferMoneyThread transferMoneyThread =
                        new TransferMoneyThread(
                                username,
                                password,
                                user.getId())
                        ;

                transferMoneyThread.start();
                threads.add(transferMoneyThread);
            }
        });

        for (TransferMoneyThread thread : threads) {
            thread.join();
        }

        long actual = walletRepository.sumAllBalance();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void manyUserTransferToManyUsers() throws IOException, InterruptedException {
        Long expected = walletRepository.sumAllBalance();

        String[] HEADERS = {"username", "fullName", "email", "password"};
        Reader in = new FileReader(ResourceUtils.getFile("classpath:Data_100.csv"));
        List<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(in)
                .getRecords();
        List<TransferMoneyThread> threads = new ArrayList<>();

        // Start threads for the transaction in the same time
        List<User> users = userRepository.findAll();

        Random randomUser = new Random();
        users.forEach(user -> {
            String username = "";
            String password = "";
            do {
                CSVRecord sourceUser = records.get(randomUser.nextInt(records.size()));
                username = sourceUser.get("username");
                password = sourceUser.get("password");
            } while (username.equals(user.getUsername()));

            TransferMoneyThread transferMoneyThread =
                    new TransferMoneyThread(
                            username,
                            password,
                            user.getId()
                    );

            transferMoneyThread.start();
            threads.add(transferMoneyThread);
        });

        for (TransferMoneyThread thread : threads) {
            thread.join();
        }

        long actual = walletRepository.sumAllBalance();
        assertThat(actual).isEqualTo(expected);
    }
}
