/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.testing.assertj;

import org.junit.jupiter.api.Test;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class JUnitAssertThrowsToAssertExceptionTypeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec
          .parser(JavaParser.fromJavaVersion()
            .classpathFromResources(new InMemoryExecutionContext(), "junit-jupiter-api-5.9+", "hamcrest-2.2+"))
          .recipe(new JUnitAssertThrowsToAssertExceptionType());
    }

    @Test
    void toAssertExceptionOfType() {
        //language=java
        rewriteRun(
          java(
            """
              import static org.junit.jupiter.api.Assertions.assertThrows;
              
              public class SimpleExpectedExceptionTest {
                  public void throwsExceptionWithSpecificType() {
                      assertThrows(NullPointerException.class, () -> {
                          throw new NullPointerException();
                      });
                  }
              }
              """,
            """
              import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
              
              public class SimpleExpectedExceptionTest {
                  public void throwsExceptionWithSpecificType() {
                      assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> {
                          throw new NullPointerException();
                      });
                  }
              }
              """
          )
        );
    }

    @Test
    void memberReference() {
        //language=java
        rewriteRun(
          java(
            """
              import static org.junit.jupiter.api.Assertions.assertThrows;
              import java.util.concurrent.CompletableFuture;
              import java.util.concurrent.ExecutionException;

              public class MemberReferenceTest {

                  public void throwsWithMemberReference() {
                      CompletableFuture<Boolean> future = new CompletableFuture<>();
                      assertThrows(ExecutionException.class, future::get);
                  }
              }
              """,
            """
              import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
              import java.util.concurrent.CompletableFuture;
              import java.util.concurrent.ExecutionException;

              public class MemberReferenceTest {

                  public void throwsWithMemberReference() {
                      CompletableFuture<Boolean> future = new CompletableFuture<>();
                      assertThatExceptionOfType(ExecutionException.class).isThrownBy(future::get);
                  }
              }
              """
          )
        );
    }
}
