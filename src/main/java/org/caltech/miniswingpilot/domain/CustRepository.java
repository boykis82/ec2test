package org.caltech.miniswingpilot.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustRepository extends JpaRepository<Cust, Integer> {
    List<Cust> findByCustNmAndBirthDtOrderByCustRgstDtDesc(String custNm, LocalDate birthDt);
}
