package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Food;
import com.mycompany.myapp.repository.FoodRepository;
import com.mycompany.myapp.service.criteria.FoodCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Food} entities in the database.
 * The main input is a {@link FoodCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Food} or a {@link Page} of {@link Food} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FoodQueryService extends QueryService<Food> {

    private final Logger log = LoggerFactory.getLogger(FoodQueryService.class);

    private final FoodRepository foodRepository;

    public FoodQueryService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    /**
     * Return a {@link List} of {@link Food} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Food> findByCriteria(FoodCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Food> specification = createSpecification(criteria);
        return foodRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Food} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Food> findByCriteria(FoodCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Food> specification = createSpecification(criteria);
        return foodRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FoodCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Food> specification = createSpecification(criteria);
        return foodRepository.count(specification);
    }

    /**
     * Function to convert {@link FoodCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Food> createSpecification(FoodCriteria criteria) {
        Specification<Food> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Food_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Food_.name));
            }
            if (criteria.getCatory() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCatory(), Food_.catory));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Food_.description));
            }
            if (criteria.getCalo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCalo(), Food_.calo));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPrice(), Food_.price));
            }
        }
        return specification;
    }
}
