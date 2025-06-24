package lab05;

import lab05.model.Cidade;
import lab05.model.Cliente;
import lab05.model.Frete;
import lab05.repository.CidadeRepository;
import lab05.repository.ClienteRepository;
import lab05.repository.FreteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FreteControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private FreteRepository freteRepository;

    private Cliente cliente;
    private Cidade cidade;

    @BeforeEach
    void setUp() {
        freteRepository.deleteAll();
        clienteRepository.deleteAll();
        cidadeRepository.deleteAll();
        cliente = clienteRepository.save(new Cliente(null, "João", "11999999999"));
        cidade = cidadeRepository.save(new Cidade(null, "São Paulo", "SP", new BigDecimal("10.00")));
    }

    String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void deveCadastrarFreteComSucesso() {
        Frete frete = new Frete();
        frete.setCodigo("F001");
        frete.setDescricao("Entrega de livros");
        frete.setPeso(new BigDecimal("2.0"));
        frete.setCliente(cliente);
        frete.setCidade(cidade);
        ResponseEntity<Frete> response = restTemplate.postForEntity(url("/fretes"), frete, Frete.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals(new BigDecimal("30.00"), response.getBody().getValor());
    }

    @Test
    void deveRetornarFretesPorCliente() {
        Frete frete = new Frete();
        frete.setCodigo("F002");
        frete.setDescricao("Entrega 2");
        frete.setPeso(new BigDecimal("1.0"));
        frete.setCliente(cliente);
        frete.setCidade(cidade);
        frete.setValor(new BigDecimal("20.00"));
        freteRepository.save(frete);
        ResponseEntity<Frete[]> response = restTemplate.getForEntity(url("/fretes/cliente/" + cliente.getId()),
                Frete[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 1);
    }

    @Test
    void deveRetornarErroSeClienteNaoExisteNoCadastroFrete() {
        Frete frete = new Frete();
        frete.setCodigo("F003");
        frete.setDescricao("Entrega 3");
        frete.setPeso(new BigDecimal("1.0"));
        frete.setCliente(new Cliente(999L, "X", "000"));
        frete.setCidade(cidade);
        ResponseEntity<String> response = restTemplate.postForEntity(url("/fretes"), frete, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Cliente não encontrado"));
    }

    @Test
    void deveRetornarErroSeCidadeNaoExisteNoCadastroFrete() {
        Frete frete = new Frete();
        frete.setCodigo("F004");
        frete.setDescricao("Entrega 4");
        frete.setPeso(new BigDecimal("1.0"));
        frete.setCliente(cliente);
        frete.setCidade(new Cidade(999L, "X", "XX", new BigDecimal("1.0")));
        ResponseEntity<String> response = restTemplate.postForEntity(url("/fretes"), frete, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Cidade não encontrada"));
    }
}