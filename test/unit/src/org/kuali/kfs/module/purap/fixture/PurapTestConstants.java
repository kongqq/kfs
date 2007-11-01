/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.fixtures;

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.RequisitionAccount;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.RequisitionDocument;

public class PurapTestConstants {

    static final Integer FY_2007 = new Integer("2007");

    @SuppressWarnings("deprecation")
    static class BeginEndDates {
        static final Date FIRST_DATE = new Date(108, 1, 1); // 2008-01-01
        static final Date LAST_DATE = new Date(109, 1, 1); // 2009-01-01
        static RequisitionDocument REQ = new RequisitionDocument();
        static PurchaseOrderDocument PO = new PurchaseOrderDocument();
    }

    static class RecurringPaymentTypes {
        static final String FIXD = "FIXD";
        static final String FVAR = "FVAR";
        static final String VARV = "VARV";
    }

    static class AmountsLimits {
        static final KualiDecimal LARGE_NEGATIVE_AMOUNT = new KualiDecimal(-1024);
        static final KualiDecimal SMALL_NEGATIVE_AMOUNT = new KualiDecimal(-32);
        static final KualiDecimal ZERO = new KualiDecimal(0);
        static final KualiDecimal SMALL_POSITIVE_AMOUNT = new KualiDecimal(32);
        static final KualiDecimal LARGE_POSITIVE_AMOUNT = new KualiDecimal(1024);
    }

    static class ItemsAccounts {
        static RequisitionItem REQ_ITEM = new RequisitionItem();
        static PurchaseOrderItem PO_ITEM = new PurchaseOrderItem();
        static final String ITEM_DESC = "Test item description";
        static final String ITEM_UOM = "EACH";
        static PurchaseOrderAccount PO_ACCOUNT = new PurchaseOrderAccount();
        static RequisitionAccount REQ_ACCOUNT = new RequisitionAccount();
        static final String CHART_CODE = "BL";
        static final String OBJECT_CODE = "5000";
        static final String ACCOUNT_NUMBER = "1031400";
        static final BigDecimal UNIT_PRICE = new KualiDecimal(32).bigDecimalValue();
        static final BigDecimal PERCENTAGE = new KualiDecimal(100).bigDecimalValue();

        static final BigDecimal UNIT_PRICE_APO_1 = new KualiDecimal(1.99).bigDecimalValue();
        static final BigDecimal UNIT_PRICE_APO_2 = new KualiDecimal(239.99).bigDecimalValue();
        static final BigDecimal QUANTITY_APO_1 = new KualiDecimal(500).bigDecimalValue();
        static final BigDecimal QUANTITY_APO_2 = new KualiDecimal(1).bigDecimalValue();
    }

    @SuppressWarnings("deprecation")
    static class PO {
        static final Integer REQ_ID = new Integer("8888");
        static final Date CREATE_DATE = new Date(107, 10, 10); // 2008-01-01
        static final KualiDecimal AMOUNT = new KualiDecimal(32);
    }

    @SuppressWarnings("deprecation")
    static class PREQInvoice {
        static final Integer PO_ID = new Integer("9999");
        static final Date INVOICE_DATE = new Date(108, 1, 1); // 2008-01-01
        static final String INVOICE_NUMBER = "123456789";
        static final KualiDecimal AMOUNT = new KualiDecimal(32);
    }

}
