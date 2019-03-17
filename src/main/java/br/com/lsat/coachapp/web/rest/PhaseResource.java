package br.com.lsat.coachapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.com.lsat.coachapp.domain.Phase;
import br.com.lsat.coachapp.repository.PhaseRepository;
import br.com.lsat.coachapp.repository.search.PhaseSearchRepository;
import br.com.lsat.coachapp.web.rest.errors.BadRequestAlertException;
import br.com.lsat.coachapp.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Phase.
 */
@RestController
@RequestMapping("/api")
public class PhaseResource {

    private final Logger log = LoggerFactory.getLogger(PhaseResource.class);

    private static final String ENTITY_NAME = "phase";

    private final PhaseRepository phaseRepository;

    private final PhaseSearchRepository phaseSearchRepository;

    public PhaseResource(PhaseRepository phaseRepository, PhaseSearchRepository phaseSearchRepository) {
        this.phaseRepository = phaseRepository;
        this.phaseSearchRepository = phaseSearchRepository;
    }

    /**
     * POST  /phases : Create a new phase.
     *
     * @param phase the phase to create
     * @return the ResponseEntity with status 201 (Created) and with body the new phase, or with status 400 (Bad Request) if the phase has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/phases")
    @Timed
    public ResponseEntity<Phase> createPhase(@Valid @RequestBody Phase phase) throws URISyntaxException {
        log.debug("REST request to save Phase : {}", phase);
        if (phase.getId() != null) {
            throw new BadRequestAlertException("A new phase cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Phase result = phaseRepository.save(phase);
        phaseSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/phases/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /phases : Updates an existing phase.
     *
     * @param phase the phase to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated phase,
     * or with status 400 (Bad Request) if the phase is not valid,
     * or with status 500 (Internal Server Error) if the phase couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/phases")
    @Timed
    public ResponseEntity<Phase> updatePhase(@Valid @RequestBody Phase phase) throws URISyntaxException {
        log.debug("REST request to update Phase : {}", phase);
        if (phase.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Phase result = phaseRepository.save(phase);
        phaseSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, phase.getId().toString()))
            .body(result);
    }

    /**
     * GET  /phases : get all the phases.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of phases in body
     */
    @GetMapping("/phases")
    @Timed
    public List<Phase> getAllPhases() {
        log.debug("REST request to get all Phases");
        return phaseRepository.findAll();
    }

    /**
     * GET  /phases/:id : get the "id" phase.
     *
     * @param id the id of the phase to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the phase, or with status 404 (Not Found)
     */
    @GetMapping("/phases/{id}")
    @Timed
    public ResponseEntity<Phase> getPhase(@PathVariable Long id) {
        log.debug("REST request to get Phase : {}", id);
        Optional<Phase> phase = phaseRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(phase);
    }

    /**
     * DELETE  /phases/:id : delete the "id" phase.
     *
     * @param id the id of the phase to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/phases/{id}")
    @Timed
    public ResponseEntity<Void> deletePhase(@PathVariable Long id) {
        log.debug("REST request to delete Phase : {}", id);

        phaseRepository.deleteById(id);
        phaseSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/phases?query=:query : search for the phase corresponding
     * to the query.
     *
     * @param query the query of the phase search
     * @return the result of the search
     */
    @GetMapping("/_search/phases")
    @Timed
    public List<Phase> searchPhases(@RequestParam String query) {
        log.debug("REST request to search Phases for query {}", query);
        return StreamSupport
            .stream(phaseSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
