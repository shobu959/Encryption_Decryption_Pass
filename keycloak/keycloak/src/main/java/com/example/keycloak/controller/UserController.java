package com.example.keycloak.controller;

import com.example.keycloak.entity.UserEntity;
import com.example.keycloak.serviceImpl.EncryptionServiceImpl;
import com.example.keycloak.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EncryptionServiceImpl encryptionService;

    @PostMapping("/addUserDetails")
    public ResponseEntity<UserEntity> getUserData(@RequestBody UserEntity userEntity) throws Exception {
        try {
            UserEntity user = userService.saveDetails(userEntity);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getDecryptedPass")
    public ResponseEntity<String> getEncryptedData(@RequestHeader String encryptedPass, @RequestHeader String email){
        try {
            String secretKey = encryptionService.secretKeyDetail(email);
            String decryptedPass = encryptionService.decrypt(encryptedPass,secretKey);
            return new ResponseEntity<>(decryptedPass,HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
