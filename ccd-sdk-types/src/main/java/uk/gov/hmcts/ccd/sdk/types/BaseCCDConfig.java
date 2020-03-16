package uk.gov.hmcts.ccd.sdk.types;

import uk.gov.hmcts.ccd.sdk.types.Tab.TabBuilder;
import uk.gov.hmcts.ccd.sdk.types.WorkBasketResult.WorkBasketResultBuilder;

public abstract class BaseCCDConfig<Model, State,
    Role extends uk.gov.hmcts.ccd.sdk.types.Role> implements
    CCDConfig<Model, State, Role>, ConfigBuilder<Model, State, Role> {

  private ConfigBuilder<Model, State, Role> builder;

  @Override
  public void configure(ConfigBuilder<Model, State, Role> builder) {
    this.builder = builder;
    configure();
  }

  protected abstract void configure();

  @Override
  public EventTypeBuilder<Model, Role, State> event(String id) {
    return builder.event(id);
  }

  @Override
  public void caseType(String caseType) {
    builder.caseType(caseType);
  }

  @Override
  public void grant(State state, String permissions, Role role) {
    builder.grant(state, permissions, role);
  }

  @Override
  public void blacklist(State state, Role... role) {
    builder.blacklist(state, role);
  }

  @Override
  public void explicitState(String eventId, Role role, String crud) {
    builder.explicitState(eventId, role, crud);
  }

  @Override
  public void prefix(State state, String prefix) {
    builder.prefix(state, prefix);
  }

  @Override
  public void caseField(String id, String label, String type, String collectionType) {
    builder.caseField(id, label, type, collectionType);
  }

  @Override
  public void caseField(String id, String label, String type) {
    builder.caseField(id, label, type);
  }

  @Override
  public void caseField(String id, String showCondition, String type, String typeParam,
      String label) {
    builder.caseField(id, showCondition, type, typeParam, label);
  }

  @Override
  public void setWebhookConvention(WebhookConvention convention) {
    builder.setWebhookConvention(convention);
  }

  @Override
  public void setEnvironment(String env) {
    builder.setEnvironment(env);
  }

  public TabBuilder<Model> tab(String caseHistory, String history) {
    return builder.tab(caseHistory, history);
  }

  @Override
  public WorkBasketResultBuilder<Model> workBasketResultFields() {
    return builder.workBasketResultFields();
  }

  @Override
  public WorkBasketResultBuilder<Model> workBasketInputFields() {
    return builder.workBasketInputFields();
  }
}

