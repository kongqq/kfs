/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.kuali.kfs.module.external.kc.dto.AccountCreationStatusDTO;
import org.kuali.kfs.module.external.kc.dto.AccountParametersDTO;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;

@ConfigureContext(session = khuntley)
public class AccountCreationServiceTest extends KualiTestBase 
{
    private AccountCreationService accountCreationService;
    private AccountParametersDTO accountParameters;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception 
    {
        // Initialize service objects.
        accountCreationService = 
            SpringContext.getBean(AccountCreationService.class);
        
        // Initialize objects.
        accountParameters = new AccountParametersDTO();
        accountParameters.setAccountName("accountName");
        accountParameters.setAccountNumber("123456");
        
        super.setUp();
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception 
    {
        super.tearDown();
    }
    
    /**
     * This method tests the service locally
     */
    public void testCreateAccountServiceLocally() 
    {   
        AccountCreationStatusDTO creationStatus =
            accountCreationService.createAccount(accountParameters);
    
        System.out.println("account number: " + creationStatus.getAccountNumber()); 
        
        assertTrue(creationStatus.isSuccess());
    }

    
    /**
     * This method tests the service using KSB, but locally 
     */
    public void testCreateAccountServiceWithKSB() {
        
        try {                        
            AccountCreationService accountService = (AccountCreationService) GlobalResourceLoader.getService(new QName("KFS", "accountCreationServiceSOAP"));  
            //AccountCreationService accountService = (AccountCreationService) GlobalResourceLoader.getService("{KFS}accountCreationServiceSOAP"); // error
            
            AccountCreationStatusDTO creationStatus = accountService.createAccount(accountParameters);        
            System.out.println("account number: " + creationStatus.getAccountNumber());        
            assertTrue(creationStatus.isSuccess());
            
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

}
