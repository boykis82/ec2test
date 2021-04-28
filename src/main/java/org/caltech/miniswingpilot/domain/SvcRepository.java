package org.caltech.miniswingpilot.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface SvcRepository extends JpaRepository<Svc, Integer> {

    Optional<Svc> findBySvcNumAndSvcStCd(String svcNum, SvcStCd svcStCd);

    Set<Svc> findByCust(Cust cust);
}
