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
package org.kuali.module.purap.bo;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.bo.SourceAccountingLine;

public abstract class PurApAccountingLineBase extends SourceAccountingLine implements PurApAccountingLine {

    protected Integer accountIdentifier;
    private Integer itemIdentifier;
    private BigDecimal accountLinePercent;

    public Integer getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(Integer requisitionAccountIdentifier) {
        this.accountIdentifier = requisitionAccountIdentifier;
    }

    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(Integer requisitionItemIdentifier) {
        this.itemIdentifier = requisitionItemIdentifier;
    }

    public BigDecimal getAccountLinePercent() {
        return accountLinePercent;
    }

    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }

    public boolean isEmpty() {
        return !(StringUtils.isNotEmpty(getAccountNumber()) || 
                 StringUtils.isNotEmpty(getChartOfAccountsCode()) || 
                 StringUtils.isNotEmpty(getFinancialObjectCode()) || 
                 StringUtils.isNotEmpty(getFinancialSubObjectCode()) || 
                 StringUtils.isNotEmpty(getOrganizationReferenceId()) || 
                 StringUtils.isNotEmpty(getProjectCode()) || 
                 StringUtils.isNotEmpty(getSubAccountNumber()));
    }
}
