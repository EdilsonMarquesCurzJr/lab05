package lab05;

import lab05.model.Frete;
import lab05.model.Cliente;
import lab05.model.Cidade;
import lab05.repository.FreteRepository;
import lab05.repository.ClienteRepository;
import lab05.repository.CidadeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.validation.ConstraintViolationException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class FreteRepositoryIntegrationTest {
    @Autowired
    private FreteRepository freteRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private CidadeRepository cidadeRepository;

    @Test
    void deveSalvarFreteValido() {
        Cliente cliente = clienteRepository.save(new Cliente(null, "Carlos", "1177777777"));
        Cidade cidade = cidadeRepository.save(new Cidade(null, "Ribeirão Preto", "SP", new BigDecimal("12.00")));
        Frete frete = new Frete();
        frete.setCodigo("F001");
        frete.setDescricao("Entrega de livros");
        frete.setPeso(new BigDecimal("5.0"));
        frete.setCliente(cliente);
        frete.setCidade(cidade);
        frete.setValor(new BigDecimal("62.00"));
        Frete salvo = freteRepository.save(frete);
        assertNotNull(salvo.getId());
    }

    @Test
    void naoDeveSalvarFreteInvalido() {
        Frete frete = new Frete();
        assertThrows(ConstraintViolationException.class, () -> {
            freteRepository.saveAndFlush(frete);
        });
    }

    @Test
    void deveBuscarFretesPorClienteOrdenadoPorValor() {
        Cliente cliente = clienteRepository.save(new Cliente(null, "Ana", "1166666666"));
        Cidade cidade = cidadeRepository.save(new Cidade(null, "Sorocaba", "SP", new BigDecimal("8.00")));
        Frete frete1 = new Frete();
        frete1.setCodigo("F002");
        frete1.setDescricao("Entrega 1");
        frete1.setPeso(new BigDecimal("2.0"));
        frete1.setCliente(cliente);
        frete1.setCidade(cidade);
        frete1.setValor(new BigDecimal("28.00"));
        freteRepository.save(frete1);
        Frete frete2 = new Frete();
        frete2.setCodigo("F003");
        frete2.setDescricao("Entrega 2");
        frete2.setPeso(new BigDecimal("3.0"));
        frete2.setCliente(cliente);
        frete2.setCidade(cidade);
        frete2.setValor(new BigDecimal("38.00"));
        freteRepository.save(frete2);
        List<Frete> fretes = freteRepository.findByClienteOrderByValorDesc(cliente);
        assertEquals(2, fretes.size());
        assertEquals("F003", fretes.get(0).getCodigo());
    }
}