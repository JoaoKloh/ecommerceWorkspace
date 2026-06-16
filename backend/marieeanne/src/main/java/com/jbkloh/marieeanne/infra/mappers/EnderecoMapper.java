package com.jbkloh.marieeanne.infra.mappers;
import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Endereco;
import com.jbkloh.marieeanne.infra.models.EnderecoEntity;

@Component

public class EnderecoMapper {

    public static EnderecoEntity toEntity(Endereco domain) {
        if (domain == null) return null;

        EnderecoEntity entity = new EnderecoEntity();
        entity.setId(domain.getId());
        entity.setRua(domain.getRua());
        entity.setNumero(domain.getNumero());
        entity.setComplemento(domain.getComplemento());
        entity.setBairro(domain.getBairro());
        entity.setCidade(domain.getCidade());
        entity.setEstado(domain.getEstado());
        entity.setCep(domain.getCep());

        return entity;
    }

    public static Endereco toDomain(EnderecoEntity entity) {
        if (entity == null) return null;

        return new Endereco(
            entity.getId(),
            entity.getRua(),
            entity.getNumero(),
            entity.getComplemento(),
            entity.getBairro(),
            entity.getCidade(),
            entity.getEstado(),
            entity.getCep()
        );
    }
    
}
