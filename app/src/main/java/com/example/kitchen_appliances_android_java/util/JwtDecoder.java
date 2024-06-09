package com.example.kitchen_appliances_android_java.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.stream.Collectors;

public class JwtDecoder {
    private static JwtDecoder instance;
    private JwtDecoder() {
    }
    public static JwtDecoder getInstance() {
        if (instance == null) {
            instance = new JwtDecoder();
        }
        return instance;
    }
    public static String decodeToken(String token) {
        // Giải mã token JWT
        DecodedJWT jwt = JWT.decode(token);

        // Trích xuất claims từ token
        Map<String, Object> claims = jwt.getClaims().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().as(Object.class)
                ));

        // Chuyển đổi claims thành JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(claims);
    }

}
