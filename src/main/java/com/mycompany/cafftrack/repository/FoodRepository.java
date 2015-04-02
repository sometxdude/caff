package com.mycompany.cafftrack.repository;

import com.mycompany.cafftrack.domain.Food;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Food entity.
 */
public interface FoodRepository extends JpaRepository<Food,Long> {

}
