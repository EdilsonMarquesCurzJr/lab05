package lab05.repository;

import lab05.model.Frete;
import lab05.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FreteRepository extends JpaRepository<Frete, Long> {
    List<Frete> findByClienteOrderByValorDesc(Cliente cliente);
}