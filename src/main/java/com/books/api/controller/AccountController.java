package com.books.api.controller;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador responsável por lidar com requisições relacionadas à conta de usuário.
 * Atualmente, oferece funcionalidade de login.
 */
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    // Repositório de acesso aos dados de conta
    private final AccountRepository accountRepository;

    /**
     * Realiza o login de um usuário com base no e-mail e senha fornecidos.
     *
     * @param body     Mapa contendo as chaves "email" e "password" fornecidas no corpo da requisição.
     * @param response Objeto de resposta HTTP (não utilizado diretamente aqui, mas pode ser usado para customizações futuras).
     * @return Um mapa contendo o status da operação, código HTTP simulado, mensagem e, em caso de sucesso, um token falso de autenticação.
     *
     * <p>Etapas realizadas:</p>
     * <ul>
     *   <li>Valida se e-mail e senha foram enviados e não estão vazios.</li>
     *   <li>Busca o usuário pelo e-mail no banco de dados.</li>
     *   <li>Verifica se a senha fornecida bate com a senha salva (criptografada).</li>
     *   <li>Se tudo estiver correto, retorna um token fictício de autenticação.</li>
     * </ul>
     */
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

        String fakeToken = "jwt-" + account.getId() + "-" + System.currentTimeMillis();

        result.put("status", "success");
        result.put("code", "200");
        result.put("message", "Login realizado com sucesso.");
        result.put("token", fakeToken);
        return result;
    }
}
