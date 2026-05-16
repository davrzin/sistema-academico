package br.com.classroompb.model.entities;

import br.com.classroompb.model.enums.TipoUsuario;

public abstract class Usuario {

    private String nome;
    private String email;
    private String matricula;
    private String senha;
    
    private TipoUsuario tipoUsuario;

    protected Usuario() {
    }

    public Usuario(String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.matricula = matricula;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }
    
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }
    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

}
