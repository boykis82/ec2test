package org.caltech.miniswingpilot.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SvcProdRepository extends JpaRepository<SvcProd, Integer> {
    @Query("FROM SvcProd sp WHERE sp.svc = :svc")
    List<SvcProd> findAllSvcProds(Svc svc);

    @Query("FROM SvcProd sp WHERE sp.svc = :svc AND sp.termDt IS NULL")
    List<SvcProd> findActiveSvcProds(Svc svc);
}
