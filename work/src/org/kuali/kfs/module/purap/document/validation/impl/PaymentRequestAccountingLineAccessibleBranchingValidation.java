/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PaymentRequestAccountingLineAccessibleBranchingValidation extends BranchingValidation {

    private static final String USE_DEFAULT_ACCOUNTING_LINE_ACCESSIBLE="useDefaultAccountingLineAccessible";
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (StringUtils.equals(PurapConstants.PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW, ((PaymentRequestDocument)event.getDocument()).getStatusCode())) {
            return null;
        }else{
            return USE_DEFAULT_ACCOUNTING_LINE_ACCESSIBLE; 
        }
    }

}
