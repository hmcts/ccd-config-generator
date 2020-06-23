package uk.gov.hmcts.ccd.sdk.generator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ccd.sdk.JsonUtils;
import uk.gov.hmcts.ccd.sdk.JsonUtils.AddMissing;
import uk.gov.hmcts.ccd.sdk.types.FixedList;
import uk.gov.hmcts.ccd.sdk.types.HasCode;
import uk.gov.hmcts.ccd.sdk.types.HasLabel;

public class FixedListGenerator {

  public static void generate(File root, Map<Class, Integer> types) {
    File dir = root.toPath().resolve("FixedLists").toFile();
    dir.mkdir();

    for (Class c : types.keySet()) {
      if (c.isEnum()) {
        List<Map<String, Object>> fields = Lists.newArrayList();

        FixedList annotation = (FixedList) c.getAnnotation(FixedList.class);

        if(annotation != null && !annotation.generate()) {
          continue;
        }

        int order = 1;
        String fixedListName = annotation != null && annotation.id().length() > 1 ? annotation.id() : c.getSimpleName();
        for (Object enumConstant : c.getEnumConstants()) {
          Map<String, Object> value = Maps.newHashMap();
          fields.add(value);
          value.put("LiveFrom", "01/01/2017");
          value.put("ID", fixedListName);
          if (enumConstant instanceof HasCode) {
            value.put("ListElementCode", ((HasCode) enumConstant).getCode());
          } else {
            value.put("ListElementCode", enumConstant);
          }
          if (enumConstant instanceof HasLabel) {
            value.put("ListElement", ((HasLabel) enumConstant).getLabel());
          } else {
            value.put("ListElement", enumConstant);
          }
          value.put("DisplayOrder", order++);
        }

        Path path = Paths.get(dir.getPath(), fixedListName + ".json");
        JsonUtils.mergeInto(path, fields, new AddMissing(), "ListElementCode");
      }
    }
  }
}
