package org.dizzymii.millenaire2.gametest;

import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * Standalone utility to generate a minimal empty 3x3x3 structure NBT file.
 * Run this once to create the file, then it can be committed.
 * 
 * Usage: java GenerateEmptyTemplate
 */
public class GenerateEmptyTemplate {

    public static void main(String[] args) throws Exception {
        // Target path for gametest structure templates
        String basePath = "src/main/resources/data/millenaire2/gametest/structures";
        File dir = new File(basePath);
        dir.mkdirs();
        File out = new File(dir, "empty.nbt");

        try (DataOutputStream dos = new DataOutputStream(
                new GZIPOutputStream(new FileOutputStream(out)))) {
            writeEmptyStructure(dos);
        }

        System.out.println("Written: " + out.getAbsolutePath());
    }

    private static void writeEmptyStructure(DataOutputStream dos) throws IOException {
        // Root compound tag (type 10), empty name
        dos.writeByte(10); // TAG_Compound
        dos.writeUTF(""); // root name

        // DataVersion: TAG_Int (type 3)
        dos.writeByte(3);
        dos.writeUTF("DataVersion");
        dos.writeInt(3955); // 1.21.1

        // size: TAG_List (type 9) of TAG_Int (type 3), count 3
        dos.writeByte(9);
        dos.writeUTF("size");
        dos.writeByte(3); // element type: INT
        dos.writeInt(3);  // count
        dos.writeInt(3);  // x
        dos.writeInt(3);  // y
        dos.writeInt(3);  // z

        // palette: TAG_List of TAG_Compound, count 1
        dos.writeByte(9);
        dos.writeUTF("palette");
        dos.writeByte(10); // element type: COMPOUND
        dos.writeInt(1);   // count
        // palette[0]: {Name: "minecraft:air"}
        dos.writeByte(8); // TAG_String
        dos.writeUTF("Name");
        dos.writeUTF("minecraft:air");
        dos.writeByte(0); // END of compound

        // entities: TAG_List of TAG_Compound, count 0
        dos.writeByte(9);
        dos.writeUTF("entities");
        dos.writeByte(10); // element type: COMPOUND
        dos.writeInt(0);   // count

        // blocks: TAG_List of TAG_Compound, count 0
        dos.writeByte(9);
        dos.writeUTF("blocks");
        dos.writeByte(10); // element type: COMPOUND
        dos.writeInt(0);   // count

        // End of root compound
        dos.writeByte(0);
    }
}
