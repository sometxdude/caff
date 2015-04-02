package com.mycompany.cafftrack.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.cafftrack.domain.LogEntry;
import com.mycompany.cafftrack.repository.LogEntryRepository;
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
 * REST controller for managing LogEntry.
 */
@RestController
@RequestMapping("/api")
public class LogEntryResource {

    private final Logger log = LoggerFactory.getLogger(LogEntryResource.class);

    @Inject
    private LogEntryRepository logEntryRepository;

    /**
     * POST  /logEntrys -> Create a new logEntry.
     */
    @RequestMapping(value = "/logEntrys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody LogEntry logEntry) throws URISyntaxException {
        log.debug("REST request to save LogEntry : {}", logEntry);
        if (logEntry.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new logEntry cannot already have an ID").build();
        }
        logEntryRepository.save(logEntry);
        return ResponseEntity.created(new URI("/api/logEntrys/" + logEntry.getId())).build();
    }

    /**
     * PUT  /logEntrys -> Updates an existing logEntry.
     */
    @RequestMapping(value = "/logEntrys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody LogEntry logEntry) throws URISyntaxException {
        log.debug("REST request to update LogEntry : {}", logEntry);
        if (logEntry.getId() == null) {
            return create(logEntry);
        }
        logEntryRepository.save(logEntry);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /logEntrys -> get all the logEntrys.
     */
    @RequestMapping(value = "/logEntrys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<LogEntry>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<LogEntry> page = logEntryRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/logEntrys", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /logEntrys/:id -> get the "id" logEntry.
     */
    @RequestMapping(value = "/logEntrys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LogEntry> get(@PathVariable Long id) {
        log.debug("REST request to get LogEntry : {}", id);
        return Optional.ofNullable(logEntryRepository.findOne(id))
            .map(logEntry -> new ResponseEntity<>(
                logEntry,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /logEntrys/:id -> delete the "id" logEntry.
     */
    @RequestMapping(value = "/logEntrys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete LogEntry : {}", id);
        logEntryRepository.delete(id);
    }
}
