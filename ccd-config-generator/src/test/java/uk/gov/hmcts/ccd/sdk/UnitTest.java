package uk.gov.hmcts.ccd.sdk;

import java.util.List;

import org.junit.Test;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.example.missingcomplex.Applicant;
import uk.gov.hmcts.example.missingcomplex.MissingComplex;
import uk.gov.hmcts.reform.fpl.enums.State;
import uk.gov.hmcts.reform.fpl.enums.UserRole;
import uk.gov.hmcts.reform.fpl.model.CaseData;

import static org.assertj.core.api.Assertions.assertThat;

public class UnitTest {

  private List<CCDConfig<MissingComplex, State, UserRole>> ccdConfigs;
  private List<CCDConfig<CaseData, State, UserRole>> ccdConfigs1;

  @Test
  public void npeBug() {
    class NPEBug implements CCDConfig<CaseData, State, UserRole> {
      @Override
      public void configure(ConfigBuilder<CaseData, State, UserRole> builder) {
        builder.event("addNotes")
          .forStates(State.Submitted, State.Open, State.Deleted)
          .fields()
          .readonly(CaseData::getProceeding)
          .complex(CaseData::getJudgeAndLegalAdvisor);
      }
    }

    List<CCDConfig<CaseData, State, UserRole>> ccdConfigs = List.of(new NPEBug());
    ConfigGenerator<CaseData, State, UserRole> generator = new ConfigGenerator<>(ccdConfigs);
    generator.resolveCCDConfig(ccdConfigs);
  }

  @Test
  public void missingComplexBug() {
    class MissingBug implements CCDConfig<MissingComplex, State, UserRole> {
      @Override
      public void configure(ConfigBuilder<MissingComplex, State, UserRole> builder) {
      }
    }

    List<CCDConfig<MissingComplex, State, UserRole>> ccdConfigs = List.of(new MissingBug());

    ConfigGenerator<MissingComplex, State, UserRole> generator = new ConfigGenerator<>(ccdConfigs);
    ResolvedCCDConfig<MissingComplex, State, UserRole> resolved = generator.resolveCCDConfig(ccdConfigs);
    assertThat(resolved.types).containsKeys(Applicant.class);
  }
}
