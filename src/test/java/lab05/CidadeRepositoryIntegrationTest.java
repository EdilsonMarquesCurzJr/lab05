package lab05;

import lab05.model.Cidade;
import lab05.repository.CidadeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.validation.ConstraintViolationException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CidadeRepositoryIntegrationTest {
    @Autowired
    private CidadeRepository cidadeRepository;

    @Test
    void deveSalvarCidadeValida() {
        Cidade cidade = new Cidade();
        cidade.setNome("São Paulo");
        cidade.setEstado("SP");
        cidade.setTaxaEntrega(new BigDecimal("15.00"));
        Cidade salvo = cidadeRepository.save(cidade);
        assertNotNull(salvo.getId());
    }

    @Test
    void naoDeveSalvarCidadeInvalida() {
        Cidade cidade = new Cidade();
        cidade.setNome(null);
        cidade.setEstado(null);
        cidade.setTaxaEntrega(null);
        assertThrows(ConstraintViolationException.class, () -> {
            cidadeRepository.saveAndFlush(cidade);
        });
    }

    @Test
    void deveBuscarPorNome() {
        Cidade cidade = new Cidade();
        cidade.setNome("Campinas");
        cidade.setEstado("SP");
        cidade.setTaxaEntrega(new BigDecimal("10.00"));
        cidadeRepository.save(cidade);
        Optional<Cidade> encontrado = cidadeRepository.findByNome("Campinas");
        assertTrue(encontrado.isPresent());
        assertEquals("SP", encontrado.get().getEstado());
    }
}