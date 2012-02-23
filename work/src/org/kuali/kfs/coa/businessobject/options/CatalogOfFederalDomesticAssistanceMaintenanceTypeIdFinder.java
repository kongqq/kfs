/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.CFDA;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Allows some information about persisted {@link Cfda} instances to be looked up.
 */
public class CatalogOfFederalDomesticAssistanceMaintenanceTypeIdFinder extends KeyValuesBase {

    /**
     * Retrieves the list of possible CFDA Maintenance Type IDs and generates a collection with all the possible values.
     *
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<CFDA> codes = boService.findAll(CFDA.class);

        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));

        for ( CFDA cfdaReference : codes ) {

            if (!isDuplicateValue(labels, cfdaReference.getCfdaMaintenanceTypeId())) {
                labels.add(new ConcreteKeyValue(cfdaReference.getCfdaMaintenanceTypeId(), cfdaReference.getCfdaMaintenanceTypeId()));
            }
        }

        return labels;
    }

    /**
     * This method determines if a value already exists in the collection.
     *
     * @param collection The collection to be examined.
     * @param value The value to be added to the collection if it does not already exist within it.
     * @return True if the value passed in already exists in the collection, false otherwise.
     */
    private boolean isDuplicateValue(List<KeyValue> collection, String value) {
        boolean duplicate = false;

        for (KeyValue klp : collection) {
            String klpLabel = klp.getValue();
            if (klpLabel != null) {
                duplicate |= klpLabel.trim().equalsIgnoreCase(value.trim());
            }
        }

        return duplicate;
    }

}
