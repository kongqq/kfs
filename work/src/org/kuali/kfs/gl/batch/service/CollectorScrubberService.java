/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.gl.batch.service;

import java.util.Collection;

import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.impl.CollectorScrubberStatus;

/**
 * An interface declaring the methods needed to scrub Collector data
 */
public interface CollectorScrubberService {
    /**
     * Runs the scrubber on the origin entries in the batch. Any OEs edits/removals result of the scrub and demerger are removed
     * from the batch, and the same changes are reflected in the details in the same batch.
     * 
     * @param batch the data read in by the Collector
     * @param collectorReportData statistics generated by the scrub run on the Collector data
     * @return an object with the collector scrubber status. Note that it contains references to at least 4 origin entry groups, and
     *         the origin entry group service and origin entry service under which these groups and their entries are stored. The
     *         groups and their entries are created to facilitate the scrub and reporting processes, and they should not be
     *         persisted after the collector finishes running. Therefore, an collection of all CollectorScrubberStatus objects
     *         returned in a single collector execution (i.e. from a nightly job) must be passed into a parameter to the
     *         {@link #removeTempGroups(Collection)} method.. The service definitions are needed because the collector may choose to
     *         store temporary origin entries and origin entry groups in another service segregated from the database.
     */
    public CollectorScrubberStatus scrub(CollectorBatch batch, CollectorReportData collectorReportData, String collectorFileDirectoryName);

    /**
     * Removes any temporarily created origin entries and origin entry groups so that they won't be persisted after the transaction
     * is committed.
     * 
     * @param allStatusObjectsFromCollectorExecution a Collection of ScrubberStatus records to help find bad Collector data
     */
    public void removeTempGroups(Collection<CollectorScrubberStatus> allStatusObjectsFromCollectorExecution);
}
