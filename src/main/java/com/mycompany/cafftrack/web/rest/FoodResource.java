package com.mycompany.cafftrack.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.cafftrack.domain.Food;
import com.mycompany.cafftrack.repository.FoodRepository;
import com.mycompany.cafftrack.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Food.
 */
@RestController
@RequestMapping("/api")
public class FoodResource {

    private final Logger log = LoggerFactory.getLogger(FoodResource.class);

    @Inject
    private FoodRepository foodRepository;

    /**
     * POST  /foods -> Create a new food.
     */
    @RequestMapping(value = "/foods",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Food food) throws URISyntaxException {
        log.debug("REST request to save Food : {}", food);
        if (food.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new food cannot already have an ID").build();
        }
        foodRepository.save(food);
        return ResponseEntity.created(new URI("/api/foods/" + food.getId())).build();
    }

    /**
     * PUT  /foods -> Updates an existing food.
     */
    @RequestMapping(value = "/foods",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Food food) throws URISyntaxException {
        log.debug("REST request to update Food : {}", food);
        if (food.getId() == null) {
            return create(food);
        }
        foodRepository.save(food);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /foods -> get all the foods.
     */
    @RequestMapping(value = "/foods",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Food>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Food> page = foodRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/foods", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /foods/:id -> get the "id" food.
     */
    @RequestMapping(value = "/foods/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Food> get(@PathVariable Long id) {
        log.debug("REST request to get Food : {}", id);
        return Optional.ofNullable(foodRepository.findOne(id))
            .map(food -> new ResponseEntity<>(
                food,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /foods/:id -> delete the "id" food.
     */
    @RequestMapping(value = "/foods/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Food : {}", id);
        foodRepository.delete(id);
    }
}
