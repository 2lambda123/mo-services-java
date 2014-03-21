/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Split Binary encoder
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
package esa.mo.mal.encoder.binary.split;

import java.nio.ByteBuffer;
import org.ccsds.moims.mo.mal.MALException;

/**
 * Implements the MALDecoder interface for a split binary encoding.
 */
public class SplitBinaryDecoder extends esa.mo.mal.encoder.binary.BinaryDecoder
{
  /**
   * Constructor.
   *
   * @param src Byte array to read from.
   */
  public SplitBinaryDecoder(final byte[] src)
  {
    super(src);
  }

  /**
   * Constructor.
   *
   * @param is Input stream to read from.
   */
  public SplitBinaryDecoder(final java.io.InputStream is)
  {
    super(is);
  }

  /**
   * Constructor.
   *
   * @param src Byte array to read from.
   * @param offset index in array to start reading from.
   */
  public SplitBinaryDecoder(final byte[] src, final int offset)
  {
    super(src, offset);
  }

  /**
   * Constructor.
   *
   * @param src Source buffer holder to use.
   */
  protected SplitBinaryDecoder(final BufferHolder src)
  {
    super(src);
  }

  @Override
  protected BufferHolder createBufferHolder(final java.io.InputStream is, final byte[] buf, final int offset, final int length)
  {
    return new SplitBufferHolder(is, buf, offset, length);
  }

  @Override
  public org.ccsds.moims.mo.mal.MALListDecoder createListDecoder(final java.util.List list) throws MALException
  {
    return new SplitBinaryListDecoder(list, sourceBuffer);
  }

  @Override
  public Boolean decodeBoolean() throws MALException
  {
    return sourceBuffer.getBool();
  }

  @Override
  public Boolean decodeNullableBoolean() throws MALException
  {
    if (sourceBuffer.getBool())
    {
      return decodeBoolean();
    }

    return null;
  }

  protected static class SplitBufferHolder extends BufferHolder
  {
    private boolean bitStoreLoaded = false;
    private java.util.BitSet bitStore = null;
    private int bitIndex = 0;

    /**
     * Constructor.
     *
     * @param is Input stream to read from.
     * @param buf Source buffer to use.
     * @param offset Buffer offset to read from next.
     * @param length Length of readable data held in the array, which may be larger.
     */
    public SplitBufferHolder(final java.io.InputStream is, final byte[] buf, final int offset, final int length)
    {
      super(is, buf, offset, length);
    }

    @Override
    public void checkBuffer(final int requiredLength) throws MALException
    {
      // ensure that the bit buffer has been loaded first
      if (!this.bitStoreLoaded)
      {
        this.bitStoreLoaded = true;
        this.bitStore = java.util.BitSet.valueOf(ByteBuffer.wrap(get(getUnsignedInt())));
      }

      super.checkBuffer(requiredLength);
    }

    @Override
    public boolean getBool() throws MALException
    {
      if (!bitStoreLoaded)
      {
        checkBuffer(1);
      }

      boolean rv = bitStore.get(bitIndex);
      ++bitIndex;
      return rv;
    }
  }
}
