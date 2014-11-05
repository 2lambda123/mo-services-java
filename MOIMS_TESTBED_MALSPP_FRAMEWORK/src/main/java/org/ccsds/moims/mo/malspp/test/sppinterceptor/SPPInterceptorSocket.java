/*******************************************************************************
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
 *******************************************************************************/
package org.ccsds.moims.mo.malspp.test.sppinterceptor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.ccsds.moims.mo.testbed.util.LoggingBase;
import org.ccsds.moims.mo.testbed.util.spp.SPPSocket;
import org.ccsds.moims.mo.testbed.util.spp.SpacePacket;

public class SPPInterceptorSocket implements SPPSocket {
	
	private SPPSocket socket;
    private static int[] scramblePattern = null;
    private Thread scrambleThread;
    private final List<SpacePacket> packets = Collections.synchronizedList(new LinkedList<SpacePacket>());
    // collect all packets requested to be sent out during 1 second before scrambling them
    private static final int COLLECTION_INTERVAL = 1000;
    private static long packetCounter = 0;

	public SPPInterceptorSocket(SPPSocket socket) {
	  this.socket = socket;
    }

	public void send(SpacePacket packet) throws Exception {
      // Put packets into queue in order of send. Any scrambling happens afterwards.
      SPPInterceptor.instance().packetSent(packet);
      if (null == scramblePattern) {
        internalSend(packet);
      } else {
        synchronized(this) {
          if (null == scrambleThread || !scrambleThread.isAlive()) {
            scrambleThread = createScrambleThread(scramblePattern.clone());
            scrambleThread.start();
          }
        }
        packets.add(packet);
      }
	}
    
    private synchronized void internalSend(SpacePacket packet) throws Exception {
		socket.send(packet);
    }
    
    private Thread createScrambleThread(final int[] scramblePattern) {
      return new Thread() {
        @Override
        public void run() {
          try {
            Thread.sleep(COLLECTION_INTERVAL);
            scrambleSend(new LinkedList<SpacePacket>(packets), scramblePattern);
          } catch (Exception ex) {
            LoggingBase.logMessage("Exception thrown: " + ex.getMessage());
          }
          packets.clear();
        }
      };
    }
    
    private void scrambleSend(final List<SpacePacket> packets, final int[] pattern) throws Exception {
      LoggingBase.logMessage("Collected " + packets.size() + " packet(s). Now send them scrambled.");
      int skip = 0;
      for (int i = 0; i < packets.size(); i++) {
        boolean sent = false;
        while (!sent) {
          int idx = pattern[(i + skip) % pattern.length] + ((i / pattern.length) * pattern.length);
          LoggingBase.logMessage("Send packet " + idx + " @ " + i);
          try {
            SpacePacket p = packets.get(idx);
            internalSend(p);
            sent = true;
        } catch (IndexOutOfBoundsException ex) {
            ++skip;
            LoggingBase.logMessage("    ... skip index");
          }
        }
      }
    }

	public SpacePacket receive() throws Exception {
	  SpacePacket packet = socket.receive();
		SPPInterceptor.instance().packetReceived(packet);
		return packet;
	}

	public void close() throws Exception {
		socket.close();
	}

	public String getDescription() {
		return socket.getDescription();
	}
    
    public static void setScramblePattern(int[] pattern) {
      scramblePattern = pattern;
    }
}
