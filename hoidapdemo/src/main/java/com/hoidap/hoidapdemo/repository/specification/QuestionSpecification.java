package com.hoidap.hoidapdemo.repository.specification;

import com.hoidap.hoidapdemo.entity.question.QuestionJpaEntity;
import com.hoidap.hoidapdemo.dto.question.QuestionFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class QuestionSpecification {
    public static Specification<QuestionJpaEntity> filter(QuestionFilter criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getKeyword())) {
                String likePattern = "%" + criteria.getKeyword().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("tieuDe")), likePattern);
                Predicate contentLike = cb.like(cb.lower(root.get("noiDung")), likePattern);
                predicates.add(cb.or(titleLike, contentLike));
            }

            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("trangThai"), criteria.getStatus()));
            }

            if (StringUtils.hasText(criteria.getMaSv())) {
                predicates.add(cb.equal(root.get("sinhVien").get("maSv"), criteria.getMaSv()));
            }

            if (StringUtils.hasText(criteria.getMaCv())) {
                predicates.add(cb.equal(root.get("cvht").get("maCv"), criteria.getMaCv()));
            }

            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayGui"), criteria.getFromDate().atStartOfDay()));
            }
            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayGui"), criteria.getToDate().atTime(LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
