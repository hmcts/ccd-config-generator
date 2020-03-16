package uk.gov.hmcts.ccd.sdk;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import java.util.List;
import java.util.Map;
import uk.gov.hmcts.ccd.sdk.types.ConfigBuilder;
import uk.gov.hmcts.ccd.sdk.types.Event;
import uk.gov.hmcts.ccd.sdk.types.EventTypeBuilder;
import uk.gov.hmcts.ccd.sdk.types.Role;
import uk.gov.hmcts.ccd.sdk.types.Tab;
import uk.gov.hmcts.ccd.sdk.types.Tab.TabBuilder;
import uk.gov.hmcts.ccd.sdk.types.Webhook;
import uk.gov.hmcts.ccd.sdk.types.WebhookConvention;
import uk.gov.hmcts.ccd.sdk.types.WorkBasketResult;
import uk.gov.hmcts.ccd.sdk.types.WorkBasketResult.WorkBasketResultBuilder;

public class ConfigBuilderImpl<T, S, R extends Role> implements ConfigBuilder<T, S, R> {

  public String caseType;
  public final Table<String, String, String> stateRoles = HashBasedTable.create();
  public final Multimap<String, String> stateRoleblacklist = ArrayListMultimap.create();
  public final Table<String, String, String> explicit = HashBasedTable.create();
  public final Map<String, String> statePrefixes = Maps.newHashMap();
  public final Table<String, String, List<Event.EventBuilder<T, R, S>>> events = HashBasedTable
      .create();
  public final List<Map<String, Object>> explicitFields = Lists.newArrayList();
  public final List<TabBuilder> tabs = Lists.newArrayList();
  public final List<WorkBasketResultBuilder> workBasketResultFields = Lists.newArrayList();

  private Class caseData;
  private WebhookConvention webhookConvention = this::defaultWebhookConvention;
  public String environment;

  private String defaultWebhookConvention(Webhook webhook, String eventId) {
    eventId = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, eventId);
    String path = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, webhook.toString());
    return "/" + eventId + "/" + path;
  }

  public ConfigBuilderImpl(Class caseData) {
    this.caseData = caseData;
  }

  @Override
  public EventTypeBuilder<T, R, S> event(final String id) {
    Event.EventBuilder<T, R, S> e = Event.EventBuilder
        .builder(caseData, webhookConvention, new PropertyUtils());
    e.eventId(id);
    e.id(id);
    return new EventTypeBuilderImpl(e);
  }

  @Override
  public void caseType(String caseType) {
    this.caseType = caseType;
  }

  @Override
  public void setEnvironment(String env) {
    this.environment = env;
  }

  @Override
  public void grant(S state, String permissions, R role) {
    stateRoles.put(state.toString(), role.getRole(), permissions);
  }

  @Override
  public void blacklist(S state, R... roles) {
    for (Role role : roles) {
      stateRoleblacklist.put(state.toString(), role.getRole());
    }
  }

  @Override
  public void explicitState(String eventId, R role, String crud) {
    explicit.put(eventId, role.getRole(), crud);

  }

  @Override
  public void prefix(S state, String prefix) {
    statePrefixes.put(state.toString(), prefix);
  }

  @Override
  public void caseField(String id, String showCondition, String type, String typeParam,
      String label) {
    Map<String, Object> data = Maps.newHashMap();
    explicitFields.add(data);
    data.put("ID", id);
    data.put("Label", label);
    data.put("FieldType", type);
    if (!Strings.isNullOrEmpty(typeParam)) {
      data.put("FieldTypeParameter", typeParam);
    }
  }

  @Override
  public void caseField(String id, String label, String type, String collectionType) {
    caseField(id, null, type, collectionType, label);
  }

  @Override
  public void caseField(String id, String label, String type) {
    caseField(id, label, type, null);
  }

  @Override
  public void setWebhookConvention(WebhookConvention convention) {
    this.webhookConvention = convention;
  }

  @Override
  public TabBuilder tab(String tabId, String tabLabel) {
    TabBuilder result = Tab.TabBuilder.builder(caseData,
        new PropertyUtils()).tabID(tabId).label(tabLabel);
    tabs.add(result);
    return result;
  }

  @Override
  public WorkBasketResultBuilder workBasketResultFields() {
    WorkBasketResultBuilder result = WorkBasketResultBuilder.builder(caseData,
        new PropertyUtils());
    workBasketResultFields.add(result);
    return result;
  }

  public List<Event<T, R, S>> getEvents() {
    Map<String, Event<T, R, S>> result = Maps.newHashMap();
    for (Table.Cell<String, String, List<Event.EventBuilder<T, R, S>>> cell : events.cellSet()) {
      for (Event.EventBuilder<T, R, S> builder : cell.getValue()) {
        Event<T, R, S> event = builder.build();
        event.setPreState(cell.getRowKey());
        event.setPostState(cell.getColumnKey());
        if (result.containsKey(event.getId())) {
          String stateSpecificId =
              event.getEventID() + statePrefixes.getOrDefault(cell.getColumnKey(), "") + cell
                  .getColumnKey();
          if (result.containsKey(stateSpecificId)) {
            throw new RuntimeException("Duplicate event:" + stateSpecificId);
          }
          event.setId(stateSpecificId);
        }
        if (event.getPreState().isEmpty()) {
          event.setPreState(null);
        }
        result.put(event.getId(), event);
      }
    }

    return Lists.newArrayList(result.values());
  }

  public class EventTypeBuilderImpl implements EventTypeBuilder<T, R, S> {


    private final Event.EventBuilder<T, R, S> builder;

    public EventTypeBuilderImpl(Event.EventBuilder<T, R, S> builder) {
      this.builder = builder;
    }

    @Override
    public Event.EventBuilder<T, R, S> forState(S state) {
      add(state.toString(), state.toString());
      return builder;
    }

    @Override
    public Event.EventBuilder<T, R, S> initialState(S state) {
      add("", state.toString());
      return builder;
    }

    @Override
    public Event.EventBuilder<T, R, S> forStateTransition(S from, S to) {
      add(from.toString(), to.toString());
      return builder;
    }

    @Override
    public Event.EventBuilder<T, R, S> forAllStates() {
      add("*", "*");
      return builder;
    }

    @Override
    public Event.EventBuilder<T, R, S> forStates(S... states) {
      for (S state : states) {
        forState(state);
      }

      return builder;
    }

    private void add(String from, String to) {
      if (!events.contains(from, to)) {
        events.put(from, to, Lists.newArrayList());
      }
      events.get(from, to).add(builder);
    }
  }
}
