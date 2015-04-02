package com.mycompany.cafftrack.web.rest;

import com.mycompany.cafftrack.Application;
import com.mycompany.cafftrack.domain.LogEntry;
import com.mycompany.cafftrack.repository.LogEntryRepository;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LogEntryResource REST controller.
 *
 * @see LogEntryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class LogEntryResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    private static final DateTime DEFAULT_LOG_TIME = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_LOG_TIME = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_LOG_TIME_STR = dateTimeFormatter.print(DEFAULT_LOG_TIME);

    private static final Integer DEFAULT_QUANTITY = 0;
    private static final Integer UPDATED_QUANTITY = 1;

    @Inject
    private LogEntryRepository logEntryRepository;

    private MockMvc restLogEntryMockMvc;

    private LogEntry logEntry;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LogEntryResource logEntryResource = new LogEntryResource();
        ReflectionTestUtils.setField(logEntryResource, "logEntryRepository", logEntryRepository);
        this.restLogEntryMockMvc = MockMvcBuilders.standaloneSetup(logEntryResource).build();
    }

    @Before
    public void initTest() {
        logEntry = new LogEntry();
        logEntry.setLogTime(DEFAULT_LOG_TIME);
        logEntry.setQuantity(DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    public void createLogEntry() throws Exception {
        int databaseSizeBeforeCreate = logEntryRepository.findAll().size();

        // Create the LogEntry
        restLogEntryMockMvc.perform(post("/api/logEntrys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(logEntry)))
                .andExpect(status().isCreated());

        // Validate the LogEntry in the database
        List<LogEntry> logEntrys = logEntryRepository.findAll();
        assertThat(logEntrys).hasSize(databaseSizeBeforeCreate + 1);
        LogEntry testLogEntry = logEntrys.get(logEntrys.size() - 1);
        assertThat(testLogEntry.getLogTime().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_LOG_TIME);
        assertThat(testLogEntry.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    public void checkLogTimeIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(logEntryRepository.findAll()).hasSize(0);
        // set the field null
        logEntry.setLogTime(null);

        // Create the LogEntry, which fails.
        restLogEntryMockMvc.perform(post("/api/logEntrys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(logEntry)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<LogEntry> logEntrys = logEntryRepository.findAll();
        assertThat(logEntrys).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllLogEntrys() throws Exception {
        // Initialize the database
        logEntryRepository.saveAndFlush(logEntry);

        // Get all the logEntrys
        restLogEntryMockMvc.perform(get("/api/logEntrys"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(logEntry.getId().intValue())))
                .andExpect(jsonPath("$.[*].logTime").value(hasItem(DEFAULT_LOG_TIME_STR)))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }

    @Test
    @Transactional
    public void getLogEntry() throws Exception {
        // Initialize the database
        logEntryRepository.saveAndFlush(logEntry);

        // Get the logEntry
        restLogEntryMockMvc.perform(get("/api/logEntrys/{id}", logEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(logEntry.getId().intValue()))
            .andExpect(jsonPath("$.logTime").value(DEFAULT_LOG_TIME_STR))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    public void getNonExistingLogEntry() throws Exception {
        // Get the logEntry
        restLogEntryMockMvc.perform(get("/api/logEntrys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLogEntry() throws Exception {
        // Initialize the database
        logEntryRepository.saveAndFlush(logEntry);
		
		int databaseSizeBeforeUpdate = logEntryRepository.findAll().size();

        // Update the logEntry
        logEntry.setLogTime(UPDATED_LOG_TIME);
        logEntry.setQuantity(UPDATED_QUANTITY);
        restLogEntryMockMvc.perform(put("/api/logEntrys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(logEntry)))
                .andExpect(status().isOk());

        // Validate the LogEntry in the database
        List<LogEntry> logEntrys = logEntryRepository.findAll();
        assertThat(logEntrys).hasSize(databaseSizeBeforeUpdate);
        LogEntry testLogEntry = logEntrys.get(logEntrys.size() - 1);
        assertThat(testLogEntry.getLogTime().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_LOG_TIME);
        assertThat(testLogEntry.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    public void deleteLogEntry() throws Exception {
        // Initialize the database
        logEntryRepository.saveAndFlush(logEntry);
		
		int databaseSizeBeforeDelete = logEntryRepository.findAll().size();

        // Get the logEntry
        restLogEntryMockMvc.perform(delete("/api/logEntrys/{id}", logEntry.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<LogEntry> logEntrys = logEntryRepository.findAll();
        assertThat(logEntrys).hasSize(databaseSizeBeforeDelete - 1);
    }
}
