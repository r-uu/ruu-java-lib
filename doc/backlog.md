# Backlog — Schwachstellen und offene Punkte

Analysiert: 2026-06-27. Zuletzt aktualisiert: 2026-06-27.
Bezieht sich auf den aktuellen Stand der `main`-Branch.

Priorität: **P1** = scharfer Defekt oder Sicherheitsproblem · **P2** = verhindert verlässlichen Betrieb ·
**P3** = Codequality / Wartbarkeit · **P4** = strategisch, mittelfristig.

---

## ~~P1 — Scharfe Defekte~~ — alle behoben ✓

### ~~P1-1 · `PostgresToolBox` — Syntaxfehler verhindert Kompilierung~~ — behoben

**Datei:** `postgres/toolbox/src/main/java/de/ruu/lib/postgres/PostgresToolBox.java`

`public abstract class` mit einem herrenlosem `{`-Block statt eines Konstruktors verhinderte
die Kompilierung des Moduls.

**Behebung:** Klasse auf `public class` geändert, privater Utility-Konstruktor hinzugefügt.

---

### ~~P1-2 · `Wsl2IpResolver` — Race Condition im Cache~~ — behoben

**Datei:** `util/src/main/java/de/ruu/lib/util/Wsl2IpResolver.java`

`cachedWsl2Ip` war nicht `volatile` deklariert. Bei gleichzeitigen Zugriffen aus mehreren
Threads konnte ein Thread einen bereits gesetzten Cache-Wert nicht sehen.

**Behebung:** `private static volatile String cachedWsl2Ip` deklariert.

---

## ~~P2 — Zuverlässigkeitsprobleme~~ — alle behoben ✓

### ~~P2-1 · `WritableFileConfigSource.getPropertyNames()` — gibt mutablee Sicht zurück~~ — behoben

**Datei:** `mp/config/src/main/java/de/ruu/lib/util/config/mp/WritableFileConfigSource.java`

`getPropertyNames()` gab direkt `properties.keySet()` zurück. Aufrufer konnten damit die
interne Map strukturell verändern (Einträge entfernen), ohne `save()` zu durchlaufen.

**Behebung:** `Collections.unmodifiableSet(properties.keySet())` als Rückgabe.

---

### ~~P2-2 · `HealthCheckRunner` — zustandsbehaftet und nicht wiedereintrittssicher~~ — behoben

**Datei:** `docker/health/src/main/java/de/ruu/lib/docker/health/HealthCheckRunner.java`

`runAll()` begann mit `results.clear(); allHealthy = true;`. Bei gleichzeitigen Aufrufen
überschrieben sich die Threads gegenseitig. Außerdem enthielten Log-Nachrichten Emojis
(✅ ❌), die machine-readable Log-Parser (JSON, ELK, Loki) störten.

**Behebung:**
- `results` und `allHealthy` als lokale Variablen in `runAll()` — Rückgabe als `RunResult`-Record.
- `AutoFixRunner` auf die neue API umgestellt.
- Emojis durch ASCII ersetzt (`[OK]` / `[FAIL]` / `[WARN]`).

---

### ~~P2-3 · `AbstractRepository.countAll()` — JPQL-String-Konkatenation mit Klassenname~~ — behoben

**Datei:** `jpa/core/src/main/java/de/ruu/lib/jpa/core/AbstractRepository.java`

`clazz.getSimpleName()` lieferte den Java-Klassennamen, nicht den JPQL-Entitätsnamen.
Weicht `@Entity(name = "...")` davon ab, schlug die Query zur Laufzeit fehl. Außerdem
führte `update()` vor dem Merge ein zusätzliches `em.find()` aus — bei Bulk-Operationen
verdoppelte das die DB-Roundtrips.

**Behebung:**
- `countAll()` nutzt `em.getMetamodel().entity(clazz).getName()`.
- `update()` vertraut auf JPA-`merge()`-Semantik; extra `find()` entfernt.

---

### ~~P2-4 · `CDIContainer` — fängt `Throwable` zur Fehlerkennung~~ — behoben

**Datei:** `cdi/se/src/main/java/de/ruu/lib/cdi/se/CDIContainer.java`

`catch (Throwable t)` fing auch `OutOfMemoryError` und `StackOverflowError`.

**Behebung:** `catch (Exception e)` — Weld wirft ausschließlich `RuntimeException`-Subtypen
bei Initialisierungsfehlern.

---

## ~~P3 — Codequality und Wartbarkeit~~ — alle behoben ✓

### ~~P3-1 · `Strings` / `StringBuffers` / `StringBuilders` — `trimChars`-Logik dreifach dupliziert~~ — behoben

**Dateien:**
- `util/src/main/java/de/ruu/lib/util/Strings.java`
- `util/src/main/java/de/ruu/lib/util/StringBuffers.java`
- `util/src/main/java/de/ruu/lib/util/StringBuilders.java`

**Behebung:**
- `trimChars` in `Strings` iterativ implementiert (keine Rekursion mehr, kein `StackOverflowError`-Risiko).
- `StringBuffers` und `StringBuilders` delegieren via Konvertierung in `String` an `Strings`.
- `isNullOrEmptyOrBlank` / `isNotNullOrEmptyOrBlank` als `@Deprecated` markiert; interne
  Aufrufe auf `isNullOrBlank` / `isNotNullOrBlank` umgestellt.

---

### ~~P3-2 · `ConfigHealthCheck` — `System.out.println` statt Logger~~ — behoben

**Datei:** `mp/config/src/main/java/de/ruu/lib/util/config/mp/ConfigHealthCheck.java`

**Behebung:** Alle `System.out.println`-Aufrufe in `printReport()` durch `log.info/error/warn`
ersetzt. Emojis in Meldungen durch ASCII-Präfixe (`[OK]`, `[FAIL]`, `[WARN]`) ersetzt.

---

### ~~P3-3 · `DisabledOnServerNotListening` — Naming-Inkonsistenz in JUnit-Extension~~ — behoben

**Dateien:** `junit/src/main/java/de/ruu/lib/junit/`

**Behebung:**
- `DisableOnServerNotListening` → `DisabledOnServerNotListeningCondition` umbenannt (JUnit-5-Konvention).
- `@ExtendWith`-Referenz in `@DisabledOnServerNotListening` angepasst.
- Alte Datei gelöscht.

---

### ~~P3-4 · `BiMap.clear()` auskommentiert~~ — behoben

**Datei:** `util/src/main/java/de/ruu/lib/util/bimapped/BiMap.java`

**Behebung:** `clear()` einkommentiert — `map.clear()` ist korrekt, da die Map beide
Richtungen in derselben `IdentityHashMap` hält.

---

### ~~P3-5 · `keycloak/admin` — blanke `catch (Exception e)` Blöcke~~ — behoben

**Datei:** `keycloak/admin/src/main/java/de/ruu/lib/keycloak/admin/`
(`KeycloakRealmManager`, `KeycloakUserManager`, `KeycloakClientManager`)

**Behebung:**
- `catch (Exception e)` durch `catch (WebApplicationException e)` (mit HTTP-Status im Log)
  gefolgt von `catch (RuntimeException e)` ersetzt.
- Double-Wrapping-Bug in `createUser`, `deleteUser` und `createClient` behoben: ein intern
  geworfenes `KeycloakAdminException` wurde zuvor fälschlicherweise nochmals eingewickelt.

---

### ~~P3-6 · `FXCApp` — ungelöste TODO-Kommentare mit auskommentiertem Code~~ — behoben

**Datei:** `fx/comp/src/main/java/de/ruu/lib/fx/comp/FXCApp.java`

**Behebung:** TODO-Kommentar und auskommentierten `onShowingProperty().addListener(...)`-Code
entfernt. Die funktionierende `setOnShowing()`-Variante bleibt.

---

## ~~P4 — Fehlende Tests~~ — teilweise behoben ✓ (5 von 9 testbaren Modulen)

Der Anteil testloser Module ist hoch für eine Bibliothek, die in Produktivprojekten
eingesetzt wird:

| Modul                                   | Kritikalität          | Status                                            |
|-----------------------------------------|-----------------------|---------------------------------------------------|
| `cdi/se` (`CDIContainer`)               | hoch                  | ✓ behoben                                         |
| `fx/comp` (FXC-Framework)               | hoch                  | offen (TestFX/headless)                           |
| `jpa/se`                                | mittel                | offen                                             |
| `ws/rs`                                 | mittel                | ✓ behoben                                         |
| `cdi/common`                            | mittel                | offen                                             |
| `docker/health`                         | mittel                | ✓ behoben                                         |
| `jdbc/core`                             | mittel                | ✓ behoben                                         |
| `mapstruct/core`                        | niedrig               | ✓ behoben                                         |
| `mapstruct/spi`                         | niedrig               | offen (Annotation-Processor, schwer unit-testbar) |
| `cdi/demo` / `fx/comp_demo` / `fx/demo` | niedrig (Demo-Module) | offen                                             |

**Behebung (implementierte Tests):**
- `cdi/se`: `CDIContainerTest` — Bootstrap, Idempotenz, Bean-Lookup via Weld SE
- `ws/rs`: `ErrorResponseTest`, `SerializableExceptionTest`, `ExceptionHierarchyTest` — reine Unit-Tests aller Value-Klassen und Exception-Typen
- `docker/health`: `HealthCheckRunnerTest` — `RunResult`-API mit anonymen Mock-`HealthCheck`-Implementierungen
- `jdbc/core`: `JDBCURLTest`, `AbstractJDBCPropertiesTest` — reine Unit-Tests
- `mapstruct/core`: `ReferenceCycleTrackingTest`, `OptionalMapperTest` — inkl. MapStruct-generierter Implementierung

**Nebenbefund behoben:**
- `keycloak/admin/pom.xml`: `org.jspecify` fehlte als explizite Abhängigkeit, obwohl `module-info.java` `requires org.jspecify` enthält — Compilation-Fehler bei Standalone-Build.

**Offen:** `fx/comp` (benötigt TestFX headless Setup), `jpa/se` (benötigt Persistence-Provider), `cdi/common` (benötigt CDI-Laufzeit), `mapstruct/spi` (Annotation-Processor-SPI, kein sinnvoller Unit-Test möglich).
