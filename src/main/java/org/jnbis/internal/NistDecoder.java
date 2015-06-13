package org.jnbis.internal;

import org.jnbis.api.model.Nist;
import org.jnbis.api.model.record.*;
import org.jnbis.internal.record.BaseRecord;
import org.jnbis.internal.record.reader.RecordReaderFactory;


/**
 * @author hamed
 * @version 1.0
 * @since Apr 29, 2007
 */
public class NistDecoder {
    private RecordReaderFactory readerFactory;

    public NistDecoder() {
        readerFactory = new RecordReaderFactory();
    }

    public Nist decode(byte[] nist) {
        if (nist == null || nist.length == 0) {
            throw new IllegalArgumentException("data is null or zero length");
        }

        NistHelper.Token token = new NistHelper.Token(nist);
        InternalNist decoded = new InternalNist();
        BaseRecord record = readerFactory.read(token);
        decoded.setTransactionInfo((TransactionInformation) record);

        while (nextRecord(token)) {
            if (token.crt < 2) {
                continue;
            }
            record = readerFactory.read(token);

            if (record instanceof UserDefinedDescriptiveText) {
                decoded.putUserDefinedText(decoded.getUserDefinedTextKeys().size(), (UserDefinedDescriptiveText) record);

            } else if (record instanceof LowResolutionGrayscaleFingerprint) {
                LowResolutionGrayscaleFingerprint fingerprint = (LowResolutionGrayscaleFingerprint) record;
                decoded.addLowResGrayscaleFingerPrint(fingerprint);

            } else if (record instanceof HighResolutionGrayscaleFingerprint) {
                HighResolutionGrayscaleFingerprint fingerprint = (HighResolutionGrayscaleFingerprint) record;
                decoded.addHiResGrayscaleFingerPrint(fingerprint);

            } else if (record instanceof LowResolutionBinaryFingerprint) {
                LowResolutionBinaryFingerprint fingerprint = (LowResolutionBinaryFingerprint) record;
                decoded.putLowResBinaryFingerPrint(decoded.getLowResBinaryFingerPrintKeys().size(), fingerprint);

            } else if (record instanceof HighResolutionBinaryFingerprint) {
                HighResolutionBinaryFingerprint fingerprint = (HighResolutionBinaryFingerprint) record;
                decoded.putHiResBinaryFingerPrint(decoded.getHiResBinaryFingerPrintKeys().size(), fingerprint);

            } else if (record instanceof UserDefinedImage) {
                decoded.putUserDefinedImage(decoded.getUserDefinedImageKeys().size(), (UserDefinedImage) record);

            } else if (record instanceof SignatureImage) {
                decoded.putSignature(decoded.getSignatureKeys().size(), (SignatureImage) record);

            } else if (record instanceof MinutiaeData) {
                //readMinutiaeData(token, decoded);
                decoded.putMinutiaeData(decoded.getMinutiaeDataKeys().size(), (MinutiaeData) record);

            } else if (record instanceof FacialAndSmtImage) {
                decoded.putFacialSmtImage(decoded.getFacialSmtKeys().size(), (FacialAndSmtImage) record);

            } else if (record instanceof VariableResolutionLatentImage) {
                decoded.putVariableResLatentImage(decoded.getVariableResLatentImageKeys().size(), (VariableResolutionLatentImage) record);

            } else if (record instanceof VariableResolutionFingerprint) {
                decoded.putVariableResFingerprint(decoded.getVariableResFingerprintKeys().size(), (VariableResolutionFingerprint) record);

            } else if (record instanceof VariableResolutionPalmprint) {
                decoded.putVariableResPalmprint(decoded.getVariableResPalmprintKeys().size(), (VariableResolutionPalmprint) record);

            } else if (record instanceof IrisImage) {
                decoded.putIrisImage(decoded.getIrisImageKeys().size(), (IrisImage) record);
            }
        }

        return decoded;
    }

    private boolean nextRecord(NistHelper.Token token) {
        if (token.header.length() == 0) {
            return false;
        }

        int rsPos = token.header.indexOf(NistHelper.SEP_RS);
        if (rsPos == -1) {
            rsPos = token.header.length() - 1;
        }

        int usPos = token.header.indexOf(NistHelper.SEP_US);
        token.crt = Integer.parseInt(token.header.substring(0, usPos));
        token.header = token.header.substring(rsPos + 1);

        return true;
    }

}
