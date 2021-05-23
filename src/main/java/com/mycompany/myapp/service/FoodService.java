package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Food;
import com.mycompany.myapp.repository.FoodRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Food}.
 */
@Service
@Transactional
public class FoodService {

    private final Logger log = LoggerFactory.getLogger(FoodService.class);

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    /**
     * Save a food.
     *
     * @param food the entity to save.
     * @return the persisted entity.
     */
    public Food save(Food food) {
        log.debug("Request to save Food : {}", food);
        return foodRepository.save(food);
    }

    /**
     * Partially update a food.
     *
     * @param food the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Food> partialUpdate(Food food) {
        log.debug("Request to partially update Food : {}", food);

        return foodRepository
            .findById(food.getId())
            .map(
                existingFood -> {
                    if (food.getName() != null) {
                        existingFood.setName(food.getName());
                    }
                    if (food.getCatory() != null) {
                        existingFood.setCatory(food.getCatory());
                    }
                    if (food.getDescription() != null) {
                        existingFood.setDescription(food.getDescription());
                    }
                    if (food.getCalo() != null) {
                        existingFood.setCalo(food.getCalo());
                    }
                    if (food.getPrice() != null) {
                        existingFood.setPrice(food.getPrice());
                    }

                    return existingFood;
                }
            )
            .map(foodRepository::save);
    }

    /**
     * Get all the foods.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Food> findAll(Pageable pageable) {
        log.debug("Request to get all Foods");
        return foodRepository.findAll(pageable);
    }

    /**
     * Get one food by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Food> findOne(Long id) {
        log.debug("Request to get Food : {}", id);
        return foodRepository.findById(id);
    }

    /**
     * Delete the food by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Food : {}", id);
        foodRepository.deleteById(id);
    }
}
