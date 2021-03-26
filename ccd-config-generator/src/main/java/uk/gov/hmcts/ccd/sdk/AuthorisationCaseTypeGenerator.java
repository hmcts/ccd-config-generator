package uk.gov.hmcts.ccd.sdk;

import com.google.common.collect.Lists;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import uk.gov.hmcts.ccd.sdk.api.HasCaseTypePerm;

class AuthorisationCaseTypeGenerator {
  public static void generate(File root, String caseType, Class<?> roleEnum) {

    List<Map<String, Object>> result = Lists.newArrayList();
    if (roleEnum.isEnum()) {
      for (Object enumConstant : roleEnum.getEnumConstants()) {
        if (enumConstant instanceof HasCaseTypePerm) {
          HasCaseTypePerm r = (HasCaseTypePerm) enumConstant;
          // Add non case roles.
          if (!r.getRole().matches("\\[.+\\]")) {
            result.add(Map.of(
                "LiveFrom", "01/01/2017",
                "CaseTypeID", caseType,
                "UserRole", r.getRole(),
                "CRUD", r.getCaseTypePermissions()
            ));
          }
        }

      }

      Path output = Paths.get(root.getPath(), "AuthorisationCaseType.json");
      JsonUtils.mergeInto(output, result, new JsonUtils.CRUDMerger(), "CaseTypeID", "UserRole");
    }
  }
}
