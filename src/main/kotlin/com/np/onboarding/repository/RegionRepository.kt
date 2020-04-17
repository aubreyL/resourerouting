package com.np.onboarding.repository

import com.np.onboarding.domain.Region
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Region] entity.
 */
@Suppress("unused")
@Repository
interface RegionRepository : JpaRepository<Region, Long>
