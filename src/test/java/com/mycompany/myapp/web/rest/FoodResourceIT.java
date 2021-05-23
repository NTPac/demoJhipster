package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Food;
import com.mycompany.myapp.repository.FoodRepository;
import com.mycompany.myapp.service.criteria.FoodCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FoodResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FoodResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CATORY = "AAAAAAAAAA";
    private static final String UPDATED_CATORY = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CALO = "AAAAAAAAAA";
    private static final String UPDATED_CALO = "BBBBBBBBBB";

    private static final String DEFAULT_PRICE = "AAAAAAAAAA";
    private static final String UPDATED_PRICE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/foods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFoodMockMvc;

    private Food food;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Food createEntity(EntityManager em) {
        Food food = new Food()
            .name(DEFAULT_NAME)
            .catory(DEFAULT_CATORY)
            .description(DEFAULT_DESCRIPTION)
            .calo(DEFAULT_CALO)
            .price(DEFAULT_PRICE);
        return food;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Food createUpdatedEntity(EntityManager em) {
        Food food = new Food()
            .name(UPDATED_NAME)
            .catory(UPDATED_CATORY)
            .description(UPDATED_DESCRIPTION)
            .calo(UPDATED_CALO)
            .price(UPDATED_PRICE);
        return food;
    }

    @BeforeEach
    public void initTest() {
        food = createEntity(em);
    }

    @Test
    @Transactional
    void createFood() throws Exception {
        int databaseSizeBeforeCreate = foodRepository.findAll().size();
        // Create the Food
        restFoodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(food)))
            .andExpect(status().isCreated());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeCreate + 1);
        Food testFood = foodList.get(foodList.size() - 1);
        assertThat(testFood.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFood.getCatory()).isEqualTo(DEFAULT_CATORY);
        assertThat(testFood.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFood.getCalo()).isEqualTo(DEFAULT_CALO);
        assertThat(testFood.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void createFoodWithExistingId() throws Exception {
        // Create the Food with an existing ID
        food.setId(1L);

        int databaseSizeBeforeCreate = foodRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFoodMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(food)))
            .andExpect(status().isBadRequest());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFoods() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList
        restFoodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(food.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].catory").value(hasItem(DEFAULT_CATORY)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].calo").value(hasItem(DEFAULT_CALO)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)));
    }

    @Test
    @Transactional
    void getFood() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get the food
        restFoodMockMvc
            .perform(get(ENTITY_API_URL_ID, food.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(food.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.catory").value(DEFAULT_CATORY))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.calo").value(DEFAULT_CALO))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE));
    }

    @Test
    @Transactional
    void getFoodsByIdFiltering() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        Long id = food.getId();

        defaultFoodShouldBeFound("id.equals=" + id);
        defaultFoodShouldNotBeFound("id.notEquals=" + id);

        defaultFoodShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFoodShouldNotBeFound("id.greaterThan=" + id);

        defaultFoodShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFoodShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFoodsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where name equals to DEFAULT_NAME
        defaultFoodShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the foodList where name equals to UPDATED_NAME
        defaultFoodShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFoodsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where name not equals to DEFAULT_NAME
        defaultFoodShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the foodList where name not equals to UPDATED_NAME
        defaultFoodShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFoodsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where name in DEFAULT_NAME or UPDATED_NAME
        defaultFoodShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the foodList where name equals to UPDATED_NAME
        defaultFoodShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFoodsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where name is not null
        defaultFoodShouldBeFound("name.specified=true");

        // Get all the foodList where name is null
        defaultFoodShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllFoodsByNameContainsSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where name contains DEFAULT_NAME
        defaultFoodShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the foodList where name contains UPDATED_NAME
        defaultFoodShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFoodsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where name does not contain DEFAULT_NAME
        defaultFoodShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the foodList where name does not contain UPDATED_NAME
        defaultFoodShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFoodsByCatoryIsEqualToSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where catory equals to DEFAULT_CATORY
        defaultFoodShouldBeFound("catory.equals=" + DEFAULT_CATORY);

        // Get all the foodList where catory equals to UPDATED_CATORY
        defaultFoodShouldNotBeFound("catory.equals=" + UPDATED_CATORY);
    }

    @Test
    @Transactional
    void getAllFoodsByCatoryIsNotEqualToSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where catory not equals to DEFAULT_CATORY
        defaultFoodShouldNotBeFound("catory.notEquals=" + DEFAULT_CATORY);

        // Get all the foodList where catory not equals to UPDATED_CATORY
        defaultFoodShouldBeFound("catory.notEquals=" + UPDATED_CATORY);
    }

    @Test
    @Transactional
    void getAllFoodsByCatoryIsInShouldWork() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where catory in DEFAULT_CATORY or UPDATED_CATORY
        defaultFoodShouldBeFound("catory.in=" + DEFAULT_CATORY + "," + UPDATED_CATORY);

        // Get all the foodList where catory equals to UPDATED_CATORY
        defaultFoodShouldNotBeFound("catory.in=" + UPDATED_CATORY);
    }

    @Test
    @Transactional
    void getAllFoodsByCatoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where catory is not null
        defaultFoodShouldBeFound("catory.specified=true");

        // Get all the foodList where catory is null
        defaultFoodShouldNotBeFound("catory.specified=false");
    }

    @Test
    @Transactional
    void getAllFoodsByCatoryContainsSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where catory contains DEFAULT_CATORY
        defaultFoodShouldBeFound("catory.contains=" + DEFAULT_CATORY);

        // Get all the foodList where catory contains UPDATED_CATORY
        defaultFoodShouldNotBeFound("catory.contains=" + UPDATED_CATORY);
    }

    @Test
    @Transactional
    void getAllFoodsByCatoryNotContainsSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where catory does not contain DEFAULT_CATORY
        defaultFoodShouldNotBeFound("catory.doesNotContain=" + DEFAULT_CATORY);

        // Get all the foodList where catory does not contain UPDATED_CATORY
        defaultFoodShouldBeFound("catory.doesNotContain=" + UPDATED_CATORY);
    }

    @Test
    @Transactional
    void getAllFoodsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where description equals to DEFAULT_DESCRIPTION
        defaultFoodShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the foodList where description equals to UPDATED_DESCRIPTION
        defaultFoodShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFoodsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where description not equals to DEFAULT_DESCRIPTION
        defaultFoodShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the foodList where description not equals to UPDATED_DESCRIPTION
        defaultFoodShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFoodsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultFoodShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the foodList where description equals to UPDATED_DESCRIPTION
        defaultFoodShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFoodsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where description is not null
        defaultFoodShouldBeFound("description.specified=true");

        // Get all the foodList where description is null
        defaultFoodShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllFoodsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where description contains DEFAULT_DESCRIPTION
        defaultFoodShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the foodList where description contains UPDATED_DESCRIPTION
        defaultFoodShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFoodsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where description does not contain DEFAULT_DESCRIPTION
        defaultFoodShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the foodList where description does not contain UPDATED_DESCRIPTION
        defaultFoodShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFoodsByCaloIsEqualToSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where calo equals to DEFAULT_CALO
        defaultFoodShouldBeFound("calo.equals=" + DEFAULT_CALO);

        // Get all the foodList where calo equals to UPDATED_CALO
        defaultFoodShouldNotBeFound("calo.equals=" + UPDATED_CALO);
    }

    @Test
    @Transactional
    void getAllFoodsByCaloIsNotEqualToSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where calo not equals to DEFAULT_CALO
        defaultFoodShouldNotBeFound("calo.notEquals=" + DEFAULT_CALO);

        // Get all the foodList where calo not equals to UPDATED_CALO
        defaultFoodShouldBeFound("calo.notEquals=" + UPDATED_CALO);
    }

    @Test
    @Transactional
    void getAllFoodsByCaloIsInShouldWork() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where calo in DEFAULT_CALO or UPDATED_CALO
        defaultFoodShouldBeFound("calo.in=" + DEFAULT_CALO + "," + UPDATED_CALO);

        // Get all the foodList where calo equals to UPDATED_CALO
        defaultFoodShouldNotBeFound("calo.in=" + UPDATED_CALO);
    }

    @Test
    @Transactional
    void getAllFoodsByCaloIsNullOrNotNull() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where calo is not null
        defaultFoodShouldBeFound("calo.specified=true");

        // Get all the foodList where calo is null
        defaultFoodShouldNotBeFound("calo.specified=false");
    }

    @Test
    @Transactional
    void getAllFoodsByCaloContainsSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where calo contains DEFAULT_CALO
        defaultFoodShouldBeFound("calo.contains=" + DEFAULT_CALO);

        // Get all the foodList where calo contains UPDATED_CALO
        defaultFoodShouldNotBeFound("calo.contains=" + UPDATED_CALO);
    }

    @Test
    @Transactional
    void getAllFoodsByCaloNotContainsSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where calo does not contain DEFAULT_CALO
        defaultFoodShouldNotBeFound("calo.doesNotContain=" + DEFAULT_CALO);

        // Get all the foodList where calo does not contain UPDATED_CALO
        defaultFoodShouldBeFound("calo.doesNotContain=" + UPDATED_CALO);
    }

    @Test
    @Transactional
    void getAllFoodsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where price equals to DEFAULT_PRICE
        defaultFoodShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the foodList where price equals to UPDATED_PRICE
        defaultFoodShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllFoodsByPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where price not equals to DEFAULT_PRICE
        defaultFoodShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);

        // Get all the foodList where price not equals to UPDATED_PRICE
        defaultFoodShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllFoodsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultFoodShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the foodList where price equals to UPDATED_PRICE
        defaultFoodShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllFoodsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where price is not null
        defaultFoodShouldBeFound("price.specified=true");

        // Get all the foodList where price is null
        defaultFoodShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllFoodsByPriceContainsSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where price contains DEFAULT_PRICE
        defaultFoodShouldBeFound("price.contains=" + DEFAULT_PRICE);

        // Get all the foodList where price contains UPDATED_PRICE
        defaultFoodShouldNotBeFound("price.contains=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllFoodsByPriceNotContainsSomething() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foodList where price does not contain DEFAULT_PRICE
        defaultFoodShouldNotBeFound("price.doesNotContain=" + DEFAULT_PRICE);

        // Get all the foodList where price does not contain UPDATED_PRICE
        defaultFoodShouldBeFound("price.doesNotContain=" + UPDATED_PRICE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFoodShouldBeFound(String filter) throws Exception {
        restFoodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(food.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].catory").value(hasItem(DEFAULT_CATORY)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].calo").value(hasItem(DEFAULT_CALO)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)));

        // Check, that the count call also returns 1
        restFoodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFoodShouldNotBeFound(String filter) throws Exception {
        restFoodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFoodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFood() throws Exception {
        // Get the food
        restFoodMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFood() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        int databaseSizeBeforeUpdate = foodRepository.findAll().size();

        // Update the food
        Food updatedFood = foodRepository.findById(food.getId()).get();
        // Disconnect from session so that the updates on updatedFood are not directly saved in db
        em.detach(updatedFood);
        updatedFood.name(UPDATED_NAME).catory(UPDATED_CATORY).description(UPDATED_DESCRIPTION).calo(UPDATED_CALO).price(UPDATED_PRICE);

        restFoodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFood.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFood))
            )
            .andExpect(status().isOk());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
        Food testFood = foodList.get(foodList.size() - 1);
        assertThat(testFood.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFood.getCatory()).isEqualTo(UPDATED_CATORY);
        assertThat(testFood.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFood.getCalo()).isEqualTo(UPDATED_CALO);
        assertThat(testFood.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingFood() throws Exception {
        int databaseSizeBeforeUpdate = foodRepository.findAll().size();
        food.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFoodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, food.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(food))
            )
            .andExpect(status().isBadRequest());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFood() throws Exception {
        int databaseSizeBeforeUpdate = foodRepository.findAll().size();
        food.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFoodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(food))
            )
            .andExpect(status().isBadRequest());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFood() throws Exception {
        int databaseSizeBeforeUpdate = foodRepository.findAll().size();
        food.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFoodMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(food)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFoodWithPatch() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        int databaseSizeBeforeUpdate = foodRepository.findAll().size();

        // Update the food using partial update
        Food partialUpdatedFood = new Food();
        partialUpdatedFood.setId(food.getId());

        partialUpdatedFood.catory(UPDATED_CATORY).calo(UPDATED_CALO);

        restFoodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFood.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFood))
            )
            .andExpect(status().isOk());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
        Food testFood = foodList.get(foodList.size() - 1);
        assertThat(testFood.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFood.getCatory()).isEqualTo(UPDATED_CATORY);
        assertThat(testFood.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFood.getCalo()).isEqualTo(UPDATED_CALO);
        assertThat(testFood.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateFoodWithPatch() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        int databaseSizeBeforeUpdate = foodRepository.findAll().size();

        // Update the food using partial update
        Food partialUpdatedFood = new Food();
        partialUpdatedFood.setId(food.getId());

        partialUpdatedFood
            .name(UPDATED_NAME)
            .catory(UPDATED_CATORY)
            .description(UPDATED_DESCRIPTION)
            .calo(UPDATED_CALO)
            .price(UPDATED_PRICE);

        restFoodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFood.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFood))
            )
            .andExpect(status().isOk());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
        Food testFood = foodList.get(foodList.size() - 1);
        assertThat(testFood.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFood.getCatory()).isEqualTo(UPDATED_CATORY);
        assertThat(testFood.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFood.getCalo()).isEqualTo(UPDATED_CALO);
        assertThat(testFood.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingFood() throws Exception {
        int databaseSizeBeforeUpdate = foodRepository.findAll().size();
        food.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFoodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, food.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(food))
            )
            .andExpect(status().isBadRequest());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFood() throws Exception {
        int databaseSizeBeforeUpdate = foodRepository.findAll().size();
        food.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFoodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(food))
            )
            .andExpect(status().isBadRequest());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFood() throws Exception {
        int databaseSizeBeforeUpdate = foodRepository.findAll().size();
        food.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFoodMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(food)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Food in the database
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFood() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        int databaseSizeBeforeDelete = foodRepository.findAll().size();

        // Delete the food
        restFoodMockMvc
            .perform(delete(ENTITY_API_URL_ID, food.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
