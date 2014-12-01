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
package org.kuali.kfs.module.ar.document.service;

import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.AppliedPayment;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

public interface InvoicePaidAppliedService<T extends AppliedPayment> {
    
    public Integer getNumberOfInvoicePaidAppliedsForInvoiceDetail(String financialDocumentReferenceInvoiceNumber, Integer invoiceItemNumber);
    
    /**
     * 
     * Clears all the PaidApplieds for the given document number from the database.
     * This is typically done prior to loading a new set of lines in.
     * 
     * NOTE that this will delete the Paid Applieds that were generated BY the 
     * documentNumber passed in, not that apply TO the documentNumber passed in.
     * 
     * @param documentNumber
     */
    public void clearDocumentPaidAppliedsFromDatabase(String documentNumber);
    
    /**
     * This method doesn't go to the database to get related invoice paid applieds.
     * It looks at a specific document to get the relations worked out.
     * 
     * @param customerInvoiceDetail
     * @param paymentApplicationDocument
     * @return
     */
//    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, PaymentApplicationDocument paymentApplicationDocument);
    
    /**
     * This method returns true if invoice has applied amounts (i.e. from application, credit memo, etc), not including
     * discounts
     * 
     * @param document
     * @return
     */
    public boolean doesInvoiceHaveAppliedAmounts(CustomerInvoiceDocument document);

    /**
     * @param documentNumber
     * @return
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(String documentNumber);

    /**
     * @param documentNumber
     * @return
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(CustomerInvoiceDocument invoice);
    
    /**
     * @param customerInvoiceDetail
     * @return
     */
//    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail);
//    public Collection<InvoicePaidApplied> getApprovedInvoicePaidAppliedsForCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail);
//    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, String applicationDocNumber);
//    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsFromSpecificDocument(String documentNumber, String referenceCustomerInvoiceDocumentNumber);
    
}
