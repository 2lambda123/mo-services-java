/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO MAL Java API
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
package org.ccsds.moims.mo.mal.structures;

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;

/**
 * The Attributes list.
 *
 */
public class AttributeList extends java.util.ArrayList<Object> implements Element {

    public AttributeList(Attribute attribute) {
        super();
        super.add(attribute);
    }

    public AttributeList() {
        super();
    }

    @Override
    public Element createElement() {
        return new AttributeList();
    }

    @Override
    public Long getShortForm() {
        throw new UnsupportedOperationException("This method should never be called!");
    }

    @Override
    public UShort getAreaNumber() {
        throw new UnsupportedOperationException("This method should never be called!");
    }

    @Override
    public UOctet getAreaVersion() {
        throw new UnsupportedOperationException("This method should never be called!");
    }

    @Override
    public UShort getServiceNumber() {
        throw new UnsupportedOperationException("This method should never be called!");
    }

    @Override
    public Integer getTypeShortForm() {
        throw new UnsupportedOperationException("This method should never be called!");
    }

    @Override
    public void encode(MALEncoder encoder) throws MALException {
        int size = this.size();
        encoder.encodeInteger(size);
        
        for (int i = 0; i < size; i++) {
            Object objToEncode = super.get(i);

            if (objToEncode instanceof Attribute) {
                encoder.encodeNullableAttribute((Attribute) objToEncode);
            } else {
                throw new MALException("The object is not an Attribute type! It is: "
                        + objToEncode.getClass().getCanonicalName());
            }
        }
    }

    @Override
    public Element decode(MALDecoder decoder) throws MALException {
        int size = decoder.decodeInteger();
        AttributeList newObj = new AttributeList();
        
        for(int i = 0; i < size; i++){
            newObj.add(decoder.decodeNullableAttribute());
        }

        return newObj;
    }
}
