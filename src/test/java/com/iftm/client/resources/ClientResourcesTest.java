package com.iftm.client.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientResourcesTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService servico;

    /**
     * Caso de testes : Verificar se o endpoint get/clients/ retorna todos os clientes existentes
     * Arrange:
     * - camada service simulada com mockito
     * - base de dado : 3 clientes
     * new Client(7l, "Jose Saramago", "10239254871", 5000.0, Instant.parse("1996-12-23T07:00:00Z"), 0);
     * new Client(4l, "Carolina Maria de Jesus", "10419244771", 7500.0, Instant.parse("1996-12-23T07:00:00Z"), 0);
     * new Client(8l, "Toni Morrison", "10219344681", 10000.0, Instant.parse("1940-02-23T07:00:00Z"), 0);
     * - Uma PageRequest default
     */

    @DisplayName("Verificar se o endpoint get/clients/ retorna todos os clientes existentes")
    @Test
    public void testarEndPointRetornaTodosClientesExistentes() throws Exception {
        //necessário para o teste de unidade
        //confiurar mockBean Servico
        List<ClientDTO> listaClientesExistentes = new ArrayList<>();
        listaClientesExistentes.add(new ClientDTO(new Client(7l, "Jose Saramago", "10239254871", 5000.0, Instant.parse("1996-12-23T07:00:00Z"), 0)));
        listaClientesExistentes.add(new ClientDTO(new Client(4l, "Carolina Maria de Jesus", "10419244771", 7500.0, Instant.parse("1996-12-23T07:00:00Z"), 0)));
        listaClientesExistentes.add(new ClientDTO(new Client(8l, "Toni Morrison", "10219344681", 10000.0, Instant.parse("1940-02-23T07:00:00Z"), 0)));

        Page<ClientDTO> pagina = new PageImpl<>(listaClientesExistentes);
        Mockito.when(servico.findAllPaged(Mockito.any())).thenReturn(pagina);

        int qtdClientes = 3;

        ResultActions resultado = mockMvc.perform(get("/clients/").accept(APPLICATION_JSON));
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").exists())
                .andExpect(jsonPath("$.numberOfElements").value(qtdClientes));
    }

    @DisplayName("Verificar se o endpoint get/clients/ retorna cliente com income informado")
    @Test
    public void testarEndPointRetornaClienteComIncomeInformado() throws Exception {
        List<ClientDTO> listaClientesExistentes = new ArrayList<>();
        listaClientesExistentes.add(new ClientDTO(new Client(7l, "Jose Saramago", "10239254871", 5000.0, Instant.parse("1996-12-23T07:00:00Z"), 0)));

        Page<ClientDTO> pagina = new PageImpl<>(listaClientesExistentes);
        Mockito.when(servico.findByIncomeGreaterThan(Mockito.any(), Mockito.any())).thenReturn(pagina);

        int qtdClientes = 1;

        ResultActions result = mockMvc.perform(
                get("/clients/incomeGreaterThan/").queryParam("income","5000.0").accept(APPLICATION_JSON)
        );
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").exists())
                .andExpect(jsonPath("$.numberOfElements").value(qtdClientes));
    }

    @DisplayName("Verificar se o endpoint post/clients/ retorna o cliente inserido junto com o codigo 201")
    @Test
    public void testarEndPointRetornaClienteInserido() throws Exception {

        Client client = new Client(10L, "Laura Cristina", "88829955678", 5000.0, Instant.parse("1995-06-15T07:00:00Z"), 0);
        ClientDTO clientDTO = new ClientDTO(client);
        Mockito.when(servico.insert(Mockito.any())).thenReturn(clientDTO);

        String json = objectMapper.writeValueAsString(clientDTO);
        ResultActions result = mockMvc.perform(post("/clients/")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Laura Cristina"))
                .andExpect(jsonPath("$.cpf").value("88829955678"))
                .andExpect(jsonPath("$.income").value(5000.0))
                .andExpect(jsonPath("$.birthDate").value("1995-06-15T07:00:00Z"))
                .andExpect(jsonPath("$.children").value(0));


    }

    @DisplayName("Verificar se o endpoint post/clients/ retorna o cliente inserido junto com o codigo 204")
    @Test
    public void testarEndPointRetornaClienteUpdateOK() throws Exception {

        Client client = new Client(10L, "Laura Cristina", "88829955678", 5000.0, Instant.parse("1995-06-15T07:00:00Z"), 0);
        ClientDTO clientDTO = new ClientDTO(client);
//        Mockito.when(servico.insert(Mockito.any())).thenReturn(clientDTO);
        Mockito.when(servico.update(10l, clientDTO)).thenReturn(clientDTO);

        String json = objectMapper.writeValueAsString(clientDTO);
        ResultActions result = mockMvc.perform(put("/clients/10")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());

    }

    @DisplayName("Verificar se o endpoint put/clients/ retorna o codigo 204 com cliente nao encontrado")
    @Test
    public void testarEndPointRetornaClienteUpdateNotFound() throws Exception {

        Client client = new Client(10L, "Laura Cristina", "88829955678", 5000.0, Instant.parse("1995-06-15T07:00:00Z"), 0);
        ClientDTO clientDTO = new ClientDTO(client);
        Mockito.when(servico.update(100l, new ClientDTO())).thenThrow(new EntityNotFoundException());

//        String json = objectMapper.writeValueAsString(clientDTO);
        ResultActions result = mockMvc.perform(put("/clients/100")
//                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());

    }

}
