package lab05;

import lab05.model.Cliente;
import lab05.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.validation.ConstraintViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ClienteRepositoryIntegrationTest {
    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void deveSalvarClienteValido() {
        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setTelefone("11999999999");
        Cliente salvo = clienteRepository.save(cliente);
        assertNotNull(salvo.getId());
    }

    @Test
    void naoDeveSalvarClienteInvalido() {
        Cliente cliente = new Cliente();
        cliente.setNome(null);
        cliente.setTelefone(null);
        assertThrows(ConstraintViolationException.class, () -> {
            clienteRepository.saveAndFlush(cliente);
        });
    }

    @Test
    void deveBuscarPorTelefone() {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria");
        cliente.setTelefone("1188888888");
        clienteRepository.save(cliente);
        Optional<Cliente> encontrado = clienteRepository.findByTelefone("1188888888");
        assertTrue(encontrado.isPresent());
        assertEquals("Maria", encontrado.get().getNome());
    }
}