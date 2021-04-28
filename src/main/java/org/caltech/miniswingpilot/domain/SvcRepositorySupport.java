package org.caltech.miniswingpilot.domain;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.caltech.miniswingpilot.domain.QSvc.svc;
import static org.caltech.miniswingpilot.domain.QProd.prod;

@Repository
@RequiredArgsConstructor
public class SvcRepositorySupport  {
    private final JPAQueryFactory queryFactory;

    public List<Svc> findByCustAndSvcStCd(int offset,
                                          int limit,
                                          Cust nmCust,
                                          boolean includeTermSvc) {
        QueryResults<Svc> result = queryFactory.selectFrom(svc)
                .innerJoin(svc.feeProd).fetchJoin()
                .where(
                        //-- 동일명의
                        (svc.cust.eq(nmCust)),
                        //-- 해지서비스포함여부에 따라 서비스상태 조건 체크
                        (includeTermSvc ? null : svc.svcStCd.in(SvcStCd.AC, SvcStCd.SP))
                )
                .offset(offset)
                .limit(limit)
                .orderBy( svc.svcStCd.asc(), svc.svcScrbDt.asc() )
                .fetchResults();

        return result.getResults();
    }
}

