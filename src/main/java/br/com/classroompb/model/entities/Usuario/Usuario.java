package br.com.classroompb.model.entities.Usuario;

import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;

public abstract class Usuario {

    private String nome;
    private String email;
    private String matricula;
    private String senha;
    private TipoUsuario tipoUsuario;

    protected Usuario() {
    }

    public Usuario(String nome, String email, String senha, TipoUsuario tipoUsuario) {
        setNome(nome);
        setEmail(email);
        setSenha(senha);
        setTipoUsuario(tipoUsuario);
    }

    public Usuario(String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
        setNome(nome);
        setEmail(email);
        setMatricula(matricula);
        setSenha(senha);
        setTipoUsuario(tipoUsuario);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        validarNome(nome);
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        validarEmail(email);
        this.email = email;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        validarMatricula(matricula);
        this.matricula = matricula;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        validarSenha(senha);
        this.senha = senha;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        validarTipoUsuario(tipoUsuario);
        this.tipoUsuario = tipoUsuario;
    }

    public void validarDadosBasicos() {
        validarNome(nome);
        validarEmail(email);
        validarSenha(senha);
        validarTipoUsuario(tipoUsuario);
    }

    public void validarDadosComMatricula() {
        validarDadosBasicos();
        validarMatricula(matricula);
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new EntradaInvalidaException("Nome do usuário não pode ser vazio.");
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new EntradaInvalidaException("E-mail do usuário não pode ser vazio.");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new EntradaInvalidaException("E-mail inválido.");
        }
    }

    private void validarMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new EntradaInvalidaException("Matrícula do usuário não pode ser vazia.");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new EntradaInvalidaException("Senha do usuário não pode ser vazia.");
        }
    }

    private void validarTipoUsuario(TipoUsuario tipoUsuario) {
        if (tipoUsuario == null) {
            throw new EntradaInvalidaException("Tipo de usuário inválido.");
        }
    }
}
