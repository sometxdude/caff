package com.mycompany.cafftrack.repository;

import com.mycompany.cafftrack.domain.LogEntry;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the LogEntry entity.
 */
public interface LogEntryRepository extends JpaRepository<LogEntry,Long> {

}
