package com.iftm.client.service;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


// para testar camada de serviço utilize essa notação, que carrega o contexto com os recursos do Spring boot
@SpringBootTest
@Transactional
public class ClientServiceTestIntegracao {

    @Autowired
    private ClientService servico;

    @DisplayName("Testar se o método deleteById apaga um registro e não retorna outras informações")
    @Test
    public void testarApagarPorIdTemSucessoComIdExistente() {
        //cenário
        long idExistente = 1;

        Assertions.assertDoesNotThrow(() -> {
            servico.delete(idExistente);
        });
    }

    @DisplayName("Testar se o método deleteById retorna exception para idInexistente")
    @Test
    public void testarApagarPorIdGeraExceptionComIdInexistente() {
        //cenário
        long idNaoExistente = 100;

        Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.delete(idNaoExistente));
    }

    /**
     * Exemplo Extra
     * Cenário de Teste : método findByIncomeGreaterThan retorna a página com clientes corretos
     * Entrada:
     * - Paginação:
     * - Pagina = 1;
     * - 2
     * - Asc
     * - Income
     * - Income: 4800.00
     * - Clientes:
     * Pagina: 0
     * {
     * "id": 7,
     * "name": "Jose Saramago",
     * "cpf": "10239254871",
     * "income": 5000.0,
     * "birthDate": "1996-12-23T07:00:00Z",
     * "children": 0
     * },
     * <p>
     * {
     * "id": 4,
     * "name": "Carolina Maria de Jesus",
     * "cpf": "10419244771",
     * "income": 7500.0,
     * "birthDate": "1996-12-23T07:00:00Z",
     * "children": 0
     * },
     * <p>
     * Pagina: 1
     * {
     * "id": 8,
     * "name": "Toni Morrison",
     * "cpf": "10219344681",
     * "income": 10000.0,
     * "birthDate": "1940-02-23T07:00:00Z",
     * "children": 0
     * }
     * Resultado:
     * Página não vazia
     * Página contendo um cliente
     * Página contendo o cliente da página 1
     */
    @Test
    @DisplayName("testar método findByIncomeGreaterThan retorna a página com clientes corretos")
    public void testarBuscaPorSalarioMaiorQueRetornaElementosEsperados() {
        //cenário de teste
        double entrada = 4800.00;
        int paginaApresentada = 0;
        int linhasPorPagina = 2;
        String ordemOrdenacao = "ASC";
        String campoOrdenacao = "income";
        Client clienteSete = new Client(7L, "Jose Saramago", "10239254871", 5000.0, Instant.parse("1996-12-23T07:00:00Z"), 0);
        Client clienteQuatro = new Client(4L, "Carolina Maria de Jesus", "10419244771", 7500.0, Instant.parse("1996-12-23T07:00:00Z"), 0);

        PageRequest pagina = PageRequest.of(paginaApresentada, linhasPorPagina,
                Sort.Direction.valueOf(ordemOrdenacao), campoOrdenacao);
        Page<ClientDTO> page = servico.findByIncomeGreaterThan(pagina, entrada);

        assertThat(page).isNotEmpty();
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getNumberOfElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.toList().get(0).toEntity()).isEqualTo(clienteSete);
        assertThat(page.toList().get(1).toEntity()).isEqualTo(clienteQuatro);
    }

    /**
     * Exercicios extras feitos em sala
     * findByCpfLike deveria retornar uma página (e chamar o método findByCpfLike do repository)
     * Cenário de teste
     * Entradas necessárias:
     * - cpf : "%447%"
     * - Uma PageRequest com os valores
     * - page = 0
     * - size = 3
     * - direction = Direction.valueOf("ASC")
     * - order = "name"
     * - Lista de clientes esperada
     * {
     * "id": 4,
     * "name": "Carolina Maria de Jesus",
     * "cpf": "10419244771",
     * "income": 7500.0,
     * "birthDate": "1996-12-23T07:00:00Z",
     * "children": 0
     * },
     */

    @Test
    public void testarSeBuscarClientesPorCPFLikeRetornaUmaPaginaComClientesComCPFQueContemTextoInformado() {
        String cpf = "%447%";
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.valueOf("ASC"), "name");


        List<Client> listaClientes = new ArrayList<Client>();
        listaClientes.add(new Client(4L, "Carolina Maria de Jesus", "10419244771", 7500.0, Instant.parse("1996-12-23T07:00:00Z"), 0));

        Page<ClientDTO> resultado = servico.findByCpfLike(pageRequest, cpf);
        Assertions.assertFalse(resultado.isEmpty());
        Assertions.assertEquals(listaClientes.size(), resultado.getNumberOfElements());
        Assertions.assertEquals(listaClientes.get(0), resultado.toList().get(0).toEntity());
    }
}
