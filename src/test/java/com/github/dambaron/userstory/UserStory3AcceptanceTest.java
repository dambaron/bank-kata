package com.github.dambaron.userstory;

import com.github.dambaron.jgiven.tags.FeatureHistory;
import com.github.dambaron.jgiven.tags.Story;
import com.github.dambaron.userstory.steps.GivenAccount;
import com.github.dambaron.userstory.steps.ThenAccount;
import com.github.dambaron.userstory.steps.WhenAccount;
import com.tngtech.jgiven.junit.ScenarioTest;

@Story({"User story #3"})
@FeatureHistory
public class UserStory3AcceptanceTest extends ScenarioTest<GivenAccount, WhenAccount, ThenAccount> {

}
