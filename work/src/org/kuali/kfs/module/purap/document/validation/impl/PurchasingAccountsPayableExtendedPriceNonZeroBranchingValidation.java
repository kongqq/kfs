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

import org.kuali.kfs.integration.purap.PurApItem;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PurchasingAccountsPayableExtendedPriceNonZeroBranchingValidation extends BranchingValidation{

    private static final String IS_EXTENDED_PRICE_NON_ZERO = "isExtendedPriceNonZero";
    private PurApItem item;
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (item.getExtendedPrice() != null && item.getExtendedPrice().isNonZero()) {
            return IS_EXTENDED_PRICE_NON_ZERO;
        } else {
            return null;
        }
    }

}
