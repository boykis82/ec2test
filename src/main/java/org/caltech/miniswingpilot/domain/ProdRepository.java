package org.caltech.miniswingpilot.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProdRepository extends JpaRepository<Prod, String> {
    List<Prod> findByProdNmContainingOrderByProdId(String prodNm);
}
