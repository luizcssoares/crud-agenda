package com.example.crud.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.crud.model.Mensagem;

@Service
public class PythonService {

    private final RestClient restClient;

    public PythonService(RestClient restClient) {
        this.restClient = restClient;
    }

    public Mensagem buscarMensagem() {

        return restClient.get()
                .uri("/hello")
                .retrieve()
                .body(Mensagem.class);

    }

    public Mensagem enviaMensagem() {

        return restClient.get()
                .uri("/whatszap")
                .retrieve()
                .body(Mensagem.class);

    }

}
