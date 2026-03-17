/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.config;

import java.io.BufferedWriter;
import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.BuildingTags;

public class DocumentedElement
implements Comparable<DocumentedElement> {
    public final String key;
    public final String description;

    public static void generateClassHelp(String directoryName, String fileName, Map items, String explanations) {
        try {
            File directory = directoryName != null ? new File(MillCommonUtilities.getMillenaireHelpDir(), directoryName) : MillCommonUtilities.getMillenaireHelpDir();
            directory.mkdirs();
            File file = new File(directory, fileName);
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            writer.write(explanations + "\n" + "\n");
            ArrayList<DocumentedElement> tags = new ArrayList<DocumentedElement>();
            for (Object rawkey : items.keySet()) {
                String key = (String)rawkey;
                if (items.get(key).getClass().isAnnotationPresent(Documentation.class)) {
                    tags.add(new DocumentedElement(key, items.get(key).getClass().getAnnotation(Documentation.class).value()));
                    continue;
                }
                MillLog.warning(null, "No description for goal: " + key);
            }
            Collections.sort(tags);
            for (DocumentedElement tag : tags) {
                writer.write(tag.key + ": " + tag.description + "\n");
                writer.write("\n");
            }
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException("Could not write tags description file: ", e);
        }
    }

    public static void generateHelpFiles() {
        DocumentedElement.generateStaticTagsHelp(null, "Villager Types Tags.txt", VillagerType.class, "List of tags for villager types that changes their behaviours.");
        DocumentedElement.generateStaticTagsHelp(null, "Building Tags.txt", BuildingTags.class, "List of tags for buildings that trigger special features.");
    }

    private static void generateStaticTagsHelp(String directoryName, String fileName, Class targetClass, String explanations) {
        try {
            File directory = directoryName != null ? new File(MillCommonUtilities.getMillenaireHelpDir(), directoryName) : MillCommonUtilities.getMillenaireHelpDir();
            directory.mkdirs();
            File file = new File(directory, fileName);
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            writer.write(explanations + "\n" + "\n");
            ArrayList<DocumentedElement> tags = new ArrayList<DocumentedElement>();
            for (Field field : targetClass.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Documentation.class) || !Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                tags.add(new DocumentedElement(field.get(null).toString(), field.getAnnotation(Documentation.class).value()));
            }
            Collections.sort(tags);
            for (DocumentedElement tag : tags) {
                writer.write(tag.key + ": " + tag.description + "\n");
                writer.write("\n");
            }
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException("Could not write tags description file: ", e);
        }
    }

    public DocumentedElement(String tag, String description) {
        this.key = tag;
        this.description = description;
    }

    @Override
    public int compareTo(DocumentedElement otherTag) {
        return this.key.compareTo(otherTag.key);
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.FIELD, ElementType.TYPE})
    public static @interface Documentation {
        public String value() default "";
    }
}

