package org.dizzymii.millenaire2.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations for the data file parameter loading system.
 * Fields annotated with @ConfigField are automatically loaded from .txt data files.
 * Ported from org.millenaire.common.annotedparameters.ConfigAnnotations.
 */
public class ConfigAnnotations {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface FieldDocumentation {
        String explanation();
        String explanationCategory() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ConfigField {
        String defaultValue() default "";
        String fieldCategory() default "";
        String paramName() default "";
        ParameterType type();
    }

    /**
     * Supported parameter types for data file loading.
     * Each type knows how to parse a string value from a .txt file into the target field.
     */
    public enum ParameterType {
        STRING,
        STRINGDISPLAY,
        STRING_LIST,
        STRING_ADD,
        STRING_INVITEM_ADD,
        STRING_CASE_SENSITIVE_ADD,
        STRING_INTEGER_ADD,
        TRANSLATED_STRING_ADD,
        BOOLEAN,
        INTEGER,
        INTEGER_ARRAY,
        FLOAT,
        RESOURCE_LOCATION,
        MILLISECONDS,
        INVITEM,
        ITEMSTACK_ARRAY,
        INVITEM_ADD,
        INVITEM_PAIR,
        INVITEM_NUMBER_ADD,
        INVITEM_PRICE_ADD,
        ENTITY_ID,
        BLOCK_ID,
        BLOCKSTATE,
        BLOCKSTATE_ADD,
        BONUS_ITEM_ADD,
        STARTING_ITEM_ADD,
        POS_TYPE,
        GOAL_ADD,
        TOOLCATEGORIES_ADD,
        GENDER,
        DIRECTION,
        CLOTHES,
        VILLAGERCONFIG,
        BUILDING,
        BUILDING_ADD,
        BUILDINGCUSTOM,
        BUILDINGCUSTOM_ADD,
        VILLAGER_ADD,
        SHOP,
        RANDOM_BRICK_COLOUR_ADD,
        BRICK_COLOUR_THEME_ADD,
        WALL_TYPE
    }
}
