package com.np.onboarding.web.rest

import com.np.onboarding.service.RegionService
import com.np.onboarding.service.dto.RegionDTO
import com.np.onboarding.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private const val ENTITY_NAME = "region"
/**
 * REST controller for managing [com.np.onboarding.domain.Region].
 */
@RestController
@RequestMapping("/api")
class RegionResource(
    private val regionService: RegionService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /regions` : Create a new region.
     *
     * @param regionDTO the regionDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new regionDTO, or with status `400 (Bad Request)` if the region has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/regions")
    fun createRegion(@RequestBody regionDTO: RegionDTO): ResponseEntity<RegionDTO> {
        log.debug("REST request to save Region : {}", regionDTO)
        if (regionDTO.id != null) {
            throw BadRequestAlertException(
                "A new region cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = regionService.save(regionDTO)
        return ResponseEntity.created(URI("/api/regions/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /regions` : Updates an existing region.
     *
     * @param regionDTO the regionDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated regionDTO,
     * or with status `400 (Bad Request)` if the regionDTO is not valid,
     * or with status `500 (Internal Server Error)` if the regionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/regions")
    fun updateRegion(@RequestBody regionDTO: RegionDTO): ResponseEntity<RegionDTO> {
        log.debug("REST request to update Region : {}", regionDTO)
        if (regionDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = regionService.save(regionDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     regionDTO.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /regions` : get all the regions.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of regions in body.
     */
    @GetMapping("/regions")
    fun getAllRegions(): MutableList<RegionDTO> {
        log.debug("REST request to get all Regions")
        return regionService.findAll()
    }

    /**
     * `GET  /regions/:id` : get the "id" region.
     *
     * @param id the id of the regionDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the regionDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/regions/{id}")
    fun getRegion(@PathVariable id: Long): ResponseEntity<RegionDTO> {
        log.debug("REST request to get Region : {}", id)
        val regionDTO = regionService.findOne(id)
        return ResponseUtil.wrapOrNotFound(regionDTO)
    }
    /**
     *  `DELETE  /regions/:id` : delete the "id" region.
     *
     * @param id the id of the regionDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/regions/{id}")
    fun deleteRegion(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Region : {}", id)
        regionService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/regions?query=:query` : search for the region corresponding
     * to the query.
     *
     * @param query the query of the region search.
     * @return the result of the search.
     */
    @GetMapping("/_search/regions")
    fun searchRegions(@RequestParam query: String): MutableList<RegionDTO> {
        log.debug("REST request to search Regions for query {}", query)
        return regionService.search(query).toMutableList()
    }
}
