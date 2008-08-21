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
package org.kuali.kfs.module.cab.document.service;

import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;


/**
 * This class declares methods used by CAB PurAp Line process
 */
public interface PurApLineService {

    /**
     * Build the purApDocList collection which have the same po_id and set the field values for each line items.
     * 
     * @param purApLineForm form
     */
    void setPurApInformation(PurApLineForm purApLineForm);

    /**
     * Changes percent quantities to a quantity of 1 for selected line item.
     * 
     * @param itemAsset Selected line item.
     */
    void processPercentPayment(PurchasingAccountsPayableItemAsset itemAsset);

    /**
     * Split the selected line item quantity and create a new line item.
     * 
     * @param itemAsset Selected line item.
     * @return
     */
    PurchasingAccountsPayableItemAsset processSplit(PurchasingAccountsPayableItemAsset itemAsset);
}
