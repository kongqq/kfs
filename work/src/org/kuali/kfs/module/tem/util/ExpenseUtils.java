/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

public class ExpenseUtils {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExpenseUtils.class);

    public static List<ImportedExpense> convertHistoricalToImportedExpense(List<HistoricalTravelExpense> historicalTravelExpenses, TravelDocument travelDocument){
        List<ImportedExpense> expenses = new ArrayList<ImportedExpense>();
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        for (HistoricalTravelExpense historicalTravelExpense : historicalTravelExpenses){
            int i = 0;
            historicalTravelExpense.refreshReferenceObject(TemPropertyConstants.CREDIT_CARD_AGENCY);
            historicalTravelExpense.refreshReferenceObject(TemPropertyConstants.AGENCY_STAGING_DATA);
            historicalTravelExpense.refreshReferenceObject(TemPropertyConstants.CREDIT_CARD_STAGING_DATA);
            historicalTravelExpense.getCreditCardAgency().refreshReferenceObject(TemPropertyConstants.TRAVEL_CARD_TYPE);

            historicalTravelExpense.setDocumentNumber(travelDocument.getDocumentNumber());

            ImportedExpense importedExpense = new ImportedExpense();
            importedExpense.setCardType(historicalTravelExpense.getCreditCardAgency().getTravelCardTypeCode());

            importedExpense.setNonReimbursable(!historicalTravelExpense.getReimbursable());
            importedExpense.setMissingReceipt(historicalTravelExpense.getMissingReceipt());
            importedExpense.setExpenseDate(historicalTravelExpense.getTransactionPostingDate());
            importedExpense.setCurrencyRate(historicalTravelExpense.getCurrencyRate());
            importedExpense.setConvertedAmount(historicalTravelExpense.getConvertedAmount());
            importedExpense.setExpenseAmount(historicalTravelExpense.getAmount());
            importedExpense.setTravelCompanyCodeName(historicalTravelExpense.getTravelCompany());

            ExpenseTypeObjectCode travelExpenseTypeCode = SpringContext.getBean(TravelExpenseService.class).getExpenseType(historicalTravelExpense.getTravelExpenseType(), travelDocument.getDocumentTypeName(), travelDocument.getTripTypeCode(), travelDocument.getTraveler().getTravelerTypeCode());

            if (travelExpenseTypeCode != null) {
                historicalTravelExpense.setDescription(travelExpenseTypeCode.getExpenseType().getName());
                importedExpense.setDescription(historicalTravelExpense.getDescription());
                importedExpense.setTravelExpenseTypeCode(travelExpenseTypeCode);
                importedExpense.setExpenseTypeObjectCodeId(travelExpenseTypeCode.getExpenseTypeObjectCodeId());
                importedExpense.setExpenseTypeCode(historicalTravelExpense.getTravelExpenseType());
            }

            importedExpense.setHistoricalTravelExpenseId(historicalTravelExpense.getId());

            expenses.add(importedExpense);
            historicalTravelExpense.setAssigned(true);
            historicalTravelExpense.setDocumentType(travelDocument.getFinancialDocumentTypeCode());
        }
        service.save(historicalTravelExpenses);
        return expenses;
    }

    public static String getDefaultChartCode(TravelDocument document){
        String defaultChartCode = null;
        if (document.getTemProfile() == null && document.getTemProfileId() != null) {
            Map<String, Object> primaryKeys = new HashMap<String, Object>();
            primaryKeys.put(TEMProfileProperties.PROFILE_ID, document.getTemProfileId().toString());
            TEMProfile profile = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(TEMProfile.class, primaryKeys);
            defaultChartCode = profile.getDefaultChartCode();
        }
        else if (document.getTemProfile() != null) {
            defaultChartCode = document.getTemProfile().getDefaultChartCode();
        }

        return defaultChartCode;
    }

    public static void assignExpense(Long historicalTravelExpenseId, String tripId, String documentNumber, String documentType, boolean isAssigned){
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        HistoricalTravelExpense historicalTravelExpense = service.findBySinglePrimaryKey(HistoricalTravelExpense.class, historicalTravelExpenseId);
        historicalTravelExpense.setAssigned(isAssigned);
        historicalTravelExpense.setTripId(tripId);
        historicalTravelExpense.setDocumentNumber(documentNumber);
        historicalTravelExpense.setDocumentType(documentType);
        service.save(historicalTravelExpense);
    }

    public static void calculateMileage(List<ActualExpense> actualExpenses){
        for (ActualExpense actualExpense : actualExpenses){
            if (!StringUtils.isBlank(actualExpense.getExpenseTypeCode()) && actualExpense.isMileage()){
                actualExpense.setCurrencyRate(new KualiDecimal(1));
                KualiDecimal total = KualiDecimal.ZERO;
                for (TEMExpense detail : actualExpense.getExpenseDetails()){
                    ActualExpense detailExpense = (ActualExpense) detail;
                    if (detailExpense.getMileageRate() != null) {
                        KualiDecimal mileage = (new KualiDecimal(detailExpense.getMiles())).multiply(detailExpense.getMileageRate().getRate());
                        detailExpense.setExpenseAmount(mileage);
                        detailExpense.setConvertedAmount(mileage);
                        total = total.add(detailExpense.getExpenseAmount());
                        detailExpense.setCurrencyRate(actualExpense.getCurrencyRate());
                        detailExpense.setExpenseTypeObjectCodeId(actualExpense.getExpenseTypeObjectCodeId());
                    }
                }
                actualExpense.setExpenseAmount(total);
            }
        }
    }

}