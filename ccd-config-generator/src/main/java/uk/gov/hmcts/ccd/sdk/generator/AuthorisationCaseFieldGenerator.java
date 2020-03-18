package uk.gov.hmcts.ccd.sdk.generator;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import uk.gov.hmcts.ccd.sdk.JsonUtils;
import uk.gov.hmcts.ccd.sdk.types.Event;
import uk.gov.hmcts.ccd.sdk.types.Field;
import uk.gov.hmcts.ccd.sdk.types.Tab;
import uk.gov.hmcts.ccd.sdk.types.Tab.TabBuilder;
import uk.gov.hmcts.ccd.sdk.types.TabField;
import uk.gov.hmcts.ccd.sdk.types.WorkBasket;
import uk.gov.hmcts.ccd.sdk.types.WorkBasket.WorkBasketBuilder;
import uk.gov.hmcts.ccd.sdk.types.WorkBasketField;

public class AuthorisationCaseFieldGenerator {

  public static void generate(File root, String caseType, List<Event> events,
      Table<String, String, String> eventRolePermissions, List<TabBuilder> tabs,
      List<WorkBasketBuilder> workBasketInputFields,
      List<WorkBasketBuilder> workBasketResultFields) {

    Table<String, String, String> fieldRolePermissions = HashBasedTable.create();
    for (Event event : events) {
      Map<String, String> eventPermissions = eventRolePermissions.row(event.getEventID());
      List<Field.FieldBuilder> fields = event.getFields().build().getFields();
      for (Field.FieldBuilder fb : fields) {

        for (Entry<String, String> rolePermission : eventPermissions.entrySet()) {
          String perm = fb.build().isReadOnly() ? "R" : rolePermission.getValue();
          if (!perm.contains("D") && fb.build().isMutable()) {
            perm += "D";
          }
          fieldRolePermissions.put(fb.build().getId(), rolePermission.getKey(),
              perm);
        }
      }
    }

    for (String role : ImmutableSet.copyOf(fieldRolePermissions.columnKeySet())) {
      // Add CRU for caseHistory for all roles
      fieldRolePermissions.put("caseHistory", role, "CRU");

      // Add read for any tab fields
      for (TabBuilder tb : tabs) {
        Tab tab = tb.build();
        for (TabField field : tab.getFields()) {
          if (!fieldRolePermissions.contains(field.getId(), role)) {
            fieldRolePermissions.put(field.getId(), role, "R");
          }
        }
      }

      // Add read for WorkBaskets
      for (WorkBasketBuilder workBasketInputField :
          Iterables.concat(workBasketInputFields, workBasketResultFields)) {
        WorkBasket basket = workBasketInputField.build();
        for (WorkBasketField field : basket.getFields()) {
          if (!fieldRolePermissions.contains(field.getId(), role)) {
            fieldRolePermissions.put(field.getId(), role, "R");
          }
        }
      }
    }

    File folder = new File(root.getPath(), "AuthorisationCaseField");
    folder.mkdir();
    for (String role : fieldRolePermissions.columnKeySet()) {

      List<Map<String, Object>> permissions = Lists.newArrayList();
      Map<String, String> rolePermissions = fieldRolePermissions.column(role);

      for (Entry<String, String> fieldPerm : rolePermissions.entrySet()) {
        if (fieldPerm.getKey().equals("[STATE]")) {
          continue;
        }

        Map<String, Object> permission = Maps.newHashMap();
        permissions.add(permission);
        permission.put("CaseTypeID", caseType);
        permission.put("LiveFrom", "01/01/2017");
        permission.put("UserRole", role);
        permission.put("CaseFieldID", fieldPerm.getKey());
        String p = fieldPerm.getValue();
        permission.put("CRUD", p);
      }

      Path output = Paths.get(folder.getPath(), role + ".json");
      JsonUtils.mergeInto(output, permissions, "CaseFieldID", "UserRole");
    }
  }
}
