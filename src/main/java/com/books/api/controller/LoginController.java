package com.books.api.controller;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.service.ConfigService;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class LoginController {

    // Repositório de acesso aos dados de conta
    private final AccountRepository accountRepository;
    private final ConfigService config;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body, HttpServletResponse response) {
        Map<String, Object> result = new LinkedHashMap<>();

        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            result.put("status", "error");
            result.put("code", "400");
            result.put("message", "E-mail e senha são obrigatórios.");
            return result;
        }

        Optional<Account> opt = accountRepository.findByEmail(email.trim().toLowerCase());
        if (opt.isEmpty()) {
            result.put("status", "error");
            result.put("code", "404");
            result.put("message", "Conta não encontrada.");
            return result;
        }

        Account account = opt.get();

        if (!BCrypt.checkpw(password, account.getPassword())) {
            result.put("status", "error");
            result.put("code", "401");
            result.put("message", "Senha incorreta.");
            return result;
        }

        if (!account.getStatus().name().equals("ON")) {
            result.put("status", "error");
            result.put("code", "403");
            result.put("message", "Conta desativada ou não autorizada.");
            return result;
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", account.getId());
        claims.put("name", account.getName());
        claims.put("email", account.getEmail());
        claims.put("role", account.getRole().name());

        String token = JwtUtil.generateToken(account.getEmail(), claims);

        result.put("status", "success");
        result.put("code", "200");
        result.put("message", "Login realizado com sucesso.");
        result.put("token", token);
        return result;
    }
}
