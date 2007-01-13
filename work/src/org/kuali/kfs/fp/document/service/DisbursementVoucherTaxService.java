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
package org.kuali.module.financial.service;

import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.document.DisbursementVoucherDocument;

/**
 * Handles queries and validation on tax id numbers.
 * 
 * 
 */
public interface DisbursementVoucherTaxService {
    public static Integer TAX_NUMBER_NOT_FOUND = new Integer(0);
    public static Integer INSTITUTION_TAX_NUMBER = new Integer(1);
    public static Integer TAX_ID_VENDOR = new Integer(2);
    public static Integer TAX_ID_EXISTING_PAYEE = new Integer(3);
    public static Integer TAX_ID_PENDING_PAYEE = new Integer(4);
    public static Integer TAX_ID_EMPLOYEE = new Integer(5);

    /**
     * Checks the existence of a tax id number as one of the following, returning the constant representing the match: 1) No Match
     * 2) Institution special tax number 3) Vendor ID Number 4) Tax ID of existing payee 5) Tax ID of pending payee 6) Employee tax
     * number
     * 
     * @param taxIDNumber
     * @return
     */
    public Integer getTaxIDNumberUsage(String taxIDNumber, String taxpayerTypeCode);

    /**
     * Returns the vendor id number whose tax number matches the number passed in, or null if no vendor is found.
     * 
     * @param taxIDNumber
     * @param taxpayerTypeCode
     * @return
     */
    public String getVendorNumber(String taxIDNumber, String taxpayerTypeCode);

    /**
     * Returns the pending payee id number whose tax number matches the number passed in, or null if no payee is found.
     * 
     * @param taxIDNumber
     * @param taxpayerTypeCode
     * @return
     */
    public String getPendingPayeeNumber(String taxIDNumber, String taxpayerTypeCode);

    /**
     * Returns the payee id number whose tax number matches the number passed in, or null if no payee is found.
     * 
     * @param taxIDNumber
     * @param taxpayerTypeCode
     * @return
     */
    public String getPayeeNumber(String taxIDNumber, String taxpayerTypeCode);

    /**
     * Returns the employee id number whose tax number matches the number passed in, or null if no employee is found.
     * 
     * @param taxIDNumber
     * @param taxpayerTypeCode
     * @return
     */
    public String getEmployeeNumber(String taxIDNumber, String taxpayerTypeCode);

    /**
     * Removes tax lines from the document's accounting lines and updates the check total.
     * 
     * @param document
     */
    public void clearNRATaxLines(DisbursementVoucherDocument document);

    /**
     * generates new tax lines based on nra tab infor, and debits the check total
     * 
     * @param document
     */
    public void processNonResidentAlienTax(DisbursementVoucherDocument document);

    /**
     * Returns the accounting line tax amount (if any)
     * 
     * @param document
     * @return
     */
    public KualiDecimal getNonResidentAlienTaxAmount(DisbursementVoucherDocument document);

    /**
     * Returns a List of Integer line numbers parsed from the line string.
     * 
     * @param taxLineString
     * @return
     */
    public List getNRATaxLineNumbers(String taxLineString);
}