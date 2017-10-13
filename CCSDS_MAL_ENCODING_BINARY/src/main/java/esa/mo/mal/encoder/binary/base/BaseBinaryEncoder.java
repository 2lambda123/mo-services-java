/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Binary encoder
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
package esa.mo.mal.encoder.binary.base;

import esa.mo.mal.encoder.gen.GENEncoder;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * Implements the MALEncoder and MALListEncoder interfaces for a binary encoding.
 */
public abstract class BaseBinaryEncoder extends GENEncoder
{
  /**
   * Constructor.
   *
   * @param os Output stream to write to.
   */
  /*public BaseBinaryEncoder(final OutputStream os)
  {
    super(new BaseBinaryStreamHolder(os));
  }*/

  /**
   * Constructor for derived classes that have their own stream holder implementation that should be used.
   *
   * @param os Output stream to write to.
   */
  protected BaseBinaryEncoder(final StreamHolder os)
  {
    super(os);
  }

  @Override
  public void encodeNullableBlob(final Blob value) throws MALException
  {
    try
    {
      if ((null != value)
              && ((value.isURLBased() && (null != value.getURL()))
              || (!value.isURLBased() && (null != value.getValue()))))
      {
        encodeBlob(value);
      }
      else
      {
        outputStream.addBytes((byte[]) null);
      }
    }
    catch (IOException ex)
    {
      throw new MALException(ENCODING_EXCEPTION_STR, ex);
    }
  }

  @Override
  public void encodeNullableBoolean(final Boolean value) throws MALException
  {
    if (null != value)
    {
      encodeBoolean(value);
    }
    else
    {
      encodeOctet((byte) 2);
    }
  }

  @Override
  public void encodeNullableULong(final ULong value) throws MALException
  {
    try
    {
      if (null != value)
      {
        encodeULong(value);
      }
      else
      {
        outputStream.addBytes((byte[]) null);
      }
    }
    catch (IOException ex)
    {
      throw new MALException(ENCODING_EXCEPTION_STR, ex);
    }
  }

  @Override
  public void encodeNullableURI(final URI value) throws MALException
  {
    try
    {
      if ((null != value) && (null != value.getValue()))
      {
        encodeURI(value);
      }
      else
      {
        outputStream.addBytes((byte[]) null);
      }
    }
    catch (IOException ex)
    {
      throw new MALException(ENCODING_EXCEPTION_STR, ex);
    }
  }

  @Override
  public void encodeNullableIdentifier(final Identifier value) throws MALException
  {
    try
    {
      if ((null != value) && (null != value.getValue()))
      {
        encodeIdentifier(value);
      }
      else
      {
        outputStream.addBytes((byte[]) null);
      }
    }
    catch (IOException ex)
    {
      throw new MALException(ENCODING_EXCEPTION_STR, ex);
    }
  }

  @Override
  public void encodeNullableString(final String value) throws MALException
  {
    try
    {
      if (null != value)
      {
        encodeString(value);
      }
      else
      {
        outputStream.addBytes((byte[]) null);
      }
    }
    catch (IOException ex)
    {
      throw new MALException(ENCODING_EXCEPTION_STR, ex);
    }
  }

  /**
   * Internal class for accessing the output stream. Overridden by sub-classes
   * to alter the low level encoding.
   */
  public static class BaseBinaryStreamHolder extends StreamHolder
  {

    private static final BigInteger B_127 = new BigInteger("127");
    private static final BigInteger B_128 = new BigInteger("128");

    /**
     * Constructor.
     *
     * @param outputStream the stream to encode in to.
     */
    public BaseBinaryStreamHolder(OutputStream outputStream)
    {
      super(outputStream);
    }

    @Override
    public void addBytes(final byte[] value) throws IOException
    {
      if (null == value)
      {
        addSignedInt(-1);
      }
      else
      {
        addSignedInt(value.length);
        directAdd(value);
      }
    }

    @Override
    public void addString(String value) throws IOException
    {
      addBytes(value.getBytes(UTF8_CHARSET));
    }

    @Override
    public void addFloat(float value) throws IOException
    {
      addSignedInt(Float.floatToRawIntBits(value));
    }

    @Override
    public void addDouble(double value) throws IOException
    {
      addSignedLong(Double.doubleToRawLongBits(value));
    }

    @Override
    public void addBigInteger(BigInteger value) throws IOException
    {
      while (value.and(B_127.not()).compareTo(BigInteger.ZERO) == 1)
      {
        byte byteToWrite = (value.and(B_127)).or(B_128).byteValue();
        directAdd(byteToWrite);
        value = value.shiftRight(7);
      }
      BigInteger encoded = value.and(B_127);
      directAdd(encoded.byteValue());
    }


    @Override
    public void addSignedLong(final long value) throws IOException
    {
      addUnsignedLong((value << 1) ^ (value >> 63));
    }

    @Override
    public void addSignedInt(final int value) throws IOException
    {
      addUnsignedInt((value << 1) ^ (value >> 31));
    }

    @Override
    public void addSignedShort(final short value) throws IOException
    {
      addUnsignedInt((value << 1) ^ (value >> 31));
    }

    @Override
    public void addUnsignedLong(long value) throws IOException
    {
      while ((value & -128L) != 0L)
      {
        directAdd((byte) (((int) value & 127) | 128));
        value >>>= 7;
      }
      directAdd((byte) ((int) value & 127));
    }

    @Override
    public void addUnsignedLong32(long value) throws IOException
    {
      addUnsignedLong(value);
    }

    @Override
    public void addUnsignedInt(int value) throws IOException
    {
      while ((value & -128) != 0L)
      {
        directAdd((byte) ((value & 127) | 128));
        value >>>= 7;
      }
      directAdd((byte) (value & 127));
    }

    @Override
    public void addUnsignedInt16(int value) throws IOException
    {
      addUnsignedInt(value);
    }

    @Override
    public void addUnsignedShort(int value) throws IOException
    {
      addUnsignedInt(value);
    }

    @Override
    public void addUnsignedShort8(short value) throws IOException
    {
      addUnsignedShort(value);
    }

    @Override
    public void addByte(byte value) throws IOException
    {
      directAdd(value);
    }

    @Override
    public void addBool(boolean value) throws IOException
    {
      if (value)
      {
        addNotNull();
      }
      else
      {
        addIsNull();
      }
    }

    @Override
    public void addNotNull() throws IOException
    {
      directAdd((byte) 1);
    }

    @Override
    public void addIsNull() throws IOException
    {
      directAdd((byte) 0);
    }

    public OutputStream getOutputStream()
    {
      return outputStream;
    }
  }
}
