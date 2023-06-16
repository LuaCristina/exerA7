package com.iftm.client.resources;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientResourcesTestIntegracao {

    @Autowired
    private MockMvc mockMvc;


    /**
     * Caso de testes : Verificar se o endpoint get/clients/ retorna todos os clientes existentes
     * - Uma PageRequest default
     */

    @DisplayName("Verificar se o endpoint get/clients/ retorna todos os clientes existentes")
    @Test
    public void testarEndPointRetornaTodosClientesExistentes() throws Exception {

        int qtdClientes = 12;

        ResultActions resultado = mockMvc.perform(get("/clients/").accept(APPLICATION_JSON));
        resultado
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.content.*", hasSize(qtdClientes)))
                .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)))
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].id").value(4L))
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 7L).exists())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 3L).exists())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 5L).exists())
                .andExpect(jsonPath("$.pageable.pageSize").exists())
                .andExpect(jsonPath("$.pageable.pageSize").value(12))
                .andExpect(jsonPath("$.numberOfElements").exists())
                .andExpect(jsonPath("$.numberOfElements").value(qtdClientes))
                .andExpect(jsonPath("$.content[0].id", is(4)))
        ;
    }

    /**
     * Caso de teste: verificar se o endPoint clients/id retorna o cliente correto quando o id existe
     * Arrange:     *
     * - Uma PageRequest default
     * - idExistente : 4L
     * {
     * "id": 4,
     * "name": "Carolina Maria de Jesus",
     * "cpf": "10419244771",
     * "income": 7500.0,
     * "birthDate": "1996-12-23T07:00:00Z",
     * "children": 0
     * }
     */
    @Test
    @DisplayName("verificar se o endPoint clients/id retorna o cliente correto quando o id existe")
    public void testarEndPointBuscaPorIDRetornaClienteIdExistente() throws Exception {
        long idExistente = 4L;
        mockMvc.perform(get("/clients/{id}", idExistente).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(idExistente))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").value("Carolina Maria de Jesus"))
                .andExpect(jsonPath("$.cpf").exists())
                .andExpect(jsonPath("$.cpf").value("10419244771"))
        ;
    }

    /**
     * Caso de teste: verificar se o endPoint clients/id retorna
     * o erro Resource not found quando o id não existe
     * Arrange:
     * - idExistente : 40L
     */
    public void testarBuscaPorIdNaoExistenteRetornaErro() throws Exception {
        long idNaoExistente = 40L;
        ResultActions resultado = mockMvc.perform(get("/clients/id/{id}", idNaoExistente)
                .accept(MediaType.APPLICATION_JSON));
        resultado.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"));
    }
}
