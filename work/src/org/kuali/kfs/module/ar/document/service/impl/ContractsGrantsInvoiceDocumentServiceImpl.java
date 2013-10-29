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
package org.kuali.kfs.module.ar.document.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ObjectLevelService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingFrequency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService;
import org.kuali.kfs.integration.cg.ContractsGrantsAwardInvoiceAccountInformation;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsCategories;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAgencyAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.businessobject.ReferralType;
import org.kuali.kfs.module.ar.businessobject.SuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.businessobject.lookup.DunningLetterDistributionOnDemandLookupUtil;
import org.kuali.kfs.module.ar.businessobject.lookup.ReferralToCollectionsDocumentUtil;
import org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao;
import org.kuali.kfs.module.ar.dataaccess.BillDao;
import org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.dataaccess.MilestoneDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.PdfFormFillerUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.AttachmentService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class implements the services required for Contracts and Grants Invoice Document.
 */
public class ContractsGrantsInvoiceDocumentServiceImpl extends CustomerInvoiceDocumentServiceImpl implements ContractsGrantsInvoiceDocumentService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceDocumentServiceImpl.class);
    private CustomerInvoiceDetailService customerInvoiceDetailService;
    private ContractsGrantsInvoiceDocumentDao contractsGrantsInvoiceDocumentDao;
    private AccountingPeriodService accountingPeriodService;
    private AwardAccountObjectCodeTotalBilledDao awardAccountObjectCodeTotalBilledDao;
    private VerifyBillingFrequencyService verifyBillingFrequencyService;
    private DateTimeService dateTimeService;
    private InvoicePaidAppliedService invoicePaidAppliedService;
    private MilestoneDao milestoneDao;
    private BillDao billDao;
    public static final String REPORT_LINE_DIVIDER = "--------------------------------------------------------------------------------------------------------------";
    private static final SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("MM-dd-yyyy");

    public void setVerifyBillingFrequencyService(VerifyBillingFrequencyService verifyBillingFrequencyService) {
        this.verifyBillingFrequencyService = verifyBillingFrequencyService;
    }

    public InvoicePaidAppliedService getInvoicePaidAppliedService() {
        return invoicePaidAppliedService;
    }

    @Override
    public void setInvoicePaidAppliedService(InvoicePaidAppliedService invoicePaidAppliedService) {
        this.invoicePaidAppliedService = invoicePaidAppliedService;
    }

    /**
     * Gets the customerInvoiceDetailService attribute.
     * 
     * @return Returns the customerInvoiceDetailService.
     */
    public CustomerInvoiceDetailService getCustomerInvoiceDetailService() {
        return customerInvoiceDetailService;
    }

    /**
     * Sets the customerInvoiceDetailService attribute value.
     * 
     * @param customerInvoiceDetailService The customerInvoiceDetailService to set.
     */
    @Override
    public void setCustomerInvoiceDetailService(CustomerInvoiceDetailService customerInvoiceDetailService) {
        this.customerInvoiceDetailService = customerInvoiceDetailService;
    }

    /**
     * @return
     */
    public ContractsGrantsInvoiceDocumentDao getContractsGrantsInvoiceDocumentDao() {
        return contractsGrantsInvoiceDocumentDao;
    }

    /**
     * @param contractsGrantsInvoiceDocumentDao
     */
    public void setContractsGrantsInvoiceDocumentDao(ContractsGrantsInvoiceDocumentDao contractsGrantsInvoiceDocumentDao) {
        this.contractsGrantsInvoiceDocumentDao = contractsGrantsInvoiceDocumentDao;
    }

    /**
     * @return
     */
    public AwardAccountObjectCodeTotalBilledDao getAwardAccountObjectCodeTotalBilledDao() {
        return awardAccountObjectCodeTotalBilledDao;
    }

    /**
     * @param awardAccountObjectCodeTotalBilledDao
     */
    public void setAwardAccountObjectCodeTotalBilledDao(AwardAccountObjectCodeTotalBilledDao awardAccountObjectCodeTotalBilledDao) {
        this.awardAccountObjectCodeTotalBilledDao = awardAccountObjectCodeTotalBilledDao;
    }

    /**
     * Gets the milestoneDao attribute.
     * 
     * @return Returns the milestoneDao.
     */
    public MilestoneDao getMilestoneDao() {
        return milestoneDao;
    }

    /**
     * Sets the milestoneDao attribute value.
     * 
     * @param milestoneDao The milestoneDao to set.
     */
    public void setMilestoneDao(MilestoneDao milestoneDao) {
        this.milestoneDao = milestoneDao;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#createSourceAccountingLinesAndGLPEs(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public void createSourceAccountingLinesAndGLPEs(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) throws WorkflowException {
        List<ContractsGrantsAwardInvoiceAccountInformation> awardInvoiceAccounts = new ArrayList<ContractsGrantsAwardInvoiceAccountInformation>();
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getAward())) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, contractsGrantsInvoiceDocument.getAward().getProposalNumber());
            map.put(KFSPropertyConstants.ACTIVE, true);
            awardInvoiceAccounts = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsGrantsAwardInvoiceAccountInformation.class).getExternalizableBusinessObjectsList(ContractsGrantsAwardInvoiceAccountInformation.class, map);
        }
        boolean awardBillByControlAccountInd = false;
        boolean awardBillByInvoicingAccountInd = false;
        List<String> invoiceAccountDetails = new ArrayList<String>();
        boolean invoiceWithControlAccountInd = false;

        // To check if the Source accounting lines are existing. If they are do nothing
        if (CollectionUtils.isEmpty(contractsGrantsInvoiceDocument.getSourceAccountingLines())) {
            // To check if the invoice account section in award has a income account set.

            String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
            boolean isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
            if (isUsingReceivableFAU) {
                if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getAward()) && CollectionUtils.isNotEmpty(awardInvoiceAccounts)) {
                    for (ContractsGrantsAwardInvoiceAccountInformation awardInvoiceAccount : awardInvoiceAccounts) {
                        if (awardInvoiceAccount.getAccountType().equals(ArPropertyConstants.INCOME_ACCOUNT)) {
                            if (awardInvoiceAccount.isActive()) {// Consider the active invoice account only.
                                awardBillByInvoicingAccountInd = true;
                                invoiceAccountDetails.add(awardInvoiceAccount.getChartOfAccountsCode());
                                invoiceAccountDetails.add(awardInvoiceAccount.getAccountNumber());
                                invoiceAccountDetails.add(awardInvoiceAccount.getObjectCode());
                            }
                        }
                    }
                }
            }

            // To check if award is set to bill by Contract Control Account.

            if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getAward()) && contractsGrantsInvoiceDocument.getAward().getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT)) {
                awardBillByControlAccountInd = true;
            }
            else {
                awardBillByControlAccountInd = false;
            }


            KualiDecimal totalMilestoneAmount = KualiDecimal.ZERO;
            // To calculate the total milestone amount.
            if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && contractsGrantsInvoiceDocument.getInvoiceMilestones().size() > 0) {
                for (InvoiceMilestone milestone : contractsGrantsInvoiceDocument.getInvoiceMilestones()) {
                    if (milestone.getMilestoneAmount() != null) {
                        totalMilestoneAmount = totalMilestoneAmount.add(milestone.getMilestoneAmount());
                    }
                }
            }
            KualiDecimal totalBillAmount = KualiDecimal.ZERO;
            // To calculate the total bill amount.
            if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && contractsGrantsInvoiceDocument.getInvoiceBills().size() > 0) {
                for (InvoiceBill bill : contractsGrantsInvoiceDocument.getInvoiceBills()) {
                    if (bill.getEstimatedAmount() != null) {
                        totalBillAmount = totalBillAmount.add(bill.getEstimatedAmount());
                    }
                }
            }

            // To retrieve the financial object code from the Organization Accounting Default.
            Map<String, Object> criteria = new HashMap<String, Object>();
            Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
            criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
            criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, contractsGrantsInvoiceDocument.getBillByChartOfAccountCode());
            criteria.put(ArPropertyConstants.CustomerInvoiceItemCodes.ORGANIZATION_CODE, contractsGrantsInvoiceDocument.getBilledByOrganizationCode());
            // Need to avoid hitting database in the loop. option would be to set the financial object code when the form loads and
            // save
            // it somewhere.
            OrganizationAccountingDefault organizationAccountingDefault = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
            if (ObjectUtils.isNotNull(organizationAccountingDefault)) {
                if (awardBillByInvoicingAccountInd) {
                    // If its bill by Invoicing Account , irrespective of it is by contract control account, there would be a single
                    // source accounting line with award invoice account specified by the user.
                    try {
                        CustomerInvoiceDetail cide = createSourceAccountingLine(contractsGrantsInvoiceDocument.getDocumentNumber(), invoiceAccountDetails.get(0), invoiceAccountDetails.get(1), invoiceAccountDetails.get(2), contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled(), Integer.parseInt("1"));
                        contractsGrantsInvoiceDocument.getSourceAccountingLines().add(cide);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
                else {
                    // If its bill by Contract Control Account there would be a single source accounting line.
                    if (awardBillByControlAccountInd) {

                        // To get the account number and coa code for contract control account.
                        String accountNumber = null;
                        // Use the first account to get the contract control account number, as every account would have the same
                        // contract control account number.
                        if (StringUtils.isNotEmpty(contractsGrantsInvoiceDocument.getAccountDetails().get(0).getContractControlAccountNumber())) {
                            accountNumber = contractsGrantsInvoiceDocument.getAccountDetails().get(0).getContractControlAccountNumber();
                        }

                        String coaCode = contractsGrantsInvoiceDocument.getBillByChartOfAccountCode();
                        String objectCode = organizationAccountingDefault.getDefaultInvoiceFinancialObjectCode();

                        try {
                            CustomerInvoiceDetail cide = createSourceAccountingLine(contractsGrantsInvoiceDocument.getDocumentNumber(), coaCode, accountNumber, objectCode, contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled(), Integer.parseInt("1"));
                            contractsGrantsInvoiceDocument.getSourceAccountingLines().add(cide);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        for (InvoiceAccountDetail invAcctD : contractsGrantsInvoiceDocument.getAccountDetails()) {
                            String accountNumber = invAcctD.getAccountNumber();
                            String coaCode = invAcctD.getChartOfAccountsCode();
                            String objectCode = organizationAccountingDefault.getDefaultInvoiceFinancialObjectCode();
                            Integer sequenceNumber = contractsGrantsInvoiceDocument.getAccountDetails().indexOf(invAcctD) + 1;// To
                                                                                                                              // set
                                                                                                                              // a
                                                                                                                              // sequence
                                                                                                                              // number
                                                                                                                              // for
                                                                                                                              // the
                                                                                                                              // Accounting
                                                                                                                              // Lines
                            try {
                                // To calculate totalAmount based on the billing Frequency. Assuming that there would be only one
                                // account if its Milestone/Predetermined Schedule.
                                KualiDecimal totalAmount = KualiDecimal.ZERO;
                                if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && totalMilestoneAmount != KualiDecimal.ZERO) {
                                    totalAmount = totalMilestoneAmount;
                                }
                                else if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && totalBillAmount != KualiDecimal.ZERO) {
                                    totalAmount = totalBillAmount;
                                }
                                else {
                                    totalAmount = invAcctD.getExpenditureAmount();
                                }


                                CustomerInvoiceDetail cide = createSourceAccountingLine(contractsGrantsInvoiceDocument.getDocumentNumber(), coaCode, accountNumber, objectCode, totalAmount, sequenceNumber);

                                contractsGrantsInvoiceDocument.getSourceAccountingLines().add(cide);

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }

                        }
                    }
                }
            }
        }

    }

    /**
     * @param docNum
     * @param coaCode
     * @param acctNum
     * @param obCode
     * @param totalAmount
     * @param seqNum
     * @return
     * @throws Exception
     */
    public CustomerInvoiceDetail createSourceAccountingLine(String docNum, String coaCode, String acctNum, String obCode, KualiDecimal totalAmount, Integer seqNum) throws Exception {
        CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
        cid.setDocumentNumber(docNum);

        cid.setAccountNumber(acctNum);
        cid.setChartOfAccountsCode(coaCode);
        cid.setFinancialObjectCode(obCode);

        cid.setSequenceNumber(seqNum);
        cid.setInvoiceItemQuantity(BigDecimal.ONE);
        cid.setInvoiceItemUnitOfMeasureCode("EA");


        cid.setInvoiceItemUnitPrice(totalAmount);
        cid.setAmount(totalAmount);
        if (totalAmount.isNegative()) {
            cid.setInvoiceItemDiscountLineNumber(seqNum);
        }
        // To get AR Object codes for the GLPEs .... as it is not being called implicitly..

        cid.setAccountsReceivableObjectCode(customerInvoiceDetailService.getAccountsReceivableObjectCodeBasedOnReceivableParameter(cid));
        return cid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#recalculateNewTotalBilled(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public void recalculateNewTotalBilled(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {

        // To verify the expenditure amounts have been changed and
        // update the invoiceDetailObjectCode
        boolean expenditureValueChanged = adjustObjectCodeAmountsIfChanged(contractsGrantsInvoiceDocument);

        if (expenditureValueChanged) {
            // update Total Direct Cost in the Invoice Detail Tab
            KualiDecimal totalDirectCostExpenditures = getInvoiceDetailExpenditureSum(contractsGrantsInvoiceDocument.getInvoiceDetails());
            // Set expenditures to Direct Cost invoice Details
            contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(0).setExpenditures(totalDirectCostExpenditures);

            // update Total Indirect Cost in the Invoice Detail Tab
            KualiDecimal totalInDirectCostExpenditures = getInvoiceDetailExpenditureSum(contractsGrantsInvoiceDocument.getInvoiceDetailsIndirectCostOnly());
            // Set expenditures to Indirect Cost invoice Details
            contractsGrantsInvoiceDocument.getInDirectCostInvoiceDetails().get(0).setExpenditures(totalInDirectCostExpenditures);

            // Set the total for Total Cost Invoice Details section.
            contractsGrantsInvoiceDocument.getTotalInvoiceDetails().get(0).setExpenditures(contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(0).getExpenditures().add(totalInDirectCostExpenditures));

            recalculateAccountDetails(contractsGrantsInvoiceDocument.getAccountDetails(), contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes());

            // update source accounting lines
            updateInvoiceSourceAccountingLines(contractsGrantsInvoiceDocument.getAccountDetails(), contractsGrantsInvoiceDocument.getSourceAccountingLines());


        }

        // set the General Detail Total to be billed - there would be only one value for Total Cost invoice Details.
        contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().setNewTotalBilled(contractsGrantsInvoiceDocument.getTotalInvoiceDetails().get(0).getExpenditures().add(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBilledToDate()));
    }

    /**
     * @param invoiceDetails
     * @return
     */
    public KualiDecimal getInvoiceDetailExpenditureSum(List<InvoiceDetail> invoiceDetails) {
        KualiDecimal totalExpenditures = KualiDecimal.ZERO;
        for (InvoiceDetail invoiceDetail : invoiceDetails) {
            totalExpenditures = totalExpenditures.add(invoiceDetail.getExpenditures());
        }
        return totalExpenditures;
    }


    /**
     * @param invoiceAccountDetails
     * @param sourceAccountingLines
     */
    public void updateInvoiceSourceAccountingLines(List<InvoiceAccountDetail> invoiceAccountDetails, List sourceAccountingLines) {

        if (sourceAccountingLines.size() > 1) {// Invoice By Award
            for (CustomerInvoiceDetail cide : (List<CustomerInvoiceDetail>) sourceAccountingLines) {
                for (InvoiceAccountDetail invoiceAccountDetail : invoiceAccountDetails) {
                    if (cide.getAccountNumber().equals(invoiceAccountDetail.getAccountNumber())) {
                        cide.setInvoiceItemUnitPrice(invoiceAccountDetail.getExpenditureAmount());
                        cide.setAmount(invoiceAccountDetail.getExpenditureAmount());
                    }
                }
            }
        }
        // This would be a case where the invoice is generated by Contract Control Account or Invoice By Account.
        else if (sourceAccountingLines.size() == 1) {
            KualiDecimal totalExpenditureAmount = KualiDecimal.ZERO;
            if (invoiceAccountDetails.size() == 1) {// Invoice By Account
                // update source accounting lines
                CustomerInvoiceDetail cide = (CustomerInvoiceDetail) sourceAccountingLines.get(0);
                cide.setInvoiceItemUnitPrice(invoiceAccountDetails.get(0).getExpenditureAmount());
                cide.setAmount(invoiceAccountDetails.get(0).getExpenditureAmount());
            }
            else {// Invoice By Contract Control Account
                for (InvoiceAccountDetail invoiceAccountDetail : invoiceAccountDetails) {
                    totalExpenditureAmount = totalExpenditureAmount.add(invoiceAccountDetail.getExpenditureAmount());
                }
                // update source accounting lines
                CustomerInvoiceDetail cide = (CustomerInvoiceDetail) sourceAccountingLines.get(0);
                cide.setInvoiceItemUnitPrice(totalExpenditureAmount);
                cide.setAmount(totalExpenditureAmount);
            }
        }

    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#prorateBill(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public void prorateBill(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) throws WorkflowException {


        KualiDecimal totalCost = new KualiDecimal(0); // Amount to be billed on this invoice
        // must iterate through the invoice details because the user might have manually changed the value
        for (InvoiceDetail invD : contractsGrantsInvoiceDocument.getInvoiceDetailsWithIndirectCosts()) {
            totalCost = totalCost.add(invD.getExpenditures());
        }
        KualiDecimal billedTotalCost = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBilledToDate(); // Total Billed
                                                                                                                   // so far
        KualiDecimal accountAwardTotal = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getAwardTotal(); // AwardTotal

        if (accountAwardTotal.subtract(billedTotalCost).isGreaterEqual(new KualiDecimal(0))) {
            KualiDecimal amountEligibleForBilling = accountAwardTotal.subtract(billedTotalCost);
            // only recalculate if the current invoice is over what's billable.

            if (totalCost.isGreaterThan(amountEligibleForBilling)) {
                // use BigDecimal because percentage should not have only a scale of 2, we need more for accuracy
                BigDecimal percentage = amountEligibleForBilling.bigDecimalValue().divide(totalCost.bigDecimalValue(), 10, BigDecimal.ROUND_HALF_DOWN);
                KualiDecimal amountToBill = new KualiDecimal(0); // use to check if rounding has left a few cents off

                for (InvoiceDetail invD : contractsGrantsInvoiceDocument.getInvoiceDetailsWithIndirectCosts()) {
                    BigDecimal newValue = invD.getExpenditures().bigDecimalValue().multiply(percentage);
                    KualiDecimal newKualiDecimalValue = new KualiDecimal(newValue.setScale(2, BigDecimal.ROUND_DOWN));
                    invD.setExpenditures(newKualiDecimalValue);
                    amountToBill = amountToBill.add(newKualiDecimalValue);
                }
                // There will be some amount left, since we are rounding down. Display warning for user to manually
                // correct/distribute where they want to put the remainder
                if (!amountToBill.equals(amountEligibleForBilling)) {
                    KualiDecimal remaining = amountEligibleForBilling.subtract(amountToBill);
                    LOG.info("Amount Set for Billing does not match Total Amount Eligible For Billing.  There is " + remaining.toString() + " remaining for billing.");
                    if (remaining.isPositive()) {
                        GlobalVariables.getMessageMap().putWarning(ArConstants.PRORATE_WARNING, ArKeyConstants.ContractsGrantsInvoiceConstants.WARNING_PRORATE_VALUE_IS_LESS_THAN_ELIGIBLE_FOR_BILLING, amountToBill.toString(), remaining.toString());
                    }
                    else {
                        GlobalVariables.getMessageMap().putWarning(ArConstants.PRORATE_WARNING, ArKeyConstants.ContractsGrantsInvoiceConstants.WARNING_PRORATE_VALUE_IS_MORE_THAN_ELIGIBLE_FOR_BILLING, amountToBill.toString(), remaining.abs().toString());
                    }
                }
                recalculateNewTotalBilled(contractsGrantsInvoiceDocument);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#addToAccountObjectCodeBilledTotal(java.util.List)
     */
    @Override
    public void addToAccountObjectCodeBilledTotal(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
            totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, invoiceDetailAccountObjectCode.getProposalNumber());
            totalBilledKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, invoiceDetailAccountObjectCode.getChartOfAccountsCode());
            totalBilledKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, invoiceDetailAccountObjectCode.getAccountNumber());
            totalBilledKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, invoiceDetailAccountObjectCode.getFinancialObjectCode());

            List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilledList = (List<AwardAccountObjectCodeTotalBilled>) SpringContext.getBean(BusinessObjectService.class).findMatching(AwardAccountObjectCodeTotalBilled.class, totalBilledKeys);
            if (awardAccountObjectCodeTotalBilledList != null && !awardAccountObjectCodeTotalBilledList.isEmpty()) {
                AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled = awardAccountObjectCodeTotalBilledList.get(0);
                awardAccountObjectCodeTotalBilled.setTotalBilled(awardAccountObjectCodeTotalBilled.getTotalBilled().add(invoiceDetailAccountObjectCode.getCurrentExpenditures()));
                awardAccountObjectCodeTotalBilledDao.save(awardAccountObjectCodeTotalBilled);
            }
            else {
                AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled = new AwardAccountObjectCodeTotalBilled();
                awardAccountObjectCodeTotalBilled.setProposalNumber(invoiceDetailAccountObjectCode.getProposalNumber());
                awardAccountObjectCodeTotalBilled.setChartOfAccountsCode(invoiceDetailAccountObjectCode.getChartOfAccountsCode());
                awardAccountObjectCodeTotalBilled.setAccountNumber(invoiceDetailAccountObjectCode.getAccountNumber());
                awardAccountObjectCodeTotalBilled.setFinancialObjectCode(invoiceDetailAccountObjectCode.getFinancialObjectCode());
                awardAccountObjectCodeTotalBilled.setTotalBilled(invoiceDetailAccountObjectCode.getCurrentExpenditures());
                awardAccountObjectCodeTotalBilledDao.save(awardAccountObjectCodeTotalBilled);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(java.util.List)
     */
    @Override
    public List<AwardAccountObjectCodeTotalBilled> getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(List<ContractsAndGrantsBillingAwardAccount> awardAccounts) {
        return awardAccountObjectCodeTotalBilledDao.getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#adjustObjectCodeAmountsIfChanged(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public boolean adjustObjectCodeAmountsIfChanged(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {

        boolean isExpenditureValueChanged = false;

        // put the invoiceDetailAccountObjectCode into a map based on category
        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes();
        Map<String, List<InvoiceDetailAccountObjectCode>> invoiceDetailAccountObjectCodeMap = new HashMap<String, List<InvoiceDetailAccountObjectCode>>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            String categoryCode = invoiceDetailAccountObjectCode.getCategoryCode();
            List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodeList = invoiceDetailAccountObjectCodeMap.get(categoryCode);
            // if new category, create new list to put into map
            if (invoiceDetailAccountObjectCodeList == null) {
                List<InvoiceDetailAccountObjectCode> newInvoiceDetailAccountObjectCodeList = new ArrayList<InvoiceDetailAccountObjectCode>();
                newInvoiceDetailAccountObjectCodeList.add(invoiceDetailAccountObjectCode);
                invoiceDetailAccountObjectCodeMap.put(categoryCode, newInvoiceDetailAccountObjectCodeList);
            }
            // else, if list is found, add it to existing list
            else {
                invoiceDetailAccountObjectCodeMap.get(categoryCode).add(invoiceDetailAccountObjectCode);
            }
        }

        // figure out if any of the current expenditures for the category has been changed. If yes, then update the
        // invoiceDetailObjectCode
        // and update account details
        for (InvoiceDetail invoiceDetail : contractsGrantsInvoiceDocument.getInvoiceDetailsWithIndirectCosts()) {
            KualiDecimal total = getSumOfExpendituresOfCategory(invoiceDetailAccountObjectCodeMap.get(invoiceDetail.getCategoryCode()));
            // To set expenditures to zero if its blank - to avoid exceptions.
            if (ObjectUtils.isNull(invoiceDetail.getExpenditures())) {
                invoiceDetail.setExpenditures(KualiDecimal.ZERO);
            }

            if (invoiceDetail.getExpenditures().compareTo(total) != 0) {
                recalculateObjectCodeByCategory(contractsGrantsInvoiceDocument, invoiceDetail, total, invoiceDetailAccountObjectCodeMap.get(invoiceDetail.getCategoryCode()));
                isExpenditureValueChanged = true;
            }
        }
        return isExpenditureValueChanged;
    }

    /**
     * @param invoiceDetailAccountObjectCodes
     * @return
     */
    private KualiDecimal getSumOfExpendituresOfCategory(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        KualiDecimal total = KualiDecimal.ZERO;
        // null can occur if this category has no invoice detail objectcode amounts
        if (invoiceDetailAccountObjectCodes != null) {
            for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
                total = total.add(invoiceDetailAccountObjectCode.getCurrentExpenditures());
            }
        }
        return total;
    }

    /**
     * This method recalculates the invoiceDetailAccountObjectCode in one category that sits behind the scenes of the invoice
     * document.
     * 
     * @param contractsGrantsInvoiceDocument
     * @param invoiceDetail
     * @param total is the sum of the current expenditures from all the object codes in that category
     * @param invoiceDetailAccountObjectCodes
     */
    private void recalculateObjectCodeByCategory(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument, InvoiceDetail invoiceDetail, KualiDecimal total, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        KualiDecimal currentExpenditure = invoiceDetail.getExpenditures();
        KualiDecimal newTotalAmount = KualiDecimal.ZERO;

        // if the sum of the object codes is 0, then distribute the expenditure change evenly to all object codes in the category
        if (total.compareTo(KualiDecimal.ZERO) == 0) {
            if (invoiceDetailAccountObjectCodes != null) {
                int numberOfObjectCodes = invoiceDetailAccountObjectCodes.size();
                if (numberOfObjectCodes != 0) {
                    KualiDecimal newAmount = new KualiDecimal(currentExpenditure.bigDecimalValue().divide(new BigDecimal(numberOfObjectCodes), 10, BigDecimal.ROUND_HALF_DOWN));
                    for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
                        invoiceDetailAccountObjectCode.setCurrentExpenditures(newAmount);
                        newTotalAmount = newTotalAmount.add(newAmount);
                    }
                }
            }
            else { // if the list is null, then there are no account/object code in the gl_balance_t. So assign the amount to the
                   // first object code in the category
                assignCurrentExpenditureToNonExistingAccountObjectCode(contractsGrantsInvoiceDocument, invoiceDetail);
            }
        }
        else {

            for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
                // this may rarely happen
                // if the initial total is 0, that means none of the object codes in this category is set to bill. If this amount is
                // change, then just divide evenly among all object codes.
                KualiDecimal newAmount = (new KualiDecimal(invoiceDetailAccountObjectCode.getCurrentExpenditures().bigDecimalValue().divide(total.bigDecimalValue(), 10, BigDecimal.ROUND_HALF_DOWN).multiply(currentExpenditure.bigDecimalValue())));
                invoiceDetailAccountObjectCode.setCurrentExpenditures(newAmount);
                newTotalAmount = newTotalAmount.add(newAmount);
            }

            int remainderFromRounding = currentExpenditure.subtract(newTotalAmount).multiply(new KualiDecimal(100)).intValue();

            // add remainder from rounding
            KualiDecimal addAmount = new KualiDecimal(0.01);
            if (remainderFromRounding < 0) {
                addAmount = new KualiDecimal(-0.01);
                remainderFromRounding = Math.abs(remainderFromRounding);
            }

            for (int i = 0, j = 0; i < remainderFromRounding; i++, j++) {
                // reset j if its more than size of list
                if (j >= invoiceDetailAccountObjectCodes.size()) {
                    j = 0;
                }
                invoiceDetailAccountObjectCodes.get(j).setCurrentExpenditures(invoiceDetailAccountObjectCodes.get(j).getCurrentExpenditures().add(addAmount));
            }
        }
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @param invoiceDetail
     */
    private void assignCurrentExpenditureToNonExistingAccountObjectCode(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument, InvoiceDetail invoiceDetail) {
        String categoryCode = invoiceDetail.getCategoryCode();
        if (categoryCode == null) {
            LOG.error("Category Code can not be null during recalculation of account object code for Contracts and Grants Invoice Document.");
        }
        // get the category that matches this category code.
        Collection<ContractsAndGrantsCategories> contractsAndGrantsCategories = SpringContext.getBean(BusinessObjectService.class).findAll(ContractsAndGrantsCategories.class);
        Iterator<ContractsAndGrantsCategories> contractsAndGrantsCategoriesIterator = contractsAndGrantsCategories.iterator();
        ContractsAndGrantsCategories category = null;
        while (contractsAndGrantsCategoriesIterator.hasNext()) {
            category = contractsAndGrantsCategoriesIterator.next();
            if (category.getCategoryCode().equals(categoryCode)) {
                break;
            }
        }

        // got the category now.
        if (category != null) {
            final KualiDecimal oneCent = new KualiDecimal(0.01);

            int size = contractsGrantsInvoiceDocument.getAccountDetails().size();
            KualiDecimal amount = new KualiDecimal(invoiceDetail.getExpenditures().bigDecimalValue().divide(new BigDecimal(size), 10, BigDecimal.ROUND_DOWN));
            KualiDecimal remainder = invoiceDetail.getExpenditures().subtract(amount.multiply(new KualiDecimal(size)));

            for (InvoiceAccountDetail invoiceAccountDetail : contractsGrantsInvoiceDocument.getAccountDetails()) {
                // get the first object code from this category
                String objectCode = (String) getObjectCodeArrayFromSingleCategory(category, contractsGrantsInvoiceDocument).toArray()[0];
                InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = new InvoiceDetailAccountObjectCode();
                invoiceDetailAccountObjectCode.setDocumentNumber(contractsGrantsInvoiceDocument.getDocumentNumber());
                invoiceDetailAccountObjectCode.setProposalNumber(contractsGrantsInvoiceDocument.getProposalNumber());
                invoiceDetailAccountObjectCode.setFinancialObjectCode(objectCode);
                invoiceDetailAccountObjectCode.setCategoryCode(categoryCode);
                invoiceDetailAccountObjectCode.setAccountNumber(invoiceAccountDetail.getAccountNumber());
                invoiceDetailAccountObjectCode.setChartOfAccountsCode(invoiceAccountDetail.getChartOfAccountsCode());
                invoiceDetailAccountObjectCode.setCumulativeExpenditures(KualiDecimal.ZERO); // it's 0.00 that's why we are in this
                                                                                             // section to begin with.
                invoiceDetailAccountObjectCode.setTotalBilled(KualiDecimal.ZERO); // this is also 0.00 because it has never been
                                                                                  // billed before

                // tack on or remove one penny until the remainder is 0.
                if (remainder.isGreaterThan(KualiDecimal.ZERO)) {
                    amount = amount.add(oneCent);
                    remainder = remainder.subtract(oneCent);
                }
                else if (remainder.isLessThan(KualiDecimal.ZERO)) {
                    amount = amount.subtract(oneCent);
                    remainder = remainder.add(oneCent);
                }
                invoiceDetailAccountObjectCode.setCurrentExpenditures(amount);

                // add this single account object code item to the list in the Map
                contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes().add(invoiceDetailAccountObjectCode);
            }
        }
        else {
            LOG.error("Category Code cannot be found from the category list during recalculation of account object code for Contracts and Grants Invoice Document.");
        }

        getObjectCodeArrayFromContractsAndGrantsCategories(contractsGrantsInvoiceDocument);
    }

    /**
     * @param invoiceAccountDetails
     * @param invoiceDetailAccountObjectCodes
     */
    public void recalculateAccountDetails(List<InvoiceAccountDetail> invoiceAccountDetails, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        Map<String, KualiDecimal> currentExpenditureByAccountNumberMap = new HashMap<String, KualiDecimal>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            String accountNumber = invoiceDetailAccountObjectCode.getAccountNumber();
            KualiDecimal expenditureSum = currentExpenditureByAccountNumberMap.get(accountNumber);
            // if account number not found in map, then create new total, 0
            if (expenditureSum == null) {
                expenditureSum = new KualiDecimal(0);
            }
            expenditureSum = expenditureSum.add(invoiceDetailAccountObjectCode.getCurrentExpenditures());
            currentExpenditureByAccountNumberMap.put(accountNumber, expenditureSum);
        }

        for (InvoiceAccountDetail invoiceAccountDetail : invoiceAccountDetails) {
            invoiceAccountDetail.setExpenditureAmount(currentExpenditureByAccountNumberMap.get(invoiceAccountDetail.getAccountNumber()));
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#performInvoiceAccountObjectCodeCleanup(java.util.List)
     */
    @Override
    public void performInvoiceAccountObjectCodeCleanup(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            if (invoiceDetailAccountObjectCode.getCurrentExpenditures().compareTo(KualiDecimal.ZERO) == 0) {
                invoiceDetailAccountObjectCode = null;
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getAllOpenContractsGrantsInvoiceDocuments(boolean)
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getAllOpenContractsGrantsInvoiceDocuments(boolean includeWorkflowHeaders) {
        Collection<ContractsGrantsInvoiceDocument> invoices = new ArrayList<ContractsGrantsInvoiceDocument>();

        // retrieve the set of documents without workflow headers
        invoices = contractsGrantsInvoiceDocumentDao.getAllOpen();

        // if we dont need workflow headers, then we're done
        if (!includeWorkflowHeaders || invoices.isEmpty()) {
            return invoices;
        }
        else {
            return populateWorkflowHeaders(invoices);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getAllCGInvoiceDocuments(boolean)
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getAllCGInvoiceDocuments(boolean includeWorkflowHeaders) {
        Collection<ContractsGrantsInvoiceDocument> invoices = new ArrayList<ContractsGrantsInvoiceDocument>();

        // retrieve the set of documents without workflow headers
        invoices = contractsGrantsInvoiceDocumentDao.getAllCGInvoiceDocuments();

        // if we dont need workflow headers, then we're done
        if (!includeWorkflowHeaders || invoices.isEmpty()) {
            return invoices;
        }
        else {
            return populateWorkflowHeaders(invoices);
        }
    }

    /**
     * @param invoices
     * @return
     */
    private Collection<ContractsGrantsInvoiceDocument> populateWorkflowHeaders(Collection<ContractsGrantsInvoiceDocument> invoices) {
        // make a list of necessary workflow docs to retrieve
        List<String> documentHeaderIds = new ArrayList<String>();
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            documentHeaderIds.add(invoice.getDocumentNumber());
        }
        // get all of our docs with full workflow headers
        Collection<ContractsGrantsInvoiceDocument> docs = new ArrayList<ContractsGrantsInvoiceDocument>();
        try {
            for (Document doc : documentService.getDocumentsByListOfDocumentHeaderIds(ContractsGrantsInvoiceDocument.class, documentHeaderIds)) {
                docs.add((ContractsGrantsInvoiceDocument) doc);
            }
        }
        catch (WorkflowException e) {
            throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
        }
        return docs;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getAwardBilledToDateByProposalNumber(java.lang.Long)
     */
    @Override
    public KualiDecimal getAwardBilledToDateByProposalNumber(Long proposalNumber) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        List<AwardAccountObjectCodeTotalBilled> accountObjectCodeTotalBilledList = (List<AwardAccountObjectCodeTotalBilled>) SpringContext.getBean(BusinessObjectService.class).findMatching(AwardAccountObjectCodeTotalBilled.class, keys);
        KualiDecimal billedToDate = KualiDecimal.ZERO;
        for (AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled : accountObjectCodeTotalBilledList) {
            billedToDate = billedToDate.add(awardAccountObjectCodeTotalBilled.getTotalBilled());
        }
        return billedToDate;
    }

    /**
     * This method retrieves all CG invoice document that match the given criteria
     * 
     * @param criteria
     * @return
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> retrieveAllCGInvoicesByCriteria(Criteria criteria) {
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentDao.getInvoicesByCriteria(criteria);
        if (CollectionUtils.isEmpty(cgInvoices)) {
            return null;
        }
        return cgInvoices;
    }


    /**
     * This method retrieves all invoices with open and with final status based on loc creation type = LOC fund
     * 
     * @param locFund
     * @param errorFileName
     * @return
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByLOCFund(String locFund, String errorFileName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("locCreationType", ArConstants.LOC_BY_LOC_FUND);
        criteria.addEqualTo("letterOfCreditFundCode", locFund);
        criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, true);
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        String detail = "LOC Creation Type:" + ArConstants.LOC_BY_LOC_FUND + " of value " + locFund;
        cgInvoices = contractsGrantsInvoiceDocumentDao.getInvoicesByCriteria(criteria);
        List<String> invalidInvoices = validateInvoices(cgInvoices, detail, errorFileName);
        if (!CollectionUtils.isEmpty(invalidInvoices)) {
            return null;

        }
        return cgInvoices;
    }

    /**
     * This method retrieves all invoices with open and with final status based on loc creation type = LOC fund group
     * 
     * @param locFundGroup
     * @param errorFileName
     * @return
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByLOCFundGroup(String locFundGroup, String errorFileName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("locCreationType", ArConstants.LOC_BY_LOC_FUND_GRP);
        criteria.addEqualTo("letterOfCreditFundGroupCode", locFundGroup);
        criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, true);
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        String detail = "LOC Creation Type:" + ArConstants.LOC_BY_LOC_FUND_GRP + " of value " + locFundGroup;
        cgInvoices = contractsGrantsInvoiceDocumentDao.getInvoicesByCriteria(criteria);
        List<String> invalidInvoices = validateInvoices(cgInvoices, detail, errorFileName);
        if (!CollectionUtils.isEmpty(invalidInvoices)) {
            return null;

        }
        return cgInvoices;
    }

    /**
     * This method retrieves all invoices with open and with final status based on customer number
     * 
     * @param customerNumber
     * @param errorFileName
     * @return
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByCustomerNumber(String customerNumber, String errorFileName) {

        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        cgInvoices = contractsGrantsInvoiceDocumentDao.getOpenInvoicesByCustomerNumber(customerNumber);
        String detail = "Customer Number#" + customerNumber;
        List<String> invalidInvoices = validateInvoices(cgInvoices, detail, errorFileName);
        if (!CollectionUtils.isEmpty(invalidInvoices)) {
            return null;

        }
        return cgInvoices;
    }

    /**
     * This method validates invoices and output an error file including unqualified invoices with reason stated.
     * 
     * @param cgInvoices
     * @param outputFileStream
     * @return
     */
    public List<String> validateInvoices(Collection<ContractsGrantsInvoiceDocument> cgInvoices, String detail, String errorFileName) {
        boolean resultInd = false;
        boolean isInvalid = false;
        String line = null;
        List<String> invalidGroup = new ArrayList<String>();
        if (CollectionUtils.isEmpty(cgInvoices)) {
            line = "There were no invoices retrieved to process for " + detail;
            invalidGroup.add(line);
            try {
                File errOutPutFile = new File(errorFileName);
                PrintStream outputFileStream = null;

                try {
                    outputFileStream = new PrintStream(errOutPutFile);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                writeErrorEntry(line, outputFileStream);
            }
            catch (IOException ioe) {
                LOG.error("LetterOfCreditCreateServiceImpl.validateInvoices Stopped: " + ioe.getMessage());
                throw new RuntimeException("LetterOfCreditCreateServiceImpl.validateInvoices Stopped: " + ioe.getMessage(), ioe);
            }
            return invalidGroup;
        }
        for (ContractsGrantsInvoiceDocument cgInvoice : cgInvoices) {
            isInvalid = false;
            // if the invoices are not final yet - then the LOC cannot be created
            if (!cgInvoice.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equalsIgnoreCase(KFSConstants.DocumentStatusCodes.APPROVED)) {
                line = "Contracts Grants Invoice# " + cgInvoice.getDocumentNumber() + " : " + ArConstants.BatchFileSystem.LOC_CREATION_ERROR_INVOICE_NOT_FINAL;
                invalidGroup.add(line);
                isInvalid = true;
            }

            // if invalid is true, the award is unqualified.
            // records the unqualified award with failed reasons.
            if (isInvalid) {
                File errOutPutFile = new File(errorFileName);
                PrintStream outputFileStream = null;

                try {
                    outputFileStream = new PrintStream(errOutPutFile);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    writeErrorEntry(line, outputFileStream);
                }
                catch (IOException ioe) {
                    LOG.error("LetterOfCreditCreateServiceImpl.validateInvoices Stopped: " + ioe.getMessage());
                    throw new RuntimeException("LetterOfCreditCreateServiceImpl.validateInvoices Stopped: " + ioe.getMessage(), ioe);
                }

                try {
                    writeNewLines("", outputFileStream);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return invalidGroup;
    }

    /**
     * This method would write errors to the error file
     * 
     * @param line
     * @param printStream
     * @throws IOException
     */
    protected void writeErrorEntry(String line, PrintStream printStream) throws IOException {
        try {
            printStream.printf("%s\n", line);
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * This method would write new line argument to the error file.
     * 
     * @param newline
     * @param printStream
     * @throws IOException
     */
    protected void writeNewLines(String newline, PrintStream printStream) throws IOException {
        try {
            printStream.printf("%s\n", newline);
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * This method calculates the Budget and cumulative amount for Award Account
     * 
     * @param awardAccount
     * @return
     */
    @Override
    public KualiDecimal getBudgetAndActualsForAwardAccount(ContractsAndGrantsBillingAwardAccount awardAccount, String balanceTypeCode, Date awardBeginningDate) {
        List<Balance> glBalances = new ArrayList<Balance>();
        KualiDecimal balanceAmount = KualiDecimal.ZERO;
        KualiDecimal balAmt = KualiDecimal.ZERO;
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();


        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getFiscalYear(awardBeginningDate);

        for (Integer i = fiscalYear; i <= currentYear; i++) {
            fiscalYears.add(i);
        }

        for (Integer eachFiscalYr : fiscalYears) {

            Map<String, Object> balanceKeys = new HashMap<String, Object>();
            balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
            balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
            balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
            balanceKeys.put("balanceTypeCode", balanceTypeCode);
            balanceKeys.put("objectTypeCode", ArPropertyConstants.EXPENSE_OBJECT_TYPE);
            glBalances.addAll(SpringContext.getBean(BusinessObjectService.class).findMatching(Balance.class, balanceKeys));
        }
        for (Balance bal : glBalances) {
            if (ObjectUtils.isNotNull(bal.getSubAccount()) && ObjectUtils.isNotNull(bal.getSubAccount().getA21SubAccount()) && !StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
                balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                balanceAmount = balanceAmount.add(balAmt);
            }
        }
        return balanceAmount;
    }

    /**
     * This method retrieves the amount to draw for the award account based on teh criteria passed
     * 
     * @param awardaccounts
     * @return
     */
    @Override
    public void setAwardAccountToDraw(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsAndGrantsBillingAward award) {

        boolean isValid = true;
        // 1. To get the billed to date amount for every award account based on the criteria passed.
        List<AwardAccountObjectCodeTotalBilled> awardAccountTotalBilledAmounts = awardAccountObjectCodeTotalBilledDao.getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);


        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {

            // 2. Get the Cumulative amount from GL Balances.

            KualiDecimal cumAmt = getBudgetAndActualsForAwardAccount(awardAccount, ArPropertyConstants.ACTUAL_BALANCE_TYPE, award.getAwardBeginningDate());
            KualiDecimal billedAmount = KualiDecimal.ZERO;
            KualiDecimal amountToDraw = KualiDecimal.ZERO;


            // 3. Amount to Draw = Cumulative amount - Billed to Date.(This would be ultimately the current expenditures in the
            // invoice document.
            for (AwardAccountObjectCodeTotalBilled awardAccountTotalBilledAmount : awardAccountTotalBilledAmounts) {
                if (awardAccountTotalBilledAmount.getAccountNumber().equals(awardAccount.getAccountNumber()) && awardAccountTotalBilledAmount.getChartOfAccountsCode().equals(awardAccount.getChartOfAccountsCode()) && awardAccountTotalBilledAmount.getProposalNumber().equals(awardAccount.getProposalNumber())) {
                    billedAmount = billedAmount.add(awardAccountTotalBilledAmount.getTotalBilled());
                }
            }
            amountToDraw = cumAmt.subtract(billedAmount);
            // set the amount to Draw in the award Account
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
            criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
            criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount.getProposalNumber());
            SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setAmountToDrawToAwardAccount(criteria, amountToDraw);
        }

    }

    /**
     * This method calculates the claim on cash balance for every award account.
     * 
     * @param awardaccount
     * @return
     */
    @Override
    public KualiDecimal getClaimOnCashforAwardAccount(ContractsAndGrantsBillingAwardAccount awardAccount, java.sql.Date awardBeginningDate) {

        // 2. Get the Cumulative amount from GL Balances.
        KualiDecimal balAmt = KualiDecimal.ZERO;
        KualiDecimal expAmt = KualiDecimal.ZERO;
        KualiDecimal incAmt = KualiDecimal.ZERO;
        KualiDecimal claimOnCash = KualiDecimal.ZERO;
        List<Balance> glBalances = new ArrayList<Balance>();
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();


        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getFiscalYear(awardBeginningDate);

        for (Integer i = fiscalYear; i <= currentYear; i++) {
            fiscalYears.add(i);
        }

        for (Integer eachFiscalYr : fiscalYears) {
            Map<String, Object> balanceKeys = new HashMap<String, Object>();
            balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
            balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
            balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
            balanceKeys.put("balanceTypeCode", ArPropertyConstants.ACTUAL_BALANCE_TYPE);
            glBalances.addAll(SpringContext.getBean(BusinessObjectService.class).findMatching(Balance.class, balanceKeys));
        }
        for (Balance bal : glBalances) {
            if (ObjectUtils.isNotNull(bal.getSubAccount()) && ObjectUtils.isNotNull(bal.getSubAccount().getA21SubAccount()) && !StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
                if (bal.getObjectTypeCode().equalsIgnoreCase(ArPropertyConstants.EXPENSE_OBJECT_TYPE)) {
                    balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());

                    expAmt = expAmt.add(balAmt);
                }
                else if (bal.getObjectTypeCode().equalsIgnoreCase(ArPropertyConstants.INCOME_OBJECT_TYPE)) {
                    balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());

                    incAmt = incAmt.add(balAmt);
                }
            }
        }

        return claimOnCash = incAmt.subtract(expAmt);


    }

    /**
     * This method retrieves the amount available to draw for the award accounts
     * 
     * @param awardTotalAmount
     * @param awardAccount
     */
    @Override
    public KualiDecimal getAmountAvailableToDraw(KualiDecimal awardTotalAmount, List<ContractsAndGrantsBillingAwardAccount> awardAccounts) {

        // 1. To get the billed to date amount for every award account based on the criteria passed.
        List<AwardAccountObjectCodeTotalBilled> awardAccountTotalBilledAmounts = awardAccountObjectCodeTotalBilledDao.getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);
        KualiDecimal billedAmount = KualiDecimal.ZERO;
        KualiDecimal amountAvailableToDraw = KualiDecimal.ZERO;
        for (AwardAccountObjectCodeTotalBilled awardAccountTotalBilledAmount : awardAccountTotalBilledAmounts) {
            billedAmount = billedAmount.add(awardAccountTotalBilledAmount.getTotalBilled());
        }
        amountAvailableToDraw = awardTotalAmount.subtract(billedAmount);

        return amountAvailableToDraw;
    }

    /**
     * This method serves as a create and update. When it is first called, the List<InvoiceSuspensionCategory> is empty. This list
     * then gets populated with invoiceSuspensionCategories where the test fails. Each time the document goes through validation,
     * and this method gets called, it will update the list by adding or remvoing the suspension categories
     * 
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#updateSuspensionCategoriesOnDocument(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public void updateSuspensionCategoriesOnDocument(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {

        ContractsAndGrantsBillingAward award = contractsGrantsInvoiceDocument.getAward();
        String documentNumber = contractsGrantsInvoiceDocument.getDocumentNumber();
        // ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService =
        // SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

        List<String> suspensionCategoryCodes = new ArrayList<String>(); // list of existing codes

        // the first time this is checked, the list will be empty. On subsequent checks, if the list is not empty, it will add the
        // suspension codes to the
        // List<String> suspensionCategoryCodes. This list is where we keep track of the codes, and base off this list, we will
        // create actual suspension category objects.
        List<InvoiceSuspensionCategory> invoiceSuspensionCategories = contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories();
        for (InvoiceSuspensionCategory invoiceSuspensionCategory : invoiceSuspensionCategories) {
            suspensionCategoryCodes.add(invoiceSuspensionCategory.getSuspensionCategoryCode());
        }

        // validation suspension code - Check if invoice is created after award expiration date
        if (isInvoiceCreateDateAfterAwardEndingDate(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.BILL_DATE_EXCEEDS_THE_AWARD_STOP_DATE);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.BILL_DATE_EXCEEDS_THE_AWARD_STOP_DATE);
        }

        // validation suspension code - Check if amount to bill with amount already billed is greater than the award total amount
        if (isBillAmountExceedAwardTotalAmount(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.NEW_TOTAL_BILLED_AMOUNT_EXCEEDS_AWARD_TOTAL);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.NEW_TOTAL_BILLED_AMOUNT_EXCEEDS_AWARD_TOTAL);
        }

        // validation suspension code - Check if invoice amount is less than the minimum allowed specified by the award
        if (isInvoiceAmountLessThanInvoiceMinimumRequirements(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.INVOICE_AMOUNT_IS_LESS_THAN_INVOICE_MINIMUM_REQUIREMENT);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.INVOICE_AMOUNT_IS_LESS_THAN_INVOICE_MINIMUM_REQUIREMENT);
        }

        // validation suspension code - Check to see that an attachment is made if it is required by the award
        if (isReportNotAttachedButRequiredByAward(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.REPORTS_ARE_REQUIRED_TO_BE_ATTACHED);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.REPORTS_ARE_REQUIRED_TO_BE_ATTACHED);
        }

        // validation suspension code - Make sure the Primary Address is completed
        if (!isAgencyPrimaryAddressComplete(award.getAgency())) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AGENCY_PRIMARY_ADDRESS_NOT_COMPLETE);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AGENCY_PRIMARY_ADDRESS_NOT_COMPLETE);
        }

        // validation suspension code - Check to see if the Alternate address is completed if it was entered to begin with
        if (!isAgencyAlternateAddressComplete(award.getAgency())) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AGENCY_ALTERNATE_ADDRESS_NOT_COMPLETE);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AGENCY_ALTERNATE_ADDRESS_NOT_COMPLETE);
        }

        // validation suspension code - Make sure invoice is final if the award is already expired
        if (isInvoiceNotFinalAndAwardExpired(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.INVOICE_NOT_FINAL_AND_EXPIRATION_DATE_REACHED);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.INVOICE_NOT_FINAL_AND_EXPIRATION_DATE_REACHED);
        }

        // validation suspension code - Check to see if object codes are included in a category. one way to check is to compare
        // total current expenditure to the sum of account current expenditure
        if (!isAwardIBillingFrequencyIsPredetermined(award) && !isAwardBillingFrequencyIsMilestone(award) && !isCategoryCumulativeExpenditureMatchAccountCumulativeExpenditureSum(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.OBJECT_CODE_FOR_AN_ACCOUNT_MAY_BE_MISSING_FROM_CGB_CATEGORY_CODE);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.OBJECT_CODE_FOR_AN_ACCOUNT_MAY_BE_MISSING_FROM_CGB_CATEGORY_CODE);
        }

        // validation suspension code - Check to see if Loc Amount is sufficient
        if (isLocAmountNotSufficent(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.LOC_REMAINING_AMOUNT_IS_NOT_SUFFICIENT);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.LOC_REMAINING_AMOUNT_IS_NOT_SUFFICIENT);
        }

        // validation suspension code - Check to see if award has any active but expired account
        if (isAwardHasActiveExpiredAccount(award)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AWARD_HAS_ACTIVE_BUT_EXPIRED_ACCOUNT);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AWARD_HAS_ACTIVE_BUT_EXPIRED_ACCOUNT);
        }

        // validation suspension code - Check to see if award has 'Suspend Invoicing' enabled
        if (award.isSuspendInvoicingIndicator()) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AWARD_SUSPENDED_BY_USER);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AWARD_SUSPENDED_BY_USER);
        }

        // validation suspension code - Check to see if invoice type is missing from award
        if (isAwardInvoiceTypeMissing(award)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.INVOICE_TYPE_IS_MISSING);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.INVOICE_TYPE_IS_MISSING);
        }

        // validation suspension code - Check to see if award has closed account of which the still have current expenditure
        if (isAwardHasClosedAccountWithCurrentExpenditures(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AWARD_HAS_CLOSED_ACCOUNT_WITH_CURRENT_EXPENDITURES);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AWARD_HAS_CLOSED_ACCOUNT_WITH_CURRENT_EXPENDITURES);
        }

        // validation suspension code - Check to see if invoice type is missing from award
        if (isAwardMarkedStopWork(award)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AWARD_HAS_STOP_WORK_MARKED);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AWARD_HAS_STOP_WORK_MARKED);
        }
    }

    /**
     * @param suspensionCategoryCodes
     * @param invoiceSuspensionCategories
     * @param documentNumber
     * @param suspensionCategoryCode
     */
    // this method adds a new InvoiceSuspensionCategory to the List<InvoiceSuspensionCategory> if it does not already exist.
    private void addSuspensionCategoryToDocument(List<String> suspensionCategoryCodes, List<InvoiceSuspensionCategory> invoiceSuspensionCategories, String documentNumber, String suspensionCategoryCode) {
        if (!suspensionCategoryCodes.contains(suspensionCategoryCode)) { // check prevents duplicate
            // To check if the suspension category is active.
            SuspensionCategory suspensionCategory = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(SuspensionCategory.class, suspensionCategoryCode);
            if (ObjectUtils.isNotNull(suspensionCategory) && suspensionCategory.isActive()) {
                invoiceSuspensionCategories.add(new InvoiceSuspensionCategory(documentNumber, suspensionCategoryCode));
            }
        }
    }

    /**
     * @param suspensionCategoryCodes
     * @param invoiceSuspensionCategories
     * @param suspensionCategoryCode
     */
    // this method removes a suspensionCategoryCode from the List<String> and removes the object InvoiceSuspensionCategory from the
    // List<InvoiceSuspensionCategory>
    private void removeSuspensionCategoryFromDocument(List<String> suspensionCategoryCodes, List<InvoiceSuspensionCategory> invoiceSuspensionCategories, String suspensionCategoryCode) {
        if (suspensionCategoryCodes.contains(suspensionCategoryCode)) {
            suspensionCategoryCodes.remove(suspensionCategoryCode);
            for (InvoiceSuspensionCategory invoiceSuspensionCategory : invoiceSuspensionCategories) {
                if (suspensionCategoryCode.equals(invoiceSuspensionCategory.getSuspensionCategoryCode())) {
                    invoiceSuspensionCategories.remove(invoiceSuspensionCategory);
                    break;
                }
            }
        }
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isInvoiceCreateDateAfterAwardEndingDate(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        Date documentDate = new Date(contractsGrantsInvoiceDocument.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis());
        Date awardEndingDate = contractsGrantsInvoiceDocument.getAward().getAwardEndingDate();

        // remove time
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(documentDate.getTime());
        cal.set(cal.HOUR_OF_DAY, 0);
        cal.set(cal.MINUTE, 0);
        cal.set(cal.SECOND, 0);
        cal.set(cal.MILLISECOND, 0);
        documentDate.setTime(cal.getTimeInMillis());

        // remove time
        cal = Calendar.getInstance();
        cal.setTimeInMillis(awardEndingDate.getTime());
        cal.set(cal.HOUR_OF_DAY, 0);
        cal.set(cal.MINUTE, 0);
        cal.set(cal.SECOND, 0);
        cal.set(cal.MILLISECOND, 0);
        awardEndingDate.setTime(cal.getTimeInMillis());

        return documentDate.after(awardEndingDate);
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isBillAmountExceedAwardTotalAmount(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        return contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().isGreaterThan(contractsGrantsInvoiceDocument.getAward().getAwardTotalAmount());
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isInvoiceAmountLessThanInvoiceMinimumRequirements(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        KualiDecimal invoiceMinimumAmount = contractsGrantsInvoiceDocument.getAward().getMinInvoiceAmount();
        if (invoiceMinimumAmount == null) {
            return false; // if no minimum specified, then no limit
        }
        return contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().isLessThan(invoiceMinimumAmount);
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isReportNotAttachedButRequiredByAward(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if (contractsGrantsInvoiceDocument.getAward().isAdditionalFormsRequiredIndicator()) {
            return true;
        }
        return false;
    }

    /**
     * @param agency
     * @return
     */
    public boolean isAgencyPrimaryAddressComplete(ContractsAndGrantsBillingAgency agency) {

        List<ContractsAndGrantsAgencyAddress> agencyAddresses = new ArrayList<ContractsAndGrantsAgencyAddress>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.AGENCY_NUMBER, agency.getAgencyNumber());
        agencyAddresses = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObjectsList(ContractsAndGrantsAgencyAddress.class, map);
        for (ContractsAndGrantsAgencyAddress agencyAddress : agencyAddresses) {
            if (ArConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE.equals(agencyAddress.getAgencyAddressTypeCode())) {
                return isAgencyAddressComplete(agencyAddress);
            }
        }
        return false;
    }

    /**
     * @param agency
     * @return
     */
    public boolean isAgencyAlternateAddressComplete(ContractsAndGrantsBillingAgency agency) {

        List<ContractsAndGrantsAgencyAddress> agencyAddresses = new ArrayList<ContractsAndGrantsAgencyAddress>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.AGENCY_NUMBER, agency.getAgencyNumber());
        agencyAddresses = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObjectsList(ContractsAndGrantsAgencyAddress.class, map);

        for (ContractsAndGrantsAgencyAddress agencyAddress : agencyAddresses) {
            if (ArConstants.AGENCY_ALTERNATE_ADDRESSES_TYPE_CODE.equals(agencyAddress.getAgencyAddressTypeCode())) {
                return isAgencyAddressComplete(agencyAddress);
            }
        }
        return true; // if no alternate address entered at all, then that is ok
    }

    /**
     * @param agencyAddress
     * @return
     */
    public boolean isAgencyAddressComplete(ContractsAndGrantsAgencyAddress agencyAddress) {
        if (!StringUtils.isEmpty(agencyAddress.getAgencyLine1StreetAddress()) && !StringUtils.isEmpty(agencyAddress.getAgencyCityName()) && !StringUtils.isEmpty(agencyAddress.getAgencyStateCode()) && !StringUtils.isEmpty(agencyAddress.getAgencyZipCode()) && !StringUtils.isEmpty(agencyAddress.getAgencyCountryCode())) {
            return true;
        }
        return false;
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isInvoiceNotFinalAndAwardExpired(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        return isAwardExpired(contractsGrantsInvoiceDocument.getAward()) && !contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().isFinalBillIndicator();
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isCategoryCumulativeExpenditureMatchAccountCumulativeExpenditureSum(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getTotalInvoiceDetails()) && CollectionUtils.isNotEmpty(contractsGrantsInvoiceDocument.getTotalInvoiceDetails())) {
            KualiDecimal categoryCumulativeExpenditure = contractsGrantsInvoiceDocument.getTotalInvoiceDetails().get(0).getCumulative();
            KualiDecimal accountDetailsCumulativeExpenditure = KualiDecimal.ZERO;

            for (InvoiceAccountDetail invoiceAccountDetail : contractsGrantsInvoiceDocument.getAccountDetails()) {
                accountDetailsCumulativeExpenditure = accountDetailsCumulativeExpenditure.add(invoiceAccountDetail.getCumulativeAmount());
            }

            if (categoryCumulativeExpenditure.equals(accountDetailsCumulativeExpenditure)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }

    }

    /**
     * @param award
     * @return
     */
    public boolean isAwardMarkedStopWork(ContractsAndGrantsBillingAward award) {
        return award.isStopWorkIndicator();
    }


    /**
     * @param award
     * @return
     */
    public boolean isAwardInvoiceTypeMissing(ContractsAndGrantsBillingAward award) {
        return StringUtils.isEmpty(award.getInvoicingOptions());
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isLocAmountNotSufficent(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if (ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE.equals(contractsGrantsInvoiceDocument.getAward().getBillingFrequency().getFrequency())) {
            if (contractsGrantsInvoiceDocument.getAward().getLetterOfCreditFund().getLetterOfCreditFundAmount().isLessThan(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param award
     * @return
     */
    public boolean isAwardHasActiveExpiredAccount(ContractsAndGrantsBillingAward award) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = award.getActiveAwardAccounts();
        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
            if (ObjectUtils.isNotNull(awardAccount.getAccount())) {
                Date accountExpirationDate = awardAccount.getAccount().getAccountExpirationDate();
                if (accountExpirationDate != null && now.after(accountExpirationDate) && awardAccount.getAccount().isActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param award
     * @return
     */
    public boolean isAwardExpired(ContractsAndGrantsBillingAward award) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        return now.after(award.getAwardEndingDate());
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isAwardHasClosedAccountWithCurrentExpenditures(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        // for each InvoiceDetailAccountObjectCode, extract the chart code and account number, and store it in a map
        // where the key is chart code and value is a set of account numbers (no duplicates).
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes()) {
            String chartOfAccountsCode = invoiceDetailAccountObjectCode.getChartOfAccountsCode();
            String accountNumber = invoiceDetailAccountObjectCode.getAccountNumber();
            if (map.containsKey(chartOfAccountsCode)) {
                Set<String> set = map.get(chartOfAccountsCode);
                set.add(accountNumber);
            }
            else {
                Set<String> set = new HashSet<String>();
                set.add(accountNumber);
                map.put(chartOfAccountsCode, set);
            }
        }

        // Then go through the map and check to see if any of them have closed accounts
        AccountService accountService = SpringContext.getBean(AccountService.class);
        Set<String> keys = map.keySet();
        for (String chartOfAccountsCode : keys) {
            Set<String> values = map.get(chartOfAccountsCode);
            for (String accountNumber : values) {
                if (accountService.getByPrimaryId(chartOfAccountsCode, accountNumber).isClosed()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param award
     * @return
     */
    public boolean isAwardBillingFrequencyIsMilestone(ContractsAndGrantsBillingAward award) {
        if (ObjectUtils.isNull(award.getPreferredBillingFrequency())) {
            return false;
        }

        return ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE.equals(award.getPreferredBillingFrequency());
    }

    /**
     * @param award
     * @return
     */
    public boolean isAwardIBillingFrequencyIsPredetermined(ContractsAndGrantsBillingAward award) {
        if (ObjectUtils.isNull(award.getPreferredBillingFrequency())) {
            return false;
        }
        return ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE.equals(award.getPreferredBillingFrequency());
    }


    /**
     * This method would make sure the amounts of the currrent period are not included. So it calculates the cumulative and
     * subtracts the current period values. This would be done for Billing Frequencies - Monthly, Quarterly, Semi-Annual and Annual.
     * 
     * @param glBalance
     * @return balanceAmount
     */
    @Override
    public KualiDecimal retrieveAccurateBalanceAmount(java.sql.Date lastBilledDate, Balance glBalance) {


        // 1. calculate invoice period
        AccountingPeriod invoicePeriod = accountingPeriodService.getByDate(lastBilledDate);
        String invoicePeriodCode = invoicePeriod.getUniversityFiscalPeriodCode();

        // 2. Get the current Period Code
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        java.sql.Date today = new java.sql.Date(ts.getTime());
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
        String currentPeriodCode = currPeriod.getUniversityFiscalPeriodCode();

        // 3. Now to iterate over the period codes and find the amounts for the differnce between invoice Period and current Period
        // - Assuming its the same fiscal year
        List<AccountingPeriod> acctPeriodList = new ArrayList<AccountingPeriod>();

        acctPeriodList = (List<AccountingPeriod>) accountingPeriodService.getAllAccountingPeriods();
        KualiDecimal currentBalanceAmount = KualiDecimal.ZERO;
        java.lang.reflect.Method method;

        if (CollectionUtils.isNotEmpty(acctPeriodList)) {

            for (int i = acctPeriodList.indexOf(invoicePeriod) + 1; i <= acctPeriodList.indexOf(currPeriod); i++) {
                if (acctPeriodList.get(i).getUniversityFiscalYear().equals(currPeriod.getUniversityFiscalYear()) && acctPeriodList.get(i).isActive()) {
                    // Now to get the month for the period.
                    String periodCode = acctPeriodList.get(i).getUniversityFiscalPeriodCode().replaceFirst("^0*", "");
                    String methodName = "getMonth" + periodCode + "Amount";
                    try {
                        method = glBalance.getClass().getMethod(methodName);
                        currentBalanceAmount = currentBalanceAmount.add((KualiDecimal) method.invoke(glBalance));

                    }
                    catch (SecurityException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    catch (NoSuchMethodException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    catch (IllegalArgumentException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    catch (IllegalAccessException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    catch (InvocationTargetException e) {
                        throw new RuntimeException(e.getMessage());
                    }

                }
            }
        }
        KualiDecimal balAmt = glBalance.getContractsGrantsBeginningBalanceAmount().add(glBalance.getAccountLineAnnualBalanceAmount());
        KualiDecimal accurateBalanceAmount = balAmt.subtract(currentBalanceAmount);

        return accurateBalanceAmount;
    }

    /**
     * This method get the milestones with the criteria defined and set value to isItBilled.
     */
    @Override
    public void retrieveAndUpdateMilestones(List<InvoiceMilestone> invoiceMilestones, String string) throws Exception {
        if (invoiceMilestones == null) {
            throw new Exception("(List<InvoiceMilestone> invoiceMilestones cannot be null");
        }
        List<Long> milestoneIds = new ArrayList<Long>();
        for (InvoiceMilestone invoiceMilestone : invoiceMilestones) {
            milestoneIds.add(invoiceMilestone.getMilestoneIdentifier());
        }
        // This method get the milestones with the criteria defined and set value to isItBilled.

        this.setMilestonesisItBilled(invoiceMilestones.get(0).getProposalNumber(), milestoneIds, string);
    }

    /**
     * This method updates value of isItBilled in Milestone BO to Yes
     * 
     * @param criteria
     */
    @SuppressWarnings("null")
    public void setMilestonesisItBilled(Long proposalNumber, List<Long> milestoneIds, String value) {
        Collection<Milestone> milestones = null;
        try {
            milestones = getMilestoneDao().getMatchingMilestoneByProposalIdAndInListOfMilestoneId(proposalNumber, milestoneIds);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        for (Milestone milestone : milestones) {
            if(value.equalsIgnoreCase(KFSConstants.ParameterValues.YES) || value.equalsIgnoreCase(KFSConstants.ParameterValues.STRING_YES)){
            milestone.setBilledIndicator(Boolean.TRUE);
            }else{
                milestone.setBilledIndicator(Boolean.FALSE);    
            }
            getBusinessObjectService().save(milestone);
        }
    }


    /**
     * This method get the bills with the criteria defined and set value to isItBilled.
     */
    @Override
    public void retrieveAndUpdateBills(List<InvoiceBill> invoiceBills, String value) throws Exception {
        if (invoiceBills == null) {
            throw new Exception("(List<InvoiceBill> invoiceBills cannot be null");
        }

        Criteria mainCriteria = new Criteria();

        for (InvoiceBill invoiceBill : invoiceBills) {
            Criteria billNumberCriteria = new Criteria();
            Criteria billIdCriteria = new Criteria();
            Criteria proposalNumberCriteria = new Criteria();
            Criteria subCriteria = new Criteria();

            billNumberCriteria.addEqualTo("billNumber", invoiceBill.getBillNumber());
            billIdCriteria.addEqualTo("billIdentifier", invoiceBill.getBillIdentifier());
            proposalNumberCriteria.addEqualTo("proposalNumber", invoiceBill.getProposalNumber());

            subCriteria.addAndCriteria(billNumberCriteria);
            subCriteria.addAndCriteria(billIdCriteria);
            subCriteria.addAndCriteria(proposalNumberCriteria);

            mainCriteria.addOrCriteria(subCriteria);
        }

        // To get the bills with the criteria defined and set value to isItBilled.
        this.setBillsisItBilled(mainCriteria, value);
    }

    public void setBillsisItBilled(Criteria criteria, String value) {
        Collection<Bill> bills = getBillDao().getBillsByMatchingCriteria(criteria);
        for (Bill bill : bills) {
            if (KFSConstants.ParameterValues.YES.equalsIgnoreCase(value) || KFSConstants.ParameterValues.STRING_YES.equalsIgnoreCase(value)) {
                bill.setBilledIndicator(true);
            }
            else {
                bill.setBilledIndicator(false);
            }
            getBusinessObjectService().save(bill);
        }
    }


    /**
     * Gets the billDao attribute.
     *
     * @return Returns the billDao.
     */
    public BillDao getBillDao() {
        return billDao;
    }

    /**
     * Sets the billDao attribute value.
     *
     * @param billDao The billDao to set.
     */
    public void setBillDao(BillDao billDao) {
        this.billDao = billDao;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#calculateTotalPaymentsToDateByAward(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public KualiDecimal calculateTotalPaymentsToDateByAward(ContractsAndGrantsBillingAward award) {
        KualiDecimal totalPayments = KualiDecimal.ZERO;

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocs = SpringContext.getBean(BusinessObjectService.class).findMatching(ContractsGrantsInvoiceDocument.class, criteria);

        for (ContractsGrantsInvoiceDocument cgInvoiceDoc : cgInvoiceDocs) {
            criteria.clear();
            criteria.put("financialDocumentReferenceInvoiceNumber", cgInvoiceDoc.getDocumentNumber());

            Collection<InvoicePaidApplied> invoicePaidApplieds = SpringContext.getBean(BusinessObjectService.class).findMatching(InvoicePaidApplied.class, criteria);
            for (InvoicePaidApplied invoicePapidApplied : invoicePaidApplieds) {

                totalPayments = totalPayments.add(invoicePapidApplied.getInvoiceItemAppliedAmount());
            }
        }
        return totalPayments;
    }

    /**
     * This method calculates the Cumulative Disbursement amount for an awardAccount
     * 
     * @param awardAccount
     * @return
     */
    public KualiDecimal getCumulativeCashDisbursement(ContractsAndGrantsBillingAwardAccount awardAccount, java.sql.Date awardBeginningDate) {
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        KualiDecimal cumAmt = KualiDecimal.ZERO;
        KualiDecimal balAmt = KualiDecimal.ZERO;
        List<Balance> glBalances = new ArrayList<Balance>();

        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();


        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getFiscalYear(awardBeginningDate);

        for (Integer i = fiscalYear; i <= currentYear; i++) {
            fiscalYears.add(i);
        }

        for (Integer eachFiscalYr : fiscalYears) {
            Map<String, Object> balanceKeys = new HashMap<String, Object>();
            balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
            balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
            balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
            balanceKeys.put("balanceTypeCode", ArPropertyConstants.ACTUAL_BALANCE_TYPE);
            balanceKeys.put("objectTypeCode", ArPropertyConstants.EXPENSE_OBJECT_TYPE);
            glBalances.addAll(SpringContext.getBean(BusinessObjectService.class).findMatching(Balance.class, balanceKeys));
        }
        for (Balance bal : glBalances) {
            if (ObjectUtils.isNotNull(bal.getSubAccount()) && ObjectUtils.isNotNull(bal.getSubAccount().getA21SubAccount()) && !StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
                balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                cumAmt = cumAmt.add(balAmt);
            }
        }
        return cumAmt;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardInvoicingOptionMissing(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean isAwardInvoicingOptionMissing(ContractsAndGrantsBillingAward award) {
        String invOption = award.getInvoicingOptions();
        if (invOption == null || invOption.isEmpty()) {
            return true;
        }
        return false;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardClosed(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean isAwardClosed(ContractsAndGrantsBillingAward award) {
        Date today, clsDt;
        today = dateTimeService.getCurrentSqlDateMidnight();
        clsDt = award.getAwardClosingDate();
        if (ObjectUtils.isNotNull(clsDt) && clsDt.before(today)) {
            return true;
        }
        return false;

    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasExpiredAccounts(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean hasExpiredAccounts(ContractsAndGrantsBillingAward award) {
        Collection<Account> accounts = getExpiredAccountsOfAward(award);
        if (ObjectUtils.isNotNull(accounts) && !accounts.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoAccountsAssigned(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean hasNoActiveAccountsAssigned(ContractsAndGrantsBillingAward award) {

        Collection<ContractsAndGrantsBillingAwardAccount> awardAccounts = award.getActiveAwardAccounts();
        if (awardAccounts.isEmpty()) {
            return true;
        }
        return false;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardInvoicingSuspendedByUser(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean isAwardInvoicingSuspendedByUser(ContractsAndGrantsBillingAward award) {

        return award.isSuspendInvoicingIndicator();
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardOrganizationIncomplete(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean isAwardOrganizationIncomplete(ContractsAndGrantsBillingAward award) {

        return false;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardPassedStopDate(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean isAwardPassedStopDate(ContractsAndGrantsBillingAward award) {

        Date today = dateTimeService.getCurrentSqlDateMidnight();
        Date stopDt = award.getAwardEndingDate();
        if (ObjectUtils.isNotNull(stopDt) && stopDt.before(today)) {
            return true;
        }
        return false;
    }

    /**
     * Check if Preferred Billing Frequency is set correctly.
     * 
     * @param award
     * @return False if preferred billing schedule is null, or set as perdetermined billing schedule or milestone billing schedule
     *         and award has no award account or more than 1 award accounts assigned.
     */
    @Override
    public boolean isPreferredBillingFrequencySetCorrectly(ContractsAndGrantsBillingAward award) {

        if (award.getPreferredBillingFrequency() == null || ((award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) || award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE)) && award.getActiveAwardAccounts().size() != 1)) {
            return false;
        }
        return true;
    }


    /**
     * Check if the value of PreferredBillingFrequency is in the BillingFrequency value set.
     * 
     * @param award
     * @return
     */
    @Override
    public boolean isValueOfPreferredBillingFrequencyValid(ContractsAndGrantsBillingAward award) {
        Boolean isValid = false;
        if (award.getPreferredBillingFrequency() != null) {
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(KFSPropertyConstants.ACTIVE, true);
            Collection<ContractsAndGrantsBillingFrequency> set = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingFrequency.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingFrequency.class, criteria);
            for (ContractsAndGrantsBillingFrequency billingFrequency : set) {
                if (award.getPreferredBillingFrequency().equalsIgnoreCase(billingFrequency.getFrequency())) {
                    isValid = true;
                    break;
                }
            }
        }

        return isValid;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getExpiredAccountsOfAward(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     *      Retrive all the expired accounts of an award
     */
    @Override
    public Collection<Account> getExpiredAccountsOfAward(ContractsAndGrantsBillingAward award) {

        Collection<ContractsAndGrantsBillingAwardAccount> awardAccounts = award.getActiveAwardAccounts();
        Collection<Account> expiredAwardAccounts = new ArrayList<Account>();

        if (awardAccounts != null && !awardAccounts.isEmpty()) {

            Date today = dateTimeService.getCurrentSqlDateMidnight();

            for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
                Account account = awardAccount.getAccount();

                if (account != null) {
                    Date expDt = account.getAccountExpirationDate();

                    if (expDt != null && expDt.before(today)) {
                        expiredAwardAccounts.add(account);
                    }
                }

            }

            return expiredAwardAccounts;
        }

        return null;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getContractControlAccounts(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public Collection<Account> getContractControlAccounts(ContractsAndGrantsBillingAward award) {

        if (!this.hasNoActiveAccountsAssigned(award)) {
            Collection<Account> controlAccounts = new ArrayList<Account>();
            for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                if (ObjectUtils.isNotNull(awardAccount.getAccount().getContractControlAccount())) {
                    controlAccounts.add(awardAccount.getAccount().getContractControlAccount());
                }
            }
            if (CollectionUtils.isEmpty(controlAccounts)) {
                return null;
            }
            else {
                return controlAccounts;
            }
        }
        return null;
    }


    /**
     * Sets the BusinessObjectService. Provides Spring compatibility.
     * 
     * @param businessObjectService
     */
    @Override
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    @Override
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    @Override
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardFinalInvoiceAlreadyBuilt(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean isAwardFinalInvoiceAlreadyBuilt(ContractsAndGrantsBillingAward award) {
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        ContractsAndGrantsBillingAwardAccount awardAccount;
        Iterator<ContractsAndGrantsBillingAwardAccount> iterator = award.getActiveAwardAccounts().iterator();
        while (iterator.hasNext()) {
            awardAccount = iterator.next();
            if (!awardAccount.isFinalBilledIndicator()) {
                awardAccounts.add(awardAccount);
            }
            if (CollectionUtils.isEmpty(awardAccounts)) {
                return true;
            }
        }

        return false;
    }


    /**
     * this method checks If all accounts of award has invoices in progress.
     * 
     * @param award
     * @return
     */
    @Override
    public boolean isInvoiceInProgress(ContractsAndGrantsBillingAward award) {

        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        ContractsAndGrantsBillingAwardAccount awardAccount;
        Iterator<ContractsAndGrantsBillingAwardAccount> iterator = award.getActiveAwardAccounts().iterator();
        while (iterator.hasNext()) {
            awardAccount = iterator.next();
            if (StringUtils.isBlank(awardAccount.getInvoiceDocumentStatus()) || awardAccount.getInvoiceDocumentStatus().equalsIgnoreCase(KewApiConstants.ROUTE_HEADER_FINAL_LABEL) || awardAccount.getInvoiceDocumentStatus().equalsIgnoreCase(KewApiConstants.ROUTE_HEADER_CANCEL_LABEL) || awardAccount.getInvoiceDocumentStatus().equalsIgnoreCase(KewApiConstants.ROUTE_HEADER_DISAPPROVED_LABEL)) {
                awardAccounts.add(awardAccount);
            }
            if (CollectionUtils.isEmpty(awardAccounts)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getActiveAwardsByCriteria(java.util.Map)
     */
    @Override
    public List<ContractsAndGrantsBillingAward> getActiveAwardsByCriteria(Map<String, Object> criteria) {

        return SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingAward.class, criteria);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoMilestonesToInvoice(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean hasNoMilestonesToInvoice(ContractsAndGrantsBillingAward award) {
        boolean isValid = false;
        if (award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
            List<Milestone> milestones = new ArrayList<Milestone>();
            List<Milestone> validMilestones = new ArrayList<Milestone>();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
            milestones = (List<Milestone>) SpringContext.getBean(BusinessObjectService.class).findMatching(Milestone.class, map);

            // To retrieve the previous period end Date to check for milestones and billing schedule.

            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            java.sql.Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            java.sql.Date invoiceDate = pair[1];


            for (Milestone awdMilestone : milestones) {
                if (awdMilestone.getMilestoneActualCompletionDate() != null && !invoiceDate.before(awdMilestone.getMilestoneActualCompletionDate()) && !awdMilestone.isBilledIndicator() && awdMilestone.getMilestoneAmount().isGreaterThan(KualiDecimal.ZERO)) {
                    validMilestones.add(awdMilestone);
                }
            }
            if (CollectionUtils.isEmpty(validMilestones)) {
                isValid = true;
            }
        }
        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoBillsToInvoice(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean hasNoBillsToInvoice(ContractsAndGrantsBillingAward award) {
        boolean isValid = false;
        if (award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {

            List<Bill> bills = new ArrayList<Bill>();
            List<Bill> validBills = new ArrayList<Bill>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());

            bills = (List<Bill>) SpringContext.getBean(BusinessObjectService.class).findMatching(Bill.class, map);
            // To retrieve the previous period end Date to check for milestones and billing schedule.

            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            java.sql.Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            java.sql.Date invoiceDate = pair[1];

            for (Bill awdBill : bills) {
                if (awdBill.getBillDate() != null && !invoiceDate.before(awdBill.getBillDate()) && !awdBill.isBilledIndicator() && awdBill.getEstimatedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                    validBills.add(awdBill);
                }
            }
            if (CollectionUtils.isEmpty(validBills)) {
                isValid = true;
            }
        }
        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#owningAgencyHasNoCustomerRecord(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean owningAgencyHasNoCustomerRecord(ContractsAndGrantsBillingAward award) {
        boolean isValid = true;
        CustomerService customerService = SpringContext.getBean(CustomerService.class);
        if (ObjectUtils.isNotNull(award.getAgency().getCustomerNumber())) {
            Customer customer = customerService.getByPrimaryKey(award.getAgency().getCustomerNumber());
            if (ObjectUtils.isNotNull(customer)) {
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getContractsGrantsInvoiceDocumentAppliedByPaymentApplicationNumber(java.lang.String)
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getContractsGrantsInvoiceDocumentAppliedByPaymentApplicationNumber(String paymentApplicationNumberCorrecting) {
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        try {
            PaymentApplicationDocument paymentApplicationDocument = (PaymentApplicationDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(paymentApplicationNumberCorrecting);
            for (InvoicePaidApplied invoicePaidApplied : paymentApplicationDocument.getInvoicePaidApplieds()) {
                cgInvoices.add((ContractsGrantsInvoiceDocument) invoicePaidApplied.getCustomerInvoiceDocument());
            }
            return cgInvoices;
        }
        catch (WorkflowException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * This method checks if the System Information and ORganization Accounting Default are setup for the Chart Code and Org Code
     * from the award accounts.
     * 
     * @param award
     * @return
     */
    @Override
    public boolean isChartAndOrgNotSetupForInvoicing(ContractsAndGrantsBillingAward award) {
        String coaCode = null, orgCode = null;
        String procCoaCode = null, procOrgCode = null;
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();

        Map<String, Object> criteria = new HashMap<String, Object>();
        Map<String, Object> sysCriteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        sysCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);


        // To get the chart code and org code for invoicing depending on the invoicing options.
        if (ObjectUtils.isNotNull(award.getInvoicingOptions())) {
            if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_ACCOUNT)) {
                for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    coaCode = awardAccount.getAccount().getChartOfAccountsCode();
                    orgCode = awardAccount.getAccount().getOrganizationCode();
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
                    // To retrieve processing codes based on billing codes using organization options
                    List<String> procCodes = getProcessingFromBillingCodes(coaCode, orgCode);
                    if (CollectionUtils.isEmpty(procCodes)) {
                        return true;
                    }
                    sysCriteria.put("processingChartOfAccountCode", procCodes.get(0));
                    sysCriteria.put("processingOrganizationCode", procCodes.get(1));
                    OrganizationAccountingDefault organizationAccountingDefault = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);

                    SystemInformation systemInformation = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, sysCriteria);
                    if (ObjectUtils.isNull(organizationAccountingDefault) || ObjectUtils.isNull(systemInformation)) {
                        return true;
                    }

                }
            }
            if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT)) {
                List<Account> controlAccounts = (List<Account>) getContractControlAccounts(award);

                for (Account controlAccount : controlAccounts) {
                    coaCode = controlAccount.getChartOfAccountsCode();
                    orgCode = controlAccount.getOrganizationCode();
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
                    // To retrieve processing codes based on billing codes using organization options
                    List<String> procCodes = getProcessingFromBillingCodes(coaCode, orgCode);
                    if (CollectionUtils.isEmpty(procCodes)) {
                        return true;
                    }
                    sysCriteria.put("processingChartOfAccountCode", procCodes.get(0));
                    sysCriteria.put("processingOrganizationCode", procCodes.get(1));
                    OrganizationAccountingDefault organizationAccountingDefault = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);

                    SystemInformation systemInformation = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, sysCriteria);
                    if (ObjectUtils.isNull(organizationAccountingDefault) || ObjectUtils.isNull(systemInformation)) {
                        return true;
                    }
                }
            }
            if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_AWARD)) {
                List<Account> controlAccounts = (List<Account>) getContractControlAccounts(award);

                for (Account controlAccount : controlAccounts) {
                    coaCode = controlAccount.getChartOfAccountsCode();
                    orgCode = controlAccount.getOrganizationCode();
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
                    // To retrieve processing codes based on billing codes using organization options
                    List<String> procCodes = getProcessingFromBillingCodes(coaCode, orgCode);
                    if (CollectionUtils.isEmpty(procCodes)) {
                        return true;
                    }
                    sysCriteria.put("processingChartOfAccountCode", procCodes.get(0));
                    sysCriteria.put("processingOrganizationCode", procCodes.get(1));
                    OrganizationAccountingDefault organizationAccountingDefault = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);

                    SystemInformation systemInformation = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, sysCriteria);
                    if (ObjectUtils.isNull(organizationAccountingDefault) || ObjectUtils.isNull(systemInformation)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * To retrieve processing chart code and org code from the billing chart code and org code
     * 
     * @param cgInvoiceDocument
     * @param billingChartOfAccountsCode
     * @param billingOrganizationCode
     * @return
     */
    @Override
    public List<String> getProcessingFromBillingCodes(String billingChartCode, String billingOrgCode) {

        List<String> procCodes = new ArrayList<String>();
        // To access Organization Options to find the billing values based on procesing codes
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, billingChartCode);
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, billingOrgCode);
        OrganizationOptions organizationOptions = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        if (ObjectUtils.isNotNull(organizationOptions)) {
            procCodes.add(0, organizationOptions.getProcessingChartOfAccountCode());
            procCodes.add(1, organizationOptions.getProcessingOrganizationCode());
        }

        return procCodes;
    }

    /**
     * This method checks if the Offset Definition is setup for the Chart Code from the award accounts.
     * 
     * @param award
     * @return
     */
    @Override
    public boolean isOffsetDefNotSetupForInvoicing(ContractsAndGrantsBillingAward award) {
        String coaCode = null, orgCode = null;
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = receivableOffsetOption.equals("3");
        // This condition is validated only if GLPE is 3 and CG enhancements is ON
        if (isUsingReceivableFAU) {
            Map<String, Object> criteria = new HashMap<String, Object>();
            Map<String, Object> sysCriteria = new HashMap<String, Object>();
            criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
            criteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, ArPropertyConstants.ACTUAL_BALANCE_TYPE);
            criteria.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, ArConstants.CGIN_DOCUMENT_TYPE);
            // 1. To get the chart code and org code for invoicing depending on the invoicing options.
            if (ObjectUtils.isNotNull(award.getInvoicingOptions())) {
                if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_ACCOUNT)) {
                    for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                        coaCode = awardAccount.getAccount().getChartOfAccountsCode();
                        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                        OffsetDefinition offset = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OffsetDefinition.class, criteria);
                        if (ObjectUtils.isNull(offset)) {
                            return true;
                        }
                    }
                }
                if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT)) {
                    List<Account> controlAccounts = (List<Account>) getContractControlAccounts(award);

                    for (Account controlAccount : controlAccounts) {
                        coaCode = controlAccount.getChartOfAccountsCode();
                        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                        OffsetDefinition offset = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OffsetDefinition.class, criteria);
                        if (ObjectUtils.isNull(offset)) {
                            return true;
                        }
                    }
                }
                if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_AWARD)) {
                    List<Account> controlAccounts = (List<Account>) getContractControlAccounts(award);

                    for (Account controlAccount : controlAccounts) {
                        coaCode = controlAccount.getChartOfAccountsCode();
                        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                        OffsetDefinition offset = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OffsetDefinition.class, criteria);
                        if (ObjectUtils.isNull(offset)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Gets the accountingPeriodService attribute.
     * 
     * @return Returns the accountingPeriodService.
     */
    public AccountingPeriodService getAccountingPeriodService() {
        return accountingPeriodService;
    }

    /**
     * Sets the accountingPeriodService attribute value.
     * 
     * @param accountingPeriodService The accountingPeriodService to set.
     */
    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    /**
     * Gets the verifyBillingFrequencyService attribute.
     * 
     * @return Returns the verifyBillingFrequencyService.
     */
    public VerifyBillingFrequencyService getVerifyBillingFrequencyService() {
        return verifyBillingFrequencyService;
    }

    /**
     * Sets the verifyBillingFrequencyService attribute value.
     * 
     * @param verifyBillingFrequencyService The verifyBillingFrequencyService to set.
     */
    public void setVerifyBillingFrequencyServuce(VerifyBillingFrequencyService verifyBillingFrequencyService) {
        this.verifyBillingFrequencyService = verifyBillingFrequencyService;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getMilestonesBilledToDate(java.lang.Long)
     */
    @Override
    public KualiDecimal getMilestonesBilledToDate(Long proposalNumber) {
        Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
        totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        KualiDecimal billedToDate = KualiDecimal.ZERO;

        List<Milestone> milestones = (List<Milestone>) SpringContext.getBean(BusinessObjectService.class).findMatching(Milestone.class, totalBilledKeys);
        if (CollectionUtils.isNotEmpty(milestones)) {
            Iterator<Milestone> iterator = milestones.iterator();
            while (iterator.hasNext()) {
                Milestone milestone = iterator.next();
                if (milestone.isBilledIndicator()) {
                    billedToDate = billedToDate.add(milestone.getMilestoneAmount());
                }
            }
        }
        return billedToDate;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getPredeterminedBillingBilledToDate(java.lang.Long)
     */
    @Override
    public KualiDecimal getPredeterminedBillingBilledToDate(Long proposalNumber) {
        Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
        totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        KualiDecimal billedToDate = KualiDecimal.ZERO;

        List<Bill> bills = (List<Bill>) SpringContext.getBean(BusinessObjectService.class).findMatching(Bill.class, totalBilledKeys);
        if (CollectionUtils.isNotEmpty(bills)) {
            Iterator<Bill> iterator = bills.iterator();
            while (iterator.hasNext()) {
                Bill bill = iterator.next();
                if (bill.isBilledIndicator()) {
                    billedToDate = billedToDate.add(bill.getEstimatedAmount());
                }
            }
        }
        return billedToDate;
    }


    /**
     * This method checks if there is atleast one AR Invoice Account present when the GLPE is 3.
     * 
     * @param award
     * @return
     */
    @Override
    public boolean hasARInvoiceAccountAssigned(ContractsAndGrantsBillingAward award) {
        boolean isValid = true;
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = receivableOffsetOption.equals("3");
        // This condition is validated only if GLPE is 3 and CG enhancements is ON
        if (isUsingReceivableFAU) {
            if (ObjectUtils.isNull(award.getActiveAwardInvoiceAccounts()) || CollectionUtils.isEmpty(award.getActiveAwardInvoiceAccounts())) {
                isValid = false;
            }
            else {
                int arCount = 0;
                for (ContractsGrantsAwardInvoiceAccountInformation awardInvoiceAccount : award.getActiveAwardInvoiceAccounts()) {
                    if (awardInvoiceAccount.getAccountType().equals(ArPropertyConstants.AR_ACCOUNT)) {
                        arCount++;

                    }
                }
                if (arCount == 0) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }


    @Override
    public Collection<DunningLetterDistributionOnDemandLookupResult> getInvoiceDocumentsForDunningLetterOnDemandLookup(Map<String, String> fieldValues) {

        // to get the search criteria
        String proposalNumber = fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String customerNumber = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NUMBER);
        String invoiceDocumentNumber = fieldValues.get("invoiceDocumentNumber");
        String awardTotal = fieldValues.get("awardTotal");
        String accountNumber = fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);


        Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments;
        Criteria criteria = new Criteria();
        if (ObjectUtils.isNotNull(proposalNumber) && StringUtils.isNotBlank(proposalNumber.toString()) && StringUtils.isNotEmpty(proposalNumber.toString())) {
            criteria.addEqualTo(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }
        if (ObjectUtils.isNotNull(customerNumber) && StringUtils.isNotBlank(customerNumber) && StringUtils.isNotEmpty(customerNumber)) {
            criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        }
        if (ObjectUtils.isNotNull(invoiceDocumentNumber) && StringUtils.isNotBlank(invoiceDocumentNumber) && StringUtils.isNotEmpty(invoiceDocumentNumber)) {
            criteria.addEqualTo("documentNumber", invoiceDocumentNumber);
        }
        if (ObjectUtils.isNotNull(awardTotal) && StringUtils.isNotBlank(awardTotal) && StringUtils.isNotEmpty(awardTotal)) {
            criteria.addEqualTo("invoiceGeneralDetail.awardTotal", awardTotal);
        }
        if (ObjectUtils.isNotNull(accountNumber) && StringUtils.isNotBlank(accountNumber) && StringUtils.isNotEmpty(accountNumber)) {
            criteria.addIn("accountDetails.accountNumber", Arrays.asList(new String[] { accountNumber }));
        }
        criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, "true");
        criteria.addEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);


        cgInvoiceDocuments = retrieveAllCGInvoicesByCriteria(criteria);


        // attach headers
        cgInvoiceDocuments = attachWorkflowHeadersToCGInvoices(cgInvoiceDocuments);

        // To validate the invoices for any additional parameters.

        Collection<ContractsGrantsInvoiceDocument> eligibleInvoiceDocuments = validateInvoicesForDunningLetters(fieldValues, cgInvoiceDocuments);


        return DunningLetterDistributionOnDemandLookupUtil.getPopulatedDunningLetterDistributionOnDemandLookupResults(eligibleInvoiceDocuments);
    }


    private Collection<ContractsGrantsInvoiceDocument> validateInvoicesForDunningLetters(Map<String, String> fieldValues, Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments) {
        Integer agingBucketStartValue = null;
        Integer agingBucketEndValue = null;
        Integer cutoffdate0 = 0;
        Integer cutoffdate30 = 30;
        Integer cutoffdate60 = 60;
        Integer cutoffdate90 = 90;
        Integer cutoffdate120 = 120;

        // To get value for FINAL days past due.
        String stateAgencyFinalCutOffDate = null;
        String finalCutOffDate = SpringContext.getBean(ParameterService.class).getParameterValueAsString(DunningCampaign.class, ArConstants.DunningLetters.DYS_PST_DUE_FINAL_PARM);
        if (ObjectUtils.isNull(finalCutOffDate)) {
            finalCutOffDate = "0";
        }
        Integer cutoffdateFinal = new Integer(finalCutOffDate);
        String agencyNumber = fieldValues.get(KFSPropertyConstants.AGENCY_NUMBER);
        String campaignID = fieldValues.get("campaignID");
        String collector = fieldValues.get("principalId");
        String agingBucket = fieldValues.get("agingBucket");
        String collectorPrincName = fieldValues.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);

        boolean checkAgingBucket = ObjectUtils.isNotNull(agingBucket) && StringUtils.isNotBlank(agingBucket) && StringUtils.isNotEmpty(agingBucket);


        if (checkAgingBucket && agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_CURRENT)) {
            agingBucketStartValue = 0;
            agingBucketEndValue = 30;
        }
        // Including State agency final here just to get some default value in place. The value will be overriden later after
        // checking with the agency.
        else if (checkAgingBucket && (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_FINAL) || agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL))) {
            agingBucketStartValue = cutoffdateFinal + 1;
            agingBucketEndValue = 0;
        }
        else if (checkAgingBucket && agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)) {
            agingBucketStartValue = 121;
            agingBucketEndValue = cutoffdateFinal;
        }
        else if (checkAgingBucket && StringUtils.isNotBlank(agingBucket)) {
            agingBucketStartValue = new Integer(agingBucket.split("-")[0]);
            agingBucketEndValue = new Integer(agingBucket.split("-")[1]);
        }

        // check other categories
        boolean checkAgencyNumber = ObjectUtils.isNotNull(agencyNumber) && StringUtils.isNotBlank(agencyNumber) && StringUtils.isNotEmpty(agencyNumber);

        boolean checkDunningCampaign = ObjectUtils.isNotNull(campaignID) && StringUtils.isNotBlank(campaignID) && StringUtils.isNotEmpty(campaignID);
        boolean checkCollector = ObjectUtils.isNotNull(collector) && StringUtils.isNotBlank(collector) && StringUtils.isNotEmpty(collector);
        boolean isCollector = true;

        if (ObjectUtils.isNotNull(collectorPrincName) && StringUtils.isNotEmpty(collectorPrincName.trim())) {
            checkCollector = true;
            Person collectorObj = getPersonService().getPersonByPrincipalName(collectorPrincName);
            if (collectorObj != null) {
                collector = collectorObj.getPrincipalId();
            }
            else {
                isCollector = false;
            }
        }

        // // Here add logic to check if selected collector exists as collector head or collector information.
        // if(isCollector && checkCollector) {
        // isCollector = collectorHierarchyDao.isCollector(collector);
        // }

        // walk through what we have, and do any extra filtering based on age and dunning campaign, if necessary
        boolean eligibleInvoiceFlag;
        Collection<ContractsGrantsInvoiceDocument> eligibleInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        for (ContractsGrantsInvoiceDocument invoice : cgInvoiceDocuments) {
            eligibleInvoiceFlag = false;
            if (ObjectUtils.isNotNull(invoice.getAge())) {
                if (invoice.getAward() == null || invoice.getAward().getDunningCampaign() == null) {
                    eligibleInvoiceFlag = false;
                    continue;
                }
                String dunningCampaignCode = invoice.getAward().getDunningCampaign();

                DunningCampaign dunningCampaign = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(DunningCampaign.class, dunningCampaignCode);
                if (ObjectUtils.isNull(dunningCampaign) || !dunningCampaign.isActive()) {
                    eligibleInvoiceFlag = false;
                    continue;
                }
                // if (checkCollector) {
                // if(isCollector) {
                // if ((invoice.getCustomer().getCustomerCollector() == null ||
                // invoice.getCustomer().getCustomerCollector().getPrincipalId() == null ||
                // !invoice.getCustomer().getCustomerCollector().getPrincipalId().equalsIgnoreCase(collector))) {
                //
                // // chk if customer collector is assigned to head
                // Criteria collectorCriteria = new Criteria();
                // collectorCriteria.addEqualTo(ArPropertyConstants.COLLECTOR_HEAD, collector);
                // collectorCriteria.addEqualTo(KFSPropertyConstants.ACTIVE, true);
                //
                // Collection<CollectorHierarchy> collectorHierarchies =
                // collectorHierarchyDao.getCollectorHierarchyByCriteria(collectorCriteria);
                // if (ObjectUtils.isNotNull(collectorHierarchies) && !collectorHierarchies.isEmpty()) {
                // CollectorHierarchy collectorHead = new ArrayList<CollectorHierarchy>(collectorHierarchies).get(0);
                // if (ObjectUtils.isNotNull(collectorHead)) {
                // // check principal ids of collector
                // if (ObjectUtils.isNotNull(collectorHead.getCollectorInformations()) &&
                // CollectionUtils.isNotEmpty(collectorHead.getCollectorInformations())) {
                // for (CollectorInformation collectorInfo : collectorHead.getCollectorInformations()) {
                // if ((collectorInfo.isActive() && ObjectUtils.isNotNull(collectorInfo.getPrincipalId()) &&
                // ObjectUtils.isNotNull(invoice.getCustomer().getCustomerCollector()) &&
                // collectorInfo.getPrincipalId().equalsIgnoreCase(invoice.getCustomer().getCustomerCollector().getPrincipalId())))
                // {
                // eligibleInvoiceFlag = true;
                // break;
                // } else {
                // eligibleInvoiceFlag = false;
                // }
                // }
                // }
                // }
                // }
                // if(!eligibleInvoiceFlag) {
                // continue;
                // }
                // }
                // } else {
                // eligibleInvoiceFlag = false;
                // continue;
                // }
                // }
                if (checkAgencyNumber && ((invoice.getAward().getAgencyNumber() == null || !invoice.getAward().getAgencyNumber().equals(agencyNumber)))) {
                    eligibleInvoiceFlag = false;
                    continue;
                }
                if (checkDunningCampaign && ((invoice.getAward().getDunningCampaign() == null || !invoice.getAward().getDunningCampaign().equals(campaignID)))) {
                    eligibleInvoiceFlag = false;
                    continue;
                }

                // To override agingBucketStartValue and agingBucketEndValue if State Agency Final is true.

                ContractsAndGrantsBillingAgency agency = invoice.getAward().getAgency();
                if (agency.isStateAgencyIndicator()) {
                    stateAgencyFinalCutOffDate = SpringContext.getBean(ParameterService.class).getParameterValueAsString(DunningCampaign.class, ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL_PARM);
                }
                if (ObjectUtils.isNotNull(stateAgencyFinalCutOffDate) && agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {

                    agingBucketStartValue = new Integer(stateAgencyFinalCutOffDate) + 1;
                    agingBucketEndValue = 0;
                }
                else if (ObjectUtils.isNotNull(stateAgencyFinalCutOffDate) && agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)) {

                    agingBucketStartValue = 121;
                    agingBucketEndValue = new Integer(stateAgencyFinalCutOffDate);
                }
                // Now to validate based on agingbucket and make sure the agency = stateagency is applied.
                if (ObjectUtils.isNotNull(agingBucketStartValue) && ObjectUtils.isNotNull(agingBucketStartValue)) {
                    if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_FINAL)) {
                        if (agency.isStateAgencyIndicator()) {
                            eligibleInvoiceFlag = false;
                            continue;
                        }
                        else {
                            if ((invoice.getAge().compareTo(agingBucketStartValue) < 0)) {
                                eligibleInvoiceFlag = false;
                                continue;
                            }
                        }
                    }
                    else if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {
                        if (!agency.isStateAgencyIndicator()) {
                            eligibleInvoiceFlag = false;
                            continue;
                        }
                        else {
                            if ((invoice.getAge().compareTo(agingBucketStartValue) < 0)) {
                                eligibleInvoiceFlag = false;
                                continue;
                            }
                        }
                    }
                    else {
                        if ((invoice.getAge().compareTo(agingBucketStartValue) < 0) || (invoice.getAge().compareTo(agingBucketEndValue) > 0)) {
                            eligibleInvoiceFlag = false;
                            continue;
                        }
                    }
                }

                List<DunningLetterDistribution> dunningLetterDistributions = dunningCampaign.getDunningLetterDistributions();
                if (dunningLetterDistributions.isEmpty()) {
                    eligibleInvoiceFlag = false;
                    continue;
                }
                for (DunningLetterDistribution dunningLetterDistribution : dunningLetterDistributions) {

                    DunningLetterTemplate dunningLetterTemplate = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(DunningLetterTemplate.class, dunningLetterDistribution.getDunningLetterTemplate());

                    if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_CURRENT)) {
                        if ((invoice.getAge().compareTo(cutoffdate0) >= 0) && (invoice.getAge().compareTo(cutoffdate30) <= 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }
                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_31_60)) {
                        if ((invoice.getAge().compareTo(cutoffdate30) > 0) && (invoice.getAge().compareTo(cutoffdate60) <= 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }
                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_61_90)) {
                        if ((invoice.getAge().compareTo(cutoffdate60) > 0) && (invoice.getAge().compareTo(cutoffdate90) <= 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }
                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_91_120)) {
                        if ((invoice.getAge().compareTo(cutoffdate90) > 0) && (invoice.getAge().compareTo(cutoffdate120) <= 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }
                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)) {
                        if (agency.isStateAgencyIndicator()) {// To replace final with state agency final value
                            cutoffdateFinal = new Integer(stateAgencyFinalCutOffDate);
                        }
                        if ((invoice.getAge().compareTo(cutoffdate120) > 0) && (invoice.getAge().compareTo(cutoffdateFinal) <= 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }

                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_FINAL)) {
                        if (agency.isStateAgencyIndicator()) {// to proceed only if agency is not state agency
                            continue;
                        }
                        else {
                            if ((invoice.getAge().compareTo(cutoffdateFinal) > 0)) {
                                if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())) {
                                    eligibleInvoiceFlag = true;
                                    invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                    break;
                                }
                            }
                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {
                        if (agency.isStateAgencyIndicator()) {// to replace final with state agency final value
                            cutoffdateFinal = new Integer(stateAgencyFinalCutOffDate);
                        }
                        else {// If the agency is not state agency - nothing to calculate.
                            continue;
                        }
                        if ((invoice.getAge().compareTo(cutoffdateFinal) > 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }
                        }
                    }
                }
            }
            else {
                eligibleInvoiceFlag = false;
            }

            if (eligibleInvoiceFlag) {
                SpringContext.getBean(BusinessObjectService.class).save(invoice.getInvoiceGeneralDetail());
                eligibleInvoices.add(invoice);
            }
        }
        return eligibleInvoices;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#attachWorkflowHeadersToCGInvoices(java.util.Collection)
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> attachWorkflowHeadersToCGInvoices(Collection<ContractsGrantsInvoiceDocument> invoices) {
        List<ContractsGrantsInvoiceDocument> docs = new ArrayList<ContractsGrantsInvoiceDocument>();
        if (invoices == null || invoices.isEmpty()) {
            return docs;
        }

        // make a list of necessary workflow docs to retrieve
        List<String> documentHeaderIds = new ArrayList<String>();
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            documentHeaderIds.add(invoice.getDocumentNumber());
        }

        // get all of our docs with full workflow headers
        try {
            for (Document doc : documentService.getDocumentsByListOfDocumentHeaderIds(ContractsGrantsInvoiceDocument.class, documentHeaderIds)) {
                docs.add((ContractsGrantsInvoiceDocument) doc);
            }
        }
        catch (WorkflowException e) {
            throw new InfrastructureException("Unable to retrieve ContractsGrants Invoice Documents", e);
        }

        return docs;
    }

    /**
     * This method retrieves all invoices with open and with final status based on proposal number
     * 
     * @param proposalNumber
     * @param outputFileStream
     * @return
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByProposalNumber(Long proposalNumber, String errorFileName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        criteria.addIsNull(ArPropertyConstants.DOCUMENT_HEADER_FINANCIAL_DOCUMENT_IN_ERROR_NUMBER);

        Collection<String> invoiceNumbers = contractsGrantsInvoiceDocumentDao.getFinancialDocumentInErrorNumbers();
        if (ObjectUtils.isNotNull(invoiceNumbers) && CollectionUtils.isNotEmpty(invoiceNumbers)) {
            criteria.addNotIn(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, invoiceNumbers);
        }

        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        cgInvoices = contractsGrantsInvoiceDocumentDao.getInvoicesByCriteria(criteria);
        String detail = "Proposal Number#" + proposalNumber;
        return cgInvoices;
    }

    @Override
    public KualiDecimal retrievePaymentAmountByDocumentNumber(String documentNumber) {
        KualiDecimal paymentAmount = KualiDecimal.ZERO;
        Collection<InvoicePaidApplied> invoicePaidApplieds = invoicePaidAppliedService.getInvoicePaidAppliedsForInvoice(documentNumber);
        if (invoicePaidApplieds != null && !invoicePaidApplieds.isEmpty()) {
            for (InvoicePaidApplied invPaidApp : invoicePaidApplieds) {
                paymentAmount = paymentAmount.add(invPaidApp.getInvoiceItemAppliedAmount());
            }
        }
        return paymentAmount;
    }

    @Override
    public java.sql.Date retrievePaymentDateByDocumentNumber(String documentNumber) {
        List<InvoicePaidApplied> invoicePaidApplieds = (List<InvoicePaidApplied>) invoicePaidAppliedService.getInvoicePaidAppliedsForInvoice(documentNumber);
        java.sql.Date paymentDate = null;
        if (invoicePaidApplieds != null && !invoicePaidApplieds.isEmpty()) {
            InvoicePaidApplied invPaidApp = invoicePaidApplieds.get(invoicePaidApplieds.size() - 1);
            PaymentApplicationDocument referenceFinancialDocument;
            try {
                referenceFinancialDocument = (PaymentApplicationDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(invPaidApp.getDocumentNumber());
                paymentDate = referenceFinancialDocument.getFinancialSystemDocumentHeader().getDocumentFinalDate();
            }
            catch (WorkflowException ex) {
                LOG.error("Could not retrieve payment application document while calculating payment date: " + ex.getMessage());
            }
        }
        return paymentDate;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getInvoiceDocumentsForReferralToCollectionsLookup(java.util.Map)
     */
    @Override
    public Collection<ReferralToCollectionsLookupResult> getInvoiceDocumentsForReferralToCollectionsLookup(Map<String, String> fieldValues) {

        String agencyNumber = fieldValues.get(KFSPropertyConstants.AGENCY_NUMBER);
        String accountNumber = fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        String proposalNumber = fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String invoiceDocumentNumber = fieldValues.get(ArPropertyConstants.ReferralToCollectionsFields.INVOICE_DOCUMENT_NUMBER);
        String awardDocumentNumber = fieldValues.get(ArPropertyConstants.ReferralToCollectionsFields.AWARD_DOCUMENT_NUMBER);
        String customerNumber = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NUMBER);
        String customerName = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NAME);

        Collection<ContractsGrantsInvoiceDocument> invoices;
        Criteria criteria = new Criteria();
        // if(ObjectUtils.isNotNull(agencyNumber)&& StringUtils.isNotBlank(agencyNumber.toString()) &&
        // StringUtils.isNotEmpty(agencyNumber.toString())){
        // criteria.addIn(ArPropertyConstants.ReferralToCollectionsFields.AGENCY_ADDRESS_DETAILS_AGENCY_NUMBER, Arrays.asList(new
        // String[] {agencyNumber}));
        // }
        if (ObjectUtils.isNotNull(proposalNumber) && StringUtils.isNotBlank(proposalNumber.toString()) && StringUtils.isNotEmpty(proposalNumber.toString())) {
            criteria.addEqualTo(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }
        if (ObjectUtils.isNotNull(accountNumber) && StringUtils.isNotBlank(accountNumber.toString()) && StringUtils.isNotEmpty(accountNumber.toString())) {
            criteria.addIn(ArPropertyConstants.ReferralToCollectionsFields.ACCOUNT_DETAILS_ACCOUNT_NUMBER, Arrays.asList(new String[] { accountNumber }));
        }
        if (ObjectUtils.isNotNull(invoiceDocumentNumber) && StringUtils.isNotBlank(invoiceDocumentNumber) && StringUtils.isNotEmpty(invoiceDocumentNumber)) {
            criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, invoiceDocumentNumber);
        }
        if (ObjectUtils.isNotNull(customerNumber) && StringUtils.isNotBlank(customerNumber) && StringUtils.isNotEmpty(customerNumber)) {
            criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        }
        if (ObjectUtils.isNotNull(customerName) && StringUtils.isNotBlank(customerName) && StringUtils.isNotEmpty(customerName)) {
            criteria.addEqualTo(ArPropertyConstants.ReferralToCollectionsFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, customerName);
        }

        criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, "true");
        criteria.addEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);

        Criteria referralNull = new Criteria();
        Criteria referralOutside = new Criteria();
        Criteria subCriteria = new Criteria();

        Map<String, String> refFieldValues = new HashMap<String, String>();
        refFieldValues.put(ArPropertyConstants.ReferralTypeFields.OUTSIDE_COLLECTION_AGENCY_IND, "true");
        refFieldValues.put(ArPropertyConstants.ReferralTypeFields.ACTIVE, "true");
        List<ReferralType> refTypes = (List<ReferralType>) SpringContext.getBean(BusinessObjectService.class).findMatching(ReferralType.class, refFieldValues);
        String outsideColAgencyCode = CollectionUtils.isNotEmpty(refTypes) ? refTypes.get(0).getReferralTypeCode() : null;

        referralNull.addIsNull(ArPropertyConstants.ReferralToCollectionsFields.REFERRAL_TYPE);
        referralOutside.addNotEqualTo(ArPropertyConstants.ReferralToCollectionsFields.REFERRAL_TYPE, outsideColAgencyCode);

        subCriteria.addOrCriteria(referralNull);
        subCriteria.addOrCriteria(referralOutside);

        criteria.addAndCriteria(subCriteria);

        invoices = this.retrieveAllCGInvoicesByCriteria(criteria);

        if ((ObjectUtils.isNotNull(awardDocumentNumber) && StringUtils.isNotBlank(awardDocumentNumber) && StringUtils.isNotEmpty(awardDocumentNumber)) || ObjectUtils.isNotNull(agencyNumber) && StringUtils.isNotBlank(agencyNumber.toString()) && StringUtils.isNotEmpty(agencyNumber.toString())) {
            this.filterInvoicesByAwardDocumentNumber(invoices, agencyNumber, awardDocumentNumber);
        }

        return ReferralToCollectionsDocumentUtil.getPopulatedReferralToCollectionsLookupResults(invoices);
    }

    /**
     * removes the invoices from list which does not match the given award document number.
     * 
     * @param invoices list of invoices.
     * @param awardDocumentNumber award document number to filter invoices.
     */
    private void filterInvoicesByAwardDocumentNumber(Collection<ContractsGrantsInvoiceDocument> invoices, String agencyNumber, String awardDocumentNumber) {
        boolean checkAwardNumber = ObjectUtils.isNotNull(awardDocumentNumber) && StringUtils.isNotBlank(awardDocumentNumber) && StringUtils.isNotEmpty(awardDocumentNumber);
        boolean checkAgencyNumber = ObjectUtils.isNotNull(agencyNumber) && StringUtils.isNotBlank(agencyNumber.toString()) && StringUtils.isNotEmpty(agencyNumber.toString());
        if (invoices != null && !invoices.isEmpty()) {
            Iterator<ContractsGrantsInvoiceDocument> itr = invoices.iterator();
            while (itr.hasNext()) {
                ContractsGrantsInvoiceDocument invoice = itr.next();
                if (invoice.getAward() == null || (checkAwardNumber && (invoice.getAward().getAwardDocumentNumber() == null || !invoice.getAward().getAwardDocumentNumber().equals(awardDocumentNumber))) || (checkAgencyNumber && (invoice.getAward().getAgencyNumber() == null || !invoice.getAward().getAgencyNumber().equals(agencyNumber)))) {
                    itr.remove();
                }
            }
        }
    }


    /**
     * This method generates the attached invoices for the agency addresses in the Contracts and Grants Invoice Document.
     */
    @Override
    public void generateInvoicesForAgencyAddresses(ContractsGrantsInvoiceDocument document) {
        InvoiceTemplate invoiceTemplate = null;
        Iterator<InvoiceAgencyAddressDetail> iterator = document.getAgencyAddressDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAgencyAddressDetail invoiceAgencyAddressDetail = iterator.next();
            byte[] reportStream;
            byte[] copyReportStream;
            // validating the invoice template
            if (ObjectUtils.isNotNull(invoiceAgencyAddressDetail.getPreferredAgencyInvoiceTemplateCode())) {
                invoiceTemplate = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(InvoiceTemplate.class, invoiceAgencyAddressDetail.getPreferredAgencyInvoiceTemplateCode());
            }
            else if (ObjectUtils.isNotNull(invoiceAgencyAddressDetail.getAgencyInvoiceTemplateCode())) {
                invoiceTemplate = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(InvoiceTemplate.class, invoiceAgencyAddressDetail.getAgencyInvoiceTemplateCode());
            }
            else {
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_FILE_UPLOAD_NO_PDF_FILE_SELECTED_FOR_SAVE, ArConstants.ACTIVE_INVOICE_TEMPLATE_ERROR);
            }

            // generate invoices from templates.
            if (ObjectUtils.isNotNull(invoiceTemplate) && invoiceTemplate.isActive() && ObjectUtils.isNotNull(invoiceTemplate.getFilepath())) {
                File templateFile = new File(invoiceTemplate.getFilepath());
                File outputDirectory = null;
                String outputFileName;
                try {
                    // genrating original invoice
                    outputFileName = document.getDocumentNumber() + "_" + invoiceAgencyAddressDetail.getAgencyAddressName() + FILE_NAME_TIMESTAMP.format(new Date()) + ArConstants.TemplateUploadSystem.EXTENSION;
                    Map<String, String> replacementList = getTemplateParameterList(document);
                    ContractsAndGrantsAgencyAddress address;
                    Map<String, Object> primaryKeys = new HashMap<String, Object>();
                    primaryKeys.put(KFSPropertyConstants.AGENCY_NUMBER, invoiceAgencyAddressDetail.getAgencyNumber());
                    primaryKeys.put("agencyAddressIdentifier", invoiceAgencyAddressDetail.getAgencyAddressIdentifier());
                    address = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObject(ContractsAndGrantsAgencyAddress.class, primaryKeys);
                    String fullAddress = "";
                    if (StringUtils.isNotEmpty(address.getAgencyLine1StreetAddress())) {
                        fullAddress += returnProperStringValue(address.getAgencyLine1StreetAddress()) + "\n";
                    }
                    if (StringUtils.isNotEmpty(address.getAgencyLine2StreetAddress())) {
                        fullAddress += returnProperStringValue(address.getAgencyLine2StreetAddress()) + "\n";
                    }
                    if (StringUtils.isNotEmpty(address.getAgencyCityName())) {
                        fullAddress += returnProperStringValue(address.getAgencyCityName());
                    }
                    if (StringUtils.isNotEmpty(address.getAgencyStateCode())) {
                        fullAddress += " " + returnProperStringValue(address.getAgencyStateCode());
                    }
                    if (StringUtils.isNotEmpty(address.getAgencyZipCode())) {
                        fullAddress += "-" + returnProperStringValue(address.getAgencyZipCode());
                    }
                    replacementList.put("#agency.fullAddress", returnProperStringValue(fullAddress));
                    reportStream = PdfFormFillerUtil.populateTemplate(templateFile, replacementList, "");
                    // creating and saving the original note with an attachment
                    if (ObjectUtils.isNotNull(document.getInvoiceGeneralDetail()) && document.getInvoiceGeneralDetail().isFinalBillIndicator()) {
                        reportStream = PdfFormFillerUtil.createFinalmarkOnFile(reportStream, "FINAL INVOICE");
                    }
                    Note note = new Note();
                    note.setNotePostedTimestampToCurrent();
                    note.setNoteText("Auto-generated invoice for Agency Address-" + document.getDocumentNumber() + "-" + invoiceAgencyAddressDetail.getAgencyAddressName());
                    note.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                    note = SpringContext.getBean(NoteService.class).createNote(note, document, null);
                    Attachment attachment = SpringContext.getBean(AttachmentService.class).createAttachment(note, outputFileName, ArConstants.TemplateUploadSystem.TEMPLATE_MIME_TYPE, reportStream.length, new ByteArrayInputStream(reportStream), "");
                    // adding attachment to the note
                    note.setAttachment(attachment);
                    SpringContext.getBean(NoteService.class).save(note);
                    attachment.setNoteIdentifier(note.getNoteIdentifier());
                    SpringContext.getBean(BusinessObjectService.class).save(attachment);
                    document.addNote(note);

                    // generating Copy invoice
                    outputFileName = document.getDocumentNumber() + "_" + invoiceAgencyAddressDetail.getAgencyAddressName() + FILE_NAME_TIMESTAMP.format(new Date()) + "_COPY" + ArConstants.TemplateUploadSystem.EXTENSION;
                    copyReportStream = PdfFormFillerUtil.createWatermarkOnFile(reportStream, "COPY");
                    // creating and saving the copy note with an attachment
                    Note copyNote = new Note();
                    copyNote.setNotePostedTimestampToCurrent();
                    copyNote.setNoteText("Auto-generated invoice (Copy) for Agency Address-" + document.getDocumentNumber() + "-" + invoiceAgencyAddressDetail.getAgencyAddressName());
                    copyNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                    copyNote = SpringContext.getBean(NoteService.class).createNote(copyNote, document, null);
                    Attachment copyAttachment = SpringContext.getBean(AttachmentService.class).createAttachment(copyNote, outputFileName, ArConstants.TemplateUploadSystem.TEMPLATE_MIME_TYPE, copyReportStream.length, new ByteArrayInputStream(copyReportStream), "");
                    // adding attachment to the note
                    copyNote.setAttachment(copyAttachment);
                    SpringContext.getBean(NoteService.class).save(copyNote);
                    copyAttachment.setNoteIdentifier(copyNote.getNoteIdentifier());
                    SpringContext.getBean(BusinessObjectService.class).save(copyAttachment);
                    document.addNote(copyNote);
                    invoiceAgencyAddressDetail.setNoteId(note.getNoteIdentifier());
                    // saving the note to the document header
                    SpringContext.getBean(DocumentService.class).updateDocument(document);
                }
                catch (IOException ex) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_FILE_UPLOAD_NO_PDF_FILE_SELECTED_FOR_SAVE, ArConstants.INVOICE_TEMPLATE_NOT_FOUND_ERROR + invoiceTemplate.getInvoiceTemplateCode() + ".");
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_FILE_UPLOAD_NO_PDF_FILE_SELECTED_FOR_SAVE, ArConstants.ACTIVE_INVOICE_TEMPLATE_ERROR);
            }
        }
    }

    /**
     * Returns a proper String Value. Also returns proper value for currency (USD)
     * 
     * @param string
     * @return
     */
    private String returnProperStringValue(Object string) {
        if (ObjectUtils.isNotNull(string)) {
            if (string instanceof KualiDecimal) {
                String amount = (new CurrencyFormatter()).format(string).toString();
                return "$" + (StringUtils.isEmpty(amount) ? "0.00" : amount);
            }
            return string.toString();
        }
        return "";
    }

    /**
     * This method generated the template parameter list to populate the pdf invoices that are attached to the Document.
     * 
     * @return
     */
    private Map<String, String> getTemplateParameterList(ContractsGrantsInvoiceDocument document) {
        ContractsAndGrantsBillingAward award = document.getAward();
        Map<String, String> parameterMap = new HashMap<String, String>();
        Map primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, document.getAccountingPeriod().getUniversityFiscalYear());
        primaryKeys.put("processingChartOfAccountCode", document.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode());
        primaryKeys.put("processingOrganizationCode", document.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode());
        SystemInformation sysInfo = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, primaryKeys);
        parameterMap.put("#documentNumber", returnProperStringValue(document.getDocumentNumber()));
        if (ObjectUtils.isNotNull(document.getDocumentHeader().getWorkflowDocument().getDateCreated())) {
            parameterMap.put("#date", returnProperStringValue(FILE_NAME_TIMESTAMP.format(document.getDocumentHeader().getWorkflowDocument().getDateCreated())));
        }
        if (ObjectUtils.isNotNull(new Date(document.getDocumentHeader().getWorkflowDocument().getDateFinalized().getMillis()))) {
            parameterMap.put("#finalStatusDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(new Date(document.getDocumentHeader().getWorkflowDocument().getDateFinalized().getMillis()))));
        }
        parameterMap.put("#proposalNumber", returnProperStringValue(document.getProposalNumber()));
        parameterMap.put("#payee.name", returnProperStringValue(document.getBillingAddressName()));
        parameterMap.put("#payee.addressLine1", returnProperStringValue(document.getBillingLine1StreetAddress()));
        parameterMap.put("#payee.addressLine2", returnProperStringValue(document.getBillingLine2StreetAddress()));
        parameterMap.put("#payee.city", returnProperStringValue(document.getBillingCityName()));
        parameterMap.put("#payee.state", returnProperStringValue(document.getBillingStateCode()));
        parameterMap.put("#payee.zipcode", returnProperStringValue(document.getBillingZipCode()));
        parameterMap.put("#advanceFlag", convertBooleanValue(isAdvance(document)));
        parameterMap.put("#reimbursementFlag", convertBooleanValue(!(isAdvance(document))));
        parameterMap.put("#accountDetails.contractControlAccountNumber", returnProperStringValue(getRecipientAccountNumber(document.getAccountDetails())));
        if (ObjectUtils.isNotNull(sysInfo)) {
            parameterMap.put("#systemInformation.feinNumber", returnProperStringValue(sysInfo.getUniversityFederalEmployerIdentificationNumber()));
            parameterMap.put("#systemInformation.name", returnProperStringValue(sysInfo.getOrganizationRemitToAddressName()));
            parameterMap.put("#systemInformation.addressLine1", returnProperStringValue(sysInfo.getOrganizationRemitToLine1StreetAddress()));
            parameterMap.put("#systemInformation.addressLine2", returnProperStringValue(sysInfo.getOrganizationRemitToLine2StreetAddress()));
            parameterMap.put("#systemInformation.city", returnProperStringValue(sysInfo.getOrganizationRemitToCityName()));
            parameterMap.put("#systemInformation.state", returnProperStringValue(sysInfo.getOrganizationRemitToStateCode()));
            parameterMap.put("#systemInformation.zipcode", returnProperStringValue(sysInfo.getOrganizationRemitToZipCode()));
        }
        if (CollectionUtils.isNotEmpty(document.getInvoiceDetails())) {
            for (int i = 0; i < document.getInvoiceDetails().size(); i++) {
                parameterMap.put("#invoiceDetail[" + i + "].invoiceDetailIdentifier", returnProperStringValue(document.getInvoiceDetails().get(i).getInvoiceDetailIdentifier()));
                parameterMap.put("#invoiceDetail[" + i + "].documentNumber", returnProperStringValue(document.getInvoiceDetails().get(i).getDocumentNumber()));
                parameterMap.put("#invoiceDetail[" + i + "].categories", returnProperStringValue(document.getInvoiceDetails().get(i).getCategory()));
                parameterMap.put("#invoiceDetail[" + i + "].budget", returnProperStringValue(document.getInvoiceDetails().get(i).getBudget()));
                parameterMap.put("#invoiceDetail[" + i + "].expenditure", returnProperStringValue(document.getInvoiceDetails().get(i).getExpenditures()));
                parameterMap.put("#invoiceDetail[" + i + "].cumulative", returnProperStringValue(document.getInvoiceDetails().get(i).getCumulative()));
                parameterMap.put("#invoiceDetail[" + i + "].balance", returnProperStringValue(document.getInvoiceDetails().get(i).getBalance()));
                parameterMap.put("#invoiceDetail[" + i + "].billed", returnProperStringValue(document.getInvoiceDetails().get(i).getBilled()));
                parameterMap.put("#invoiceDetail[" + i + "].adjustedCumulativeExpenditures", returnProperStringValue(document.getInvoiceDetails().get(i).getAdjustedCumExpenditures()));
                parameterMap.put("#invoiceDetail[" + i + "].adjustedBalance", returnProperStringValue(document.getInvoiceDetails().get(0).getAdjustedBalance()));
            }
        }
        if (CollectionUtils.isNotEmpty(document.getDirectCostInvoiceDetails())) {
            parameterMap.put("#directCostInvoiceDetail.invoiceDetailIdentifier", returnProperStringValue(document.getDirectCostInvoiceDetails().get(0).getInvoiceDetailIdentifier()));
            parameterMap.put("#directCostInvoiceDetail.documentNumber", returnProperStringValue(document.getDirectCostInvoiceDetails().get(0).getDocumentNumber()));
            parameterMap.put("#directCostInvoiceDetail.categories", returnProperStringValue(document.getDirectCostInvoiceDetails().get(0).getCategory()));
            parameterMap.put("#directCostInvoiceDetail.budget", returnProperStringValue(document.getDirectCostInvoiceDetails().get(0).getBudget()));
            parameterMap.put("#directCostInvoiceDetail.expenditure", returnProperStringValue(document.getDirectCostInvoiceDetails().get(0).getExpenditures()));
            parameterMap.put("#directCostInvoiceDetail.cumulative", returnProperStringValue(document.getDirectCostInvoiceDetails().get(0).getCumulative()));
            parameterMap.put("#directCostInvoiceDetail.balance", returnProperStringValue(document.getDirectCostInvoiceDetails().get(0).getBalance()));
            parameterMap.put("#directCostInvoiceDetail.billed", returnProperStringValue(document.getDirectCostInvoiceDetails().get(0).getBilled()));
            parameterMap.put("#directCostInvoiceDetail.adjustedCumulativeExpenditures", returnProperStringValue(document.getDirectCostInvoiceDetails().get(0).getAdjustedCumExpenditures()));
            parameterMap.put("#directCostInvoiceDetail.adjustedBalance", returnProperStringValue(document.getDirectCostInvoiceDetails().get(0).getAdjustedBalance()));
        }
        if (CollectionUtils.isNotEmpty(document.getInDirectCostInvoiceDetails())) {
            parameterMap.put("#inDirectCostInvoiceDetail.invoiceDetailIdentifier", returnProperStringValue(document.getInDirectCostInvoiceDetails().get(0).getInvoiceDetailIdentifier()));
            parameterMap.put("#inDirectCostInvoiceDetail.documentNumber", returnProperStringValue(document.getInDirectCostInvoiceDetails().get(0).getDocumentNumber()));
            parameterMap.put("#inDirectCostInvoiceDetail.categories", returnProperStringValue(document.getInDirectCostInvoiceDetails().get(0).getCategory()));
            parameterMap.put("#inDirectCostInvoiceDetail.budget", returnProperStringValue(document.getInDirectCostInvoiceDetails().get(0).getBudget()));
            parameterMap.put("#inDirectCostInvoiceDetail.expenditure", returnProperStringValue(document.getInDirectCostInvoiceDetails().get(0).getExpenditures()));
            parameterMap.put("#inDirectCostInvoiceDetail.cumulative", returnProperStringValue(document.getInDirectCostInvoiceDetails().get(0).getCumulative()));
            parameterMap.put("#inDirectCostInvoiceDetail.balance", returnProperStringValue(document.getInDirectCostInvoiceDetails().get(0).getBalance()));
            parameterMap.put("#inDirectCostInvoiceDetail.billed", returnProperStringValue(document.getInDirectCostInvoiceDetails().get(0).getBilled()));
            parameterMap.put("#inDirectCostInvoiceDetail.adjustedCumulativeExpenditures", returnProperStringValue(document.getInDirectCostInvoiceDetails().get(0).getAdjustedCumExpenditures()));
            parameterMap.put("#inDirectCostInvoiceDetail.adjustedBalance", returnProperStringValue(document.getInDirectCostInvoiceDetails().get(0).getAdjustedBalance()));
        }
        if (CollectionUtils.isNotEmpty(document.getTotalInvoiceDetails())) {
            parameterMap.put("#totalInvoiceDetail.invoiceDetailIdentifier", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getInvoiceDetailIdentifier()));
            parameterMap.put("#totalInvoiceDetail.documentNumber", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getDocumentNumber()));
            parameterMap.put("#totalInvoiceDetail.categories", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getCategory()));
            parameterMap.put("#totalInvoiceDetail.budget", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getBudget()));
            parameterMap.put("#totalInvoiceDetail.expenditure", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getExpenditures()));
            parameterMap.put("#totalInvoiceDetail.cumulative", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getCumulative()));
            parameterMap.put("#totalInvoiceDetail.balance", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getBalance()));
            parameterMap.put("#totalInvoiceDetail.billed", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getBilled()));
            parameterMap.put("#totalInvoiceDetail.estimatedCost", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getBilled().add(document.getTotalInvoiceDetails().get(0).getExpenditures())));
            parameterMap.put("#totalInvoiceDetail.adjustedCumulativeExpenditures", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getAdjustedCumExpenditures()));
            parameterMap.put("#totalInvoiceDetail.adjustedBalance", returnProperStringValue(document.getTotalInvoiceDetails().get(0).getAdjustedBalance()));
        }
        if (CollectionUtils.isNotEmpty(document.getAgencyAddressDetails())) {
            for (int i = 0; i < document.getAgencyAddressDetails().size(); i++) {
                parameterMap.put("#agencyAddressDetails[" + i + "].documentNumber", returnProperStringValue(document.getAgencyAddressDetails().get(i).getDocumentNumber()));
                parameterMap.put("#agencyAddressDetails[" + i + "].agencyNumber", returnProperStringValue(document.getAgencyAddressDetails().get(i).getAgencyNumber()));
                parameterMap.put("#agencyAddressDetails[" + i + "].agencyAddressIdentifier", returnProperStringValue(document.getAgencyAddressDetails().get(i).getAgencyAddressIdentifier()));
                parameterMap.put("#agencyAddressDetails[" + i + "].agencyAddressTypeCode", returnProperStringValue(document.getAgencyAddressDetails().get(i).getAgencyAddressTypeCode()));
                parameterMap.put("#agencyAddressDetails[" + i + "].agencyAddressName", returnProperStringValue(document.getAgencyAddressDetails().get(i).getAgencyAddressName()));
                parameterMap.put("#agencyAddressDetails[" + i + "].agencyInvoiceTemplateCode", returnProperStringValue(document.getAgencyAddressDetails().get(i).getAgencyInvoiceTemplateCode()));
                parameterMap.put("#agencyAddressDetails[" + i + "].preferredAgencyInvoiceTemplateCode", returnProperStringValue(document.getAgencyAddressDetails().get(i).getPreferredAgencyInvoiceTemplateCode()));
            }
        }
        if (CollectionUtils.isNotEmpty(document.getAccountDetails())) {
            for (int i = 0; i < document.getAccountDetails().size(); i++) {
                parameterMap.put("#accountDetails[" + i + "].documentNumber", returnProperStringValue(document.getAccountDetails().get(i).getDocumentNumber()));
                parameterMap.put("#accountDetails[" + i + "].accountNumber", returnProperStringValue(document.getAccountDetails().get(i).getAccountNumber()));
                parameterMap.put("#accountDetails[" + i + "].proposalNumber", returnProperStringValue(document.getAccountDetails().get(i).getProposalNumber()));
                parameterMap.put("#accountDetails[" + i + "].universityFiscalYear", returnProperStringValue(document.getAccountDetails().get(i).getUniversityFiscalYear()));
                parameterMap.put("#accountDetails[" + i + "].chartOfAccountsCode", returnProperStringValue(document.getAccountDetails().get(i).getChartOfAccountsCode()));
                parameterMap.put("#accountDetails[" + i + "].budgetAmount", returnProperStringValue(document.getAccountDetails().get(i).getBudgetAmount()));
                parameterMap.put("#accountDetails[" + i + "].expenditureAmount", returnProperStringValue(document.getAccountDetails().get(i).getExpenditureAmount()));
                parameterMap.put("#accountDetails[" + i + "].balanceAmount", returnProperStringValue(document.getAccountDetails().get(i).getBalanceAmount()));
                Map map = new HashMap<String, Object>();
                map.put(KFSPropertyConstants.ACCOUNT_NUMBER, document.getAccountDetails().get(i).getAccountNumber());
                map.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, document.getAccountDetails().get(i).getChartOfAccountsCode());
                Account account = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Account.class, map);
                if (ObjectUtils.isNotNull(account)) {
                    parameterMap.put("#accountDetails[" + i + "].account.responsibilityID", returnProperStringValue(account.getContractsAndGrantsAccountResponsibilityId()));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(document.getInvoiceMilestones())) {
            for (int i = 0; i < document.getInvoiceMilestones().size(); i++) {
                parameterMap.put("#invoiceMilestones[" + i + "].proposalNumber", returnProperStringValue(document.getInvoiceMilestones().get(i).getProposalNumber()));
                parameterMap.put("#invoiceMilestones[" + i + "].milestoneNumber", returnProperStringValue(document.getInvoiceMilestones().get(i).getMilestoneNumber()));
                parameterMap.put("#invoiceMilestones[" + i + "].milestoneIdentifier", returnProperStringValue(document.getInvoiceMilestones().get(i).getMilestoneIdentifier()));
                parameterMap.put("#invoiceMilestones[" + i + "].milestoneDescription", returnProperStringValue(document.getInvoiceMilestones().get(i).getMilestoneDescription()));
                parameterMap.put("#invoiceMilestones[" + i + "].milestoneAmount", returnProperStringValue(document.getInvoiceMilestones().get(i).getMilestoneAmount()));
                parameterMap.put("#invoiceMilestones[" + i + "].milestoneExpectedCompletionDate", returnProperStringValue(document.getInvoiceMilestones().get(i).getMilestoneExpectedCompletionDate()));
                parameterMap.put("#invoiceMilestones[" + i + "].milestoneCompletionDate", returnProperStringValue(document.getInvoiceMilestones().get(i).getMilestoneActualCompletionDate()));
                parameterMap.put("#invoiceMilestones[" + i + "].isItBilled", returnProperStringValue(document.getInvoiceMilestones().get(i).isBilledIndicator()));
            }
        }
        if (ObjectUtils.isNotNull(document.getInvoiceGeneralDetail())) {
            parameterMap.put("#invoiceGeneralDetail.documentNumber", returnProperStringValue(document.getInvoiceGeneralDetail().getDocumentNumber()));
            parameterMap.put("#invoiceGeneralDetail.awardDateRange", returnProperStringValue(document.getInvoiceGeneralDetail().getAwardDateRange()));
            parameterMap.put("#invoiceGeneralDetail.billingFrequency", returnProperStringValue(document.getInvoiceGeneralDetail().getBillingFrequency()));
            parameterMap.put("#invoiceGeneralDetail.finalBill", convertBooleanValue(document.getInvoiceGeneralDetail().isFinalBillIndicator()));
            parameterMap.put("#invoiceGeneralDetail.finalInvoice", convertBooleanValue(document.getInvoiceGeneralDetail().isFinalBillIndicator()));
            if (document.getInvoiceGeneralDetail().isFinalBillIndicator()) {
                parameterMap.put("#invoiceGeneralDetail.finalInvoiceYesNo", "Yes");
            }
            else {
                parameterMap.put("#invoiceGeneralDetail.finalInvoiceYesNo", "No");
            }
            parameterMap.put("#invoiceGeneralDetail.billingPeriod", returnProperStringValue(document.getInvoiceGeneralDetail().getBillingPeriod()));
            parameterMap.put("#invoiceGeneralDetail.contractGrantType", returnProperStringValue(document.getInvoiceGeneralDetail().getContractGrantType()));
            parameterMap.put("#invoiceGeneralDetail.awardTotal", returnProperStringValue(document.getInvoiceGeneralDetail().getAwardTotal()));
            parameterMap.put("#invoiceGeneralDetail.newTotalBilled", returnProperStringValue(document.getInvoiceGeneralDetail().getNewTotalBilled()));
            parameterMap.put("#invoiceGeneralDetail.amountRemainingToBill", returnProperStringValue(document.getInvoiceGeneralDetail().getAmountRemainingToBill()));
            parameterMap.put("#invoiceGeneralDetail.billedToDate", returnProperStringValue(document.getInvoiceGeneralDetail().getBilledToDate()));
            parameterMap.put("#invoiceGeneralDetail.costShareAmount", returnProperStringValue(document.getInvoiceGeneralDetail().getCostShareAmount()));
            parameterMap.put("#invoiceGeneralDetail.lastBilledDate", returnProperStringValue(document.getInvoiceGeneralDetail().getLastBilledDate()));
            String strArray[] = document.getInvoiceGeneralDetail().getBillingPeriod().split(" to ");
            if (ObjectUtils.isNotNull(strArray[0])) {
                parameterMap.put("#invoiceGeneralDetail.invoicingPeriodStartDate", returnProperStringValue(strArray[0]));
            }
            if (ObjectUtils.isNotNull(strArray[1])) {
                parameterMap.put("#invoiceGeneralDetail.invoicingPeriodEndDate", returnProperStringValue(strArray[1]));
                parameterMap.put("#award.cumulativePeriod", returnProperStringValue(award.getAwardBeginningDate().toString() + " to " + strArray[1]));
            }
        }

        if (CollectionUtils.isNotEmpty(document.getInvoiceBills())) {
            for (int i = 0; i < document.getInvoiceBills().size(); i++) {
                parameterMap.put("#invoiceBills[" + i + "].proposalNumber", returnProperStringValue(document.getInvoiceBills().get(i).getProposalNumber()));
                parameterMap.put("#invoiceBills[" + i + "].billNumber", returnProperStringValue(document.getInvoiceBills().get(i).getBillNumber()));
                parameterMap.put("#invoiceBills[" + i + "].billDescription", returnProperStringValue(document.getInvoiceBills().get(i).getBillDescription()));
                parameterMap.put("#invoiceBills[" + i + "].billIdentifier", returnProperStringValue(document.getInvoiceBills().get(i).getBillIdentifier()));
                parameterMap.put("#invoiceBills[" + i + "].billDate", returnProperStringValue(document.getInvoiceBills().get(i).getBillDate()));
                parameterMap.put("#invoiceBills[" + i + "].amount", returnProperStringValue(document.getInvoiceBills().get(i).getEstimatedAmount()));
                parameterMap.put("#invoiceBills[" + i + "].isItBilled", returnProperStringValue(document.getInvoiceBills().get(i).isBilledIndicator()));
            }
        }
        if (ObjectUtils.isNotNull(award)) {
            KualiDecimal billing = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).getAwardBilledToDateByProposalNumber(award.getProposalNumber());
            KualiDecimal payments = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).calculateTotalPaymentsToDateByAward(award);
            KualiDecimal receivable = billing.subtract(payments);
            parameterMap.put("#award.billings", returnProperStringValue(billing));
            parameterMap.put("#award.payments", returnProperStringValue(payments));
            parameterMap.put("#award.receivables", returnProperStringValue(receivable));
            parameterMap.put("#award.proposalNumber", returnProperStringValue(award.getProposalNumber()));
            parameterMap.put("#award.awardId", returnProperStringValue(award.getAwardId()));
            if (ObjectUtils.isNotNull(award.getAwardBeginningDate())) {
                parameterMap.put("#award.awardBeginningDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(award.getAwardBeginningDate())));
            }
            if (ObjectUtils.isNotNull(award.getAwardEndingDate())) {
                parameterMap.put("#award.awardEndingDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(award.getAwardEndingDate())));
            }
            parameterMap.put("#award.awardTotalAmount", returnProperStringValue(award.getAwardTotalAmount()));
            parameterMap.put("#award.awardAddendumNumber", returnProperStringValue(award.getAwardAddendumNumber()));
            parameterMap.put("#award.awardAllocatedUniversityComputingServicesAmount", returnProperStringValue(award.getAwardAllocatedUniversityComputingServicesAmount()));
            parameterMap.put("#award.federalPassThroughFundedAmount", returnProperStringValue(award.getFederalPassThroughFundedAmount()));
            if (ObjectUtils.isNotNull(award.getAwardEntryDate())) {
                parameterMap.put("#award.awardEntryDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(award.getAwardEntryDate())));
            }
            parameterMap.put("#award.agencyFuture1Amount", returnProperStringValue(award.getAgencyFuture1Amount()));
            parameterMap.put("#award.agencyFuture2Amount", returnProperStringValue(award.getAgencyFuture2Amount()));
            parameterMap.put("#award.agencyFuture3Amount", returnProperStringValue(award.getAgencyFuture3Amount()));
            parameterMap.put("#award.awardDocumentNumber", returnProperStringValue(award.getAwardDocumentNumber()));
            if (ObjectUtils.isNotNull(award.getAwardLastUpdateDate())) {
                parameterMap.put("#award.awardLastUpdateDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(award.getAwardLastUpdateDate())));
            }
            parameterMap.put("#award.federalPassthroughIndicator", convertBooleanValue(award.getFederalPassThroughIndicator()));
            parameterMap.put("#award.oldProposalNumber", returnProperStringValue(award.getOldProposalNumber()));
            parameterMap.put("#award.awardDirectCostAmount", returnProperStringValue(award.getAwardDirectCostAmount()));
            parameterMap.put("#award.awardIndirectCostAmount", returnProperStringValue(award.getAwardIndirectCostAmount()));
            parameterMap.put("#award.federalFundedAmount", returnProperStringValue(award.getFederalFundedAmount()));
            parameterMap.put("#award.awardCreateTimestamp", returnProperStringValue(award.getAwardCreateTimestamp()));
            if (ObjectUtils.isNotNull(award.getAwardClosingDate())) {
                parameterMap.put("#award.awardClosingDate", returnProperStringValue(FILE_NAME_TIMESTAMP.format(award.getAwardClosingDate())));
            }
            parameterMap.put("#award.proposalAwardTypeCode", returnProperStringValue(award.getProposalAwardTypeCode()));
            parameterMap.put("#award.awardStatusCode", returnProperStringValue(award.getAwardStatusCode()));
            if (ObjectUtils.isNotNull(award.getLetterOfCreditFund())) {
                parameterMap.put("#award.letterOfCreditFundGroupCode", returnProperStringValue(award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode()));
            }
            parameterMap.put("#award.letterOfCreditFundCode", returnProperStringValue(award.getLetterOfCreditFundCode()));
            parameterMap.put("#award.grantDescriptionCode", returnProperStringValue(award.getGrantDescriptionCode()));
            if (ObjectUtils.isNotNull(award.getProposal())) {
                parameterMap.put("#award.grantNumber", returnProperStringValue(award.getProposal().getGrantNumber()));
            }
            parameterMap.put("#agencyNumber", returnProperStringValue(award.getAgencyNumber()));
            parameterMap.put("#agency.fullName", returnProperStringValue(award.getAgency().getFullName()));
            parameterMap.put("#award.federalPassThroughAgencyNumber", returnProperStringValue(award.getFederalPassThroughAgencyNumber()));
            parameterMap.put("#award.agencyAnalystName", returnProperStringValue(award.getAgencyAnalystName()));
            parameterMap.put("#award.analystTelephoneNumber;", returnProperStringValue(award.getAnalystTelephoneNumber()));
            parameterMap.put("#award.preferredBillingFrequency", returnProperStringValue(award.getPreferredBillingFrequency()));
            parameterMap.put("#award.preferredReportTemplate", returnProperStringValue(award.getPreferredReportTemplate()));
            parameterMap.put("#award.preferredReportFrequency", returnProperStringValue(award.getPreferredReportFrequency()));
            parameterMap.put("#award.awardProjectTitle", returnProperStringValue(award.getAwardProjectTitle()));
            parameterMap.put("#award.awardCommentText", returnProperStringValue(award.getAwardCommentText()));
            parameterMap.put("#award.awardPurposeCode", returnProperStringValue(award.getAwardPurposeCode()));
            parameterMap.put("#award.active", convertBooleanValue(award.isActive()));
            parameterMap.put("#award.kimGroupNames", returnProperStringValue(award.getKimGroupNames()));
            parameterMap.put("#award.routingOrg", returnProperStringValue(award.getRoutingOrg()));
            parameterMap.put("#award.routingChart", returnProperStringValue(award.getRoutingChart()));
            parameterMap.put("#award.suspendInvoicing", convertBooleanValue(award.isSuspendInvoicingIndicator()));
            parameterMap.put("#award.additionalFormsRequired", convertBooleanValue(award.isAdditionalFormsRequiredIndicator()));
            parameterMap.put("#award.additionalFormsDescription", returnProperStringValue(award.getAdditionalFormsDescription()));
            parameterMap.put("#award.contractGrantType", returnProperStringValue(award.getContractGrantType()));
            parameterMap.put("#award.minInvoiceAmount", returnProperStringValue(award.getMinInvoiceAmount()));
            parameterMap.put("#award.autoApprove", returnProperStringValue(award.getAutoApproveIndicator()));
            parameterMap.put("#award.lookupPersonUniversalIdentifier", returnProperStringValue(award.getLookupPersonUniversalIdentifier()));
            parameterMap.put("#award.lookupPerson", returnProperStringValue(award.getLookupPerson().getPrincipalName()));
            parameterMap.put("#award.userLookupRoleNamespaceCode", returnProperStringValue(award.getUserLookupRoleNamespaceCode()));
            parameterMap.put("#award.userLookupRoleName", returnProperStringValue(award.getUserLookupRoleName()));
            parameterMap.put("#award.fundingExpirationDate", returnProperStringValue(award.getFundingExpirationDate()));
            parameterMap.put("#award.commentText", returnProperStringValue(award.getCommentText()));
            parameterMap.put("#award.awardProjectDirector.name", returnProperStringValue(award.getAwardPrimaryProjectDirector().getProjectDirector().getName()));
            parameterMap.put("#award.letterOfCreditFundCode", returnProperStringValue(award.getLetterOfCreditFundCode()));
            if (ObjectUtils.isNotNull(award.getAwardPrimaryFundManager())) {
                parameterMap.put("#award.primaryFundManager.name", returnProperStringValue(award.getAwardPrimaryFundManager().getFundManager().getName()));
                parameterMap.put("#award.primaryFundManager.email", returnProperStringValue(award.getAwardPrimaryFundManager().getFundManager().getEmailAddress()));
                parameterMap.put("#award.primaryFundManager.phone", returnProperStringValue(award.getAwardPrimaryFundManager().getFundManager().getPhoneNumber()));
            }
            if (ObjectUtils.isNotNull(document.getInvoiceGeneralDetail())) {
                parameterMap.put("#totalAmountDue", returnProperStringValue(receivable.add(document.getInvoiceGeneralDetail().getNewTotalBilled())));
            }
        }
        return parameterMap;
    }

    /**
     * Returns true if the billing Frequency is Predetermined Billing.
     * 
     * @return
     */
    private boolean isAdvance(ContractsGrantsInvoiceDocument document) {
        if (ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE.equals(document.getInvoiceGeneralDetail().getBillingFrequency())) {
            return true;
        }
        return false;
    }

    /**
     * iText compatible boolean value converter.
     * 
     * @param bool
     * @return
     */
    private String convertBooleanValue(boolean bool) {
        if (bool) {
            return "Yes";
        }
        return "Off";
    }

    /**
     * returns proper contract control Account Number.
     * 
     * @return
     */
    private String getRecipientAccountNumber(List<InvoiceAccountDetail> accountDetails) {
        if (ObjectUtils.isNull(accountDetails.get(0).getContractControlAccountNumber())) {
            return accountDetails.get(0).getAccountNumber();
        }
        return accountDetails.get(0).getContractControlAccountNumber();
    }

    /**
     * This method updates the awardAccounts when the final invoice is generated.
     */
    @Override
    public void doWhenFinalInvoice(ContractsGrantsInvoiceDocument document) {
        if (document.getInvoiceGeneralDetail().isFinalBillIndicator()) {
            Iterator it = document.getAccountDetails().iterator();
            while (it.hasNext()) {
                InvoiceAccountDetail id = (InvoiceAccountDetail) it.next();
                setAwardAccountFinalBilledValue(id, true, document.getProposalNumber());
            }
        }
    }

    /**
     * This method sets the last billed date to Award and Award Account objects based on the status of the invoice. Final or
     * Corrected.
     * 
     * @param invoiceStatus
     */
    @Override
    public void updateLastBilledDate(String invoiceStatus, ContractsGrantsInvoiceDocument document) {
        // To calculate and update Last Billed Date based on the status of the invoice. Final or Corrected.
        // 1. Set last Billed Date to Award Accounts

        Iterator<InvoiceAccountDetail> iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = iterator.next();
            this.calculateAwardAccountLastBilledDate(id, invoiceStatus, document.getInvoiceGeneralDetail().getLastBilledDate(), document.getProposalNumber());
        }

        // 2. Set last Billed to Award = least of last billed date of award account.
        java.sql.Date awdLastBilledDate;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getProposalNumber());
        ContractsAndGrantsBillingAward award = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);
        ContractsAndGrantsBillingAwardAccount awardAccount = award.getActiveAwardAccounts().get(0);
        awdLastBilledDate = award.getActiveAwardAccounts().get(0).getCurrentLastBilledDate();
        for (int i = 0; i < award.getActiveAwardAccounts().size(); i++) {
            if (ObjectUtils.isNull(awdLastBilledDate) || ObjectUtils.isNull(award.getActiveAwardAccounts().get(i).getCurrentLastBilledDate())) {
                // The dates would be null if the user is correcting the first invoice created for the award. Then the award last
                // billed date should also be null.
                awdLastBilledDate = null;
            }
            else if (awdLastBilledDate.after(award.getActiveAwardAccounts().get(i).getCurrentLastBilledDate())) {
                awdLastBilledDate = award.getActiveAwardAccounts().get(i).getCurrentLastBilledDate();
            }
        }

        // To set last billed Date to award.
        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setLastBilledDateToAward(award.getProposalNumber(), awdLastBilledDate);
    }

    /**
     * This method updates the AwardAccount object's last billed Variable with the value provided
     * 
     * @param id
     * @param value
     */
    private void calculateAwardAccountLastBilledDate(InvoiceAccountDetail id, String invoiceStatus, java.sql.Date lastBilledDate, Long proposalNumber) {
        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        // To set previous and current last Billed Date for award account .
        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setLastBilledDateToAwardAccount(mapKey, invoiceStatus, lastBilledDate);

    }

    /**
     * This method updates the Bills and Milestone objects isItBilles Field.
     * 
     * @param string
     */
    @Override
    public void updateBillsAndMilestones(String string, List<InvoiceMilestone> invoiceMilestones, List<InvoiceBill> invoiceBills) {
        updateMilestonesIsItBilled(string, invoiceMilestones);
        updateBillsIsItBilled(string, invoiceBills);
    }

    /**
     * Update Milestone objects isItBilled value.
     * 
     * @param string
     */
    protected void updateMilestonesIsItBilled(String string, List<InvoiceMilestone> invoiceMilestones) {
        // Get a list of invoiceMilestones from the CGIN document. Then search for the actual Milestone object in this list through
        // dao
        // Finally, set these milestones to billed

        if (invoiceMilestones != null && !invoiceMilestones.isEmpty()) {

            List<Long> milestoneIds = new ArrayList<Long>();
            for (InvoiceMilestone invoiceMilestone : invoiceMilestones) {
                milestoneIds.add(invoiceMilestone.getMilestoneIdentifier());
            }

            try {
                ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
                contractsGrantsInvoiceDocumentService.retrieveAndUpdateMilestones(invoiceMilestones, string);

            }
            catch (Exception ex) {
                LOG.error("An error occurred while updating Milestones as billed: " + ex.toString());
            }
        }
    }

    /**
     * Update Bill objects isItBilled value.
     * 
     * @param string
     */
    protected void updateBillsIsItBilled(String string, List<InvoiceBill> invoiceBills) {
        /* update Bill */

        if (invoiceBills != null && !invoiceBills.isEmpty()) {

            try {
                ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
                contractsGrantsInvoiceDocumentService.retrieveAndUpdateBills(invoiceBills, string);

            }
            catch (Exception ex) {
                LOG.error("An error occurred while updating Bills as billed: " + ex.toString());
            }
        }
    }

    /**
     * This method updates the ContractsAndGrantsBillingAwardAccount object's FinalBilled Variable with the value provided
     * 
     * @param id
     * @param value
     */
    private void setAwardAccountFinalBilledValue(InvoiceAccountDetail id, boolean value, Long proposalNumber) {
        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        // To set final Billed to award Account
        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setFinalBilledToAwardAccount(mapKey, value);
    }

    /**
     * This method updates AwardAccounts
     */
    @Override
    public void updateUnfinalizationToAwardAccount(List<InvoiceAccountDetail> accountDetails, Long proposalNumber) {
        Iterator iterator = accountDetails.iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            setAwardAccountFinalBilledValue(id, false, proposalNumber);
        }
    }

    /**
     * Corrects the Contracts and Grants Invoice Document.
     * 
     * @throws WorkflowException
     */
    @Override
    public void correctContractsGrantsInvoiceDocument(ContractsGrantsInvoiceDocument document) throws WorkflowException {
        Iterator iterator = document.getInvoiceDetails().iterator();
        // correct Invoice Details.
        while (iterator.hasNext()) {
            InvoiceDetail id = (InvoiceDetail) iterator.next();
            id.correctInvoiceDetailsCurrentExpenditure();
        }

        // update correction to the InvoiceAccountDetail objects
        iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            id.correctInvoiceAccountDetailsCurrentExpenditureAmount();
        }

        // correct invoiceDetailAccountObjectCode.
        iterator = document.getInvoiceDetailAccountObjectCodes().iterator();
        while (iterator.hasNext()) {
            InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = (InvoiceDetailAccountObjectCode) iterator.next();
            invoiceDetailAccountObjectCode.correctInvoiceDetailAccountObjectCodeExpenditureAmount();
        }

        // correct Bills
        KualiDecimal totalBillingAmount = KualiDecimal.ZERO;
        iterator = document.getInvoiceBills().iterator();
        while (iterator.hasNext()) {
            InvoiceBill bill = (InvoiceBill) iterator.next();
            bill.setEstimatedAmount(bill.getEstimatedAmount().negated());
            totalBillingAmount = totalBillingAmount.add(bill.getEstimatedAmount());
        }

        // correct Milestones
        KualiDecimal totalMilestonesAmount = KualiDecimal.ZERO;
        iterator = document.getInvoiceMilestones().iterator();
        while (iterator.hasNext()) {
            InvoiceMilestone milestone = (InvoiceMilestone) iterator.next();
            milestone.setMilestoneAmount(milestone.getMilestoneAmount().negated());
            totalMilestonesAmount = totalMilestonesAmount.add(milestone.getMilestoneAmount());
        }

        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        // set the billed to Date Field
        if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && CollectionUtils.isNotEmpty(document.getInvoiceMilestones())) {// To
            // check
            // if
            // award
            // has
            // milestones
            document.getInvoiceGeneralDetail().setBilledToDate(contractsGrantsInvoiceDocumentService.getMilestonesBilledToDate(document.getProposalNumber()));
            // update the new total billed for the invoice.
            document.getInvoiceGeneralDetail().setNewTotalBilled(document.getInvoiceGeneralDetail().getNewTotalBilled().add(totalMilestonesAmount));
        }
        else if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && CollectionUtils.isNotEmpty(document.getInvoiceBills())) {// To
            // check
            // if
            // award
            // has
            // bills
            document.getInvoiceGeneralDetail().setBilledToDate(contractsGrantsInvoiceDocumentService.getPredeterminedBillingBilledToDate(document.getProposalNumber()));
            // update the new total billed for the invoice.
            document.getInvoiceGeneralDetail().setNewTotalBilled(document.getInvoiceGeneralDetail().getNewTotalBilled().add(totalBillingAmount));
        }
        else {
            document.getInvoiceGeneralDetail().setBilledToDate(contractsGrantsInvoiceDocumentService.getAwardBilledToDateByProposalNumber(document.getProposalNumber()));
            // update the new total billed for the invoice.
            document.getInvoiceGeneralDetail().setNewTotalBilled(document.getInvoiceGeneralDetail().getNewTotalBilled().add(document.getTotalInvoiceDetails().get(0).getExpenditures()));
        }

        // to set Marked for processing and Date report processed to null.
        document.setMarkedForProcessing(null);
        document.setDateReportProcessed(null);

    }

    /**
     * This method corrects the Maintenance Document for Predetermined Billing
     * 
     * @throws WorkflowException
     */
    @Override
    public void correctBills(List<InvoiceBill> invoiceBills) throws WorkflowException {
        updateBillsIsItBilled(KFSConstants.ParameterValues.STRING_NO, invoiceBills);
    }

    /**
     * This method corrects the Maintenance Document for milestones
     * 
     * @throws WorkflowException
     */
    @Override
    public void correctMilestones(List<InvoiceMilestone> invoiceMilestones) throws WorkflowException {
        updateMilestonesIsItBilled(KFSConstants.ParameterValues.STRING_NO, invoiceMilestones);
    }

    /**
     * This method takes all the applicable attributes from the associated award object and sets those attributes into their
     * corresponding invoice attributes.
     * 
     * @param award The associated award that the invoice will be linked to.
     */
    @Override
    public void populateInvoiceFromAward(ContractsAndGrantsBillingAward award, List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsGrantsInvoiceDocument document) {
        List<Milestone> milestones = new ArrayList<Milestone>();
        List<Bill> bills = new ArrayList<Bill>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        milestones = (List<Milestone>) SpringContext.getBean(BusinessObjectService.class).findMatching(Milestone.class, map);
//        bills = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(AccountsReceivableBill.class).getExternalizableBusinessObjectsList(AccountsReceivableBill.class, map);
        bills = (List<Bill>) SpringContext.getBean(BusinessObjectService.class).findMatching(Bill.class, map);


        if (ObjectUtils.isNotNull(award)) {

            // Invoice General Detail section
            document.setProposalNumber(award.getProposalNumber());
            InvoiceGeneralDetail invoiceGeneralDetail = new InvoiceGeneralDetail();
            invoiceGeneralDetail.setDocumentNumber(document.getDocumentNumber());

            ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
            VerifyBillingFrequencyService verifyBillingFrequencyService = SpringContext.getBean(VerifyBillingFrequencyService.class);

            // Set the last Billed Date and Billing Period
            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            java.sql.Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            invoiceGeneralDetail.setBillingPeriod(pair[0] + " to " + pair[1]);
            invoiceGeneralDetail.setLastBilledDate(pair[1]);


            invoiceGeneralDetail.populateInvoiceFromAward(award);
            document.setInvoiceGeneralDetail(invoiceGeneralDetail);
            // To set Bill by address identifier because it is a required field - set the value to 1 as it is never being used.
            document.setCustomerBillToAddressIdentifier(Integer.parseInt("1"));

            // Set Invoice due date to current date as it is required field and never used.
            document.setInvoiceDueDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight());

            // copy award's agency address to invoice agency address details
            document.getAgencyAddressDetails().clear();

            List<ContractsAndGrantsAgencyAddress> agencyAddresses = new ArrayList<ContractsAndGrantsAgencyAddress>();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.AGENCY_NUMBER, award.getAgency().getAgencyNumber());
            agencyAddresses = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObjectsList(ContractsAndGrantsAgencyAddress.class, mapKey);
            for (ContractsAndGrantsAgencyAddress agencyAddress : agencyAddresses) {

                InvoiceAgencyAddressDetail invoiceAgencyAddressDetail = new InvoiceAgencyAddressDetail();
                invoiceAgencyAddressDetail.setDocumentNumber(document.getDocumentNumber());
                invoiceAgencyAddressDetail.setAgencyNumber(agencyAddress.getAgencyNumber());
                invoiceAgencyAddressDetail.setAgencyAddressIdentifier(agencyAddress.getAgencyAddressIdentifier());
                invoiceAgencyAddressDetail.setAgencyAddressTypeCode(agencyAddress.getAgencyAddressTypeCode());
                invoiceAgencyAddressDetail.setAgencyAddressName(agencyAddress.getAgencyAddressName());
                invoiceAgencyAddressDetail.setAgencyInvoiceTemplateCode(agencyAddress.getAgencyInvoiceTemplateCode());
                invoiceAgencyAddressDetail.setPreferredAgencyInvoiceTemplateCode(agencyAddress.getAgencyInvoiceTemplateCode());
                invoiceAgencyAddressDetail.setInvoiceIndicatorCode(agencyAddress.getInvoiceIndicatorCode());
                invoiceAgencyAddressDetail.setPreferredInvoiceIndicatorCode(agencyAddress.getInvoiceIndicatorCode());

                document.getAgencyAddressDetails().add(invoiceAgencyAddressDetail);
            }

            java.sql.Date invoiceDate = document.getInvoiceGeneralDetail().getLastBilledDate();
            if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && CollectionUtils.isNotEmpty(milestones)) {// To
                // check
                // if
                // award
                // has
                // milestones

                // copy award milestones to invoice milestones
                document.getInvoiceMilestones().clear();
                for (Milestone awdMilestone : milestones) {
                    // To consider the completed milestones only.
                    // To check for null - Milestone Completion date.

                    if (awdMilestone.getMilestoneActualCompletionDate() != null && !invoiceDate.before(awdMilestone.getMilestoneActualCompletionDate()) && !awdMilestone.isBilledIndicator() && awdMilestone.getMilestoneAmount().isGreaterThan(KualiDecimal.ZERO)) {

                        InvoiceMilestone invMilestone = new InvoiceMilestone();
                        invMilestone.setProposalNumber(awdMilestone.getProposalNumber());
                        invMilestone.setMilestoneNumber(awdMilestone.getMilestoneNumber());
                        invMilestone.setMilestoneIdentifier(awdMilestone.getMilestoneIdentifier());
                        invMilestone.setMilestoneDescription(awdMilestone.getMilestoneDescription());
                        invMilestone.setBilledIndicator(awdMilestone.isBilledIndicator());
                        invMilestone.setMilestoneActualCompletionDate(awdMilestone.getMilestoneActualCompletionDate());
                        invMilestone.setMilestoneAmount(awdMilestone.getMilestoneAmount());
                        document.getInvoiceMilestones().add(invMilestone);
                    }
                }
            }
            else if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && CollectionUtils.isNotEmpty(bills)) {// To
                // check
                // if
                // award
                // has
                // bills

                // copy award milestones to invoice milestones
                document.getInvoiceBills().clear();
                for (Bill awdBill : bills) {
                    // To check for null - Bill Completion date.
                    // To consider the completed milestones only.
                    if (awdBill.getBillDate() != null && !invoiceDate.before(awdBill.getBillDate()) && !awdBill.isBilledIndicator() && awdBill.getEstimatedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                        InvoiceBill invBill = new InvoiceBill();
                        invBill.setProposalNumber(awdBill.getProposalNumber());
                        invBill.setBillNumber(awdBill.getBillNumber());
                        invBill.setBillIdentifier(awdBill.getBillIdentifier());
                        invBill.setBillDescription(awdBill.getBillDescription());
                        invBill.setBilledIndicator(awdBill.isBilledIndicator());
                        invBill.setBillDate(awdBill.getBillDate());
                        invBill.setEstimatedAmount(awdBill.getEstimatedAmount());
                        document.getInvoiceBills().add(invBill);
                    }
                }
            }
            else {

                // To set values for categories and populate invoice details section
                generateValuesForAccountObjectCodes(awardAccounts, award, document);
                generateValuesForCategories(awardAccounts, document);
            }

            // copy award's accounts to invoice account details
            document.getAccountDetails().clear();
            for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {

                InvoiceAccountDetail invoiceAccountDetail = new InvoiceAccountDetail();
                invoiceAccountDetail.setDocumentNumber(document.getDocumentNumber());

                invoiceAccountDetail.setAccountNumber(awardAccount.getAccountNumber());
                if (ObjectUtils.isNotNull(awardAccount.getAccount()) && StringUtils.isNotEmpty(awardAccount.getAccount().getContractControlAccountNumber())) {
                    invoiceAccountDetail.setContractControlAccountNumber(awardAccount.getAccount().getContractControlAccountNumber());
                }
                invoiceAccountDetail.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
                invoiceAccountDetail.setProposalNumber(award.getProposalNumber());
                invoiceAccountDetail.setBudgetsAndCumulatives(document.getInvoiceGeneralDetail().getLastBilledDate(), document.getInvoiceGeneralDetail().getBillingFrequency(), award.getAwardBeginningDate());
                document.getAccountDetails().add(invoiceAccountDetail);
            }
            // Set some basic values to invoice Document
            setUpValuesForContractsGrantsInvoiceDocument(award, document);

        }
    }


    /**
     * This method helps in setting up basic values for Contracts Grants Invoice Document
     */
    public void setUpValuesForContractsGrantsInvoiceDocument(ContractsAndGrantsBillingAward award, ContractsGrantsInvoiceDocument document) {
        if (ObjectUtils.isNotNull(award.getAgency())) {
            if (ObjectUtils.isNotNull(document.getAccountsReceivableDocumentHeader())) {
                document.getAccountsReceivableDocumentHeader().setCustomerNumber(award.getAgency().getCustomerNumber());
            }
            Customer customer = SpringContext.getBean(CustomerService.class).getByPrimaryKey(award.getAgency().getCustomerNumber());
            if (ObjectUtils.isNotNull(customer)) {
                document.setCustomerName(customer.getCustomerName());
            }
        }
        // To set open invoice indicator to true to help doing cash control for the invoice
        document.setOpenInvoiceIndicator(true);

        // To set LOC creation type and appropriate values from award.
        if (StringUtils.isNotEmpty(award.getLetterOfCreditCreationType())) {
            document.setLetterOfCreditCreationType(award.getLetterOfCreditCreationType());
        }
        // To set up values for Letter of Credit Fund and Fund Group irrespective of the LOC Creation type.
        if (StringUtils.isNotEmpty(award.getLetterOfCreditFundCode())) {
            document.setLetterOfCreditFundCode(award.getLetterOfCreditFundCode());
        }
        if (ObjectUtils.isNotNull(award.getLetterOfCreditFund())) {
            if (StringUtils.isNotEmpty(award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode())) {
                document.setLetterOfCreditFundGroupCode(award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode());
            }
        }

        // To set Account Receivable object code when the parameter is 3.

        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
        List<ContractsGrantsAwardInvoiceAccountInformation> awardInvoiceAccounts = new ArrayList<ContractsGrantsAwardInvoiceAccountInformation>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        map.put(KFSPropertyConstants.ACTIVE, true);
        awardInvoiceAccounts = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsGrantsAwardInvoiceAccountInformation.class).getExternalizableBusinessObjectsList(ContractsGrantsAwardInvoiceAccountInformation.class, map);
        if (isUsingReceivableFAU) {
            if (CollectionUtils.isNotEmpty(awardInvoiceAccounts)) {
                for (ContractsGrantsAwardInvoiceAccountInformation awardInvoiceAccount : awardInvoiceAccounts) {
                    if (awardInvoiceAccount.getAccountType().equals(ArPropertyConstants.AR_ACCOUNT)) {
                        if (awardInvoiceAccount.isActive()) {// consider the active invoice account only.
                            document.setPaymentChartOfAccountsCode(awardInvoiceAccount.getChartOfAccountsCode());
                            document.setPaymentAccountNumber(awardInvoiceAccount.getAccountNumber());
                            document.setPaymentSubAccountNumber(awardInvoiceAccount.getSubAccountNumber());
                            document.setPaymentFinancialObjectCode(awardInvoiceAccount.getObjectCode());
                            document.setPaymentFinancialSubObjectCode(awardInvoiceAccount.getSubObjectCode());
                        }
                    }
                }
            }
        }

        // set totalBilled by Account Number in Account Details
        Map<String, KualiDecimal> totalBilledByAccountNumberMap = new HashMap<String, KualiDecimal>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : document.getInvoiceDetailAccountObjectCodes()) {
            String accountNumber = invoiceDetailAccountObjectCode.getAccountNumber();
            KualiDecimal totalBilled = totalBilledByAccountNumberMap.get(accountNumber);
            // if account number not found in map, then create new total, 0
            if (totalBilled == null) {
                totalBilled = new KualiDecimal(0);
            }
            totalBilled = totalBilled.add(invoiceDetailAccountObjectCode.getTotalBilled());
            totalBilledByAccountNumberMap.put(accountNumber, totalBilled);
        }

        for (InvoiceAccountDetail invAcctD : document.getAccountDetails()) {
            if (totalBilledByAccountNumberMap.get(invAcctD.getAccountNumber()) != null) {
                invAcctD.setBilledAmount(totalBilledByAccountNumberMap.get(invAcctD.getAccountNumber()));
            }
        }


        KualiDecimal totalMilestoneAmount = KualiDecimal.ZERO;
        // To calculate the total milestone amount.
        if (document.getInvoiceMilestones().size() > 0) {
            for (InvoiceMilestone milestone : document.getInvoiceMilestones()) {
                if (milestone.getMilestoneAmount() != null) {
                    totalMilestoneAmount = totalMilestoneAmount.add(milestone.getMilestoneAmount());
                }
            }
        }
        totalMilestoneAmount = totalMilestoneAmount.add(document.getInvoiceGeneralDetail().getBilledToDate());

        KualiDecimal totalBillAmount = KualiDecimal.ZERO;
        // To calculate the total bill amount.
        if (document.getInvoiceBills().size() > 0) {
            for (InvoiceBill bill : document.getInvoiceBills()) {
                if (bill.getEstimatedAmount() != null) {
                    totalBillAmount = totalBillAmount.add(bill.getEstimatedAmount());
                }
            }
        }
        totalBillAmount = totalBillAmount.add(document.getInvoiceGeneralDetail().getBilledToDate());

        // to set the account detail expenditure amount
        KualiDecimal totalExpendituredAmount = KualiDecimal.ZERO;
        for (InvoiceAccountDetail invAcctD : document.getAccountDetails()) {
            KualiDecimal currentExpenditureAmount = KualiDecimal.ZERO;

            currentExpenditureAmount = invAcctD.getCumulativeAmount().subtract(invAcctD.getBilledAmount());
            invAcctD.setExpenditureAmount(currentExpenditureAmount);
            // overwriting account detail expenditure amount if locReview Indicator is true - and award belongs to LOC Billing
            for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                if (awardAccount.getAccountNumber().equals(invAcctD.getAccountNumber()) && awardAccount.isLetterOfCreditReviewIndicator() && award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE)) {
                    currentExpenditureAmount = awardAccount.getAmountToDraw();
                    invAcctD.setExpenditureAmount(currentExpenditureAmount);
                }
            }
            totalExpendituredAmount = totalExpendituredAmount.add(currentExpenditureAmount);
        }
        totalExpendituredAmount = totalExpendituredAmount.add(document.getInvoiceGeneralDetail().getBilledToDate());

        // To set the New Total Billed Amount.
        if (document.getInvoiceMilestones().size() > 0) {
            document.getInvoiceGeneralDetail().setNewTotalBilled(totalMilestoneAmount);
        }
        else if (document.getInvoiceBills().size() > 0) {
            document.getInvoiceGeneralDetail().setNewTotalBilled(totalBillAmount);
        }
        else {
            document.getInvoiceGeneralDetail().setNewTotalBilled(totalExpendituredAmount);
        }

    }

    /**
     * @param awardAccounts
     * @param award
     */
    public void generateValuesForAccountObjectCodes(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsAndGrantsBillingAward award, ContractsGrantsInvoiceDocument document) {

        List<Balance> glBalances = new ArrayList<Balance>();
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        Map<String, Set<String>> objectCodeFromCategoriesMap = new HashMap<String, Set<String>>();

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        Collection<ContractsAndGrantsCategories> contractsAndGrantsCategories = SpringContext.getBean(BusinessObjectService.class).findMatching(ContractsAndGrantsCategories.class, criteria);
        // get the categories and create a new arraylist for each one
        for (ContractsAndGrantsCategories category : contractsAndGrantsCategories) {
            // populate the category object code maps
            objectCodeFromCategoriesMap.put(category.getCategoryCode(), getObjectCodeArrayFromSingleCategory(category, document));

        }
        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
            // Changes made to retrieve balances of previous years (useful when the award is billed for the first time and in case
            // of fiscal year change)
            // 1. If award is billed for the first time.


            Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getFiscalYear(award.getAwardBeginningDate());

            for (Integer i = fiscalYear; i <= currentYear; i++) {
                fiscalYears.add(i);
            }

            for (Integer eachFiscalYr : fiscalYears) {
                Map<String, Object> balanceKeys = new HashMap<String, Object>();
                balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
                balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
                balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
                balanceKeys.put("objectTypeCode", ArPropertyConstants.EXPENSE_OBJECT_TYPE);
                balanceKeys.put("balanceTypeCode", ArPropertyConstants.ACTUAL_BALANCE_TYPE);

                glBalances.addAll(SpringContext.getBean(BusinessObjectService.class).findMatching(Balance.class, balanceKeys));
            }
        } // now you have a list of balances from all accounts;


        for (Balance bal : glBalances) {
            if (ObjectUtils.isNotNull(bal.getSubAccount()) && ObjectUtils.isNotNull(bal.getSubAccount().getA21SubAccount()) && !StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {

                ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

                for (ContractsAndGrantsCategories category : contractsAndGrantsCategories) {
                    Set<String> objectCodeFromCategoriesSet = objectCodeFromCategoriesMap.get(category.getCategoryCode());

                    // if the object code from this balance is in the list of object code retrieved from the category, then include
                    // in
                    // the detail
                    if (objectCodeFromCategoriesSet.contains(bal.getObjectCode())) {
                        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode;
                        // Check if there is an existing invoice detail account object code existing (if there are more than one
                        // fiscal years)
                        Map<String, Object> invDtlKeys = new HashMap<String, Object>();
                        invDtlKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getProposalNumber());
                        invDtlKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, bal.getChartOfAccountsCode());
                        invDtlKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, bal.getAccountNumber());
                        invDtlKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, bal.getObjectCode());
                        invDtlKeys.put(KFSPropertyConstants.DOCUMENT_NUMBER, document.getDocumentHeader().getDocumentNumber());
                        invoiceDetailAccountObjectCode = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(InvoiceDetailAccountObjectCode.class, invDtlKeys);

                        if (ObjectUtils.isNull(invoiceDetailAccountObjectCode)) {
                            invoiceDetailAccountObjectCode = new InvoiceDetailAccountObjectCode();
                            invoiceDetailAccountObjectCode.setDocumentNumber(document.getDocumentHeader().getDocumentNumber());
                            invoiceDetailAccountObjectCode.setProposalNumber(document.getProposalNumber());
                            invoiceDetailAccountObjectCode.setFinancialObjectCode(bal.getObjectCode());
                            invoiceDetailAccountObjectCode.setCategoryCode(category.getCategoryCode());
                            invoiceDetailAccountObjectCode.setAccountNumber(bal.getAccountNumber());
                            invoiceDetailAccountObjectCode.setChartOfAccountsCode(bal.getChartOfAccountsCode());
                            document.getInvoiceDetailAccountObjectCodes().add(invoiceDetailAccountObjectCode);
                        }
                        // Retrieve cumulative amounts based on the biling period.

                        if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MONTHLY_BILLING_SCHEDULE_CODE) || document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.QUATERLY_BILLING_SCHEDULE_CODE) || document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE) || document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.ANNUALLY_BILLING_SCHEDULE_CODE)) {
                            invoiceDetailAccountObjectCode.setCumulativeExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().add(contractsGrantsInvoiceDocumentService.retrieveAccurateBalanceAmount(document.getInvoiceGeneralDetail().getLastBilledDate(), bal)));
                        }
                        else if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.BILLED_AT_TERM)) {
                            invoiceDetailAccountObjectCode.setCumulativeExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().add(bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount())));
                        }
                        else {// This code should be removed. This is temporary - just to make sure the amounts are pulled up.
                            invoiceDetailAccountObjectCode.setCumulativeExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().add(bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount())));
                        }

                        // add this single account object code item to the list in the Map
                        SpringContext.getBean(BusinessObjectService.class).save(invoiceDetailAccountObjectCode);


                        break; // found a match into which category, we can stop and move on to next balance entry
                    }
                }
            }
        }

        // Modifying the code to set invoiceDetailaccountobject codes calculation here checking with loc review indicator and also
        // accounting more than one fiscal years.

        for (ContractsAndGrantsBillingAwardAccount awdAcct : awardAccounts) {
            if (awdAcct.isLetterOfCreditReviewIndicator() && award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE)) {
                KualiDecimal amountToDrawForObjectCodes = KualiDecimal.ZERO;

                List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodeList = new ArrayList<InvoiceDetailAccountObjectCode>();
                for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : document.getInvoiceDetailAccountObjectCodes()) {
                    if (invoiceDetailAccountObjectCode.getDocumentNumber().equals(document.getDocumentNumber()) && invoiceDetailAccountObjectCode.getProposalNumber().equals(document.getProposalNumber()) && invoiceDetailAccountObjectCode.getAccountNumber().equals(awdAcct.getAccountNumber()) && invoiceDetailAccountObjectCode.getChartOfAccountsCode().equals(awdAcct.getChartOfAccountsCode())) {
                        invoiceDetailAccountObjectCodeList.add(invoiceDetailAccountObjectCode);
                    }
                }
                amountToDrawForObjectCodes = awdAcct.getAmountToDraw().divide(new KualiDecimal(invoiceDetailAccountObjectCodeList.size()));

                // Now to set the divided value equally to all the object code rows.
                for (InvoiceDetailAccountObjectCode invDtllAcctOB : invoiceDetailAccountObjectCodeList) {
                    invDtllAcctOB.setCurrentExpenditures(amountToDrawForObjectCodes);

                }
            }
            else {
                // code to write values from award acct total billed amount to invoice detail account object code..

                Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
                totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getProposalNumber());
                totalBilledKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awdAcct.getChartOfAccountsCode());
                totalBilledKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awdAcct.getAccountNumber());

                List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilledList = (List<AwardAccountObjectCodeTotalBilled>) SpringContext.getBean(BusinessObjectService.class).findMatching(AwardAccountObjectCodeTotalBilled.class, totalBilledKeys);

                for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : document.getInvoiceDetailAccountObjectCodes()) {
                    if (CollectionUtils.isNotEmpty(awardAccountObjectCodeTotalBilledList)) {
                        for (AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled : awardAccountObjectCodeTotalBilledList) {
                            if (invoiceDetailAccountObjectCode.getFinancialObjectCode().equalsIgnoreCase(awardAccountObjectCodeTotalBilled.getFinancialObjectCode())) {
                                invoiceDetailAccountObjectCode.setTotalBilled(awardAccountObjectCodeTotalBilled.getTotalBilled());
                            }
                        }
                    }
                    // Set current expenditures
                    invoiceDetailAccountObjectCode.setCurrentExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().subtract(invoiceDetailAccountObjectCode.getTotalBilled()));

                }
            }

        }
    }

    /**
     * 1. This method is responsible to populate categories column for the ContractsGrantsInvoice Document. 2. The categories are
     * retrieved from the Maintenance document as a collection and then a logic with conditions to handle ranges of Object Codes. 3.
     * Once the object codes are retrieved and categories are set the performAccountingCalculations method of InvoiceDetail BO will
     * do all the accounting calculations.
     */
    public void generateValuesForCategories(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsGrantsInvoiceDocument document) {

        Set<String> categoryArray = new HashSet<String>();

        // To get only the active categories.
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        Collection<ContractsAndGrantsCategories> contractsAndGrantsCategories = SpringContext.getBean(BusinessObjectService.class).findMatching(ContractsAndGrantsCategories.class, criteria);
        Iterator<ContractsAndGrantsCategories> it = contractsAndGrantsCategories.iterator();

        // query database for award account object code details. then divi them up into categories
        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilleds = contractsGrantsInvoiceDocumentService.getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);

        while (it.hasNext()) {
            ContractsAndGrantsCategories category = it.next();
            // To add all the values from Category Array to Invoice Details category only if they are retrieved well.

            InvoiceDetail invDetail = new InvoiceDetail();
            invDetail.setDocumentNumber(document.getDocumentHeader().getDocumentNumber());

            invDetail.setCategory(category.getCategoryName());
            invDetail.setCategoryCode(category.getCategoryCode());
            invDetail.setIndirectCostIndicator(category.isIndirectCostIndicator());
            // calculate total billed first
            Set<String> completeObjectCodeArrayForSingleCategory = getObjectCodeArrayFromSingleCategory(category, document);
            invDetail.performTotalBilledCalculation(awardAccountObjectCodeTotalBilleds, completeObjectCodeArrayForSingleCategory);


            invDetail.performCumulativeExpenditureCalculation(document.getInvoiceDetailAccountObjectCodes(), completeObjectCodeArrayForSingleCategory);

            invDetail.performCurrentExpenditureCalculation(document.getInvoiceDetailAccountObjectCodes(), completeObjectCodeArrayForSingleCategory);

            // calculate the rest using billed to date
            invDetail.performBudgetCalculations(awardAccounts, completeObjectCodeArrayForSingleCategory, document.getAward().getAwardBeginningDate());// accounting
                                                                                                                                                      // calculations
            // happening here

            document.getInvoiceDetails().add(invDetail);
        }


        // To calculate total values for Invoice Detail section.

        KualiDecimal totalDirectCostBudget = KualiDecimal.ZERO;
        KualiDecimal totalDirectCostCumulative = KualiDecimal.ZERO;
        KualiDecimal totalDirectCostExpenditures = KualiDecimal.ZERO;
        KualiDecimal totalDirectCostBalance = KualiDecimal.ZERO;
        KualiDecimal totalDirectCostBilled = KualiDecimal.ZERO;
        KualiDecimal totalInDirectCostBudget = KualiDecimal.ZERO;
        KualiDecimal totalInDirectCostCumulative = KualiDecimal.ZERO;
        KualiDecimal totalInDirectCostExpenditures = KualiDecimal.ZERO;
        KualiDecimal totalInDirectCostBalance = KualiDecimal.ZERO;
        KualiDecimal totalInDirectCostBilled = KualiDecimal.ZERO;
        Iterator<InvoiceDetail> o = document.getInvoiceDetails().iterator();

        while (o.hasNext()) {

            InvoiceDetail invD = o.next();
            // To sum up values for indirect Cost Invoice Details

            if (invD.isIndirectCostIndicator()) {
                if (null != invD.getBudget()) {
                    totalInDirectCostBudget = totalInDirectCostBudget.add(invD.getBudget());
                }
                if (null != invD.getCumulative()) {
                    totalInDirectCostCumulative = totalInDirectCostCumulative.add(invD.getCumulative());

                }
                if (null != invD.getBalance()) {
                    totalInDirectCostBalance = totalInDirectCostBalance.add(invD.getBalance());
                }
                if (null != invD.getBilled()) {
                    totalInDirectCostBilled = totalInDirectCostBilled.add(invD.getBilled());
                }
                if (null != invD.getExpenditures()) {
                    totalInDirectCostExpenditures = totalInDirectCostExpenditures.add(invD.getExpenditures());
                }

            }
            else {
                if (null != invD.getBudget()) {
                    totalDirectCostBudget = totalDirectCostBudget.add(invD.getBudget());
                }
                if (null != invD.getCumulative()) {
                    totalDirectCostCumulative = totalDirectCostCumulative.add(invD.getCumulative());

                }
                if (null != invD.getBalance()) {
                    totalDirectCostBalance = totalDirectCostBalance.add(invD.getBalance());
                }
                if (null != invD.getBilled()) {
                    totalDirectCostBilled = totalDirectCostBilled.add(invD.getBilled());
                }
                if (null != invD.getExpenditures()) {
                    totalDirectCostExpenditures = totalDirectCostExpenditures.add(invD.getExpenditures());
                }
            }
        }
        InvoiceDetail directCostInvDetail = new InvoiceDetail();
        directCostInvDetail.setDocumentNumber(document.getDocumentHeader().getDocumentNumber());

        directCostInvDetail.setCategory(ArConstants.TOTAL_DIRECT_COST);
        directCostInvDetail.setCategoryCode(ArConstants.TOTAL_DIRECT_COST_CD);
        directCostInvDetail.setBudget(totalDirectCostBudget);
        directCostInvDetail.setExpenditures(totalDirectCostExpenditures);
        directCostInvDetail.setCumulative(totalDirectCostCumulative);
        directCostInvDetail.setBalance(totalDirectCostBalance);
        directCostInvDetail.setBilled(totalDirectCostBilled);
        document.getDirectCostInvoiceDetails().add(directCostInvDetail);

        // To create a Total In Direct Cost invoice detail to add values for indirect cost invoice details.

        InvoiceDetail indInvDetail = new InvoiceDetail();
        indInvDetail.setDocumentNumber(document.getDocumentHeader().getDocumentNumber());
        indInvDetail.setIndirectCostIndicator(true);
        indInvDetail.setCategory(ArConstants.TOTAL_IN_DIRECT_COST);
        indInvDetail.setCategoryCode(ArConstants.TOTAL_IN_DIRECT_COST_CD);
        indInvDetail.setBudget(totalInDirectCostBudget);
        indInvDetail.setExpenditures(totalInDirectCostExpenditures);
        indInvDetail.setCumulative(totalInDirectCostCumulative);
        indInvDetail.setBalance(totalInDirectCostBalance);
        indInvDetail.setBilled(totalInDirectCostBilled);
        document.getInDirectCostInvoiceDetails().add(indInvDetail);

        // Sum up the direct cost and indirect cost invoice details.

        InvoiceDetail totalInvDetail = new InvoiceDetail();
        totalInvDetail.setDocumentNumber(document.getDocumentHeader().getDocumentNumber());

        totalInvDetail.setCategory(ArConstants.TOTAL_COST);
        totalInvDetail.setCategoryCode(ArConstants.TOTAL_COST_CD);

        if (ObjectUtils.isNotNull(document.getDirectCostInvoiceDetails()) && CollectionUtils.isNotEmpty(document.getDirectCostInvoiceDetails())) {
            totalInvDetail.setBudget(document.getDirectCostInvoiceDetails().get(0).getBudget().add(totalInDirectCostBudget));
            totalInvDetail.setExpenditures(document.getDirectCostInvoiceDetails().get(0).getExpenditures().add(totalInDirectCostExpenditures));
            totalInvDetail.setCumulative(document.getDirectCostInvoiceDetails().get(0).getCumulative().add(totalInDirectCostCumulative));
            totalInvDetail.setBalance(document.getDirectCostInvoiceDetails().get(0).getBalance().add(totalInDirectCostBalance));
            totalInvDetail.setBilled(document.getDirectCostInvoiceDetails().get(0).getBilled().add(totalInDirectCostBilled));
        }

        document.getTotalInvoiceDetails().add(totalInvDetail);

    }

    /**
     * This method takes a ContractsAndGrantsCategory, retrieves the specified object code or object code range. It then parses this
     * string, and returns all the possible object codes specified by this range.
     * 
     * @param category
     * @return Set<String> objectCodes
     */
    @Override
    public Set<String> getObjectCodeArrayFromSingleCategory(ContractsAndGrantsCategories category, ContractsGrantsInvoiceDocument document) throws IllegalArgumentException {
        Set<String> objectCodeArray = new HashSet<String>();
        Set<String> levels = new HashSet<String>();
        if (ObjectUtils.isNotNull(category.getCategoryObjectCodes()) && StringUtils.isNotEmpty(category.getCategoryObjectCodes())) {
            List<String> objectCodes = Arrays.asList(category.getCategoryObjectCodes().split(","));

            // get a list of qualifying object codes listed in the categories
            for (int j = 0; j < objectCodes.size(); j++) {

                // This is to check if the object codes are in a range of values like 1001-1009 or 100* or 10* or 1*. The wildcard
                // should be included in the suffix only.
                if (objectCodes.get(j).contains("-")) {// To check ranges like A000 - ZZZZ (includes A001, A002 .. A009 , A00A to
                                                       // A00Z
                    // and so on to ZZZZ)
                    String obCodeFirst = StringUtils.substringBefore(objectCodes.get(j), "-").trim();
                    String obCodeLast = StringUtils.substringAfter(objectCodes.get(j), "-").trim();
                    // To validate if the object Code formed is in proper format of [0-9a-zA-Z]{4}

                    if (obCodeFirst.matches("[0-9a-zA-Z]{4}") && obCodeLast.matches("[0-9a-zA-Z]{4}")) {
                        try {

                            List<String> objectCodeValues = incrementAlphaNumericString(obCodeFirst, obCodeLast);
                            // To Check for the first value as it is not being included in the array
                            objectCodeArray.add(obCodeFirst);

                            for (int i = 0; i < objectCodeValues.size(); i++) {
                                objectCodeArray.add(objectCodeValues.get(i));
                            }
                        }
                        catch (Exception ex) {
                            String msg = String.format("Failed to get Object Codes for Contracts and Grants Invoice", ex.getMessage());
                            LOG.error(msg);
                            throw new RuntimeException(msg, ex);
                        }
                    }
                    else {
                        throw new IllegalArgumentException("Invalid Object Code range specification for the category:" + category.getCategoryName());
                    }
                }
                else if (objectCodes.get(j).contains("*")) {// To check for wildcard suffix
                    String obCodeFirst = StringUtils.substringBefore(objectCodes.get(j), "*").trim();
                    String obCodeLast = StringUtils.substringBefore(objectCodes.get(j), "*").trim(); // substringBefore is correct
                                                                                                     // here
                    // To make the code work for wildcards like 1* 10* 100* etc
                    // 10* will give you from 100 - 10Z.

                    for (int x = obCodeFirst.length(); x < 4; x++) {
                        obCodeFirst = obCodeFirst.concat("0");
                    }

                    for (int x = obCodeLast.length(); x < 4; x++) {
                        obCodeLast = obCodeLast.concat("Z");
                    }
                    if (obCodeFirst.matches("[0-9a-zA-Z]{4}") && obCodeLast.matches("[0-9a-zA-Z]{4}")) {
                        try {
                            List<String> obCodeValues = incrementAlphaNumericString(obCodeFirst, obCodeLast);

                            // To Check for the first value as it is not being included in the array
                            objectCodeArray.add(obCodeFirst);
                            for (int i = 0; i < obCodeValues.size(); i++) {
                                objectCodeArray.add(obCodeValues.get(i));
                            }
                        }
                        catch (Exception ex) {
                            String msg = String.format("Failed to get Object Codes for Contracts and Grants Invoice for the category:" + category.getCategoryName(), ex.getMessage());
                            LOG.error(msg);
                            throw new RuntimeException(msg, ex);
                        }
                    }
                    else {
                        throw new IllegalArgumentException("Invalid Object Code range specification for the category:" + category.getCategoryName());
                    }
                }
                else {// If the object code is directly provided.
                    if (objectCodes.get(j).trim().matches("[0-9a-zA-Z]{4}")) {

                        objectCodeArray.add(objectCodes.get(j).trim());
                    }
                    else {
                        throw new IllegalArgumentException("Invalid Object Code range specification for the category:" + category.getCategoryName());
                    }
                }

            }
        }
        if (ObjectUtils.isNotNull(category.getCategoryConsolidations()) && StringUtils.isNotEmpty(category.getCategoryConsolidations())) {
            List<String> consolidationCodes = Arrays.asList(category.getCategoryConsolidations().split(","));
            List<ObjectLevel> objectLevels = SpringContext.getBean(ObjectLevelService.class).getObjectLevelsByConsolidationsIds(consolidationCodes);
            if (ObjectUtils.isNotNull(objectLevels) && !objectLevels.isEmpty()) {
                for (ObjectLevel level : objectLevels) {
                    levels.add(level.getFinancialObjectLevelCode());
                }
            }
        }
        if (ObjectUtils.isNotNull(category.getCategoryLevels()) && StringUtils.isNotEmpty(category.getCategoryLevels())) {
            List<String> levelCodes = Arrays.asList(category.getCategoryLevels().split(","));
            List<ObjectLevel> objectLevels = SpringContext.getBean(ObjectLevelService.class).getObjectLevelsByLevelIds(levelCodes);
            if (ObjectUtils.isNotNull(objectLevels) && !objectLevels.isEmpty()) {
                for (ObjectLevel level : objectLevels) {
                    levels.add(level.getFinancialObjectLevelCode());
                }
            }
        }
        if (ObjectUtils.isNotNull(levels) && !levels.isEmpty()) {
            List<ObjectCode> objectCodes = SpringContext.getBean(ObjectCodeService.class).getObjectCodesByLevelIds(new ArrayList<String>(levels));
            if (ObjectUtils.isNotNull(objectCodes) && !objectCodes.isEmpty()) {
                for (ObjectCode objectCode : objectCodes) {
                    objectCodeArray.add(objectCode.getFinancialObjectCode());
                }
            }
        }
        return objectCodeArray;
    }

    /**
     * This method returns the complete set of object codes for ALL ContractsAndGrantsCategories.
     * 
     * @return Set<String> objectCodes
     */
    public Set<String> getObjectCodeArrayFromContractsAndGrantsCategories(ContractsGrantsInvoiceDocument document) {
        Set<String> objectCodeArray = new HashSet<String>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        Collection<ContractsAndGrantsCategories> contractsAndGrantsCategories = SpringContext.getBean(BusinessObjectService.class).findMatching(ContractsAndGrantsCategories.class, criteria);
        Iterator<ContractsAndGrantsCategories> contractsAndGrantsCategoriesIterator = contractsAndGrantsCategories.iterator();

        while (contractsAndGrantsCategoriesIterator.hasNext()) {
            ContractsAndGrantsCategories category = contractsAndGrantsCategoriesIterator.next();
            objectCodeArray.addAll(getObjectCodeArrayFromSingleCategory(category, document));
        }

        return objectCodeArray;
    }

    /**
     * This method returns a list of character strings that represent base 36 integers from start(non-inclusive) to limit
     * (inclusive).
     * 
     * @param start the starting point of the list. This value is not included in the list.
     * @param limit the ending point of the list. This value is included in the list
     * @return the list of strings
     * @throws IllegalArgumentException if start is not less than limit
     */
    public List<String> incrementAlphaNumericString(String stringToIncrement, String stringLimit) throws IllegalArgumentException {
        int startInt = Integer.parseInt(stringToIncrement, 36);
        int limitInt = Integer.parseInt(stringLimit, 36);
        if (startInt >= limitInt) {
            throw new IllegalArgumentException("Starting code must be less than limit code.");
        }
        List<String> retval = new ArrayList<String>();
        for (int i = startInt + 1; i <= limitInt; i++) {
            // format below forces the string back to 4 characters and replace makes the extra
            // characters '0'
            retval.add(String.format("%4s", Integer.toString(i, 36)).replace('\u0020', '0'));
        }
        return retval;
    }

}
