package com.iftm.client.repositories;

import com.iftm.client.entities.Client;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class ClientRepositoryTest {
    @Autowired
    private ClientRepository repositorio;

    /**
     * Cenário de Teste 1
     * Objetivo: Verificar se a busca por id realmente retorna o cliente correto.
     * ●
     * monta o cenário,
     * - arquivo import.sql carrega o cenário (clientes cadastrados)
     * - definir o id de um cliente que exista em import.sql
     * ●
     * executa a ação
     * - executar o método de buscar por id
     * (Utilizaremos a classe Optional para encapsular o objeto da
     * classe Client)
     * ●
     * e valida a saída.
     * - verifica se foi retornado um objeto.
     * - verifica se o nome e o cpf do cliente retornado corresponde
     * ao esperado.
     */
    @Test
    @DisplayName("Verificar se a busca por id realmente retorna o\n" + "     * cliente correto.")
    public void testarBuscaPorIDRetornaClienteCorreto() {
        long idBuscado = 1; //corresponde ao primeiro registro do arquivo import.sql
        String nomeBuscado = "Conceição Evaristo";
        String cpfBuscado = "10619244881";

        Client respostaEsperada = new Client(1L, "Conceição Evaristo", "10619244881", null, null, null);

        Optional<Client> resposta = repositorio.findById(idBuscado);

        //verificação se um client foi retornado
        Assertions.assertThat(resposta).isPresent();
        //assertTrue(resposta.isPresent());
        //assertNotNull(resposta.get());

        //verificar se o objeto retorna corresponde ao objeto esperado.
        //Assertions.assertThat(resposta.get()).isEqualTo(respostaEsperada);

        //verificar se os dados do cliente esperado corresponde aos dados do cliente retornado
        Assertions.assertThat(resposta.get().getName()).isEqualTo(nomeBuscado);
        Assertions.assertThat(resposta.get().getCpf()).isEqualTo(cpfBuscado);
        //assertEquals(nomeBuscado, resposta.get().getName());
        //assertEquals(cpfBuscado, resposta.get().getCpf());
    }

    /**
     * Cenário de Teste 2
     * Objetivo: Verificar se a busca por id inexistente retorna nenhum cliente.
     * ●
     * monta o cenário,
     * - arquivo import.sql carrega o cenário (clientes cadastrados)
     * - definir o id de um cliente que não exista em import.sql
     * ●
     * executa a ação
     * - executar o método de buscar por id
     * ●
     * e valida a saída.
     * - verifica se não foi retornado um objeto.
     */
    @Test
    @DisplayName("Verificar se a busca por id inexistente retorna nenhum cliente")
    public void testarBuscaPorIdNaoRetornaObjetoParaIdInexistente() {
        long idBuscado = 100;

        Optional<Client> resultado = repositorio.findById(idBuscado);

        Assertions.assertThat(resultado).isEmpty();
        //assertTrue(resultado.isEmpty());
    }

    /**
     * Cenário de Teste 3
     * Objetivo: Verificar se a exclusão realmente apaga um registro existente.
     * ●
     * monta o cenário,
     * - arquivo import.sql carrega o cenário (clientes cadastrados)
     * - definir o id de um cliente que exista em import.sql
     * ●
     * executa a ação
     * - executar o método de exclusão por id
     * - executar o método de buscar por id
     * ●
     * e valida a saída.
     * - verificar se o resultado do método de busca é falso
     */
    @Test
    @DisplayName("Verificar se a exclusão realmente apaga um registro existente.")
    public void TestarExcluirPorIdApagaRegistroExistente() {
        long idBuscado = 8;
        long quantidadeRegistrosEsperado = 11;

        repositorio.deleteById(idBuscado);
        Optional<Client> resultado = repositorio.findById(idBuscado);

        Assertions.assertThat(resultado).isEmpty();
        //assertTrue(resultado.isPresent());
        Assertions.assertThat(repositorio.count()).isEqualTo(quantidadeRegistrosEsperado);
        //assertEquals(quantidadeRegistrosEsperado, repositorio.count());

    }

    /**
     * Cenário de Teste 4
     * Objetivo: Verificar se a exclusão retorna um erro quando um id não existente é informado.
     * monta o cenário,
     * - arquivo import.sql carrega o cenário (clientes cadastrados)
     * - definir o id de um cliente que não exista em import.sql
     * executa a ação
     * - executar o método de exclusão por id
     * e valida a saída.
     * - verificar se ocorre o erro: EmptyResultDataAccessException
     */
    @Test
    @DisplayName("Verificar se a exclusão retorna um erro quando um id não existente é informado.")
    public void testarExcluirPorIdRetornaExceptionCasoNaoExista() {
        long idEsperado = 20;

        assertThrows(EmptyResultDataAccessException.class, () -> {
            repositorio.deleteById(idEsperado);
        });

    }

    /**
     * Cenário de Teste 5
     * Objetivo: Verificar se a exclusão de todos elementos realmente apaga todos os registros do Banco de dados.
     * monta o cenário,
     * - arquivo import.sql carrega o cenário (clientes cadastrados)
     * executa a ação
     * - executar o método de exclusão de todos registros
     * e valida a saída.
     * - consultar todos os registros do banco e verificar se retorna vazio.
     */
    @Test
    @DisplayName("Verificar se a exclusão de todos elementos realmente apaga todos os registros do Banco de dados.")
    public void testarApagarTodosLimpaBancoDados() {
        assertDoesNotThrow(() -> {
            repositorio.deleteAll();
        });
        Assertions.assertThat(repositorio.count()).isEqualTo(0);
    }

    /**
     * Cenário de Teste 06
     * Objetivo: Verificar se a exclusão de uma entidade existente no banco de dados realmente ocorre.
     * monta o cenário,
     * - arquivo import.sql carrega o cenário (clientes cadastrados)
     * - id de um cliente que existe em import.sql
     * executa a ação
     * - executa o método encontrar por id para retornar a entidade do cliente com id informado.
     * - executa o método apagar por id
     * - executar novamente o método encontrar por id e verificar se o retorno dele é vazio
     * e valida a saída.
     * - verifica se o retorno do método encontrar por id é vazio.
     */

    @Test
    void testaSeApagarTodosTornaBDVazio2() {
        long idExistente = 1;
        Optional<Client> resultado = repositorio.findById(idExistente);
        repositorio.delete(resultado.get());
        Optional<Client> busca = repositorio.findById(idExistente);
        Assertions.assertThat(busca).isEmpty();
    }

    /**
     * Cenário de Teste 07
     * Objetivo: Verificar se um cliente pode ser excluído pelo cpf.
     * monta o cenário,
     * - arquivo import.sql carrega o cenário (clientes cadastrados)
     * - cpf de um cliente cadastrado
     * executa a ação
     * - executar um método para excluir um cliente pelo cpf (não existe ainda).
     * - buscar um cliente pelo cpf (não existe)
     * e valida a saída.
     * - a busca deve retornar vazia.
     */
    @Test
    void testaSeApagarClientePeloCPF() {
        repositorio.deleteClientByCPF("10204374161");
        Optional<Client> resultado = repositorio.findClientByCPf("10204374161");
        Assertions.assertThat(resultado).isEmpty();
    }

    /**
     * Caso de teste 08
     * INSERT INTO tb_client (name, cpf, income, birth_date, children) VALUES('Carolina Maria de Jesus', '10419244771', 7500.0, TIMESTAMP WITH TIME ZONE '1996-12-23T07:00:00Z', 0);
     * INSERT INTO tb_client (name, cpf, income, birth_date, children) VALUES('Gilberto Gil', '10419344882', 2500.0, TIMESTAMP WITH TIME ZONE '1949-05-05T07:00:00Z', 4);
     */
    @Test
    @DisplayName("Testar se a busca por cpf com Like retorna a lista esperada.")
    void testaBuscaClientesInicioCPFQueExiste() {
        //Arrange
        String parteCpfBuscado = "104";
        int tamanhoEsperado = 2;
        String cpfClientesEsperados[] = {"10419244771", "10419344882"};
        //act
        List<Client> resultado = repositorio.findByCpfStartingWith(parteCpfBuscado);
        //Assign
        Assertions.assertThat(resultado).isNotEmpty();
        Assertions.assertThat(resultado.size()).isEqualTo(tamanhoEsperado);
        Assertions.assertThat(resultado.get(0).getCpf()).isEqualTo(cpfClientesEsperados[0]);
        Assertions.assertThat(resultado.get(1).getCpf()).isEqualTo(cpfClientesEsperados[1]);
    }
}
