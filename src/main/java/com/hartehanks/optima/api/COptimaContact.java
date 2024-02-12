package com.hartehanks.optima.api;


import java.io.IOException;

public class COptimaContact {
    public String[] getArrFieldValues() {
        return arrFieldValues;
    }

    private String[] arrFieldValues = new String[66];

    public COptimaContact() {
        for(int var1 = 0; var1 < 66; ++var1) {
            this.arrFieldValues[var1] = "";
        }

    }

    public COptimaContact(COptimaContact var1) {
        for(int var2 = 0; var2 < 66; ++var2) {
            this.arrFieldValues[var2] = var1.getField(var2);
        }

    }

    public String getField(int var1) {
        return var1 >= 0 && var1 < 66 ? this.arrFieldValues[var1] : "";
    }

    public void setField(int var1, String var2) {
        if (var1 >= 0 && var1 < 66) {
            if (var2.length() > 79) {
                this.arrFieldValues[var1] = var2.substring(0, 79);
            } else {
                this.arrFieldValues[var1] = var2;
            }
        }

    }

    public void Clear() {
        for(int var1 = 0; var1 < 66; ++var1) {
            this.arrFieldValues[var1] = "";
        }

    }

    public void CopyFrom(COptimaSearchSession var1) {
        this.Clear();
        if (var1 != null) {
            for(int var2 = 0; var2 < var1.getFieldCount(); ++var2) {
                this.setField(var1.getFieldType(var2), var1.getFieldValue(var2));
            }

            this.setField(56, var1.getCurrentCountryISO());
        }

    }

    void Write(CClientSocket var1) {
        for(int var2 = 0; var2 < 66; ++var2) {
            if (this.arrFieldValues[var2].length() > 0) {
                var1.Write(var2);
                var1.Write(this.arrFieldValues[var2]);
            }
        }

        var1.Write(-1);
    }

    void Read(CClientSocket var1) throws IOException {
        this.Clear();

        for(int var2 = var1.ReadInt(); var2 != -1; var2 = var1.ReadInt()) {
            this.arrFieldValues[var2] = var1.ReadString();
        }

    }

    public void setRecordID(String var1) {
        this.setField(0, var1);
    }

    public String getRecordID() {
        return this.getField(0);
    }

    public void setTitle(String var1) {
        this.setField(1, var1);
    }

    public String getTitle() {
        return this.getField(1);
    }

    public void setFirstName(String var1) {
        this.setField(2, var1);
    }

    public String getFirstName() {
        return this.getField(2);
    }

    public void setMiddleInitials(String var1) {
        this.setField(3, var1);
    }

    public String getMiddleInitials() {
        return this.getField(3);
    }

    public void setLastName(String var1) {
        this.setField(4, var1);
    }

    public String getLastName() {
        return this.getField(4);
    }

    public void setFullName(String var1) {
        this.setField(5, var1);
    }

    public String getFullName() {
        return this.getField(5);
    }

    public void setNameSuffix(String var1) {
        this.setField(6, var1);
    }

    public String getNameSuffix() {
        return this.getField(6);
    }

    public void setGender(String var1) {
        this.setField(7, var1);
    }

    public String getGender() {
        return this.getField(7);
    }

    public void setJobTitle(String var1) {
        this.setField(8, var1);
    }

    public String getJobTitle() {
        return this.getField(8);
    }

    public void setSalutation(String var1) {
        this.setField(9, var1);
    }

    public String getSalutation() {
        return this.getField(9);
    }

    public void setDepartment(String var1) {
        this.setField(10, var1);
    }

    public String getDepartment() {
        return this.getField(10);
    }

    public void setCompany(String var1) {
        this.setField(11, var1);
    }

    public String getCompany() {
        return this.getField(11);
    }

    public void setBuilding(String var1) {
        this.setField(12, var1);
    }

    public String getBuilding() {
        return this.getField(12);
    }

    public void setSubBuilding(String var1) {
        this.setField(13, var1);
    }

    public String getSubBuilding() {
        return this.getField(13);
    }

    public void setPremise(String var1) {
        this.setField(14, var1);
    }

    public String getPremise() {
        return this.getField(14);
    }

    public void setStreet(String var1) {
        this.setField(15, var1);
    }

    public String getStreet() {
        return this.getField(15);
    }

    public void setSubStreet(String var1) {
        this.setField(16, var1);
    }

    public String getSubStreet() {
        return this.getField(16);
    }

    public void setPOBox(String var1) {
        this.setField(17, var1);
    }

    public String getPOBox() {
        return this.getField(17);
    }

    public void setSubCity(String var1) {
        this.setField(18, var1);
    }

    public String getSubCity() {
        return this.getField(18);
    }

    public void setCity(String var1) {
        this.setField(19, var1);
    }

    public String getCity() {
        return this.getField(19);
    }

    public void setRegion(String var1) {
        this.setField(20, var1);
    }

    public String getRegion() {
        return this.getField(20);
    }

    public void setPrincipality(String var1) {
        this.setField(21, var1);
    }

    public String getPrincipality() {
        return this.getField(21);
    }

    public void setPostcode(String var1) {
        this.setField(22, var1);
    }

    public String getPostcode() {
        return this.getField(22);
    }

    public void setCountry(String var1) {
        this.setField(23, var1);
    }

    public String getCountry() {
        return this.getField(23);
    }

    public void setDPS(String var1) {
        this.setField(24, var1);
    }

    public String getDPS() {
        return this.getField(24);
    }

    public void setCedex(String var1) {
        this.setField(25, var1);
    }

    public String getCedex() {
        return this.getField(25);
    }

    public void setMKN(String var1) {
        this.setField(26, var1);
    }

    public String getMKN() {
        return this.getField(26);
    }

    public void setMKA(String var1) {
        this.setField(27, var1);
    }

    public String getMKA() {
        return this.getField(27);
    }

    public void setMKC(String var1) {
        this.setField(28, var1);
    }

    public String getMKC() {
        return this.getField(28);
    }

    public void setACR(String var1) {
        this.setField(29, var1);
    }

    public String getACR() {
        return this.getField(29);
    }

    public void setWCR(String var1) {
        this.setField(30, var1);
    }

    public String getWCR() {
        return this.getField(30);
    }

    public void setNCR(String var1) {
        this.setField(31, var1);
    }

    public String getNCR() {
        return this.getField(31);
    }

    public void setTCR(String var1) {
        this.setField(32, var1);
    }

    public String getTCR() {
        return this.getField(32);
    }

    public void setECR(String var1) {
        this.setField(33, var1);
    }

    public String getECR() {
        return this.getField(33);
    }

    public void setPercent(String var1) {
        this.setField(34, var1);
    }

    public String getPercent() {
        return this.getField(34);
    }

    public void setDuplicate(String var1) {
        this.setField(35, var1);
    }

    public String getDuplicate() {
        return this.getField(35);
    }

    public void setDUPmaster(String var1) {
        this.setField(36, var1);
    }

    public String getDUPmaster() {
        return this.getField(36);
    }

    public void setDUPconfidence(String var1) {
        this.setField(37, var1);
    }

    public String getDUPconfidence() {
        return this.getField(37);
    }

    public void setOther1(String var1) {
        this.setField(38, var1);
    }

    public String getOther1() {
        return this.getField(38);
    }

    public void setOther2(String var1) {
        this.setField(39, var1);
    }

    public String getOther2() {
        return this.getField(39);
    }

    public void setOther3(String var1) {
        this.setField(40, var1);
    }

    public String getOther3() {
        return this.getField(40);
    }

    public void setOther4(String var1) {
        this.setField(41, var1);
    }

    public String getOther4() {
        return this.getField(41);
    }

    public void setOther5(String var1) {
        this.setField(42, var1);
    }

    public String getOther5() {
        return this.getField(42);
    }

    public void setOther6(String var1) {
        this.setField(43, var1);
    }

    public String getOther6() {
        return this.getField(43);
    }

    public void setOther7(String var1) {
        this.setField(44, var1);
    }

    public String getOther7() {
        return this.getField(44);
    }

    public void setOther8(String var1) {
        this.setField(45, var1);
    }

    public String getOther8() {
        return this.getField(45);
    }

    public void setOther9(String var1) {
        this.setField(46, var1);
    }

    public String getOther9() {
        return this.getField(46);
    }

    public void setOther10(String var1) {
        this.setField(47, var1);
    }

    public String getOther10() {
        return this.getField(47);
    }

    public void setAddressLine1(String var1) {
        this.setField(48, var1);
    }

    public String getAddressLine1() {
        return this.getField(48);
    }

    public void setAddressLine2(String var1) {
        this.setField(49, var1);
    }

    public String getAddressLine2() {
        return this.getField(49);
    }

    public void setAddressLine3(String var1) {
        this.setField(50, var1);
    }

    public String getAddressLine3() {
        return this.getField(50);
    }

    public void setAddressLine4(String var1) {
        this.setField(51, var1);
    }

    public String getAddressLine4() {
        return this.getField(51);
    }

    public void setAddressLine5(String var1) {
        this.setField(52, var1);
    }

    public String getAddressLine5() {
        return this.getField(52);
    }

    public void setAddressLine6(String var1) {
        this.setField(53, var1);
    }

    public String getAddressLine6() {
        return this.getField(53);
    }

    public void setAddressLine7(String var1) {
        this.setField(54, var1);
    }

    public String getAddressLine7() {
        return this.getField(54);
    }

    public void setAddressLine8(String var1) {
        this.setField(55, var1);
    }

    public String getAddressLine8() {
        return this.getField(55);
    }

    public void setCountryISO(String var1) {
        this.setField(56, var1);
    }

    public String getCountryISO() {
        return this.getField(56);
    }

    public void setMobileTelephone(String var1) {
        this.setField(57, var1);
    }

    public String getMobileTelephone() {
        return this.getField(57);
    }

    public void setTelephone1(String var1) {
        this.setField(58, var1);
    }

    public String getTelephone1() {
        return this.getField(58);
    }

    public void setTelephone2(String var1) {
        this.setField(59, var1);
    }

    public String getTelephone2() {
        return this.getField(59);
    }

    public void setTelephone3(String var1) {
        this.setField(60, var1);
    }

    public String getTelephone3() {
        return this.getField(60);
    }

    public void setTelephone4(String var1) {
        this.setField(61, var1);
    }

    public String getTelephone4() {
        return this.getField(61);
    }

    public void setEmail1(String var1) {
        this.setField(62, var1);
    }

    public String getEmail1() {
        return this.getField(62);
    }

    public void setEmail2(String var1) {
        this.setField(63, var1);
    }

    public String getEmail2() {
        return this.getField(63);
    }

    public void setURL1(String var1) {
        this.setField(64, var1);
    }

    public String getURL1() {
        return this.getField(64);
    }

    public void setURL2(String var1) {
        this.setField(65, var1);
    }

    public String getURL2() {
        return this.getField(65);
    }
}
