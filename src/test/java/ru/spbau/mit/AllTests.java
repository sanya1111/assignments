package ru.spbau.mit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestCollections.class, TestPredicate.class, TestFunction1.class })
public class AllTests {

}
