package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;

public class PermissaoService {

    public static void validarPermissao(
        Usuario usuario,
        TipoUsuario tipoEsperado
    ){
        if(usuario.getTipoUsuario() != tipoEsperado){
            throw new RuntimeException("Acesso negado! Permissão negada!");
        }
    }
}
