package com.hey.integration;

import com.hey.integration.entity.User;
import com.hey.integration.repository.TransferStatementRepository;
import com.hey.integration.repository.UserRepository;
import com.hey.integration.repository.WalletRepository;
import com.hey.integration.test_scenario.CreateLuckyMoneyThread;
import com.hey.integration.test_scenario.ReceiveLuckyMoneyThread;
import com.hey.integration.test_scenario.TransferMoneyThread;
import com.hey.integration.utils.RestTemplateUtil;
import com.hey.integration.utils.RestTemplateUtilImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.hey.integration.constants.Constant.BASE_URL;
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
    public void manyUserTransferToOneUser() throws IOException, InterruptedException {
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

        Random random = new Random();
        long amount = random.nextInt(25_123) + 10_000;

        records.forEach(record -> {
            String username = record.get("username");
            String password = record.get("password");

            if (!username.equals(targetUser.getUsername())) {

                TransferMoneyThread transferMoneyThread =
                        new TransferMoneyThread(
                                username,
                                password,
                                targetId,
                                amount);

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

        Random random = new Random();
        long amount = random.nextInt(25_123) + 10_000;
        users.forEach(user -> {
            if (!username.equals(user.getUsername())) {
                TransferMoneyThread transferMoneyThread =
                        new TransferMoneyThread(
                                username,
                                password,
                                user.getId(), amount);

                threads.add(transferMoneyThread);
            }
        });
        for (TransferMoneyThread thread : threads) {
            thread.start();
        }
        for (TransferMoneyThread thread : threads) {
            thread.join();
        }

        long actual = walletRepository.sumAllBalance();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void manyUsersTransferToManyUsers() throws IOException, InterruptedException {
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

        Random random = new Random();
        long amount = random.nextInt(25_123) + 10_000;

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
                            user.getId(),
                            amount);

            threads.add(transferMoneyThread);
        });
        for (TransferMoneyThread thread : threads) {
            thread.start();
        }
        for (TransferMoneyThread thread : threads) {
            thread.join();
        }
        long actual = walletRepository.sumAllBalance();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void oneUserTransferAndReceiveTransfer() throws IOException, InterruptedException {
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

        User mainUser = users.get(randomUser.nextInt(records.size()));
        CSVRecord mainUserRecord = records.stream().filter(record -> record.get("username").equals(mainUser.getUsername())).findFirst().get();
        String mainUserPassword = mainUserRecord.get("password");

        Random random = new Random();
        long amount = random.nextInt(25_123) + 10_000;

        users.forEach(user -> {
            if (!mainUser.getUsername().equals(user.getUsername())) {
                TransferMoneyThread transferMoneyFromMainUserThread =
                        new TransferMoneyThread(
                                mainUser.getUsername(),
                                mainUserPassword,
                                user.getId(),
                                amount);
                threads.add(transferMoneyFromMainUserThread);
            }
        });
        records.forEach(record -> {
            if (!mainUser.getUsername().equals(record.get("username"))) {
                TransferMoneyThread transferMoneyToMainUserThread =
                        new TransferMoneyThread(
                                record.get("username"),
                                record.get("password"),
                                mainUser.getId(),
                                amount);
                threads.add(transferMoneyToMainUserThread);
            }
        });
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long actual = walletRepository.sumAllBalance();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Disabled
    public void NUserTransferN_1User() throws IOException, InterruptedException {
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

        Random random = new Random();
        long amount = random.nextInt(25_123) + 10_000;
        List<User> users = userRepository.findAll();
        records.forEach(record -> {
            users.forEach(user -> {
                if (!record.get("username").equals(user.getUsername())) {
                    TransferMoneyThread transferMoneyThread =
                            new TransferMoneyThread(
                                    record.get("username"),
                                    record.get("password"),
                                    user.getId(),
                                    amount);

                    threads.add(transferMoneyThread);
                }
            });
        });

        for (TransferMoneyThread t : threads) {
            t.start();
        }
        for (TransferMoneyThread t : threads) {
            t.join();
        }
        long actual = walletRepository.sumAllBalance();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void oneUserTransferToManyUsersOverMoney() throws IOException, InterruptedException {
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

        Random random = new Random();
        long amount = random.nextInt(350_123) + 200_000;
        users.forEach(user -> {
            if (!username.equals(user.getUsername())) {
                TransferMoneyThread transferMoneyThread =
                        new TransferMoneyThread(
                                username,
                                password,
                                user.getId(), amount);

                threads.add(transferMoneyThread);
            }
        });


        for (TransferMoneyThread thread : threads) {
            thread.start();
        }

        for (TransferMoneyThread thread : threads) {
            thread.join();
        }

        long actual = walletRepository.sumAllBalance();
        int numberWalletNeg = walletRepository.countAllByBalanceLessThan(0L);

        assertThat(actual).isEqualTo(expected);
        assertThat(numberWalletNeg).isEqualTo(0);
    }

    @Test
    public void oneUserTransferAndCreateLuckyMoney() throws IOException, InterruptedException {
        Long expected = walletRepository.sumAllBalance();

        String[] HEADERS = {"username", "fullName", "email", "password"};
        Reader in = new FileReader(ResourceUtils.getFile("classpath:Data_100.csv"));
        List<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(in)
                .getRecords();
        List<Thread> threads = new ArrayList<>();

        // Start threads for the transaction in the same time
        List<User> users = userRepository.findAll();

        Random randomUser = new Random();
        CSVRecord sourceUser = records.get(randomUser.nextInt(records.size()));

        String username = sourceUser.get("username");
        String password = sourceUser.get("password");

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));
        RestTemplateUtil restTemplateUtil = new RestTemplateUtilImpl(restTemplate);
        restTemplateUtil.login(records.get(0).get("username"), records.get(0).get("password"));
        Map<String, Object> getChatListRes = restTemplateUtil.getChatList();
        Map<String, Map<String, Object>> payload = (Map<String, Map<String, Object>>) getChatListRes.get("payload");
        List<Object> items = (List<Object>) payload.get("items");
        Map<String, Object> item0 = (Map<String, Object>) items.get(0);
        String sessionId = (String) item0.get("sessionId");

        Random random = new Random();
        long amount = random.nextInt(3_123) + 1000;
        users.forEach(user -> {
            if (!username.equals(user.getUsername())) {
                TransferMoneyThread transferMoneyThread =
                        new TransferMoneyThread(
                                username,
                                password,
                                user.getId(), amount);
                CreateLuckyMoneyThread createLuckyMoneyThread = new CreateLuckyMoneyThread(username, password, sessionId);
                threads.add(transferMoneyThread);
                threads.add(createLuckyMoneyThread);

            }
        });


        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long actual = walletRepository.sumAllBalance();
        int numberWalletNeg = walletRepository.countAllByBalanceLessThan(0L);

        assertThat(actual).isEqualTo(expected);
        assertThat(numberWalletNeg).isEqualTo(0);
    }

    @Test
    public void oneUserTransferAndCreateLuckyMoneyAndReceiveLuckyMoney() throws IOException, InterruptedException {
        Long expected = walletRepository.sumAllBalance();

        String[] HEADERS = {"username", "fullName", "email", "password"};
        Reader in = new FileReader(ResourceUtils.getFile("classpath:Data_100.csv"));
        List<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(in)
                .getRecords();
        List<Thread> threads = new ArrayList<>();

        // Start threads for the transaction in the same time
        List<User> users = userRepository.findAll();

        Random randomUser = new Random();
        CSVRecord sourceUser = records.get(randomUser.nextInt(records.size()));

        String username = sourceUser.get("username");
        String password = sourceUser.get("password");

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));
        RestTemplateUtil restTemplateUtil = new RestTemplateUtilImpl(restTemplate);
        restTemplateUtil.login(records.get(0).get("username"), records.get(0).get("password"));
        Map<String, Object> getChatListRes = restTemplateUtil.getChatList();
        Map<String, Map<String, Object>> payload = (Map<String, Map<String, Object>>) getChatListRes.get("payload");
        List<Object> items = (List<Object>) payload.get("items");
        Map<String, Object> item0 = (Map<String, Object>) items.get(0);
        String sessionId = (String) item0.get("sessionId");

        Random random = new Random();
        long amount = random.nextInt(3_123) + 1000;
        users.forEach(user -> {
            if (!username.equals(user.getUsername())) {
                TransferMoneyThread transferMoneyThread = new TransferMoneyThread(username, password, user.getId(), amount);
                threads.add(transferMoneyThread);

            }
        });

        records.forEach(record -> {

            CreateLuckyMoneyThread createLuckyMoneyThread = new CreateLuckyMoneyThread(record.get("username"), record.get("password"), sessionId);
            ReceiveLuckyMoneyThread receiveLuckyMoneyThread = new ReceiveLuckyMoneyThread(record.get("username"),record.get("password"),sessionId);

            threads.add(createLuckyMoneyThread);
            threads.add(receiveLuckyMoneyThread);
        });


        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long actual = walletRepository.sumAllBalance();
        int numberWalletNeg = walletRepository.countAllByBalanceLessThan(0L);

        assertThat(actual).isEqualTo(expected);
        assertThat(numberWalletNeg).isEqualTo(0);
    }

}
