/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.gl.service.impl.scrubber;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.batch.collector.CollectorBatch;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.CollectorScrubberService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ScrubberService;
import org.kuali.module.gl.util.CollectorReportData;
import org.kuali.module.gl.util.CollectorScrubberStatus;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CollectorScrubberServiceImpl implements CollectorScrubberService {
    private DateTimeService dateTimeService;
    private UniversityDateDao universityDateDao;
    private KualiConfigurationService kualiConfigurationService;
    private PersistenceService persistenceService;
    private ScrubberService scrubberService;

    /**
     * @see org.kuali.module.gl.service.CollectorScrubberService#scrub(org.kuali.module.gl.batch.collector.CollectorBatch,
     *      org.kuali.module.gl.util.CollectorReportData)
     */
    public CollectorScrubberStatus scrub(CollectorBatch batch, CollectorReportData collectorReportData) {
        CollectorScrubberProcess collectorScrubberProcess = new CollectorScrubberProcess(batch, SpringContext.getBean(OriginEntryService.class), SpringContext.getBean(OriginEntryGroupService.class), kualiConfigurationService, persistenceService, scrubberService, collectorReportData);
        return collectorScrubberProcess.scrub();
    }

    /**
     * @see org.kuali.module.gl.service.CollectorScrubberService#removeTempGroups(java.util.Collection)
     */
    public void removeTempGroups(Collection<CollectorScrubberStatus> allStatusObjectsFromCollectorExecution) {
        for (CollectorScrubberStatus collectorScrubberStatus : allStatusObjectsFromCollectorExecution) {
            OriginEntryGroupService originEntryGroupService = collectorScrubberStatus.getOriginEntryGroupService();
            Collection<OriginEntryGroup> groupsToDelete = new ArrayList<OriginEntryGroup>();

            if (collectorScrubberStatus.getInputGroup() != null && collectorScrubberStatus.getInputGroup().getId() != null) {
                groupsToDelete.add(collectorScrubberStatus.getInputGroup());
            }
            if (collectorScrubberStatus.getValidGroup() != null && collectorScrubberStatus.getValidGroup().getId() != null) {
                groupsToDelete.add(collectorScrubberStatus.getValidGroup());
            }
            if (collectorScrubberStatus.getExpiredGroup() != null && collectorScrubberStatus.getExpiredGroup().getId() != null) {
                groupsToDelete.add(collectorScrubberStatus.getExpiredGroup());
            }
            if (collectorScrubberStatus.getErrorGroup() != null && collectorScrubberStatus.getErrorGroup().getId() != null) {
                groupsToDelete.add(collectorScrubberStatus.getErrorGroup());
            }
            if (!groupsToDelete.isEmpty()) {
                originEntryGroupService.deleteGroups(groupsToDelete);
            }
        }
    }

    protected Date calculateRunDate() {
        return dateTimeService.getCurrentSqlDate();
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the universityDateDao attribute value.
     * 
     * @param universityDateDao The universityDateDao to set.
     */
    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }

    /**
     * Gets the persistenceService attribute.
     * 
     * @return Returns the persistenceService.
     */
    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    /**
     * Sets the persistenceService attribute value.
     * 
     * @param persistenceService The persistenceService to set.
     */
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Gets the universityDateDao attribute.
     * 
     * @return Returns the universityDateDao.
     */
    public UniversityDateDao getUniversityDateDao() {
        return universityDateDao;
    }

    /**
     * Sets the scrubberService attribute value.
     * 
     * @param scrubberService The scrubberService to set.
     */
    public void setScrubberService(ScrubberService scrubberService) {
        this.scrubberService = scrubberService;
    }


}
