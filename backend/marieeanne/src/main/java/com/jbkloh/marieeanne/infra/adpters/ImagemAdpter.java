package com.jbkloh.marieeanne.infra.adpters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.ports.ImagemRepositoryPort;

@Component
public class ImagemAdpter implements ImagemRepositoryPort{

    @Value("${upload.directory}")
    private String uploadDir;

    @Override
    public String upload(byte[] conteudo,String nome) {
        try {
            Path pathDir = Paths.get(uploadDir).normalize();

            if(!Files.exists(pathDir)){
                Files.createDirectories(pathDir);
            }
            Path filePath = pathDir.resolve(nome);
            Files.write(filePath, conteudo);

            return "/static/images/" + nome;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar o arquivo" + e);            
        }
    }

    
}
