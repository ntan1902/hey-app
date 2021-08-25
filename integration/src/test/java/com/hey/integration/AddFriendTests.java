package com.hey.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.integration.endpoint.MyEndpoint;
import com.hey.integration.entity.User;
import com.hey.integration.repository.TransferStatementRepository;
import com.hey.integration.repository.UserRepository;
import com.hey.integration.repository.WalletRepository;
import com.hey.integration.test_scenario.AcceptFriendThread;
import com.hey.integration.test_scenario.AddFriendThread;
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

import javax.websocket.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static com.hey.integration.constants.Constant.BASE_URL;

@SpringBootTest
public class AddFriendTests {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    TransferStatementRepository transferStatementRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void manyUserAddFriendToOneUser() throws IOException, InterruptedException, DeploymentException {
        String[] HEADERS = {"username", "fullName", "email", "password"};
        Reader in = new FileReader(ResourceUtils.getFile("classpath:Data_100.csv"));
        List<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(in)
                .getRecords();
        List<AddFriendThread> threads = new ArrayList<>();
        List<User> users = userRepository.findAll();

        // Start threads for the transaction in the same time
        Random randomUser = new Random();
        CSVRecord targetRecord = records.get(randomUser.nextInt(records.size()));
        String targetUsername = targetRecord.get("username");
        String targetPassword = targetRecord.get("password");

        records.forEach(record -> {
            String username = record.get("username");
            String password = record.get("password");

            if (!username.equals(targetUsername)) {

                AddFriendThread addFriendThread =
                        new AddFriendThread(
                                username,
                                password,
                                targetUsername
                        );

                addFriendThread.start();
                threads.add(addFriendThread);
            }
        });

        for (AddFriendThread thread : threads) {
            thread.join();
        }

        List<AcceptFriendThread> acceptFriendThreads = new ArrayList<>();
        users.forEach(user -> {
            String username = user.getUsername();
            if (!username.equals(targetUsername)) {
                AcceptFriendThread acceptFriendThread =
                        new AcceptFriendThread(
                                targetUsername,
                                targetPassword,
                                user.getId(),
                                username
                        );
                acceptFriendThread.start();
                acceptFriendThreads.add(acceptFriendThread);
            }
        });

        for (AcceptFriendThread acceptFriendThread : acceptFriendThreads) {
            acceptFriendThread.join();
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

        RestTemplateUtil restTemplateUtil = new RestTemplateUtilImpl(restTemplate);

        // Login
        Map<String, String> payload = restTemplateUtil.login(targetUsername, targetPassword);

        String token = payload.get("accessToken");
        String refreshToken = payload.get("refreshToken");

        // Create group

        final WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        MyEndpoint myEndpoint = new MyEndpoint();
        webSocketContainer.connectToServer(myEndpoint, URI.create("ws://localhost:8090?jwt=" + token));

        Map<String, Object> message = new HashMap<>();
        message.put("type", "CHAT_MESSAGE_REQUEST");
        message.put("sessionId", "-1");
        message.put("message", "Hello");
        message.put("usernames", users.stream()
                .map(User::getUsername)
                .filter(username -> !username.equals(targetUsername))
                .collect(Collectors.toList())
        );
        message.put("groupName", "Group Test");
        message.put("groupChat", true);

        myEndpoint.sendMessage(new ObjectMapper().writeValueAsString(message));

        restTemplateUtil.logout(refreshToken);
    }

}
