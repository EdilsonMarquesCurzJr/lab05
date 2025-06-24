package lab05;

import lab05.model.Frete;
import lab05.model.Cliente;
import lab05.model.Cidade;
import lab05.repository.FreteRepository;
import lab05.repository.ClienteRepository;
import lab05.repository.CidadeRepository;
import lab05.service.FreteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FreteServiceTest {
    @Mock
    private FreteRepository freteRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private CidadeRepository cidadeRepository;
    @InjectMocks
    private FreteService freteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCadastrarFreteComSucesso() {
        Cliente cliente = new Cliente(1L, "João", "11999999999");
        Cidade cidade = new Cidade(1L, "São Paulo", "SP", new BigDecimal("10.00"));
        Frete frete = new Frete();
        frete.setCliente(cliente);
        frete.setCidade(cidade);
        frete.setPeso(new BigDecimal("2.0"));
        frete.setCodigo("F001");
        frete.setDescricao("Entrega");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cidadeRepository.findById(1L)).thenReturn(Optional.of(cidade));
        when(freteRepository.save(any(Frete.class))).thenAnswer(i -> i.getArgument(0));
        Frete salvo = freteService.cadastrarFrete(frete);
        assertEquals(new BigDecimal("30.00"), salvo.getValor());
    }

    @Test
    void deveLancarExcecaoSeClienteNaoExiste() {
        Frete frete = new Frete();
        frete.setCliente(new Cliente(99L, "X", "000"));
        frete.setCidade(new Cidade(1L, "São Paulo", "SP", new BigDecimal("10.00")));
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        when(cidadeRepository.findById(1L)).thenReturn(Optional.of(new Cidade()));
        assertThrows(IllegalArgumentException.class, () -> freteService.cadastrarFrete(frete));
    }

    @Test
    void deveLancarExcecaoSeCidadeNaoExiste() {
        Frete frete = new Frete();
        frete.setCliente(new Cliente(1L, "João", "11999999999"));
        frete.setCidade(new Cidade(99L, "X", "SP", new BigDecimal("10.00")));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(new Cliente()));
        when(cidadeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> freteService.cadastrarFrete(frete));
    }

    @Test
    void deveCalcularValorFreteCorretamente() {
        BigDecimal valor = freteService.calcularValorFrete(new BigDecimal("3.0"), new BigDecimal("5.00"));
        assertEquals(new BigDecimal("35.00"), valor);
    }

    @Test
    void deveRetornarFreteMaisCaro() {
        Frete f1 = new Frete();
        f1.setValor(new BigDecimal("10.00"));
        Frete f2 = new Frete();
        f2.setValor(new BigDecimal("50.00"));
        when(freteRepository.findAll()).thenReturn(Arrays.asList(f1, f2));
        Optional<Frete> maisCaro = freteService.freteMaisCaro();
        assertTrue(maisCaro.isPresent());
        assertEquals(new BigDecimal("50.00"), maisCaro.get().getValor());
    }

    @Test
    void deveRetornarCidadeComMaisFretes() {
        Cidade c1 = new Cidade(1L, "A", "SP", new BigDecimal("1.00"));
        Cidade c2 = new Cidade(2L, "B", "RJ", new BigDecimal("2.00"));
        Frete f1 = new Frete();
        f1.setCidade(c1);
        Frete f2 = new Frete();
        f2.setCidade(c1);
        Frete f3 = new Frete();
        f3.setCidade(c2);
        when(freteRepository.findAll()).thenReturn(Arrays.asList(f1, f2, f3));
        Optional<Cidade> mais = freteService.cidadeComMaisFretes();
        assertTrue(mais.isPresent());
        assertEquals(c1, mais.get());
    }
}