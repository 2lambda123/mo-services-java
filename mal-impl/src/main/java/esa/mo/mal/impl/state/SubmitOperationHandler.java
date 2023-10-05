/* ----------------------------------------------------------------------------
 * Copyright (C) 2016      European Space Agency
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
package esa.mo.mal.impl.state;

import esa.mo.mal.impl.MALContextFactoryImpl;
import java.util.Map;
import java.util.logging.Level;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Handles the state machine for a consumer for an SUBMIT operation.
 */
public class SubmitOperationHandler extends OperationHandler {

    protected boolean receivedInitialStage = false;
    protected final int interactionType;
    protected final int interactionStage;

    /**
     * Constructor.
     *
     * @param syncOperation true if this is a isSynchronous call.
     * @param responseHolder The response holder.
     */
    public SubmitOperationHandler(final boolean syncOperation,
            final OperationResponseHolder responseHolder) {
        super(syncOperation, responseHolder);
        this.interactionType = InteractionType._SUBMIT_INDEX;
        this.interactionStage = MALSubmitOperation._SUBMIT_ACK_STAGE;
    }

    /**
     * Constructor.
     *
     * @param responseHolder The response holder.
     */
    public SubmitOperationHandler(final OperationResponseHolder responseHolder) {
        super(false, responseHolder);
        this.interactionType = InteractionType._SUBMIT_INDEX;
        this.interactionStage = MALSubmitOperation._SUBMIT_ACK_STAGE;
    }

    protected SubmitOperationHandler(final int interactionType, final int interactionStage,
            final boolean syncOperation, final OperationResponseHolder responseHolder) {
        super(syncOperation, responseHolder);
        this.interactionType = interactionType;
        this.interactionStage = interactionStage;
    }

    @Override
    public synchronized StateMachineDetails handleStage(final MALMessage msg) throws MALInteractionException {
        if (!receivedInitialStage) {
            if ((interactionType == msg.getHeader().getInteractionType().getOrdinal())
                    && checkStage(msg.getHeader().getInteractionStage().getValue())) {
                receivedInitialStage = true;

                return new StateMachineDetails(true, msg, false);
            } else {
                logUnexpectedTransitionError(msg.getHeader().getInteractionType().getOrdinal(),
                        msg.getHeader().getInteractionStage().getValue());
                return new StateMachineDetails(false, msg, true);
            }
        } else {
            logUnexpectedTransitionError(interactionType, interactionStage);
            return new StateMachineDetails(false, msg, true);
        }
    }

    @Override
    public void processStage(final StateMachineDetails state) throws MALInteractionException {
        if (state.isAckStage()) {
            try {
                if (isSynchronous) {
                    responseHolder.signalResponse(false, state.getMessage());
                } else {
                    informListener(state.getMessage());
                }
            } catch (MALException ex) {
                // nothing we can do with this
                MALContextFactoryImpl.LOGGER.log(Level.WARNING,
                        "Exception thrown handling stage {0}", ex);
            }
        } else {
            logUnexpectedTransitionError(interactionType, interactionStage);
            throw new MALInteractionException(new MOErrorException(
                    MALHelper.INCORRECT_STATE_ERROR_NUMBER, null));
        }
    }

    @Override
    public synchronized void handleError(final MALMessageHeader hdr,
            final MOErrorException err, final Map qosMap) {
        if (isSynchronous) {
            responseHolder.signalResponse(true,
                    new DummyMessage(hdr, new DummyErrorBody(err), qosMap));
        } else {
            try {
                responseHolder.getListener().submitErrorReceived(hdr,
                        new DummyErrorBody(err), qosMap);
            } catch (MALException ex) {
                // not a lot we can do with this at this stage apart from log it
                MALContextFactoryImpl.LOGGER.log(Level.WARNING,
                        "Error received from consumer error handler in response to a provider error! {0}", ex);
            }
        }
    }

    @Override
    public synchronized boolean finished() {
        return receivedInitialStage;
    }

    protected boolean checkStage(final int stage) {
        return interactionStage == stage;
    }

    protected void informListener(final MALMessage msg) throws MALException {
        if (msg.getHeader().getIsErrorMessage()) {
            responseHolder.getListener().submitErrorReceived(msg.getHeader(),
                    (MALErrorBody) msg.getBody(), msg.getQoSProperties());
        } else {
            responseHolder.getListener().submitAckReceived(msg.getHeader(),
                    msg.getQoSProperties());
        }
    }
}
