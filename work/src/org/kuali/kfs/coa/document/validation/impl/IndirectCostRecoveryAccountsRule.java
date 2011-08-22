/*
 * Copyright 2005 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.document.validation.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDescription;
import org.kuali.kfs.coa.businessobject.AccountGuideline;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 */
abstract public class IndirectCostRecoveryAccountsRule extends KfsMaintenanceDocumentRuleBase {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostRecoveryAccountsRule.class);

    protected static final BigDecimal BD100 = new BigDecimal(100);
    private List<? extends IndirectCostRecoveryAccount> indirectCostRecoveryAccountList;

    /**
     * Custom processing for adding collection lines
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;

        // this incoming bo needs to be refreshed because it doesn't have its subobjects setup
        bo.refreshNonUpdateableReferences();

        if (bo instanceof IndirectCostRecoveryAccount) {
            IndirectCostRecoveryAccount account = (IndirectCostRecoveryAccount) bo;
            success &= checkIndirectCostRecoveryAccount(account);
        }
        return success;
    }

    /**
     * This method calls the rule: KFSPropertyConstants.INDIRECT_COST_RECOVERY_ACCOUNT
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        return success = checkIndirectCostRecoveryAccountDistributions();
    }
    
    /**
     * This method checks if the ICR collection should or should not be filled
     * error message is not handled in the function 
     * 
     * @param expectFilled
     * @return
     */
    protected boolean checkICRCollectionExist(boolean expectFilled) {
        boolean success = true;
        
        success = expectFilled != indirectCostRecoveryAccountList.isEmpty();
        
        //double check each of the account/coa codes are not blank
        if (!success && expectFilled){
            for (IndirectCostRecoveryAccount account : indirectCostRecoveryAccountList){
                success &= StringUtils.isNotBlank(account.getIndirectCostRecoveryAccountNumber())
                    && StringUtils.isNotBlank(account.getIndirectCostRecoveryFinCoaCode());
            }
        }
        
        return success;
    }
    

    /**
     * This method checks if the ICR collection should or should not be filled
     * add error message if validation is not successful
     * 
     * @param expectFilled
     * @param errorMessage
     * @param args
     * @return
     */
    protected boolean checkICRCollectionExistWithErrorMessage(boolean expectFilled, String errorMessage, String...args) {
        boolean success = true;
        success = checkICRCollectionExist(expectFilled);
        if (!success){
            putFieldError(KFSPropertyConstants.INDIRECT_COST_RECOVERY_ACCOUNTS, errorMessage, args);
        }
        return success;
    }
    
    /**
     * Check valid IndirectCostRecovery Account
     * 
     * @return
     */
    private boolean checkIndirectCostRecoveryAccount(IndirectCostRecoveryAccount icrAccount) {
        
        boolean success = true;
        
        //check for empty values on the ICR account
        
        // The chart and account  must exist in the database.
        String chartOfAccountsCode = icrAccount.getIndirectCostRecoveryFinCoaCode();
        String accountNumber = icrAccount.getIndirectCostRecoveryAccountNumber();
        

        if (StringUtils.isBlank(chartOfAccountsCode)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ICR_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_REQUIRED, 
                    getDDAttributeLabel(KFSPropertyConstants.ICR_CHART_OF_ACCOUNTS_CODE));
            success &= false;
        }
        
        if (StringUtils.isBlank(accountNumber)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ICR_ACCOUNT_NUMBER, KFSKeyConstants.ERROR_REQUIRED,
                    getDDAttributeLabel(KFSPropertyConstants.ICR_ACCOUNT_NUMBER));
            success &= false;
        }
        
        if (StringUtils.isNotBlank(chartOfAccountsCode) && StringUtils.isNotBlank(accountNumber)) {
            Map<String, String> chartAccountMap = new HashMap<String, String>();
            chartAccountMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Chart.class, chartAccountMap) < 1) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ICR_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_EXISTENCE, chartOfAccountsCode);
                success &= false;
            }
            chartAccountMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Account.class, chartAccountMap) < 1) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ICR_ACCOUNT_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, chartOfAccountsCode + "-" + accountNumber);
                success &= false;
            }
        }
        
        BigDecimal icraAccountLinePercentage = ObjectUtils.isNotNull(icrAccount.getAccountLinePercent()) ? icrAccount.getAccountLinePercent() : BigDecimal.ZERO;
        
        //check the percent line
        if (icraAccountLinePercentage.compareTo(BigDecimal.ZERO) <= 0 || icraAccountLinePercentage.compareTo(BD100) == 1){
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ICR_ACCOUNT_LINE_PERCENT, 
                    KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_INVALID_LINE_PERCENT);
            success &= false;
        }
        
        return success;
    }

    /**
     * Check the collection list of indirect cost recovery account
     * 
     * 1. Check each account with rule: checkIndirectCostRecoveryAccount
     * 2. Total distributions from all the account should be 100
     * 
     * @param document
     * @return
     */
    private boolean checkIndirectCostRecoveryAccountDistributions() {
        
        boolean result = true;
        if (ObjectUtils.isNull(indirectCostRecoveryAccountList)) {
            return result;
        }
        
        DictionaryValidationService dvService = super.getDictionaryValidationService();
        
        int i=0;
        BigDecimal totalDistribution = BigDecimal.ZERO;
        
        for (IndirectCostRecoveryAccount icra : indirectCostRecoveryAccountList){
            String errorPath = MAINTAINABLE_ERROR_PREFIX + KFSPropertyConstants.INDIRECT_COST_RECOVERY_ACCOUNTS + "[" + i++ + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
            checkIndirectCostRecoveryAccount(icra);
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            
            totalDistribution = totalDistribution.add(icra.getAccountLinePercent());
        }
        
        //check the total distribution is 100
        if (totalDistribution.compareTo(BD100) != 0){
            putFieldError(KFSPropertyConstants.INDIRECT_COST_RECOVERY_ACCOUNTS, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_TOTAL_NOT_100_PERCENT);
            result &= false;
        }
        
        return result;
    }
    
    /**
     * Get the attribute label from DataDictionary
     * @param attribute
     * @return
     */
    private String getDDAttributeLabel(String attribute){
        return ddService.getAttributeLabel(IndirectCostRecoveryAccount.class, attribute);
    }
    
    public List<? extends IndirectCostRecoveryAccount> getIndirectCostRecoveryAccountList() {
        return indirectCostRecoveryAccountList;
    }

    public void setIndirectCostRecoveryAccountList(List<? extends IndirectCostRecoveryAccount> indirectCostRecoveryAccountList) {
        this.indirectCostRecoveryAccountList = indirectCostRecoveryAccountList;
    }

}

