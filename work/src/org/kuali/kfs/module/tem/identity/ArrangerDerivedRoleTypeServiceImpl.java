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
package org.kuali.kfs.module.tem.identity;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.service.TEMRoleService;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Check for Arranger Derived Role base on the TEM Profile ID and the Principal
 */
public class ArrangerDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
    
    TEMRoleService temRoleService;

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification) {
        //first we need to grab the profileId if it exists
        if(qualification!=null && !qualification.isEmpty()){
            String profileId = qualification.get(TEMProfileProperties.PROFILE_ID);
            String documentType = qualification.get(KimAttributes.DOCUMENT_TYPE_NAME);
            if(ObjectUtils.isNotNull(profileId)) {
                return temRoleService.isTravelDocumentArrangerForProfile(documentType, principalId, Integer.valueOf(profileId));
            }
        } 
        
        //Because workflow (route/save/copy) would not pick up the qualifer from Document Authorizor, but ONLY base on the permission template, we will
        //simply check whether the person is an arranger (not particularly tied to a profile)
        return temRoleService.isProfileArranger(principalId);
    }

    @Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        final List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>(1);
        return members;
    }

    public void setTemRoleService(TEMRoleService temRoleService) {
        this.temRoleService = temRoleService;
    }

}