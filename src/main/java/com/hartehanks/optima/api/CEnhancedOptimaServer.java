package com.hartehanks.optima.api;

public class CEnhancedOptimaServer extends COptimaServer {
    public CEnhancedOptimaServer() {
    }

    public boolean StandardiseAddress(COptimaContact var1) {
        boolean var2 = super.StandardiseAddress(var1);
        String var3 = var1.getField(56);
        if (var3.equalsIgnoreCase("CH2")) {
            if (var1.getField(25).trim().length() <= 0 && var1.getField(24).trim().length() <= 0) {
                String var4 = var1.getField(18);
                if (var4.length() > 0 && var1.getField(15).length() == 0 && var4.charAt(var4.length() - 1) == 21306) {
                    var1.setField(15, var4);
                    var1.setField(18, "");
                }
            } else {
                var1.setField(29, "U0-P0S0A0T0R0Z0C4-SDS");
            }
        } else if (var3.equalsIgnoreCase("KO2") && (var1.getField(25).trim().length() > 0 || var1.getField(24).trim().length() > 0)) {
            var1.setField(29, "U0-P0S0A0T0R0Z0C4-SDS");
        }

        return var2;
    }
}

