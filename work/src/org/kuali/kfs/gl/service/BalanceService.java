/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;

public interface BalanceService {
    /**
     * Save
     * 
     * @param b
     */
    public void save(Balance b);

    public boolean hasAssetLiabilityFundBalanceBalances(Account account);

    public boolean fundBalanceWillNetToZero(Account account);

    public boolean hasEncumbrancesOrBaseBudgets(Account account);

    public boolean beginningBalanceLoaded(Account account);

    public boolean hasAssetLiabilityOrFundBalance(Account account);

    /**
     * @param fiscalYear
     * @return an Iterator over all balances for a given year
     */
    public Iterator<Balance> findBalancesForFiscalYear(Integer fiscalYear);

    /**
     * This method finds the summary records of balance entries according to input fields an values
     * 
     * @param fieldValues the input fields an values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     */
    public Iterator findCashBalance(Map fieldValues, boolean isConsolidated);

    /**
     * This method gets the size of cash balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the size of cash balance entries
     */
    public Integer getCashBalanceRecordCount(Map fieldValues, boolean isConsolidated);

    /**
     * This method gets the size of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the size of balance entries
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated);

    /**
     * This method finds the summary records of balance entries according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the summary records of balance entries
     */
    public Integer getBalanceRecordCount(Map fieldValues, boolean isConsolidated);

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chart, int year);

    /**
     * Get the GL Balance summary for the GL Summary report
     * 
     * @param universityFiscalYear
     * @param balanceTypeCodes
     * @return
     */
    public List getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes);

    /**
     * This method returns the total count of balances for a fiscal year
     * 
     * @param year fiscal year to check
     * @return the count of balances
     */
    public int countBalancesForFiscalYear(Integer year);

    /**
     * This method returns all of the balances specifically for the nominal activity closing job
     * 
     * @param year year to find balances for
     * @return an Iterator of nominal activity balances
     */
    public Iterator<Balance> findNominalActivityBalancesForFiscalYear(Integer year);

    /**
     * Returns all the balances specifically to be processed by the balance forwards job for the "general" rule
     * 
     * @param year
     * @return
     */
    public Iterator<Balance> findGeneralBalancesToForwardForFiscalYear(Integer year);

    /**
     * Returns all the balances to be forwarded for the "cumulative" rule
     * 
     * @param year
     * @return
     */
    public Iterator<Balance> findCumulativeBalancesToForwardForFiscalYear(Integer year);

    /**
     * Returns all of the balances to be forwarded for the organization reversion process
     * 
     * @param year the year of balances to find
     * @param endOfYear whether the organization reversion process is running end of year (before the fiscal year change over) or
     *        beginning of year (after the fiscal year change over)
     * @return an iterator of balances to put through the strenuous organization reversion process
     */
    public Iterator<Balance> findOrganizationReversionBalancesForFiscalYear(Integer year, boolean endOfYear);
}