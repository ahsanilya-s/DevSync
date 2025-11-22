Tests and Detector Unit Tests

Overview
--------
This document explains the unit tests that were added for the detectors under `com.devsync.detectors`.
The project includes focused JUnit 5 tests that parse small Java snippets with JavaParser and verify each detector's behavior.

Goals
-----
- Explain how to run the tests (IDE and Maven).
- Describe the test structure and conventions used.
- Give guidance for adding or maintaining detector tests.
- Provide troubleshooting steps for differences between IDE and Maven test runs.

Where tests live
----------------
All detector tests are located under:

- `src/test/java/com/devsync/detectors/`

Key test classes added
----------------------
- `ComplexConditionalDetectorTest.java`
- `MissingDefaultDetectorTest.java`
- `LongMethodDetectorTest.java`
- `MagicNumberDetectorTest.java`
- `LongStatementDetectorTest.java`
- `LongParameterListDetectorTest.java`
- `LongIdentifierDetectorTest.java`
- `EmptyCatchDetectorTest.java`
- `DeficientEncapsulationDetectorTest.java`
- `UnnecessaryAbstractionDetectorTest.java`
- `BrokenModularizationDetectorTest.java`

Shared test helper
------------------
`DetectorTestBase.java` provides helpers used across tests:

- `parseCode(String code)` — parses Java source using `StaticJavaParser.parse(code)` and returns a `CompilationUnit`.
- `hasIssue(List<String> issues, String pattern)` — checks whether a detector output list contains a substring (e.g. detector tag).
- `countSeverity(List<String> issues, String severity)` — counts messages that start with a severity emoji.

Why the tests use JavaParser
----------------------------
Each detector consumes a `CompilationUnit` AST. Tests create small self-contained Java snippets (as strings) and parse them into `CompilationUnit` objects using JavaParser. This keeps tests fast and deterministic: they don't require compiling classes to disk or running the whole application.

How tests are structured
------------------------
- Each detector has a one-to-one JUnit 5 test class.
- Test methods follow `should<Behavior>` style names (e.g. `shouldReportHighExternalDependencies`).
- Tests use black-box assertions: they assert the presence or absence of an issue tag (e.g. `LongMethod`, `MagicNumber`) and look for meaningful substrings rather than exact messages.
- Tests avoid depending on exact line numbers or file-storage details to remain robust across environments.

Running tests
-------------
From IntelliJ (recommended for development):
- Use the built-in test runner: right-click a test class or method and click Run. IntelliJ runs JUnit with its own runner and classpath setup.

From the command-line (Maven wrapper provided in repository):

```powershell
cd "D:\devsync - stable - eleven 1.5.1"
.\mvnw.cmd -DskipTests=false test
```

Notes about environment differences
----------------------------------
If tests pass in the IDE but fail under Maven, common causes include:
- Differences in classpath (IDE may add test helpers or different JUnit provider versions).
- JavaParser configuration differences (position/column info or comment attachment may vary depending on parser options). Tests here use `StaticJavaParser.parse(...)` to be deterministic.
- Tests that depend on file storage (the detector uses `cu.getStorage()` to show a filename) — since tests parse in-memory snippets, `getStorage()` often returns empty. Tests must not rely on exact file names.

Test conventions and guidance for adding new tests
------------------------------------------------
1. Put tests under `src/test/java/com/devsync/detectors/` and name the class `XxxDetectorTest`.
2. Use `DetectorTestBase`'s `parseCode(...)` to obtain a `CompilationUnit`.
3. Call the detector's `detect(cu)` method to get List<String> results.
4. Assert presence/absence via `hasIssue(issues, "DetectorTag")` or by checking message substrings using standard JUnit assertions.
5. Prefer small, self-contained Java snippets (single class or interface) to exercise a single behavior.
6. Avoid brittle assertions that depend on exact score numbers or file names.

Example test pattern
--------------------
A typical test does the following:

```java
String code = "class C{ void m(){ if(a && b && c && d){} } }";
CompilationUnit cu = parseCode(code);
List<String> issues = new ComplexConditionalDetector().detect(cu);
assertTrue(hasIssue(issues, "ComplexConditional"));
```

Troubleshooting tips
--------------------
- If a detector test fails only under Maven:
  1. Run the failing test alone from the command line to get the full surefire report: `mvn -Dtest=MyTest test`.
  2. Inspect the exception / assertion output in `target/surefire-reports/`.
  3. Check whether the test depends on parser position info (`getBegin()`, `getEnd()`); ensure that positions are available or rewrite the test not to rely on line numbers.
- If JavaParser parsing fails for a snippet, wrap the snippet in a small complete class or interface; parser expects syntactically-correct source.

Common patterns that caused flakiness and how we fixed them
---------------------------------------------------------
- Using `JavaParser parser = new JavaParser()` vs `StaticJavaParser` — switched all tests to `StaticJavaParser.parse` to ensure consistent parser config and behavior across runners.
- Tests relying on `cu.getStorage()` for filenames — detectors fall back to `UnknownFile` when storage is missing. Tests should not assert exact filenames.

Extending tests
----------------
- Add tests that exercise private helper logic only when necessary; prefer black-box testing where possible.
- For deep testing of private helpers, use reflection and keep those tests focused and minimal.

Next steps and suggestions
--------------------------
- If you want me to formalize the test naming conventions or create a test template snippet file, I can add `TEST_TEMPLATE.md` with examples.
- If you'd like CI that runs Maven tests (so Maven and IDE runs are both verified automatically), I can add a simple GitHub Actions workflow.

Contact and ownership
---------------------
If you need me to adjust any specific failing test, tell me which detector and I will craft a deterministic snippet and update the test accordingly.

---
Document generated on: 2025-11-21

