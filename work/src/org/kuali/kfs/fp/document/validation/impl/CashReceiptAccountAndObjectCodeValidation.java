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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_ACCT_OBJ_CD;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation that checks the sales tax account/object code combination on accounting lines of the cash receipt
 */
public class CashReceiptAccountAndObjectCodeValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    private ParameterService parameterService;

    /**
     * This method processes the accounting line to make sure if a sales tax account is used the right object code is used with it
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;
        // not evaluating, just want to retrieve the evaluator to get the values of the parameter
        // get the object code and account
        String objCd = getAccountingLineForValidation().getFinancialObjectCode();
        String account = getAccountingLineForValidation().getAccountNumber();
        if (!StringUtils.isEmpty(objCd) && !StringUtils.isEmpty(account)) {
            String[] params = getParameterService().getParameterValuesAsString(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES).toArray(new String[] {});
            boolean acctsMatched = false;
            for (int i = 0; i < params.length; i++) {
                String paramAcct = params[i].split(":")[0];
                if (account.equalsIgnoreCase(paramAcct)) {
                    acctsMatched = true;
                }
            }
            if (acctsMatched) {
                String compare = account + ":" + objCd;
                ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES, compare);
                if (!evaluator.evaluationSucceeds()) {
                    isValid = false;
                }
            }

        }
        if (!isValid) {
            GlobalVariables.getMessageMap().putError("accountNumber", ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_ACCT_OBJ_CD, account, objCd);
        }
        return isValid;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
