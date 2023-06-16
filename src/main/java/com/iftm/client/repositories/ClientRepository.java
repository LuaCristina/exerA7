package com.iftm.client.repositories;

import com.iftm.client.entities.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query(value = "SELECT c FROM Client c WHERE c.id = :id")
    public Optional<Client> findById(Long id);

    /*
    @Modifying
    @Query("DELETE FROM Client c WHERE c.id = :id")
    public void deleteById(Long id);
     */


    @Modifying
    @Query("DELETE FROM Client obj WHERE "
            + "obj.cpf = :cpf")
    void deleteClientByCPF(String cpf);

    @Query("SELECT DISTINCT obj FROM Client obj WHERE "
            + "obj.cpf = :cpf")
    Optional<Client> findClientByCPf(String cpf);

    public List<Client> findByCpfStartingWith(String cpf);

    Page<Client> findByIncome(double salarioI, Pageable pageable);

    Page<Client> findByIncomeGreaterThan(double salarioI, Pageable pageable);

    Page<Client> findByCpfLike(String parteCpf, Pageable pageable);


}