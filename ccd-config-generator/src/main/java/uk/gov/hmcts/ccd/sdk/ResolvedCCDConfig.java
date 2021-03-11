package uk.gov.hmcts.ccd.sdk;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uk.gov.hmcts.ccd.sdk.api.Event;
import uk.gov.hmcts.ccd.sdk.api.HasRole;

public class ResolvedCCDConfig<T, S, R extends HasRole> {

  public final Class<T> typeArg;
  public final ConfigBuilderImpl<T, S, R> builder;
  public final List<Event<T, R, S>> events;
  public final Map<Class<?>, Integer> types;
  public final String environment;
  public final Class<S> stateArg;
  public final Class<R> roleType;
  public final ImmutableSet<S> allStates;

  public ResolvedCCDConfig(Class<T> typeArg, Class<S> stateArg, Class<R> roleType,
                           ConfigBuilderImpl<T, S, R> builder, List<Event<T, R, S>> events,
                           Map<Class<?>, Integer> types, String environment,
                           Set<S> allStates) {
    this.typeArg = typeArg;
    this.stateArg = stateArg;
    this.roleType = roleType;
    this.builder = builder;
    this.events = events;
    this.types = types;
    this.environment = environment;
    this.allStates = ImmutableSet.copyOf(allStates);
  }
}
