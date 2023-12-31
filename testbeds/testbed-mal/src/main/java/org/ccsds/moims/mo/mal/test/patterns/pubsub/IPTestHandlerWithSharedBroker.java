/** *****************************************************************************
 * Copyright or © or Copr. CNES
 *
 * This software is a computer program whose purpose is to provide a
 * framework for the CCSDS Mission Operations services.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ****************************************************************************** */
package org.ccsds.moims.mo.mal.test.patterns.pubsub;

import org.ccsds.moims.mo.mal.*;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.test.patterns.IPTestHandlerImpl;
import org.ccsds.moims.mo.mal.test.patterns.MonitorPublishInteractionListener;
import org.ccsds.moims.mo.mal.test.suite.TestServiceProvider;
import org.ccsds.moims.mo.mal.test.util.AssertionHelper;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.malprototype.MALPrototypeHelper;
import org.ccsds.moims.mo.malprototype.iptest.IPTestServiceInfo;
import org.ccsds.moims.mo.malprototype.iptest.provider.MonitorMultiPublisher;
import org.ccsds.moims.mo.malprototype.iptest.provider.MonitorPublisher;
import org.ccsds.moims.mo.malprototype.structures.TestPublishDeregister;
import org.ccsds.moims.mo.malprototype.structures.TestPublishRegister;
import org.ccsds.moims.mo.malprototype.structures.TestPublishUpdate;
import org.ccsds.moims.mo.malprototype.structures.Assertion;
import org.ccsds.moims.mo.testbed.transport.TransportInterceptor;
import org.ccsds.moims.mo.testbed.util.Configuration;
import org.ccsds.moims.mo.testbed.util.FileBasedDirectory;
import org.ccsds.moims.mo.testbed.util.LoggingBase;

/**
 * Provider side
 */
public class IPTestHandlerWithSharedBroker extends IPTestHandlerImpl {

    private String ipTestProviderWithSharedBrokerFileName;

    public IPTestHandlerWithSharedBroker() {
        super();
        ipTestProviderWithSharedBrokerFileName = TestServiceProvider.IP_TEST_PROVIDER_WITH_SHARED_BROKER_NAME;
    }

    public String getIpTestProviderWithSharedBrokerFileName() {
        return ipTestProviderWithSharedBrokerFileName;
    }

    public void setIpTestProviderWithSharedBrokerFileName(String ipTestProviderWithSharedBrokerFileName) {
        this.ipTestProviderWithSharedBrokerFileName = ipTestProviderWithSharedBrokerFileName;
    }

    @Override
    protected void doPublishRegister(TestPublishRegister publishRegister,
            MonitorPublishInteractionListener listener) throws MALInteractionException, MALException {
        LoggingBase.logMessage("IPTestHandlerWithSharedBroker.doPublishRegister(" + publishRegister + ')');
        FileBasedDirectory.URIpair uris = FileBasedDirectory.loadURIs(ipTestProviderWithSharedBrokerFileName);

        String key = publishRegister.getDomain().toString()
                + publishRegister.getNetworkZone().toString()
                + publishRegister.getSession().toString()
                + publishRegister.getSessionName().toString();

        // Reset the listener
        listener.setHeader(null);
        listener.setError(null);
        listener.setKey(key);

        Time timestamp = new Time(System.currentTimeMillis());

        UShort opNumber = null;
        try {
            if (publishRegister.getTestMultiType()) {
                opNumber = IPTestServiceInfo.MONITORMULTI_OP.getNumber();
                MonitorMultiPublisher publisher = getMonitorMultiPublisher(publishRegister.getDomain(),
                        publishRegister.getNetworkZone(),
                        publishRegister.getSession(),
                        publishRegister.getSessionName(),
                        publishRegister.getQos(),
                        publishRegister.getPriority());

                LoggingBase.logMessage("IPTestHandlerWithSharedBroker.doPublishRegister: The keyNames are: " + publishRegister.getKeyNames());
                publisher.asyncRegister(publishRegister.getKeyNames(), publishRegister.getKeyTypes(), listener);
            } else {
                opNumber = IPTestServiceInfo.MONITOR_OP.getNumber();
                MonitorPublisher publisher = getMonitorPublisher(publishRegister.getDomain(),
                        publishRegister.getNetworkZone(),
                        publishRegister.getSession(),
                        publishRegister.getSessionName(),
                        publishRegister.getQos(),
                        publishRegister.getPriority());

                LoggingBase.logMessage("IPTestHandlerWithSharedBroker.doPublishRegister: The keyNames are: " + publishRegister.getKeyNames());
                publisher.asyncRegister(publishRegister.getKeyNames(), publishRegister.getKeyTypes(), listener);
            }
            listener.cond.waitFor(Configuration.WAIT_TIME_OUT);
        } catch (InterruptedException e) {
        }

        listener.cond.reset();

        MALMessageHeader expectedPublishRegisterHeader = new MALMessageHeader(
                new Identifier(uris.uri.getValue()),
                TestServiceProvider.IP_TEST_AUTHENTICATION_ID,
                new Identifier(uris.broker.getValue()),
                timestamp,
                InteractionType.PUBSUB,
                new UOctet(MALPubSubOperation._PUBLISH_REGISTER_STAGE),
                null,
                MALPrototypeHelper.MALPROTOTYPE_AREA_NUMBER,
                IPTestServiceInfo.IPTEST_SERVICE_NUMBER,
                opNumber,
                MALPrototypeHelper.MALPROTOTYPE_AREA.getVersion(),
                Boolean.FALSE,
                new NamedValueList());

        MALMessage publishRegisterMsg = TransportInterceptor.instance().getLastSentMessage(uris.uri);

        LoggingBase.logMessage("IPTestHandlerWithSharedBroker.doPublishRegister: Looking for last message sent to " + uris.broker + " : " + publishRegisterMsg);

        AssertionHelper.checkHeader("PubSub.checkPublishRegisterHeader", assertions,
                publishRegisterMsg.getHeader(), expectedPublishRegisterHeader);

        boolean isErrorTest = (publishRegister.getErrorCode().getValue() != 999);

        MALMessageHeader expectedPublishRegisterAckHeader = new MALMessageHeader(
                new Identifier(uris.broker.getValue()),
                FileBasedDirectory.loadSharedBrokerAuthenticationId(),
                new Identifier(uris.uri.getValue()),
                publishRegisterMsg.getHeader().getTimestamp(),
                InteractionType.PUBSUB,
                new UOctet(MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE),
                publishRegisterMsg.getHeader().getTransactionId(),
                MALPrototypeHelper.MALPROTOTYPE_AREA_NUMBER,
                IPTestServiceInfo.IPTEST_SERVICE_NUMBER,
                opNumber,
                MALPrototypeHelper.MALPROTOTYPE_AREA.getVersion(),
                isErrorTest,
                new NamedValueList());

        String procedureName;
        if (isErrorTest) {
            procedureName = "PubSub.checkPublishRegisterErrorHeader";
        } else {
            procedureName = "PubSub.checkPublishRegisterAckHeader";
        }

        MALMessageHeader publishRegisterAck = listener.getHeader();
        AssertionHelper.checkHeader(procedureName, assertions,
                publishRegisterAck, expectedPublishRegisterAckHeader);

        if (publishRegister.getErrorCode().getValue() != 999) {
            MOErrorException error = listener.getError();
            assertions.add(new Assertion(procedureName,
                    "Error received", (error != null)));
            if (error != null) {
                AssertionHelper.checkEquality(procedureName,
                        assertions, "errorNumber", error.getErrorNumber(),
                        publishRegister.getErrorCode());
            }
        }
    }

    @Override
    public void publishUpdates(TestPublishUpdate publishUpdate, MALInteraction interaction) throws MALException {
        LoggingBase.logMessage("IPTestHandlerWithSharedBroker.publishUpdates(" + publishUpdate + ')');
        FileBasedDirectory.URIpair uris = FileBasedDirectory.loadURIs(ipTestProviderWithSharedBrokerFileName);

        MonitorPublishInteractionListener listener = defaultListener;
        String key = publishUpdate.getDomain().toString()
                + publishUpdate.getNetworkZone().toString()
                + publishUpdate.getSession().toString()
                + publishUpdate.getSessionName().toString();

        UShort opNumber;
        if (publishUpdate.getTestMultiType()) {
            opNumber = IPTestServiceInfo.MONITORMULTI_OP.getNumber();
        } else {
            opNumber = IPTestServiceInfo.MONITOR_OP.getNumber();
        }

        MALMessageHeader expectedPublishHeader = new MALMessageHeader(
                new Identifier(uris.uri.getValue()),
                TestServiceProvider.IP_TEST_AUTHENTICATION_ID,
                new Identifier(uris.broker.getValue()),
                new Time(System.currentTimeMillis()),
                InteractionType.PUBSUB,
                new UOctet(MALPubSubOperation._PUBLISH_STAGE),
                listener.getPublishRegisterTransactionId(key),
                MALPrototypeHelper.MALPROTOTYPE_AREA_NUMBER,
                IPTestServiceInfo.IPTEST_SERVICE_NUMBER,
                opNumber,
                MALPrototypeHelper.MALPROTOTYPE_AREA.getVersion(),
                Boolean.FALSE,
                new NamedValueList());

        super.publishUpdates(publishUpdate, interaction);

        if (publishUpdate.getErrorCode().getValue() != 999
                && publishUpdate.getIsException()) {
            // No Publish message is expected to be sent
            return;
        }

        MALMessage publishMsg = TransportInterceptor.instance().getLastSentMessage(uris.uri);

        assertions.add(new Assertion("PubSub.checkPublishHeader", "Publish sent", (publishMsg != null)));

        if (publishMsg == null) {
            return;
        }

        AssertionHelper.checkHeader("PubSub.checkPublishHeader", assertions,
                publishMsg.getHeader(), expectedPublishHeader);
    }

    @Override
    protected void doPublishDeregister(TestPublishDeregister _TestPublishDeregister,
            MonitorPublishInteractionListener listener)
            throws MALInteractionException, MALException {
        LoggingBase.logMessage("IPTestHandlerWithSharedBroker.doPublishDeregister(" + _TestPublishDeregister + ')');
        FileBasedDirectory.URIpair uris = FileBasedDirectory.loadURIs(ipTestProviderWithSharedBrokerFileName);

        Time timestamp = new Time(System.currentTimeMillis());

        UShort opNumber = null;
        try {
            if (_TestPublishDeregister.getTestMultiType()) {
                opNumber = IPTestServiceInfo.MONITORMULTI_OP.getNumber();
                MonitorMultiPublisher publisher = getMonitorMultiPublisher(
                        _TestPublishDeregister.getDomain(),
                        _TestPublishDeregister.getNetworkZone(),
                        _TestPublishDeregister.getSession(),
                        _TestPublishDeregister.getSessionName(),
                        _TestPublishDeregister.getQos(),
                        _TestPublishDeregister.getPriority());
                publisher.asyncDeregister(listener);
            } else {
                opNumber = IPTestServiceInfo.MONITOR_OP.getNumber();
                MonitorPublisher publisher = getMonitorPublisher(
                        _TestPublishDeregister.getDomain(),
                        _TestPublishDeregister.getNetworkZone(),
                        _TestPublishDeregister.getSession(),
                        _TestPublishDeregister.getSessionName(),
                        _TestPublishDeregister.getQos(),
                        _TestPublishDeregister.getPriority());
                publisher.asyncDeregister(listener);
            }
            listener.cond.waitFor(Configuration.WAIT_TIME_OUT);
        } catch (InterruptedException e) {
        }

        listener.cond.reset();

        MALMessageHeader expectedPublishDeregisterHeader = new MALMessageHeader(
                new Identifier(uris.uri.getValue()),
                TestServiceProvider.IP_TEST_AUTHENTICATION_ID,
                new Identifier(uris.broker.getValue()),
                timestamp,
                InteractionType.PUBSUB,
                new UOctet(MALPubSubOperation._PUBLISH_DEREGISTER_STAGE),
                null,
                MALPrototypeHelper.MALPROTOTYPE_AREA_NUMBER,
                IPTestServiceInfo.IPTEST_SERVICE_NUMBER,
                opNumber,
                MALPrototypeHelper.MALPROTOTYPE_AREA.getVersion(),
                Boolean.FALSE,
                new NamedValueList());

        MALMessage publishDeregisterMsg = TransportInterceptor.instance().getLastSentMessage(uris.uri);

        AssertionHelper.checkHeader("PubSub.checkPublishDeregisterHeader", assertions,
                publishDeregisterMsg.getHeader(), expectedPublishDeregisterHeader);

        MALMessageHeader expectedPublishDeregisterAckHeader = new MALMessageHeader(
                new Identifier(uris.broker.getValue()),
                FileBasedDirectory.loadSharedBrokerAuthenticationId(),
                new Identifier(uris.uri.getValue()),
                publishDeregisterMsg.getHeader().getTimestamp(),
                InteractionType.PUBSUB,
                new UOctet(MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE),
                publishDeregisterMsg.getHeader().getTransactionId(),
                MALPrototypeHelper.MALPROTOTYPE_AREA_NUMBER,
                IPTestServiceInfo.IPTEST_SERVICE_NUMBER,
                opNumber,
                MALPrototypeHelper.MALPROTOTYPE_AREA.getVersion(),
                Boolean.FALSE,
                new NamedValueList());

        MALMessageHeader publishDeregisterAck = listener.getHeader();
        AssertionHelper.checkHeader("PubSub.checkPublishDeregisterAckHeader", assertions,
                publishDeregisterAck, expectedPublishDeregisterAckHeader);
    }

    @Override
    public void testMultipleNotify(TestPublishUpdate _TestPublishUpdate, MALInteraction interaction) throws MALInteractionException {
        throw new MALInteractionException(new MOErrorException(MALHelper.INTERNAL_ERROR_NUMBER,
                new Union("The transmit multiple is not supported with a shared broker")));
    }

    @Override
    protected FileBasedDirectory.URIpair getProviderURIs() {
        return FileBasedDirectory.loadURIs(ipTestProviderWithSharedBrokerFileName);
    }

    @Override
    protected Blob getBrokerAuthenticationId() {
        return FileBasedDirectory.loadSharedBrokerAuthenticationId();
    }
}
