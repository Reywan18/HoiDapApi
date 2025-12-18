package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.faq;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.faq.FAQJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.faq.FAQFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class FAQSpecification {
    public static Specification<FAQJpaEntity> filter(FAQFilter filter) {
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(filter.getKeyword() != null && !filter.getKeyword().isEmpty()) {
                String likePattern = "%" + filter.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("tieuDe")), likePattern),
                        cb.like(cb.lower(root.get("noiDung")), likePattern)
                ));
            }

            if (filter.getChuDe() != null && !filter.getChuDe().isEmpty()) {
                predicates.add(cb.equal(root.get("chuDe"), filter.getChuDe()));
            }
            if (filter.getKhoaVien() != null && !filter.getKhoaVien().isEmpty()) {
                predicates.add(cb.equal(root.get("khoaVien"), filter.getKhoaVien()));
            }
            if (filter.getKhoaHoc() != null && !filter.getKhoaHoc().isEmpty()) {
                predicates.add(cb.equal(root.get("khoaHoc"), filter.getKhoaHoc()));
            }
            if (filter.getNamHoc() != null && !filter.getNamHoc().isEmpty()) {
                predicates.add(cb.equal(root.get("namHoc"), filter.getNamHoc()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
