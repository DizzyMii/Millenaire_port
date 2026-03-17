/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.config;

import java.lang.reflect.Field;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;

public class MillConfigParameter {
    private static final Object[] BOOLEAN_VALS = new Object[]{Boolean.TRUE, Boolean.FALSE};
    public static final int LANGUAGE = 1;
    public static final int EDITABLE_STRING = 2;
    public static final int KEY = 3;
    public static final int EDITABLE_INTEGER = 4;
    public static final int LOG = 5;
    public static final int BONUS_KEY = 6;
    final Field field;
    public final String key;
    public Object defaultVal;
    private Object[] possibleVals;
    public int special = 0;
    public int strLimit = 20;
    public boolean displayConfig = true;
    public boolean displayConfigDev = false;

    private static String getBooleanString(boolean b) {
        if (b) {
            return LanguageUtilities.string("config.valuetrue");
        }
        return LanguageUtilities.string("config.valuefalse");
    }

    public MillConfigParameter(Field field, String key, int special) {
        this.field = field;
        this.special = special;
        this.key = key.toLowerCase();
        if (special == 5) {
            this.defaultVal = "";
        }
    }

    public MillConfigParameter(Field field, String key, Object ... possibleVals) {
        this.field = field;
        this.possibleVals = possibleVals;
        this.key = key.toLowerCase();
        if (this.isBoolean()) {
            this.possibleVals = BOOLEAN_VALS;
        } else if (possibleVals.length == 0) {
            MillLog.error(null, "No possible values specified for non-boolean config: " + field.getName());
        }
    }

    public boolean compareValuesFromString(String newValStr) {
        Object newVal = this.getValueFromString(newValStr);
        if (newVal == null) {
            return false;
        }
        return newVal.equals(this.getValue());
    }

    public Object getDefaultValueForDisplay() {
        if (this.special == 3 && this.defaultVal != null) {
            return Mill.proxy.getKeyString((Integer)this.defaultVal);
        }
        return this.defaultVal;
    }

    public String getDesc() {
        if (this.special == 5) {
            return "";
        }
        return LanguageUtilities.string("config." + this.key + ".desc", this.getStringFromValue(this.defaultVal));
    }

    public String getLabel() {
        if (this.special == 5) {
            return this.key;
        }
        return LanguageUtilities.string("config." + this.key + ".label");
    }

    public Object[] getPossibleVals() {
        if (this.special == 1) {
            return new Object[]{MillConfigValues.loadedLanguages.get("fr"), MillConfigValues.loadedLanguages.get("en")};
        }
        if (this.special == 5) {
            return new Object[]{0, 1, 2, 3};
        }
        return this.possibleVals;
    }

    public String getSaveValue() {
        try {
            return this.getSaveValue(this.field.get(null));
        }
        catch (Exception e) {
            MillLog.printException(this + ": Exception when getting the field.", e);
            return null;
        }
    }

    public String getSaveValue(Object o) {
        if (this.special == 3 && o != null) {
            return Mill.proxy.getKeyString((Integer)o);
        }
        if (this.special == 5) {
            return MillLog.getLogLevel((Integer)o);
        }
        if (o == null) {
            return "";
        }
        return o.toString();
    }

    public String getStringFromValue(Object o) {
        if (this.special == 3 && o != null) {
            return Mill.proxy.getKeyString((Integer)o);
        }
        if (this.special == 5) {
            return MillLog.getLogLevel((Integer)o);
        }
        if (this.isBoolean() && o != null) {
            return MillConfigParameter.getBooleanString((Boolean)o);
        }
        if (o == null) {
            return "";
        }
        return o.toString();
    }

    public String getStringValue() {
        try {
            return this.getStringFromValue(this.field.get(null));
        }
        catch (Exception e) {
            MillLog.printException(this + ": Exception when getting the field.", e);
            return null;
        }
    }

    public Object getValue() {
        try {
            return this.field.get(null);
        }
        catch (Exception e) {
            MillLog.printException(this + ": Exception when getting the field.", e);
            return null;
        }
    }

    public Object getValueFromString(String val) {
        if (this.special == 1) {
            return MillConfigValues.loadedLanguages.get(val);
        }
        if (this.special == 3) {
            return Mill.proxy.loadKeySetting(val);
        }
        if (this.special == 5) {
            return MillLog.readLogLevel(val);
        }
        if (this.isString()) {
            return val;
        }
        if (this.isInteger()) {
            return Integer.parseInt(val.trim());
        }
        if (this.isBoolean()) {
            return Boolean.parseBoolean(val.trim());
        }
        return null;
    }

    public boolean hasTextField() {
        return this.special == 2 || this.special == 3 || this.special == 4 || this.special == 6;
    }

    public boolean isBoolean() {
        return this.field.getType().equals(Boolean.class) || this.field.getType().equals(Boolean.TYPE);
    }

    public boolean isCurrentValueTheDefault() {
        if (this.defaultVal == null) {
            return false;
        }
        if (this.special == 5 && (Integer)this.getValue() == 0) {
            return true;
        }
        return this.defaultVal.equals(this.getValue());
    }

    public boolean isInteger() {
        return this.field.getType().equals(Integer.class) || this.field.getType().equals(Integer.TYPE);
    }

    public boolean isString() {
        return this.field.getType().equals(String.class);
    }

    public void setDefaultValue(Object defaultVal) {
        this.defaultVal = defaultVal;
    }

    public MillConfigParameter setDisplayDev(boolean display) {
        this.displayConfigDev = display;
        return this;
    }

    public MillConfigParameter setMaxStringLength(int len) {
        this.strLimit = len;
        return this;
    }

    public void setValue(Object val) {
        if (this.special == 5 && val instanceof String) {
            val = MillLog.readLogLevel((String)val);
        }
        if (this.special == 3 && val.equals(0)) {
            return;
        }
        try {
            this.field.set(null, val);
        }
        catch (Exception e) {
            MillLog.printException(this + ": Exception when setting the field.", e);
        }
        if (this.special == 6 && Mill.proxy.getTheSinglePlayer() != null) {
            MillConfigValues.checkBonusCode(true);
        }
    }

    public void setValueFromString(String val, boolean setDefault) {
        this.setValue(this.getValueFromString(val));
        if (setDefault) {
            this.setDefaultValue(this.getValueFromString(val));
        }
    }

    public String toString() {
        return "MillConfigParameter:" + this.key;
    }
}

