package br.edu.ifpb.pweb2.bloomfinance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CorrentistaForm {
    private Long id; // null = novo; != null = edição

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    private String email;

    // Campos de segurança (na tabela users)
    @NotBlank(message = "O login (username) é obrigatório.")
    private String username;

    // Senha obrigatória apenas na criação; na edição é opcional
    private String senha;

    private boolean admin;
    private boolean bloqueado;
}
