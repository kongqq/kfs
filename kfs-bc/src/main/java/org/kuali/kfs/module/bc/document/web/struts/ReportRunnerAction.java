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
package org.kuali.kfs.module.bc.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRParameter;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BudgetConstructionReportMode;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountMonthlyDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountSalaryDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionDocumentAccountObjectDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.util.BudgetUrlUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Action class to display document reports and dumps menu
 */
public class ReportRunnerAction extends BudgetExpansionAction {

    /**
     * Handles any special onetime setup when called from another screen action
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ReportRunnerForm reportRunnerForm = (ReportRunnerForm) form;

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Runs the reports or dump selected by the user using the BudgetConstructionDocumentReportMode to help determine the various
     * objects needed to actually build the report data and render the report.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performReportDump(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ReportRunnerForm reportRunnerForm = (ReportRunnerForm) form;
        String principalName = GlobalVariables.getUserSession().getPerson().getPrincipalId();

        int selectIndex = this.getSelectedLine(request);
        String reportModeName = reportRunnerForm.getBudgetConstructionDocumentReportModes().get(selectIndex).getReportModeName();

        Collection reportSet = new ArrayList();
        String jasperFileName = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        switch (selectIndex) {

            case 0: {
                jasperFileName = "BudgetAccountObjectDetail";
                SpringContext.getBean(BudgetConstructionDocumentAccountObjectDetailReportService.class).updateDocumentAccountObjectDetailReportTable(principalName, reportRunnerForm.getDocumentNumber(), reportRunnerForm.getUniversityFiscalYear(), reportRunnerForm.getChartOfAccountsCode(), reportRunnerForm.getAccountNumber(), reportRunnerForm.getSubAccountNumber());
                reportSet = SpringContext.getBean(BudgetConstructionDocumentAccountObjectDetailReportService.class).buildReports(principalName);
                break;
            }


            case 1: {
                jasperFileName = "BudgetAccountSalaryDetail";
                reportSet = SpringContext.getBean(BudgetConstructionAccountSalaryDetailReportService.class).buildReports(reportRunnerForm.getUniversityFiscalYear(), reportRunnerForm.getChartOfAccountsCode(), reportRunnerForm.getAccountNumber(), reportRunnerForm.getSubAccountNumber());
                break;
            }


            case 2: {
                jasperFileName = "BudgetAccountMonthlyDetail";
                reportSet = SpringContext.getBean(BudgetConstructionAccountMonthlyDetailReportService.class).buildReports(reportRunnerForm.getDocumentNumber(), reportRunnerForm.getUniversityFiscalYear(), reportRunnerForm.getChartOfAccountsCode(), reportRunnerForm.getAccountNumber(), reportRunnerForm.getSubAccountNumber());
                break;
            }
            case 3: {
                return new ActionForward(buildReportExportForwardURL(reportRunnerForm, mapping, BudgetConstructionReportMode.ACCOUNT_EXPORT.reportModeName), true);
            }

            case 4: {
                return new ActionForward(buildReportExportForwardURL(reportRunnerForm, mapping, BudgetConstructionReportMode.FUNDING_EXPORT.reportModeName), true);
            }

            case 5: {
                return new ActionForward(buildReportExportForwardURL(reportRunnerForm, mapping, BudgetConstructionReportMode.MONTHLY_EXPORT.reportModeName), true);
            }

        }

        if (reportSet.isEmpty()) {
            List<String> messageList = new ArrayList<String>();
            messageList.add(BCConstants.Report.MSG_REPORT_NO_DATA);
            SpringContext.getBean(BudgetConstructionReportsServiceHelper.class).generatePdf(messageList, baos);
            WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, jasperFileName + ReportGeneration.PDF_FILE_EXTENSION);
        }
        else {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(BCConstants.Report.REPORT_MESSAGES_CLASSPATH, Locale.getDefault());
            Map<String, Object> reportData = new HashMap<String, Object>();
            reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);

            SpringContext.getBean(ReportGenerationService.class).generateReportToOutputStream(reportData, reportSet, BCConstants.Report.REPORT_TEMPLATE_CLASSPATH + jasperFileName, baos);
            WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, jasperFileName + ReportGeneration.PDF_FILE_EXTENSION);
        }
        return null;
    }

    /**
     * Builds URL for the report dump url.
     */
    private String buildReportExportForwardURL(ReportRunnerForm reportRunnerForm, ActionMapping mapping, String documentReportMode) {
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, reportRunnerForm.getChartOfAccountsCode());
        parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, reportRunnerForm.getAccountNumber());
        parameters.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, reportRunnerForm.getSubAccountNumber());
        parameters.put(BCConstants.Report.REPORT_MODE, documentReportMode);
        parameters.put(BCConstants.IS_ORG_REPORT_REQUEST_PARAMETER, "false");

        return BudgetUrlUtil.buildBudgetUrl(mapping, reportRunnerForm, BCConstants.REPORT_EXPORT_ACTION, parameters);
    }
}
