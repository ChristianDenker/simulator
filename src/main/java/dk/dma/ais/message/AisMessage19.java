/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.ais.message;

import dk.dma.ais.binary.BinArray;
import dk.dma.ais.binary.SixbitEncoder;
import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.sentence.Vdm;
import dk.dma.enav.model.geometry.Position;

/**
 * AIS message 19
 * <p>
 * Extended Class B equipment position report as defined by ITU-R M.1371-4
 */
public class AisMessage19 extends AisStaticCommon implements IVesselPositionMessage {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Reserved for definition by a competent regional or local authority. Should be set to zero, if not used for any
     * regional or local application. Regional applications should not use zero.
     */
    private int spare1; // 8 bits

    /**
     * Speed over ground in 1/10 knot steps (0-102.2 knots) 1023 = not available, 1022 = 102.2 knots or higher
     */
    private int sog; // 10 bits

    /**
     * AisPosition Accuracy 1 = high ( =&lt; 10 m) 0 = low (&gt;10 m) 0 = default The PA flag should be determined in
     * accordance with Table 47
     */
    private int posAcc; // 1 bit

    /**
     * Store the positions just as in message 1-3
     */
    private AisPosition pos; // : Lat/Long 1/10000 minute

    /**
     * Course over ground in 1/10 = (0-3599). 3600 (E10h) = not available = default; 3601-4095 should not be used
     */
    private int cog; // 12 bits

    /**
     * True heading Degrees (0-359) (511 indicates not available = default)
     */
    private int trueHeading; // 9 bits

    /**
     * Time stamp: UTC second when the report was generated by the EPFS (0-59 or 60 if time stamp is not available,
     * which should also be the default value or 61 if positioning system is in manual input mode or 62 if electronic
     * position fixing system operates in estimated (dead reckoning) mode or 63 if the positioning system is
     * inoperative) 61, 62, 63 are not used by CS AIS
     */
    private int utcSec; // 6 bits : UTC Seconds

    /**
     * Not used. Should be set to zero. Reserved for future use
     */
    private int spare2; // 4 bits

    /**
     * Type of electronic position fixing device: 0 = undefined (default) 1 = GPS 2 = GLONASS 3 = combined GPS/GLONASS 4
     * = Loran-C 5 = Chayka 6 = integrated navigation system 7 = surveyed 8 = Galileo, 9-14 = not used 15 = internal
     * GNSS
     */
    private int posType; // 4 bits

    /**
     * RAIM-flag: RAIM (Receiver autonomous integrity monitoring) flag of electronic position fixing device; 0 = RAIM
     * not in use = default; 1 = RAIM in use see Table 47
     */
    private int raimFlag; // 1 bit

    /**
     * DTE: Data terminal equipment (DTE) ready 0 = available 1 = not available = default see 3.3.1
     */
    private int dte; // 1 bit : DTE flag

    /**
     * Mode flag: 0 = Station operating in autonomous and continuous mode = default 1 = Station operating in assigned
     * mode
     */
    private int modeFlag; // 1 bit

    /**
     * Not used. Should be set to zero. Reserved for future use
     */
    private int spare3; // 4 bits

    /**
     * Instantiates a new Ais message 19.
     */
    public AisMessage19() {
        super(19);
    }

    /**
     * Instantiates a new Ais message 19.
     *
     * @param vdm the vdm
     * @throws AisMessageException the ais message exception
     * @throws SixbitException     the sixbit exception
     */
    public AisMessage19(Vdm vdm) throws AisMessageException, SixbitException {
        super(vdm);
        parse(vdm.getBinArray());
    }

    @Override
    protected void parse(BinArray binArray) throws AisMessageException, SixbitException {
        BinArray sixbit = vdm.getBinArray();
        if (sixbit.getLength() != 312) {
            throw new AisMessageException("Message 19 wrong length " + sixbit.getLength());
        }

        super.parse(sixbit);

        this.spare1 = (int) sixbit.getVal(8);
        this.sog = (int) sixbit.getVal(10);
        this.posAcc = (int) sixbit.getVal(1);
        this.pos = new AisPosition();
        this.pos.setRawLongitude(sixbit.getVal(28));
        this.pos.setRawLatitude(sixbit.getVal(27));
        this.cog = (int) sixbit.getVal(12);
        this.trueHeading = (int) sixbit.getVal(9);
        this.utcSec = (int) sixbit.getVal(6);
        this.spare2 = (int) sixbit.getVal(4);
        this.name = sixbit.getString(20);
        this.shipType = (int) sixbit.getVal(8);
        this.dimBow = (int) sixbit.getVal(9);
        this.dimStern = (int) sixbit.getVal(9);
        this.dimPort = (int) sixbit.getVal(6);
        this.dimStarboard = (int) sixbit.getVal(6);
        this.posType = (int) sixbit.getVal(4);
        this.raimFlag = (int) sixbit.getVal(1);
        this.dte = (int) sixbit.getVal(1);
        this.modeFlag = (int) sixbit.getVal(1);
        this.spare3 = (int) sixbit.getVal(4);
    }

    @Override
    public SixbitEncoder getEncoded() {
        SixbitEncoder encoder = super.encode();
        encoder.addVal(spare1, 8);
        encoder.addVal(sog, 10);
        encoder.addVal(posAcc, 1);
        encoder.addVal(pos.getRawLongitude(), 28);
        encoder.addVal(pos.getRawLatitude(), 27);
        encoder.addVal(cog, 12);
        encoder.addVal(trueHeading, 9);
        encoder.addVal(utcSec, 6);
        encoder.addVal(spare2, 4);
        encoder.addString(name, 20);
        encoder.addVal(shipType, 8);
        encoder.addVal(dimBow, 9);
        encoder.addVal(dimStern, 9);
        encoder.addVal(dimPort, 6);
        encoder.addVal(dimStarboard, 6);
        encoder.addVal(posType, 4);
        encoder.addVal(raimFlag, 1);
        encoder.addVal(dte, 1);
        encoder.addVal(modeFlag, 1);
        encoder.addVal(spare3, 4);
        return encoder;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append(", spare1=");
        builder.append(spare1);
        builder.append(", sog=");
        builder.append(sog);
        builder.append(", posAcc=");
        builder.append(posAcc);
        builder.append(", pos=");
        builder.append(pos);
        builder.append(", cog=");
        builder.append(cog);
        builder.append(", trueHeading=");
        builder.append(trueHeading);
        builder.append(", utcSec=");
        builder.append(utcSec);
        builder.append(", spare2=");
        builder.append(spare2);
        builder.append(", name=");
        builder.append(name);
        builder.append(", shipType=");
        builder.append(shipType);
        builder.append(", dimBow=");
        builder.append(dimBow);
        builder.append(", dimStern=");
        builder.append(dimStern);
        builder.append(", dimPort=");
        builder.append(dimPort);
        builder.append(", dimStarboard=");
        builder.append(dimStarboard);
        builder.append(", posType=");
        builder.append(posType);
        builder.append(", raimFlag=");
        builder.append(raimFlag);
        builder.append(", dte=");
        builder.append(dte);
        builder.append(", modeFlag=");
        builder.append(modeFlag);
        builder.append(", spare3=");
        builder.append(spare3);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Gets spare 1.
     *
     * @return the spare 1
     */
    public int getSpare1() {
        return spare1;
    }

    /**
     * Sets spare 1.
     *
     * @param spare1 the spare 1
     */
    public void setSpare1(int spare1) {
        this.spare1 = spare1;
    }

    public int getSog() {
        return sog;
    }

    /**
     * Sets sog.
     *
     * @param sog the sog
     */
    public void setSog(int sog) {
        this.sog = sog;
    }

    public int getPosAcc() {
        return posAcc;
    }

    /**
     * Sets pos acc.
     *
     * @param posAcc the pos acc
     */
    public void setPosAcc(int posAcc) {
        this.posAcc = posAcc;
    }

    @Override
    public Position getValidPosition() {
        AisPosition pos = this.pos;
        return pos == null ? null : pos.getGeoLocation();
    }

    public AisPosition getPos() {
        return pos;
    }

    /**
     * Sets pos.
     *
     * @param pos the pos
     */
    public void setPos(AisPosition pos) {
        this.pos = pos;
    }

    public int getCog() {
        return cog;
    }

    /**
     * Sets cog.
     *
     * @param cog the cog
     */
    public void setCog(int cog) {
        this.cog = cog;
    }

    public int getTrueHeading() {
        return trueHeading;
    }

    /**
     * Sets true heading.
     *
     * @param trueHeading the true heading
     */
    public void setTrueHeading(int trueHeading) {
        this.trueHeading = trueHeading;
    }

    public int getUtcSec() {
        return utcSec;
    }

    /**
     * Sets utc sec.
     *
     * @param utcSec the utc sec
     */
    public void setUtcSec(int utcSec) {
        this.utcSec = utcSec;
    }

    /**
     * Gets spare 2.
     *
     * @return the spare 2
     */
    public int getSpare2() {
        return spare2;
    }

    /**
     * Sets spare 2.
     *
     * @param spare2 the spare 2
     */
    public void setSpare2(int spare2) {
        this.spare2 = spare2;
    }

    /**
     * Gets pos type.
     *
     * @return the pos type
     */
    public int getPosType() {
        return posType;
    }

    /**
     * Sets pos type.
     *
     * @param posType the pos type
     */
    public void setPosType(int posType) {
        this.posType = posType;
    }

    /**
     * Gets raim flag.
     *
     * @return the raim flag
     */
    public int getRaimFlag() {
        return raimFlag;
    }

    /**
     * Sets raim flag.
     *
     * @param raimFlag the raim flag
     */
    public void setRaimFlag(int raimFlag) {
        this.raimFlag = raimFlag;
    }

    /**
     * Gets dte.
     *
     * @return the dte
     */
    public int getDte() {
        return dte;
    }

    /**
     * Sets dte.
     *
     * @param dte the dte
     */
    public void setDte(int dte) {
        this.dte = dte;
    }

    /**
     * Gets mode flag.
     *
     * @return the mode flag
     */
    public int getModeFlag() {
        return modeFlag;
    }

    /**
     * Sets mode flag.
     *
     * @param modeFlag the mode flag
     */
    public void setModeFlag(int modeFlag) {
        this.modeFlag = modeFlag;
    }

    /**
     * Gets spare 3.
     *
     * @return the spare 3
     */
    public int getSpare3() {
        return spare3;
    }

    /**
     * Sets spare 3.
     *
     * @param spare3 the spare 3
     */
    public void setSpare3(int spare3) {
        this.spare3 = spare3;
    }

    @Override
    public boolean isPositionValid() {
        Position geo = pos.getGeoLocation();
        return geo != null;
    }

    @Override
    public boolean isCogValid() {
        return cog < 3600;
    }

    @Override
    public boolean isSogValid() {
        return sog < 1023;
    }

    @Override
    public boolean isHeadingValid() {
        return trueHeading < 360;
    }

    @Override
    public int getRaim() {
        return getRaimFlag();
    }

}