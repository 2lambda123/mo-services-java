/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO MAL Java Implementation
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
package esa.mo.mal.impl.interactionpatterns;

import esa.mo.mal.impl.Address;
import esa.mo.mal.impl.MALSender;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.provider.MALProgress;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;

/**
 * Progress interaction class.
 */
public class ProgressIPProviderHandler extends IPProviderHandler implements MALProgress {

    private boolean ackSent = false;

    /**
     * Constructor.
     *
     * @param sender Used to return the messages.
     * @param address Details of this endpoint.
     * @param msg The source message.
     * @throws MALInteractionException if the received message operation is
     * unknown.
     */
    public ProgressIPProviderHandler(final MALSender sender, final Address address,
            final MALMessage msg) throws MALInteractionException {
        super(sender, address, msg);
    }

    @Override
    public MALMessage sendAcknowledgement(final Object... result)
            throws MALInteractionException, MALException {
        ackSent = true;
        return returnResponse(MALProgressOperation.PROGRESS_ACK_STAGE, result);
    }

    @Override
    public MALMessage sendAcknowledgement(final MALEncodedBody body)
            throws MALInteractionException, MALException {
        ackSent = true;
        return returnResponse(MALProgressOperation.PROGRESS_ACK_STAGE, body);
    }

    @Override
    public MALMessage sendUpdate(final Object... update) throws MALException {
        return returnResponse(MALProgressOperation.PROGRESS_UPDATE_STAGE, update);
    }

    @Override
    public MALMessage sendUpdate(final MALEncodedBody body)
            throws MALInteractionException, MALException {
        return returnResponse(MALProgressOperation.PROGRESS_UPDATE_STAGE, body);
    }

    @Override
    public MALMessage sendResponse(final Object... result)
            throws MALInteractionException, MALException {
        return returnResponse(MALProgressOperation.PROGRESS_RESPONSE_STAGE, result);
    }

    @Override
    public MALMessage sendResponse(final MALEncodedBody body)
            throws MALInteractionException, MALException {
        return returnResponse(MALProgressOperation.PROGRESS_RESPONSE_STAGE, body);
    }

    @Override
    public MALMessage sendError(final MOErrorException error) throws MALException {
        UOctet stage = MALProgressOperation.PROGRESS_ACK_STAGE;

        if (ackSent) {
            stage = MALProgressOperation.PROGRESS_RESPONSE_STAGE;
        }

        return returnError(stage, error);
    }

    @Override
    public MALMessage sendUpdateError(final MOErrorException error) throws MALException {
        return returnError(MALProgressOperation.PROGRESS_UPDATE_STAGE, error);
    }
}
