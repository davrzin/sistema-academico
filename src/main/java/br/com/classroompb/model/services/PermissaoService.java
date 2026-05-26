package br.com.classroompb.model.services;

import java.util.Objects;

import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;

public class PermissaoService {

    public static void validarPermissao(
        Usuario usuario,
        TipoUsuario tipoEsperado
    ) {
        if (usuario == null) {
            throw new RuntimeException("Acesso negado! Usuário inválido.");
        }

        if (usuario.getTipoUsuario() != tipoEsperado) {
            throw new RuntimeException("Acesso negado! Permissão negada!");
        }
    }

    public static void validarPermissaoBuscaPorMatricula(
        Usuario usuarioLogado,
        Usuario usuarioEncontrado
    ) {
        if (usuarioLogado == null || usuarioEncontrado == null) {
            throw new RuntimeException("Acesso negado! Usuário inválido.");
        }

        if (Objects.equals(usuarioLogado.getMatricula(), usuarioEncontrado.getMatricula())) {
            throw new RuntimeException("Para consultar seus próprios dados, acesse a opção 'Meus dados' no menu do seu perfil.");
        }

        TipoUsuario tipoUsuarioLogado = usuarioLogado.getTipoUsuario();
        TipoUsuario tipoUsuarioEncontrado = usuarioEncontrado.getTipoUsuario();

        if (!podeBuscarPorMatricula(tipoUsuarioLogado, tipoUsuarioEncontrado)) {
            throw new RuntimeException("Acesso negado! Você não tem permissão para buscar esse tipo de usuário.");
        }
    }

    public static boolean podeBuscarPorMatricula(
        TipoUsuario tipoUsuarioLogado,
        TipoUsuario tipoUsuarioEncontrado
    ) {
        if (tipoUsuarioLogado == null || tipoUsuarioEncontrado == null) {
            return false;
        }

        if (tipoUsuarioLogado == TipoUsuario.ADMINISTRADOR) {
            return tipoUsuarioEncontrado == TipoUsuario.ALUNO
                    || tipoUsuarioEncontrado == TipoUsuario.PROFESSOR
                    || tipoUsuarioEncontrado == TipoUsuario.COORDENADOR;
        }

        if (tipoUsuarioLogado == TipoUsuario.COORDENADOR) {
            return tipoUsuarioEncontrado == TipoUsuario.ALUNO
                    || tipoUsuarioEncontrado == TipoUsuario.PROFESSOR;
        }

        if (tipoUsuarioLogado == TipoUsuario.PROFESSOR) {
            return tipoUsuarioEncontrado == TipoUsuario.ALUNO;
        }

        return false;
    }
}