import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FontTool {
    public static void main(final String[] args) throws IOException, URISyntaxException {
        Path path = Paths.get("./bmfont_config_cn_5000_and_en_002_max.bmfc");
        System.out.println(path.toAbsolutePath());
        String s = Files.readString(path);
        Object[] objects = Arrays.stream(s.split("# selected chars")).toArray();
        List<Integer> out = new ArrayList<>();
        String str = (String) objects[1];
        String str2 = str.replaceAll("\nchars=", ",");
        String str3 = str2.replaceAll("\nchars=", ",");
        String str4 = str3.replaceAll("\n\n# imported icon images", "");
        String[] parts = str4.split(",");
        for (String part : parts) {
            if(part!=""){
                part = part.replaceAll("\n","");
                if (part.contains("-")){
                    String[] split = part.split("-");
                    out.add((int) Long.parseUnsignedLong(split[0]));
                    out.add((int) Long.parseUnsignedLong(split[1]));
                }else{
                    int n = (int) Long.parseUnsignedLong(part);
                    out.add(n);
                    out.add(n);
                }
            }
        }

        StringBuilder ouput = new StringBuilder("package com.poerlang.nars3dview;\npublic class ChineseCharRanges{\n\tpublic static short[] ranges = {\n\t\t");
        for (int aShort : out) {
            ouput.append("(short) ").append(aShort).append(",");
        }
        ouput.append("\n\t};\n}");

        Path path1 = Paths.get("./ChineseCharRanges.java");
        Files.writeString(path1, ouput.toString(), StandardOpenOption.CREATE);
    }
}
