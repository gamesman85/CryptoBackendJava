package com.example.cryptobackend.controller;

import com.example.cryptobackend.model.CryptoRequest;
import com.example.cryptobackend.model.CryptoResponse;
import com.example.cryptobackend.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class CryptoController {

    private final CryptoService cryptoService;

    @Autowired
    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/")
    public Map<String, String> root() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Crypto Explorer Backend");
        response.put("status", "active");
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        return response;
    }

    @PostMapping("/api/crypto")
    public ResponseEntity<CryptoResponse> processCrypto(@RequestBody CryptoRequest request) {
        try {
            if (request.getText() == null || request.getText().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new CryptoResponse(null, "Text is required"));
            }

            String result;
            switch (request.getAlgorithm()) {
                case "aes-256-cbc":
                    if (request.getKey() == null || request.getKey().isEmpty()) {
                        return ResponseEntity.badRequest().body(
                            new CryptoResponse(null, "Key is required for AES encryption/decryption"));
                    }
                    
                    result = "encrypt".equals(request.getOperation())
                            ? cryptoService.aesEncrypt(request.getText(), request.getKey())
                            : cryptoService.aesDecrypt(request.getText(), request.getKey());
                    break;
                    
                case "sha256":
                    result = cryptoService.hashSHA256(request.getText());
                    break;
                    
                case "md5":
                    result = cryptoService.hashMD5(request.getText());
                    break;
                    
                case "base64":
                    result = "encrypt".equals(request.getOperation())
                            ? cryptoService.encodeBase64(request.getText())
                            : cryptoService.decodeBase64(request.getText());
                    break;
                    
                default:
                    return ResponseEntity.badRequest().body(
                        new CryptoResponse(null, "Unsupported algorithm"));
            }

            return ResponseEntity.ok(new CryptoResponse(result));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new CryptoResponse(null, e.getMessage()));
        }
    }
}