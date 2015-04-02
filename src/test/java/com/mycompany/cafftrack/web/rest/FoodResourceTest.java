package com.mycompany.cafftrack.web.rest;

import com.mycompany.cafftrack.Application;
import com.mycompany.cafftrack.domain.Food;
import com.mycompany.cafftrack.repository.FoodRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FoodResource REST controller.
 *
 * @see FoodResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class FoodResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Integer DEFAULT_CAFFEINE = 0;
    private static final Integer UPDATED_CAFFEINE = 1;
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    @Inject
    private FoodRepository foodRepository;

    private MockMvc restFoodMockMvc;

    private Food food;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FoodResource foodResource = new FoodResource();
        ReflectionTestUtils.setField(foodResource, "foodRepository", foodRepository);
        this.restFoodMockMvc = MockMvcBuilders.standaloneSetup(foodResource).build();
    }

    @Before
    public void initTest() {
        food = new Food();
        food.setName(DEFAULT_NAME);
        food.setCaffeine(DEFAULT_CAFFEINE);
        food.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createFood() throws Exception {
        int databaseSizeBeforeCreate = foodRepository.findAll().size();

        // Create the Food
        restFoodMockMvc.perform(post("/api/foods")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(food)))
                .andExpect(status().isCreated());

        // Validate the Food in the database
        List<Food> foods = foodRepository.findAll();
        assertThat(foods).hasSize(databaseSizeBeforeCreate + 1);
        Food testFood = foods.get(foods.size() - 1);
        assertThat(testFood.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFood.getCaffeine()).isEqualTo(DEFAULT_CAFFEINE);
        assertThat(testFood.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(foodRepository.findAll()).hasSize(0);
        // set the field null
        food.setName(null);

        // Create the Food, which fails.
        restFoodMockMvc.perform(post("/api/foods")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(food)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Food> foods = foodRepository.findAll();
        assertThat(foods).hasSize(0);
    }

    @Test
    @Transactional
    public void checkCaffeineIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(foodRepository.findAll()).hasSize(0);
        // set the field null
        food.setCaffeine(null);

        // Create the Food, which fails.
        restFoodMockMvc.perform(post("/api/foods")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(food)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Food> foods = foodRepository.findAll();
        assertThat(foods).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllFoods() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get all the foods
        restFoodMockMvc.perform(get("/api/foods"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(food.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].caffeine").value(hasItem(DEFAULT_CAFFEINE)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getFood() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get the food
        restFoodMockMvc.perform(get("/api/foods/{id}", food.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(food.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.caffeine").value(DEFAULT_CAFFEINE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFood() throws Exception {
        // Get the food
        restFoodMockMvc.perform(get("/api/foods/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFood() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);
		
		int databaseSizeBeforeUpdate = foodRepository.findAll().size();

        // Update the food
        food.setName(UPDATED_NAME);
        food.setCaffeine(UPDATED_CAFFEINE);
        food.setDescription(UPDATED_DESCRIPTION);
        restFoodMockMvc.perform(put("/api/foods")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(food)))
                .andExpect(status().isOk());

        // Validate the Food in the database
        List<Food> foods = foodRepository.findAll();
        assertThat(foods).hasSize(databaseSizeBeforeUpdate);
        Food testFood = foods.get(foods.size() - 1);
        assertThat(testFood.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFood.getCaffeine()).isEqualTo(UPDATED_CAFFEINE);
        assertThat(testFood.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteFood() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);
		
		int databaseSizeBeforeDelete = foodRepository.findAll().size();

        // Get the food
        restFoodMockMvc.perform(delete("/api/foods/{id}", food.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Food> foods = foodRepository.findAll();
        assertThat(foods).hasSize(databaseSizeBeforeDelete - 1);
    }
}
