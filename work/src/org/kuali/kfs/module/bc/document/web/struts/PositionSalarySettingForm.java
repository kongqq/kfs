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
package org.kuali.module.budget.web.struts.form;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class...
 */
public class PositionSalarySettingForm extends KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PositionSalarySettingForm.class);

    private BudgetConstructionPosition budgetConstructionPosition; 

    //TODO probably need to push these and some url parms to new superclass BCExpansionForm??
    private boolean hideDetails = false;

    //TODO not sure editingMode is valid here since context is account,subaccount (document)
    //maybe bcdoc needs to have an editingMode map and an ojb ref to bcdoc added in bcaf?
    protected Map editingMode;

    // url parameters sent from BCDoc
    private String returnAnchor;
    private String returnFormKey;

    private Integer universityFiscalYear;
    private String positionNumber;

    //set and pass these when budgetByAccountMode to prefill the add line
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;

    //pass the value of this as a url parm, false setting means budget by organization
    //this also controls where we return the user when done
    private Boolean budgetByAccountMode;

    /**
     * Constructs a PositionSalarySettingForm.java.
     */
    public PositionSalarySettingForm() {
        super();
        setBudgetConstructionPosition(new BudgetConstructionPosition());
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);

        //TODO add insert line populate call here

        populateBCAFLines();
    }

    /**
     * This method iterates over all of the BCAF lines for the BudgetConstructionPosition
     * TODO verify this - and calls prepareAccountingLineForValidationAndPersistence on each one.
     * This is called to refresh ref objects for use by validation
     */
    protected void populateBCAFLines(){

        //TODO add bcaf totaling here??

        Iterator bcafLines = this.getBudgetConstructionPosition().getPendingBudgetConstructionAppointmentFunding().iterator();
        while (bcafLines.hasNext()){
            PendingBudgetConstructionAppointmentFunding bcafLine = (PendingBudgetConstructionAppointmentFunding) bcafLines.next();
            this.populateBCAFLine(bcafLine);
        }
    }

    /**
     * Populates the dependent fields of objects contained within the BCAF line
     * 
     * @param line
     */
    private void populateBCAFLine(PendingBudgetConstructionAppointmentFunding line){

//        final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "financialObject", "financialSubObject", "laborObject", "budgetConstructionMonthly"}));
      final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] {"chartOfAccounts", "account", "subAccount", "financialObject", "financialSubObject"}));
//        SpringServiceLocator.getPersistenceService().retrieveReferenceObjects(line, REFRESH_FIELDS);
        KNSServiceLocator.getPersistenceService().retrieveReferenceObjects(line, REFRESH_FIELDS);

    }
    /**
     * Gets the budgetConstructionPosition attribute. 
     * @return Returns the budgetConstructionPosition.
     */
    public BudgetConstructionPosition getBudgetConstructionPosition() {
        return budgetConstructionPosition;
    }

    /**
     * Sets the budgetConstructionPosition attribute value.
     * @param budgetConstructionPosition The budgetConstructionPosition to set.
     */
    public void setBudgetConstructionPosition(BudgetConstructionPosition budgetConstructionPosition) {
        this.budgetConstructionPosition = budgetConstructionPosition;
    }

    /**
     * Gets the positionNumber attribute. 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the returnAnchor attribute. 
     * @return Returns the returnAnchor.
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor attribute value.
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the returnFormKey attribute. 
     * @return Returns the returnFormKey.
     */
    public String getReturnFormKey() {
        return returnFormKey;
    }

    /**
     * Sets the returnFormKey attribute value.
     * @param returnFormKey The returnFormKey to set.
     */
    public void setReturnFormKey(String returnFormKey) {
        this.returnFormKey = returnFormKey;
    }

    /**
     * Gets the editingMode attribute. 
     * @return Returns the editingMode.
     */
    public Map getEditingMode() {
        return editingMode;
    }

    /**
     * Sets the editingMode attribute value.
     * @param editingMode The editingMode to set.
     */
    public void setEditingMode(Map editingMode) {
        this.editingMode = editingMode;
    }

    /**
     * Gets the hideDetails attribute. 
     * @return Returns the hideDetails.
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * 
     * @return hideDetails attribute
     * @see #isHideDetails()
     */
    public boolean getHideDetails() {
        return isHideDetails();
    }

   /**
     * Sets the hideDetails attribute value.
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }

}
