/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.kuali.module.gl.web.struts.action;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.query.Criteria;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.LookupService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.module.gl.bo.CorrectionChange;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.CorrectionCriteria;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.web.struts.form.CorrectionForm;

/**
 * @author Laran Evans <lc278@cornell.edu>
 *         Shawn Choo  <schoo@indiana.edu>
 * @version $Id: CorrectionAction.java,v 1.7 2006-06-05 02:14:35 schoo Exp $
 * 
 */

public class CorrectionAction extends KualiDocumentActionBase {
    
    //shawn
    private DateTimeService dateTimeService;
    private CorrectionForm errorCorrectionForm;
    private CorrectionDocument document;
    private OriginEntryGroupService originEntryGroupService;
    private OriginEntryService originEntryService;
    private OriginEntryDao originEntryDao;
    
    
    /**
     * Add a new CorrectionGroup to the Document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    
    
    
    public ActionForward uploadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) 
    throws FileNotFoundException, IOException {
        
        errorCorrectionForm = (CorrectionForm) form;
        
        originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
        originEntryService = (OriginEntryService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
        
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", true, false, false);
        
        /*String groupId[] = null;
        groupId[0] = newOriginEntryGroup.getId().toString();
        */
        errorCorrectionForm = (CorrectionForm) form;
        FormFile sourceFile = errorCorrectionForm.getSourceFile();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(sourceFile.getInputStream()));
        OriginEntry entryFromFile = new OriginEntry();
        
        try {
            String currentLine = br.readLine();
            while (currentLine != null) {
                entryFromFile.setFromTextFile(currentLine);
                
                entryFromFile.setEntryGroupId(newOriginEntryGroup.getId());
                originEntryService.createEntry(entryFromFile, newOriginEntryGroup);
                
                currentLine = br.readLine();
            }
        }
        finally {
            br.close();
        }
        
        HttpSession session = request.getSession(true);
        session.setAttribute("groupId", newOriginEntryGroup.getId().toString());
        
        if (errorCorrectionForm.getEditMethod().equals("manual")){
            
            showAllEntries(newOriginEntryGroup.getId().toString());
            session.setAttribute("allEntriesForManualEditHashMap", errorCorrectionForm.getAllEntriesForManualEditHashMap());
            session.setAttribute("allEntriesForManualEdit", errorCorrectionForm.getAllEntriesForManualEdit());
            session.setAttribute("updatedEntriesFromManualEdit", null);
            
        }
        
        if (errorCorrectionForm.getEditMethod().equals("criteria")){
            
            /*List correctionGroups = document.getCorrectionChangeGroup();
            String groupId[] = {newOriginEntryGroup.getId().toString()};
            
            searchAndReplace(groupId, correctionGroups);*/
        }
        
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
    
    
    public ActionForward addCorrectionGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) 
    throws Exception {
       
        errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        // create a new correction group and add it to the document
        document.addCorrectionGroup(new CorrectionChangeGroup());
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Remove an existing CorrectionChangeGroup from the Document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeCorrectionGroup(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
       
        errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        // load the correction group number from the request
        Integer groupNumber = 
            CorrectionActionHelper
                .getFromRequestCorrectionGroupNumberToRemove(request, document);
        
        // remove the correction group from the document
        document.removeCorrectionGroup(groupNumber);
        
        if(document.getCorrectionChangeGroup().isEmpty()) {
            document.addCorrectionGroup(new CorrectionChangeGroup());
        }
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Add a new search criteria to a specific group of criteria.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addSearchCriterion(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
       
        errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        Map groupNumbers = new HashMap();
        for(Iterator i = document.getCorrectionChangeGroup().iterator(); i.hasNext();) {
            CorrectionChangeGroup g = (CorrectionChangeGroup) i.next();
            groupNumbers.put(g.getCorrectionChangeGroupLineNumber(), g);
        }
        
        for(Iterator i = groupNumbers.keySet().iterator(); i.hasNext();) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupNumbers.get(i.next());
            
            // populate the criterion submitted to be added
            CorrectionCriteria criterion = 
                CorrectionActionHelper
                    .getFromRequestNewSearchCriterion(request, correctionGroup.getCorrectionChangeGroupLineNumber());
            
            if(null == criterion) {
                // not the criterion we intended to add.
                continue;
            }
            
            criterion.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
            
            // add the new criterion to the appropriate group of search criteria owned by the document
            correctionGroup.addSearchCriterion(criterion);
        }
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Remove a search criterion from a group.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeSearchCriterion(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
        
        errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        // load the correction group from the request
        Integer[] criterionIndex = 
            CorrectionActionHelper.getCriterionToDelete(request, document);
        CorrectionChangeGroup group = document.getCorrectionGroup(criterionIndex[0]);
        CorrectionCriteria criterion = group.getSearchCriterion(criterionIndex[1]);
        // remove the criterion from the search criteria owned by the correction group
        group.getCorrectionCriteria().remove(criterion);
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Add a new replacement specification to a specific group of specifications.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addReplacementSpecification(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
       
        errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        Map groupNumbers = new HashMap();
        for(Iterator i = document.getCorrectionChangeGroup().iterator(); i.hasNext();) {
			CorrectionChangeGroup g = (CorrectionChangeGroup) i.next();
			groupNumbers.put(g.getCorrectionChangeGroupLineNumber(), g);
		}
        
        for(Iterator i = groupNumbers.keySet().iterator(); i.hasNext();) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupNumbers.get(i.next());
            
            // populate the specification submitted to be added
            CorrectionChange specification = 
                CorrectionActionHelper
                    .getFromRequestNewReplacementSpecification(request, correctionGroup.getCorrectionChangeGroupLineNumber());
            
            if(null == specification) {
                // not the specification we intended to add.
                continue;
            }
            
            specification.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
            
            // add the new specification to the appropriate group of search criteria owned by the document
            correctionGroup.addReplacementSpecification(specification);
        }

        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Remove a replacement specification from a group.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeReplacementSpecification(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
       
        errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        // load the correction group from the request
        Integer[] specificationIndex = 
            CorrectionActionHelper.getSpecificationToDelete(request, document);
        CorrectionChangeGroup group = document.getCorrectionGroup(specificationIndex[0]);
        CorrectionChange specification = 
            group.getReplacementSpecification(specificationIndex[1]);
        // remove the criterion from the search criteria owned by the correction group
        group.getCorrectionChange().remove(specification);
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param reponse
     * @param withReplacement
     * @return
     * @throws Exception
     */
    public ActionForward searchAndReplaceWithCriteria(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse reponse)
    throws Exception {
        

        errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
                
        // Configure the query.
        //PersistenceBroker broker = PersistenceBrokerFactory.defaultPersistenceBroker();
        //Criteria criteria = new Criteria();
        String groupId[] = request.getParameterValues("pending-origin-entry-group-id");
        
        BusinessObject obj = null;
        Class example = OriginEntry.class;
        try {
            obj = (BusinessObject) example.newInstance();
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot get new instance of " + example.getName(), e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate " + example.getName(), e);
        }
        
        
        
        List correctionGroups = document.getCorrectionChangeGroup();
         
        //check required field and show error messages
        if (!checkEmpteyValues(correctionGroups, groupId)){
            CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
            
            return mapping.findForward(Constants.MAPPING_BASIC);
        }
       
        
        searchAndReplace(groupId, correctionGroups);
        
        
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadDocument(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response)    {
        
        errorCorrectionForm = (CorrectionForm) form;
        
        HttpSession session = request.getSession(true);
        String groupId[] = request.getParameterValues("pending-origin-entry-group-id");
        showAllEntries(groupId[0]);
        session.setAttribute("allEntriesForManualEditHashMap", errorCorrectionForm.getAllEntriesForManualEditHashMap());
        session.setAttribute("allEntriesForManualEdit", errorCorrectionForm.getAllEntriesForManualEdit());
        
        //shawn: 
        session.setAttribute("updatedEntriesFromManualEdit", null);
        
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    
    
 
    
    public String changeSearchField(String op, String field){
    
        if (op.equals("ne")){ return "!" + field; }
        
        if (op.equals("gt")){ return ">" + field; }
        
        if (op.equals("lt")){ return "<" + field; }
        
        if (op.equals("sw")){ return field + "%"; }
        
        if (op.equals("ew")){ return "%" + field; }
        
        return "%" + field + "%";
        
    }
    
    
    
    
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

      
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
    
    
    }
    
    
    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    
        return mapping.findForward(Constants.MAPPING_CANCEL);
    }

    
    
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
        return mapping.findForward(Constants.MAPPING_CLOSE);
    }   
    
    
    private boolean checkEmptyValues(CorrectionDocument document){
        
        
        return true;
    }
    
    
    private Map createSearchMap(CorrectionChangeGroup correctionGroup){
        Map fieldValues = new HashMap();
        CorrectionCriteria correctionSearchCriterion;
        Iterator fieldIter = correctionGroup.getCorrectionCriteria().iterator();
        while (fieldIter.hasNext()){
            correctionSearchCriterion = (CorrectionCriteria) fieldIter.next();
            String operator = correctionSearchCriterion.getOperator();
            String searchValue = correctionSearchCriterion.getCorrectionFieldValue();
            String searchField = correctionSearchCriterion.getCorrectionFieldName();
            if (!operator.equals("eq")){
                //Add operator
                searchValue = changeSearchField(operator, searchValue);
            }
            
            //check duplicate field, and make the Value list as Collection
            if (fieldValues.containsKey(searchField)){
                Collection fieldValueCollection = new ArrayList(); 
                
                if(fieldValues.get(searchField) instanceof Collection){
                    fieldValueCollection = (Collection) fieldValues.get(searchField);
                    
                } else {
                    String previousSearchValue = (String) fieldValues.get(searchField);
                    fieldValueCollection.add(previousSearchValue);
                }
                
                fieldValueCollection.add(searchValue);
                fieldValues.put(searchField, fieldValueCollection);
                
            } else {
                fieldValues.put(searchField, searchValue);
            }
        }
        return fieldValues;
    }
    
    private void createNewOutput(OriginEntryGroup newOriginEntryGroup, String groupId){
            
            originEntryDao = (OriginEntryDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryDao");
            originEntryService = (OriginEntryService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
            // Collection returnCollection = new ArrayList();
            //create all OriginEntry which will change GroupID to newOriginEntryGroup ID
            Map groupIdMap = new HashMap();
            groupIdMap.put("entryGroupId", groupId);
            
            //get all Entries
            Collection oldEntries = originEntryDao.getMatchingEntriesByCollection(groupIdMap);
            
            //change the Entries' groupId and create all
            Iterator oldEntryIter = oldEntries.iterator();
            while (oldEntryIter.hasNext()){
                OriginEntry eachEntry = (OriginEntry) oldEntryIter.next();
                eachEntry.setEntryGroupId(newOriginEntryGroup.getId());
                //returnCollection.add(eachEntry);
                originEntryService.createEntry(eachEntry, newOriginEntryGroup);
                
                }
            
            //return returnCollection;
    }
    
    public void replaceOriginEntryValues(OriginEntry eachReplaceEntries, CorrectionChangeGroup correctionGroup) throws Exception{
        originEntryDao = (OriginEntryDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryDao");
        CorrectionChange correctionReplacementSpecification;
        Iterator replaceIter = correctionGroup.getCorrectionChange().iterator();
        
        while (replaceIter.hasNext()){
            correctionReplacementSpecification = (CorrectionChange) replaceIter.next();
            String replaceValue = correctionReplacementSpecification.getCorrectionFieldValue();
            String replaceField = correctionReplacementSpecification.getCorrectionFieldName();
               
                if (replaceField.equals("accountNumber")){
                    eachReplaceEntries.setAccountNumber(replaceValue);
                }
                if (replaceField.equals("financialDocumentNumber")){
                    eachReplaceEntries.setFinancialDocumentNumber(replaceValue);
                }
                if (replaceField.equals("referenceFinancialDocumentNumber")){
                    eachReplaceEntries.setReferenceFinancialDocumentNumber(replaceValue);
                }
                if (replaceField.equals("referenceFinancialDocumentTypeCode")){
                    eachReplaceEntries.setReferenceFinancialDocumentTypeCode(replaceValue);
                }
                if (replaceField.equals("financialDocumentReversalDate")){
                    //convert String to Date
                    Date convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(replaceValue);
                    eachReplaceEntries.setFinancialDocumentReversalDate(convertDate);
                }
                if (replaceField.equals("financialDocumentTypeCode")){
                    eachReplaceEntries.setFinancialDocumentTypeCode(replaceValue);
                }
                if (replaceField.equals("financialBalanceTypeCode")){
                    eachReplaceEntries.setFinancialBalanceTypeCode(replaceValue);
                }
                if (replaceField.equals("chartOfAccountsCode")){
                    eachReplaceEntries.setChartOfAccountsCode(replaceValue);
                }
                if (replaceField.equals("financialObjectTypeCode")){
                    eachReplaceEntries.setFinancialObjectTypeCode(replaceValue);
                }
                if (replaceField.equals("financialObjectCode")){
                    eachReplaceEntries.setFinancialObjectCode(replaceValue);
                }
                if (replaceField.equals("financialSubObjectCode")){
                    eachReplaceEntries.setFinancialSubObjectCode(replaceValue);
                }
                if (replaceField.equals("financialSystemOriginationCode")){
                    eachReplaceEntries.setFinancialSystemOriginationCode(replaceValue);
                }
                if (replaceField.equals("organizationDocumentNumber")){
                    eachReplaceEntries.setOrganizationDocumentNumber(replaceValue);
                }
                if (replaceField.equals("organizationReferenceId")){
                    eachReplaceEntries.setOrganizationReferenceId(replaceValue);
                }
                if (replaceField.equals("projectCode")){
                    eachReplaceEntries.setProjectCode(replaceValue);
                }
                if (replaceField.equals("subAccountNumber")){
                    eachReplaceEntries.setSubAccountNumber(replaceValue);
                }
                if (replaceField.equals("transactionDate")){
                    //convert String to Date
                    Date convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(replaceValue);
                    eachReplaceEntries.setFinancialDocumentReversalDate(convertDate);
                }
                if (replaceField.equals("transactionDebitCreditCode")){
                    eachReplaceEntries.setTransactionDebitCreditCode(replaceValue);
                }
                if (replaceField.equals("transactionEncumbranceUpdateCode")){
                    eachReplaceEntries.setTransactionEncumbranceUpdateCode(replaceValue);
                }
                if (replaceField.equals("transactionLedgerEntrySequenceNumber")){
                    //convert String to Integer
                    int convertInt = Integer.parseInt(replaceValue);
                    eachReplaceEntries.setTransactionLedgerEntrySequenceNumber(new Integer(convertInt));
                }
                if (replaceField.equals("transactionLedgerEntryAmount")){
                    
                    eachReplaceEntries.setTransactionLedgerEntryAmount(new KualiDecimal(replaceValue));
                }
                if (replaceField.equals("transactionLedgerEntryDescription")){
                    eachReplaceEntries.setTransactionLedgerEntryDescription(replaceValue);
                }
                if (replaceField.equals("universityFiscalPeriodCode")){
                    eachReplaceEntries.setUniversityFiscalPeriodCode(replaceValue);
                }
                if (replaceField.equals("universityFiscalYear")){
                    //convert String to Integer
                    int convertInt = Integer.parseInt(replaceValue);
                    eachReplaceEntries.setUniversityFiscalYear(new Integer(convertInt));
                }
                if (replaceField.equals("budgetYear")){
                    eachReplaceEntries.setBudgetYear(replaceValue);
                }
                
                originEntryDao.saveOriginEntry(eachReplaceEntries);
                
            }   
        }
    
    
    
    public boolean checkEmpteyValues(List correctionGroups, String[] groupId){
        boolean returnVal = true; 
        
        //if the description is required, then  
        /*if (document.getDocumentHeader().getFinancialDocumentDescription() == null) {
            //GlobalVariables.getErrorMap().put("documentHeader.financialDocumentDescription", KeyConstants.ERROR_REQUIRED, "Financial Document Description");
            GlobalVariables.getErrorMap().put("document.documentHeader.financialDocumentDescription", KeyConstants.ERROR_REQUIRED, "Financial Document Description");
        }*/
         
        if (groupId== null) {
            GlobalVariables.getErrorMap().put("searchFieldError", 
                    KeyConstants.ERROR_GL_ERROR_CORRECTION_ORIGINGROUP_REQUIRED);
            returnVal = false;
        }
        Iterator groupIter = correctionGroups.iterator();
        while (groupIter.hasNext()) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupIter.next();
            if (correctionGroup.getCorrectionCriteria().size() < 1){
                //displaing error message
                GlobalVariables.getErrorMap().put("searchFieldError", 
                        KeyConstants.ERROR_GL_ERROR_CORRECTION_SEARCHFIELD_REQUIRED);
                returnVal = false;
            }
            if (correctionGroup.getCorrectionChange().size() < 1){
                GlobalVariables.getErrorMap().put("searchFieldError", 
                        KeyConstants.ERROR_GL_ERROR_CORRECTION_MODIFYFIELD_REQUIRED);
                returnVal = false;
            }
            
        }
        
        return returnVal;
    }
    
    
    
    public ActionForward chooseRadio(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response)
    throws Exception {
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        errorCorrectionForm.setChooseSystem(request.getParameter("chooseSystem"));
        //String chooseSystem = request.getParameter("chooseSystem");
        //request.setAttribute("chooseSystem", chooseSystem);
        errorCorrectionForm.setEditMethod(request.getParameter("editMethod"));
        //String editMethod = request.getParameter("editMethod");
        //request.setAttribute("editMethod", editMethod);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    
    
    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        

        
        
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }    
    
    public ActionForward showOneEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        errorCorrectionForm = (CorrectionForm) form;
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        String stringEditEntryId = request.getParameter("methodToCall.showOneEntry");
        Integer editEntryId = Integer.parseInt(stringEditEntryId);
       
        OriginEntry eachEntry = (OriginEntry) errorCorrectionForm.getAllEntriesForManualEditHashMap().get(editEntryId);
        errorCorrectionForm.setEachEntryForManualEdit(eachEntry);
        
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }    
    
    public ActionForward editEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        errorCorrectionForm = (CorrectionForm) form;
     
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        Date convertDate;
        int convertInt;
        
        String stringEditEntryId = request.getParameter("methodToCall.editEntry");
        Integer editEntryId = Integer.parseInt(stringEditEntryId);
        
        
        OriginEntry oe = new OriginEntry();
        
        
        String editAccountNumber = request.getParameter("editAccountNumber");
        String editFinancialDocumentNumber = request.getParameter("editFinancialDocumentNumber");
        String editReferenceFinancialDocumentNumber = request.getParameter("editReferenceFinancialDocumentNumber");
        String editReferenceFinancialDocumentTypeCode = request.getParameter("editReferenceFinancialDocumentTypeCode");
        String editFinancialDocumentReversalDate = request.getParameter("editFinancialDocumentReversalDate");
        String editFinancialDocumentTypeCode = request.getParameter("editFinancialDocumentTypeCode");
        String editFinancialBalanceTypeCode = request.getParameter("editFinancialBalanceTypeCode");
        String editChartOfAccountsCode = request.getParameter("editChartOfAccountsCode");
        String editFinancialObjectTypeCode = request.getParameter("editFinancialObjectTypeCode");
        String editFinancialObjectCode = request.getParameter("editFinancialObjectCode");
        String editFinancialSubObjectCode = request.getParameter("editFinancialSubObjectCode");
        String editFinancialSystemOriginationCode = request.getParameter("editFinancialSystemOriginationCode");
        String editReferenceFinancialSystemOriginationCode = request.getParameter("editReferenceFinancialSystemOriginationCode");
        String editOrganizationDocumentNumber = request.getParameter("editOrganizationDocumentNumber");
        String editOrganizationReferenceId = request.getParameter("editOrganizationReferenceId");
        String editProjectCode = request.getParameter("editProjectCode");
        String editSubAccountNumber = request.getParameter("editSubAccountNumber");
        String editTransactionDate = request.getParameter("editTransactionDate");
        String editTransactionDebitCreditCode = request.getParameter("editTransactionDebitCreditCode");
        String editTransactionEncumbranceUpdateCode = request.getParameter("editTransactionEncumbranceUpdateCode");
        String editTransactionLedgerEntrySequenceNumber = request.getParameter("editTransactionLedgerEntrySequenceNumber");
        String editTransactionLedgerEntryAmount = request.getParameter("editTransactionLedgerEntryAmount");
        String editTransactionLedgerEntryDescription = request.getParameter("editTransactionLedgerEntryDescription");
        String editUniversityFiscalPeriodCode = request.getParameter("editUniversityFiscalPeriodCode");
        String editUniversityFiscalYear = request.getParameter("editUniversityFiscalYear");
        String editBudgetYear = request.getParameter("editBudgetYear");
        
        oe.setEntryId(editEntryId);
        oe.setAccountNumber(editAccountNumber);
        oe.setFinancialDocumentNumber(editFinancialDocumentNumber);
        oe.setReferenceFinancialDocumentNumber(editReferenceFinancialDocumentNumber);
        oe.setReferenceFinancialDocumentTypeCode(editReferenceFinancialDocumentTypeCode);
        
        if (!(editFinancialDocumentReversalDate == null | editFinancialDocumentReversalDate.equals(""))) {
            convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(editFinancialDocumentReversalDate);
            oe.setFinancialDocumentReversalDate(convertDate);
        }
        
        oe.setFinancialDocumentTypeCode(editFinancialDocumentTypeCode);
        oe.setFinancialBalanceTypeCode(editFinancialBalanceTypeCode);
        oe.setChartOfAccountsCode(editChartOfAccountsCode);
        oe.setFinancialObjectTypeCode(editFinancialObjectTypeCode);
        oe.setFinancialObjectCode(editFinancialObjectCode);
        oe.setFinancialSubObjectCode(editFinancialSubObjectCode);
        oe.setFinancialSystemOriginationCode(editFinancialSystemOriginationCode);
        oe.setReferenceFinancialSystemOriginationCode(editReferenceFinancialSystemOriginationCode);
        oe.setOrganizationDocumentNumber(editOrganizationDocumentNumber);
        oe.setOrganizationReferenceId(editOrganizationReferenceId);
        oe.setProjectCode(editProjectCode);
        oe.setSubAccountNumber(editSubAccountNumber);
        
        if (!(editTransactionDate == null | editTransactionDate.equals(""))) {
            convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(editTransactionDate);
            oe.setTransactionDate(convertDate);
        }
        
        oe.setTransactionDebitCreditCode(editTransactionDebitCreditCode);
        oe.setTransactionEncumbranceUpdateCode(editTransactionEncumbranceUpdateCode);
        
        if (!(editTransactionLedgerEntrySequenceNumber == null | editTransactionLedgerEntrySequenceNumber.equals(""))) {
            convertInt = Integer.parseInt(editTransactionLedgerEntrySequenceNumber);
            oe.setTransactionLedgerEntrySequenceNumber(new Integer(convertInt));
        }
        if (!(editTransactionLedgerEntryAmount == null | editTransactionLedgerEntryAmount.equals(""))) {
            oe.setTransactionLedgerEntryAmount(new KualiDecimal(editTransactionLedgerEntryAmount));
        }
        
        oe.setTransactionLedgerEntryDescription(editTransactionLedgerEntryDescription);
        oe.setUniversityFiscalPeriodCode(editUniversityFiscalPeriodCode);
        
        if (!(editUniversityFiscalYear == null | editUniversityFiscalYear.equals(""))) {
            convertInt = Integer.parseInt(editUniversityFiscalYear);
            oe.setUniversityFiscalYear(new Integer(convertInt));
        }
        
        oe.setBudgetYear(editBudgetYear);
        
        
        HttpSession session = request.getSession(true);
        
        

        Collection updatedEntriesFromManualEdit = errorCorrectionForm.getUpdatedEntriesFromManualEdit();
        //remove entry which is already in updatedEntries
        Iterator iter = updatedEntriesFromManualEdit.iterator();
        while (iter.hasNext()){
            OriginEntry eachEntry = (OriginEntry) iter.next();
            if (eachEntry.getEntryId().equals(editEntryId)) {
                updatedEntriesFromManualEdit.remove(eachEntry);
                
            }
            if (updatedEntriesFromManualEdit.isEmpty()){
                break;
            }
        }
        updatedEntriesFromManualEdit.add(oe);
        errorCorrectionForm.setUpdatedEntriesFromManualEdit(updatedEntriesFromManualEdit);
        session.setAttribute("updatedEntriesFromManualEdit", errorCorrectionForm.getUpdatedEntriesFromManualEdit());
        
        
        
        //remove the OriginEntry from list
        
        Map allEntriesForManualEditHashMap = errorCorrectionForm.getAllEntriesForManualEditHashMap();
        //Collection allEntriesForManualEdit = errorCorrectionForm.getAllEntriesForManualEdit();
        
        //update the list
        //allEntriesForManualEdit.remove(allEntriesForManualEditHashMap.get(oe.getEntryId()));
        allEntriesForManualEditHashMap.remove(oe.getEntryId());
        allEntriesForManualEditHashMap.put(oe.getEntryId(), oe);
        
        //errorCorrectionForm.setAllEntriesForManualEditHashMap(allEntriesForManualEditHashMap);
        //errorCorrectionForm.setAllEntriesForManualEdit(allEntriesForManualEdit);
        errorCorrectionForm.setAllEntriesForManualEditHashMap(allEntriesForManualEditHashMap);
        
        //session.setAttribute("allEntriesForManualEditHashMap", errorCorrectionForm.getAllEntriesForManualEditHashMap());
        //session.setAttribute("allEntriesForManualEdit", errorCorrectionForm.getAllEntriesForManualEdit());
       
        return mapping.findForward(Constants.MAPPING_BASIC);
        
       
    }
    
    
    public ActionForward searchForManualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        HttpSession session = request.getSession(true);
        String groupId;
       
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        List correctionGroups = document.getCorrectionChangeGroup();
        
        //if user upload a file, then the groupId should get from session
        if (errorCorrectionForm.getChooseSystem().equals("file") & session.getAttribute("groupId") != null ) {
            groupId = (String) session.getAttribute("groupId");
        } else {
            groupId = request.getParameter("pending-origin-entry-group-id");
            
        }
        
        CorrectionChangeGroup correctionGroup;
        
        Map fieldValues = new HashMap();
        Collection searchResults = null;
            
            
        Iterator groupIter = correctionGroups.iterator();
           
        while (groupIter.hasNext()) {
        
            correctionGroup = (CorrectionChangeGroup) groupIter.next();
            fieldValues = createSearchMap(correctionGroup);
            
            fieldValues.put("entryGroupId", groupId);
            }
        
            
        LookupService lookupService = (LookupService) SpringServiceLocator.getBeanFactory().getBean("lookupService");
        //searchResults is collection of OriginEntry
        searchResults = (Collection) lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);
        
        errorCorrectionForm.setAllEntriesForManualEdit(searchResults);
        
        session.setAttribute("allEntriesForManualEditHashMap", errorCorrectionForm.getAllEntriesForManualEditHashMap());
        session.setAttribute("allEntriesForManualEdit", errorCorrectionForm.getAllEntriesForManualEdit()); 
        
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
    
    public OriginEntry setOriginEntryValues(OriginEntry oe){
        
        return oe;
    }
    
    
    public ActionForward manualErrorCorrection(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        errorCorrectionForm = (CorrectionForm) form;
        originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
        originEntryService = (OriginEntryService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", true, true, false);
        
        HttpSession session = request.getSession(true);
        Map allEntriesForManualEditHashMap = errorCorrectionForm.getAllEntriesForManualEditHashMap();
        
        Iterator entryIter = allEntriesForManualEditHashMap.values().iterator();
        
        while (entryIter.hasNext()){
            OriginEntry eachEntry = (OriginEntry) entryIter.next();
            eachEntry.setEntryGroupId(newOriginEntryGroup.getId());
            originEntryService.createEntry(eachEntry, newOriginEntryGroup);
            }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
    
    
    public void showAllEntries(String groupId){
        
        originEntryDao = (OriginEntryDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryDao");
        Map searchMap = new HashMap();
        
        // Configure the query.
        
        searchMap.put("entryGroupId", groupId);
        Collection resultFromGroupId = originEntryDao.getMatchingEntriesByCollection(searchMap);
        errorCorrectionForm.setAllEntriesForManualEdit(resultFromGroupId);
        
        
        //all entries to a Map
        Iterator iter = resultFromGroupId.iterator();
        while (iter.hasNext()){
            OriginEntry oe = (OriginEntry) iter.next();
            errorCorrectionForm.getAllEntriesForManualEditHashMap().put(oe.getEntryId(), oe);
        }
        
   }
   
    public void searchAndReplace(String[] groupId, List correctionGroups){
        
        originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
        Map fieldValues = new HashMap();
        CorrectionChangeGroup correctionGroup;
        Collection searchResults = null;
        OriginEntryGroup newOriginEntryGroup = null;
        
        boolean createGroupFlag = false;
        //message for test result
        String message= ""; 
        //search using Groups and Fields
        for (int i=0; i<groupId.length; i+=1) {
            
            
            Iterator groupIter = correctionGroups.iterator();
            
            //tempGroupNumber is just for test
            int tempGroupNumber = 1; 
            
            while (groupIter.hasNext()) {
                correctionGroup = (CorrectionChangeGroup) groupIter.next();
                    
              
                    
                //Set a Map with fields
                fieldValues = createSearchMap(correctionGroup);
                
                fieldValues.put("entryGroupId", groupId[i]);
                
                LookupService lookupService = (LookupService) SpringServiceLocator.getBeanFactory().getBean("lookupService");
                
                //searchResults is collection of OriginEntry
                searchResults = (Collection) lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);
        
                
                //below 4-5 lines are just for test 
                String convert = String.valueOf(searchResults.size());
                //Long resultSize = Long.valueOf(convert);
                message += "OriginGroup " + groupId[i] + ",  Group : " + convert + "items found. /     ";
                tempGroupNumber += 1;
               
               
                if (searchResults.size() > 0) {
                    
                    if (!createGroupFlag){ 
                        //creat Output
                        //create an OriginEntryGroup
                        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                        newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", true, true, false);
                    
                        createNewOutput(newOriginEntryGroup, groupId[i]);
                        createGroupFlag = true;
                    } 
                    fieldValues.remove("entryGroupId");
                    fieldValues.put("entryGroupId", newOriginEntryGroup.getId().toString());
                    Collection replaceEntries = null;
                    
                    //search entries to replace and save
                    replaceEntries = (Collection) lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);
                    Iterator replaceEntriesIter = replaceEntries.iterator();
                    while (replaceEntriesIter.hasNext()) {
                        OriginEntry eachReplaceEntries = (OriginEntry) replaceEntriesIter.next();
                        //replace entries
                        try {
                            replaceOriginEntryValues(eachReplaceEntries, correctionGroup);
                        }
                        catch (Exception e) {}
                    }
                    
                }
                //initialize search field
                fieldValues = new HashMap();
            
       }
            createGroupFlag = false;
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        }
    }
    
    public ActionForward fileUploadSearchAndReplaceWithCriteria(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse reponse)
    throws Exception {
      
        errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        //TODO: Change later. The purpose of this method is just for groupId. 
        //This method will be useless if groupId set from session using session "chooseSystem".
        
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
                
        // Configure the query.
        //PersistenceBroker broker = PersistenceBrokerFactory.defaultPersistenceBroker();
        
        HttpSession session = request.getSession(true);
        String groupId[] = {(String) session.getAttribute("groupId")};
        
        List correctionGroups = document.getCorrectionChangeGroup();
         
        //check required field and show error messages
        /*if (!checkEmpteyValues(correctionGroups, groupId)){
            CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
            
            return mapping.findForward(Constants.MAPPING_BASIC);
        }*/
       
        
        searchAndReplace(groupId, correctionGroups);
        
        
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    
    }
    
}

