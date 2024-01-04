package com.example.keycloak.serviceImpl;


import com.example.keycloak.entity.UserEntity;
import com.example.keycloak.model.CredentialModel;
import com.example.keycloak.model.UserModel;
import com.example.keycloak.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class UserServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private EncryptionServiceImpl encryptionService;

    public UserEntity saveDetails(UserEntity userEntity) throws Exception {
        UserEntity user = new UserEntity();
        SecretKey secretKey = encryptionService.getSecretKey();
        String encryptedPass = encryptionService.encrypt(userEntity.getPassword(),secretKey);
        userEntity.setPassword(encryptedPass);
        byte[] rawData = secretKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        userEntity.setSecretKey(encodedKey);
        try {
            user = userRepository.save(userEntity);
            UserModel userModel = new UserModel();
            userModel.setUsername(userEntity.getUsername());
            userModel.setFirstName(userEntity.getFirstName());
            userModel.setLastName(userEntity.getLastName());
            userModel.setEmail(userEntity.getEmail());
            userModel.setEnabled(userEntity.isEnabled());
            userModel.setEmailVerified(userEntity.isEmailVerified());

            List<CredentialModel> list = new ArrayList<>();
            CredentialModel credentialModel = new CredentialModel();
            credentialModel.setType(encryptedPass);
            credentialModel.setValue("password");
            list.add(credentialModel);
            userModel.setCredentials(list);

            //WebClient POST Call
            String baseUrl = "http://localhost:8080/admin/realms/check/users";
            String response = WebClient.create(baseUrl).post().accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(userModel),UserModel.class).retrieve()
                    .bodyToMono(String.class).block();

            System.out.println(response);

        } catch (Exception e) {
            throw new Exception("User Already Created");
        }
        return user;
    }
}
