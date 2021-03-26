package uk.gov.hmcts.ccd.sdk;

import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.example.missingcomplex.Applicant;
import uk.gov.hmcts.example.missingcomplex.MissingComplex;
import uk.gov.hmcts.reform.fpl.enums.CaseRoles;
import uk.gov.hmcts.reform.fpl.enums.State;
import uk.gov.hmcts.reform.fpl.enums.UserRole;
import uk.gov.hmcts.reform.fpl.model.CaseData;

import static org.assertj.core.api.Assertions.assertThat;

public class UnitTest {

    Reflections reflections;

    @Before
    public void setup() {
      reflections = new Reflections("uk.gov.hmcts");
    }

  @Test
  public void npeBug() {
    class NPEBug implements CCDConfig<CaseData, State, UserRole, CaseRoles> {
      @Override
      public void configure(ConfigBuilder<CaseData, State, UserRole, CaseRoles> builder) {
        builder.event("addNotes")
          .forStates(State.Submitted, State.Open, State.Deleted)
          .allWebhooks()
          .fields()
          .readonly(CaseData::getProceeding)
          .complex(CaseData::getJudgeAndLegalAdvisor);
      }
    }

    reflections = new Reflections("uk.gov.hmcts");

    ConfigGenerator<CaseData, State, UserRole, CaseRoles> generator = new ConfigGenerator<>(reflections, "uk.gov.hmcts");
    generator.resolveCCDConfig(new NPEBug());
  }

  @Test
  public void missingComplexBug() {
    class MissingBug implements CCDConfig<MissingComplex, State, UserRole, CaseRoles> {
      @Override
      public void configure(ConfigBuilder<MissingComplex, State, UserRole, CaseRoles> builder) {
      }
    }

    reflections = new Reflections("uk.gov.hmcts");
    ConfigGenerator<MissingComplex, State, UserRole, CaseRoles> generator = new ConfigGenerator<>(reflections, "uk.gov.hmcts");
    ResolvedCCDConfig<MissingComplex, State, UserRole, CaseRoles> resolved = generator.resolveCCDConfig(new MissingBug());
    assertThat(resolved.types).containsKeys(Applicant.class);
  }
}
