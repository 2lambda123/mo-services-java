/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO COM Test bed
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package org.ccsds.moims.mo.com.test.activity;

import static org.ccsds.moims.mo.com.test.util.COMTestHelper.OBJ_NO_ASE_OPERATION_ACTIVITY;
import static org.ccsds.moims.mo.testbed.util.LoggingBase.logMessage;

/**
 *
 * Holds the instance identifier values received by a listener for events
 * generated by activity objects.
 */
public class InstIdLists {
    // The instance ID shall be unique for each COM type, so we maintain
    // an array with an entry for each type which holds all the instance identifiers
    // used to date

    InstIDList lists[] = new InstIDList[OBJ_NO_ASE_OPERATION_ACTIVITY];
    // Singleton so this varible holds the object
    static InstIdLists obj = null;

    public static InstIdLists inst() {
        if (obj == null) {
            obj = new InstIdLists();
        }
        return obj;
    }

    public InstIdLists() {
        for (int i = 0; i < lists.length; i++) {
            lists[i] = new InstIDList();
        }
    }

    public boolean add(int objectNumber, Long instId) {
        boolean bValid = false;
        try {
            bValid = (lists[objectNumber].add(instId));
        } catch (Exception ex) {
            logMessage("InstIdlists:add + " + ex + " " + objectNumber);
        }
        return bValid;
    }

    // Removes all entries
    public void reset() {
        for (int i = 0; i < lists.length; i++) {
            lists[i].clear();
        }
    }

    private class InstIDList extends java.util.ArrayList<Long> {

        @Override
        public boolean add(Long instId) {
            boolean bValid = false;
            if (!contains(instId)) {
                bValid = true;
                super.add(instId);
            }
            return bValid;
        }
    }
}
